package runningtracker.presenter.suggest_place;

import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import runningtracker.data.model.suggest_place.SuggestLocation;
import runningtracker.model.suggets_place.Distance;
import runningtracker.model.suggets_place.Duration;
import runningtracker.model.suggets_place.ItemSuggest;
import runningtracker.model.suggets_place.Route;
import runningtracker.presenter.common.DistanceTwoPoint;

public class DirectionFinder {
    private static final String TAG = "DirectionFinder" ;

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public DirectionFinder(){
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    /**
     * @param : list item chosen type
     * @return : list location suggest
    * */
    public List<Location> getResultPlace(List<SuggestLocation> suggestLocationList, ArrayList<ItemSuggest> itemSuggestList){
        List<Location> listLocation = new ArrayList<>();
        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+suggestLocationList.size());
        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+suggestLocationList.get(1).getTypePlace());
        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+suggestLocationList.get(1).getLatitudeValue());

        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+itemSuggestList.get(0).getPosition());
        for(int i = 0; i < suggestLocationList.size(); i++){
            SuggestLocation suggestLocation = new SuggestLocation();

            suggestLocation = suggestLocationList.get(i);
            if((suggestLocation.getTypePlace()-1) == itemSuggestList.get(0).getPosition()){
                Location location = new Location("A");
                location.setLatitude(suggestLocation.getLatitudeValue());
                location.setLongitude(suggestLocation.getLongitudeValue());
                listLocation.add(location);
            }
        }
        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+listLocation.size());
        return listLocation;
    }

    /**
     * @param : list item chosen, list location get list location suggest, Google map
     * @return : list location suggest
    * */
    public List<Location> setMarkerLocation(final ArrayList<ItemSuggest> itemSuggestList, final GoogleMap mMap, final SuggestCallback suggestCallback)
    {
        final List<Location>[] listLocation = new ArrayList[1];

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        db.collection("suggestlocations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    List<SuggestLocation> suggestLocationList = task.getResult().toObjects(SuggestLocation.class);
                    Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+suggestLocationList.size());
                    listLocation[0] = getResultPlace(suggestLocationList, itemSuggestList);

                    Log.d(TAG,"BBBBBBBBBBBBBBBBBBBBBBB 1: "+ listLocation[0]);

                    for(int i = 0; i < listLocation[0].size(); i++){
                        LatLng location = new LatLng(listLocation[0].get(i).getLatitude(), listLocation[0].get(i).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(location).title("Suggest Location"));
                    }

                    suggestCallback.getListLocation( listLocation[0]);
                }
                else {
                    Log.d(TAG,"Get an error abcdxyz");
                }
            }
        });


//        listLocation = getResultPlace(itemSuggestList);
//        for(int i = 0; i < listLocation.size(); i++){
//            LatLng location = new LatLng(listLocation.get(i).getLatitude(), listLocation.get(i).getLongitude());
//            mMap.addMarker(new MarkerOptions().position(location).title("Suggest Location"));
//        }
        Log.d(TAG,"BBBBBBBBBBBBBBBBBBBB 2: "+ listLocation[0]);
        return listLocation[0];
    }

    public List<Location> aa(List<Location> lll){
        List<Location> rrr = new ArrayList<>();
        rrr = lll;
        return rrr;
    }


    public float locationDistanceMin(Location location, ArrayList<Location> listLocation){
        DistanceTwoPoint distance = new DistanceTwoPoint();
        float minDistance = distance.DistanceLocation(location,listLocation.get(0));
        for(int i = 0; i < listLocation.size(); i++){
            //if(distance.DistanceLocation(location, ))
        }
        return minDistance;
    }
}

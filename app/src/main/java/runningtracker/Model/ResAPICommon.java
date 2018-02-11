package runningtracker.model;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import runningtracker.view.running.MainActivity;

public class ResAPICommon extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    //post data to server
    public void  RestPostClient(final Context context, String url, final JSONObject  para) throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, para,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String str = (String) response.get("result");
                           if(Integer.parseInt(str) == 1){
                              // Toast.makeText(context, "Save success" , Toast.LENGTH_SHORT).show();
                           }
                           else{
                               //Toast.makeText(context, "Save False" , Toast.LENGTH_SHORT).show();
                           }
                        }
                        catch (Exception e) {
                           e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context, "Error:" +error , Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, ""+para, Toast.LENGTH_SHORT).show();
                        //Log.d("ERROR","error => "+para.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    //get data to server
    public static void RestGetClient(String url, final Context context, final DataCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
       /* JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(context, "Error" +response, Toast.LENGTH_SHORT).show();
                        jsonArray[0] = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(context, "Error" +error, Toast.LENGTH_SHORT).show();
                    }
                }
        );*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(context, "Error" +response, Toast.LENGTH_SHORT).show();
                        try {
                            callback.onSuccess(response);
                        } catch (Exception  e){
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error" +error, Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}

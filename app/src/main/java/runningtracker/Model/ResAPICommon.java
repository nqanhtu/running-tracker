package runningtracker.Model;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ResAPICommon extends AppCompatActivity {
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
                               Toast.makeText(context, "Save success" , Toast.LENGTH_SHORT).show();
                           }
                           else{
                               Toast.makeText(context, "Save False" , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Error:" +error , Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, ""+para, Toast.LENGTH_SHORT).show();
                        //Log.d("ERROR","error => "+para.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
    //get data to server
    public  void RestGetClient(String url, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(context, "Error" +error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
/*        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error" +error, Toast.LENGTH_SHORT).show();
                    }
                }
        );*/

       requestQueue.add(jsonArrayRequest);
    }

}

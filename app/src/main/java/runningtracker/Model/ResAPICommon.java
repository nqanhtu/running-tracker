package runningtracker.model;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class ResAPICommon extends AppCompatActivity {
    //post data to server
    public void  RestPostClient(final Context context, String url, final HashMap<String, String> para){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        /*StringRequest stringRequest =  new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Server is error", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //get values with params
                if (para != null) {
                    for (Map.Entry<String, String> entry : para.entrySet()) {
                        params.put(entry.getKey(), entry.getValue());
                    }
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(para),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
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
                        //System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(context, "Error" +error, Toast.LENGTH_SHORT).show();
                        //System.out.println(error);
                    }
                }
        );
        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );*/
        requestQueue.add(jsonArrayRequest);
    }

}

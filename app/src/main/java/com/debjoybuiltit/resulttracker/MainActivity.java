package com.debjoybuiltit.resulttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    private String mainUrl;

    private final int ALERT_DANGER=-1;
    private final int ALERT_INFO=0;
    private final int ALERT_SUCCESS=1;
    private final int ALERT_OK_QUIT=9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        mainUrl="https://atdebjoy.com/others/api/perform/";
        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        if (spref.contains("username")) {
            String username=spref.getString("username","");
            String pwd=spref.getString("pwd","");
            String email=spref.getString("email","");
            check_login( username, pwd, email);
        } else {
            goTo();
        }

    }

    public void goTo() {
        Intent loginActiviity = new Intent(this, LoginActivity.class);
        startActivity(loginActiviity);
        finishAffinity();
    }

    public void check_login(String username, String pwd, String email){
        String requestUrl=mainUrl+"login.php";

        JSONObject postparams = new JSONObject();
        try {
            if(username.equals(""))
                postparams.put("email", email);
            else
                postparams.put("username", username);
            postparams.put("pwd",pwd);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                requestUrl, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        try {
                            int code=response.getInt("code");
                            String msg=response.getString("msg");
                            if(code==202){
                                int user_id=response.getInt("user");
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("data_user", MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putInt("user_id", user_id);
                                editor.commit();
                                Intent dashActivity=new Intent(mContext, DashActivity.class);
                                startActivity(dashActivity);
                                finishAffinity();
                            }else{
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("data_user", MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear();
                                editor.commit();
                                Intent loginActiviity = new Intent(mContext, LoginActivity.class);
                                loginActiviity.putExtra("msg", msg);
                                loginActiviity.putExtra("alert", ALERT_DANGER);
                                startActivity(loginActiviity);
                                finishAffinity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure Callback
                        Intent loginActiviity = new Intent(mContext, LoginActivity.class);
                        loginActiviity.putExtra("msg", "Network error");
                        loginActiviity.putExtra("alert", ALERT_OK_QUIT);
                        startActivity(loginActiviity);
                        finishAffinity();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }
}

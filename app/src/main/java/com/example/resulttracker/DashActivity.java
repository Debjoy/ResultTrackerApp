package com.example.resulttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashActivity extends AppCompatActivity {
    private String mainUrl;
    private int exitFlag=1;
    private Context mContext;
    private BottomNavigationView bottomNav;
    private ArrayList<Integer> mTermIdList= new ArrayList<>();
    private ArrayList<String> mTermNameList = new ArrayList<>();
    private FrameLayout mDashFrameLayout;
    private ProgressBar mDashLoading;
    private boolean gotResponse;
    private String userName;
    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gotResponse=false;
        setContentView(R.layout.activity_dash);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        exitFlag=1;
        mContext=this;
        bottomNav=findViewById(R.id.bottom_navigation_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        mDashFrameLayout=findViewById(R.id.main_dashboard_frame);
        mDashLoading=findViewById(R.id.dash_loading);
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";

        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        userName=spref.getString("username",null);
        userId=spref.getInt("user_id",1);

        beginWithHome();
    }

    public void beginWithHome(){
        mDashLoading.setVisibility(View.VISIBLE);
        mDashFrameLayout.setVisibility(View.GONE);

        String requestUrl=mainUrl+"getlistterm.php?stud_id="+userId;
        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202) {
                                JSONArray responseArray=response.getJSONArray("response");
                                for (int i = 0; i < responseArray.length(); i++) {
                                    mTermIdList.add(responseArray.getJSONObject(i).getInt("term_id"));
                                    mTermNameList.add(responseArray.getJSONObject(i).getString("term_name"));
                                }
                                gotResponse = true;
                                mDashLoading.setVisibility(View.GONE);
                                mDashFrameLayout.setVisibility(View.VISIBLE);
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame, new HomeFragment(mTermIdList, mTermNameList)).commit();
                                bottomNav.findViewById(R.id.nav_home).performClick();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(DashActivity.this);
        requestQueue.add(jsonArrReq);

    }

    public void onLogOut(){
        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        SharedPreferences.Editor sprefEditor=spref.edit();
        sprefEditor.clear();
        sprefEditor.commit();
        Intent mainActivity=new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;
                    exitFlag=1;
                    if(!gotResponse)
                        return true;
                    switch(item.getItemId()){
                        case R.id.nav_home : selectedFragment=new HomeFragment(mTermIdList,mTermNameList);
                        break;
                        case R.id.nav_term: selectedFragment = new TermsFragment();
                        break;
                        case R.id.nav_subjects: selectedFragment =  new SubjectFragment();
                        break;
                        case R.id.nav_input: selectedFragment = new InputFragment(DashActivity.this, userId);
                        break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==0){
            if(exitFlag==1){
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                exitFlag=0;
                return;
            }
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);

        Drawable drawable = menu.findItem(R.id.action_logout).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorWhite));
        menu.findItem(R.id.action_logout).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                new AlertDialog.Builder(DashActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onLogOut();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

                break;
            default:
                break;
        }
        return true;
    }
}

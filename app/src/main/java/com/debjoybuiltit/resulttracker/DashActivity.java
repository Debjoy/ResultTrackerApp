package com.debjoybuiltit.resulttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class DashActivity extends AppCompatActivity {
    private int exitFlag=1;
    private BottomNavigationView bottomNav;
    private String userName;
    private int userId;
    private String user_pass;
    private SmoothBottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        exitFlag=1;
        //bottomNav=findViewById(R.id.bottom_navigation_view);
        //bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomBar=findViewById(R.id.bottom_navigation_view);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                Fragment selectedFragment=null;
                exitFlag=1;
                switch(i){
                    case 0 : selectedFragment=new HomeFragment(userId, user_pass, DashActivity.this, DashActivity.this);
                        break;
                    case 1: selectedFragment = new TermsFragment( userId,user_pass,DashActivity.this);
                        break;
                    case 2: selectedFragment =  new SubjectFragment(userId, user_pass, DashActivity.this, DashActivity.this);
                        break;
                    case 3: selectedFragment = new InputFragment(DashActivity.this, userId,user_pass);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,selectedFragment).commit();
            }
        });

        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        userName=spref.getString("username",null);
        userId=spref.getInt("user_id",-99);
        user_pass=spref.getString("pwd", "");
        Toasty.info(DashActivity.this,"Welcome "+userName,Toasty.LENGTH_SHORT,false).show();


        getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,new HomeFragment(userId,user_pass, DashActivity.this, DashActivity.this)).commit();
    }




//    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    Fragment selectedFragment=null;
//                    exitFlag=1;
//                    switch(item.getItemId()){
//                        case R.id.nav_home : selectedFragment=new HomeFragment(userId, user_pass, DashActivity.this, DashActivity.this);
//                        break;
//                        case R.id.nav_term: selectedFragment = new TermsFragment( userId,user_pass,DashActivity.this);
//                        break;
//                        case R.id.nav_subjects: selectedFragment =  new SubjectFragment(userId, user_pass, DashActivity.this, DashActivity.this);
//                        break;
//                        case R.id.nav_input: selectedFragment = new InputFragment(DashActivity.this, userId,user_pass);
//                        break;
//                    }
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,selectedFragment).commit();
//                    return true;
//                }
//            };



    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==0){
            if(exitFlag==1){
                Toasty.normal(this, "Press back again to exit", Toasty.LENGTH_SHORT).show();
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

        Drawable drawable = menu.findItem(R.id.action_guide).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorWhite));
        menu.findItem(R.id.action_guide).setIcon(drawable);

        drawable = menu.findItem(R.id.action_general).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorWhite));
        menu.findItem(R.id.action_general).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_guide:


                AlertDialog.Builder builder = new AlertDialog.Builder(DashActivity.this);
                LayoutInflater inflater = LayoutInflater.from(DashActivity.this);
                final View layout = inflater.inflate(R.layout.alert_guide,null);
                builder.setView(layout);

                final AlertDialog alertDialog=builder.show();
                layout.findViewById(R.id.alert_guide_cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                layout.findViewById(R.id.alert_guide_learn_more_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.youtube.com/watch?v=-50NdPawLVY&feature=youtu.be&t=30";
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        // Verify that the intent will resolve to an activity
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // Here we use an intent without a Chooser unlike the next example
                            startActivity(intent);
                        }
                    }
                });

                break;
            case R.id.action_general:
                Intent loginActiviity = new Intent(DashActivity.this, GeneralActivity.class);
                startActivity(loginActiviity);
                break;
            default:
                break;
        }
        return true;
    }
}

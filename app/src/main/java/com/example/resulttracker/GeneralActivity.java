package com.example.resulttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GeneralActivity extends AppCompatActivity {

    private TextView mLogoutButton;
    private TextView mGithubButton;
    private TextView mGeneralButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        mLogoutButton=findViewById(R.id.general_logout_button);
        mGithubButton=findViewById(R.id.general_github_link);
        mGeneralButton=findViewById(R.id.general_help_link);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GeneralActivity.this)
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
            }
        });

        mGithubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/Debjoy/ResultTrackerApp";
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Here we use an intent without a Chooser unlike the next example
                    startActivity(intent);
                }
            }
        });

        mGeneralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/watch?v=wZZ7oFKsKzY";
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Here we use an intent without a Chooser unlike the next example
                    startActivity(intent);
                }
            }
        });
    }

    public void onLogOut(){
        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        SharedPreferences.Editor sprefEditor=spref.edit();
        sprefEditor.clear();
        sprefEditor.commit();
        Intent mainActivity=new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }
}

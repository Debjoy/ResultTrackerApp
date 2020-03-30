package com.example.resulttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneralActivity extends AppCompatActivity {

    private TextView mLogoutButton;
    private TextView mGithubButton;
    private TextView mGeneralButton;

    private TextView mExamStructureButton;
    private TextView mProfileEditButton;

    private int stud_id;
    private String mainUrl;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";
        mLogoutButton=findViewById(R.id.general_logout_button);
        mGithubButton=findViewById(R.id.general_github_link);
        mGeneralButton=findViewById(R.id.general_help_link);
        mExamStructureButton=findViewById(R.id.general_exam_structure_button);
        mProfileEditButton=findViewById(R.id.general_edit_profile_button);

        SharedPreferences spref = getSharedPreferences("data_user", MODE_PRIVATE);
        stud_id=spref.getInt("user_id",-99);

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

        mExamStructureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneralActivity.this);
                LayoutInflater inflater = LayoutInflater.from(GeneralActivity.this);
                final View alertLayout = inflater.inflate(R.layout.alert_general_exam_structure,null);
                builder.setView(alertLayout);
                final AlertDialog alertD=builder.show();

                alertLayout.findViewById(R.id.alert_general_exam_structure_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertD.dismiss();
                    }
                });

                loadExamStructure(alertLayout);

            }
        });

        mProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneralActivity.this);
                LayoutInflater inflater = LayoutInflater.from(GeneralActivity.this);
                final View alertLayout = inflater.inflate(R.layout.alert_edit_profile,null);
                builder.setView(alertLayout);
                final AlertDialog alertD=builder.show();

                awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

                EditText mFullNameEdit=alertLayout.findViewById(R.id.alert_edit_profile_full_name);
                EditText mEmailEdit =  alertLayout.findViewById(R.id.alert_edit_profile_email_id);
                EditText mUsername = alertLayout.findViewById(R.id.alert_edit_profile_username);


                awesomeValidation.addValidation(mFullNameEdit, "^[A-Za-z ]{1,}$", "Should contain only aphabets and space");
                awesomeValidation.addValidation(mEmailEdit, Patterns.EMAIL_ADDRESS, "Enter proper email address");
                awesomeValidation.addValidation(mUsername, new SimpleCustomValidation() {
                    @Override
                    public boolean compare(String s) {

                        return false;
                    }
                },"error");

                alertLayout.findViewById(R.id.alert_edit_profile_cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertD.dismiss();
                    }
                });

                alertLayout.findViewById(R.id.alert_edit_profile_save_button).setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if(awesomeValidation.validate()){
                             Toast.makeText(GeneralActivity.this, "perfect", Toast.LENGTH_SHORT).show();
                         }
                     }
                });


            }
        });
    }

    public void loadExamStructure(final View alertLayout){

        alertLayout.findViewById(R.id.alert_general_exam_structure_progress).setVisibility(View.VISIBLE);
        String requestUrl=mainUrl+"get_exam.php?stud_id="+stud_id;

        //ADD Functionality for adding exam
        alertLayout.findViewById(R.id.alert_general_exam_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GeneralActivity.this, "Function Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202){
                                JSONArray responseArray=response.getJSONArray("response");
                                AlertExamListRecyclerViewAdapter adapter= new AlertExamListRecyclerViewAdapter(responseArray,GeneralActivity.this, alertLayout);
                                RecyclerView mExamListRecycler=alertLayout.findViewById(R.id.alert_general_exam_recycler_list);
                                mExamListRecycler.setAdapter(adapter);

                                mExamListRecycler.setLayoutManager(new LinearLayoutManager(GeneralActivity.this));
                                alertLayout.findViewById(R.id.alert_general_exam_structure_progress).setVisibility(View.GONE);
                            }else{
                                Toast.makeText(GeneralActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                alertLayout.findViewById(R.id.alert_general_exam_structure_progress).setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GeneralActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(GeneralActivity.this);
        requestQueue.add(jsonObjectRequest);
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

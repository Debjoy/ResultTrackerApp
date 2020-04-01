package com.example.resulttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
    Context mGeneral;
    String profile_full_name;
    String profile_email;
    String profile_username;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    //for exam add validation
    private AwesomeValidation addExamValidation;
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
        mGeneral=GeneralActivity.this;

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

        loadProfileInfo();

    }

    public void loadProfileInfo(){
        findViewById(R.id.general_profile_info_layout).setVisibility(View.GONE);
        findViewById(R.id.general_profile_info_progress).setVisibility(View.VISIBLE);

        profile_full_name="";
        profile_email="";
        profile_username="";

        String requestUrl= mainUrl+"basic_details.php?profile&stud_id="+stud_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202){
                                profile_full_name=response.getJSONObject("response").getString("full_name");
                                profile_email=response.getJSONObject("response").getString("email");
                                profile_username=response.getJSONObject("response").getString("username");

                                ((TextView)findViewById(R.id.general_profile_info_name)).setText(profile_full_name);
                                ((TextView)findViewById(R.id.general_profile_info_email)).setText(profile_email);
                                ((TextView)findViewById(R.id.general_profile_info_username)).setText(profile_username);


                            }else{
                                Toast.makeText(GeneralActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        findViewById(R.id.general_profile_info_progress).setVisibility(View.GONE);
                        findViewById(R.id.general_profile_info_layout).setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mGeneral, "Network error", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.general_profile_info_progress).setVisibility(View.GONE);
                        findViewById(R.id.general_profile_info_layout).setVisibility(View.VISIBLE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(GeneralActivity.this);
        requestQueue.add(jsonObjectRequest);


        mProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneralActivity.this);
                LayoutInflater inflater = LayoutInflater.from(GeneralActivity.this);
                final View alertLayout = inflater.inflate(R.layout.alert_edit_profile,null);
                builder.setView(alertLayout);
                final AlertDialog alertD=builder.show();

                awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

                final EditText mFullNameEdit=alertLayout.findViewById(R.id.alert_edit_profile_full_name);
                final EditText mEmailEdit =  alertLayout.findViewById(R.id.alert_edit_profile_email_id);
                final EditText mUsername = alertLayout.findViewById(R.id.alert_edit_profile_username);

                mFullNameEdit.setText(profile_full_name);
                mEmailEdit.setText(profile_email);
                mUsername.setText(profile_username );


                awesomeValidation.addValidation(mFullNameEdit, "^[A-Za-z ]{1,}$", "Should contain only aphabets and space");
                awesomeValidation.addValidation(mEmailEdit, Patterns.EMAIL_ADDRESS, "Enter proper email address");
                awesomeValidation.addValidation(mUsername, "^[a-z0-9_-]+$","Username should only contain alphabets, numbers and underscore");
                awesomeValidation.addValidation(mUsername, "^.{3,}$","Username should atleast contain 4 characters");
                awesomeValidation.addValidation(mUsername, "^.{0,16}$","Username should not exceed 16 characters");
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

                            String requestUrl=mainUrl+"register.php";

                            JSONObject postparams = new JSONObject();
                            try {
                                postparams.put("stud_id", stud_id);
                                postparams.put("name", mFullNameEdit.getText().toString());
                                postparams.put("email", mEmailEdit.getText().toString());
                                postparams.put("username", mUsername.getText().toString());
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }

                            alertLayout.findViewById(R.id.alert_edit_profile_progress).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest1=new JsonObjectRequest(Request.Method.POST, requestUrl, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    Toast.makeText(GeneralActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                                    alertLayout.findViewById(R.id.alert_edit_profile_progress).setVisibility(View.GONE);
                                                    alertD.dismiss();
                                                    loadProfileInfo();
                                                }else if(response.getInt("code")==463){
                                                    Toast.makeText(GeneralActivity.this, "Username Exists", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(GeneralActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            alertLayout.findViewById(R.id.alert_edit_profile_progress).setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(GeneralActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                            alertLayout.findViewById(R.id.alert_edit_profile_progress).setVisibility(View.GONE);
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(GeneralActivity.this);
                            requestQueue.add(jsonObjectRequest1);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneralActivity.this);
                LayoutInflater inflater = LayoutInflater.from(GeneralActivity.this);
                final View layout = inflater.inflate(R.layout.alert_add_exam,null);
                builder.setView(layout);
                final AlertDialog alertDialog=builder.show();

                layout.findViewById(R.id.alert_add_exam_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                addExamValidation = new AwesomeValidation(ValidationStyle.BASIC);

                final EditText mAddExamName=layout.findViewById(R.id.alert_add_exam_name);
                final EditText mAddExamFullMarks=layout.findViewById(R.id.alert_add_exam_full_marks);
                final EditText mAddExamFrequency=layout.findViewById(R.id.alert_add_exam_frequency);

                addExamValidation.addValidation(mAddExamName,"^.{1,}","Exam name cannot be empty");
                addExamValidation.addValidation(mAddExamFullMarks,"^.{1,}$","Full marks cannot be empty");
                addExamValidation.addValidation(mAddExamFrequency, "^0*([1-9]|1[0-9]|2[0-4])$","Frequency allowed from 1 till 24");

                layout.findViewById(R.id.alert_add_exam_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(addExamValidation.validate()){
                            String requestUrl=mainUrl+"update_exam.php";

                            JSONObject postparms= new JSONObject();
                            try {
                                postparms.put("exam_name",((EditText)layout.findViewById(R.id.alert_add_exam_name)).getText().toString());
                                postparms.put("full_marks",((EditText)layout.findViewById(R.id.alert_add_exam_full_marks)).getText().toString());
                                postparms.put("exam_no",((EditText)layout.findViewById(R.id.alert_add_exam_frequency)).getText().toString());
                                postparms.put("stud_id",stud_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            layout.findViewById(R.id.alert_add_exam_progress).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, postparms,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    Toast.makeText(GeneralActivity.this, "Exam successfully added", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                    loadExamStructure(alertLayout);
                                                }else{
                                                    Toast.makeText(GeneralActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            layout.findViewById(R.id.alert_add_exam_progress).setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(GeneralActivity.this, "Network error :", Toast.LENGTH_SHORT).show();
                                            layout.findViewById(R.id.alert_add_exam_progress).setVisibility(View.GONE);
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(GeneralActivity.this);
                            requestQueue.add(jsonObjectRequest);
                        }
                    }
                });

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

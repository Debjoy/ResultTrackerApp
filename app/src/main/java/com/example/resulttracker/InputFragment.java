package com.example.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class InputFragment extends Fragment {
    private String mainUrl;
    private Context mContext;
    private int user_id;
    private RecyclerView mTermsRecyclerView;
    private Button mAddNewTermButton;
    private Button mAddMarksButton;
    private int mSelectedTermId;
    private int mSelectedSubjectId;
    private int mSelectedExamId;
    private int mSelectedExamNo;
    private int mSelectedFullMarks;
    private JSONArray subjectListResponse=new JSONArray();
    private JSONArray termListResponse=new JSONArray();
    private JSONArray examListResponse=new JSONArray();
    private LinearLayout mTermWiseSubjetsLoading;
    InputFragment(Context mContext,int user_id){
        this.mContext=mContext;
        this.user_id=user_id;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_input,container,false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";
        mTermsRecyclerView=view.findViewById(R.id.input_recycler_layout);
        mTermWiseSubjetsLoading=view.findViewById(R.id.input_term_wise_subjets_loading);
        mAddNewTermButton=view.findViewById(R.id.input_add_new_term);
        mAddMarksButton=view.findViewById(R.id.input_layout_add_marks_button);

        mAddMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddMarksButton.setVisibility(View.INVISIBLE);
                String requestUrl=mainUrl+"marks_enter_check.php?stud_id="+user_id;
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getInt("code")==202){
                                        mAddMarksButton.setVisibility(View.VISIBLE);
                                        //creating alertDialog for the marks input

                                        if(response.getJSONArray("response").length()>0){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            LayoutInflater inflater = LayoutInflater.from(mContext);
                                            final View alertLayout = inflater.inflate(R.layout.alert_input_marks,null);
                                            builder.setView(alertLayout);
                                            final AlertDialog alertD=builder.show();

                                            //initializing and making the spinners constant for accessing it from listenners
                                            final Spinner spinnerTermList = ((Spinner)alertLayout.findViewById(R.id.alert_add_marks_term_spinner));
                                            final Spinner spinnerSubjectList = ((Spinner)alertLayout.findViewById(R.id.alert_add_marks_subject_spinner));
                                            final Spinner spinnerExamList = ((Spinner)alertLayout.findViewById(R.id.alert_add_marks_exam_spinner));

                                            termListResponse=response.getJSONArray("response");
                                            ArrayList<String> termList=new ArrayList<>();
                                            for(int i=0;i<termListResponse.length();i++){
                                                termList.add(((JSONObject)termListResponse.get(i)).getString("term_name"));
                                            }
                                            ArrayAdapter<String> termListAdapter= new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, termList);
                                            termListAdapter.setDropDownViewResource(R.layout.spinner_list_item);
                                            //when adapter is set to a this spinner the setOnItemSelectedListenner is automatically called
                                            spinnerTermList.setAdapter(termListAdapter);

                                            spinnerTermList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                                    try {
                                                        ArrayList<String> subjectList=new ArrayList<>();
                                                        subjectListResponse =((JSONObject)termListResponse.get(i)).getJSONArray("term_sub");
                                                        mSelectedTermId=((JSONObject)termListResponse.get(i)).getInt("term_id");
                                                        for(int k=0;k<subjectListResponse.length();k++){
                                                            subjectList.add(((JSONObject)subjectListResponse.get(k)).getString("sub_name"));
                                                        }
                                                        ArrayAdapter<String> subjectListAdapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_item, subjectList);
                                                        subjectListAdapter.setDropDownViewResource(R.layout.spinner_list_item);
                                                        spinnerSubjectList.setAdapter(subjectListAdapter);

                                                        spinnerSubjectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                try {
                                                                    ArrayList<String> examList=new ArrayList<>();
                                                                    examListResponse = ((JSONObject)subjectListResponse.get(i)).getJSONArray("sub_ass");
                                                                    mSelectedSubjectId=((JSONObject)subjectListResponse.get(i)).getInt("sub_id");
                                                                    for(int k=0;k<examListResponse.length();k++){
                                                                        examList.add(((JSONObject)examListResponse.get(k)).getString("ass_name")+" "+((JSONObject)examListResponse.get(k)).getString("ass_no"));
                                                                    }
                                                                    ArrayAdapter<String> subjectListAdapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_item, examList);
                                                                    subjectListAdapter.setDropDownViewResource(R.layout.spinner_list_item);
                                                                    spinnerExamList.setAdapter(subjectListAdapter);

                                                                    spinnerExamList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                        @Override
                                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                            try {
                                                                                mSelectedExamId=((JSONObject)examListResponse.get(i)).getInt("ass_id");
                                                                                mSelectedExamNo=((JSONObject)examListResponse.get(i)).getInt("ass_no");
                                                                                mSelectedFullMarks=((JSONObject)examListResponse.get(i)).getInt("full_marks");
                                                                                ((TextView)alertLayout.findViewById(R.id.alert_add_marks_full_marks)).setText(mSelectedFullMarks+"");
                                                                                ((EditText)alertLayout.findViewById(R.id.alert_add_marks_edit_text)).setFilters(new InputFilter[]{new InputFilterMinMax(0,mSelectedFullMarks,mContext)});
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                                                        }
                                                                    });

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                            }
                                                        });

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });

                                            (alertLayout.findViewById(R.id.alert_add_marks_cancel_button)).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertD.dismiss();
                                                }
                                            });

                                            //functionality of submit button for marks
                                            (alertLayout.findViewById(R.id.alert_add_marks_submit_button)).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    String requestURL=mainUrl+"add_marks.php";
                                                    JSONObject postparams = new JSONObject();
                                                    int marksEntered=Integer.parseInt(((EditText)alertLayout.findViewById(R.id.alert_add_marks_edit_text)).getText().toString());
                                                    try {
                                                        postparams.put("marks", marksEntered);
                                                        postparams.put("term_id",mSelectedTermId);
                                                        postparams.put("sub_id",mSelectedSubjectId);
                                                        postparams.put("ass_id",mSelectedExamId);
                                                        postparams.put("ass_no",mSelectedExamNo);
                                                    }catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    alertLayout.findViewById(R.id.alert_add_marks_progress).setVisibility(View.VISIBLE);
                                                    JsonObjectRequest jsonObjectRequest1=new JsonObjectRequest(Request.Method.POST, requestURL, postparams,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        if(response.getInt("code")==202){
                                                                            Toast.makeText(mContext, "Marks added", Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(mContext, "Something is wrong", Toast.LENGTH_SHORT).show();                                                                    }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    alertLayout.findViewById(R.id.alert_add_marks_progress).setVisibility(View.GONE);
                                                                    alertD.dismiss();
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Toast.makeText(mContext, "Connection error: "+error.toString(), Toast.LENGTH_SHORT).show();
                                                                    alertD.dismiss();
                                                                    alertLayout.findViewById(R.id.alert_add_marks_progress).setVisibility(View.GONE);
                                                                }
                                                            });
                                                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                                                    requestQueue.add(jsonObjectRequest1);
                                                }
                                            });

                                        }
                                        else{
                                            AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                                            LayoutInflater inflater = LayoutInflater.from(mContext);
                                            final View alertLayout = inflater.inflate(R.layout.alert_input_info_no_marks,null);
                                            builder.setView(alertLayout);
                                            final AlertDialog alertD=builder.show();
                                            alertLayout.findViewById(R.id.alert_input_no_marks_cancel_button).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    alertD.dismiss();
                                                }
                                            });

                                        }

                                    }else{
                                        Toast.makeText(mContext, "Something is wrong.", Toast.LENGTH_SHORT).show();
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
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(jsonObjectRequest);
            }
        });
        //ADDING FUNCTIONALITES OF THE ADD NEW TERM BUTTON
        mAddNewTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View alertLayout = inflater.inflate(R.layout.alert_input_add_new_term,null);
                builder.setView(alertLayout);
                final AlertDialog alertD=builder.show();
                ((Button)alertLayout.findViewById(R.id.alert_add_term_cancel_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertD.dismiss();
                    }
                });
                ((Button)alertLayout.findViewById(R.id.alert_add_term_submit_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String requestUrl=mainUrl+"add_term.php";
                        String termName=((EditText)alertLayout.findViewById(R.id.alert_add_term_name_edit)).getText().toString();
                        JSONObject postparams = new JSONObject();
                        try {
                            postparams.put("stud_id", user_id);
                            postparams.put("term_name",termName);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ((ProgressBar)alertLayout.findViewById(R.id.alert_add_term_progress)).setVisibility(View.VISIBLE);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, postparams,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if(response.getInt("code")==202){
                                                Toast.makeText(mContext, "Term created successfully", Toast.LENGTH_SHORT).show();
                                                loadTermsWithSubjects();
                                            }else{
                                                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                            ((ProgressBar)alertLayout.findViewById(R.id.alert_add_term_progress)).setVisibility(View.GONE);
                                            alertD.dismiss();
                                        } catch (JSONException e) {
                                            ((ProgressBar)alertLayout.findViewById(R.id.alert_add_term_progress)).setVisibility(View.GONE);
                                            alertD.dismiss();
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(mContext, "Connection error", Toast.LENGTH_SHORT).show();
                                        ((ProgressBar)alertLayout.findViewById(R.id.alert_add_term_progress)).setVisibility(View.GONE);
                                        alertD.dismiss();
                                    }
                                });
                        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                        requestQueue.add(jsonObjectRequest);
                    }
                });
            }
        });
        loadTermsWithSubjects();
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadTermsWithSubjects(){
        mTermWiseSubjetsLoading.setVisibility(View.VISIBLE);
        String requestUrl=mainUrl+"termwisesubjects.php?stud_id="+user_id;
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202){
                                InputTermRecyclerViewAdapter termAdapter= new InputTermRecyclerViewAdapter(response.getJSONArray("response"),mContext, InputFragment.this);
                                mTermsRecyclerView.setAdapter(termAdapter);
                                mTermsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mTermWiseSubjetsLoading.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTermWiseSubjetsLoading.setVisibility(View.GONE);
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
}

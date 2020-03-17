package com.example.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

public class InputFragment extends Fragment {
    private String mainUrl;
    private Context mContext;
    private int user_id;
    private RecyclerView mTermsRecyclerView;
    private ImageButton mAddNewTermButton;
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
        String requestUrl=mainUrl+"termwisesubjects.php?stud_id=1";
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

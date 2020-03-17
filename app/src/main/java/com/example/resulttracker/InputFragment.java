package com.example.resulttracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    private RecyclerView mTermsRecyclerView;
    private LinearLayout mTermWiseSubjetsLoading;
    InputFragment(Context mContext){
        this.mContext=mContext;
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

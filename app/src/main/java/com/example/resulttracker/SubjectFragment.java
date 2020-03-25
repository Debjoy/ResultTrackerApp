package com.example.resulttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;

public class SubjectFragment extends Fragment {

    private String mainUrl="";
    private int stud_id;
    private Context mContext;
    private Button mSelectSubjectButton;
    private TextView mSubjectNameTv;
    private TextView mTermNameTv;
    private RecyclerView mSubjectRecyclerMarks;

    SubjectFragment(int stud_id, Context mContext){
        this.stud_id=stud_id;
        this.mContext=mContext;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_subject,container,false);
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSelectSubjectButton=view.findViewById(R.id.subject_select_subject_button);
        mSubjectNameTv=view.findViewById(R.id.subject_subject_name_tv);
        mTermNameTv=view.findViewById(R.id.subject_term_name_tv);
        mSubjectRecyclerMarks=view.findViewById(R.id.subject_marks_list_recycler);

        String requestUrl=mainUrl+"getsubjects.php?stud_id="+stud_id;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            if(response.getInt("code")==202){
                                final JSONArray arraySubjects=response.getJSONArray("response");

                                final ArrayList<String> subjectList= new ArrayList<>();
                                for(int i=0;i<arraySubjects.length();i++){
                                    subjectList.add(((JSONObject)arraySubjects.get(i)).getString("subject_name"));
                                }

                                final String[] itemnamesArray=subjectList.toArray(new String[subjectList.size()]);

                                mSelectSubjectButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("Select a Subject");
                                        builder.setItems(itemnamesArray, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {

                                                // will toast your selection
                                                Toast.makeText(mContext, "name: "+subjectList.get(item), Toast.LENGTH_SHORT).show();
                                                setSubject(item,arraySubjects);
                                                dialog.dismiss();

                                            }
                                        }).show();

                                    }
                                });
                                if(arraySubjects.length()>0){
                                    setSubject(0,arraySubjects);
                                }

                            }else{
                                Toast.makeText(mContext, "Something is wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    public void setSubject(int position, JSONArray arraySubjects){
        try {
            mSubjectNameTv.setText(((JSONObject)arraySubjects.get(position)).getString("subject_name"));
            mTermNameTv.setText(((JSONObject)arraySubjects.get(position)).getString("term_name"));
            int sub_id=((JSONObject)arraySubjects.get(position)).getInt("sub_id");


            String requestUrl=mainUrl+"getsubjects.php?subject="+sub_id;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code")==202){
                                    JSONArray subjectJsonArray=response.getJSONArray("response");
                                    SubjectMarksRecyclerViewAdapter adapter =new SubjectMarksRecyclerViewAdapter(subjectJsonArray);
                                    mSubjectRecyclerMarks.setAdapter(adapter);
                                    mSubjectRecyclerMarks.setLayoutManager(new LinearLayoutManager(mContext));

                                }else{
                                    Toast.makeText(mContext, "Something is wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

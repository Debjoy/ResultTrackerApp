package com.example.resulttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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
    private ProgressBar mFullSubjectProgress;
    private LinearLayout mFullViewSubject;

    private ProgressBar mIndividualMarksProgress;
    private LinearLayout mIndividualMarksView;

    private ArrayList<String> labels = new ArrayList<>();

    private HorizontalBarChart mChartSubject;

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
        mChartSubject=view.findViewById(R.id.subject_marks_charts);
        mFullViewSubject=view.findViewById(R.id.subject_full_view);
        mFullSubjectProgress=view.findViewById(R.id.subject_full_progress);

        mIndividualMarksProgress=view.findViewById(R.id.subject_individual_marks_progress);
        mIndividualMarksView=view.findViewById(R.id.subject_individual_marks_view);


        XAxis xAxis = mChartSubject.getXAxis();
        xAxis.setDrawGridLines(false);
        mChartSubject.getAxisLeft().setDrawGridLines(false);
        mChartSubject.getAxisRight().setDrawGridLines(false);

        mChartSubject.getAxisRight().setAxisMinimum(0f);
        mChartSubject.getAxisLeft().setAxisMinimum(0f);
        mChartSubject.getAxisRight().setAxisMaximum(100f);
        mChartSubject.getAxisLeft().setAxisMaximum(100f);

        mChartSubject.getAxisLeft().setDrawGridLines(false);
        mChartSubject.setPinchZoom(false);
        mChartSubject.setDrawBarShadow(false);
        mChartSubject.setDrawGridBackground(true);
        mChartSubject.setContentDescription("");
        mChartSubject.setTouchEnabled(false);
        mChartSubject.setDrawBarShadow(false);

        mChartSubject.getDescription().setEnabled(false);

        mChartSubject.getLegend().setEnabled(false);


        mFullViewSubject.setVisibility(View.GONE);
        mFullSubjectProgress.setVisibility(View.VISIBLE);
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

                                               setSubject(item,arraySubjects);
                                                dialog.dismiss();

                                            }
                                        }).show();

                                    }
                                });
                                if(arraySubjects.length()>0){
                                    setSubject(0,arraySubjects);
                                }
                                mFullViewSubject.setVisibility(View.VISIBLE);
                                mFullSubjectProgress.setVisibility(View.GONE);
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
            mIndividualMarksView.setVisibility(View.GONE);
            mIndividualMarksProgress.setVisibility(View.VISIBLE);
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
                                    mSubjectRecyclerMarks.setNestedScrollingEnabled(false);
                                    mSubjectRecyclerMarks.setLayoutManager(new LinearLayoutManager(mContext));


                                    ArrayList<BarEntry> entries = new ArrayList<>();
                                    ViewGroup.LayoutParams params = mChartSubject.getLayoutParams();
                                    params.height = 117 * subjectJsonArray.length();
                                    mChartSubject.setLayoutParams(params);
                                    for (int i = 0; i < subjectJsonArray.length(); i++) {
                                        JSONObject obj = (JSONObject) subjectJsonArray.get(i);
                                        double percentage = obj.getDouble("percentage");
                                        String exam_name = obj.getString("exam_name");
                                        String exam_number = obj.getString("assesment_number");
                                        String final_label = exam_name;
                                        if (final_label.length() < 11) {
                                            int number = 11 - final_label.length();
                                            for (int j = 0; j < number; j++)
                                                final_label += "  ";
                                        } else {
                                            final_label = final_label.substring(0, 9) + "..";
                                        }
                                        final_label = final_label + " #" + exam_number;
                                        labels.add(final_label);
                                        entries.add(new BarEntry((i + 1), (float) percentage));
                                    }


                                    XAxis xAxis = mChartSubject.getXAxis();
                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

                                    ValueFormatter formatter = new ValueFormatter() {


                                        @Override
                                        public String getFormattedValue(float value) {
                                            return labels.get((int) value - 1);
                                        }
                                    };
                                    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                                    xAxis.setValueFormatter(formatter);
                                    xAxis.setTextColor(Color.parseColor("#FFFFFF"));
                                    xAxis.setXOffset(75f);

                                    BarDataSet dataset = new BarDataSet(entries, "score in percentage %");
                                    //if(position%2==0)
                                    dataset.setColor(Color.parseColor("#263238"));
                                    //else
                                    //dataset.setColor(Color.parseColor("#263238"));
                                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                                    dataSets.add(dataset);
                                    BarData data = new BarData(dataSets);
                                    mChartSubject.setData(data);
                                    mChartSubject.setNoDataText("data not available lol");
                                    mChartSubject.invalidate();

                                    mIndividualMarksView.setVisibility(View.VISIBLE);
                                    mIndividualMarksProgress.setVisibility(View.GONE);

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

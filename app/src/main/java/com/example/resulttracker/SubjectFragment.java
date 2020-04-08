package com.example.resulttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectFragment extends Fragment {

    private String mainUrl="";
    private int stud_id;
    private String stud_pass;
    private Context mContext;
    private Button mSelectSubjectButton;
    private TextView mSubjectNameTv;
    private TextView mTermNameTv;
    private RecyclerView mSubjectRecyclerMarks;
    private ProgressBar mFullSubjectProgress;
    private LinearLayout mFullViewSubject;
    private LinearLayout mFullNoSubject;

    private ProgressBar mIndividualMarksProgress;
    private LinearLayout mIndividualMarksView;

    private TextView mDifferencePercentageTv;
    private ImageView mDifferencePercentageImage;
    private double mOASP;

    private TextView mRankTv;
    private TextView mRankTotalTv;
    private View mainView;

    private ArrayList<String> labels = new ArrayList<>();

    private HorizontalBarChart mChartSubject;

    SubjectFragment(int stud_id, String stud_pass, Context mContext){
        this.stud_id=stud_id;
        this.mContext=mContext;
        this.stud_pass=stud_pass;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_subject,container,false);
        mainUrl="https://atdebjoy.com/others/api/perform/";
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView=view;
        mSelectSubjectButton=view.findViewById(R.id.subject_select_subject_button);
        mSubjectNameTv=view.findViewById(R.id.subject_subject_name_tv);
        mTermNameTv=view.findViewById(R.id.subject_term_name_tv);
        mSubjectRecyclerMarks=view.findViewById(R.id.subject_marks_list_recycler);
        mChartSubject=view.findViewById(R.id.subject_marks_charts);
        mFullViewSubject=view.findViewById(R.id.subject_full_view);
        mFullSubjectProgress=view.findViewById(R.id.subject_full_progress);
        mFullNoSubject=view.findViewById(R.id.subject_full_no_term);

        mIndividualMarksProgress=view.findViewById(R.id.subject_individual_marks_progress);
        mIndividualMarksView=view.findViewById(R.id.subject_individual_marks_view);

        mDifferencePercentageTv=view.findViewById(R.id.subject_difference_tv);
        mDifferencePercentageImage=view.findViewById(R.id.subject_difference_up_down);
        mOASP=0.0;

        mRankTv=view.findViewById(R.id.subject_rank_tv);
        mRankTotalTv=view.findViewById(R.id.subject_rank_total_tv);


        RoundedHorizontalBarChartRenderer roundedBarChartRenderer= new RoundedHorizontalBarChartRenderer(mChartSubject,mChartSubject.getAnimator(),mChartSubject.getViewPortHandler());
        roundedBarChartRenderer.setmRadius(40f);
        mChartSubject.setRenderer(roundedBarChartRenderer);

        XAxis xAxis = mChartSubject.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        mChartSubject.getAxisLeft().setDrawGridLines(false);
        mChartSubject.getAxisRight().setDrawGridLines(false);
        mChartSubject.getAxisLeft().setDrawAxisLine(true);
        mChartSubject.getAxisRight().setDrawAxisLine(true);
        mChartSubject.getAxisLeft().setDrawLabels(true);

        mChartSubject.getAxisRight().setAxisMinimum(0f);
        mChartSubject.getAxisLeft().setAxisMinimum(0f);
        mChartSubject.getAxisRight().setAxisMaximum(100f);
        mChartSubject.getAxisLeft().setAxisMaximum(100f);

        mChartSubject.getAxisLeft().setDrawGridLines(false);
        mChartSubject.setPinchZoom(false);
        mChartSubject.setDrawBarShadow(false);
        mChartSubject.setContentDescription("");
        mChartSubject.setTouchEnabled(false);
        mChartSubject.setDrawBarShadow(false);

        mChartSubject.getDescription().setEnabled(false);

        mChartSubject.getLegend().setEnabled(false);


        mFullViewSubject.setVisibility(View.GONE);
        mFullSubjectProgress.setVisibility(View.VISIBLE);
        mFullNoSubject.setVisibility(View.GONE);
        String requestUrl=mainUrl+"get_subjects.php?stud_id="+stud_id+"&pass="+stud_pass;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            if(response.getInt("code")==202){
                                final JSONArray arraySubjects=response.getJSONArray("response");
                                if(arraySubjects.length()>0){
                                    final ArrayList<String> subjectList= new ArrayList<>();
                                    for(int i=0;i<arraySubjects.length();i++){
                                        subjectList.add(((JSONObject)arraySubjects.get(i)).getString("subject_name"));
                                    }

                                    final String[] itemnamesArray=subjectList.toArray(new String[subjectList.size()]);

                                    if(response.getJSONObject("average").getInt("code")==202){
                                        mOASP=response.getJSONObject("average").getDouble("response");
                                    }

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
                                    mRankTotalTv.setText(""+arraySubjects.length());
                                    mFullViewSubject.setVisibility(View.VISIBLE);
                                    mFullSubjectProgress.setVisibility(View.GONE);
                                    mFullNoSubject.setVisibility(View.GONE);
                                }else{
                                    mFullViewSubject.setVisibility(View.GONE);
                                    mFullSubjectProgress.setVisibility(View.GONE);
                                    mFullNoSubject.setVisibility(View.VISIBLE);

                                    mainView.findViewById(R.id.subject_full_no_term_input_section).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,new InputFragment(mContext, stud_id, stud_pass)).commit();
                                            ((BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation_view)).getMenu().getItem(3).setChecked(true);
                                        }
                                    });
                                }
                            }else if(response.getInt("code")==351){
                                Toast.makeText(mContext, "Authentication Error", Toast.LENGTH_SHORT).show();
                                Intent mainActivity=new Intent(mContext, MainActivity.class);
                                mContext.startActivity(mainActivity);
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

            mRankTv.setText(convertIndexToRank(position));
            mSubjectNameTv.setText(((JSONObject)arraySubjects.get(position)).getString("subject_name"));

            double subjectAverage=arraySubjects.getJSONObject(position).getDouble("average");
            if(mOASP>subjectAverage){
                mDifferencePercentageTv.setText((Math.round((mOASP-subjectAverage)*10))/10.0+"%");
                mDifferencePercentageImage.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
            }else{
                mDifferencePercentageTv.setText((Math.round((subjectAverage-mOASP)*10))/10.0+"%");
            }
            mTermNameTv.setText(short_Term(((JSONObject)arraySubjects.get(position)).getString("term_name")));
            int sub_id=((JSONObject)arraySubjects.get(position)).getInt("sub_id");


            String requestUrl=mainUrl+"get_subjects.php?subject="+sub_id+"&stud_id="+stud_id+"&pass="+stud_pass;

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
                                    xAxis.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                                    xAxis.setXOffset(75f);

                                    BarDataSet dataset = new BarDataSet(entries, "score in percentage %");
                                    //if(position%2==0)
                                    //dataset.setColor(Color.parseColor("#263238"));
                                    dataset.setColor(mContext.getResources().getColor(R.color.colorAccent));
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

                                }else if(response.getInt("code")==351){
                                    Toast.makeText(mContext, "Authentication Error", Toast.LENGTH_SHORT).show();
                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                    mContext.startActivity(mainActivity);
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

    public String convertIndexToRank(int position){
        position++;
        String result="";
        int lastDigit=position%10;
        if(lastDigit==1){
            result+=position+"st";
        }else if(lastDigit==2)
            result+=position+"nd";
        else if(lastDigit==3)
            result+=position+"rd";
        else
            result+=position+"th";
        return result;
    }

    public String short_Term(String term){
        String return_Term="";
        if(term.length()>6){
            return_Term=term.substring(0,2)+".."+term.substring(term.length()-2);
        }
        return return_Term;
    }
}

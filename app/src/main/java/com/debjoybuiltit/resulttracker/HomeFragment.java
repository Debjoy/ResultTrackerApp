package com.debjoybuiltit.resulttracker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.ibrahimsn.lib.SmoothBottomBar;

public class HomeFragment extends Fragment {
    private String mainUrl;
    private int stud_id;
    private String stud_pass;
    private Context mContext;
    int pStatus = 0;
    private Handler handler = new Handler();

    private TextView mCirclarProgressText;
    private ProgressBar mCirclularProgress;
    private TextView mTotalMarksEntry;
    private Button mChartResetButton;

    private LinearLayout mBestOneLayout;
    private TextView mBestOneSubject;
    private TextView mBestOneMarks;

    private LinearLayout mBestTwoLayout;
    private TextView mBestTwoSubject;
    private TextView mBestTwoMarks;

    private LinearLayout mBestThreeLayout;
    private TextView mBestThreeSubject;
    private TextView mBestThreeMarks;


    private LinearLayout mWorstOneLayout;
    private TextView mWorstOneSubject;
    private TextView mWorstOneMarks;

    private LinearLayout mWorstTwoLayout;
    private TextView mWorstTwoSubject;
    private TextView mWorstTwoMarks;

    private LinearLayout mWorstThreeLayout;
    private TextView mWorstThreeSubject;
    private TextView mWorstThreeMarks;

    private LineChart mLineChart;

    private ScrollView mHomeScrollView;
    private ProgressBar mHomeProgressBar;
    private LinearLayout mHomeNoMarks;
    private View mainView;
    private DashActivity mDash;

    HomeFragment(int stud_id, String stud_pass,Context mContext, DashActivity mDash){
        this.stud_id=stud_id;
        this.mContext=mContext;
        this.stud_pass=stud_pass;
        this.mDash=mDash;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_home,container,false);
        mainUrl="https://atdebjoy.com/others/api/perform/";
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView=view;
        mCirclarProgressText=view.findViewById(R.id.circularProgressBar_tv);
        mCirclularProgress=view.findViewById(R.id.circularProgressbar);
        mTotalMarksEntry=view.findViewById(R.id.home_total_marks_entry_tv);
        mChartResetButton=view.findViewById(R.id.home_chart_reset_button);

        mBestOneLayout=view.findViewById(R.id.home_best_one_layout);
        mBestOneSubject=view.findViewById(R.id.home_best_one_subject);
        mBestOneMarks=view.findViewById(R.id.home_best_one_marks);

        mBestTwoLayout=view.findViewById(R.id.home_best_two_layout);
        mBestTwoSubject=view.findViewById(R.id.home_best_two_subject);
        mBestTwoMarks=view.findViewById(R.id.home_best_two_marks);

        mBestThreeLayout=view.findViewById(R.id.home_best_three_layout);
        mBestThreeSubject=view.findViewById(R.id.home_best_three_subject);
        mBestThreeMarks=view.findViewById(R.id.home_best_three_marks);


        mWorstOneLayout=view.findViewById(R.id.home_worst_one_layout);
        mWorstOneSubject=view.findViewById(R.id.home_worst_one_subject);
        mWorstOneMarks=view.findViewById(R.id.home_worst_one_marks);

        mWorstTwoLayout=view.findViewById(R.id.home_worst_two_layout);
        mWorstTwoSubject=view.findViewById(R.id.home_worst_two_subject);
        mWorstTwoMarks=view.findViewById(R.id.home_worst_two_marks);

        mWorstThreeLayout=view.findViewById(R.id.home_worst_three_layout);
        mWorstThreeSubject=view.findViewById(R.id.home_worst_three_subject);
        mWorstThreeMarks=view.findViewById(R.id.home_worst_three_marks);

        mLineChart=view.findViewById(R.id.home_line_chart);

        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getAxisRight().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawGridLines(false);


        mLineChart.getAxisRight().setAxisMaximum(100f);
        mLineChart.getAxisLeft().setAxisMaximum(100f);

        mLineChart.setPinchZoom(false);
        //mLineChart.setTouchEnabled(false);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(!mLineChart.isFullyZoomedOut()){
                    mChartResetButton.setVisibility(View.VISIBLE);
                }else{
                    mChartResetButton.setVisibility(View.GONE);
                }
                return false;
            }

        });

        mChartResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLineChart.fitScreen();
                mChartResetButton.setVisibility(View.GONE);
            }
        });

        mHomeScrollView=view.findViewById(R.id.home_scroll_scrollView);
        mHomeProgressBar=view.findViewById(R.id.home_progress_basic);
        mHomeNoMarks=view.findViewById(R.id.home_full_no_term);

        mHomeProgressBar.setVisibility(View.VISIBLE);
        mHomeScrollView.setVisibility(View.GONE);
        mHomeNoMarks.setVisibility(View.GONE);


        loadData();
    }

    public void loadData(){
        String requestUrl=mainUrl+"basic_details.php?stud_id="+stud_id+"&pass="+stud_pass;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            if(response.getInt("code")==202){
                                JSONObject average=response.getJSONObject("average");
                                JSONObject average_response=average.getJSONObject("response");

                                JSONObject responseAll=response.getJSONObject("all");
                                final JSONArray allMarksResponseList= responseAll.getJSONArray("response");

                                if(allMarksResponseList.length()>2){
                                    final double average_value=Math.round(average_response.getDouble("value") * 10) / 10.0;;

                                    mCirclularProgress.setProgress(0);   // Main Progress
                                    mCirclularProgress.setSecondaryProgress(100); // Secondary Progress
                                    mCirclularProgress.setMax(100); // Maximum Progress

                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            while (pStatus < average_value) {
                                                pStatus += 1;

                                                handler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        // TODO Auto-generated method stub
                                                        mDash.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mCirclularProgress.setProgress(pStatus);
                                                                mCirclarProgressText.setText(pStatus + "%");
                                                            }
                                                        });

                                                    }
                                                });
                                                try {
                                                    Thread.sleep(16); //thread will take approx 3 seconds to finish,change its value according to your needs
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            mDash.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mCirclularProgress.setProgress((int)average_value);
                                                    mCirclarProgressText.setText(average_value + "%");
                                                }
                                            });

                                        }
                                    }).start();
                                    final int allMarksResponseListLength=allMarksResponseList.length();

                                    new Thread(new Runnable() {
                                        private int count=0;
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub

                                            while (count < allMarksResponseListLength) {
                                                count += 1;

                                                handler.post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        // TODO Auto-generated method stub
                                                        mTotalMarksEntry.setText(count+"");

                                                    }
                                                });
                                                try {
                                                    Thread.sleep(16); //thread will take approx 3 seconds to finish,change its value according to your needs
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).start();

                                    List<Entry> entries = new ArrayList<Entry>();

                                    for(int i=0;i<allMarksResponseListLength;i++){
                                        entries.add(new Entry((float)i, (float)((JSONObject)allMarksResponseList.get(i)).getDouble("percentage")));
                                    }

                                    LineDataSet dataSet = new LineDataSet(entries, "Marks");
                                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                                    dataSet.setDrawFilled(true);
                                    dataSet.setFillColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                                    dataSet.setDrawCircles(false);
                                    dataSet.setFillAlpha(255);
                                    dataSet.setDrawValues(false);

                                    dataSet.setColor(mContext.getResources().getColor(R.color.colorAccentLight));
                                    LineData lineData = new LineData(dataSet);
                                    mLineChart.setData(lineData);

                                    ValueFormatter formatter = new ValueFormatter() {

                                        @Override
                                        public String getFormattedValue(float value) {
                                            String label="";
                                            try {
                                                label= short_Term(((JSONObject)allMarksResponseList.get((int) value)).getString("term_name"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return label;
                                        }

                                        // we don't draw numbers, so no decimal digits needed
                                    };
                                    XAxis xAxis = mLineChart.getXAxis();
                                    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                                    xAxis.setValueFormatter(formatter);
                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                                    mLineChart.invalidate();


                                    JSONObject top_three=response.getJSONObject("top_three");
                                    JSONArray top_three_array=top_three.getJSONArray("response");

                                    if(top_three_array.length()>0){
                                        mBestOneLayout.setVisibility(View.VISIBLE);
                                        mBestOneSubject.setText(((JSONObject)top_three_array.get(0)).getString("subject_name"));
                                        mBestOneMarks.setText((Math.round(10*((JSONObject)top_three_array.get(0)).getDouble("average"))/10f)+" %");
                                    }
                                    if(top_three_array.length()>1){
                                        mBestTwoLayout.setVisibility(View.VISIBLE);
                                        mBestTwoSubject.setText(((JSONObject)top_three_array.get(1)).getString("subject_name"));
                                        mBestTwoMarks.setText((Math.round(10*((JSONObject)top_three_array.get(1)).getDouble("average"))/10f)+" %");
                                    }
                                    if(top_three_array.length()>2){
                                        mBestThreeLayout.setVisibility(View.VISIBLE);
                                        mBestThreeSubject.setText(((JSONObject)top_three_array.get(2)).getString("subject_name"));
                                        mBestThreeMarks.setText((Math.round(10*((JSONObject)top_three_array.get(2)).getDouble("average"))/10f)+" %");
                                    }

                                    JSONObject worst_three=response.getJSONObject("bottom_three");
                                    JSONArray worst_three_array=worst_three.getJSONArray("response");
                                    if(worst_three_array.length()>0){
                                        mWorstOneLayout.setVisibility(View.VISIBLE);
                                        mWorstOneSubject.setText(((JSONObject)worst_three_array.get(0)).getString("subject_name"));
                                        mWorstOneMarks.setText((Math.round(10*((JSONObject)worst_three_array.get(0)).getDouble("average"))/10f)+" %");
                                    }
                                    if(worst_three_array.length()>1){
                                        mWorstTwoLayout.setVisibility(View.VISIBLE);
                                        mWorstTwoSubject.setText(((JSONObject)worst_three_array.get(1)).getString("subject_name"));
                                        mWorstTwoMarks.setText((Math.round(10*((JSONObject)worst_three_array.get(1)).getDouble("average"))/10f)+" %");
                                    }
                                    if(worst_three_array.length()>2){
                                        mWorstThreeLayout.setVisibility(View.VISIBLE);
                                        mWorstThreeSubject.setText(((JSONObject)worst_three_array.get(2)).getString("subject_name"));
                                        mWorstThreeMarks.setText((Math.round(10*((JSONObject)worst_three_array.get(2)).getDouble("average"))/10f)+" %");
                                    }
                                    mHomeProgressBar.setVisibility(View.GONE);
                                    mHomeScrollView.setVisibility(View.VISIBLE);
                                    mHomeNoMarks.setVisibility(View.GONE);
                                }else{
                                    mHomeProgressBar.setVisibility(View.GONE);
                                    mHomeScrollView.setVisibility(View.GONE);
                                    mHomeNoMarks.setVisibility(View.VISIBLE);

                                    mainView.findViewById(R.id.home_full_no_term_input_section).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,new InputFragment(mContext, stud_id,stud_pass)).commit();
                                            //((BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation_view)).getMenu().getItem(3).setChecked(true);
                                            ((SmoothBottomBar)getActivity().findViewById(R.id.bottom_navigation_view)).setActiveItem(3);
                                        }
                                    });
                                }



                            }else if(response.getInt("code")==351){
                                Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                Intent mainActivity=new Intent(mContext, MainActivity.class);
                                mContext.startActivity(mainActivity);
                            }else{
                                Toasty.warning(mContext, "Something is wrong", Toasty.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.warning(mContext, "Network error", Toasty.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    public String short_Term(String term){
        String return_Term="";
        if(term.length()>6){
            return_Term=term.substring(0,2)+".."+term.substring(term.length()-2);
        }else{
            return_Term=term;
        }
        return return_Term;
    }
}

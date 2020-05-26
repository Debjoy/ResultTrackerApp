package com.debjoybuiltit.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
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

import es.dmoral.toasty.Toasty;

public class TermsFragment extends Fragment {
    private String mainUrl;
    private HorizontalBarChart termAverageBarChart;
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<Integer> mSubid = new ArrayList<>();
    private ArrayList<String> mSubName = new ArrayList<>();
    private RecyclerView mHomeRecyclerView;
    private TextView mCurrentTermName;
    private Button mChangeTermButton;
    private ProgressBar mHomeProgressAverage;
    private LinearLayout mTermFullView;
    private ProgressBar mTermFullProgress;
    private LinearLayout mTermFullnoTerm;
    private Context mContext;
    private int user_id;
    private String user_pass;
    private View mainView;

    TermsFragment(int user_id, String user_pass, Context mContext){
        this.mContext=mContext;
        this.user_id=user_id;
        this.user_pass=user_pass;
    }
    public TermsFragment() {
        // doesn't do anything special
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_term,container,false);
        mainUrl="https://atdebjoy.com/others/api/perform/";
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView=view;
        termAverageBarChart=view.findViewById(R.id.termwise_average_barchart);
        mHomeRecyclerView=view.findViewById(R.id.term_recycler_view);
        mCurrentTermName=view.findViewById(R.id.term_term_name_heading);
        mChangeTermButton=view.findViewById(R.id.term_change_term);
        mHomeProgressAverage=view.findViewById(R.id.term_progress_average_term);
        mTermFullView=view.findViewById(R.id.term_full_view);
        mTermFullProgress=view.findViewById(R.id.term_full_progress);
        mTermFullnoTerm=view.findViewById(R.id.term_full_no_term);


        mTermFullView.setVisibility(View.GONE);
        mTermFullnoTerm.setVisibility(View.GONE);
        mTermFullProgress.setVisibility(View.VISIBLE);
        String requestUrl=mainUrl+"get_list_term.php?stud_id="+user_id+"&pass="+user_pass;
        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202) {
                                JSONArray responseArray=response.getJSONArray("response");

                                final ArrayList<Integer> mTermIdList= new ArrayList<>();
                                final ArrayList<String> mTermNameList = new ArrayList<>();
                                for (int i = 0; i < responseArray.length(); i++) {
                                    mTermIdList.add(responseArray.getJSONObject(i).getInt("term_id"));
                                    mTermNameList.add(responseArray.getJSONObject(i).getString("term_name"));
                                }

                                if(mTermIdList.size()>0){
                                    showTermWiseAverageChart(mTermIdList.get(mTermIdList.size()-1));
                                    mCurrentTermName.setText(mTermNameList.get(mTermNameList.size()-1));
                                    populteRecyclerSubjects(mTermNameList.size()-1, mTermIdList );
                                }else{
                                    //GO TO THE INPUT FRAGMENT OR DO SOMETHING
                                    mTermFullView.setVisibility(View.GONE);
                                    mTermFullnoTerm.setVisibility(View.VISIBLE);
                                    mTermFullProgress.setVisibility(View.GONE);
                                }

                                mainView.findViewById(R.id.term_full_no_term_input_section).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_dashboard_frame,new InputFragment(mContext, user_id, user_pass)).commit();
                                        ((BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation_view)).getMenu().getItem(3).setChecked(true);
                                    }
                                });

                                mChangeTermButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String[] itemnamesArray=mTermNameList.toArray(new String[mTermNameList.size()]);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Select a Term");
                                        builder.setItems(itemnamesArray, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(mTermIdList.size()>0){
                                                    showTermWiseAverageChart(mTermIdList.get(i));
                                                    mCurrentTermName.setText(mTermNameList.get(i));
                                                    populteRecyclerSubjects(i, mTermIdList);
                                                }else{
                                                    Toasty.warning(getContext().getApplicationContext(), "No Terms Found", Toasty.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                });

                            }else if(response.getInt("code")==351){
                                Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                Intent mainActivity=new Intent(mContext, MainActivity.class);
                                mContext.startActivity(mainActivity);
                            }else{
                                Toasty.warning(mContext, "Something went wrong", Toasty.LENGTH_SHORT).show();
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
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonArrReq);




    }

    public void showTermWiseAverageChart(int term_id){


        termAverageBarChart.setVisibility(View.GONE);
        mHomeProgressAverage.setVisibility(View.VISIBLE);

        RoundedHorizontalBarChartRenderer roundedBarChartRenderer= new RoundedHorizontalBarChartRenderer(termAverageBarChart,termAverageBarChart.getAnimator(),termAverageBarChart.getViewPortHandler());
        roundedBarChartRenderer.setmRadius(40f);
        termAverageBarChart.setRenderer(roundedBarChartRenderer);
        XAxis xAxis = termAverageBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        termAverageBarChart.getAxisLeft().setDrawGridLines(false);
        termAverageBarChart.getAxisRight().setDrawGridLines(false);

        termAverageBarChart.getAxisRight().setAxisMinimum(0f);
        termAverageBarChart.getAxisLeft().setAxisMinimum(0f);
        termAverageBarChart.getAxisRight().setAxisMaximum(100f);
        termAverageBarChart.getAxisLeft().setAxisMaximum(100f);

        termAverageBarChart.getAxisLeft().setDrawGridLines(false);
        termAverageBarChart.setPinchZoom(false);
        termAverageBarChart.setDrawBarShadow(false);
        termAverageBarChart.setDrawGridBackground(false);
        termAverageBarChart.setContentDescription("");
        termAverageBarChart.setTouchEnabled(false);
        termAverageBarChart.setDrawBarShadow(false);

        termAverageBarChart.getDescription().setEnabled(false);
        termAverageBarChart.getLegend().setEnabled(false);

        String requestUrl=mainUrl+"get_term_wise.php?term="+term_id+"&stud_id="+user_id+"&pass="+user_pass;
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Success Callback

                        try {

                            if(response.length()>0){
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                ViewGroup.LayoutParams params = termAverageBarChart.getLayoutParams();
                                params.height=117*response.length();
                                termAverageBarChart.setLayoutParams(params);
                                for(int i=0;i<response.length();i++){
                                    JSONObject obj= (JSONObject) response.get(i);
                                    double average=obj.getDouble("avg");
                                    double full_marks=obj.getDouble("full_marks");
                                    double percentage=(average/full_marks)*100;
                                    String exam_name=obj.getString("exam_name");
                                    String exam_number=obj.getString("assesment_number");
                                    String final_label=exam_name;
                                    if(final_label.length()<11){
                                        int number=11-final_label.length();
                                        for(int j=0;j<number;j++)
                                            final_label+="  ";
                                    }else{
                                        final_label=final_label.substring(0,9)+"..";
                                    }
                                    final_label=final_label+" #"+exam_number;
                                    labels.add(final_label);
                                    entries.add(new BarEntry((i+1),(float)percentage));
                                }


                                XAxis xAxis = termAverageBarChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

                                ValueFormatter formatter = new ValueFormatter() {


                                    @Override
                                    public String getFormattedValue(float value) {
                                        return labels.get((int) value-1);
                                    }
                                };
                                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                                xAxis.setValueFormatter(formatter);
                                xAxis.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                                xAxis.setXOffset(75f);

                                BarDataSet dataset = new BarDataSet(entries, "score in percentage %");
                                dataset.setColor(mContext.getResources().getColor(R.color.colorAccentLight));

                                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                                dataSets.add(dataset);
                                BarData data = new BarData(dataSets);
                                termAverageBarChart.setData(data);
                                termAverageBarChart.setNoDataText("data not available");
                                termAverageBarChart.invalidate();

                                termAverageBarChart.setVisibility(View.VISIBLE);
                                mHomeProgressAverage.setVisibility(View.GONE);

                                mTermFullView.setVisibility(View.VISIBLE);
                                mTermFullnoTerm.setVisibility(View.GONE);
                                mTermFullProgress.setVisibility(View.GONE);
                            }else {
                                mTermFullView.setVisibility(View.GONE);
                                mTermFullnoTerm.setVisibility(View.VISIBLE);
                                mTermFullProgress.setVisibility(View.GONE);
                                ((TextView)mainView.findViewById(R.id.term_full_no_term_heading)).setText("Hey, seems like you have not added any Marks");
                                ((TextView)mainView.findViewById(R.id.term_full_no_term_content)).setText("Go to the input section,\nadd a subject in a term, \n" +
                                        "and then finally add some marks\n to view this page");
                            }

                            //Log.i("RESULT",termAverageBarChart.getBarData().getDataSets().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure Callback
                        Toasty.warning(mContext, "Network error", Toasty.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonArrReq);
    }

    public void populteRecyclerSubjects(int term_position, ArrayList <Integer>mTermIdList){
        String requestUrl=mainUrl+"get_subjects.php?term_id="+mTermIdList.get(term_position)+"&stud_id="+user_id+"&pass="+user_pass;
        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202) {
                                mSubid.clear();
                                mSubName.clear();
                                JSONArray responseArray= response.getJSONArray("response");
                                for (int i = 0; i < responseArray.length(); i++) {
                                    mSubid.add(responseArray.getJSONObject(i).getInt("sub_id"));
                                    mSubName.add(responseArray.getJSONObject(i).getString("sub_name"));
                                }

                                HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(mContext, mSubid, mSubName, user_id, user_pass);
                                mHomeRecyclerView.setAdapter(adapter);
                                mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                //mHomeRecyclerView.setNestedScrollingEnabled(false);
                            }else if(response.getInt("code")==351){
                                Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                Intent mainActivity=new Intent(mContext, MainActivity.class);
                                mContext.startActivity(mainActivity);
                            }else{
                                Toasty.warning(mContext, "Something went wrong", Toasty.LENGTH_SHORT).show();
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
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonArrReq);
    }
}

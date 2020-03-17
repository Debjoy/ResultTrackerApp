package com.example.resulttracker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private String mainUrl;
    private HorizontalBarChart termAverageBarChart;
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<Integer> mSubid = new ArrayList<>();
    private ArrayList<String> mSubName = new ArrayList<>();
    private RecyclerView mHomeRecyclerView;
    private ArrayList<Integer> mTermIdList;
    private ArrayList<String> mTermNameList;
    private TextView mCurrentTermName;
    private Button mChangeTermButton;
    private ProgressBar mHomeProgressAverage;
    private Context mContext;

    HomeFragment(ArrayList<Integer> mTermIdList,ArrayList<String> mTermNameList){
        this.mTermIdList=mTermIdList;
        this.mTermNameList=mTermNameList;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.dashboard_home,container,false);
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        termAverageBarChart=view.findViewById(R.id.termwise_average_barchart);
        mHomeRecyclerView=view.findViewById(R.id.home_recycler_view);
        mCurrentTermName=view.findViewById(R.id.home_term_name_heading);
        mChangeTermButton=view.findViewById(R.id.home_change_term);
        mContext=getContext().getApplicationContext();
        mHomeProgressAverage=view.findViewById(R.id.home_progress_average_term);

        if(mTermIdList.size()>0)
        showTermWiseAverageChart(mTermIdList.get(mTermIdList.size()-1));
        mCurrentTermName.setText(mTermNameList.get(mTermNameList.size()-1));


        populteRecyclerSubjects(mTermNameList.size()-1);


        mChangeTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] itemnamesArray=mTermNameList.toArray(new String[mTermNameList.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select a Term");
                builder.setItems(itemnamesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext().getApplicationContext(), "POSITION: "+i, Toast.LENGTH_SHORT).show();
                        if(mTermIdList.size()>0){
                            showTermWiseAverageChart(mTermIdList.get(i));
                            mCurrentTermName.setText(mTermNameList.get(i));
                            populteRecyclerSubjects(i);
                        }else{
                            Toast.makeText(getContext().getApplicationContext(), "No Terms Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void showTermWiseAverageChart(int term_id){


        termAverageBarChart.setVisibility(View.GONE);
        mHomeProgressAverage.setVisibility(View.VISIBLE);

        XAxis xAxis = termAverageBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        termAverageBarChart.getAxisLeft().setDrawGridLines(false);
        termAverageBarChart.getAxisRight().setDrawGridLines(false);

        termAverageBarChart.getAxisRight().setAxisMinimum(0f);
        termAverageBarChart.getAxisLeft().setAxisMinimum(0f);
        termAverageBarChart.getAxisRight().setAxisMaximum(100f);
        termAverageBarChart.getAxisLeft().setAxisMaximum(100f);

        termAverageBarChart.getAxisLeft().setDrawGridLines(false);
        termAverageBarChart.setPinchZoom(false);
        termAverageBarChart.setDrawBarShadow(false);
        termAverageBarChart.setDrawGridBackground(true);
        termAverageBarChart.setContentDescription("");
        termAverageBarChart.setTouchEnabled(false);
        termAverageBarChart.setDrawBarShadow(false);

        termAverageBarChart.getDescription().setEnabled(false);

        String requestUrl=mainUrl+"termwise.php?term="+term_id;
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Success Callback

                        try {
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
                            xAxis.setTextColor(Color.parseColor("#FFFFFF"));
                            xAxis.setXOffset(75f);

                            BarDataSet dataset = new BarDataSet(entries, "score in percentage %");
                            dataset.setColor(Color.parseColor("#263238"));
                            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                            dataSets.add(dataset);
                            BarData data = new BarData(dataSets);
                            termAverageBarChart.setData(data);
                            termAverageBarChart.setNoDataText("data not available");
                            termAverageBarChart.invalidate();

                            termAverageBarChart.setVisibility(View.VISIBLE);
                            mHomeProgressAverage.setVisibility(View.GONE);

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
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrReq);
    }



    public void populteRecyclerSubjects(int term_position){
        mContext=getActivity().getApplicationContext();
        String requestUrl=mainUrl+"getsubjects.php?term_id="+mTermIdList.get(term_position);
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

                                HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(mContext, mSubid, mSubName);
                                mHomeRecyclerView.setAdapter(adapter);
                                mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                //mHomeRecyclerView.setNestedScrollingEnabled(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(jsonArrReq);
    }
}

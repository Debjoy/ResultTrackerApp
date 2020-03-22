package com.example.resulttracker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    private int stud_id;
    private Context mContext;
    int pStatus = 0;
    private Handler handler = new Handler();

    private TextView mCirclarProgressText;
    private ProgressBar mCirclularProgress;
    private TextView mTotalMarksEntry;

    private ScrollView mHomeScrollView;
    private ProgressBar mHomeProgressBar;

    HomeFragment(int stud_id,Context mContext){
        this.stud_id=stud_id;
        this.mContext=mContext;
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
        mCirclarProgressText=view.findViewById(R.id.circularProgressBar_tv);
        mCirclularProgress=view.findViewById(R.id.circularProgressbar);
        mTotalMarksEntry=view.findViewById(R.id.home_total_marks_entry_tv);

        mHomeScrollView=view.findViewById(R.id.home_scroll_scrollView);
        mHomeProgressBar=view.findViewById(R.id.home_progress_basic);

        mHomeProgressBar.setVisibility(View.VISIBLE);
        mHomeScrollView.setVisibility(View.GONE);

        loadData();
    }

    public void loadData(){
        String requestUrl=mainUrl+"basic_details.php?stud_id="+stud_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        mHomeProgressBar.setVisibility(View.GONE);
                        mHomeScrollView.setVisibility(View.VISIBLE);
                        try {
                            if(response.getInt("code")==202){
                                JSONObject average=response.getJSONObject("average");
                                JSONObject average_response=average.getJSONObject("response");
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
                                                    mCirclularProgress.setProgress(pStatus);
                                                    mCirclarProgressText.setText(pStatus + "%");

                                                }
                                            });
                                            try {
                                                Thread.sleep(16); //thread will take approx 3 seconds to finish,change its value according to your needs
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        mCirclularProgress.setProgress((int)average_value);
                                        mCirclarProgressText.setText(average_value + "%");
                                    }
                                }).start();
                                JSONObject responseAll=response.getJSONObject("all");
                                JSONArray allMarksResponseList= responseAll.getJSONArray("response");

                                final int allMarksResponseListLength=allMarksResponseList.length();

                                new Thread(new Runnable() {
                                    private int count=0;
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        while (count <= allMarksResponseListLength) {
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


}

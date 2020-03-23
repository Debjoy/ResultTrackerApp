package com.example.resulttracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolderHome>{
    private static final String TAG = "HomeRecyclerViewAdapter";

    private ArrayList<Integer> mSubId;
    private ArrayList<String> mSubName;
    private ArrayList<String> labels = new ArrayList<>();
    private Context mContext;
    private String mainUrl;

    public HomeRecyclerViewAdapter( Context mContext,ArrayList<Integer> mSubId, ArrayList<String> mSubName) {
        this.mSubId = mSubId;
        this.mContext = mContext;
        this.mSubName = mSubName;
        this.mainUrl="https://atdebjoy.com/others/api/trackerapp/";
    }


    @NonNull
    @Override
    public ViewHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.term_recylcer_list,parent, false);
        ViewHolderHome holder=new ViewHolderHome(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderHome holder, final int position) {
        if(mSubName.get(position).length()<18)
        holder.mHomeRecyclerSubjectName.setText(" "+mSubName.get(position));
        else
            holder.mHomeRecyclerSubjectName.setText(" "+mSubName.get(position).substring(0,18)+"...");

        //json call and populate chart here;
        String requestUrl=mainUrl+"termwise.php?subject="+mSubId.get(position);
        JsonObjectRequest mJsonArrayRequest= new JsonObjectRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code")==202) {
                                JSONArray responseArray=response.getJSONArray("response");
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                ViewGroup.LayoutParams params = holder.mHomeRecyclerChart.getLayoutParams();
                                params.height = 117 * responseArray.length();
                                holder.mHomeRecyclerChart.setLayoutParams(params);
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject obj = (JSONObject) responseArray.get(i);
                                    double marks = obj.getDouble("marks");
                                    double full_marks = obj.getDouble("full_marks");
                                    double percentage = (marks / full_marks) * 100;
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


                                XAxis xAxis = holder.mHomeRecyclerChart.getXAxis();
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
                                dataset.setColor(Color.parseColor("#304ffe"));
                                //else
                                //dataset.setColor(Color.parseColor("#263238"));
                                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                                dataSets.add(dataset);
                                BarData data = new BarData(dataSets);
                                holder.mHomeRecyclerChart.setData(data);
                                holder.mHomeRecyclerChart.setNoDataText("data not available lol");
                                holder.mHomeRecyclerChart.invalidate();

                                holder.mHomeRecyclerProgress.setVisibility(View.GONE);
                                holder.mHomeRecyclerChart.setVisibility(View.VISIBLE);
                                //Log.i("RESULT",holder.mHomeRecyclerChart.getBarData().getDataSets().toString());
                            }
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(mJsonArrayRequest);

    }

    @Override
    public int getItemCount() {
        return mSubId.size();
    }

    public class ViewHolderHome extends RecyclerView.ViewHolder{

        TextView mHomeRecyclerSubjectName;
        HorizontalBarChart mHomeRecyclerChart;
        ProgressBar mHomeRecyclerProgress;
        public ViewHolderHome(@NonNull View itemView) {
            super(itemView);
            mHomeRecyclerSubjectName=itemView.findViewById(R.id.home_recycler_item_subject_name);
            mHomeRecyclerChart=itemView.findViewById(R.id.home_recycler_item_chart);
            mHomeRecyclerProgress=itemView.findViewById(R.id.home_progress_recycler_subjects);
            mHomeRecyclerProgress.setVisibility(View.VISIBLE);
            mHomeRecyclerChart.setVisibility(View.GONE);

            XAxis xAxis = mHomeRecyclerChart.getXAxis();
            xAxis.setDrawGridLines(false);
            mHomeRecyclerChart.getAxisLeft().setDrawGridLines(false);
            mHomeRecyclerChart.getAxisRight().setDrawGridLines(false);

            mHomeRecyclerChart.getAxisRight().setAxisMinimum(0f);
            mHomeRecyclerChart.getAxisLeft().setAxisMinimum(0f);
            mHomeRecyclerChart.getAxisRight().setAxisMaximum(100f);
            mHomeRecyclerChart.getAxisLeft().setAxisMaximum(100f);

            mHomeRecyclerChart.getAxisLeft().setDrawGridLines(false);
            mHomeRecyclerChart.setPinchZoom(false);
            mHomeRecyclerChart.setDrawBarShadow(false);
            mHomeRecyclerChart.setDrawGridBackground(true);
            mHomeRecyclerChart.setContentDescription("");
            mHomeRecyclerChart.setTouchEnabled(false);
            mHomeRecyclerChart.setDrawBarShadow(false);

            mHomeRecyclerChart.getDescription().setEnabled(false);
        }
    }
}

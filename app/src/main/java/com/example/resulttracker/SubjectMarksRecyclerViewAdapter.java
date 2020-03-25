package com.example.resulttracker;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubjectMarksRecyclerViewAdapter extends RecyclerView.Adapter<SubjectMarksRecyclerViewAdapter.SubjectViewHolder> {

    private JSONArray subjectArrayJson;

    private Handler handler = new Handler();
    SubjectMarksRecyclerViewAdapter(JSONArray subjectArrayJson){
        this.subjectArrayJson=subjectArrayJson;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_subject_marks,parent, false);
        SubjectViewHolder holder = new SubjectViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SubjectViewHolder holder, int position) {
        try {
            JSONObject marksJSON=(JSONObject) subjectArrayJson.get(position);
            holder.mActualMarks.setText(Math.round(marksJSON.getDouble("marks"))+"");
            holder.mFullMarks.setText(""+Math.round(marksJSON.getDouble("full_marks")));
            if(marksJSON.getInt("assesment_number")==1)
                holder.mExamName.setText(marksJSON.getString("exam_name"));
            else
                holder.mExamName.setText(marksJSON.getString("exam_name")+" #"+marksJSON.getInt("assesment_number"));

            holder.mCircularProgressBar.setProgress(0);
            holder.mCircularProgressBar.setSecondaryProgress(100);
            holder.mCircularProgressBar.setMax(100);

            final int average_value=(int)Math.round(marksJSON.getDouble("percentage") ) ;;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    int pStatus=0;
                    while (pStatus < average_value) {
                        pStatus += 1;

                        final int finalPStatus = pStatus;
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                holder.mCircularProgressBar.setProgress(finalPStatus);
                                holder.mProgressText.setText(finalPStatus + "%");

                            }
                        });
                        try {
                            Thread.sleep(16); //thread will take approx 3 seconds to finish,change its value according to your needs
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    holder.mCircularProgressBar.setProgress((int)average_value);
                    holder.mProgressText.setText(average_value + "%");
                }
            }).start();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.subjectArrayJson.length();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder{
        private TextView mActualMarks;
        private TextView mExamName;
        private TextView mFullMarks;
        private ProgressBar mCircularProgressBar;
        private TextView mProgressText;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            mActualMarks=itemView.findViewById(R.id.subject_recycler_actual_marks);
            mExamName=itemView.findViewById(R.id.subject_recycler_exam_name);
            mFullMarks=itemView.findViewById(R.id.subject_recycler_full_marks);
            mCircularProgressBar=itemView.findViewById(R.id.subject_recycler_circularProgressbar);
            mProgressText=itemView.findViewById(R.id.subject_recycler_circularProgressBar_tv);

        }
    }
}

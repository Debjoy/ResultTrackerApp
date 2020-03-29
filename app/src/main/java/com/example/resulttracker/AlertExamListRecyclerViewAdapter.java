package com.example.resulttracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class AlertExamListRecyclerViewAdapter extends RecyclerView.Adapter<AlertExamListRecyclerViewAdapter.AlertExamListViewHolder> {

    private JSONArray examListArray;
    AlertExamListRecyclerViewAdapter(JSONArray examListArray){
        this.examListArray=examListArray;
    }
    @NonNull
    @Override
    public AlertExamListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_general_exam_structure,parent, false);
        AlertExamListViewHolder holder= new AlertExamListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlertExamListViewHolder holder, int position) {
        try {
            holder.mExamName.setText(examListArray.getJSONObject(position).getString("exam_name"));
            holder.mExamNo.setText(examListArray.getJSONObject(position).getString("exam_no"));
            holder.mFullMarks.setText(examListArray.getJSONObject(position).getString("full_marks"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return examListArray.length();
    }

    public class AlertExamListViewHolder extends RecyclerView.ViewHolder{
        TextView mExamName;
        TextView mExamNo;
        TextView mFullMarks;
        Button mEditExamButton;
        Button mDeleteExamButton;
        public AlertExamListViewHolder(@NonNull View itemView) {
            super(itemView);
            mExamName=itemView.findViewById(R.id.alert_exam_list_name);
            mExamNo=itemView.findViewById(R.id.alert_exam_list_exam_no);
            mFullMarks=itemView.findViewById(R.id.alert_exam_list_full_marks);
            mEditExamButton=itemView.findViewById(R.id.alert_exam_list_edit_name_button);
            mDeleteExamButton=itemView.findViewById(R.id.alert_exam_list_delete_button);
        }
    }
}

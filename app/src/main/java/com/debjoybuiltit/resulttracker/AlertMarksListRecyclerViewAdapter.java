package com.debjoybuiltit.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import es.dmoral.toasty.Toasty;

public class AlertMarksListRecyclerViewAdapter extends RecyclerView.Adapter<AlertMarksListRecyclerViewAdapter.AlertMarksListViewHolder> {
        /*
        * This the recycler view used for loading the list of marks term wise from the input fragment section
        * This recycler view will be loading itself inside a alert dialog.
        *
        * */
    private JSONArray mMarksList;
    private Context mContext;
    private String mainURL;
    private InputTermRecyclerViewAdapter inputTermRecyclerViewAdapter;
    String requestURLParent;
    View alertLayoutParent;
    AlertDialog alertDParent;
    JSONObject finalTermDataParent;
    private int stud_id;
    private String stud_pass;
    AlertMarksListRecyclerViewAdapter(JSONArray mMarksList, Context mContext, InputTermRecyclerViewAdapter inputTermRecyclerViewAdapter
            , String requestURLParent,View alertLayoutParent,AlertDialog alertDParent,JSONObject finalTermDataParent, int stud_id, String stud_pass){
        this.mMarksList=mMarksList;
        this.mContext=mContext;
        mainURL="https://atdebjoy.com/others/api/perform/";
        this.inputTermRecyclerViewAdapter=inputTermRecyclerViewAdapter;

        this.requestURLParent=requestURLParent;
        this.alertLayoutParent=alertLayoutParent;
        this.alertDParent=alertDParent;
        this.finalTermDataParent=finalTermDataParent;

        this.stud_id=stud_id;
        this.stud_pass=stud_pass;
    }
    @NonNull
    @Override
    public AlertMarksListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_alert_show_marks_list,parent, false);
        AlertMarksListViewHolder holder= new AlertMarksListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AlertMarksListViewHolder holder, final int position) {
        try {
            final String marks=((JSONObject)mMarksList.get(position)).getString("marks");
            final String marks_id=((JSONObject)mMarksList.get(position)).getString("marks_id");
            holder.mMarksButton.setText(marks);
            final String full_marks="/ "+((JSONObject)mMarksList.get(position)).getString("full_marks");
            holder.mFullMarks.setText(full_marks);
            String examName=((JSONObject)mMarksList.get(position)).getString("exam_name");
            if(examName.length()>17)
                examName=examName.substring(0,15)+"..";
            holder.mExamName.setText(examName);
            String subjectName = ((JSONObject)mMarksList.get(position)).getString("subject_name");
            if(subjectName.length()>15)
                subjectName=subjectName.substring(0,13)+"..";
            holder.mSubjectName.setText(subjectName);

            //ADDDING FUNCTIONALITY TO DELETE BUTTON
            holder.mDeleteMarksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View alertLayout=inflater.inflate(R.layout.alert_input_delete_confirmation,null);
                    builder.setView(alertLayout);
                    final AlertDialog alertD=builder.show();
                    (alertLayout.findViewById(R.id.alert_delete_cancel_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertD.dismiss();
                        }
                    });
                    (alertLayout.findViewById(R.id.alert_delete_ok_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String requestURL=mainURL+"del_marks.php";
                            JSONObject postparams = new JSONObject();
                            try {
                                postparams.put("marks_id", marks_id);
                                postparams.put("stud_id",stud_id);
                                postparams.put("pass",stud_pass);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            (alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, requestURL, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202) {
                                                    Toasty.success(mContext, "Deleted", Toasty.LENGTH_SHORT).show();
                                                }else if(response.getInt("code")==351){
                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                    mContext.startActivity(mainActivity);
                                                }
                                                else
                                                    Toasty.warning(mContext,"Something is wrong", Toasty.LENGTH_SHORT).show();
                                                inputTermRecyclerViewAdapter.fillWithMarksInAlertDialog(requestURLParent,alertLayoutParent,alertDParent,finalTermDataParent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            alertD.dismiss();
                                            (alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toasty.warning(mContext, "Network error", Toasty.LENGTH_SHORT).show();
                                            alertD.dismiss();
                                            (alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.GONE);
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(jsonObjectRequest);
                        }
                    });
                }
            });

            //ADDING FUNCTIONALITY TO THE MARKS BUTTON , ie, ALLOW EDITING THE EXISTING MARKS
            holder.mEditMarks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mMarksButton.performClick();
                }
            });
            holder.mMarksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View alertLayout=inflater.inflate(R.layout.alert_input_edit_marks,null);
                    builder.setView(alertLayout);
                    final AlertDialog alertD=builder.show();
                    ((EditText)alertLayout.findViewById(R.id.alert_update_marks_edit_text)).setText(marks);
                    ((EditText)alertLayout.findViewById(R.id.alert_update_marks_edit_text)).setFilters(new InputFilter[]{new InputFilterMinMax("0",full_marks,mContext)});
                    ((TextView)alertLayout.findViewById(R.id.alert_update_marks_full_text_view)).setText(full_marks);

                    ((Button)alertLayout.findViewById(R.id.alert_update_marks_cancel_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertD.dismiss();
                        }
                    });
                    ((Button)alertLayout.findViewById(R.id.alert_update_marks_update_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String requestURL=mainURL+"update_marks_value.php";
                            String marksNew=((EditText)alertLayout.findViewById(R.id.alert_update_marks_edit_text)).getText().toString();
                            JSONObject postparams = new JSONObject();
                            try {
                                postparams.put("marks_id", marks_id);
                                postparams.put("marks",marksNew);
                                postparams.put("stud_id", stud_id);
                                postparams.put("pass", stud_pass);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ((ProgressBar)alertLayout.findViewById(R.id.alert_update_marks_progress)).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.POST, requestURL, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    Toasty.success(mContext, "Marks Updated", Toasty.LENGTH_SHORT).show();
                                                    ((ProgressBar)alertLayout.findViewById(R.id.alert_update_marks_progress)).setVisibility(View.GONE);
                                                    inputTermRecyclerViewAdapter.fillWithMarksInAlertDialog(requestURLParent,alertLayoutParent,alertDParent,finalTermDataParent);
                                                }else if(response.getInt("code")==351){
                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                    mContext.startActivity(mainActivity);
                                                }else{
                                                    Toasty.warning(mContext, "Something went wrong", Toasty.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                ((ProgressBar)alertLayout.findViewById(R.id.alert_update_marks_progress)).setVisibility(View.GONE);
                                                e.printStackTrace();
                                            }
                                            alertD.dismiss();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            ((ProgressBar)alertLayout.findViewById(R.id.alert_update_marks_progress)).setVisibility(View.GONE);
                                            Toasty.warning(mContext, "Network Error"+error.toString(), Toasty.LENGTH_SHORT).show();
                                            alertD.dismiss();
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(jsonObjectRequest);

                        }
                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int length;
        length=mMarksList.length();
        return length;
    }

    public class AlertMarksListViewHolder extends RecyclerView.ViewHolder{
        private Button mMarksButton;
        private TextView mSubjectName;
        private TextView mExamName;
        private TextView mFullMarks;
        private Button mEditMarks;
        private Button mDeleteMarksButton;
        public AlertMarksListViewHolder(@NonNull View itemView) {
            super(itemView);
            mMarksButton=itemView.findViewById(R.id.alert_marks_list_marks_button);
            mSubjectName=itemView.findViewById(R.id.alert_marks_list_subject_name);
            mExamName=itemView.findViewById(R.id.alert_marks_list_exam_name);
            mFullMarks=itemView.findViewById(R.id.alert_marks_list_full_marks);
            mDeleteMarksButton=itemView.findViewById(R.id.alert_marks_list_delete_marks);
            mEditMarks=itemView.findViewById(R.id.alert_marks_list_edit_marks);
        }
    }
}

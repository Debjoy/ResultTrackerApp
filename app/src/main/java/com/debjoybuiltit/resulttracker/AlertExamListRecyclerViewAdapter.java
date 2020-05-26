package com.debjoybuiltit.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import static android.content.Context.MODE_PRIVATE;

public class AlertExamListRecyclerViewAdapter extends RecyclerView.Adapter<AlertExamListRecyclerViewAdapter.AlertExamListViewHolder> {

    private JSONArray examListArray;
    private Context mContext;
    private View alertLayoutParent;
    private GeneralActivity generalActivity;
    private InputFragment inputFragment;
    private String mainUrl;
    private int stud_id;
    private String stud_pass;
    AlertExamListRecyclerViewAdapter(JSONArray examListArray, GeneralActivity mContext, View alertLayoutParent,int stud_id, String stud_pass){
        this.examListArray=examListArray;
        this.mContext=mContext;
        this.alertLayoutParent=alertLayoutParent;
        this.generalActivity=mContext;
        this.stud_id=stud_id;
        this.stud_pass=stud_pass;
    }
    AlertExamListRecyclerViewAdapter(JSONArray examListArray, Context mContext, InputFragment inputFragment, View alertLayoutParent, int stud_id,String stud_pass){
        this.examListArray=examListArray;
        this.mContext=mContext;
        this.alertLayoutParent=alertLayoutParent;
        this.inputFragment=inputFragment;
        this.stud_id=stud_id;
        this.stud_pass=stud_pass;
    }



    @NonNull
    @Override
    public AlertExamListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_general_exam_structure,parent, false);
        AlertExamListViewHolder holder= new AlertExamListViewHolder(view);
        mainUrl="https://atdebjoy.com/others/api/perform/";
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlertExamListViewHolder holder, final int position) {
        try {
            holder.mExamName.setText(examListArray.getJSONObject(position).getString("exam_name"));
            holder.mExamNo.setText(examListArray.getJSONObject(position).getString("exam_no"));
            holder.mFullMarks.setText(examListArray.getJSONObject(position).getString("full_marks"));

            holder.mDeleteExamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View alertLayout=inflater.inflate(R.layout.alert_input_delete_confirmation,null);
                    builder.setView(alertLayout);
                    final AlertDialog alertD2=builder.show();
                    ((TextView)alertLayout.findViewById(R.id.alert_delete_message)).setText("Deleting a Exam will also delete all the marks of it. Are you sure?");
                    ((Button)alertLayout.findViewById(R.id.alert_delete_cancel_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertD2.dismiss();
                        }
                    });
                    ((Button)alertLayout.findViewById(R.id.alert_delete_ok_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences spref = mContext.getSharedPreferences("data_user", MODE_PRIVATE);
                            String password=spref.getString("pwd","");
                            String requestUrl=mainUrl+"del_exam.php";
                            JSONObject postparams=new JSONObject();
                            try {
                                postparams.put("stud_id",stud_id);
                                postparams.put("pass",password);
                                postparams.put("ass_id",examListArray.getJSONObject(position).getInt("ass_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            alertLayout.findViewById(R.id.alert_delete_progress).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    Toasty.success(mContext, "Successfully Deleted", Toasty.LENGTH_SHORT).show();
                                                    if(generalActivity==null)
                                                    inputFragment.loadExamStructure(alertLayoutParent);
                                                    else
                                                    generalActivity.loadExamStructure(alertLayoutParent);
                                                    alertD2.dismiss();
                                                }else if(response.getInt("code")==351){
                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                    mContext.startActivity(mainActivity);
                                                }else{
                                                    Toasty.warning(mContext,"Some thing went wrong: "+response.toString(), Toasty.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            alertLayout.findViewById(R.id.alert_delete_progress).setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toasty.error(mContext, "Network error "+error.toString(), Toasty.LENGTH_SHORT).show();
                                            alertLayout.findViewById(R.id.alert_delete_progress).setVisibility(View.GONE);
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(jsonObjectRequest);
                        }
                    });
                }
            });

            holder.mEditExamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View alertLayout = inflater.inflate(R.layout.alert_edit_name,null);
                    builder.setView(alertLayout);
                    final AlertDialog alertD=builder.show();

                    ((TextView)alertLayout.findViewById(R.id.alert_edit_name_title)).setText("Edit Exam Structure");
                    final EditText mExamNameEditTextt=alertLayout.findViewById(R.id.alert_edit_name_edit_text);
                    mExamNameEditTextt.setHint("Enter the exam name here");
                    try {
                        mExamNameEditTextt.setText(examListArray.getJSONObject(position).getString("exam_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    alertLayout.findViewById(R.id.alert_edit_name_cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertD.dismiss();
                        }
                    });

                    alertLayout.findViewById(R.id.alert_edit_name_delete_button).setVisibility(View.GONE);
                    alertLayout.findViewById(R.id.alert_edit_name_update_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String requestUrl=mainUrl+"update_exam.php?ass_id=";
                            JSONObject postparams = new JSONObject();
                            try {
                                postparams.put("ass_id", examListArray.getJSONObject(position).getString("ass_id"));
                                postparams.put("exam_name",mExamNameEditTextt.getText().toString());
                                postparams.put("stud_id",stud_id);
                                postparams.put("pass",stud_pass);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            alertLayout.findViewById(R.id.alert_edit_name_progress).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, requestUrl, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    alertLayout.findViewById(R.id.alert_edit_name_progress).setVisibility(View.GONE);
                                                    Toasty.success(mContext, "Successfully updated", Toasty.LENGTH_SHORT).show();

                                                    if(generalActivity==null)
                                                        inputFragment.loadExamStructure(alertLayoutParent);
                                                    else
                                                        generalActivity.loadExamStructure(alertLayoutParent);
                                                    alertD.dismiss();
                                                }else if(response.getInt("code")==351){
                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                    mContext.startActivity(mainActivity);
                                                }else{
                                                    Toasty.warning(mContext, "Something went wrong"+response.toString(), Toasty.LENGTH_SHORT).show();
                                                    alertLayout.findViewById(R.id.alert_edit_name_progress).setVisibility(View.GONE);
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
                                            alertLayout.findViewById(R.id.alert_edit_name_progress).setVisibility(View.GONE);

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

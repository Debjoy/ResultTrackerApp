package com.example.resulttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class InputTermRecyclerViewAdapter extends RecyclerView.Adapter<InputTermRecyclerViewAdapter.InputViewHolderTerm>{

    private JSONArray mTermsWithSubjects;
    private Context mContext;
    private String mainURL;
    private InputFragment fragment;
    private int stud_id;
    private String stud_pass;
    InputTermRecyclerViewAdapter(JSONArray mTermsWithSubjects,Context mContext, InputFragment fragment, int stud_id, String stud_pass){
        this.mTermsWithSubjects=mTermsWithSubjects;
        this.mContext=mContext;
        this.fragment=fragment;
        mainURL="https://atdebjoy.com/others/api/perform/";
        this.stud_id=stud_id;
        this.stud_pass=stud_pass;
    }

    @NonNull
    @Override
    public InputViewHolderTerm onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.input_term_recycler_list,parent, false);
        InputViewHolderTerm holder= new InputViewHolderTerm(view);
        return holder;
    }

    public int dpToPx(int dp) {
        float density = mContext.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onBindViewHolder(@NonNull InputViewHolderTerm holder, int position) {
        JSONObject termData= null;
        String term_id="";


        //POPULATING THE SUBJECT NAMES
        try {
            termData = (JSONObject) mTermsWithSubjects.get(position);
            holder.termNameText.setText(termData.getString("term_name"));

            term_id=termData.getString("term_id");
            final JSONArray subjectArray= termData.getJSONArray("subjects");
            for(int i=0;i<subjectArray.length();i++){
                Button newButton= new Button(mContext);
                newButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                newButton.setText(((JSONObject)subjectArray.get(i)).getString("sub_name"));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newButton.getLayoutParams();
                params.height=dpToPx(35);
                params.setMargins(0,0,dpToPx(2),0);
                newButton.setLayoutParams(params);
                newButton.setAllCaps(false);
                newButton.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_button_accent));
                newButton.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                newButton.setMinimumWidth(0);
                newButton.setMinWidth(0);
                newButton.setPadding(dpToPx(10),0,dpToPx(10),0);

                final int finalI = i;
                //ADDING CLICK BEHAVIOUR TO BUTTONS
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            final String subjectName=((JSONObject)subjectArray.get(finalI)).getString("sub_name");
                            final int subjectId=((JSONObject)subjectArray.get(finalI)).getInt("sub_id");

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            final View layout=inflater.inflate(R.layout.alert_edit_name,null);
                            builder.setView(layout);
                            //builder.setTitle("hello");
                            final AlertDialog alertD=builder.show();
                            ((TextView)layout.findViewById(R.id.alert_edit_name_title)).setText("Edit Subject Name:");
                            final EditText mSubjectNameEditTextt=layout.findViewById(R.id.alert_edit_name_edit_text);
                            mSubjectNameEditTextt.setHint("Enter the subject name here");

                            mSubjectNameEditTextt.setText(subjectName);
                            ((Button)layout.findViewById(R.id.alert_edit_name_cancel_button)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertD.dismiss();
                                }
                            });
                            ((Button)layout.findViewById(R.id.alert_edit_name_update_button)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    //alertD.dismiss();
                                    String subject_name=mSubjectNameEditTextt.getText().toString();
                                    String requestUrl=mainURL+"update_subject_name.php";
                                    ((ProgressBar)layout.findViewById(R.id.alert_edit_name_progress)).setVisibility(View.VISIBLE);
                                    JSONObject postparams = new JSONObject();
                                    try {
                                        postparams.put("sub_id", subjectId);
                                        postparams.put("sub_name",subject_name);
                                        postparams.put("stud_id",stud_id);
                                        postparams.put("pass",stud_pass);

                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, requestUrl, postparams,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    ((ProgressBar)layout.findViewById(R.id.alert_edit_name_progress)).setVisibility(View.GONE);
                                                    try {
                                                        if(response.getInt("code")==202) {
                                                            Toasty.success(mContext, "Subject name updated.", Toasty.LENGTH_SHORT).show();
                                                        }else if(response.getInt("code")==351){
                                                            Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                            Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                            mContext.startActivity(mainActivity);
                                                        }else{
                                                            Toasty.warning(mContext, "Something went wrong", Toasty.LENGTH_SHORT).show();
                                                        }
                                                    }catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    alertD.dismiss();
                                                    fragment.loadTermsWithSubjects();
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toasty.warning(mContext, "Network Error", Toasty.LENGTH_SHORT).show();
                                                    alertD.dismiss();
                                                }
                                            });
                                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                                    requestQueue.add(jsonObjReq);
                                }
                            });
                            ((Button)layout.findViewById(R.id.alert_edit_name_delete_button)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                                    LayoutInflater inflater = LayoutInflater.from(mContext);
                                    final View alertLayout=inflater.inflate(R.layout.alert_input_delete_confirmation,null);
                                    builder.setView(alertLayout);
                                    final AlertDialog alertD2=builder.show();
                                    ((TextView)alertLayout.findViewById(R.id.alert_delete_message)).setText("Deleting a subject will also delete all the marks of it. Are you sure?");
                                    ((Button)alertLayout.findViewById(R.id.alert_delete_cancel_button)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertD2.dismiss();
                                        }
                                    });
                                    ((Button)alertLayout.findViewById(R.id.alert_delete_ok_button)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String requestURL=mainURL+"del_subject.php";
                                            JSONObject postparams = new JSONObject();
                                            try {
                                                postparams.put("sub_id", subjectId);
                                                postparams.put("stud_id",stud_id);
                                                postparams.put("pass",stud_pass);
                                            }catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            (alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.VISIBLE);
                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestURL, postparams,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try {
                                                                if(response.getInt("code")==202){
                                                                    Toasty.success(mContext, "Deleted", Toasty.LENGTH_SHORT).show();
                                                                }else if(response.getInt("code")==351){
                                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                                    mContext.startActivity(mainActivity);
                                                                }else{
                                                                    Toasty.warning(mContext, "Something went wrong.", Toasty.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            fragment.loadTermsWithSubjects();
                                                            (alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.GONE);
                                                            alertD2.dismiss();
                                                            alertD.dismiss();
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Toasty.warning(mContext, "Network Error", Toasty.LENGTH_SHORT).show();
                                                            alertD2.dismiss();
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
                });

                holder.subjectHolderFlowLayout.addView(newButton);
            }

            //for deleting the terms button
            final String finalTerm_id2 = term_id;
            holder.mDeleteTermButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View alertLayout=inflater.inflate(R.layout.alert_input_delete_confirmation,null);
                    builder.setView(alertLayout);
                    final AlertDialog alertD=builder.show();
                    ((TextView)alertLayout.findViewById(R.id.alert_delete_message)).setText("Deleting a term will also delete all the subjects from it along with its marks. Are you sure?");
                    ((Button)alertLayout.findViewById(R.id.alert_delete_cancel_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertD.dismiss();
                        }
                    });
                    ((Button)alertLayout.findViewById(R.id.alert_delete_ok_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String requestURL=mainURL+"del_term.php";
                            JSONObject postparams = new JSONObject();
                            try {
                                postparams.put("term_id", finalTerm_id2);
                                postparams.put("stud_id",stud_id);
                                postparams.put("pass",stud_pass);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ((ProgressBar)alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.VISIBLE);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestURL, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("code")==202){
                                                    Toasty.success(mContext, "Deleted", Toasty.LENGTH_SHORT).show();
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
                                            alertD.dismiss();
                                            fragment.loadTermsWithSubjects();
                                            ((ProgressBar)alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toasty.warning(mContext, "Network Error", Toasty.LENGTH_SHORT).show();
                                            ((ProgressBar)alertLayout.findViewById(R.id.alert_delete_progress)).setVisibility(View.GONE);
                                            alertD.dismiss();

                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(jsonObjectRequest);
                        }
                    });

                }
            });
            ImageButton newButton= new ImageButton(mContext);
            newButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newButton.getLayoutParams();
            params.height=dpToPx(35);
            params.width=dpToPx(35);
            params.setMargins(0,0,dpToPx(2),0);
            newButton.setLayoutParams(params);
            newButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_add_circle_black_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                newButton.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorWhite));
            }
            newButton.setPadding(dpToPx(10),0,dpToPx(10),0);
            final String finalTerm_id = term_id;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View layout=inflater.inflate(R.layout.alert_input_add_subject_name,null);
                    builder.setView(layout);
                    final AlertDialog alertD=builder.show();
                    ((Button)layout.findViewById(R.id.alert_add_subject_cancel_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertD.dismiss();
                        }
                    });
                    ((Button)layout.findViewById(R.id.alert_add_subject_add_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String subject_name=((TextView)layout.findViewById(R.id.alert_add_subject_edit_text)).getText().toString();
                            String requestUrl=mainURL+"add_subject.php";
                            ((ProgressBar)layout.findViewById(R.id.alert_add_subject_progress)).setVisibility(View.VISIBLE);
                            JSONObject postparams = new JSONObject();

                            try {
                                postparams.put("term_id", finalTerm_id);
                                postparams.put("sub_name",subject_name);
                                postparams.put("stud_id",stud_id);
                                postparams.put("pass",stud_pass);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    requestUrl, postparams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            ((ProgressBar)layout.findViewById(R.id.alert_add_subject_progress)).setVisibility(View.GONE);
                                            try {
                                                if(response.getInt("code")==202) {
                                                    Toasty.success(mContext, "Subject name added.", Toasty.LENGTH_SHORT).show();
                                                }else if(response.getInt("code")==351){
                                                    Toasty.error(mContext, "Authentication Error", Toasty.LENGTH_SHORT).show();
                                                    Intent mainActivity=new Intent(mContext, MainActivity.class);
                                                    mContext.startActivity(mainActivity);
                                                }else{
                                                    Toasty.warning(mContext, "Something went wrong", Toasty.LENGTH_SHORT).show();
                                                }
                                            }catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            alertD.dismiss();
                                            fragment.loadTermsWithSubjects();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toasty.warning(mContext, "Network Error", Toasty.LENGTH_SHORT).show();
                                            alertD.dismiss();
                                        }
                                    });
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(jsonObjReq);
                        }
                    });


                }
            });

            holder.subjectHolderFlowLayout.addView(newButton);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ADDING THE FUNCTIONALITY OF THE MARKS VIEWING BUTTON

        final String finalTerm_id1 = term_id;
        final JSONObject finalTermData = termData;
        holder.viewMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View alertLayout=inflater.inflate(R.layout.alert_input_show_list_marks,null);
                builder.setView(alertLayout);
                final AlertDialog alertD=builder.show();
                ((Button)alertLayout.findViewById(R.id.alert_show_marks_cancel_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertD.dismiss();
                    }
                });

                String requestURL=mainURL+"get_term_wise.php?all&term="+ finalTerm_id1+"&stud_id="+stud_id+"&pass="+stud_pass;

                fillWithMarksInAlertDialog(requestURL,alertLayout,alertD,finalTermData);


            }
        });

    }

    public void fillWithMarksInAlertDialog(final String requestURL, final View alertLayout, final AlertDialog alertD, final JSONObject finalTermData){
        ((ProgressBar)alertLayout.findViewById(R.id.alert_show_marks_progress)).setVisibility(View.VISIBLE);
        final InputTermRecyclerViewAdapter inputTermRecyclerViewAdapter=this;
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, requestURL,null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {

                        //RECYCLER VIEW FOR THE LIST OF MARKS LOADING INSIDE THE ALERT DIALOG
                        try{
                            AlertMarksListRecyclerViewAdapter adapter= new AlertMarksListRecyclerViewAdapter(response,mContext,inputTermRecyclerViewAdapter,requestURL,alertLayout,alertD,finalTermData, stud_id, stud_pass );
                            RecyclerView showMakrsListRecycler=(alertLayout.findViewById(R.id.alert_show_marks_recycler_list));
                            showMakrsListRecycler.setAdapter(adapter);
                            showMakrsListRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                            ((ProgressBar)alertLayout.findViewById(R.id.alert_show_marks_progress)).setVisibility(View.GONE);

                            ((TextView)alertLayout.findViewById(R.id.alert_show_marks_term_name)).setText(finalTermData.getString("term_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.warning(mContext, "Network Error", Toasty.LENGTH_SHORT).show();
                        ((ProgressBar)alertLayout.findViewById(R.id.alert_show_marks_progress)).setVisibility(View.GONE);
                        alertD.dismiss();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public int getItemCount() {
        return mTermsWithSubjects.length();
    }

    public class InputViewHolderTerm extends RecyclerView.ViewHolder{
        private TextView termNameText;
        private Button viewMarksButton;
        private FlowLayout subjectHolderFlowLayout;
        private Button mDeleteTermButton;
        public InputViewHolderTerm(@NonNull View itemView) {
            super(itemView);
            termNameText=itemView.findViewById(R.id.input_recycler_termname);
            viewMarksButton=itemView.findViewById(R.id.input_view_marks);
            subjectHolderFlowLayout=itemView.findViewById(R.id.input_recycler_flowLayout);
            mDeleteTermButton=itemView.findViewById(R.id.input_delete_term);
        }
    }
}

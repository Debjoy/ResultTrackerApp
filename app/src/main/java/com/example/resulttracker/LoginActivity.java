package com.example.resulttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Fragment signInFragment;
    private String mainUrl;
    private EditText mUsernameEdit1;
    private EditText mPasswordEdit1;

    private EditText mUsernameEdit2;
    private EditText mNameEdit2;
    private EditText mEmailEdit2;
    private EditText mPasswordEdit2;
    private Context mContext;

    private final int ALERT_DANGER=-1;
    private final int ALERT_INFO=0;
    private final int ALERT_SUCCESS=1;

    private int exitFlag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInFragment = new SignInFragment();
        FragmentManager fm=getSupportFragmentManager();
        mainUrl="https://atdebjoy.com/others/api/trackerapp/";
        mContext=this;
        exitFlag=1;
        Intent intent = getIntent();
        if(!(intent.getStringExtra("msg") == null))
        //Toast.makeText(mContext, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
        displayDialog( intent.getStringExtra("msg"), intent.getIntExtra("alert",0));
        fm.beginTransaction().replace(R.id.LoginFragments,signInFragment).commit();

    }
    public void onSignUp(View view){
        exitFlag=1;
        Fragment signUpFragment=new SignUpFragment();
        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.LoginFragments,signUpFragment).addToBackStack("SIGN UP").commit();
    }
    public void onSignInSubmit(View view){
        exitFlag=1;
        mUsernameEdit1=findViewById(R.id.username_edit_text_1);
        mPasswordEdit1=findViewById(R.id.password_edit_text_1);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("data_user", MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", mUsernameEdit1.getText().toString());
        editor.putString("pwd", mPasswordEdit1.getText().toString());
        editor.commit();

        Intent mainActivity=new Intent(mContext, MainActivity.class);
        startActivity(mainActivity);

    }
    public void onSignUpSubmit(View view){
        exitFlag=1;
        String requestUrl=mainUrl+"register.php";
        mUsernameEdit2=findViewById(R.id.username_edit_text_2);
        mNameEdit2=findViewById(R.id.name_edit_text_2);
        mEmailEdit2=findViewById(R.id.email_edit_text_2);
        mPasswordEdit2=findViewById(R.id.password_edit_text_2);

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("username", this.mUsernameEdit2.getText().toString());
            postparams.put("name", this.mNameEdit2.getText().toString());
            postparams.put("email", this.mEmailEdit2.getText().toString());
            postparams.put("pwd", this.mPasswordEdit2.getText().toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                requestUrl, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        try {
                            int code=response.getInt("code");
                            String msg=response.getString("msg");

                            if(code==202){
                                int user_id=response.getInt("user");
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("data_user", MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("username", mUsernameEdit2.getText().toString());
                                editor.putString("user_id", user_id+"");
                                editor.putString("pwd", mPasswordEdit2.getText().toString());
                                editor.commit();

                                Intent mainActivity=new Intent(mContext, MainActivity.class);
                                startActivity(mainActivity);
                            }else{
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mContext, "ERROR: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    public void displayDialog(String msg, int alertType){

        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater= LoginActivity.this.getLayoutInflater();
        View layout=inflater.inflate(R.layout.alert_default,null);
        builder.setView(layout);
        switch (alertType){
            case -1:((TextView)layout.findViewById(R.id.alert_defalut_title)).setText("Error");
                    ((TextView)layout.findViewById(R.id.alert_defalut_msg)).setText(msg);
                    ((ImageView)layout.findViewById(R.id.alert_defalut_icon)).setImageResource(R.drawable.alert_danger);
                    break;
            case 0: ((TextView)layout.findViewById(R.id.alert_defalut_title)).setText("Info");
                    ((TextView)layout.findViewById(R.id.alert_defalut_msg)).setText(msg);
                    ((ImageView)layout.findViewById(R.id.alert_defalut_icon)).setImageResource(R.drawable.alert_info);
                    break;
            case 1:((TextView)layout.findViewById(R.id.alert_defalut_title)).setText("Success");
                    ((TextView)layout.findViewById(R.id.alert_defalut_msg)).setText(msg);
                    ((ImageView)layout.findViewById(R.id.alert_defalut_icon)).setImageResource(R.drawable.alert_success);
                    break;
        }
        final AlertDialog alertD=builder.show();
        ((Button)layout.findViewById(R.id.alert_default_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()==0){
            if(exitFlag==1){
                Toast.makeText(mContext, "Press back again to exit", Toast.LENGTH_SHORT).show();
                exitFlag=0;
                return;
            }
            finish();
            return;
        }
        super.onBackPressed();
    }
}

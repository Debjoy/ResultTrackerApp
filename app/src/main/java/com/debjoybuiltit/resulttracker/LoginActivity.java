package com.debjoybuiltit.resulttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

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

    AwesomeValidation signUpValidation;

    private int exitFlag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInFragment = new SignInFragment();
        FragmentManager fm=getSupportFragmentManager();
        mainUrl="https://atdebjoy.com/others/api/perform/";
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

        String email="";
        String username="";
        if(isEmailValid(mUsernameEdit1.getText().toString())){
            email=mUsernameEdit1.getText().toString();
        }else{
            username=mUsernameEdit1.getText().toString();
        }

        editor.putString("username",username);
        editor.putString("email",email);
        editor.putString("pwd", mPasswordEdit1.getText().toString());
        editor.commit();

        Intent mainActivity=new Intent(mContext, MainActivity.class);
        startActivity(mainActivity);

    }

    public boolean isEmailValid(String str){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public void onSignUpSubmit(View view){
        exitFlag=1;
        String requestUrl=mainUrl+"register.php";
        mUsernameEdit2=findViewById(R.id.username_edit_text_2);
        mNameEdit2=findViewById(R.id.name_edit_text_2);
        mEmailEdit2=findViewById(R.id.email_edit_text_2);
        mPasswordEdit2=findViewById(R.id.password_edit_text_2);

        signUpValidation=  new AwesomeValidation(ValidationStyle.BASIC);
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("username", this.mUsernameEdit2.getText().toString());
            postparams.put("name", this.mNameEdit2.getText().toString());
            postparams.put("email", this.mEmailEdit2.getText().toString());
            postparams.put("pwd", this.mPasswordEdit2.getText().toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }


        signUpValidation.addValidation(mNameEdit2,"^[A-Za-z ]{1,}$", "Should contain only aphabets and space");
        signUpValidation.addValidation(mEmailEdit2, Patterns.EMAIL_ADDRESS, "Should contain only aphabets and space");
        signUpValidation.addValidation(mUsernameEdit2, "^[a-z0-9_-]+$","Username should only contain alphabets, numbers and underscore");
        signUpValidation.addValidation(mUsernameEdit2, "^.{3,}$","Username should atleast contain 4 characters");
        signUpValidation.addValidation(mUsernameEdit2, "^.{0,16}$","Username should not exceed 16 characters");
        signUpValidation.addValidation(mPasswordEdit2, "^.{8,}$", "Should contain atleast 8 characters");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                requestUrl, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        try {
                            int code=response.getInt("code");
                            String msg=response.getString("msg");
                            mEmailEdit2.setError(null);
                            mUsernameEdit2.setError(null);
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
                                Toasty.success(mContext, msg, Toasty.LENGTH_LONG).show();
                            }else if(code==261 || code==263 ){
                                if(code==261){
                                    mEmailEdit2.requestFocus();
                                    mEmailEdit2.setError("Email ID exists");
                                }
                                if(code==263){
                                    mUsernameEdit2.requestFocus();
                                    mUsernameEdit2.setError("Username exists");
                                }

                            }else{
                                Toasty.warning(LoginActivity.this, msg, Toasty.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        findViewById(R.id.sign_up_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.sign_up_progress).setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure Callback
                        Toasty.warning(mContext, "ERROR: "+error.getMessage(), Toasty.LENGTH_SHORT).show();
                        findViewById(R.id.sign_up_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.sign_up_progress).setVisibility(View.GONE);
                    }
                });
        if(signUpValidation.validate()) {
            findViewById(R.id.sign_up_button).setVisibility(View.GONE);
            findViewById(R.id.sign_up_progress).setVisibility(View.VISIBLE);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjReq);
        }
    }

    public void displayDialog(String msg, int alertType){


        if(alertType==9)// for network error
        {
            new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AppTheme))
                    .setTitle("Error")
                    .setMessage(msg)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent mainActivity=new Intent(mContext, MainActivity.class);
                            startActivity(mainActivity);
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }
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
                Toasty.normal(mContext, "Press back again to exit", Toasty.LENGTH_SHORT).show();
                exitFlag=0;
                return;
            }
            finish();
            return;
        }
        super.onBackPressed();
    }
}

package com.blucor.steamersindia.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucor.steamersindia.R;
import com.blucor.steamersindia.extra.Constants;
import com.blucor.steamersindia.extra.CustomLoader;
import com.blucor.steamersindia.extra.Preferences;
import com.blucor.steamersindia.extra.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    //declare
    LinearLayout llMain;
    Button btnLogin;
    TextView tvSignupbtn;


   //custome loader
   CustomLoader loader;

   //Edittext
    EditText etEmail;
    EditText etPassword;

    //Checkbox
    CheckBox  checkbox;


    //Preferences
    Preferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getId
        llMain = findViewById(R.id.llMain);
        tvSignupbtn = findViewById(R.id.tvSignupbtn);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        checkbox=findViewById(R.id.checkbox);


        //Onclick
        btnLogin.setOnClickListener(this);
        tvSignupbtn.setOnClickListener(this);


        //loader
        loader = new CustomLoader(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        //preferences
        pref=new Preferences(this);



        //Animate
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        llMain.startAnimation(fadeInAnimation);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    show_password();
                }
                else
                {
                    hide_password();
                }
            }
        });



    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvSignupbtn:
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
                break;
            case R.id.btnLogin:
                String URL=getString(R.string.API_URL)+"login.php";
                if (etEmail.getText().toString().isEmpty()) {
                    etEmail.requestFocus();
                    etEmail.setError("Enter email");
                }
                else if(etPassword.getText().toString().isEmpty())
                {
                    etPassword.requestFocus();
                    etPassword.setError("Enter password");
                }
                else
                {


                    if (Utils.isNetworkConnectedMainThred(getApplicationContext())){
                        loader.show();
                        HitLoginApi(etEmail.getText().toString(),etPassword.getText().toString(),URL);
                    }
                    else {
                        Toasty.warning(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }


                break;
        }
    }




    private void HitLoginApi(final String email,final String pass,String url) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               loader.cancel();
                Log.e("Login_response",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("Success").equalsIgnoreCase("true"))
                    {
                        JSONObject mJsonobject=jsonObject.getJSONObject("user");
                        pref.set(Constants.id,mJsonobject.getString("id"));
                        pref.set(Constants.role,mJsonobject.getString("role"));
                        pref.set(Constants.email,mJsonobject.getString("email"));
                        pref.set(Constants.mobileno,mJsonobject.getString("mobileno"));
                        pref.set(Constants.fullname,mJsonobject.getString("fullname"));
                        pref.commit();


                        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
                        Toasty.success(LoginActivity.this,"Welcome "+mJsonobject.getString("fullname") +" to Steamer's India",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                   Toasty.error(LoginActivity.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.cancel();
                Log.e("Login_error",""+error);

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String>parms=new HashMap<String, String>();

                parms.put("email",email);
                parms.put("password",pass);

                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }




    public void show_password()
    {
        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

    }


    public void hide_password()
    {

        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }






}
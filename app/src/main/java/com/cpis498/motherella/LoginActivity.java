package com.cpis498.motherella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.services.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {
    AppCompatButton btnLogin;
    ImageView btnTogglePassword;
    TextView txtForgetPassword,txtGoSignup;
    EditText etxtEmail,etxtPassword;
    FirebaseHelper mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize variables
        btnLogin=findViewById(R.id.btn_login);
        btnTogglePassword=findViewById(R.id.btn_show_hide_password);
        txtForgetPassword=findViewById(R.id.txt_forget_password);
        etxtPassword=findViewById(R.id.etxt_password);
        etxtEmail=findViewById(R.id.etxt_email);
        mFirebase=new FirebaseHelper(this);
        txtGoSignup=findViewById(R.id.txt_go_signup);


        //set on click
        txtGoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch Signup activity
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                 startActivity(intent);
                 finish();
            }
        });




        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etxtPassword.getTransformationMethod()==PasswordTransformationMethod.getInstance())
                {
                    etxtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    etxtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get info from fields
                String email=etxtEmail.getText().toString();
                String password=etxtPassword.getText().toString();
                //check if fields are empty
                if(email.trim().isEmpty()||password.trim().isEmpty()){
                    Toast.makeText(LoginActivity.this,"توجد حقول فارغة!" ,Toast.LENGTH_LONG).show();
                    return;
                }

                //free to login
                mFirebase.login(email,password);

            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });



    }
}
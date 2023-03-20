package com.cpis498.motherella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cpis498.motherella.services.FirebaseHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etxtEmail;
    AppCompatButton btnSendLink;
    FirebaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        //initialize Variables
        etxtEmail=findViewById(R.id.etxt_email);
        btnSendLink=findViewById(R.id.btn_send_link);
        helper=new FirebaseHelper(this);

        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get info from fields

                String email=etxtEmail.getText().toString();

                //check if fields are empty
                if(email.trim().isEmpty()){
                    Toast.makeText(ResetPasswordActivity.this,"الحقل فارغ!" ,Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isValidEmail(email)){
                    String errorString="البريد الالكتروني مكتوب بصيغة خاطئة";


                    etxtEmail.setError(errorString);
                    return;
                }

                helper.resetPassword(email);
            }
        });





    }
    public  boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}
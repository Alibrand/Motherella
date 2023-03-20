package com.cpis498.motherella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.models.Pregnant;
import com.cpis498.motherella.services.FirebaseHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    EditText etxtFullName,etxtAge,etxtEmail,etxtPassword,etxtConfirmPassword,etxtCyclrLength;
    DatePicker dateLMP;
    AppCompatButton btnSignUp;
    TextView txtGoSignin;
    ImageView btnTogglePassword,btnToggleConfirmPassword;
    FirebaseHelper mFireHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //initialize variables
        etxtFullName=findViewById(R.id.etxt_user_fullname);
        etxtAge=findViewById(R.id.etxt_user_age);
        etxtEmail=findViewById(R.id.etxt_email);
        etxtPassword=findViewById(R.id.etxt_password);
        etxtConfirmPassword=findViewById(R.id.etxt_confirm_password);
        etxtCyclrLength=findViewById(R.id.etxt_cycle_length);
        dateLMP=findViewById(R.id.date_lmp);
        btnSignUp=findViewById(R.id.btn_signup);
        btnTogglePassword=findViewById(R.id.btn_show_hide_password);
        btnToggleConfirmPassword=findViewById(R.id.btn_show_hide_confirm_password);
        txtGoSignin=findViewById(R.id.txt_go_signin);
        //set max date for date picker
        dateLMP.setMaxDate(new Date().getTime());


        mFireHelper=new FirebaseHelper(this);

        //set on click login
        txtGoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch login activity
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);

                startActivity(intent);
                finish();
            }
        });


        //when tap on eye icon to show password or hide
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etxtPassword.getTransformationMethod()== PasswordTransformationMethod.getInstance())
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
        //when tap on eye icon to show confirm password or hide
        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etxtConfirmPassword.getTransformationMethod()== PasswordTransformationMethod.getInstance())
                {
                    etxtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    etxtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });

        //when tap signup
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get info from fields
                String fullname=etxtFullName.getText().toString();
                String ageString=etxtAge.getText().toString();
                String email=etxtEmail.getText().toString();
                String password=etxtPassword.getText().toString();
                String confirmpassword=etxtConfirmPassword.getText().toString();
                String cycleLengthString=etxtCyclrLength.getText().toString();
                //get lmp date
                Calendar lmpCalendar=Calendar.getInstance();
                lmpCalendar.set(dateLMP.getYear(),dateLMP.getMonth(),dateLMP.getDayOfMonth());
                Date lmp=lmpCalendar.getTime();
                //check if fields are empty
                if(fullname.trim().isEmpty() || ageString.trim().isEmpty()||email.trim().isEmpty()||password.trim().isEmpty()||
                confirmpassword.trim().isEmpty()||cycleLengthString.trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"توجد حقول فارغة!" ,Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isValidEmail(email)){
                    String errorString="البريد الالكتروني مكتوب بصيغة خاطئة";


                    etxtEmail.setError(errorString);
                    return;
                }

                if(!isValidPassword(password)){
                    String errorString="كلمة المرور يجب أن تكون 8 محارف على الأقل";
                    errorString+="\n يجب ان تحوي مزيج بين الاحرف الكبيرة والصغيرة و الأرقام";

                    etxtPassword.setError(errorString);
                    return;
                }

                //get integer cycle length
                int cycleLength=Integer.parseInt(etxtCyclrLength.getText().toString()) ;
                //get age
                int age=Integer.parseInt(etxtAge.getText().toString()) ;
                //check if age is valid
                if(age<=0 ||age>=100)
                {
                    Toast.makeText(SignUpActivity.this,"يجب إدخال قيمة مناسبة للعمر بين 1 و 100" ,Toast.LENGTH_LONG).show();
                    return;
                }
                //check if pasword match
                if(!password.equals(confirmpassword))
                {
                    Toast.makeText(SignUpActivity.this,"كلمات المرور غير متطابقة" ,Toast.LENGTH_LONG).show();
                    return;
                }


                //free to signup
                //create new pregnant object
                Pregnant newPregnant=new Pregnant();
                newPregnant.setFullname(fullname);
                newPregnant.setAge(age);
                if(cycleLength>0)
                newPregnant.setCycleLength(cycleLength);
                newPregnant.setEmail(email);
                newPregnant.setPassword(password);
                newPregnant.setLmp(lmp);
                newPregnant.calculatePregnancyInfo();
                //check lmp date validity
                if(newPregnant.getPregnanyDays()>280)
                {
                    Toast.makeText(SignUpActivity.this,"التاريخ المدخل خارج فترة الحمل" ,Toast.LENGTH_LONG).show();
                    return;
                }




                //call register fuction from helper
                mFireHelper.registerNewUser(newPregnant);


            }
        });

    }

    public  boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

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
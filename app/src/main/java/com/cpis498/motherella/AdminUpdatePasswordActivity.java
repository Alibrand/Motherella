package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpis498.motherella.models.Pregnant;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminUpdatePasswordActivity extends AppCompatActivity {
    EditText etxtOldPassword,etxtNewPassword,etxtConfirmPassword;
    AppCompatButton btnUpdatePassword;
    ImageView btnToggleOldPassword,btnToggleNewPassword,btnToggleConfirmPassword;
    FirebaseHelper mFireHelper;
    FirebaseFirestore mFirestore;
    String uid;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_password);
        //initialize variables
        etxtOldPassword = findViewById(R.id.etxt_old_password);
        etxtNewPassword = findViewById(R.id.etxt_new_password);
        etxtConfirmPassword = findViewById(R.id.etxt_confirm_password);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        btnToggleOldPassword = findViewById(R.id.btn_show_hide_old_password);
        btnToggleNewPassword = findViewById(R.id.btn_show_hide_new_password);
        btnToggleConfirmPassword = findViewById(R.id.btn_show_hide_confirm_password);
        mFirestore = FirebaseFirestore.getInstance();
        mFireHelper = new FirebaseHelper(this);
        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get pregnant uid
        uid = mFireHelper.getLoggedUserId();


        //when tap on eye icon to show password or hide
        btnToggleOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtOldPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    etxtOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleOldPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                } else {
                    etxtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleOldPassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });
        //when tap on eye icon to show password or hide
        btnToggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtNewPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    etxtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                } else {
                    etxtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });
        //when tap on eye icon to show confirm password or hide
        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtConfirmPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    etxtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                } else {
                    etxtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpassword = etxtOldPassword.getText().toString();
                String newpassword = etxtNewPassword.getText().toString();
                String confirmpassword = etxtConfirmPassword.getText().toString();

                //check if fields are empty
                if (oldpassword.trim().isEmpty() ||
                        newpassword.trim().isEmpty() || confirmpassword.trim().isEmpty()) {
                    Toast.makeText(AdminUpdatePasswordActivity.this, "توجد حقول فارغة!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isValidPassword(newpassword)) {
                    String errorString = "كلمة المرور يجب أن تكون 8 محارف على الأقل";
                    errorString += "\n يجب ان تحوي مزيج بين الاحرف الكبيرة والصغيرة و الأرقام";

                    etxtNewPassword.setError(errorString);
                    return;
                }
                //check if pasword match
                if (!newpassword.equals(confirmpassword)) {
                    Toast.makeText(AdminUpdatePasswordActivity.this, "كلمات المرور غير متطابقة", Toast.LENGTH_LONG).show();
                    return;
                }

                mFireHelper.updatePassword(oldpassword, newpassword);
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
}
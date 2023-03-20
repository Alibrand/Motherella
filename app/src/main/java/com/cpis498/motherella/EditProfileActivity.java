package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    EditText etxtFullName,etxtAge,etxtOldPassword,etxtNewPassword,etxtConfirmPassword,etxtCyclrLength;
    DatePicker dateLMP;
    AppCompatButton btnSave,btnUpdatePassword;
    ImageView btnToggleOldPassword,btnToggleNewPassword,btnToggleConfirmPassword;
    FirebaseHelper mFireHelper;
    FirebaseFirestore mFirestore;
    String uid;
    ProgressDialog dialog;
    Pregnant currentPregnant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //initialize variables
        etxtFullName=findViewById(R.id.etxt_user_fullname);
        etxtAge=findViewById(R.id.etxt_user_age);
        etxtOldPassword=findViewById(R.id.etxt_old_password);
        etxtNewPassword=findViewById(R.id.etxt_new_password);
        etxtConfirmPassword=findViewById(R.id.etxt_confirm_password);
        etxtCyclrLength=findViewById(R.id.etxt_cycle_length);
        dateLMP=findViewById(R.id.date_lmp);
        btnSave=findViewById(R.id.btn_save_profile);
        btnUpdatePassword=findViewById(R.id.btn_update_password);
        btnToggleOldPassword=findViewById(R.id.btn_show_hide_old_password);
        btnToggleNewPassword=findViewById(R.id.btn_show_hide_new_password);
        btnToggleConfirmPassword=findViewById(R.id.btn_show_hide_confirm_password);
        mFirestore=FirebaseFirestore.getInstance();
        mFireHelper=new FirebaseHelper(this);

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //get pregnant uid
        uid=mFireHelper.getLoggedUserId();

        //get info
        getPregnantInfo();

        //set max date for date picker
        dateLMP.setMaxDate(new Date().getTime());



        //when tap on eye icon to show password or hide
        btnToggleOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etxtOldPassword.getTransformationMethod()== PasswordTransformationMethod.getInstance())
                {
                    etxtOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleOldPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    etxtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleOldPassword.setImageResource(R.drawable.ic_eye_visible_pass);
                }

            }
        });
        //when tap on eye icon to show password or hide
        btnToggleNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etxtNewPassword.getTransformationMethod()== PasswordTransformationMethod.getInstance())
                {
                    etxtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_slash_hide_pass);
                }else
                {
                    etxtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_visible_pass);
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

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpassword=etxtOldPassword.getText().toString();
                String newpassword=etxtNewPassword.getText().toString();
                String confirmpassword=etxtConfirmPassword.getText().toString();

                //check if fields are empty
                if(oldpassword.trim().isEmpty()||
                        newpassword.trim().isEmpty()|| confirmpassword.trim().isEmpty()){
                    Toast.makeText(EditProfileActivity.this,"توجد حقول فارغة!" ,Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isValidPassword(newpassword)){
                    String errorString="كلمة المرور يجب أن تكون 8 محارف على الأقل";
                    errorString+="\n يجب ان تحوي مزيج بين الاحرف الكبيرة والصغيرة و الأرقام";

                    etxtNewPassword.setError(errorString);
                    return;
                }
                //check if pasword match
                if(!newpassword.equals(confirmpassword))
                {
                    Toast.makeText(EditProfileActivity.this,"كلمات المرور غير متطابقة" ,Toast.LENGTH_LONG).show();
                    return;
                }

                mFireHelper.updatePassword(oldpassword,newpassword);
            }
        });


        //when tap save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get info from fields
                String fullname=etxtFullName.getText().toString();
                String ageString=etxtAge.getText().toString();

                String cycleLengthString=etxtCyclrLength.getText().toString();
                //get lmp date
                Calendar lmpCalendar=Calendar.getInstance();
                lmpCalendar.set(dateLMP.getYear(),dateLMP.getMonth(),dateLMP.getDayOfMonth());
                Date lmp=lmpCalendar.getTime();
                //check if fields are empty
                if(fullname.trim().isEmpty() || ageString.trim().isEmpty()||cycleLengthString.trim().isEmpty()){
                    Toast.makeText(EditProfileActivity.this,"توجد حقول فارغة!" ,Toast.LENGTH_LONG).show();
                    return;
                }




                //get integer cycle length
                int cycleLength=Integer.parseInt(etxtCyclrLength.getText().toString()) ;
                //get age
                int age=Integer.parseInt(etxtAge.getText().toString()) ;
                //check if age is valid
                if(age<=0 ||age>=100)
                {
                    Toast.makeText(EditProfileActivity.this,"يجب إدخال قيمة مناسبة للعمر بين 1 و 100" ,Toast.LENGTH_LONG).show();
                    return;
                }



                //update current info
                currentPregnant.setFullname(fullname);
                currentPregnant.setAge(age);
                if(cycleLength>0)
                    currentPregnant.setCycleLength(cycleLength);
                currentPregnant.setLmp(lmp);
                currentPregnant.calculatePregnancyInfo();
                //check lmp date validity
                if(currentPregnant.getPregnanyDays()>280)
                {
                    Toast.makeText(EditProfileActivity.this,"التاريخ المدخل خارج فترة الحمل" ,Toast.LENGTH_LONG).show();
                    return;
                }
                //show waiting
                if(dialog==null)
                dialog=new ProgressDialog(EditProfileActivity.this);
                dialog.setMessage("الرجاء الانتظار قليلا");
                dialog.setCancelable(false);
                dialog.show();

                mFirestore.collection("pregnant_info")
                        .whereEqualTo("id",uid)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);

                                 mFirestore.collection("pregnant_info")
                                         .document(doc.getId())
                                         .update(currentPregnant.toMap())
                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void unused) {
                                                 Toast.makeText(EditProfileActivity.this,"تم حفظ البيانات بنجاح" ,Toast.LENGTH_LONG).show();
                                                 dialog.dismiss();
                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(EditProfileActivity.this,"حدث خطأ أثناء الحفظ" ,Toast.LENGTH_LONG).show();
                                         dialog.dismiss();
                                     }
                                 });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this,"حدث خطأ في الحصول على البيانات" ,Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });





            }
        });
    }

    private void getPregnantInfo(){
        //show waiting
        dialog=new ProgressDialog(this);
        dialog.setMessage("الرجاء الانتظار قليلا");
        dialog.setCancelable(false);
        dialog.show();
        //initialize variable
        currentPregnant=new Pregnant() ;
        String uid=mFireHelper.getLoggedUserId();
        mFirestore.collection("pregnant_info")
                .whereEqualTo("id",uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs=queryDocumentSnapshots.getDocuments();
                        DocumentSnapshot info=docs.get(0);
                        Map<String,Object> data=info.getData();
                        //get pregnant object from data
                        currentPregnant=new Pregnant(data) ;

                        etxtFullName.setText(currentPregnant.getFullname());
                        etxtAge.setText(String.valueOf(currentPregnant.getAge()) );
                        etxtCyclrLength.setText(String.valueOf(currentPregnant.getCycleLength()));
                        Calendar lmpcalendar=Calendar.getInstance();
                        lmpcalendar.setTime(currentPregnant.getLmp());

                        dateLMP.updateDate(lmpcalendar.get(Calendar.YEAR),
                                lmpcalendar.get(Calendar.MONTH),
                                lmpcalendar.get(Calendar.DATE));
                         dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this,"حدث خطأ في الحصول على البيانات" ,Toast.LENGTH_LONG).show();

                finish();
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
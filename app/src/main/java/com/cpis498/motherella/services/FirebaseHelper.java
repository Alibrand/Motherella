package com.cpis498.motherella.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cpis498.motherella.AdminActivity;
import com.cpis498.motherella.HomeActivity;
import com.cpis498.motherella.LoginActivity;
import com.cpis498.motherella.models.Pregnant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    Context mContext;
    FirebaseAuth mAuth;
    FirebaseFirestore mFireStore;
    ProgressDialog progressDialog;


    public FirebaseHelper(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
    }

    public void registerNewUser(Pregnant pregnant) {
        showDialog("الرجاء الانتظار قليلاً");
        mAuth.createUserWithEmailAndPassword(pregnant.getEmail(), pregnant.getPassword())
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                           FirebaseUser newUser=mAuth.getCurrentUser();
                            pregnant.setId(newUser.getUid());
                            //save pregnant info to firebase store
                            mFireStore.collection("pregnant_info")
                                    .add(pregnant.toMap())
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //after a successful signup
                                    //go to Trimester check
                                    Intent intent=new Intent(mContext, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    checkException(task.getException());
                                }
                            });



                        } else {
                            checkException(task.getException());
                             }
                        dismissDialog();
                    }
                });
    }

    public void login(String email, String password) {
        showDialog("الرجاء الانتظار قليلاً");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //after a successful signin
                            //check if the user is the admin
                            if(user.getEmail().equals("admin@motherella.com")) {
                                Intent intent = new Intent(mContext, AdminActivity.class);
                                mContext.startActivity(intent);
                            }
                            //if the user is not admin
                            else{
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                            }
                            ((Activity) mContext).finish();

                            Toast.makeText(mContext, "أهلا وسهلا بك" , Toast.LENGTH_LONG).show();

                        } else {
                            checkException(task.getException());

                        }
                        dismissDialog();
                    }
                });
    }


    public  void updatePassword(String oldPass,String newPass)
    {
        showDialog("الرجاء الانتظار قليلاً");
         FirebaseUser currentUser=mAuth.getCurrentUser();
        final String email = currentUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        currentUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        currentUser.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mContext, "تم تعديل كلمة المرور بنجاح" , Toast.LENGTH_LONG).show();
                                        dismissDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                checkException(e);
                                dismissDialog();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                checkException(e);
                dismissDialog();
            }
        });

    }



    public void resetPassword(String email) {
        showDialog("الرجاء الانتظار قليلاً");
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(mContext, "لقد تم إرسال رابط إلى " + email, Toast.LENGTH_LONG).show();
                        } else {
                             checkException(task.getException());
                        }
                        dismissDialog();
                    }
                });
    }

    public String getLoggedUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid= currentUser.getUid();
            return uid;

        } else
            return null;
    }

    public void logout() {
        mAuth.signOut();

        Intent intent=new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }




    //dialog functions

    private void showDialog(String message) {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void checkException(Exception taskException){
        try {
            throw taskException;
        } catch(FirebaseAuthWeakPasswordException e) {
            Toast.makeText(mContext,"كلمة المرور ضعيفة يرجى اختيار كلمة اقوى", Toast.LENGTH_LONG).show();

        } catch(FirebaseAuthInvalidCredentialsException e) {
            Toast.makeText(mContext,"كلمة المرور خطأ", Toast.LENGTH_LONG).show();

        }
        catch( FirebaseAuthInvalidUserException e) {
            Toast.makeText(mContext,"البريد الالكتروني غير موجود في النظام", Toast.LENGTH_LONG).show();
        }
        catch( FirebaseAuthUserCollisionException e) {
            Toast.makeText(mContext,"يوجد حساب آخر يستخدم البريد الالكتروني ذاته", Toast.LENGTH_LONG).show();
        }
     catch( FirebaseAuthEmailException e) {
            Toast.makeText(mContext,"البريد الالكتروني مكتوب بصيغة غير صحيحة", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(mContext,"حدث خطأ", Toast.LENGTH_LONG).show();

        }
    }
}

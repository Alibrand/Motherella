package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.models.Contraction;
import com.cpis498.motherella.models.LabourContractions;
import com.cpis498.motherella.models.Pregnant;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Timer;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ContractionTimerActivity extends AppCompatActivity {
    AppCompatButton btnStartStop,btnReset,btnSave,btnHistory;
    TextView txtContractionssRecord,txtContractionType,txtContractionMode,txtSetMode;
    boolean timerStarted=false;
    LabourContractions contractions;
    Contraction currentContraction;
    Chronometer chronometer;
    Calendar calendar;
    FirebaseHelper mFirehelper;
    FirebaseFirestore mFireStore;
    ProgressDialog dialog;
    GifImageView ivHourglass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contraction_timer);
        //initialize variables and objects
        btnReset=findViewById(R.id.btn_reset);
        btnStartStop=findViewById(R.id.btn_start_stop);
        btnSave=findViewById(R.id.btn_save);
        btnHistory=findViewById(R.id.btn_history);
        chronometer=findViewById(R.id.chronometer);
        txtContractionssRecord=findViewById(R.id.txt_contractions_record);
        txtContractionType=findViewById(R.id.txt_contraction_type);
        ivHourglass=findViewById(R.id.hourglass);
        txtContractionMode=findViewById(R.id.txt_contraction_mode);
        txtSetMode=findViewById(R.id.txt_set_mode);
       // ivHourglass.setVisibility(View.INVISIBLE);
        ((GifDrawable)ivHourglass.getDrawable()).stop();
        //get current date
        calendar=Calendar.getInstance();
        contractions=new LabourContractions(calendar.getTime());
        mFirehelper=new FirebaseHelper(this);
        mFireStore=FirebaseFirestore.getInstance();

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set on click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLabourCnotractions();
            }
        });


        //set on click listener
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch activity
                Intent intent=new Intent(ContractionTimerActivity.this,ContractionsHistoryActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if timer not started
               if(timerStarted==false) {
                   //start timer
                   chronometer.setBase( SystemClock.elapsedRealtime());
                   timerStarted=true;

                   chronometer.start();
                   //create new contraction object
                   currentContraction=new Contraction();
                   calendar=Calendar.getInstance();
                   //set start time for the new contraction
                   currentContraction.setStart_time(calendar.getTime());
                   //alter button la
                   btnStartStop.setText("إيقاف");
                 //  ivHourglass.setVisibility(View.VISIBLE);
                   txtContractionMode.setBackgroundResource(R.drawable.bg_round_red_corners);
                   txtSetMode.setBackgroundResource(R.drawable.bg_round_gray_corners);

                   ((GifDrawable)ivHourglass.getDrawable()).reset();

               }
               else {
                  // ivHourglass.setVisibility(View.INVISIBLE);
                  //((GifDrawable)ivHourglass.getDrawable()).reset();

                   txtContractionMode.setBackgroundResource(R.drawable.bg_round_gray_corners);
                   txtSetMode.setBackgroundResource(R.drawable.bg_round_green_corners);
                   chronometer.setBase( SystemClock.elapsedRealtime());
                   chronometer.start();
                   //((GifDrawable)ivHourglass.getDrawable()).stop();
                       btnStartStop.setText("إبدأ");
                       timerStarted=false;
                   calendar=Calendar.getInstance();
                   currentContraction.setEnd_time(calendar.getTime());
                   contractions.addContraction(currentContraction);
                   txtContractionssRecord.setText(contractions.toString());
                    checkType();

               }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetContractions();
                ((GifDrawable)ivHourglass.getDrawable()).stop();
            }
        });
    }

    private void resetContractions() {
        txtContractionMode.setBackgroundResource(R.drawable.bg_round_gray_corners);
        txtSetMode.setBackgroundResource(R.drawable.bg_round_gray_corners);
        contractions.clear();
        checkType();
        txtContractionssRecord.setText(contractions.toString());
        chronometer.stop();
        btnStartStop.setText("إبدأ");
        timerStarted=false;
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void saveLabourCnotractions() {
        if(contractions.getContractions().size()==0)
        {
            Toast.makeText(ContractionTimerActivity.this,"لا توجد نتائج للحفظ",Toast.LENGTH_LONG).show();
            return;
        }
        //show waiting
        dialog=new ProgressDialog(this);
        dialog.setMessage("الرجاء الانتظار قليلا");
        dialog.setCancelable(false);
        dialog.show();
        //initialize variable
        String uid=mFirehelper.getLoggedUserId();
        contractions.setUid(uid);
        mFireStore.collection("labour_history")
                .add(contractions)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ContractionTimerActivity.this,"تم حفظ النتائج بنجاح",Toast.LENGTH_LONG).show();
                        resetContractions();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContractionTimerActivity.this,"حدث حطأ أثناء حفظ النتائج",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }


    private  void checkType(){
        if(contractions.getContractions().size()==0)
            txtContractionType.setVisibility(View.INVISIBLE);
        else
            txtContractionType.setVisibility(View.VISIBLE);
        if(contractions.isActive())
        {
            txtContractionType.setText("صادق");
            txtContractionType.setBackgroundResource(R.drawable.bg_round_red_corners);
        }
        else{
            txtContractionType.setText("كاذب");
            txtContractionType.setBackgroundResource(R.drawable.bg_round_green_corners);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        chronometer.stop();

    }
}
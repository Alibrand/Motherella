package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.models.Pregnant;
import com.cpis498.motherella.models.Reminder;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    Pregnant currentPregnant;
    FirebaseHelper mFireHelper;
    FirebaseFirestore mFireStore;
    ProgressDialog dialog;
    TextView txtTrimesterInfo;

    AppCompatButton btnReviews,btnContractionTimer,btnReminders,btnPregnancyInfo,
    btnExercises,btnHealthyRecipes,btnFavourites,btnContactUs;
    ImageButton btnEditProfile,btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //initialize variables
        mFireHelper=new FirebaseHelper(this);
        mFireStore=FirebaseFirestore.getInstance();
        txtTrimesterInfo=findViewById(R.id.txt_pregnancy_info);
        btnReviews=findViewById(R.id.btn_reviews);
        btnContractionTimer=findViewById(R.id.btn_contraction_timer);
        btnReminders=findViewById(R.id.btn_reminders);
        btnPregnancyInfo=findViewById(R.id.btn_info);
        btnExercises=findViewById(R.id.btn_excersices);
        btnHealthyRecipes=findViewById(R.id.btn_healthy_recipes);
        btnEditProfile=findViewById(R.id.btn_edit_profile);
        btnFavourites=findViewById(R.id.btn_favourite);
        btnContactUs=findViewById(R.id.btn_contactus);
        btnLogout=findViewById(R.id.btn_log_out);



        //set on click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFireHelper.logout();
            }
        });


        //set on click listener
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });


        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("motherella.project@gmail.com"));
                 intent.setType("message/rfc822");
                intent.setPackage("com.google.android.gm");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "motherella.project@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "شاركنا الرأي");
                intent.putExtra(Intent.EXTRA_TEXT, "شاركونا بآرائكم");
                startActivity(Intent.createChooser(intent, ""));
            }
        });




        //set on click listener
        btnHealthyRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,RecipesActivity.class);
                intent.putExtra("trimester",currentPregnant.getTrimester());
                intent.putExtra("trimesterLabel",currentPregnant.getTrimesterLabel());

                startActivity(intent);
            }
        });

        //set on click listener
        btnExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(HomeActivity.this,ExercisesActivity.class);
                intent.putExtra("trimester",currentPregnant.getTrimester());
                intent.putExtra("trimesterLabel",currentPregnant.getTrimesterLabel());
                startActivity(intent);
            }
        });

        //set on click listener
        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ReviewsActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,VaccinationsReminderActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnContractionTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ContractionTimerActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnPregnancyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,PregnancyInfoActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,FavouritesActivity.class);
                startActivity(intent);
            }
        });



        //get current logged pregnant info from firebase
        getPregnantInfo();










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
                    mFireStore.collection("pregnant_info")
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
                            //calculate pregnancy info
                            currentPregnant.calculatePregnancyInfo();
                            //tell the user about the trimester
                            txtTrimesterInfo.setText("أنت في ثلث الحمل " + currentPregnant.getTrimesterLabel());

                            //ckeck if the pregnenat missed a vaccine
                            mFireStore.collection("reminders")
                                    .whereEqualTo("uid",uid).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            boolean complete=true;
                                            if(queryDocumentSnapshots.size()>0)
                                            {DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                                            int trimesterNumber= currentPregnant.getTrimester();
                                            Map<String,Boolean> trimester= (Map<String, Boolean>) doc.get("trimester"+trimesterNumber);
                                            //check trimester
                                            for (Map.Entry<String,Boolean> entry:
                                                    trimester.entrySet())
                                                    complete&=entry.getValue();
                                            }
                                            if(!complete)
                                            {
                                              notifyPregnant();
                                            }
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this,"حدث خطأ في العملية ",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
    }

    private void notifyPregnant() {
        createNotificationChannel();
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, VaccinationsReminderActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("تنبيه")
                .setContentText("يجب أن تكملي تطعميات ومكملات ثلث الحمل "+currentPregnant.getTrimesterLabel())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOngoing(true)
       .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
      .setLights(Color.MAGENTA, 3000, 3000)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("تنبيه")
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.ic_report_svgrepo_com,"متابعة" ,pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mychannel";
            String description = "new";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
            channel.setLightColor(Color.MAGENTA );
            channel.enableLights(true);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
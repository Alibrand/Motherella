package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.models.Reminder;
import com.cpis498.motherella.services.FirebaseHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VaccinationsReminderActivity extends AppCompatActivity {

    PieChart chart;
    FirebaseHelper mFireHelper;
    FirebaseFirestore mFireStore;
    Reminder reminder;
    CheckBox chbxFolicAcid,chbxVitaminB12,chbxUltraSonic1,chkbxUltrasonic2,
        chkbxGlucoseTest2,chkbxSupplements2,chkbxGlucoseTest3,chkbxSupplements3,
           chkbxVaccine ;
    AppCompatButton btnSave;
    TextView txtResult;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccinations_reminder);

        chart = (PieChart) findViewById(R.id.chart);
        chbxFolicAcid=findViewById(R.id.chbx_folic_acid);
        chbxVitaminB12=findViewById(R.id.chbx_vitamin_b12);
        chbxUltraSonic1=findViewById(R.id.chbx_ultrasonic1);
        chkbxUltrasonic2=findViewById(R.id.chbx_ultrasonic2);
        chkbxGlucoseTest2=findViewById(R.id.chbx_gluocose_test2);
        chkbxSupplements2=findViewById(R.id.chbx_supplements2);
        chkbxGlucoseTest3=findViewById(R.id.chbx_gluocose_test3);
        chkbxSupplements3=findViewById(R.id.chbx_supplements3);
        chkbxVaccine=findViewById(R.id.chbx_vaccine);
        btnSave=findViewById(R.id.btn_save);
        txtResult=findViewById(R.id.txt_result);

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mFireHelper=new FirebaseHelper(this);
        mFireStore=FirebaseFirestore.getInstance();
        uid=mFireHelper.getLoggedUserId();
        //load data from firebase
        loadData();

        //set on click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog=new ProgressDialog(VaccinationsReminderActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("يتم حفظ التغييرات");
                dialog.show();
                //get values from checkboxes
                Map<String,Boolean> trimester1=new HashMap<String,Boolean>();
                trimester1.put("folic_acid",chbxFolicAcid.isChecked());
                trimester1.put("vitamin_b12",chbxVitaminB12.isChecked());
                trimester1.put("ultra_sonic",chbxUltraSonic1.isChecked());
                Map<String,Boolean> trimester2=new HashMap<String,Boolean>();
                trimester2.put("glucose_test",chkbxGlucoseTest2.isChecked());
                trimester2.put("supplements",chkbxSupplements2.isChecked());
                trimester2.put("ultra_sonic",chkbxUltrasonic2.isChecked());
                Map<String,Boolean> trimester3=new HashMap<String,Boolean>();
                trimester3.put("glucose_test",chkbxGlucoseTest3.isChecked());
                trimester3.put("supplements",chkbxSupplements3.isChecked());
                trimester3.put("vaccine",chkbxVaccine.isChecked());
                reminder.setTrimster1(trimester1);
                reminder.setTrimster2(trimester2);
                reminder.setTrimster3(trimester3);

                //update in firestore
                mFireStore.collection("reminders")
                        .document(reminder.getId())
                        .update(reminder.toMap())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(VaccinationsReminderActivity.this,"تم الحفظ بنجاح" ,Toast.LENGTH_LONG).show();
                                //load data and show the new chart
                                loadData();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VaccinationsReminderActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

            }
        });





    }

    private void loadData(){
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("الرجاء الانتظار قليلاً");
        dialog.show();
        mFireStore.collection("reminders")
                .whereEqualTo("uid",uid)
                .get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Map<String, Object> data = documentSnapshot.getData();
                            reminder = new Reminder(data);
                            reminder.setId(documentSnapshot.getId());
                            chbxFolicAcid.setChecked(reminder.getTrimster1().get("folic_acid"));
                            chbxVitaminB12.setChecked(reminder.getTrimster1().get("vitamin_b12"));
                            chbxUltraSonic1.setChecked(reminder.getTrimster1().get("ultra_sonic"));
                            chkbxGlucoseTest2.setChecked(reminder.getTrimster2().get("glucose_test"));
                            chkbxSupplements2.setChecked(reminder.getTrimster2().get("supplements"));
                            chkbxUltrasonic2.setChecked(reminder.getTrimster2().get("ultra_sonic"));
                            chkbxGlucoseTest3.setChecked(reminder.getTrimster3().get("glucose_test"));
                            chkbxSupplements3.setChecked(reminder.getTrimster3().get("supplements"));
                            chkbxVaccine.setChecked(reminder.getTrimster3().get("vaccine"));
                            showChartData();
                            dialog.dismiss();
                        }
                        else{
                            reminder = new Reminder();
                            reminder.setUid(uid);
                            mFireStore.collection("reminders")
                                    .add(reminder.toMap())
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            dialog.dismiss();
                                            loadData();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VaccinationsReminderActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }



                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VaccinationsReminderActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
            dialog.dismiss();
            }
        });
        

    }

    private void showChartData() {


        int firstRatio=0;
        int secondRatio=0;
        int thirdRatio=0;
        int totalRatio=0;
        
        //check first trimester
        for (Map.Entry<String,Boolean> entry:
             reminder.getTrimster1().entrySet()) {
           if(entry.getValue()==true)
               firstRatio++;
        }

        //check second trimester
        for (Map.Entry<String,Boolean> entry:
                reminder.getTrimster2().entrySet()) {
            if(entry.getValue()==true)
                secondRatio++;
        }


        //check third trimester
        for (Map.Entry<String,Boolean> entry:
                reminder.getTrimster3().entrySet()) {
            if(entry.getValue()==true)
                thirdRatio++;
        }


        ArrayList<PieEntry> entries=new ArrayList<>();

        // add slices to chart
        if(firstRatio>0)
        entries.add(new PieEntry(firstRatio*100/3,"الأول"));
        if(secondRatio>0)
        entries.add(new PieEntry(secondRatio*100/3,"الثاني"));
        if(thirdRatio>0)
        entries.add(new PieEntry(thirdRatio*100/3,"الثالث"));

         totalRatio=(firstRatio+secondRatio+thirdRatio);
        totalRatio=totalRatio*100/9;
        if(totalRatio>0)
        txtResult.setText( "لقد استكملت نسبة " + totalRatio + " % من التطعيمات والفحوصات");
        else
            txtResult.setText("يجب أن تستكملي الفحوصات والمكملات الغذائية والتطعيمات");

        PieDataSet pieDataSet=new PieDataSet(entries,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        int [] color={ Color.rgb(174, 1, 126),
                Color.rgb(73, 0, 106),
                Color.rgb(122, 1, 119),};
        pieDataSet.setColors(color);

        pieDataSet.setValueTextColor(Color.WHITE);


        pieDataSet.setValueTextSize(15f);

        PieData pieData=new PieData(pieDataSet);
        pieData.notifyDataChanged();

        chart.setData(pieData);
        chart.getDescription().setEnabled(false);
        if(totalRatio==0)
        chart.setCenterText("لاتوجد بيانات");
        else
            chart.setCenterText("ثلث الحمل");
        chart.notifyDataSetChanged();
        chart.invalidate();




    }
}
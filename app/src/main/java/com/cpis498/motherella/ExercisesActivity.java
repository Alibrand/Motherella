package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ExercisesScrollAdapter;
import com.cpis498.motherella.models.Exercise;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExercisesActivity extends AppCompatActivity {
    RecyclerView rvExercises;
    TextView txtMainTitle;
    int trimester;
    String trimesterLabel;
    FirebaseFirestore mFireStore;
    FirebaseHelper mFireHelper;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        //initialize objects
        rvExercises=findViewById(R.id.rv_exercises);
        txtMainTitle=findViewById(R.id.main_label);
        mFireStore=FirebaseFirestore.getInstance();
        mFireHelper= new FirebaseHelper(this);
        uid=mFireHelper.getLoggedUserId();

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        //get trimester from intent
        Intent intent=getIntent();
        trimester=intent.getIntExtra("trimester",1);
        trimesterLabel=intent.getStringExtra("trimesterLabel");
        txtMainTitle.setText(  "تمارين خاصة بثلث الحمل " + trimesterLabel);
//        //load exercises from firestore
        loadExercices();

    }

    private void loadExercices() {
        List<Exercise> exerciseList=new ArrayList<Exercise>();
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("الرجاء الانتظار قليلاً");
        dialog.setCancelable(false);
        dialog.show();
        //query data from firestore
        mFireStore.collection("exercises")
                .whereEqualTo("trimester",String.valueOf(trimester))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                        {

                            Map<String,Object> map=doc.getData();
                            map.put("id",doc.getId());
                            Exercise exercise=new Exercise(map);
                            exerciseList.add(exercise);
                        }

                        //create scroll adapter
                        ExercisesScrollAdapter adapter=new ExercisesScrollAdapter(exerciseList,ExercisesActivity.this,uid);
                        rvExercises.setAdapter(adapter);
                         dialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(ExercisesActivity.this,"حدث خطأ في تحميل البيانات" , Toast.LENGTH_LONG).show();

            }
        });

    }
}
package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ExercisesScrollAdapter;
import com.cpis498.motherella.models.Exercise;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminManageExercisesActivity extends AppCompatActivity {

    RecyclerView rvExercises;
    List<Exercise> exerciseList;
    AppCompatButton btnSendExercise;
    EditText etxtExerciseTitle, etxtExerciseLink;
    RadioGroup trimesterGroup;

    FirebaseFirestore mFireStore;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar,progressBarSending;
    String uid="admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_exercises);
        //initialize variables
        rvExercises=findViewById(R.id.rv_exercises);
        btnSendExercise=findViewById(R.id.btn_send_exercise);
        etxtExerciseTitle =findViewById(R.id.etxt_exercise_title);
        etxtExerciseLink =findViewById(R.id.etxt_exercise_link);
        trimesterGroup=findViewById(R.id.gp_trimester);

        progressBar=findViewById(R.id.progress_bar);
        progressBarSending=findViewById(R.id.progress_sending);
        mFireStore=FirebaseFirestore.getInstance();
        firebaseHelper=new FirebaseHelper(this);

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        //set on click listener to send
        btnSendExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title= etxtExerciseTitle.getText().toString();
                String link= etxtExerciseLink.getText().toString();
                String trimester="1";
                RadioButton selectedTrimesterRadio=findViewById(trimesterGroup.getCheckedRadioButtonId());
                trimester=selectedTrimesterRadio.getText().toString();




                //check if empty
                if(title.trim().isEmpty()||link.trim().isEmpty() )
                {
                    Toast.makeText(AdminManageExercisesActivity.this,"يوجد حقول فارغة",Toast.LENGTH_LONG).show();
                    return;
                }
                //test youtube url
                String videoId=extractYTId(link);

                if(videoId==null)
                {
                    Toast.makeText(AdminManageExercisesActivity.this,"رابط الفيديو غير صالح..يرجى نسخ رابط يوتيوب",Toast.LENGTH_LONG).show();
                    return;
                }
                //form the image thumbnail url
                String imageUrl="https://img.youtube.com/vi/"+videoId+"/0.jpg";

                Exercise newExercise=new Exercise();
                newExercise.setVideo_url(link);
                newExercise.setImage_url(imageUrl);
                newExercise.setTitle(title);
                newExercise.setTrimester(trimester);

                //show loading
                progressBarSending.setVisibility(View.VISIBLE);

                //call firestore to add new exercise
                mFireStore.collection("exercises")
                        .add(newExercise).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBarSending.setVisibility(View.INVISIBLE);
                        Toast.makeText(AdminManageExercisesActivity.this,"تمت إضافة الفيديو بنجاح",Toast.LENGTH_LONG).show();
                        getExercises();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminManageExercisesActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

                    }
                });




            }
        });

        //get exercises from database
        getExercises();









    }
    
    

    private void getExercises() {
        progressBar.setVisibility(View.VISIBLE);
        exerciseList=new ArrayList<Exercise>();
        rvExercises.setAdapter(null);
        mFireStore.collection("exercises")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> map=doc.getData();
                            // add doc id
                            map.put("id",doc.getId());
                            Exercise exercise=new Exercise(map);
                            exerciseList.add(exercise);
                        }
                        ExercisesScrollAdapter adapter=new ExercisesScrollAdapter(exerciseList,AdminManageExercisesActivity.this,uid);
                        rvExercises.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        etxtExerciseTitle.setText("");
                        etxtExerciseLink.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminManageExercisesActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }
}
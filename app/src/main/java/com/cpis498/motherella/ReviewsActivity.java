package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ReviewsScrollAdapter;
import com.cpis498.motherella.models.Review;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {
    RecyclerView rvReviews;
    List<Review> reviewList;
    AppCompatButton btnSendReview;
    EditText etxtDoctorName,etxtDoctorSp,etxtcomments;
    RatingBar rbRating;
    FirebaseFirestore mFireStore;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar,progressBarSending;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        //initialize variables
        rvReviews=findViewById(R.id.rv_reviews);
        btnSendReview=findViewById(R.id.btn_send_review);
        etxtcomments=findViewById(R.id.etxt_review_comments);
        etxtDoctorName=findViewById(R.id.etxt_doctor_name);
        etxtDoctorSp=findViewById(R.id.etxt_doctor_sp);
        rbRating=findViewById(R.id.rb_review_rating);
        progressBar=findViewById(R.id.progress_bar);
        progressBarSending=findViewById(R.id.progress_sending);
        mFireStore=FirebaseFirestore.getInstance();
        firebaseHelper=new FirebaseHelper(this);
        uid=firebaseHelper.getLoggedUserId();
        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //set on click listener to send
        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=etxtDoctorName.getText().toString();
                String speciality=etxtDoctorSp.getText().toString();
                String comments=etxtcomments.getText().toString();
                float rating=rbRating.getRating();

                //check if empty
                if(name.trim().isEmpty()||speciality.trim().isEmpty()||
                comments.trim().isEmpty())
                {
                    Toast.makeText(ReviewsActivity.this,"يوجد حقول فارغة",Toast.LENGTH_LONG).show();
                    return;
                }
                Review newReview=new Review();
                newReview.setRating(rating);
                newReview.setDoctor_specialitiy(speciality);
                newReview.setDoctor_name(name);
                newReview.setComments(comments);
                newReview.setUid(uid);
                //get  current date or today
                Calendar calendar= Calendar.getInstance();
                Date now=calendar.getTime();
                newReview.setReview_date(now);
                //show loading
                progressBarSending.setVisibility(View.VISIBLE);

                //call firestore to add new review
                mFireStore.collection("reviews")
                        .add(newReview).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBarSending.setVisibility(View.INVISIBLE);
                        Toast.makeText(ReviewsActivity.this,"تمت إضافة التقييم بنجاح",Toast.LENGTH_LONG).show();
                        getReviews();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewsActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

                    }
                });




            }
        });

        //get reviews from database
        getReviews();









    }

    private void getReviews() {
        progressBar.setVisibility(View.VISIBLE);
        reviewList=new ArrayList<Review>();
        rvReviews.setAdapter(null);
        mFireStore.collection("reviews")
                .orderBy("review_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> map=doc.getData();
                            // add doc id
                            map.put("id",doc.getId());
                            Review review=new Review(map);
                            reviewList.add(review);
                        }
                        ReviewsScrollAdapter adapter=new ReviewsScrollAdapter(reviewList,ReviewsActivity.this,uid);
                        rvReviews.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        etxtDoctorName.setText("");
                        etxtcomments.setText("");
                        etxtDoctorSp.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReviewsActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }
}
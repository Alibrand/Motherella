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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminManageReviews extends AppCompatActivity {
    RecyclerView rvReviews;
    List<Review> reviewList;
    FirebaseFirestore mFireStore;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar;
    String uid="admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_reviews);
        //initialize variables
        rvReviews=findViewById(R.id.rv_reviews);
        progressBar=findViewById(R.id.progress_bar);
        mFireStore=FirebaseFirestore.getInstance();
        firebaseHelper=new FirebaseHelper(this);

        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                        ReviewsScrollAdapter adapter=new ReviewsScrollAdapter(reviewList,AdminManageReviews.this,uid);
                        rvReviews.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminManageReviews.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }
}
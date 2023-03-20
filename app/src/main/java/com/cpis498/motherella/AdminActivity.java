package com.cpis498.motherella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.cpis498.motherella.services.FirebaseHelper;

public class AdminActivity extends AppCompatActivity {

    FirebaseHelper mFireHelper;

    AppCompatButton btnReviews, btnArticles,
            btnExercises,btnHealthyRecipes;
    ImageButton btnEditProfile,btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //initialize variables
        mFireHelper=new FirebaseHelper(this);
        btnReviews=findViewById(R.id.btn_reviews);
        btnArticles =findViewById(R.id.btn_articles);
        btnExercises=findViewById(R.id.btn_excersices);
        btnHealthyRecipes=findViewById(R.id.btn_healthy_recipes);
        btnEditProfile=findViewById(R.id.btn_edit_profile);
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
                Intent intent=new Intent(AdminActivity.this,AdminUpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnHealthyRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(AdminActivity.this,AdminManageRecipes.class);

                startActivity(intent);
            }
        });

        //set on click listener
        btnExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminActivity.this,AdminManageExercisesActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener
        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminActivity.this,AdminManageReviews.class);
                startActivity(intent);
            }
        });

        btnArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminActivity.this,AdminManageArticlesActivity.class);
                startActivity(intent);
            }
        });


    }
}
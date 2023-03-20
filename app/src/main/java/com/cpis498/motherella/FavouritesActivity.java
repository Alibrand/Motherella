package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ExercisesScrollAdapter;
import com.cpis498.motherella.adapters.RecipesScrollAdapter;
import com.cpis498.motherella.adapters.ReviewsScrollAdapter;
import com.cpis498.motherella.models.Exercise;
import com.cpis498.motherella.models.Recipe;
import com.cpis498.motherella.models.Review;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavouritesActivity extends AppCompatActivity {
    RecyclerView rvRecipes;
    RecyclerView rvExercises;
    FirebaseFirestore mFireStore;
    FirebaseHelper mFireHelper;
    String uid;
    ProgressBar progressBarExe,progressBarRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        //initialize objects
        rvRecipes=findViewById(R.id.rv_recipes);
        rvExercises=findViewById(R.id.rv_exercises);
        mFireStore=FirebaseFirestore.getInstance();
        mFireHelper= new FirebaseHelper(this);
        progressBarExe=findViewById(R.id.progress_bar_exe);
        progressBarRec=findViewById(R.id.progress_bar_rec);
        uid=mFireHelper.getLoggedUserId();
        ImageButton btnHome=findViewById(R.id.btn_home);

       btnHome.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });

        //load fav recipes
        loadFavRecipes();
        //load fav exercise
        loadFavExercices();


    }

    private void loadFavRecipes() {
        List<Recipe> recipeList=new ArrayList<Recipe>();

        //query data from firestore
        mFireStore.collection("recipes")
                .whereArrayContains("favourites",uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Map<String,Object> map=doc.getData();
                                map.put("id",doc.getId());
                                Recipe recipe = new Recipe(map);
                                recipeList.add(recipe);
                            }

                            //create scroll adapter
                            RecipesScrollAdapter adapter = new RecipesScrollAdapter(recipeList, FavouritesActivity.this,uid);
                            rvRecipes.setAdapter(adapter);
                            progressBarRec.setVisibility(View.GONE);
                        }
                        catch (Exception e)
                        {
                            progressBarRec.setVisibility(View.GONE);
                            Toast.makeText(FavouritesActivity.this,e.getMessage() , Toast.LENGTH_LONG).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              progressBarRec.setVisibility(View.GONE);
                Toast.makeText(FavouritesActivity.this,"حدث خطأ في تحميل البيانات" , Toast.LENGTH_LONG).show();

            }
        });

    }

    private void loadFavExercices() {
        List<Exercise> exerciseList=new ArrayList<Exercise>();


        //query data from firestore
        mFireStore.collection("exercises")
                .whereArrayContains("favourites",uid)
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
                        ExercisesScrollAdapter adapter=new ExercisesScrollAdapter(exerciseList,FavouritesActivity.this,uid);
                        rvExercises.setAdapter(adapter);
                        progressBarExe.setVisibility(View.GONE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBarExe.setVisibility(View.GONE);
                Toast.makeText(FavouritesActivity.this,"حدث خطأ في تحميل البيانات" , Toast.LENGTH_LONG).show();

            }
        });

    }
}
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

import com.cpis498.motherella.adapters.RecipesScrollAdapter;
import com.cpis498.motherella.models.Recipe;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipesActivity extends AppCompatActivity {
    RecyclerView rvRecipes;
    int trimester;
    TextView txtMainTitle;
    FirebaseFirestore mFireStore;
    FirebaseHelper mFireHelper;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        //initialize objects
        rvRecipes=findViewById(R.id.rv_recipes);
        mFireStore=FirebaseFirestore.getInstance();
        mFireHelper= new FirebaseHelper(this);
        uid=mFireHelper.getLoggedUserId();
        txtMainTitle=findViewById(R.id.main_label);
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
        String trimesterLabel=intent.getStringExtra("trimesterLabel");
        txtMainTitle.setText(  "وجبات صحية خاصة بثلث الحمل " + trimesterLabel);


//        //load recipes from firestore
        loadRecipes();

    }

    private void loadRecipes() {
        List<Recipe> recipeList=new ArrayList<Recipe>();
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("الرجاء الانتظار قليلاً");
        dialog.setCancelable(false);
        dialog.show();
        //query data from firestore
        mFireStore.collection("recipes")
                .whereEqualTo("trimester",String.valueOf(trimester))
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
                            RecipesScrollAdapter adapter = new RecipesScrollAdapter(recipeList, RecipesActivity.this,uid);
                            rvRecipes.setAdapter(adapter);
                            dialog.dismiss();
                        }
                        catch (Exception e)
                        {
                            dialog.dismiss();
                            Toast.makeText(RecipesActivity.this,e.getMessage() , Toast.LENGTH_LONG).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RecipesActivity.this,"حدث خطأ في تحميل البيانات" , Toast.LENGTH_LONG).show();

            }
        });

    }
}
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cpis498.motherella.adapters.RecipesScrollAdapter;
import com.cpis498.motherella.adapters.RecipesScrollAdapter;
import com.cpis498.motherella.models.Recipe;
import com.cpis498.motherella.models.Recipe;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminManageRecipes extends AppCompatActivity {
    RecyclerView rvRecipes;
    List<Recipe> recipeList;
    AppCompatButton btnSendRecipe;
    EditText etxtRecipeTitle, etxtRecipeLink;
     RadioGroup trimesterGroup;

    FirebaseFirestore mFireStore;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar,progressBarSending;
    String uid="admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_recipes);
        //initialize variables
        rvRecipes=findViewById(R.id.rv_recipes);
        btnSendRecipe=findViewById(R.id.btn_send_recipe);
        etxtRecipeTitle =findViewById(R.id.etxt_recipe_title);
        etxtRecipeLink =findViewById(R.id.etxt_recipe_link);
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
        btnSendRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title= etxtRecipeTitle.getText().toString();
                String link= etxtRecipeLink.getText().toString();
                String trimester="1";
                RadioButton selectedTrimesterRadio=findViewById(trimesterGroup.getCheckedRadioButtonId());
                trimester=selectedTrimesterRadio.getText().toString();




                //check if empty
                if(title.trim().isEmpty()||link.trim().isEmpty() )
                {
                    Toast.makeText(AdminManageRecipes.this,"يوجد حقول فارغة",Toast.LENGTH_LONG).show();
                    return;
                }
                Recipe newRecipe=new Recipe();
                newRecipe.setLink(link);
                newRecipe.setTitle(title);
                newRecipe.setTrimester(trimester);

                //show loading
                progressBarSending.setVisibility(View.VISIBLE);

                //call firestore to add new recipe
                mFireStore.collection("recipes")
                        .add(newRecipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBarSending.setVisibility(View.INVISIBLE);
                        Toast.makeText(AdminManageRecipes.this,"تمت إضافة الوصفة بنجاح",Toast.LENGTH_LONG).show();
                        getRecipes();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminManageRecipes.this,"حدث خطأ",Toast.LENGTH_LONG).show();

                    }
                });




            }
        });

        //get recipes from database
        getRecipes();









    }

    private void getRecipes() {
        progressBar.setVisibility(View.VISIBLE);
        recipeList=new ArrayList<Recipe>();
        rvRecipes.setAdapter(null);
        mFireStore.collection("recipes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> map=doc.getData();
                            // add doc id
                            map.put("id",doc.getId());
                            Recipe recipe=new Recipe(map);
                            recipeList.add(recipe);
                        }
                        RecipesScrollAdapter adapter=new RecipesScrollAdapter(recipeList,AdminManageRecipes.this,uid);
                        rvRecipes.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        etxtRecipeTitle.setText("");
                        etxtRecipeLink.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminManageRecipes.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }
}
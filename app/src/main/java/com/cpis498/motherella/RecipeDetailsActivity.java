package com.cpis498.motherella;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cpis498.motherella.models.Recipe;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {
    TextView txtIngredients,txtDirections,txtMealSize;
    TextView txtMainTitle;
    ImageView ivRecipeImge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        //initializeVariables
        txtIngredients=findViewById(R.id.txt_ingredients);
        txtDirections=findViewById(R.id.txt_directions);
        txtMealSize=findViewById(R.id.txt_meal_size);
        ivRecipeImge=findViewById(R.id.iv_recipe_image);
        txtMainTitle=findViewById(R.id.main_label);

        //get recipe object from intent
//        Intent intent=getIntent();
//        Recipe recipe= (Recipe) intent.getSerializableExtra("recipe");
//
//        //show recipe info in places
//        //set title
//        txtMainTitle.setText(recipe.getTitle());
//        //show ingredients
//        List<String> ingredients=recipe.getIngredients();
//        for(int i=0;i<ingredients.size();i++)
//            txtIngredients.append((i+1) +" - "+ingredients.get(i)+"\n");
//        //show directions
//        List<String> directios=recipe.getDirections();
//        for(int i=0;i<directios.size();i++)
//            txtDirections.append((i+1) +" - "+directios.get(i)+"\n");
//        //show meal size
//        txtMealSize.setText(recipe.getSize());
//
//        //load image
//        Glide.with(this)
//                .load(recipe.getImage_url())
//                .placeholder(getDrawable(R.drawable.ic_recipe_svgrepo_com))
//                .into(ivRecipeImge);

    }
}
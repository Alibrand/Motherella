package com.cpis498.motherella.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cpis498.motherella.R;
import com.cpis498.motherella.RecipeDetailsActivity;
import com.cpis498.motherella.models.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesScrollAdapter extends RecyclerView.Adapter<RecipeItem> {
    List<Recipe> recipes;
    Context mContext;
    String uid;
    FirebaseFirestore mFirestore;

    public RecipesScrollAdapter(List<Recipe> recipes, Context mContext,String uid) {
        this.recipes = recipes;
        this.mContext = mContext;
        this.uid=uid;
        mFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecipeItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_recipe,
                        parent,
                        false);
        return new RecipeItem(itemView) ;
    }

    private  void openUrl(String url){
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeItem holder, int position) {
        Recipe recipe=recipes.get(position);
        holder.txtTitle.setText(recipe.getTitle());
        holder.txtTrimester.setText(recipe.getTrimester());
        holder.ivRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(recipe.getLink());
            }
        });
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(recipe.getLink());
            }
        });

        // show delete button if
        //   the current user is the admin
        if(uid.equals("admin"))
        {
            holder.btnFav.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        if(recipe.isFavourite(uid))
        {
            holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
        else {
            holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        
        

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog=new ProgressDialog(mContext);
                dialog.setMessage("الرجاء الانتظار قليلا");
                dialog.setCancelable(false);
                dialog.show();
                if(recipe.isFavourite(uid))
                {
                    recipe.removeFavourite(uid);
                }
                else{
                    recipe.addFavourite(uid);
                }
                Map<String,Object> map=new HashMap<>();
                map.put("favourites",recipe.getFavourites());
                mFirestore.collection("recipes")
                        .document(recipe.getId())
                        .update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(recipe.isFavourite(uid))
                                {
                                    holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                                else {
                                    holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                }
                                dialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(!recipe.isFavourite(uid))
                        {
                            holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_24);
                        }
                        else {
                            holder.btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                        Toast.makeText(mContext, "حدث خطأ" ,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });


            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog dialog=new ProgressDialog(mContext);
                dialog.setMessage("الرجاء الانتظار قليلا");
                dialog.setCancelable(false);
                dialog.show();
                mFirestore.collection("recipes")
                        .document(recipe.getId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext, "تم حذف الوصفة بنجاح" ,Toast.LENGTH_LONG).show();
                        recipes.remove(recipe);
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), recipes.size());

                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "حدث خطأ" ,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}

class RecipeItem extends RecyclerView.ViewHolder{
    ImageView ivRecipeImage;
    TextView txtTitle,txtTrimester;
    ImageButton btnFav,btnDelete;
    

    public RecipeItem(@NonNull View itemView) {
        super(itemView);
        ivRecipeImage=itemView.findViewById(R.id.iv_recipe_image);
        txtTitle=itemView.findViewById(R.id.txt_recipe_title);
        btnFav=itemView.findViewById(R.id.btn_favourite);
        btnDelete=itemView.findViewById(R.id.btn_delete);
        txtTrimester=itemView.findViewById(R.id.txt_trimester);
    }
}

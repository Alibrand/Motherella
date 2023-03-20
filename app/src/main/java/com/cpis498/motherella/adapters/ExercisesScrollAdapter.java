package com.cpis498.motherella.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cpis498.motherella.R;
import com.cpis498.motherella.models.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExercisesScrollAdapter extends RecyclerView.Adapter<ExerciseItem> {
    List<Exercise> exercises;
    Context mContext;
    String uid;
    FirebaseFirestore mFirestore;

    public ExercisesScrollAdapter(List<Exercise> exercises, Context mContext,String uid) {
        this.exercises = exercises;
        this.mContext = mContext;
        this.uid=uid;
        mFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ExerciseItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_exercise,
                        parent,
                        false);
        return new ExerciseItem(itemView) ;
    }

    private  void openUrl(String url){
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseItem holder, int position) {
        Exercise exercise=exercises.get(position);
        holder.txtTitle.setText(exercise.getTitle());
        Glide.with(mContext)
                .load(exercise.getImage_url())
                .placeholder(mContext.getDrawable(R.drawable.ic_video_svgrepo_com))
                .into(holder.ivVideoImage);

        holder.ivVideoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(exercise.getVideo_url());
            }
        });
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(exercise.getVideo_url());
            }
        });

        // show delete button if
        //   the current user is the admin
        if(uid.equals("admin"))
        {
            holder.btnFav.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        if(exercise.isFavourite(uid))
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
                if(exercise.isFavourite(uid))
                {
                    exercise.removeFavourite(uid);
                }
                else{
                    exercise.addFavourite(uid);
                }
                Map<String,Object> map=new HashMap<>();
                map.put("favourites",exercise.getFavourites());
                mFirestore.collection("exercises")
                        .document(exercise.getId())
                        .update(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(exercise.isFavourite(uid))
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
                        if(!exercise.isFavourite(uid))
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
                mFirestore.collection("exercises")
                        .document(exercise.getId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext, "تم حذف الفيديو بنجاح" ,Toast.LENGTH_LONG).show();
                        exercises.remove(exercise);
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), exercises.size());

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
        return exercises.size();
    }
}

class ExerciseItem extends RecyclerView.ViewHolder{
    ImageView ivVideoImage;
    TextView txtTitle;
    ImageButton btnFav,btnDelete;

    public ExerciseItem(@NonNull View itemView) {
        super(itemView);
        ivVideoImage=itemView.findViewById(R.id.iv_video_image);
        txtTitle=itemView.findViewById(R.id.txt_video_title);
        btnFav=itemView.findViewById(R.id.btn_favourite);
        btnDelete=itemView.findViewById(R.id.btn_delete);
    }
}

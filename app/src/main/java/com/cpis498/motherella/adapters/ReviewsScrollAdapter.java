package com.cpis498.motherella.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpis498.motherella.R;
import com.cpis498.motherella.models.Review;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReviewsScrollAdapter extends RecyclerView.Adapter<ReviewItem> {
    List<Review> reviews;
    Context mContext;
    FirebaseHelper firebaseHelper;
    FirebaseFirestore mFirestore;
    String uid;
    ProgressDialog dialog;

    public ReviewsScrollAdapter(List<Review> reviews, Context mContext,String uid) {
        this.reviews = reviews;
        this.mContext = mContext;
        this.firebaseHelper=new FirebaseHelper(mContext);
        this.uid=uid;
        this.mFirestore=FirebaseFirestore.getInstance();
        this.dialog=new ProgressDialog(mContext);
        this.dialog.setMessage("يتم الان حذف التعليق");
        this.dialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ReviewItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_review,
                        parent,
                        false);
        return new ReviewItem(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewItem holder, int position) {
        Review review=reviews.get(position);

        // show delete button if the current user  own the review
        // or the current user is the admin
        if(review.getUid().equals(uid) || uid.equals("admin"))
        {
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        holder.rbRating.setRating(review.getRating());
        holder.txtComents.setText(review.getComments());
        holder.txtDoctorSpecial.setText(review.getDoctor_specialitiy());
        holder.txtDoctorName.setText(review.getDoctor_name());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             dialog.show();
            mFirestore.collection("reviews")
                    .document(review.getId())
            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(mContext, "تم حذف التعليق بنجاح" ,Toast.LENGTH_LONG).show();
                    reviews.remove(review);
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), reviews.size());

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
        return reviews.size();
    }

}

class ReviewItem extends RecyclerView.ViewHolder{
    RatingBar rbRating;
    TextView txtDoctorName,txtDoctorSpecial,txtComents;
    ImageButton btnDelete;

    public ReviewItem(@NonNull View itemView) {
        super(itemView);
        rbRating=itemView.findViewById(R.id.rbar_rating);
        txtComents=itemView.findViewById(R.id.txt_review_comment);
        txtDoctorName=itemView.findViewById(R.id.txt_doctor_name);
        txtDoctorSpecial=itemView.findViewById(R.id.txt_doctor_sp);
        btnDelete=itemView.findViewById(R.id.btn_delete);
    }
}

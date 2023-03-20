package com.cpis498.motherella.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.cpis498.motherella.R;
import com.cpis498.motherella.models.Article;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ArticlesScrollAdapter extends RecyclerView.Adapter<ArticleItem> {
    List<Article> articles;
    Context mContext;
    FirebaseHelper firebaseHelper;
    FirebaseFirestore mFirestore;
    String uid;
    ProgressDialog dialog;

    public ArticlesScrollAdapter(List<Article> articles, Context mContext,String uid) {
        this.articles = articles;
        this.mContext = mContext;
        this.firebaseHelper=new FirebaseHelper(mContext);
        this.uid=uid;
        this.mFirestore=FirebaseFirestore.getInstance();
        this.dialog=new ProgressDialog(mContext);
        this.dialog.setMessage("يتم الان حذف المقال");
        this.dialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ArticleItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_article,
                        parent,
                        false);
        return new ArticleItem(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleItem holder, int position) {
        Article article=articles.get(position);

        // show delete button if
        //   the current user is the admin
        if(uid.equals("admin"))
        {
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        holder.btnArticle.setText(article.getTitle());

        holder.btnArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(article.getLink());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             dialog.show();
            mFirestore.collection("articles")
                    .document(article.getId())
            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(mContext, "تم حذف المقال بنجاح" ,Toast.LENGTH_LONG).show();
                    articles.remove(article);
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), articles.size());

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
        return articles.size();
    }
    private  void openUrl(String url){
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext. startActivity(intent);
    }
}

class ArticleItem extends RecyclerView.ViewHolder{
    AppCompatButton btnArticle;
    ImageButton btnDelete;

    public ArticleItem(@NonNull View itemView) {
        super(itemView);
        btnArticle=itemView.findViewById(R.id.btn_article);
        btnDelete=itemView.findViewById(R.id.btn_delete);
    }
}

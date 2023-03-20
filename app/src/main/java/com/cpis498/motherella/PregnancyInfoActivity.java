package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ArticlesScrollAdapter;
import com.cpis498.motherella.models.Article;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PregnancyInfoActivity extends AppCompatActivity {

    RecyclerView rvArticles;
    List<Article> articleList;
    FirebaseFirestore mFireStore;


    ProgressBar progressBar;
    String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnancy_info);
        //initialize variables
        rvArticles=findViewById(R.id.rv_articles);
        progressBar=findViewById(R.id.progress_bar);
        mFireStore= FirebaseFirestore.getInstance();
        ImageButton btnHome=findViewById(R.id.btn_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get articles from database
        getArticles();


    }
    private void getArticles() {
        progressBar.setVisibility(View.VISIBLE);
        articleList=new ArrayList<Article>();
        rvArticles.setAdapter(null);
        mFireStore.collection("articles")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> map=doc.getData();
                            // add doc id
                            map.put("id",doc.getId());
                            Article article=new Article(map);
                            articleList.add(article);
                        }
                        ArticlesScrollAdapter adapter=new ArticlesScrollAdapter(articleList,PregnancyInfoActivity.this,uid);
                        rvArticles.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PregnancyInfoActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }


}
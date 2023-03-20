package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cpis498.motherella.adapters.ArticlesScrollAdapter;
import com.cpis498.motherella.models.Article;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminManageArticlesActivity extends AppCompatActivity {
    RecyclerView rvArticles;
    List<Article> articleList;
    AppCompatButton btnSendArticle;
    EditText etxtArticleTitle, etxtArticleLink;

    FirebaseFirestore mFireStore;
    FirebaseHelper firebaseHelper;
    ProgressBar progressBar,progressBarSending;
    String uid="admin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_articles);
        //initialize variables
        rvArticles=findViewById(R.id.rv_articles);
        btnSendArticle=findViewById(R.id.btn_send_article);
        etxtArticleTitle =findViewById(R.id.etxt_article_title);
        etxtArticleLink =findViewById(R.id.etxt_article_link);

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
        btnSendArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title= etxtArticleTitle.getText().toString();
                String link= etxtArticleLink.getText().toString();


                //check if empty
                if(title.trim().isEmpty()||link.trim().isEmpty() )
                {
                    Toast.makeText(AdminManageArticlesActivity.this,"يوجد حقول فارغة",Toast.LENGTH_LONG).show();
                    return;
                }
                Article newArticle=new Article();
                newArticle.setLink(link);
                newArticle.setTitle(title);

                //show loading
                progressBarSending.setVisibility(View.VISIBLE);

                //call firestore to add new article
                mFireStore.collection("articles")
                        .add(newArticle).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBarSending.setVisibility(View.INVISIBLE);
                        Toast.makeText(AdminManageArticlesActivity.this,"تمت إضافة المقال بنجاح",Toast.LENGTH_LONG).show();
                        getArticles();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminManageArticlesActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

                    }
                });




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
                        ArticlesScrollAdapter adapter=new ArticlesScrollAdapter(articleList,AdminManageArticlesActivity.this,uid);
                        rvArticles.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        etxtArticleTitle.setText("");
                        etxtArticleLink.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminManageArticlesActivity.this,"حدث خطأ",Toast.LENGTH_LONG).show();

            }
        });
    }
}
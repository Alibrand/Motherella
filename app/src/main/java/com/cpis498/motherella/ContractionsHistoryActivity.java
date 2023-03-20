package com.cpis498.motherella;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cpis498.motherella.adapters.LabourRecordScrollAdapter;
import com.cpis498.motherella.models.LabourContractions;
import com.cpis498.motherella.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContractionsHistoryActivity extends AppCompatActivity {
    RecyclerView rvRecords;
    FirebaseHelper mFirehelper;
    FirebaseFirestore mFireStore;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractions_history);
        //initialize variables
        rvRecords=findViewById(R.id.rv_labour_history);
        mFirehelper=new FirebaseHelper(this);
        mFireStore=FirebaseFirestore.getInstance();
        //get contractions history
        getLabourHistory();
    }

    private void getLabourHistory() {
        //initialize list
        List<LabourContractions> contractions=new ArrayList<LabourContractions>();
        //show waiting
        dialog=new ProgressDialog(this);
        dialog.setMessage("الرجاء الانتظار قليلا");
        dialog.setCancelable(false);
        dialog.show();
        //get current user uid from firehelper
        String uid=mFirehelper.getLoggedUserId();
        //get records from firestore
        mFireStore.collection("labour_history")
                .whereEqualTo("uid",uid)
                .orderBy("labor_date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            LabourContractions labourContractions=new LabourContractions(doc.getData());
                            contractions.add(labourContractions);
                        }
                        LabourRecordScrollAdapter adapter=new LabourRecordScrollAdapter(contractions);
                        rvRecords.setAdapter(adapter);
                        dialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContractionsHistoryActivity.this,"حدث حطأ أثناء حفظ النتائج",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }
}
package com.cpis498.motherella.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpis498.motherella.R;
import com.cpis498.motherella.models.LabourContractions;

import java.text.SimpleDateFormat;
import java.util.List;

public class LabourRecordScrollAdapter extends RecyclerView.Adapter<LabourRecordItem> {
    List<LabourContractions> contractionsList;

    public LabourRecordScrollAdapter(List<LabourContractions> contractionsList) {
        this.contractionsList = contractionsList;
    }

    @NonNull
    @Override
    public LabourRecordItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView=
                LayoutInflater
                        .from(parent.getContext())
                .inflate(R.layout.list_item_labour_record,
                        parent,
                        false
                        );
        return  new LabourRecordItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LabourRecordItem holder, int position) {
            LabourContractions contractions=contractionsList.get(position);
        //format date to dispaly
        SimpleDateFormat formatDate =new SimpleDateFormat("YYYY-MM-dd");
        String formattedDate=formatDate.format(contractions.getLabor_date());
            holder.txtDate.setText("التاريخ:"+formattedDate);
            holder.txtContractions.setText(contractions.toString());
            String type=contractions.isActive()?"صادق":"كاذب";
            holder.txtType.setText("النتيجة:"+type);
    }

    @Override
    public int getItemCount() {
        return contractionsList.size();
    }
}
class LabourRecordItem extends RecyclerView.ViewHolder
{
    TextView txtDate,txtContractions,txtType;

    public LabourRecordItem(@NonNull View itemView) {
        super(itemView);
        txtDate=itemView.findViewById(R.id.txt_labour_date);
        txtContractions=itemView.findViewById(R.id.txt_labour_contractions);
        txtType=itemView.findViewById(R.id.txt_labour_type);
    }
}

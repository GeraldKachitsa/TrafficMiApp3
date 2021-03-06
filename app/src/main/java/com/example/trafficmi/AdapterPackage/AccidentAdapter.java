package com.example.trafficmi.AdapterPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trafficmi.Model.AccidentSceneModel;
import com.example.trafficmi.R;
import com.example.trafficmi.Views.ViewVehicleTheftDetails;

import java.util.ArrayList;

public class AccidentAdapter extends RecyclerView.Adapter<AccidentAdapter.ViewHolder> {
    ArrayList<AccidentSceneModel> accidentSceneModels;
    Context context;

    public AccidentAdapter(Context context, ArrayList<AccidentSceneModel> accidentSceneModels ) {
        this.accidentSceneModels = accidentSceneModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.display_theft_cases_layout_model,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccidentSceneModel accidentSceneModel = accidentSceneModels.get(position);

        holder.name.setText(accidentSceneModel.getName());
        holder.regnum.setText(accidentSceneModel.getRegNum());
        holder.color.setText(accidentSceneModel.getColor());
        holder.lat.setVisibility(View.GONE);
        holder.lon.setVisibility(View.GONE);

//        holder.otherDetails2.setText(accidentSceneModel.getOtherDetails().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewVehicleTheftDetails.class);
                intent.putExtra("carRegnum", accidentSceneModel.getRegNum().toString());
                intent.putExtra("nameOfCar", accidentSceneModel.getName().toString());
                intent.putExtra("colorOfCar", accidentSceneModel.getColor().toString());
                intent.putExtra("yearOfMake", accidentSceneModel.getYearOfMake().toString());
                intent.putExtra("otherDetails", accidentSceneModel.getOtherDetails().toString());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return accidentSceneModels.size();
    }

    public void filterList(ArrayList<AccidentSceneModel> models){
        accidentSceneModels = models;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView regnum,color,name, lat, lon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            regnum = itemView.findViewById(R.id.car_name);
            color = itemView.findViewById(R.id.location_name);
            name = itemView.findViewById(R.id.year_of_make);
            lat = itemView.findViewById(R.id.tv_lat);
            lon = itemView.findViewById(R.id.tv_long);
//            otherDetails2 = itemView.findViewById(R.id.otherDetails2);
        }
    }
}

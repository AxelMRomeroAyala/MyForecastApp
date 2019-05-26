package com.axelromero.myforecastapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ForecastModel> forecastModelList;

    public HistoryAdapter(){
        forecastModelList= new ArrayList<>();
    }

    public void setForecastModelList(List<ForecastModel> forecastModelList) {
        this.forecastModelList = forecastModelList;
    }

    public List<ForecastModel> getForecastModelList() {
        return forecastModelList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.textView.setText(forecastModelList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return forecastModelList.size();
    }

    public void addItem(ForecastModel model){

        if(forecastModelList.size() == 5){
            forecastModelList.remove(0);
        }
        forecastModelList.add(model);
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.city_name);
        }
    }
}

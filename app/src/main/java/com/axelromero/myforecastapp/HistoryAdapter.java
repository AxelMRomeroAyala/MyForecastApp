package com.axelromero.myforecastapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ForecastModel> forecastModelList;
    private Context context;
    private HistoryInteractor interactor;

    public HistoryAdapter(Context context, HistoryInteractor interactor) {
        this.context = context;
        this.interactor = interactor;
        forecastModelList = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull final HistoryViewHolder holder, int position) {
        holder.textView.setText(forecastModelList.get(position).name);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactor.onCitySelected(forecastModelList.get(holder.getAdapterPosition()));
            }
        });
        holder.deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemFromList(holder.getAdapterPosition());
            }
        });
    }

    public void removeItemFromList(int position) {
        forecastModelList.remove(position);
        notifyItemRemoved(position);
        Utils.saveHistoryList(context, getForecastModelList());
    }

    @Override
    public int getItemCount() {
        return forecastModelList.size();
    }

    public void addItem(ForecastModel model) {

        //First we search for a repeated object.
        boolean alreadyListed= false;
        int repeatedPosition= -1;
        for (int x = 0; x < forecastModelList.size(); x++) {
            if (forecastModelList.get(x).id == model.id) {
                repeatedPosition = x;
                alreadyListed= true;
                break;
            }
        }
        //if we found one, we move it to the last position in the array.
        if(alreadyListed){
            ForecastModel modelToMove= forecastModelList.get(repeatedPosition);
            forecastModelList.remove(repeatedPosition);
            forecastModelList.add(modelToMove);
        }
        //if it wasn't in the list, we just add it
        else {
            forecastModelList.add(model);
        }

        //Remove the first item is list size greater than 5
        if (forecastModelList.size() > 5) {
            forecastModelList.remove(0);
        }
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView deleteEntry;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.city_name);
            deleteEntry = itemView.findViewById(R.id.delete_city_name);
        }
    }
}

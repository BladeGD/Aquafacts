package com.example.aquariumapp.View.Aquarium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aquariumapp.Model.UserSettings.UserSettings;
import com.example.aquariumapp.R;

import java.util.List;

public class AquariumAdapter extends RecyclerView.Adapter<AquariumAdapter.ViewHolder> {

    private List<UserSettings> aquariumsList;
    private OnItemListener onItemListener;
    private Context context;

    public AquariumAdapter(List<UserSettings> aquariumsList, OnItemListener onItemListener, Context context){
        this.aquariumsList = aquariumsList;
        this.onItemListener = onItemListener;
        this.context = context;
    }

    @NonNull
    @Override
    //create and initialize ViewHolder and View
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aquarium_recycler_view, parent, false);
        return new ViewHolder(view, onItemListener);
    }

    @Override
    //bind data to ViewHolders textViews
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int posToDisplay = position+1;
        String display = "Aquarium " + posToDisplay + ": " +aquariumsList.get(position).getAquariumName();
        holder.getDisplayCourseTitle().setText(display);
    }

    @Override
    public int getItemCount() {
        return aquariumsList.size();
    }

    public List<UserSettings> getAquariumsList(){
        return aquariumsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView displayAquariumName;
        OnItemListener onItemListener;

        //ViewHolder to describe course recycler view items
        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            displayAquariumName = itemView.findViewById(R.id.aquariumNameTextView);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        public TextView getDisplayCourseTitle() {
            return displayAquariumName;
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    //interface to be used in MainActivity to send position of clicked item
    public interface OnItemListener{
        void onItemClick(int position);
    }
}

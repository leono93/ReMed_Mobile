package com.example.remed.utils;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remed.R;
import com.example.remed.models.ReminderModel;

import java.util.ArrayList;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private ArrayList<ReminderModel> reminders;
    private Context context;
    private ImageView tourist;
    private int index = 0;


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName, date, dose;

        MyViewHolder(View view) {
            super(view);
            reminderName = view.findViewById(R.id.reminder_name);
            date = view.findViewById(R.id.date);
            dose = view.findViewById(R.id.dose);
        }
    }

    public ReminderAdapter(ArrayList<ReminderModel> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ReminderModel reminderModel = reminders.get(position);
        holder.reminderName.setText(reminderModel.reminderName);
        holder.dose.setText(reminderModel.dose);
        holder.date.setText(reminderModel.date);

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

}

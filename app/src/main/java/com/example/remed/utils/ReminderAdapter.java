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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private ArrayList<ReminderModel> reminders;
    private FirebaseUser currentUser;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName, date, dose;
        ImageView delete;

        MyViewHolder(View view) {
            super(view);
            reminderName = view.findViewById(R.id.reminder_name);
            date = view.findViewById(R.id.date);
            dose = view.findViewById(R.id.dose);
            delete = view.findViewById(R.id.delete);
        }
    }

    public ReminderAdapter(ArrayList<ReminderModel> reminders, Context context, FirebaseUser currentUser) {
        this.reminders = reminders;
        this.currentUser = currentUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ReminderModel reminderModel = reminders.get(position);
        holder.reminderName.setText(reminderModel.reminderName);
        holder.dose.setText(reminderModel.dose);
        holder.date.setText(reminderModel.date);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                CloudFirestore cloudFirestore = new CloudFirestore(currentUser);
                assert currentUser != null;
                cloudFirestore.delete(reminderModel.id, currentUser);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

}

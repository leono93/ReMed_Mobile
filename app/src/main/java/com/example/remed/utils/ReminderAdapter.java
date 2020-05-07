package com.example.remed.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.example.remed.HomePage;
import com.example.remed.R;
import com.example.remed.models.ReminderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> implements RecurrencePickerDialogFragment.OnRecurrenceSetListener,
        TimePickerDialogFragment.TimePickerDialogHandler {

    private ArrayList<ReminderModel> reminders;
    private FirebaseUser currentUser;
    private Context context;
    private String date = "";

    @Override
    public void onRecurrenceSet(String rrule) {

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName, date;
        EditText dose;
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
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ReminderModel reminderModel = reminders.get(position);
        holder.reminderName.setText(reminderModel.reminderName);
        holder.dose.setText(reminderModel.dose);
        holder.dose.setImeOptions(EditorInfo.IME_ACTION_DONE);
        holder.dose.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();
                    CloudFirestore cloudFirestore = new CloudFirestore(currentUser);
                    assert currentUser != null;
                    holder.dose.setFocusable(false);
                    cloudFirestore.updateDose(reminderModel.id, currentUser, v.getText().toString());
                    Toast.makeText(context, "Reminder Updated!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        holder.date.setText(reminderModel.date.replace("Notification set for:", ""));
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog(holder, reminderModel);

            }
        });

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

    private void timeDialog(final MyViewHolder holder, final ReminderModel reminderModel) {

        TimePickerBuilder tpb = new TimePickerBuilder()
                .setFragmentManager(((HomePage) context).getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .addTimePickerDialogHandler(new TimePickerDialogFragment.TimePickerDialogHandler() {
                    @Override
                    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();
                        CloudFirestore cloudFirestore = new CloudFirestore(currentUser);
                        assert currentUser != null;
                        cloudFirestore.updateDate(reminderModel.id, currentUser, hourOfDay + ":" + "00");
                        Toast.makeText(context, "Reminder Updated!", Toast.LENGTH_SHORT).show();
                    }
                });
        tpb.show();
        Log.d("UpdatingPill", tpb.toString());
//        holder.date.setText(date);
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        if (minute == 0) {
            date = hourOfDay + ":" + "00";
            Log.d("Updating Pill", date);
        } else {
            date = hourOfDay + ":" + minute;
            Log.d("Updating Pill", date);
        }
    }



    @Override
    public int getItemCount() {
        return reminders.size();
    }

}

package com.example.remed;

import android.content.res.Resources;
import android.os.Bundle;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.example.remed.models.ReminderModel;
import com.example.remed.utils.CloudFirestore;
import com.example.remed.utils.ReminderAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomePage extends AppCompatActivity implements RecurrencePickerDialogFragment.OnRecurrenceSetListener,
        TimePickerDialogFragment.TimePickerDialogHandler {

    private static final String TAG = "HomePageActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    // Firebase
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ArrayList<ReminderModel> reminderList;
    private RecyclerView.Adapter reminderAdapter;
    private RecyclerView recyclerView;
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    String mRrule, day, time;
    TextView time_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setUpFirebaseAuthentication();

        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, HomePage.this);
        recyclerView = findViewById(R.id.list);
        setUpListView();
        loadReminders();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = "";
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomePage.this);
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.setTitle("Mahmoud");
                bottomSheetDialog.show();

                Button button = bottomSheetDialog.findViewById(R.id.add_pill_button);
                final EditText medicine_edit_text = bottomSheetDialog.findViewById(R.id.medicine_edit_text);
                final EditText dose_edit_text = bottomSheetDialog.findViewById(R.id.dose_edit_text);
                final TextView time_edit_text = bottomSheetDialog.findViewById(R.id.time_text_view);

                time_edit_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timeDialog(time_edit_text);
                        time_edit_text.setText(time);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (medicine_edit_text.getText().toString().equals("") && dose_edit_text.getText().toString().equals("") && time.equals("")) {
                            Toast.makeText(HomePage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, String> pillMap = new HashMap<>();
                            pillMap.put("medicine_name", medicine_edit_text.getText().toString());
                            pillMap.put("dose", dose_edit_text.getText().toString());
                            pillMap.put("time", time);
                            CloudFirestore cloudFirestore = new CloudFirestore(currentUser);
                            cloudFirestore.addPill(pillMap);
                            bottomSheetDialog.hide();
                            Toast.makeText(HomePage.this, "Reminder added!", Toast.LENGTH_SHORT).show();
                            loadReminders();
                        }

                    }
                });
            }
        });
    }

    private void loadReminders() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db.collection("users").document(currentUser.getUid()).collection("user_reminders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    callAdapter(document.get("medicine_name").toString(), document.get("dose").toString(), document.get("time").toString());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void callAdapter(String reminderName, String dose, String date) {
        ReminderModel reminder = new ReminderModel();
        reminder.reminderName = reminderName;
        reminder.dose = dose;
        reminder.date = date;
        reminderList.add(reminder);
        reminderAdapter.notifyDataSetChanged();
    }
    private void setUpListView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reminderAdapter);
    }

    private void dayDialog(){
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        Time time = new Time();
        time.setToNow();
        bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);
        bundle.putBoolean(RecurrencePickerDialogFragment.BUNDLE_HIDE_SWITCH_BUTTON, true);



        RecurrencePickerDialogFragment rpd = new RecurrencePickerDialogFragment();
        rpd.setArguments(bundle);
        rpd.setOnRecurrenceSetListener(HomePage.this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
    }

    private void setUpFirebaseAuthentication() {
        authentication = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d(TAG, "Success");
                } else {
                    Log.d(TAG, "signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        authentication.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            authentication.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onRecurrenceSet(String rrule) {

        mRrule = rrule;

        if (mRrule != null) {

            mEventRecurrence.parse(mRrule);

        }

        populateRepeats();
    }

    private void populateRepeats() {

        Resources r = getResources();

        String repeatString = "";

        boolean enabled;

        if (!TextUtils.isEmpty(mRrule)) {

            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);

        }


        day = repeatString;

    }

    private void timeDialog(TextView textView){

        TimePickerBuilder tpb = new TimePickerBuilder()
                .setFragmentManager(getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
        tpb.show();
        textView.setText(time);
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        if(minute == 0){
            time = hourOfDay + ":" + "00";
        }else {
            time = hourOfDay + ":" + minute;
        }
    }
}

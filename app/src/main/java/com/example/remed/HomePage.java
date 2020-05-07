package com.example.remed;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomePage extends AppCompatActivity implements RecurrencePickerDialogFragment.OnRecurrenceSetListener, TimePickerDialogFragment.TimePickerDialogHandler,
TimePickerDialog.OnTimeSetListener{

    private static final String TAG = "HomePageActivity";
    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";

    // Firebase
    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ArrayList<ReminderModel> reminderList;
    private RecyclerView.Adapter reminderAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ReminderModel> reminderList2;
    private RecyclerView.Adapter reminderAdapter2;
    private RecyclerView recyclerView2;
    private ArrayList<ReminderModel> reminderList3;
    private RecyclerView.Adapter reminderAdapter3;
    private RecyclerView recyclerView3;

    private EventRecurrence mEventRecurrence = new EventRecurrence();
    String mRrule, day, time;
    FloatingActionButton fab;
    TextView dateTextView;
    ImageButton settingsButton;
    ImageButton backButton;
    ImageButton rightButton;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    private String dateSet = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setUpFirebaseAuthentication();
        init();
        setUpListView();
        loadReminders();
        setUpWidgets();
    }

    private void init() {
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, HomePage.this, currentUser);
        recyclerView = findViewById(R.id.list);

        reminderList2 = new ArrayList<>();
        reminderAdapter2 = new ReminderAdapter(reminderList2, HomePage.this, currentUser);
        recyclerView2 = findViewById(R.id.list2);

        reminderList3 = new ArrayList<>();
        reminderAdapter3 = new ReminderAdapter(reminderList3, HomePage.this, currentUser);
        recyclerView3 = findViewById(R.id.list3);

        settingsButton = findViewById(R.id.settingButton);
        dateTextView = findViewById(R.id.dateTextView);

        dateTextView.setText(getDayOfWeek());

        fab = findViewById(R.id.fab);
        backButton = findViewById(R.id.backArrow);
        rightButton = findViewById(R.id.rightArrow);
    }

    private void setUpArrowButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateSet.length() == 9 && Integer.parseInt(dateSet.substring(8, 9)) == 1) {
                    calendar.roll(Calendar.MONTH, -1);
                    calendar.roll(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE) - 1);
                } else {
                    calendar.roll(Calendar.DATE, -1);
                }

                dateSet = dateFormat.format(calendar.getTime());
                dateTextView.setText(dateSet);
                loadReminders();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int max = calendar.getActualMaximum(Calendar.DATE);

                if (dateSet.length() == 10 && Integer.parseInt(dateSet.substring(8, 10)) == max) {
                    calendar.roll(Calendar.MONTH, 1);
                    calendar.roll(Calendar.DATE, -(max - 1));
                } else {
                    calendar.roll(Calendar.DATE, 1);
                }

                dateSet = dateFormat.format(calendar.getTime());
                dateTextView.setText(dateSet);
                loadReminders();
            }
        });
    }


    private void setUpWidgets() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomePage.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                        bottomSheetDialog.setContentView(dialogView);
                        bottomSheetDialog.setTitle("Mahmoud");
                        bottomSheetDialog.show();
                        setUpDialog();
                    }
                });
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, SettingActivity.class);
                startActivity(intent);
                /*get notification permission*/
                NotificationManager notificationManager =
                        (NotificationManager) HomePage.this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent1 = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent1);
                }
            }
        });

        setUpArrowButtons();
    }

    private void setUpDialog() {
        time = "";
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomePage.this);
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();

        Button addPillButton = bottomSheetDialog.findViewById(R.id.add_pill_button);
        final EditText medicine_edit_text = bottomSheetDialog.findViewById(R.id.medicine_edit_text);
        final EditText dose_edit_text = bottomSheetDialog.findViewById(R.id.dose_edit_text);
        final TextView time_edit_text = bottomSheetDialog.findViewById(R.id.time_text_view);
        final TextView day_text_view = bottomSheetDialog.findViewById(R.id.day_text_view);

        time_edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });


        assert day_text_view != null;
        day_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayDialog();
            }
        });

        assert addPillButton != null;
        addPillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (medicine_edit_text.getText().toString().equals("") && dose_edit_text.getText().toString().equals("") && time.equals("")) {
                    Toast.makeText(HomePage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, String> pillMap = new HashMap<>();
                    pillMap.put("medicine_name", medicine_edit_text.getText().toString());
                    pillMap.put("dose", dose_edit_text.getText().toString());
                    pillMap.put("time", time);
                    pillMap.put("day", day);
                    CloudFirestore cloudFirestore = new CloudFirestore(currentUser);
                    cloudFirestore.addPill(pillMap);
                    bottomSheetDialog.hide();
                    Toast.makeText(HomePage.this, "Reminder added!", Toast.LENGTH_SHORT).show();
                    loadReminders();
                    bottomSheetDialog.hide();
                }

            }
        });


    }

    private String getDayOfWeek() {

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE MMM d");
        dateSet = dateFormat.format(calendar.getTime());
        return dateSet;
    }

    private void loadReminders() {
        Log.d("momom", "loading");

        reminderList.clear();
        reminderList2.clear();
        reminderList3.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db.collection("users").document(currentUser.getUid()).collection("user_reminders")
                .orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("day").toString().contains(dateSet.substring(0, 3)))
                                    callAdapter(document.get("medicine_name").toString(), document.get("dose").toString(), document.get("time").toString(), document.getId());
                            }

                            reminderAdapter.notifyDataSetChanged();
                            reminderAdapter2.notifyDataSetChanged();
                            reminderAdapter3.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void callAdapter(String reminderName, String dose, String date, String id) {
        ReminderModel reminder = new ReminderModel();
        reminder.id = id;
        reminder.reminderName = reminderName;
        reminder.dose = dose;
        reminder.date = date;

        date = date.replace("Notification set for: ", "");
        int h;
        if (date.substring(1, 2).equals(":")) {
            h = Integer.parseInt(date.substring(0, 1));
        } else {
            h = Integer.parseInt(date.substring(0, 2));
        }

        if (h < 12 && h > 6) {
            reminderList.add(reminder);
            reminderAdapter.notifyDataSetChanged();
        } else if (h >= 12 && h <= 19) {
            reminderList2.add(reminder);
            reminderAdapter2.notifyDataSetChanged();
        } else {
            reminderList3.add(reminder);
            reminderAdapter3.notifyDataSetChanged();
        }

    }

    private void setUpListView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reminderAdapter);

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(reminderAdapter2);

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView3.setLayoutManager(mLayoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(reminderAdapter3);
    }

    private void dayDialog() {
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
        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
        }
        day = repeatString;
    }

    //takes variables hourOfDay and minute which the alert will begin at
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Hour: " + hourOfDay + " Minute: " + minute);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);

    }

    //updates the original text to show an alert is set
    private void updateTimeText(Calendar c) {
        time = "Notification set for: ";
        time += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

    }

    //starts the alert
    private void startAlarm(Calendar c) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        //RTC_WAKEUP is used so the notification will still activate even if the device is in sleep / lock mode
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        if (minute == 0) {
            time = hourOfDay + ":" + "00";
        } else {
            time = hourOfDay + ":" + minute;
        }
    }


}

package com.example.remed.utils;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CloudFirestore {

    private FirebaseUser currentUser;

    public CloudFirestore(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }


    public void addPill(Map<String, String> pillMap) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .collection("user_reminders").add(pillMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore Adding Pill", "Successful");
                        } else {
                            Log.d("Firestore Adding Pill", "failure");
                        }
                    }
                });
    }

    public void delete(String reminderID, FirebaseUser currentUser) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .collection("user_reminders").document(reminderID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore Deleting Pill", "Successful");
                        } else {
                            Log.d("Firestore Deleting Pill", "failure");
                        }
                    }
                });
    }

    public void updateDate(String reminderID, FirebaseUser currentUser, String newTime) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .collection("user_reminders").document(reminderID).update("time", newTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Firestore Updating Pill", "Successful");
                } else {
                    Log.d("Firestore Updating Pill", "failure");
                }
            }
        });
    }

    public void updateDose(String reminderID, FirebaseUser currentUser, String newDose) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .collection("user_reminders").document(reminderID).update("dose", newDose).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Firestore Updating Dose", "Successful");
                } else {
                    Log.d("Firestore Updating Dose", "failure");
                }
            }
        });
    }

}

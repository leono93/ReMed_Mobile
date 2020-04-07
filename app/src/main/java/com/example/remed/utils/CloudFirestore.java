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

}

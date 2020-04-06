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

    private Map<String, String> pillMap;
    private FirebaseUser currentUser;

    public CloudFirestore(Map<String, String> pillMap, FirebaseUser currentUser) {
        this.pillMap = pillMap;
        this.currentUser = currentUser;
    }


    void addPill() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a new document with a generated ID
        db.collection("users")
                .document(currentUser.getUid())
                .collection("user_reminders").add(pillMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d("Firestore Adding Pill", "Successful");
                    }
                });
    }

}

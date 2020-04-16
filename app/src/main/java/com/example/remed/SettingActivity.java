package com.example.remed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private Context context;
    private Button logout_btn;
    private ImageButton back_btn;
    private TextView account;
    private TextView lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
        setUpWidgets();
    }

    private void init(){
        logout_btn = findViewById(R.id.logout_btn);
        back_btn = findViewById(R.id.backButton);
        account = findViewById(R.id.account_textView);
        lang = findViewById(R.id.language_textview);
        context = SettingActivity.this;
    }

    private void setUpWidgets(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser != null) {
            account.setText(mUser.getEmail());
        }


        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        String language = Locale.getDefault().getLanguage();

        if(language.equals("ko")) {
            lang.setText(R.string.korean);
        }
        else{
            lang.setText(R.string.english);
        }


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void logout() {
        setUpGooogleSignin();
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();
        Intent intent = new Intent(this.context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.context.startActivity(intent);
    }

    private void setUpGooogleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this.context, gso);
    }

    private void setAppLocale(String localeCode){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(localeCode));
        }
        else{
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        res.updateConfiguration(conf,dm);

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

package com.example.ennemoser.orderappv100;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private boolean loggedIn;
    private TextView tvLogout;
    private Button btnSave, btnPreisliste;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("login", 0);
        tvLogout = (TextView)findViewById(R.id.tvLogout);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnPreisliste = (Button)findViewById(R.id.btnPreisliste);
        loggedIn = sharedPreferences.getBoolean("LoggedIn", false);
        tvLogout.setText("Logout");
        tvLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LoggedIn",false);
                editor.apply();
                finish();
                startActivity(getIntent());

            }
        });

        btnPreisliste.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Preisliste.class));
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Home.class));
            }
        });

        if(loggedIn){
        }
        else {
            startActivity(new Intent(this,Login.class));
        }

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }
}

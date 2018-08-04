package com.example.ennemoser.orderappv100;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private TextView tvLogin;
    private Button btnRegister;
    private EditText etEmailRegister, etPasswordRegister1, etPasswordRegister2;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        etEmailRegister = (EditText)findViewById(R.id.etEmailRegister);
        etPasswordRegister1 = (EditText)findViewById(R.id.etPasswordRegister1);
        etPasswordRegister2 = (EditText)findViewById(R.id.etPasswordRegister2);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection())
                    registerUser();
                else {
                    new AlertDialog.Builder(Register.this)
                            .setTitle("No connection")
                            .setMessage("It seems you're not having any internet connection!")
                            .setNegativeButton("close", null).create().show();

                }
            }
        });


        tvLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }
    public  boolean isInternetConnection()
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }
    private void registerUser(){

        //Eingaben müssen noch besser überprüft werden


        String email = etEmailRegister.getText().toString().trim();
        String password = etPasswordRegister1.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            //email is empty
            //...
            return;
        }

        if (TextUtils.isEmpty(password)){
            //password is empty
            //...
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();



        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Successfull registered
                            Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            startActivity(new Intent(Register.this, Login.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }
}

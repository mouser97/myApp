package com.example.ennemoser.orderappv100;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView tvRegister;
    private EditText etEmail, etPassword;
    private Button btLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences("login",0);
        tvRegister = (TextView)findViewById(R.id.tvRegister);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword= (EditText)findViewById(R.id.etPassword);
        btLogin = (Button)findViewById(R.id.btLogin);
        progressDialog = new ProgressDialog(this);
        btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnection()){
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
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

                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                                //Shared username, logged in
                                FirebaseUser user = mAuth.getCurrentUser();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("LoggedIn",true);
                                editor.putString("id",user.getUid());
                                editor.apply();
                                progressDialog.cancel();
                                startActivity(new Intent(Login.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                    });
                }
                else {
                    new AlertDialog.Builder(Login.this)
                            .setTitle("No connection")
                            .setMessage("It seems you're not having any internet connection!")
                            .setNegativeButton("close", null).create().show();
                }




            }
        });

        tvRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
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


    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    */
}

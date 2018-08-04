package com.example.ennemoser.orderappv100;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {
    TextView tvHome;
    EditText name;
    Button btn;
    SharedPreferences sharedPreferences;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences("login", 0);
        String s = sharedPreferences.getString("id", "default");
        myRef = database.getReference(s);
        btn = (Button)findViewById(R.id.btnTest);
        name = (EditText)findViewById(R.id.etName);
        tvHome = (TextView)findViewById(R.id.tvHome);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = myRef.push().getKey();
                myRef.child(s).setValue(name.getText().toString());
            }
        });
    }
}

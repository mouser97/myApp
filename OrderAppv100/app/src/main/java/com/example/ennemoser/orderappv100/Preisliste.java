package com.example.ennemoser.orderappv100;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.printservice.PrintService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Preisliste extends AppCompatActivity {
    private ListView listView;
    private ArrayList<GetraenkeListe> getraenke = new ArrayList<>();
    private ListAdapterT adapter;
    Button btnNew;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRefnew;
    private boolean first;
    SharedPreferences sharedPreferences;
    private ArrayList<String> listid = new ArrayList();
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preisliste);
        sharedPreferences = getSharedPreferences("login", 0);
        user = sharedPreferences.getString("id", "default") + "preisliste";
        myRefnew = database.getReference(user);
        first = true;
        btnNew = (Button) findViewById(R.id.btnNew);
        listView = (ListView)findViewById(R.id.listview);

        if (isInternetConnection()){
            load();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("No connection")
                    .setMessage("It seems you're not having any internet connection!")
                    .setNegativeButton("close", null).create().show();

        }

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(Preisliste.this);
                    builder.setTitle("Produkt entfernen")
                            .setMessage("Willst du wirklich das Produkt aus der Liste entfernen")
                            .setPositiveButton(android.R.string.yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //geht nicht
                                    DatabaseReference rem = FirebaseDatabase.getInstance().getReference(user).child(listid.get(position + 1));
                                    rem.removeValue();
                                    listid.remove(position);

                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();

                    return true;
                }
            });


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Preisliste.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog, null);

                final EditText etGetraenk = (EditText) mView.findViewById(R.id.etG);
                final EditText etPreis = (EditText) mView.findViewById(R.id.etP);
                Button btnSpeichern = (Button) mView.findViewById(R.id.btnSave);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();


                btnSpeichern.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etGetraenk.getText().toString().isEmpty() || etPreis.getText().toString().isEmpty()) {
                            Toast.makeText(Preisliste.this, "Ungültige Eingabe", Toast.LENGTH_SHORT).show();
                        } else {
                            int count = 0;
                            for (int i = 0; i < getraenke.size(); i++) {
                                if (getraenke.get(i).getraenk.equals(etGetraenk.getText().toString())) {
                                    count++;
                                }
                            }
                            if (count == 0) {
                                //populateListView(etGetraenk.getText().toString(), etPreis.getText().toString());
                                getraenke.add(new GetraenkeListe(etGetraenk.getText().toString(), etPreis.getText().toString()));
                                dialog.dismiss();
                                save();
                                //savePrice();
                                Toast.makeText(Preisliste.this, "Gespeichert", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(Preisliste.this, "Bereits vorhanden", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });

            }
        });







        /*if (getraenke.size() > 0){

        }*/


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


    public void load() {



        myRefnew.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listid.add(s);
                String a = dataSnapshot.getValue(String.class);
                String getraenk = "", preis = "";
                Boolean get = true;
                for (int i = 0; i < a.length();i++){
                    if (a.charAt(i) == '|'){
                        if (!get){
                            break;
                        }
                        get = false;
                    }
                    else {
                        if (get){
                            getraenk += a.charAt(i);
                        }
                        else {
                            preis += a.charAt(i);
                        }
                    }
                }
                populateListView(getraenk,preis);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //listView.invalidateViews();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
        myRefnew.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String produkt = "", preis = "", speicher = "";
                boolean b = true;
                if (first){
                    for(DataSnapshot best : dataSnapshot.getChildren()){
                        speicher = best.getValue(String.class);
                        for (int i = 0; i < speicher.length(); i++){
                            if (b){
                                if (speicher.charAt(i) == '|'){
                                    b = false;
                                }
                                else {
                                    produkt += speicher.charAt(i);
                                }
                            }
                            else {
                                if (speicher.charAt(i) == '|'){
                                    b = true;
                                    populateListView(produkt, preis);
                                    produkt = "";
                                    preis = "";
                                }
                                else {
                                    preis += speicher.charAt(i);
                                }
                            }
                        }
                        first = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        */

    }


    public void remove(){

    }


    public void save() {

        int last = getraenke.size();
        String si = myRefnew.push().getKey();
        for (int i = 0; i < last; i++){
            myRefnew.child(si).setValue(getraenke.get(i).getraenk + "|" + getraenke.get(i).preis + "|");
        }

    }

    public void populateListView(String getraenk, String preis) {
        listView = (ListView) findViewById(R.id.listview);
        getraenke.add(new GetraenkeListe(getraenk, preis));
        adapter = new ListAdapterT(Preisliste.this, getraenke);
        listView.setAdapter(adapter);
    }
}
class ListAdapterT extends BaseAdapter {
    Activity context;
    ArrayList<GetraenkeListe> getraenke;
    private static LayoutInflater inflater = null;

    public ListAdapterT (Activity context, ArrayList<GetraenkeListe> getraenke){
        this.context = context;
        this.getraenke = getraenke;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return getraenke.size();
    }

    @Override
    public GetraenkeListe getItem(int position) {
        return getraenke.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        itemView = (itemView == null)? inflater.inflate(R.layout.list_itam, null): itemView;
        TextView tvGetrank = (TextView) itemView.findViewById(R.id.tvGetraenk);
        TextView tvPreis = (TextView)itemView.findViewById(R.id.tvPreis);
        GetraenkeListe gl = getraenke.get(position);
        tvGetrank.setText(gl.getraenk);
        tvPreis.setText("" + gl.preis + " €");
        return itemView;
    }
}
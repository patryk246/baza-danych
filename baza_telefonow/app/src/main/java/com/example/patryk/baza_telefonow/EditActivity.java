package com.example.patryk.baza_telefonow;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends Activity {
    private EditText marka;
    private EditText model;
    private EditText android;
    private EditText www;
    private Button zapisz;
    private Button anuluj;
    private Button wwwButton;
    private long mIdWiersza;
    boolean czyMarkaWypelniona=false;
    boolean czyModelWypelniony=false;
    boolean czyAndroidWypelniony=false;
    boolean czyWwwWypelnione=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        marka=(EditText)findViewById(R.id.Marka);
        model=(EditText)findViewById(R.id.Model);
        android=(EditText)findViewById(R.id.Android);
        www=(EditText)findViewById(R.id.WWW);
        zapisz=(Button)findViewById(R.id.zapisz);
        zapisz.setEnabled(false);
        anuluj=(Button)findViewById(R.id.anuluj);
        wwwButton=(Button)findViewById(R.id.wwwButton);
        wwwButton.setEnabled(false);

        marka.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!marka.getText().toString().equals("")){
                            czyMarkaWypelniona=true;
                            sprawdzPola();
                        }
                    }
                }
        );
        model.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!model.getText().toString().equals("")){
                            czyModelWypelniony=true;
                            sprawdzPola();
                        }
                    }
                }
        );
        android.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!android.getText().toString().equals("")){
                            czyAndroidWypelniony=true;
                            sprawdzPola();
                        }
                    }
                }
        );
        www.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!www.getText().toString().equals("")){
                            czyWwwWypelnione=true;
                            wwwButton.setEnabled(true);
                            sprawdzPola();
                        }
                    }
                }
        );

        mIdWiersza = -1;
        if(savedInstanceState != null){
            mIdWiersza = savedInstanceState.getLong(PomocnikBD.ID);
        }
        else{
            Bundle tobolek = getIntent().getExtras();
            if(tobolek != null){
                mIdWiersza = tobolek.getLong(PomocnikBD.ID);
            }
        }
        if(mIdWiersza != -1){
            wypelnij(); //wypełnienie pól jeśli nie jest tworzony nowy rekord
        }


        zapisz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zapisz();
                    }
                }
        );

        anuluj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        anuluj();
                    }
                }
        );

        wwwButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otworzWWW();
                    }
                }
        );


    }

    public void anuluj(){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void powrotDoMain(){
        Intent powrotDoMain=new Intent();
        setResult(RESULT_OK, powrotDoMain);
        finish();
    }


    public void zapisz(){   //zapisanie wprowadzonych wartości
        ContentValues wartosci = new ContentValues();
        wartosci.put(PomocnikBD.MARKA,marka.getText().toString());
        wartosci.put(PomocnikBD.MODEL,model.getText().toString());
        wartosci.put(PomocnikBD.ANDROID,android.getText().toString());
        wartosci.put(PomocnikBD.WWW,www.getText().toString());
        if(mIdWiersza==-1) {    //jeśli jest tworzony nowy rekord
            Uri uriNowego = getContentResolver().insert(MojProvider.URI_ZAWARTOSCI, wartosci);
            mIdWiersza = Integer.parseInt(uriNowego.getLastPathSegment());
        }
        else{
            Uri uriStworzonego=ContentUris.withAppendedId(MojProvider.URI_ZAWARTOSCI,mIdWiersza);
            getContentResolver().update(uriStworzonego,wartosci,null,null);
        }
        powrotDoMain();
    }

    public void sprawdzPola(){
        if(czyMarkaWypelniona & czyModelWypelniony & czyAndroidWypelniony & czyWwwWypelnione){
            zapisz.setEnabled(true);
        }
    }

    public void wypelnij(){ //wypełnienie pól przy edycji istniejącego rekordu
        String[] projekcja={PomocnikBD.MARKA,PomocnikBD.MODEL,PomocnikBD.ANDROID,PomocnikBD.WWW};
        Uri uri=ContentUris.withAppendedId(MojProvider.URI_ZAWARTOSCI,mIdWiersza);
        Cursor kursor=getContentResolver().query(uri,projekcja,null,null,null); //inicjalizacja kursora
        kursor.moveToFirst();   //ustawienie kursora
        marka.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.MARKA)));    //Wstawienie do pola wartości z bazy
        model.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.MODEL)));
        android.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.ANDROID)));
        www.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.WWW)));
        kursor.close();
    }

    public void otworzWWW(){
        String adres=www.getText().toString();
        if(!adres.startsWith("http://")){
            adres="http://"+adres;
        }
        Intent zamiarPrzegladarki = new Intent("android.intent.action.VIEW",Uri.parse(adres));
        startActivity(zamiarPrzegladarki);

    }
}

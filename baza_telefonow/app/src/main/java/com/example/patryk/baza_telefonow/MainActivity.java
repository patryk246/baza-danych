package com.example.patryk.baza_telefonow;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapterKursora;
    private ListView mLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLista=(ListView)findViewById(R.id.lista_wartosci);
        uruchomLoader();
        mLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);  //Ustawienie możliwości wyboru elementów listy
        mLista.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Intent zamiar = new Intent(MainActivity.this, EditActivity.class);
                        zamiar.putExtra(PomocnikBD.ID,id);
                        startActivityForResult(zamiar, 0);
                    }
                }
        );
        mLista.setMultiChoiceModeListener(wyborWieluElementowListy());  //Listener do listy sprawdzający zaznaczenie
    }

    public void dodajNowy(){
        Intent zamiar=new Intent(MainActivity.this,EditActivity.class);
        zamiar.putExtra(PomocnikBD.ID,(long) -1);
        startActivityForResult(zamiar, 0);
    }

    public void uruchomLoader(){    //Wypełnienie listy danymi z bazy
        getLoaderManager().initLoader(0,null, this);
        String[] mapujZ=new String[]{PomocnikBD.MARKA,PomocnikBD.MODEL};
        int[] mapujDo=new int[]{R.id.Marka,R.id.Model};
        mAdapterKursora=new SimpleCursorAdapter(this,R.layout.wiersz_listy,null,mapujZ,mapujDo,0);
        mLista.setAdapter(mAdapterKursora);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args){  //Implementacja Loadera
        String[] projekcja={PomocnikBD.ID, PomocnikBD.MARKA, PomocnikBD.MODEL};
        CursorLoader loaderKursora = new CursorLoader(this,MojProvider.URI_ZAWARTOSCI,projekcja,null,null,null);
        return loaderKursora;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dane) {
        mAdapterKursora.swapCursor(dane);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterKursora.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dane)
    {
        super.onActivityResult(requestCode, resultCode, dane);
        getLoaderManager().restartLoader(0, null, this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Utworzenie menu kontekstowego
        getMenuInflater().inflate(R.menu.pasek_kontekstowy_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.dodaj){
            dodajNowy();    //dodanie nowego rekordu do bazy
        }
        return super.onOptionsItemSelected(item);
    }


    private AbsListView.MultiChoiceModeListener wyborWieluElementowListy()  //Implementacja Listenera do zaznaczenia elementów
    {
        return new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

            /**
             * Dodaje menu
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.pasek_kontekstowy_listy, menu); //utworzenie paska do usuwania elementów
                return true;
            }

            /**
             * Metoda wykonuje się gdy zostanie kliknięty jakiś przycisk w menu
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                if(item.getItemId() == R.id.usun) //Instrukcja wykona się gdy zostanie wciśnięty przycisk "USUŃ"
                {
                    long[] zaznaczone = mLista.getCheckedItemIds();

                    for(int i = 0; i < zaznaczone.length; i++)
                    {
                        getContentResolver().delete(ContentUris.withAppendedId(MojProvider.URI_ZAWARTOSCI, zaznaczone[i]), null, null); //Usunięcie wszystkich zaznaczonych elementów
                    }
                    return true;
                }
                return false;
            }
        };
    }

}

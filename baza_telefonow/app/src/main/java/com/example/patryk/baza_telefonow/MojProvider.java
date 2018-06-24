package com.example.patryk.baza_telefonow;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MojProvider extends ContentProvider {
    private PomocnikBD mPomocnikBD; //identyfikator dostawcy
    private static final String IDENTYFIKATOR = "com.example.patryk.baza_telefonow.MojProvider";    //stała – aby nie trzeba było wpisywać tekstu samodzielnie
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://" + IDENTYFIKATOR + "/" + PomocnikBD.NAZWA_TABELI);   //stałe pozwalające zidentyfikować rodzaj rozpoznanego URI
    private static final int CALA_TABELA = 1;
    private static final int WYBRANY_WIERSZ = 2;
    private static final UriMatcher sDopasowanieUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sDopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI, CALA_TABELA);    //dodanie rozpoznawanych URI
        sDopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI + "/#", WYBRANY_WIERSZ);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza=mPomocnikBD.getWritableDatabase();  //otwieranie magazynu (bazy danych)
        int liczbaUsunietych = 0;
        switch (typUri) {
            case CALA_TABELA:
                liczbaUsunietych = baza.delete(PomocnikBD.NAZWA_TABELI, selection, selectionArgs);  //usuwanie rekordów
                break;
            case WYBRANY_WIERSZ:
                liczbaUsunietych =baza.delete(PomocnikBD.NAZWA_TABELI, dodajIdDoSelekcji(selection, uri), selectionArgs);   //usuwanie rekordu
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);  //powiadomienie o zmianie danych
        return liczbaUsunietych;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = mPomocnikBD.getWritableDatabase();    //otwieranie magazynu (bazy danych)
        long idDodanego = 0;
        switch (typUri) {   //sprawdzenie czy dodaje
            case CALA_TABELA:
                idDodanego=baza.insert(PomocnikBD.NAZWA_TABELI,null,values);   //!!!
                //zapisanie do magazynu – np. insert do bazy...
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        //powiadomienie o zmianie danych (->np. odświeżenie listy)
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PomocnikBD.NAZWA_TABELI + "/" + idDodanego);
    }

    @Override
    public boolean onCreate() {
        mPomocnikBD = new PomocnikBD(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza=mPomocnikBD.getWritableDatabase();  //otwieranie magazynu – np. bazy danych...
        Cursor kursor = null;
        switch (typUri) {
            case CALA_TABELA:
                kursor =baza.query(false, PomocnikBD.NAZWA_TABELI, projection, selection, selectionArgs, null, null, sortOrder, null, null);    //umieszczenie danych w kursorze...
                break;
            case WYBRANY_WIERSZ:
                kursor = baza.query(false, PomocnikBD.NAZWA_TABELI, projection, dodajIdDoSelekcji(selection, uri), selectionArgs, null, null, sortOrder, null, null);
//umieszczenie danych w kursorze...
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        //URI może być monitorowane pod kątem zmiany danych – tu jest
        //rejestrowane. Obserwator (którego trzeba zarejestrować
        //będzie powiadamiany o zmianie danych)
        kursor.setNotificationUri(getContext().getContentResolver(), uri);
        return kursor;
    }

    private String dodajIdDoSelekcji(String selekcja, Uri uri)
    {
        if(selekcja != null && !selekcja.equals(""))
        {
            selekcja = selekcja + " and " + PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }

        else
        {
            selekcja = PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }

        return selekcja;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int typUri = sDopasowanieUri.match(uri);
//otwieranie magazynu – np. bazy danych...
        SQLiteDatabase baza=mPomocnikBD.getWritableDatabase();
        int liczbaZaktualizowanych = 0;
        switch (typUri) {
            case CALA_TABELA:
                liczbaZaktualizowanych = baza.update(PomocnikBD.NAZWA_TABELI,values,selection,selectionArgs);
//aktualizacja...
                break;
            case WYBRANY_WIERSZ:
                liczbaZaktualizowanych = baza.update(PomocnikBD.NAZWA_TABELI,values,dodajIdDoSelekcji(selection,uri),selectionArgs);
//aktualizacja...
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
//powiadomienie o zmianie danych
        getContext().getContentResolver().notifyChange(uri, null);
        return liczbaZaktualizowanych;
    }

}

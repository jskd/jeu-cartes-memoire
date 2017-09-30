package com.example.joaquim.memocards;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Random;


public class BaseCards  extends SQLiteOpenHelper {

    private static final String DB_NAME = "cards.db";

    public static final String HISTOIRE = "Histoire";
    public static final String ENGLISH = "Anglais";

    /* attributs */
    public static final String ID = "id";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PRIORITY = "priority";

    private static int VERSION = 1;

    private static final String CREATE_HISTOIRE =
            "create table " + HISTOIRE + "( " +
                    ID + " INTEGER PRIMARY KEY, " +
                    QUESTION + " String, " +
                    ANSWER + " String, " +
                    PRIORITY + " int);";

    private static final String CREATE_ENGLISH =
            "create table " + ENGLISH + "( " +
                    ID + " INTEGER PRIMARY KEY, " +
                    QUESTION + " String, " +
                    ANSWER + " String, " +
                    PRIORITY + " int);";

    public BaseCards(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_HISTOIRE);
        db.execSQL(CREATE_ENGLISH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(newVersion > oldVersion){
            db.execSQL("drop table if exists " + HISTOIRE);
            db.execSQL("drop table if exists " + ENGLISH);
            onCreate(db);
        }
    }

    public void add_set(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + name);

        db.execSQL("create table " + name + "( " +
                ID + " INTEGER PRIMARY KEY, " +
                QUESTION + " String, " +
                ANSWER + " String, " +
                PRIORITY + " int);");
    }

    public void delete_set(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + name);
    }

    public boolean add_card(String setcard, Card c){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(QUESTION, c.getQuestion());
        row.put(ANSWER, c.getAnswer());
        row.put(PRIORITY, c.getPriority());

        if(db.insert(setcard, null, row) == -1)
            return false;
        else return true;
    }

    public ArrayList<String> getSetsNames(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }
        c.close();
        arrTblNames.remove(0);
        return arrTblNames;
    }

    public boolean setIsEmpty(String set){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + set + ";", null);
        boolean res = !(cursor.moveToNext());
        cursor.close();
        return res;
    }

    public boolean setIsFinish(String set){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + set + ";", null);
        int total_row = cursor.getCount();
        cursor = db.rawQuery("SELECT * FROM " + set + " WHERE priority = -1 ;", null);
        int total_trivial = cursor.getCount();

        return (total_row == total_trivial);
    }

    public boolean hasCardsInBox(String set, int box){
        SQLiteDatabase db = this.getReadableDatabase();
        int prio = 5 - box;
        Cursor cursor = db.rawQuery("SELECT * FROM " + set + " WHERE priority = "+ prio +";", null);
        boolean res = (cursor.moveToNext());
        cursor.close();

        return res;
    }

    public Card getRandomCardOf(String set, int box){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Card> liste_cards = new ArrayList<Card>();
        Card card = null;

        int prio = 5 - box;

        Cursor cursor = db.rawQuery("SELECT * FROM " + set + " WHERE priority = "+ prio +";", null);

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(this.ID));
            String question = cursor.getString(cursor.getColumnIndex(this.QUESTION));
            String answer = cursor.getString(cursor.getColumnIndex(this.ANSWER));
            int priority = cursor.getInt(cursor.getColumnIndex(this.PRIORITY));

            Card c = new Card(question, answer, priority);
            c.setId(id);

            liste_cards.add(c);
        }

        Random rand = new Random();


        if(liste_cards.size() > 0){
            int randomNum = rand.nextInt(( (liste_cards.size()-1) - 0) + 1) + 0;
            card = liste_cards.get(randomNum);
        }

        cursor.close();

        return card;
    }

    public Card getCardById(String set, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Card card = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + set + " WHERE id = "+ id +" ;", null);

        while (cursor.moveToNext()){
            int i = cursor.getInt(cursor.getColumnIndex(this.ID));
            String question = cursor.getString(cursor.getColumnIndex(this.QUESTION));
            String answer = cursor.getString(cursor.getColumnIndex(this.ANSWER));
            int priority = cursor.getInt(cursor.getColumnIndex(this.PRIORITY));

            card = new Card(question, answer, priority);
            card.setId(i);
        }

        cursor.close();
        return card;
    }

    public boolean setPriorityOf(String set, int id_card, int priority){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(PRIORITY, priority);

        if(db.update(set, row, "id = " + id_card, null) == -1)
            return false;
        else return true;

    }
}

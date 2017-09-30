package com.example.joaquim.memocards;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    private static final String ns = null;

    EditText edit_address;
    Button button_download;
    Button button_install;
    ProgressBar bar_download;
    ProgressBar bar_install;

    String file_name;
    DownloadManager dm;
    long id;
    BaseCards db;

    class InstallTask extends AsyncTask<String, Integer, Void> {

        String cardset_name;
        int progress;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress = 0;
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(v);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            bar_install.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(String... params) {
            try{

                InputStream in = new FileInputStream(params[0]);
                final List<Card> liste_cards = parse(in);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar_install.setMax(liste_cards.size());
                    }
                });


                db.add_set(cardset_name);

                for(Card c : liste_cards){
                    db.add_card(cardset_name, c);
                    progress++;
                    publishProgress(progress);

                    Log.d("CARD", "" + c.getQuestion() + " - " + c.getAnswer() + " - " + c.getPriority());
                }


            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public List parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readCardSet(parser);
            } finally {
                in.close();
            }
        }

        private List readCardSet(XmlPullParser parser) throws XmlPullParserException, IOException {
            List cards = new ArrayList();

            parser.require(XmlPullParser.START_TAG, ns, "cardset");
            cardset_name = parser.getAttributeValue(ns, "name");
            Log.d("CARDSET = ",  cardset_name);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                if (name.equals("card")) {
                    cards.add(readCard(parser));
                } else {
                    skip(parser);
                }
            }
            return cards;
        }


        private Card readCard(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, "card");
            String question = null;
            String answer = null;
            String priority = null;

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("question")) {
                    question = readQuestion(parser);
                } else if (name.equals("answer")) {
                    answer = readAnswer(parser);
                } else if (name.equals("priority")) {
                    priority = readPriority(parser);
                } else {
                    skip(parser);
                }
            }
            return new Card(question, answer, Integer.parseInt(priority));
        }

        private String readQuestion(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "question");
            String question = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "question");
            return question;
        }

        private String readAnswer(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "answer");
            String answer = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "answer");
            return answer;
        }

        private String readPriority(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "priority");
            String priority = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "priority");
            return priority;
        }

        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if(savedInstanceState != null) {

        }
        else {
            MenuFragment menu = new MenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();
        }

        edit_address = (EditText) findViewById(R.id.editText_download_address);
        button_download = (Button) findViewById(R.id.button_download);
        button_install = (Button) findViewById(R.id.button_download_install);
        button_install.setEnabled(false);

        bar_download = (ProgressBar) findViewById(R.id.progressBar_download);
        bar_install = (ProgressBar) findViewById(R.id.progressBar_download_install);

        dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        db = new BaseCards(this);
    }

    public void install(View v){
        new InstallTask().execute(file_name);
    }

    public void download(View v){
        button_download.setEnabled(false);
        button_install.setEnabled(false);
        bar_download.setProgress(0);

        String address_file = edit_address.getText().toString();
        Uri uri = Uri.parse(address_file);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        id = dm.enqueue(req);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;

                while(downloading){
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(id);

                    Cursor cursor = dm.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL){
                        downloading = false;
                        int fileNameid = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                        file_name = cursor.getString(fileNameid);
                        Log.d("FILE : ", file_name);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_download.setEnabled(true);
                                button_install.setEnabled(true);
                            }
                        });
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 1001) / bytes_total);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_download.setProgress(dl_progress);
                        }
                    });

                    cursor.close();
                }
            }
        }).start();


    }
}

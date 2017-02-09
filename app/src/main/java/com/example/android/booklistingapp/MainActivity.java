package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText search;
    String query;
    ArrayList<BooksInfo> bookInfo;
    BooksInfoCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = (EditText) findViewById(R.id.search);
        bookInfo = new ArrayList<BooksInfo>();
        adapter = new BooksInfoCustomAdapter(this, bookInfo);
        ListView bookList = (ListView) findViewById(R.id.bookList);
        bookList.setEmptyView(findViewById(R.id.emptyList));
        bookList.setAdapter(adapter);
    }

    public void searchBooks(View v) {
        bookInfo.clear();
        query = search.getText().toString();
        FetchBookData fetchBookData = new FetchBookData();
        fetchBookData.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("Data", bookInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            bookInfo = (ArrayList<BooksInfo>) savedInstanceState.getSerializable("Data");
            BooksInfoCustomAdapter adapter = new BooksInfoCustomAdapter(this, bookInfo);
            ListView bookList = (ListView) findViewById(R.id.bookList);
            bookList.setEmptyView(findViewById(R.id.emptyList));
            bookList.setAdapter(adapter);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public class FetchBookData extends AsyncTask<Void, Void, Void> {

        public void parseData(String jData) throws JSONException {
            JSONObject booksData = new JSONObject(jData);
            JSONArray booksArray = booksData.getJSONArray("items");
            String bookTitle[] = new String[booksArray.length()];
            String booksAuthors[] = new String[booksArray.length()];
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject books = booksArray.getJSONObject(i);
                JSONObject booksInfo = books.getJSONObject("volumeInfo");
                bookTitle[i] = booksInfo.getString("title");
                JSONArray authors = booksInfo.getJSONArray("authors");
                booksAuthors[i] = authors.getString(0);
                bookInfo.add(new BooksInfo(bookTitle[i], booksAuthors[i]));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            String booksData = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=" + query;

            try {
                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (input == null) {
                    booksData = null;
                }

                reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    booksData = null;
                }

                booksData = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
                try {
                    parseData(booksData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }
}

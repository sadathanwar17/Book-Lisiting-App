package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BooksInfoCustomAdapter extends ArrayAdapter<BooksInfo> {

    public BooksInfoCustomAdapter(Context context, ArrayList<BooksInfo> bookInfo) {
        super(context, 0, bookInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }
        BooksInfo currentBookInfo = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(currentBookInfo.title);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        author.setText(currentBookInfo.author);

        return convertView;
    }
}

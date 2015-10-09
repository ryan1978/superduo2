package com.example.jagr.alexandria.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jagr.alexandria.data.AlexandriaContract;
import com.example.jagr.superduo20.R;

/**
 * Created by ryan.gilreath on 10/8/2015.
 */
public class BookListAdapter extends CursorAdapter {

    private static final String LOG_TAG = BookListAdapter.class.getSimpleName();

    public static class BookListAdapterViewHolder {
        public final ImageView bookCover;
        public final TextView bookTitle;
        public final TextView bookSubTitle;

        public BookListAdapterViewHolder(View view) {
            bookCover       = (ImageView) view.findViewById(R.id.imageview_book_cover);
            bookTitle       = (TextView) view.findViewById(R.id.textview_book_title);
            bookSubTitle    = (TextView) view.findViewById(R.id.textview_book_subtitle);
        }
    }

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        BookListAdapterViewHolder viewHolder = (BookListAdapterViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        Glide.with(context)
                .load(imgUrl)
                .error(R.drawable.ic_launcher)
                .crossFade()
                .into(viewHolder.bookCover);
        //new DownloadImage(viewHolder.bookCover).execute(imgUrl);

        String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        viewHolder.bookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        viewHolder.bookSubTitle.setText(bookSubTitle);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

        BookListAdapterViewHolder viewHolder = new BookListAdapterViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}

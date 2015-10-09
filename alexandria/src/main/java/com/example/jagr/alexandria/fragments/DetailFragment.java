package com.example.jagr.alexandria.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jagr.alexandria.data.AlexandriaContract.BookEntry;
import com.example.jagr.alexandria.data.AlexandriaContract.AuthorEntry;
import com.example.jagr.alexandria.data.AlexandriaContract.CategoryEntry;
import com.example.jagr.alexandria.services.BookService;
import com.example.jagr.superduo20.R;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;

    public static final String EAN_KEY = "EAN";

    private String mEan;
    private String mTitleText;
    private TextView mBookTitle;
    private TextView mBookSubtitle;
    private TextView mBookDescription;
    private TextView mBookAuthors;
    private TextView mBookCategories;
    private ImageView mBookCover;

    private static final String[] DETAIL_COLUMNS = {
            BookEntry.TABLE_NAME + "." + BookEntry._ID,
            BookEntry.IMAGE_URL,
            BookEntry.TITLE,
            BookEntry.SUBTITLE,
            BookEntry.DESC,
            AuthorEntry.AUTHOR,
            CategoryEntry.CATEGORY
    };

    public static final int COL_BOOK_ID = 0;
    public static final int COL_BOOK_COVER = 1;
    public static final int COL_BOOK_TITLE = 2;
    public static final int COL_BOOK_SUBTITLE = 3;
    public static final int COL_BOOK_DESC = 4;
    public static final int COL_AUTHOR = 5;
    public static final int COL_CATEGORY = 6;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail, menu);
        finishCreatingMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                deleteBook();
                return true;
            default:
                return false;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mEan = arguments.getString(DetailFragment.EAN_KEY);
        }

        View rootView       = inflater.inflate(R.layout.fragment_detail, container, false);
        mBookTitle          = (TextView) rootView.findViewById(R.id.textview_book_title);
        mBookSubtitle       = (TextView) rootView.findViewById(R.id.textview_book_subtitle);
        mBookDescription    = (TextView) rootView.findViewById(R.id.textview_book_description);
        mBookAuthors        = (TextView) rootView.findViewById(R.id.textview_book_authors);
        mBookCategories     = (TextView) rootView.findViewById(R.id.textview_book_categories);
        mBookCover          = (ImageView) rootView.findViewById(R.id.imageview_book_cover);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                BookEntry.buildFullBookUri(Long.parseLong(mEan)),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mTitleText = data.getString(COL_BOOK_TITLE);
            mBookTitle.setText(mTitleText);

            String bookSubTitle = data.getString(COL_BOOK_SUBTITLE);
            mBookSubtitle.setText(bookSubTitle);

            String desc = data.getString(COL_BOOK_DESC);
            mBookDescription.setText(desc);

            String authors = data.getString(COL_AUTHOR);
            String[] authorsArr = authors.split(",");
            mBookAuthors.setLines(authorsArr.length);
            mBookAuthors.setText(authors.replace(",", "\n"));

            String categories = data.getString(COL_CATEGORY);
            mBookCategories.setText(categories);

            String imgUrl = data.getString(COL_BOOK_COVER);
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                Glide.with(getActivity())
                        .load(imgUrl)
                        .error(R.drawable.ic_launcher)
                        .crossFade()
                        .into(mBookCover);
            }
        }

        // Clear the toolbar to display only the share menu action item
        Toolbar toolbarView = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (null != toolbarView) {
            Menu menu = toolbarView.getMenu();
            if (null != menu) {
                menu.clear();
            }
            toolbarView.inflateMenu(R.menu.fragment_detail);
            finishCreatingMenu(menu);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareBookIntent());
    }

    private Intent createShareBookIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + mTitleText);
        return shareIntent;
    }

    private void deleteBook() {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, mEan);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}

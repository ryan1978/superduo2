package com.example.jagr.alexandria.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jagr.alexandria.adapters.BookListAdapter;
import com.example.jagr.alexandria.data.AlexandriaContract;
import com.example.jagr.superduo20.R;

/**
 * Created by ryan.gilreath on 10/8/2015.
 */
public class BookListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BookListFragment.class.getSimpleName();
    private static final int BOOKLIST_LOADER = 0;

    private SearchView mSearchView;
    private BookListAdapter mBookListAdapter;
    private ListView mBookList;
    private TextView mEmptyView;
    private int mPosition = ListView.INVALID_POSITION;

    public BookListFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_booklist, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_NONE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restartLoader();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booklist, container, false);

        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        mBookListAdapter = new BookListAdapter(getActivity(), cursor, 0);

        mBookList = (ListView) rootView.findViewById(R.id.listview_books);
        mBookList.setAdapter(mBookListAdapter);
        mBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mBookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onBookSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });

        mEmptyView = (TextView) rootView.findViewById(R.id.listview_books_empty);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOOKLIST_LOADER, null, this);
        getActivity().setTitle(R.string.title_bookslist);
        super.onActivityCreated(savedInstanceState);
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(BOOKLIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " +
                AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = mSearchView != null ? mSearchView.getQuery().toString() : null;

        if (searchString != null && searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString,searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookListAdapter.swapCursor(data);

        Toast.makeText(getActivity(), "Implement Empty View Here!!!", Toast.LENGTH_LONG).show();

        if (mBookListAdapter.getCount() == 0) {
            mPosition = ListView.INVALID_POSITION;
        }

        if (mPosition != ListView.INVALID_POSITION) {
            mBookList.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookListAdapter.swapCursor(null);
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of book
     * selections.
     */
    public interface Callback {
        public void onBookSelected(String ean);
    }
}

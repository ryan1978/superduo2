package com.example.jagr.alexandria.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jagr.alexandria.activities.BarcodeScannerActivity;
import com.example.jagr.alexandria.data.AlexandriaContract;
import com.example.jagr.alexandria.helpers.Utility;
import com.example.jagr.alexandria.services.BookService;
import com.example.jagr.superduo20.R;

/**
 * Created by jagr on 10/9/2015.
 */
public class AddBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = AddBooksFragment.class.getSimpleName();

    private static final int LOADER_ID  = 1;
    private final String EAN_CONTENT    = "eanContent";

    private TextView    mErrorMessage;
    private EditText    mEanText;
    private Button      mScanButton;
    private View        mDetailLayout;
    private TextView    mBookTitle;
    private TextView    mBookSubtitle;
    private TextView    mBookDescription;
    private TextView    mBookAuthors;
    private TextView    mBookCategories;
    private ImageView   mBookCover;
    private View        mButtonsLayout;
    private View        mDeleteButton;
    private View        mSaveButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.title_addbooks);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_books, container, false);

        mErrorMessage       = (TextView) rootView.findViewById(R.id.error_message);
        mScanButton         = (Button) rootView.findViewById(R.id.scan_button);
        mEanText            = (EditText) rootView.findViewById(R.id.ean);
        mDetailLayout       = rootView.findViewById(R.id.book_details);
        mBookTitle          = (TextView) rootView.findViewById(R.id.textview_book_title);
        mBookSubtitle       = (TextView) rootView.findViewById(R.id.textview_book_subtitle);
        mBookDescription    = (TextView) rootView.findViewById(R.id.textview_book_description);
        mBookAuthors        = (TextView) rootView.findViewById(R.id.textview_book_authors);
        mBookCategories     = (TextView) rootView.findViewById(R.id.textview_book_categories);
        mBookCover          = (ImageView) rootView.findViewById(R.id.imageview_book_cover);
        mButtonsLayout      = rootView.findViewById(R.id.buttoncontainer);
        mDeleteButton       = rootView.findViewById(R.id.button_delete);
        mSaveButton         = rootView.findViewById(R.id.button_save);

        mEanText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();

                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }

                if (ean.length() < 13) {
                    clearFields();
                    return;
                }

                if (Utility.isNetworkAvailable(getActivity())) {
                    //Once we have an ISBN, start a book intent
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.putExtra(BookService.EAN, ean);
                    bookIntent.setAction(BookService.FETCH_BOOK);
                    getActivity().startService(bookIntent);
                    restartLoader();
                } else {
                    mErrorMessage.setVisibility(View.VISIBLE);
                    mDetailLayout.setVisibility(View.GONE);
                    mButtonsLayout.setVisibility(View.GONE);
                }
            }
        });

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BarcodeScannerActivity.class);
                startActivityForResult(i, BarcodeScannerActivity.RC_SCAN_BARCODE);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEanText.setText("");
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEanText.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                mEanText.setText("");
            }
        });

        if(savedInstanceState != null){
            mEanText.setText(savedInstanceState.getString(EAN_CONTENT));
            mEanText.setHint("");
        }

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mEanText.getText().length()==0){
            return null;
        }
        String eanStr= mEanText.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            mBookTitle.setText(bookTitle);

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if(Patterns.WEB_URL.matcher(imgUrl).matches()){
                Glide.with(getActivity())
                        .load(imgUrl)
                        .error(R.drawable.ic_launcher)
                        .crossFade()
                        .into(mBookCover);
                mBookCover.setVisibility(View.VISIBLE);
            }

            String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            mBookSubtitle.setText(bookSubTitle);

            String description = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            mBookDescription.setText(description);

            String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            mBookCategories.setText(categories);

            // TODO: FIX THIS! I've seen at least one book crash the app here when the authors returned were null
            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            if (authors != null) {
                String[] authorsArr = authors.split(",");
                mBookAuthors.setLines(authorsArr.length);
                mBookAuthors.setText(authors.replace(",", "\n"));
            }

            mDetailLayout.setVisibility(View.VISIBLE);
            mButtonsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BarcodeScannerActivity.RC_SCAN_BARCODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle res      = data.getExtras();
                    String barcode  = res.getString(BarcodeScannerActivity.EXTRA_BARCODE);
                    mEanText.setText(barcode);
                }
                break;
        }
    }

    private void clearFields(){
        mBookTitle.setText("");
        mBookSubtitle.setText("");
        mBookDescription.setText("");
        mBookAuthors.setText("");
        mBookCategories.setText("");
        mBookCover.setVisibility(View.INVISIBLE);
        mDetailLayout.setVisibility(View.INVISIBLE);
        mButtonsLayout.setVisibility(View.INVISIBLE);
    }
}

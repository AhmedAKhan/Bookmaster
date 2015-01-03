package com.example.ahmed.applicationonethirdtry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    private static final String IMAGE_URL_BASE = "http://covers.openLibrary.org/b/id/";
    ShareActionProvider mShareActionProvider;
    String mImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView mImageView = (ImageView) findViewById(R.id.img_cover);

        //do the image thing
        String coverID = this.getIntent().getExtras().getString("coverID");
        if(coverID.length() > 0){
            mImageURL = IMAGE_URL_BASE + coverID + "-L.jpg";
            Picasso.with(this).load(mImageURL).placeholder(R.drawable.img_books_loading).into(mImageView);
        }
    }//end function

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if(shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
            //mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider(shareItem);
        }
        setShareIntent();
        return true;
    }

    private void setShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mImageURL);

        mShareActionProvider.setShareIntent(shareIntent);
    }//end function
}//end class
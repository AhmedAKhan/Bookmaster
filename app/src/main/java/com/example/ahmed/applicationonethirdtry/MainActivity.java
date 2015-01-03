package com.example.ahmed.applicationonethirdtry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;

    ListView mainListView;
    JSONAdapter mJSONAdapter;
    ArrayList mNameList = new ArrayList();

    ShareActionProvider mShareActionProvider;

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

    //used for saving variables
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //text field
        mainTextView = (TextView) findViewById(R.id.main_textview);

        //button
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        mainEditText = (EditText) findViewById(R.id.main_edittext);

        // sets up the stuff for adding the text to the arrayList
        mainListView = (ListView) findViewById(R.id.main_listview);

        // makes it so the function gets called whenever the mainListView item is pressed
        mainListView.setOnItemClickListener(this);

        displayWindow();

        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());
        mainListView.setAdapter(mJSONAdapter);

        //for the progess dialog box
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching for book");
        mDialog.setCancelable(false);
    }

    private void displayWindow(){
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name = mSharedPreferences.getString(PREF_NAME, "");
        if(name.length() > 0){
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        }else{
            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    // Grab the EditText's input
                    String inputName = input.getText().toString();

                    // Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    // Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();
        }
    }//end display window function

    @Override
    public void onClick(View v) {
        queryBooks(mainEditText.getText().toString());
        /*
        mainTextView.setText(mainEditText.getText().toString() + " is learning android development");

        //add the new item into the array list
        mNameList.add(mainEditText.getText().toString());
        mArrayAdapter.notifyDataSetChanged();

        setShareIntent(); */
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = (JSONObject) mJSONAdapter.getItem(position);
        String coverID = jsonObject.optString("cover_i", "");

        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("coverID", coverID);

        //if i want the detail view to do more then i
        // would need to send more information through the intent

        startActivity(detailIntent);
    }

    //
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        // Create an Intent to share your content
        setShareIntent();

        return true;
    }
    private void setShareIntent() {
        if (mShareActionProvider != null) {
            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());
            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void queryBooks(String searchString){
        String urlString = "";
        try{
            urlString = URLEncoder.encode(searchString, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //create a client to form networking
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("OMG ANDROID", "URL: " + QUERY_URL + urlString);
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        Log.d("OMG ANDROID", "IT IS A SUCCESS");
                        Log.d("OMG ANDROID", jsonObject.toString());
                        mJSONAdapter.updateData(jsonObject.optJSONArray("docs"));
                        mDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        Log.d("OMG ANDROID", statusCode + " " + throwable.getMessage());
                        mDialog.dismiss();

                    }
                }
        );
        mDialog.show();
    }//end query books

}//end class


package com.example.ahmed.applicationonethirdtry;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONAdapter extends BaseAdapter{

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater){
        this.mContext = context;
        this.mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() { return mJsonArray.length();  }

    @Override
    public Object getItem(int position) {  return mJsonArray.optJSONObject(position); }

    @Override
    public long getItemId(int position) {
        // your particular dataset uses String IDs
        // but you have to put something in this method
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.row_book, null);

            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.authorTextView = (TextView) convertView.findViewById(R.id.text_author);

            convertView.setTag(holder);
        }//end if convertView is null
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        //handle the image
        JSONObject jsonObject = (JSONObject) getItem(position);
        if(jsonObject.has("cover_i")){
            Log.d("OMG ANDROID", "inside the picture thing for " + position);
            String imageId = jsonObject.optString("cover_i");
            String imageURL = IMAGE_URL_BASE + imageId + "-S.jpg";
            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        }else{
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }

        //grab the book title and author
        String bookTitle = "";
        String authorName = "";
        if(jsonObject.has("title")) {  bookTitle = jsonObject.optString("title");   }
        if(jsonObject.has("author_name")) { authorName = jsonObject.optJSONArray("author_name").optString(0); }

        holder.titleTextView.setText(bookTitle);
        holder.authorTextView.setText(authorName);

        return convertView;
    }

    public void updateData(JSONArray jsonArray){
        this.mJsonArray = jsonArray;
        this.notifyDataSetChanged();
    }

    private static class ViewHolder{
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }
}
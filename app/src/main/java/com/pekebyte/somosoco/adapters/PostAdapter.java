package com.pekebyte.somosoco.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pekebyte.somosoco.helpers.Database;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.PostDetail;
import com.pekebyte.somosoco.R;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    private LayoutInflater mInflater;
    private Boolean isHome;

    public PostAdapter(@NonNull Context context, List<Post> posts, Boolean isHome) {
        super(context, R.layout.row_news, posts);
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isHome = isHome;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        final Post post = getItem(position);

        convertView = mInflater.inflate(R.layout.row_news, parent, false);

        holder = new ViewHolder();

        holder.title = (TextView) convertView.findViewById(R.id.NoticiaTitle);

        holder.title.setText(post.getTitle());

        holder.image = (ImageView) convertView.findViewById(R.id.postimage);

        holder.content = (CardView) convertView.findViewById(R.id.card_view);

        //Favorite Button
        holder.favoriteButton = (ImageView) convertView.findViewById(R.id.favorite);

        String postImage = extractUrls(post.getContent());

        if (postImage != null){
            Picasso.with(mContext).load(postImage).fit().into(holder.image);
        }
        else{
            holder.image.setVisibility(View.GONE);
            holder.favoriteButton.setVisibility(View.GONE);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(), PostDetail.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Gson gson = new Gson();
                i.putExtra("post", gson.toJson(post));
                mContext.startActivity(i);
            }
        };

        holder.content.setOnClickListener(listener);

        //Favorite Button
        final Database db = new Database();

        View.OnClickListener fl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.isFavorite(mContext, post)){
                    holder.favoriteButton.setImageResource(R.drawable.star2);
                    db.makeFavorite(mContext, post,0);
                }
                else{
                    holder.favoriteButton.setImageResource(R.drawable.star);
                    db.makeFavorite(mContext, post,1);
                }
                if (!isHome){
                    remove(post);
                    notifyDataSetChanged();
                }
            }
        };
        if (db.isFavorite(mContext, post)){
            holder.favoriteButton.setImageResource(R.drawable.star);

        }
        else{
            holder.favoriteButton.setImageResource(R.drawable.star2);
        }

        holder.favoriteButton.setOnClickListener(fl);

        return convertView;
    }

    public static class ViewHolder{
        TextView title;
        ImageView image;
        ImageView favoriteButton;
        CardView content;
    }

    public String extractUrls(String input) {

        String result = null;
        Pattern pattern = Pattern.compile(
                "<img[^>]*src=[\\\\\\\"']([^\\\\\\\"^']*)");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String src = matcher.group();
            int startIndex = src.indexOf("src=") + 5;
            if (result == null){
                result = src.substring(startIndex, src.length());
            }
        }
        return result;
    }
}

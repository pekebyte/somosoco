package pekebyte.com.somosoco.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import pekebyte.com.somosoco.Model.Item;
import pekebyte.com.somosoco.PostDetail;
import pekebyte.com.somosoco.R;

public class PostAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    private LayoutInflater mInflater;

    public PostAdapter(@NonNull Context context,List<Item> items) {
        super(context, R.layout.row_news, items);
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        final Item item = getItem(position);

        convertView = mInflater.inflate(R.layout.row_news, parent, false);

        holder = new ViewHolder();

        holder.title = (TextView) convertView.findViewById(R.id.NoticiaTitle);

        holder.title.setText(item.getTitle());

        holder.image = (ImageView) convertView.findViewById(R.id.postimage);

        String postImage = extractUrls(item.getContent());

        if (postImage != null){
            Picasso.with(mContext).load(postImage).fit().into(holder.image);
        }
        else{
            holder.image.setVisibility(View.GONE);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(), PostDetail.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Gson gson = new Gson();
                i.putExtra("item", gson.toJson(item));
                mContext.startActivity(i);
            }
        };

        holder.image.setOnClickListener(listener);

        return convertView;
    }

    public static class ViewHolder{
        TextView title;
        ImageView image;
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

package com.example.esatgozcu.firebaseinstagramclone;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostClass extends ArrayAdapter<String> {

    private final ArrayList<String> useremail;
    private final ArrayList<String> userImage;
    private final ArrayList<String> userComment;
    private final ArrayList<String> userLike;
    private final ArrayList<String> userId;
    private final Activity context;


    public PostClass(ArrayList<String> useremail, ArrayList<String> userImage, ArrayList<String> userComment, ArrayList<String> userLike ,ArrayList<String> userId,Activity context) {
        super(context, R.layout.custom_view,useremail);
        this.useremail = useremail;
        this.userImage = userImage;
        this.userLike = userLike;
        this.userComment = userComment;
        this.userId = userId;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_view,null,true);

        TextView useremailText = (TextView) customView.findViewById(R.id.username);
        TextView commentText = (TextView) customView.findViewById(R.id.commentText);
        ImageView imageView = (ImageView) customView.findViewById(R.id.imageView2);
        TextView like = (TextView) customView.findViewById(R.id.like);

        useremailText.setText(useremail.get(position));
        commentText.setText(userComment.get(position));
        like.setText(userLike.get(position));
        Picasso.with(context).load(userImage.get(position)).into(imageView);

        return customView;
    }
}


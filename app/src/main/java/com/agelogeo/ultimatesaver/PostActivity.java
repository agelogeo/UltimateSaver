package com.agelogeo.ultimatesaver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent i = getIntent();
        final int position = i.getIntExtra("position",0);
        ImageView imageView = findViewById(R.id.post_Photo);
        Picasso.get().setIndicatorsEnabled(true);
        Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(position).getLink()).into(imageView);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

        Button post_Save = findViewById(R.id.post_Save);
        post_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedDownloads.SaveImage(PostActivity.this,position);
            }
        });





    }
}

package com.agelogeo.ultimatesaver;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        VideoView videoView = findViewById(R.id.post_Video);
        ImageView imageView = findViewById(R.id.post_Photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        final int position = i.getIntExtra("position",0);

        setTitle("@"+SavedDownloads.getItemFromStaticDownloads(position).getUsername());


        if(SavedDownloads.getItemFromStaticDownloads(position).isVideo) {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            try {
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(imageView);

                videoView.setMediaController(mediaController);
                videoView.setVideoURI(Uri.parse(SavedDownloads.getItemFromStaticDownloads(position).getLink()));
                videoView.start();

            }  catch ( Exception e){
                e.printStackTrace();
            }
        }else{
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(position).getLink()).into(imageView);
            //PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        }

        Button post_Save = findViewById(R.id.post_Save);
        post_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SavedDownloads.getItemFromStaticDownloads(position).isVideo)
                    SavedDownloads.SaveVideo(PostActivity.this,position);
                else
                    SavedDownloads.SaveImage(PostActivity.this,position);
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

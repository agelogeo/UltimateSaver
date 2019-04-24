package com.agelogeo.ultimatesaver.downloadFragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.agelogeo.ultimatesaver.Download;
import com.agelogeo.ultimatesaver.PostActivity;
import com.agelogeo.ultimatesaver.R;
import com.agelogeo.ultimatesaver.SavedDownloads;
import com.squareup.picasso.Picasso;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DownloadViewHolder>{
    View v;

    RVAdapter(){
    }


    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_custom_view, viewGroup, false);
        DownloadViewHolder pvh = new DownloadViewHolder(v, SavedDownloads.getItemFromStaticDownloads(i));
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadViewHolder downloadViewHolder, int i) {
        final int position = i;
        downloadViewHolder.username.setText("@"+SavedDownloads.getItemFromStaticDownloads(i).getUsername());

        try {
            Picasso.get().setIndicatorsEnabled(true);
            Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(i).getPreview()).into(downloadViewHolder.photoWallpaper);
            Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(i).getProfile_url()).into(downloadViewHolder.profilePicView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadViewHolder.save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SavedDownloads.getItemFromStaticDownloads(position).isVideo())
                    SavedDownloads.SaveVideo(v.getContext(),position);
                else
                    SavedDownloads.SaveImage(v.getContext(),position);
            }
        });

        downloadViewHolder.photoWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                intent.putExtra("position",position);
                if(SavedDownloads.getItemFromStaticDownloads(position).isVideo()){
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            // the context of the activity
                            (Activity)v.getContext(),

                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute

                            new Pair<View, String>(v.findViewById(R.id.custom_photoWallpaper),
                                    v.getContext().getString(R.string.transition_string_video))
                    );
                    ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
                }else{
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            // the context of the activity
                            (Activity)v.getContext(),

                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute

                            new Pair<View, String>(v.findViewById(R.id.custom_photoWallpaper),
                                    v.getContext().getString(R.string.transition_string_photo))
                    );
                    ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return SavedDownloads.getStaticDownloads().size();
    }


    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView username;
        ImageView photoWallpaper, profilePicView;
        ImageButton save_button, repost_button , share_button;
        Download download;

        DownloadViewHolder(final View itemView, final Download download) {
            super(itemView);
            this.download = download;
            cv = itemView.findViewById(R.id.custom_cv);
            username = itemView.findViewById(R.id.custom_usernameTextView);
            photoWallpaper = itemView.findViewById(R.id.custom_photoWallpaper);
            profilePicView = itemView.findViewById(R.id.custom_profilePicView);
            save_button = itemView.findViewById(R.id.custom_save_button);
            repost_button = itemView.findViewById(R.id.custom_repost_button);
            share_button = itemView.findViewById(R.id.custom_share_button);



        }
    }




}

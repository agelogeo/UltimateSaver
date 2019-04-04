package com.agelogeo.ultimatesaver.downloadFragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.agelogeo.ultimatesaver.Download;
import com.agelogeo.ultimatesaver.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DownloadViewHolder> {
    ArrayList<Download> downloads;

    RVAdapter(ArrayList<Download> downloads){
        this.downloads = downloads;
    }


    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_custom_view, viewGroup, false);
        DownloadViewHolder pvh = new DownloadViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder downloadViewHolder, int i) {
        downloadViewHolder.username.setText("@"+downloads.get(i).getUsername());
        try {
            Picasso.get().load(downloads.get(i).getPreviews().get(0)).into(downloadViewHolder.photoWallpaper);
            Picasso.get().load(downloads.get(i).getProfile_url()).into(downloadViewHolder.profilePicView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return downloads.size();
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView username;
        ImageView photoWallpaper, profilePicView;
        ImageButton save_button, repost_button , share_button;

        DownloadViewHolder(View itemView) {
            super(itemView);
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

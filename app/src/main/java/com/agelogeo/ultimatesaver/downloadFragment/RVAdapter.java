package com.agelogeo.ultimatesaver.downloadFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agelogeo.ultimatesaver.Download;
import com.agelogeo.ultimatesaver.R;
import com.agelogeo.ultimatesaver.SavedDownloads;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DownloadViewHolder>{
    //ArrayList<Download> downloads;
    View v;

    RVAdapter(ArrayList<Download> downloads){
        //this.downloads = downloads;
    }


    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_custom_view, viewGroup, false);
        DownloadViewHolder pvh = new DownloadViewHolder(v, SavedDownloads.getItemFromStaticDownloads(i),i);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder downloadViewHolder,int i) {
        final int position = i;
        downloadViewHolder.username.setText("@"+SavedDownloads.getItemFromStaticDownloads(i).getUsername());

        try {
            Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(i).getPreviews().get(0)).into(downloadViewHolder.photoWallpaper);
            Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(i).getProfile_url()).into(downloadViewHolder.profilePicView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadViewHolder.save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImage(v.getContext(),position);
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

        DownloadViewHolder(final View itemView, final Download download, final int position) {
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

    private static void SaveImage(final Context context, final int position){
        final ProgressDialog progress = new ProgressDialog(context);
        class SaveThisImage extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setTitle("Processing");
                progress.setMessage("Please Wait...");
                progress.setCancelable(false);
                progress.show();
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                try{
                    // Find the SD Card path
                    File filepath = Environment.getExternalStorageDirectory();

                    // Create a new folder in SD Card
                    File dir = new File(filepath.getAbsolutePath()
                            + "/UltimateSaver/");
                    dir.mkdirs();

                    //String filteredUploader = uploader.replaceAll("[^a-z0-9A-Z]", "_");
                    //Log.i("Uploader",uploader);
                    //Log.i("FilteredUploader",filteredUploader);
                    // Create a name for the saved image
                    long datetime = System.currentTimeMillis();

                    File myImageFile = new File(dir, SavedDownloads.getItemFromStaticDownloads(position).getUsername()+Long.toString(datetime)+".png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(myImageFile);
                        Bitmap bitmap = Picasso.get().load(SavedDownloads.getItemFromStaticDownloads(position).getLinks().get(0)).get();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(myImageFile));
                        context.sendBroadcast(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if(progress.isShowing()){
                    progress.dismiss();
                }
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
        }
        SaveThisImage shareimg = new SaveThisImage();
        shareimg.execute();
    }


}

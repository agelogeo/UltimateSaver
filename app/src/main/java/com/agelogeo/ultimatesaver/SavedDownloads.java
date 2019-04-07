package com.agelogeo.ultimatesaver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public final class SavedDownloads {

    public static RecyclerView recyclerView ;
    public static ArrayList<Download> staticDownloads = new ArrayList<Download>();

    public static ArrayList<Download> getStaticDownloads() {
        return staticDownloads;
    }

    public static void setStaticDownloads(ArrayList<Download> staticDownloads) {
        SavedDownloads.staticDownloads = staticDownloads;
    }

    public static void addOnStaticDownloads(Download download){
        staticDownloads.add(download);
        //recyclerView.smoothScrollToPosition(0);
    }

    public static void addOnStaticDownloads(int position,Download download){
        staticDownloads.add(position,download);
    }

    public static Download getItemFromStaticDownloads(int position){
        return staticDownloads.get(position);
    }

    public static void removeFromStaticDownloads(int position){
        staticDownloads.remove(position);
    }

    public static void clearStaticDownloads(){
        staticDownloads.clear();
    }

    public static void setRecyclerView(RecyclerView recycler){
        recyclerView = recycler;
    }

    public static void SaveImage(final Context context, final int position){
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

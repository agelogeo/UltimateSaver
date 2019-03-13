package com.agelogeo.ultimatesaver.downloadFragment;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.agelogeo.ultimatesaver.ImageAdapter;
import com.agelogeo.ultimatesaver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class downloadFragment extends Fragment {
    String link;
    GridView imageGrid;
    ArrayList<Bitmap> bitmapList;
    View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.download_fragment, container, false);

        imageGrid = v.findViewById(R.id.gridview);
        bitmapList = new ArrayList<Bitmap>();

        FloatingActionButton fab = v.findViewById(R.id.pasteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = readFromClipboard();
                Log.i("URL",link);
                fetchPhoto();
            }
        });




        return v;
    }

    public void pasteButton(View view){
        link = readFromClipboard();
        fetchPhoto();
        Log.i("URL",link);
        //urlText.setText(link);
    }

    public String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        Toast.makeText(getContext(),"Please copy a valid link.",Toast.LENGTH_SHORT).show();
        return null;
    }

    public void analyzePost(String s){
        try{
            String link ;
            Pattern pattern = Pattern.compile("window._sharedData = (.*?)[}];");
            Matcher matcher = pattern.matcher(s);

            matcher.find();
            String jObject = matcher.group(1)+"}";
            JSONObject jsonObject = new JSONObject(jObject);
            JSONObject entry_data = jsonObject.getJSONObject("entry_data");
            JSONArray PostPage = entry_data.getJSONArray("PostPage");
            JSONObject first_graphql_shortcode_media = PostPage.getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
            JSONObject owner = first_graphql_shortcode_media.getJSONObject("owner");

            Log.i("USERNAME",owner.getString("username"));
            Log.i("PROFILE_PIC_URL",owner.getString("profile_pic_url"));

            if(first_graphql_shortcode_media.has("edge_sidecar_to_children")){
                JSONArray children_edges = first_graphql_shortcode_media.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");
                Log.i("WITH_CHILDREN_COUNT",Integer.toString(children_edges.length()));

                for(int i=0; i<children_edges.length(); i++){
                    JSONObject node = children_edges.getJSONObject(i).getJSONObject("node");

                    if(node.has("video_url")){
                        //link = node.getString("video_url");
                        link = node.getJSONArray("display_resources").getJSONObject(2).getString("src");
                        Log.i("CHILDREN_W_VIDEO_"+(i+1),node.getString("video_url"));
                    }else{
                        link = node.getJSONArray("display_resources").getJSONObject(2).getString("src");
                        Log.i("CHILDREN_W_PHOTO_"+(i+1),node.getJSONArray("display_resources").getJSONObject(2).getString("src"));
                    }
                    ImageDownloader imageTask = new ImageDownloader();
                    imageTask.execute(link);
                }
            }else{
                if(first_graphql_shortcode_media.has("video_url")){
                    Log.i("NO_CHILDREN_W_VIDEO",first_graphql_shortcode_media.getString("video_url"));
                    //first_graphql_shortcode_media.getString("video_url");
                    link = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src");
                }else{
                    Log.i("NO_CHILDREN_W_PHOTO",first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src"));
                    link = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src");
                }
                ImageDownloader imageTask = new ImageDownloader();
                imageTask.execute(link);
            }
        }catch (Exception e){
            Toast.makeText(getContext(),"Error with your link.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void fetchPhoto(){
        DownloadTask task = new DownloadTask();
        try {
            task.execute(link);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void ,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "",line = null;
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                while ((line = reader.readLine()) != null)
                    result += line;

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            analyzePost(s);
        }
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bitmapList.add(bitmap);
            imageGrid.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), bitmapList));

        }
    }
}

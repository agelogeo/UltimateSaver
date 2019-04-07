package com.agelogeo.ultimatesaver.downloadFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.agelogeo.ultimatesaver.Download;
import com.agelogeo.ultimatesaver.InternalStorage;
import com.agelogeo.ultimatesaver.R;
import com.agelogeo.ultimatesaver.SavedDownloads;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class downloadFragment extends Fragment {
    String link = "";
    RecyclerView recyclerView;
    View v;
    RVAdapter adapter;
    Download mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.download_fragment, container, false);

        final FloatingActionButton fab = v.findViewById(R.id.pasteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = readFromClipboard();
                Log.i("URL", link);
                openLink();
            }
        });

        recyclerView = v.findViewById(R.id.recycler_view);
        SavedDownloads.setRecyclerView(recyclerView);
        //final Snackbar snackbar = Snackbar.make(recyclerView, "Do you want to delete all saves?",Snackbar.LENGTH_SHORT);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            Snackbar snackbar = null;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    fab.hide();
                    if(snackbar == null && SavedDownloads.getStaticDownloads().size() > 10){
                        snackbar = Snackbar.make(recyclerView, "Do you want to delete all saves?",Snackbar.LENGTH_LONG);
                        snackbar.setAction("Delete All!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SavedDownloads.clearStaticDownloads();
                                saveDownloads();
                                adapter.notifyDataSetChanged();
                            }
                        });
                        snackbar.show();
                    }else if(SavedDownloads.getStaticDownloads().size() > 10){
                        if(!snackbar.isShown()){
                            snackbar = Snackbar.make(recyclerView, "Do you want to delete all saves?",Snackbar.LENGTH_LONG);
                            snackbar.setAction("Delete All!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SavedDownloads.clearStaticDownloads();
                                    saveDownloads();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }else{
                    if(fab.getVisibility() == View.GONE){
                        fab.show();
                        if(snackbar != null)
                            if(snackbar.isShown())
                                snackbar.dismiss();
                    }
                }
            }
        });

        adapter = new RVAdapter();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        try {
            // Retrieve the list from internal storage
            ArrayList<Download> cachedDownloads = (ArrayList<Download>) InternalStorage.readObject(getContext(), "downloads");
            SavedDownloads.clearStaticDownloads();
            // Display the items from the list retrieved.
            for (Download download : cachedDownloads) {
                addOnList(download);
            }
            adapter.notifyDataSetChanged();

        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("Error", e.getMessage());
        }

        return v;
    }

    public void addOnList(Download download){
        SavedDownloads.addOnStaticDownloads(download);
        recyclerView.smoothScrollToPosition(SavedDownloads.getStaticDownloads().size()-1);
        saveDownloads();
    }

    public void openLink() {
        DownloadTask task = new DownloadTask();
        try {
            task.execute(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "", line = null;
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                while ((line = reader.readLine()) != null)
                    result += line;

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            analyzePost(s);
        }
    }

    public void analyzePost(String s) {
        try {
            //list_of_downloads = new ArrayList<Download>();
            String link, preview;
            Pattern pattern = Pattern.compile("window._sharedData = (.*?)[}];");
            Matcher matcher = pattern.matcher(s);

            matcher.find();
            String jObject = matcher.group(1) + "}";
            JSONObject jsonObject = new JSONObject(jObject);
            JSONObject entry_data = jsonObject.getJSONObject("entry_data");
            JSONArray PostPage = entry_data.getJSONArray("PostPage");
            JSONObject first_graphql_shortcode_media = PostPage.getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
            JSONObject owner = first_graphql_shortcode_media.getJSONObject("owner");

            Log.i("USERNAME", owner.getString("username"));
            Log.i("PROFILE_PIC_URL", owner.getString("profile_pic_url"));


            if (first_graphql_shortcode_media.has("edge_sidecar_to_children")) {
                JSONArray children_edges = first_graphql_shortcode_media.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");
                Log.i("WITH_CHILDREN_COUNT", Integer.toString(children_edges.length()));

                for (int i = 0; i < children_edges.length(); i++) {
                    JSONObject node = children_edges.getJSONObject(i).getJSONObject("node");
                    Download myDownload = new Download();
                    myDownload.setUsername(owner.getString("username"));
                    myDownload.setProfile_url(owner.getString("profile_pic_url"));

                    if (node.has("video_url")) {
                        link = node.getString("video_url");
                        preview = node.getJSONArray("display_resources").getJSONObject(0).getString("src");
                        myDownload.addOnLinks(link, true, preview);
                        Log.i("CHILDREN_W_VIDEO_" + (i + 1), link);
                    } else {
                        link = node.getJSONArray("display_resources").getJSONObject(2).getString("src");
                        preview = node.getJSONArray("display_resources").getJSONObject(0).getString("src");
                        myDownload.addOnLinks(link, false, preview);
                        Log.i("CHILDREN_W_PHOTO_" + (i + 1), link);
                    }
                    addOnList(myDownload);
                    Log.i("Download", myDownload.toString());
                }
            } else {
                Download myDownload = new Download();
                myDownload.setUsername(owner.getString("username"));
                myDownload.setProfile_url(owner.getString("profile_pic_url"));
                if (first_graphql_shortcode_media.has("video_url")) {
                    link = first_graphql_shortcode_media.getString("video_url");
                    preview = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(0).getString("src");
                    myDownload.addOnLinks(link, true, preview);
                    Log.i("NO_CHILDREN_W_VIDEO", link);
                } else {
                    link = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(2).getString("src");
                    preview = first_graphql_shortcode_media.getJSONArray("display_resources").getJSONObject(0).getString("src");
                    myDownload.addOnLinks(link, false, preview);
                    Log.i("NO_CHILDREN_W_PHOTO", link);
                }
                addOnList(myDownload);
                Log.i("Download", myDownload.toString());
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error with your link.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void saveDownloads(){
        try {
            InternalStorage.writeObject(getContext(), "downloads", SavedDownloads.getStaticDownloads());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(v, "1 Item deleted.",
                Snackbar.LENGTH_SHORT);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedDownloads.addOnStaticDownloads(mRecentlyDeletedItemPosition,
                        mRecentlyDeletedItem);
                adapter.notifyItemInserted(mRecentlyDeletedItemPosition);
                saveDownloads();
            }
        });
        snackbar.show();
    }


    public void deleteItem(int position) {
        mRecentlyDeletedItem = SavedDownloads.getItemFromStaticDownloads(position);
        mRecentlyDeletedItemPosition = position;
        SavedDownloads.removeFromStaticDownloads(position);
        adapter.notifyItemRemoved(position);
        saveDownloads();
        showUndoSnackbar();
    }

    public String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        Toast.makeText(getContext(), "Please copy a valid link.", Toast.LENGTH_SHORT).show();
        return "";
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private RVAdapter mAdapter;
        private Drawable icon;
        private Drawable  background ;

        public SwipeToDeleteCallback(RVAdapter adapter) {
            super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
            icon = ContextCompat.getDrawable(getContext(),
                    R.drawable.baseline_delete_sweep_black_48);
            background = getResources().getDrawable(R.drawable.side_nav_bar);

        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = viewHolder.getAdapterPosition();
            deleteItem(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 5;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(Color.rgb(255,255,255));
            }

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin ;
                int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }


}

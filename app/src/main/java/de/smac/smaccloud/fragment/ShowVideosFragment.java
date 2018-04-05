package de.smac.smaccloud.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.ShowGalleryItemListActivity;
import de.smac.smaccloud.adapter.GalleryFolderAdapter;

public class ShowVideosFragment extends Fragment{
    private static RecyclerView recyclerView;
    private GalleryFolderAdapter adapterFolder;
    private List<String> arrayListFolderName = new ArrayList<>();
    private List<String> arrayListFolderBitmaplist =new ArrayList<>();
    private final String[] projection = new String[]{ MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA };
    private final String[] projection2 = new String[]{MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.DATA };
    public static List<String> arrayListVideosListPath =new ArrayList<>();
    public static List<String> arrayListVideoListName = new ArrayList<>();
    public static List<Boolean> arrayListSelectedFlag =new ArrayList<>();

    private List<String> arrayListFolderBitmapList =new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bucket names reloaded
        arrayListFolderBitmapList.clear();
        arrayListVideosListPath.clear();
        arrayListVideoListName.clear();
        arrayListFolderName.clear();
        getVideosFolderListFromMediaStore();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_containing_video_images, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        populateRecyclerView();
        return v;
    }

    private void populateRecyclerView() {
        adapterFolder = new GalleryFolderAdapter(arrayListFolderName, arrayListFolderBitmaplist,getContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterFolder);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                getVideosUnderFolder(arrayListFolderName.get(position));
                Intent intent=new Intent(getContext(), ShowGalleryItemListActivity.class);
                intent.putExtra("FROM","Videos");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        adapterFolder.notifyDataSetChanged();
    }
    public void getVideosFolderListFromMediaStore(){
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Video.Media.DATE_ADDED);
        ArrayList<String> folderNamesTEMP = new ArrayList<>(cursor.getCount());
        ArrayList<String> bitmapListTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                String image = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(image);
                if (file.exists() && !albumSet.contains(album)) {
                    folderNamesTEMP.add(album);
                    bitmapListTEMP.add(image);
                    albumSet.add(album);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (folderNamesTEMP == null) {
            arrayListFolderName = new ArrayList<>();
        }
        arrayListFolderName.clear();
        arrayListFolderBitmaplist.clear();
        arrayListFolderName.addAll(folderNamesTEMP);
        arrayListFolderBitmaplist.addAll(bitmapListTEMP);
    }
    public void getVideosUnderFolder(String bucket){
        arrayListSelectedFlag.clear();
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME+" =?",new String[]{bucket},MediaStore.Video.Media.DATE_ADDED);
        ArrayList<String> videosTEMPpath = new ArrayList<>(cursor.getCount());
        ArrayList<String> videosTEMPName = new ArrayList<>(cursor.getCount());

        HashSet<String> albumPath = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String path = cursor.getString(cursor.getColumnIndex(projection2[1]));
                String title = cursor.getString(cursor.getColumnIndex(projection2[0]));
                file = new File(path);
                if (file.exists() && !albumPath.contains(path)) {
                    videosTEMPpath.add(path);
                    albumPath.add(path);
                    videosTEMPName.add(title);
                    arrayListSelectedFlag.add(false);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (videosTEMPpath == null) {
            arrayListFolderName = new ArrayList<>();
        }
        arrayListVideosListPath.clear();
        arrayListVideoListName.clear();
        arrayListVideosListPath.addAll(videosTEMPpath);
        arrayListVideoListName.addAll(videosTEMPName);
    }
    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
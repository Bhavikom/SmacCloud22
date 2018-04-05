package de.smac.smaccloud.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.GalleryItemAdapter;
import de.smac.smaccloud.fragment.ShowImagesFragment;
import de.smac.smaccloud.fragment.ShowVideosFragment;
import de.smac.smaccloud.model.SelectedMediaFromGalleryModel;

public class ShowGalleryItemListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GalleryItemAdapter adapterGalleryItemlist;
    private List<String> arrayListMediaPath = new ArrayList<>();
    private List<String> arrayListMediaName = new ArrayList<>();
    public  static List<Boolean> arrayListMediaFlag = new ArrayList<>();
    public static ArrayList<SelectedMediaFromGalleryModel> arrayListSelectedMedia = new ArrayList<>();
    public static String parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery_item);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //toolbar.setNavigationIcon(R.drawable.arrow_back);
        setTitle(ShowGalleryFolderListActivity.title);
        if (arrayListSelectedMedia.size() > 0) {
            setTitle(String.valueOf(arrayListSelectedMedia.size()));
        }
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        parent = getIntent().getExtras().getString("FROM");
        arrayListMediaPath.clear();
        arrayListMediaName.clear();
        arrayListMediaFlag.clear();
        if (parent.equals("Images")) {
            arrayListMediaPath.addAll(ShowImagesFragment.arrayListImagesListPath);
            arrayListMediaName.addAll(ShowImagesFragment.arrayListImagesListName);
            arrayListMediaFlag.addAll(ShowImagesFragment.arrayListSelectedMediaFlag);
        } else {
            arrayListMediaPath.addAll(ShowVideosFragment.arrayListVideosListPath);
            arrayListMediaName.addAll(ShowVideosFragment.arrayListVideoListName);
            arrayListMediaFlag.addAll(ShowVideosFragment.arrayListSelectedFlag);
        }
        populateRecyclerView();
    }


    private void populateRecyclerView() {
        for (int i = 0; i < arrayListMediaFlag.size(); i++) {
            if (arrayListSelectedMedia.contains(arrayListMediaPath.get(i))) {
                arrayListMediaFlag.set(i, true);
            } else {
                arrayListMediaFlag.set(i, false);
            }
        }
        adapterGalleryItemlist = new GalleryItemAdapter(arrayListMediaPath, arrayListMediaFlag, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(adapterGalleryItemlist);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!arrayListMediaFlag.get(position).equals(true)) {
                    SelectedMediaFromGalleryModel selectedMediaFromGalleryModel = new SelectedMediaFromGalleryModel();
                    selectedMediaFromGalleryModel.setMediaBitmapPath(arrayListMediaPath.get(position));
                    selectedMediaFromGalleryModel.setMediaName(arrayListMediaName.get(position));
                    if(parent.equalsIgnoreCase("Images")){
                        selectedMediaFromGalleryModel.setFileType("image/png");
                    }else {
                        selectedMediaFromGalleryModel.setFileType("video/mp4");
                    }
                    arrayListSelectedMedia.add(selectedMediaFromGalleryModel);
                    arrayListMediaFlag.set(position, !arrayListMediaFlag.get(position));
                    adapterGalleryItemlist.notifyItemChanged(position);
                } else if (arrayListMediaFlag.get(position).equals(true)) {
                    if (arrayListSelectedMedia.indexOf(arrayListMediaPath.get(position)) != -1) {
                        arrayListSelectedMedia.remove(arrayListSelectedMedia.indexOf(arrayListMediaPath.get(position)));
                        arrayListMediaFlag.set(position, !arrayListMediaFlag.get(position));
                        adapterGalleryItemlist.notifyItemChanged(position);
                    }
                }
                ShowGalleryFolderListActivity.selectionTitle = arrayListSelectedMedia.size();
                if (arrayListSelectedMedia.size() != 0) {
                    setTitle(String.valueOf(arrayListSelectedMedia.size()));
                } else {
                    setTitle(ShowGalleryFolderListActivity.title);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
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


package de.smac.smaccloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.ShowGalleryItemListActivity;
import de.smac.smaccloud.model.SelectedMediaFromGalleryModel;

/**
 * Created by S Soft on 29-Mar-18.
 */

public class SelectedMediaAdapter extends RecyclerView.Adapter<SelectedMediaAdapter.MyViewHolder>{
    private ArrayList<SelectedMediaFromGalleryModel> arrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail,delete;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            thumbnail=(ImageView) view.findViewById(R.id.image_thumbnail);
            delete=(ImageView) view.findViewById(R.id.image_delete);
            progressBar = (ProgressBar)view.findViewById(R.id.proressbar);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowGalleryItemListActivity.arrayListSelectedMedia.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }
    public SelectedMediaAdapter(ArrayList<SelectedMediaFromGalleryModel> arrayList,Context context) {
        this.arrayList = arrayList;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_media_itemlist, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(context).load("file://"+arrayList.get(position).getMediaBitmapPath()).override(153,160).crossFade().centerCrop().dontAnimate().skipMemoryCache(true).into(holder.thumbnail);

        if(arrayList.get(position).isUploadedCompleted()){
            holder.progressBar.setVisibility(View.GONE);
        }
        else {
            holder.progressBar.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}



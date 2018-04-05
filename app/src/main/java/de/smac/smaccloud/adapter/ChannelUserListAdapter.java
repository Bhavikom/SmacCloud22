package de.smac.smaccloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.model.CreateChannnelModel;

/**
 * Created by S Soft on 14-Mar-18.
 */

public class ChannelUserListAdapter extends RecyclerView.Adapter<ChannelUserListAdapter.MyViewHolder> {

    public List<CreateChannnelModel> arrayListUser = new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    private int selectedPosition = -1;
    public ChannelUserListAdapter(Context activity, List<CreateChannnelModel> arrayList)
    {
        this.context = activity;
        inflater = LayoutInflater.from(activity);
        arrayListUser = arrayList;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.channel_itemlist, null);
        MyViewHolder viewHolder = new MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final int finalPosition = position;
        CreateChannnelModel createChannnelModel = arrayListUser.get(position);
        holder.textViewUser.setText(createChannnelModel.getName());

        holder.checkBoxUser.setChecked(createChannnelModel.isSelected());
        holder.checkBoxUser.setTag(arrayListUser.get(position));
        //holder.checkBoxUser.setTag(position);
        /*if (position == selectedPosition) {
            holder.checkBoxUser.setChecked(true);
        } else {
            holder.checkBoxUser.setChecked(false);
        }*/
        holder.checkBoxUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* single selection for checkbox*/
                /*CheckBox cb = (CheckBox) v;
                if(cb.isChecked()){
                    selectedPosition = (int) cb.getTag();
                }else {
                    selectedPosition = -1;
                }
                notifyDataSetChanged();*/
                /* single selection for checkbox*/

               /* for multiple selection of checkbox */
                CheckBox cb = (CheckBox) v;
                CreateChannnelModel model = (CreateChannnelModel) cb.getTag();

                model.setSelected(cb.isChecked());
                arrayListUser.get(position).setSelected(cb.isChecked());
                notifyDataSetChanged();
                /* end for multiple selection of checkbox */
            }
        });
        /*if (clickListener != null)
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    switch (view.getId())
                    {
                        case R.id.compoundButtonDetail:
                            clickListener.onItemDetailClick(finalPosition, view);
                            break;

                        case R.id.itemView:
                            clickListener.onItemClick(finalPosition, view);
                            break;
                    }
                }
            };

        }*/
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayListUser.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewUser;
        CheckBox checkBoxUser;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            textViewUser = (TextView) itemView.findViewById(R.id.textview_user_item);
            checkBoxUser = (CheckBox) itemView.findViewById(R.id.checkbox_user_item);

        }
    }
}

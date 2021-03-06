package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

/**
 * POJO(Plain Old Java Object) class for Channel
 */
public class Channel implements Parcelable
{
    public static final Creator<Channel> CREATOR = new Creator<Channel>()
    {
        public Channel createFromParcel(Parcel source)
        {
            return new Channel(source);
        }

        public Channel[] newArray(int size)
        {
            return new Channel[size];
        }
    };
    public int id;
    public int creatorId;
    public String name;
    public String location;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int syncType;
    public int isSynced;
    public User creator;
    public String thumbnail;

    public Channel()
    {
    }

    protected Channel(Parcel in)
    {
        this.id = in.readInt();
        this.location = in.readString();
        this.name = in.readString();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.creatorId = in.readInt();
        this.syncType = in.readInt();
        this.isSynced = in.readInt();
        this.creator = in.readParcelable(User.class.getClassLoader());
        this.thumbnail = in.readString();
    }

    public static void parseFromCursor(Cursor cursor, Channel channel) throws ParseException
    {
        if (cursor != null)
        {
            channel.id = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_ID));
            channel.creatorId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_CREATOR_ID));
            channel.name = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_NAME));
            channel.location = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_LOCATION));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channel.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channel.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channel.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channel.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channel.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channel.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            channel.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_IS_SYNCED));
            channel.thumbnail = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_THUMBNAIL));
        }
        else
            channel.id = -1;
    }

    public static void parseListFromJson(JSONArray channelsJson, ArrayList<Channel> channels) throws JSONException, ParseException
    {
        for (int i = 0; i < channelsJson.length(); i++)
        {
            Channel channel = new Channel();
            JSONObject channelJson = channelsJson.getJSONObject(i);
            parseFromJson(channelJson, channel);
            channels.add(channel);
        }
    }

    public static void parseFromJson(JSONObject channelJson, Channel channel) throws JSONException, ParseException
    {
        channel.id = channelJson.getInt("Id");
        channel.creatorId = channelJson.getInt("CreatorId");
        channel.location = channelJson.getString("Location");
        channel.name = channelJson.getString("Name");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = channelJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channel.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channel.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channel.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channel.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channel.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channel.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        channel.syncType = channelJson.optInt("SyncType");
        channel.thumbnail = channelJson.optString("Thumbnail");
        channel.creator = new User();
        User.parseFromJson(channelJson.getJSONObject("Creator"), channel.creator);
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addChannel(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateChannel(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeChannel(context, this);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.id);
        dest.writeString(this.location);
        dest.writeString(this.name);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.creatorId);
        dest.writeInt(this.syncType);
        dest.writeInt(this.isSynced);
        dest.writeParcelable(this.creator, flags);
        dest.writeString(this.thumbnail);
    }
    public static void parseListFromJsonObject(JSONObject channelsJson, ArrayList<Channel> channels) throws JSONException, ParseException
    {
        Channel channel = new Channel();
        parseFromJson(channelsJson, channel);
        channels.add(channel);

    }
}

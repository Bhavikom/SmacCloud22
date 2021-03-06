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
 * POJO(Plain Old Java Object) class for Channel User
 */
public class ChannelUser implements Parcelable
{
    public static final Creator<ChannelUser> CREATOR = new Creator<ChannelUser>()
    {
        public ChannelUser createFromParcel(Parcel source)
        {
            return new ChannelUser(source);
        }

        public ChannelUser[] newArray(int size)
        {
            return new ChannelUser[size];
        }
    };
    public int id;
    public int channelId;
    public int userId;
    public int addedBy;
    public int syncType;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int isSynced;


    public ChannelUser()
    {
    }

    protected ChannelUser(Parcel in)
    {
        this.addedBy = in.readInt();
        this.channelId = in.readInt();
        this.id = in.readInt();
        this.userId = in.readInt();
        this.syncType = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.isSynced = in.readInt();
    }

    public static void parseListFromJson(JSONArray payloadJson, ArrayList<ChannelUser> channelUsers) throws JSONException, ParseException
    {

        for (int i = 0; i < payloadJson.length(); i++)
        {
            ChannelUser channelUser = new ChannelUser();
            JSONObject channelUserJson = payloadJson.getJSONObject(i);
            parseFromJson(channelUserJson, channelUser);
            channelUsers.add(channelUser);
        }
    }

    public static void parseFromJson(JSONObject channelFileJson, ChannelUser channelUser) throws JSONException, ParseException
    {

        channelUser.addedBy = channelFileJson.optInt("AddedBy");
        channelUser.channelId = channelFileJson.optInt("ChannelId");
        channelUser.id = channelFileJson.optInt("Id");
        channelUser.userId = channelFileJson.optInt("UserId");
        channelUser.syncType = channelFileJson.optInt("SyncType");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String tempDate = channelFileJson.optString("InsertDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelUser.insertDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelUser.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelFileJson.optString("UpdateDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelUser.updateDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelUser.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
        tempDate = channelFileJson.optString("DeleteDate");
        if (tempDate != null && !tempDate.isEmpty())
        {
            try
            {
                channelUser.deleteDate = Helper.getDateFormate().parse(tempDate);
            }
            catch (ParseException px)
            {
                channelUser.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
            }
        }
    }

    public static void parseFromCursor(Cursor cursor, ChannelUser channelUser) throws ParseException
    {
        if (cursor != null)
        {
            channelUser.id = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_ID));
            channelUser.addedBy = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_USER_ADDED_BY));
            channelUser.channelId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_FILE_CHANNEL_ID));
            channelUser.userId = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_USER_USER_ID));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_USER_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelUser.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelUser.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_USER_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelUser.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelUser.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.CHANNEL_USER_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    channelUser.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    channelUser.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            channelUser.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.CHANNEL_USER_IS_SYNCED));

        }
        else
            channelUser.id = -1;
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addChannelUsers(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateChannelUsers(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeChannelUsers(context, this);
    }

    @Override
    public int describeContents()
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.addedBy);
        dest.writeInt(this.channelId);
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeInt(this.syncType);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.isSynced);
    }
}

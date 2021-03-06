package de.smac.smaccloud.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;

/**
 * POJO(Plain Old Java Object) class for Media Version
 */
public class MediaVersion implements Parcelable
{
    public static final Creator<MediaVersion> CREATOR = new Creator<MediaVersion>()
    {
        public MediaVersion createFromParcel(Parcel source)
        {
            return new MediaVersion(source);
        }

        public MediaVersion[] newArray(int size)
        {
            return new MediaVersion[size];
        }
    };
    public User creator;
    public int creatorId;
    public int fileId;
    public int modifierId;
    public int id;
    public int version;
    public Date insertDate;
    public Date updateDate;
    public Date deleteDate;
    public int syncType;
    public int isSynced;

    public MediaVersion()
    {
    }

    protected MediaVersion(Parcel in)
    {
        this.creator = in.readParcelable(User.class.getClassLoader());
        this.creatorId = in.readInt();
        this.fileId = in.readInt();
        this.modifierId = in.readInt();
        this.id = in.readInt();
        this.version = in.readInt();
        long tmpInsertDate = in.readLong();
        this.insertDate = tmpInsertDate == -1 ? null : new Date(tmpInsertDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        long tmpDeleteDate = in.readLong();
        this.deleteDate = tmpDeleteDate == -1 ? null : new Date(tmpDeleteDate);
        this.syncType = in.readInt();
        this.isSynced = in.readInt();
    }

    public static void parseListFromJson(JSONObject mediaVersionJson, MediaVersion mediaVersion) throws JSONException, ParseException
    {
        if (mediaVersion != null)
        {
            mediaVersion.id = mediaVersionJson.optInt("Id");
            mediaVersion.creator = new User();
            User.parseFromJson(mediaVersionJson.optJSONObject("CreatedBy"), mediaVersion.creator);
            mediaVersion.creatorId = mediaVersionJson.optInt("CreatorId");
            mediaVersion.fileId = mediaVersionJson.optInt("FileId");
            mediaVersion.modifierId = mediaVersionJson.optInt("ModifierId");
            mediaVersion.version = mediaVersionJson.optInt("Version");
            mediaVersion.syncType = mediaVersionJson.optInt("SyncType");
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = mediaVersionJson.optString("InsertDate");
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = mediaVersionJson.optString("UpdateDate");
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = mediaVersionJson.optString("DeleteDate");
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
        }


    }

    public static void parseFromCursor(Cursor cursor, MediaVersion mediaVersion) throws ParseException
    {
        if (cursor != null)
        {
            mediaVersion.id = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_ID));
            mediaVersion.creatorId = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_CREATOR_ID));
            mediaVersion.fileId = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_FILE_ID));
            mediaVersion.modifierId = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_MODIFIER_ID));
            mediaVersion.version = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION));
            mediaVersion.isSynced = cursor.getInt(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_IS_SYNCED));
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault());
            //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_INSERT_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.insertDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.insertDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_UPDATE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.updateDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.updateDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
            tempDate = cursor.getString(cursor.getColumnIndex(DataHelper.MEDIA_VERSION_DELETE_DATE));
            if (tempDate != null && !tempDate.isEmpty())
            {
                try
                {
                    mediaVersion.deleteDate = Helper.getDateFormate().parse(tempDate);
                }
                catch (ParseException px)
                {
                    mediaVersion.deleteDate = Helper.getDateFormate().parse(Helper.parseDate(Helper.getDateFormate().toPattern(), new Date(tempDate)));
                }
            }
        }
        else
            mediaVersion.id = -1;
    }

    public boolean add(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.addMediaVersion(context, this);
    }

    public boolean saveChanges(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.updateMediaVersion(context, this);
    }

    public boolean remove(Context context)
    {
        if (id <= 0)
            throw new IllegalArgumentException("Id cannot be zero or negative");
        return DataHelper.removeMediaVersion(context, this);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(this.creator, 0);
        dest.writeInt(this.creatorId);
        dest.writeInt(this.fileId);
        dest.writeInt(this.modifierId);
        dest.writeInt(this.id);
        dest.writeInt(this.version);
        dest.writeLong(insertDate != null ? insertDate.getTime() : -1);
        dest.writeLong(updateDate != null ? updateDate.getTime() : -1);
        dest.writeLong(deleteDate != null ? deleteDate.getTime() : -1);
        dest.writeInt(this.syncType);
        dest.writeInt(this.isSynced);
    }
}

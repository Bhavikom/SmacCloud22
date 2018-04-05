package de.smac.smaccloud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO(Plain Old Java Object) class for Channel User
 */
public class MediaAllDownload implements Parcelable
{
    public int mediaId;
    public int currentVersionId;
    public int channelId;
    public int isDownloading;

    public MediaAllDownload()
    {

    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(mediaId);
        out.writeInt(currentVersionId);
        out.writeInt(channelId);
        out.writeInt(isDownloading);
    }

    public static final Creator<MediaAllDownload> CREATOR
            = new Creator<MediaAllDownload>()
    {
        public MediaAllDownload createFromParcel(Parcel in)
        {
            return new MediaAllDownload(in);
        }

        public MediaAllDownload[] newArray(int size)
        {
            return new MediaAllDownload[size];
        }
    };

    public MediaAllDownload(Parcel in)
    {
        mediaId = in.readInt();
        currentVersionId = in.readInt();
        channelId = in.readInt();
        isDownloading = in.readInt();
    }

}

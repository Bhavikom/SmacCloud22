package de.smac.smaccloud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S Soft on 29-Mar-18.
 */

public class SelectedMediaFromGalleryModel implements Parcelable{
    String mediaName;
    String mediaBitmapPath;
    String fileType;
    boolean isUploadedCompleted;
    boolean isUplodaingRunning;

    public boolean isUplodaingRunning() {
        return isUplodaingRunning;
    }

    public void setUplodaingRunning(boolean uplodaingRunning) {
        isUplodaingRunning = uplodaingRunning;
    }

    public boolean isUploadedCompleted() {
        return isUploadedCompleted;
    }

    public void setUploadedCompleted(boolean uploadedCompleted) {
        isUploadedCompleted = uploadedCompleted;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaBitmapPath() {
        return mediaBitmapPath;
    }

    public void setMediaBitmapPath(String mediaBitmapPath) {
        this.mediaBitmapPath = mediaBitmapPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

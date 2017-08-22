package br.com.uilquemessias.favoritemovies.services.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Video implements Parcelable {

    public static final String SITE_YOUTUBE = "YouTube";

    public final static Creator<Video> CREATOR = new Creator<Video>() {

        @SuppressWarnings({"unchecked"})
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Expose
    private String id;
    @Expose
    private String key;
    @Expose
    private String site;

    public Video() {
        //
    }

    public Video(Parcel in) {
        id = in.readString();
        key = in.readString();
        site = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(site);
    }
}

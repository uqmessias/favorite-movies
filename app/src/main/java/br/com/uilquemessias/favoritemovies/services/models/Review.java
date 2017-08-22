package br.com.uilquemessias.favoritemovies.services.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Review implements Parcelable {

    public final static Creator<Review> CREATOR = new Creator<Review>() {

        @SuppressWarnings({"unchecked"})
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;

    public Review() {
        //
    }

    public Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id){
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author){
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content){
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }
}

package br.com.uilquemessias.favoritemovies.services.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public final static Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {

        @SuppressWarnings({"unchecked"})
        public Movie createFromParcel(Parcel in) {
            Movie instance = new Movie();
            instance.posterPath = in.readString();
            instance.backdropPath = in.readString();

            instance.title = in.readString();
            instance.releaseDate = in.readString();
            instance.voteAverage = in.readFloat();
            instance.overview = in.readString();
            return instance;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @SerializedName("poster_path")
    private String posterPath;
    @Expose
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @Expose
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("vote_average")
    private Float voteAverage;

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(backdropPath);

        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeFloat(voteAverage);
        dest.writeString(overview);
    }
}

package br.com.uilquemessias.favoritemovies.services.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public final static Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {

        @SuppressWarnings({"unchecked"})
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
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

    public Movie() {
        //
    }

    public Movie(Parcel in) {
        posterPath = in.readString();
        backdropPath = in.readString();

        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readFloat();
        overview = in.readString();
    }

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

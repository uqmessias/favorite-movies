package br.com.uilquemessias.favoritemovies.services.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResult {

    @Expose
    private Integer page;
    @Expose
    private Integer id;
    @SerializedName("results")
    private List<Video> videos = null;
    @SerializedName("total_results")
    private Integer totalResults;
    @SerializedName("total_pages")
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }
}

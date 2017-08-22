package br.com.uilquemessias.favoritemovies.services.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResult {

    @Expose
    private Integer page;
    @Expose
    private Integer id;
    @SerializedName("results")
    private List<Review> reviews = null;
    @SerializedName("total_results")
    private Integer totalResults;
    @SerializedName("total_pages")
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }
}

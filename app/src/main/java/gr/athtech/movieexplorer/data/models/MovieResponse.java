package gr.athtech.movieexplorer.data.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MovieResponse {
    private int page;
    private String api_key;
    private String query;
    private Movie[] results;
    private int total_results;
    private int total_pages;
}

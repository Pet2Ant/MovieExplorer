package gr.athtech.movieexplorer.data.models;


public class MovieResponse {
    private int page;
    private String api_key;
    private Movie[] results;

    public Movie[] getResults() {
        return results;
    }
}
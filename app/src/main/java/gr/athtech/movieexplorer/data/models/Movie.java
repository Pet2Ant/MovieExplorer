package gr.athtech.movieexplorer.data.models;

public class Movie {
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getPoster_path() {
        return poster_path;
    }

    private int id;
    private String title;
    private String poster_path;

}

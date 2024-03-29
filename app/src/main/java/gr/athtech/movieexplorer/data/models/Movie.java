package gr.athtech.movieexplorer.data.models;

public class Movie {
    public Movie(int id, String title, String posterPath, boolean b) {
        this.id = id;
        this.title = title;
        this.poster_path = posterPath;
        this.isFavorite = b;
    }

    public Movie(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getPoster_path() {
        return poster_path;
    }
    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public double getVote_average() {
        return vote_average;
    }
    public double getPopularity() {
        return popularity;
    }


    private int id;
    private String title;
    private String poster_path;
    private double vote_average;
    private double popularity;
    private static boolean isFavorite;


}

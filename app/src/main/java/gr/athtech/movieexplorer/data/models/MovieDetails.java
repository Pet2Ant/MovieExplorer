package gr.athtech.movieexplorer.data.models;


public class MovieDetails {

    public int getId() {
        return id;
    }
    public String getOverview() {
        return overview;
    }
    public double getPopularity() {
        return popularity;
    }
    public String getPoster_path() {
        return poster_path;
    }
    public String getBackdrop_path() {
        return backdrop_path;
    }
    public String getTitle() {
        return title;
    }
    public String getRelease_date() {
        return release_date;
    }
    public Genres[] getGenres() {
        return genres;
    }
    public String getBudget() {
        return budget;
    }
    public String getRuntime() {
        return runtime;
    }
    public String getVote_average() {
        return vote_average;
    }


    private int id;
    private String title;
    private String overview;
    private Genres[] genres;
    private double popularity;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private String budget;
    private String runtime;
    private String vote_average;

    public static class Genres {
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        private int id;
        private String name;

        @Override
        public String toString() {
            return name;
        }
    }
}

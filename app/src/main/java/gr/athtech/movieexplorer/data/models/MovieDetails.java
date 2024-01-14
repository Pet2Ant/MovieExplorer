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
    public Cast[] getCast() {
        return cast;
    }
    public Crew[] getCrew() {
        return crew;
    }
    public String getBudget() {
        return budget;
    }
    public int getRuntime() {
        return runtime;
    }
    public double getVote_average() {
        return vote_average;
    }

    public void setId(int id) {
        this.id = id;
    }
    private int id;
    private String title;
    private String overview;
    private Genres[] genres;
    private Cast[] cast;
    private Crew[] crew;
    private double popularity;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private String budget;
    private int runtime;
    private double vote_average;

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

    public static class Cast {
        public int getId() {
            return id;
        }
        public String getProfile_path() {
            return profile_path;
        }
        public String getCharacter() {
            return character;
        }
        public String getName() {
            return name;
        }
        private int id;
        private String profile_path;
        private String character;
        private String name;
    }

    public static class Crew {
        public int getId() {
            return id;
        }
        public String getProfile_path() {
            return profile_path;
        }
        public String getJob() {
            return job;
        }
        public String getDepartment() {
            return department;
        }
        public String getName() {
            return name;
        }
        private int id;
        private String profile_path;
        private String job;
        private String department;
        private String name;
    }
}

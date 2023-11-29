package gr.athtech.movieexplorer.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Movie {
    private int id;
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;
    private double vote_average;
    private int vote_count;
    private String backdrop_path;
    private String original_language;
    private String original_title;
    private double popularity;
    private boolean adult;
    private boolean video;
    private int[] genre_ids;
}

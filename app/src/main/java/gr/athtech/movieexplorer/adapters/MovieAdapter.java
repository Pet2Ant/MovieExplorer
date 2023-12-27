package gr.athtech.movieexplorer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.activities.MovieDetailsActivity;
import gr.athtech.movieexplorer.data.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;

    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieHolder, int position) {
        Movie movie = movies.get(position);

        // Set the movie data
        movieHolder.tvTitle.setText(movie.getTitle());

        // Load the movie poster
        Glide.with(context)
                .load(movie.getPoster_path())
                .placeholder(R.drawable.ic_launcher_background)
                .into(movieHolder.ivPoster);

        // Set the click listener
        movieHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPoster = itemView.findViewById(R.id.ivPoster);
        }
    }
}

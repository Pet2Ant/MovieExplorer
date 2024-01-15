package gr.athtech.movieexplorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.data.models.MovieDetails;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private final Context context;
    private final MovieDetails.Cast[] cast;


    public CastAdapter(Context context, MovieDetails.Cast[] cast) {
        this.context = context;
        this.cast = cast;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        MovieDetails.Cast castMember = cast[position];
        if (castMember.getName() != null) {
            holder.tvName.setText(castMember.getName());
        }
        if (castMember.getCharacter() != null) {
            holder.tvCharacter.setText(castMember.getCharacter());
        }
        Glide.with(context).load("https://image.tmdb.org/t/p/original/" + castMember.getProfile_path()).placeholder(R.drawable.ic_launcher_background).into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return cast.length;
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvCharacter;

        CastViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvCharacter = itemView.findViewById(R.id.tvCharacter);
        }
    }
}

package gr.athtech.movieexplorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import gr.athtech.movieexplorer.R;
import gr.athtech.movieexplorer.data.models.MovieDetails;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder>{
    private final Context context;
    private final MovieDetails.Crew[] crew;

    public CrewAdapter(Context context, MovieDetails.Crew[] crew) {
        this.context = context;
        this.crew = crew;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CrewViewHolder holder, int position) {
        MovieDetails.Crew crewMember = crew[position];
        if (crewMember.getName() != null) {
            holder.tvName.setText(crewMember.getName());
        }
        if (crewMember.getJob() != null) {
            holder.tvCharacter.setText(crewMember.getJob());
        }
        Glide.with(context).load("https://image.tmdb.org/t/p/original/" + crewMember.getProfile_path()).placeholder(R.drawable.ic_launcher_background).into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return crew.length;
    }

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvCharacter;

        CrewViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvCharacter = itemView.findViewById(R.id.tvCharacter);
        }
    }
}

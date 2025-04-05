package ru.myitschool.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<UserScore> userScores;

    public LeaderboardAdapter(List<UserScore> userScores) {
        this.userScores = userScores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore userScore = userScores.get(position);
        holder.rankText.setText(String.valueOf(position + 1));
        holder.usernameText.setText(userScore.getUsername());
        holder.scoreText.setText(String.valueOf(userScore.getScore()));
    }

    @Override
    public int getItemCount() {
        return userScores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText;
        TextView usernameText;
        TextView scoreText;

        ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rank_text);
            usernameText = itemView.findViewById(R.id.username_text);
            scoreText = itemView.findViewById(R.id.score_text);
        }
    }
} 
package ru.myitschool.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;
    private OnPostInteractionListener listener;

    public interface OnPostInteractionListener {
        void onLikeClicked(Post post);
        void onCommentClicked(Post post);
        void onMenuClicked(Post post, View anchor);
    }

    public PostAdapter(Context context, List<Post> posts, OnPostInteractionListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        
        // Load user avatar
        if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
            Glide.with(context)
                .load(post.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.default_avatar)
                .into(holder.userAvatar);
        }

        // Set user name
        holder.userName.setText(post.getUserName());

        // Set timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(post.getTimestamp()));
        holder.timestamp.setText(formattedDate);

        // Set content
        holder.content.setText(post.getContent());

        // Handle post image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                .load(post.getImageUrl())
                .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Set counts
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()));

        // Set click listeners
        holder.likeButton.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClicked(post);
        });

        holder.commentButton.setOnClickListener(v -> {
            if (listener != null) listener.onCommentClicked(post);
        });

        holder.menuButton.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClicked(post, v);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView timestamp;
        TextView content;
        ImageView postImage;
        ImageButton likeButton;
        TextView likeCount;
        ImageButton commentButton;
        TextView commentCount;
        ImageButton menuButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            content = itemView.findViewById(R.id.post_content);
            postImage = itemView.findViewById(R.id.post_image);
            likeButton = itemView.findViewById(R.id.like_button);
            likeCount = itemView.findViewById(R.id.like_count);
            commentButton = itemView.findViewById(R.id.comment_button);
            commentCount = itemView.findViewById(R.id.comment_count);
            menuButton = itemView.findViewById(R.id.post_menu);
        }
    }
} 
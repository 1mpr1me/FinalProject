package ru.myitschool.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private List<Friend> friends;
    private OnFriendClickListener listener;
    private OnProfileClickListener profileListener;

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }
    
    public interface OnProfileClickListener {
        void onProfileClick(Friend friend);
    }

    public FriendsAdapter(List<Friend> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
        this.profileListener = null;
    }
    
    public FriendsAdapter(List<Friend> friends, OnFriendClickListener listener, OnProfileClickListener profileListener) {
        this.friends = friends;
        this.listener = listener;
        this.profileListener = profileListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.friendName.setText(friend.getName());
        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend));
        
        // Set up profile image click listener if available
        if (profileListener != null) {
            holder.profileImage.setOnClickListener(v -> profileListener.onProfileClick(friend));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        CircleImageView profileImage;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            profileImage = itemView.findViewById(R.id.friend_profile_image);
        }
    }
} 






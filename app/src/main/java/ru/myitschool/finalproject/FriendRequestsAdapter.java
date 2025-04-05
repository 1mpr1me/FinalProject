package ru.myitschool.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder> {
    private List<MessagesFragment.FriendRequest> requests;
    private OnRequestAcceptedListener acceptListener;
    private OnRequestRejectedListener rejectListener;

    public interface OnRequestAcceptedListener {
        void onRequestAccepted(MessagesFragment.FriendRequest request);
    }

    public interface OnRequestRejectedListener {
        void onRequestRejected(MessagesFragment.FriendRequest request);
    }

    public FriendRequestsAdapter(List<MessagesFragment.FriendRequest> requests,
                               OnRequestAcceptedListener acceptListener,
                               OnRequestRejectedListener rejectListener) {
        this.requests = requests;
        this.acceptListener = acceptListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        MessagesFragment.FriendRequest request = requests.get(position);
        holder.nameText.setText(request.getName());
        
        holder.acceptButton.setOnClickListener(v -> {
            if (acceptListener != null) {
                acceptListener.onRequestAccepted(request);
            }
        });
        
        holder.rejectButton.setOnClickListener(v -> {
            if (rejectListener != null) {
                rejectListener.onRequestRejected(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        MaterialButton acceptButton;
        MaterialButton rejectButton;

        RequestViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.request_name);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
} 
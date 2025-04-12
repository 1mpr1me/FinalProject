package ru.myitschool.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
        void onShareClicked(Post post);
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

        // Set user name (in header and description)
        holder.userName.setText(post.getUserName());
        holder.userNameDescription.setText(post.getUserName());

        // Set timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date(post.getTimestamp()));
        holder.timestamp.setText(formattedDate);

        // Set content
        holder.content.setText(post.getDescription());

        // Handle post image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Handle code content
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            holder.codeView.setVisibility(View.VISIBLE);
            setupCodeView(holder.codeView, post.getCodeContent());
        } else {
            holder.codeView.setVisibility(View.GONE);
        }

        // Set counts
        String likeCountText = post.getLikeCount() + " likes";
        holder.likeCount.setText(likeCountText);
        
        String commentCountText = "View all " + post.getCommentCount() + " comments";
        holder.commentCount.setText(commentCountText);

        // Set click listeners
        holder.likeButton.setOnClickListener(v -> listener.onLikeClicked(post));
        holder.commentButton.setOnClickListener(v -> listener.onCommentClicked(post));
        holder.shareButton.setOnClickListener(v -> listener.onShareClicked(post));
        holder.menuButton.setOnClickListener(v -> listener.onMenuClicked(post, v));
    }

    private void setupCodeView(WebView webView, String code) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Escape the code for JavaScript
        String escapedCode = code
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");

        // Create HTML content with CodeMirror
        String htmlContent = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<link rel=\"stylesheet\" href=\"file:///android_asset/codemirror.css\">" +
            "<script src=\"file:///android_asset/codemirror.js\"></script>" +
            "<script src=\"file:///android_asset/javascript.js\"></script>" +
            "<style>" +
            "body { margin: 0; padding: 0; background: #1e1e1e; }" +
            ".CodeMirror { height: auto; font-size: 14px; }" +
            ".CodeMirror-scroll { max-height: 300px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<script>" +
            "var editor = CodeMirror(document.body, {" +
            "  value: \"" + escapedCode + "\"," +
            "  mode: \"javascript\"," +
            "  theme: \"monokai\"," +
            "  readOnly: true," +
            "  lineNumbers: true," +
            "  lineWrapping: true" +
            "});" +
            "</script>" +
            "</body>" +
            "</html>";

        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
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
        TextView userNameDescription;
        TextView timestamp;
        TextView content;
        ImageView postImage;
        WebView codeView;
        ImageButton likeButton;
        TextView likeCount;
        ImageButton commentButton;
        TextView commentCount;
        ImageButton shareButton;
        ImageButton menuButton;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            userNameDescription = itemView.findViewById(R.id.user_name_description);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            content = itemView.findViewById(R.id.post_content);
            postImage = itemView.findViewById(R.id.post_image);
            codeView = itemView.findViewById(R.id.code_view);
            likeButton = itemView.findViewById(R.id.like_button);
            likeCount = itemView.findViewById(R.id.like_count);
            commentButton = itemView.findViewById(R.id.comment_button);
            commentCount = itemView.findViewById(R.id.comment_count);
            shareButton = itemView.findViewById(R.id.share_button);
            menuButton = itemView.findViewById(R.id.post_menu);
        }
    }
} 
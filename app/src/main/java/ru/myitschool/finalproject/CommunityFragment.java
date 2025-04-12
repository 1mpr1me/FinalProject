package ru.myitschool.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityFragment extends Fragment implements PostAdapter.OnPostInteractionListener {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPost;
    private DatabaseReference postsRef;
    private DatabaseReference friendsRef;
    private FirebaseAuth mAuth;
    private List<Post> posts;
    private List<String> friendIds;
    private PostAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        friendsRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("friends");
        posts = new ArrayList<>();
        friendIds = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        
        recyclerView = view.findViewById(R.id.posts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new PostAdapter(getContext(), posts, this);
        recyclerView.setAdapter(adapter);
        
        fabAddPost = view.findViewById(R.id.fab_add_post);
        fabAddPost.setOnClickListener(v -> {
            if (getActivity() != null) {
                CreatePostFragment createPostFragment = new CreatePostFragment();
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createPostFragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
        
        // Load friends first, then load posts
        loadFriends();
        
        return view;
    }

    private void loadFriends() {
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendIds.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    if (friendId != null) {
                        friendIds.add(friendId);
                    }
                }
                // After loading friends, load posts
                loadPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), 
                    "Error loading friends: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && (friendIds.contains(post.getUserId()) || post.getUserId().equals(mAuth.getCurrentUser().getUid()))) {
                        posts.add(post);
                    }
                }
                
                // Sort posts by timestamp (newest first)
                Collections.sort(posts, (p1, p2) -> 
                    Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), 
                    "Error loading posts: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLikeClicked(Post post) {
        // TODO: Implement like functionality
        Toast.makeText(getContext(), "Like functionality coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentClicked(Post post) {
        // TODO: Implement comment functionality
        Toast.makeText(getContext(), "Comment functionality coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuClicked(Post post, View anchor) {
        // TODO: Implement post menu functionality
        Toast.makeText(getContext(), "Post menu coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClicked(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = post.getDescription() + "\n\nShared from CodeHub";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share post"));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ... existing code ...
    }
} 
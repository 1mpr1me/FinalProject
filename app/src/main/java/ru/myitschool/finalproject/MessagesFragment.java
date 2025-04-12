package ru.myitschool.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView friendsList;
    private RecyclerView requestsList;
    private FloatingActionButton addFriendFab;
    private LinearLayout requestsContainer;

    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private List<Friend> friends = new ArrayList<>();
    private List<FriendRequest> friendRequests = new ArrayList<>();
    private FriendsAdapter friendsAdapter;
    private FriendRequestsAdapter requestsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Debug information
        if (currentUser != null) {
            System.out.println("Current user ID: " + currentUser.getUid());
            System.out.println("Current user display name: " + currentUser.getDisplayName());
            System.out.println("Current user email: " + currentUser.getEmail());
            
            // Check if display name is set
            if (currentUser.getDisplayName() == null || currentUser.getDisplayName().isEmpty()) {
                System.out.println("WARNING: Display name is not set for current user");
            }
        } else {
            System.out.println("ERROR: No user is currently logged in");
        }

        // Initialize views
        friendsList = view.findViewById(R.id.friends_list);
        addFriendFab = view.findViewById(R.id.add_friend_fab);
        requestsContainer = view.findViewById(R.id.requests_container);
        requestsList = view.findViewById(R.id.requests_list);

        // Set up RecyclerViews
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        friendsAdapter = new FriendsAdapter(friends, this::onFriendSelected);
        requestsAdapter = new FriendRequestsAdapter(friendRequests, this::onRequestAccepted, this::onRequestRejected);
        
        friendsList.setAdapter(friendsAdapter);
        requestsList.setAdapter(requestsAdapter);

        // Set up add friend FAB
        addFriendFab.setOnClickListener(v -> showAddFriendDialog());

        // Load friends list and friend requests
        loadFriends();
        loadFriendRequests();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Add a button to view friend requests
        view.findViewById(R.id.view_requests_button).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.slide_out_left,   // exit
                        R.anim.slide_in_left,    // popEnter
                        R.anim.slide_out_right   // popExit
                    )
                    .replace(R.id.fragment_container, new FriendRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showAddFriendDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        TextInputEditText friendUsernameInput = dialogView.findViewById(R.id.friend_username_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_friend)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String friendUsername = friendUsernameInput.getText().toString().trim();
                    if (!friendUsername.isEmpty()) {
                        sendFriendRequest(friendUsername);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void sendFriendRequest(String friendUsername) {
        System.out.println("Searching for user with username: " + friendUsername);
        // Find user by username
        databaseRef.child("users").orderByChild("username").equalTo(friendUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("Search result exists: " + dataSnapshot.exists());
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String friendId = userSnapshot.getKey();
                                System.out.println("Found user with ID: " + friendId);
                                
                                if (friendId != null && !friendId.equals(currentUser.getUid())) {
                                    // Check if already friends
                                    databaseRef.child("users").child(currentUser.getUid())
                                            .child("friends").child(friendId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        Toast.makeText(getContext(), R.string.already_friends, Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    
                                                    // Check if request already exists
                                                    databaseRef.child("users").child(friendId)
                                                            .child("friendRequests").child(currentUser.getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        Toast.makeText(getContext(), R.string.request_already_sent, Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                    
                                                                    // Create friend request object
                                                                    FriendRequest request = new FriendRequest(
                                                                            currentUser.getUid(),
                                                                            currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous"
                                                                    );
                                                                    
                                                                    // Send friend request
                                                                    databaseRef.child("users").child(friendId)
                                                                            .child("friendRequests").child(currentUser.getUid())
                                                                            .setValue(request)
                                                                            .addOnSuccessListener(aVoid -> {
                                                                                System.out.println("Friend request sent successfully");
                                                                                Toast.makeText(getContext(), R.string.friend_request_sent, Toast.LENGTH_SHORT).show();
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                System.out.println("Failed to send friend request: " + e.getMessage());
                                                                                Toast.makeText(getContext(), R.string.error_adding_friend, Toast.LENGTH_SHORT).show();
                                                                            });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    System.out.println("Error checking existing request: " + error.getMessage());
                                                                    Toast.makeText(getContext(), R.string.error_adding_friend, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    System.out.println("Error checking friendship: " + error.getMessage());
                                                    Toast.makeText(getContext(), R.string.error_adding_friend, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    return;
                                }
                            }
                        }
                        System.out.println("User not found with username: " + friendUsername);
                        Toast.makeText(getContext(), R.string.user_not_found, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Database error while searching for user: " + databaseError.getMessage());
                        Toast.makeText(getContext(), R.string.error_adding_friend, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFriendRequests() {
        if (currentUser != null) {
            String path = "users/" + currentUser.getUid() + "/friendRequests";
            System.out.println("Loading friend requests from path: " + path);
            
            databaseRef.child("users").child(currentUser.getUid()).child("friendRequests")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println("Friend requests data changed. Exists: " + dataSnapshot.exists() + ", Children count: " + dataSnapshot.getChildrenCount());
                            
                            friendRequests.clear();
                            for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                                String senderId = requestSnapshot.getKey();
                                MessagesFragment.FriendRequest request = requestSnapshot.getValue(MessagesFragment.FriendRequest.class);
                                if (request != null && senderId != null) {
                                    request.setId(senderId); // Ensure the ID is set correctly
                                    friendRequests.add(request);
                                    System.out.println("Added friend request from: " + request.getName());
                                }
                            }
                            System.out.println("Total friend requests loaded: " + friendRequests.size());
                            
                            requestsAdapter.notifyDataSetChanged();
                            requestsContainer.setVisibility(friendRequests.isEmpty() ? View.GONE : View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("Error loading friend requests: " + databaseError.getMessage());
                            Toast.makeText(getContext(), R.string.error_loading_friends, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            System.out.println("Cannot load friend requests - currentUser is null");
        }
    }

    private void onRequestAccepted(FriendRequest request) {
        if (currentUser == null) return;
        
        String friendId = request.getId();
        String friendName = request.getName();
        
        // Add to friends list for both users
        databaseRef.child("users").child(currentUser.getUid())
                .child("friends").child(friendId)
                .setValue(friendName)
                .addOnSuccessListener(aVoid -> {
                    databaseRef.child("users").child(friendId)
                            .child("friends").child(currentUser.getUid())
                            .setValue(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous")
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove friend request
                                databaseRef.child("users").child(currentUser.getUid())
                                        .child("friendRequests").child(friendId)
                                        .removeValue()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(getContext(), R.string.friend_added, Toast.LENGTH_SHORT).show();
                                            // Reload friends list
                                            loadFriends();
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Error removing friend request: " + e.getMessage());
                                            Toast.makeText(getContext(), R.string.error_accepting_request, Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Error adding friend to other user: " + e.getMessage());
                                Toast.makeText(getContext(), R.string.error_accepting_request, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding friend: " + e.getMessage());
                    Toast.makeText(getContext(), R.string.error_accepting_request, Toast.LENGTH_SHORT).show();
                });
    }

    private void onRequestRejected(FriendRequest request) {
        if (currentUser == null) return;
        
        String friendId = request.getId();
        
        // Remove friend request
        databaseRef.child("users").child(currentUser.getUid())
                .child("friendRequests").child(friendId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), R.string.request_rejected, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error rejecting friend request: " + e.getMessage());
                    Toast.makeText(getContext(), R.string.error_rejecting_request, Toast.LENGTH_SHORT).show();
                });
    }

    private void loadFriends() {
        if (currentUser != null) {
            databaseRef.child("users").child(currentUser.getUid()).child("friends")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Keep AI Assistant as first friend
                            friends.clear();
                            friends.add(new Friend("ai_assistant", "AI Assistant"));
                            
                            for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                                String friendId = friendSnapshot.getKey();
                                String friendName = friendSnapshot.getValue(String.class);
                                if (friendId != null && friendName != null) {
                                    friends.add(new Friend(friendId, friendName));
                                }
                            }
                            friendsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), R.string.error_loading_friends, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onFriendSelected(Friend friend) {
        if (friend.getId().equals("ai_assistant")) {
            // Open chat with AI Assistant
            ChatFragment chatFragment = ChatFragment.newInstance("ai_assistant", "AI Assistant");
            getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
        } else {
            // Open chat with regular friend
            ChatFragment chatFragment = ChatFragment.newInstance(friend.getId(), friend.getName());
            getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
        }
    }

    public static class Friend {
        private String id;
        private String name;

        public Friend(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class FriendRequest {
        private String id;
        private String name;

        public FriendRequest() {
            // Required for Firebase
        }

        public FriendRequest(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        
        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
    }
} 
package com.example.user.friendsandfamily;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    RecyclerView findFriendsRecyclerView;
    Toolbar mToolbar;
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        findFriendsRecyclerView=findViewById(R.id.find_friends_recyclerViewId);
        findFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolbar=findViewById(R.id.find_friends_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("FInd Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> option=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull Contacts model) {
                holder.userStatus.setText(model.getStatus());
                holder.userName.setText(model.getName());
                Picasso.get().load(model.getUserImage()).placeholder(R.drawable.profile_image).into(holder.profileImge);
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                return  viewHolder;
            }
        };

        findFriendsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        TextView userName,userStatus;
        CircleImageView profileImge;
        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=(TextView) itemView.findViewById(R.id.user_profile_name);
            userStatus=(TextView) itemView.findViewById(R.id.user_profile_status);
            profileImge=(CircleImageView) itemView.findViewById(R.id.user_profile_image);

        }
    }
}

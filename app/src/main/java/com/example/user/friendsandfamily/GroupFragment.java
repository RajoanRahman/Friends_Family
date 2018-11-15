package com.example.user.friendsandfamily;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private View groupFragmentView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> list_of_grp=new ArrayList<>();
    DatabaseReference groupDatabase;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView=inflater.inflate(R.layout.fragment_group, container, false);
        groupDatabase=FirebaseDatabase.getInstance().getReference().child("Groups");

        listView=(ListView)groupFragmentView.findViewById(R.id.group_list);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_grp);
        listView.setAdapter(arrayAdapter);

        retrieveGroupName();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: If I clicked on particular group Name it'wll go GroupChatActivity alongSide the Group Name.
               String currentGroupName =parent.getItemAtPosition(position).toString();// Get the Group Name Based on position

                Intent grpChatIntent=new Intent(getContext(),GroupChatActivity.class);
                grpChatIntent.putExtra("groupName",currentGroupName);
                startActivity(grpChatIntent);

            }
        });
        return groupFragmentView;
    }

    //TODO: Retrieve the groups Name from the Database
    private void retrieveGroupName() {
        groupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Iterator is used to read one  by one element from a list.
                //HashSet is is used to contains a list of unique value
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());//getKey() will get the Groups name from Database

                }
                list_of_grp.clear();
                list_of_grp.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

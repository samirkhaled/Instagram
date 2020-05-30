package samir.com.instagram.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import samir.com.instagram.Adapters.PostAdapter;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDetails extends Fragment {

    public PostDetails() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    String postId;
    PostAdapter postAdapter;
    List<Posts> posts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_post_details, container, false);
       //inflate
        recyclerView=view.findViewById(R.id.rv);
        SharedPreferences preferences=getContext().getSharedPreferences("insta",0);
        postId=preferences.getString("postId","none");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        posts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),posts);
        recyclerView.setAdapter(postAdapter);
        //
       readPost();
       return  view;
    }

    private  void readPost(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           posts.clear();
           Posts post=dataSnapshot.getValue(Posts.class);
           posts.add(post);
           postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }
}

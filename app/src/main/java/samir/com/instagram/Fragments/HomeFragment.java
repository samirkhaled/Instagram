package samir.com.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import samir.com.instagram.Adapters.PostAdapter;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Posts> posts;
    FirebaseUser firebaseUser;
    List<String>Flist;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view =inflater.inflate(R.layout.fragment_home, container, false);
    //inflate elements
        recyclerView=view.findViewById(R.id.home_rv);
        progressBar=view.findViewById(R.id.pb);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        posts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),posts);
        recyclerView.setAdapter(postAdapter);
        //end
        getFollowingList();
    return view;
    }
    private void getFollowingList(){
      Flist=new ArrayList<>();
      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
              .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
              .child("Followers");

      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Flist.clear();
              for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                  Flist.add(snapshot.getKey());
                  Log.v("foll",snapshot.getKey());
              }

              readPosts();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


    }


   private void readPosts(){
       DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");


       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              posts.clear();
              for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                  Posts post=snapshot.getValue(Posts.class);
                  for (String id:Flist){
                      if(post.getPublisher().equals(id)){
                          posts.add(post);
                      }else if(post.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
                          posts.add(post);
                      }
                  }


              }
              postAdapter.notifyDataSetChanged();
              progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });




   }


}

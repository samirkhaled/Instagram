package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;


import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import samir.com.instagram.Adapters.UsersAdapter;
import samir.com.instagram.Models.Users;

public class FollowerActivity extends AppCompatActivity {

    String title;
    String id;
    List<String>idList;
    RecyclerView  recyclerView;
    UsersAdapter usersAdapter;
    List<Users> usersList;
    TextView textView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        //inflate
        recyclerView =findViewById(R.id.rv);
        Toolbar toolbar=findViewById(R.id.toobar);
        AppBarLayout appBarLayout=findViewById(R.id.bar);
        textView=findViewById(R.id.title);
        //


        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");
        textView.setText(title);
        //recyclerVie setting
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersList=new ArrayList<>();
        usersAdapter=new UsersAdapter(usersList,this);
        recyclerView.setAdapter(usersAdapter);
        //end
        idList=new ArrayList<>();
        switch(title){
            case "Like":
                getLikes();
                break;
            case "Followers":
                getFollowers();
                break;
            case "Following":
                getFollowing();
                break;
        }

        
    }

    private void getLikes() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                viewUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                viewUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowing() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                viewUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

  private  void viewUsers(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               usersList.clear();
               for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   Users user=snapshot.getValue(Users.class);
                   for(String id:idList){
                       if(user.getID().equals(id)){
                           usersList.add(user);
                       }

                   }
               }
               usersAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

  }


}

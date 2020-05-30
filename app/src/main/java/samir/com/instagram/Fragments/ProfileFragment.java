package samir.com.instagram.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Adapters.OptionsActivity;
import samir.com.instagram.Adapters.Profile_post_Adapter;
import samir.com.instagram.EditProfile;
import samir.com.instagram.FollowerActivity;
import samir.com.instagram.Login;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    ImageView optional;
    RecyclerView profile_myPosts,profile_saved;
    CircleImageView myProfileImage;
    TextView posts,followers,following,bio,username,fullName;
    Button edit_profile;
    ImageButton myPosts_button,saved_button;
    String profileID;
    FirebaseUser firebaseUser;
    List<Posts>myPosts;
    Profile_post_Adapter mypostAdapter;
    List<Posts>myPosts_save;
    Profile_post_Adapter mypostAdapter_save;
    List<String> savedpostId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_profile, container, false);
       //inflate all elements here  ,here we go
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        optional=view.findViewById(R.id.optional);
        profile_myPosts=view.findViewById(R.id.profile_rv);
        profile_saved=view.findViewById(R.id.profile_saved_rv);
        myProfileImage=view.findViewById(R.id.profile_image);
        posts=view.findViewById(R.id.profile_posts);
        followers=view.findViewById(R.id.profile_follower);
        following=view.findViewById(R.id.profile_following);
        bio=view.findViewById(R.id.profile_bio);
        username=view.findViewById(R.id.profile_userName);
        fullName=view.findViewById(R.id.profile_fullName);
        edit_profile=view.findViewById(R.id.profile_edit_profile);
        myPosts_button=view.findViewById(R.id.myPosts);
        saved_button=view.findViewById(R.id.myPostsSaved);
        //
        //setting my post rv
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        profile_myPosts.setHasFixedSize(true);
        profile_myPosts.setLayoutManager(linearLayoutManager);
        myPosts=new ArrayList<>();
        mypostAdapter=new Profile_post_Adapter(getContext(),myPosts);
        profile_myPosts.setAdapter(mypostAdapter);
        //


        //setting post saved rv
        LinearLayoutManager linearLayoutManager1=new GridLayoutManager(getContext(),3);
        profile_saved.setHasFixedSize(true);
        profile_saved.setLayoutManager(linearLayoutManager1);
        myPosts_save=new ArrayList<>();
        mypostAdapter_save=new Profile_post_Adapter(getContext(),myPosts_save);
        profile_saved.setAdapter(mypostAdapter_save);
        savedpostId=new ArrayList<>();

        //end
        //gwt user id to get  info
        SharedPreferences preferences=getContext().getSharedPreferences("insta",0);
        profileID=preferences.getString("userProfile","none");
       //

        //logout
        optional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        //


        //edit clickable  //
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=edit_profile.getText().toString();
                if(btn.equals("Edit profile")){
                    //do
                    startActivity(new Intent(getContext(), EditProfile.class));

                }else if(btn.equals("Follow")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(firebaseUser.getUid()).child("Following")
                            .child(profileID).setValue(true);
                    addNotifications();

                    //store in firebase like above but vice versa exchange roles
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(profileID).child("Followers")
                            .child(firebaseUser.getUid()).setValue(true);
                }else if(btn.equals("Following")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(firebaseUser.getUid()).child("Following")
                            .child(profileID).removeValue();

                    //store in firebase like above but vice versa exchange roles
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(profileID).child("Followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        getUserInfo();
        getFollowers();
        getNoOFPosts();
        if(profileID.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit profile");

        }else{
            checkFollowing();
            saved_button.setVisibility(View.GONE);

        }
        getMyPosts();
        myPosts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_myPosts.setVisibility(View.VISIBLE);
                profile_saved.setVisibility(View.GONE);

            }
        });
        saved_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSavedPostId();
                profile_myPosts.setVisibility(View.GONE);
                profile_saved.setVisibility(View.VISIBLE);
            }
        });


        //show following
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowerActivity.class);
                intent.putExtra("id",profileID);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });
        //
        //show follower
    followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FollowerActivity.class);
                intent.putExtra("id",profileID);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });
        //

       return view;
    }

    private void addNotifications(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification").child(profileID);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("postId","");
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("comment","Started following you");
        hashMap.put("isPost",false);
        reference.push().setValue(hashMap);



    }

    //get user info from firebase
  private void getUserInfo(){
      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(profileID);
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if(getContext()==null){
                  return;
              }
              Users user=dataSnapshot.getValue(Users.class);
              Glide.with(getContext()).load(user.getImageUri()).into(myProfileImage);
              username.setText(user.getUserName());
              fullName.setText(user.getFullName());
              bio.setText(user.getBio());


          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

  }

  //if following set button text foll0wing else foll0w
  private void checkFollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid()).child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileID).exists()){
                    edit_profile.setText("Following");

                }else{
                    edit_profile.setText("Follow");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
  }




  //get followers , and following

  private void getFollowers(){
       DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
               .child(firebaseUser.getUid()).child("Followers");

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              followers.setText(""+dataSnapshot.getChildrenCount());
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
      DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Follow")
              .child(firebaseUser.getUid()).child("Following");

      reference1.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              following.setText(""+dataSnapshot.getChildrenCount());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });




  }


  //get number of posts ,based on id , if post publisher id equals to profileId ,counter increased
    // and so on to get all user posts
  private void getNoOFPosts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Posts post=snapshot.getValue(Posts.class);
                    if(post.getPublisher().equals(profileID)){

                        i++;
                    }


                }
                posts.setText(""+i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
  }
//get all my posts

    private  void  getMyPosts(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               myPosts.clear();
               for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   Posts post=snapshot.getValue(Posts.class);
                   if(post.getPublisher().equals(profileID)){
                       myPosts.add(post);

                   }
               }
                Collections.reverse(myPosts);
               mypostAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //get saved post

    private void getSavedPostId(){

      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Saves")
              .child(firebaseUser.getUid());
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                  savedpostId.add(snapshot.getKey());
              }
              readSavedPost();

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

    }

    private void readSavedPost() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
                 reference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         myPosts_save.clear();
                         for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                             Posts post=snapshot.getValue(Posts.class);
                             for(String id:savedpostId){
                                 if(post.getPostId().equals(id)){

                                     myPosts_save.add(post);
                                 }

                             }


                         }

                         mypostAdapter_save.notifyDataSetChanged();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

    }

}

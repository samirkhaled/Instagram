package samir.com.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Fragments.ProfileFragment;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolderAdapter> {

     List<Users> users;
     Context  mcontext;
     FirebaseUser firebaseUser;

    public UsersAdapter(List<Users> users, Context mcontext) {
        this.users = users;
        this.mcontext = mcontext;
    }
    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
      return new ViewHolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAdapter holder, int position) {
        //get ready
        final Users mUsers=users.get(position);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        holder.follow_btn.setVisibility(View.VISIBLE);
        holder.username.setText(mUsers.getUserName());
        holder.fullname.setText(mUsers.getFullName());
        //load image
        Glide.with(mcontext).load(mUsers.getImageUri()).into(holder.circleImageView);
        //function to check if following or not
        isFollowing(mUsers.getID(),holder.follow_btn);
        //end
        //this disappearing button if this my profile
             if(mUsers.getID().equals(firebaseUser.getUid())){
            holder.follow_btn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //simple   SharedPreferences Editor to save user id i clicked ,to get through it
                SharedPreferences.Editor editor=mcontext.getSharedPreferences("insta",0).edit();
                editor.putString("userProfile",mUsers.getID());
                editor.apply();

                //this important thing you must remember it , Fragment replacement from one to another in adapter
                //with there is no real activity exits
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container,new ProfileFragment()).commit();
                //end
            }
        });

             // if following button is clickable then do this
        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if button text is equals to follow do this
                if(holder.follow_btn.getText().toString().equals("Follow")){
                    //store in firebase, in child follow then child myID,and then Following and then userId that i
                    // was click in him follow button is true (is following) means i follow these peoples
                FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(firebaseUser.getUid()).child("Following")
                 .child(mUsers.getID()).setValue(true);

                //store in firebase like above but vice versa exchange roles
                 FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(mUsers.getID()).child("Followers")
                     .child(firebaseUser.getUid()).setValue(true);
                  addNotifications(mUsers.getID());

                }else
                {
                    //else remove from firebase in here and there
                    FirebaseDatabase.getInstance().getReference()
                        .child("Follow").child(firebaseUser.getUid()).child("Following")
                        .child(mUsers.getID()).removeValue();
                    //

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow").child(mUsers.getID()).child("Followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    //viewHolder Class it's a normal class that  extends ViewHolder to store data with itself
    class  ViewHolderAdapter extends RecyclerView.ViewHolder{
        TextView username;
        TextView fullname;
        Button follow_btn;
        CircleImageView circleImageView;

        public ViewHolderAdapter(@NonNull View itemView) {
            super(itemView);
            this.username=itemView.findViewById(R.id.username);
            this.fullname=itemView.findViewById(R.id.fullName);
            this.circleImageView=itemView.findViewById(R.id.imageProfile_search);
            this.follow_btn=itemView.findViewById(R.id.Follow_btn);


        }
    }
    //this function for edit button text based on clicking if clicked and data is exists  in firebase ,ok set button text following
    //else set button text follow cause he is not following in firebase
    private void isFollowing(final String uid, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
        .child("Follow").child(firebaseUser.getUid()).child("Following");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(uid).exists()){

                            button.setText("Following");


                        }else {
                            button.setText("Follow");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
    private void addNotifications(String userId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification").child(userId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("postId","");
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("comment","Is Started Following You");
        hashMap.put("isPost",false);
        reference.push().setValue(hashMap);



    }

}

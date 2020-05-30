package samir.com.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Fragments.PostDetails;
import samir.com.instagram.Fragments.ProfileFragment;
import samir.com.instagram.Models.Notification;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

public class NotificationAdapter  extends  RecyclerView.Adapter<NotificationAdapter.ViewHolderAdapter>{
    private Context mcontext;
   private List<Notification> notificationsList;

    public NotificationAdapter(Context mcontext, List<Notification> notificationsList) {
        this.mcontext = mcontext;
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext).inflate(R.layout.notification_item,parent,false);
       return new ViewHolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapter holder, int position) {
        final Notification notification=notificationsList.get(position);
        holder.comment.setText(notification.getComment());
        Log.v("post",String.valueOf(notification.isISPost()));
        if(notification.isISPost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(notification.getPostId(),holder.postImage);
        }
        else {
            holder.postImage.setVisibility(View.GONE);

        }

       getUserInfo(notification.getUserId(),holder.userName,holder.profileImage);
        //getPostImage(notification.getPostId(),holder.postImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isISPost()){
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("insta",0).edit();
                    editor.putString("postId",notification.getPostId());
                    editor.apply();
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.main_container
                    ,new PostDetails()).commit();
                }else{
                    SharedPreferences.Editor editor=mcontext.getSharedPreferences("insta",0).edit();
                    editor.putString("userProfile",notification.getUserId());
                    editor.apply();
                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.main_container
                            ,new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    class  ViewHolderAdapter extends RecyclerView.ViewHolder{
     CircleImageView profileImage;
     ImageView postImage;
     TextView userName,comment;
     public ViewHolderAdapter(@NonNull View itemView) {
         super(itemView);
         this.comment=itemView.findViewById(R.id.comment);
         this.profileImage=itemView.findViewById(R.id.profileImage);
         this.postImage=itemView.findViewById(R.id.postImage);
         this.userName=itemView.findViewById(R.id.username);
     }
 }

 private void getUserInfo(String userId, final TextView username, final CircleImageView profile)
 {

     DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
     reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             Users user=dataSnapshot.getValue(Users.class);
             Glide.with(mcontext).load(user.getImageUri()).into(profile);
             username.setText(user.getUserName());

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });


 }
 private void getPostImage(String postId,final ImageView img){
      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  Posts post=dataSnapshot.getValue(Posts.class);
              Glide.with(mcontext).load(post.getImage()).into(img);
          }
          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


 }


}

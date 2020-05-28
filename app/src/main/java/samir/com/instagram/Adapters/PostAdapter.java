package samir.com.instagram.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.ViewHolderPosts>{
    public Context mcontext;
    public List<Posts>  posts;
 private FirebaseUser firebaseUser;
    public PostAdapter(Context mcontext, List<Posts> posts) {
        this.mcontext = mcontext;
        this.posts = posts;
    }
    @NonNull
    @Override
    public ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(mcontext).inflate(R.layout.post_items,parent,false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPosts holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Posts myPost=posts.get(position);
        Glide.with(mcontext).load(myPost.getImage()).into(holder.postImage);
        if(myPost.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(myPost.getDescription());
        }
        getPublisher(holder.profileImage,holder.userName,holder.publisher,myPost.getPublisher());
        //likes
        isLikes(holder.like,myPost.getPostId());
        NoLikes(holder.likes,myPost.getPostId());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("Like")){
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Likes")
                            .child(myPost.getPostId()).child(firebaseUser.getUid());
                    reference.setValue(true);

                }else{
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Likes")
                            .child(myPost.getPostId()).child(firebaseUser.getUid());
                    reference.removeValue();
                }
            }
        });



        //end likes

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolderPosts extends RecyclerView.ViewHolder{

         public ImageView like,save,comment,postImage;
         public TextView userName,likes,comments,publisher,description;
         public CircleImageView profileImage;
      public ViewHolderPosts(@NonNull View itemView) {
          super(itemView);
          this.profileImage=itemView.findViewById(R.id.home_profile_image);
          this.userName=itemView.findViewById(R.id.home_userName);
          this.like=itemView.findViewById(R.id.post_like);
          this.save=itemView.findViewById(R.id.post_save);
          this.comment=itemView.findViewById(R.id.post_comment);
          this.likes=itemView.findViewById(R.id.post_likes);
          this.comments=itemView.findViewById(R.id.post_comments);
          this.publisher=itemView.findViewById(R.id.post_publisher);
          this.description=itemView.findViewById(R.id.post_description);
          this.postImage=itemView.findViewById(R.id.post_image);

      }
  }



  //check user is checked or not ,if checked set image liked or set image like
  //through post id and Reference
private void isLikes(final ImageView imageView, String postId){
        final FirebaseUser firebaseUse=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if(dataSnapshot.child(firebaseUse.getUid()).exists()){
               imageView.setImageResource(R.drawable.ic_liked);
               imageView.setTag("Liked");
           }else{
               imageView.setImageResource(R.drawable.ic_like);
               imageView.setTag("Like");

           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

}

//this function get number of likes that do it in each post
private void NoLikes(final TextView likes, String pId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Likes").child(pId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" Likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

}


//this function to get publisher info and set image and user name and publisher text
  private void getPublisher(final CircleImageView profile, final TextView username, final TextView publisher, String uid){

      DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(uid);
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Users user=dataSnapshot.getValue(Users.class);
              Glide.with(mcontext).load(user.getImageUri()).into(profile);
              username.setText(user.getFullName());
              publisher.setText(user.getUserName());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });



  }

}

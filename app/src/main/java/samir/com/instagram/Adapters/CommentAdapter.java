package samir.com.instagram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import samir.com.instagram.MainInsta;
import samir.com.instagram.Models.Comments;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolderAdapter> {

Context mcontext;
FirebaseUser firebaseUser;
List<Comments> comments;

    public CommentAdapter(Context mcontext, List<Comments> comments) {
        this.mcontext = mcontext;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.comment_items,parent,false);
        return new ViewHolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapter holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comments comm=comments.get(position);
        holder.comment.setText(comm.getComment());
        //user info
        getUserInfo(comm.getPublisher(),holder.profileimage,holder.username);
        //end info
        //
        holder.profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, MainInsta.class);
                intent.putExtra("publisherId",comm.getPublisher());
                mcontext.startActivity(intent);
            }
        });

        //

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolderAdapter extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView comment,username;
       public ViewHolderAdapter(@NonNull View itemView) {
           super(itemView);
           this.profileimage=itemView.findViewById(R.id.item_profile_image);
           this.comment=itemView.findViewById(R.id.comment);
           this.username=itemView.findViewById(R.id.username);

       }
   }
   private  void getUserInfo(String publisher, final CircleImageView profile, final TextView username){

       DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisher);
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Users user=dataSnapshot.getValue(Users.class);
               Glide .with(mcontext).load(user.getImageUri()).into(profile);
               username.setText(user.getUserName());
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

   }
}

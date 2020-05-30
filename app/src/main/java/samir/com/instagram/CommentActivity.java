package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Adapters.CommentAdapter;
import samir.com.instagram.Models.Comments;
import samir.com.instagram.Models.Users;

public class CommentActivity extends AppCompatActivity {
EditText commentContent;
TextView post;
FirebaseUser firebaseUser;
String postID;
CircleImageView profileImage;
String publisherId;
RecyclerView recyclerView;
CommentAdapter commentAdapter;
List<Comments> commentList;

ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        //inflate elements
        commentContent=findViewById(R.id.comment_addComment);
        post=findViewById(R.id.comment_post);
        profileImage=findViewById(R.id.comment_profileImage);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=findViewById(R.id.comment_rv);
        progressBar=findViewById(R.id.pb);
        //end
        // recyclerView setting
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);
        //
        Intent intent=getIntent();
        postID=intent.getStringExtra("postId");
        publisherId=intent.getStringExtra("publisherId");
        //check comment empty
        post.setVisibility(View.GONE);
        commentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    post.setVisibility(View.GONE);
                }else{
                    post.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
     getImage();
     readComments();
    }

    //send comment to fire base child post id //

    private void addComment() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments")
                .child(postID);
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("Comment",commentContent.getText().toString());
        hashMap.put("Publisher",firebaseUser.getUid());
        reference.push().setValue(hashMap);
        addNotifications();
        commentContent.setText("");

    }

    //read my profile image
    private void getImage(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user=dataSnapshot.getValue(Users.class);
                Glide.with(CommentActivity.this).load(user.getImageUri()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //read all post comments based on child post id
   private void readComments(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comments  comment=snapshot.getValue(Comments.class);
                    commentList.add(comment);

                }
                commentAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }
    private void addNotifications(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification").child(publisherId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("postId",postID);
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("comment","Commented: "+commentContent.getText().toString());
        hashMap.put("isPost",true);
        reference.push().setValue(hashMap);



    }

}

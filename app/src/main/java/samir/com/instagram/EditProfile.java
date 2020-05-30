package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import samir.com.instagram.Models.Users;

public class EditProfile extends AppCompatActivity {

    ImageView close;
    CircleImageView profileimg;
    TextView save,changePhoto;
    MaterialEditText username,fullname,bio,gender;
FirebaseUser firebaseUser;
private Uri imageUri;
private StorageTask uploadTask;
private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //inflate elements
        close=findViewById(R.id.close);
        profileimg=findViewById(R.id.profileImage);
        save=findViewById(R.id.save);
        changePhoto=findViewById(R.id.change_text);
        username=findViewById(R.id.userName);
        fullname=findViewById(R.id.fullName);
        bio=findViewById(R.id.bio);
        gender=findViewById(R.id.gender);
        //
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference("uploaded");
        //getuserinfo
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user=dataSnapshot.getValue(Users.class);
                fullname.setText(user.getFullName());
                username.setText(user.getUserName());
                bio.setText(user.getBio());
                gender.setText(user.getGender());
                Glide.with(getApplicationContext()).load(user.getImageUri()).into(profileimg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
           //close
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
          //change photo
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });
        //
        //change profile image on click
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });
        //end

        //save changes
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChanges(fullname.getText().toString(),
                        username.getText().toString(),
                        bio.getText().toString(),
                        gender.getText().toString());
            }
        });



        //








    }

    private void uploadChanges(String full, String name, String bi,String g) {

      DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object>hashMap=new HashMap<>();

        hashMap.put("FullName",full);
        hashMap.put("userName",name);
        hashMap.put("Bio",bi);
        hashMap.put("gender",g);
        reference.updateChildren(hashMap);
    }
    private String getFileExtenation(Uri  uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap  mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){

            CropImage.ActivityResult activityResult= CropImage.getActivityResult(data);
            imageUri=activityResult.getUri();
            upLoadImage();
        }else{
            Toast.makeText(EditProfile.this,"Something gone wrong!",Toast.LENGTH_LONG).show();
        }


    }

    private  void upLoadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if(imageUri!=null){
            final StorageReference fileSto=storageRef.child(System.currentTimeMillis()+"."+getFileExtenation(imageUri));

            uploadTask=fileSto.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                     if(!task.isSuccessful()){
                         throw task.getException();
                     }
                     return  fileSto.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri down=task.getResult();
                        String uri=down.toString();
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageUri",uri);
                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(EditProfile.this,"Failed",Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this,e.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }else {
            Toast.makeText(EditProfile.this,"There is No Image selected",Toast.LENGTH_LONG).show();
        }









    }
}

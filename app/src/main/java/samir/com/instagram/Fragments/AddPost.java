package samir.com.instagram.Fragments;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import samir.com.instagram.MainInsta;
import samir.com.instagram.R;

public class AddPost extends AppCompatActivity {
Uri imagUri;
String myUri;
TextView post;
EditText postdesc;
ImageView close,imagAdded;
StorageReference storageReference;
StorageTask storageTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        post=findViewById(R.id.post);
        postdesc=findViewById(R.id.post_description);
        close=findViewById(R.id.close);
        imagAdded=findViewById(R.id.image_added);

        storageReference= FirebaseStorage.getInstance().getReference("Posts");
        //if close button clicked go to mainInsta activity
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPost.this, MainInsta.class));
                finish();
            }
        });
        //end

        //if post text is checked
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //crop activity pop up once you call this activity
        CropImage.activity().setAspectRatio(1,1).start(AddPost.this);

    }

    //this method cause of get image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if all good
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){

            CropImage.ActivityResult activityResult=CropImage.getActivityResult(data);
            imagUri=activityResult.getUri();
            imagAdded.setImageURI(imagUri);
        }else{
            Toast.makeText(getBaseContext(),"Something Gone Wrong!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(AddPost.this,MainInsta.class));
            finish();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();

        if(imagUri!=null){
          final StorageReference storageFile=storageReference.child(System.currentTimeMillis()+"."+
                  getFileExtenation(imagUri));
          storageTask=storageFile.putFile(imagUri);
          storageTask.continueWithTask(new Continuation() {
              @Override
              public Object then(@NonNull Task task) throws Exception {
                  if(!task.isSuccessful()){

                      throw task.getException();
                  }
                  return storageFile.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                  if(task.isSuccessful()){
                      Uri download=task.getResult();
                      myUri=download.toString();
                      DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
                      String postId=reference.push().getKey();

                      HashMap<String,Object> hashMap=new HashMap<>();
                      hashMap.put("postId",postId);
                      hashMap.put("image",myUri);
                      hashMap.put("description",postdesc.getText().toString());
                      hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                      reference.child(postId).setValue(hashMap);
                      progressDialog.dismiss();
                      startActivity(new Intent(AddPost.this,MainInsta.class));
                      finish();
                  }else{
                      Toast.makeText(getBaseContext(),"Failed!",Toast.LENGTH_LONG).show();
                  }
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
              }
          });
        }else{
            Toast.makeText(getBaseContext(),"No image selected",Toast.LENGTH_LONG).show();
        }

    }
    //this function get file type
    private String getFileExtenation(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap map=MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}

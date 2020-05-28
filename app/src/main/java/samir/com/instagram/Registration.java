package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
EditText fullname,email,password,gender,userbio;
Button register;
    ProgressBar progressBar;
    FirebaseAuth  auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //inflate elment
        fullname=findViewById(R.id.reg_fullname);
        email=findViewById(R.id.reg_Email);
        password=findViewById(R.id.reg_password);
        gender=findViewById(R.id.reg_gender);
        register=findViewById(R.id.reg_btn);
        progressBar=findViewById(R.id.reg_pb);
        userbio=findViewById(R.id.reg_bio);
        //
        //set appbar title
        getSupportActionBar().setTitle("Registration");
        //firebase
        auth=FirebaseAuth.getInstance();
        //
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name=fullname.getText().toString();
                String useremail=email.getText().toString();
                String usergender=gender.getText().toString();
                String userpass=password.getText().toString();
                String bio=userbio.getText().toString();
                if(TextUtils.isEmpty(name)==true&&TextUtils.isEmpty(useremail)==true&&
                        TextUtils.isEmpty(usergender)==true&&TextUtils.isEmpty(userpass)==true

                &&TextUtils.isEmpty(bio)==true){
                    Toast.makeText(getBaseContext(),"All fields required",Toast.LENGTH_LONG).show();

                }else if(useremail.contains("@")==false){
                    Toast.makeText(getBaseContext(),"Enter  valid email",Toast.LENGTH_LONG).show();

                }else if(password.length()<6){
                    Toast.makeText(getBaseContext()," password must be greater than 6 digit",Toast.LENGTH_LONG).show();

                }else{
                    doRegister(name,useremail,userpass,usergender, bio);

                }


            }
        });

    }
private void doRegister(final String name, String email, String pass, final String gender, final String bio){

    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if(task.isSuccessful()){
                FirebaseUser firebaseUser=auth.getCurrentUser();
                reference=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
                HashMap<String,Object> map=new HashMap<>();
                map.put("userName",name);
                map.put("gender",gender);
                map.put("ID",firebaseUser.getUid());
                map.put("imageUri","gs://instagram-80795.appspot.com/chat.png");
                map.put("FullName",name.toLowerCase());
                map.put("Bio",bio);
                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getBaseContext(),"Login now",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                             Intent intent=new Intent(Registration.this,Login.class);
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(intent);

                        }
                    }
                });

            }else{
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(),"Try again!",Toast.LENGTH_LONG).show();
            }

        }
    });


}

}

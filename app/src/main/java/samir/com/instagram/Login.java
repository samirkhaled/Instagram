package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
TextView register;
EditText password,email;
Button login;
ProgressBar progressBar;
FirebaseAuth auth;
FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //inflate
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.bt_login);
        progressBar=findViewById(R.id.pb);
        //
        //register
       getSupportActionBar().setTitle("Login");
       register=findViewById(R.id.login_register);
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Login.this,Registration.class));
           }
       });
       //end register
        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        //start login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String uemail=email.getText().toString();
                String upass=password.getText().toString();
                if(TextUtils.isEmpty(uemail)==false&&TextUtils.isEmpty(upass)==false){
                    doLogin(uemail,upass);


                }else if(email.getText().toString().contains("@")==false){
                    Toast.makeText(getBaseContext(),"Enter valid email",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }else {
                    Toast.makeText(getBaseContext(),"All fields required",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }


            }
        });

        //end login


    }

    @Override
    protected void onStart() {
        super.onStart();
      if(firebaseUser!=null){
          startActivity(new Intent(Login.this,MainInsta.class));

      }
    }

    void doLogin(String Email, String password){
       auth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){

                   Toast.makeText(getBaseContext(),"successfully login",Toast.LENGTH_LONG).show();
                   progressBar.setVisibility(View.GONE);
                   startActivity(new Intent(Login.this,MainInsta.class));
               }else{
                   Toast.makeText(getBaseContext(),"Incorrect Email or password ",Toast.LENGTH_LONG).show();
                   progressBar.setVisibility(View.GONE);
               }
           }
       });


   }



}

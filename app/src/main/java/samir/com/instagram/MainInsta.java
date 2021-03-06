package samir.com.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import samir.com.instagram.Fragments.AddPost;
import samir.com.instagram.Fragments.FavoriteFragment;
import samir.com.instagram.Fragments.HomeFragment;
import samir.com.instagram.Fragments.NotificationFram;
import samir.com.instagram.Fragments.ProfileFragment;
import samir.com.instagram.Fragments.SearchFragment;

public class MainInsta extends AppCompatActivity {
  Fragment selectedFragment;
  BottomNavigationView bottomNavigationView;
FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_insta);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        bottomNavigationView.setItemIconTintList(null);
       //check if there a target

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
      Bundle intent=getIntent().getExtras();
       if(intent!=null){
           String publisher=intent.getString("publisherId");
           SharedPreferences.Editor editor=getSharedPreferences("insta",0).edit();
           editor.putString("userProfile",publisher);
           editor.apply();

           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.main_container,new ProfileFragment())
                   .commit();
       }else{
           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.main_container,new HomeFragment())
                   .commit();
       }

       //



    }


private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                     switch (item.getItemId()){

                         case R.id.nav_home:
                             selectedFragment=new HomeFragment();
                             break;
                         case R.id.nav_profile:
                             SharedPreferences.Editor editor=getSharedPreferences("insta",0).edit();
                             editor.putString("userProfile",firebaseUser.getUid());
                             editor.apply();
                             selectedFragment=new ProfileFragment();
                             break;
                         case R.id.nav_add:
                             Intent intent=new Intent(MainInsta.this, AddPost.class);
                          //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(intent);
                             break;
                         case R.id.nav_heart:
                             selectedFragment=new NotificationFram();
                             break;
                         case R.id.nav_search:
                             selectedFragment=new SearchFragment();
                             break;
                     }
                     if(selectedFragment!=null){

                         getSupportFragmentManager().beginTransaction()
                                 .replace(R.id.main_container,selectedFragment)
                         .commit();
                     }

                return true;
            }
        };

}

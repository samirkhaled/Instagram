package samir.com.instagram.Fragments;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import samir.com.instagram.Adapters.UsersAdapter;
import samir.com.instagram.Models.Users;
import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {

    }
    RecyclerView recyclerView;
    EditText search;
    UsersAdapter usersAdapter;
    List<Users> mUsers;
    LinearLayout linearLoading;
    TextView textloading;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View view =inflater.inflate(R.layout.fragment_search, container, false);

     recyclerView=view.findViewById(R.id.search_rv);
     search=view.findViewById(R.id.search_edit_text);
     textloading=view.findViewById(R.id.text_loading);
        progressBar=view.findViewById(R.id.pb);
        linearLoading=view.findViewById(R.id.loading);
     recyclerView.setHasFixedSize(true);
     recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
     mUsers=new ArrayList<>();
     usersAdapter=new UsersAdapter(mUsers,getContext());
     recyclerView.setAdapter(usersAdapter);

     readUsers();

     //this is a TextWatcher  Listener for text change all time ,we interested to on textChange
     search.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {

             searchUsers(s.toString());
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
     });


     return view;
    }

    //this function to get userSearch
    private void searchUsers(String text){
        textloading.setVisibility(View.GONE);
       recyclerView.setVisibility(View.GONE);
        //this query get userdata ,first orderbychild searchname and thent if start with
        // and end with text+"/uf8ff" mean  Java unicode Escape character.
        Query query= FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("userName").
                startAt(text).endAt(text+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Users users = snapshot.getValue(Users.class);
                        mUsers.add(users);

                    }
                    usersAdapter.notifyDataSetChanged();
                    textloading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLoading.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                }else{
                    textloading.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    linearLoading.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    textloading.setText("No such user");
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                textloading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                linearLoading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //read all users ,
    private void readUsers(){
        textloading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Users users=snapshot.getValue(Users.class);
                    mUsers.add(users);
                }
                usersAdapter.notifyDataSetChanged();
                textloading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                linearLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                textloading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                linearLoading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


    }
}

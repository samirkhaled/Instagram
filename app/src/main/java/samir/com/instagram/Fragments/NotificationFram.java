package samir.com.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import samir.com.instagram.Adapters.NotificationAdapter;
import samir.com.instagram.Models.Notification;
import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFram extends Fragment {

    public NotificationFram() {
        // Required empty public constructor
    }

    RecyclerView notRv;
    NotificationAdapter notificationAdapter;
    List<Notification> notifications;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification_fram, container, false);
        notRv=view.findViewById(R.id.rv);
        notRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        notRv.setLayoutManager(linearLayoutManager);
        notifications=new ArrayList<>();
        notificationAdapter =new NotificationAdapter(getContext(),notifications);
        notRv.setAdapter(notificationAdapter);
        readNotification();
        return view;
    }
    private void readNotification(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for(DataSnapshot  snapshot:dataSnapshot.getChildren()){
                    Notification notification=snapshot.getValue(Notification.class);
                    notifications.add(notification);
                }
                Collections.reverse(notifications);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










    }
}

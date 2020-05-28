package samir.com.instagram.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import samir.com.instagram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    public FavoriteFragment() {
        // Required empty public constructor
    }
ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_favorite, container, false);

        imageView=view.findViewById(R.id.images);
        String uri="https://firebasestorage.googleapis.com/v0/b/instagram-80795.appspot.com/o/Posts%2F1590692146674.null?alt=media&token=8237c9db-f1ea-42cd-9d5a-ad78369af8ee";
        Glide.with(getContext()).load(uri).into(imageView);


        return view;
    }
}

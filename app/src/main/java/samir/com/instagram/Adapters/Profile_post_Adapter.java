package samir.com.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import samir.com.instagram.Fragments.PostDetails;
import samir.com.instagram.Models.Posts;
import samir.com.instagram.R;

public class Profile_post_Adapter  extends RecyclerView.Adapter<Profile_post_Adapter.ViewHolderAdapter>{
    private Context mcontext;
    private List<Posts> posts;

    public Profile_post_Adapter(Context mcontext, List<Posts> posts) {
        this.mcontext = mcontext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext).inflate(R.layout.profile_post_items,parent,false);

        return new ViewHolderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapter holder, int position) {
        final Posts post=posts.get(position);
        Glide.with(mcontext).load(post.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferences=mcontext.getSharedPreferences("insta",0).edit();
                preferences.putString("postId",post.getPostId());
                preferences.apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new PostDetails()).commit();
            }
        });



    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolderAdapter extends RecyclerView.ViewHolder{
ImageView image;
     public ViewHolderAdapter(@NonNull View itemView) {
         super(itemView);
         this.image=itemView.findViewById(R.id.post_image);
     }
 }

}

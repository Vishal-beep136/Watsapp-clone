package kaitka.vishal.meeta.zoker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kaitka.vishal.meeta.zoker.Activitys.MainActivity;
import kaitka.vishal.meeta.zoker.Modells.Status;
import kaitka.vishal.meeta.zoker.Modells.User;
import kaitka.vishal.meeta.zoker.Modells.UserStatus;
import kaitka.vishal.meeta.zoker.R;
import kaitka.vishal.meeta.zoker.databinding.ItemSentBinding;
import kaitka.vishal.meeta.zoker.databinding.ItemStatusBinding;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() -1);

        Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.imageSt);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // MyStory's ArrayList
                        .setStoryDuration(9000) // Optional, default is 2000 Millis
                        .setTitleText(userStatus.getName())
                        .setSubtitleText("")
                        .setTitleLogoUrl(userStatus.getProfileImage())
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                // your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                // your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before show method
                        .show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder {
        ItemStatusBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }

}

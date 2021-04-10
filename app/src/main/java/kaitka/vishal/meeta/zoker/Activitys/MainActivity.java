package kaitka.vishal.meeta.zoker.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import kaitka.vishal.meeta.zoker.Adapters.TopStatusAdapter;
import kaitka.vishal.meeta.zoker.Modells.Message;
import kaitka.vishal.meeta.zoker.Modells.Status;
import kaitka.vishal.meeta.zoker.Modells.UserStatus;
import kaitka.vishal.meeta.zoker.R;
import kaitka.vishal.meeta.zoker.Modells.User;
import kaitka.vishal.meeta.zoker.Adapters.UsersAdapter;
import kaitka.vishal.meeta.zoker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    FirebaseDatabase database;

    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;
    ChatActivity chatActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();
        chatActivity = new ChatActivity();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this, userStatuses);

//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(layoutManager);
        binding.statusList.setAdapter(statusAdapter);

        binding.recyclerView.setAdapter(usersAdapter);

        binding.recyclerView.showShimmerAdapter();
        binding.statusList.showShimmerAdapter();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                binding.recyclerView.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);

                    }
                    binding.statusList.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.status:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 75);
                    break;
//
//                case R.id.camera:
//                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intentCamera, pic_id);
//                    break;
            }
            return false;
        });

    }//Oncreate

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId)
                .setValue("ON");
    }


    @Override
    protected void onPause() {
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId)
                .setValue("OFF");
        super.onPause();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == pic_id){
//            if (data != null){
//                dialog.show();
////                Bitmap photo = (Bitmap)data.getExtras().get("data");
//                if (data != null){
//                    if (data.getData() != null){
//                        Uri selectedImage = data.getData();
//                        Calendar calendar = Calendar.getInstance();
//                        StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
//                        dialog.show();
//                        reference.putFile(selectedImage).addOnCompleteListener(task -> {
//                            dialog.dismiss();
//                            if (task.isSuccessful()){
//                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                    String filePath = uri.toString();
////                                    String messageTxt = binding.messageBox.getText().toString();
//                                    String messageTxt = chatActivity.binding.messageBox.getText().toString();
//
//                                    Date date = new Date();
//
//                                    Message message = new Message(messageTxt, senderUid, date.getTime());
//                                    message.setMessage("photo");
//                                    message.setImageUrl(filePath);
////                                    binding.messageBox.setText("");
//                                    chatActivity.binding.messageBox.setText("");
//
//                                    String randomKey = database.getReference().push().getKey();
//
//                                    HashMap<String, Object> lastMsgObj = new HashMap<>();
//                                    lastMsgObj.put("lastMsg", message.getMessage());
//                                    lastMsgObj.put("lastMsgTime", date.getTime());
//
//                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
//                                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
//
//
//                                    database.getReference().child("chats")
//                                            .child(senderRoom)
//                                            .child("messages")
//                                            .child(randomKey)
//                                            .setValue(message).addOnSuccessListener(aVoid -> database.getReference().child("chats")
//                                            .child(receiverRoom)
//                                            .child("messages")
//                                            .child(randomKey)
//                                            .setValue(message).addOnSuccessListener(aVoid1 -> {
//
//
//                                            }));
//
//                                });
//                            }
//
//                        });
//
//                    }
//                }
//
//                if (data.getData() != null){
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    Date date = new Date();
//                    StorageReference reference = storage.getReference().child("status")
//                            .child(date.getTime() + "");
//
//                    reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (task.isSuccessful()){
//                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        UserStatus userStatus = new UserStatus();
//                                        userStatus.setName(user.getName());
//                                        userStatus.setProfileImage(user.getProfileImage());
//                                        userStatus.setLastUpdated(date.getTime());
//
//                                        HashMap<String, Object> obj = new HashMap<>();
//                                        obj.put("name", userStatus.getName());
//                                        obj.put("profileImage", userStatus.getProfileImage());
//                                        obj.put("lastUpdated", userStatus.getLastUpdated());
//
//                                        String imageUrl = uri.toString();
//                                        Status status = new Status(imageUrl, userStatus.getLastUpdated());
//
//                                        database.getReference()
//                                                .child("stories")
//                                                .child(FirebaseAuth.getInstance().getUid())
//                                                .updateChildren(obj);
//
//                                        database.getReference()
//                                                .child("stories")
//                                                .child(FirebaseAuth.getInstance().getUid())
//                                                .child("statuses")
//                                                .push().setValue(status);
//
//                                        dialog.dismiss();
//
//
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//                }
//            }
//        }
//
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.group:
                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
                Toast.makeText(this, "welcome to friends zone!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.setting:
                Toast.makeText(this, "Setting option clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.search:
                Toast.makeText(this, "Search btn clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
}
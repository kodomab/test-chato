/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.shadowbez.chato;

import android.app.ActionBar;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getName();

    private TextView usernameTextView;
    private Button chatButton;
    private EditText chatEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //    private DatabaseReference databaseReference;
    private FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            user = currentUser;
            usernameTextView.setText(user.getEmail());
//            databaseReference = FirebaseDatabase.getInstance().getReference();
            db = FirebaseFirestore.getInstance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_log_out:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void chatWithPersonOnClick(View view) {
//        if (!chatEditText.getText().toString().isEmpty()) {
//            Query query = databaseReference.child(DatabaseConstants.USERS).orderByChild("email").equalTo(chatEditText.getText().toString().toLowerCase());
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                    dataSnapshot.getChildren().iterator().next().child("databaseId").getValue().toString();
//                    //TODO NEED TO ONLY GET ONE ELEMENT
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        final String partnerId = data.child("id").getValue().toString();
//
//                        Query query = databaseReference.child(DatabaseConstants.USERS).child(user.getUid()).child(DatabaseConstants.USER_CHATS);
//                        query.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.getValue() != null) {
//                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                        if (data.getKey().equals(partnerId)) {
//                                            Log.i("wololo", "start activity without creating a new room");
//                                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
//                                            i.putExtra("chatRoomId", data.getValue().toString());
//                                            startActivity(i);
//                                            break;
//                                        }
//                                        Log.i("wololo", "start activity and create a new room 1");
//                                        String chatRoomId = createChatRoom(partnerId);
//                                        addChatRoomAndFriend(partnerId, chatRoomId);
//                                        Intent i = new Intent(MainActivity.this, ChatActivity.class);
//                                        i.putExtra("chatRoomId", chatRoomId);
//                                        startActivity(i);
//                                    }
//                                } else {
//                                    Log.i("wololo", "start activity and create a new room 2");
//                                    String chatRoomId = createChatRoom(partnerId);
//                                    addChatRoomAndFriend(partnerId, chatRoomId);
//                                    Intent i = new Intent(MainActivity.this, ChatActivity.class);
//                                    i.putExtra("chatRoomId", chatRoomId);
//                                    startActivity(i);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Log.i("wololo", "ON CANCELLED");
//                            }
//                        });
//                        Log.i("wololo", "OUTSIDE");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }

        if (!chatEditText.getText().toString().isEmpty()) {
            db.collection(DatabaseConstants.USERS).whereEqualTo("email", chatEditText.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                User partner = document.toObject(User.class);
                                partner.setId((String) document.getData().get("id"));
                                partner.setEmail((String) document.get("email"));
                                partner.setUserChats((Map<String, String>) document.get("userChats"));
                                String partnerId = partner.getId();
                                if (partner.getUserChats().isEmpty()) {
                                    Log.i("wololo", "start activity and create a new room 1");
                                    String chatRoomId = createChatRoom(partnerId);
                                    addChatRoomAndFriend(partnerId, chatRoomId);
                                    Intent i = new Intent(MainActivity.this, ChatActivity.class);
                                    i.putExtra("chatRoomId", chatRoomId);
                                    startActivity(i);
                                } else {
                                    for (String key : partner.getUserChats().keySet()) {
                                        if (key.equals(user.getUid())) {
                                            Log.i("wololo", "start activity without creating a new room");
                                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                                            i.putExtra("chatRoomId", partner.getUserChats().get(key));
                                            startActivity(i);
                                            break;
                                        } else {
                                            Log.i("wololo", "start activity and create a new room 2");
                                            String chatRoomId = createChatRoom(partnerId);
                                            addChatRoomAndFriend(partnerId, chatRoomId);
                                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                                            i.putExtra("chatRoomId", chatRoomId);
                                            startActivity(i);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "user " + chatEditText.getText().toString() + " not found 2",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "operation not successful - 2",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        initGUI();
    }

    private void initGUI() {
        usernameTextView = findViewById(R.id.username);
        chatEditText = findViewById(R.id.enter_name_chat);
        chatButton = findViewById(R.id.chat_button);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private String createChatRoom(String partnerId) {
//        DatabaseReference chatRooms = databaseReference.child(DatabaseConstants.CHAT_ROOMS);
//        DatabaseReference newChatRoom = chatRooms.push();
//        String newChatRoomId = newChatRoom.getKey();
//        newChatRoom.setValue(new ChatRoom(newChatRoomId, user.getUid(), partnerId));
        DocumentReference newChatRoom = db.collection(DatabaseConstants.CHAT_ROOMS).document();
        String newChatRoomId = newChatRoom.getId();
        newChatRoom.set(new ChatRoom(newChatRoomId, user.getUid(), partnerId));
        return newChatRoomId;
    }

    private void addChatRoomAndFriend(String partnerId, String chatRoomId) {
//        DatabaseReference myChats = databaseReference.child(DatabaseConstants.USERS).child(user.getUid()).child(DatabaseConstants.USER_CHATS);
//        DatabaseReference partnerChats = databaseReference.child(DatabaseConstants.USERS).child(partnerId).child(DatabaseConstants.USER_CHATS);
//
//        HashMap<String, Object> chat = new HashMap<>();
//        chat.put(partnerId, chatRoomId);
//        HashMap<String, Object> chatPartner = new HashMap<>();
//        chatPartner.put(user.getUid(), chatRoomId);
//
//        myChats.updateChildren(chat);
//        partnerChats.updateChildren(chatPartner);

        DocumentReference myself = db.collection(DatabaseConstants.USERS).document(user.getUid());
        DocumentReference partner= db.collection(DatabaseConstants.USERS).document(partnerId);

        HashMap<String, Object> chat = new HashMap<>();
        chat.put(partnerId, chatRoomId);
        HashMap<String, Object> chatPartner = new HashMap<>();
        chatPartner.put(user.getUid(), chatRoomId);

        myself.update(
                "userChats", chat
        );
        partner.update(
                "userChats", chatPartner
        );
    }
}
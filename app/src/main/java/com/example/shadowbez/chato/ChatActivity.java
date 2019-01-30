package com.example.shadowbez.chato;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.view.ViewCompat.LAYOUT_DIRECTION_LTR;
import static android.support.v4.view.ViewCompat.LAYOUT_DIRECTION_RTL;

public class ChatActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        TextView timeTextView;
        CircleImageView messengerImageView;
        LinearLayout mainLinearLayout;
        LinearLayout secondaryLinearLayout;
        LinearLayout parentLinearLayout;

        public MessageViewHolder(View v) {
            super(v);
            parentLinearLayout = (LinearLayout) itemView.findViewById(R.id.parentLinearLayout);
            mainLinearLayout = (LinearLayout) itemView.findViewById(R.id.mainLinearLayout);
            secondaryLinearLayout = (LinearLayout) itemView.findViewById(R.id.secondaryLinearLayout);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    public static final String TAG = ChatActivity.class.getName();
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;

    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    //    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    //    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;
    private FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder> mFirestoreAdapter;

    private SharedPreferences mSharedPreferences;
    private String mUsername;
    private String mPhotoUrl;
    private String chatRoomId;

    @Override
    public void onPause() {
//        mFirebaseAdapter.stopListening();
        mFirestoreAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mFirebaseAdapter.startListening();
        mFirestoreAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        chatRoomId = getIntent().getExtras().getString("chatRoomId");
        initGUI();
        initFIrebase();
        initUser();

        readMessages();
    }

    public void sendMessagesOnClick(View view) {
        ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(), mUsername, mPhotoUrl);
//        mFirebaseDatabaseReference.child(DatabaseConstants.CHAT_ROOMS).child(chatRoomId).child(DatabaseConstants.MESSAGES)
//                .push().setValue(chatMessage);
        db.collection(DatabaseConstants.CHAT_ROOMS).document(chatRoomId).collection(DatabaseConstants.MESSAGES).add(chatMessage);
        mMessageEditText.setText("");
    }

    private void initGUI() {
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
    }

    private void initFIrebase() {
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initUser() {
        mUsername = firebaseUser.getEmail();
    }

    private void readMessages() {
//        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
//            @Override
//            public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
//                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
//                if (chatMessage != null) {
//                    chatMessage.setId(dataSnapshot.getKey());
//                }
//                return chatMessage;
//            }
//        };

//        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(DatabaseConstants.CHAT_ROOMS).child(chatRoomId).child(DatabaseConstants.MESSAGES);
//        FirebaseRecyclerOptions<ChatMessage> options =
//                new FirebaseRecyclerOptions.Builder<ChatMessage>()
//                        .setQuery(messagesRef, parser)
//                        .build();

        CollectionReference collectionReference = db.collection(DatabaseConstants.CHAT_ROOMS).document(chatRoomId).collection(DatabaseConstants.MESSAGES);
        Query query = collectionReference.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ChatMessage> options =
                new FirestoreRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();


//        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
//            @Override
//            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
//            }
//
//            @Override
//            protected void onBindViewHolder(final MessageViewHolder viewHolder,
//                                            int position,
//                                            ChatMessage friendlyMessage) {
//                if (friendlyMessage.getText() != null) {
//                    viewHolder.messageTextView.setText(friendlyMessage.getText());
//                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
//                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
//                } else if (friendlyMessage.getImageUrl() != null) {
//                    String imageUrl = friendlyMessage.getImageUrl();
//                    if (imageUrl.startsWith("gs://")) {
//                        StorageReference storageReference = FirebaseStorage.getInstance()
//                                .getReferenceFromUrl(imageUrl);
//                    }
//                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
//                    viewHolder.messageTextView.setVisibility(TextView.GONE);
//                }
//
//
//                viewHolder.messengerTextView.setText(friendlyMessage.getName());
//                if (friendlyMessage.getPhotoUrl() == null) {
//                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
//                            R.drawable.abc_ab_share_pack_mtrl_alpha));
//                }
//            }
//        };

        mFirestoreAdapter = new FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            ChatMessage chatMessage) {
                if (chatMessage.getName().equals(mUsername)) {
                    viewHolder.parentLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    viewHolder.mainLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    viewHolder.secondaryLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    viewHolder.parentLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    viewHolder.mainLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    viewHolder.secondaryLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
                if (chatMessage.getText() != null) {
                    Date time = chatMessage.getTimestamp();
                    viewHolder.messageTextView.setText(chatMessage.getText());
                    viewHolder.timeTextView.setText(time.getHours() + ":" + time.getMinutes() + "  " + time.getDay() + "." + time.getMonth() + "." + time.getYear());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.timeTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                }
//                else if (chatMessage.getImageUrl() != null) {
//                    String imageUrl = chatMessage.getImageUrl();
//                    if (imageUrl.startsWith("gs://")) {
//                        StorageReference storageReference = FirebaseStorage.getInstance()
//                                .getReferenceFromUrl(imageUrl);
//                    }
//                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
//                    viewHolder.messageTextView.setVisibility(TextView.GONE);
//                }


                viewHolder.messengerTextView.setText(chatMessage.getName());
                if (chatMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_launcher_background));
                }
            }
        };


//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendlyMessageCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mMessageRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });

        mFirestoreAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirestoreAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


//        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageRecyclerView.setAdapter(mFirestoreAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt("friendly_msg_length", DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
}

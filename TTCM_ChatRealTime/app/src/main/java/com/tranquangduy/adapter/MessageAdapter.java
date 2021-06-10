package com.tranquangduy.adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tranquangduy.model.Message;
import com.tranquangduy.ttcm_chatrealtime.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context mContext;
    private final List<Message> mMessage;
    private final String imgURL;

    private FirebaseUser firebaseUser;
    private boolean checkDownload = false;
    private boolean checkDelete = false;


    public MessageAdapter(Context mContext, List<Message> mMessage, String imgURL) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = mMessage.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(msg.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());


        if (msg.getType().equals("text")) {
            holder.txtShowMessage.setVisibility(View.VISIBLE);
            holder.txtShowMessage.setText(msg.getMessage());
            holder.txtTime.setVisibility(View.VISIBLE);
            holder.txtTime.setText(dateFormat.format(calendar.getTime()));

            holder.txtTimeImg.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.GONE);
        } else {
            holder.imgMessage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(msg.getMessage()).into(holder.imgMessage);
            holder.txtTimeImg.setVisibility(View.VISIBLE);
            holder.txtTimeImg.setText(dateFormat.format(calendar.getTime()));

            holder.txtShowMessage.setVisibility(View.GONE);
            holder.txtTime.setVisibility(View.GONE);

        }

        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            Glide.with(mContext).load(imgURL).into(holder.imgAvatar);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!checkDelete) {
                    holder.imgDelete.setVisibility(View.VISIBLE);
                    checkDelete = true;
                } else {
                    holder.imgDelete.setVisibility(View.GONE);
                    checkDelete = false;
                }
                return true;
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgDelete.setVisibility(View.GONE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Message message = dataSnapshot.getValue(Message.class);

                            if (msg.getTime() == message.getTime() && msg.getMessage().equals(message.getMessage())) {
                                dataSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });


        holder.imgMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!checkDownload) {
                    holder.imgDownload.setVisibility(View.VISIBLE);
                    checkDownload = true;
                } else {
                    holder.imgDownload.setVisibility(View.GONE);
                    checkDownload = false;
                }


                holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgDownload.setVisibility(View.GONE);
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bitmap = ((BitmapDrawable) holder.imgMessage.getDrawable()).getBitmap();
                                    File filePath = Environment.getExternalStorageDirectory();
                                    File dir = new File(filePath + "/Download");
                                    dir.mkdir();
                                    File file = new File(dir, System.currentTimeMillis() + ".jpg");
                                    OutputStream outputStream;
                                    try {
                                        outputStream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                            Toast.makeText(mContext, "Tải xuống thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Tải thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        });


        holder.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.show_image_view_dialog);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final PhotoView imageViewPost = dialog.findViewById(R.id.showImageView);
                final ImageButton imageButtonBack = dialog.findViewById(R.id.showBtnBack);
                Glide.with(mContext).load(msg.getMessage()).into(imageViewPost);

                imageButtonBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return mMessage.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtShowMessage;
        ImageView imgAvatar, imgMessage, imgDownload, imgDelete;
        TextView txtTime;
        TextView txtTimeImg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgItem_chat_avatar);
            txtShowMessage = itemView.findViewById(R.id.txtItem_chat_messageContent);
            imgMessage = itemView.findViewById(R.id.imgItem_chat_message);
            txtTime = itemView.findViewById(R.id.txtItem_chat_time);
            txtTimeImg = itemView.findViewById(R.id.txtItem_chat_timeImg);
            imgDownload = itemView.findViewById(R.id.imgItem_chat_download);
            imgDelete = itemView.findViewById(R.id.imgItem_chat_delete);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessage.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

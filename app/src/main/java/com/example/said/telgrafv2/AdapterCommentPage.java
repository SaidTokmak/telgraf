package com.example.said.telgrafv2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

//yorumlarin gosterildigi sayfa verileri ile sayfa tasarimini birlestirme islemi(recyclerview uzerinde)
public class AdapterCommentPage extends RecyclerView.Adapter<AdapterCommentPage.cardViewDesignHolder>{
    private Context mContext;
    private List<CommentElement> commentInfos;

    //constructer olarak sayfanin contextini ve verileri ister
    public AdapterCommentPage(Context mContext, List<CommentElement> commentInfos) {
        this.mContext = mContext;
        this.commentInfos = commentInfos;
    }

    //tasarımdaki alanların id leri ile erişilerek tanımlanması
    public class cardViewDesignHolder extends RecyclerView.ViewHolder{
        TextView commentUsername,commentTime,commentMain;
        ImageView commentSendImage;

        public cardViewDesignHolder(View view){
            super(view);
            commentUsername=view.findViewById(R.id.commentSendUsername);
            commentMain=view.findViewById(R.id.commentMain);
            commentTime=view.findViewById(R.id.commentTime);
            commentSendImage=view.findViewById(R.id.commentSendImage);
        }
    }

    @NonNull
    @Override
    public cardViewDesignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //recyclerview icin olusturdugumuz tasarimlarin recyclerviewde gosterilmesi
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_comment,parent,false);
        return new cardViewDesignHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewDesignHolder holder, final int position) {
            //uygun verilerin uygun tasarim elemanlarina yerlestirilmesi
            CommentElement commentElements=commentInfos.get(position);
            holder.commentUsername.setText(commentElements.getCommentUserName());
            holder.commentMain.setText(commentElements.getCommentMain());
            holder.commentTime.setText(DateCalculate.calculate(commentElements.getTime()));

            //picasso kutuphanesi kullanilarak profil fotograflarinin gorunmesi
            String imageUrl=mContext.getResources().getString(R.string.serverUrl)+"/"+commentElements.getProfilePicture();
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(holder.commentSendImage);

            //yorum yapan kisinin ismine tiklama ialemi eklenecek
            holder.commentUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int userId = commentInfos.get(position).getUserId();
                    Intent goProfile=new Intent(mContext,ProfilePage.class);
                    goProfile.putExtra("userId",String.valueOf(userId));
                    mContext.startActivity(goProfile);
                }
            });
        }

    @Override
    public int getItemCount() {
        return commentInfos.size();
    }
}

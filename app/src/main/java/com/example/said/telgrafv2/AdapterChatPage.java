package com.example.said.telgrafv2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

//chat sayfasi verileri ile sayfa tasarimini birlestirme islemi(recyclerview uzerinde)
public class AdapterChatPage extends RecyclerView.Adapter<AdapterChatPage.cardViewDesignHolder> {
    private Context mContext; //adapterin bulundugu sayfa
    private List<ChatElement> chatElements; //yerlestirilecek olan veriler

    //constructer olarak sayfanin contextini ve verileri ister
    public AdapterChatPage(Context mContext, List<ChatElement> chatElements) {
        this.mContext = mContext;
        this.chatElements = chatElements;
    }

    //sayfadaki tasarim elemanlarini tanimladigimiz bolum
    public class cardViewDesignHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView chatCardViewImage;
        LinearLayout layoutChatAll;

        public cardViewDesignHolder(View view){
            super(view);
            username=view.findViewById(R.id.chatCardViewUsername);
            chatCardViewImage=view.findViewById(R.id.chatCardViewImage);
            layoutChatAll=view.findViewById(R.id.layoutChatAll);
        }
    }

    @NonNull
    @Override
    public cardViewDesignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //recyclerview icin olusturdugumuz tasarimlarin recyclerviewde gosterilmesi
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat,parent,false);
        return new AdapterChatPage.cardViewDesignHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cardViewDesignHolder holder, int position) {

        //uygun verilerin uygun tasarim elemanlarina yerlestirilmeleri
        final ChatElement chatElement=chatElements.get(position);
        holder.username.setText(chatElement.getUsername());

        //mesaj atanin resminin gorunmesi
        String imageUrl=mContext.getResources().getString(R.string.serverUrl)+"/"+chatElement.getProfilePicture();
        Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(holder.chatCardViewImage);

        //gelen mesaj kisisinin uzerine tiklanma olayi
        holder.layoutChatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String conversationId=chatElement.getConversationId();
                Intent goChatPage=new Intent(mContext,MessagePage.class);
                goChatPage.putExtra("conversationId",conversationId);
                goChatPage.putExtra("recId",chatElement.getRecId());
                goChatPage.putExtra("username",chatElement.getUsername());
                //message sayfasina username,conversationId ve recipientId nin gonderilmesi
                mContext.startActivity(goChatPage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatElements.size();
    }

}

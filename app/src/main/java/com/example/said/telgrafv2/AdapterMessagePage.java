package com.example.said.telgrafv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//masaj yazma-okuma sayfasi verileri ile sayfa tasarimini birlestirme islemi(recyclerview uzerinde)
public class AdapterMessagePage extends RecyclerView.Adapter<AdapterMessagePage.cardViewDesignHolder> {

    //mesajlarin ayrilmasi icin gerekli(gelen-gonderilen mesaj seklinde)
    public static final int Message_type_left=0;
    public static final int Message_type_right=1;

    private Context mContext;
    private List<MessageElement> messageInfos;

    //constructer olarak sayfanin contextini ve verileri ister
    public AdapterMessagePage(Context mContext, List<MessageElement> messageInfos) {
        this.mContext = mContext;
        this.messageInfos = messageInfos;
    }

    //tasarımdaki alanların id leri ile erişilerek tanımlanması
    public class cardViewDesignHolder extends RecyclerView.ViewHolder{
        TextView messageText,messageTime;

        public cardViewDesignHolder(View view){
            super(view);
            messageText=view.findViewById(R.id.textViewMessage);
            messageTime=view.findViewById(R.id.textViewMessageTime);
        }
    }

    @NonNull
    @Override
    public AdapterMessagePage.cardViewDesignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //eger gonderilen mesaj ise viewType 1 olur(asagidaki fonksiyondan geliyor viewType)
        if(viewType==Message_type_right) {
            //burasi gonderilen mesaj oldugu icin message_recipient tasarimi kullanildi(isimlerndirme yanlis olabilir :))
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recipient, parent, false);
            return new AdapterMessagePage.cardViewDesignHolder(view);
        }else{
            //burasi gelen mesajlarinn oldugu tasarim tanimlama yeri
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sender, parent, false);
            return new AdapterMessagePage.cardViewDesignHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMessagePage.cardViewDesignHolder holder, final int position) {

        MessageElement messageElements=messageInfos.get(position);
        holder.messageText.setText(messageElements.messageText);
        holder.messageTime.setText(DateCalculate.messageHour(messageElements.messageTime));
    }

    @Override
    public int getItemCount() {
        return messageInfos.size();
    }

    @Override
    public int getItemViewType(int position) {

        //global classtan uygulamayi kullanan kisinin id sini aldik
        GlobalClass globalClass=(GlobalClass) mContext.getApplicationContext();
        int userId=globalClass.getId();

        //kullanicin id si ile mesaj gonderenin id si karsilastirilmasi
        if(String.valueOf(userId).equals(messageInfos.get(position).senderId)){
            return Message_type_right;
        }else{
            return Message_type_left;
        }

    }
}

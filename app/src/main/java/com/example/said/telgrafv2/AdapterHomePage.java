package com.example.said.telgrafv2;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//anasayfadaki veriler ile sayfa tasarimini birlestirme islemi(recyclerview uzerinde)
public class AdapterHomePage extends RecyclerView.Adapter<AdapterHomePage.cardViewDesignHolder>{
    private Context mContext;
    private List<ContentElement> contentInfos;

    //constructer olarak sayfanin contextini ve verileri ister
    public AdapterHomePage(Context mContext, List<ContentElement> contentInfos) {
        this.mContext = mContext;
        this.contentInfos = contentInfos;
    }

    //tasarımdaki alanların id leri ile erişilerek tanımlanması
    public class cardViewDesignHolder extends RecyclerView.ViewHolder{
        TextView contentUsername,contentTime,contentMain,emptyHolder,contentTextLike;
        ImageView imageView,likeImage;
        LinearLayout comment;

        public cardViewDesignHolder(View view){
            super(view);
            contentUsername= view.findViewById(R.id.contentUsername);
            contentTime= view.findViewById(R.id.contentTime);
            contentMain= view.findViewById(R.id.contentMain);
            imageView=view.findViewById(R.id.contentImage);
            emptyHolder=view.findViewById(R.id.emptyHolder);
            comment=view.findViewById(R.id.contentComment);
            contentTextLike=view.findViewById(R.id.contentTextLike);
            likeImage=view.findViewById(R.id.contentImageLike);
        }
    }

    @NonNull
    @Override
    public cardViewDesignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //recyclerview icin olusturdugumuz tasarimlarin recyclerviewde gosterilmesi
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_home,parent,false);
        return new cardViewDesignHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewDesignHolder holder, final int position) {

        //eger veriler bos ise islemi suan calismiyor bunu okuyan icin emptyHolder=progressBar
        if(contentInfos.isEmpty()){
            holder.emptyHolder.setVisibility(View.VISIBLE);
        }else {

            ContentElement contentElements=contentInfos.get(position);

            //veritabanindan gelen verilerin uygun yerlere yerlestirilmesi
            holder.contentUsername.setText(contentElements.getUsername());
            holder.contentMain.setText(contentElements.getContentText());
            holder.contentTextLike.setText(contentElements.getLikeCount());

            //picasso kutuphanesi kullanilarak profil fotograflarinin gorunmesi
            String imageUrl=mContext.getResources().getString(R.string.serverUrl)+"/"+contentElements.getImageUrl();
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(holder.imageView);

            //yuklemeden sonra gecen surenin hesaplanmasi
            int time = contentElements.getTime();
            if (time / 60 < 1) {
                holder.contentTime.setText("1 dk");
            } else if (time / 60 <= 59) {
                time = time / 60;
                holder.contentTime.setText(String.valueOf(time) + " dk");
            } else if (time / 3600 <= 23) {
                time = time / 3600;
                holder.contentTime.setText(String.valueOf(time) + " saat");
            } else if (time / (3600 * 24) <= 6) {
                time = time / (3600 * 24);
                holder.contentTime.setText(String.valueOf(time) + " gün");
            } else {
                time = time / (3600 * 24 * 7);
                holder.contentTime.setText(String.valueOf(time) + " hafta");
            }

            //begeni kontrol
            if(contentElements.isUserLike()==true){
                holder.likeImage.setColorFilter(mContext.getResources().getColor(R.color.colorLike), PorterDuff.Mode.SRC_ATOP);
            }
        }

        //kullanici ismine tiklandiginda profile gitme olayi
        holder.contentUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userId = contentInfos.get(position).getUserId();
                Intent goProfile=new Intent(mContext,ProfilePage.class);
                goProfile.putExtra("userId",String.valueOf(userId));
                mContext.startActivity(goProfile);
            }
        });
        //ana contente tiklandiginda postu ayri sayfada yorumlarla birlikte gosterilmesi
        holder.contentMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postId=contentInfos.get(position).getPostId();
                Intent goPostShow=new Intent(mContext,PostShow.class);
                goPostShow.putExtra("postId",String.valueOf(postId));
                mContext.startActivity(goPostShow);
            }
        });
        //yorumlarla birlikte ana contentin ayri sayfada gosterilmesi olayi
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postId=contentInfos.get(position).getPostId();
                Intent goPostShow=new Intent(mContext,PostShow.class);
                goPostShow.putExtra("postId",String.valueOf(postId));
                mContext.startActivity(goPostShow);
            }
        });
        //begen butonuna basma islemi
        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postId=contentInfos.get(position).getPostId();
                //begeni kontrol
                if(contentInfos.get(position).isUserLike()){

                }else {
                    begen(postId);
                    notifyDataSetChanged();
                    holder.likeImage.setColorFilter(mContext.getResources().getColor(R.color.colorLike), PorterDuff.Mode.SRC_ATOP);
                    holder.contentTextLike.setText(contentInfos.get(position).getLikeCount());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentInfos.size();
    }

    public void begen(int post){

        final String token=GlobalClass.getToken();
        //gonderilecek postu json objesine donusturulmesi
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("postId", post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();

        String url="http://telgraf.ankara.edu.tr/dandelion/likes";
        //web servise baglanma gonderim islemi
        StringRequest jsonObjectRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gönderilmesi için jsona header eklenmesi
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError { //web servise istek gönderilmesi için jsona body eklenmesi
                try {
                    //mRequest body yukarıda tanımladığımız json formatındaki gönderilecek veri
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };
        Volley.newRequestQueue(mContext).add(jsonObjectRequest);
    }

}

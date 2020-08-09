package com.example.said.telgrafv2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//postun yorumlarla birlikte ayri bir sayfada gosterilmesi
public class PostShow extends AppCompatActivity {
    static ArrayList<CommentElement> commentElements=new ArrayList<>();
    RecyclerView recyclerView;
    AdapterCommentPage adapterCommentPage;
    GlobalClass globalClass;
    TextView userName,contentTimer,contentMains;
    EditText editTextComment;
    ImageView contentImage;
    Button buttonComment;
    Toolbar toolbar;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_show);

        //layout elemenlarinin tanimlanmasi
        userName=findViewById(R.id.contentUsername);
        contentTimer=findViewById(R.id.contentTime);
        contentMains=findViewById(R.id.contentMain);
        editTextComment=findViewById(R.id.editTextComment);
        buttonComment=findViewById(R.id.buttonCommentSend);
        contentImage=findViewById(R.id.contentImage);

        toolbar=findViewById(R.id.toolbarComment);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Yorumlar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //toolbardaki geri tusu

        //sayfadaya gelen verilerin kayması ve liste şeklinde görüntülenmesi işlemleri
        recyclerView=findViewById(R.id.recyclerViewComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //sayfaya gelen post id sinin cekilmesi
        final String postId=getIntent().getStringExtra("postId");

        //global classtan token cekilmesi
        globalClass=(GlobalClass) getApplicationContext();
        final String token=globalClass.getToken();

        //ana contentin bilgilerini cekebilmek icin gerekli servis adresi
        String contentUrl=getResources().getString(R.string.serverUrl)+"/posts/"+postId;

        //contentin iceriginin cekilmesi icin request
        StringRequest getContentInfos=new StringRequest(Request.Method.GET, contentUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    String contentMain=jsonObject.getString("content");
                    contentMains.setText(contentMain);
                    String time=jsonObject.getString("createdAt");
                    contentTimer.setText(DateCalculate.calculate(time));
                    JSONObject author=jsonObject.getJSONObject("author");
                    userId=author.getString("id");
                    String name=author.getString("name");
                    String surname=author.getString("surname");
                    String username =name+" "+surname;
                    String profilePicture=author.getString("profile_picture");
                    //picasso kutuphanesi kullanilarak profil fotograflarinin gorunmesi
                    String imageUrl=getResources().getString(R.string.serverUrl)+"/"+profilePicture;
                    Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(contentImage);
                    userName.setText(username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(getContentInfos);

        //post id sine gore yorumlarin servisten cekilmesi
        String reqUrl=getResources().getString(R.string.serverUrl)+"/comments?postId="+postId;

        //contente ait yorumlarin cekilmesi icin request
        StringRequest getComment=new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                commentElements.clear();
                try{//gelen json formatındaki verinin parçalanması ve adapter'a gönderilmek üzere arraya alınması
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject getJson=jsonArray.getJSONObject(i);
                        String commentId=getJson.getString("id");
                        String commentMain=getJson.getString("text");
                        String time=getJson.getString("createdAt");
                        JSONObject author=getJson.getJSONObject("author");
                        int userId=author.getInt("id");
                        String username=author.getString("name")+" "+author.getString("surname");
                        String profilePictures=author.getString("profile_picture");
                        CommentElement commentElement=new CommentElement(commentId,userId,username,time,commentMain,profilePictures);
                        commentElements.add(commentElement);
                    }
                    adapterCommentPage.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gönderilmesi için jsona header eklenmesi
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(getComment);

        //recyclerview de gösterilecek tasarımlar için oluşturulan adapterin kullanılması
        adapterCommentPage=new AdapterCommentPage(this,commentElements);

        //recyclerview ile adapterin birleştirilmesi
        recyclerView.setAdapter(adapterCommentPage);

        //kullanici adina tiklandiginda profile gitmesi
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goProfile=new Intent(PostShow.this,ProfilePage.class);
                goProfile.putExtra("userId",String.valueOf(userId));
                startActivity(goProfile);
            }
        });

        //yapilan yorumun gonderilmesi butonu
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editTextComment.getText().toString().isEmpty()){
                    Toast.makeText(PostShow.this, "Yorum Giriniz...", Toast.LENGTH_SHORT).show();
                }else{

                    String sendComment=editTextComment.getText().toString();

                    //gonderilecek postu json objesine donusturulmesi
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("text", sendComment);
                        jsonBody.put("postId",postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String mRequestBody = jsonBody.toString();

                    //yorumun gonderilme adresi
                    String sendUrl=getResources().getString(R.string.serverUrl)+"/comments";

                    //yorumun web servise gonderilmesi ve veri tabanina yuklenmesi
                    StringRequest sentComment=new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("err",error.toString());
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
                    Volley.newRequestQueue(getApplicationContext()).add(sentComment); // istegin volley sirasina eklenmesi
                }
                finish();
                startActivity(getIntent());
            }
        });
    }
    //toolbar ustundeki geri tusu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent geriButonu = new Intent(getApplicationContext(), MainActivity.class);
                // geriButonu intentini çalıştırıyoruz.
                NavUtils.navigateUpTo(this, geriButonu);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

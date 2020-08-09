package com.example.said.telgrafv2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {
    private Toolbar toolbar;
    static ArrayList<ContentElement> contentElements=new ArrayList<>();
    AdapterHomePage adapterHomePage;
    RecyclerView recyclerView;
    TextView profileUserName,bioProfil;
    ImageView imageViewProfil;
    static int userId;
    static String username,conversationId,id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //gelinen sayfadan user id nin cekilmesi
        id=getIntent().getStringExtra("userId");

        GlobalClass globalClass=(GlobalClass) getApplicationContext();
        final String token=globalClass.getToken();

        profileUserName=findViewById(R.id.textViewUserNameProfil);
        bioProfil=findViewById(R.id.textViewBioProfil);
        imageViewProfil=findViewById(R.id.imageViewProfil);

        toolbar=findViewById(R.id.toolbarProfile);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Profil Sayfası");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //toolbardaki geri tusu


        recyclerView=findViewById(R.id.recyclerViewProfil);
        //sayfadaya gelen verilerin kayması ve liste şeklinde görüntülenmesi işlemleri
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //web servis bağlantısı ile post verilerinin çekilmesi
        String url=getResources().getString(R.string.serverUrl)+"/posts?userId="+id+"&%24limit=100"; //login işlemi için haberleşilecek server adresi

        //web servis kullanici bilgilerinin cekilmesi icin url
        String urlUser=getResources().getString(R.string.serverUrl)+"/users/"+id;

        //web servisten kullanii bilgilerinin cekilmesi icin istek gonderilmesi
        StringRequest infoProfile=new StringRequest(Request.Method.GET, urlUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject author=new JSONObject(response);
                    String name=author.getString("name");
                    String surname=author.getString("surname");
                    String bio=author.getString("bio");
                    String imageUrl=getResources().getString(R.string.serverUrl)+"/"+author.getString("profile_picture");
                    Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(imageViewProfil);
                    String username=name+" "+surname;
                    profileUserName.setText(username);
                    bioProfil.setText("bio:"+bio);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //volley kütüphanesi ile web servise istekte bulunulması ve dönen cevabın parçalanması işlemi
        //post bilgilerinin elde edilmesi ve adaptera yerlestirilmesi
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                contentElements.clear();
                try{  //gelen json formatındaki verinin parçalanması ve adapter'a gönderilmek üzere arraya alınması
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject postJson=jsonArray.getJSONObject(i);
                        int postId=postJson.getInt("id");
                        String content=postJson.getString("content");
                        boolean userLike=postJson.getBoolean("user_like");
                        String likeCount=postJson.getString("like_count");

                        //postu atan kullanici bilgileri icin
                        JSONObject author=postJson.getJSONObject("author");
                        userId=author.getInt("id");
                        String name=author.getString("name");
                        String surname=author.getString("surname");
                        String imageUrl=author.getString("profile_picture");
                        username=name+" "+surname;
                        int time=postJson.getInt("since");

                        // verilerin arrayliste uygun şekilde yerleştirilmesi
                        ContentElement c1=new ContentElement(postId,userId,username,time,content,imageUrl,userLike,likeCount);
                        contentElements.add(c1);
                    }
                    adapterHomePage.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gönderilmesi için jsona header eklenmesi
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue mRequest=Volley.newRequestQueue(this);

        //web servise gonderilecek isteklerin siraya alinmasi ve cekilmesi
        // isteğin web servise gönderilmesi
        mRequest.add(stringRequest);
        mRequest.add(infoProfile);

        //recyclerview de gösterilecek tasarımlar için oluşturulan adapterin kullanılması
        adapterHomePage=new AdapterHomePage(this,contentElements);

        //recyclerview ile adapterin birleştirilmesi
        recyclerView.setAdapter(adapterHomePage);

    }

    //toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.profile_message_send, menu);
            return true;
    }

    //toolbar ustundeki geri tusu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent geriButonu = new Intent(ProfilePage.this, MainActivity.class);
                // geriButonu intentini çalıştırıyoruz.
                startActivity(geriButonu);
            case R.id.profileMessageSend:
                control(String.valueOf(userId));

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void control(String recId){
        final String token=GlobalClass.getToken();
        //konusma baslatilacak kisi ile daha onceden konusma var mi kontrol
        String reqMessageControlUrl = getResources().getString(R.string.serverUrl) + "/messages?recipientId=" + recId;
        final StringRequest conversationControl = new StringRequest(Request.Method.GET, reqMessageControlUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //json parse islemi
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject messageText = jsonArray.getJSONObject(0);
                    conversationId = messageText.getString("conversationId");
                } catch (JSONException e) {
                    e.printStackTrace();
                    conversationId=null;
                }
                Intent goMessage=new Intent(ProfilePage.this,MessagePage.class);
                goMessage.putExtra("recId",String.valueOf(userId));
                goMessage.putExtra("username",username);
                goMessage.putExtra("conversationId",conversationId);
                startActivity(goMessage);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gonderilmesi icin jsona header eklenmesi
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(conversationControl);
    }
}

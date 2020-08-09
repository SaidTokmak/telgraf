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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//kullanicinin mesajlastigi kisilerin goruntulenmesi
public class MessagePage extends AppCompatActivity {

    //kullanilacak elemanlarin tanimlanmasi
    AdapterMessagePage adapterMessagePage;
    RecyclerView recyclerView;
    static ArrayList<MessageElement> messageElementList=new ArrayList<>();
    Button messageSend;
    EditText editMessage;
    String recId;
    Toolbar toolbar;
    static String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);

        // tasarim elemanlarina erisilmesi
        messageSend=findViewById(R.id.buttonMessageSend);
        editMessage=findViewById(R.id.editTextMessage);

        //kullanıcı isminin alinmasi
        String userName=getIntent().getStringExtra("username");

        toolbar=findViewById(R.id.toolbarMessage);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(userName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //toolbardaki geri tusu

        //sayfadaya gelen verilerin kayması ve liste şeklinde görüntülenmesi işlemleri
        recyclerView=findViewById(R.id.recyclerViewMessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //global classtan token cekilmesi
        GlobalClass globalClass=(GlobalClass) getApplicationContext();
        final String token=globalClass.getToken();

        //chat sayfasindan kullaniciya tiklaninca gelen conversation id
        conversationId=getIntent().getStringExtra("conversationId");
        recId=getIntent().getStringExtra("recId");

        if(conversationId==null){
            messageElementList.clear();
        }else {

            //istek gonderilecek adres conversation id sine gore cekilecek
            String reqUrl = getResources().getString(R.string.serverUrl) + "/messages?conversationId=" + conversationId + "&%24limit=100";

            //conversation id sine gore mesajlarin cekilmesi icin olusturulan istek
            StringRequest getMessages = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    messageElementList.clear(); //onceki mesajlarin tekrar gosterilmemesi icin icinin bosaltilmasi
                    try {
                        //json parse islemi
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject messageText = jsonArray.getJSONObject(i);
                            String messageId = messageText.getString("id");
                            String senderId = messageText.getString("senderId");
                            String recipientId = messageText.getString("recipientId");
                            String text = messageText.getString("text");
                            String conversationId = messageText.getString("conversationId");
                            String messageTime = messageText.getString("createdAt");
                            MessageElement messageElement = new MessageElement(messageId, senderId, recipientId, text, conversationId, messageTime);
                            messageElementList.add(messageElement);
                        }
                        adapterMessagePage.notifyDataSetChanged(); //verilerin guncellenmesini saglar
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

            Volley.newRequestQueue(this).add(getMessages);
        }
        //recyclerview de gosterilecek tasarimlar icin olusturulan adapterin kullanilmasi
        adapterMessagePage=new AdapterMessagePage(this,messageElementList);

        //recyclerview ile adapterin birlestirilmesi
        recyclerView.setAdapter(adapterMessagePage);

        // mesaj gonderme butonuna tiklama olayi
        messageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // mesaj yazma kismi bos olmasi kontrol
                if(editMessage.getText().toString().isEmpty()){
                    Toast.makeText(MessagePage.this, "Mesaj yazınız...", Toast.LENGTH_SHORT).show();
                }else{
                    //gonderilecek postu json objesine donusturulmesi
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("recipientId",recId);
                        jsonBody.put("text", editMessage.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String mRequestBody = jsonBody.toString();

                    String reqSendMessageUrl=getResources().getString(R.string.serverUrl)+"/messages";
                    StringRequest sendMessages=new StringRequest(Request.Method.POST, reqSendMessageUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            adapterMessagePage.notifyDataSetChanged();
                            control();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gonderilmesi icin jsona header eklenmesi
                            HashMap headers = new HashMap();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", token);
                            return headers;
                        }
                        @Override
                        public byte[] getBody() throws AuthFailureError { //web servise istek gonderilmesi icin jsona body eklenmesi
                            try {
                                //mRequest body yukarıda tanımladığımız json formatındaki gönderilecek veri
                                return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                                return null;
                            }
                        }
                    };
                    Volley.newRequestQueue(MessagePage.this).add(sendMessages);
                    //sayfanin yenilenmesi(sayfanin kapatilip yeniden yuklenmesi)
                }
            }
        });
    }
    //toolbar ustundeki geri tusu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent geriButonu = new Intent(getApplicationContext(), ChatPage.class);
                // geriButonu intentini çalıştırıyoruz.
                NavUtils.navigateUpTo(this, geriButonu);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void control(){
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
                    finish();
                    startActivity(getIntent().putExtra("conversationId",conversationId));
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
            public Map<String, String> getHeaders() throws AuthFailureError { //web servise istek gonderilmesi icin jsona header eklenmesi
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        Volley.newRequestQueue(MessagePage.this).add(conversationControl);
    }
}

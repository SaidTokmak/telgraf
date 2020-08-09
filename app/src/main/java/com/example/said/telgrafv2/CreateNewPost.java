package com.example.said.telgrafv2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//yeni post paylasma sayfasi
public class CreateNewPost extends AppCompatActivity {
    private Toolbar toolbarPostShare;
    EditText editTextPostShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);

        editTextPostShare=findViewById(R.id.editTextPostShare);

        //toolbarin ozellestirilmesi ve gosterilemesi
        toolbarPostShare=findViewById(R.id.toolbarPostShare);
        toolbarPostShare.setTitle("Post oluştur");
        toolbarPostShare.setTitleTextColor(Color.WHITE);
        toolbarPostShare.getNavigationIcon();
        setSupportActionBar(toolbarPostShare);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_share_design,menu);
        return true;
    }

    //toolbar ustundeki paylas yazısı ile paylasma yapilmasi
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                sharePost(); //paylasa tiklandiginda fonksiyonun cagrilmasi
            case android.R.id.home:
                Intent geriButonu = new Intent(getApplicationContext(), MainActivity.class);
                // geriButonu intentini çalıştırıyoruz.
                NavUtils.navigateUpTo(this, geriButonu);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sharePost(){
        if(editTextPostShare.getText().toString().isEmpty()){
            Toast.makeText(this, "Lütfen postu yazınız tekrar deneyiniz", Toast.LENGTH_SHORT).show();
        }else{

            //paylasim yapilacak web servis adresi
            String url=getResources().getString(R.string.serverUrl)+"/posts";

            final String content=editTextPostShare.getText().toString();

            //global classtan gerekli token verisinin alinmasi
            GlobalClass globalClass=(GlobalClass) getApplicationContext();
            final String token=globalClass.getToken();

            //gonderilecek postu json objesine donusturulmesi
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String mRequestBody = jsonBody.toString();

            //web servise baglanma gonderim islemi
            StringRequest jsonObjectRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreateNewPost.this,"Hata Mesajı:"+error.toString(), Toast.LENGTH_SHORT).show();
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
            Volley.newRequestQueue(this).add(jsonObjectRequest);

            //gonderme islemine gecerse intentle anasayfaya gonderiyor
            Intent goHome=new Intent(this,MainActivity.class);
            startActivity(goHome);
        }
    }
}

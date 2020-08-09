package com.example.said.telgrafv2;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {
    Button buttonLogin;
    EditText username,password;
    TextView forgetPassword;
    String user,pass;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //tasarim elemanlarinin javaya tanimlanmasi
        buttonLogin=findViewById(R.id.buttonLogin);
        forgetPassword=findViewById(R.id.forget_password);
        username=findViewById(R.id.edit_user_login);
        password=findViewById(R.id.edit_password_login);
        progressBar=findViewById(R.id.progressBarLogin);

        //giris butonu kullnici girisi butun alanlar dolu ise login fonksiyonuna gider
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=username.getText().toString();
                pass=password.getText().toString();
                if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(LoginPage.this, "Eksik Alanları Doldurunuz..", Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }
            }
        });

        //parolami unuttum butonu (suan icin islevi yok
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginPage.this, "Service not available yet...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //kullanici girisi icin fonksiyon web servis baglantisi yaparak kullanici bilgilerini kontrol edip giris icin izin verir
    public void login(){

        progressBar.setVisibility(View.VISIBLE); //progressBar calismiyor kontrol edin

        //authentication ile bize ozel token aliyoruz o token ile islemlerimizi halledecegiz diger sayfalarda
        String url=getResources().getString(R.string.serverUrl)+"/authentication"; //login işlemi için haberleşilecek server adresi

        //volley kutuphanesi ile web servise baglaniyoruz
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //serverden cevap olarak gelen tokeni '.' karakterlerine kadar parcalanir
                String[] responseToken=response.split("\\.");

                //jwit.io ile sifrelenmis token 3 bolumden olusur. 2.bolum kullanici id'sini saklar responseToken[1] denilerek 2.bolum alinir
                byte[] data = Base64.decode(responseToken[1], Base64.DEFAULT); //alinan 2.bolum base64 formatinda cekilir

                try {

                    JSONObject tokenObject=new JSONObject(response);
                    String token=tokenObject.getString("accessToken"); //web servisten gelen tokenin alinmasi

                    //GlobalClass adındaki classın bir nesnesi olarak tokeni kaydettik. istediğimiz yerden bu verileri cekebiliriz
                    GlobalClass globalClass=(GlobalClass) getApplicationContext();
                    globalClass.setToken(token);

                    String text = new String(data, "UTF-8"); //base64 seklindeki token string türüne cevirilir (tür olarak json formatındadır.)

                    //json parçalamsı ile userId(kullanıcı id'si) alınır
                    JSONObject jsonObject=new JSONObject(text);
                    int id=jsonObject.getInt("userId");
                    globalClass.setId(id);

                    //global classta emaili de tutuyoruz
                    globalClass.setEmail(user);

                    //login sayfasından anasayfaya gecis
                    Intent intent=new Intent(LoginPage.this,MainActivity.class);
                    startActivity(intent);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // service'den donen hata kodlarinin yakalanmasi ve duruma gore cevap verilmesi
                if (error.networkResponse != null) {
                    if(error.networkResponse.statusCode==401){ // notAuthendication hatası
                        Toast.makeText(LoginPage.this, "Giriş bilgileriniz yanlış", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Error Response code" , String.valueOf(error.networkResponse.statusCode));
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { //service'e veri gönderme işlemi
                //web servise gonderilecek username,password ve strategy'nin tanimlanmasi ve gonderilmesi islemi
                Map<String,String> params=new HashMap<>();
                params.put("email",user);
                params.put("password",pass);
                params.put("strategy","local");
                return params;
            }
        };

        // web service'e istek gonderilmesi
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onBackPressed() { // login ekranında geri tuşuna basılırsa uygulamayı sonlandırır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        finish();
    }
}

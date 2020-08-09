package com.example.said.telgrafv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends Fragment {

    FloatingActionButton newPostButton;
    RecyclerView recyclerView;
    static ArrayList<ContentElement> contentElements=new ArrayList<>();
    AdapterHomePage adapterHomePage;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GlobalClass globalClass=(GlobalClass) getActivity().getApplicationContext();
        final String token=globalClass.getToken();

        final View rootView=inflater.inflate(R.layout.page_home, container, false);

        //floating button=ekranın sağ altında bulunan sabit buton. Butonun tanımlanması ve click işlemi
        newPostButton=rootView.findViewById(R.id.floatingActionButton);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goPostPage=new Intent(rootView.getContext(),CreateNewPost.class);
                startActivity(goPostPage);
            }
        });

        //sayfadaya gelen verilerin kayması ve liste şeklinde görüntülenmesi işlemleri
        recyclerView=rootView.findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //web servis bağlantısı ile post verilerinin çekilmesi (%24 icin dandelion/docs/'a bakin(web serviste))
        String url=getResources().getString(R.string.serverUrl)+"/posts?%24limit=100"; //login işlemi için haberleşilecek server adresi

        //volley kütüphanesi ile web servise istekte bulunulması ve dönen cevabın parçalanması işlemi
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                contentElements.clear();
                if(response.isEmpty()){
                    contentElements=null;
                }
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
                        int userId=author.getInt("id");
                        String name=author.getString("name");
                        String surname=author.getString("surname");
                        String imageUrl=author.getString("profile_picture");
                        String username=name+" "+surname;
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
                Toast.makeText(rootView.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(rootView.getContext()).add(stringRequest); // isteğin web servise gönderilmesi

        //recyclerview de gösterilecek tasarımlar için oluşturulan adapterin kullanılması
        adapterHomePage=new AdapterHomePage(rootView.getContext(),contentElements);

        //recyclerview ile adapterin birleştirilmesi
        recyclerView.setAdapter(adapterHomePage);

        //yukaridan kaydirinca ekranin yenilenmesi
        swipeRefreshLayout=rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapterHomePage.notifyDataSetChanged();
                startActivity(getActivity().getIntent());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }
}

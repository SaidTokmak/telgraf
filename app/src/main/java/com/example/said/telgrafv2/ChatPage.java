package com.example.said.telgrafv2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ChatPage extends Fragment {

    private RecyclerView recyclerView;
    static private ArrayList<ChatElement> chatElements=new ArrayList<>();
    private AdapterChatPage adapterChatPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.page_chat, container, false);

        GlobalClass globalClass=(GlobalClass) getActivity().getApplicationContext();
        final String token=globalClass.getToken();

        //recyclerviewin tanimlamalari ve ayarlari
        recyclerView=rootView.findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //kullanicinin konustugu kisilerin bilgilerinin adresi
        String reqUrl=getResources().getString(R.string.serverUrl)+"/conversations";

        //kullanicinin konustugu kisilerin bilgilerinin web servisten cekilme islemi
        StringRequest getMessages=new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                chatElements.clear(); //daha once yuklenen sayfanin tekrar yuklenmesini engellemek icin verilerin bosaltilmasi
                try{
                    //json parse islemi
                    JSONObject getMessage=new JSONObject(response);
                    JSONArray jsonArray=getMessage.getJSONArray("data");
                    for(int index=0;index<jsonArray.length();index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        String conversationId=jsonObject.getString("id");
                        JSONObject talk = jsonObject.getJSONObject("other");
                        String userId=talk.getString("id");
                        String name = talk.getString("name");
                        String surname = talk.getString("surname");
                        String profilePicture=talk.getString("profile_picture");
                        String username = name + " " + surname;
                        ChatElement c1 = new ChatElement(profilePicture,conversationId,username, userId);
                        chatElements.add(c1);
                    }
                    adapterChatPage.notifyDataSetChanged(); //degisiklik durumunda verilerin guncellenmesini saglar
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("hata",error.toString());
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
        Volley.newRequestQueue(rootView.getContext()).add(getMessages);

        //adapterlarin tanimlanmasi ve verilerin gonderilmesi
        adapterChatPage=new AdapterChatPage(rootView.getContext(),chatElements);
        recyclerView.setAdapter(adapterChatPage);

        //rootview sayfanin contextini tutar burasi bir fragment oldugu icin
        return rootView;
    }
}

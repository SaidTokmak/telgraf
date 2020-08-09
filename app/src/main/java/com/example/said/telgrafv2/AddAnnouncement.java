package com.example.said.telgrafv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddAnnouncement extends AppCompatActivity {

    Toolbar toolbar;
    EditText editHeader,editContent;
    Button addNotificaitonButton;
    Spinner spinner;
    ArrayList<String> arrayOptions=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        addNotificaitonButton = findViewById(R.id.addNotificationButton);

        editContent=findViewById(R.id.addNotificationContent);
        editHeader=findViewById(R.id.addNotificationHeader);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Spinner başlangıç
        // Spinner ile duyurunun türünü seçilmesi işlemleri
        spinner = findViewById(R.id.addNotificationSpinner);

        // spinner menü elemanlerının tanımlanması
        arrayOptions.add("Burs");
        arrayOptions.add("Staj");
        arrayOptions.add("İş İlanı");
        arrayOptions.add("Okul");
        arrayOptions.add("Önemli");

        // adaptör ile menünün özellikleri ve kullanacağı listenin tanımlanması
        arrayAdapter = new ArrayAdapter<String>(this
                ,android.R.layout.simple_list_item_1
                ,android.R.id.text1
                ,arrayOptions);

        // adaptörün spinnerla birleştirilmesi
        spinner.setAdapter(arrayAdapter);
        // Spinner bitis

        addNotificaitonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String veri = spinner.getSelectedItem().toString();
                String header=editHeader.getText().toString();
                String content=editContent.getText().toString();
                Toast.makeText(AddAnnouncement.this, veri+"\n"+header+"\n"+content, Toast.LENGTH_SHORT).show();
            }
        });

    }
}

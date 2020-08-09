package com.example.said.telgrafv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//fragmentlerin tutuldugu ve ayarlandigi yer
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Fragment fragment;
    GlobalClass globalClass;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //global classtan kullanicinin id sini cektik
        globalClass=(GlobalClass) getApplicationContext();
        userId=globalClass.getId();

        //uygulama ilk acildiginda gosterilecek fragmentin ayarlanmasi
        fragment=new HomePage();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentHolder,fragment).commit();

        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);

        //toolbarin ozellestirilmesi
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("TELGRAF");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View header=navigationView.inflateHeaderView(R.layout.menu_header);
        navigationView.setNavigationItemSelectedListener(this);

        //menudeki fotografa veya kullanici ismine tiklanildigi zaman profil sayfasi acilmasi
        ImageView imageViewMenuHeader=header.findViewById(R.id.menuImageView);
        TextView textViewMenuHeader=header.findViewById(R.id.menuUsername);

        //kullanici emailini global classtan cektik
        textViewMenuHeader.setText(globalClass.getEmail());

        //profil tıklama olaylari
        imageViewMenuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goProfile=new Intent(MainActivity.this,ProfilePage.class);
                goProfile.putExtra("userId",String.valueOf(userId));
                startActivity(goProfile);
            }
        });
        textViewMenuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goProfile=new Intent(MainActivity.this,ProfilePage.class);
                goProfile.putExtra("userId",String.valueOf(userId));
                startActivity(goProfile);
            }
        });

    }

    //navigation menude tiklanan sayfaya yonlendirme islemi
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.homepage){
            fragment = new HomePage();
        }
        if(id == R.id.notifications){
            fragment = new NotificationsPage();
        }
        if(id == R.id.announcement){
            fragment = new AnnouncementPage();
        }
        if(id == R.id.chat){
            fragment = new ChatPage();
        }
        if(id == R.id.archive){
            fragment = new ArchivePage();
        }
        if(id == R.id.profil){
            Intent goProfile=new Intent(MainActivity.this,ProfilePage.class);
            goProfile.putExtra("userId",String.valueOf(userId));
            startActivity(goProfile);
        }
        if(id == R.id.quit){
            Intent gLogin=new Intent(this,LoginPage.class);
            startActivity(gLogin);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder,fragment).commit();

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        //control is navigation drawer open? (ilk basta ingilizce comment alacaktim ;))
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Uygulamadan çıkmak istiyor musunuz?");
            builder.setCancelable(false);

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}

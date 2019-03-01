package ru.vladislav_akulinin.mychat_version_2;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.images.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.vladislav_akulinin.mychat_version_2.Adapter.ViewPagerAdapter;
import ru.vladislav_akulinin.mychat_version_2.Fragments.CallFragment;
import ru.vladislav_akulinin.mychat_version_2.Fragments.ChatsFragment;
import ru.vladislav_akulinin.mychat_version_2.Fragments.ProfileFragment;
import ru.vladislav_akulinin.mychat_version_2.Fragments.UsersFragment;
import ru.vladislav_akulinin.mychat_version_2.Model.Chat;
import ru.vladislav_akulinin.mychat_version_2.Model.User;

public class MainActivity extends AppCompatActivity { //implements BottomNavigationView.OnNavigationItemSelectedListener

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser; //предоставляет информацию о профиле пользователя, содержит методы для изменения и получения информации
    DatabaseReference reference;

    //для удобного управления фрагментами
    final Fragment fragment1 = new UsersFragment();
    final Fragment fragment2 = new CallFragment();
    final Fragment fragment3 = new ChatsFragment();
    final Fragment fragment4 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment3; //выбор первого фрагмента

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation); //получить ссылку на объект
        Integer indexItem = 2; //для установления индекса первоначального фрагмента
        navigation.getMenu().getItem(indexItem).setChecked(true); //отправка нажатия кнопки и индекса первоначальног фрагмента
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);

        fm.beginTransaction()
                .add(R.id.fragment_container, fragment4, "4")
                    .hide(fragment4)
                        .commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment1, "1").hide(fragment1).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment3, "3").commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //установить toolbar для окна действий
        getSupportActionBar().setTitle("Чаты"); //для изменения title in toolbar

//        profile_image = findViewById(R.id.profile_image);
//        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); //получаем текущего пользователя, вошедшего в систему
        reference = FirebaseDatabase
                .getInstance() //получить ссылку на местоположение
                .getReference("User") //где хранятся
                .child(firebaseUser.getUid());

        //для отображения пользовательских данных
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                try { //обработка исключений, если не найденны данные на сервере
//                    username.setText(user.getUsername());
//                    if(user.getImageURL().equals("default")){
//                        profile_image.setImageResource(R.drawable.ic_user); //картинка по умолчанию
//                    }
//                    else{
////                        Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
//                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//                    }
//                }
//                catch (NullPointerException exc){
//                    Toast.makeText(MainActivity.this, "Для данного пользователя не нашлось данных", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        final TabLayout tabLayout = findViewById(R.id.tab_layout);
//        final ViewPager viewPager = findViewById(R.id.view_pager);

//        //добавление количества непрочитанных сообщений
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//                int unread = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
//                        unread++;
//                    }
//                }
//
//                //*************************************
////                bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
////                    @Override
////                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
////                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//////                        Fragment fragment;
////                        switch (item.getItemId()){
////                            case R.id.contacts:
////                                viewPagerAdapter.addFragments(new UsersFragment(), "Пользователи");
//////                                fragment = fragment1;
////                                return true;
////                            case R.id.challenges:
////                                viewPagerAdapter.addFragments(new CallFragment(), "Вызовы");
//////                                fragment = fragment2;
////                                return true;
////                            case R.id.chats:
////                                viewPagerAdapter.addFragments(new ChatsFragment(), "Чаты");
//////                                fragment = fragment3;
////                                return true;
////                            case R.id.settings:
////                                viewPagerAdapter.addFragments(new ProfileFragment(), "Профиль");
//////                                fragment = fragment4;
////                                return true;
////                        }
////                        viewPager.setAdapter(viewPagerAdapter);
////                        return false;
////                    }
////                });
//                //*************************************
//
//                if(unread == 0){
//                    viewPagerAdapter.addFragments(new ChatsFragment(), "Чаты");
//                }
//                else {
//                    viewPagerAdapter.addFragments(new ChatsFragment(), "("+unread+") Чаты");
//                }
//
//                viewPagerAdapter.addFragments(new UsersFragment(), "Пользователи");
//                viewPagerAdapter.addFragments(new ProfileFragment(), "Профиль");
//
//                viewPager.setAdapter(viewPagerAdapter);
//
////                tabLayout.setupWithViewPager(viewPager);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.contacts:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    getSupportActionBar().setTitle("Контакты"); //для изменения title in toolbar
                    return true;

                case R.id.challenges:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    getSupportActionBar().setTitle("Вызовы"); //для изменения title in toolbar
                    return true;

                case R.id.chats:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    getSupportActionBar().setTitle("Чаты"); //для изменения title in toolbar
                    return true;

                case R.id.settings:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    getSupportActionBar().setTitle("Профиль"); //для изменения title in toolbar
                    return true;
            }

            return false;
        }
    };



//    //Загрузка фрагмента по умолчанию
//    private boolean loadFragment(Fragment fragment) {
//        if (fragment != null) {
//            getSupportFragmentManager()
//                    .beginTransaction() //начать операции по работе с фрагментами
//                    .replace(R.id.fragment_container, fragment) //заменяет один фрагмент на другой
//                    .commit(); //записывает данные синхронно (блокируя вызываемый поток из). Затем он информирует вас об успешности операции.
//            return true;
//        }
//        return false;
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut(); //выйти из учетной записи
////                //Нужно продумать и изменить данный код, ибо будет зависать приложение (из-за статуса)
//                startActivity(new Intent(MainActivity.this, StartActivity.class)
//                                                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); //для пользовательского статуса
////                finish();
//                return true;
//        }
//
//        return false;
//    }

    //для пользовательского статуса
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

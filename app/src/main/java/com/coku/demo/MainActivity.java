package com.coku.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.coku.tmt.Login;

import io.reactivex.Observable;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Class clazz = Class.forName("com.coku.demo.$UserApi");

            Retrofit retrofit = null;
            Observable<Login<String>> observable = retrofit.create(HomeApi.class).logout(null);





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
package com.coku.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.coku.imp.HomeDisPatchImp;
import com.coku.lib.RequestCallback;
import com.coku.lib.TError;
import com.coku.tmt.Login;

import io.reactivex.Observable;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://appbg.lcfarm.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            $HomeApiService service = new $HomeApiService(retrofit,new HomeDisPatchImp());
            service.query(new RequestCallback<Object>() {
                @Override
                public void onSuccess(Object data) {

                    Log.d("MainActivity S",data.toString());
                }

                @Override
                public void onError(TError error) {
                    Log.d("MainActivity E",error.toString());
                }
            });



    }


}
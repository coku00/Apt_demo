package com.coku.demo;

import com.coku.annotation.AutoInject;
import com.coku.annotation.AutoRequest;
import com.coku.tmt.Login;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

@AutoInject
public interface HomeApi {

    @AutoRequest("com.coku.demo.HomeApi")
    @GET("/a/b/c")
    Observable<Login> logout(@Body Login login);

    @AutoRequest("com.coku.demo.HomeApi")
    @GET("/a/b/c")
    Observable<Login> query();
}

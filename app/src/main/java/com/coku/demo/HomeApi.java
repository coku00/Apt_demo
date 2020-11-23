package com.coku.demo;

import com.coku.annotation.BuildClass;
import com.coku.annotation.AutoRequest;
import com.coku.tmt.Login;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

@BuildClass
public interface HomeApi {

    @AutoRequest(className = "com.coku.demo.HomeApi")
    @GET("/a/b/c")
    Observable<Login<String>> logout(@Body Login login);

    @AutoRequest(className = "com.coku.demo.HomeApi")
    @GET("/a/b/c")
    Observable<Login<String>> query();
}

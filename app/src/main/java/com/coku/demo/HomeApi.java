package com.coku.demo;

import com.coku.annotation.AutoClass;
import com.coku.annotation.AutoMethod;
import com.coku.tmt.Login;
import com.coku.tmt.UserInfo;

import io.reactivex.Observable;
import okhttp3.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

@AutoClass
public interface HomeApi {

    @AutoMethod(className = "com.coku.demo.HomeApi")
    @GET("/app/logout")
    Observable<Response> logout();

    @AutoMethod(className = "com.coku.demo.HomeApi")
    @GET("/api/user/userStatus.htm")
    Observable<Object> query();
}

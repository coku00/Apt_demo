package com.coku.demo;

import com.coku.annotation.AutoInject;
import com.coku.annotation.AutoRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 */

@AutoInject
public interface HomeApi {

    @AutoRequest
    @GET("/a/b/c")
    Observable<Login> login(@Field("name") String name,@Field("age") int age);


//    @AutoRequest
//    @GET("/a/b/c")
//    Observable<Login> logout(@Body Login login);

}

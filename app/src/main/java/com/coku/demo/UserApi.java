package com.coku.demo;
import com.coku.annotation.BuildClass;
import com.coku.annotation.AutoRequest;
import com.coku.tmt.Login;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 */


@BuildClass
public interface UserApi {

    @AutoRequest(className = "com.coku.demo.UserApi")
    @GET("/a/b/c")
    Observable<Login> login(@Field("name") String name, @Field("age") int age);



}

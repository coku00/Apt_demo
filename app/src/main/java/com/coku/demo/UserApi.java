package com.coku.demo;
import com.coku.annotation.AutoInject;
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


@AutoInject
public interface UserApi {

    @AutoRequest("com.coku.demo.UserApi")
    @GET("/a/b/c")
    Observable<Login> login(@Field("name") String name, @Field("age") int age);



}

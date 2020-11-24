package com.coku.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class BaseService {
    protected Retrofit mRetrofit;
    private CompositeDisposable mCompositeDisposable;

    public BaseService(Retrofit retrofit) {
        this.mRetrofit = retrofit;
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

    protected void addSubscribe(Disposable subscription) {
        if (this.mCompositeDisposable == null || this.mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable = new CompositeDisposable();
        }

        this.mCompositeDisposable.add(subscription);
    }


    public void dispose() {
        if (this.mCompositeDisposable != null) {
            this.mCompositeDisposable.dispose();
        }
    }

    public<T> ObservableTransformer<T,T> transformer() {

        return new ObservableTransformer<T,T>(){
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(mainScheduler());
            }
        };
    }

    private Scheduler mainScheduler(){

        Scheduler scheduler = null;

        try {
            Class mainSchedulerClass = Class.forName("io.reactivex.android.schedulers.AndroidSchedulers");

            Method method = mainSchedulerClass.getMethod("mainThread");

            scheduler = (Scheduler) method.invoke(null);

        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        return scheduler;
    }

}
package com.coku.lib;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;


public class BaseService<T extends Class<?>> {
    protected Retrofit mRetrofit;
    private CompositeDisposable mCompositeDisposable;

    public BaseService(RetrofitFactory factory) {
        this.mRetrofit = factory.created();
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
}
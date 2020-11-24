package com.coku.imp;
import com.coku.demo.$HomeApiDisPatch;
import com.coku.lib.RequestCallback;
import com.coku.lib.TError;
import com.coku.lib.TargetObserver;
import com.coku.tmt.Login;
import io.reactivex.functions.Consumer;
import okhttp3.Response;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/21.
 * @email coku_lwp@126.com
 */
public class HomeDisPatchImp implements $HomeApiDisPatch {


    @Override
    public TargetObserver logout(RequestCallback<Response> var1) {
        return null;
    }

    @Override
    public TargetObserver query(final RequestCallback<Object> var1) {
        return TargetObserver.bindTarget(var1, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                var1.onSuccess(var1);
            }
        }, new Consumer<TError>() {
            @Override
            public void accept(TError tError) throws Exception {
                var1.onError(tError);
            }
        });
    }
}

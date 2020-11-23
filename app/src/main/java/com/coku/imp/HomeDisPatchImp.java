package com.coku.imp;


import com.coku.demo.$HomeApiDisPatch;
import com.coku.lib.RequestCallback;
import com.coku.lib.TError;
import com.coku.lib.TargetObserver;

import io.reactivex.functions.Consumer;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/21.
 * @email coku_lwp@126.com
 */
public class HomeDisPatchImp implements $HomeApiDisPatch {
    @Override
    public TargetObserver logout(RequestCallback<Object> var1) {
        return TargetObserver.bindTarget(var1, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {

            }
        },onError(var1));
    }

    @Override
    public TargetObserver query(RequestCallback<Object> var1) {
        return null;
    }

    private Consumer onError(final RequestCallback callback){
        return new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                callback.onError((TError) o);
            }
        };
    }
}

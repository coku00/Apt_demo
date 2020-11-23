package com.coku.lib;


import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;

/**
 * @author liuwaiping
 * @desc 绑定一个回调对象，统一进行回调的判空
 * @date 2020/8/29.
 * @email coku_lwp@126.com
 */
public abstract class TargetObserver<T> extends ErrorObserver<T> {
    public static <T> TargetObserver<T> bindTarget(Object target, final Consumer<? super T> action) {
        return new TargetObserver<T>(target) {
            @Override
            protected void onSuccess(T t) {
                try {
                    action.accept(t);
                } catch (Exception e) {
                    onError(e);
                }
            }
        };
    }


    public static <T> TargetObserver<T> bindTarget(Object target, final Consumer<? super T> nextAction, final Consumer<TError> errorAction) {
        return new TargetObserver<T>(target) {
            @Override
            public void onComplete() {

            }

            @Override
            protected void onSuccess(T t) {
                try {
                    nextAction.accept(t);
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(TError ex) {
                try {
                    errorAction.accept(ex);
                } catch (Exception e) {
                    onError(e);
                }
            }
        };
    }


    //弱引用，防止内存泄露
    private WeakReference<Object> mTarget;


    protected TargetObserver() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private TargetObserver(Object target) {
        mTarget = new WeakReference<Object>(target);
    }

    private Object getTarget() {
        return mTarget == null ? null : mTarget.get();
    }

    @Override
    public void onStart() {
        if (getTarget() != null) {

        }
    }


    @Override
    public void onNext(T t) {
        if (getTarget() != null) {
            onSuccess(t);
        }

    }

    @Override
    protected void onError(TError var1) {

    }

    @Override
    public void onError(Throwable e) {
        if (getTarget() != null) {
            super.onError(e);
        }

    }

    protected abstract void onSuccess(T t);

    @Override
    public void onComplete() {

    }
}

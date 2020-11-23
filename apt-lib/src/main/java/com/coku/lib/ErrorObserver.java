package com.coku.lib;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/8/18.
 * @email coku_lwp@126.com
 */
public abstract class ErrorObserver<T> extends DisposableObserver<T> {
    public ErrorObserver() {
    }

    public static <T> ErrorObserver<T> create(final Consumer<? super T> nextAction) {
        return new ErrorObserver<T>() {
            public void onNext(T value) {
                try {
                    nextAction.accept(value);
                } catch (Exception var3) {
                    this.onError(var3);
                }

            }

            public void onComplete() {
            }

            public void onError(TError ex) {
            }
        };
    }

    public static <T> ErrorObserver<T> create(final Consumer<? super T> nextAction, final Consumer<TError> errorAction) {
        return new ErrorObserver<T>() {
            public void onComplete() {
            }

            public void onNext(T value) {
                try {
                    nextAction.accept(value);
                } catch (Exception var3) {
                    this.onError(var3);
                }

            }

            public void onError(TError ex) {
                try {
                    errorAction.accept(ex);
                } catch (Exception var3) {
                    this.onError(var3);
                }

            }
        };
    }

    public static <T> ErrorObserver<T> create(final Action startAction, final Consumer<? super T> nextAction, final Consumer<TError> errorAction, final Action completedAction) {
        return new ErrorObserver<T>() {
            public void onStart() {
                try {
                    startAction.run();
                } catch (Exception var2) {
                    this.onError(var2);
                }

            }

            public void onComplete() {
                try {
                    completedAction.run();
                } catch (Exception var2) {
                    this.onError(var2);
                }

            }

            public void onNext(T value) {
                try {
                    nextAction.accept(value);
                } catch (Exception var3) {
                    this.onError(var3);
                }

            }

            public void onError(TError ex) {
                try {
                    errorAction.accept(ex);
                } catch (Exception var3) {
                    this.onError(var3);
                }

            }
        };
    }

    public void onError(Throwable e) {
        this.onError(e);
    }

    public void onError(String code, String message) {
        this.dispose();
        this.onError(new TError(code, message));
    }

    protected abstract void onError(TError var1);
}
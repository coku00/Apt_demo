package com.coku.lib;

public interface AutoRequestCall<D,E> {
    void onSuccess(D data);

    void onError(E error);
}

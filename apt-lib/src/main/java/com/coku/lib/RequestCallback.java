package com.coku.lib;

public interface RequestCallback<D> {
    void onSuccess(D data);

    void onError(TError error);
}

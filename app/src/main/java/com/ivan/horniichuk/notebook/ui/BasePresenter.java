package com.ivan.horniichuk.notebook.ui;

import android.content.Context;

public class BasePresenter<MvpView> {
    protected Context context;
    protected MvpView view;

    public void onViewAttach(Context context, MvpView view)
    {
        this.view=view;
        this.context=context;
    }

    public void onViewDetach()
    {
        view=null;
    }
}

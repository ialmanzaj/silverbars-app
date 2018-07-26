package com.app.app.silverbarsapp.fragments;

import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 05/27/17.
 */

public abstract class BaseFragmentExtended extends BaseFragment  {

    @BindView(R.id.loading) LinearLayout mLoadingView;
    @BindView(R.id.error_view) LinearLayout mErrorView;




    protected void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void onLoadingViewOff(){mLoadingView.setVisibility(View.GONE);}

    protected void onErrorViewOn(){
        mErrorView.setVisibility(View.VISIBLE);
    }

    protected void onErrorViewOff(){
        mErrorView.setVisibility(View.GONE);
    }
}

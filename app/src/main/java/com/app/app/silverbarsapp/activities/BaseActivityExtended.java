package com.app.app.silverbarsapp.activities;

import android.view.View;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.R;

import butterknife.BindView;

/**
 * Created by isaacalmanza on 05/27/17.
 */

public abstract class BaseActivityExtended extends BaseActivity{

    @BindView(R.id.error_view)LinearLayout mErrorView;
    @BindView(R.id.loading)LinearLayout mLoadingView;



    protected void onLoadingViewOn(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void onLoadingViewOff(){
        mLoadingView.setVisibility(View.GONE);
    }

    protected void onErrorViewOn(){mErrorView.setVisibility(View.VISIBLE);}

    protected void onErrorViewOff(){mErrorView.setVisibility(View.GONE);}
}

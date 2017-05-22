package com.app.app.silverbarsapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.app.app.silverbarsapp.utils.Utilities;

import butterknife.BindView;
import im.delight.android.webview.AdvancedWebView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyViewFragment extends BaseFragment {

    @BindView(R.id.empty_state) LinearLayout mEmptyView;
    @BindView(R.id.empty_text) TextView mEmptyText;

    @BindView(R.id.webview) AdvancedWebView webView;

    private Utilities mUtilities = new Utilities();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_empty_view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWebview();
        onEmptyViewOn(CONTEXT.getString(R.string.fragment_progress_daily_empty));
    }

    private void setupWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        mUtilities.loadBodyFromUrl(CONTEXT, webView);
    }

    private void onEmptyViewOn(String text) {
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyText.setText(text);
    }

    private void onEmptyViewOff() {
        mEmptyView.setVisibility(View.GONE);
    }

}

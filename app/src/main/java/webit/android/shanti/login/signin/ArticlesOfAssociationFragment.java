package webit.android.shanti.login.signin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.URL;

import webit.android.shanti.R;

public class ArticlesOfAssociationFragment extends SignUpBaseFragment {

 private static String mUrl;


    public ArticlesOfAssociationFragment() {
        // Required empty public constructor
    }


    public static ArticlesOfAssociationFragment getInstance(String url) {
        ArticlesOfAssociationFragment fragment = new ArticlesOfAssociationFragment();
        mUrl = url;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView =  inflater.inflate(R.layout.fragment_articles_of_association, container, false);

        WebView mWebView = (WebView) mRootView.findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="+mUrl);

        return mRootView;
    }


}

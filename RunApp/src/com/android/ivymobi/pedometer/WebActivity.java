package com.android.ivymobi.pedometer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ivymobi.runapp.R;
import com.msx7.annotations.Inject;
import com.msx7.annotations.InjectActivity;
import com.msx7.annotations.InjectView;

@InjectActivity(id = R.layout.web_activity)
public class WebActivity extends BaseActivity {
    @InjectView(id = R.id.webView)
    WebView webView;
    @InjectView(id = R.id.title)
    TextView mTitleView;
    String url;
    @InjectView(id = R.id.textView1)
    TextView textView;
    @InjectView(id = R.id.progressBar1)
    ProgressBar progressBar;
    @InjectView(id = R.id.net_error)
    TextView netErrorView;

    private static final int PROGRESS_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Inject.inject(this);
        mTitleView.setText(getIntent().getStringExtra("title"));
        url = getIntent().getStringExtra("url");
        netErrorView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);
        		showDialog(PROGRESS_ID);
        	}
        	
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                dismissDialog(PROGRESS_ID);
                // 页面下载完毕,却不代表页面渲染完毕显示出来
                // WebChromeClient中progress==100时也是一样
                if (webView.getContentHeight() != 0) {
                    // 这个时候网页才显示
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                // 自身加载新链接,不做外部跳转
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
            		String failingUrl) {
            	super.onReceivedError(view, errorCode, description, failingUrl);
            	netErrorView.setVisibility(View.VISIBLE);
            	webView.setVisibility(View.GONE);
            }

        });

//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                // TODO Auto-generated method stub
//                super.onProgressChanged(view, newProgress);
//                // 这里将textView换成你的progress来设置进度
//                if (newProgress == 0) {
//                    textView.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                textView.setText(newProgress + "");
//                textView.postInvalidate();
//                progressBar.setProgress(newProgress);
//                progressBar.postInvalidate();
//                if (newProgress == 100) {
//                    textView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });

    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		if (id == PROGRESS_ID) {
			return ProgressDialog.show(WebActivity.this, null, "载入中...");
		}
		return super.onCreateDialog(id);
	}

}

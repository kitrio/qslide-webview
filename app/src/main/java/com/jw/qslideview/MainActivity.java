package com.jw.qslideview;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lge.app.floating.FloatableActivity;
import com.lge.app.floating.FloatingWindow;


import static android.content.ContentValues.TAG;

public class MainActivity extends FloatableActivity {

    private WebView mWebView;
    private WebSettings mWebSetting;

    private ImageButton qslideButton;
    private ImageButton webViewBackButton;
    private EditText etAddress;

    private ValueCallback<Uri[]> mfilePathCallback;
    public  static final int INPUT_FILE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etAddress = (EditText)findViewById(R.id.etAddress);
        qslideButton = (ImageButton) findViewById(R.id.button_qslide);
        webViewBackButton = findViewById(R.id.button_wvBack);

        mWebView = (WebView) findViewById(R.id.webViewMain);
        mWebView.setWebViewClient(new myWebClient());
        mWebView.setWebChromeClient(new myWebChromeClient());
        mWebSetting = mWebView.getSettings();

        mWebSetting.setDomStorageEnabled(true);
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setAllowFileAccess(true);
        mWebView.loadUrl("https://m.naver.com");

        qslideButton.setOnTouchListener(qListener);

        etAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("button","web enterkey");
                    String url = etAddress.getText().toString();
                    if(!url.startsWith("http://") && !url.startsWith("https://")){
                        url = "http://"+url;
                    }
                    mWebView.loadUrl(url);
                    etAddress.setText(mWebView.getUrl());
                    return true;
                }
                return false;
            }
        });
        webViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWebView.canGoBack()){
                    mWebView.goBack();
                    Log.d("button","webview back");
                }
            }
        });

    }

    public class myWebClient extends WebViewClient
    {
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            // TODO Auto-generated method stub
//            super.onPageStarted(view, url, favicon);
//        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("tel:")){
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                return  true;
            }
            if (url.startsWith("mailto:")) {
                startActivity(new Intent(Intent.EXTRA_EMAIL, Uri.parse(url)));
                return true;
            }
            return false;
        }

    }
    public class myWebChromeClient extends WebChromeClient{

        @Override
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback,   WebChromeClient.FileChooserParams fileChooserParams) {
            if(mfilePathCallback != null){
                mfilePathCallback.onReceiveValue(null);
                mfilePathCallback = null;
            }
            mfilePathCallback = filePathCallback ;
            Intent contentIntent = fileChooserParams.createIntent();
            try {
                startActivityForResult(contentIntent,INPUT_FILE_REQUEST_CODE);
                Log.d("Activity","startActivity");
            } catch (ActivityNotFoundException e){
                mfilePathCallback = null;
                return false;
            }
            return true;
        }

    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == INPUT_FILE_REQUEST_CODE) {
                if (mfilePathCallback == null) return;
                mfilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mfilePathCallback = null;
            }
        }

    View.OnTouchListener qListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean floatingmode = isInFloatingMode();  		// test if application is in floating window mode

            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                //qslideButton.setBackgroundResource(R.drawable.back_pressed); // setting dingy background for the button when pressed

                if(floatingmode) {
                    //qslideButton.setImageResource(R.drawable.floating_btn_fullscreen_normal); // if in floating mode then change button image
                } else {
                    //qslideButton.setImageResource(R.drawable.ic_menu_floating_app_pressed);  // else normal image for full screen mode
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //qslideButton.setBackgroundResource(R.drawable.);  // setting normal background for the button when released
                if(floatingmode) {
                    // close the floating window and return to full screen mode (true parameter)
                    getFloatingWindow().close(true);
                } else {
//                    boolean useOverlay = true;//mUseOverlayCheckbox.isChecked();
//                    boolean useOverlappingTitle = true;//mUseOverlappingTitleCheckbox.isChecked();
//                    boolean isResizable = true;//mIsResizableCheckbox.isChecked();

                    //switchToFloatingMode(useOverlay, useOverlappingTitle, isResizable, null);
                    setDontFinishOnFloatingMode(true);
                    switchToFloatingMode();

                }
                return true;
            }
            return false;
        }
    };

    // This is called when the floating window becomes visible to the user.
    @Override
    public void onAttachedToFloatingWindow(FloatingWindow w) {
        Log.d(TAG,"onAttachedToFloatingWindow.");
        /* all resources should be reinitialized once again
         * if you set new layout for the floating mode setContentViewForFloatingMode()*/
        qslideButton = (ImageButton)findViewById(R.id.button_qslide);

        // and also listeners a should be added once again to the buttons in floating mode
        qslideButton.setOnTouchListener(qListener);

        //FloatingWindow.LayoutParams mParams = w.getLayoutParams();
        w.setSize(700,1000);
        // set an onUpdateListener to limit the width of the floating window

    }
    // This is called when the floating window is closed.
    @Override
    public boolean onDetachedFromFloatingWindow(FloatingWindow w, boolean isReturningToFullScreen) {
        Log.d(TAG,"onDetachedFromFloatingWindow. Returning to Fullscreen: " + isReturningToFullScreen);

//        //set the last position of the floating window when the window is closing
//        Intent intent = getIntent();
//        intent.putExtra("posX", w.getLayoutParams().x);
//        intent.putExtra("posY", w.getLayoutParams().y);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isSwitchingToFloatingMode()) {
            // release application specific resources here
            // only when application isn't switching to floating window mode
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isSwitchingToFloatingMode()) {
            // release application specific resources here
            // only when application isn't switching to floating window mode
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isSwitchingToFloatingMode()) {
            // release application specific resources here
            // only when application isn't switching to floating window mode
        }
    }

}

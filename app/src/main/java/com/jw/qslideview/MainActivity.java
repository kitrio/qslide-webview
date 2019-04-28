package com.jw.qslideview;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.lge.app.floating.FloatableActivity;
import com.lge.app.floating.FloatingWindow;


public class MainActivity extends FloatableActivity {

    private WebView mWebView;
    private WebSettings mWebSetting;
    private ImageButton qslideButton;
    private ImageButton webViewBackButton;
    private ImageButton mainMenuButton;
    private EditText etAddress;
    private ValueCallback<Uri[]> mfilePathCallback;
    public  static final int INPUT_FILE_REQUEST_CODE = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAddress = (EditText)findViewById(R.id.etAddress);
        qslideButton = (ImageButton) findViewById(R.id.button_qslide);
        webViewBackButton = (ImageButton) findViewById(R.id.button_wvBack);
        mainMenuButton = (ImageButton) findViewById(R.id.button_mainMenu);

        mWebView = (WebView) findViewById(R.id.webViewMain);
        mWebView.setWebChromeClient(new myWebChromeClient());//this = webview
        mWebView.setWebViewClient(new myWebClient());

        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSetting.setSupportMultipleWindows(true);
        mWebSetting.setDomStorageEnabled(true);
        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setAllowFileAccess(true);
        mWebView.loadUrl("https://m.naver.com");

        qslideButton.setOnTouchListener(qListener);

        etAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("button","web enterkey");
                    String url = etAddress.getText().toString();
                    if(!url.startsWith("http://") && !url.startsWith("https://")){
                        url = "https://"+url;
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

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popMenu = new PopupMenu(
                        getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.main_menu, popMenu.getMenu());
                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        return false;
                    }
                });
                popMenu.show();
            }
        });

    }


    public class myWebClient extends WebViewClient
    {
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            // TODO Auto-generated method stub
//             super.onPageStarted(view, url, favicon);
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
                MainActivity.this.startActivityForResult(contentIntent,INPUT_FILE_REQUEST_CODE);
            } catch (ActivityNotFoundException e){
                mfilePathCallback = null;
                return false;
            }
            return true;
        }

//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            return super.onJsAlert(view, url, message, result);
//        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

//            new AlertDialog.Builder(view.getContext()).setMessage(message)
//                    .setPositiveButton(android.R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    result.confirm();
//                                }
//                            })
//                    .setNegativeButton(android.R.string.cancel,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    result.cancel();
//                                }
//                            })
//                    .create()
//                    .show();
//            return true;

            mContext = getWindow().getContext();
            final Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                dialog.setContentView(R.layout.custom_dialog);
                final TextView tvTitle = (TextView) dialog.findViewById(R.id.titleAlert);
                final Button btnOK = dialog.findViewById(R.id.button_ok);
                final Button btnCancel = dialog.findViewById(R.id.button_cancel);
                tvTitle.setText(message);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.confirm();
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.cancel();
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();

                return  true;
        }


    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == INPUT_FILE_REQUEST_CODE) {
                if (mfilePathCallback == null) return;

                mfilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mfilePathCallback = null;
            }super.onActivityResult(requestCode,INPUT_FILE_REQUEST_CODE,data);
            onStop();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
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

        Log.d("WindowFlow","onAttachedToFloatingWindow.");
        /* all resources should be reinitialized once again
         * if you set new layout for the floating mode setContentViewForFloatingMode()*/
        //mContext = w.getContentView().getContext();
        qslideButton = (ImageButton)findViewById(R.id.button_qslide);
        mContext = this.getFloatingWindow().getContentView().getContext();

        // and also listeners a should be added once again to the buttons in floating mode
        qslideButton.setOnTouchListener(qListener);

        w.setSize(700,1000);


        w.setOnUpdateListener(new FloatingWindow.DefaultOnUpdateListener() {

            @Override
            public void onResizeFinished(FloatingWindow window, int width,
                                         int height) {
                DisplayMetrics disp = getApplicationContext().getResources().getDisplayMetrics();
                int deviceWidth = disp.widthPixels;
                int deviceHeight = disp.heightPixels;

                //resizedWidthRatio = (float) width / deviceWidth;
                //((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
                //mLunarView.setResizeRatio(resizedWidthRatio);
            }
        });
        // set an onUpdateListener to limit the width of the floating window



    }
    // This is called when the floating window is closed.
    @Override
    public boolean onDetachedFromFloatingWindow(FloatingWindow w, boolean isReturningToFullScreen) {
        Log.d("WindowFlow","onDetachedFromFloatingWindow. Returning to Fullscreen: " + isReturningToFullScreen);

//        //set the last position of the floating window when the window is closing
//        Intent intent = getIntent();
//        intent.putExtra("posX", w.getLayoutParams().x);
//        intent.putExtra("posY", w.getLayoutParams().y);

        return true;
    }
    @Override
    public void switchToFloatingMode() {
        if (onStartedAsFloatingMode()) {
            setDontFinishOnFloatingMode(true);
        }
        super.switchToFloatingMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(onStartedAsFloatingMode()){
            Log.d("isStartedFloat","isfloat");
            //setDontFinishOnFloatingMode(true);
        }
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
        if (isSwitchingToFloatingMode()) {
            // release application specific resources here
            // only when application isn't switching to floating window mode
        }
    }

}

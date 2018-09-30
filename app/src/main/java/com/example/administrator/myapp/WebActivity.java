package com.example.administrator.myapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebActivity extends AppCompatActivity {

    private final static String url_air = "http://192.168.0.120:1880/gse/pages/indexA320.html";
    private final static String url_test = "http://192.168.0.238:8080/input.html";
    private final static int PHOTO_REQUEST = 100;
    //private final static int VIDEO_REQUEST = 120;
    private File fileUri;
    WebView mWebview;
    WebSettings mWebSettings;
    TextView beginLoading,mtitle;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        //imageUri = Uri.fromFile(fileUri);
        mWebview = (WebView) findViewById(R.id.webView1);
        //beginLoading = (TextView) findViewById(R.id.text_beginLoading);
        //mtitle = (TextView) findViewById(R.id.title);

        mWebSettings = mWebview.getSettings();
        //设置webview 对本地文件访问权限属性
        //允许webview对文件的操作
        mWebSettings.setAllowFileAccess(true);
//        mWebview.loadDataWithBaseURL( "file:///android_asset/", "html", "text/html",
//                "utf-8", null );

        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        mWebSettings.setBuiltInZoomControls(true); // 显示放大缩小
        mWebSettings.setSupportZoom(true); // 可以缩放
        // 一定要在这里设置javascript属性
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultFontSize(12);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.loadUrl(url_air);
        //在Js类里实现javascript想调用的方法(H5调用android)，"jsObj"就是这个接口的别名
        mWebview.addJavascriptInterface(new Js(), "jsObj");
        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {

            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                //mtitle.setText(title);
            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                    //beginLoading.setText(progress);
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                    //beginLoading.setText(progress);
                }
            }
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                AlertDialog.Builder b2 = new AlertDialog.Builder(WebActivity.this)
                        .setTitle("提示").setMessage(message)
                        .setPositiveButton("ok",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();

//                if (message != null) {
//                    Toast.makeText(WebActivity.this, message, Toast.LENGTH_SHORT).show();
//                }
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      JsPromptResult result) {
                // TODO Auto-generated method stub
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                //beginLoading.setText("开始加载了");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                //beginLoading.setText("结束加载了");

            }
        });

        /*
        mWebview.evaluateJavascript("javascript:callJS()", new ValueCallback<String>(){
            @Override public void onReceiveValue(String value) {
                //此处为 js 返回的结果
            }
        });
        */
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }


    public void onGoSite(View view){
        EditText gourl = (EditText)findViewById(R.id.editGo);
        String url = gourl.getText().toString();
        mWebview.loadUrl(url);
        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    public void setPlatformType(final String result) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //android调用H5代码
                mWebview.loadUrl("javascript: cameraResult('"+ result.toString() + "')");
            }
        }, 1000);
    }

    class Js {
        @JavascriptInterface
        public void goAppCamera() {
            takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == PHOTO_REQUEST ) {
//            Uri[] results = null;
//            if (resultCode == Activity.RESULT_OK) {
//                if (intent == null) {
//                    results = new Uri[]{imageUri};
//                } else {
//
//                }
//                Bitmap bitmap = BitmapFactory.decodeFile(results[0].getPath());
//                Bitmap compressBitmap = compressImage(bitmap);
//                Log.d("it520", "else中的bitmapToBase64被调了...");
//                String str = bitmapToBase64(compressBitmap);
//                setPlatformType(str);
//            }
//        }
        /*
        if (requestCode == PHOTO_REQUEST ) {
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent == null) {
                    results = new Uri[]{imageUri};
                } else {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }

                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        //mUploadCallbackAboveL.onReceiveValue(results);
        //mUploadCallbackAboveL = null;
        */

        ///new 2 method

        Uri uri = intent.getData();
        if(uri == null){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Bitmap  bitmap = (Bitmap) bundle.get("data"); //get bitmap
                saveBitmapFile(bitmap);
                Bitmap compressBitmap = compressImage(bitmap);
                Log.d("it520", "bitmapToBase64被调了...");
                String str = bitmapToBase64(compressBitmap);
                setPlatformType(str);
            }
        }else {
            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
            saveBitmapFile(bitmap);
            Bitmap compressBitmap = compressImage(bitmap);
            Log.d("it520", "else中的bitmapToBase64被调了...");
            String str = bitmapToBase64(compressBitmap);
            setPlatformType(str);
        }
    }
    public void takePhoto() {
        PhotoUtils.takePicture(WebActivity.this, imageUri, PHOTO_REQUEST);
    }

    ////img deal
    /**
     * 图片压缩
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 60, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//因为获取到的ByteArrayOutputStream大小基本为50几kb，所以不用压缩，所以以下代码注释掉
//        int options = 90;
//        while (baos.toByteArray().length / 1024 > 200) {// 循环判断如果压缩后图片是否大于200kb,大于继续压缩
//            baos.reset(); // 重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options，把压缩后的数据存放到baos中
//            options -= 10;// 每次都减少10
//        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // bitmap转base64
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = "data:image/jpg;base64,";//必须加上“data:image/png;base64”图片的数据格式H5才能识别出来
        ByteArrayOutputStream bos = null;
        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);// 将bitmap放入字节数组流中
                bos.flush();// 将bos流缓存在内存中的数据全部输出，清空缓存
                bos.close();
                byte[] bitmapByte = bos.toByteArray();
                result += Base64.encodeToString(bitmapByte, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("it520", "result=" + result);
        Log.d("it520", "size=" + bos.toByteArray().length / 1024);//获取ByteArrayOutputStream的大小，单位kb，
        return result;
    }

    /**
     * base64转Bitmap
     * @param base64String
     * @return
     */
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }


    /**
    //写图片文件到本地
     */
    public static void saveBitmapFile(Bitmap bitmap) {
        try{
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
            FileOutputStream fos= null;
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,60, fos);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 上传图片码流Base64到服务器
     */
    private boolean upLoad(final String strBase64) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("jpg", strBase64);
                    HttpGetter http = new HttpGetter();
                    http.upload("http://192.168.0.238:8080/upload",  jsonObject.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }
}

package com.example.administrator.myapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapp.dispatch.LinePathView;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WebActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static String url_air = "http://192.168.0.120:1880/gse/pages/indexA320.html";
    private final static String url_test = "http://192.168.0.238:8080/input.html";
    private final static int PHOTO_REQUEST = 100;
    //private final static int VIDEO_REQUEST = 120;
    private File fileUri;
    private File fileDir;
    private WebView mWebview;
    private WebSettings mWebSettings;
    private TextView beginLoading,mtitle;
    private Uri imageUri;
//    private LinePathView mLinePathView;
//    private PopupWindow  mShowDlg;
//    private View mShareView;

    private ProgressDialog waitDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" +  "GsePhoto");
        if ( !fileDir.exists()){
            fileDir.mkdir();
        }
        fileUri = new File(fileDir.getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        imageUri = Uri.fromFile(fileUri);
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
        mWebview.loadUrl(url_test);

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


//            @Override
//            public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request) {
//                FileInputStream input;
//                String url = request.getUrl().toString();
//                    String key = "http://androidimg";
//                    /*如果请求包含约定的字段 说明是要拿本地的图片*/
//                    if (url.contains(key)) {
//                        String imgPath = url.replace(key, "");
//                        Log.v("本地图片路径：", imgPath.trim());
//                        try {
//                            /*重新构造WebResourceResponse  将数据已流的方式传入*/
//                            input = new FileInputStream(new File(imgPath.trim()));
//                            WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);
//                            /*返回WebResourceResponse*/
//                            return response;
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                }
//                return super.shouldInterceptRequest(view, request);
//            }

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
                String strHeader = "data:image/jpg;base64,";//必须加上“data:image/png;base64”图片的数据格式H5才能识别出来
                strHeader += result;
                mWebview.loadUrl("javascript: cameraResult('"+ strHeader.toString() + "')");
            }
        }, 1000);
    }

    class Js {
        @JavascriptInterface
        public void goAppCamera() {
            takePhoto();
        }
        @JavascriptInterface
        public void goSigned() {
            onSigned();
        }
    }

    //将图片文件添加至相册（便于浏览）
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PHOTO_REQUEST){
            if (intent != null) {
                Uri uri = intent.getData();
                if(uri == null){
                    Bundle bundle = intent.getExtras(); //从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
                    if (bundle != null) {
                        Bitmap bitmap = (Bitmap) bundle.get("data"); //将data中的信息流解析为Bitmap类型
                        bitmap = drawDate2Bitmap(bitmap);
                        saveBitmapFile(bitmap);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                        Bitmap compressBitmap = PhotoUtils.compressImage(bitmap);
                        Log.d("it520", "uri == null, bitmapToBase64被调了...");
                        String str = bitmapToBase64(compressBitmap);
                        setPlatformType(str);
                        //upLoad(str);
                    }
                }else {
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                    bitmap = drawDate2Bitmap(bitmap);
                    saveBitmapFile(bitmap);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                    Bitmap compressBitmap = PhotoUtils.compressImage(bitmap);
                    Log.d("it520", "uri != null, else中的bitmapToBase64被调了...");
                    String str = bitmapToBase64(compressBitmap);
                    setPlatformType(str);
                    //upLoad(str);
                }
            } //if (intent != null)
            else {

                /*
                int permission = ActivityCompat.checkSelfPermission(WebActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(WebActivity.this, PERMISSIONS_STORAGE,
                           REQUEST_EXTERNAL_STORAGE);
                }*/

                String path = imageUri.getPath();
                Bitmap bitmap = null;
                try{
                    //bitmap = this.decodeFile(path);
                    bitmap = PhotoUtils.getBitmapFormUri(this, imageUri);
                }catch(IOException e){
                    e.printStackTrace();
                }
                Bitmap compressBitmap = PhotoUtils.compressImage(bitmap);
                Bitmap bitmap2 = drawDate2Bitmap(compressBitmap);
                Log.d("it520", "intent == null, else中的bitmapToBase64被调了...");
                String str = bitmapToBase64(bitmap2);
                setPlatformType(str);
                galleryAddPic(); //将照片添加到相册
                upLoad(str);
            }
        }
    }


    public void handlerCamera(){
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] CameraIdList = cameraManager.getCameraIdList();
            //获取可用相机设备列表
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(CameraIdList[0]);
            //在这里可以通过CameraCharacteristics设置相机的功能,当然必须检查是否支持
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (configurationMap != null) {
                //获取图片输出的尺寸
                Size[]  size = configurationMap.getOutputSizes(ImageFormat.JPEG);
                Log.v("setting photo", "1111");
            }

            Camera  mCameraDevice = android.hardware.Camera.open(0);
            Camera.Parameters mParameters = mCameraDevice.getParameters();
            List<Camera.Size> litsize = mParameters.getSupportedPictureSizes();
            for (int i=0; i<litsize.size();i++){
                Camera.Size size = litsize.get(i);
                Log.v("Pic size :", String.valueOf(size.height) + " ---" + String.valueOf(size.width));
            }
            mParameters.setJpegQuality(50);
            mParameters.setPictureSize(1280, 720);
            mCameraDevice.setParameters(mParameters);
            mCameraDevice.release();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据 路径 得到 file 得到 bitmap
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Bitmap decodeFile(String filePath) throws IOException{
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 600;

        File f = new File(filePath);
        if (f == null){
            return null;
        }
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();
        return b;
    }

    public void takePhoto() {
        //handlerCamera();
        PhotoUtils.takePicture(WebActivity.this, imageUri, PHOTO_REQUEST);
    }


    public static Bitmap drawDate2Bitmap(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String date = sdf.format(new Date());
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true); // 获取可改变的位图
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.RED);
        // text size in pixels
        paint.setTextSize(16);
        // text shadow
        // paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds(date, 0, date.length(), bounds);
        //String location = "南方航空 ";
        //Rect bounds2 = new Rect();
        //paint.getTextBounds(location, 0, location.length(), bounds2);
        int x = (bitmap.getWidth() - bounds.width());
        canvas.drawText(date, x - 10, bitmap.getHeight() - 10, paint);
        //x = (bitmap.getWidth() - bounds.width() - bounds2.width());
        //canvas.drawText(location, x - 15, bitmap.getHeight() - 10, paint);
        canvas.save();
        return bitmap;
    }

    ////img deal
    /**
     * 图片压缩
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 65, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        //因为获取到的ByteArrayOutputStream大小基本为50几kb，所以不用压缩，所以以下代码注释掉
        /*
        int options = 90;
        while (baos.toByteArray().length / 1024 > 200) {// 循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
           image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }*/
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // bitmap转base64
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将bitmap放入字节数组流中
                bos.flush();// 将bos流缓存在内存中的数据全部输出，清空缓存
                bos.close();
                byte[] bitmapByte = bos.toByteArray();
                result = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
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
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
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
                    jsonObject.put("Jpg", "1123#DDDDDDDD8888888888888&999999999999999");
                    jsonObject.put("Sign", "1");
                    jsonObject.put("Cam", 2);
                    HttpGetter http = new HttpGetter();
                    http.uploadJson("http://192.168.0.238:8080/uploadJson",  jsonObject.toString());

                    /*
                    HttpGetter http = new HttpGetter();
                    http.HttpPost(Cmd.STR_CMD_UPLOAD_PHOTO, strBase64, 3000);
                    */
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }


    /**
     * 处理签名
     */
    public void onSigned() {
        //singed
        View mShareView = LayoutInflater.from(this).inflate(R.layout.showsignature, null);
        final LinePathView mLinePathView = (LinePathView) mShareView.findViewById(R.id.PAINT_SIGN);
        mLinePathView.setBackColor(Color.WHITE);
        mLinePathView.setPaintWidth(10);
        mLinePathView.setPenColor(Color.BLACK);
        mLinePathView.clear();

        final PopupWindow mShowDlg = new PopupWindow(mShareView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        mShowDlg.setOutsideTouchable(true);
        mShowDlg.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        mShowDlg.setFocusable(true);

        mShareView.findViewById(R.id.BTN_SIGN_CANCEL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowDlg.dismiss();
            }
        });

        mShareView.findViewById(R.id.BTN_SIGN_CLEAR).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinePathView.clear();
            }
        });

        mShareView.findViewById(R.id.BTN_SIGN_OK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinePathView.getTouched()) {
                    try {
                        String strpath ;
                        strpath = getExternalFilesDir("/") + "/signature1.png";
                        mLinePathView.save(strpath, true, 10);
                        setResult(100);
                        mShowDlg.dismiss();
                        UploadSignPng(strpath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(WebActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mShowDlg.showAtLocation(mShareView.getRootView(), Gravity.CENTER, 0, 0);
        mLinePathView.clear();
    }


    /**
      * 处理图片上传
     */
    private String strLocalPath = "";
    public void UploadSignPng(String strpath)
    {
        waitDlg = new ProgressDialog(this);
        waitDlg.setMessage("正在上传签名，请稍候..");
        waitDlg.show();

        strLocalPath = strpath;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // /* sessionid=094505.321&cmd=B2&CmbID=12345&OperatorNo=A030&BridgeName=203A */
                String param = "&CmbID=" + "1102" + "&OperatorNo=" + "lisi" + "&pictype=png";
                HttpGetter httpGet = new HttpGetter();
                boolean res = httpGet.uploadFile(param, strLocalPath, "1102" + ".png");
                String param2 = "11122";
                //deal picture upload
                //String param = "&operatorno=" +LoginActivity.strWorkerID + "&bridgename=" + mSchd.strBridgeName + "&cmbid=" + mSchd.nCmbID;
                //String httpresult = mContext.mHttpGet.HttpGet(Cmd.STR_CMD_GET_DEV_CHARGE, param);
                mHandler.sendMessage(mHandler.obtainMessage(MSG_UPLOAD_FINISH, res ? strLocalPath : null));
            }
        }).start();
    }

    private String strSignPic = "";
    private static final int MSG_LOAD_SPEC_FINISH = 2001;
    private static final int MSG_UPLOAD_FINISH = 2002;
    private static final int MSG_GETHTTP_PIC_FINISH = 2003;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull  Message msg) {
            super.handleMessage(msg);
            String loadresult;
            switch (msg.what) {
                case MSG_UPLOAD_FINISH:
                    waitDlg.dismiss();
                    loadresult = (String) msg.obj;
                    if (loadresult == null) {
                        Toast.makeText(WebActivity.this, "加载失败，请稍后再试..", Toast.LENGTH_LONG).show();

                    }
                    break;
                case MSG_LOAD_SPEC_FINISH:
                    waitDlg.dismiss();
                    loadresult = (String) msg.obj;
                    if (loadresult == null) {
                        Toast.makeText(WebActivity.this, "上传失败，请稍后再试..", Toast.LENGTH_LONG).show();

                    }
                    break;
                case MSG_GETHTTP_PIC_FINISH:
                    break;
            }
        }
    };
}

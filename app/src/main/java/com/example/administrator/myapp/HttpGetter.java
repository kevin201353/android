package com.example.administrator.myapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class HttpGetter {

    public static final int HTTP_LOGINTIMEOUT = 10 * 1000;
    public static final int HTTP_DATATIMEOUT = 30 * 1000;


    private String strUsername;
    private String strAirportCode;
    private int   nWorkStatus;

    public String getUserName() {
        return strUsername;
    }

    public String getStrAirportCode() {
        return strAirportCode;
    }

    public int getnWorkStatus() {
        return nWorkStatus;
    }

    protected String MakeUrl(String addr,  int port) {
        return "http://" +  addr + ":" + String.valueOf(port);
    }

    protected  String MakePngUrl(String addr, int port, String pngPath) {
        return "http://" + addr + ":" + String.valueOf(port) + "/" + pngPath;
    }

    protected  String MakeUrl() {
        return "http://" +  "192.168.0.238" + ":" + "8080";
    }

    private int ParseLoginResult(String jstr) {
        if (jstr.isEmpty()) {
            return Cmd.ERROR_CODE_CONNSERVICE_SYNERROR;
        }
        try {
            JSONObject jsonObject = new JSONObject(jstr);
            int errorcode = jsonObject.getInt("errorcode");
            if (errorcode == 0) {
                strUsername = jsonObject.getString("operatorname");
                //LoginActivity.strSessionToken = jsonObject.getString("tocken");
                if (jsonObject.has("airport")) {
                    strAirportCode = jsonObject.getString("airport");
                }
                if (jsonObject.has("onduty")) {
                    nWorkStatus = jsonObject.getInt("onduty");
                }

                return Cmd.ERROR_CODE_CONNSERVICE_SUCCESS;
            } else if (errorcode == 1) {
                return Cmd.ERROR_CODE_PASS_ERROR;
            } else if (errorcode == 2) {
                return Cmd.ERROR_CODE_USER_ACCESS;
            } else if (errorcode == 3) {
                return Cmd.ERROR_CODE_USER_EXISTS;
            } else if (errorcode == 4) {
                return Cmd.ERROR_CODE_CHECKCODE_ERROR;
            } else {
                return Cmd.ERROR_CODE_CONNSERVICE_REGFAIL;
            }
        } catch (JSONException e) {
            Log.v("Login", "[HttpGetter::ParseLoginResult] JSONException -- " + e.toString());
            return Cmd.ERROR_CODE_CONNSERVICE_SYNERROR;
        }
    }

    public int HttpLogin(String url) {
        try {
            URL ur = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) ur.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(HTTP_LOGINTIMEOUT);
            connection.setReadTimeout(HTTP_LOGINTIMEOUT);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer strinbuffer = new StringBuffer("");
            char[] buff = new char[1024];
            int readlen;
            Log.v("Login", "[HttpGetter] read start\r\n");
            while (true) {
                readlen = in.read(buff, 0, 1024);
                if (readlen < 0) {
                    break;
                }
                if (readlen > 0) {
                    String strread = new String(buff, 0, readlen);
                    strinbuffer.append(strread);
                    Log.v(TAG, strread);
                }
            }
            // parse login info
            Log.v("Login", "[HttpGetter] recv=" + strinbuffer.toString());
            return ParseLoginResult(strinbuffer.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }
        return  0;
    }

    /**
    *  //post 普通数据
     */
    public String HttpPost(String cmd, String para, int timeoutMillis)
    {
        StringBuffer url = new StringBuffer(MakeUrl() + "/" + "upgernal" + "?" ); // http连接
        url.append("sessionid=" + "123456"); // 此次登录会话ID
        url.append("&cmd=" + cmd);

        Log.v(TAG, "[HttpGetter] HttpPost url=" + url.toString());
        Log.v(TAG, "[HttpGetter] HttpPost para=" + para.toString());

        try {
            URL ur = new URL(url.toString());
            HttpURLConnection connection = (HttpURLConnection) ur.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(timeoutMillis);
            connection.setReadTimeout(timeoutMillis);
            connection.setDoOutput(true);
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(para.getBytes());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer strinbuffer = new StringBuffer("");

            char[] buff = new char[1024];
            int readlen;
            Log.v(TAG, "[HttpGetter] read start\r\n");
            while (true) {
                readlen = in.read(buff, 0, 1024);
                if (readlen < 0) {
                    break;
                }
                if (readlen > 0) {
                    String strread = new String(buff, 0, readlen);
                    strinbuffer.append(strread);
                    Log.v(TAG, strread);
                }
            }
            Log.v(TAG, "[HttpGetter] read end\r\n");
            return strinbuffer.toString();

        } catch (SocketTimeoutException e) {
            Log.v(TAG, "[HttpGetter] http Exception!!!! - " + e.toString());
            return null;
        } catch (Exception e) {
            Log.v(TAG, "[HttpGetter] http Exception!!!! - " + e.toString());
            return null;
        }
    }

    /**
    *  http 上传json 数据
    * */
    public int uploadJson(String url, String json) {
        Log.v("http upload json: ", json);
        String result = "";
        BufferedReader reader = null;
        try {
            URL ur = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) ur.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(HTTP_LOGINTIMEOUT);
            connection.setReadTimeout(HTTP_LOGINTIMEOUT);
            //设置是否向httpURLConnection输出，因为post请求参数要放在http正文内，所以要设置为true
            connection.setDoOutput(true);
            //设置是否从httpURLConnection读入，默认是false
            connection.setDoInput(true);
            //POST请求不能用缓存，设置为false
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            connection.setRequestProperty("accept","application/json");
            // 往服务器里面发送数据
            if (json != null && !TextUtils.isEmpty(json)) {
                byte[] writebytes = json.getBytes();
                // 设置文件长度
                connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = connection.getOutputStream();
                outwritestream.write(json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                connection.connect();
                Log.d("hlhupload", "doJsonPost: conn"+connection.getResponseCode());
            }
            if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                result = reader.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * Android上传文件到服务端
     *
     * @param localpath 需要上传的文件路径
     * @param png 图片名称（CmbID.png）
     * @return 返回响应的内容
     */
    public boolean uploadFile(String para, String localpath, String png) {

        File file = new File(localpath);
        Log.d(TAG, "uploadFile: file.length()=" + file.length());
        final String CHARSET = "utf-8";
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            //URL url = new URL(MakePngUrl(png));
            StringBuffer url = new StringBuffer(MakeUrl() + "/?"); // http连接
            url.append("sessionid=" + "20181009"); // 此次登录会话ID
            url.append("&cmd=" + Cmd.STR_CMD_UPLOAD_SIGNIMG + para); // 其他参数
            Log.v(TAG, "[HttpGetter] url=" + url.toString());

            URL ur = new URL(url.toString());
            //URL url = new URL("http://116.62.105.113:8080/upload/"); // for test
            HttpURLConnection conn = (HttpURLConnection) ur.openConnection();
            conn.setRequestMethod("POST"); // 请求方式
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + png + "\"" + LINE_END);
                sb.append("Content-Type: multipart/form-data; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);

                Log.d(TAG, "uploadFile: POST write: " + sb);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0; int alllen = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len); alllen += len;
                    Log.d(TAG, "uploadFile: write len=" + len +" alllen:"+alllen);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();

                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    Log.d(TAG, "result : " + sb1.toString());

                } else {
                    Log.d(TAG, "上传失败：code=" + res);
                }

                return true;
            }
            else {
                Log.d(TAG, "uploadFile: localfile==null!!! path=" + localpath);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadFile1: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "uploadFile2: " + e.toString());
        }
        return false;
    }
}

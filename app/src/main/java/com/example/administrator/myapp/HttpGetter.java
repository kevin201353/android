package com.example.administrator.myapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
package com.example.administrator.myapp;

public class Cmd {

    public static final int    HEAD_LEN 					= 11; // 消息头长度(指令+长度)

    public static final String HEAD_SPLIT 					= "#CMD@"; // 指令头
    /* 退出登录指令 */
    public static final String STR_CMD_UNREGISTER 		    = "75";
    /* 注册指令 */
    public static final String STR_CMD_REGISTER 			= "76";

    public static final String STR_CMD_UPLOAD_SIGNIMG       = "77";

    public static final String STR_CMD_UPLOAD_PHOTO       = "78";

    public static final int ERROR_CODE_CONNSERVICE_SUCCESS  = 0;   // 成功
    public static final int ERROR_CODE_CONNSERVICE_TIMEOUT  = -1;  // 连接超时
    public static final int ERROR_CODE_CONNSERVICE_REGFAIL  = -2;  // 登录失败
    public static final int ERROR_CODE_CONNSERVICE_SYNERROR = -3;  // 消息格式错误
    public static final int ERROR_CODE_CONNSERVICE_EXCPT  	= -4;  // 其他异常
    public static final int ERROR_CODE_HOSTNAME_ERROR  		= -5;  // 域名解析错误
    public static final int ERROR_CODE_USER_EXISTS  		= -6;  // 该用户已经登录
    public static final int ERROR_CODE_PASS_ERROR  			= -7;  // 用户名或者密码错误
    public static final int ERROR_CODE_USER_ACCESS  		= -8;  // 用户权限错误
    public static final int ERROR_CODE_CHECKCODE_ERROR  	= -9;  // 验证码错误（手机号错误，手机号和工号对应不上）
}

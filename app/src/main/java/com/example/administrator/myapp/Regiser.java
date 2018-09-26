package com.example.administrator.myapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Regiser extends AppCompatActivity {

    private String strPhone;
    private String strVerify;
    private String strPassword;
    private String strDupli_pwd;

    private EditText  edit_phone;
    private EditText  edit_verify;
    private Button btn_verify;
    private EditText  edit_password;
    private EditText  edit_dupPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiser);
        edit_phone = (EditText) findViewById(R.id.editPhone);
        edit_verify = (EditText) findViewById(R.id.edit_verify);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_dupPwd = (EditText) findViewById(R.id.edit_duplicate);
    }
}

package com.example.administrator.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;

public class LoginActivity extends AppCompatActivity {

    private static int selType;   //0: 航班调度  1：维修人员  2：航班信息 3：引导车调度  4：摆渡车调度
    private static final String[] arrayUserType = {"操作员", "维修员", "航班信息", "引导车", "摆渡车"};
    private Spinner UserTypeSelSpinner;
    private EditText address;
    private Editable strAddr;
    private AutoCompleteTextView User;
    private Editable strUser;
    private String   strSelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v("LoginAct", "Login Activity");
        UserTypeSelSpinner = (Spinner)findViewById(R.id.spinnerSel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, arrayUserType);
        adapter.setDropDownViewResource(R.layout.dropdown_stytle);
        UserTypeSelSpinner.setAdapter(adapter);
        //address
        address = (EditText)findViewById(R.id.edit_addr);
        //user
        User = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView_Emple);
        String[] emp_array = getResources().getStringArray(R.array.emp_array);
        ArrayAdapter<String> arryEmpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, emp_array);
        User.setAdapter(arryEmpAdapter);
        //set spinner listener
        UserTypeSelSpinner.setOnItemSelectedListener(new Spinner2Listener());
    }

    public void OnLoginClick(View view) {
        strAddr = address.getText();
        strUser = User.getText();
        StringBuilder builerMsg = new StringBuilder();
        builerMsg.append("seltype: ");
        builerMsg.append(strSelType + " ");
        builerMsg.append("addr: " + strAddr + " " + strUser);
        Log.v("Login info: ", builerMsg.toString());
        this.finish();
        startActivity(new Intent(this, frameActivity.class));
    }

    public class Spinner2Listener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            strSelType = parent.getItemAtPosition(position).toString();
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

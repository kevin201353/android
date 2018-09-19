package com.example.administrator.myapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;

import javax.xml.transform.Result;

public class LoginActivity extends AppCompatActivity {

    private static int selType;   //0: 航班调度  1：维修人员  2：航班信息 3：引导车调度  4：摆渡车调度
    private static final String[] arrayUserType = {"操作员", "维修员", "航班信息", "引导车", "摆渡车"};
    private Spinner UserTypeSelSpinner;
    private EditText address;
    private Editable strAddr;
    private AutoCompleteTextView User;
    private Editable strUser;
    private String   strSelType;
    private ProgressBar progressBar; // 进度条
    private MyLoginTask  myTask;
    private Boolean  bLogining = false;

    private Handler handler;
    private  MyThread mythrd;
    private  Thread  mthrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bLogining = false;
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.INVISIBLE);
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
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        progressBar.setProgress(0);
        myTask = new MyLoginTask();
    }

    public void OnLoginClick(View view) {
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
       // if (!bLogining) {
           // myTask.execute();  //excute my task.
            bLogining = true;
            btnLogin.setVisibility(View.INVISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
       // }

        mythrd = new MyThread();
        mthrd = new Thread(mythrd);
        mthrd.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        progressBar.setProgress(Integer.parseInt(String.valueOf(msg.obj)));
                        break;
                    case 0:
                        progressBar.setProgress(0);
                        Button btnLogin = (Button)findViewById(R.id.btnLogin);
                        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
                        btnLogin.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };

//        strAddr = address.getText();
//        strUser = User.getText();
//        StringBuilder builerMsg = new StringBuilder();
//        builerMsg.append("seltype: ");
//        builerMsg.append(strSelType + " ");
//        builerMsg.append("addr: " + strAddr + " " + strUser);
//        Log.v("Login info: ", builerMsg.toString());
//        this.finish();
//        startActivity(new Intent(this, frameActivity.class));
    }


    private class MyThread implements Runnable {
        @Override
        public void run() {
            int ncount = 0;
            int length = 1;
            while (ncount < 99) {
                try {
                    ncount += length;
                    length++;
                    Message msg = new Message();
                    msg.obj = String.valueOf(ncount);
                    msg.what = 1;
                    handler.sendMessage(msg);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }//while
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    public void OnCancelClick(View view) {
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
       /// if (bLogining) {
        bLogining = false;
        btnLogin.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
        progressBar.setProgress(0);
        handler.removeCallbacks(mythrd);
        mthrd.interrupt();
           // myTask.cancel(true);
       // }
    }

    public class Spinner2Listener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            strSelType = parent.getItemAtPosition(position).toString();
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private  class MyLoginTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                int ncount = 0;
                int length = 1;
                while (ncount < 99) {
                    ncount += length;
                    length++;
                    publishProgress(ncount);
                    Thread.sleep(2000);
                }

            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled(String result)
        {
            super.onCancelled();
            progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.v("values", values[0].toString());
            progressBar.setProgress(values[0]);
        }
    }
}

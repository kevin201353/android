package com.example.administrator.myapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    public  static final String EXTRA_MESSAGE = ".com.example.app.MESSAGE";
    private static final int GET_CODE = 0;
    private  TipDlg dlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(MainActivity.this, DisplayMsgActivity.class);
        EditText ctrlEdit =  findViewById(R.id.textView);
        String strMsg = ctrlEdit.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, strMsg);
        startActivity(intent);
    }

    public void goBack(View view) {
        //Intent  intent = new Intent(MainActivity.this, DisplayMsgActivity.class);
       // startActivityForResult(intent, GET_CODE);
        /*
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.show();
        */

        /////
        dlg = new TipDlg(MainActivity.this);
        dlg.SetOnYesClickListener(new TipDlg.YesOnClickListener() {
            @Override
            public void onYesClick() {
                Log.v("Result", "ok111");
                dlg.dismiss();
            }
        });
        dlg.SetOnCancelClickListener(new TipDlg.CancelOnClickListener() {
            @Override
            public void onCancelClick() {
                Log.v("Result", "cancel111");
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CODE && resultCode == 2) {
            String strResult = data.getAction();
            Log.v("Result", strResult);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.new_game:
                Log.v("Menu", "new_game");
                break;
            case R.id.help:
                Log.v("Menu", "help11111");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

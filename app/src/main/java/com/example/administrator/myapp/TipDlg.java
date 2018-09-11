package com.example.administrator.myapp;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TipDlg extends Dialog {

    private YesOnClickListener  onYesClickListener;
    private CancelOnClickListener  onCancelClickListener;
    private Button yesBtn;
    private Button cancelBtn;

    public TipDlg(@NonNull Context context) {
        super(context, R.style.TipDlg);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_dlg);
        yesBtn = findViewById(R.id.btn_ok);
        cancelBtn = findViewById(R.id.btn_cancel);
        initEvent();
    }

    public void initEvent() {
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYesClickListener != null) {
                    onYesClickListener.onYesClick();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onCancelClick();
                }
            }
        });
    }

    public void SetOnYesClickListener(YesOnClickListener parOnClickListener){
        if (parOnClickListener !=  null) {
            this.onYesClickListener = parOnClickListener;
        }
    }

    public void SetOnCancelClickListener(CancelOnClickListener parOnClickListener){
        if (parOnClickListener !=  null) {
            this.onCancelClickListener = parOnClickListener;
        }
    }

    //ok, cancel接口
    public interface YesOnClickListener {
        void onYesClick();
    }

    public interface CancelOnClickListener {
        void onCancelClick();
    }
}

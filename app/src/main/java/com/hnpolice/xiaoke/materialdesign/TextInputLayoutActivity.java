package com.hnpolice.xiaoke.materialdesign;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class TextInputLayoutActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_input_layout);

        name = (TextInputLayout) findViewById(R.id.id_name);
        pwd = (TextInputLayout) findViewById(R.id.id_pas);

        name.getEditText().addTextChangedListener(new MyTextWatchername(name, "用户名长度不能小于2位或大于6"));
        pwd.getEditText().addTextChangedListener(new MyTextWatcherpas(pwd, "用户名长度不能小于6位或大于16"));

    }

    //内部类监听EditText的动态
    private class MyTextWatchername implements TextWatcher {
        private TextInputLayout mti;
        private String errorinfo;

        public MyTextWatchername(TextInputLayout mtextInputLayout, String errorinfo) {
            this.mti = mtextInputLayout;
            this.errorinfo = errorinfo;
        }

        //编辑之前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //编辑时
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        //编辑之后
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() < 2 || s.length() > 6) {
                mti.setError(errorinfo);        //错误提示信息
                mti.setErrorEnabled(true);      //是否设置提示错误
            } else {
                mti.setErrorEnabled(false);     //不设置错误提示信息
            }
        }
    }

    //内部类监听EditText的动态
    private class MyTextWatcherpas implements TextWatcher {
        private TextInputLayout mti;
        private String errorinfo;

        public MyTextWatcherpas(TextInputLayout mtextInputLayout, String errorinfo) {
            this.mti = mtextInputLayout;
            this.errorinfo = errorinfo;
        }

        //编辑之前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.e("beforeTextChanged: ", s + ", " + start + ", " + count + ", " + after);

        }

        //编辑时
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 6 || s.length() > 16) {
                mti.setErrorEnabled(true);      //是否设置提示错误
//                mti.setError(errorinfo);        //错误提示信息
            } else {
                mti.setErrorEnabled(false);     //不设置错误提示信息
            }
            Log.e("onTextChanged: ", s + ", " + start + ", " + before + ", " + count);
        }

        //编辑之后
        @Override
        public void afterTextChanged(Editable s) {
            mti.setError(errorinfo);        //错误提示信息
            Log.e("afterTextChanged: ",  mti.getError()+"");
        }
    }
}


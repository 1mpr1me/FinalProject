package ru.myitschool.finalproject;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    Context mContext;
    private String userCode = "";

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void sendCodeToAndroid(String code) {
        Log.d("WebAppInterface", "Received code from JavaScript: " + code);
        if (code != null && !code.trim().isEmpty()) {
            this.userCode = code.trim();
            Log.d("WebAppInterface", "Stored code: " + this.userCode);
        } else {
            Log.e("WebAppInterface", "Received null or empty code");
            this.userCode = "";
        }
    }

    public String getUserCode() {
        Log.d("WebAppInterface", "Getting user code: " + userCode);
        return userCode;
    }
}

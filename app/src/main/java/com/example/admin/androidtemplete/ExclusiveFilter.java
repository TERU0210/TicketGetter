package com.example.admin.androidtemplete;

import android.content.Context;

import com.apkfuns.logutils.LogUtils;

/**
 * Created by Admin on 2016/05/03.
 */
public class ExclusiveFilter {
    private Context context = null;

    public ExclusiveFilter(Context context) {
        this.context = context;
    }

    public boolean isExclusiveTitle(String title) {
        boolean retVal = false;

        // title
        String excludeTitles = context.getString(R.string.excludeTitle);
        LogUtils.d("excludeTitles = " + excludeTitles);
        LogUtils.d("title = " + title);
        for(String t : excludeTitles.split(",")) {
            if(title.indexOf(t) >= 0) {
                retVal = true;
                break;
            }
        }

        return retVal;
    }
}

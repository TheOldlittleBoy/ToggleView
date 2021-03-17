package com.zsl.toggleview;

import android.content.Context;

public class DensityUtil {
    public static int dp2px(Context context,int dimen) {
        return (int) (dimen*(context.getResources().getDisplayMetrics().density)+0.5f);
    }

}

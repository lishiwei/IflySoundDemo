package com.tcl.myapplication.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcl.myapplication.bean.MainBean;

import java.lang.reflect.Type;

/**
 * Created by Howe on 2016/10/23.
 */

public class JsonParserNew {


    private static MainBean mBean;

    /**
     * 返回当前说话的内容
     *
     * @param json
     * @return
     */
    public static MainBean parseIatResult(String json) {
        mBean = new MainBean();
        try {
            Type type = new TypeToken<MainBean>() {
            }.getType();

            mBean = new Gson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBean;
    }
}

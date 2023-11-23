package com.fy.baselibrary.utils.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;

import java.util.Map;

/**
 * describe： SharedPreferences 代理工具类
 * Created by fangs on 2018/12/24 16:39.
 */
final public class SpfAgent {
//    创建的Preferences文件存放位置可以在Eclipse中查看：
//	  DDMS->File Explorer /<package name>/shared_prefs/setting.xml

    private volatile static SpfAgent instance;
    private String mFileName;

    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private SpfAgent(String fileName) {
        SharedPreferences spf = getSpf(fileName);
        this.mFileName = fileName;
        this.editor = spf.edit();
    }


    public static synchronized SpfAgent init() {
        return init("SPFDefaultName");
    }

    public static synchronized SpfAgent init(@NonNull final String fileName) {
        if (null == instance) {
            synchronized (SpfAgent.class) {
                if (null == instance) {
                    instance = new SpfAgent(fileName);
                }
            }
        }

        return instance;
    }


    public static synchronized SpfAgent enableNewSpf(@NonNull final String fileName){
        return new SpfAgent(fileName);
    }


    /**
     * 通过 application 获取 指定名称的 SharedPreferences
     * @param fileName 文件名称
     * @return         SharedPreferences
     */
    public static SharedPreferences getSpf(String fileName){
        Context ctx = ConfigUtils.getAppCtx();
        return ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }



    /**
     * 向 SharedPreferences 保存String 数据
     * @param key   保存的键
     * @param value 保存的内容
     * @return SpfAgent
     */
    public SpfAgent saveString(String key, String value){
        if (TextUtils.isEmpty(value)) value = "";
        this.editor.putString(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 int 数据
     * @param key   保存的键
     * @param value 保存的内容
     * @return SpfAgent
     */
    public SpfAgent saveInt(String key, int value){
        this.editor.putInt(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 long 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveLong(String key, long value){
        this.editor.putLong(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存boolean 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveBoolean(String key, boolean value){
        this.editor.putBoolean(key, value);
        return this;
    }

    /**
     * 向 SharedPreferences 保存 float 数据
     * @param key    key
     * @param value  value
     * @return SpfAgent
     */
    public SpfAgent saveFloat(String key, float value){
        this.editor.putFloat(key, value);
        return this;
    }

    /**
     * 删除 指定 key 的内容
     * @param key
     * @return
     */
    public SpfAgent remove(@NonNull final String key) {
        this.editor.remove(key);
        return this;
    }

    /**
     * 删除 指定 key 的内容
     * @param key      The key of sp.
     * @param isCommit True to use {@link SharedPreferences.Editor#commit()},
     *                 false to use {@link SharedPreferences.Editor#apply()}
     */
    public void remove(@NonNull final String key, final boolean isCommit) {
        this.editor.remove(key);
        commit(isCommit);
    }

    /**
     * 清除所有数据
     * @param isCommit True to use {@link SharedPreferences.Editor#commit()},
     *                 false to use {@link SharedPreferences.Editor#apply()}
     */
    public void clear(final boolean isCommit) {
        this.editor.clear();
        commit(isCommit);
    }

    /**
     * 提交
     * @param isCommit 是否同步提交
     */
    public void commit(final boolean isCommit){
        if (isCommit) {
            this.editor.commit();
        } else {
            this.editor.apply();
        }
    }


    /**
     * 从 SharedPreferences 取String 数据
     * @param key key
     * @return   没有对应的key  默认返回 ""
     */
    public String getString(String key){
        return getSpf(mFileName).getString(key, "");
    }

    public String getString(String key, String defValue){
        return getSpf(mFileName).getString(key, defValue);
    }

    /**
     * 从 SharedPreferences 取int数据
     * @param key key
     * @return   没有对应的key  默认返回 -1
     */
    public int getInt(String key){
        return getSpf(mFileName).getInt(key, -1);
    }
    public int getInt(String key, int defValue) {
        return getSpf(mFileName).getInt(key, defValue);
    }

    /**
     * 从 SharedPreferences 取 long 数据
     * @param key key
     * @return   没有对应的key  默认返回 0
     */
    public long getLong(String key){
        return getSpf(mFileName).getLong(key, 0);
    }
    public long getLong(String key, long defValue){
        return getSpf(mFileName).getLong(key, defValue);
    }

    /**
     * 从 SharedPreferences 获取 float 数据
     * @param key       key
     * @return          没有对应的key 默认返回 0f
     */
    public float getFloat(String key){
        return getSpf(mFileName).getFloat(key, 0f);
    }
    public float getFloat(String key, float defValue){
        return getSpf(mFileName).getFloat(key, defValue);
    }

    /**
     * 从 SharedPreferences 获取 boolean数据
     * @param key   key
     * @return      没有对应的key 默认返回false
     */
    public boolean getBoolean(String key){
        return getSpf(mFileName).getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def){
        return getSpf(mFileName).getBoolean(key, def);
    }


    /**
     * 获取所有键值对
     * @return
     */
    public Map<String, ?> getAll(){
        return getSpf(mFileName).getAll();
    }

}

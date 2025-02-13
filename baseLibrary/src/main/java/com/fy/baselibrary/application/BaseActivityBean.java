package com.fy.baselibrary.application;

import com.fy.baselibrary.statuslayout.StatusLayoutManager;

import java.io.Serializable;

/**
 * 使用 ActivityLifecycleCallbacks 实现给所有 Activity 执行 ButterKnife.bind
 * Created by fangs on 2017/5/18.
 */
public class BaseActivityBean implements Serializable {



//    private BaseOrientoinListener orientoinListener;
//
//    public BaseOrientoinListener getOrientoinListener() {
//        return orientoinListener;
//    }
//
//    public void setOrientoinListener(BaseOrientoinListener orientoinListener) {
//        this.orientoinListener = orientoinListener;
//    }


    private StatusLayoutManager slManager;

    public StatusLayoutManager getSlManager() {
        return slManager;
    }

    public void setSlManager(StatusLayoutManager slManager) {
        this.slManager = slManager;
    }


//    private BehaviorSubject<String> subject;
//
//    public BehaviorSubject<String> getSubject() {
//        return subject;
//    }
//
//    public void setSubject(BehaviorSubject<String> subject) {
//        this.subject = subject;
//    }
}

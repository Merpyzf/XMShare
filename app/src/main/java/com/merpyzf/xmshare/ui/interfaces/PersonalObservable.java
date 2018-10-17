package com.merpyzf.xmshare.ui.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wangke
 * @date 2018/1/31
 * 用户头像昵称变化的被观察者对象
 */

public class PersonalObservable {

    private static PersonalObservable mPersonalObservable;
    private List<PersonalObserver> mObserverList;

    public static PersonalObservable getInstance() {
        if (mPersonalObservable == null) {
            synchronized (Object.class) {
                if (mPersonalObservable == null) {
                    mPersonalObservable = new PersonalObservable();
                }
            }
        }
        return mPersonalObservable;
    }
    private PersonalObservable() {
        mObserverList = new ArrayList<>();
    }
    /**
     * 注册一个观察者
     *
     * @param personalObserver
     */
    public void register(PersonalObserver personalObserver) {
        if (!mObserverList.contains(personalObserver)) {
            mObserverList.add(personalObserver);
        }
    }
    /**
     * 移除一个观察者
     *
     * @param personalObserver
     */
    public void unRegister(PersonalObserver personalObserver) {
        // 移除一个观察者对象
        if (mObserverList.contains(personalObserver)) {
            mObserverList.remove(personalObserver);
        }
    }
    /**
     * 通知所有的观察者
     */
    public void notifyAllObserver(){
        for (PersonalObserver observer : mObserverList) {
            observer.updateUserInfo();
        }
    }
}

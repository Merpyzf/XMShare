package com.merpyzf.xmshare.observer;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.common.Const;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监听已选文件传输列表发生变化的被观察者
 */
public class FilesStatusObservable {

    public static final int FILE_SELECTED = 0x001;
    public static final int FILE_CANCEL_SELECTED = 0x002;
    public static final int FILE_SELECTED_ALL = 0x003;
    public static final int FILE_CANCEL_SELECTED_ALL = 0x004;

    private static FilesStatusObservable sObservable;
    private HashMap<String, FilesStatusObserver> mObserverMap = new HashMap<>();

    private FilesStatusObservable() {
    }

    public static FilesStatusObservable getInstance() {
        if (sObservable == null) {
            synchronized (FilesStatusObservable.class) {
                if (sObservable == null) {
                    sObservable = new FilesStatusObservable();
                }
            }
        }
        return sObservable;

    }

    public void register(String observerName, FilesStatusObserver observer) {
        if (observer != null && mObserverMap.get(observerName) == null) {
            mObserverMap.put(observerName, observer);
        }
    }

    public void remove(FilesStatusObserver observer) {
        if (observer != null && mObserverMap.containsValue(observer)) {
            mObserverMap.values().remove(observer);
        }
    }

    public void remove(String tag) {
        FilesStatusObserver observer = mObserverMap.get(tag);
        if (observer != null) {
            mObserverMap.values().remove(observer);
        }
    }

    public void removeAll() {
        mObserverMap.clear();
    }

    public void notifyObservers(BaseFileInfo changedFile, String observerName, int notifyType) {
        boolean flag = false;
        for (Map.Entry<String, FilesStatusObserver> observerEntry : mObserverMap.entrySet()) {
            FilesStatusObserver observer = observerEntry.getValue();
            String name = observerEntry.getKey();
            // 只有当被观察者是文件传输列表时，才会将文件的变动通知给除了自己以外的所有界面
            if (observerName.equals(Const.HOME_OBSERVER_NAME)) {
                if (!observerName.equals(name)) {
                    switch (notifyType) {
                        case FILE_SELECTED:
                            observer.onSelected(changedFile);
                            break;
                        case FILE_CANCEL_SELECTED:
                            observer.onCancelSelected(changedFile);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                //    当被观察者是文件展示界面时，只需要将当前页面的变化通知给文件传输列表即可
                if (!flag) {
                    FilesStatusObserver homeObserver = mObserverMap.get(Const.HOME_OBSERVER_NAME);
                    if (null != homeObserver) {
                        switch (notifyType) {
                            case FILE_SELECTED:
                                homeObserver.onSelected(changedFile);
                                break;
                            case FILE_CANCEL_SELECTED:
                                homeObserver.onCancelSelected(changedFile);
                                break;
                            default:
                                break;
                        }
                    }
                    flag = true;
                }
            }
        }
    }

    public void notifyObservers(List<BaseFileInfo> changedFiles, String observerName, int notifyType) {

        // 文件传输列表的文件发生改变后才需要将改动通知给所有的文件展示页面
        // 而文件展示页面将文件的改变通知给主页面的文件传输列表的文件是一对一的

        for (Map.Entry<String, FilesStatusObserver> observerEntry : mObserverMap.entrySet()) {
            FilesStatusObserver observer = observerEntry.getValue();
            String name = observerEntry.getKey();
            // 只有当被观察者是文件传输列表时，才会将文件的变动通知给除了自己以外的所有界面
            if (observerName.equals(Const.HOME_OBSERVER_NAME)) {
                if (!observerName.equals(name)) {
                    switch (notifyType) {
                        case FILE_SELECTED_ALL:
                            observer.onSelectedAll(changedFiles);
                            break;
                        case FILE_CANCEL_SELECTED_ALL:
                            observer.onCancelSelectedAll(changedFiles);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                //    当被观察者是文件展示界面时，只需要将当前页面的变化通知给文件传输列表即可
                FilesStatusObserver homeObserver = mObserverMap.get(Const.HOME_OBSERVER_NAME);
                if (null != homeObserver) {
                    switch (notifyType) {
                        case FILE_SELECTED_ALL:
                            homeObserver.onSelectedAll(changedFiles);
                            break;
                        case FILE_CANCEL_SELECTED_ALL:
                            homeObserver.onCancelSelectedAll(changedFiles);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}

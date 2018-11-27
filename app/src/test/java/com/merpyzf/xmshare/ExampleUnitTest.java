package com.merpyzf.xmshare;

import android.graphics.Point;

import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.FormatUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testStr2Unicode() {

        String name = "1";

        String unicode = FormatUtils.string2Unicode(name);
    }

    @Test
    public void testPath() {
        String fileName = "这个世界会好吗(2014i/O版)";
        if (fileName.contains("/")) {
            System.out.println("包含/");
            String s = fileName.replaceAll("/", "-");
            System.out.println(s);
        } else {
            System.out.println("不包含");
        }
    }

    @Test
    public void testThread() {

        ThreadCtrDemo threadCtrDemo = new ThreadCtrDemo();
        threadCtrDemo.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2秒之后线程转向挂起状态
        threadCtrDemo.mySuspend();


        try {
            // 线程挂起2秒钟
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 两秒钟之后线程中的任务继续执行
        threadCtrDemo.myResume();


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2秒钟之后，停止线程任务
        threadCtrDemo.myStop();

    }


    class ThreadCtrDemo extends Thread {

        // 停止
        private final int STOP = -1;
        // 延迟
        private final int SUSPEND = 0;
        // 运行
        private final int RUNNING = 1;
        // 记录当前线程运行的状态
        private int status = 1;
        private long count = 0;


        @Override
        public synchronized void run() {
            super.run();

            while (status != STOP) {


                count++;

                if (status == SUSPEND) {

                    try {

                        System.out.println("线程准备要暂停了");
                        // 阻塞
                        wait();


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    count++;
                    System.out.println(count);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }

        }

        /**
         * 恢复
         */
        public synchronized void myResume() {
            // 修改状态
            status = RUNNING;
            // 唤醒
            notifyAll();
        }

        /**
         * 挂起
         */
        public void mySuspend() {
            // 修改状态
            status = SUSPEND;
        }

        /**
         * 停止
         */
        public void myStop() {
            // 修改状态
            status = STOP;
        }
    }

    @Test
    public void testSplitSuffix() {

        String fileSuffix = FileUtils.getFileSuffix("/storage/emulated/0/xmshare/receive/apk/手机京东.apk");

        System.out.println(fileSuffix);

    }

    @Test
    public void testRx() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("hello");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });


    }

    @Test
    public void testDate() {

        Date date = new Date("2018/4/24");
        System.out.println(date.getTime());
        Date date1 = new Date("2018/4/23");
        System.out.println(date1.getTime());

    }

    @Test
    public void testRxFilter() {

        Observable.just(1)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return true;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });

    }

    @Test
    public void testNullPoint() {

        Point point = null;

        //System.out.println(Objects.requireNonNull(point).x);
        System.out.println(point.x);


    }

    @Test
    public void testArrayHashCode() {

        ArrayList<Integer> integers = new ArrayList<>();
        System.out.println(integers.hashCode());
        integers.add(1);
        ArrayList mTempList = integers;
        System.out.println(integers.hashCode());
        integers.add(2);
        System.out.println(integers.hashCode());
        if (mTempList == integers) {
            System.out.println("是同一个对象");
        } else {
            System.out.println("不是同一个对象");
        }
    }

    @Test
    public void testRxListReference() {

        List mList = new ArrayList<Integer>();
        mList.add(1);
        List mTempList = mList;
        if (mTempList == mList) {
            System.out.println("同一对象");
        } else {
            System.out.println("不同对象");
        }
        Observable.just(2)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(integer -> {
                    if (mTempList == mList) {
                        System.out.println("同一对象");
                    } else {
                        System.out.println("不同对象");
                    }
                    mList.add(integer);
                });
    }

    @Test
    public void testStartWith() {

        List listA = new ArrayList<String>();
        listA.add("A");
        listA.add("B");
        listA.add("C");

        List listB = new ArrayList<String>();
        listB.add("1");
        listB.add("2");
        listB.add("3");

        Observable observableA = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (Object o : listA) {
                    String s = (String) o;
                    emitter.onNext(s);
                    Thread.sleep(500);
                }
            }
        });
        Observable observableB = Observable.fromIterable(listB);
        Observable observable = observableA.startWith(observableB);

        System.out.println(observableA.hashCode());
        System.out.println(observableB.hashCode());
        System.out.println(observable.hashCode());


        observable.subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                String s = (String) o;
                System.out.println(s);
            }
        });


    }

    @Test
    public void testDistinctUntilChanged() {
        List listA = new ArrayList<String>();
        listA.add("A");
        listA.add("A");
        listA.add("A");

        List listB = new ArrayList<String>();
        listB.add("1");
        listB.add("2");
        listB.add("3");


        Observable.fromIterable(listA)
                .distinctUntilChanged()
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        String s = (String) o;
                        System.out.println(s);
                    }
                });
    }

    @Test
    public void testCombineLatest() {
        List listA = new ArrayList<String>();
        listA.add("A");
        listA.add("B");
        listA.add("C");

        List listB = new ArrayList<String>();
        listB.add("1");
        listB.add("2");
        listB.add("3");
        Observable<String> observableA = Observable.fromIterable(listA);
        Observable<String> observableB = Observable.fromIterable(listB);

        Observable.combineLatest(observableB, observableA, new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) throws Exception {
                return s + "-" + s2;
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                String s = (String) o;
                System.out.println(s);
            }
        });


    }
    @Test
    public void testVauleDefault(){



    }



}

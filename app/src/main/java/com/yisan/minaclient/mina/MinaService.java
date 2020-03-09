package com.yisan.minaclient.mina;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @author：wzh
 * @description: 跑mina服务
 * @packageName: com.yisan.getuidemo.minatest
 * @date：2020/3/6 0006 下午 3:16
 */
public class MinaService extends Service {

    private ConnectionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread = null;
    }


    private class ConnectionThread extends HandlerThread {

        private Context context;
        private ConnectionManager manager;
        private boolean isConnect;

        public ConnectionThread(String name, Context context) {
            super(name);
            this.context = context;

            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("192.168.169.190") //输入自己的服务器ip地址
                    .setPort(3344)
                    .setReadBufferSize(1024)
                    .setConnectionTimeOut(10000)
                    .builder();

            this.manager = new ConnectionManager(config);
        }

        /**
         * 开始连接我们的服务器
         */
        @Override
        protected void onLooperPrepared() {
            System.out.println("onLooperPrepared");
            for (; ; ) {
                isConnect = manager.connect();
                if (isConnect) {
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
            }

        }

        public void disConnection() {
            manager.disConnection();
        }
    }
}

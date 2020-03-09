package com.yisan.minaclient.mina;

import android.content.Context;
import android.util.Log;
import com.yisan.minaclient.event.MinaMessageFormServerEvent;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

/**
 * @author：wzh
 * @description: mina连接管理
 * @packageName: com.yisan.getuidemo.minatest
 * @date：2020/3/6 0006 下午 2:22
 */
public class ConnectionManager {

    private static final String TAG = "客户端:";

    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<Context>(config.getContext());
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()
        ));
        mConnection.setHandler(new DefaultHandler(mContext.get()));
        mConnection.setDefaultRemoteAddress(mAddress);
    }

    /**
     * 连接服务器
     *
     * @return boolean
     */
    public boolean connect() {
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "mina server connect is error !!!");
            return false;
        }
        return mSession != null;
    }

    /**
     * 断开连接
     */
    public void disConnection() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }


    /**
     * 重新连接服务器
     */
    private void reConnect() {

        if (mConnection == null || mAddress == null) {
            init();
        }

        int failCount = 0;
        for (; ; ) {


            try {
                Thread.sleep(5000);
                System.out.println("重连："+mAddress.getAddress().getHostAddress());
                ConnectFuture future = mConnection.connect();
                future.awaitUninterruptibly();
                mSession = future.getSession();
                if (mSession != null && mSession.isConnected()) {
                    Log.i(TAG, "断线重连[" + mAddress.getAddress().getHostAddress() + ":" + mAddress.getPort() + "]成功!");
                    failCount = 0;
                    break;
                }
            } catch (Exception e) {
                failCount++;
                Log.e(TAG, "断线重连失败: " + failCount);
                if (failCount > 5) {
                    Log.e(TAG, "退出重连 ");
                    break;
                }
            }

        }

    }


    /**
     * 处理数据的接受发送其他回调
     */
    private class DefaultHandler extends IoHandlerAdapter {

        private Context mContext;

        DefaultHandler(Context context) {
            this.mContext = context;
        }

        /**
         * 连接上服务器第一个走该方法
         */
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            Log.e(TAG, "session创建 ");
            //将我们的session保存到我们的session manager类中，从而可以发送消息到服务器
            SessionManager.getInstance().setSession(session);

        }

        /**
         * 连接上服务器第二个走该方法
         */
        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            Log.e(TAG, "session打开 ");

        }

        /**
         * 发送消息的时候会走该回调方法
         */
        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            Log.e(TAG, "messageSent: 信息发送");

        }


        /**
         * 收到消息走这个回调
         */
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {

            if (message != null) {
                String str = message.toString();
                EventBus.getDefault().post(new MinaMessageFormServerEvent(str));
                Log.e(TAG, "messageReceived: " + str);
            }

        }


        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            super.sessionIdle(session, status);
            Log.e(TAG, "sessionIdle: ");
        }


        /**
         * 异常一般都是客户端出现异常断开了服务器的连接、一般这种情况比较多
         * 报的错误是：org.apache.mina.core.RuntimeIoException: Failed to get the session.
         */
        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            super.exceptionCaught(session, cause);
            Log.e(TAG, "exceptionCaught 异常捕获: ");
            cause.printStackTrace();
        }


        /**
         * 服务器断开的时候先执行该方法、消息通道关闭
         */
        @Override
        public void inputClosed(IoSession session) throws Exception {
            super.inputClosed(session);
            Log.e(TAG, "inputClosed: ");
        }

        /**
         * 服务器断开的时候最后执行该方法
         */
        @Override
        public void sessionClosed(IoSession session) throws Exception {
            Log.e(TAG, "session关闭 ");
            reConnect();
        }

    }


}
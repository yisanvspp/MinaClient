package com.yisan.minaclient.mina;

import org.apache.mina.core.session.IoSession;

/**
 * @author：wzh
 * @description:
 * @packageName: com.yisan.getuidemo.minatest
 * @date：2020/3/6 0006 下午 3:39
 */
public class SessionManager {

    private IoSession mSession;

    private static SessionManager mInstatnce;

    public static SessionManager getInstance() {
        if (mInstatnce == null) {
            synchronized (SessionManager.class) {
                if (mInstatnce == null) {
                    mInstatnce = new SessionManager();
                }
            }
        }
        return mInstatnce;
    }

    private SessionManager() {
    }

    public void setSession(IoSession session) {
        this.mSession = session;
    }


    /**
     * 将对象写到服务器
     *
     * @param msg
     */
    public void writeToServer(Object msg) {
        if (mSession != null) {
            mSession.write(msg);
        }
    }

    /**
     * 关闭Session
     */
    public void closeSession() {
        if (mSession != null) {
            mSession.closeOnFlush();
        }
    }

    public void removeSession() {
        this.mSession = null;
    }


}

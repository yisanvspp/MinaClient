package com.yisan.minaclient.mina;

import android.content.Context;

/**
 * @author：wzh
 * @description: 连接参数配置
 * @packageName: com.yisan.getuidemo.minatest
 * @date：2020/3/6 0006 下午 2:24
 */
public class ConnectionConfig {


    private Context context;
    private String ip;
    private int port;
    private int readBufferSize;
    private long connectionTimeOut;


    public Context getContext() {
        return context;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public long getConnectionTimeOut() {
        return connectionTimeOut;
    }


    /**
     * 构造器
     */
    public static class Builder {

        private Context context;
        private String ip = "192.168.1.10";
        private int port = 3344;
        private int readBufferSize = 1024;
        private long connectionTimeOut = 10000;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setReadBufferSize(int readBufferSize) {
            this.readBufferSize = readBufferSize;
            return this;
        }

        public Builder setConnectionTimeOut(long connectionTimeOut) {
            this.connectionTimeOut = connectionTimeOut;
            return this;
        }

        private void applyConfig(ConnectionConfig config) {
            config.ip = this.ip;
            config.port = this.port;
            config.context = this.context;
            config.connectionTimeOut = this.connectionTimeOut;
            config.readBufferSize = this.readBufferSize;
        }

        public ConnectionConfig builder(){
            ConnectionConfig connectionConfig = new ConnectionConfig();
            applyConfig(connectionConfig);
            return connectionConfig;
        }

    }
}

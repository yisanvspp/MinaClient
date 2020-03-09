package com.yisan.minaclient.event;

/**
 * @author：wzh
 * @description: 从服务器接收到的消息
 * @packageName: com.yisan.minaclient.event
 * @date：2020/3/6  下午 4:03
 */
public class MinaMessageFormServerEvent {

    public String message;

    public MinaMessageFormServerEvent(String message) {
        this.message = message;
    }
}

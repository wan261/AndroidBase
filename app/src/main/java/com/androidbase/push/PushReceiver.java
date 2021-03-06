package com.androidbase.push;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.androidbase.commons.Constants;
import com.androidbase.entity.Message;
import com.commons.support.db.config.ConfigUtil;

/**
 * Created by User on 2015/10/19.
 */
public class PushReceiver implements IPushReceiver {

    private static PushReceiver pushReceiver = new PushReceiver();

    public static PushReceiver getInstance(){
        return pushReceiver;
    }


    private boolean isNotificationSelf;

    private IPushMessage iPushMessage;

    public void setiPushMessage(IPushMessage iPushMessage){
        this.iPushMessage = iPushMessage;
    }

    /**
     * 初始化操作，设置PUSH_TOPIC、PUSH_PUBLIC_TOKEN、setUserAccount等
     */
    public static void initPush(Context context) {
        MiPushMessageReceiver.initMiPush(context, ConfigUtil.getConfigValue(Constants.PUSH_TOPIC)
                                                , ConfigUtil.getConfigValue(Constants.PUSH_PUBLIC_TOKEN));
    }

    /**
     * 执行此方法则改为自定义通知消息样式及处理
     */
    public void setNotificationSelf(){
        isNotificationSelf = true;
        // TODO: 2015/10/19 推送平台设置为自定义样式
    }

    @Override
    public void onReceivePassThroughMessage(String message) {
        PushMessage msg = PushMessage.parser(message);
        // 发到ui线程
        if (msg != null) switch (msg.getUxid()) {
            case 200:
                if (iPushMessage != null) {
                    iPushMessage.message200();
                }
                break;
            case 201:
                if (iPushMessage != null) {
                    Message getMsg = JSON.parseObject(msg.getData(), Message.class);
                    iPushMessage.message201(getMsg);
                }
                break;
            case 202:
                if (iPushMessage != null) {
                    Message getMsg = JSON.parseObject(msg.getData(), Message.class);
                    iPushMessage.message202(msg.getRef(), getMsg);
                }
                break;
        }
    }

    @Override
    public void onNotificationMessageArrived(String message) {
        PushMessage msg = PushMessage.parser(message);
        if(isNotificationSelf && msg!=null && msg.getUxid() < 200) {
            showNotification(msg);
        }
    }

    @Override
    public void onNotificationMessageClicked(String message) {
        PushMessage msg = PushMessage.parser(message);

    }

    /**
     * 自定义通知
     * @param msg
     */
    private void showNotification(PushMessage msg) {

    }
}

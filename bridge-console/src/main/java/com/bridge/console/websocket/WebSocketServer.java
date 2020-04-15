package com.bridge.console.websocket;

import com.alibaba.fastjson.JSON;
import com.bridge.console.model.vo.SystemLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-08-30 10:33
 */
@ServerEndpoint("/websocket/{envId}/{appCodeList}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static Set<WebSocketServer> webSocketSet = Collections.synchronizedSet(new HashSet<>());

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收sid
     */
    private Integer envId;

    /**
     * 系统编码的集合
     */
    private String[] appCodeList;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("envId") Integer envId, @PathParam("appCodeList") String appCodeList) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        log.info("有新窗口开始监听:" + envId + ",当前在连接数为" + getOnlineCount());
        this.envId = envId;
        if (!StringUtils.isEmpty(appCodeList)) {
            this.appCodeList = appCodeList.split(",");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        log.info("关闭了一个连接，当前在连接数为:{}", getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口" + envId + "的信息:" + message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                log.error("websocket消息发送失败");
            }
        }
    }

    /**
     * 错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket发生错误");
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        synchronized (session) {
            this.session.getBasicRemote().sendText(message);
        }
    }


    /**
     * 发送日志信息，只有环境对应上的的才会发
     *
     * @param systemLogVO
     */
    public static void sendLog(SystemLogVO systemLogVO) {
        for (WebSocketServer item : webSocketSet) {
            try {
                Integer envId = item.envId;
                String[] appCodeArray = item.appCodeList;
                String appCodeInSys = systemLogVO.getAppCode();
                if (envId != null && appCodeArray != null && !StringUtils.isEmpty(appCodeInSys)) {
                    if (envId.equals(systemLogVO.getEnvId()) && appCodeArray.length > 0) {
                        // 判断是否包含这个系统
                        for (String str : appCodeArray) {
                            if (!StringUtils.isEmpty(str) && str.equals(systemLogVO.getAppCode())) {
                                log.info("推送消息到[{}]，推送内容:{}", item.envId, JSON.toJSONString(systemLogVO));
                                item.sendMessage(JSON.toJSONString(systemLogVO));
                                return;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("发送websocket消息失败，异常信息为：", e);
            }
        }
    }


    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

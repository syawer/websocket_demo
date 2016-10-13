

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sanyet.fireball.base.AppUtil;
import com.sanyet.fireball.base.entity.ActionRes;
import com.sanyet.fireball.base.message.service.MessageService;

@ServerEndpoint(value = "/game",configurator=GetHttpSessionConfigurator.class)
public class MessageSocket {
    private static final Logger logger = LogManager.getLogger(MessageSocket.class);
 
    static Map<String,Session> sessionMap = new Hashtable<String,Session>();
    static Map<Integer,Integer> sessinCount = new HashMap<Integer,Integer>();
    
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {
        logger.debug("链接建立");
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        Object obj = httpSession.getAttribute("userId");
        sessionMap.put(String.valueOf(obj), session);
        int userId = Integer.parseInt(String.valueOf(obj));
        Integer num = sessinCount.get(userId);
        if(num == null){
            MessageService messageService = AppUtil.getBean(MessageService.class);
            int count = messageService.getUnreadMessages(userId);
            num = count;
        }
        sessinCount.put(userId, num);
        if(sessinCount.get(userId) > 0){
           broadcastToUser(userId, num);
        }else{
           broadcastToUser(userId, num);
        }
    }
 
    @OnMessage
    public void onMessage(String unscrambledWord, Session session) {
        broadcastAll("message",unscrambledWord);
    }
    
    public static void broadcastToUser(int userId,int count){
        ActionRes res = new ActionRes();
        res.addData("count", count);
        sessinCount.put(userId, count);
        String type = "message";
        try {
            Session session = sessionMap.get(String.valueOf(userId));
            session.getBasicRemote().sendText("{type:'"+type+"',text:'"+count+"'}");
        } catch (IOException e) {
            throw new RuntimeException("有新数据传到前台时报错");
        }
         
    }
    /**
     * 广播给所有人
     * @param message
     */
    public static void broadcastAll(String type,String message){
        Set<Map.Entry<String,Session>> set = sessionMap.entrySet();
        for(Map.Entry<String,Session> i: set){
            try {
                i.getValue().getBasicRemote().sendText("{type:'"+type+"',text:'"+message+"'}");
            } catch (Exception e) {
                throw new RuntimeException("有新数据传到前台时报错");
            }
        }
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
//        sessionMap.remove(session.getId());
        logger.debug(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
     
    @OnError
    public void error(Session session, java.lang.Throwable throwable){
//        sessionMap.remove(session.getId());
        logger.error("session "+session.getId()+" error:"+throwable);
    }

}

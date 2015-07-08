package org.jivesoftware.openfire.plugin;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.plugin.entity.ChatLog;
import org.jivesoftware.openfire.plugin.entity.ChatLogRoom;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/*消息插件
 *	JiveGlobals.getProperty(String) 和 JiveGlobals.setProperty(String, String) 方法将我们的插件设置为openfire的一个全局属性
 *	org.jivesoftware.util.PropertyEventListener方法可以将我们的插件做成一个属性监听器监听任何属性的变化
 *  PropertyEventDispatcher.addListener(PropertyEventListener)方法可以注册监听
 * */
public class ChatLogsPlugin implements PacketInterceptor,Plugin{
	private InterceptorManager interceptorManager;
    private static final Logger log = LoggerFactory.getLogger(ChatLogsPlugin.class);
    private static PluginManager pluginManager;
    private static DbChatLogsManager logsManager;
    private static DbChatLogsRoomManger logsRoomManager;
    private static 	ChatLog temp = null;
    /*construct*/
    public ChatLogsPlugin() {
        interceptorManager = InterceptorManager.getInstance();
        logsManager = DbChatLogsManager.getInstance();
        logsRoomManager = DbChatLogsRoomManger.getInstance();
    }
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
        interceptorManager.addInterceptor(this);//注册插件
        pluginManager = manager;
        debug("安装聊天记录插件成功！");
	}
	@Override
	public void destroyPlugin() {
        interceptorManager.removeInterceptor(this);//销毁插件
        debug("销毁聊天记录插件成功！");
	}
	/*拦截用户发送的消息包
	 * 拦截消息核心方法，Packet就是拦截消息对象
	 */
	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
        if (session != null) {
            debug(packet, incoming, processed, session);
        }
        this.doAction(packet, incoming, processed, session);
	}
	/*
	 * 执行保存(分析聊天记录动作)
	 */
    private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
    
        Packet copyPacket = packet.createCopy();
        if (packet instanceof Message) {
            Message message = (Message) copyPacket;          
            if (message.getType() == Message.Type.chat) {// 一对一聊天，单人模式
                log.info("单人聊天信息：{}", message.toXML());
                debug("单人聊天信息：" + message.toXML());              
                // 程序执行中；是否为结束或返回状态（是否是当前session用户发送消息）
                if (processed || !incoming) {
                    return;
                }
                logsManager.add(this.get(packet, incoming, session));             
            // 群聊天，多人模式
            } else if (message.getType() ==  Message.Type.groupchat) {
                List<?> els = message.getElement().elements("x");
                if (els != null && !els.isEmpty()) {
                    log.info("群聊天信息：{}", message.toXML());
                    debug("群聊天信息：" + message.toXML());
                    if (processed || !incoming) {
                        return;
                    }
                    logsRoomManager.add(this.getRoom(packet, incoming, session));     
                } else {
                    log.info("群系统信息：{}", message.toXML());
                    debug("群系统信息：" + message.toXML());
                }          
            // 其他信息
            } else {
                log.info("其他信息：{}", message.toXML());
                debug("其他信息：" + message.toXML());
            }
        } else if (packet instanceof IQ) {
            IQ iq = (IQ) copyPacket;
            if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {
                log.info("用户登录成功：{}", iq.toXML());
                debug("用户登录成功：" + iq.toXML());
            }
        } else if (packet instanceof Presence) {
            Presence presence = (Presence) copyPacket;
            if (presence.getType() == Presence.Type.unavailable) {
                log.info("用户退出服务器成功：{}", presence.toXML());
                debug("用户退出服务器成功：" + presence.toXML());
            }
        } 
    }
    /*
	 * 创建一个一对一聊天记录实体对象，并设置相关数据
	 */
    private ChatLog get(Packet packet, boolean incoming, Session session) {
        Message message = (Message) packet;
        ChatLog logs = new ChatLog();
        JID jid = session.getAddress();
        if (incoming) {        // 发送者
            logs.setSender(jid.getNode());
            JID recipient = message.getTo();
            logs.setReceiver(recipient.getNode());
        } 
        logs.setContent(message.getBody());
        logs.setCreateDate(new Timestamp(new Date().getTime()));
        logs.setLength(logs.getContent().length());
        logs.setState(0);
        return logs;
    }
    /*提取群聊实体*/
    private ChatLogRoom getRoom(Packet packet, boolean incoming, Session session) {
    	long roomId=-1;//房间id
    	Message message = (Message) packet;
    	JID messagejid = message.getTo();
    	String roomnamearry[] = messagejid.toString().split("@");
    	String roomname = roomnamearry[0];
    	if(roomname==null)
    		return null;
    	else{
    		roomId = logsRoomManager.getRoomid(roomname);
    		if(roomId==-1||roomId==0) 
    			return null;    		
            ChatLogRoom logs = new ChatLogRoom();
            JID jid = session.getAddress();
            logs.setSender(jid.getNode()); //发送者
            logs.setRoomid(roomId);
            logs.setContent(message.getBody());
            logs.setCreateDate(new Timestamp(new Date().getTime()));
            logs.setLength(logs.getContent().length());
            logs.setState(0);
            return logs;
    	}
    	
    }
    /*
   	 *  调试信息
   	 */
    private void debug(Packet packet, boolean incoming, boolean processed, Session session) {
        String info = "[ packetID: " + packet.getID() + ", to: " + packet.getTo() + ", from: " + packet.getFrom() + ", incoming: " + incoming + ", processed: " + processed + " ]";
        long timed = System.currentTimeMillis();
        debug("################### start ###################" + timed);
        debug("id:" + session.getStreamID() + ", address: " + session.getAddress());
        debug("info: " + info);
        debug("xml: " + packet.toXML());
        debug("################### end #####################" + timed);
        log.info("id:" + session.getStreamID() + ", address: " + session.getAddress());
        log.info("info: {}", info);
        log.info("plugin Name: " + pluginManager.getName(this) + ", xml: " + packet.toXML());
    }
    private void debug(Object message) {
        if (true) {
            System.out.println(message);
        }
    }
}

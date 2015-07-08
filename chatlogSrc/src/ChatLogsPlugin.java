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

/*��Ϣ���
 *	JiveGlobals.getProperty(String) �� JiveGlobals.setProperty(String, String) ���������ǵĲ������Ϊopenfire��һ��ȫ������
 *	org.jivesoftware.util.PropertyEventListener�������Խ����ǵĲ������һ�����Լ����������κ����Եı仯
 *  PropertyEventDispatcher.addListener(PropertyEventListener)��������ע�����
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
        interceptorManager.addInterceptor(this);//ע����
        pluginManager = manager;
        debug("��װ�����¼����ɹ���");
	}
	@Override
	public void destroyPlugin() {
        interceptorManager.removeInterceptor(this);//���ٲ��
        debug("���������¼����ɹ���");
	}
	/*�����û����͵���Ϣ��
	 * ������Ϣ���ķ�����Packet����������Ϣ����
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
	 * ִ�б���(���������¼����)
	 */
    private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {
    
        Packet copyPacket = packet.createCopy();
        if (packet instanceof Message) {
            Message message = (Message) copyPacket;          
            if (message.getType() == Message.Type.chat) {// һ��һ���죬����ģʽ
                log.info("����������Ϣ��{}", message.toXML());
                debug("����������Ϣ��" + message.toXML());              
                // ����ִ���У��Ƿ�Ϊ�����򷵻�״̬���Ƿ��ǵ�ǰsession�û�������Ϣ��
                if (processed || !incoming) {
                    return;
                }
                logsManager.add(this.get(packet, incoming, session));             
            // Ⱥ���죬����ģʽ
            } else if (message.getType() ==  Message.Type.groupchat) {
                List<?> els = message.getElement().elements("x");
                if (els != null && !els.isEmpty()) {
                    log.info("Ⱥ������Ϣ��{}", message.toXML());
                    debug("Ⱥ������Ϣ��" + message.toXML());
                    if (processed || !incoming) {
                        return;
                    }
                    logsRoomManager.add(this.getRoom(packet, incoming, session));     
                } else {
                    log.info("Ⱥϵͳ��Ϣ��{}", message.toXML());
                    debug("Ⱥϵͳ��Ϣ��" + message.toXML());
                }          
            // ������Ϣ
            } else {
                log.info("������Ϣ��{}", message.toXML());
                debug("������Ϣ��" + message.toXML());
            }
        } else if (packet instanceof IQ) {
            IQ iq = (IQ) copyPacket;
            if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {
                log.info("�û���¼�ɹ���{}", iq.toXML());
                debug("�û���¼�ɹ���" + iq.toXML());
            }
        } else if (packet instanceof Presence) {
            Presence presence = (Presence) copyPacket;
            if (presence.getType() == Presence.Type.unavailable) {
                log.info("�û��˳��������ɹ���{}", presence.toXML());
                debug("�û��˳��������ɹ���" + presence.toXML());
            }
        } 
    }
    /*
	 * ����һ��һ��һ�����¼ʵ����󣬲������������
	 */
    private ChatLog get(Packet packet, boolean incoming, Session session) {
        Message message = (Message) packet;
        ChatLog logs = new ChatLog();
        JID jid = session.getAddress();
        if (incoming) {        // ������
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
    /*��ȡȺ��ʵ��*/
    private ChatLogRoom getRoom(Packet packet, boolean incoming, Session session) {
    	long roomId=-1;//����id
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
            logs.setSender(jid.getNode()); //������
            logs.setRoomid(roomId);
            logs.setContent(message.getBody());
            logs.setCreateDate(new Timestamp(new Date().getTime()));
            logs.setLength(logs.getContent().length());
            logs.setState(0);
            return logs;
    	}
    	
    }
    /*
   	 *  ������Ϣ
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

package org.jivesoftware.openfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.plugin.entity.ChatLog;
import org.jivesoftware.openfire.plugin.entity.ChatLogRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbChatLogsRoomManger {
	 private static final Logger Log = LoggerFactory.getLogger(DbChatLogsRoomManger.class);
	 private static final DbChatLogsRoomManger CHAT_LOGS_ROOM_MANAGER = new DbChatLogsRoomManger();
	 private static final String LOGS_GETROOMID = "SELECT ROOMID FROM OFMUCROOM where NAME = ?";
	 private static final String LOGS_COUNT = "SELECT count(1) FROM OFCHATLOGSROOM where ROOMID = ?";
	 private static final String LOGS_INSERT = "INSERT INTO OFCHATLOGSROOM(messageId, sender, ROOMID, createDate, length, content, state) VALUES(seq_chatRoomlog.NEXTVAL,?,?,?,?,?,?)";
	 private static final String LOGS_HIDE = "UPDATE OFCHATLOGSROOM set state = 1 where messageId = ? and ROOMID = ?";
	 private static final String LOGS_REMOVE = "delete OFCHATLOGSROOM where messageId = ? and ROOMID = ?";
	 private static final String LOGS_FIND_BY_ID = "SELECT messageId, sender, roomid, createDate, length, content, state FROM OFCHATLOGSROOM where messageId = ?";
	 private static final String LOGS_QUERY = "SELECT messageId, sender, roomid, createDate, length, content FROM OFCHATLOGSROOM where state = 0";
	 private static final String ALL_CONTACT_BY_ROOMID = "SELECT distinct sender FROM OFCHATLOGSROOM where state = 0 and ROOMID = ?";
	 /*construct*/
    private DbChatLogsRoomManger() {

    }
    public static DbChatLogsRoomManger getInstance() {
        return CHAT_LOGS_ROOM_MANAGER;
    }
    /*
     *根据roomname获取roomid 
     * */
    public int getRoomid(String name) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int roomid = -1;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_GETROOMID);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	roomid = rs.getInt(1);
            } else {
            	roomid = 0;
            }
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
            return 0;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return roomid;
    }
    /*
   	 *多条件搜索查询
     */
    public List<ChatLogRoom> query(ChatLogRoom entity) {
        Connection con = null;
        Statement pstmt = null;
        ChatLogRoom logs = null;
        List<ChatLogRoom> result = new ArrayList<ChatLogRoom>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.createStatement();
            String sql = LOGS_QUERY;
            if (entity != null) {
            	if(entity.getRoomid()!=-1&&entity.getRoomid()!=0){//roomid is not null
            		sql += " and roomid = " + entity.getRoomid() + "";
            	}
            	if (!StringUtils.isEmpty(entity.getSender())){//
            		sql += " and sender = '" + entity.getSender() + "'";
            	}
            	if (!StringUtils.isEmpty(entity.getContent())) {
                    sql += " and content like '%" + entity.getContent() + "%'";
                 }
                if (entity.getCreateDate() != null) {
                   DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                   String crateatDate = df.format(new Date(entity.getCreateDate().getTime()));              
                   sql += " and createDate like '" + crateatDate + "%'";
                }
               
            }
            sql += " order by createDate asc";
            ResultSet rs = pstmt.executeQuery(sql);
            while (rs.next()) {
                logs = new ChatLogRoom();
                logs.setMessageId(rs.getInt("messageId"));
                logs.setContent(rs.getString("content"));
                logs.setCreateDate(rs.getTimestamp("createDate"));
                logs.setLength(rs.getInt("length"));
                logs.setSender(rs.getString("sender"));
                logs.setRoomid(rs.getLong("roomid"));
                result.add(logs);
            }
            return result;
        } catch (SQLException sqle) {
            Log.error("chatLogs search exception: {}", sqle);
            return result;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    /*
   	 *查找roomid的聊天用户
     */
    public List<String> AllContactByRoomid(long roomid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        List<String> result = new ArrayList<String>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_CONTACT_BY_ROOMID);
            pstmt.setLong(1, roomid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("sender"));
            }
            return result;
        } catch (SQLException sqle) {
            Log.error("chatLogs find exception: {}", sqle);
            return result;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    /*
 	 *根据roomid messageid 隐藏群聊天记录信息
     */
    public boolean hide(long id,long roomid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_HIDE);
            pstmt.setLong(1, id);
            pstmt.setLong(2, roomid);
            return pstmt.execute();
        } catch (SQLException sqle) {
            Log.error("chatLogs remove exception: {}", sqle);
            return false;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    /*
 	 *根据roomid messageid 删除群聊天记录信息
     */
    public boolean remove(long id,long roomid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_REMOVE);
            pstmt.setLong(1, id);
            pstmt.setLong(2, roomid);
            return pstmt.execute();
        } catch (SQLException sqle) {
            Log.error("chatLogs remove exception: {}", sqle);
            return false;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    /**待检测**/
    

    /*
 	 *添加会议室聊天记录信息
     */
    public boolean add(ChatLogRoom logs) {	
    	Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_INSERT);
            int i = 1;
            pstmt.setString(i++, logs.getSender());
            pstmt.setLong(i++, logs.getRoomid());
            pstmt.setString(i++, logs.getCreateDate().toString());
            pstmt.setInt(i++, logs.getLength());
            pstmt.setString(i++, logs.getContent());
            pstmt.setInt(i++, logs.getState());
            return pstmt.execute();
        } catch (SQLException sqle) {
            Log.error("chatLogs add exception: {}", sqle);
            return false;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
    /*
 	 *获取会议室roomid房间的总数量(没有返回0)
      */
    public int getCount(Integer id) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = -1;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_COUNT);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            } else {
                count = 0;
            }
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
            return 0;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return count;
    }
    
    /*
   	 *通过messageid查询聊天记录信息
     */
    public ChatLog find(int id) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ChatLog logs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_FIND_BY_ID);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                logs = new ChatLog();
                logs.setMessageId(rs.getInt("messageId"));
                logs.setContent(rs.getString("content"));
                logs.setCreateDate(rs.getTimestamp("createDate"));
                logs.setLength(rs.getInt("length"));
                logs.setSender(rs.getString("sender"));
                logs.setReceiver(rs.getString("receiver"));
                logs.setState(rs.getInt("state"));
            }
            return logs;
        } catch (SQLException sqle) {
            Log.error("chatLogs find exception: {}", sqle);
            return logs;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    
}

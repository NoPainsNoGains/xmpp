package org.jivesoftware.openfire.plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.entity.ChatLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbChatLogsManager {
    private static final Logger Log = LoggerFactory.getLogger(DbChatLogsManager.class);
    private static final DbChatLogsManager CHAT_LOGS_MANAGER = new DbChatLogsManager();
    private static final String LOGS_COUNT = "SELECT count(1) FROM OFCHATLOGS";
    private static final String LOGS_LAST_MESSAGE_ID = "SELECT max(messageId) FROM OFCHATLOGS";
    private static final String LOGS_FIND_BY_ID = "SELECT messageId, sender, receiver, createDate, length, content FROM OFCHATLOGS where messageId = ?";
    private static final String LOGS_REMOVE = "delete OFCHATLOGS where messageId = ?";
    private static final String LOGS_INSERT = "INSERT INTO OFCHATLOGS(messageId, sender, receiver, createDate, length, content, state) VALUES(seq_chatlog.NEXTVAL,?,?,?,?,?,?)";
    private static final String LOGS_QUERY = "SELECT messageId, sender, receiver, createDate, length, content FROM OFCHATLOGS where 1=1";
    private static final String LOGS_SEARCH = "SELECT * FROM OFCHATLOGS where state = 0";
    private static final String LOGS_LAST_CONTACT = "SELECT distinct receiver FROM OFCHATLOGS where sender = ?";
    private static final String LOGS_ALL_CONTACT = "SELECT distinct sender FROM OFCHATLOGS";
    private static final String LOGS_FILEJUDGE="SELECT STATE FROM OFCHATLOGS WHERE MESSAGEID=?";
    private static final String LOGS_FILEPATH="SELECT CONTENT FROM OFCHATLOGS WHERE MESSAGEID=?";
    /*construct*/
    private DbChatLogsManager() {

    }
    /*DbChatLogsManager���*/
    public static DbChatLogsManager getInstance() {
        return CHAT_LOGS_MANAGER;
    }
    /*
     * ����id��ȡ�ļ�·��
     * */
    public String getPath(long messageid){
    	 Connection con = null;
         PreparedStatement pstmt = null;
         String result=""; 
         try {
             con = DbConnectionManager.getConnection();
             pstmt = con.prepareStatement(LOGS_FILEPATH);
             pstmt.setLong(1,messageid);
             ResultSet rs = pstmt.executeQuery();
             if (rs.next()) {
            	String tempPath = rs.getString(1);
         		if(tempPath.length()==0)
         			return "";
         		else{//�ָ��ļ�
         			String[] templist = tempPath.split("@");
         			if(templist.length!=2)
         				return "";
         			else{
         				result = templist[1];
         				return result;
         			}
         		}         	
             }else{
            	 return "";
             }    
         } catch (SQLException sqle) {
        	 Log.error(sqle.getMessage(), sqle);
             return "";
         } finally {
            DbConnectionManager.closeConnection(pstmt, con);
         }
    }
    /*
     * ɾ�����������¼ �����ļ�
     * return true-�ļ� false-��ͨ��¼
     * */
    public boolean judge(long messageid){
    	 Connection con = null;
         PreparedStatement pstmt = null;
         int result=-1; 
         try {
             con = DbConnectionManager.getConnection();
             pstmt = con.prepareStatement(LOGS_FILEJUDGE);
             pstmt.setLong(1,messageid);
             ResultSet rs = pstmt.executeQuery();
             if (rs.next()) {
            	 result = rs.getInt(1);
            	 if(result==1)
            		 return true;
            	 else
            		 return false;
             } else {
            	 return false;
             }
             
         } catch (SQLException sqle) {
        	 Log.error(sqle.getMessage(), sqle);
             return false;
         } finally {
            DbConnectionManager.closeConnection(pstmt, con);
         }
    }
    /*
 	 *��ȡ���һ��id(û�з���0) 		
     */
    public int getLastId() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int lastId = -1;
        try {//SELECT max(messageId) FROM ofChatLogs Ϊʲô��max
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_LAST_MESSAGE_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	lastId = rs.getInt(1);
            } else {
            	lastId = 0;
            }
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
            return 0;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return lastId;
    }
    /*
 	 *��ȡ������(û�з���0)
     */
    public int getCount() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = -1;
        try {//SELECT count(1) FROM ofChatLogs
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_COUNT);
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
 	 *ɾ�������¼��Ϣ
     */
    public boolean remove(Integer id) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {//delete ofChatLogs where messageId = ?
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_REMOVE);
            pstmt.setInt(1, id);
            pstmt.execute();
            return true;
        } catch (SQLException sqle) {
            Log.error("chatLogs remove exception: {}", sqle);
            return false;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    public boolean removeFile(Integer id){
    	String path = getPath(id);
    	//��ɾ�����ݿ��
    	if(remove(id)){
    		/*ɾ�������ļ�*/
    		File file = new File(path);
    		if (file.isFile() && file.exists()) {//·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
    			file.delete();
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    }
    /*
 	 *��������¼��Ϣ
     */
    public boolean add(ChatLog logs) {
    	
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_INSERT);
            int i = 1;
            pstmt.setString(i++, logs.getSender());
            pstmt.setString(i++, logs.getReceiver());
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
   	 *ͨ��id��ѯ�����¼��Ϣ
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
            }
            return logs;
        } catch (SQLException sqle) {
            Log.error("chatLogs find exception: {}", sqle);
            return logs;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    /*
   	 *������������ѯ������List&lt;ChatLogs>����
     */
    public List<ChatLog> query(ChatLog entity) {
        Connection con = null;
        Statement pstmt = null;
        ChatLog logs = null;
        List<ChatLog> result = new ArrayList<ChatLog>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.createStatement();
            String sql = LOGS_QUERY;
            if (entity != null) {
                if (!StringUtils.isEmpty(entity.getSender()) && !StringUtils.isEmpty(entity.getReceiver())) {
                    sql += " and (sender = '" + entity.getSender() + "' and receiver = '" + entity.getReceiver() + "')";     
                } else {
                    if (!StringUtils.isEmpty(entity.getSender())) {
                        sql += " and sender = '" + entity.getSender() + "'";
                    }
                    if (!StringUtils.isEmpty(entity.getReceiver())) {
                        sql += " and receiver = '" + entity.getReceiver() + "'";
                    }
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
                logs = new ChatLog();
                logs.setMessageId(rs.getInt("messageId"));
                logs.setContent(rs.getString("content"));
                logs.setCreateDate(rs.getTimestamp("createDate"));
                logs.setLength(rs.getInt("length"));
                logs.setSender(rs.getString("sender"));
                logs.setReceiver(rs.getString("receiver"));
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
   	 *������������ѯ������List&lt;ChatLogs>����
     */
    public List<HashMap<String, Object>> search(ChatLog entity) {
        Connection con = null;
        Statement pstmt = null;
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.createStatement();
            String sql = LOGS_SEARCH;
            if (entity != null) {		
                if (!StringUtils.isEmpty(entity.getSender()) && !StringUtils.isEmpty(entity.getReceiver())) {
                    sql += " and (sender = '" + entity.getSender() + "' and receiver = '" + entity.getReceiver() + "')";             
                } else {
                    if (!StringUtils.isEmpty(entity.getSender())) {
                        sql += " and sender = '" + entity.getSender() + "'";
                    } 
                    if (!StringUtils.isEmpty(entity.getReceiver())) {
                        sql += " and receiver = '" + entity.getReceiver() + "'";
                    }
                }
                if (!StringUtils.isEmpty(entity.getContent())) {
                    sql += " and content like '%" + entity.getContent() + "%'";
                }
                if (entity.getCreateDate() != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String crateatDate = df.format(new Date(entity.getCreateDate().getTime()));
                    sql += " and to_char(createDate, 'yyyy-mm-dd') = '" + crateatDate + "'";
                }
            }
            sql += " order by createDate asc";
            ResultSet rs = pstmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            /** ��ȡ�����������*/  
            int columnCount = rsmd.getColumnCount();  
            while (rs.next()) {  
                HashMap<String, Object> map = new HashMap<String, Object>();  
                /** ��ÿһ����(key, value)����HashMap, ������Ϊkey,��ֵ��Ϊvalue */  
                for (int i = 1; i <= columnCount; ++i) {  
                    String columnVal = rs.getString(i);  
                    if (columnVal == null) {  
                        columnVal = "";  
                    }  
                    map.put(rsmd.getColumnName(i), columnVal);  
                }  
                /** ��װ��һ�����ݵ�HashMap����list */  
                result.add(map);  
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
   	 *�����ϵ��
     */
    public List<String> findLastContact(ChatLog entity) {
        Connection con = null;
        PreparedStatement pstmt = null;
        List<String> result = new ArrayList<String>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_LAST_CONTACT);
            pstmt.setString(1, entity.getSender());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("receiver"));
               
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
   	 *�������������û�
     */
    public List<String> findAllContact() {
        Connection con = null;
        PreparedStatement pstmt = null;
        List<String> result = new ArrayList<String>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_ALL_CONTACT);
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
}

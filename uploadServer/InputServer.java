package org.jivesoftware.openfire.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.plugin.entity.FileLog;

/**
 * �������߳���
 */
public class InputServer implements Runnable {
    private Socket socket = null;
    private final String LOGS_INSERT = "INSERT INTO OFCHATLOGS(messageId, sender, receiver, createDate, length, content, state) VALUES(seq_chatlog.NEXTVAL,?,?,?,?,?,?)";
    public InputServer(Socket socket) {
        this.socket = socket;
    }
    public boolean insertFilelog(FileLog logs) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOGS_INSERT);
            pstmt.setString(1, logs.getSender());
            pstmt.setString(2, logs.getReceiver());
            pstmt.setString(3, logs.getCreateDate());
            pstmt.setInt(4, logs.getLength());
            pstmt.setString(5, logs.getContent());
            pstmt.setInt(6, logs.getState());
            return pstmt.execute();
        } catch (SQLException sqle) {
            return false;
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
    public FileLog dealpath(FileLog filelog,String path){
    	String filename =filelog.getContent();
    	filename+="@";
    	filename+=path;
    	filelog.setContent(filename);
		return filelog;
    }
    public FileLog getFileEntity(BufferedInputStream inputStream) throws IOException{
    	 int len = 0;
         byte[] bys = new byte[1024 * 10];
         len = inputStream.read(bys);
         String str = new String(bys, 0, len);
         if(str.length()==0)
        	return null;
         else{
        	FileLog filelog = new FileLog();
          	String[] StringArray = str.split("@");
          	if(StringArray.length!=3){
          		 return null;
          	}else{
          		String sender = StringArray[0];
              	String receiver = StringArray[1];
              	String content = StringArray[2];
              	filelog.setSender(sender);
              	filelog.setReceiver(receiver);
              	filelog.setLength(content.length());
              	filelog.setCreateDate((new Timestamp(new Date().getTime()).toString()));
              	filelog.setContent(content);
              	filelog.setState(1);
              	return filelog;
          	}
         }
    }
	@Override
    public void run() {
        BufferedOutputStream outputStream = null;
        try {
            System.out.println(socket.getInetAddress().getHostAddress() + " ����");
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            /*��һ�ν���:���û��ַ���ת����ʵ����*/          
            FileLog filelog = getFileEntity(inputStream);
            /*����ѧ���ļ���*/  
            File dir1 = new File("C:\\uploadFile\\"+filelog.getSender());
            if (!dir1.exists()) {
                dir1.mkdir();
            }
            /*�ڶ��ν���:��ȡ�ͻ��˷��������ļ���*/ 
            int len=filelog.getContent().length();
            String strtemp = filelog.getContent();
            byte[] bys = new byte[1024 * 10];
            File file = new File(dir1, strtemp);
            /* ������������ڴ��ļ�����������*/
            int count = 0;
            while (file.exists()) {
                count++;
                file = new File(dir1, strtemp.replaceAll("(.+)\\.(.+)", "$1("
                        + count + "\\).$2"));
            }
            /*��֪�ͻ��� ׼����������*/
            PrintStream writer = new PrintStream(socket.getOutputStream(), true);
            writer.write("ok".getBytes());     
            /*���¾���·�������ݿ�*/
            filelog = dealpath(filelog,file.getAbsolutePath());
            insertFilelog(filelog);
	        /***��ʼд���ļ����������ֽ���***/
	        outputStream = new BufferedOutputStream(new FileOutputStream(file));
	        // ѭ����д�ļ���������
	        while ((len = inputStream.read(bys)) != -1) {
	            outputStream.write(bys, 0, len);
	            outputStream.flush();
	        }
	        // ��֪�ͻ��˽������
	       writer.write("�ϴ��ɹ���".getBytes());
	       socket.shutdownOutput();
        } catch (SocketException se) {
            System.out.println("�ͻ������Ӷ�ʧ��\n\t" + se.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // �ر���Դ
            if (outputStream != null) {
                try {
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
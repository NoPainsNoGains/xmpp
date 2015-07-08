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
 * 服务器线程类
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
            System.out.println(socket.getInetAddress().getHostAddress() + " 连接");
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            /*第一次接收:将用户字符串转化成实体类*/          
            FileLog filelog = getFileEntity(inputStream);
            /*创建学号文件夹*/  
            File dir1 = new File("C:\\uploadFile\\"+filelog.getSender());
            if (!dir1.exists()) {
                dir1.mkdir();
            }
            /*第二次接收:读取客户端发过来的文件名*/ 
            int len=filelog.getContent().length();
            String strtemp = filelog.getContent();
            byte[] bys = new byte[1024 * 10];
            File file = new File(dir1, strtemp);
            /* 如果服务器存在此文件则重名命名*/
            int count = 0;
            while (file.exists()) {
                count++;
                file = new File(dir1, strtemp.replaceAll("(.+)\\.(.+)", "$1("
                        + count + "\\).$2"));
            }
            /*告知客户端 准备工作就绪*/
            PrintStream writer = new PrintStream(socket.getOutputStream(), true);
            writer.write("ok".getBytes());     
            /*更新绝对路径到数据库*/
            filelog = dealpath(filelog,file.getAbsolutePath());
            insertFilelog(filelog);
	        /***开始写入文件到服务器字节流***/
	        outputStream = new BufferedOutputStream(new FileOutputStream(file));
	        // 循环读写文件到服务器
	        while ((len = inputStream.read(bys)) != -1) {
	            outputStream.write(bys, 0, len);
	            outputStream.flush();
	        }
	        // 告知客户端接收完毕
	       writer.write("上传成功！".getBytes());
	       socket.shutdownOutput();
        } catch (SocketException se) {
            System.out.println("客户端连接丢失！\n\t" + se.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
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
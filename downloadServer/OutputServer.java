package org.jivesoftware.openfire.plugin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class OutputServer implements Runnable{
	private Socket socket = null;
	DataInputStream inputStream = null;
	private String filePath;
	
	public OutputServer(Socket socket) {
        this.socket = socket;
    }
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
    public String getPath( ) throws IOException{
    	inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    	int len = 0;
        byte[] bys = new byte[1024 * 10];
        len = inputStream.read(bys);
        String str = new String(bys, 0, len);
        if(str.length()==0){
        	return "";
        }
        else{
        	return str;
        }
    }
	@Override
	public void run() {
		try {
			filePath=getPath();//获得path
            File fi = new File(filePath);
            System.out.println("文件长度:" + (int) fi.length());//xxxxxxxxxxxxxx
            System.out.println("建立socket链接");//xxxxxxxxxxxxxx
            DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            DataOutputStream ps = new DataOutputStream(socket.getOutputStream());
            ps.writeUTF(fi.getName());
            ps.flush();
            ps.writeLong((long) fi.length());
            ps.flush();

            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            while (true) {
                int read = 0;
                if (fis != null) {
                    read = fis.read(buf);
                }

                if (read == -1) {
                    break;
                }
                ps.write(buf, 0, read);
            }
            ps.flush();       
            fis.close();
            inputStream.close();
            socket.close();                
		}catch (Exception e) {
            e.printStackTrace();
        }
	}
}

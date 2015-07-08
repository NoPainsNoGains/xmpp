package org.jivesoftware.openfire.plugin;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager; 
/**
 * 上传服务器类
 */
public class uploadPlugin implements Plugin,Runnable{
    public uploadPlugin() {
        super();
    }
    public static void getFiles1() {
    	new Thread(new uploadPlugin()).start();
    }

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("upload plugin is starting....");
		getFiles1();
	}
	@Override
	public void destroyPlugin() {
		System.out.println("upload plugin is stopping....");
	}
	@Override
	public void run() {
		   ServerSocket server = null;
	        try {
	            server = new ServerSocket(10001);
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
	        Socket socket = null;
	        while (true) {
	            try {
	                socket = server.accept();//获取服务器Socket对象
	                new Thread(new InputServer(socket)).start();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	            }
	        }//while
	}
}
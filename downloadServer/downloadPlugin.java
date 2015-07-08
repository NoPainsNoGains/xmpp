package org.jivesoftware.openfire.plugin;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
 
/**
 * 下载服务器类
 * YMH
 */
public class downloadPlugin implements Plugin,Runnable{
    
	public downloadPlugin() {
        super();
    }
  
	public static void getFiles2() {
		new Thread(new downloadPlugin()).start();
	}
	
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("download plugin is starting....");
		getFiles2();
	}

	@Override
	public void destroyPlugin() {
		System.out.println("download plugin is stopping....");
	}

	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(8821);// 上传10001
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Socket socket = null;
		while (true) {
			try {
				socket = server.accept();// 获取服务器Socket对象
				new Thread(new OutputServer(socket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
		}
	}
}
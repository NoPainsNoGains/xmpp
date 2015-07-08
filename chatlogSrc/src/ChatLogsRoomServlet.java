package org.jivesoftware.openfire.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.plugin.entity.ChatLog;
import org.jivesoftware.openfire.plugin.entity.ChatLogRoom;
import org.jivesoftware.util.ParamUtils;

public class ChatLogsRoomServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static final ObjectMapper mapper = new ObjectMapper();
	private static DbChatLogsRoomManger logsRoomManager;
	@Override
	public void init() throws ServletException {
	   super.init();
	   logsRoomManager = DbChatLogsRoomManger.getInstance();
	   AuthCheckFilter.addExclude("chatlogs");
	   AuthCheckFilter.addExclude("chatlogs/ChatLogsRoomServlet");
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   doPost(request,response);
	}

	 @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	
	        System.out.println("************ meeting action  ************");
	        response.setCharacterEncoding("utf-8");
	        PrintWriter out = response.getWriter();
	        String action = ParamUtils.getParameter(request, "action");
	        if("alluser".equals(action)){
	        	 System.out.println("查看指定用户");
	        	 long roomid=-1;
	        	 String  strid= ParamUtils.getParameter(request, "alluseroomid");
	        	 if(strid==null){
	        		 roomid=0;
	         	}else{
	         		roomid = Integer.parseInt(strid);
	         		List<String> result = logsRoomManager.AllContactByRoomid(roomid);
	         		request.setAttribute("alluser", result);
	                request.getRequestDispatcher("/plugins/chatlogs/chatLogsRoom-all-contact-service.jsp").forward(request, response);
	         	}
	        }else if("hide".equals(action)){
	        	System.out.println("隐藏记录");
	        	long messageId = ParamUtils.getIntParameter(request, "messageId", -1);
	        	long roomid = ParamUtils.getIntParameter(request, "roomid", -1);
	        	logsRoomManager.hide(messageId, roomid);
	            request.getRequestDispatcher("/plugins/chatlogs/chatLogsRoom-service.jsp").forward(request, response);
	        }else if("remove".equals(action)){
	        	System.out.println("删除记录");
	        	long messageId = ParamUtils.getIntParameter(request, "messageId", -1);
	        	long roomid = ParamUtils.getIntParameter(request, "roomid", -1);
	        	logsRoomManager.remove(messageId, roomid);
	            request.getRequestDispatcher("/plugins/chatlogs/chatLogsRoom-service.jsp").forward(request, response);
	        }
	    }
	
	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("chatlogs/ChatLogsRoomServlet");
		AuthCheckFilter.removeExclude("chatlogs");
	}

	private void replyMessage(String message, HttpServletResponse response, PrintWriter out) {
        response.setContentType("text/json");
        out.println(message);
        out.flush();
    }
}

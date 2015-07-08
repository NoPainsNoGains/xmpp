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
import org.jivesoftware.util.ParamUtils;


public class ChatLogsServlet extends HttpServlet{
	private static final long serialVersionUID = -5404916983906926869L;
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static DbChatLogsManager logsManager;
    @Override
    public void init() throws ServletException {
        super.init();
        logsManager = DbChatLogsManager.getInstance();
        AuthCheckFilter.addExclude("chatlogs");
        AuthCheckFilter.addExclude("chatlogs/ChatLogsServlet");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        ChatLog entity = new ChatLog();
        String action = ParamUtils.getParameter(request, "action");
        if("time".equals(action)){
        	System.out.println("时间设置");
        	Integer day = ParamUtils.getIntParameter(request, "vday", -1);
        	switch(day)
				{ 
					case 10:    	//教师
						 System.out.println("10天");
				 		 break;
			        case 20:    //超级用户
			        	System.out.println("20天");
			             break;
			        case 30:    //没有权限人员
			        	System.out.println("30天");
			            break;
			        default:
			        	System.out.println("默认设置10天");
			        	break;
				}//switch
        	
        	request.getRequestDispatcher("/plugins/chatlogs/chatLogs-service.jsp").forward(request, response);
        	
        }else if("last!contact".equals(action)) {
        	System.out.println("最近联系人 ");
            String sender = ParamUtils.getParameter(request, "sender");
            entity.setSender(sender);
            List<String> result = logsManager.findLastContact(entity);
            request.setAttribute("lastContact", result);
            request.getRequestDispatcher("/plugins/chatlogs/chatLogs-last-contact-service.jsp").forward(request, response);
        } else if ("all!contact".equals(action)) {
        	System.out.println("所有人");
        	List<String> result = logsManager.findAllContact();
            request.setAttribute("allContact", result);
            request.getRequestDispatcher("/plugins/chatlogs/chatLogs-all-contact-service.jsp").forward(request, response);
        } else if("remove!contact".equals(action)){
        	System.out.println("删除");
        	Integer messageId = ParamUtils.getIntParameter(request, "messageId", -1);
        	if(logsManager.judge(messageId)){
        		logsManager.removeFile(messageId);
                request.getRequestDispatcher("/plugins/chatlogs/chatLogs-service.jsp").forward(request, response);
        	}else{
        		logsManager.remove(messageId);
                request.getRequestDispatcher("/plugins/chatlogs/chatLogs-service.jsp").forward(request, response);
        	}
        } else if ("lately!contact".equals(action)) {
        	System.out.println("2findLastContact ");
        	String sender = ParamUtils.getParameter(request, "sender");
            entity.setSender(sender);          
            List<String> result = logsManager.findLastContact(entity);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, result);
            replyMessage(writer.toString(), response, out);         
        } else if ("entire!contact".equals(action)) {
        	System.out.println("2所有人 ");
            List<String> result = logsManager.findAllContact();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, result);
            replyMessage(writer.toString(), response, out);           
        } else if ("delete!contact".equals(action)) {
        	System.out.println("2删除 ");
            Integer messageId = ParamUtils.getIntParameter(request, "messageId", -1);         
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, logsManager.remove(messageId));
            replyMessage(writer.toString(), response, out);           
        } else if ("query".equals(action)) {
        	System.out.println("多条件搜索查询");
            String sender = ParamUtils.getParameter(request, "sender");
            String receiver = ParamUtils.getParameter(request, "receiver");
            String content = ParamUtils.getParameter(request, "content");
            String createDate = ParamUtils.getParameter(request, "createDate");
            try {
                if (createDate != null && !"".equals(createDate)) {
                    entity.setCreateDate(new Timestamp(df.parse(createDate).getTime()));
                }
            } catch (Exception e) {
            }
            entity.setContent(content);
            entity.setReceiver(receiver);
            entity.setSender(sender);
            List<ChatLog> logs = logsManager.query(entity);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, logs);
            replyMessage(writer.toString(), response, out);
        } else {       	
        	System.out.println("多条件搜索查询search");
            String sender = ParamUtils.getParameter(request, "sender");
            String receiver = ParamUtils.getParameter(request, "receiver");
            String content = ParamUtils.getParameter(request, "content");
            String createDate = ParamUtils.getParameter(request, "createDate");
            try {
                if (createDate != null && !"".equals(createDate)) {
                   entity.setCreateDate(new Timestamp(df.parse(createDate).getTime()));
                }
            } catch (Exception e) {
            }
            entity.setContent(content);
            entity.setReceiver(receiver);
            entity.setSender(sender);
            List<HashMap<String, Object>> logs = logsManager.search(entity);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, logs);
            replyMessage(writer.toString(), response, out);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        AuthCheckFilter.removeExclude("chatlogs/ChatLogsServlet");
        AuthCheckFilter.removeExclude("chatlogs");
    }

    private void replyMessage(String message, HttpServletResponse response, PrintWriter out) {
        response.setContentType("text/json");
        out.println(message);
        out.flush();
    }
    
}


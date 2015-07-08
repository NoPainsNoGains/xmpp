<%@page import="org.jivesoftware.openfire.plugin.entity.ChatLogRoom"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="org.jivesoftware.util.ParamUtils"%>
<%@page import="org.jivesoftware.openfire.XMPPServer"%>
<%@page import="org.jivesoftware.openfire.plugin.ChatLogsPlugin"%>
<%@page import="org.jivesoftware.openfire.plugin.DbChatLogsRoomManger"%>
<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<title>ChatLogsRoom 群聊天记录</title>  
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
    <meta name="pageID" content="chatLogsRoom-service"/>  
    </head>  
    <%
    	String sender = ParamUtils.getParameter(request, "sender");
   		String strid = ParamUtils.getParameter(request, "roomid");
    	String content = ParamUtils.getParameter(request, "content");
    	ChatLogRoom entity = new ChatLogRoom();
    	if(strid==null){
    		entity.setRoomid(0);
    	}else{
    		int roomid = Integer.parseInt(strid);
    		entity.setRoomid(roomid);
    	}
    	entity.setSender(sender);
		entity.setContent(content);
		DbChatLogsRoomManger logsRoomManager = DbChatLogsRoomManger.getInstance();
    	List<ChatLogRoom> logs = logsRoomManager.query(entity);
		System.out.println("sender: "+entity.getSender()+" roomid:"+entity.getRoomid()+" content:"+entity.getContent());
    %>
    <body>
    <div class="jive-contentBoxHeader">设置数据库删除时间</div>
  	 <div class="jive-contentBox">
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=10">10</a>
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=20">20</a>
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=30">30</a>
    </div>
    
    <div class="jive-contentBoxHeader">查看聊天用户</div>
    <div class="jive-contentBox">                     
     <form action="${pageContext.request.contextPath }/plugins/chatlogs/roomservlet">
	            群号：<input type="text" name="alluseroomid" value="${param.alluseroomid}">   
	          <input type="submit">
		      <input type="text" name="action" value="alluser" style="visibility:hidden"> 
		
	 </form>
    </div>

    <div class="jive-contentBoxHeader">搜索</div>
    <div class="jive-contentBox">
	    <form action="chatLogsRoom-service.jsp">
	            群号：<input type="text" name="roomid" value="${param.roomid }">   
	            发送人：<input type="text" name="sender" value="${param.sender }">      
	            内容：<input type="text" name="content" value="${param.content }">
	            <input type="submit">
	            <input type="reset">
	    </form>
    </div>
    <div class="jive-table">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th>群号</th>
                <th>发送人</th>
                <th>内容</th>
                <th>发送时间</th>
                <th>影藏</th>
                <th>删除</th>
            </tr>
        </thead>
         <tbody>
            <% for (int i = 0, len = logs.size(); i < len; i++) { 
                ChatLogRoom log = logs.get(i);
            %>
            <tr class="jive-<%= i % 2 == 0 ? "even" : "odd" %>">
                <td><%=log.getRoomid() %></td>
                <td><%=log.getSender() %></td>
                <td><%=log.getContent() %></td>
                <td><%=log.getCreateDate() %></td>
                <td>
                	<a href="${pageContext.request.contextPath }/plugins/chatlogs/roomservlet?action=hide&messageId=<%=log.getMessageId()%>&roomid=<%=log.getRoomid()%>">
                    	<img title="影藏" src="images/hide.png">
               		</a>
               	</td>
               	<td>
                	<a href="${pageContext.request.contextPath }/plugins/chatlogs/roomservlet?action=remove&messageId=<%=log.getMessageId()%>&roomid=<%=log.getRoomid()%>">
                    	<img title="删除" src="images/delete.png">
               		</a>
               	</td>
            </tr>
            <% } %>
         </tbody> 
        </table>
    </div>
    </body>  
</html>  
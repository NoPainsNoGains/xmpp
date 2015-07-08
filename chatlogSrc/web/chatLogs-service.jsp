<%@page import="org.jivesoftware.openfire.plugin.entity.ChatLog"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="org.jivesoftware.util.ParamUtils"%>
<%@page import="org.jivesoftware.openfire.XMPPServer"%>
<%@page import="org.jivesoftware.openfire.plugin.ChatLogsPlugin"%>
<%@page import="org.jivesoftware.openfire.plugin.DbChatLogsManager"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>ChatLogs 聊天记录</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="pageID" content="chatLogs-service"/>
  </head>
  <%
    String sender = ParamUtils.getParameter(request, "sender");
    String receiver = ParamUtils.getParameter(request, "receiver");
    String content = ParamUtils.getParameter(request, "content");
    ChatLog entity = new ChatLog();
    entity.setContent(content);
    entity.setReceiver(receiver);
    entity.setSender(sender);
    DbChatLogsManager logsManager = DbChatLogsManager.getInstance();
    List<ChatLog> logs = logsManager.query(entity);
     System.out.println("1sender: "+entity.getSender()+" receiver:"+entity.getReceiver()+" content:"+entity.getContent());
  %>
  <body>
  	<div class="jive-contentBoxHeader">设置数据库删除时间</div>
  	 <div class="jive-contentBox">
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=10">10</a>
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=20">20</a>
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=time&vday=30">30</a>
    </div>
    
    <div class="jive-contentBoxHeader">所有聊天用户</div>
    <div class="jive-contentBox">
        <a href="${pageContext.request.contextPath }/plugins/chatlogs?action=all!contact">查看</a>
    </div>

    <div class="jive-contentBoxHeader">搜索</div>
    <div class="jive-contentBox">
	    <form action="chatLogs-service.jsp">
	            发送人：<input type="text" name="sender" value="${param.sender }">
	            接收人：<input type="text" name="receiver" value="${param.receiver }">
	            内容：<input type="text" name="content" value="${param.content }">
	            <input type="submit">
	            <input type="reset">
	    </form>
    </div>
    <div class="jive-table">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th>发送人</th>
                <th>接收者</th>
                <th>内容</th>
                <th>发送时间</th>
                <th>删除</th>
            </tr>
        </thead>
        <tbody>
            <% for (int i = 0, len = logs.size(); i < len; i++) { 
                ChatLog log = logs.get(i);
            %>
            <tr class="jive-<%= i % 2 == 0 ? "even" : "odd" %>">
                <td><%=log.getSender() %></td>
                <td><%=log.getReceiver() %></td>
                <td><%=log.getContent() %></td>
                <td><%=log.getCreateDate() %></td>
               	<td>
                	<a href="${pageContext.request.contextPath }/plugins/chatlogs?action=remove!contact&messageId=<%=log.getMessageId() %>">
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
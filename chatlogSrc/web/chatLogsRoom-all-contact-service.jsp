<%@page import="org.xmpp.packet.JID"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>ChatLogsRoom 群用户</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="pageID" content="chatLogsRoom-all-contact-service"/>
  </head>
  <body>
    <div class="jive-contentBoxHeader">ChatLogsRoom 群聊天联系人</div>
    <div class="jive-table">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th>联系人JID</th>
                <th>他/她的聊天记录</th>
            </tr>
        </thead>
        <tbody>
            <% 
            Object obj = request.getAttribute("alluser");
            if (obj != null) {
                List<String> allContact = (List<String>) obj;
                for (int i = 0, len = allContact.size(); i < len; i++) {
                    String contact = allContact.get(i);
                    JID jid = new JID(contact);
                %>
                <tr class="jive-<%= i % 2 == 0 ? "even" : "odd" %>">
                       <td><%=contact %></td>
                    <td>
                        <a href="${pageContext.request.contextPath }/plugins/chatlogs/chatLogsRoom-service.jsp?sender=<%=jid.getNode() %>">他/她的聊天记录</a>
                    </td>
                </tr>
            <% }
            }
            %>
         </tbody>
        </table>
    </div>
  </body>
</html>
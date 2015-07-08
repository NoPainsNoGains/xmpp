<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>ChatLogs 聊天记录 openfire plugin</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="pageID" content="chatLogs-all-contact-service"/>
  </head>
  <body>
    <div class="jive-contentBoxHeader">ChatLogs 所有聊天联系人</div>
    <div class="jive-table">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <thead>
            <tr>
                <th>最近联系人JID</th>
                <th>【他/她】的联系人</th>
                <th>他/她的聊天记录</th>
            </tr>
        </thead>
        <tbody>
            <% 
            Object obj = request.getAttribute("lastContact");
            if (obj != null) {
                List<String> allContact = (List<String>) obj;
                for (int i = 0, len = allContact.size(); i < len; i++) {
                    String contact = allContact.get(i);
                %>
                <tr class="jive-<%= i % 2 == 0 ? "even" : "odd" %>">
                       <td><%=contact %></td>
                    <td>
                        <a href="${pageContext.request.contextPath }/plugins/chatlogs/chatLogs-service.jsp?sender=${param.sender}&receiver=<%=contact%>">与该联系人的聊天记录</a>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath }/plugins/chatlogs/chatLogs-service.jsp?receiver=<%=contact%>">该联系人的所有聊天记录</a>
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
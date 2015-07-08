package org.jivesoftware.openfire.plugin.entity;

import org.jivesoftware.util.JiveConstants;

public class FileLog {
		private long messageId;
	    private String sender;//发者
	    private String receiver;//收者
	    private String createDate;
	    private String content;//内容
	    private int length;
	    private int state; // 1 表示删除
		public long getMessageId() {
			return messageId;
		}
		public void setMessageId(long messageId) {
			this.messageId = messageId;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getReceiver() {
			return receiver;
		}
		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getLength() {
			return length;
		}
		public void setLength(int length) {
			this.length = length;
		}
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		 public class FileLogConstants extends JiveConstants { 
		        public static final int CHAT_LOGS = 1;// 日志表id自增对应类型
		        public static final int USER_ONLINE_STATE = 53;// 用户在线统计id自增对应类型
		 }
}

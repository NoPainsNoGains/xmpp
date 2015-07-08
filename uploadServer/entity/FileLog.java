package org.jivesoftware.openfire.plugin.entity;

import org.jivesoftware.util.JiveConstants;

public class FileLog {
		private long messageId;
	    private String sender;//����
	    private String receiver;//����
	    private String createDate;
	    private String content;//����
	    private int length;
	    private int state; // 1 ��ʾɾ��
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
		        public static final int CHAT_LOGS = 1;// ��־��id������Ӧ����
		        public static final int USER_ONLINE_STATE = 53;// �û�����ͳ��id������Ӧ����
		 }
}

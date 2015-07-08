package org.jivesoftware.openfire.plugin.entity;

import java.sql.Timestamp;

import org.jivesoftware.util.JiveConstants;

public class ChatLog {
    private long messageId;
    private String sender;//发者
    private String receiver;//收者
    private Timestamp createDate;
    private String content;//内容
    private int length;
    private int state; // 1表示下载
  
    public interface LogState {
        int show = 0;
        int remove = 1;
    }
    /*construct*/
    public ChatLog() {

    }
    public ChatLog(Timestamp createDate, String content, String detail, int length) {
        super();
        this.createDate = createDate;
        this.content = content;
        this.length = length;
    }
    public ChatLog(long messageId, Timestamp createDate, String content, String detail, int length, int state) {
        super();
        this.messageId = messageId;
        this.createDate = createDate;
        this.content = content;
        this.length = length;
        this.state = state;
    }
    public ChatLog(String sender, String receiver, Timestamp createDate, String content, String detail, int length, int state) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.createDate = createDate;
        this.content = content;
        this.length = length;
        this.state = state;
    }
    /*get set*/
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
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
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
}

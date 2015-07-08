package org.jivesoftware.openfire.plugin.entity;

import java.sql.Timestamp;

import org.jivesoftware.util.JiveConstants;

public class ChatLogRoom {
	private long messageId;
	private String sender;// 发者
	private long roomid;
	private Timestamp createDate;
	private String content;// 内容
	private int length;
	private int state; // 无作用
	/* construct */
	public ChatLogRoom() {

	}
	
	public ChatLogRoom(long messageId, String sender, long roomid,
			Timestamp createDate, String content, int length, int state) {
		super();
		this.messageId = messageId;
		this.sender = sender;
		this.roomid = roomid;
		this.createDate = createDate;
		this.content = content;
		this.length = length;
		this.state = state;
	}

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

	public long getRoomid() {
		return roomid;
	}

	public void setRoomid(long roomid) {
		this.roomid = roomid;
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

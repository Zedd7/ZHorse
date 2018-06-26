package com.github.zedd7.zhorse.database;

public class FriendRecord {
	
	private String requester;
	private String recipient;
	
	public FriendRecord(String requester, String recipient) {
		this.requester = requester;
		this.recipient = recipient;
	}

	public String getRequester() {
		return requester;
	}

	public String getRecipient() {
		return recipient;
	}

}

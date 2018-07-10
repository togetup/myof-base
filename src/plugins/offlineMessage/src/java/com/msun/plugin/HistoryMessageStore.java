package com.msun.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class HistoryMessageStore {
	
	private static final Logger log = LoggerFactory.getLogger(HistoryMessageStore.class);
	
	private static final String INSERT_HISTORYMSG =
	        "INSERT INTO historymsg (username, messageID, creationDate, messageSize, stanza) " +
	        "VALUES (?, ?, ?, ?, ?)";
	
	// 单例模式
	private static HistoryMessageStore instance = null;
	
	public static HistoryMessageStore getInstance() {
		if (instance == null) {
			instance = new HistoryMessageStore();
		}
		
		return instance;
	}
	
	
	/**
	 * 向数据库中插入数据
	 * @param message
	 */
	public void addMessage(Message message) {
        if (message == null) {
            return;
        }

        JID recipient = message.getTo();
        String username = recipient.getNode();
        if (username == null) {
            return;
        }

        String messageID = message.getID();

        String msgXML = message.getElement().asXML();

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_HISTORYMSG);
            pstmt.setString(1, username);
            pstmt.setString(2, messageID);
            pstmt.setString(3, StringUtils.dateToMillis(new java.util.Date()));
            pstmt.setInt(4, msgXML.length());
            pstmt.setString(5, msgXML);
            pstmt.executeUpdate();
        }catch (Exception e) {
            log.error("数据库操作异常：" + e.getMessage());
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }
}

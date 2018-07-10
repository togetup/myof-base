package com.msun.plugin;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.openfire.OfflineMessageStore;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class OfflineMessagePlugin implements Plugin, PacketInterceptor {
private static final Logger log = LoggerFactory.getLogger(OfflineMessagePlugin.class);
	
	private InterceptorManager interceptorManager;
	
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("OfflineMessage init");
	
		interceptorManager = InterceptorManager.getInstance();
		interceptorManager.addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		log.info("OfflineMessage destroy");
		
		interceptorManager.removeInterceptor(this);
	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		
		if (incoming && !processed) {
			if (packet instanceof Message) {
				Message msg = (Message)packet;

				// 只对有body内容的消息做处理
				String body = msg.getBody();				
				if (body != null && StringUtils.isNotEmpty(body)) {
					HistoryMessageStore.getInstance().addMessage(msg);
				}
			}		
		}		
	}
}

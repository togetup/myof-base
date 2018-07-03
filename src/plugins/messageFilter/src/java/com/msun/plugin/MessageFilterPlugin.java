package com.msun.plugin;

import java.io.File;

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

/**
 * 插件的初始化工作
 * @author togetup
 */
public class MessageFilterPlugin implements Plugin, PacketInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(MessageFilterPlugin.class);
	
	
	private InterceptorManager interceptorManager;
			
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("MessageFilter init");
		
		interceptorManager = InterceptorManager.getInstance();
		interceptorManager.addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		log.info("MessageFilter destroy");
		
		interceptorManager.removeInterceptor(this);
	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		
		if (incoming && !processed) {
			if (packet instanceof Message) {
				Message msg = (Message)packet;
				String body = msg.getBody();
				
				if (body!=null && body.contains("fuck")) {
					PacketRejectedException rejectException = new PacketRejectedException();
					rejectException.setRejectionMessage("fuck is error");
					throw rejectException;
				}				
			}		
		}		
		
	}	
}

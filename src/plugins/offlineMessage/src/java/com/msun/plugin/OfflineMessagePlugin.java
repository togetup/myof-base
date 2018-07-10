package com.msun.plugin;

import java.io.File;
import java.util.Map;

import org.jivesoftware.openfire.OfflineMessageStore;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * 插件的初始化工作
 * @author togetup
 */
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
				if (body != null) {
					OfflineMessageStore.getInstance().addMessage(msg);
				}		

				// 这里就把消息终止到这里了，即使对方在线也不能收到，只有等对方再次上线后已离线消息的形式发送过去，然后消息表ofoffline中相应的信息删除
				// 所以如果要实际存储的话，新建表进行存储
				throw new PacketRejectedException();
			}		
		}		
	}
}

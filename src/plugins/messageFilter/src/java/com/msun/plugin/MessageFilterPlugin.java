package com.msun.plugin;

import java.io.File;
import java.util.Map;

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
public class MessageFilterPlugin implements Plugin, PacketInterceptor, PropertyEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(MessageFilterPlugin.class);
	
	
	private InterceptorManager interceptorManager;
	
	// 被过滤的内容
	private String filterContent;
	
	/**
	 * 构造函数
	 */
	public MessageFilterPlugin() {
		// 第一个参数表示数据库表中的属性名，第二个属性表示取不到时的默认值
		filterContent = JiveGlobals.getProperty("plugin.messagefilter.content", "fuck");
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.info("MessageFilter init");
		
		// 将此类加入属性监听管理
		PropertyEventDispatcher.addListener(this);
		
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
				
				if (body!=null && body.contains(filterContent)) {
					PacketRejectedException rejectException = new PacketRejectedException();
					rejectException.setRejectionMessage(filterContent + " is error");
					throw rejectException;
				}				
			}		
		}		
		
	}

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		log.info("propertySet:" + property);	
		
		// 数据库中的值修改后自动更新程序中的内容
		if (property.equals("plugin.messagefilter.content")) {
			filterContent = params.get("value").toString();
		}
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		log.info("propertyDeleted:" + property);	
		
		if (property.equals("plugin.messagefilter.content")) {
			filterContent = "";
		}
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		log.info("xmlPropertySet:" + property);			
	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		log.info("xmlPropertyDeleted:" + property);			
	}	
}

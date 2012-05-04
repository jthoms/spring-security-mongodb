package com.sustia;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public final class Launcher {
	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(System.getProperty("port", "8080"));
		Server server = new Server(port);
		ProtectionDomain domain = Launcher.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(location.toExternalForm());
		server.setHandler(webapp);
		server.start();
		server.join();
	}
}
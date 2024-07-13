package io.github.severnarch.javahttpserver;

import io.github.severnarch.javahttpserver.Configuration;
import io.github.severnarch.javahttpserver.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JavaHTTPServer {
	public final static String JHS_Version = "0.0.0";
	
	private static File rootDirectory;
	private static Configuration serverConfig;

	public static void main(String[] args) {
		rootDirectory = new File(System.getProperty("user.dir"));
		try {
			rootDirectory = new File(JavaHTTPServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!rootDirectory.isDirectory()) {
				rootDirectory = new File(rootDirectory.getParent());
			}	
		} catch (java.net.URISyntaxException exc) {}
		Logger.setup();

		if (rootDirectory.getAbsolutePath() == System.getProperty("user.dir")) {
			Logger.warn("Unable to find the .jar file's location, using current working directory for configurations.");
		} else {
			Logger.info(String.format("JAR file located:%s",Logger.Colour.YELLOW_BRIGHT.code), rootDirectory.getAbsolutePath());
		}
		Logger.info(String.format("JavaHTTPServer version:%s",Logger.Colour.YELLOW_BRIGHT.code),JHS_Version);
		serverConfig = new Configuration(rootDirectory.getAbsolutePath()+"\\configs\\server.cfg", "/defaults/server.cfg");
		stop(0);
	}

	public static File getRootDirectory() {
		return rootDirectory;
	}

	public static void stop(int code) {
		Logger.info(String.format("JHS stopped with exit code%s",Logger.Colour.YELLOW_BRIGHT.code),code);
		try {
			int foundDef = 0;
			List<String> lines = new BufferedReader(new InputStreamReader(JavaHTTPServer.class.getResourceAsStream("/exitcodes.txt"))).lines().collect(Collectors.toList());
			for (String line : lines) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] sline = line.split(": ");
				if (Integer.valueOf(sline[0]) == code) {
					Logger.info(String.format("Exit code definition:%s",Logger.Colour.YELLOW_BRIGHT.code),sline[1]);
					foundDef = 1;
					break;
				}
			}
			if (foundDef == 0) {
				Logger.warn(String.format("Unable to get exit code definition:%s",Logger.Colour.YELLOW_BRIGHT.code),"Code number not found in exitcodes.txt");
			}
		} catch (Exception exc) {
			Logger.warn(String.format("Unable to get exit code definition:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
		}
		System.exit(code);
	}
}
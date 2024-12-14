package io.github.severnarch.javahttpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	private static File logFile = new File(".");
	private static boolean filelogEnabled = false;

	public static void setup() {
		System.out.println("Setting up logger...");
		File rootDirectory = JavaHTTPServer.getRootDirectory();
		if (!new File(rootDirectory.getAbsolutePath()+"/logs").exists()) {
			System.out.println("Attempting to create logs directory...");
			boolean success = new File(rootDirectory.getAbsolutePath()+"/logs").mkdir();
			if (!success) {
				warn("Failed to create log directory.");
			}
		}
		if (new File(rootDirectory.getAbsolutePath()+"/logs").exists()) {
			System.out.println("Log directory exists.");
			logFile = new File(rootDirectory.getAbsolutePath()+"/logs/"+new SimpleDateFormat("dd'-'MM'-'yyyy'_'HH'-'mm'-'ss'_'z'.txt'").format(Calendar.getInstance().getTime()));
			try {
				if (logFile.createNewFile()) {
					filelogEnabled = true;
				}
				Logger.info(String.format("Created log file:%s",Colour.YELLOW_BRIGHT.code),logFile.getName());
			} catch (IOException exc) {
				System.out.println("Unable to create log file, file logging is disabled.");
			}
		} else {
			System.out.println("Unable to create log directory, file logging is disabled.");
		}
	}

	@SuppressWarnings("unused")
	public enum Colour {
		RESET("\033[0m"),
		BLACK("\033[0;30m"),
		RED("\033[0;31m"),
		GREEN("\033[0;32m"),
		YELLOW("\033[0;33m"),
		BLUE("\033[0;34m"),
		MAGENTA("\033[0;35m"),
		CYAN("\033[0;36m"),
		WHITE("\033[0;37m"),
		BLACK_BRIGHT("\033[0;90m"),
		RED_BRIGHT("\033[0;91m"),
		GREEN_BRIGHT("\033[0;92m"),
		YELLOW_BRIGHT("\033[0;93m"),
		BLUE_BRIGHT("\033[0;94m"),
		MAGENTA_BRIGHT("\033[0;95m"),
		CYAN_BRIGHT("\033[0;96m"),
		WHITE_BRIGHT("\033[0;97m");

		public final String code;

        	Colour(String code) {
        		this.code = code;
        	}
	}

	private static String timestampPrefix() {
		return new SimpleDateFormat(String.format("'%s[%s'dd'%s/%s'MM'%s/%s'yyyy' 'HH'%s:%s'mm'%s:%s'ss' 'z'%s]'", Colour.BLACK_BRIGHT.code, Colour.CYAN.code, Colour.BLACK_BRIGHT.code, Colour.CYAN.code, Colour.BLACK_BRIGHT.code, Colour.CYAN.code, Colour.BLACK_BRIGHT.code, Colour.CYAN.code, Colour.BLACK_BRIGHT.code, Colour.CYAN.code, Colour.BLACK_BRIGHT.code)).format(Calendar.getInstance().getTime());
	}
	
	private static void writeLogFile(String line) {
		if (filelogEnabled) {
			try {
				Files.write(
      					Paths.get(logFile.getAbsolutePath()), 
      					(line.replaceAll("\\[[0-9A-Z];[0-9A-Z]*m","")+"\n").getBytes(), 
      					StandardOpenOption.APPEND
				);
			} catch (IOException exc) {
				System.out.println("Unable to write to log file:");
			}
		}
	}

	public static void info(Object... args) {
		StringBuilder content = new StringBuilder();
		for (Object arg : args) {
			content.append(" ").append(arg);
		}
		String logLine = timestampPrefix()+String.format("[%sINFO   %s]%s ", Colour.WHITE.code, Colour.BLACK_BRIGHT.code, Colour.WHITE.code)+ content.toString().trim();
		writeLogFile(logLine);
		System.out.println(logLine);
	}
	public static void warn(Object... args) {
		StringBuilder content = new StringBuilder();
		for (Object arg : args) {
			content.append(" ").append(arg);
		}
		String logLine = timestampPrefix()+String.format("[%sWARNING%s]%s ", Colour.YELLOW.code, Colour.BLACK_BRIGHT.code, Colour.YELLOW.code)+ content.toString().trim();
		writeLogFile(logLine);
		System.out.println(logLine);
	}
	public static void error(Object... args) {
		StringBuilder content = new StringBuilder();
		for (Object arg : args) {
			content.append(" ").append(arg);
		}
		String logLine = timestampPrefix()+String.format("[%sERROR  %s]%s ", Colour.RED.code, Colour.BLACK_BRIGHT.code, Colour.RED.code)+ content.toString().trim();
		writeLogFile(logLine);
		System.out.println(logLine);
	}
}
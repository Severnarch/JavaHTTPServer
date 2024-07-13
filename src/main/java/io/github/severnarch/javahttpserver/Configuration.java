package io.github.severnarch.javahttpserver;

import io.github.severnarch.javahttpserver.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {
	private final String fileLocation;
	private final String templateLocation;
	private HashMap<String, String> configData;
	
	public Configuration(String fileLocation, String templateLocation) {
		Logger.info(String.format("Checking configuration file%s", Logger.Colour.YELLOW_BRIGHT.code),fileLocation,String.format("%swith template file%s",Logger.Colour.WHITE.code,Logger.Colour.YELLOW_BRIGHT.code),templateLocation);
		this.fileLocation = fileLocation;
		this.templateLocation = templateLocation;
		int createdNewFile = 0;
		if (!new File(fileLocation).exists()) {
			Logger.info("Specified configuration file not found!");
			Logger.info("Creating configuration file...");
			try {
				createdNewFile = 1;
				new File(new File(fileLocation).getParent()).mkdir();
				new File(fileLocation).createNewFile();
			} catch (IOException exc) {
				Logger.error("Unable to create configuration file!");
				JavaHTTPServer.stop(1501);
			}
		}
		if (new File(fileLocation).exists()) {
			Logger.info("Configuration file exists.");
			if (createdNewFile == 1 && templateLocation != null) {
				try {
					Logger.info("Writing default configuration file...");
					Files.copy(JavaHTTPServer.class.getResourceAsStream(templateLocation), Paths.get(fileLocation), StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception exc) {
					Logger.warn(String.format("Encountered exception:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
					JavaHTTPServer.stop(1502);
				}
			}
			try {
				HashMap<String, String> templateValues = new HashMap<String, String>();
				List<String> lines = new BufferedReader(new InputStreamReader(JavaHTTPServer.class.getResourceAsStream(templateLocation))).lines().collect(Collectors.toList());
				for (String line : lines) {
					if (line.startsWith("#")) {
						continue;
					}
					String[] sline = line.split("=");
					if (sline.length == 2) {
						templateValues.put(sline[0].trim(), sline[1].trim());
					} else if (sline.length == 1) {
						templateValues.put(sline[0].trim(), null);
					}
				}
				this.configData = templateValues;
				lines = Files.readAllLines(Paths.get(fileLocation));
				for (String line : lines) {
					if (line.startsWith("#")) {
						continue;
					}
					String[] sline = line.split("=");
					if (sline.length == 2) {
						this.configData.put(sline[0].trim(), sline[1].trim());
					} else if (sline.length == 1 && templateLocation != null) {
						this.configData.put(sline[0].trim(), templateValues.get(sline[0].trim()));
					}
				}
				String[] fileDir = fileLocation.replaceAll("\\\\","/").split("/");
				String fileName = fileDir[fileDir.length - 1];
				if (this.configData.keySet().size() != 0) {
					Logger.info(String.format("Read configuration:%s",Logger.Colour.YELLOW_BRIGHT.code),fileName);
				} else {
					Logger.warn(String.format("Configuration empty:%s",Logger.Colour.YELLOW_BRIGHT.code),fileName);
				}
			} catch (Exception exc) {
				Logger.warn(String.format("Encountered exception:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
				JavaHTTPServer.stop(1503);
			}
		} else {
			Logger.error("Configuration file doesn't exist! Stopping to prevent issues...");
			JavaHTTPServer.stop(1504);
		}
	}
	public Configuration(String fileLocation) {this(fileLocation, null);}
}
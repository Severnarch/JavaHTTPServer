package io.github.severnarch.javahttpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Configuration {
	private final String fileLocation;
    private HashMap<String, String> configData;
	private HashMap<String, String> templateData;
	
	public Configuration(String fileLocation, String templateLocation) {
		Logger.info(String.format("Checking configuration file%s", Logger.Colour.YELLOW_BRIGHT.code),fileLocation,String.format("%swith template file%s",Logger.Colour.WHITE.code,Logger.Colour.YELLOW_BRIGHT.code),templateLocation);
		this.fileLocation = fileLocation.replaceAll("\\\\","/");
        int createdNewFile = 0;
		if (!new File(fileLocation).exists()) {
			Logger.info("Specified configuration file not found!");
			Logger.info("Creating configuration file...");
			try {
				createdNewFile = 1;
				boolean dirsuccess = new File(new File(fileLocation).getParent()).mkdir();
				boolean filsuccess = new File(fileLocation).createNewFile();
				if (!dirsuccess || !filsuccess) {
					Logger.error("Failed to create configuration file!");
					JavaHTTPServer.stop(1501);
				}
			} catch (IOException exc) {
				Logger.error("Unable to create configuration file!");
				JavaHTTPServer.stop(1501);
			}
		}
		if (new File(fileLocation).exists()) {
			Logger.info("Configuration file exists.");
			Path fileLocationPath = Paths.get(fileLocation);
			if (createdNewFile == 1 && templateLocation != null) {
				try {
					Logger.info("Writing default configuration file...");
					Files.copy(Objects.requireNonNull(JavaHTTPServer.class.getResourceAsStream(templateLocation)), fileLocationPath, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception exc) {
					Logger.warn(String.format("Encountered exception:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
					JavaHTTPServer.stop(1502);
				}
			}
			try {
				if (templateLocation != null) {
					this.templateData = new HashMap<>();
					List<String> lines = new BufferedReader(new InputStreamReader(Objects.requireNonNull(JavaHTTPServer.class.getResourceAsStream(templateLocation)))).lines().collect(Collectors.toList());
					for (String line : lines) {
						if (line.startsWith("#")) {
							continue;
						}
						String[] sline = line.split("=");
						if (sline.length == 2) {
							this.templateData.put(sline[0].trim(), sline[1].trim());
						} else if (sline.length == 1) {
							this.templateData.put(sline[0].trim(), null);
						}
					}
					this.configData = this.templateData;
				}
				List<String> lines = Files.readAllLines(fileLocationPath);
				for (String line : lines) {
					if (line.startsWith("#")) {
						continue;
					}
					String[] sline = line.split("=");
					if (sline.length == 2) {
                        assert this.configData != null;
                        this.configData.put(sline[0].trim(), sline[1].trim());
					} else if (sline.length == 1 && templateLocation != null) {
						this.configData.put(sline[0].trim(), templateData.get(sline[0].trim()));
					}
				}
				String[] fileDir = fileLocation.split("/");
				String fileName = fileDir[fileDir.length - 1];
                assert this.configData != null;
                if (!this.configData.isEmpty()) {
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

	public String get(String key) {
		String[] fileDir = fileLocation.split("/");
		String fileName = fileDir[fileDir.length - 1];
		if (this.configData.containsKey(key)) {
			return this.configData.get(key);
		} else if (this.templateData.containsKey(key)) {
			Logger.warn(String.format("Key '%s%s%s' does not exist in configuration '%s%s%s', using value from template.",Logger.Colour.YELLOW_BRIGHT.code,key,Logger.Colour.YELLOW.code,Logger.Colour.YELLOW_BRIGHT.code,fileName,Logger.Colour.YELLOW.code));
			return this.templateData.get(key);
		} else {
			Logger.error(String.format("Key '%s%s%s' does not exist in configuration '%s%s%s' nor its template!",Logger.Colour.YELLOW_BRIGHT.code,key,Logger.Colour.RED.code,Logger.Colour.YELLOW_BRIGHT.code,fileName,Logger.Colour.RED.code));
			return null;
		}
	}
}
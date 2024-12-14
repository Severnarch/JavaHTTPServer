package io.github.severnarch.javahttpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Runtime;
import java.lang.Thread;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavaHTTPServer {
	public final static String JHS_Version = "1.0.3";
	
	private static File rootDirectory;
    private static ServerSocket server;
	private static Boolean hasShutdownHookRan = false;

	public static void main(String[] args) {
		rootDirectory = new File(System.getProperty("user.dir"));
		try {
			rootDirectory = new File(JavaHTTPServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!rootDirectory.isDirectory()) {
				rootDirectory = new File(rootDirectory.getParent());
			}	
		} catch (java.net.URISyntaxException ignored) {}
		Logger.setup();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				hasShutdownHookRan = true;
				stop(0);
			} catch (Exception exc) {
				Logger.error("Exception in shutdown hook:",exc);
			}
		}));
		if (rootDirectory.getAbsolutePath().equals(System.getProperty("user.dir"))) {
			Logger.warn("Unable to find the .jar file's location, using current working directory for configurations.");
		} else {
			Logger.info(String.format("JAR file located:%s",Logger.Colour.YELLOW_BRIGHT.code), rootDirectory.getAbsolutePath());
		}
		Logger.info(String.format("JavaHTTPServer version:%s",Logger.Colour.YELLOW_BRIGHT.code),JHS_Version);
        Configuration serverConfig = new Configuration(rootDirectory.getAbsolutePath() + "/configs/server.cfg", "/defaults/server.cfg");
		while (!new File(rootDirectory.getAbsolutePath()+"/"+ serverConfig.get("mirrorDirectory")+"/").exists()) {
			boolean created = new File(rootDirectory.getAbsolutePath()+"/"+ serverConfig.get("mirrorDirectory")+"/").mkdir();
			if (!created) {
				JavaHTTPServer.stop(1505);
			}
		}
		try {
			server = new ServerSocket(Integer.parseInt(serverConfig.get("port")), Integer.parseInt(serverConfig.get("maxQueueLength")), InetAddress.getByName(serverConfig.get("address")));
			Logger.info(String.format("Server started on%s %s:%s",Logger.Colour.YELLOW_BRIGHT.code, serverConfig.get("address"), serverConfig.get("port")));
			while (!hasShutdownHookRan) {
				Socket client = server.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String clientMethod;
				String location;
				String[] largs = in.readLine().split(" ");
				clientMethod = largs[0];
				location = largs[1];
				String responseCode = "404 Not Found";
				String responseType = "text/plain";
				String responseBody;
				//noinspection ALL ; this skips any first empty lines of HTTP requests
                while ("\r\n".equals(in.readLine())) {}
				try {
					File queriedFile = new File(rootDirectory.getAbsolutePath()+"/"+ serverConfig.get("mirrorDirectory")+location);
					if (queriedFile.exists()) {
						if (!location.endsWith("/")) {
							String[] fileDir = location.split("/");
							String fileName = fileDir[fileDir.length - 1];
							String[] fileArgs = fileName.split("\\.");
							if (fileArgs.length > 1) {
								String extension = fileArgs[fileArgs.length - 1];
								List<String> lines = new BufferedReader(new InputStreamReader(Objects.requireNonNull(JavaHTTPServer.class.getResourceAsStream("/mimetypes.txt")))).lines().collect(Collectors.toList());
								for (String line : lines) {
									String[] sline = line.split(" ");
									if (sline[0].equals("."+extension)) {
										responseType = sline[1];
										break;
									}
								}

							}
							responseCode = "200 OK";
							responseBody = String.join("\n", Files.readAllLines(Paths.get(queriedFile.getAbsolutePath())));
						} else {
							if (Boolean.parseBoolean(serverConfig.get("showFilesInDirectories"))) {
								responseCode = "200 OK";
								responseType = "text/html";
								StringBuilder files = new StringBuilder();
								List<String> dirs = new ArrayList<>();
								dirs.add("..");
								for (File file : Objects.requireNonNull(queriedFile.listFiles())) {
									if (file.isDirectory()) {
										dirs.add(file.getName());
									} else {
										files.append("<li style='list-style-type:\"&#128196;\"'><a href='").append(file.getName()).append("'>").append(file.getName()).append("</a></li>");
									}
								}
								Collections.reverse(dirs);
								for (String dir : dirs) {
									files.insert(0, "<li style='list-style-type:\"&#128193;\"'><a href='" + dir + "/'>" + dir + "/</a></li>");
								}
								responseBody = "<style>a {text-decoration:none;color:black;}</style><title>"+location+"</title><h1>"+location+"</h1><hr><ul>"+files+"</ul>";
							} else {
								if (new File(queriedFile.getAbsolutePath()+"/index.html").exists()) {
									responseCode = "200 OK";
									responseType = "text/html";
								}
								responseBody = String.join("\n", Files.readAllLines(Paths.get(queriedFile.getAbsolutePath()+"/index.html")));
							}
						}
					} else {
						responseType = "text/html";
						File notFoundFile = new File(rootDirectory.getAbsolutePath()+"/"+ serverConfig.get("mirrorDirectory")+"/.special/404.html");
						if (notFoundFile.exists()) {
							responseBody = String.join("\n", Files.readAllLines(Paths.get(notFoundFile.getAbsolutePath())));
						} else {
							responseBody = "<title>404 Not Found</title><h1>404</h1><h2>Not Found</h2>";
						}
					}
				} catch (java.nio.file.NoSuchFileException exc) {
					responseBody = "<title>404 Not Found</title><h1>404</h1><h2>Not Found</h2>";
				} catch (Exception exc) {
					responseCode = "500 Internal Server Error";
					responseBody = "<title>500 Internal Server Error</title><h1>500</h1><h2>Internal Server Error</h2><p>"+exc.getClass()+"<br>See logs for more information.</p>";
					Logger.error(String.format("Encountered exception:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
				}
				Logger.info(String.format("Request from %s%s%s for %s%s%s with method %s%s%s received as %s%s",Logger.Colour.YELLOW_BRIGHT.code,client.getRemoteSocketAddress().toString(),Logger.Colour.WHITE.code,Logger.Colour.YELLOW_BRIGHT.code,location,Logger.Colour.WHITE.code,Logger.Colour.YELLOW_BRIGHT.code,clientMethod,Logger.Colour.WHITE.code,Logger.Colour.YELLOW_BRIGHT.code,responseCode));
				out.println("HTTP/1.1 "+responseCode);
				out.println("Connection: close");
				out.println("Server: JavaHTTPServer/"+JHS_Version);
				out.println("Content-Type: "+responseType);
				out.println("Content-Length: "+responseBody.length());
				out.println();
				out.println(responseBody);
				out.flush();
			}
		} catch (java.net.SocketException exc) {
			Logger.info("Server stopped.");
		} catch (Exception exc) {
			Logger.error(String.format("Encountered exception:%s",Logger.Colour.YELLOW_BRIGHT.code),exc);
		}
		if (!hasShutdownHookRan) {
			stop(0);
		}
	}

	public static File getRootDirectory() {
		return rootDirectory;
	}

	public static void stop(int code) {
		if (server != null) {
			Logger.info("Server exists, stopping...");
			try {
				server.close();
			} catch (IOException exc) {
				Logger.error("Something went wrong stopping the server:",exc);
			}
		}
		Logger.info(String.format("JHS stopped with exit code%s",Logger.Colour.YELLOW_BRIGHT.code),code);
		try {
			int foundDef = 0;
			List<String> lines = new BufferedReader(new InputStreamReader(Objects.requireNonNull(JavaHTTPServer.class.getResourceAsStream("/exitcodes.txt")))).lines().collect(Collectors.toList());
			for (String line : lines) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] sline = line.split(": ");
				if (Integer.parseInt(sline[0]) == code) {
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
		System.out.println(Logger.Colour.RESET.code);
		System.exit(code);
	}
}
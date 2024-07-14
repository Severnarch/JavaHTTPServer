> Please follow the [Installation Guide](INSTALL.md) before doing any of this!
# Getting Started
**Table of Contents**<br>
╠ [Directory Setup](#directory-setup)<br>
╠ [Server Configuration](#server-configuration)<br>
╚ [File System](#file-system)<br>
‎▹ ╚ [Special Files](#special-files#)
## Directory Setup
To get the basic structure of the server ready, open up a command line and run the following command (where `JavaHTTPServer-X.Y.Z.jar` is the jar file you downlaoded in the Installation Guide):
```powershell
java -jar JavaHTTPServer-X.Y.Z.jar
```
Once this runs and it starts the server, press `CTRL` and `C` simultaneously to stop the server as the directory is now set up.
## Server Configuration
Navigate to the `configs` folder, and you'll see a `server.cfg` file. This is your main configuration file, and the valid settings it can use are below.
| Key                  | Description                                                                                                                       | Value Type | Default Value |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------- |:----------:| -------------:|
| address              | The address to host the server on.                                                                                                | String     |     localhost |
| port                 | The port to host the server on.                                                                                                   | Integer    |          8080 |
| maxQueueLength       | The maximum number of connections at any one time. Connections past this number will be refused.                                  | Integer    |            50 |
| mirrorDirectory      | The directory containing files mirrored to the server, e.g. [dir]/docs/index.html equals to [address]:[port]/docs/index.html      | String     |         files |
| showFilesInDirectory | Whether or not directories should attempt to show an index.html file within them, or to show a list of files (like file:/// URLs) | Boolean    |         false |
## File System
The file system used by the server is essentially just a file mirror. Anything under the `mirrorDirectory` folder in the `server.cfg` file is forwarded to the server, 
in a way that with an unchanged mirror directory, a file at `files/hello.txt` is directly accessible from the server at `/hello.txt` or a `files/media/cat.png` is at
`/media/cat.png`.<br><br>If `showFilesInDirectory` is enabled, navigating to a directory on the server in your browser (e.g. `/media/`) will show a file explorer based off of
basic `file:///` URLs.
### Special Files
There is a special directory in the `mirrorDirectory` called `.special`, which while still being accessible by `/.special/` URLs on the server, files inside serve an extra
purpose elsewhere. A list of these files can be found below.
| File Name  | Purpose                        |
| ---------- | ------------------------------ |
| `404.html` | Replaces the default 404 page. |
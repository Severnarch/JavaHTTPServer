# Installation
**Table of Contents**<br>
╠ [Requirements](#requirements)<br>
╠ [Obtaining the Executable](#obtaining-the-executable)<br>
║ ╠ [GitHub Releases](#github-releases)<br>
║ ╚ [Source Building](#source-building)<br>
╠ [Next Steps](#next-steps)
## Requirements
| Software                                             | Version |
| ---------------------------------------------------- | -------:|
| `Java Runtime Environment` or `Java Development Kit` |     ≥ 8 |
## Obtaining the Executable
There are two ways you can get the JHS executable, either you download it from GitHub releases or build it from the source code.
### GitHub Releases
The first way to obtain the JHS executable is to simply download it.
1. Go to the [Releases](https://github.com/Severnarch/JavaHTTPServer/releases) page.
2. Look for the version you want. In most cases, you want the **Latest** release.
3. Open up the **Assets** tab, and download the file named `JavaHTTPServer-X.Y.Z.jar` where `X.Y.Z` is respective to the version of the release you want.
4. You may have noticed the **File Hash** is included in the release information. This is so if you want to verify your file is legitimate, you can check yourself. How you do this depends on your operating system, but most commonly you use one of the following two commands:
>
> **Windows PowerShell**
> ```powershell
> Get-FileHash JavaHTTPServer-X.Y.Z.jar
> ```
> For example, version 1.0.0 has the following output:
> ```
> Algorithm       Hash                                                                   Path
> ---------       ----                                                                   ----
> SHA256          2FE2C91BE8C7A681377C6EC437BE11C02B2E94AB629391A68DFF0CAC47BD623F       C:\Users\Example\D...
> ```
> **Linux Bash**
> ```bash
> sha256sum JavaHTTPServer-X.Y.Z.jar
> ```
> For example, version 1.0.0 has the following output:
> ```
> 2fe2c91be8c7a681377c6ec437be11c02b2e94ab629391a68dff0cac47bd623f
> ```
5. The command output should contain a string of numbers and letters, which is the file Hash. Compare this to what is shown in the release information. If it doesn't match, download the file again and reverify if it matches. If it still doesn't match, you may open an issue for assistance.
6. Move this file elsewhere, e.g. `C:\Users\Example\Documents\JHS` on Windows or `~\JHS` on Linux.
7. Optionally, you can rename this file to whatever you would like.
### Source Building
The second way to obtain the JHS executable is to build it from the source.
1. Go to the [GitHub Repository](https://github.com/Severnarch/JavaHTTPServer).
2. Click on the **Code** dropdown button in the corner, then press **Download ZIP**
3. Navigate to the location where the ZIP is, on Windows this will normally be `C:\Users\<user>\Downloads\`.
4. Extract the ZIP into a folder, the name is up to you.
5. Open a command line in the new extracted folder, and run the following command:
> If using Windows Command Prompt, you can remove the `.\` prefix.
> ```powershell
> .\gradlew build
> ```
6. Allow the command to run, once done navigate to the `builds` folder and open `libs`. Inside, will be a file called `JavaHTTPServer.jar`.
7. Move this file elsewhere, e.g. `C:\Users\Example\Documents\JHS` on Windows or `~\JHS` on Linux.
8. Optionally, you can rename this file to whatever you would like.
## Next Steps
Now you have downloaded the JHS executable, it's time to [get started with using JHS](GETTING_STARTED.md).
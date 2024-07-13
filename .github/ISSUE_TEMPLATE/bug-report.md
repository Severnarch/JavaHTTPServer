---
name: Bug Report
about: Report something that shouldn't seem like it should be happening.
title: ''
labels: Bug Report
assignees: ''

---

<!--
Before making a bug report please ensure that:
 1. You have checked to make sure that
    nobody else has reported this issue.
 2. You have attempted or tried to find
    a way to resolve the issue yourself.
If all of these criteria are met, you may proceed to fill in the fields below.
Make sure you provide a log file! You may modify it to conceal personal
information such as your name, IP address, et cetera.
 --> 

**JavaHTTPServer Version:** `?.?.?`
<!-- 
Your version of JHS can be found in the logs, you want the content on the same line after "`JavaHTTPServer Version:`"
Example: If it says "`JavaHTTPServer Version: 1.2.3`", you are using JHS Version `1.2.3`.
-->

**Exit Code:** `????`
<!--
The exit code can be found once JHS closes properly. The line containing it says "`JHS stopped with exit code ?`", where ? is replaced with the exit code. (Example A)
If the error isn't fatal, resulting in a lack of an exit code, please provide the exception instead. (Example B)
Example A: If it says "`JHS stopped with exit code 1503`", the exit code is `1503` and the program failed to read a configuration file.
Example B: If it says "`Encountered exception: java.lang.NullPointerException`", the exception is "`java.lang.NullPointerException`".
-->

**Reproduction Steps:**
1. ???
2. ???
3. ???<br>
...
<!--
The steps you did in order to get this bug. If you can't find out what causes it, you can replace the instruction list with what you think might be happening or leave this field blank.
-->

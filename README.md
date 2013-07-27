face-hadoop-eclipse
===================

Do face detection and recognition in hadoop, both works on local sfaingle node hadoop and Amazon Elastic MapReduce.

## Compile
This work is only tested in Ubuntu 12.04, and Amazon hadoop 1.0.3. Any update in Amazon may require a rebuild of JavaCV libraries.

In order to compile, first clone this repo, and import it into Eclipse as Existing Project. Right click on the root folder in Package Explorer, "properties", "Java build path", "add external jar files". Add all the jars in "amazon_build/". 

Now you should be able to compile the project. In order to run hadoop tasks, we need to build executable jar. Right click on the root folder, "Export", "Java/Runnable JAR file". Choose "Launch configuration" to be "TextFaceDetection-face-eclipse", and "Library handling" to be "Extract required ...". Click "Finish" to generate the Jar file. If you can't find "TextFaceDetection-face-eclipse", just open TextFaceDetection.java, Ctrl+F11 to run it. The configuration will be generated automatically.

## Run on local hadoop
With the Jar file at hand, you can run it on hadoop. Cd to your hadoop install folder. For me, it's `/usr/local/hadoop`. The first thing is to copy input data into HDFS. I create a folder `/usr/local/hadoop/mycode` for convenience. You can copy `face-eclipse/data/collection.txt` into this folder, and do `hadoop dfs -put mycode/collection.txt /user/hduser/faceinput` to move it into HDFS. 



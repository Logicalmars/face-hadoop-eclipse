face-hadoop-eclipse
===================

Do face detection and recognition in hadoop, both works on local single node hadoop and Amazon Elastic MapReduce.

## Compile
This work is only tested in Ubuntu 12.04, x86 32bit, and Amazon hadoop 1.0.3. Any update in Amazon may require a rebuild of JavaCV libraries.(Especially when they upgrade gcc version, they are using gcc 4.5 now.)

In order to compile, first clone this repo, and import it into Eclipse as Existing Project. Right click on the root folder in Package Explorer, "properties", "Java build path", "add external jar files". Add all the jars in "amazon_build/". 

Now you should be able to compile the project. In order to run hadoop tasks, we need to build executable jar. Right click on the root folder, "Export", "Java/Runnable JAR file". Choose "Launch configuration" to be "TextFaceDetection-face-eclipse", and "Library handling" to be "Extract required ...". Click "Finish" to generate the Jar file. If you can't find "TextFaceDetection-face-eclipse", just open TextFaceDetection.java, Ctrl+F11 to run it. The configuration will be generated automatically.

## Run on local hadoop
With the Jar file at hand, you can run it on hadoop. Cd to your hadoop install folder. For me, it's `/usr/local/hadoop`. The first thing is to copy input data into HDFS. I create a folder `/usr/local/hadoop/mycode` for convenience. You can copy `face-eclipse/data/collection.txt` into `mycode/text_input/`, and do `hadoop dfs -put mycode/text_input /user/hduser/faceinput` to move it into HDFS. 

Then,
```shell
cp /home/linmengl/workspace/face-eclipse/FaceDetection.jar mycode/
bin/hadoop jar mycode/FaceDetection.jar info.mendlin.FaceDetection /user/hduser/faceinput /user/hduser/face-output
```
I have attached a Makefile in `face-eclipse/scripts/Makefile`. You can put it in hadoop install folder. `make text` will copy the jar file, do some clean and run hadoop. Refer to that script for further understanding.

## Run on Amazon EMR
With the same Jar file, you can run it on Amazon EMR. In the web console, click on "Create New Job Flow". Choose "Custom JAR", choose your Jar location in S3, and use the following arguments `info.mendlin.TextFaceDetection s3n://info.mendlin.face/text_input s3n://info.mendlin.face/text_output`, replace your own bucket here.

The most important thing here is you need to bootstrap every node, you can find the script in `face-eclipse/scripts/bootstrap.sh`. Basically it installs all the library that OpenCV needs. It also copies the face classifier into node's local file system. You need to upload this classifier to your S3 bucket and change the corresponding `hadoop fs -get` in the `bootstrap.sh`. 

And a very funny thing you can find is, I use "apt-get install -y" in my bootstrap script. Without "-y", apt-get will ask you a yes/no question, and all the nodes will just suspend there waiting for your decision, which you can't make. SSH into a real elastic node is very important, otherwise you can't find a bug like this. Refer to [here](http://docs.aws.amazon.com/ElasticMapReduce/latest/DeveloperGuide/emr-build-binaries.html) for more ssh info.

happiness: clean_fs
	rm -f mycode/FaceDetection.jar
	cp /home/linmengl/workspace/face-eclipse/FaceDetection.jar mycode/
	bin/hadoop jar mycode/FaceDetection.jar info.mendlin.FaceDetection /user/hduser/faceinput /user/hduser/face-output
	rm -rf face-output
	bin/hadoop dfs -get /user/hduser/face-output face-output

text: clean_fs
	rm -f mycode/FaceDetection.jar
	cp /home/linmengl/workspace/face-eclipse/FaceDetection.jar mycode/
	bin/hadoop jar mycode/FaceDetection.jar info.mendlin.FaceDetection /user/hduser/Text /user/hduser/text-output
	rm -rf text-output
	bin/hadoop dfs -get /user/hduser/text-output text-output

run_facedetect1: compile_fd clean_fs
	bin/hadoop jar mycode/facedetect.jar FaceDetector1 \
	-libjars mycode/lib/javacv.jar,mycode/lib/javacpp.jar,mycode/commons-logging-1.1.3.jar,mycode/lib/javacv-linux-x86.jar,mycode/lib/opencv-2.4.5-linux-x86.jar \
	-archives mycode/lib/javacv.jar,mycode/lib/javacpp.jar,mycode/commons-logging-1.1.3.jar,mycode/lib/javacv-linux-x86.jar,mycode/lib/opencv-2.4.5-linux-x86.jar \	
	/user/hduser/faceinput /user/hduser/face-output \
	-D mapred.job.tracker=local -D java.library.path=/usr/local/hadoop/mycode/lib/

run_facedetect: compile_fd clean_fs
	bin/hadoop jar mycode/facedetect.jar FaceDetect \
	-libjars mycode/lib/javacv.jar,mycode/lib/javacpp.jar,mycode/commons-logging-1.1.3.jar,mycode/lib/javacv-linux-x86.jar,mycode/lib/opencv-2.4.5-linux-x86.jar \
	-archives mycode/lib/javacv.jar,mycode/lib/javacpp.jar,mycode/commons-logging-1.1.3.jar,mycode/lib/javacv-linux-x86.jar,mycode/lib/opencv-2.4.5-linux-x86.jar \	
	/user/hduser/faceinput /user/hduser/face-output \
	-D mapred.job.tracker=local -D java.library.path=/usr/local/hadoop/mycode/lib/

run_wordcount: compile clean_fs	
	bin/hadoop jar mycode/wordcount.jar WordCount -libjars mycode/lib/opencv-246.jar -files mycode/lib/libopencv_java246.so /user/hduser/gutenberg /user/hduser/gutenberg-output

run_wordcount_local: compile clean_fs
	bin/hadoop jar mycode/wordcount.jar WordCount -libjars mycode/lib/opencv-246.jar -files mycode/lib/libopencv_java246.so /user/hduser/gutenberg /user/hduser/gutenberg-output \
	-D mapred.job.tracker=local -D java.library.path=/usr/local/hadoop/mycode/lib/

compile_wc:
	(cd mycode;	make wordcount)

compile_fd:
	(cd mycode; make facedetect)

clean_fs: 
	bin/hadoop dfs -rmr /user/hduser/*-output*

package info.mendlin;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickCount;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickFrequency;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import static com.googlecode.javacv.cpp.opencv_contrib.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DistFaceRecognition extends Configured implements Tool{

	public static class FaceRecognitionMapper extends Mapper<Text, Text, Text, Text> {
		
		public String findValidPath(String filename)
		{
			String path1 = "/usr/local/hadoop/mycode/";
			String path2 = "/home/hadoop/facedata/";
			String path3 = "/home/linmengl/workspace/face-eclipse/att_faces/";
			
			System.out.println(path1 + filename);
			
			if (new File(path1 + filename).exists())
				return path1 + filename;
			
			if (new File(path2 + filename).exists())
				return path2 + filename;
			
			if (new File(path3 + filename).exists())
				return path3 + filename;			
			
			System.err.println("Can't find recognizer!!");
			return filename;
		}

		@Override
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			//Key is the path to recognizer
			//Value is the image string
			
			//Load image and get grayImage
			Image2String c = new Image2String();			
			IplImage img = c.string2Image(value.toString());
			IplImage grayImage = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
	        cvCvtColor(img, grayImage, CV_BGR2GRAY);
	        
	        //Load recognizer
	        FaceRecognizer faceRecognizer = createEigenFaceRecognizer();		
			faceRecognizer.load(findValidPath(key.toString()));
			
			//Predict
			int[] pred = new int[1];
			double[] confidence = new double[1];
            faceRecognizer.predict(grayImage, pred, confidence);			
			
	        //Use the same key
            context.write(new Text("KEY"), new Text("" + pred[0] + "," + confidence[0]));
		}
	}	
	
	public static class FaceRecognitionReducer extends Reducer<Text, Text, Text, Text> {
		
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
		{
			int min_pred = -1;
			double min_confidence = 1e99;
			
			for (Text value : values)
			{
				System.out.println(value.toString());
				StringTokenizer t = new StringTokenizer(value.toString(), ",");
				int pred = Integer.parseInt(t.nextToken());
				double confidence = Double.parseDouble(t.nextToken());
				if (confidence < min_confidence)
				{
					min_confidence = confidence;
					min_pred = pred;
				}
			}
			
			try {
				context.write(new Text("KEY"), new Text("" + min_pred + "," + min_confidence));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	

	public static void main(String[] args) throws Exception {		
		long t = cvGetTickCount();		
		int res = ToolRunner.run(new Configuration(), new DistFaceRecognition(), args);
		t = cvGetTickCount() - t;
		System.out.println("Distributed Face Recognition Run Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
		
		System.exit(res);
	}	
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "facedetection");
		job.setJarByClass(DistFaceRecognition.class);
		
		job.setMapperClass(FaceRecognitionMapper.class);
		job.setCombinerClass(FaceRecognitionReducer.class);
		job.setReducerClass(FaceRecognitionReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		
		return job.waitForCompletion(true) ? 0 : 1;		
	}
}
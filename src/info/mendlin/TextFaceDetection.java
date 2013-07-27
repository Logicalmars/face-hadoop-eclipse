package info.mendlin;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickCount;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickFrequency;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class TextFaceDetection extends Configured implements Tool{

	public static class FaceDetectionMapper extends Mapper<Text, Text, Text, Text> {
		
		CvHaarClassifierCascade classifier = null;
		CvMemStorage storage = null;
		
		@Override
		protected void setup(Context context) {			
			storage = CvMemStorage.create();
			
			// Make sure the cascade is loaded
			try {
				if (classifier == null || classifier.isNull()) {
					classifier = new CvHaarClassifierCascade(cvLoad("/home/hadoop/facedata/haarcascade_frontalface_alt.xml"));
					if (classifier != null && classifier.isNull() == false)
						System.out.println("Getting classifier in 6");
				}
			} catch (Exception e) {				
				System.err.println("Hadoop classifier path not found: " + e.getMessage());
			}
			
			try {
				if (classifier == null || classifier.isNull()) {
					URL uri = getClass().getResource("haarcascade_frontalface_alt.xml");
					if (uri != null)
						classifier = new CvHaarClassifierCascade(cvLoad(uri.getPath()));
					
					if (classifier != null && classifier.isNull() == false)
						System.out.println("Getting classifier in 2");
				}
				
				if (classifier == null || classifier.isNull()) {
					System.err.println("Error loading classifier file");
				}
			} catch (Exception e) {				
				System.err.println("Classifier not found: " + e.getMessage());
			}
		}

		@Override
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			
			Image2String c = new Image2String();
			
			IplImage img = c.string2Image(value.toString());
			
			// setup gray image
			IplImage grayImage = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
	        cvCvtColor(img, grayImage, CV_BGR2GRAY);
	 
	        // We detect the faces.	        
	        CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.5, 3, 0);
	 
	        // We iterate over the discovered faces and draw red rectangles
	        // around them.
	        for (int i = 0; i < faces.total(); i++) {
	            CvRect r = new CvRect(cvGetSeqElem(faces, i));
	            cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.RED, 5, CV_AA, 0);
	        }
			
	        context.write(key, new Text(c.image2String(img)));
		}
	}	

	public static void main(String[] args) throws Exception {		
		long t = cvGetTickCount();		
		int res = ToolRunner.run(new Configuration(), new TextFaceDetection(), args);
		t = cvGetTickCount() - t;
		System.out.println("Text Face Detection Run Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
		
		System.exit(res);
	}	
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "facedetection");
		job.setJarByClass(TextFaceDetection.class);
		
		job.setMapperClass(FaceDetectionMapper.class);				
		job.setNumReduceTasks(0);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		
		return job.waitForCompletion(true) ? 0 : 1;		
	}
}
package info.mendlin;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class LocalFaceDetection {
	
	CvMemStorage storage = CvMemStorage.create();
	CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad("src/info/mendlin/haarcascade_frontalface_alt.xml"));
	
	public void detect(String image_path)
	{		
		System.out.println("Running face detection...");
		
        IplImage originalImage = cvLoadImage(image_path, 1);        
        System.out.println(originalImage);
        IplImage grayImage = IplImage.create(originalImage.width(), originalImage.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);        
        
        CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.5, 3, 0);
 
        // We iterate over the discovered faces and draw yellow rectangles
        // around them.
        for (int i = 0; i < faces.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            cvRectangle(originalImage, cvPoint(r.x(), r.y()), cvPoint(r.x() + r.width(), r.y() + r.height()),
                    CvScalar.RED, 5, CV_AA, 0);
        }
 
        // Save the image to a new file.
        cvSaveImage("00001_face.jpg", originalImage);
	}
	
	public static void main(String[] args) 
	{
		long t = cvGetTickCount();		
		int count = 0;
		for (File file : new File("/home/linmengl/workspace/data/Meng").listFiles())
		{
			if (file.getName().endsWith(".ppm"))
			{
				new LocalFaceDetection().detect(file.getPath());
			}
			count ++;
			if (count == 100)
				break;
		}				
		t = cvGetTickCount() - t;
		System.out.println("Loca Face Detection Run Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
	}
}


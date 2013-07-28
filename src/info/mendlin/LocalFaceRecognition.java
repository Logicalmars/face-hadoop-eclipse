package info.mendlin;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_contrib.*;
import java.io.File;
import java.io.FilenameFilter;

//Local test is done on "att_faces", The ORL face database
public class LocalFaceRecognition {
	
	public static void train(int personStart, int personStop)
	{		
		int numImages = (personStop - personStart) * 9;
		
		MatVector images = new MatVector(numImages);
        int[] labels = new int[numImages];
        int counter = 0;
        
		for (int personid = personStart; personid < personStop;personid++)
		{
			for (int picid = 1; picid < 10; picid++)
			{
				String path = "att_faces/s" + personid + "/" + picid + ".pgm";
				IplImage img = cvLoadImage(path);
	            IplImage grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
	            cvCvtColor(img, grayImg, CV_BGR2GRAY);
	            
	            images.put(counter, grayImg);
	            labels[counter] = personid;
	            counter++;
			}
		}
		
		FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
		faceRecognizer.train(images, labels);
		faceRecognizer.save(String.format("att_faces/%d_to_%d_trainning.xml", personStart, personStop));
	}
	
	public static void test(int personStart, int personStop)
	{
		FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
		faceRecognizer.load(String.format("att_faces/%d_to_%d_trainning.xml", personStart, personStop));
		
		for (int personid = personStart; personid < personStop;personid++)
		{
			String path = "att_faces/s" + personid + "/10.pgm";			
			IplImage img = cvLoadImage(path);
            IplImage grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            
			int[] pred = new int[1];
			double[] confidence = new double[1];
            faceRecognizer.predict(grayImg, pred, confidence);
			System.out.println("" + pred[0] + ", " + confidence[0]);
		}
	}
	
    public static void main(String[] args) {
//        train(1, 10);
    	test(1, 10);
    }
}
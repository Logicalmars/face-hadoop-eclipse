package info.mendlin;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class LocalFaceDetection {
	
	public void detect()
	{
		System.out.println("Running face detection...");
		
//		String image_path = getClass().getResource("00001_930831_fa_a.ppm").getPath(); 
//		String classifier_path = getClass().getResource("haarcascade_frontalface_alt.xml").getPath();
		String image_path = "src/info/mendlin/00001_930831_fa_a.ppm";
		
		String resourceName = "info/mendlin/haarcascade_frontalface_alt.xml";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String classifier_path = classLoader.getResource(resourceName).getPath();
		
//		String classifier_path = "src/info/mendlin/haarcascade_frontalface_alt.xml";
		
		// Load the original image.
        IplImage originalImage = cvLoadImage(image_path, 1);
        
        System.out.println(originalImage);
 
        // We need a grayscale image in order to do the recognition, so we
        // create a new image of the same size as the original one.
        IplImage grayImage = IplImage.create(originalImage.width(),
                originalImage.height(), IPL_DEPTH_8U, 1);
 
        // We convert the original image to grayscale.
        cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);
 
        CvMemStorage storage = CvMemStorage.create();
 
        // We instantiate a classifier cascade to be used for detection, using
        // the cascade definition.
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifier_path));
 
        // We detect the faces.
        CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.5, 3, 0);
 
        // We iterate over the discovered faces and draw yellow rectangles
        // around them.
        for (int i = 0; i < faces.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            cvRectangle(originalImage, cvPoint(r.x(), r.y()),
                    cvPoint(r.x() + r.width(), r.y() + r.height()),
                    CvScalar.RED, 5, CV_AA, 0);
        }
 
        // Save the image to a new file.
        cvSaveImage("00001_face.jpg", originalImage);
	}
	
	public static void main(String[] args) 
	{
		new LocalFaceDetection().detect();		
	}
}


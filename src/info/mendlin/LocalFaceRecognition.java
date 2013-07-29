package info.mendlin;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_contrib.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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
		
		for (int personid = personStart; personid < personStop; personid++)
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
	
	public static void genHadoopInput(int test_personid)
	{
		try {
			String path = "att_faces/s" + test_personid + "/10.pgm";
			IplImage img = cvLoadImage(path);
			String imgstr = new Image2String().image2String(img);
			
			PrintWriter out = new PrintWriter("att_faces/collection.txt");
			
			for (File file : new File("att_faces").listFiles())
			{
				if (file.getName().endsWith("_trainning.xml"))
				{
					out.println(file.getName() + "\t" + imgstr);					
				}
			}
			
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) {
//    	train(1, 11);
//    	train(11, 21);
//    	train(21, 31);
//    	train(31, 41);
//    	test(21, 31);
    	genHadoopInput(17);
    }
}
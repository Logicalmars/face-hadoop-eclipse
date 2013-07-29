package info.mendlin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import org.apache.commons.codec.binary.Base64;

public class Image2String {	
	
	public void show(IplImage image)
	{
		show(image, "My Image");
	}
	
	public void show(IplImage image, String title)
	{
		final CanvasFrame canvas = new CanvasFrame(title, 1);
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas.showImage(image);
	}
	
	public void show_collection(String filename, int take) throws IOException
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			for (int i = 0;i<take;i++)
			{				
				StringTokenizer line = new StringTokenizer(reader.readLine(), "\t");				
				String key = line.nextToken();
				String value = line.nextToken();				
				show(string2Image(value), key);
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String image2String(IplImage image) throws IOException
	{	
		System.out.println("Image2String: " + image);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		DataOutputStream out = new DataOutputStream(stream);		
		out.writeInt(image.height());
		out.writeInt(image.width());
		out.writeInt(image.depth());
		out.writeInt(image.nChannels());
		out.writeInt(image.imageSize());		
		
		// Write image bytes
		ByteBuffer buffer = image.getByteBuffer();		
		byte[] b = new byte[buffer.remaining()];
		buffer.get(b);
		out.write(b);		
		out.close();				
		
		byte[] encodedBytes = Base64.encodeBase64(stream.toByteArray());		
		return new String(encodedBytes);		
	}
	
	public IplImage string2Image(String s) throws IOException
	{	
		ByteArrayInputStream stream = new ByteArrayInputStream(Base64.decodeBase64(s.getBytes()));
		
		DataInputStream in = new DataInputStream(stream);
		// Read image information
		int height = in.readInt();
		int width = in.readInt();
		int depth = in.readInt();
		int nChannels = in.readInt();
		int imageSize = in.readInt();
		// Read image bytes
		byte[] bytes = new byte[imageSize];		
		in.read(bytes, 0, imageSize);

		IplImage img = cvCreateImage(cvSize(width, height), depth, nChannels);		
		img.imageData(new BytePointer(bytes));
		
		System.out.println("String2Image: " + img);		
		return img;
	}
	
	public static void encodingTest()
	{
		final IplImage img = cvLoadImage("src/info/mendlin/00001_930831_fa_a.ppm");
		
		try {			
			Image2String convertor = new Image2String();
			String temp = convertor.image2String(img);
			convertor.show(convertor.string2Image(temp));			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public static void encodingFolder(String folder) throws IOException
	{
		PrintWriter out = new PrintWriter(folder + "/collection.txt");
		
		File actual = new File(folder);
		int count = 0;
		for (File f : actual.listFiles())
		{
			if (f.getName().endsWith("ppm"))
			{
				System.out.println("converting: " + f.getName());
				final IplImage img = cvLoadImage(f.getPath());
				if (img != null)
				{					
					out.println(f.getName() + "\t" + new Image2String().image2String(img));
					count ++;
				}
				else
					System.out.println("fail");
			}
			
			if (count == 100)
				break;
		}
	}
	
	public static void main(String[] args) throws IOException {
		encodingFolder("/home/linmengl/workspace/data/Meng");
//		encodingTest();
//		new Image2String().show_collection("/home/linmengl/workspace/data/MengSmall/part-m-00000", 5);
	}
}

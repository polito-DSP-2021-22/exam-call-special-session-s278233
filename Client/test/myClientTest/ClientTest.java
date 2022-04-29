package myClientTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import myClient.ConversionRequest;
import myClientTest.ExitDeniedSecurityManager.ExitSecurityException;
import myServer.Converter;

class ClientTest {
	private ConversionRequest test;
	private static Thread thread;
	private static File outputDir = new File(".\\ConvertedImages\\");
	
	public static File findFileForId(final String filePrefix, final String extension) {
	    return outputDir.listFiles(new FileFilter() {
	        public boolean accept(File pathname) {
	            return pathname.getName().startsWith(filePrefix) && pathname.getName().endsWith(extension);    
	            		}
	    })[0];
	}
	
	public static boolean checkImageFormat(File image, String format) {
		ImageInputStream iis = null;
		if(format.equals("jpg")) format = "jpeg";
		try {
			iis = ImageIO.createImageInputStream(image);
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

			while (imageReaders.hasNext()) {
			    ImageReader reader = (ImageReader) imageReaders.next();
			    if(reader.getFormatName().equalsIgnoreCase(format)) return true;
			    else return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	};
	
	private static void emptyOutputDir() {
		for(File f: outputDir.listFiles()) 
			  f.delete(); 
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		   thread = new Thread("Server Thread") {
			      public void run(){
			  		Converter.main(null);
			      }
			   };
			   
			   thread.start();
				System.gc();
				emptyOutputDir();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Converter.stop();
		System.gc();
		emptyOutputDir();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	final void testWrongArguments() {
        System.setSecurityManager(new ExitDeniedSecurityManager());
		String args11[] = { "jpg", "perù.png" };
		String args12[] = { "jpg", "png", "perù.jpg", "perù.png" };
		try {
			ConversionRequest.main(args11);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(1, status);
		}
		try {
			ConversionRequest.main(args12);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(1, status);
		}

		String args21[] = { "jp", "png", "perù.jpg" };
		String args22[] = { "perù.jpg", "png", "perù.jpg" };
		String args23[] = { "jpg", "pn", "perù.jpg" };
		String args24[] = { "jpg", "perù.png", "perù.jpg" };
		try {
			ConversionRequest.main(args21);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(2, status);
		}
		try {
			ConversionRequest.main(args22);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(2, status);
		}
		try {
			ConversionRequest.main(args23);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(2, status);
		}
		try {
			ConversionRequest.main(args24);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(2, status);
		}

		String args31[] = { "jpg", "tiff", "perù.jpg" };
		String args32[] = { "bmp", "png", "perù.jpg" };
		try {
			ConversionRequest.main(args31);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(3, status);
		}
		try {
			ConversionRequest.main(args32);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(3, status);
		}

		String args41[] = { "jpg", "png", "" };
		String args42[] = { "jpg", "png",
				"C://Users/Utente/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/perù.jpg" };
		try {
			ConversionRequest.main(args41);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(4, status);
		}
		try {
			ConversionRequest.main(args42);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(4, status);
		}

		String args51[] = { "jpg", "png", "../../perù.jpg" };
		try {
			ConversionRequest.main(args51);
			fail("Expected exit");
		} catch (ExitSecurityException e) {
			int status = e.getStatus();
			assertEquals(5, status);
		}
	}
	
	@Test
	final void testConnection() {
		try {
			test = new ConversionRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@Test
	final void testMultipleConnections() {
		int clients = 50;
		int i;
		Thread[] clientArray = new Thread[clients];
		for(i=0;i<clients;i++) {
			clientArray[i] = new Thread("Client Thread " + i) {
			      public void run(){
			    	  try {
						new ConversionRequest();
					} catch (IOException e) {
						e.printStackTrace();
					}
				      }
				   };
				   clientArray[i].start();
		}
		
		try {
			Thread.sleep(40 * 100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(i=0;i<clients;i++) clientArray[i].interrupt();
		assertTrue(true);
	}

	@Test
	final void testReadFile() {
		try {
			test = new ConversionRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File src = new File(".\\ToConvertImages\\", "perrù.jpg");
		assertThrows(Exception.class, () -> test.readFile(src));
		
		File src2 = new File(".\\ToConvertImages\\", "perù.jpg");
		byte[] bytes = null;
		try {
			bytes = test.readFile(src2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assertions.assertNotEquals(new byte[0], bytes);
		Assertions.assertNotNull(bytes);
	}
	
	@Test
	final void testWriteFile() {
		try {
			test = new ConversionRequest();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File src = new File(".\\ToConvertImages\\", "perù.jpg");
		File dst = new File(".\\ConvertedImages\\", "perù.jpg");

		assertThrows(Exception.class, () -> test.writeFile(null, null));

		assertThrows(Exception.class, () -> test.writeFile(null, dst));

		byte[] bytes3;
		try {
			bytes3 = Files.readAllBytes(src.toPath());
			test.writeFile(bytes3, dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
		
		assertTrue(dst.isFile());
		System.gc();
		dst.delete();
	}

	@Test
	final void testSendWrongData() {
		try {
			test = new ConversionRequest();
			assertThrows(Exception.class, () -> test.sendData(null, "jpg", "png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	final void testMain() {
		String args1[] = { "jpg", "png", "perù.jpg" };
		String args2[] = { "png", "jpg", "tux.png" };
		String args3[] = { "jpg", "gif", "perù.jpg" };
		String args4[] = { "gif", "jpg", "micheal.gif" };
		String args5[] = { "gif", "png", "micheal.gif" };
		String args6[] = { "png", "gif", "tux.png" };
				
		ConversionRequest.main(args1);
		File output1 = findFileForId("perù", "png");
		assertTrue(checkImageFormat(output1, "png"));
		
		ConversionRequest.main(args2);
		File output2 = findFileForId("tux", "jpg");
		assertTrue(checkImageFormat(output2, "jpg"));
		
		ConversionRequest.main(args3);
		File output3 = findFileForId("perù", "gif");
		assertTrue(checkImageFormat(output3, "gif"));
		
		ConversionRequest.main(args4);
		File output4 = findFileForId("micheal", "jpg");
		assertTrue(checkImageFormat(output4, "jpg"));
		
		ConversionRequest.main(args5);
		File output5 = findFileForId("micheal", "png");
		assertTrue(checkImageFormat(output5, "png"));
		
		ConversionRequest.main(args6);
		File output6 = findFileForId("tux", "gif");
		assertTrue(checkImageFormat(output6, "gif"));
	}
	
	@Test
	final void testBigFile() {
		String args[] = { "jpg", "png", "big.jpg" };
				
		ConversionRequest.main(args);
		File output = findFileForId("big", "png");
		assertTrue(checkImageFormat(output, "png"));
		System.gc();
		output.delete();
	}
	
	@Test
	final void testMultipleMain() {
		String args[] = { "jpg", "png", "perù.jpg"};
		int clients = 50;
		int i;
		Thread[] clientArray = new Thread[clients];
		
		int filesInfolder = outputDir.listFiles().length;
				
		for(i=0;i<clients;i++) {
			clientArray[i] = new Thread("Client Thread " + i) {
			      public void run(){
			    	  ConversionRequest.main(args);
				      }
				   };
				   clientArray[i].start();
		}
		
		for(i=0;i<clients;i++)
			try {
				clientArray[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue((outputDir.listFiles().length - filesInfolder) == clients);
	}

}

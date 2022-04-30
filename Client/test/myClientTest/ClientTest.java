package myClientTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
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
	private static String inputDirName = "." + File.separator + "ToConvertImages" + File.separator;
	private static String outputDirName = "." + File.separator + "ConvertedImages" + File.separator;
	private static File outputDir = new File(outputDirName);

	public static File findFileForId(final String filePrefix, final String extension) {
		File[] listFiles = outputDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(filePrefix) && pathname.getName().endsWith(extension);
			}
		});
		if (listFiles != null && listFiles.length != 0)
			return listFiles[0];
		else
			return null;
	}

	public static boolean checkImageFormat(File image, String format) {
		ImageInputStream iis = null;
		if (format.equals("jpg"))
			format = "jpeg";
		try {
			iis = ImageIO.createImageInputStream(image);
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

			while (imageReaders.hasNext()) {
				ImageReader reader = (ImageReader) imageReaders.next();
				if (reader.getFormatName().equalsIgnoreCase(format))
					return true;
				else
					return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	};

	private static void emptyOutputDir() {
		for (File f : outputDir.listFiles())
			f.delete();
	}

	class ClientThread extends Thread {
		String[] args;

		ClientThread(String[] args) {
			this.args = args;
		}

		public void run() {
			ConversionRequest.main(args);
		}
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		thread = new Thread("Server Thread") {
			public void run() {
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
				"C://Users/Utente/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/Cartella1/Cartella2/per�.jpg" };
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

		String args51[] = { "jpg", "png", ".." + File.separator + ".." + File.separator + "perù.jpg" };
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
		int i, clients = 50;
		try {
			for (i = 0; i < clients; i++)
				new ConversionRequest();
			Thread.sleep(40 * 100);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(true);
	}

	@Test
	final void testReadFile() {
		try {
			test = new ConversionRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File src = new File(inputDirName, "perrù.jpg");
		assertThrows(Exception.class, () -> test.readFile(src));

		File src2 = new File(inputDirName, "perù.jpg");
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
		File src = new File(inputDirName, "perù.jpg");
		File dst = new File(outputDirName, "perù.jpg");

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
		int clients = 6, i;
		String[][] args = { { "jpg", "png", "perù.jpg" }, { "png", "jpg", "tux.png" }, { "jpg", "gif", "perù.jpg" },
				{ "gif", "jpg", "micheal.gif" }, { "gif", "png", "micheal.gif" }, { "png", "gif", "tux.png" } };
		String[][] parameters = { { "perù", "png" }, { "tux", "jpg" }, { "perù", "gif" }, { "micheal", "jpg" },
				{ "micheal", "png" }, { "tux", "gif" } };
		File outputs[] = new File[clients];
		for (i = 0; i < clients; i++) {
			ConversionRequest.main(args[i]);
			outputs[i] = findFileForId(parameters[i][0], parameters[i][1]);
			if (outputs[i] == null)
				fail();
			assertTrue(checkImageFormat(outputs[i], parameters[i][1]));
		}
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
		String args[] = { "jpg", "png", "perù.jpg" };
		int i, clients = 50;

		int filesInfolder = outputDir.listFiles().length;

		for (i = 0; i < clients; i++)
			ConversionRequest.main(args);

		assertTrue((outputDir.listFiles().length - filesInfolder) == clients);
	}

}

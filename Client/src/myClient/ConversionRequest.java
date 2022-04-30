package myClient;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConversionRequest {
	private final static String remoteHostname = null;
	private final static int remotePort = 2001;
	private final static String inFolder = ".\\ToConvertImages\\";
	private final static String outFolder = ".\\ConvertedImages\\";
	private final static String supportedTypes[] = { "gif", "jpg", "png" };
	private final static int blockSize = 65535;
	private final static int TIMEOUT = 30 * 1000;
	private DataOutputStream outputSocketStream = null;
	private DataInputStream inputSocketStream = null;
	private Socket socket;
	private String error;
	private String success;
	private String srcFilename;
	private String dstFilename;

	public ConversionRequest() throws IOException {
		success = "Connected";
		this.socket = new Socket(InetAddress.getByName(remoteHostname), remotePort);
		inputSocketStream = new DataInputStream(socket.getInputStream());
		outputSocketStream = new DataOutputStream(socket.getOutputStream());
		socket.setSoTimeout(TIMEOUT);
	}

	public byte[] readFile(File srcImage) throws IOException {
		error = "File reading error!";
		success = String.format("Read %s", srcFilename);
		return Files.readAllBytes(srcImage.toPath());
	}

	public void sendData(byte[] fileBuffer, String srcType, String dstType) throws IOException {
		error = "Connection error!";
		success = String.format("Sent file [%s]", srcFilename);
		int i, index, size;
		int length = fileBuffer.length;
		i = index = size = 0;

		outputSocketStream.write(srcType.getBytes(), 0, 3);
		outputSocketStream.write(dstType.getBytes(), 0, 3);

		while ((index = i * blockSize) <= length) {
			size = Math.min(length - index, blockSize);
			outputSocketStream.writeShort(size);
			outputSocketStream.write(fileBuffer, index, size);
			i++;
		}

		if (size == blockSize)
			outputSocketStream.writeInt(0);

		outputSocketStream.flush();
	}

	public byte[] receiveData() throws IOException {
		error = "Connection error!";
		success = "Received file converted";
		byte[] block = new byte[blockSize];
		int bytesRead, payloadLength;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		char controlChar = (char) inputSocketStream.read();

		do {
			payloadLength = inputSocketStream.readShort();
			payloadLength &= 0xFFFF;
			bytesRead = inputSocketStream.read(block, 0, payloadLength);
			if (bytesRead == -1)
				throw new IOException();
			else
				baos.write(block, 0, payloadLength);
		} while (payloadLength == blockSize);

		switch (controlChar) {
		case '0':
			break;
		case '1':
			error = String.format("Wrong request: [%s]", baos.toString());
			throw new IOException();
		case '2':
			error = String.format("Internal server error: [%s]", baos.toString());
			throw new IOException();
		default:
			error = "Unrecognized control character!";
			throw new IOException();
		}
		;
		return baos.toByteArray();
	}

	public void writeFile(byte[] fileBuffer, File dstImage) throws IOException {
		error = "File writing error!";
		success = String.format("Wrote %s", dstFilename);
		OutputStream os = new FileOutputStream(dstImage);
		os.write(fileBuffer);
		os.close();
	}

	public static void main(String[] args) {
		
		Logger logger = Logger.getLogger("JARVIS");
		
		if (args.length < 3 || args.length > 3) {
			logger.log(Level.WARNING, "Wrong arguments! [USAGE: <srcType> <dstType> <imgPath>]");
			System.exit(1);
		}
		if (args[0].length() < 3 || args[0].length() > 5 || args[1].length() < 3 || args[1].length() > 5) {
			logger.log(Level.WARNING, "Wrong types! [SUPPORTED TYPES: gif jpg png]");
			System.exit(2);
		}
		if (!Arrays.stream(supportedTypes).anyMatch(args[0]::equals)
				|| !Arrays.stream(supportedTypes).anyMatch(args[1]::equals)) {
			logger.log(Level.WARNING, "Wrong types! [SUPPORTED TYPES: gif jpg png]");
			System.exit(3);
		}
		if (args[2].length() < 1 || args[2].length() > 4096) {
			logger.log(Level.WARNING, "Wrong path! [INSERT VALID PATH]");
			System.exit(4);
		}
		File srcPath;
//		if(!args[2].endsWith(args[0])) srcPath = new File(inFolder, args[2].concat(".").concat(args[0]));
//		else 
			srcPath = new File(inFolder, args[2]);
		try {
			if (!srcPath.getCanonicalFile().toPath().normalize().getParent().endsWith(Path.of(inFolder).normalize())) {
				logger.log(Level.WARNING, "Wrong path! [INSERT VALID PATH]");
				System.exit(5);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Wrong path!", e);
		}

		String srcType = args[0];
		String dstType = args[1];
		ConversionRequest client = null;
		byte[] toSendBuffer = null;
		byte[] receivedBuffer = null;

		try {
			logger.log(Level.INFO, "Connecting to remote server...");
			client = new ConversionRequest();
			logger.log(Level.INFO, client.success);
			client.srcFilename = srcPath.getName();
			File dstPath = new File(outFolder, client.srcFilename.split("\\.(?=[^\\.]+$)")[0].concat(String.format("%.6f",Math.random()*1000000)).concat(".").concat(dstType));
			client.dstFilename = dstPath.getName();
			toSendBuffer = client.readFile(srcPath);
			logger.log(Level.INFO, client.success);
			client.sendData(toSendBuffer, srcType, dstType);
			logger.log(Level.INFO, client.success);
			receivedBuffer = client.receiveData();
			logger.log(Level.INFO, client.success);
			client.writeFile(receivedBuffer, dstPath);
			logger.log(Level.INFO, client.success);
		} catch (Exception e) {
			if(client !=null) {
				logger.log(Level.SEVERE, client.error, e);
					System.exit(6);
				}else {
			logger.log(Level.SEVERE, "Connection error!", e);
			System.exit(7);
			}
		}
	}
}
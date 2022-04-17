package myClient;

import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.io.*;

public class ConversionRequest {
	private final static String remoteHostname = null;
	private final static int remotePort = 2001;
	private final static String inFolder = ".\\ToConvertImages\\";
	private final static String outFolder = ".\\ConvertedImages\\";
	private final static String supportedTypes[] = { "gif", "jpg", "png" };
	private final static int blockSize = 65535;
	private DataOutputStream outputSocketStream = null;
	private DataInputStream inputSocketStream = null;
	private static final int TIMEOUT = 30 * 1000;
	private Socket socket;
	private static String error;

	private ConversionRequest() throws IOException {
		ConversionRequest.error = "Connection error!";
		this.socket = new Socket(InetAddress.getByName(remoteHostname), remotePort);
		inputSocketStream = new DataInputStream(socket.getInputStream());
		outputSocketStream = new DataOutputStream(socket.getOutputStream());
		socket.setSoTimeout(TIMEOUT);
	}

	private byte[] readFile(File srcImage) throws IOException {
		ConversionRequest.error = "File reading error!";
		return Files.readAllBytes(srcImage.toPath());
	}

	private void sendData(byte[] fileBuffer, String srcType, String dstType) throws IOException {
		ConversionRequest.error = "Connection error!";
		int i, index, size;
		int length = fileBuffer.length;
		i = index = size = 0;

		outputSocketStream.write(srcType.getBytes(), 0, 3);
		outputSocketStream.write(dstType.getBytes(), 0, 3);

		while ((index = i * blockSize) <= length) {
			size = Math.min(length - index, blockSize);
			outputSocketStream.writeInt(size);
			outputSocketStream.write(fileBuffer, index, size);
			i++;
		}

		if (size == blockSize)
			outputSocketStream.writeInt(0);

		outputSocketStream.flush();
		socket.shutdownOutput();
	}

	private byte[] receiveData() throws IOException {
		ConversionRequest.error = "Connection error!";
		byte[] block = new byte[blockSize];
		int bytesRead, payloadLength;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		char controlChar = (char) inputSocketStream.read();

		do {
			payloadLength = inputSocketStream.readInt();
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
			ConversionRequest.error = String.format("Wrong request: [%s]", baos.toString());
			throw new IOException();
		case '2':
			ConversionRequest.error = String.format("Internal server error: [%s]", baos.toString());
			throw new IOException();
		default:
			ConversionRequest.error = "Unrecognized control character!";
			throw new IOException();
		}
		;
		socket.close();
		return baos.toByteArray();
	}

	private void writeFile(byte[] fileBuffer, File dstImage) throws IOException {
		ConversionRequest.error = "File writing error!";
		OutputStream os = new FileOutputStream(dstImage);
		os.write(fileBuffer);
		os.close();
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Wrong arguments! [USAGE: <srcType> <dstType> <imgPath>]");
			System.exit(1);
		}
		if (args[0].length() < 3 || args[0].length() > 5 || args[1].length() < 3 || args[1].length() > 5) {
			System.err.println("Wrong types! [SUPPORTED TYPES: gif jpg png]");
			System.exit(1);
		}
		if (!Arrays.stream(supportedTypes).anyMatch(args[0]::equals)
				|| !Arrays.stream(supportedTypes).anyMatch(args[0]::equals)) {
			System.err.println("Wrong types! [SUPPORTED TYPES: gif jpg png]");
			System.exit(1);
		}
		if (args[2].length() < 1 || args[2].length() > 4096) {
			System.err.println("Wrong path! [INSERT VALID PATH]");
			System.exit(1);
		}
		File srcPath = new File(inFolder, args[2]);
		if (!srcPath.getPath().startsWith(inFolder)) {
			System.err.println("Wrong path! [INSERT VALID PATH]");
			System.exit(1);
		}

		String srcType = args[0];
		String dstType = args[1];
		String srcFilename = srcPath.getName();
		File dstPath = new File(outFolder, srcFilename.split("\\.(?=[^\\.]+$)")[0].concat(".").concat(dstType));
		String dstFilename = dstPath.getName();
		ConversionRequest client = null;
		byte[] toSendBuffer = null;
		byte[] receivedBuffer = null;

		try {
			System.out.println("Connecting to remote server...");
			client = new ConversionRequest();
			System.out.println(String.format("Reading %s...", srcFilename));
			toSendBuffer = client.readFile(srcPath);
			System.out.println(String.format("Sending file [%s]...", srcFilename));
			client.sendData(toSendBuffer, srcType, dstType);
			System.out.println(String.format("Waiting for response..."));
			receivedBuffer = client.receiveData();
			System.out.println(String.format("Writing %s...", dstFilename));
			client.writeFile(receivedBuffer, dstPath);
			System.out.println("Conversion done.");
		} catch (Exception e) {
			System.err.println(ConversionRequest.error);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
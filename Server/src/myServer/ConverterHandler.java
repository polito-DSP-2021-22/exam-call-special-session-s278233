package myServer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class ConverterHandler implements Runnable {

	private final static String supportedTypes[] = { "gif", "jpg", "png" };
	private final static int blockSize = 65535;
	private DataOutputStream outputSocketStream = null;
	private DataInputStream inputSocketStream = null;
	private static final int TIMEOUT = 30 * 1000;
	private Socket socket;
	private String srcType, dstType;
	private String error;
	private char errorChar;

	public ConverterHandler(Socket socket) throws IOException {
		this.socket = socket;
		inputSocketStream = new DataInputStream(socket.getInputStream());
		outputSocketStream = new DataOutputStream(socket.getOutputStream());
		socket.setSoTimeout(TIMEOUT);
	}

	private void receiveHeader() throws IOException {
		this.error = "Header error!";
		this.errorChar = '1';
		byte[] type = new byte[3];
		inputSocketStream.read(type, 0, 3);
		srcType = new String(type);
		inputSocketStream.read(type, 0, 3);
		dstType = new String(type);
		if (!Arrays.stream(supportedTypes).anyMatch(srcType::equals)
				|| !Arrays.stream(supportedTypes).anyMatch(dstType::equals))
			throw new IOException("Unsupported format");
	}

	private byte[] receiveFile() throws IOException {
		this.error = "Payload error!";
		this.errorChar = '1';
		byte[] block = new byte[blockSize];
		int bytesRead, payloadLength;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		do {
			payloadLength = inputSocketStream.readInt();
			bytesRead = inputSocketStream.read(block, 0, payloadLength);
			if (bytesRead == -1)
				throw new IOException("Read error!");
			else
				baos.write(block, 0, payloadLength);
		} while (payloadLength == blockSize);

		socket.shutdownInput();
		return baos.toByteArray();
	}

	private byte[] convertFile(byte[] fileBuffer) throws IOException {
		this.error = "Conversion error!";
		this.errorChar = '2';
		ByteArrayInputStream bais = new ByteArrayInputStream(fileBuffer);
		BufferedImage bi = ImageIO.read(bais);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, dstType, baos);
		return baos.toByteArray();
	}

	private void sendData(byte[] fileBuffer, char controlChar) throws IOException {
		this.error = "Response processing error!";
		this.errorChar = '2';
		int i, index, size;
		int length = fileBuffer.length;
		i = index = size = 0;

		outputSocketStream.write(controlChar);

		while ((index = i * blockSize) <= length) {
			size = Math.min(length - index, blockSize);
			outputSocketStream.writeInt(size);
			outputSocketStream.write(fileBuffer, index, size);
			i++;
		}

		if (size == blockSize)
			outputSocketStream.writeInt(0);
		outputSocketStream.flush();
		socket.close();
	}

	@Override
	public void run() {
		byte[] toSendBuffer = null;
		byte[] receivedBuffer = null;

		try {

			try {
				System.out.println("Waiting for header...");
				receiveHeader();
				System.out.println("Waiting for file...");
				receivedBuffer = receiveFile();
				System.out.println("Converting file...");
				toSendBuffer = convertFile(receivedBuffer);
				System.out.println("Sending response...");
				sendData(toSendBuffer, '0');
				System.out.println("Done.");
			} catch (IOException e) {
				System.err.println(error);
				e.printStackTrace();
				sendData(error.getBytes(), errorChar);
			}

		} catch (IOException e) {
			System.err.println("Fatal connection error!");
			e.printStackTrace();
		}
	}
}

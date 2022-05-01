package myServer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ConverterHandler implements Runnable {

	private final static String supportedTypes[] = { "gif", "jpg", "jpeg", "png" };
	private final static int blockSize = 65535;
	private final static int TIMEOUT = 30 * 1000;
	private DataOutputStream outputSocketStream = null;
	private DataInputStream inputSocketStream = null;
	private Socket socket;
	private String srcType, dstType;
	private String error;
	private String success;
	private char errorChar;
	private String loggerName;

	public ConverterHandler(Socket socket) throws IOException {
		this.socket = socket;
		inputSocketStream = new DataInputStream(socket.getInputStream());
		outputSocketStream = new DataOutputStream(socket.getOutputStream());
		socket.setSoTimeout(TIMEOUT);
		loggerName = String.format("[%s] ", socket.getRemoteSocketAddress());
	}

	private void receiveHeader() throws IOException {
		error = "Header error!";
		success = "Received header";
		errorChar = '1';
		byte[] type = new byte[3];
		inputSocketStream.read(type, 0, 3);
		srcType = new String(type);
		inputSocketStream.read(type, 0, 3);
		dstType = new String(type);
		if (!Arrays.stream(supportedTypes).anyMatch(srcType::equals)
				|| !Arrays.stream(supportedTypes).anyMatch(dstType::equals))
			throw new IOException("Unsupported format!");
	}

	private byte[] receiveFile() throws IOException {
		error = "Payload error!";
		success = "Received file";
		errorChar = '1';
		byte[] block = new byte[blockSize];
		int bytesRead, bytesReadTotal, payloadLength;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		do {
			payloadLength = inputSocketStream.readShort();
			payloadLength &= 0xFFFF;
			bytesReadTotal = 0;
			while (bytesReadTotal < payloadLength) {
				bytesRead = inputSocketStream.read(block, 0, payloadLength - bytesReadTotal);
				if (bytesRead == -1)
					throw new IOException("Read request error!");
				baos.write(block, 0, bytesRead);
				bytesReadTotal += bytesRead;
			}
		} while (payloadLength == blockSize);

		socket.shutdownInput();
		return baos.toByteArray();
	}

	private byte[] convertFile(byte[] fileBuffer) throws IOException {
		error = "Conversion error!";
		success = "Converted file";
		errorChar = '1';

		String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(fileBuffer));
		String fileType = contentType.split("/")[0];
		String imageType = contentType.split("/")[1];

		if (!fileType.equals("image"))
			throw new IOException("Not an image!");
		if (srcType.equals("jpg")) {
			if (!imageType.equals(srcType) && !imageType.equals("jpeg"))
				throw new IOException("Format mismatch!");
		} else if (!imageType.equals(srcType))
			throw new IOException("Format mismatch!");

		errorChar = '2';

		ByteArrayInputStream bais = new ByteArrayInputStream(fileBuffer);
		BufferedImage loadedImage = ImageIO.read(bais);

		BufferedImage bi = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(loadedImage, 0, 0, bi.getWidth(), bi.getHeight(), Color.WHITE, null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, dstType, baos);
		return baos.toByteArray();
	}

	private void sendData(byte[] fileBuffer, char controlChar) throws IOException {
		error = "Response processing error!";
		success = "Sent converted file";
		errorChar = '2';
		int i, index, size;
		int length = fileBuffer.length;
		i = index = size = 0;

		outputSocketStream.write(controlChar);

		while ((index = i * blockSize) <= length) {
			size = Math.min(length - index, blockSize);
			outputSocketStream.writeShort(size);
			outputSocketStream.write(fileBuffer, index, size);
			i++;
		}

		if (size == blockSize)
			outputSocketStream.writeShort(0);
		outputSocketStream.flush();
	}

	public void closeConnection() throws IOException {
		error = "Exit error!";
		success = "Closing connection...";
		socket.close();
	}

	@Override
	public void run() {
		byte[] toSendBuffer = null;
		byte[] receivedBuffer = null;

		Logger logger = Logger.getLogger(loggerName);

		try {

			try {
				logger.log(Level.INFO, loggerName + "Waiting for header");
				receiveHeader();
				logger.log(Level.INFO, loggerName + success);
				receivedBuffer = receiveFile();
				logger.log(Level.INFO, loggerName + success);
				toSendBuffer = convertFile(receivedBuffer);
				logger.log(Level.INFO, loggerName + success);
				sendData(toSendBuffer, '0');
				logger.log(Level.INFO, loggerName + success);
				closeConnection();
				logger.log(Level.INFO, loggerName + success);
			} catch (IOException e) {
				logger.log(Level.SEVERE, loggerName + error, e);
				sendData(error.getBytes(), errorChar);
				closeConnection();
				logger.log(Level.INFO, loggerName + success);
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, loggerName + "Fatal connection error!", e);
		}
	}
}
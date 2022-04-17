package myServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Converter {

	private final static int port = 2001;
	private ServerSocket serverSocket = null;
	ExecutorService threadPool = null;

	public Converter() throws IOException {
		serverSocket = new ServerSocket(port);
		threadPool = Executors.newCachedThreadPool();
	}

	public void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			threadPool.submit(new ConverterHandler(socket));
		}
	}

	public void stop() throws IOException {
		threadPool.shutdown();
		serverSocket.close();
	}

	public static void main(String[] args) {

		Converter converter = null;

		try {

			try {
				System.out.println("Initializing server...");
				converter = new Converter();
				System.out.println(String.format("Server listening on port [%d].", port));
				converter.start();
				converter.stop();
			} catch (IOException e) {
				System.err.println("Internal server error!");
				e.printStackTrace();
				if(converter != null) {
					System.out.println("Closing server...");
					converter.stop();
				}
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Exit error!");
			e.printStackTrace();
			System.exit(2);
		}
	}
}
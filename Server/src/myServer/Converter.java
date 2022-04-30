package myServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Converter {

	private final static int port = 2001;
	private static ServerSocket serverSocket = null;
	private static ExecutorService threadPool = null;
	private static boolean gracefulClose = false;
	private static List<Socket> clientList = new ArrayList<Socket>();

	public Converter() throws IOException {
		serverSocket = new ServerSocket(port);
		threadPool = Executors.newCachedThreadPool();
	}

	public void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			clientList.add(socket);
			threadPool.submit(new ConverterHandler(socket));
			clientList.removeIf((s) -> s.isClosed());
		}
	}

	public static void stop() throws IOException {
		gracefulClose = true;
		clientList.forEach((client) -> {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		if (threadPool != null && !threadPool.isShutdown())
			threadPool.shutdown();
		if (serverSocket != null && !serverSocket.isClosed())
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
				stop();
			} catch (IOException e) {
				if (!gracefulClose) {
					System.err.println("Internal server error!");
					e.printStackTrace();
					if (converter != null) {
						System.out.println("Closing server...");
						Converter.stop();
					}
				} else {
					System.out.println("Closing server...");
					return;
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
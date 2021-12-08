import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyChattingServer {

    MyChattingServer() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(7456);

                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + "IP에서 접속");

                MyChattingWorker worker = new MyChattingWorker(socket);
                worker.start();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new MyChattingServer();
    }
}

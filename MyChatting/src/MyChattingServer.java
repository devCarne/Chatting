import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyChattingServer {

    MyChattingServer() {

        try {
            ServerSocket serverSocket = new ServerSocket(7456);
            List<MyChattingWorker> chattingWorkers = new ArrayList<>();
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + "IP에서 접속");

                MyChattingWorker worker = new MyChattingWorker(socket, chattingWorkers);
                worker.start();
                chattingWorkers.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyChattingServer();
    }
}

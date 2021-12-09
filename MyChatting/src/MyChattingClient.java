import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MyChattingClient implements Runnable {

    Socket socket;
    Scanner scanner = new Scanner(System.in);
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    String nickname;

    MyChattingClient() {
        System.out.println("닉네임을 입력해주세요.");
        nickname = scanner.nextLine();
        if (nickname == null || nickname.length() == 0) nickname = "guest";

        try {
            socket = new Socket("125.135.234.228", 7456);
            System.out.println("접속되었습니다.");

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            MyChattingMessage joinMessage = new MyChattingMessage();
            joinMessage.setNickname(nickname);
            joinMessage.setAction(MyChattingAction.JOIN);

            outputStream.writeObject(joinMessage);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MyChattingMessage inputMessage = (MyChattingMessage) inputStream.readObject();

                if (inputMessage.getAction() == MyChattingAction.EXIT) {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    System.exit(0);
                } else if (inputMessage.getAction() == MyChattingAction.SEND) {
                    System.out.println(inputMessage.getMessage());
//                    System.out.println("메시지를 입력하세요");
//                    String message = scanner.nextLine();
//                    if (message.equals("EXIT")) {
//                        chattingMessage.setAction(MyChattingAction.EXIT);
//                    } else {
//                        chattingMessage.setAction(MyChattingAction.SEND);
//                        chattingMessage.setMessage(message);
//                        chattingMessage.setNickname(nickname);
//                    }
//                    outputStream.writeObject(chattingMessage);
//                    outputStream.flush();
                }
                System.out.println("메시지를 입력하세요.");
                MyChattingMessage outputMessage = new MyChattingMessage();
                outputMessage.setAction(MyChattingAction.SEND);
                outputMessage.setMessage(scanner.nextLine());

                outputStream.writeObject(outputMessage);
                outputStream.flush();

            } catch (IOException | ClassNotFoundException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new MyChattingClient();
    }
}
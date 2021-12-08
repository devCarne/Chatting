import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MyChattingWorker extends Thread{

    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Socket socket;

    MyChattingWorker(Socket socket) {
        this.socket = socket;

        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                MyChattingMessage chattingMessage = (MyChattingMessage) inputStream.readObject();
                String nickname = chattingMessage.getNickname();

                if (chattingMessage.getAction() == MyChattingAction.EXIT) {
                    MyChattingMessage serverMessage = new MyChattingMessage();
                    serverMessage.setAction(MyChattingAction.EXIT);
                    serverMessage.setNickname(nickname);
                    outputStream.writeObject(serverMessage);
                    outputStream.flush();

                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    System.out.println(chattingMessage.getNickname() + "님이 접속을 종료했습니다.");
                    break;
                } else if (chattingMessage.getAction() == MyChattingAction.JOIN) {
                    MyChattingMessage serverMessage = new MyChattingMessage();
                    serverMessage.setAction(MyChattingAction.SEND);
                    serverMessage.setMessage(nickname + "님이 입장하셨습니다.");
                    outputStream.writeObject(serverMessage);
                    outputStream.flush();
                } else if (chattingMessage.getAction() == MyChattingAction.SEND) {
                    MyChattingMessage serverMessage = new MyChattingMessage();
                    serverMessage.setAction(MyChattingAction.SEND);
                    serverMessage.setMessage(nickname + " : " + chattingMessage.getMessage());
                    outputStream.writeObject(serverMessage);
                    outputStream.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(MyChattingMessage chattingMessage) throws IOException {
        System.out.println(chattingMessage.getNickname() + " : " + chattingMessage.getMessage());
    }
}

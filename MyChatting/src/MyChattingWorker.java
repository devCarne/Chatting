import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyChattingWorker extends Thread{

    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Socket socket;
    List<MyChattingWorker> chattingWorkers;

    MyChattingWorker(Socket socket, List<MyChattingWorker> chattingWorkers) {
        this.socket = socket;
        this.chattingWorkers = chattingWorkers;

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
                MyChattingMessage inputMessage = (MyChattingMessage) inputStream.readObject();
                String nickname = inputMessage.getNickname();

                if (inputMessage.getAction() == MyChattingAction.EXIT) {
                    MyChattingMessage serverMessage = new MyChattingMessage();
                    serverMessage.setAction(MyChattingAction.EXIT);
                    serverMessage.setNickname(nickname);
                    outputStream.writeObject(serverMessage);
                    outputStream.flush();

                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    System.out.println(inputMessage.getNickname() + "님이 접속을 종료했습니다.");
                    break;
                } else if (inputMessage.getAction() == MyChattingAction.JOIN) {
                    MyChattingMessage outputMessage = new MyChattingMessage();
                    outputMessage.setAction(MyChattingAction.SEND);
                    outputMessage.setMessage(nickname + "님이 입장하셨습니다.");
                    broadcast(outputMessage);

                } else if (inputMessage.getAction() == MyChattingAction.SEND) {
                    MyChattingMessage outputMessage = new MyChattingMessage();
                    outputMessage.setAction(MyChattingAction.SEND);
                    outputMessage.setMessage(nickname + " : " + inputMessage.getMessage());
                    broadcast(outputMessage);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(MyChattingMessage chattingMessage) throws IOException {
        for(MyChattingWorker worker : chattingWorkers) {
            worker.outputStream.writeObject(chattingMessage);
            worker.outputStream.flush();
        }
    }
}

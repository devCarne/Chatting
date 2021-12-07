package chatting.basic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ChatWorker extends Thread // 처리해주는 곳(소켓에 대한 정보가 담겨있는 곳. 소켓을 처리함)
{
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	private List<ChatWorker> list;

	public ChatWorker(Socket socket, List<ChatWorker> list) throws IOException {
		this.socket = socket;
		this.list = list;
		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
	}

	public void run() {
		ChatData chatData = null;
		String nickName;
		try {
			while (true) {
				chatData = (ChatData) reader.readObject();
				nickName = chatData.getNickName();

				// System.out.println("배열 크기:"+ar.length);
				// 사용자가 접속을 끊었을 경우. 프로그램을 끝내서는 안되고 남은 사용자들에게 퇴장메세지를 보내줘야 한다.
				if (chatData.getAction() == ChatAction.EXIT) {
					ChatData sendChatData = new ChatData();
					// 나가려고 exit를 보낸 클라이언트에게 답변 보내기
					sendChatData.setAction(ChatAction.EXIT);
					writer.writeObject(sendChatData);
					writer.flush();

					reader.close();
					writer.close();
					socket.close();
					// 남아있는 클라이언트에게 퇴장메세지 보내기
					list.remove(this);

					sendChatData.setAction(ChatAction.SEND);
					sendChatData.setMessage(nickName + "님 퇴장하였습니다");
					broadcast(sendChatData);
					break;
				} else if (chatData.getAction() == ChatAction.JOIN) {
					// 모든 사용자에게 메세지 보내기
					// nickName = dto.getNickName();
					// 모든 클라이언트에게 입장 메세지를 보내야 함
					ChatData sendDto = new ChatData();
					sendDto.setAction(ChatAction.SEND);
					sendDto.setMessage(nickName + "님 입장하였습니다");
					broadcast(sendDto);
				} else if (chatData.getAction() == ChatAction.SEND) {
					ChatData sendDto = new ChatData();
					sendDto.setAction(ChatAction.SEND);
					sendDto.setMessage("[" + nickName + "]" + chatData.getMessage());
					broadcast(sendDto);
				}
			} // while

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	// 다른 클라이언트에게 전체 메세지 보내주기
	public void broadcast(ChatData sendChatData) throws IOException {
		for (ChatWorker handler : list) {
			handler.writer.writeObject(sendChatData); // 핸들러 안의 writer에 값을 보내기
			handler.writer.flush(); // 핸들러 안의 writer 값 비워주기
		}
	}
}

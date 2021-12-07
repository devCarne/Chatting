package chatting.client;

import chatting.basic.ChatAction;
import chatting.basic.ChatData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class ChatClient extends JFrame implements ActionListener, Runnable {
	private JTextArea jTextArea;
	private JTextField jTextField;
	private JButton jButton;
	private Socket socket;
	private ObjectInputStream reader = null;
	private ObjectOutputStream writer = null;
	private String nickName;

	public ChatClient() {
		// 센터에 TextArea만들기
		jTextArea = new JTextArea();
		jTextArea.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		jTextArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(jTextArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 항상 스크롤바가 세로로 떠있음
		// 하단에 버튼과 TextArea넣기
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		jTextField = new JTextField();

		jButton = new JButton("보내기");

		bottom.add("Center", jTextField); // 센터에 붙이기
		bottom.add("East", jButton); // 동쪽에 붙이기
		// container에 붙이기
		Container c = this.getContentPane();
		c.add("Center", scroll); // 센터에 붙이기
		c.add("South", bottom); // 남쪽에 붙이기
		// 윈도우 창 설정
		setBounds(300, 300, 300, 300);
		setVisible(true);

		// 윈도우 이벤트
		// 익명 클래스 생성 : new 가장_가까운_부모_클래스() {
//								클래스 구현
//							}
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
//				System.exit(0);
				try {
					// ChatData chatData = new ChatData(nickName, ChatAction.EXIT);
					ChatData chatData = new ChatData();
					chatData.setNickName(nickName);
					chatData.setAction(ChatAction.EXIT);
					writer.writeObject(chatData); // 역슬러쉬가 필요가 없음
					writer.flush();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		});
	}

	public void service() {
		// 서버 IP 입력받기
		// String serverIP = JOptionPane.showInputDialog(this, "서버IP를
		// 입력하세요","서버IP",JOptionPane.INFORMATION_MESSAGE);
		String serverIP = JOptionPane.showInputDialog(this, "서버IP를 입력하세요", "192.168.0.200"); // 기본적으로 아이피 값이 입력되어 들어가게 됨
		if (serverIP == null || serverIP.length() == 0) { // 만약 값이 입력되지 않았을 때 창이 꺼짐
			System.out.println("서버 IP가 입력되지 않았습니다.");
			System.exit(0);
		}
		// 닉네임 받기
		nickName = JOptionPane.showInputDialog(this, "닉네임을 입력하세요", "닉네임", JOptionPane.INFORMATION_MESSAGE);
		if (nickName == null || nickName.length() == 0) {
			nickName = "guest";
		}
		try {
			socket = new Socket(serverIP, 9500);
			// 에러 발생
			reader = new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("전송 준비 완료!");

		} catch (UnknownHostException e) {
			System.out.println("서버를 찾을 수 없습니다.");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("서버와 연결이 안되었습니다.");
			e.printStackTrace();
			System.exit(0);
		}
		try {
			// 서버로 닉네임 보내기

			ChatData chatData = new ChatData();
			chatData.setAction(ChatAction.JOIN);
			chatData.setNickName(nickName);
			writer.writeObject(chatData); // 역슬러쉬가 필요가 없음
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 스레드 생성

		Thread t = new Thread(this);
		t.start();
		jTextField.addActionListener(this);
		jButton.addActionListener(this); // 멕션 이벤트 추가
	}

	// 스레드 오버라이드
	@Override
	public void run() {
		// 서버로부터 데이터 받기
		ChatData chatData = null;
		while (true) {
			try {
				chatData = (ChatData) reader.readObject();
				if (chatData.getAction() == ChatAction.EXIT) { // 서버로부터 내 자신의 exit를 받으면 종료됨
					reader.close();
					writer.close();
					socket.close();
					System.exit(0);
				} else if (chatData.getAction() == ChatAction.SEND) {
					jTextArea.append(chatData.getMessage() + "\n");

					int pos = jTextArea.getText().length();
					jTextArea.setCaretPosition(pos);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// ActionPerformed
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// 서버로 보냄
			// JTextField값을 서버로보내기
			// 버퍼 비우기
			String msg = jTextField.getText();
			ChatData chatData = new ChatData();
			// dto.setNickName(nickName);
			if (msg.equals("exit")) {
				chatData.setAction(ChatAction.EXIT);
			} else {
				chatData.setAction(ChatAction.SEND);
				chatData.setMessage(msg);
				chatData.setNickName(nickName);
			}
			writer.writeObject(chatData);
			writer.flush();
			jTextField.setText("");

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
//동시 채팅을 위해 쓰레드를 생성해주어야 함
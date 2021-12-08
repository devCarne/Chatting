import java.io.Serializable;

public class MyChattingMessage implements Serializable {

    String nickname;
    String message;
    MyChattingAction action;

    public MyChattingMessage(){}

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public MyChattingAction getAction() {
        return action;
    }

    public void setAction(MyChattingAction action) {
        this.action = action;
    }
}

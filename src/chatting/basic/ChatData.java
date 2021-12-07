package chatting.basic;

import java.io.Serializable;

public class ChatData implements Serializable {

    private String nickName;
    private String message;
    private ChatAction action;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatAction getAction() {
        return action;
    }

    public void setAction(ChatAction action) {
        this.action = action;
    }
}

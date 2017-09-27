package cn.sinjinsong.rpc.core.domain;

import cn.sinjinsong.rpc.core.enumeration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private MessageType type;
    public static final Message PING = new Message(MessageType.PING);
    public static final Message PONG = new Message(MessageType.PONG);
}

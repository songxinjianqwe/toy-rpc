package cn.sinjinsong.rpc.domain.common;

import cn.sinjinsong.rpc.enumeration.MessageType;
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

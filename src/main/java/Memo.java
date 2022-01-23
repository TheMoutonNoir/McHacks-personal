import discord4j.common.util.Snowflake;

import java.util.Date;
import java.util.ArrayList;

public class Memo {
    private ArrayList<String> msg_content;
    private Snowflake channel_id;
    private Date time;

    public Memo(String msg_content, Snowflake channel_id)
    {
        this.channel_id = channel_id;
        this.msg_content = new ArrayList<>();
        this.msg_content.add(msg_content);
    }

    public Memo(String msg_content, Date time){
        this.msg_content = new ArrayList<>();
        this.msg_content.add(msg_content);
        this.time = time;
    }

    public Snowflake getChannel_id(){
        return this.channel_id;
    }

    public void addAlreadyMemo(String extra_msg){
        this.msg_content.add(extra_msg);
    }

    public String toString(){
        StringBuilder memo_block_msg = new StringBuilder();
        for (String s : msg_content) {
            memo_block_msg.append(" " + s);
        }
        return memo_block_msg.toString();
    }
}

import discord4j.common.util.Snowflake;

import java.util.Date;
import java.util.ArrayList;

public class Reminder {
    private ArrayList<String> msg_content;
    private Snowflake channel_id;
    private Date time;

    public Reminder(String msg_content, Snowflake channel_id)
    {
        this.channel_id = channel_id;
        this.msg_content = new ArrayList<>();
        this.msg_content.add(msg_content);
    }

    public Reminder(String msg_content, Date time){
        this.msg_content = new ArrayList<>();
        this.msg_content.add(msg_content);
        this.time = time;
    }

    public Snowflake getChannel_id(){
        return this.channel_id;
    }

    public void addAlreadyReminder(String extra_msg){
        this.msg_content.add(extra_msg);
    }

    public String toString(){
        StringBuilder reminder_block_msg = new StringBuilder();
        for (String s : msg_content) {
            reminder_block_msg.append(" " + s);
        }
        return reminder_block_msg.toString();
    }
}

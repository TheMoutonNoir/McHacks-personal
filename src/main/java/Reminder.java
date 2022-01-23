import java.util.Date;
import java.util.ArrayList;

public class Reminder {
    private ArrayList<String> msg_content;
    private Date time;

    public Reminder(String msg_content, Date time){
        this.msg_content = new ArrayList<>();
        this.msg_content.add(msg_content);
        this.time = time;
    }

    public void addAlreadyReminder(String extra_msg){
        this.msg_content.add(extra_msg);
    }
}

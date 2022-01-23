
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.HashMap;


public class Bot {

    private static HashMap<String,Reminder> ReminderList;
    private static boolean running;

    public static void main(String[] args){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        running = true;
        Thread clock = new Thread() {
            @Override
            public void run() {
                try{
                    while(running){
                        String current = dtf.format(LocalDateTime.now());
                        if(ReminderList.containsKey(current)){
                            ReminderList.get(current);
                            sleep(1000);
                        }
                    }
                }catch (Exception e){
                }
            }
        };

        final DiscordClient client = DiscordClient.create(System.getenv("BOT_TOKEN"));
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!reminder".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("").block();
            }
        });

        gateway.onDisconnect().block();
    }
}

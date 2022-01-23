
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.*;


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
            final String messageText = message.getContent();


            if(messageText.length() >= 9 && "!reminder".equals(messageText.toLowerCase().substring(0, messageText.indexOf(" ")))){

                final MessageChannel channel = message.getChannel().block();
                String date = messageText.substring(messageText.indexOf(" "));
                String hour = "";
                String minute = "";
                for(int i = 0; i < date.length(); i++){


                    if(date.charAt(i) == ':'){
                        hour = date.substring(1, i);
                        minute = date.substring(i+1);
                    }
                }

                Calendar c = new GregorianCalendar();

                Date dateValue = new Date();

                dateValue.setHours(Integer.parseInt(hour));
                dateValue.setMinutes(Integer.parseInt(minute));
                c.setTime(dateValue);

                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                String current = dtf2.format(LocalDateTime.now());

                // LocalDate parsedDate = LocalDate.parse(date, ofLocalizedDateTime(MEDIUM,MEDIUM));

                //channel.createMessage("Reminder set for " + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + " at " + c.get(Calendar.HOUR_OF_DAY) + "h" + c.get(Calendar.MINUTE)).block();
                channel.createMessage(current).block();


            }





    });
        gateway.onDisconnect().block();
    }
}


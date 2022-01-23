
import discord4j.common.util.Snowflake;
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.*;


public class Bot {
    private static Snowflake channel_id;
    private static HashMap<String, Reminder> ReminderList = new HashMap<>();
    private static boolean running;

    public static void main(String[] args) {

        final DiscordClient client = DiscordClient.create(System.getenv("BOT_TOKEN"));
        final GatewayDiscordClient gateway = client.login().block();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        running = true;
        Thread clock = new Thread(() -> {
            try {
                while (running) {
                    String current = dtf.format(LocalDateTime.now());
                    System.out.println(current);
                    System.out.flush();
                    if (ReminderList.containsKey(current)) {
                        Reminder current_reminder= ReminderList.get(current);
                        RestChannel destination_msg = client.getChannelById(current_reminder.getChannel_id());
                        destination_msg.createMessage(current_reminder.toString()).block();
                        ReminderList.remove(current);
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
            }
        });

       clock.start();

       System.out.println("Hey");


        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            final String messageText = message.getContent();
            Snowflake temp = event.getMessage().getChannelId();
            channel_id = temp;


            if (messageText.length() >= 9 && "!reminder".equals(messageText.toLowerCase().substring(0, messageText.indexOf(" ")))) {

                final MessageChannel channel = message.getChannel().block();
                String date = messageText.substring(messageText.indexOf(" "));
                String hour = "";
                String minute = "";
                for (int i = 0; i < date.length(); i++) {


                    if (date.charAt(i) == ':') {
                        hour = date.substring(1, i);
                        minute = date.substring(i + 1);
                    }
                }

                Calendar c = new GregorianCalendar();

                Date dateValue = new Date();

                dateValue.setHours(Integer.parseInt(hour));
                dateValue.setMinutes(Integer.parseInt(minute));
                c.setTime(dateValue);

                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
                String current = dtf2.format(LocalDateTime.now());

                // LocalDate parsedDate = LocalDate.parse(date, ofLocalizedDateTime(MEDIUM,MEDIUM));

                //channel.createMessage("Reminder set for " + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR) + " at " + c.get(Calendar.HOUR_OF_DAY) + "h" + c.get(Calendar.MINUTE)).block();
                String tempReminderTime = c.get(Calendar.DAY_OF_MONTH) + "/"
                        + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) +
                        "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                System.out.println(temp.toString());
                ReminderList.put(tempReminderTime, new Reminder("Input reminder", temp));

                channel.createMessage("Reminder set for " + tempReminderTime + " , now: " + current).block();
            }


        });
        gateway.onDisconnect().block();
    }
}


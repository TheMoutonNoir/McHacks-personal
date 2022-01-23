
import discord4j.common.util.Snowflake;
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.*;


public class Bot {
    private static HashMap<String, Memo> MemoList = new HashMap<>();
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
                    if (MemoList.containsKey(current)) {
                        Memo current_memo= MemoList.get(current);
                        RestChannel destination_msg = client.getChannelById(current_memo.getChannel_id());
                        destination_msg.createMessage(current_memo.toString()).block();
                        MemoList.remove(current);
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
            }
        });

       clock.start();


        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            final String messageText = message.getContent();
            Snowflake temp = event.getMessage().getChannelId();

            if (messageText.equals("!cat".toLowerCase())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("https://media4.giphy.com/media/f8ywYgttpGzzVPH5AO/giphy.gif").block();
            }

            if (messageText.length() >= 9 && "!memo".equals(messageText.toLowerCase().substring(0, messageText.indexOf(" ")))) {

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

                String tempMemoTime = c.get(Calendar.DAY_OF_MONTH) + "/"
                        + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) +
                        "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                MemoList.put(tempMemoTime, new Memo("Input memo", temp));

                channel.createMessage("Memo set for " + tempMemoTime + " , now: " + current).block();
            }


        });
        gateway.onDisconnect().block();
    }
}


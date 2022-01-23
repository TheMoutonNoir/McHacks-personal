
import discord4j.common.util.Snowflake;
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Bot {
    private static HashMap<String, Memo> MemoList = new HashMap<>();
    private static boolean running;

    public static void main(String[] args) {

        final DiscordClient client = DiscordClient.create(System.getenv("BOT_TOKEN"));
        final GatewayDiscordClient gateway = client.login().block();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        running = true;
        Thread clock = new Thread(() -> {
            try {
                while (running) {
                    String current = dtf.format(LocalDateTime.now());
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
            Pattern patternSimple = Pattern.compile("^\\!memo [0-2]?[0-9]\\:[0-5][0-9]\\p{ASCII}*$", Pattern.CASE_INSENSITIVE);
            Pattern patternComplex = Pattern.compile("^\\!memo [0-2]?[0-9]\\:[0-5][0-9] [0-3]?[0-9]\\/[0-1]?[0-9]\\/(20)?[0-9][0-9]\\p{ASCII}*$", Pattern.CASE_INSENSITIVE);




            final Message message = event.getMessage();
            final String messageText = message.getContent();
            Snowflake temp = event.getMessage().getChannelId();

            if (messageText.equals("!cat".toLowerCase())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("https://media4.giphy.com/media/f8ywYgttpGzzVPH5AO/giphy.gif").block();
            }

            Matcher matcherSimple = patternSimple.matcher(messageText);
            boolean matchSimple = matcherSimple.find();

            Matcher matcherComplex = patternComplex.matcher(messageText);
            boolean matchComplex = matcherComplex.find();




            if (matchComplex || matchSimple) {

                final MessageChannel channel = message.getChannel().block();
                String date = messageText.substring(messageText.indexOf(" "));
                String hour = "";
                String minute = "";
                String[] userInput = messageText.split(" ");

                 for (int i = 0; i < date.length(); i++) {
                    if (date.charAt(i) == ':') {
                        hour = date.substring(1, i);
                        minute = date.substring(i + 1, i+3);
                    }
                }

                Calendar c = new GregorianCalendar();

                Date dateValue = new Date();


                Date tempDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
                tempDate.setMinutes(LocalDateTime.now().getMinute());
                tempDate.setHours(LocalDateTime.now().getHour());


                    dateValue.setHours(Integer.parseInt(hour));
                    dateValue.setMinutes(Integer.parseInt(minute));



                if(dateValue.before(tempDate)){
                    c.setTime(dateValue);
                    Calendar tempCalendar = new GregorianCalendar();

                    if(matchComplex){
                        String[] customDate = userInput[1].split("/");

                        int year = Integer.parseInt(customDate[2]) < 100 ? 2000 + Integer.parseInt(customDate[2]) : Integer.parseInt(customDate[2]);
                        int month = Integer.parseInt(customDate[1]) - 1;
                        int day = Integer.parseInt(customDate[0]);

                        c.set(year, month, day, dateValue.getHours(), dateValue.getMinutes());;

                    }else{
                        tempCalendar.setTime(c.getTime());



                    }




                    tempCalendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) + 1);

                    c.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));

                }else {
                    if(matchComplex){

                        String[] customDate = userInput[2].split("/");


                        int year = Integer.parseInt(customDate[2]) < 100 ? 2000 + Integer.parseInt(customDate[2]) : Integer.parseInt(customDate[2]);
                        int month = Integer.parseInt(customDate[1]) -1;
                        int day = Integer.parseInt(customDate[0]);

                        c.set(year, month, day, dateValue.getHours(), dateValue.getMinutes());

                    }else{
                        c.setTime(dateValue);





                    }


                }




                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
                String current = dtf2.format(LocalDateTime.now());

                    String userMessage = "";
                    if(matchComplex){
                        if(userInput.length > 3){
                            for (int i = 3; i < userInput.length; i++) {
                                userMessage += userInput[i] + " ";
                            }
                        }
                    }else{
                        if(userInput.length > 2){
                            for (int i = 2; i < userInput.length; i++) {
                                userMessage += userInput[i] + " ";
                            }
                        }
                    }

                String tempMemoTime = (c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY)) + ":"
                        + ((c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE))) + " " + c.get(Calendar.DAY_OF_MONTH) + "/"
                        + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) +
                        "/" + c.get(Calendar.YEAR);
                MemoList.put(tempMemoTime, new Memo(userMessage, temp));

                channel.createMessage("Memo set for " + tempMemoTime).block();


            }


        });
        gateway.onDisconnect().block();
    }






}


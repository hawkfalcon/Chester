package com.hawkfalcon.chester;

import com.hawkfalcon.jmegahal.JMegaHal;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.*;
import java.util.Random;

public class Chester extends ListenerAdapter {
    JMegaHal hal = new JMegaHal();

    @SuppressWarnings("unchecked")
    public void main(String[] args) throws Exception {
        Configuration config = new Configuration.Builder()
                .setName("Chester")
                .setLogin("Chester")
                .setAutoNickChange(true)
                .setServer("irc.esper.net", 6667)
                .addAutoJoinChannel("#hawkfalcon")
                .buildConfiguration();
        PircBotX bot = new PircBotX(config);
        bot.startBot();
    }

    public Chester() {
        try (BufferedReader br = new BufferedReader(new FileReader("chester.brain"))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                hal.add(line);
            }
        } catch (Exception e) {
            firstRun();
        }
    }

    private void firstRun() {
        System.err.println("Couldn't find the brain chester.brain so will use default data");
        hal.add("Hello World");
        hal.add("Can I have some coffee?");
        hal.add("Please slap me");
    }

    public void onPrivateMessage(PrivateMessageEvent event) {
        String message = event.getMessage();
        if (message.startsWith("!join")) {
            event.getBot().sendIRC().joinChannel(message.split(" ")[1]);
        }
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        String message = event.getMessage();
        String words[] = message.split(" ");
        String seed = words[new Random().nextInt(words.length)];
        if (message.toLowerCase().contains("chester")) {
            event.respond(dePing(hal.getSentence(seed), 2));
        } else {
            addToBrain(message);
        }
    }

    public void addToBrain(String rawmessage) {
        if (rawmessage != null && rawmessage.length() < 3) return;
        String message = clean(rawmessage);
        hal.add(message);
        write(message);
    }

    public String clean(String string) {
        if (string.length() > 300) {
            string = string.substring(0, 300);
        }
        return string.replaceAll("<.*?>", "").replaceAll("\\[.*?\\]", "");
    }

    public String dePing(String text, int period) {
        String insert = String.valueOf((char) 0x200b);
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index, Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

    public void write(String sentence) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("chester.brain"))) {
            bw.append(sentence);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
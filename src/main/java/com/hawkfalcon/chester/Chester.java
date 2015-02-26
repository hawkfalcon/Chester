package com.hawkfalcon.chester;

import org.jibble.jmegahal.JMegaHal;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Chester extends ListenerAdapter {
    JMegaHal hal = new JMegaHal();
    String ping = "Chester";

    public Chester() {
        try (BufferedReader br = new BufferedReader(new FileReader("chester.brain"))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                hal.add(line);
            }
        } catch (Exception e) {
            firstRun();
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration.Builder()
                .setName("Chester")
                .setLogin("Chester")
                .setAutoNickChange(true)
                .setServer("irc.esper.net", 6667)
                .addAutoJoinChannel("#hawkfalcon")
                .addAutoJoinChannel("#drtshock")
                .addListener(new Chester())
                .buildConfiguration();
        PircBotX bot = new PircBotX(config);
        bot.startBot();
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

    public void onMessage(MessageEvent event) throws Exception {
        String message = event.getMessage();
        if (message.toLowerCase().contains(ping)) {
            event.getChannel().send().message(dePing(hal.getSentence(getSeed(message)), 2));
        } else {
            addToBrain(message);
        }
    }

    public String getSeed(String message) {
        List<String> words = new ArrayList<>(Arrays.asList(message.split(" ")));
        words.remove(ping);
        words.removeIf(word -> word.length() < 4);
        if (words.isEmpty()) return null;
        return words.get(new Random().nextInt(words.size()));
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("chester.brain", true))) {
            bw.append(sentence);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

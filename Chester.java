import org.jibble.jmegahal.JMegaHal;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.*;
import java.util.Random;

public class Chester extends ListenerAdapter implements Listener {
    public static void main(String[] args) throws Exception {
        PircBotX bot = new PircBotX();
        bot.setAutoNickChange(true);
        bot.setName("Chester");
        bot.setLogin("Chester");
        bot.setVersion("Chester https://github.com/hawkfalcon/Chester");
        bot.setFinger("Get your hand off of me!");
        bot.setVerbose(true);
        bot.connect("chaos.esper.net");
        bot.joinChannel("#hawkfalcon");
        bot.joinChannel("#drtshock");
        bot.getListenerManager().addListener(new Chester());
    }

    JMegaHal hal = new JMegaHal();

	public static String silence(String string) {
		if (string == null || string.length() < 4) {
			return string;
		} else {
			return insertPeriodically(string, String.valueOf((char) 0x200b), 2);
		}
	}

	public static String insertPeriodically(String text, String insert, int period) {
		StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length()/period)+1);
		int index = 0;
		String prefix = "";
		while (index < text.length()) {
			builder.append(prefix);
			prefix = insert;
			builder.append(text.substring(index, Math.min(index + period, text.length())));
			index += period;
		}
		return builder.toString();
	}

    public Chester() {
        try {
            FileReader fr = new FileReader("chester.brain");
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                hal.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            firstRun();
        } catch (IOException e) {
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
        String[] args = event.getMessage().split("[ ]+");
        String message = event.getMessage();
        Channel channel = event.getBot().getChannel(args[1]);
        if (message.startsWith("!join")) {
            event.getBot().joinChannel(args[1]);
        } else if (message.startsWith("!leave")) {
            event.getBot().partChannel(channel, "I was asked to leave");
        } else {
            hal.add(message);
            write(message);
        }
    }

    public void onMessage(MessageEvent event) throws Exception {
        String message = event.getMessage();
        String words[] = message.split(" ");
        Random rand = new Random();
        String word = words[rand.nextInt(words.length)];
        if (message.toLowerCase().contains("chester")) {
            event.getBot().sendMessage(event.getChannel(), clean(hal.getSentence(word)));
        } else {
            hal.add(message);
            write(message);
        }
    }

    public static String clean(String string) {
        if (string != null && string.length() > 300) {
            string = string.substring(0, 300);
        }
        String newstring = string.replaceAll("<.*?>", "").replaceAll("\\[.*?\\]", "");
        return silence(newstring);
    }

    public static void write(String sentence) {
        try {
            FileWriter fw = new FileWriter("chester.brain", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.append(sentence);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

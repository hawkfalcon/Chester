import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jibble.jmegahal.JMegaHal;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Chester extends ListenerAdapter implements Listener {
    public static void main(String[] args) throws Exception {
		PircBotX bot = new PircBotX();
		bot.setAutoNickChange(true);
		bot.setName("Chester");
		bot.setLogin("Chester");
		bot.setVersion("Chester"); 
		bot.setFinger("Get your hand off of me!");
		bot.setVerbose(true);
		bot.connect("irc.esper.net");
		bot.joinChannel("#hawkfalcon");
		bot.getListenerManager().addListener(new Chester());
    }	
    
	public void onConnect(ConnectEvent event) {
    event.getBot().identify("chester_");
	}
	JMegaHal hal = new JMegaHal();
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
		if(message.startsWith("!join")){
			event.getBot().joinChannel(args[1]);
		}
		else if(message.startsWith("!leave")){
			event.getBot().partChannel(channel, "I was asked to leave");
		}
		else {
			hal.add(message);
			write(message);
		}
	}

	public void onMessage(MessageEvent event) throws Exception{
		String message = event.getMessage();
		if(message.toLowerCase().contains("chester") && !event.getChannel().getName().toLowerCase().equals("#bukkitdev")) {
			event.getBot().sendMessage(event.getChannel(), clean(hal.getSentence()));
		} else {
			hal.add(message);
            write(message);
		}
	}
	public static String clean(String string){
		if (string != null && string.length() > 300) {
			string = string.substring(0, 300);
	     }
		String newstring = string.replaceAll("<.*?>", "").replaceAll("\\[.*?\\]", "");
		return newstring;  
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

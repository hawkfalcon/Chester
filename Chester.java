/* Uses PircBotX, JMegaHal, and is based off of ChatBackBot*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.jibble.jmegahal.JMegaHal;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class Chester extends ListenerAdapter implements Listener {

	private static String BRAIN = "brain.ser";
	JMegaHal hal = new JMegaHal();

	public Chester() {
		// load any previously saved brain
		ObjectInputStream in = null;
		try {
			File file = new File(BRAIN);
			in = new ObjectInputStream(new FileInputStream(file));
			hal = (JMegaHal) in.readObject();
		} catch (FileNotFoundException e) {
			firstRun();
		} catch (IOException e) {
			firstRun();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void firstRun() {
		System.err.println("Couldn't find the brain ("+ BRAIN +") so will use default data");
		hal.add("Hello World");
		hal.add("Can I have some coffee?");
		hal.add("Please slap me");
	}

	public void onPrivateMessage(PrivateMessageEvent event) {
		String delims = "[ ]+";
		String[] args = event.getMessage().split(delims);
		String message = event.getMessage();
		Channel channel = event.getBot().getChannel(args[1]);
		if(message.startsWith("!join")){
			event.getBot().joinChannel(args[1]);
		}
		else if(message.startsWith("!leave")){
			event.getBot().sendAction(channel, "was asked to leave");
			event.getBot().partChannel(channel, "I was asked to leave");
		}
		else {
			hal.add(message);
			try {
				// save the new data
				ObjectOutput out = new ObjectOutputStream(new FileOutputStream(BRAIN));
				out.writeObject(hal);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onMessage(MessageEvent event) throws Exception{
		String delims = "[ ]+";
		String[] args = event.getMessage().split(delims);
		String message = event.getMessage();

		// if the bot name is used, get a reply
		if(message.toLowerCase().contains("chester")) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			event.getBot().sendMessage(event.getChannel(), truncate((hal.getSentence()), 300));
		} else {
			// add the new data to the brain
			hal.add(message);
			try {
				// save the new data
				ObjectOutput out = new ObjectOutputStream(new FileOutputStream(BRAIN));
				out.writeObject(hal);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//truncator
	public static String truncate(String string, int length)
	{
		if (string != null && string.length() > length) 
			string = string.substring(0, length);
		return string;  
		
	}

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		try {
			config.load(new FileInputStream("chester.properties"));
		} catch (IOException ioex) {
			System.err.println("Error loading config file: chester.properties");
			System.exit(0);
		}
		PircBotX bot = new PircBotX();
		bot.setAutoNickChange(true);
		bot.setName(config.getProperty("nick", "Chester"));
		bot.setVerbose(true);
		bot.connect(config.getProperty("server", "irc.esper.net"));
		bot.joinChannel(config.getProperty("channel", "#hawkfalcon"));
		bot.getListenerManager().addListener(new Chester());

		;	}
}

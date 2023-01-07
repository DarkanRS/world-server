package com.rs.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.tools.old.WebPage;
import com.rs.utils.ChatGPT;
import com.rs.utils.json.ControllerAdapter;

public class DialogueCHAD {
	
	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());
		
		poopOutDialogue("Olivia");
	}

	public static void poopOutDialogue(String name) throws MalformedURLException {
		WebPage page = new WebPage("https://runescape.wiki/w/Transcript:"+name+"?action=raw");
		try {
			page.load();
		} catch (Exception e) {
			System.out.println("Invalid page: " + name);
			return;
		}
		String transcript = "";
		for (String line: page.getLines())
			transcript += line + "\r\n";
		
		ChatGPT chat = new ChatGPT();
		
		System.out.println("Feeding code...");
		chat.getResponse(FEED_CODE);
		
		System.out.println("Feeding transcript...");
		chat.getResponse(FEED_TRANSCRIPT);
		
		System.out.println("Getting constructor...");
		System.out.println(chat.getResponse("Can you generate me a constructor for this transcript? \r\n\r\n " + transcript));
	}
	
	private static final String FEED_CODE = """
	Here is some example code of a constructor for a Conversation in my video game:
			
			public TzHaarMejJal(Player player, NPC npc) {
				super(player);
				
				addNPC(npc.getId(), HeadE.T_CONFUSED, "You want help TzHaar-Mej-" + player.getDisplayName() + "?");
				addOptions(this, "baseOptions", ops -> {
					ops.add("What is this place?")
						.addPlayer(HeadE.CONFUSED, "What is this place?")
						.addNPC(npc.getId(), HeadE.T_CALM_TALK, "This is the Fight Cave, ThzHaar-Xil made it for practice but many JalYt come here to fight, too. Just enter the cave and make sure you're prepared.")
						.addOptions(ops2 -> {
							ops2.add("Are there any rules?")
								.addPlayer(HeadE.CONFUSED, "Are there any rules?")
								.addNPC(npc.getId(), HeadE.T_LAUGH, "Rules? Survival is the only rule in there.")
								.addOptions(ops3 -> {
									ops3.add("Do I win anything?")
										.addPlayer(HeadE.CONFUSED, "Do I win anything?")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "You ask a lot questions. Might give you TokKul if you last long enough.")
										.addPlayer(HeadE.CONFUSED, "You're still handing out ToKKul as a reward? TzHaar-Mej- Jeh said it was going to be melted down in the sacred lava, to release your dead from their torment.")
										.addNPC(npc.getId(), HeadE.T_ANGRY, "You ask a lot questions. Might give you TokKul if you last long enough.")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "TzHaar do not need currency. Each TzHaar work hard, and does their duty according to their caste. TzHaar ensure they have food and shelter,")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "and all needs are met. JalYt come to TzHaar City, with heads full of gold and wealth and greed.")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "Gold no use to TzHaar. Soft and easily broken, like JalYt. JalYt need rare token to trade. TokKul is memories of dead TzHaar. Trapped. Precious.")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "TokKul is only rare token TzHaar have. Until TzHaar find new token, or JalYt have less greed, must trade in TokKul.");
									
									ops3.add("Sounds good.")
										.addGotoStage("baseOptions", this);
								});
							
							ops2.add("Thanks.");
						});
					
					ops.add("What did you call me?")
						.addPlayer(HeadE.CONFUSED, "What did you call me?")
						.addNPC(npc.getId(), HeadE.T_CONFUSED, "Are you not TzHaar-Mej?")
						.addOptions(ops2 -> {
							ops2.add("Why do you call me 'TzHaar-Mej'?")
								.addPlayer(HeadE.CONFUSED, "Why do you call me 'TzHaar-Mej'?")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "That what you are...you user of mystic powers, no? And you are JalYt no longer. You are TzHaar now.")
								.addPlayer(HeadE.CONFUSED, "Well, yes, I suppose I am...")
								.addNPC(npc.getId(), HeadE.T_LAUGH, "Then you TzHaar-Mej!")
								.addOptions(ops3 -> {
									ops3.add("What are you then?")
										.addPlayer(HeadE.CONFUSED, "What are you then?")
										.addNPC(npc.getId(), HeadE.T_CALM_TALK, "I am TzHaar-Mej, one of the mystics of this city. The TzHaar-Mej guide the TzHaar when change is necessary, and tend to TzHaar eggs to ensure they are hot and healthy.")
										.addNPC(npc.getId(), HeadE.CALM_TALK, "TzHaar-Mej are keepers of knowledge and magic. There are also the mighty TzHaar-Ket who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who craft our homes and tools.");
								
									ops3.add("Thanks for explaining it.");
								});
							
							ops2.add("Yes, I certainly am.")
								.addPlayer(HeadE.CHEERFUL, "Yes, I certainly am.")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "Then it is an honour to speak with you, TzHaar-Mej-" + player.getDisplayName() + ". You great and powerful TzHaar, and defender of our city.");
							
							ops2.add("You must have me confused with another TzHaar.")
								.addPlayer(HeadE.CONFUSED, "You must have me confused with another TzHaar.")
								.addNPC(npc.getId(), HeadE.T_LAUGH, "I heard you are modest, TzHaar-Mej-" + player.getDisplayName() + ". No need. Revel in our praise. You deserve all honour.");
						});
					ops.add("No I'm fine thanks.");
				});
			}
			""";
			
			private static final String FEED_TRANSCRIPT = """
	It matches up with the following transcript:
			
			==Normal dialogue==
			* '''Tzhaar-Mej-Jal:''' You want help, TzHaar-Mej-Player?
			** What is this place?
			*** '''Tzhaar-Mej-Jal:''' This is the Fight Cave. TzHaar-Xil made it for practice, but many JalYt come here to fight, too. Just enter the cave and make sure you're prepared.
			**** Are there any rules?
			***** '''Tzhaar-Mej-Jal:''' Rules? Survival is the only rule in there.
			****** Do I win anything?
			******* '''Tzhaar-Mej-Jal:''' You ask a lot of questions. Might give you TokKul if you last long enough.
			******* '''Player:''' You're still handing out ToKKul as a reward? TzHaar-Mej- Jeh said it was going to be melted down in the sacred lava, to release your dead from their torment.
			******* '''Tzhaar-Mej-Jal:''' TzHaar do not need currency. Each TzHaar work hard, and does their duty according to their caste. TzHaar ensure they have food and shelter, and all needs are met. JalYt come to TzHaar City, with heads full of gold and wealth and greed. Gold no use to TzHaar. Soft and easily broken, like JalYt. JalYt need rare token to trade. TokKul is memories of dead TzHaar. Trapped. Precious. TokKul is only rare token TzHaar have. Until TzHaar find new token, or JalYt have less greed, must trade in TokKul.
			****** Sounds good.
			******* ''(returns to previous options)''
			**** Thanks.
			***** ''(dialogue terminates)''
			** What did you call me?
			*** '''Tzhaar-Mej-Jal:''' Are you not TzHaar-Mej?
			**** Why do you call me 'TzHaar-Mej'?
			***** '''Tzhaar-Mej-Jal:''' That what you are...you user of mystic powers, no? And you are JalYt no longer. You are TzHaar now.
			***** '''Player:''' Well, yes, I suppose I am...
			***** '''Tzhaar-Mej-Jal:''' Then you TzHaar-Mej!
			****** What are you then?
			******* '''Tzhaar-Mej-Jal:''' I am TzHaar-Mej, one of the mystics of this city. The TzHaar-Mej guide the TzHaar when change is necessary, and tend to TzHaar eggs to ensure they are hot and healthy. TzHaar-Mej are keepers of knowledge and magic. There are also the mighty TzHaar-Ket who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who craft our homes and tools.
			******* ''(dialogue terminates)''
			****** Thanks for explaining it.
			******* ''(dialogue terminates)''
			**** Yes, I certainly am.
			***** '''Tzhaar-Mej-Jal:''' Then it is an honour to speak with you, TzHaar-Mej-Player. You great and powerful TzHaar, and defender of our city.
			***** ''(dialogue terminates)''
			**** You must have me confused with another TzHaar.
			***** '''Tzhaar-Mej-Jal:''' I heard you are modest, TzHaar-Mej-Player. No need. Revel in our praise. You deserve all honour.
			***** ''(dialogue terminates)''
			** No, I'm fine. Thanks.
			*** ''(dialogue terminates)''
			""";
}

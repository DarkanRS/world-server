package com.rs.game.player.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class OziachD extends Conversation {
	
	private static final int OZIACH = 747;

	public static NPCClickHandler handleOziachDialogue = new NPCClickHandler(OZIACH) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("trade"))
				ShopsHandler.openShop(e.getPlayer(), "oziach"); 
			else
				e.getPlayer().startConversation(new OziachD(e.getPlayer()));
		}
	};
	
	public OziachD(Player player) {
		super(player);

		addPlayer(HeadE.CALM, "Good day to you.");
		addNPC(747, HeadE.HAPPY_TALKING, "Aye, 'tis a fair day, mighty dragon-slaying friend.");
	    if (player.getQuestManager().isComplete(Quest.DRAGON_SLAYER) && player.getInventory().containsItem(11286))
	    	addNPC(747, HeadE.AMAZED, "Ye've got... Ye've found a draconic visage! Could I look at it?");
	    
		addOptions(new Options() {
			@Override
			public void create() {
				option("Can I buy a rune platebody now, please?", new Dialogue().addPlayer(HeadE.CALM, "Can I buy a rune platebody now, please?").addNext(() -> {
					ShopsHandler.openShop(player, "oziach"); 
				}));
				
			   option("I'm not your friend.", new Dialogue().addPlayer(HeadE.CALM, "I'm not your friend.").addNPC(747, HeadE.FRUSTRATED, "I'm surprised if you're anyone's friend with those kind of manners."));
			   option("Yes, it's a very nice day.", new Dialogue().addPlayer(HeadE.CALM, "Yes, it's a very nice day.").addNPC(747, HeadE.HAPPY_TALKING, "Aye, may the gods walk by yer side. Now leave me alone."));
			   if (!player.getQuestManager().isComplete(Quest.DRAGON_SLAYER))
				   option("Can I have another key to Melzar's Maze?", new Dialogue().addPlayer(HeadE.CALM, "Can I have another key to Melzar's Maze?").addNPC(747, HeadE.CALM_TALK, "It's the Guildmaster in the Champions' Guild who hands those keys out now. Go talk to him. No need to bother me if you don't need armour."));
			
				if (player.getQuestManager().isComplete(Quest.DRAGON_SLAYER) && player.getInventory().containsItem(11286)) {
					   option("Here you go", new Dialogue().addPlayer(HeadE.CALM, "Here you go.")
						   .addNPC(747, HeadE.AMAZED_MILD, "Amazin'! Ye can almost feel it pulsing with draconic power!")
						   .addNPC(747, HeadE.AMAZED_MILD, "Now, if ye want me to, I could attach this to yer anti-dragonbreath shield and make something pretty special.")
						   .addNPC(747, HeadE.AMAZED_MILD, "The shield won't be easy to wield though; ye'll need level 75 Defence.")
						   .addNPC(747, HeadE.AMAZED_MILD, "I'll Charge 1,250,000 coins to construct it. What d'ye say?")
						   .addOption("Select an option", "Yes, please!", "No, thanks.", "That's a bit expensive!")
						   .addNPC(747, HeadE.CALM, "Great lets take a look.")
						   .addNext(
						       (player.getInventory().containsItem(11286) && player.getInventory().containsItem(995, 1250000) && player.getInventory().containsItem(1540) ? 
						       new Dialogue().addItem(11283, "Oziach skillfully forges the shield and visage into a new shield.", () -> { 
								   player.getInventory().deleteItem(11286, 1);
								   player.getInventory().deleteItem(995, 1250000);
								   player.getInventory().deleteItem(1540, 1);
								   player.getInventory().addItem(11283, 1);
						       }) : new Dialogue().addNPC(747, HeadE.CALM_TALK, "Ye seem to be missing some stuff. Come see me when ye have an anti-dragon shield and my payment."))
						   ));
				}
			}
		});
	
		create();
	}

}

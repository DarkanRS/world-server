package com.rs.game.content.quests.holygrail.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class SirGalahadHolyGrailD extends Conversation {
	private static final int NPC = 218;
	public SirGalahadHolyGrailD(Player player) {
		super(player);
		addNPC(NPC, HeadE.CALM_TALK, "Welcome to my home. It's rare for me to have guests! Would you like a cup of tea? I'll just put the kettle on.");
		addSimple("Brother Galahad hangs a kettle over the fire");
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA -> {
				addPlayer(HeadE.HAPPY_TALKING, " I'm looking for Sir Galahad");
				addNPC(NPC, HeadE.CALM_TALK, "I AM Sir Galahad. Although I've retired as a Knight, and now live as a solitary monk. Also, I prefer to be " +
						"known as Brother rather than Sir now.");
				addPlayer(HeadE.HAPPY_TALKING, "I'm on a quest to find the Holy Grail!");
				addNPC(NPC, HeadE.CALM_TALK, "Ah... the Grail... yes... that did fill me with wonder! Oh, that I could have stayed forever! The atmosphere, the food, the people...");
				addPlayer(HeadE.HAPPY_TALKING, "How can I find it?");
				addNPC(NPC, HeadE.CALM_TALK, "Well, I did not find it through looking - though admittedly I looked long and hard - eventually, it found me.");
				addPlayer(HeadE.HAPPY_TALKING, "Interesting. Though, I don't know where to start asking you questions.");
				addNPC(NPC, HeadE.CALM_TALK, "Ah, well you can always have tea...");
				addPlayer(HeadE.SECRETIVE, "Umm....");

			}
			case GO_TO_MCGRUBOR, SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addPlayer(HeadE.HAPPY_TALKING, " I'm looking for Sir Galahad");
				addNPC(NPC, HeadE.CALM_TALK, "I AM Sir Galahad. Although I've retired as a Knight, and now live as a solitary monk. Also, I prefer to be " +
						"known as Brother rather than Sir now.");
				addPlayer(HeadE.HAPPY_TALKING, "I'm on a quest to find the Holy Grail!");
				addNPC(NPC, HeadE.CALM_TALK, "Ah... the Grail... yes... that did fill me with wonder! Oh, that I could have stayed forever! The atmosphere, the food, the people...");
				addPlayer(HeadE.HAPPY_TALKING, "Hmm, actually, yes I seek an item from the realm of the Fisher King. Do you have one?");
				addNPC(NPC, HeadE.CALM_TALK, "Funny you should mention that, but when I left there I took a small cloth from the table as a keepsake.");
				addPlayer(HeadE.HAPPY_TALKING, "I don't suppose I could borrow that? It could come in useful on my quest.");
				addSimple("Galahad reluctantly passes you a small cloth.", ()->{
					player.getInventory().addItem(new Item(15, 1), true);
					if(player.getQuestManager().getStage(Quest.HOLY_GRAIL) == GO_TO_MCGRUBOR)
						player.getQuestManager().setStage(Quest.HOLY_GRAIL, SPEAK_TO_FISHER_KING);
				});
			}
			default -> {
				addPlayer(HeadE.HAPPY_TALKING, "No, no thank you, I will be heading out.");
			}
		}
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new SirGalahadHolyGrailD(e.getPlayer()).getStart()));
}

package com.rs.game.content.quests.familycrest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_AVAN;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_GEM_TRADER;

@PluginEventHandler
public class GemTraderFamilyCrestD extends Conversation {
	private static final int NPC = 540;
	public GemTraderFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case TALK_TO_GEM_TRADER -> {
			addPlayer(HeadE.HAPPY_TALKING, "I'm in search of a man named Avan Fitzharmon.");
			addNPC(NPC, HeadE.CALM_TALK, "Fitzharmon, eh? Hmmm... If I'm not mistaken, that's the family name of a member of the Varrockian nobility.");
			addNPC(NPC, HeadE.CALM_TALK, "You know, I HAVE seen someone of that persuasion around here recently... wearing a 'poncey' yellow cape, he was.");
			addNPC(NPC, HeadE.CALM_TALK, "Came in here all la-di-dah, high and mighty, asking for jewellery made from 'perfect gold' - whatever that is - " +
					"like 'normal' gold just isn't good enough for 'little lord fancy pants' there!");
			addNPC(NPC, HeadE.CALM_TALK, "I told him to head to the desert 'cos I know there's gold out there, in them there sand dunes. And if it's " +
					"not up to his lordship's high standards of 'gold perfection', then...", ()-> {
						player.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_AVAN);
					});
			addNPC(NPC, HeadE.CALM_TALK, "Well, maybe we'll all get lucky and the scorpions will deal with him.");
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Good day to you, traveller. Would you be interested in buying some gems?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Yes, please", new Dialogue()
							.addNext(()->{
								ShopsHandler.openShop(player, "gem_trader");
							})
							);
					option("No, thank you.", new Dialogue());
				}
			});
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new GemTraderFamilyCrestD(e.getPlayer()).getStart()));
}

package com.rs.game.player.quests.handlers.familycrest.dialogues;

import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_AVAN;
import static com.rs.game.player.quests.handlers.familycrest.FamilyCrest.TALK_TO_GEM_TRADER;

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
public class GemTraderFamilyCrestD extends Conversation {
	private static final int NPC = 540;
	public GemTraderFamilyCrestD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case TALK_TO_GEM_TRADER -> {
			addPlayer(HeadE.HAPPY_TALKING, "I'm in search of a man named Avan Fitzharmon.");
			addNPC(NPC, HeadE.CALM_TALK, "Fitzharmon, eh? Hmmm... If I'm not mistaken, that's the family name of a member of the Varrockian nobility.");
			addNPC(NPC, HeadE.CALM_TALK, "You know, I HAVE seen someone of that persuasion around here recently... wearing a 'poncey' yellow cape, he was.");
			addNPC(NPC, HeadE.CALM_TALK, "Came in here all la-di-dah, high and mighty, asking for jewellery made from 'perfect gold' - whatever that is - " +
					"like 'normal' gold just isn't good enough for 'little lord fancy pants' there!");
			addNPC(NPC, HeadE.CALM_TALK, "I told him to head to the desert 'cos I know there's gold out there, in them there sand dunes. And if it's " +
					"not up to his lordship's high standards of 'gold perfection', then...", ()-> {
						p.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_AVAN);
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
								ShopsHandler.openShop(p, "gem_trader");
							})
							);
					option("No, thank you.", new Dialogue());
				}
			});
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(NPC) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new GemTraderFamilyCrestD(e.getPlayer()).getStart());
		}
	};
}

package com.rs.game.content.quests.familycrest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.familycrest.FamilyCrest.GIVE_AVAN_JEWLERY;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_BOOT;

@PluginEventHandler
public class BootFamilyCrestD extends Conversation {
	private static final int NPC = 665;
	public BootFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case TALK_TO_BOOT, GIVE_AVAN_JEWLERY -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello tall person");
			addPlayer(HeadE.HAPPY_TALKING, "Hello. I'm in search of very high quality gold");
			addNPC(NPC, HeadE.CALM_TALK, "High quality gold eh? Hmmm... Well, the very best quality gold that I know of can be found in an underground ruin near Witchaven.");
			addNPC(NPC, HeadE.CALM_TALK, "I don't believe it's exactly easy to get to though...", ()->{
				player.getQuestManager().setStage(Quest.FAMILY_CREST, GIVE_AVAN_JEWLERY);
			});
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello tall person.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Hello short person.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Hello short person.")
							.addNPC(NPC, HeadE.FRUSTRATED, "...")
							);
					option("Why are you called boot?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Why are you called boot?")
							.addNPC(NPC, HeadE.CALM_TALK, "I'm called Boot, because when I was very young, I used to sleep, in a large boot.")
							.addPlayer(HeadE.HAPPY_TALKING, "Yeah, great, I didn't want your life story.")
							);
				}
			});

		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new BootFamilyCrestD(e.getPlayer()).getStart()));
}

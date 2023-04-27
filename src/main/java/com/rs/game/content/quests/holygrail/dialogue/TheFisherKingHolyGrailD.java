package com.rs.game.content.quests.holygrail.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.holygrail.HolyGrail.*;

@PluginEventHandler
public class TheFisherKingHolyGrailD extends Conversation {
	private static final int NPC = 220;
	public TheFisherKingHolyGrailD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case SPEAK_TO_FISHER_KING, SPEAK_TO_PERCIVAL, GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Ah! You got inside at last! You spent all that time fumbling around outside. I thought you'd never make it here.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("How did you know what I have been doing?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "How did you know what I have been doing?")
								.addNPC(NPC, HeadE.CALM_TALK, "Oh, I can see what is happening in my realm. I have sent clues to help you get here, such as the fisherman, and the crone.")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you mind if I have a look around?")
								.addNPC(NPC, HeadE.CALM_TALK, "No, not at all. Please, be my guest.")

						);
						option("I seek the Holy Grail", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I seek the Holy Grail")
								.addNPC(NPC, HeadE.CALM_TALK, "Ah excellent. A knight come to seek the Holy Grail. Maybe our land can be restored to its" +
										" former glory. At the moment the Grail cannot be removed from the castle.")
								.addNPC(NPC, HeadE.CALM_TALK, "Legend has it a questing knight will one day work out how to restore our land; then he will claim the Grail as his prize.")
								.addPlayer(HeadE.HAPPY_TALKING, "Any ideas how I can restore the land?")
								.addNPC(NPC, HeadE.CALM_TALK, "None at all.")
						);
						option("You don't look too well.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "You don't look too well.")
								.addNPC(NPC, HeadE.CALM_TALK, "Nope, I don't feel so good either. I fear my life is running short... Alas, my son and heir is not here. I am waiting for my son to return to this castle.")
								.addNPC(NPC, HeadE.CALM_TALK, "If you could find my son, that would be a great weight off my shoulders.")
								.addPlayer(HeadE.HAPPY_TALKING, "Who is your son?")
								.addNPC(NPC, HeadE.CALM_TALK, "He is known as Percival. I believe he is a knight of the round table.")
								.addPlayer(HeadE.HAPPY_TALKING, "I shall go and see if I can find him.", ()->{
									player.getQuestManager().setStage(Quest.HOLY_GRAIL, SPEAK_TO_PERCIVAL);
								})
						);
					}
				});

			}
			default->{
				addNPC(NPC, HeadE.CALM_TALK, "Hello adventurer!");
				addPlayer(HeadE.HAPPY_TALKING, "Hi...");
			}
		}
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new TheFisherKingHolyGrailD(e.getPlayer()).getStart()));
}

package com.rs.game.content.quests.merlinscrystal;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.BREAK_MERLIN_CRYSTAL;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ThrantaxMerlinsCrystalD extends Conversation {
	final int NPC = 238;

	public ThrantaxMerlinsCrystalD(Player player) {
		super(player);
		addSimple("Suddenly a mighty spirit appears!");
		addPlayer(HeadE.HAPPY_TALKING, "Now what were those magic words again?");
		addNext(()->{player.startConversation(new ThrantaxMerlinsCrystalD(player, true).getStart());});
	}


	public ThrantaxMerlinsCrystalD(Player player, boolean filler) {
		super(player);
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Snarthtrick Candanto Termon", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthtrick... Candanto... Termon!")
						.addPlayer(HeadE.HAPPY_TALKING, "No, that wasn't right")
						.addNext(()->{player.startConversation(new ThrantaxMerlinsCrystalD(player, true).getStart());}));
				option("Snarthon Candtrick Termanto", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthon... Candtrick... Termanto!")
						.addNPC(NPC, HeadE.CALM_TALK, "GRAAAAAAGH! Thou hast me in thine control. So that I mayst return from whence I am, I must grant " +
								"thee a boon. What dost thou wish of me?")
						.addPlayer(HeadE.HAPPY_TALKING, "I wish to free Merlin from his giant crystal!")
						.addNPC(NPC, HeadE.CALM_TALK, "GRAAAAAAGH! The deed is done. Thou mayst now shatter Merlins' crystal with Excalibur and I can " +
								"once more rest. Begone! And leave me once more in peace.")
						.addNext(()->{
							player.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, BREAK_MERLIN_CRYSTAL);
							player.getControllerManager().forceStop();
							player.unlock();
						}));

				option("Snarthanto Candon Termtrick", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthanto... Candon... Termtrick!")
						.addPlayer(HeadE.HAPPY_TALKING, "No, that wasn't right")
						.addNext(()->{player.startConversation(new ThrantaxMerlinsCrystalD(player, true).getStart());}));
			}
		});
	}

}

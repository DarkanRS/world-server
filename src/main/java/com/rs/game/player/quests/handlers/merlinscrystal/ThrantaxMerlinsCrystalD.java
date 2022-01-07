package com.rs.game.player.quests.handlers.merlinscrystal;

import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.BREAK_MERLIN_CRYSTAL;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ThrantaxMerlinsCrystalD extends Conversation {
	final int NPC = 238;

	public ThrantaxMerlinsCrystalD(Player p) {
		super(p);
		addSimple("Suddenly a mighty spirit appears!");
		addPlayer(HeadE.HAPPY_TALKING, "Now what were those magic words again?");
		addNext(()->{p.startConversation(new ThrantaxMerlinsCrystalD(p, true).getStart());});
	}


	public ThrantaxMerlinsCrystalD(Player p, boolean filler) {
		super(p);
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Snarthtrick Candanto Termon", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthtrick... Candanto... Termon!")
						.addPlayer(HeadE.HAPPY_TALKING, "No, that wasn't right")
						.addNext(()->{p.startConversation(new ThrantaxMerlinsCrystalD(p, true).getStart());}));
				option("Snarthon Candtrick Termanto", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthon... Candtrick... Termanto!")
						.addNPC(NPC, HeadE.CALM_TALK, "GRAAAAAAGH! Thou hast me in thine control. So that I mayst return from whence I am, I must grant " +
								"thee a boon. What dost thou wish of me?")
						.addPlayer(HeadE.HAPPY_TALKING, "I wish to free Merlin from his giant crystal!")
						.addNPC(NPC, HeadE.CALM_TALK, "GRAAAAAAGH! The deed is done. Thou mayst now shatter Merlins' crystal with Excalibur and I can " +
								"once more rest. Begone! And leave me once more in peace.")
						.addNext(()->{
							p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, BREAK_MERLIN_CRYSTAL);
							p.getControllerManager().forceStop();
							p.unlock();
						}));

				option("Snarthanto Candon Termtrick", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Snarthanto... Candon... Termtrick!")
						.addPlayer(HeadE.HAPPY_TALKING, "No, that wasn't right")
						.addNext(()->{p.startConversation(new ThrantaxMerlinsCrystalD(p, true).getStart());}));
			}
		});
	}

}

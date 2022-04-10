package com.rs.game.content.skills.summoning;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Interactions {
	private static boolean checkIsOwner(Player player, Familiar familiar) {
		if(familiar.getOwner() == player)
			return true;
		player.sendMessage("This isn't your familiar");
		return false;
	}

    public static NPCClickHandler handleSpiritWolf = new NPCClickHandler(new Object[]{6829}) {
        @Override
        public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				if(e.getPlayer().getSkills().getLevelForXp(Skills.SUMMONING) >= 11) {
					int NPC = e.getNPCId();
					if(e.getPlayer().getInventory().containsOneItem(526, 530, 532, 528, 534, 536, 2859, 2530, 3125, 4834, 4832, 6729, 18830, 18832, 6812)) {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_EXPLAIN, "Throw the bone! I want to chase it!")
								.addPlayer(HeadE.HAPPY_TALKING, "I can't just throw bones away - I need them to train my Prayer!")
						);
						return;
					}
					switch(Utils.random(0, 4)) {
						case 0 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_EXPLAIN, "What are you doing?")
									.addPlayer(HeadE.HAPPY_TALKING, "Oh, just some...biped things. I'm sure it would bore you.")
							);
						}
						case 1 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_SHOOK, "Danger!")
									.addPlayer(HeadE.HAPPY_TALKING, "Where?!")
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "False alarm...")
							);
						}
						case 2 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_CALM_TALK, "I smell something good! Hunting time!")
									.addPlayer(HeadE.HAPPY_TALKING, "We can go hunting in a moment. I just have to take care of something first.")
							);
						}
						case 3 -> {
							e.getPlayer().startConversation(new Dialogue()
									.addNPC(NPC, HeadE.CAT_EXPLAIN, "When am I going to get to chase something?")
									.addPlayer(HeadE.HAPPY_TALKING, "Oh I'm sure we'll find something for you in a bit.")
							);
						}
					}
					return;
				}
				e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.CAT_EXPLAIN, "Whurf?"));
			}
        }
    };

	public static NPCClickHandler handleDreadFowl = new NPCClickHandler(new Object[]{6825}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				int NPC = e.getNPCId();
				switch(Utils.random(0, 3)) {
					case 0 -> {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Attack! Fight! Annihilate!")
								.addPlayer(HeadE.HAPPY_TALKING, "It always worries me when you're so happy saying that.")
						);
					}
					case 1 -> {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "Can it be fightin' time, please?")
								.addPlayer(HeadE.HAPPY_TALKING, "Look I'll find something for you to fight, just give me a second.")
						);
					}
					case 2 -> {
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(NPC, HeadE.CAT_CALM_TALK2, "I want to fight something.")
								.addPlayer(HeadE.HAPPY_TALKING, "I'll find something for you in a minute - just be patient.")
						);
					}
				}
			}
		}
	};

	public static NPCClickHandler handleSpiritSpider = new NPCClickHandler(new Object[]{6841}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPC() instanceof Familiar familiar && checkIsOwner(e.getPlayer(), familiar)) {
				e.getPlayer().startConversation(new Dialogue());
			}
		}
	};


}

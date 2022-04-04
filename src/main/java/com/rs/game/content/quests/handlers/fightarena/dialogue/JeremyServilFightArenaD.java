package com.rs.game.content.quests.handlers.fightarena.dialogue;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

import static com.rs.game.content.quests.handlers.fightarena.FightArena.*;

@PluginEventHandler
public class JeremyServilFightArenaD extends Conversation {
	private static final int NPC = 265;
	public JeremyServilFightArenaD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.FIGHT_ARENA)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hello...");
				addPlayer(HeadE.HAPPY_TALKING, "Hello...");
			}
			case FREE_JEREMY -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Please, " + p.getPronoun("sir", "madam") + ", don't hurt me.");
				addSimple("You give Sammy a friendly wink.");
				addPlayer(HeadE.HAPPY_TALKING, "Shhh. I am in disguise; I'm here to help you and your father escape, but I need your help. Do you know where " +
						"they keep the keys?");
				addNPC(NPC, HeadE.CALM_TALK, "The guard always keeps hold of them.");
				addPlayer(HeadE.HAPPY_TALKING, "Which guard?");
				addNPC(NPC, HeadE.CALM_TALK, "The bald, fat, lazy guard with a goatee. He's the one who usually locks up.");
				addPlayer(HeadE.HAPPY_TALKING, "Right, I'll look for him. Don't lose heart – I'll be back.", ()->{
					p.getQuestManager().setStage(Quest.FIGHT_ARENA, GET_JAIL_KEYS);
				});
			}
			case GET_JAIL_KEYS -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hurry, I don't know what they are going to do with me next.");
			}
			case QUEST_COMPLETE ->  {

			}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new JeremyServilFightArenaD(e.getPlayer()).getStart());
		}
	};

	public static NPCInteractionDistanceHandler jeremyJailDistance = new NPCInteractionDistanceHandler(NPC) {
		@Override
		public int getDistance(Player player, com.rs.game.model.entity.npc.NPC npc) {
			return 1;
		}
	};
}

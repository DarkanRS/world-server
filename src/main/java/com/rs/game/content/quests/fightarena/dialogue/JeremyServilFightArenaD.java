package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

import static com.rs.game.content.quests.fightarena.FightArena.*;

@PluginEventHandler
public class JeremyServilFightArenaD extends Conversation {
	private static final int NPC = 265;
	public JeremyServilFightArenaD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FIGHT_ARENA)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CHILD_CRYING, "Hello...");
				addPlayer(HeadE.HAPPY_TALKING, "Hello...");
			}
			case FREE_JEREMY -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CHILD_CRYING, "Please, " + player.getPronoun("sir", "madam") + ", don't hurt me.");
				addSimple("You give Sammy a friendly wink.");
				addPlayer(HeadE.HAPPY_TALKING, "Shhh. I am in disguise; I'm here to help you and your father escape, but I need your help. Do you know where " +
						"they keep the keys?");
				addNPC(NPC, HeadE.CHILD_CALM_TALK, "The guard always keeps hold of them.");
				addPlayer(HeadE.HAPPY_TALKING, "Which guard?");
				addNPC(NPC, HeadE.CHILD_ANGRY, "The bald, fat, lazy guard with a goatee. He's the one who usually locks up.");
				addPlayer(HeadE.HAPPY_TALKING, "Right, I'll look for him. Don't lose heart - I'll be back.", ()->{
					player.getQuestManager().setStage(Quest.FIGHT_ARENA, GET_JAIL_KEYS);
				});
			}
			case GET_JAIL_KEYS -> {
				addNPC(NPC, HeadE.CHILD_CRYING, "Hurry, I don't know what they are going to do with me next.");
			}
			case RETURN_TO_LADY_SERVIL, QUEST_COMPLETE ->  {
				addNPC(7533, HeadE.CHILD_AWE, "That was amazing " + this.player.getDisplayName() + "!");
				addPlayer(HeadE.HAPPY_TALKING, "Yea that was a trip...");
			}
		}
		create();
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC, 7533}, e -> e.getPlayer().startConversation(new JeremyServilFightArenaD(e.getPlayer())));

	public static NPCInteractionDistanceHandler jeremyJailDistance = new NPCInteractionDistanceHandler(NPC, (player, npc) -> 2);
}

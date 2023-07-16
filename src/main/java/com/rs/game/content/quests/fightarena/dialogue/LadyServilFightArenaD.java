package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.fightarena.FightArena.*;

@PluginEventHandler
public class LadyServilFightArenaD extends Conversation {
	private static final int NPC = 258;
	public LadyServilFightArenaD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FIGHT_ARENA)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hi there. It looks like your cart has broken down. Do you need any help?");
				addNPC(NPC, HeadE.SAD_SNIFFLE, "*sob* Oh I don't care about the cart. *sob* If only Justin were here, he'd fix it. My poor Justin! *sob* " +
						"*sniffle* And Jeremy! *sob* He's only a child.");
				addNPC(NPC, HeadE.SAD_CRYING, "*sob* How could anyone be so inhumane? *wail* My poor boy! *sob* I've got to find them. *sob*");
				addPlayer(HeadE.HAPPY_TALKING, "I hope you can find them too.");
				addPlayer(HeadE.HAPPY_TALKING, "But, umm, can I help you?");
				addNPC(NPC, HeadE.SAD_SNIFFLE, "Would you, please? I'm Lady Servil, and my husband is Sir Servil. We were travelling north with our son " +
						"Jeremy when we were ambushed by General Khazard's men.");
				addPlayer(HeadE.SECRETIVE, "General Khazard?");
				addNPC(NPC, HeadE.SAD_MILD_LOOK_DOWN, "He has been victimising my family ever since we declined to hand over our lands. Now he's kidnapped my husband " +
						"and son to fight in his Fight Arena to the south of here.");
				addNPC(NPC, HeadE.SAD_MILD, "I hate to think what he'll do to them. They're not warriors and won't survive against the creatures of the arena! " +
						"The General is a sick and twisted individual.");
				addOptions("Start Fight Arena?", new Options() {
					@Override
					public void create() {
						option("Yes", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'll try my best to return your family.", ()->{
								player.getQuestManager().setStage(Quest.FIGHT_ARENA, FREE_JEREMY);
								player.getVars().setVarBit(5626, 1);
							})
							.addNPC(NPC, HeadE.SAD, "Please do. My family can reward you for your troubles. I'll be waiting here for you")
						);
						option("No", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Welp, good look with that!")
							.addNPC(NPC, HeadE.SAD_EXTREME, "*Wails *Sobs")
						);
					}
				});


			}
			case FREE_JEREMY, GET_JAIL_KEYS -> {
				addNPC(NPC, HeadE.CALM_TALK, "Please find my son and husband!");
				addPlayer(HeadE.HAPPY_TALKING, "I will m'am.");
			}
			case RETURN_TO_LADY_SERVIL ->  {
				addNPC(NPC, HeadE.CALM_TALK, "You're alive!");
				addPlayer(HeadE.HAPPY_TALKING, "Yes.");
				addNPC(NPC, HeadE.CALM_TALK, "I thought Khazard's men had taken you. My son and husband are safe and sound, as you can see! Without you they " +
						"would certainly be dead.");
				addNPC(NPC, HeadE.CALM_TALK, "I am truly grateful for your service. All I can offer in return is a small amount of material wealth. " +
						"Please take these coins as a sign of my gratitude.");
				addNext(()->{player.getQuestManager().completeQuest(Quest.FIGHT_ARENA);});
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Thank you " + this.player.getDisplayName() + "! My family is alive and safe because of you.");
			}
		}
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new LadyServilFightArenaD(e.getPlayer()).getStart()));
}

package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

import static com.rs.game.content.quests.fightarena.FightArena.*;

@PluginEventHandler
public class LazyGuardFightArenaD extends Conversation {
	private static final int NPC = 7550;
	public LazyGuardFightArenaD(Player player, final NPC npc) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FIGHT_ARENA)) {
			case NOT_STARTED, FREE_JEREMY -> {
				addNPC(NPC, HeadE.CALM_TALK, "Whatchu want?");
				addPlayer(HeadE.HAPPY_TALKING, "Nothing.");
			}
			case GET_JAIL_KEYS -> {
				if(player.getInventory().containsItem(76)) {
					addNPC(NPC, HeadE.CALM_TALK, "Phew, Almost fell asleep there");
					addPlayer(HeadE.SECRETIVE, "Yea, you almost did...");
					addNPC(NPC, HeadE.CALM_TALK, "Blimey I lost my keys too. Looks like I will have to make a new set again.");
					addPlayer(HeadE.SECRETIVE, "Yup, looks like you will...");
					return;
				}

				if(player.getInventory().containsItem(77)) {
					addPlayer(HeadE.HAPPY_TALKING, "Hello again.");
					addNPC(NPC, HeadE.CALM_TALK, "Bored, bored, bored. You'd think suffering and slaughter would be more entertaining. The slaves hardly make any effort to make the fights fun. Selfish, the lot of them");
					addPlayer(HeadE.HAPPY_TALKING, "Do you still fancy a drink? I just happen to have a bottle of the good stuff on me.");
					addNPC(NPC, HeadE.CALM_TALK, "No, no I really shouldn't. Not while I'm on duty.");
					addPlayer(HeadE.HAPPY_TALKING, "Go on... you've worked hard all day. A quick sip will be just enough to keep you going.");
					addNPC(NPC, HeadE.CALM_TALK, "Well, I have had a hard day. Oh, go on.");
					addSimple("You hand a bottle of Khali brew to the guard. He takes a mouthful of the drink.");
					addNPC(NPC, HeadE.CALM_TALK, "Blimey! This stuff is pretty good. It isn't Khali brew, is it?");
					addPlayer(HeadE.HAPPY_TALKING, "No, of course not. Don't worry, it's just a quick pick-me-up. You'll be fine.");
					addSimple("The guard quickly drinks half of the bottle and sways slightly.", () -> {
						player.lock(9);
						player.getInventory().deleteItem(77, 1);
						player.faceTile(Tile.of(2617, 3144, 0));
						player.getVars().setVarBit(5627, 1);
						WorldTasks.schedule(new WorldTask() {
							int tick;
							@Override
							public void run() {
								if (tick == 5)
									npc.setNextAnimation(new Animation(11669));
								if (tick == 9) {
									npc.setNextAnimation(new Animation(11670));
									player.getVars().setVarBit(5627, 2);
								}
								if(tick == 10)
									stop();
								tick++;
							}
						}, 0, 1);
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "Long live General Khazard!");
				addNPC(NPC, HeadE.CALM_TALK, "Erm, yes... quite right. Have you come to laugh at the fighting slaves? I used to really enjoy it but, after a " +
						"while, they became quite boring. ");
				addNPC(NPC, HeadE.CALM_TALK, "To be honest, now I've locked them up, all I want is a decent drink. Mind you, too much Khali brew and I fall asleep.");
				addPlayer(HeadE.HAPPY_TALKING, "Hard stuff that Khali brew. Tastes great though, doesn't it?");
				addNPC(NPC, HeadE.CALM_TALK, "Well, yes. Between you and me, I find it hard to turn down a nice cold bottle of Khali brew from the bar");
				addNPC(NPC, HeadE.CALM_TALK, "Mmmm, yes. Ahem! Well, yes, enough chit-chat - back to work, guard.");
				addPlayer(HeadE.HAPPY_TALKING, "Yessir!");
			}
			case QUEST_COMPLETE ->  {

			}
		}
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC, 7525, 7526, 7527}, e -> {
    	e.getNPC().faceSouth();
        if(e.getOption().equalsIgnoreCase("Steal-keys")) {
			if(e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().startConversation(new Dialogue().addSimple("You grab the keys"));
				e.getPlayer().getInventory().addItem(76, 1);
				e.getPlayer().getVars().setVarBit(5627, 3);
				return;
			}
			e.getPlayer().sendMessage("You need free space to grab his keys...");
		}
		if(e.getPlayer().getVars().getVarBit(5627) > 0)
			return;

		e.getPlayer().startConversation(new LazyGuardFightArenaD(e.getPlayer(), e.getNPC()).getStart());
    });

	public static NPCInteractionDistanceHandler HandleDistance = new NPCInteractionDistanceHandler(new Object[] { NPC, 7525, 7526, 7527 }, (player, npc) -> 1);
}

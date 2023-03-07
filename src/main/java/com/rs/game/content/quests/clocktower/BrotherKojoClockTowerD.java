package com.rs.game.content.quests.clocktower;

import static com.rs.game.content.quests.clocktower.ClockTower.NOT_STARTED;
import static com.rs.game.content.quests.clocktower.ClockTower.QUEST_COMPLETE;
import static com.rs.game.content.quests.clocktower.ClockTower.REPAIR_CLOCK_TOWER;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BrotherKojoClockTowerD extends Conversation {
	private static final int NPC = 223;
	public BrotherKojoClockTowerD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.CLOCK_TOWER)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello monk.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello adventurer. My name is Brother Kojo. Do you happen to know the time?");
				addPlayer(HeadE.HAPPY_TALKING, "No, sorry, I don't.");
				addNPC(NPC, HeadE.CALM_TALK, "Exactly! This clock tower has recently broken down, and without it nobody can tell the correct time. I must fix it before the town people become too angry!");
				addNPC(NPC, HeadE.CALM_TALK, "I don't suppose you could assist me in the repairs? I'll pay you for your help.");
				addOptions("Start Clock Tower?", new Options() {
					@Override
					public void create() {
						option("Yes", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "OK old monk, what can I do?", ()->{
									player.getQuestManager().setStage(Quest.CLOCK_TOWER, REPAIR_CLOCK_TOWER);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "Oh, thank you kind " + player.getPronoun("sir", "madam") + "! In the cellar below, you'll find four cogs. They're too heavy for me, but you should be able to carry them one at a time.")
								.addNPC(NPC, HeadE.CALM_TALK, "I know one goes on each floor... but I can't exactly remember which goes where specifically. Oh well, I'm sure you can figure it out fairly easily.")
								.addPlayer(HeadE.HAPPY_TALKING, "Well, I'll do my best.")
								.addNPC(NPC, HeadE.CALM_TALK, "Thank you again! And remember to be careful, the cellar is full of strange beasts.")

						);
						option("No", new Dialogue());
					}
				});
			}
			case REPAIR_CLOCK_TOWER-> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello again.");
				if(ClockTower.allCogsFinished(player)) {
					addNPC(NPC, HeadE.CALM_TALK, "Hello.");
					addPlayer(HeadE.HAPPY_TALKING, "I have replaced all the cogs!");
					addNPC(NPC, HeadE.CALM_TALK, "Really...? Wait, listen! Well done, well done! Yes yes yes, you've done it! You ARE clever!");
					addNPC(NPC, HeadE.CALM_TALK, "The townsfolk will be able to know the correct time now! Thank you so much for all of your help! And as promised, here is your reward!");
					addNext(()->{
						player.getQuestManager().completeQuest(Quest.CLOCK_TOWER);
					});
					return;
				}
				addNPC(NPC, HeadE.CALM_TALK, "Oh hello, are you having trouble? The cogs are in four rooms below us. Place one cog on a pole on each of the four tower levels.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Can I have a hint?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can I have a hint?")
								.addNPC(NPC, HeadE.CALM_TALK, "If I knew where the cogs were I wouldn't be asking you. But they are in the basement somewhere.")
						);
						option("I'll carry on looking.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'll carry on looking.")
						);
					}
				});
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "Hello again Brother Kojo.");
				addNPC(NPC, HeadE.CALM_TALK, "Oh hello there traveller. You've done a grand job with the clock. It's just like new.");
			}
		}
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e ->  e.getPlayer().startConversation(new BrotherKojoClockTowerD(e.getPlayer()).getStart()));
}

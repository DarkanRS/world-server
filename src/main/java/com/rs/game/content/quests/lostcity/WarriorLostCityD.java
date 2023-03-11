package com.rs.game.content.quests.lostcity;

import static com.rs.game.content.quests.lostcity.LostCity.CHOP_DRAMEN_TREE;
import static com.rs.game.content.quests.lostcity.LostCity.FIND_ZANARIS;
import static com.rs.game.content.quests.lostcity.LostCity.NOT_STARTED;
import static com.rs.game.content.quests.lostcity.LostCity.QUEST_COMPLETE;
import static com.rs.game.content.quests.lostcity.LostCity.TALK_TO_LEPRAUCAN;
import static com.rs.game.content.quests.lostcity.LostCity.WARRIOR;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WarriorLostCityD extends Conversation {
	private final int LOOKINGFORZANARIS = 0;
	private final int LOOKFORLEPRECAUN = 1;
	public WarriorLostCityD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.LOST_CITY)) {
		case NOT_STARTED -> {
			addNPC(WARRIOR, HeadE.CALM_TALK, "Hello there, traveler");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Why are you camped out here?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Why are you camped out here?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "We're looking for Zanaris...GAH! I mean we're not here for any particular reason at all")
							.addNext(()->{player.startConversation(new WarriorLostCityD(player, LOOKINGFORZANARIS).getStart());})
							);
					option("Do you know any good adventurers I can go on?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Do you know any good adventurers I can go on?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "Well, we're on an adventure right now. Mind you, this is OUR adventure and we don't want to" +
									" share it - find your own!")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Please tell me", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Please tell me?")
											.addNPC(WARRIOR, HeadE.CALM_TALK, "No.")
											.addPlayer(HeadE.HAPPY_TALKING, "Please?")
											.addNPC(WARRIOR, HeadE.FRUSTRATED, "No!")
											.addPlayer(HeadE.AMAZED_MILD, "PLEEEEEEEEEEEEEEEEEEEEEEASE???")
											.addNPC(WARRIOR, HeadE.VERY_FRUSTRATED, "NO!")
											);
									option("I don't think you've found a good adventure at all!", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I don't think you've found a good adventure at all!")
											.addNPC(WARRIOR, HeadE.CALM_TALK, "Hah! Adventurers of our caliber don't just hang around in forests for fun, whelp!")
											.addPlayer(HeadE.HAPPY_TALKING, "Oh really? Why are you camped out here?")
											.addNPC(WARRIOR, HeadE.CALM_TALK, "We're looking for Zanaris...GAH! I mean we're not here for any particular reason at all")
											.addNext(()->{player.startConversation(new WarriorLostCityD(player, LOOKINGFORZANARIS).getStart());})
											);
								}
							})


							);
				}
			});

		}
		case TALK_TO_LEPRAUCAN -> {
			addPlayer(HeadE.HAPPY_TALKING, "So let me get this straight. I need to search the trees around here for a leprechaun. Then, when I find him, " +
					"he will tell me where this 'Zanaris' is?");
			addNPC(WARRIOR, HeadE.CALM_TALK, "What? How did you know that? Uh... I mean, no, no you're very wrong. Very wrong, and not right at all, and " +
					"I definitely didn't tell you about that at all.");
		}
		case CHOP_DRAMEN_TREE, FIND_ZANARIS ->  {
			addPlayer(HeadE.HAPPY_TALKING, "Have you found anything yet?");
			addNPC(WARRIOR, HeadE.CALM_TALK, "We're still searching for Zanaris... GAH! I mean we're not doing anything here at all.");
			addPlayer(HeadE.HAPPY_TALKING, "I haven't found it yet either.");
		}

		case QUEST_COMPLETE ->  {
			addPlayer(HeadE.HAPPY_TALKING, "Hey, thanks for all the information. It REALLY helped me out in finding the lost city of Zanaris and all.");
			addNPC(WARRIOR, HeadE.SCARED, "Oh, please don't say that anymore! If the rest of my party knew I'd helped you, they'd probably throw me out and make me walk home by myself! ");
			addNPC(WARRIOR, HeadE.HAPPY_TALKING, "So anyway, what have you found out? Where is the fabled Zanaris? Is it all the legends say it is?");
			addPlayer(HeadE.SECRETIVE, "You know...I think I'll keep that to myself.");

		}
		}
	}

	public WarriorLostCityD(Player p, int id) {
		super(p);
		switch(id) {
		case LOOKINGFORZANARIS -> {
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Who's Zanaris?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Who's Zanaris?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "Ahahahaha! Zanaris isn't a person! It's a magical hidden city filled with treasures and " +
									"rich... uh, nothing, it's nothing.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("If it's hidden, how are you planning to find it?", new Dialogue()
											.addNext(()->{p.startConversation(new WarriorLostCityD(p, LOOKFORLEPRECAUN).getStart());})
											);
									option("There's no such thing", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "There's no such thing!")
											.addNPC(WARRIOR, HeadE.CALM_TALK, "When we've found Zanaris, you'll... GAH! I mean, we're not here for any " +
													"particular reason at all.")
											.addNext(()->{p.startConversation(new WarriorLostCityD(p, LOOKINGFORZANARIS).getStart());})
											);
								}
							})
							);
					option("What's Zanaris?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What's Zanaris?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "I don't think we want other people competing with us to find it. Forget I said anything.")
							);
					option("What makes you think it's out here?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What makes you think it's out here?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "Don't you know of the legends that tell of the magical city, hidden in the swam... " +
									"Uh, no, you're right, we're wasting our time here.")
							.addPlayer(HeadE.HAPPY_TALKING, "If it's hidden, how are you planning to find it?")
							.addNext(()->{p.startConversation(new WarriorLostCityD(p, LOOKFORLEPRECAUN).getStart());})
							);
				}
			});
		}
		case LOOKFORLEPRECAUN -> {
			addPlayer(HeadE.HAPPY_TALKING, "If it's hidden, how are you planning to find it?");
			addNPC(WARRIOR, HeadE.CALM_TALK, "Well, we don't want to tell anyone else about that, because we don't want anyone else sharing in all the " +
					"glory and treasure.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Please tell me", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Please tell me?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "No.")
							.addPlayer(HeadE.HAPPY_TALKING, "Please?")
							.addNPC(WARRIOR, HeadE.FRUSTRATED, "No!")
							.addPlayer(HeadE.AMAZED_MILD, "PLEEEEEEEEEEEEEEEEEEEEEEASE???")
							.addNPC(WARRIOR, HeadE.VERY_FRUSTRATED, "NO!"));
					option("Looks like you don't know either", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Well, it looks to me like YOU don't know EITHER seeing as you're all just sat around here.")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "Of course we know! We just haven't figured out which tree the stupid leprechaun's hiding in " +
									"yet! GAH! I didn't mean to tell you that! Look, just forget I said anything okay?")
							.addPlayer(HeadE.HAPPY_TALKING, "So a leprechaun knows where Zanaris is, eh?")
							.addNPC(WARRIOR, HeadE.CALM_TALK, "Ye... uh, no. No, not at all. And even if he did - which he doesn't - he DEFINETLY ISN'T " +
									"hiding in some tree around here. Nope, definitely not. Honestly.")
							.addOptions("Start Lost City?", new Options() {
								@Override
								public void create() {
									if(p.getSkills().getLevel(Constants.CRAFTING) >= 31 && p.getSkills().getLevel(Constants.WOODCUTTING) >= 36)
										option("Yes", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Thanks for the help!", () -> {
													p.getQuestManager().setStage(Quest.LOST_CITY, TALK_TO_LEPRAUCAN);
												})
												.addNPC(WARRIOR, HeadE.CALM_TALK, "Help, What help? I didn't help! Please don't say I did, I'll get in trouble!")
												);
									else
										option("Yes", new Dialogue()
												.addSimple("You don't have the skill levels to start this quest...", () ->{
													if(p.getSkills().getLevel(Constants.CRAFTING) < 31)
														p.sendMessage("You need 31 crafting.");
													if(p.getSkills().getLevel(Constants.WOODCUTTING) < 36)
														p.sendMessage("You need 36 woodcutting.");
												})
												);

									option("No", new Dialogue());
								}
							})
							);
				}
			});


		}

		}
	}

	public static NPCClickHandler handleWarriorDialogue = new NPCClickHandler(new Object[] { WARRIOR }, e -> e.getPlayer().startConversation(new WarriorLostCityD(e.getPlayer()).getStart()));
}

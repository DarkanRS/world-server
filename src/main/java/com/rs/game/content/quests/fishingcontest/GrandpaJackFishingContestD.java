package com.rs.game.content.quests.fishingcontest;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.fishingcontest.FishingContest.DO_ROUNDS;
import static com.rs.game.content.quests.fishingcontest.FishingContest.ENTER_COMPETITION;

@PluginEventHandler
public class GrandpaJackFishingContestD extends Conversation {
	private static final int NPC = 230;


	public GrandpaJackFishingContestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FISHING_CONTEST)) {
		case ENTER_COMPETITION, DO_ROUNDS -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello young "+ player.getPronoun("man", "lady") + "! Come to visit old Grandpa Jack? I can tell ye stories for sure. I used to be the " +
					"best fisherman these parts have seen!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Tell me a story then", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Tell me a story then")
							.addNPC(NPC, HeadE.CALM_TALK, "Well, when I were a young man we used to take fishing trips over to Catherby: The fishing over " +
									"there -")
							.addNPC(NPC, HeadE.CALM_TALK, "now that was something! Anyway, we decided to do a bit of fishing with our nets, I wasn't having " +
									"the best of days turning up nothing but old boots and bits of seaweed. Then my net suddenly got really heavy! I pulled it up...")
							.addNPC(NPC, HeadE.CALM_TALK, "To my amazement I'd caught this little chest thing! Even more amazing was when I opened it, " +
									"it contained a diamond the size of a radish! That's the best catch I've ever had!")
							);
					option("What rod can I use for the competition?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What rod can I use for the competition?")
							.addNPC(NPC, HeadE.CALM_TALK, "Why, any old 'fishing rod' would do...")
							);
					option("Are you entering the fishing competition?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Are you entering the fishing competition?")
							.addNPC(NPC, HeadE.CALM_TALK, "Ah... the Hemenster fishing competition... I know all about that... I won that four years " +
									"straight! I'm too old for that lark now though...")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option(" I don't suppose you could give me any hints?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, " I don't suppose you could give me any hints?")
											.addNPC(NPC, HeadE.CALM_TALK, "Well, you sometimes get these really big fish in the water just by the outflow " +
													"pipes. I think they're some kind of carp...")
											.addNPC(NPC, HeadE.CALM_TALK, "Try to get a spot round there. The best sort of bait for them is red vine worms. " +
													"I used to get those from McGrubor's wood, north of here. Just dig around in the red vines up there but be careful of the guard dogs.")
											.addPlayer(HeadE.HAPPY_TALKING, "There's this weird creepy guy who says he's not a vampyre using that spot." +
													" He keeps winning too.")
											.addNPC(NPC, HeadE.CALM_TALK, "Ahh well, I'm sure you'll find something to put him off. Afer all, there " +
													"must be a kitchen around here with some garlic in it, perhaps in Seers Village or Ardougne.")
											.addNPC(NPC, HeadE.CALM_TALK, "If he's pretending to be a vampyre then he can pretend to be scared of garlic!")
											.addPlayer(HeadE.HAPPY_TALKING, "You're right! Thanks Jack!")
											);
									option("That's less competition for me then.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "That's less competition for me then.")
											.addNPC(NPC, HeadE.CALM_TALK, "Why you young whippersnapper! If I was twenty years younger I’d show you" +
													" something that’s for sure!")
											);
								}
							})
							);
					option("Sorry, I don't have time now.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Sorry, I don't have time now.")
							);
				}
			});
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello young "+ player.getPronoun("man", "lady") + "! Come to visit old Grandpa Jack? I can tell ye stories for sure. I used to be the " +
					"best fisherman these parts have seen!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Tell me a story then", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Tell me a story then")
							.addNPC(NPC, HeadE.CALM_TALK, "Well, when I were a young man we used to take fishing trips over to Catherby: The fishing over " +
									"there -")
							.addNPC(NPC, HeadE.CALM_TALK, "now that was something! Anyway, we decided to do a bit of fishing with our nets, I wasn't having " +
									"the best of days turning up nothing but old boots and bits of seaweed. Then my net suddenly got really heavy! I pulled it up...")
							.addNPC(NPC, HeadE.CALM_TALK, "To my amazement I'd caught this little chest thing! Even more amazing was when I opened it, " +
									"it contained a diamond the size of a radish! That's the best catch I've ever had!")
							);
					option("Sorry, I don't have time now.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Sorry, I don't have time now.")
							);
				}
			});

		}
		}
	}

	public static NPCClickHandler handleialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new GrandpaJackFishingContestD(e.getPlayer()).getStart()));


}

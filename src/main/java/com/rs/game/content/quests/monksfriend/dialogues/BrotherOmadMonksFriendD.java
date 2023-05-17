package com.rs.game.content.quests.monksfriend.dialogues;

import com.rs.cache.loaders.map.ClipFlag;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.minigames.partyroom.Balloon;
import com.rs.game.content.minigames.partyroom.PartyRoom;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;

import static com.rs.game.content.quests.monksfriend.MonksFriend.*;

@PluginEventHandler
public class BrotherOmadMonksFriendD extends Conversation {
	private static final int NPC = 279;
	private static final int PARTY_TICK_LENGTH = Ticks.fromSeconds(30);

	public BrotherOmadMonksFriendD(Player player) {
		super(player);
		Dialogue cedricOptions = new Dialogue();
		cedricOptions.addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("I've got no time for that, sorry.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I've got no time for that, sorry.")
						.addNPC(NPC, HeadE.CALM_TALK, "Okay traveler, take care.")
				);
				option("Where should I look?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Where should I look?")
						.addNPC(NPC, HeadE.CALM_TALK, "Oh, he won't be far. He might've taken a wrong turn between here and Ardougne.")
						.addPlayer(HeadE.HAPPY_TALKING, "Ok, I'll go and find him.", () -> {
							player.getQuestManager().setStage(Quest.MONKS_FRIEND, HELP_CEDRIC);
						})
				);
				option("Can I come to the party?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "' Can I come to the party?")
						.addNPC(NPC, HeadE.CALM_TALK, "Of course, but we need the wine first.")
						.addNext(() -> {
							player.startConversation(cedricOptions);
						})
				);
			}
		});
		Dialogue party = new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Ooh! What party?")
				.addNPC(NPC, HeadE.CALM_TALK, "Brother Androe's son's birthday party. He's going to be one year old!")
				.addPlayer(HeadE.HAPPY_TALKING, "That's sweet!")
				.addNPC(NPC, HeadE.CALM_TALK, "It's also a great excuse for a drink!")
				.addNPC(NPC, HeadE.CALM_TALK, "We just need Brother Cedric to return with the wine.")
				.addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Who's Brother Cedric?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Who's Brother Cedric?")
								.addNPC(NPC, HeadE.CALM_TALK, "Cedric is a member of the order too. We sent him out three days ago to collect wine. But he didn't return!")
								.addNPC(NPC, HeadE.CALM_TALK, "He most probably got drunk and lost in the forest!")
								.addNPC(NPC, HeadE.CALM_TALK, "I don't suppose you could go look for him?")
								.addNext(() -> {
									player.startConversation(cedricOptions);
								})
						);
						option("Enjoy it! I'll see you soon!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Enjoy it! I'll see you soon!")
								.addNPC(NPC, HeadE.CALM_TALK, "Take care traveler")
						);
					}
				});
		switch (player.getQuestManager().getStage(Quest.MONKS_FRIEND)) {
			case NOT_STARTED -> {
				Dialogue start = new Dialogue().addPlayer(HeadE.HAPPY_TALKING, "Can I help at all?")
						.addNPC(NPC, HeadE.CALM_TALK, "Would you? We won't be able to help you as we are peaceful men but we would be grateful for your help!")
						.addPlayer(HeadE.HAPPY_TALKING, "Do you know where the thieves went?")
						.addNPC(NPC, HeadE.CALM_TALK, "They hide in a secret cave near the battlefield to the west. It's hidden under a ring of stones.")
						.addNPC(NPC, HeadE.CALM_TALK, "Please bring back the blanket!", () -> {
							player.getQuestManager().setStage(Quest.MONKS_FRIEND, GET_BLANKET);
						});
				addPlayer(HeadE.HAPPY_TALKING, "Hello there. What's wrong?");
				addNPC(NPC, HeadE.CALM_TALK, "*yawn* ...oh, hello... yawn* I'm sorry! I'm just so tired! I haven't slept in a week!");
				addOptions("Start Monk's Friend?", new Options() {
					@Override
					public void create() {
						option("Yes.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Why can't you sleep?")
								.addNPC(NPC, HeadE.CALM_TALK, "It's Brother Androe's son, with his constant 'Waaaaaah! Waaaaaaaah!' Androe said it's natural," +
										" but it's so annoying!")
								.addPlayer(HeadE.HAPPY_TALKING, "I suppose that's what kids do.")
								.addNPC(NPC, HeadE.CALM_TALK, "He was fine, up until last week! Thieves broke in! They stole his favourite sleeping blanket!")
								.addNPC(NPC, HeadE.CALM_TALK, "Now he won't rest until it's returned... ...and that means neither can I!")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("Why would they steal a kid's blanket?", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Why would they steal a kid's blanket?")
												.addNPC(NPC, HeadE.CALM_TALK, "Who knows? Young scallywags! You'll find hundreds of people in the marketplace, " +
														"pilfering from the stalls while the owners' backs are turned.")
												.addNext(() -> {
													player.startConversation(start);
												})
										);
										option("Can I help at all?", new Dialogue()
												.addNext(() -> {
													player.startConversation(start);
												})
										);
									}
								})
						);
						option("No", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Sorry, I'm too busy to hear your problems!")
								.addNPC(NPC, HeadE.SAD_MILD_LOOK_DOWN, "Okay...")
						);
					}
				});


			}
			case GET_BLANKET -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "*yawn*...oh, hello again...*yawn*");
				addNPC(NPC, HeadE.CALM_TALK, "Please tell me you have the blanket.");
				if (player.getInventory().containsItems(new Item(90, 1))) {
					addPlayer(HeadE.HAPPY_TALKING, "Yes! I've recovered it from the clutches of the evil thieves!");
					addSimple("You hand the monk the child's blanket.", () -> {
						player.getInventory().removeItems(new Item(90, 1));
						player.getQuestManager().setStage(Quest.MONKS_FRIEND, ASK_ABOUT_PARTY);
					});
					addNPC(NPC, HeadE.CALM_TALK, "Really, that's excellent, well done! Maybe now I'll be able to get some sleep.");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Is there anything else I can help with?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Is there anything else I can help with?")
									.addNPC(NPC, HeadE.CALM_TALK, "I'm glad you asked, you see there's this party.")
									.addNext(() -> {
										player.startConversation(party);
									})
							);
							option("Farewell!", new Dialogue());
						}
					});
				} else {
					addPlayer(HeadE.HAPPY_TALKING, "I'm afraid not.");
					addNPC(NPC, HeadE.CALM_TALK, "I need some sleep!");
				}
			}
			case ASK_ABOUT_PARTY -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello, how are you?");
				addNPC(NPC, HeadE.CALM_TALK, "Much better now I'm sleeping well! Now I can organise the party.");
				addNext(() -> {
					player.startConversation(party);
				});
			}
			case HELP_CEDRIC -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello brother Omad.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello adventurer, have you found Brother Cedric?");
				addPlayer(HeadE.HAPPY_TALKING, "Not yet.");
				addNPC(NPC, HeadE.CALM_TALK, "Well, keep looking, we need that wine!");
			}
			case RETURN_TO_OMAD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hi Omad, Brother Cedric is on his way!");
				addNPC(NPC, HeadE.CALM_TALK, "Good! Good! Now we can party!");
				addNPC(NPC, HeadE.CALM_TALK, "I have little to repay you with, but I'd like to offer you some rune stones. But first, let's party!");
				addNext(() -> {
					partyTime(this.player);
				});
			}
			case QUEST_COMPLETE -> {
				if (System.currentTimeMillis() - player.getTempAttribs().getL("last_party_time") < 1000 * 60 * 30)
					addNPC(NPC, HeadE.DRUNK, "Dum dee do la la! *hiccup* That was some party!");
				else {
					addNPC(NPC, HeadE.CALM_TALK, "Wanna join our party " + this.player.getPronoun("brother ", "sister ") + player.getDisplayName() + "?");
					addOptions("Join their party?", new Options() {
						@Override
						public void create() {
							option("Let's party!", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Let's party!")
									.addNext(() -> {
										partyTime(player);
									})
							);
							option("No thank you.", new Dialogue());
						}
					});


				}
			}
		}
	}

	/**
	 * Every 4 ticks make a monk dance. Each 4 is 0 1 2 3 0 1 2 3 0 1 2 3 with modulus...
	 *
	 * @param p
	 */
	public static void partyTime(Player p) {
		p.lock();
		p.getTempAttribs().setL("last_party_time", System.currentTimeMillis());
		String[] dancePhrases = new String[]{"Let's boogie!", "Get down!", "Let's dance!", "Party time!", "Feel the rhythm!", "Watch me go!", "Party!", "Woop!", "Oh my!"};
		ArrayList<NPC> monks = new ArrayList<>();
		for (NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
			if (npc.getName().equalsIgnoreCase("Monk") || npc.getName().equalsIgnoreCase("Brother Omad"))
				monks.add(npc);
		WorldTasks.scheduleTimer(i -> {
			if (i % 15 == 3) {
				p.setNextAnimation(new Animation(818));
				p.forceTalk(dancePhrases[Utils.random(0, dancePhrases.length)]);
				throwBalloons();
			}
			if (i % 4 == 0) {
				int monkIndex = (i / 4) % monks.size();
				monks.get(monkIndex).setLockedForTicks(4);
				monks.get(monkIndex).setNextAnimation(new Animation(818));
				if (Utils.randomInclusive(0, 1) == 0)
					monks.get(monkIndex).forceTalk(dancePhrases[Utils.random(0, dancePhrases.length)]);
			}
			if (i == PARTY_TICK_LENGTH) {
				p.getQuestManager().completeQuest(Quest.MONKS_FRIEND);
				p.unlock();
				return false;
			}
			return true;
		});
	}

	public static void throwBalloons() {
		ArrayList<Balloon> balloons = new ArrayList<>();
		for (int x = 2601; x < 2612; x++)
			for (int y = 3205; y < 3222; y++)
				if (World.getObject(Tile.of(x, y, 0)) == null && (ClipFlag.flagged(World.getClipFlags(0, x, y), ClipFlag.UNDER_ROOF)))
					if (Utils.randomInclusive(0, 1) == 0)
						balloons.add(new Balloon(PartyRoom.getRandomBalloon(), 0, x, y, 0));
		WorldTasks.scheduleTimer(i -> {
			if (i == 0) {
				World.spawnObjectTemporary(balloons.get(i), Ticks.fromSeconds(25));
				return true;
			}
			i *= 2;
			i--;
			if (i >= balloons.size())
				return false;
			World.spawnObjectTemporary(balloons.get(i), Ticks.fromSeconds(25));
			i++;
			if (i >= balloons.size())
				return false;
			World.spawnObjectTemporary(balloons.get(i), Ticks.fromSeconds(25));
			return true;
		});
	}
}

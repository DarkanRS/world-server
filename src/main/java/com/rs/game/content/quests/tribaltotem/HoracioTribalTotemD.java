package com.rs.game.content.quests.tribaltotem;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class HoracioTribalTotemD extends Conversation {
	private static final int NPC = 845;
	public HoracioTribalTotemD(Player player) {
		super(player);
		addNPC(NPC, HeadE.CALM_TALK, "It's a fine day to be out in a garden, isn't it?");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Yes. It's very nice.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "es. It's very nice.")
						.addNPC(NPC, HeadE.CALM_TALK, "Days like these make me glad to be alive!")
						);
				option("So... who are you?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "So... who are you?")
						.addNPC(NPC, HeadE.CALM_TALK, "My name is Horacio Dobson. I'm a gardener to Lord Handlemort. Take a look around this beautiful " +
								"garden, all of this is my handiwork")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("So... do you garden round the back, too?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "So... do you garden round the back, too?")
										.addNPC(NPC, HeadE.CALM_TALK, "That I do!")
										.addPlayer(HeadE.HAPPY_TALKING, "Doesn't all of the security around the house get in your way then?")
										.addNPC(NPC, HeadE.CALM_TALK, "Ah. I'm used to all that. I have my keys, the guard dogs know me, and I know the " +
												"combination to the door lock. It's rather easy, it's his middle name.")
										.addPlayer(HeadE.HAPPY_TALKING, "Whose middle name?")
										.addNPC(NPC, HeadE.CALM_TALK, "Hum. I probably shouldn't have said that. Forget I mentioned it.")
										);
								option("Do you need any help?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Do you need any help?")
										.addNPC(NPC, HeadE.CALM_TALK, "Trying to muscle in on my job, eh? I'm more than happy to do this all by myself!\n")
										);
							}
						}));
                option("How do I get into the mansion?", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "How do I get into the mansion?")
                        .addNPC(NPC, HeadE.CALM_TALK, "It is impossible!")
                        .addNPC(NPC, HeadE.CALM_TALK, "Lord Handelmort has the latest of security features in his home...")
                        .addNPC(NPC, HeadE.CALM_TALK, "He has a state of the art password lock and trick stairs which dump you into the sewers.")
                        .addNPC(NPC, HeadE.CALM_TALK, "Yep, there is no getting past that locked door, only me and him know password, it's rather easy," +
                                " it's his middle name")
                        .addPlayer(HeadE.HAPPY_TALKING, "Whose middle name?")
                        .addNPC(NPC, HeadE.AMAZED, "Hum. I probably shouldn't have said that. Forget I mentioned it.")
                );
			}
		});

	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new HoracioTribalTotemD(e.getPlayer()).getStart()));
}

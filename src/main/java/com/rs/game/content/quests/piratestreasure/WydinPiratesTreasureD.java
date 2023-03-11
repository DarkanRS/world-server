package com.rs.game.content.quests.piratestreasure;

import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.*;
import static com.rs.game.content.world.doors.Doors.handleDoor;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class WydinPiratesTreasureD extends Conversation {

	public WydinPiratesTreasureD(Player player) {
		super(player);
		int NPC = WYDIN;
		switch (player.getQuestManager().getStage(Quest.PIRATES_TREASURE)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Welcome to my food store! Would you like to buy anything?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Yes please.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Yes please.")
								.addNext(()->{ShopsHandler.openShop(WydinPiratesTreasureD.this.player, "wydins_food_store");})
						);
						option("No, thank you.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "No, thank you.")
						);
						option("What can you recommend?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What can you recommend?")
								.addNPC(NPC, HeadE.CALM_TALK, "We have this really exotic fruit all the way from Karamja. It's called a banana.")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("Hmm, I think I'll try one.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Hmm, I think I'll try one.")
												.addNPC(NPC, HeadE.CALM_TALK, "Great. You might as well take a look at the rest of my wares as well.")
												.addNext(()->{ShopsHandler.openShop(WydinPiratesTreasureD.this.player, "wydins_food_store");})
										);

										option("I don't like the sound of that.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I don't like the sound of that.")
												.addNPC(NPC, HeadE.CALM_TALK, "Well, it's your choice, but I do recommend them.")
										);
									}
								})
						);
					}
				});

			}
			case SMUGGLE_RUM -> {
				addPlayer(HeadE.HAPPY_TALKING, "Can I get a job here?");
				addNPC(WYDIN, HeadE.CALM_TALK, "Well, you're keen, I'll give you that. Okay, I'll give you a go. Have you got your own white apron?");
				if(player.getInventory().containsItem(APRON, 1) || player.getEquipment().getChestId() == APRON) {
					addPlayer(HeadE.HAPPY_TALKING, "Yes, I have one right here.");
					addNPC(WYDIN, HeadE.CALM_TALK, "Wow - you are well prepared! You're hired. Go through to the back and tidy up for me, please.", ()->{
						player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("WYDIN_EMPLOYMENT", true);
					});
				}
				else {
					addPlayer(HeadE.HAPPY_TALKING, "No, I haven't.");
					addNPC(WYDIN, HeadE.CALM_TALK, "Well, you can't work here unless you have a white apron. Health and safety regulations, you understand.");
					addPlayer(HeadE.HAPPY_TALKING, "Where can I get one of those?");
					addNPC(WYDIN, HeadE.CALM_TALK, "Well, I get all of mine over at the clothing shop in Varrock. They sell them cheap there. Oh, and I'm sure " +
							"that I've seen a spare one over in Gerrant's fish store somewhere. It's the little place just north of here.");
				}
			}
			case GET_TREASURE, QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Is it nice and tidy round the back now?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Yes, can I work out front now?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, can I work out front now?")
								.addNPC(NPC, HeadE.CALM_TALK, "No, I'm the one who works here.")

						);
						option("Yes, are you going to pay me yet?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, are you going to pay me yet?")
								.addNPC(NPC, HeadE.CALM_TALK, "Umm... No, not yet.")

						);
						option("No, it's a complete mess", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "No, it's a complete mess")
								.addNPC(NPC, HeadE.CALM_TALK, "Ah well, it'll give you something to do, won't it.")

						);
						option("Can I buy something please?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can I buy something please?")
								.addNPC(NPC, HeadE.CALM_TALK, "Yes, of course.")
								.addNext(()->{ShopsHandler.openShop(WydinPiratesTreasureD.this.player, "wydins_food_store");})
						);
					}
				});

			}
		}
	}

	public static ObjectClickHandler handleBackRoom = new ObjectClickHandler(new Object[] { 2069 }, e -> {
		GameObject obj = e.getObject();
		if(e.getPlayer().getX() < obj.getX()) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		if(e.getPlayer().getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM) {
			if(!e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("WYDIN_EMPLOYMENT")) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(WYDIN, HeadE.CALM_TALK, "Hey, you are not employed here!");
						create();
					}
				});
				return;
			}
			if(e.getPlayer().getEquipment().getChestId() != APRON) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(WYDIN, HeadE.CALM_TALK, "Hey, you need your apron on!");
						create();
					}
				});
				return;
			}
		}
		handleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleSmuggleCrate = new ObjectClickHandler(new Object[] { 2071 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM)
			if(e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("RUM_IN_SARIM_CRATE")) {
				e.getPlayer().getInventory().addItem(new Item(RUM, 1), true);
				e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).removeB("RUM_IN_SARIM_CRATE");
				e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("HAS_SMUGGLED_RUM", true);
			} else if(e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("RUM_IN_KARAMJA_CRATE"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.HAPPY_TALKING, "Darn, I should probably tell Lathus to send the crate!");
						create();
					}
				});
	});

	public static NPCClickHandler handleWydin = new NPCClickHandler(new Object[] { WYDIN }, e -> {
		if(e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new WydinPiratesTreasureD(e.getPlayer()).getStart());
		else if(e.getOption().equalsIgnoreCase("trade"))
			ShopsHandler.openShop(e.getPlayer(), "wydins_food_store");
	});
}

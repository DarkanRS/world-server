package com.rs.game.player.content.world.regions;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.world.doors.DoorPair;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Draynor {
	
	public static NPCClickHandler handleNed = new NPCClickHandler(918) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.EXPLORERS_RING).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleAva = new NPCClickHandler(5199) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!Quest.ANIMAL_MAGNETISM.meetsRequirements(e.getPlayer(), "to enter this area."))
				return;
			Conversation chooseDevice = new Conversation(e.getPlayer()) {
				{
					addOptions("Which device would you like?", new Options() {
						@Override
						public void create() {
							option("The attractor", () -> {
								if (player.getInventory().containsItem(995, 999)) {
									player.getInventory().deleteItem(995, 999);
									player.getInventory().addItem(10498, 1);
									player.startConversation(new Dialogue().addSimple("You buy an attractor for 999 coins."));
								} else
									player.startConversation(new Conversation(e.getPlayer()).addNPC(e.getNPCId(), HeadE.NO_EXPRESSION, "I'm not running a charity. You need at least 999 coins to buy a new attractor."));
							});
							if (e.getPlayer().getSkills().getLevelForXp(Constants.RANGE) >= 50) {
								option("The accumulator", () -> {
									if (player.getInventory().containsItem(995, 999) && player.getInventory().containsItem(886, 75)) {
										player.getInventory().deleteItem(995, 999);
										player.getInventory().deleteItem(886, 75);
										player.getInventory().addItem(10499, 1);
										player.startConversation(new Dialogue().addSimple("You buy an accumulator for 999 coins and 75 steel arrows."));
									} else
										player.startConversation(new Conversation(e.getPlayer()).addNPC(e.getNPCId(), HeadE.NO_EXPRESSION, "I'm not running a charity. You need at least 999 coins and 75 steel arrows to buy a new accumulator."));
								});
							}
							if (Quest.DO_NO_EVIL.meetsRequirements(player, "to claim an alerter.")) {
								option("The alerter", () -> {
									if (player.getInventory().containsItem(995, 999) && player.getInventory().containsItem(886, 75)) {
										player.getInventory().deleteItem(995, 999);
										player.getInventory().deleteItem(886, 75);
										player.getInventory().addItem(20068, 1);
										player.startConversation(new Dialogue().addSimple("You buy an alerter for 999 coins and 75 steel arrows."));
									} else
										player.startConversation(new Conversation(e.getPlayer()).addNPC(e.getNPCId(), HeadE.NO_EXPRESSION, "I'm not running a charity. You need at least 999 coins and 75 steel arrows to buy a new alerter."));
								});
							}
						}
					});
				}
			};
			
			switch(e.getOpNum()) {
			case 1:
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.CONFUSED, "Hello again; I'm busy with my newest research, so can't gossip too much. Are you after information, an upgrade, another device, or would you like to see my goods for sale?");
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								option("I seem to need a new device.", () -> player.startConversation(chooseDevice));
								option("I'd like to see your stuff for sale please.", () -> ShopsHandler.openShop(player, "avas_odds_and_ends"));
							}
						});
					}
				});
				break;
			case 3:
				ShopsHandler.openShop(e.getPlayer(), "avas_odds_and_ends");
				break;
			case 4:
				e.getPlayer().startConversation(chooseDevice);
				break;
			}
		}
	};
	
	public static ObjectClickHandler handleEnterDraynorAvaSecret = new ObjectClickHandler(new Object[] { 160, 47404 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Quest.ANIMAL_MAGNETISM.meetsRequirements(e.getPlayer(), "to enter this area."))
				return;
			e.getPlayer().lock();
			e.getPlayer().setNextFaceWorldTile(e.getObject());
			e.getPlayer().setNextAnimation(new Animation(1548));
			WorldTasksManager.delay(2, () -> {
				e.getPlayer().addWalkSteps(e.getPlayer().transform(0, e.getObjectId() == 47404 ? -1 : 1), 1, true);
			});
			WorldTasksManager.delay(4, () -> {
				GameObject door1 = World.getObjectWithId(e.getObject().transform(e.getObjectId() == 47404 ? 0 : 1, e.getObjectId() == 47404 ? -1 : 2), 47531);
				GameObject door2 = World.getObjectWithId(e.getObject().transform(e.getObjectId() == 47404 ? 0 : 1, e.getObjectId() == 47404 ? -2 : 1), 47529);
				if (door1 != null && door2 != null) {
					World.spawnObjectTemporary(new GameObject(door1).setIdNoRefresh(83), 2, true);
					World.spawnObjectTemporary(new GameObject(door2).setIdNoRefresh(83), 2, true);
					World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(e.getPlayer(), door1), door1.getType(), door1.getRotation(1), door1.transform(-1, 0, 0)), 2, true);
					World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(e.getPlayer(), door2), door2.getType(), door2.getRotation(-1), door2.transform(-1, 0, 0)), 2, true);
				}
				e.getPlayer().addWalkSteps(e.getObject().transform(e.getObjectId() == 47404 ? -1 : 1, e.getObjectId() == 47404 ? -1 : 1), 3, false);
				e.getPlayer().unlock();
			});
		}
	};
	
	public static ObjectClickHandler handleClimbWizardsTowerBasement = new ObjectClickHandler(new Object[] { 32015 }, new WorldTile(3103, 9576, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3105, 3162, 0));
		}
	};
	
	public static ObjectClickHandler handleDraynorManorBasement = new ObjectClickHandler(new Object[] { 47643, 164 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 47643)
				e.getPlayer().useStairs(new WorldTile(3080, 9776, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3115, 3355, 0));
		}
	};
	
	public static ObjectClickHandler handleDraynorManorStairs = new ObjectClickHandler(new Object[] { 47364, 47657 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(0, e.getObjectId() == 47364 ? 5 : -5, e.getObjectId() == 47364 ? 1 : -1));
		}
	};
}

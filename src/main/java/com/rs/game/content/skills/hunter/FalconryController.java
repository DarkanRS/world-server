// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.hunter;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FalconryController extends Controller {

	public int[] xp = { 103, 132, 156 };
	public int[] furRewards = { 10125, 10115, 10127 };
	public int[] levels = { 43, 57, 69 };

	@Override
	public void start() {
		player.setNextAnimation(new Animation(1560));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(new WorldTile(2371, 3619, 0));
			}
		});
		player.getEquipment().setSlot(Equipment.WEAPON, new Item(10024, 1));
		player.getAppearance().generateAppearanceData();
		player.simpleDialogue("Simply click on the target and try your luck.");
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == 3 || slotId == 5)
			return false;
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public void forceClose() {
		player.getEquipment().deleteSlot(Equipment.WEAPON);
		player.getInventory().deleteItem(10024, Integer.MAX_VALUE);
		player.getAppearance().generateAppearanceData();
	}

	@Override
	public boolean processNPCClick1(final NPC npc) {
		player.setNextFaceEntity(npc);
		if (npc.getDefinitions().getName().toLowerCase().contains("kebbit")) {
			if (player.getTempAttribs().getB("falconReleased")) {
				player.simpleDialogue("You cannot catch a kebbit without your falcon.");
				return false;
			}
			int level = levels[(npc.getId() - 5098)];
			if (proccessFalconAttack(npc)) {
				if (player.getSkills().getLevel(Constants.HUNTER) < level) {
					player.simpleDialogue("You need a Hunter level of " + level + " to capture this kebbit.");
					return true;
				}
				if (FlyingEntityHunter.isSuccessful(player, level, player -> {
					if (player.getEquipment().getGlovesId() == 10075)
						return 3;
					return 1;
				})) {
					player.getEquipment().setSlot(Equipment.WEAPON, new Item(10023, 1));
					player.getAppearance().generateAppearanceData();
					player.getTempAttribs().setB("falconReleased", true);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							World.sendProjectile(player, npc, 918, 41, 16, 31, 35, 16, 0);
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									npc.transformIntoNPC(npc.getId() - 4);
									player.getTempAttribs().setO("ownedFalcon", npc);
									player.sendMessage("The falcon successfully swoops down and captures the kebbit.");
									player.getHintIconsManager().addHintIcon(npc, 1, -1, false);
								}
							});
						}
					});
				} else {
					player.getEquipment().setSlot(Equipment.WEAPON, new Item(10023, 1));
					player.getAppearance().generateAppearanceData();
					player.getTempAttribs().setB("falconReleased", true);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							World.sendProjectile(player, npc, 918, 41, 16, 31, 35, 16, 0);
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									World.sendProjectile(npc, player, 918, 41, 16, 31, 35, 16, 0);
									WorldTasks.schedule(new WorldTask() {
										@Override
										public void run() {
											player.getEquipment().setSlot(Equipment.WEAPON, new Item(10024, 1));
											player.getAppearance().generateAppearanceData();
											player.getTempAttribs().removeB("falconReleased");
											player.sendMessage("The falcon swoops down on the kebbit, but just barely misses catching it.");
										}
									});
								}
							}, Utils.getDistance(player.getTile(), npc.getTile()) > 3 ? 2 : 1);
						}
					});
				}
			}
			return false;
		}
		if (npc.getDefinitions().getName().toLowerCase().contains("gyr falcon")) {
			NPC kill = player.getTempAttribs().getO("ownedFalcon");
			if (kill == null)
				return false;
			if (kill != npc) {
				player.simpleDialogue("This isn't your kill!");
				return false;
			}
			npc.transformIntoNPC(npc.getId() + 4);
			npc.setRespawnTask();
			// player.getInventory().addItem(new Item(furRewards[(npc.getId() -
			// 5094)], 1));
			player.getInventory().addItem(new Item(526, 1));
			// player.getSkills().addXp(Constants.HUNTER, xp[(npc.getId() -
			// 5094)]);
			player.sendMessage("You retreive the falcon as well as the fur of the dead kebbit.");
			player.getHintIconsManager().removeUnsavedHintIcon();
			player.getEquipment().setSlot(Equipment.WEAPON, new Item(10024, 1));
			player.getAppearance().generateAppearanceData();
			player.getTempAttribs().removeO("ownedFalcon");
			player.getTempAttribs().removeB("falconReleased");
			return false;
		}
		return true;
	}

	private boolean proccessFalconAttack(NPC target) {
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = player.getSize();
		int maxDistance = 16;
		player.resetWalkSteps();
		if ((!player.lineOfSightTo(target, maxDistance == 0)) || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			if (!player.calcFollow(target, 2, true))
				return true;
		return true;
	}

	public static ObjectClickHandler enterArea = new ObjectClickHandler(new Object[] { 19222 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			beginFalconry(e.getPlayer());
		}
	};
	
	/**
	 * Success rates:
	 * Spotted: 26/256 - 310/256
	 * Dark: 0/256 - 253/256
	 * Dashing: 0/256 - 205/256
	 */
	public static void beginFalconry(Player player) {
		if (!player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("Falconry is temporarily closed.");
			return;
		}
		if ((player.getEquipment().getItem(3) != null && player.getEquipment().getItem(3).getId() == -1) || (player.getEquipment().getItem(5) != null && player.getEquipment().getItem(5).getId() == -1)) {
			player.simpleDialogue("You need both hands free to use a falcon.");
			return;
		}
		if (player.getSkills().getLevel(Constants.HUNTER) < 43) {
			player.simpleDialogue("You need a Hunter level of at least 43 to use a falcon, come back later.");
			return;
		}
		player.getControllerManager().startController(new FalconryController());
	}
	
//	"Sorry, you really need both hands free for falconry. I'd"
//	" suggest that you put away your weapons and gloves before we start."
//
//	Pay 500 coins.
//		"The falconer gives you a large leather glove and brings one of the smaller"
//		"birds over to land on it."
//
//		"Don't worry; I'll keep an eye on you to make sure you "
//		"don't upset it too much."
//	Not right now.


// "You should speak to Matthias to get this removed safely.
//"Hello again."
//"Ah, you're back. How are you getting along with her then?"
//"It's certainly harder than it looks."
//"Sorry, but I was talking to the falcon, not you. But yes it "
//"is. Have you had enough yet?"
//	"Actually, I'd like to keep trying a little longer."
//		"Ok then, just come talk to me when you're done."
//	"I think I'll leave it for now."
//		"You give the falcon and glove back to Matthias."
//
//"It'll cost you 500 coins to borrow one of my birds."
	
	public static NPCClickHandler handleMatthias = new NPCClickHandler(new Object[] { 5092 }) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Talk-to" -> {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CHEERFUL, "Hello there.");
						addNPC(e.getNPCId(), HeadE.CONFUSED, "Greetings. Can I help you at all?");
						addOptions(ops -> {
							ops.add("Do you have any quests I could do?")
								.addNPC(e.getNPCId(), HeadE.CONFUSED, "A quest? What a strange notion. Do you normally go around asking complete strangers for quests?")
								.addPlayer(HeadE.SKEPTICAL, "Er, yes, now that you come to mention it.")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Oh, ok then. Well, no, I don't. Sorry.");
							ops.add("What is this place?")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "A good question; straight and to the point.")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "My name is Matthias, I am a falconer, and this is where I train my birds.")
								.addOptions(watOp -> {
									watOp.add("That sounds like fun; could I have a go?")
										;
									
									watOp.add("That doesn't sound like my sort of thing.")
										.addNPC(e.getNPCId(), HeadE.CALM_TALK, "Fair enough; it does require a great deal of patience and skill, so I can understand if you might feel intimidated.");
									
									watOp.add("What's this falconry thing all about then?")
										.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Well, some people see it as a sport, although such a term does not really convey the amount of patience and dedication to be profiecient at the task.")
										.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Putting it simply, it is the training and use of birds of prey in hunting quarry.");
								});
						});
						create();
					}
				});
			}
			case "Falconry" -> {}
			}
		}
	};
}

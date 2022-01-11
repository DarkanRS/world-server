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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.dragonslayer.DragonSlayer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Karamja  {

	public static NPCClickHandler handlePirateJackieFruit = new NPCClickHandler(1055) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleKalebParamaya = new NPCClickHandler(512) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleJungleForesters = new NPCClickHandler(401, 402) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};

	public static ObjectClickHandler handleBrimhavenDungeonEntrance = new ObjectClickHandler(new Object[] { 5083 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2713, 9564, 0));
		}
	};

	public static ObjectClickHandler handleBrimhavenDungeonExit = new ObjectClickHandler(new Object[] { 5084 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2745, 3152, 0));
		}
	};

	public static ObjectClickHandler handleJogreLogWalk = new ObjectClickHandler(new Object[] { 2332 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getX() > 2908)
				Agility.walkToAgility(e.getPlayer(), 155, new WorldTile(2906, 3049, 0), 0);
			else
				Agility.walkToAgility(e.getPlayer(), 155, new WorldTile(2910, 3049, 0), 0);
		}
	};

	public static ObjectClickHandler handleMossGiantRopeSwings = new ObjectClickHandler(new Object[] { 2322, 2323 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			final WorldTile toTile = e.getObjectId() == 2322 ? new WorldTile(2704, 3209, 0) : new WorldTile(2709, 3205, 0);
			if (Agility.hasLevel(e.getPlayer(), 10))
				if (e.isAtObject()) {
					if (e.getObjectId() == 2322 ? e.getPlayer().getX() == 2704 : e.getPlayer().getX() == 2709) {
						e.getPlayer().sendMessage("You can't reach that.", true);
						return;
					}
					e.getPlayer().lock();
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().setNextAnimation(new Animation(751));
					World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(497));

					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 1, toTile, 3, Utils.getAngleTo(toTile.getX() - e.getPlayer().getX(), toTile.getY() - e.getPlayer().getY())));
					e.getPlayer().sendMessage("You skillfully swing across the rope.", true);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							e.getPlayer().unlockNextTick();
							e.getPlayer().getSkills().addXp(Constants.AGILITY, 0.1);
							e.getPlayer().setNextWorldTile(toTile);
						}

					});
					e.getPlayer().unlock();
				}
		}
	};

	public static ObjectClickHandler handleJogreWaterfallSteppingStones = new ObjectClickHandler(new Object[] { 2333, 2334, 2335 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 30))
				return;
			e.getPlayer().setNextAnimation(new Animation(741));
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, e.getObject(), 1, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().setNextWorldTile(new WorldTile(e.getObject()));
				}
			}, 0);
		}
	};

	public static ObjectClickHandler handleRareTreeDoors = new ObjectClickHandler(new Object[] { 9038, 9039 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
				if (e.getPlayer().getX() >= e.getObject().getX())
					Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				else if (e.getPlayer().getInventory().containsItem(6306, 10)) {
					Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
					e.getPlayer().getInventory().deleteItem(6306, 10);
				} else
					e.getPlayer().sendMessage("You need 10 trading sticks to use this door.");
			} else if (e.getOpNum() == ClientPacket.OBJECT_OP1)
				if (e.getPlayer().getX() >= e.getObject().getX())
					Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				else
					e.getPlayer().sendOptionDialogue("Pay 10 trading sticks to enter?", new String[] {"Yes", "No"}, new DialogueOptionEvent() {

						@Override
						public void run(Player player) {
							if (getOption() == 1)
								if (e.getPlayer().getInventory().containsItem(6306, 10)) {
									Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
									e.getPlayer().getInventory().deleteItem(6306, 10);
								} else
									e.getPlayer().sendMessage("You need 10 trading sticks to use this door.");
						}

					});
		}
	};

	public static ObjectClickHandler handleCrandorVolcanoCrater = new ObjectClickHandler(new Object[] { 25154 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.DRAGON_SLAYER) == DragonSlayer.PREPARE_FOR_CRANDOR) {
				if(!p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.INTRODUCED_ELVARG_ATTR)) {
					DragonSlayer.introduceElvarg(p);
					return;
				}
				;
			}


			e.getPlayer().setNextWorldTile(new WorldTile(2834, 9657, 0));
		}
	};

	public static ObjectClickHandler handleCrandorVolcanoRope = new ObjectClickHandler(new Object[] { 25213 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2832, 3255, 0));
		}
	};

	public static ObjectClickHandler handleKaramjaVolcanoRocks = new ObjectClickHandler(new Object[] { 492 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2857, 9569, 0));
		}
	};

	public static ObjectClickHandler handleKaramjaVolcanoRope = new ObjectClickHandler(new Object[] { 1764 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2855, 3169, 0));
		}
	};

	public static ObjectClickHandler handleElvargHiddenWall = new ObjectClickHandler(new Object[] { 2606 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.DRAGON_SLAYER) || e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.FINISHED_BOAT_SCENE_ATTR)) {
				e.getPlayer().sendMessage("You know from your boat accident there is more behind this wall...");
				Doors.handleDoor(e.getPlayer(), e.getObject());
			} else
				e.getPlayer().sendMessage("You see nothing but a wall...");
		}
	};

	public static ObjectClickHandler handleShiloFurnaceDoor = new ObjectClickHandler(new Object[] { 2266, 2267 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 2267)
				return;
			if (e.getPlayer().getY() > e.getObject().getY())
				Doors.handleDoor(e.getPlayer(), e.getObject());
			else if (e.getPlayer().getInventory().containsItem(995, 20)) {
				e.getPlayer().getInventory().deleteItem(995, 20);
				Doors.handleDoor(e.getPlayer(), e.getObject());
			} else
				e.getPlayer().sendMessage("You need 20 gold to use this furnace.");
		}
	};

	public static ObjectClickHandler handleTzhaarEnter = new ObjectClickHandler(new Object[] { 68134 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(4667, 5059, 0));
		}
	};

	public static ObjectClickHandler handleTzhaarExit = new ObjectClickHandler(new Object[] { 68135 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2845, 3170, 0));
		}
	};

	public static ObjectClickHandler handleJogreCaveEnter = new ObjectClickHandler(new Object[] { 2584 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2830, 9522, 0));
		}
	};

	public static ObjectClickHandler handleJogreCaveExit = new ObjectClickHandler(new Object[] { 2585 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2824, 3120, 0));
		}
	};

	public static ObjectClickHandler handleShiloEnter = new ObjectClickHandler(new Object[] { 2216 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You quickly climb over the cart.");
			e.getPlayer().ladder(e.getPlayer().getX() > e.getObject().getX() ? e.getPlayer().transform(-4, 0, 0) : e.getPlayer().transform(4, 0, 0));
		}
	};

	public static ObjectClickHandler handleShiloCartEnter = new ObjectClickHandler(new Object[] { 2230 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2833, 2954, 0));
		}
	};

	public static ObjectClickHandler handleShiloCartExit = new ObjectClickHandler(new Object[] { 2265 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2778, 3210, 0));
		}
	};

	public static ObjectClickHandler handleElvargEntrance = new ObjectClickHandler(new Object[] { 25161 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();

			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;
				boolean goingEast = true;

				@Override
				public void run() {
					if (ticks == 0) {
						if (p.getX() == 2845) {
							p.setFaceAngle(Direction.getAngleTo(Direction.EAST));
							p.setNextAnimation(new Animation(839));
							goingEast = true;
						} else if (p.getX() == 2847) {
							p.setFaceAngle(Direction.getAngleTo(Direction.WEST));
							p.setNextAnimation(new Animation(839));
							goingEast = false;
						} else
							return;
					} else if (ticks >= 1) {
						if (goingEast)
							p.setNextWorldTile(new WorldTile(2847, p.getY(), 0));
						if (!goingEast)
							p.setNextWorldTile(new WorldTile(2845, p.getY(), 0));
						stop();
					}
					ticks++;
				}
			}, 0, 1);

		}
	};
}

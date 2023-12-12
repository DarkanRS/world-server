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
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.ItemConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rs.game.content.world.doors.Doors.handleDoubleDoor;
import static com.rs.game.content.world.doors.Doors.handleGate;

@QuestHandler(Quest.PRIEST_IN_PERIL)
@PluginEventHandler
public class PriestInPeril extends QuestOutline {

	public enum Monuments{
		N(3494, Tile.of(3423, 9895, 0), 2347,2949, "Saradomin is the hammer that crushes evil everywhere."), //Hammer
		NE(3497, Tile.of(3427,9894,0),2944,2945, "Saradomin is the key that unlocks the mysteries of life"), //Key
		E(3495, Tile.of(3428,9890, 0), 314,2950, "Saradomin is the delicate touch that brushes us with love."), //Feather
		SE(3498, Tile.of(3427, 9885, 0), 1970,2946, "Saradomin is the spark that lights the fire in our hearts."), //Tinderbox
		S(3496, Tile.of(3422, 9884, 0), 1733,2951, "Saradomin is the needle that binds our lives together."), //Needle
		W(3493, Tile.of(3416, 9890, 0), 1931,2948, "Saradomin is the vesel that keeps us safe from harm."), //Pot
		NW(3499, Tile.of(3418,9894, 0), 36,2947, "Saradomin is the light that shines throughout our lives."); //Candle

		private final int monumentID;

		private final Tile tile;

		private final int itemID;

		private final int goldenID;
		private String message;

		Monuments(int monumentID, Tile tile, int itemID, int goldenID, String message){
			this.monumentID = monumentID;
			this.tile = tile;
			this.itemID = itemID;
			this.goldenID = goldenID;
			this.message = message;
		}

		public static Monuments getMonumentByTile(Tile tile) {
			for (Monuments monument : Monuments.values()) {
				if (monument.tile.equals(tile)) {
					return monument;
				}
			}
			return null;
		}
	}

	private static final int Drezel = 1047;

	@Override
	public int getCompletedStage() {
		return 11;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
			case 0:
				lines.add("I can start this quest by speaking to King Roald");
				lines.add("in the Varrock palace.");
				break;
			case 1:
				lines.add("");
				lines.add("I spoke to King Roald who asked me to investigate");
				lines.add("why his friend Priest Drezel has stopped communicating");
				lines.add("with him.");
				break;
			case 2:
				lines.add("");
				lines.add("I went to the temple where Drezel lives, but it was");
				lines.add("locked shut. I spoke to Drezel through the locked door.");
				lines.add("He told me that there was an annoying dog below the temple.");
				lines.add("Drezel told me to kill the dog.");
				break;
			case 3:
				lines.add("");
				lines.add("I killed the dog easily");
				break;
			case 4:
				lines.add("");
				lines.add("When I told Roald what I had done, he was furious!");
				lines.add("The person that told me to kill the dog wasn't Drezel at all!");
				break;
			case 5:
				lines.add("");
				lines.add("I returned to the temple and found the real Drezel locked in a cell.");
				lines.add("The cell was guarded by a vampyre!");
				break;
			case 6, 7, 8:
				break;
			case 9:
				lines.add("");
				lines.add("I used a key from a monument to open the cell door,");
				lines.add("and used Holy Water to trap the vampyre in his coffin.");
				break;
			case 10:
				lines.add("");
				lines.add("I followed Drezel to the mausoleum only to fine the Salve contaminated");
				lines.add("and in need of purification.");
				break;
			case 11:
				lines.add("");
				lines.add("I brought Drezel fifty rune essences and the contaminats were dissolved");
				lines.add("from the Salve.");
				lines.add("Drezel rewarded me for my help with an ancient Holy Weapon");
				break;
			default:
				lines.add("Invalid quest stage. Report this to an administrator.");
				break;
		}
		return lines;
	}

	@Override
	public void updateStage(Player player, int stage) {
		if (stage == 2) {
			player.getVars().setVar(302, 2);
		}
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.PRAYER,1406);
		player.getInventory().addItem(2952, 1, true);
		sendQuestCompleteInterface(player, 2952);
	}

	@Override
	public String getStartLocationDescription() {
		return "Talk to King Roald in the Varrock Palace.";
	}

	@Override
	public String getRequiredItemsString() {
		return
				"50 unnoted rune essence or pure essence and a Bucket.";
	}

	@Override
	public String getCombatInformationString() {
		return "You will need to defeat a level 30 enemy.";
	}

	@Override
	public String getRewardsString() {
		return "1,406 Prayer XP<br>"+
				"The Wolfbane Dagger<br>"+
				"Access to Morytania.";
	}

	public static ObjectClickHandler handleTempleDoor = new ObjectClickHandler(new Object[] { 30707, 30708 }, e -> {
		if(e.getOption().equalsIgnoreCase("open"))
			if(e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 4){
				handleDoubleDoor(e.getPlayer(), e.getObject());
				return;
			}
			else {
				e.getPlayer().simpleDialogue("The door is securely locked.");
				return;
			}
		if (e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 0 || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 4) {
			e.getPlayer().sendMessage("You knock at the door...");
			e.getPlayer().sendMessage("Doesn't seem like anyone's home.");
			return;
		}
		if (!e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) && e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 1) {
			e.getPlayer().sendMessage("You knock at the door...");
			e.getPlayer().sendMessage("You hear a voice from inside.");
			new TempleDoorD(e.getPlayer());
		}
	});

	public static ObjectClickHandler handleDungeonGate = new ObjectClickHandler(new Object[] { 3444, Tile.of(3405, 9895, 0), }, e -> {
		if(e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 3){
			handleGate(e.getPlayer(), e.getObject());
		}
		else {
			e.getPlayer().startConversation(new Dialogue() .addNPC(7711, HeadE.CAT_SHOUTING, "Grrr..."));
			e.getPlayer().sendMessage("It looks like the dog is protecting the gate.");
		}
	});

	public static ObjectClickHandler handleDrezelDungeonGate = new ObjectClickHandler(new Object[] { 3445, Tile.of(3431, 9897, 0), }, e -> {
		if (e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 9) {
			handleGate(e.getPlayer(), e.getObject());
		} else
			e.getPlayer().startConversation(new Dialogue()
					.addSimple("The gate is securely locked.")
			);
	});

	public static ObjectClickHandler handleHolyBarrier = new ObjectClickHandler(new Object[] { 3443 }, e -> {
		if(e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL))
			e.getPlayer().ladder(Tile.of(3423, 3484, 0));
		else {
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(Drezel, HeadE.ANGRY, "STOP!")
					.addPlayer(HeadE.CONFUSED, "Can't I go through there?")
					.addNPC(Drezel, HeadE.ANGRY, "No, you cannot! It is taking all of my willpower to hold that barrier in place. You must restore the sanctity of the Salve as soon as possible!")
			);
		}
	});

	public static ObjectClickHandler handleWell = new ObjectClickHandler(new Object[] { 3485 }, Tile.of(3423, 9890, 0), e -> {
		if(!e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL))
			e.getPlayer().sendMessage("You look down the well and see the filthy polluted water of the River Salve moving slowly along.");
		else
			e.getPlayer().sendMessage("You look down the well and see the fresh water of the River Salve moving slowly along.");
	});

	public static ItemOnObjectHandler handleBucketOnWell = new ItemOnObjectHandler(new Object[] { 3485 }, new Object[] { 1925 }, e -> {
		if (!e.getPlayer().isQuestStarted(Quest.PRIEST_IN_PERIL)) {
			e.getPlayer().sendMessage("This water is filthy, I best leave it alone.");
			return;
		}

		if (e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 7 || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 8) {
			e.getPlayer().getInventory().replace(1925, 2953);
			e.getPlayer().sendMessage("This water doesn't look particularly holy to me... I think I'd better check with Drezel first.");
			e.getPlayer().getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 8);
			return;
		}

		if (e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 9 || e.getPlayer().isQuestComplete(Quest.PRIEST_IN_PERIL))
			e.getPlayer().getInventory().replace(1925, 1929);

	});

	public static NPCDeathHandler handleMonkKeys = new NPCDeathHandler(new Object[] {1044, 1045, 1046}, e -> {
		if (e.getKiller() instanceof Player player && (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 4 || player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 5))
			World.addGroundItem(new Item(2944), e.getNPC().getTile(), player);
	});

	public static ObjectClickHandler handleNorthStair = new ObjectClickHandler(new Object[] { 30725 }, Tile.of(3415, 3491, 1), e -> {
		e.getPlayer().useStairs(827, Tile.of(3414, 3491, 0), 1, 2);
	});

	public static ObjectClickHandler handleSouthStair = new ObjectClickHandler(new Object[] { 30723 }, Tile.of(3415, 3486, 1), e -> {
		e.getPlayer().useStairs(827, Tile.of(3414, 3486, 0), 1, 2);
	});

	public static ObjectClickHandler HandleCoffin = new ObjectClickHandler( new Object[] { 30728 }, e -> {
		if (e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 8)
			e.getPlayer().sendMessage("The vampyre should be dealt with. I'd still best not risk it though.");
		else
			e.getPlayer().sendMessage("It sounds like there's something alive inside it. I don't think it would be a very good idea to open it...");
	});

	public static ObjectClickHandler handleJailDoor = new ObjectClickHandler(new Object[] { 3463 }, Tile.of(3413,3487,2), e -> {
		final int Drezel = 1047;
		if(e.getOption().equalsIgnoreCase("Talk-through")) {
			if (e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) ||  e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 6) {
				handleGate(e.getPlayer(), e.getObject());
				return;
			}
			else
				new DrezelD(e.getPlayer());
		}
		if(e.getOption().equalsIgnoreCase("Open")) {
			if (e.getPlayer().getQuestManager().isComplete(Quest.PRIEST_IN_PERIL) || e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) >= 6) {
				handleGate(e.getPlayer(), e.getObject());
				return;
			}
			if (e.getPlayer().getInventory().containsItem(2944)) {
				e.getPlayer().sendMessage("This key doesn't fit.");
				return;
			}
			if (e.getPlayer().getInventory().containsItem(2945)) {
				e.getPlayer().startConversation( new Dialogue()
						.addNPC(Drezel, HeadE.CALM_TALK, "Oh! Thank you! You have found the key!")
				);
				e.getPlayer().getInventory().deleteItem(2945,1);
				e.getPlayer().getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 7);
				handleGate(e.getPlayer(), e.getObject());
				return;
			}
			else
				new DrezelD(e.getPlayer());

		}
	});

	public static ItemOnObjectHandler handleBucketOnCoffin = new ItemOnObjectHandler(new Object[] { 30728 }, new Object[] { 1929, 2953, 2954 }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 8) {
			if (e.getItem().getId() == 1929) {
				e.getPlayer().sendMessage("I don't think pouring normal water on the coffin is going to help...");
				return;
			}
			if (e.getItem().getId() == 2953) {
				e.getPlayer().sendMessage("This water doesn't look particularly holy to me... I think I'd better check with Drezel first.");
				return;
			}
			if (e.getItem().getId() == 2954) {
				e.getPlayer().sendMessage("You pour the blessed water over the coffin...");
				e.getPlayer().getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 9);
				e.getPlayer().getInventory().replace(2954, 1925);
				e.getPlayer().setNextAnimation(new Animation(2771));
			}
		}
	});

	public static ObjectClickHandler HandleMonuments = new ObjectClickHandler(Arrays.stream(Monuments.values()).map(monuments -> monuments.monumentID).toArray(), e -> {
		Player player = e.getPlayer();
		if (e.getOption().equalsIgnoreCase("study")) {
			player.getInterfaceManager().sendInterface(272);
			player.getPackets().setIFText(272, 17, Monuments.getMonumentByTile(e.getObject().getTile()).message);
			if(player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getB(String.valueOf(Monuments.getMonumentByTile(e.getObject().getTile())))){
				player.getPackets().setIFItem(272, 4, Monuments.getMonumentByTile(e.getObject().getTile()).itemID, 1);
				player.sendMessage("This monument holds a " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).itemID).getUidName().replaceAll("_", " "));
			}
			else {
				player.getPackets().setIFItem(272, 4, Monuments.getMonumentByTile(e.getObject().getTile()).goldenID, 1);
				player.sendMessage("This monument holds a " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).goldenID).getUidName().replaceAll("_", " "));
			}
		}
		if(e.getOption().equalsIgnoreCase("take-from")) {
			if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 5) {
				if (!player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).getB(String.valueOf(Monuments.getMonumentByTile(e.getObject().getTile())))) {
					if (player.getInventory().containsItem(Monuments.getMonumentByTile(e.getObject().getTile()).itemID)) {
						player.getInventory().replace(Monuments.getMonumentByTile(e.getObject().getTile()).itemID, Monuments.getMonumentByTile(e.getObject().getTile()).goldenID);
						player.sendMessage("You take the " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).goldenID).getUidName().replaceAll("_", " ") + " from the statue leaving your " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).itemID).getUidName().replaceAll("_", " ") + " in its place.");
						player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setB(String.valueOf(Monuments.getMonumentByTile(e.getObject().getTile())), true);
					} else {
						player.applyHit(new Hit(player, 3, Hit.HitLook.TRUE_DAMAGE));
						player.sendMessage("A holy power prevents you stealing from the monument.");
					}
				} else {
					if (player.getInventory().containsItem(Monuments.getMonumentByTile(e.getObject().getTile()).goldenID)) {
						player.getInventory().replace(Monuments.getMonumentByTile(e.getObject().getTile()).goldenID, Monuments.getMonumentByTile(e.getObject().getTile()).itemID);
						player.sendMessage("You take the " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).itemID).getUidName().replaceAll("_", " ") + " from the statue leaving your " + ItemConfig.get(Monuments.getMonumentByTile(e.getObject().getTile()).goldenID).getUidName().replaceAll("_", " ") + " in its place.");
						player.getQuestManager().getAttribs(Quest.PRIEST_IN_PERIL).setB(String.valueOf(Monuments.getMonumentByTile(e.getObject().getTile())), false);
					} else {
						player.applyHit(new Hit(player, 3, Hit.HitLook.TRUE_DAMAGE));
						player.sendMessage("A holy power prevents you stealing from the monument.");
					}
				}
			}
			else {
				player.applyHit(new Hit(player, 3, Hit.HitLook.TRUE_DAMAGE));
				player.sendMessage("A holy power prevents you stealing from the monument.");
			}
		}
	});

}

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
package com.rs.game.content.world.areas.seers_village;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.holygrail.HolyGrail;
import com.rs.game.content.quests.holygrail.dialogue.KingArthurHolyGrailD;
import com.rs.game.content.quests.holygrail.dialogue.MerlinHolyGrailD;
import com.rs.game.content.quests.holygrail.dialogue.knightsroundtable.*;
import com.rs.game.content.quests.merlinscrystal.KingArthurMerlinsCrystalD;
import com.rs.game.content.quests.merlinscrystal.knightsroundtable.*;
import com.rs.game.content.quests.scorpioncatcher.ScorpionCatcher;
import com.rs.game.content.quests.scorpioncatcher.SeerScorpionCatcherD;
import com.rs.game.content.quests.scorpioncatcher.ThormacScorpionCatcherD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.world.doors.Doors.handleDoor;

@PluginEventHandler
public class SeersVillage {

	public static NPCClickHandler handleStankers = new NPCClickHandler(new Object[] { 383 }, e -> {
		e.getPlayer().startConversation(new Dialogue()
				.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?")
				.addOptions("What would you like to say?", ops -> {
					ops.add("About the Achievement System...", new AchievementSystemDialogue(e.getPlayer(), e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
				}));
	});
	
	public static ObjectClickHandler beehives = new ObjectClickHandler(new Object[] { 68 }, e -> {
		if (e.getPlayer().getInventory().containsItem(28)) {
			if (e.getPlayer().getInventory().containsItem(1925)) {
				e.getPlayer().setNextAnimation(new Animation(833));
				e.getPlayer().lock(1);
				e.getPlayer().getInventory().deleteItem(1925, 1);
				e.getPlayer().getInventory().addItem(30, 1);
			} else
				e.getPlayer().sendMessage("You need a bucket to gather the wax into.");
		} else {
			e.getPlayer().setNextAnimation(new Animation(833));
			e.getPlayer().lock(1);
			e.getPlayer().setNextForceTalk(new ForceTalk("Ouch!"));
			e.getPlayer().applyHit(new Hit(10, HitLook.TRUE_DAMAGE));
			e.getPlayer().sendMessage("The bees sting your hands as you reach inside!");
		}
	});
	

	public static ObjectClickHandler grubersWoodFence = new ObjectClickHandler(new Object[] { 51 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		p.forceMove(p.getX() < obj.getX() ? Tile.of(2662, 3500, 0) : Tile.of(2661, 3500, 0), 3844, 25, 75);
	});

	public static ObjectClickHandler grubersShedDoor = new ObjectClickHandler(new Object[] { 99 }, e -> {
		if(e.getPlayer().getInventory().containsItem(85) || e.getPlayer().getY() <= 3496) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		e.getPlayer().sendMessage("It is locked...");
	});

	public static NPCClickHandler handleSeer = new NPCClickHandler(new Object[] { 388 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.DRUNK, "Uh, what was that dark force? I've never sensed anything like it...");
				addNPC(e.getNPCId(), HeadE.NO_EXPRESSION, "Anyway, sorry about that.");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						if (e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS)
							option("About Scorpion Catcher", new SeerScorpionCatcherD(e.getPlayer()).getStart());
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
					}
				});
			}
		});
	});
	
	public static int[] battlestaves = { 1397, 1395, 1399, 1393, 3053, 6562, 11736 };
	public static int[] mystics = { 1405, 1403, 1407, 1401, 3054, 6563, 11738 };

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(332, e -> {
		int staffIdx = e.getComponentId()-21;
		if (e.getPlayer().getInventory().hasCoins(40000) && e.getPlayer().getInventory().containsItem(battlestaves[staffIdx], 1)) {
			e.getPlayer().getInventory().removeCoins(40000);
			e.getPlayer().getInventory().deleteItem(battlestaves[staffIdx], 1);
			e.getPlayer().getInventory().addItem(mystics[staffIdx], 1);
		} else
			e.getPlayer().sendMessage("You need 40,000 coins and a battlestaff of the correct type to enchant.");
	});

	public static NPCClickHandler handleThormacDialogue = new NPCClickHandler(new Object[] { 389 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.SCORPION_CATCHER))
			e.getPlayer().startConversation(new ThormacScorpionCatcherD(e.getPlayer()).getStart());
		else
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.HAPPY_TALKING, "You said you would give me mystic battlestaffs for enchanted battlestaffs for 40k...");
					addNext(() -> player.getInterfaceManager().sendInterface(332));
					create();
				}
			});
	});

	//---Camelot Castle

	public static NPCClickHandler handleSirKay = new NPCClickHandler(new Object[] { 241 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.SEERS_HEADBAND).getStart());
						if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED)
							option("About Holy Grail", new Dialogue()
									.addNext(()->{e.getPlayer().startConversation(new SirKayHolyGrailD(e.getPlayer()).getStart());}));
						if (!player.isQuestComplete(Quest.MERLINS_CRYSTAL))
							option("About Merlin's Crystal", new Dialogue()
									.addNext(()->{e.getPlayer().startConversation(new SirKayMerlinsCrystalD(e.getPlayer()).getStart());}));
					}
				});
			}
		});
	});

	public static NPCClickHandler handleSirBedivere = new NPCClickHandler(new Object[] { 242 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirBedivereHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirBedivereMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleSirGawain = new NPCClickHandler(new Object[] { 240 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirGawainHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirGawainMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleSirLancelot = new NPCClickHandler(new Object[] { 239 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirLancelotHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirLancelotMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleSirLucan = new NPCClickHandler(new Object[] { 245 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirLucanHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirLucanMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleSirPalomedes = new NPCClickHandler(new Object[] { 3787 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirPalomedesHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirPalomedesMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleSirPelleas = new NPCClickHandler(new Object[] { 244 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirPelleasHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirPelleasMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleTristram = new NPCClickHandler(new Object[] { 243 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new SirTristamHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new SirTristramMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleKingArthur = new NPCClickHandler(new Object[] { 251 }, e -> {
		if(e.getPlayer().isQuestComplete(Quest.MERLINS_CRYSTAL)) {//After merlins crystal is holy grail...
			e.getPlayer().startConversation(new KingArthurHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		e.getPlayer().startConversation(new KingArthurMerlinsCrystalD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleMerlin = new NPCClickHandler(new Object[] { 213 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) > HolyGrail.NOT_STARTED) {
			e.getPlayer().startConversation(new MerlinHolyGrailD(e.getPlayer()).getStart());
			return;
		}
		if(e.getPlayer().isQuestComplete(Quest.MERLINS_CRYSTAL))
			e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Thank you for saving me from that crystal!"));
	});

	//--End Camelot Castle

	public static ObjectClickHandler handleRoofLadder = new ObjectClickHandler(new Object[] { 26118, 26119 }, e -> {
		e.getPlayer().ladder(e.getPlayer().transform(0, 0, e.getObjectId() == 26118 ? 2 : -2));
	});

	public static ObjectClickHandler handleIkovTrapDoor = new ObjectClickHandler(new Object[] { 6278 }, e -> {
		e.getPlayer().sendMessage("It appears locked from the inside...");
	});

	public static ObjectClickHandler handleCoalTruckLogBalance = new ObjectClickHandler(new Object[] { 2296 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 20))
			return;
		AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 1 ? -5 : 5, 0, 0), 4);
	});

	public static ObjectClickHandler handleSinclairMansionLogBalance = new ObjectClickHandler(new Object[] { 9322, 9324 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 48))
			return;
		AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9322 ? -4 : 4, 0), 3);
	});
}

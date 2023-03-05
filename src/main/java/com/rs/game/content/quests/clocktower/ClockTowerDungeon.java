package com.rs.game.content.quests.clocktower;

import static com.rs.game.content.world.doors.Doors.handleGate;
import static com.rs.game.content.world.doors.Doors.handleInPlaceSingleDoor;

import com.rs.game.World;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PickupItemHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class ClockTowerDungeon {
	public static ObjectClickHandler handleGateLever = new ObjectClickHandler(new Object[]{ 37 }, e -> {
		if(e.getPlayer().getTempAttribs().getB("ClockTowerGateOpenedRats") || e.getPlayer().getX() <= 2595) {
			handleGate(e.getPlayer(), e.getObject());
			return;
		}
		e.getPlayer().faceWest();
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SKEPTICAL_THINKING, "Perhaps one of those levers opens this door..."));
		e.getPlayer().sendMessage("The door is shut...");
	});

	public static ObjectClickHandler handleGateLeverRats = new ObjectClickHandler(new Object[]{ 33 }, Tile.of(2591, 9661, 0), e -> {
		e.getObject().setIdTemporary(34, Ticks.fromSeconds(20));
		e.getPlayer().getTempAttribs().setB("ClockTowerGateOpenedRats", true);
	});

	public static ObjectClickHandler handlePoisonedRatDoor = new ObjectClickHandler(new Object[]{ 39 }, e -> {
		if(e.getPlayer().getX() <= 2578) {
			handleInPlaceSingleDoor(e.getPlayer(), e.getObject());
			return;
		}
		if(e.getPlayer().getTempAttribs().getB("ClockTowerPoisonedRats")) {
			handleInPlaceSingleDoor(e.getPlayer(), e.getObject());
			e.getPlayer().startConversation(new Dialogue().addSimple("The death of the rats seemed to shake the door open."));
			return;
		}
		e.getPlayer().sendMessage("The door is shut...");
	});

	public static PickupItemHandler handleBlackCogPickup = new PickupItemHandler(new Object[] { 21 }, Tile.of(2613, 9639, 0), e -> {
		if(e.getPlayer().getEquipment().getGlovesId() == 1580)//ice gloves
			return;
		if(e.getPlayer().getInventory().containsItem(1929, 1)) {//water bucket
			e.getPlayer().sendMessage("You cool the cog down in a bucket of water and the water evaporates...");
			e.getPlayer().getInventory().replaceItem(1925, 1, e.getPlayer().getInventory().getItems().lookupSlot(1929));
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.AMAZED, "Geez, that is hot!"));
		e.getPlayer().sendMessage("You need something to cool down the cog...");
		e.cancelPickup();
	});

	public static ItemOnObjectHandler handleRatTrap = new ItemOnObjectHandler(new Object[] { 40 }, e -> {
		if (e.getItem().getId() == 24) {
			e.getPlayer().getInventory().removeItems(new Item(24, 1));
			e.getPlayer().lock(5);
			for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
				if(npc.getId() == 224) {
					npc.setIgnoreNPCClipping(true);
					npc.walkToAndExecute(Tile.of(2586, 9655, 0), ()->{
						npc.setIgnoreNPCClipping(false);
						npc.sendDeath(e.getPlayer());
					});
				}
			e.getPlayer().getTempAttribs().setB("ClockTowerPoisonedRats", true);
		}
	});

	public static ItemOnObjectHandler handleUnusedSpindles = new ItemOnObjectHandler(new Object[] { 25, 26, 27, 28 }, e -> {
		if(e.getItem().getId() >= 20 && e.getItem().getId() <= 23) {
			e.getPlayer().sendMessage("The cog doesn't fit...");
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SKEPTICAL_THINKING, "What use is that?"));
	});

	public static ItemOnObjectHandler handleUsedSpindles = new ItemOnObjectHandler(new Object[] { 29, 30, 31, 32 }, e -> {
		if(e.getItem().getId() >= 20 && e.getItem().getId() <= 23) {
			if(e.getItem().getId() == 23 && e.getObjectId() == 29//red
			|| e.getItem().getId() == 22 && e.getObjectId() == 32//blue
			|| e.getItem().getId() == 21 && e.getObjectId() == 30//black
			|| e.getItem().getId() == 20 && e.getObjectId() == 31) {//white
				e.getPlayer().getInventory().removeItems(e.getItem());
				e.getPlayer().getQuestManager().getAttribs(Quest.CLOCK_TOWER).setB(e.getItem().getName() + "Done", true);
				e.getPlayer().sendMessage("The cog fits neatly into the spindle...");
				return;
			}
			e.getPlayer().sendMessage("The cog doesn't fit...");
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SKEPTICAL_THINKING, "What use is that?"));
	});
}

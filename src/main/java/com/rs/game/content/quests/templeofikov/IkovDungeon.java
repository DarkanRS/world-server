package com.rs.game.content.quests.templeofikov;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.quests.templeofikov.dialogues.GaurdianArmadylTempleOfIkov;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;
import com.rs.utils.Ticks;

import static com.rs.game.content.world.doors.Doors.handleDoor;
import static com.rs.game.content.world.doors.Doors.handleDoubleDoor;

@PluginEventHandler
public class IkovDungeon {
	
	public static NPCDeathHandler handlePendant = new NPCDeathHandler(new Object[] { 274, 275 }, e -> {
		if (e.getKiller() instanceof Player p && p.getQuestManager().isComplete(Quest.TEMPLE_OF_IKOV))
			e.getNPC().sendDrop(p, new Item(87));
	});
	
	public static ObjectClickHandler handleIkovEmergencyExitLadder = new ObjectClickHandler(new Object[] { 32015 }, Tile.of(2637, 9808, 0), e -> e.getPlayer().useLadder(Tile.of(2637, 3409, 0)));

	public static ItemOnObjectHandler leverIceDoor = new ItemOnObjectHandler(false, new Object[] { 86 }, new Object[] { 83 }, e -> {
		e.getPlayer().getInventory().removeItems(new Item(83, 1));
		e.getObject().setIdTemporary(e.getObjectId() + 1, Ticks.fromSeconds(30));
	});

	public static ObjectClickHandler handleIceLever = new ObjectClickHandler(new Object[]{ 87 }, e -> {
		//e.getObject().setId(36);
		e.getPlayer().sendMessage("You pull the lever and hear a click.");
		if(e.getPlayer().getQuestManager().getStage(Quest.TEMPLE_OF_IKOV) == TempleOfIkov.HELP_LUCIEN)
			e.getPlayer().getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).setB("LeverIcePulled", true);
	});

	public static ObjectClickHandler handleLadderToMcGruborsShed = new ObjectClickHandler(new Object[]{ 32015 }, Tile.of(2659, 9892, 0), e -> e.getPlayer().useLadder(Tile.of(2658, 3492, 0)));

	public static ObjectClickHandler handleFireWarriorDoorWithFight = new ObjectClickHandler(new Object[]{ 93 }, e -> {
		if(e.getPlayer().getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).getB("FireWarriorKilled")) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 3))
			if(npc instanceof FireWarrior warrior && warrior.getOwner() == e.getPlayer())
				return;
		NPC warrior = new FireWarrior(e.getPlayer(), 277, e.getPlayer().getTile().transform(0, -1));
		warrior.faceEntity(e.getPlayer());
		warrior.forceTalk("You will not pass!");
		warrior.setRandomWalk(false);
	});

	public static ObjectClickHandler handleArmadylWall = new ObjectClickHandler(new Object[]{1586}, e -> Doors.handleDoor(e.getPlayer(), e.getObject(), -1));

	public static PickupItemHandler handleArmaStaffPickup = new PickupItemHandler(new Object[] { 84 }, Tile.of(2638, 9906, 0), e -> {
		for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 3))
			if (npc.getName().contains("Guardian of Armadyl") && npc.lineOfSightTo(e.getPlayer(), false)) {
				e.cancelPickup();
				e.getPlayer().startConversation(new Dialogue().addSimple("An Armadyl Guardian glares at you..."));
			}
	});

	public static NPCClickHandler handleFireWarriorTalk = new NPCClickHandler(new Object[]{277}, new String[]{"Talk-to"}, e -> {
		int NPC = e.getNPCId();
		e.getPlayer().startConversation(new Dialogue()
			.addNPC(NPC, HeadE.ANGRY, "Who dares to enter the Temple of Ikov!")
			.addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("A mighty hero!", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "A mighty hero!")
							.addNPC(NPC, HeadE.CALM_TALK, "Pathetic fool! Prepare to die!")
							.addNext(()->{
								for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 3))
									if(npc instanceof FireWarrior warrior && warrior.getOwner() == e.getPlayer())
										warrior.setTarget(e.getPlayer());
							})
					);
					option("A humble pilgrim.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "A humble pilgrim.")
							.addNPC(NPC, HeadE.CALM_TALK, "I haven't seen a pilgrim for thousands of years! Temple is closed!")
					);
				}
			}));
	});

	public static NPCClickHandler handleGaurdianTalk = new NPCClickHandler(new Object[]{274, 275}, new String[]{"Talk-to"}, e -> {
		if(e.getPlayer().getEquipment().getAmuletId() == 86) {//Lucien amulet
			e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Thou art a foul agent of Lucien! Such an agent must die!"));
			WorldTasks.delay(3, () -> e.getNPC().setTarget(e.getPlayer()));
			return;
		}
		e.getPlayer().startConversation(new GaurdianArmadylTempleOfIkov(e.getPlayer(), e.getNPC()).getStart());
	});

	public static ObjectClickHandler handleFireWarriorLever = new ObjectClickHandler(new Object[]{ 91 }, e -> {
		if(e.getOption().equals("Search for traps")) {
			if(e.getPlayer().getSkills().getLevel(Skills.THIEVING) >= 42) {
				e.getPlayer().getTempAttribs().setB("IkovLeverTrapDisabled", true);
				e.getPlayer().sendMessage("You disable to trap...");
				return;
			}
			e.getPlayer().sendMessage("This trap requires 42 thieving...");
		}
		if(e.getOption().equals("Pull")) {
			if(e.getPlayer().getTempAttribs().getB("IkovLeverTrapDisabled")) {
				e.getObject().setIdTemporary(36, Ticks.fromSeconds(20));
				e.getPlayer().getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).setB("IkovFireWarriorEntranceOpen", true);
				return;
			}
			e.getPlayer().applyHit(new Hit(20, Hit.HitLook.TRUE_DAMAGE));
			e.getPlayer().sendMessage("You activate a trap!");
		}
	});

	public static ObjectClickHandler handleIceArrowChests = new ObjectClickHandler(new Object[]{ 35123 }, e -> {
		if(e.getOption().equals("Search") && e.getPlayer().getTempAttribs().getL("IkovChest" + e.getObject().getX()) < World.getServerTicks()) {
			e.getPlayer().getInventory().addItem(78, Utils.random(0, 6));
			e.getPlayer().getTempAttribs().setL("IkovChest" + e.getObject().getX(), World.getServerTicks() + (long)Ticks.fromSeconds(30));
		}
	});

	public static ObjectClickHandler handleFireWarriorDoorByLever = new ObjectClickHandler(new Object[]{ 92 }, e -> {
		if(e.getPlayer().getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).getB("IkovFireWarriorEntranceOpen")) {
			handleDoor(e.getPlayer(), e.getObject());
			return;
		}
		e.getPlayer().sendMessage("The door is firmly locked...");
	});

	public static ObjectClickHandler handleFearGate = new ObjectClickHandler(new Object[]{ 94, 95 }, e -> {
		if(e.getPlayer().getEquipment().getAmuletId() == 86 || e.getPlayer().getY() >= e.getObject().getY()) {
			handleDoubleDoor(e.getPlayer(), e.getObject());
			return;
		}
		e.getPlayer().faceNorth();
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SCARED, "Gah!"));
		e.getPlayer().sendMessage("An immense feeling of terror overwhelms you...");
	});

	public static ObjectClickHandler handleSouthIceGate = new ObjectClickHandler(new Object[]{ 89, 90 }, e -> {
		if(e.getPlayer().isQuestComplete(Quest.TEMPLE_OF_IKOV) || e.getPlayer().getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).getB("LeverIcePulled")) {
			handleDoubleDoor(e.getPlayer(), e.getObject());
			return;
		}
		e.getPlayer().faceSouth();
		e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.SKEPTICAL_THINKING, "It seems locked..."));
		e.getPlayer().sendMessage("The door is firmly shut...");
	});

	public static PlayerStepHandler handleBridge = new PlayerStepHandler(new Tile[] { Tile.of(2650, 9828, 0), Tile.of(2650, 9829, 0), Tile.of(2647, 9828, 0), Tile.of(2647, 9829, 0) }, e -> {
		Player p = e.getPlayer();
		if(p.getTempAttribs().getB("CrossingIkovBridge"))
			return;
		p.lock(5);
		p.setRunHidden(false);
		p.getTempAttribs().setB("CrossingIkovBridge", true);
		WorldTasks.scheduleTimer(i -> {
			if(i == 1)
				p.addWalkSteps(Tile.of((e.getTile().getX() == 2650 ? 2647 : 2650), e.getTile().getY(), 0), 4, false);
			if(i == 2 && p.getWeight() > 0) {
				p.resetWalkSteps();
				p.sendMessage("The bridge gives way under the weight...");
				p.setNextAnimation(new Animation(4280));
				p.applyHit(new Hit(20, Hit.HitLook.TRUE_DAMAGE), 1);
			}
			if(i == 4  && p.getWeight() > 0) {
				p.sendMessage("Good thing the lava was shallow!");
				p.tele(Tile.of(2648, 9826, 0));
				p.setRunHidden(true);
				p.getTempAttribs().removeB("CrossingIkovBridge");
				return false;
			}
			if(i == 5) {
				p.setRunHidden(true);
				p.getTempAttribs().removeB("CrossingIkovBridge");
				return false;
			}
			return true;
		});
	});
}

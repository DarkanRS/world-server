package com.rs.game.content.quests.handlers.merlinscrystal;

import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class KeepLaFayeMerlinsCrystalLoc {
	public static ObjectClickHandler handleVariousStaircasesUp = new ObjectClickHandler(new Object[] { 25786 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			p.useStairs(-1, WorldTile.of(p.getX(), obj.getY()+3, p.getPlane() + 1), 0, 1);
		}
	};
	public static ObjectClickHandler handleVariousStaircasesDown = new ObjectClickHandler(new Object[] { 25787 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			p.useStairs(-1, WorldTile.of(p.getX(), obj.getY()-3, p.getPlane() - 1), 0, 1);
		}
	};

	public static ObjectClickHandler handleCrate = new ObjectClickHandler(new Object[] { 63 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == CONFRONT_KEEP_LA_FAYE)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addOptions("Hide in the crate?", new Options() {
							@Override
							public void create() {
								option("Yes", new Dialogue()
										.addNext(()->{p.getControllerManager().startController(new MerlinsCrystalCrateScene());}));
								option("No", new Dialogue());
							}
						});

						addSimple("Would you like to enter the crate?");
						create();
					}
				});
		}
	};

	public static PlayerStepHandler handleStrongholdFight = new PlayerStepHandler(
			WorldTile.of(2769, 3401, 2), WorldTile.of(2770, 3401, 2), WorldTile.of(2771, 3401, 2),
			WorldTile.of(2771, 3402, 2), WorldTile.of(2770, 3402, 2), WorldTile.of(2769, 3402, 2),
			WorldTile.of(2768, 3402, 2), WorldTile.of(2768, 3401, 2)) {
		final int MORDRED = 247;
		@Override
		public void handle(PlayerStepEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) != CONFRONT_KEEP_LA_FAYE)
				return;
			NPC mordred = null;
			for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
				if(npc.getId() == MORDRED) {
					mordred = npc;
					break;
				}
			if(mordred == null || mordred.getTarget() != null || !mordred.canAggroPlayer(p))
				return;
			mordred.forceTalk("You DARE invade MY stronghold?!?! Have at thee knave!!");
			mordred.setTarget(p);
		}
	};

	public static ObjectClickHandler handleFrontDoor = new ObjectClickHandler(new Object[] { 71, 72 }) {
		int NPC = 490;
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			e.getPlayer().sendMessage("The door is securely locked.");
			if(p.getX() > obj.getX())
				return;
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addSimple("You knock at the door. You hear a voice from inside...");
					addNPC(NPC, HeadE.CALM_TALK, "Yes? What do you want?");
					addPlayer(HeadE.HAPPY_TALKING, "Um...");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Pizza delivery!", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Pizza delivery!")
									.addNPC(NPC, HeadE.CALM_TALK, "We didn't order any Pizza. Get lost!")
									.addSimple("It looks like you'll have to find another way in...")
									);
							option("Ever considered letting Saradomin into your life?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Have you ever considered letting the glory of Saradomin into your life? I have some pamphlets you may be interested in reading and discussing with me.")
									.addNPC(NPC, HeadE.CALM_TALK, "No. Go away.")
									.addSimple("It looks like you'll have to find another way in...")
									);
							option("Can I interest you in some double glazing?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can I interest you in some double glazing? An old castle like this must get very draughty in the winter...")
									.addNPC(NPC, HeadE.CALM_TALK, "No. Get out of here before I run you through.")
									.addSimple("It looks like you'll have to find another way in...")
									);
							option("Would you like to buy some lucky leather?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Would you like to buy some lucky leather?")
									.addNPC(NPC, HeadE.CALM_TALK, "No. Go away")
									.addSimple("It looks like you'll have to find another way in...")
									);
						}
					});

					create();
				}
			});
		}
	};

	protected final static Set<Integer> STRONGHOLD_CHUNKS = new HashSet<>(Arrays.asList(5672264, 5688648, 5672256));
	public static EnterChunkHandler handleAgressiveKnights = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (e.getEntity() instanceof Player p && p.hasStarted() && STRONGHOLD_CHUNKS.contains(e.getChunkId())) {
				for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId())) {
					if (!npc.getName().equalsIgnoreCase("Renegade Knight") || !npc.lineOfSightTo(p, false))
						continue;
					npc.setTarget(p);
					if (Utils.random(0, 5) == 1)
						npc.forceTalk("Intruder!");
				}
			}
		}
	};
}

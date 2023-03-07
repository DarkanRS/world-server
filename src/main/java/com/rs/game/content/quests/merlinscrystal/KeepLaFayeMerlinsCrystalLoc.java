package com.rs.game.content.quests.merlinscrystal;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class KeepLaFayeMerlinsCrystalLoc {
	public static ObjectClickHandler handleVariousStaircasesUp = new ObjectClickHandler(new Object[] { 25786 }, e -> {
		GameObject obj = e.getObject();
		e.getPlayer().useStairs(-1, Tile.of(e.getPlayer().getX(), obj.getY()+3, e.getPlayer().getPlane() + 1), 0, 1);
	});
	
	public static ObjectClickHandler handleVariousStaircasesDown = new ObjectClickHandler(new Object[] { 25787 }, e -> {
		GameObject obj = e.getObject();
		e.getPlayer().useStairs(-1, Tile.of(e.getPlayer().getX(), obj.getY()-3, e.getPlayer().getPlane() - 1), 0, 1);
	});

	public static ObjectClickHandler handleCrate = new ObjectClickHandler(new Object[] { 63 }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == CONFRONT_KEEP_LA_FAYE)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addOptions("Hide in the crate?", new Options() {
						@Override
						public void create() {
							option("Yes", new Dialogue()
									.addNext(()->{e.getPlayer().getControllerManager().startController(new MerlinsCrystalCrateScene());}));
							option("No", new Dialogue());
						}
					});

					addSimple("Would you like to enter the crate?");
					create();
				}
			});
	});

	public static PlayerStepHandler handleStrongholdFight = new PlayerStepHandler(new Tile[] { Tile.of(2769, 3401, 2), Tile.of(2770, 3401, 2), Tile.of(2771, 3401, 2), Tile.of(2771, 3402, 2), Tile.of(2770, 3402, 2), Tile.of(2769, 3402, 2), Tile.of(2768, 3402, 2), Tile.of(2768, 3401, 2) }, e -> {
		if(e.getPlayer().getQuestManager().getStage(Quest.MERLINS_CRYSTAL) != CONFRONT_KEEP_LA_FAYE)
			return;
		NPC mordred = null;
		for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
			if(npc.getId() == 247) {
				mordred = npc;
				break;
			}
		if(mordred == null || mordred.getTarget() != null || !mordred.canAggroPlayer(e.getPlayer()))
			return;
		mordred.forceTalk("You DARE invade MY stronghold?!?! Have at thee knave!!");
		mordred.setTarget(e.getPlayer());
	});

	public static ObjectClickHandler handleFrontDoor = new ObjectClickHandler(new Object[] { 71, 72 }, e -> {
		GameObject obj = e.getObject();
		e.getPlayer().sendMessage("The door is securely locked.");
		if(e.getPlayer().getX() > obj.getX())
			return;
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addSimple("You knock at the door. You hear a voice from inside...");
				addNPC(490, HeadE.CALM_TALK, "Yes? What do you want?");
				addPlayer(HeadE.HAPPY_TALKING, "Um...");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Pizza delivery!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Pizza delivery!")
								.addNPC(490, HeadE.CALM_TALK, "We didn't order any Pizza. Get lost!")
								.addSimple("It looks like you'll have to find another way in...")
								);
						option("Ever considered letting Saradomin into your life?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Have you ever considered letting the glory of Saradomin into your life? I have some pamphlets you may be interested in reading and discussing with me.")
								.addNPC(490, HeadE.CALM_TALK, "No. Go away.")
								.addSimple("It looks like you'll have to find another way in...")
								);
						option("Can I interest you in some double glazing?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can I interest you in some double glazing? An old castle like this must get very draughty in the winter...")
								.addNPC(490, HeadE.CALM_TALK, "No. Get out of here before I run you through.")
								.addSimple("It looks like you'll have to find another way in...")
								);
						option("Would you like to buy some lucky leather?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Would you like to buy some lucky leather?")
								.addNPC(490, HeadE.CALM_TALK, "No. Go away")
								.addSimple("It looks like you'll have to find another way in...")
								);
					}
				});

				create();
			}
		});
	});

	protected final static Set<Integer> STRONGHOLD_CHUNKS = new HashSet<>(Arrays.asList(5672264, 5688648, 5672256));
	public static EnterChunkHandler handleAgressiveKnights = new EnterChunkHandler(e -> {
		if (e.getEntity() instanceof Player player && player.hasStarted() && STRONGHOLD_CHUNKS.contains(e.getChunkId())) {
			for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1)) {
				if (!npc.getName().equalsIgnoreCase("Renegade Knight") || !npc.lineOfSightTo(player, false))
					continue;
				npc.setTarget(player);
				if (Utils.random(0, 5) == 1)
					npc.forceTalk("Intruder!");
			}
		}
	});
}

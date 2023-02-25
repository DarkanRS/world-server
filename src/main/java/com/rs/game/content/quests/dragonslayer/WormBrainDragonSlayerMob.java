package com.rs.game.content.quests.dragonslayer;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.MAP_PART2;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.PREPARE_FOR_CRANDOR;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.WORM_BRAIN;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class WormBrainDragonSlayerMob extends Conversation {
	public WormBrainDragonSlayerMob(Player p) {
		super(p);
		addNPC(WORM_BRAIN, HeadE.FRUSTRATED, "Whut you want?");
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case PREPARE_FOR_CRANDOR -> {
			if(p.getInventory().containsItem(MAP_PART2, 1)) {
				addPlayer(HeadE.SKEPTICAL_THINKING, "Sorry I thought this was a zoo.");
				addNPC(745, HeadE.CALM, "...");
				return;
			}
			addPlayer(HeadE.CALM_TALK, "I believe you've got a piece of a map that I need.");
			addNPC(745, HeadE.SKEPTICAL_THINKING,  "So? Why should I be giving it to you? What you do for Wormbrain?");
			addOptions("Select an option:", new Options() {
				@Override
				public void create() {
					option("I'm not going to do anything for you. Forget it.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "I'm not going to do anything for you. Forget it.")
							.addNPC(745, HeadE.ANGRY, "Be dat way then"));
					option("I'll let you live. I could just kill you.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "I'll let you live. I could just kill you.")
							.addNPC(745, HeadE.LAUGH, "Ha! Me in here and you out dere. You not get map piece."));
					option("I suppose I could pay you for the map piece ... Say, 500 coins?", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "I suppose I could pay you for the map piece ... Say, 500 coins?")
							.addNPC(745, HeadE.FRUSTRATED, "Me not stooped, it worth at least 10,000 coins!")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									if(p.getInventory().hasCoins(10000))
										option("Aright then, 10,000 it is.", new Dialogue()
												.addSimple("You buy the map piece from Wormbrain.", ()->{
													p.getInventory().removeCoins(10000);
													p.getInventory().addItem(MAP_PART2, 1, true);
												})
												.addNPC(WORM_BRAIN, HeadE.HAPPY_TALKING, "Tank you very much! Now me can bribe da guards, hehehe."));
									else
										option("Darn, I don't have that.", new Dialogue()
												.addPlayer(HeadE.SAD, "Darn, I don't have that.")
												.addNPC(WORM_BRAIN, HeadE.CALM_TALK, "No map for you!"));
									option("You must be joking! Forget it", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "You must be joking! Forget it")
											.addNPC(WORM_BRAIN, HeadE.CALM_TALK, "Fine, you not get map piece"));
								}
							}));
					option("Where did you get the map piece from?", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL_THINKING, "Where did you get the map piece from?")
							.addNPC(WORM_BRAIN, HeadE.HAPPY_TALKING, "We rob house of stupid wizard. She very old, not put up much fight at all. Hahaha!")
							.addPlayer(HeadE.SECRETIVE, "Uh ... Hahaha.")
							.addNPC(WORM_BRAIN, HeadE.CALM_TALK, "Her house full of pictures of a city on island and old pictures of people. Me not recognise " +
									"island. Me find map piece. Me not know what it is, but it in locked box so me figure it important.")
							.addNPC(WORM_BRAIN, HeadE.CALM_TALK, "But, by the time me get box open, other goblins gone. Then me not run fast enough and " +
									"guards catch me. But now you want map piece so must be special! What do for me to get it?"));
				}
			});
			break;
		}
		default -> {
			addPlayer(HeadE.SKEPTICAL_THINKING, "Sorry I thought this was a zoo.");
			addNPC(WORM_BRAIN, HeadE.CALM, "...");
		}
		}
	}

	public static NPCClickHandler handleWormBrain = new NPCClickHandler(new Object[] { WORM_BRAIN }, e -> e.getPlayer().startConversation(new WormBrainDragonSlayerMob(e.getPlayer()).getStart()));

	public static NPCDeathHandler handleWormBrainDrop = new NPCDeathHandler(WORM_BRAIN, e -> {
		if(e.killedByPlayer()) {
			Player p = (Player) e.getKiller();
			if(p.getQuestManager().getStage(Quest.DRAGON_SLAYER) == PREPARE_FOR_CRANDOR && !p.getInventory().containsItem(MAP_PART2, 1))
				World.addGroundItem(new Item(MAP_PART2, 1), Tile.of(e.getNPC().getTile()), (Player) e.getKiller());
		}
	});

	public static ObjectClickHandler handleWormBrainJailGate = new ObjectClickHandler(new Object[] { 40184 }, e -> e.getPlayer().sendMessage("It is firmly shut..."));

	public static NPCInteractionDistanceHandler wormbrainDistance = new NPCInteractionDistanceHandler(WORM_BRAIN, (player, npc) -> 1);
}

package com.rs.game.content.quests.merlinscrystal;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.*;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class BeggarMerlinsCrystalD extends Conversation {
	final static int NPC=252;
	final static int BREAD = 2309;
	final static int LADYLAKE=250;
	public BeggarMerlinsCrystalD(Player player) {
		super(player);
		if(player.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB("LADY_TEST")) {
			if (player.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB("KNOWS_BEGGAR")) {
				if (player.getInventory().containsItem(BREAD, 1)) {
					addNPC(NPC, HeadE.CALM_TALK, "Do you have the bread now?");
					addNext(() -> {
						player.startConversation(new BeggarMerlinsCrystalD(player, true).getStart());
					});
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "Have you got any bread for me yet?");
					addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
				}
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "Please... my family and I are starving... Could you find it in your heart to spare me a simple loaf of bread?");
				if(player.getInventory().containsItem(BREAD, 1))
					addNext(() -> {
						player.startConversation(new BeggarMerlinsCrystalD(player, true).getStart());
					});
				else
					addPlayer(HeadE.HAPPY_TALKING, "... except I don't have any bread on me at the moment...");
			}
		} else {
			addNPC(NPC, HeadE.CALM_TALK, "Hold on, I am testing someone to see if they are worthy.");
			addPlayer(HeadE.HAPPY_TALKING, "Weird, but okay");
		}



	}

	public BeggarMerlinsCrystalD(Player p, boolean filler) {
		super(p);
		NPC buff = null;
		for(NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
			if(npc.getId() == NPC)
				buff = npc;
		NPC beggar = buff;
		addPlayer(HeadE.HAPPY_TALKING, "Yes certainly");
		addSimple("You give the beggar some bread");
		addNPC(LADYLAKE, HeadE.CALM_TALK, "Well done. You have passed my test. Here is Excalibur, guard it well.", ()->{
			p.getInventory().removeItems(new Item(BREAD, 1));
			p.getInventory().addItem(new Item(EXCALIBUR, 1));
			p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, PERFORM_RITUAL);
			if(beggar != null) {
				beggar.transformIntoNPC(LADYLAKE);
				beggar.forceTalk("Well done!");
				beggar.finishAfterTicks(4);
			}
		});
	}
	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new BeggarMerlinsCrystalD(e.getPlayer()).getStart()));

	public static PlayerStepHandler handleBeggar = new PlayerStepHandler(Tile.of(3016, 3246, 0), e -> {
		Player p = e.getPlayer();
		if((p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) != OBTAINING_EXCALIBUR) || !p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB("LADY_TEST"))
			return;
		for(NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
			if(npc.getId() == 252)
				return;
        OwnedNPC beggar = new OwnedNPC(p, 252, Tile.of(3016, 3247, 0), true);
		beggar.setNextSpotAnim(new SpotAnim(1605));
		beggar.forceTalk("Hey!");
	});
}

package com.rs.game.content.quests.handlers.merlinscrystal;

import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.EXCALIBUR;
import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.LADY_LAKE_TEST_ATTR;
import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.OBTAINING_EXCALIBUR;
import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.PERFORM_RITUAL;
import static com.rs.game.content.quests.handlers.merlinscrystal.MerlinsCrystal.PLAYER_KNOWS_BEGGAR_ATTR;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class BeggarMerlinsCrystalD extends Conversation {
	final static int NPC=252;
	final static int BREAD = 2309;
	final static int LADYLAKE=250;
	public BeggarMerlinsCrystalD(Player p) {
		super(p);
		if(p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(LADY_LAKE_TEST_ATTR)) {
			if (p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(PLAYER_KNOWS_BEGGAR_ATTR)) {
				if (p.getInventory().containsItem(BREAD, 1)) {
					addNPC(NPC, HeadE.CALM_TALK, "Do you have the bread now?");
					addNext(() -> {
						p.startConversation(new BeggarMerlinsCrystalD(p, true).getStart());
					});
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "Have you got any bread for me yet?");
					addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
				}
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "Please... my family and I are starving... Could you find it in your heart to spare me a simple loaf of bread?");
				if(p.getInventory().containsItem(BREAD, 1))
					addNext(() -> {
						p.startConversation(new BeggarMerlinsCrystalD(p, true).getStart());
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
		for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
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
	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new BeggarMerlinsCrystalD(e.getPlayer()).getStart());
		}
	};

	public static PlayerStepHandler handleBeggar = new PlayerStepHandler(WorldTile.of(3016, 3246, 0)) {
		final int BEGGAR = 252;
		@Override
		public void handle(PlayerStepEvent e) {
			Player p = e.getPlayer();
			if((p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) != OBTAINING_EXCALIBUR) || !p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(LADY_LAKE_TEST_ATTR))
				return;
			for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
				if(npc.getId() == BEGGAR)
					return;
            OwnedNPC beggar = new OwnedNPC(p, BEGGAR, WorldTile.of(3016, 3247, 0), true);
			beggar.setNextSpotAnim(new SpotAnim(1605));
			beggar.forceTalk("Hey!");
		}
	};
}

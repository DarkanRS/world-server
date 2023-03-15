package com.rs.game.content.quests.templeofikov.dialogues;

import com.rs.game.World;
import com.rs.game.content.skills.magic.Magic;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WineldaWitchTempleOfIkov extends Conversation {
	private static final int NPC = 276;
	public WineldaWitchTempleOfIkov(Player player) {
		super(player);
		Dialogue talkAboutSpell = new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "I'm knowing some magic trickesses! I could get over easy as that! Don't tell them! They always come! They pester poor Winelda!")
				.addPlayer(HeadE.HAPPY_TALKING, "If you're such a great witch, get me over!")
				.addNPC(NPC, HeadE.CALM_TALK, "See? They pester Winelda!")
				.addPlayer(HeadE.HAPPY_TALKING, "I can do something for you!")
				.addNPC(NPC, HeadE.CALM_TALK, "Good! Don't pester, help! Get Winelda 20 limpwurt roots for my pot. Then we shows them some magic!");
		if(player.getInventory().containsItem(225, 20))
			talkAboutSpell.addPlayer(HeadE.HAPPY_TALKING, "Okay, here are your limpwurt roots.")
					.addNPC(NPC, HeadE.CALM_TALK, "Ooh, they're well prepared! Hehe!")
					.addNPC(NPC, HeadE.CALM_TALK, "Good! Good! My potion is nearly ready! Bubble, bubble, toil and trouble! Now we shows them ours magic! Hold on tight!")
					.addNext(()->{
						for(NPC npc : World.getNPCsInChunkRange(player.getChunkId(), 2))
							if(npc.getId() == 276) {
								npc.faceEntity(player);
								npc.setNextAnimation(new Animation(711));
								npc.setNextSpotAnim(new SpotAnim(108));
								WorldTasks.delay(1, () -> {
									Magic.sendNormalTeleportSpell(player, Tile.of(2663, 9878, 0));
									player.getInventory().removeItems(new Item(225, 20));
								});
							}
					});
		else
			talkAboutSpell.addPlayer(HeadE.HAPPY_TALKING, "Alright, I'll come back when I've got enough roots.");
		addNPC(NPC, HeadE.CALM_TALK, "Hehe! We see you're in a pickle! Wants to be getting over the nasty lava do we?");
		addOptions("Do you want to go over the lava?", new Options() {
			@Override
			public void create() {
				option("Nah, not bothered!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Nah, not bothered!")
						.addNPC(NPC, HeadE.CALM_TALK, "Hehe! Ye'll come back! They always come back!")
				);
				option("Yes we do!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Yes we do!")
						.addNPC(NPC, HeadE.CALM_TALK, "Mocking us are we? Clever one aren't we?")
						.addNext(talkAboutSpell)
				);
				option("Yes I do!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Yes I do!")
						.addNext(talkAboutSpell)
				);
			}
		});
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new WineldaWitchTempleOfIkov(e.getPlayer()).getStart()));
}

package com.rs.game.content.quests.tribaltotem;

import static com.rs.game.content.quests.tribaltotem.TribalTotem.GET_TOTEM;
import static com.rs.game.content.quests.tribaltotem.TribalTotem.REDIRECT_TELE_STONE;
import static com.rs.game.content.quests.tribaltotem.TribalTotem.TALK_TO_WIZARD;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WizardCrompertyTribalTotemD extends Conversation {
	private static final int NPC = 844;
	public WizardCrompertyTribalTotemD(Player p, NPC npc) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM)) {
		case GET_TOTEM -> {
			addPlayer(HeadE.HAPPY_TALKING, "Can I be teleported please?");
			addNPC(NPC, HeadE.CALM_TALK, "By all means! I'm afraid I can't give you any specifics as to where you will come out however. Presumably " +
					"wherever the other block is located.");
			addPlayer(HeadE.HAPPY_TALKING, "Yes, that sounds good. Teleport me!");
			addNPC(NPC, HeadE.CALM_TALK, "Okey dokey! Ready?");
			addNext(()->{
				npc.setNextForceTalk(new ForceTalk("Dipsolum sentento sententi!"));
				World.sendProjectile(npc, p, 50, 5, 5, 5, 1, 5, 0);
				p.lock(3);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						p.setNextTile(Tile.of(2642, 3321, 0));//Mansion in ardy
					}
				}, 2);
			});
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello Player, I'm Cromperty. Sedridor has told me about you. As a wizard and an inventor, he has aided me in" +
					" my great invention!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Two jobs? That's got to be tough", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Two jobs? That's got to be tough")
							.addNPC(NPC, HeadE.CALM_TALK, "Not when you combine them it isn't! Invent MAGIC things!")

							);
					option("So, what have you invented?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So, what have you invented?")
							.addNPC(NPC, HeadE.CALM_TALK, "Ah! My latest invention is my patent pending teleportation block! It emits a low level magical" +
									" signal, that will allow me to locate it anywhere in the world,")
							.addNPC(NPC, HeadE.CALM_TALK, "and teleport anything directly to it! I hope to revolutionize the entire teleportation system! " +
									"Don't you think I'm great? Uh, I mean it's great?", ()-> {
										if(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM) == TALK_TO_WIZARD)
											p.getQuestManager().setStage(Quest.TRIBAL_TOTEM, REDIRECT_TELE_STONE);
									})
							.addNPC(NPC, HeadE.CALM_TALK, "So... want me to teleport you to it?")
							.addOptions("Teleport?", new Options() {
								@Override
								public void create() {
									option("Yes", new Dialogue()
											.addNext(()->{
												NPC wizard = null;
												for(NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
													if(npc.getId() == NPC)
														wizard = npc;
												wizard.setNextForceTalk(new ForceTalk("Dipsolum sentento sententi!"));
												World.sendProjectile(wizard, p, 50, 5, 5, 5, 1, 5, 0);
												p.lock(3);
												WorldTasks.schedule(new WorldTask() {
													@Override
													public void run() {
														if(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM) >= GET_TOTEM)
															p.setNextTile(Tile.of(2642, 3321, 0)); //Mansion in ardy
														else
															p.setNextTile(Tile.of(2649, 3271, 0)); //RPDT crates in ardy
													}
												}, 2);
											}));
									option("No", new Dialogue());
								}
							})
							);

					option("So where is the other block?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So where is the other block?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well...Hmm. I would guess somewhere between here and the Wizards' Tower in Misthalin.")
							.addNPC(NPC, HeadE.CALM_TALK, "All I know is that it hasn't got there yet as the wizards there would have contacted me. " +
									"I'm using the RPDT for delivery. They assured me it would be delivered promptly.")
							.addPlayer(HeadE.HAPPY_TALKING, "Who are the RPDT?")
							.addNPC(NPC, HeadE.CALM_TALK, "The Runescape Parcel Delivery Team. They come very highly recommended. Their motto is: \"We " +
									"aim to deliver your stuff at some point after you have paid us!\"", ()-> {
										if(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM) == TALK_TO_WIZARD)
											p.getQuestManager().setStage(Quest.TRIBAL_TOTEM, REDIRECT_TELE_STONE);
									})
							);
				}
			});
		}
		}
	}



	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new WizardCrompertyTribalTotemD(e.getPlayer(), e.getNPC()).getStart()));
}

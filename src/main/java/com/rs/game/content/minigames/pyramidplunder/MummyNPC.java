package com.rs.game.content.minigames.pyramidplunder;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import java.util.*;

@PluginEventHandler
public class MummyNPC {
	private static final int[] ARTEFACT_IDS = {
			PPArtefact.IVORY_COMB.getArtefactId(),
			PPArtefact.POTTERY_SCARAB.getArtefactId(),
			PPArtefact.POTTERY_STATUETTE.getArtefactId(),
			PPArtefact.STONE_SEAL.getArtefactId(),
			PPArtefact.STONE_SCARAB.getArtefactId(),
			PPArtefact.STONE_STATUETTE.getArtefactId(),
			PPArtefact.GOLD_SEAL.getArtefactId(),
			PPArtefact.GOLD_SCARAB.getArtefactId(),
			PPArtefact.GOLD_STATUETTE.getArtefactId()
	};
	private static final int MUMMYID = 4476;
	private static final int[] PHARAOHS_SCEPTRE = new int[] { 9050, 9048, 9046, 9044 };

	public static ItemOnNPCHandler handleSceptreOnGuardianMummy = new ItemOnNPCHandler(new Object[] { 4476 }, e -> {
		if(e.getItem().getName().contains("Pharaoh's sceptre"))
			MummyRecharge(e.getPlayer());
	});

	private static boolean playerHasArtefacts(Player player) {
		for (int artefactId : ARTEFACT_IDS) {
			if (player.getInventory().containsItem(artefactId)) {
				return true;
			}
		}
		return false;
	}

	private static boolean playerHasSceptre(Player player) {
		for (int sceptreID : PHARAOHS_SCEPTRE) {
			if (player.getInventory().containsItem(sceptreID)) {
				return true;
			}
		}
		return false;
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{ MUMMYID }, e -> {
		Player player = e.getPlayer();
		if (e.getOption().equalsIgnoreCase("start-minigame")) {
			player.getControllerManager().startController(new PyramidPlunderController());
			return;
		}
		if (e.getOption().equalsIgnoreCase("talk-to"))
			player.startConversation(new Conversation(player) {
				{
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "*sigh* Not another one.");
					addPlayer(HeadE.CALM_TALK, "Another what?");
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Another 'archaeologist'. I'm not going to let you plunder my master's tomb you know.");
					addPlayer(HeadE.HAPPY_TALKING, "That's a shame. Have you got anything else I could do while I'm here?");
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "If it will keep you out of mischief I suppose I could set something up for you... I have a few rooms " +
							"full of some things you humans might consider valuable, do you want to give it a go?");
					addOptions(("What would you like to say?"), option -> {
						option.add("Play Pyramid Plunder?", new Dialogue()
								.addOptions(("What would you like to say?"), option2 -> {
									option2.add("That sounds like fun; what do I do?")
											.addPlayer(HeadE.HAPPY_TALKING, "That sounds like fun; what do I do?")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "You have five minutes to explore the treasure rooms and collect as many artefacts as " +
													"you can. The artefacts are in the urns, chests and sarcophagi found in each room.")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "There are eight treasure rooms, each subsequent room requires higher thieving skills to" +
													" both enter the room and thieve from the urns and other containers")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "The rewards also become more lucrative the further into the tomb you go. You will also have" +
													" to deactivate a trap in order to enter the main part of each room. ")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "When you want to move onto the next room you need to find the correct door first. " +
													"There are four possible exits... you must open the door before finding out whether it is the exit or not.")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Opening the doors require picking their locks. Having a lockpick will make this easier.")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Do you want to do it?")
											.addOptions(("Sure"), option3 -> {

												option3.add("I am ready to give it a go now.")
														.addNext(() -> {
															player.getControllerManager().startController(new PyramidPlunderController());
														});

												option3.add("Not right now.", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Not right now.")
														.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
												);
											});

									option2.add("I know what I'm doing, so let's get on with it.", new Dialogue()
											.addNext(() -> player.getControllerManager().startController(new PyramidPlunderController())));

									option2.add("Not right now.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Not right now.")
											.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
									);

								}));

						if(playerHasSceptre(player))
							option.add("I want to charge my sceptre", new Dialogue()
									.addNext(() -> MummyRecharge(e.getPlayer()))
							);

						if(playerHasArtefacts(player))
							option.add("I want to note my artefacts", new Dialogue()
									.addNext(() -> {
										for (int artefactId : ARTEFACT_IDS) {
											int count = player.getInventory().getNumberOf(artefactId);
											if (count > 0) {
												player.getInventory().removeItems(new Item(artefactId, count));
												player.getInventory().addItem(new Item(artefactId + 1, count));
											}
										}
									}));

						option.add("Not right now.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Not right now.")
								.addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
						);
					});
				}
			});
	});

	public static void MummyRecharge(Player player) {
		player.startConversation(new Conversation(player) {
			{
				if (player.getInventory().containsOneItem(PHARAOHS_SCEPTRE[3],PHARAOHS_SCEPTRE[2],PHARAOHS_SCEPTRE[1])) {
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "I'm not waisting the King's magic on a charged sceptre");
				}
				else {
					addPlayer(HeadE.CALM_TALK, "I want to charge my sceptre.");
					addNPC(MUMMYID, HeadE.CHILD_ANGRY_HEADSHAKE, "You shouldn't have that thing in the first place, thief!");
					addPlayer(HeadE.SKEPTICAL, "Hmm... If I give you back some of the artefacts I've taken from the tomb, will you recharge the sceptre for me?");
					addNPC(MUMMYID, HeadE.CHILD_ANGRY, "*sigh* Oh alright, but this is such a waste of the King's magic...");
					addOptions("What artefact would you like to use?", option -> {
						option.add("Gold artefacts?", new Dialogue()
								.addPlayer(HeadE.CALM, "I'd like to use some gold artefacts")
								.addNext(() -> rechargeSceptreWithArtefacts(player,
										Arrays.asList(
												new ArtefactInfo(PPArtefact.NOTED_GOLD_SEAL.getArtefactId(), 6, "gold"),
												new ArtefactInfo(PPArtefact.NOTED_GOLD_SCARAB.getArtefactId(), 6, "gold"),
												new ArtefactInfo(PPArtefact.NOTED_GOLD_STATUETTE.getArtefactId(), 6, "gold"),
												new ArtefactInfo(PPArtefact.GOLD_SEAL.getArtefactId(), 6, "gold"),
												new ArtefactInfo(PPArtefact.GOLD_SCARAB.getArtefactId(), 6, "gold"),
												new ArtefactInfo(PPArtefact.GOLD_STATUETTE.getArtefactId(), 6, "gold")
										)
								))
						);

						option.add("Stone artefacts?", new Dialogue()
								.addPlayer(HeadE.CALM, "I'd like to use some stone artefacts")
								.addNext(() -> rechargeSceptreWithArtefacts(player,
										Arrays.asList(
												new ArtefactInfo(PPArtefact.NOTED_STONE_SEAL.getArtefactId(), 12, "stone"),
												new ArtefactInfo(PPArtefact.NOTED_STONE_SCARAB.getArtefactId(), 12, "stone"),
												new ArtefactInfo(PPArtefact.NOTED_STONE_STATUETTE.getArtefactId(), 12, "stone"),
												new ArtefactInfo(PPArtefact.STONE_SEAL.getArtefactId(), 12, "stone"),
												new ArtefactInfo(PPArtefact.STONE_SCARAB.getArtefactId(), 12, "stone"),
												new ArtefactInfo(PPArtefact.STONE_STATUETTE.getArtefactId(), 12, "stone")
										)
								))
						);

						option.add("Pottery or ivory artefacts?", new Dialogue()
								.addPlayer(HeadE.CALM, "I'd like to use some pottery or ivory artefacts")
								.addNext(() -> rechargeSceptreWithArtefacts(player,
										Arrays.asList(
												new ArtefactInfo(PPArtefact.NOTED_IVORY_COMB.getArtefactId(), 24, "ivory or pottery"),
												new ArtefactInfo(PPArtefact.NOTED_POTTERY_SCARAB.getArtefactId(), 24, "ivory or pottery"),
												new ArtefactInfo(PPArtefact.NOTED_POTTERY_STATUETTE.getArtefactId(), 24, "ivory or pottery"),
												new ArtefactInfo(PPArtefact.IVORY_COMB.getArtefactId(), 24, "ivory or pottery"),
												new ArtefactInfo(PPArtefact.POTTERY_SCARAB.getArtefactId(), 24, "ivory or pottery"),
												new ArtefactInfo(PPArtefact.POTTERY_STATUETTE.getArtefactId(), 24, "ivory or pottery")
										)
								))
						);

						option.add("Jewelled artefacts?", new Dialogue()
								.addPlayer(HeadE.CALM, "I'd like to use a jewelled artefact")
								.addNext(() -> rechargeSceptreWithJewelledArtefact(player,
										PPArtefact.JEWELLED_GOLDEN.getArtefactId()
								))
						);
					});
				}
			}
		});
	}

	private static void rechargeSceptreWithArtefacts(Player player, List<ArtefactInfo> artefacts) {
		boolean hasRequiredArtefacts = false;
		int finalArtefactID = 0;
		int finalCount =0;

		for (ArtefactInfo artefactInfo : artefacts) {
			int itemId = artefactInfo.getItemId();
			int requiredCount = artefactInfo.getRequiredCount();
			if (player.getInventory().containsItem(itemId, requiredCount)) {
				hasRequiredArtefacts = true;
				finalArtefactID = itemId;
				finalCount = requiredCount;
				break;
			}
		}

		if (hasRequiredArtefacts) {
			if (finalArtefactID == PPArtefact.JEWELLED_GOLDEN.getArtefactId()) {
				rechargeSceptreWithJewelledArtefact(player, finalArtefactID);
			}
			else {
				exchangeSceptre(player, finalArtefactID, finalCount);
				player.sendMessage("You recharge your sceptre with " + ArtefactInfo.artefactType + " artefacts.");
			}
		}
		else {
			int finalRequired = switch (ArtefactInfo.artefactType) {
				case "ivory or pottery" -> 24;
				case "stone" -> 12;
				case "gold" -> 6;
				case "jewelled" -> 1;
				default -> 0;
			};
			player.startConversation(new Conversation(player) {
				{
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "You need to have " + finalRequired + " of the same " + ArtefactInfo.artefactType + " artefacts to recharge your sceptre.");
				}
			});
		}
	}

	private static void rechargeSceptreWithJewelledArtefact(Player player, int jewelledArtefactId) {
		if (player.getInventory().containsItem(jewelledArtefactId, 1)) {
			exchangeSceptre(player, jewelledArtefactId, 1);
			player.sendMessage("You recharge your sceptre with a jewelled golden statuette.");
		}
		else {
			player.startConversation(new Conversation(player) {
				{
					addNPC(MUMMYID, HeadE.CHILD_FRUSTRATED, "You don't have a jewelled golden statuette.");
				}
			});
		}
	}

	private static void exchangeSceptre(Player player, int artefactID, int count) {
		player.getInventory().removeItems(new Item(PHARAOHS_SCEPTRE[0]), new Item(PHARAOHS_SCEPTRE[1]), new Item(PHARAOHS_SCEPTRE[2]));
		player.getInventory().removeItems(new Item(artefactID, count));
		player.getInventory().addItem(new Item(PHARAOHS_SCEPTRE[3]));
		player.getInventory().refresh();
	}

	private static class ArtefactInfo {
		private final int itemId;
		private final int requiredCount;
		private static String artefactType = "";

		public ArtefactInfo(int itemId, int requiredCount, String artefactType) {
			this.itemId = itemId;
			this.requiredCount = requiredCount;
			this.artefactType = artefactType;
		}

		public int getItemId() {
			return itemId;
		}

		public int getRequiredCount() {
			return requiredCount;
		}

		public String getArtefactType() {
			return artefactType;
		}
	}
}

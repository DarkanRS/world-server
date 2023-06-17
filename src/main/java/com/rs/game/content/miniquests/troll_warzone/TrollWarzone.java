package com.rs.game.content.miniquests.troll_warzone;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestHandler;
import com.rs.engine.miniquest.MiniquestOutline;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@MiniquestHandler(Miniquest.TROLL_WARZONE)
@PluginEventHandler
public class TrollWarzone extends MiniquestOutline {
    //9 - troll general comes down from the mountain
    //10 - ambushing trolls with archers
    //11 - intro to burthorpe tutorial
    //12 - player shoots cannon to close off the troll invasion

    @Override
    public int getCompletedStage() {
        return 6;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        ArrayList<String> lines = new ArrayList<>();
        switch (stage) {
            case 0 -> {
                lines.add("I can start this miniquest by speaking to Major Nigel Corothers in");
                lines.add("northern Burthorpe.");
                lines.add("");
            }

            case 4 -> {
                lines.add("");
                lines.add("");
                lines.add("MINIQUEST COMPLETE!");
            }
            default -> lines.add("Invalid quest stage. Report this to an administrator.");
        }
        return lines;
    }

    @Override
    public void complete(Player player) {
        player.getSkills().addXpQuest(Skills.COOKING, 110);
        player.getSkills().addXpQuest(Skills.MINING, 110);
        player.getSkills().addXpQuest(Skills.WOODCUTTING, 110);
        player.getInventory().addItemDrop(23030, 1);
        player.getInventory().addItemDrop(8007, 5);
        player.getInventory().addItemDrop(8009, 5);
        player.getInventory().addItemDrop(2429, 5);
        player.getInventory().addItemDrop(114, 5);
        player.getInventory().addItemDrop(2433, 5);
        player.getInventory().addItemDrop(2435, 5);
        getQuest().sendQuestCompleteInterface(player, 23030, "A baby troll!", "110 Cooking XP", "110 Mining XP", "110 Woodcutting XP", "Some teleport tablets", "Some combat potions");
    }

    @Override
    public void updateStage(Player player) {
        if (player.getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) >= 5)
            player.getVars().setVarBit(10683, player.getMiniquestManager().getStage(Miniquest.TROLL_WARZONE));
        //varbit 10683 updates corporal keymans to claim the baby troll
    }

    public static ObjectClickHandler handleTrollCaveEnterExit = new ObjectClickHandler(new Object[] { 66533, 66534 }, e -> {
        switch(e.getObjectId()) {
            case 66533 -> {
                if (e.getPlayer().getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) < 1) {
                    e.getPlayer().simpleDialogue("You should speak with Major Nigel Corothers before going in here. He's only just south of here.");
                    return;
                }
                if (e.getPlayer().getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) == 1) {
                    e.getPlayer().sendOptionDialogue("Would you like to continue the Troll Warzone miniquest?", ops -> {
                        ops.add("Yes.", () -> e.getPlayer().getControllerManager().startController(new TrollGeneralAttackController()));
                        ops.add("Not right now.");
                    });
                    return;
                }
                e.getPlayer().useStairs(-1, Tile.of(2208, 4364, 0), 0, 1);
            }
            case 66534 -> e.getPlayer().useStairs(-1, Tile.of(2878, 3573, 0), 0, 1);
        }
    });

    public static Dialogue getCaptainJuteDialogue(Player player, NPC npc) {
        Dialogue dialogue = new Dialogue();
        switch(player.getMiniquestManager().getStage(Miniquest.TROLL_WARZONE)) {
            case 0 -> dialogue.addNPC(npc, HeadE.FRUSTRATED, "The trolls are overrunning us! Major Nigel has been trying to find recruits.");
            case 1 -> dialogue.addNPC(npc, HeadE.FRUSTRATED, "Get back in the cave over there to help Ozan and Keymans!");
            case 2 -> {
                dialogue.addNPC(npc, HeadE.CALM_TALK, "Ozan tells me that you defeated one of the troll generals.")
                        .addNPC(npc, HeadE.CALM_TALK, "The trolls are getting into that cave through a back entrance high on Death Plateau. If we try to collapse this end, we could collapse the whole castle with it!")
                        .addNPC(npc, HeadE.CALM_TALK, "Death Plateau itself is too dangerous to assault. I need you to get to the top of the castle and direct cannon fire onto that back entrance!")
                        .addOptions(ops -> {
                            ops.add("I'll do it right away!", () -> player.getMiniquestManager().setStage(Miniquest.TROLL_WARZONE, 3));
                            ops.add("I want to kill more trolls!");
                        });
            }
            case 3 -> dialogue.addNPC(npc, HeadE.CALM_TALK, "What are you waiting for? Get up on top of the castle and fire a cannon into that cavern back entrance!");
            default -> dialogue.addNPC(npc, HeadE.CALM_TALK, "Excellent work bringing down the cavern entrance. You should go check in with Corothers.");
        }
        return dialogue;
    }

    public static ObjectClickHandler handleCannonFire = new ObjectClickHandler(new Object[] { 66981 }, e -> {
       if (e.getPlayer().getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) == 3) {
           e.getPlayer().getMiniquestManager().setStage(Miniquest.TROLL_WARZONE, 4);
           e.getPlayer().playPacketCutscene(12, () -> e.getPlayer().playerDialogue(HeadE.HAPPY_TALKING, "That just about does it. I should check in with Corothers."));
           return;
       }
       if (e.getPlayer().getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) < 3)
           e.getPlayer().sendMessage("You haven't been given approval to fire off any cannons here.");
       else
           e.getPlayer().sendMessage("I've collapsed the entrance with the cannon. I should check in with Corothers.");
    });
}

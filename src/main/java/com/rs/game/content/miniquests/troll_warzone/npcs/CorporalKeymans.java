package com.rs.game.content.miniquests.troll_warzone.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.content.pets.Pets;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CorporalKeymans {
    //13445
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { 14994 }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> {
                if (!e.getPlayer().getMiniquestManager().isComplete(Miniquest.TROLL_WARZONE) && e.getPlayer().getMiniquestManager().getStage(Miniquest.TROLL_WARZONE) >= 5) {
                    e.getPlayer().startConversation(new Dialogue()
                            .addNPC(14994, HeadE.CALM_TALK, "Ozan dumped that baby troll on me. I don't know what to do with him.")
                            .addPlayer(HeadE.HAPPY_TALKING, "I could look after him.")
                            .addNext(() -> e.getPlayer().getMiniquestManager().complete(Miniquest.TROLL_WARZONE)));
                    return;
                }
                if (e.getPlayer().containsItem(23030) || (e.getPlayer().getPet() != null && e.getPlayer().getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()))
                    e.getPlayer().npcDialogue(e.getNPC(), HeadE.CALM_TALK, "Thanks for all your help in the cave");
                else
                    e.getPlayer().startConversation(new Dialogue()
                            .addNPC(e.getNPCId(), HeadE.FRUSTRATED, "I found this little guy wandering around up here. Thought you might want him back. Try not to lose him again.")
                            .addItemToInv(e.getPlayer(), new Item(23030), "You reclaim the baby troll."));
            }
        }
    });
}

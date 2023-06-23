package com.rs.game.content.minigames.trollinvasion;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.content.miniquests.troll_warzone.TrollWarzone;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CaptainJute extends Conversation {
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { 13697 }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new CaptainJute(e.getPlayer(), e.getNPC()));
            case "Start-repairing" ->  e.getPlayer().startConversation(new Dialogue().addNPC(13697, HeadE.FRUSTRATED, "I'll be in charge of the Troll Invasion defenses once it's implemented!"));
            case "Start-fighting" ->  e.getPlayer().startConversation(new Dialogue().addNPC(13697, HeadE.FRUSTRATED, "I'll be in charge of the Troll Invasion defenses once it's implemented!"));
            case "Rewards" ->  e.getPlayer().startConversation(new Dialogue().addNPC(13697, HeadE.FRUSTRATED, "I'll be in charge of the Troll Invasion defenses once it's implemented!"));

        }
    });

    public CaptainJute(Player player, NPC npc) {
        super(player);
        if (!player.getMiniquestManager().isComplete(Miniquest.TROLL_WARZONE)) {
            addNext(TrollWarzone.getCaptainJuteDialogue(player, npc));
            create();
            return;
        }
        addNPC(npc, HeadE.HAPPY_TALKING, "I will be in charge of the Troll Invasion defenses once the minigame gets implemented.");
    }
}

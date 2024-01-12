package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CoshingVolunteer {

    public static NPCClickHandler CoshingVolunteer = new NPCClickHandler(new Object[]{ 11290, 11292, 11288 }, new String[]{"Talk-to"}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CALM_TALK, "I'm a coshing trainer. I'm here to...")
                .addNext(() -> npc.forceTalk("Gulp"))
                .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "I'm here to get hit over the head with a rubber cosh and be robbed of my hankies. Why did I ever agree to this?")
                .addNPC(npc.getId(), HeadE.CALM_TALK, "Anyway, Big Man's your man for more info. He's the big man over there.")
        );
    });
}

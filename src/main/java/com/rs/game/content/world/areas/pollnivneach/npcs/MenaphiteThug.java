package com.rs.game.content.world.areas.pollnivneach.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class MenaphiteThug {

    public static LoginHandler login = new LoginHandler(e -> {
        if (e.getPlayer().isQuestComplete(Quest.THE_FEUD)) {
            e.getPlayer().getVars().setVarBit(340, 2);
        }
    });

    public static NPCClickHandler HandleMenaphiteThug = new NPCClickHandler(new Object[]{ 1903 }, new String[]{"Talk-to"}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello.")
                .addNPC(npc, HeadE.FRUSTRATED, "What do you want?")
                .addPlayer(HeadE.CALM_TALK, "Nothing really, I'm new to town and wanted to get to know the locals.")
                .addNPC(npc, HeadE.FRUSTRATED, "I'm too busy for chitchat. Come back if you have some business to discuss with us.")
        );
    });
}

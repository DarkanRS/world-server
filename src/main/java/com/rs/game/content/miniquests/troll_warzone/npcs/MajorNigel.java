package com.rs.game.content.miniquests.troll_warzone.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.content.miniquests.troll_warzone.TrollGeneralAttackController;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class MajorNigel extends Conversation {
    public static NPCClickHandler handleOps = new NPCClickHandler(new Object[] { 14850 }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new MajorNigel(e.getPlayer(), e.getNPC()));
            case "Get-recommendation" ->  e.getPlayer().startConversation(new Dialogue().addNPC(14850, HeadE.FRUSTRATED, "If you're looking to train combat, soldier, I'd recommend ridding the local area of as many trolls as possible. Or there are some cows to the south."));
        }
    });

    public MajorNigel(Player player, NPC npc) {
        super(player);
        switch(player.getMiniquestManager().getStage(Miniquest.TROLL_WARZONE)) {
            case 0 -> addOptions("Would you like to start the Troll Warzone miniquest?", ops -> {
                ops.add("Yes.", () -> {
                    player.playPacketCutscene(11, () -> player.getControllerManager().startController(new TrollGeneralAttackController()));
                    player.getMiniquestManager().setStage(Miniquest.TROLL_WARZONE, 1);
                });
                ops.add("Not right now.");
            });
            case 1 -> addNPC(npc, HeadE.FRUSTRATED, "You need to get back up to the cave to the north and assist in stopping the trolls!");
            case 2, 3 -> addNPC(npc, HeadE.FRUSTRATED, "Excellent work in holding back the trolls in the cave. Captain Jute up north said he wanted to talk to you.");
            case 4 -> {
                addNPC(npc, HeadE.FRUSTRATED, "So you've thwarted the recent attack by defeating the general. Good work. Looks like the trolls have let up their attacks for now.").voiceEffect(12434);
                addNPC(npc, HeadE.FRUSTRATED, "Here in Burthorpe we've been hit pretty hard. Taverly, the town to the south, has been sending us aid and they're in bad shape too.").voiceEffect(11121);
                addNPC(npc, HeadE.FRUSTRATED, "What we need to do now is recover in time for the next attack. I need you to work your way around Burthorpe and Taverly lending your help where you can.").voiceEffect(12488);
                addNPC(npc, HeadE.FRUSTRATED, "We have a lot of experts here, helping with the war effort. Check in with them to see what you can do.").voiceEffect(11208);
                addPlayer(HeadE.CHEERFUL, "I'm on it.");
                addNext(() -> player.getMiniquestManager().setStage(Miniquest.TROLL_WARZONE, 5));
            }
            case 5 -> addNPC(npc, HeadE.FRUSTRATED, "Go check in with Keymans. He said he has something for you.");
            default -> addNPC(npc, HeadE.FRUSTRATED, "Burthorpe is still under dire threat. We need every hero we can get in top shape.").voiceEffect(12443);
        }
    }
}

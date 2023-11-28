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
    static String[] Lure = new String[]{
            "Watch out! The fellow behind you has a club!",
            "Behind you! A three-headed monkey!",
            "That's the third biggest platypus I've ever seen!",
            "Look over THERE!",
            "Look! An eagle!",
            "Your shoelace is untied.",
    };

    public static boolean LureStatus(){
        return true;
    }

    public static NPCClickHandler CoshingVolunteer = new NPCClickHandler(new Object[]{ 11290, 11292, 11296 }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        if(e.getOption().equalsIgnoreCase("Talk-to")){
            player.startConversation(new Dialogue()
                    .addNPC(npc.getId(), HeadE.CALM_TALK, "I'm a coshing trainer. I'm here to...")
                    .addNext(() -> npc.forceTalk("Gulp"))
                    .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "I'm here to get hit over the head with a rubber cosh and be robbed of my hankies. Why did I ever agree to this?")
                    .addNPC(npc.getId(), HeadE.CALM_TALK, "Anyway, Big Man's your man for more info. He's the big man over there.")
            );
            return;
        }
        if(e.getOption().equalsIgnoreCase("Lure")){
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.AMAZED_MILD, Lure[Utils.random(6)])
                    .addNPC(npc.getId(), HeadE.SCARED, "Oh nooooo!")
                    .addNext(() -> {
                        //TODO Lure
                    })
            );
            return;
        }
        if(e.getOption().equalsIgnoreCase("Knock-out")){
            if(player.getEquipment().getWeaponId() != 18644){
                player.playerDialogue(HeadE.SKEPTICAL_THINKING, "I'll need a training cosh to practise my technique with.");
                return;
            }
            //TODO blackjacking
            player.sendMessage("Blackjacking has not been added yet");
        }
    });
}

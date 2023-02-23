package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

@PluginEventHandler
public class WoundedSoldier extends Conversation {

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverrides(15019,15021,15022,15023,15024,15025,15026,15027,15028,15029,15030,15031,15033);
    }

    public WoundedSoldier(Player player) {
        super(player);
        String[] whatHappened = new String[]{
                "Troll bite. There's always a fever after you've been bitten by one of those. I'm sure they don't clean their teeth.",
                "What happened to me is nothing compared to what's gonna happen to the filthy Troll who did it. When I'm out of here, I'm gonna find him and fix him good.",
                "Trolls. I was on patrol on the Plateau, but those Trollheim scumbags jumped me from behind and hit me with a big stick.",
                "He broke my bones... my blood... my everything!",

        };
        addPlayer(HeadE.SCARED, "What happened to you?");
        addNPC(15022, HeadE.SAD_CRYING, whatHappened[(Utils.random(1, 4))]);
        addPlayer(HeadE.NERVOUS,"I think I'd better leave you to recover.");
        create();
    }


    public static NPCInteractionDistanceHandler WoundedSoldierDistance = new NPCInteractionDistanceHandler(new Object[] { 15019,15021,15022,15023,15024,15025,15026,15027,15028,15029,15030,15031,15033 }, (p, n) -> 1);

    public static NPCClickHandler WoundedSoldierHandler = new NPCClickHandler(new Object[] { 15019,15021,15022,15023,15024,15025,15026,15027,15028,15029,15030,15031,15033 }, e -> {
        if(e.getOption().equalsIgnoreCase("talk-to")){
            e.getNPC().resetDirection();
                e.getPlayer().startConversation(new WoundedSoldier(e.getPlayer()));
        }
    });
}







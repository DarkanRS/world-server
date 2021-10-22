package com.rs.game.player.quests.handlers.princealirescue;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class AggiePrinceAliRescueD extends Conversation {
    Player p;
    public final static int AGGIE = 922;
    final int CONVO1 = 0;
    final int CONVO2 = 1;
    final int CONVO3 = 2;

    public AggiePrinceAliRescueD(Player p) {
        super(p);
        this.p = p;


    }

    public AggiePrinceAliRescueD(Player p, int convoID) {
        super(p);
        this.p = p;

        switch(convoID) {
            case CONVO1:
                convo1(p);
                break;
            case CONVO2:
                convo2(p);
                break;
            case CONVO3:
                convo3(p);
                break;
        }

    }

    private void convo1(Player p) {

    }

    private void convo2(Player p) {

    }

    private void convo3(Player p) {

    }
}


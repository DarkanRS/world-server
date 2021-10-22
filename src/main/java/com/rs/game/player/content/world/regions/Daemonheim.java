package com.rs.game.player.content.world.regions;

import com.rs.game.ge.GE;
import com.rs.game.player.Player;
import com.rs.game.player.content.Potions;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Daemonheim {
    public static NPCClickHandler handleFremmyBanker = new NPCClickHandler(9710) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            if(e.getOption().equalsIgnoreCase("bank"))
                p.getBank().open();
            if(e.getOption().equalsIgnoreCase("collect"))
                GE.openCollection(p);
            if(e.getOption().equalsIgnoreCase("talk-to"))
                p.startConversation(new Conversation(p) {
                    {
                        addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Good day. How may I help you?");
                        addOptions("Select an option", new Options() {
                            @Override
                            public void create() {
                                option("I'd like to access my bank account, please.", new Dialogue().addNext(()->{p.getBank().open();}));
                                option("I'd like to check my PIN settings.", new Dialogue().addNext(()->{p.getBank().openPinSettings();}));
                                option("I'd like to see my collection box", new Dialogue().addNext(()->{GE.openCollection(p);}));
                            }
                        });
                        create();
                    }
                });

        }
    };

}

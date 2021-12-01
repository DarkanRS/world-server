// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions;

import com.rs.game.ge.GE;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

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

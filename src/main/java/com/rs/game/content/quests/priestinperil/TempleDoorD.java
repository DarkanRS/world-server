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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class TempleDoorD extends Conversation {
    final int MONK = 1045;
    
    public TempleDoorD(Player player) {
        super(player);
        if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 1) {
            player.startConversation(new Dialogue()
                    .addNPC(MONK, HeadE.CALM_TALK, " Who are you and what do you want?", "Mysterious Voice")
                    .addPlayer(HeadE.CALM_TALK, "Ummmm....")
                    .addOptions(ops -> {
                        ops.add("Roald sent me to check on Drezel.")
                                .addPlayer(HeadE.CALM_TALK, "Roald sent me to check on Drezel.")
                                .addNPC(MONK, HeadE.CALM_TALK, "Psst... Hey... Who's Roald? Who's Drezel?", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "Uh... Isn't Drezel that dude upstairs? Oh, wait, Roald's the king of Misthalin right?", "Mysterious Voice #2")
                                .addNPC(MONK, HeadE.CALM_TALK, "He is? Aww man...", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "Hey, you deal with this okay.", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "He's just coming! Wait a second!", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "Hello, my name is Drevil.", "Drevil")
                                .addNPC(MONK, HeadE.CALM_TALK, "Drezel!", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "I mean Drezel. How can I help?", "* Drezel *")
                                .addPlayer(HeadE.CALM_TALK, "Well, as I say, the king sent me to make sure everything's okay with you.")
                                .addNPC(MONK, HeadE.CALM_TALK, "And, uh, what would you do if everything wasn't okay with me?", "* Drezel *")
                                .addPlayer(HeadE.CALM_TALK, "I'm not sure. Ask you what help you need I suppose.")
                                .addNPC(MONK, HeadE.CALM_TALK, "Ah, good, well, I don't think...", "* Drezel *")
                                .addNPC(MONK, HeadE.CALM_TALK, "Psst... Hey... The dog!", "Mysterious Voice #2")
                                .addNPC(MONK, HeadE.CALM_TALK, "OH! Yes, of course!", "* Drezel *")
                                .addNPC(MONK, HeadE.CALM_TALK, "Will you do me a favour, adventurer?", "* Drezel *")
                                .addOptions(ops2 ->{
                                    ops2.add("Sure, I'm a helpful person!")
                                            .addPlayer(HeadE.CALM_TALK, "Sure. I'm a helpful person!")
                                            .addNPC(MONK, HeadE.CALM_TALK, "HAHAHAHA! Really? Thanks buddy! You see that mausoleum out there? There's a horrible big dog underneath it that I'd like you to kill for me! It's been really bugging me! Barking all the time and stuff!", "* Drezel *")
                                            .addNPC(MONK, HeadE.CALM_TALK, "Please kill it for me buddy!", "* Drezel *")
                                            .addPlayer(HeadE.CALM_TALK, "Okey-dokey, one dead dog coming up.", () -> {
                                                player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 2);
                                                    });
                                    ops2.add("Nope. Something about all this is very suspicious...")
                                            .addPlayer(HeadE.CALM_TALK, "Nope. Something about all this is very suspicious...")
                                            .addNPC(MONK, HeadE.CALM_TALK, "Get lost then! I have important things to do, as sure as my name is Dibzil.", "* Dibzil *")
                                            .addNPC(MONK, HeadE.CALM_TALK, "Drezel!", "Mysterious Voice")
                                            .addNPC(MONK, HeadE.CALM_TALK, "Drezel. Now, go away!", "* Drezel *");
                                });
                        ops.add("Hi, I just moved in next door.")
                                .addPlayer(HeadE.CALM_TALK,"Hi, I just moved in next door.")
                                .addPlayer(HeadE.CALM_TALK,"Can I borrow a cup of coffee?")
                                .addNPC(MONK, HeadE.CALM_TALK, "What next door? What's coffee? Who ARE you?", "Mysterious Voice");
                        ops.add("I hear this place is of historic interest.")
                                .addPlayer(HeadE.CALM_TALK,"I hear this place is of historic interest.")
                                .addPlayer(HeadE.CALM_TALK,"Can I come in and have a wander around? Possibly look at some antiques or buy something from your gift shop?")
                                .addNPC(MONK, HeadE.CALM_TALK, "Pssst... Hey... Is this place of historic interest?", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "I dunno. I guess it might be. Does it matter?", "Mysterious Voice #2")
                                .addNPC(MONK, HeadE.CALM_TALK, "I suppose not.", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "Clear off! You can't come in!", "Mysterious Voice");
                        ops.add("The council sent me to check your pipes.")
                                .addPlayer(HeadE.CALM_TALK,"The council sent me to check your pipes.")
                                .addNPC(MONK, HeadE.CALM_TALK, "They did? Ummm....", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "Psst... Are there any pipes in here, you reckon?", "Mysterious Voice")
                                .addNPC(MONK, HeadE.CALM_TALK, "I dunno... I don't think so...", "Mysterious Voice #2")
                                .addNPC(MONK, HeadE.CALM_TALK, "We don't have any thanks! Bye!", "Mysterious Voice");
                        ops.add("Nothing. Never mind.");
                    })
            );
        }
        if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 2) {
            player.startConversation(new Dialogue()
                    .addNPC(MONK, HeadE.CALM_TALK, "Hello?", "* Drezel *")
                    .addPlayer(HeadE.CALM_TALK,"What am I supposed to be doing again?")
                    .addNPC(MONK, HeadE.CALM_TALK, "Who are you?", "* Drezel *")
                    .addNPC(MONK, HeadE.CALM_TALK, "SHHHH! It's the adventurer!", "Mysterious Voice #2")
                    .addNPC(MONK, HeadE.CALM_TALK, "Ah! I want you to go kill the horrible dog in the mausoleum for me! You can use the entrance out there.", "* Drezel *")
                    .addNPC(MONK, HeadE.CALM_TALK, "You'll do this for good old Delzig, won't ya buddy?", "* Delzig *")
                    .addNPC(MONK, HeadE.CALM_TALK, "Drezel!", "Mysterious Voice")
                    .addNPC(MONK, HeadE.CALM_TALK, "*cough* for good old Drezel, right buddy?", "* Drezel *")
            );
        }
        if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 3) {
            player.startConversation(new Dialogue()
                    .addNPC(MONK, HeadE.CALM_TALK, "You again? What do you want now?", "* Drezel *")
                    .addPlayer(HeadE.CALM_TALK,"I killed that dog for you.")
                    .addNPC(MONK, HeadE.CALM_TALK, "Really? Hey, that's great!", "* Drezel *")
                    .addNPC(MONK, HeadE.CALM_TALK, "Yeah thanks a lot buddy!", "Mysterious Voice")
                    .addNPC(MONK, HeadE.CALM_TALK, "HAHAHAHAHAHA!", "Mysterious Voice #2")
                    .addPlayer(HeadE.CALM_TALK,"What's so funny?")
                    .addNPC(MONK, HeadE.CALM_TALK, "Nothing buddy! We're just so grateful!", "* Drezel *")
                    .addNPC(MONK, HeadE.CALM_TALK, "Yeah, maybe you should go tell the king what a great job you did buddy!", "Mysterious Voice #2")
            );
        }
    }
}

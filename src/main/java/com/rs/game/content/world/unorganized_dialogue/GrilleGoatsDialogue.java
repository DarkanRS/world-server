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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

public class GrilleGoatsDialogue extends Conversation {
    public GrilleGoatsDialogue(Player player) {
        super(player);

        addNPC(3807, HeadE.CALM_TALK, "Hello, I'm Gillie. What can I do for you?");
        addOptions("baseOptions", ops -> {
            ops.add("Who are you?")
                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                .addNPC(3807, HeadE.CALM_TALK, "My name's Gillie Groats. My father is a farmer and I milk the cows for him.")
                .addPlayer(HeadE.CALM_TALK, "Do you have any buckets of milk spare?")
                .addNPC(3807, HeadE.CALM_TALK, "I'm afraid not. We need all of our milk to sell to market, but you can milk the cow yourself if you need milk.")
                .addPlayer(HeadE.CALM_TALK, "Thanks.");
            ops.add("Can you tell me how to milk a cow?")
                .addPlayer(HeadE.CALM_TALK, "Can you tell me how to milk a cow?")
                .addNPC(3807, HeadE.CALM_TALK, "It's very easy. First, you need an empty bucket to hold the milk.")
                .addNPC(3807, HeadE.CALM_TALK, "You can buy empty buckets from the general store in Lumbridge, south-west of here, or from general stores in RuneScape. You can also buy them from the Grand Exchange in Varrock.")
                .addNPC(3807, HeadE.CALM_TALK, "You look like you could do with an empty bucket. Here, take this spare one.")
                .addItemToInv(player, new Item(1925, 1), "She hands you a bucket.")
                .addNPC(3807, HeadE.CALM_TALK, "Then find a dairy cow to milk - you can't milk just any cow.")
                .addPlayer(HeadE.CALM_TALK, "How do I find a dairy cow?")
                .addNPC(3807, HeadE.CALM_TALK, "They are easy to spot - they have a cowbell around their neck and are tethered to a post to stop them wandering around all over the place. There are a couple in this field.")
                .addPlayer(HeadE.CALM_TALK, "What about top-quality milk?")
                .addNPC(3807, HeadE.CALM_TALK, "Ah, for that you'll have to see my prized cow, on the east side of the field, over by the cliff.")
                .addNPC(3807, HeadE.CALM_TALK, "Then you just need to use your bucket on the cow and you'll get some tasty, nutritious milk.");
            ops.add("Can I buy milk off you?")
                .addPlayer(HeadE.CALM_TALK, "Can I buy milk off you?")
                .addNPC(3807, HeadE.CALM_TALK, "I'm afraid not. My husband has already taken all of our stocks to the market.")
                .addNPC(3807, HeadE.CALM_TALK, "You could get some by milking the dairy cows yourself. If you would still rather buy it, you can probably get some at the Grand Exchange in Varrock, just north of here. A lot of adventurers sell their goods there.");
            ops.add("I'm fine thanks.")
                .addPlayer(HeadE.CALM_TALK, "I'm fine thanks.");
        });
    }
}




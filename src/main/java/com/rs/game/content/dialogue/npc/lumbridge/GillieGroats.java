package com.rs.game.content.dialogue.npc.lumbridge;

// Basic dialogue handler for linear text conversations with no choices.

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GillieGroats extends Conversation {

    //Identify NPC by ID Object[]{XXXX})
    public static NPCClickHandler GillieGroats = new NPCClickHandler(new Object[]{3807}) {
        @Override
        //Handle Right-Click
        public void handle(NPCClickEvent e) {
            switch (e.getOption()) {
                //Start Conversation
                case "Talk-to" -> e.getPlayer().startConversation(new GillieGroats(e.getPlayer()));
            }
        }
    };


    public GillieGroats(Player player) {
        super(player);
        //Identify NPC by ID
        int npcId = 3807;

        addOptions(new Options() {
            @Override
            public void create() {
                //Give player options
                addNPC(npcId, HeadE.CALM_TALK, "Hello, I'm Gillie the Milkmaid. What can I do for you?");
                addOptions(new Options() {
                    @Override
                    public void create() {
                        //Simple Reply
                        option("Who are you?", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                                .addNPC(npcId, HeadE.CALM_TALK, "My name is Gillie Groats. My father is a farmer and I milk the cows for him.")
                                .addPlayer(HeadE.CALM_TALK, "Do you have any buckets of milk spare?")
                                .addNPC(npcId, HeadE.CALM_TALK, "I'm afraid not. We need all of our milk to sell to market, but you can milk the cow yourself if you need the milk.")
                                .addPlayer(HeadE.CALM_TALK, "Thanks.")
                        );
                        //Conversation
                        option("Can you tell me how to milk a cow?", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "So how do you milk a cow then?")
                                .addNPC(npcId, HeadE.CALM_TALK, "It's very easy. First you need an empty bucket to hold the milk.")
                                .addNPC(npcId, HeadE.CALM_TALK, "Then find a dairy cow to milk - you can't milk just any cow.")
                                .addPlayer(HeadE.CALM_TALK, " How do I find a dairy cow?")
                                .addNPC(npcId, HeadE.CHUCKLE, "They are easy to spot - they are dark brown and white, unlike beef cows, which are light brown and white. We also tether them to a post to stop them wandering around all over the place.")
                                .addNPC(npcId, HeadE.CALM_TALK, "There are a couple very near, in this field.")
                                .addNPC(npcId, HeadE.CALM_TALK, "Then just milk the cow and your bucket will fill with tasty, untritious milk.")
                        );

                        //Nested player options with responses
                        option("I'm fine, thanks.", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "I'm fine, thanks.")
                        );
                    }
                });

            }
        });
    }
};

//TODO add milking cow without a bucket interupt

/*
Attempting to milk a dairy cow without a bucket
Gillie Groats the Milkmaid: Tee hee! You've never milked a cow before, have you?
Player: Erm... No. How could you tell?
Gillie Groats the Milkmaid: Because you're spilling milk all over the floor. What a waste! You need something to hold the milk.
Player: Ah yes, I really should have guessed that one, shouldn't I?
Gillie Groats the Milkmaid: You're from the city, aren't you... Try it again with an empty bucket.
Player: Right, I'll do that.
 */


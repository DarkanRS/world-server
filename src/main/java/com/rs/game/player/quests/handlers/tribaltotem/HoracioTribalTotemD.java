package com.rs.game.player.quests.handlers.tribaltotem;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class HoracioTribalTotemD extends Conversation {
    private static final int NPC = 845;
    public HoracioTribalTotemD(Player p) {
        super(p);
        addNPC(NPC, HeadE.CALM_TALK, "It's a fine day to be out in a garden, isn't it?");
        addOptions("Choose an option:", new Options() {
            @Override
            public void create() {
                option("Yes. It's very nice.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "es. It's very nice.")
                        .addNPC(NPC, HeadE.CALM_TALK, "Days like these make me glad to be alive!")
                );
                option("So... who are you?", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "So... who are you?")
                        .addNPC(NPC, HeadE.CALM_TALK, "My name is Horacio Dobson. I'm a gardener to Lord Handlemort. Take a look around this beautiful " +
                                "garden, all of this is my handiwork")
                        .addOptions("Choose an option:", new Options() {
                            @Override
                            public void create() {
                                option("So... do you garden round the back, too?", new Dialogue()
                                        .addPlayer(HeadE.HAPPY_TALKING, "So... do you garden round the back, too?")
                                        .addNPC(NPC, HeadE.CALM_TALK, "That I do!")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Doesn't all of the security around the house get in your way then?")
                                        .addNPC(NPC, HeadE.CALM_TALK, "Ah. I'm used to all that. I have my keys, the guard dogs know me, and I know the " +
                                                "combination to the door lock. It's rather easy, it's his middle name.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Whose middle name?")
                                        .addNPC(NPC, HeadE.CALM_TALK, "Hum. I probably shouldn't have said that. Forget I mentioned it.")
                                );
                                option("Do you need any help?", new Dialogue()
                                        .addPlayer(HeadE.HAPPY_TALKING, "Do you need any help?")
                                        .addNPC(NPC, HeadE.CALM_TALK, "Trying to muscle in on my job, eh? I'm more than happy to do this all by myself!\n")
                                );
                            }
                        })
                );
            }
        });

    }

    public static NPCClickHandler handleDialogue = new NPCClickHandler(NPC) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new HoracioTribalTotemD(e.getPlayer()).getStart());
        }
    };
}

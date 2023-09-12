package com.rs.game.content.world.areas.pollnivneach.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AspAndSnake {
    public static NPCClickHandler HandleAliTheBarman = new NPCClickHandler(new Object[]{ 1864 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello there.")
                .addNPC(npc, HeadE.CALM_TALK, "Good day. Can I help you with anything?")
                .addOptions(options -> {
                    options.add("Yes, I'd like a drink please.", () -> ShopsHandler.openShop(e.getPlayer(), "asp_and_snake"));
                    options.add("What's going on in town?")
                            .addPlayer(HeadE.CALM_TALK, "What's going on in town?")
                            .addNPC(npc, HeadE.CALM_TALK, "Nothing much really. About what you'd expect from a sleepy little town like ours.")
                            .addPlayer(HeadE.SKEPTICAL, "What about those two gangs? That seems exciting.")
                            .addNPC(npc,HeadE.FRUSTRATED, "They're paying customers, so I won't rock the boat if you don't mind.");
                    options.add("The 'Asp and Snake'? What a strange name for a bar.")
                            .addPlayer(HeadE.CALM_TALK, "The 'Asp and Snake'? What a strange name for a bar.")
                            .addNPC(npc, HeadE.CALM_TALK, "Maybe around these parts. Were you expecting it to have 'Ali' in the name?")
                            .addNPC(npc, HeadE.CALM_TALK, "I did think about calling it 'Ali's', but then it'd get confused with the Camel Shop, the Kebab House, the Mayor's residence...")
                            .addPlayer(HeadE.CALM_TALK, "Yes, I get your point. It's a perfectly fine name.");

                })
        );
    });
    public static NPCClickHandler HandleAliTheDrunk = new NPCClickHandler(new Object[]{1863}, new String[]{"Talk-to"}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addNPC(npc, HeadE.DRUNK, "Ahh, a kind stranger. Get this old man another drink so that he may wet his throat and talk to you.")
        );
    });

    public static ItemOnNPCHandler HandleAliBeer = new ItemOnNPCHandler(new Object[]{1863}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        String[] responses = new String[]{
                "Wha'? Are you lookin' at me? Are you lookin' at me? You know something? You're drunk, you are.",
                "Did you hear the one where the camel walks into the bar and orders a pint? The barman asks 'Why the long face!'.",
                "Did you hear the one about the man who walked into a bar? He hurt his foot! Hah!",
                "I've got a lovely bunch of coconuts! Hehehe...",
                "What are you looking at?",
                "Why does nobody like me?",
                "You know, I have this friend, and she... I can't remember. What was I saying?",
                "Thank you my friend - now if you don't mind I'm having a conversation with my imaginary friend Ali here.",
                "Cheers for the beers!",
                "I was a sailor once, you know..."
        };
        if (e.getItem().getId() == 1917) {
            player.getInventory().deleteItem(e.getItem());
            player.startConversation(new Dialogue()
                    .addNPC(npc, HeadE.DRUNK, responses[(Utils.random(1, 10))], () -> npc.forceTalk("Burp"))
            );
        }
        else
            player.startConversation(new Dialogue()
                    .addNPC(npc, HeadE.DRUNK_ANGRY, "Eh? What's this? I don't want that, get me a beer.")
            );
    });
}

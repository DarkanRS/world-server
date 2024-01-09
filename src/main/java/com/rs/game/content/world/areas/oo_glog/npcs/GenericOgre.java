package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GenericOgre {
    public static NPCClickHandler GenericOgre = new NPCClickHandler(new Object[]{ 15235, 15236, 15237, 15238, 15239, 15240, 15241, 15242, 15243, 15244 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Hi, human.")
                .addPlayer(HeadE.CALM_TALK, "Hi, ogre.")
                .addPlayer(HeadE.CALM_TALK, "How are you today, little ogre?")
                .addNext(() -> randomDialogue(player, npc))
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Hi, human.")
                .addPlayer(HeadE.CALM_TALK, "Hi, ogre.")
                .addOptions(ops -> {
                    ops.add("How are you today?", () -> randomDialogue(player, npc));
                    int npcId = npc.getId();
                    switch (npc.getId()) {
                        // Thuddley & Snert
                        case 15235, 15240 ->
                                ops.add("Can you tell me about this copper-coloured pool?")
                                        .addPlayer(HeadE.CONFUSED, "Can you tell me about this copper-coloured pool?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Yeah, it red!")
                                        .addPlayer(HeadE.ROLL_EYES, "Yes, I can see that.")
                                        .addNPC(npcId, HeadE.CHILD_THINKING, "Then why you ask, silly human?")
                                        .addPlayer(HeadE.CALM_TALK, "Well, it's hard to explain. I just sense a strange energy coming from this pool. Do you know anything about it?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Oh, well, it called de 'Bandos pool' round here. It s'pposed to be special-special.")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "It hard to explain to human creature like you. Maybe you need talk to auntie Seegud. She explain good about these things.");

                        // Tyke & Grr'bah
                        case 15236, 15241 ->
                                ops.add("Can you tell me about this stinky, green spring?")
                                        .addPlayer(HeadE.CONFUSED, "Can you tell me about this stinky, green pool?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "What you mean, stinky? It smell delightful!")
                                        .addPlayer(HeadE.ROLL_EYES, "Um, right. Semantics aside, can you tell me anything about it?")
                                        .addNPC(npcId, HeadE.CHILD_THINKING, "Oh, yeah, it very nice place to have a bath. You smell pretty, and mum says it refreshes the soul.")
                                        .addPlayer(HeadE.CALM_TALK, "What do you mean?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Well, it's a bit confusing, but mum says that taking a bath here is as good as going to the altar.");

                        // Snarrl & Chomp
                        case 15237, 15242 ->
                                ops.add("Can you tell me about this salt-water spring?")
                                        .addPlayer(HeadE.CONFUSED, "Can you tell me about this salt-water spring?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Yeah, sure, human! The water flows fast from underground. When you bathe here, it makes you flow fast overground for a long, long time.")
                                        .addPlayer(HeadE.CONFUSED, "Flow fast?")
                                        .addNPC(npcId, HeadE.CHILD_THINKING, "You know, flow on your feetsies. Fast-like. Quick, like a bunny!")
                                        .addPlayer(HeadE.CALM_TALK, "Are you talking about running?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Yeah, run, flow - same thing. Have a bath here, it doesn't matter how heavy you are, how much you carry - you can flow for a long time!")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Thanks, that's good to know.");

                        // Snarrk & Grubb
                        case 15238, 15243 ->
                                ops.add("Can you tell me about this thermal bath?")
                                        .addPlayer(HeadE.CONFUSED, "Can you tell me about this thermal bath?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Yes, it very hot!")
                                        .addPlayer(HeadE.CONFUSED, "Yes. Hence the word 'thermal' as a descriptor. How terribly observant of you.")
                                        .addNPC(npcId, HeadE.CHILD_THINKING, "Heat come up from belly of de ground, make you feel lots better when you swim in it.")
                                        .addPlayer(HeadE.CALM_TALK, "What do you mean?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Oh, it simple! You feel poisoned, you feel diseased or you just take beating in battle, just take bath in de hot water and you feel all better in no time. It easy to notice effect if you just try it out - but maybe you human types not smart enough to notice things much.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Ahem!")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Oo, no spread that cough to me - it sound bad! You better take a swim right now, feel better!");

                        // Grunther & I'rk
                        case 15239, 15244 ->
                                ops.add("Can you tell me about this mud pool?")
                                        .addPlayer(HeadE.CONFUSED, "Can you tell me about this mud pool?")
                                        .addNPC(npcId, HeadE.CHILD_CALM_TALK, "Dat my favourite pool, dat is! It nice and sticky mud, very warm and comfy. It de muddiest mud around!")
                                        .addPlayer(HeadE.CONFUSED, "The muddiest mud?")
                                        .addNPC(npcId, HeadE.CHILD_THINKING, "Plus, if you lie in de mud for a while, it much easier to sneaky-sneak hunt de creatures. De mud, it cover your awful human stink!")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Huh! I'll have to try that sometime.");
                    }
                })
        );
    }

    private static void randomDialogue(Player player, NPC npc) {
        int randomCase = Utils.random(1, 9);

        switch (randomCase) {
            case 1:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Can I have some shiny pretties, human?")
                        .addPlayer(HeadE.SKEPTICAL, "What makes you think I'd give my shiny pretties to you?")
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "That skinny lady at de bank say visitors bring lots of shiny pretties with them when they come visit Oo'glog.")
                        .addNPC(npc.getId(), HeadE.CHILD_TANTRUM, "Me want my share!")
                        .addPlayer(HeadE.HAPPY_TALKING, "Ah, you see, I've given your share to that skinny lady at the bank; you'll have to ask her for it yourself.")
                        .addPlayer(HeadE.HAPPY_TALKING, "Make sure you ask really loudly and growl at her every time she uses a word you don't understand, okay?")
                        .addNPC(npc.getId(), HeadE.CHILD_HAPPY_TALK, "Yay! Thanks human. I'm gonna get my shiny pretties, even if I have to growl all afternoon.")
                );
                break;
            case 2:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Fine, please and thank you very much, madam sir!")
                        .addPlayer(HeadE.HAPPY_TALKING, "You're very...polite.")
                        .addNPC(npc.getId(), HeadE.CHEERFUL, "Me know! Me practising! Balnea say I can have job in Customer Rations when I grow up if me polite enough.")
                        .addPlayer(HeadE.CONFUSED, "Don't you mean 'Customer Relations'?")
                        .addNPC(npc.getId(), HeadE.CHILD_TANTRUM, "Blechh! Dat not sound near as tasty as 'Customer Rations'. Me not sure me want de job after all.")
                );
                break;
            case 3:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Hey, human. What did you bring me?")
                        .addPlayer(HeadE.LAUGH, "Hmm, let me think carefully about this. Oh, yes, I remember, now! Absolutely nothing.")
                        .addNPC(npc.getId(), HeadE.CHILD_SAD, "Aw, shucks.")
                );
                break;
            case 4:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "How does it feel to be so puny wee small, human?")
                        .addPlayer(HeadE.CONFUSED, "Oh, I dunno. How does it feel to be so incredibly dense?")
                        .addNPC(npc.getId(), HeadE.CALM_TALK, "Uhh...what dat s'pposed to mean?")
                        .addPlayer(HeadE.SKEPTICAL, "Never mind.")
                );
                break;
            case 5:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Me like pie!")
                        .addPlayer(HeadE.HAPPY_TALKING, "Doesn't everyone?")
                        .addNPC(npc.getId(), HeadE.CHILD_LAUGH, "Pie! Pie! Pie! Pie! Pie!")
                        .addPlayer(HeadE.CHEERFUL_EXPOSITION, "Yes, dear, pie.")
                );
                break;
            case 6:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Me wanna go visit Fycie 'n Bugs!")
                        .addPlayer(HeadE.HAPPY_TALKING, "Yes, they're both delightful individuals.")
                        .addNPC(npc.getId(), HeadE.CHILD_THINKING, "Will you take me to see dem, human?")
                        .addPlayer(HeadE.SKEPTICAL, "Didn't your mother ever teach you not to talk to strangers?")
                );
                break;
            case 7:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Me wants CHOMPY for dinner!")
                        .addPlayer(HeadE.LAUGH, "Me hopes you GETS chompy for dinner!")
                        .addNPC(npc.getId(), HeadE.CHILD_FRUSTRATED, "What you talk weird like dat for, human? You sound silly!")
                );
                break;
            case 8:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "Not so very good. Me just drop me lunch in de pool.")
                        .addPlayer(HeadE.SKEPTICAL, "Ew...I think I can see it floating over there.")
                        .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Oh, thank you human! I have it as a snack next time I go for swim!")
                );
                break;
            case 9:
                player.startConversation(new Dialogue()
                        .addNPC(npc.getId(), HeadE.CHILD_CALM_TALK, "You very cute, little creature. Me want to have a human as a pet.")
                        .addNPC(npc.getId(), HeadE.CHILD_LAUGH, "What you doing, human? You busy? You want be my little pet human creature?")
                        .addPlayer(HeadE.CONFUSED, "Uh...I think I'm busy at the moment.")
                        .addNPC(npc.getId(), HeadE.CHILD_AWE, "Pwetty please?")
                        .addPlayer(HeadE.SHAKING_HEAD, "Look, sorry to disappoint, but this isn't going to happen.")
                        .addNPC(npc.getId(), HeadE.CHILD_FRUSTRATED, "Aw, you no fun, human.")
                );
                break;
        }
    }
}

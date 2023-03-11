package com.rs.game.content.quests.wolfwhistle;

import com.rs.game.content.world.npcs.Pikkupstix;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.wolfwhistle.WolfWhistle.*;

@PluginEventHandler
public class QuestPikkupstix extends Conversation {
    // npcs
    final static int PIKKUPSTIX = 6988;

    // animations
    static final int ANIMATION_ENCHANT = 15924;

    // spot animations
    static final int SPOT_ANIM_ENCHANT = 2814;


    public QuestPikkupstix(Player p, NPC pikkupstix) {
        super(p);

        switch (player.getQuestManager().getStage(Quest.WOLF_WHISTLE)) {
            case NOT_STARTED ->
                addPlayer(HeadE.CONFUSED, "Hello there. I'm " + player.getDisplayName() + ", the adventurer. Do you have a quest for me?")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well I do have something you could look into, although I don't know if I would call it a quest, as such.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "But if you do have some time free, could you look into the whereabouts of my two assistants, Scalectrix and Bowloftrix?")
                    .addPlayer(HeadE.CALM_TALK, "Why, have they gone missing?")
                    .addNPC(PIKKUPSTIX, HeadE.SAD, "Well, they are only slightly late compared to poor Feletrix...")
                    .addNPC(PIKKUPSTIX, HeadE.SHAKING_HEAD, "Actually they are quite late, to be honest. I sent them out this morning to gather supplies, and they have not returned. It is a little worrying.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "They were over by the old dry well in west Taverleym opposite the watermill. Could you take a look for me?")
                    .addPlayer(HeadE.CHEERFUL_EXPOSITION, "All right. I am sure they will be fine.")
                    .addPlayer(HeadE.CALM_TALK, "They are probably just taking their time, or maybe stopped for a chat.")
                    .addNPC(PIKKUPSTIX, HeadE.LAUGH, "Oh, I do hope so! Thank you my lad!")
                    .addNext(() -> player.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.FIND_SCALECTRIX));
            case FIND_SCALECTRIX -> {
            }
            case PIKKUPSTIX_HELP ->
                // pikkupstix is supposed to offer looping quest dialogue from WOLPERTINGER_MATERIALS here
                addPlayer(HeadE.MORTIFIED, "Bowloftrix has been kidnapped by trolls!")
                    .addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "Oh no, this is terrible! Is Scalectrix all right?")
                    .addPlayer(HeadE.CALM_TALK, "Yes, she is keeping an eye on the trolls by the well. She says there are too many trolls to attack directly.")
                    .addPlayer(HeadE.CALM_TALK, "She tried scaring them away, but a spirit wolf didn't work. Do you have anything more powerful that could work?")
                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "I...might well do. But it's risky.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Although with you assisting me we might well do it. I sense deep wells of energy within you that you could tap.")
                    .addNPC(PIKKUPSTIX, HeadE.CHEERFUL, "You might even survive!")
                    .addPlayer(HeadE.CHEERFUL, "Well thanks, I...")
                    .addPlayer(HeadE.MORTIFIED_JAW_DROP, "...I might what now?")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "We are going to attempt a momentous  feat of summoning! We are going to try and summon...a giant wolpertinger!")
                    .addPlayer(HeadE.CONFUSED, "How will that help?")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "A giant wolpertinger is a legendary spirit creature that can generate an aura of fear so potent that even a hore of trolls will flee in terror at its approach.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Usually such a summoning would take a circle of experience druids, but we have to clear out that horde before they attack.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "We will begin immediately. I need you to bring me a few things.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "I will need you to bring me my ancient wolf bone amulet, some white hare meat, and an embroidered pouch. I will also need to give you a blessed spirit shard, and a grey charm.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The amulet should be with my assistant, Stikklebrix. He took it to White Wolf Mountain, which is the large range to the west of Taverley.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The hare meat is available from the pet shop, which is on the bottom floor of the building to the south of here.")
                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "The pouch is lost somewhere upstairs. I managed to lose it in a rather violent kafuffle.", () -> {
                        player.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.WOLPERTINGER_MATERIALS);
                        player.getVars().setVarBit(10734, 1);
                    });
            case WOLPERTINGER_MATERIALS -> {
                if (WolfWhistle.wolfWhistleObeliskReadyToInfusePouch(p)) {
                    addNPC(PIKKUPSTIX, HeadE.CONFUSED, "How goes the hunt?")
                        .addPlayer(HeadE.CHEERFUL_EXPOSITION, "I have the items all here!")
                        .addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "I see what I have heard about you is more than true! You've got everything right here!")
                        .addNPC(PIKKUPSTIX, HeadE.CALM, "Now, before you go running off, let me do something...")
                        .addNext(() -> {
                            pikkupstix.setNextAnimation(new Animation(ANIMATION_ENCHANT));
                            pikkupstix.setNextSpotAnim(new SpotAnim(SPOT_ANIM_ENCHANT));
                            //pikkupstix.playSound(123, 1); supposed to play a sound
                            player.lock(3);
                        })
                        .addPlayer(HeadE.CONFUSED, "What was that about?")
                        .addNPC(PIKKUPSTIX, HeadE.CALM, "I have some spirit helpers that sometimes do tasks for me. In this case I just asked them to make sure that these items will all return to me if they get lost or damaged.")
                        .addNPC(PIKKUPSTIX, HeadE.CALM, "Now, all you have to do is go and infuse them at the obelisk there. You won't have to worry about picking which pouch to make, as you know exactly what to make from these items.")
                        .addPlayer(HeadE.CALM, "Then what?")
                        .addNPC(PIKKUPSTIX, HeadE.CALM, "Well, you should come over here and let me inspect your work. You never know what could go wrong with such a monumental summoning effort like this one!")
                        .addNext(() -> {
                            player.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.WOLPERTINGER_CREATION);
                            player.getVars().setVarBit(10734, 0);
                        });
                } else {
                    addNPC(PIKKUPSTIX, HeadE.CONFUSED, "How goes the hunt?")
                        .addPlayer(HeadE.CALM, "I need to ask about the items I need to make the Giant Wolpertinger pouch.")
                        .addOptions("Select an Option", new Options() {
                            @Override
                            public void create() {
                                if (!player.getInventory().containsItem(ANCIENT_WOLF_BONE_AMULET) && !player.getBank().containsItem(ANCIENT_WOLF_BONE_AMULET, 1)) {
                                    if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("ANCIENT_AMULET")) {
                                        option("Ask about the wolf bone amulet.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "No matter. The amulet is enchanted to return to the owner, and in this case that is currently Stikklebrix. So go and search him again.")
                                            .addNPC(PIKKUPSTIX, HeadE.FRUSTRATED, "This time, try and take better care of it!")
                                        );
                                    } else {
                                        option("Ask about the wolf bone amulet.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "I gave it to my assistant, Stikklebrix, when I sent him over to White Wolf Mountain to bring me some wolf fur. For luck, you know.")
                                            .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Although now I come to think of it, I haven't seen him in a while...what day is it again? Huh...that means he's been gone about a week. I do hope he's all right.")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "Anyway, I'm sure you'll find him in good health. White Wolf Mountain is to the west of here. You will need to cross the river at the bridge to the south-west.")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "I'd suggest taking some food. There are vicious wolves there. If you see one, then just run past it if you can.")
                                        );
                                    }
                                }
                                if (!player.getInventory().containsItem(WHITE_HARE_MEAT) && !player.getBank().containsItem(WHITE_HARE_MEAT, 1)) {
                                    if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("HARE_MEAT")) {
                                        option("Ask about the white hare meat.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "Wonderful, where is it?")
                                            .addPlayer(HeadE.CHEERFUL_EXPOSITION, "I have no idea!")
                                            .addNPC(PIKKUPSTIX, HeadE.AMAZED, "Hmm?")
                                            .addPlayer(HeadE.CONFUSED, "Should I check with the pet shop owner to see if he has some more?")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Yes. Yes you should.")
                                        );
                                    } else {
                                        option("Ask about the white hare meat.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "The pet shop owner in town has some. They import it for some of the more pampered pets, although to be honest more of it ends up on the plates of pet owners than in doggy-bowls.")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "The pet shop is on the bottom floor of the building to the south of my house.")
                                            .addNPC(PIKKUPSTIX, HeadE.NERVOUS, "I would go and get it myself, but since the bird seed incident we are not on speaking terms.")
                                        );
                                    }
                                }
                                if (!player.getInventory().containsItem(EMBROIDERED_POUCH) && !player.getBank().containsItem(EMBROIDERED_POUCH, 1)) {
                                    if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("EMBROIDERED_POUCH")) {
                                        option("Ask about the embroidered pouch.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Where did you lose it? Have you tried looking there for it again?")
                                        );
                                    } else {
                                        option("Ask about the embroidered pouch.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It's in my room upstairs, somewhere. We lost it when Beezeltrix summoned that smoke devil.")
                                            .addPlayer(HeadE.CONFUSED, "What happened to her?")
                                            .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Oh it was terrible! Really, true terrible...I've never seen such...carnage and devastation!")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "We have managed to clean up all the glass and soot, but I couldn't find it. Obviously a fresh pair of eyes will do the trick.")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "Oh, and if you find a foot up there, do let me know. There's a good chap.")
                                            .addPlayer(HeadE.AMAZED, "A foot? As in a 'foot' foot or a foot of something?")
                                            .addNPC(PIKKUPSTIX, HeadE.LAUGH, "Never mind! I remember where we found it now.")
                                        );
                                    }
                                }
                                if (!player.getInventory().containsItem(RARE_SUMMONING_ITEMS) && !player.getBank().containsItem(RARE_SUMMONING_ITEMS, 1)) {
                                    if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("RARE_ITEMS")) {
                                        option("Ask about the rare summoning items.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Yes.")
                                            .addPlayer(HeadE.SECRETIVE, "Theorectically, HOW rare and HOW unique were they?")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Rare and unique enough for me to enchant them to return to me if they were lost.")
                                            .addNPC(PIKKUPSTIX, HeadE.FRUSTRATED, "So, imagine my surprise when I turn around and find them lying on my table.")
                                            .addPlayer(HeadE.SECRETIVE, "Ahaha...funny story...")
                                            .addItem(RARE_SUMMONING_ITEMS, "Pikkupstix carefully hands you the rare summoning items.", () -> player.getInventory().addItem(RARE_SUMMONING_ITEMS))
                                            .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Save it. Take them back, and try not to drop them this time.")

                                        );
                                    } else {
                                        option("Ask for the rare summoning items.", new Dialogue()
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "Of course. Remember, these are EXTREMELY rare and precious, so I plead that you take good care of them.")
                                            .addItem(RARE_SUMMONING_ITEMS, "Pikkupstix carefully hands you the grey charm and blessed spirit shard.", () -> {
                                                player.getInventory().addItem(RARE_SUMMONING_ITEMS);
                                                player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).setB("RARE_ITEMS", true);
                                            })
                                            .addPlayer(HeadE.LAUGH, "Well, you know, I'm pretty skilled at holding things. I should be able to keep this safe.")
                                            .addNPC(PIKKUPSTIX, HeadE.CALM, "I hope you're right. These are very valuable relics.")
                                        );
                                    }
                                }
                                option("I think I had better go.", new Dialogue()
                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "Well, best of luck in your hunt.")
                                );
                            }
                        });
                }
            }
            case WolfWhistle.WOLPERTINGER_CREATION ->
                addPlayer(HeadE.CALM_TALK, "I need to ask you something about the quest...")
                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What is it you need?")
                    .addOptions(new Options() {
                        @Override
                        public void create() {
                            if (!p.getInventory().containsItem(ANCIENT_WOLF_BONE_AMULET) && !p.getBank().containsItem(ANCIENT_WOLF_BONE_AMULET, 1)) {
                                option("Discuss the quest", new Dialogue()
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I lost your amulet.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "I know. The spirits brought it back to me, covered in Guthix knows what kind of muck and slime.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "I don't know what you were thinking, but try and keep a hold of this one!", () -> p.getInventory().addItem(ANCIENT_WOLF_BONE_AMULET)));
                            } else if (!p.getInventory().containsItem(WHITE_HARE_MEAT) && !p.getBank().containsItem(WHITE_HARE_MEAT, 1)) {
                                option("Discuss the quest", new Dialogue()
                                    .addPlayer(HeadE.NERVOUS, "I put the meat down for a second and it vanished. I think wolves took it.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well it's a good job I enchanted it.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Keep better track of it this time.", () -> p.getInventory().addItem(WHITE_HARE_MEAT)));
                            } else if (!p.getInventory().containsItem(EMBROIDERED_POUCH) && !p.getBank().containsItem(EMBROIDERED_POUCH, 1)) {
                                option("Discuss the quest", new Dialogue()
                                    .addPlayer(HeadE.SECRETIVE, "Oh hey...you remember that pouch I found?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "You mean the one my spirit helpers returned to me because you un-found it?", () -> p.getInventory().addItem(EMBROIDERED_POUCH))
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Yeah, that one...")
                                );
                            } else if (!p.getInventory().containsItem(RARE_SUMMONING_ITEMS) && !p.getBank().containsItem(RARE_SUMMONING_ITEMS, 1)) {
                                option("Discuss the quest", new Dialogue()
                                    .addPlayer(HeadE.SECRETIVE, "That enchantment that you cast on all those valuable items you have me. How effective was it?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Very effective.")
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Uh, well I'd best be going now that I got these back.", () -> p.getInventory().addItem(RARE_SUMMONING_ITEMS)));
                            } else {
                                option("Discuss the quest", new Dialogue()
                                    .addPlayer(HeadE.CONFUSED, "What do I need to do again?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, you need to take those items over there and infuse them at the obelisk to make them into a giant wolpertinger pouch.")
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "I think that about covers it. Do you need any more help?")
                                    .addPlayer(HeadE.CALM_TALK, "Nope, I think I can handle that.")
                                );
                            }

                            option("You really seem to have had some problems with your assistants.", new Dialogue()
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "You are right, it has been pretty hard since the war started.")
                                .addPlayer(HeadE.CONFUSED, "Well what's happened? Have the trolls been killing them, or has it all been down to accidents?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "It is a mixture of both, really. To be honest, I have always found that my assistants have had a lot of eagerness and a lot less common sense for self-preservation.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, let me see...You already know about Stikklebrix...who else was there?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "There was poor Lunatrix, who went mad. He tried to commune with some elder spirits, and they were not happy.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Last I saw of him, he went running out of the door with his pants on his head...")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Ashtrix. He managed to cut his own hand off, the poor fool.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It would have been all right, except he then managed to wedge a hacksaw in the stump, and in his delirium wouldn't let anyone help him.")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Spartrix, who was captured by the Black Knights and taken as a slave.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "I hear that he tried to lead a slaved rebellion. Which, while noble in itself, is not really the sort of thing a 5-foot, sickly, bookish druid should do.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Especially when unarmed and facing evil knights in full plate mail.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Let's see...there was Beezeltrix and the smoke devil; we literally lost Felitrix; those trolls took the twins when they were healing soldiers on the front line...")
                                .addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "And the less said about Hastrix, the better!")
                                .addPlayer(HeadE.AMAZED, "Wow...you really haven't had much luck with assistants.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "It has been one heck of a busy week, I can tell you.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, is there anything else you need?")
                                .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false))));
                        }
                    });
            case WolfWhistle.WOLPERTINGER_POUCH_CHECK -> {
                if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("WOLPERTINGER_POUCH")) {
                    addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Have you done it? Do you have the pouch?");
                } else {
                    addPlayer(HeadE.CALM_TALK, "I need to ask you something about the quest...")
                        .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What is it you need?")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                // TODO: missing dialogue?
                            }
                        });
                }

                addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Have you done it? Do you have the pouch?")
                    .addOptions(new Options() {
                        @Override
                        public void create() {
                            if (player.getInventory().containsItem(GIANT_WOLPERTINGER_POUCH)) {
                                option("I made the giant wolpertinger pouch!", new Dialogue()
                                    .addNPC(PIKKUPSTIX, HeadE.AMAZED, "Phenomenal! I knew I sensed great things in your future my assistant. Come, let me see it!")
                                    .addPlayer(HeadE.CALM, "Here it is.")
                                    .addNPC(PIKKUPSTIX, HeadE.AMAZED, "I had never thought we'd have needed one of these in my lifetime. Well done.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "I think you are ready. Take this to Scalectrix. With her help, you should be able to summon this spirit beast and rout the trolls.")
                                    .addPlayer(HeadE.CALM, "So can't I do this on my own?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "No, no. The energy needed to summon this beast is too much for just one person. Even with the pair of you it will only last a few moments.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "But Scalectrix is strong too. Between you both you should be able to accomplish this.")
                                    .addNext(() -> p.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.SAVE_BOWLOFTRIX)));
                            } else {
                                if (player.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("WOLPERTINGER_POUCH")) {
                                    option("I made the giant wolpertinger pouch!", new Dialogue()
                                        .addPlayer(HeadE.SECRETIVE, "Well, between here and the obelisk I may have sort of managed to lose it...")
                                        .addNPC(PIKKUPSTIX, HeadE.TERRIFIED, "WHAT? One of the most potent summoning pouches of this era and you LOST it?")
                                        .addNPC(PIKKUPSTIX, HeadE.AMAZED, "Do you have any idea what this thing could do in the wrong hands? Any idea at all?")
                                        .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "You are lucky that I planned for this contingency. The enchantment I put on the items may well work now they have been combined into a pouch.")
                                        .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Now, don't lose this one!", () -> p.getInventory().addItem(GIANT_WOLPERTINGER_POUCH)));
                                } else {
                                    addPlayer(HeadE.CALM_TALK, "I need to ask you something about the quest...")
                                        .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What is it you need?")
                                        .addOptions(new Options() {
                                            @Override
                                            public void create() {
                                                option("Discuss the quest", new Dialogue()
                                                    .addPlayer(HeadE.CONFUSED, "What do I need to do again?")
                                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "well, you need to take those items over there and infuse them at the obelisk to make them into a giant wolpertinger pouch.")
                                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "I think that about covers it. Do you need any more help?")
                                                    .addPlayer(HeadE.CALM, "Nope, I think I can handle that.")
                                                );
                                                option("You really seem to have had some problems with your assistants.", new Dialogue()
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD, "You are right, it has been pretty hard since the war started.")
                                                    .addPlayer(HeadE.CONFUSED, "Well what's happened? Have the trolls been killing them, or has it all been down to accidents?")
                                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "It is a mixture of both, really. To be honest, I have always found that my assistants have had a lot of eagerness and a lot less common sense for self-preservation.")
                                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, let me see...You already know about Stikklebrix...who else was there?")
                                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "There was poor Lunatrix, who went mad. He tried to commune with some elder spirits, and they were not happy.")
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Last I saw of him, he went running out of the door with his pants on his head...")
                                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Ashtrix. He managed to cut his own hand off, the poor fool.")
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It would have been all right, except he then managed to wedge a hacksaw in the stump, and in his delirium wouldn't let anyone help him.")
                                                    .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Spartrix, who was captured by the Black Knights and taken as a slave.")
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "I hear that he tried to lead a slaved rebellion. Which, while noble in itself, is not really the sort of thing a 5-foot, sickly, bookish druid should do.")
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Especially when unarmed and facing evil knights in full plate mail.")
                                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Let's see...there was Beezeltrix and the smoke devil; we literally lost Felitrix; those trolls took the twins when they were healing soldiers on the front line...")
                                                    .addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "And the less said about Hastrix, the better!")
                                                    .addPlayer(HeadE.AMAZED, "Wow...you really haven't had much luck with assistants.")
                                                    .addNPC(PIKKUPSTIX, HeadE.SAD, "It has been one heck of a busy week, I can tell you.")
                                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, is there anything else you need?")
                                                    .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false))));
                                            }
                                        });
                                }
                            }
                            option("You really seem to have had some problems with your assistants.", new Dialogue()
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "You are right, it has been pretty hard since the war started.")
                                .addPlayer(HeadE.CONFUSED, "Well what's happened? Have the trolls been killing them, or has it all been down to accidents?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "It is a mixture of both, really. To be honest, I have always found that my assistants have had a lot of eagerness and a lot less common sense for self-preservation.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, let me see...You already know about Stikklebrix...who else was there?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "There was poor Lunatrix, who went mad. He tried to commune with some elder spirits, and they were not happy.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Last I saw of him, he went running out of the door with his pants on his head...")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Ashtrix. He managed to cut his own hand off, the poor fool.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It would have been all right, except he then managed to wedge a hacksaw in the stump, and in his delirium wouldn't let anyone help him.")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Spartrix, who was captured by the Black Knights and taken as a slave.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "I hear that he tried to lead a slaved rebellion. Which, while noble in itself, is not really the sort of thing a 5-foot, sickly, bookish druid should do.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Especially when unarmed and facing evil knights in full plate mail.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Let's see...there was Beezeltrix and the smoke devil; we literally lost Felitrix; those trolls took the twins when they were healing soldiers on the front line...")
                                .addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "And the less said about Hastrix, the better!")
                                .addPlayer(HeadE.AMAZED, "Wow...you really haven't had much luck with assistants.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "It has been one heck of a busy week, I can tell you.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, is there anything else you need?")
                                .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false))));
                        }
                    });
            }
            case WolfWhistle.SAVE_BOWLOFTRIX ->
                addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What are you waiting for? You have to get that pouch to Scalectrix as soon as possible!")
                    .addOptions(new Options() {
                        @Override
                        public void create() {
                            if (!player.getInventory().containsItem(GIANT_WOLPERTINGER_POUCH) && !player.getBank().containsItem(GIANT_WOLPERTINGER_POUCH, 1)) {
                                option("Ask where Scalectrix is", new Dialogue()
                                    .addPlayer(HeadE.SECRETIVE, "Mumblemumble...")
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What is it? I can't hear you.")
                                    .addPlayer(HeadE.SECRETIVE, "Mumblemumble...")
                                    .addNPC(PIKKUPSTIX, HeadE.FRUSTRATED, "What was that? You LOST the giant wolpertinger pouch?")
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Mumble?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "And you want me to get it back for you?")
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Mumble...")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Now, don't lose this one!", () -> p.getInventory().addItem(GIANT_WOLPERTINGER_POUCH)));
                            } else {
                                option("Ask where Scalectrix is", new Dialogue()
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What? You've forgotten that she is over at the old well already?")
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Maybe crafting that pouch took more out of you than I epxected...I wonder if this is a symptom of melty-brain syndrome...yes, that could be it.")
                                    .addPlayer(HeadE.AMAZED, "Melty-brain syndrome? What...why didn't you tell me I could get that?")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Well, technically creating a pouch would not cause it. Nobody knows what causes it.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "You could be walking along, minding your own business, and then the next thing you know your brains are squirting out of your ears and nostrils.")
                                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Like what happened to poor old Soltrix...")
                                    .addPlayer(HeadE.SCARED, "Uh...the old well, right?")
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Hmm? Oh yes, Scalectrix. That's where she is. Is there anything else?")
                                    .addPlayer(HeadE.SCARED, "Uh, no. Not right now. If you need me I'll be delivering this pouch, and then trying not to shake my head around too much.")
                                    .addNPC(PIKKUPSTIX, HeadE.CALM_TALK, "Good idea. If you do not have MBS then keeping your head steady until your brain congeals is the best plan.")
                                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Anyways, is there anything else I can help you with?")
                                    .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false))));
                            }
                            option("You really seem to have had some problems with your assistants.", new Dialogue()
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "You are right, it has been pretty hard since the war started.")
                                .addPlayer(HeadE.CONFUSED, "Well what's happened? Have the trolls been killing them, or has it all been down to accidents?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "It is a mixture of both, really. To be honest, I have always found that my assistants have had a lot of eagerness and a lot less common sense for self-preservation.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, let me see...You already know about Stikklebrix...who else was there?")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "There was poor Lunatrix, who went mad. He tried to commune with some elder spirits, and they were not happy.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Last I saw of him, he went running out of the door with his pants on his head...")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Ashtrix. He managed to cut his own hand off, the poor fool.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "It would have been all right, except he then managed to wedge a hacksaw in the stump, and in his delirium wouldn't let anyone help him.")
                                .addNPC(PIKKUPSTIX, HeadE.CALM, "Then there was Spartrix, who was captured by the Black Knights and taken as a slave.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "I hear that he tried to lead a slaved rebellion. Which, while noble in itself, is not really the sort of thing a 5-foot, sickly, bookish druid should do.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "Especially when unarmed and facing evil knights in full plate mail.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Let's see...there was Beezeltrix and the smoke devil; we literally lost Felitrix; those trolls took the twins when they were healing soldiers on the front line...")
                                .addNPC(PIKKUPSTIX, HeadE.MORTIFIED, "And the less said about Hastrix, the better!")
                                .addPlayer(HeadE.AMAZED, "Wow...you really haven't had much luck with assistants.")
                                .addNPC(PIKKUPSTIX, HeadE.SAD, "It has been one heck of a busy week, I can tell you.")
                                .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Now, is there anything else you need?")
                                .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false))));
                        }
                    });
            case WolfWhistle.QUEST_COMPLETE ->
                addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "Well done! I heard the news! You've saved my assistants from a fate worse than death!")
                    .addPlayer(HeadE.CHEERFUL_EXPOSITION, "Yes, that giant wolpertinger really made the difference.")
                    .addPlayer(HeadE.CONFUSED, "Say, do you have the parts needed to summon it again? I'm sure it would come in handy.")
                    .addNPC(PIKKUPSTIX, HeadE.SAD_MILD_LOOK_DOWN, "No, I am afraid not. The pieces you used were rare...unique in some cases.")
                    .addNPC(PIKKUPSTIX, HeadE.HAPPY_TALKING, "But in the end they were all used up for a good cause. And it did take a hero like you to bring it about.")
                    .addPlayer(HeadE.SAD, "Darn. Oh well, at least Bowloftrix is safe...")
                    .addPlayer(HeadE.SECRETIVE, "...for now.")
                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "What was that?")
                    .addPlayer(HeadE.SECRETIVE, "Nothing.")
                    .addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "Anyway, once they are rested, I'll be sending those two off to gather some of those highly poisonous weeds that I need for my research.")
                    .addNPC(PIKKUPSTIX, HeadE.CHEERFUL_EXPOSITION, "They grow in a pretty dangerous place in the wilderness. Tell me, would you like to be my assistant as well?")
                    .addPlayer(HeadE.TERRIFIED, "I have to go! Look at the time! My oven is on and I need to feed the cat!")
                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Strange...that's what most people say when I ask them that.")
                    .addNPC(PIKKUPSTIX, HeadE.CONFUSED, "Is there anything else I can help you with?")
                    .addNext(() -> p.startConversation(new Pikkupstix(p, pikkupstix, false)));
            default -> p.sendMessage("Uh oh");
        }
        create();
    }

    public static String getNextOptionTextPikkupstix(Player p) {
        return switch (p.getQuestManager().getStage(Quest.WOLF_WHISTLE)) {
            case NOT_STARTED -> "Do you have a quest for me?";
            case WolfWhistle.PIKKUPSTIX_HELP -> "Bowloftrix has been kidnapped by trolls!";
            case WolfWhistle.FIND_SCALECTRIX,
                WolfWhistle.WOLPERTINGER_MATERIALS,
                WolfWhistle.WOLPERTINGER_CREATION,
                WolfWhistle.WOLPERTINGER_POUCH_CHECK,
                WolfWhistle.SAVE_BOWLOFTRIX,
                WolfWhistle.QUEST_COMPLETE -> "I need to ask you something about the quest...";
            default -> null;
        };
    }

}

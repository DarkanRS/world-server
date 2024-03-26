package com.rs.game.content.miniquests.abyss

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ZamorakMageD(p: Player, npc: NPC) {
    init {
        p.startConversation {
            if (p.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS) == EnterTheAbyss.NOT_STARTED) {
                npc(npc, HeadE.FRUSTRATED, "Ah, you again. What was it you wanted? The Wilderness is hardly the appropriate place for a conversation now, is it?")
            }

            when (p.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS)) {
                EnterTheAbyss.SCRYING_ORB -> {
                    npc(npc, HeadE.CONFUSED, "Well? Have you managed to use my scrying orb to obtain the information yet?")
                    if (!p.inventory.containsOneItem(5518, 5519)) {
                        player(HeadE.WORRIED, "Uh... No.... I kind of lost that orb thingy that you gave me...")
                        npc(npc, HeadE.FRUSTRATED, "What? Incompetent fool. Take this. And do not make me regret allying myself with you.") {
                            p.inventory.addItemDrop(5519, 1)
                        }
                        return@startConversation
                    }
                    if (p.inventory.containsItem(5518, 1)) {
                        player(HeadE.CHEERFUL, "Yes I have! I've got it right here!")
                        npc(npc, HeadE.SKEPTICAL, "Excellent. Give it here, and I shall examine the findings. Speak to me in a small while.") {
                            p.inventory.deleteItem(5518, 28)
                            p.inventory.deleteItem(5519, 28)
                            p.miniquestManager.setStage(Miniquest.ENTER_THE_ABYSS, EnterTheAbyss.COMPLETED_SCRYING_ORB)
                        }
                        return@startConversation
                    }
                    player(HeadE.WORRIED, "No... Actually, I had something I wanted to ask you...")
                    npc(npc, HeadE.FRUSTRATED, "I assumed the task to be self-explanatory. What is it you wish to know?")
                }

                EnterTheAbyss.COMPLETED_SCRYING_ORB -> {
                    player(HeadE.CONFUSED, "So... that's my end of the deal upheld. What do I get in return?")
                    npc(npc, HeadE.CALM_TALK, "Indeed. A deal is always a deal. I offer you three things as a reward for your efforts on behalf of my Lord Zamorak; The first is knowledge. I offer you my collected research on the abyss. ")
                    npc(npc, HeadE.CALM_TALK, "I also offer you 1,000 points of experience in RuneCrafting for your trouble. Your second gift is convenience. Here, you may take this pouch I discovered amidst my research.")
                    npc(npc, HeadE.CALM_TALK, "You will find it to have certain... interesting properties. Your final gift is that of movement. I will from now on offer you a teleport to the abyss whenever you should require it.")
                    player(HeadE.CONFUSED, "Huh? Abyss? What are you talking about? You told me that you would help me with RuneCrafting!")
                    npc(npc, HeadE.CALM_TALK, "And so I have done. Read my research notes, they may enlighten you somewhat.")
                    exec { p.miniquestManager.complete(Miniquest.ENTER_THE_ABYSS) }
                    return@startConversation
                }
            }

            label("startOptions")
            options {
                when (p.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS)) {
                    EnterTheAbyss.MEET_IN_VARROCK  -> {
                        if (p.isQuestComplete(Quest.RUNE_MYSTERIES)) {
                            op("Where do you get your runes from?") {
                                player(HeadE.CONFUSED, "Where do you get your runes from? No offence, but people around here don't exactly like 'your type'.")
                                npc(npc, HeadE.FRUSTRATED, "My 'type'? Explain.")
                                player(HeadE.SKEPTICAL_THINKING, "You know... Scary bearded men in dark clothing with unhealthy obsessions with destruction and stuff.")
                                npc(npc, HeadE.CALM_TALK, "Hmmm. Well, you may be right, the foolish Saradominists that own this pathetic city don't appreciate loyal Zamorakians, it is true.")
                                player(HeadE.SKEPTICAL, "So you can't be getting your runes anywhere around here...")
                                npc(npc, HeadE.SECRETIVE, "That is correct, stranger. The Zamorakian Brotherhood has a method of manufacturing runes that it keeps a closely guarded secret.")
                                player(HeadE.CHEERFUL, "Oh, you mean the whole teleporting to the rune essence mine, mining some essence, then using the talismans to locate the Rune Temples, then binding runes there? I know all about it...")
                                npc(npc, HeadE.AMAZED, "WHAT? I... but... you... Tell me, this is important: You have access to the rune essence mine? The Saradominist wizards will cast their teleport spell for you?")
                                player(HeadE.CONFUSED, "You mean they won't cast it for you?")
                                npc(npc, HeadE.FRUSTRATED, "No, not at all. Ever since the Saradominist wizards betrayed us a hundred years ago and blamed us for the destruction of the Wizards' Tower, they have refused to share information with our order.")
                                npc(npc, HeadE.FRUSTRATED, "We occasionally manage to plunder small samples of rune essence but we have had to make do without a reliable supply. But if they trust you...this changes everything.")
                                player(HeadE.CONFUSED, "How do you mean?")
                                npc(npc, HeadE.CALM_TALK, "For many years there has been a struggle for power on this world.")
                                npc(npc, HeadE.CALM_TALK, "You may dispute the morality of each side as you wish, but the stalemate that exists between my Lord Zamorak and that pathetic meddling fool Saradomin has meant that our struggles have become more secretive.")
                                npc(npc, HeadE.CALM_TALK, "We exist in a 'cold war' if you will, each side fearful of letting the other gain too much power, and each side equally fearful of entering into open warfare for fear of bringing our struggles to the attention of... other beings.")
                                player(HeadE.CONFUSED, "You mean Guthix?")
                                npc(npc, HeadE.CALM_TALK, "Indeed. Amongst others. But since the destruction of the first Tower the Saradominist wizards have had exclusive access to the rune essence mine, which has shifted the balance of power dangerously towards one side.")
                                npc(npc, HeadE.CALM_TALK, "I implore you adventurer, you may or may not agree with my aims, but you cannot allow such a shift in the balance of power to continue.")
                                npc(npc, HeadE.CONFUSED, "Will you help me and my fellow Zamorakians to access the essence mine? In return I will share with you the research we have gathered.")
                                options("Help the Zamorakian mage?") {
                                    op("Yes") {
                                        player(HeadE.CALM_TALK, "Okay, I'll help you. What can I do?")
                                        npc(npc, HeadE.CALM_TALK, "All I need from you is the spell that will teleport me to this essence mine. That should be sufficient for the armies of Zamorak to once more begin stockpiling magic for war.")
                                        player(HeadE.SKEPTICAL_THINKING, "Oh. Erm.... I don't actually know that spell.")
                                        npc(npc, HeadE.CONFUSED, "What? Then how do you access this location?")
                                        player(HeadE.CHEERFUL, "Oh, well, people who do know the spell teleport me there directly. Apparently they wouldn't teach it to me to try and keep the location secret.")
                                        npc(npc, HeadE.FRUSTRATED, "Hmmm. Yes, yes I see. Very well then, you may still assist us in finding this mysterious essence mine.")
                                        player(HeadE.CONFUSED, "How would I do that?")
                                        npc(npc, HeadE.CALM_TALK, "Here, take this scrying orb. I have cast a standard cypher spell upon it, so that it will absorb mystical energies that it is exposed to.")
                                        npc(npc, HeadE.CALM_TALK, "Bring it with you and teleport to the rune essence location, and it will absorb the mechanics of the spell and allow us to reverse-engineer the magic behind it.")
                                        npc(npc, HeadE.CALM_TALK, "The important part is that you must teleport to the essence location from three entirely separate locations.")
                                        npc(npc, HeadE.CALM_TALK, "More than three may be helpful to us, but we need a minimum of three in order to triangulate the position of this essence mine.")
                                        npc(npc, HeadE.CONFUSED, "Is that all clear, stranger?") {
                                            p.miniquestManager.setStage(Miniquest.ENTER_THE_ABYSS, EnterTheAbyss.SCRYING_ORB)
                                            p.inventory.addItemDrop(5519, 1)
                                        }
                                        player(HeadE.CALM_TALK, "Yeah, I think so.")
                                        npc(npc, HeadE.CALM_TALK, "Good. If you encounter any difficulties speak to me again.")
                                    }
                                    op("No") { player(HeadE.CALM, "Sorry, no.") }
                                }
                            }
                        }
                    }

                    EnterTheAbyss.SCRYING_ORB -> {
                        op("What am I supposed to be doing again?") {
                            player(HeadE.CONFUSED, "Please excuse me, I have a very bad short term memory. What exactly am I supposed to be doing again?")
                            npc(npc, HeadE.FRUSTRATED, "I am slightly concerned about your capability for this mission, if you cannot even recall such a simple task...")
                            npc(npc, HeadE.CALM_TALK, "All I wish for you to do is to teleport to this 'rune essence' location from three different locations while carrying the scrying orb I gave you.")
                            npc(npc, HeadE.CALM_TALK, "It will collect the data as you teleport, and if you then bring it to me I will be able to use it to further my own investigations.")
                            player(HeadE.CONFUSED, "So you want me to teleport to the rune essence mine while carrying the scrying ball?")
                            npc(npc, HeadE.CALM_TALK, "That is correct.")
                            player(HeadE.CONFUSED, "And I need to teleport into the essence mine from three different locations?")
                            npc(npc, HeadE.CALM_TALK, "That is also correct.")
                            player(HeadE.SKEPTICAL_THINKING, "Okay... I think I understand that...")
                            npc(npc, HeadE.CONFUSED, "That is good to know. Is there something else you need clarifying?")
                            goto("startOptions")
                        }
                        op("What's in this for me, anyway?") {
                            player(HeadE.CONFUSED, "I just want to know one thing: What's in this for me?")
                            npc(npc, HeadE.LAUGH, "Well now, I can certainly understand that attitude.")
                            npc(npc, HeadE.CALM_TALK, "From what you tell me, the Saradominist wizards have given you access to the rune essence mine, but your method of reaching the temples used to bind this essence is random and counter-productive.")
                            npc(npc, HeadE.CALM_TALK, "I, on the other hand, have information on quickly accessing these temples, yet my methods of procuring essence are time consuming and useless as a means of mass production.")
                            npc(npc, HeadE.CALM_TALK, "I think you can see for yourself how this may benefit both of us: you allow me the knowledge of finding plentiful essence, and I will in return show you the secret of these temples.")
                            player(HeadE.CALM_TALK, "Yeah, okay, fair enough.")
                            npc(npc, HeadE.CONFUSED, "Was there anything else?")
                            goto("startOptions")
                        }
                        op("You're not going to use this for evil are you?") {
                            player(HeadE.CONFUSED, "If I help you with this... I'd just like to keep my conscience clear and know that you're not going to use whatever information I give to you in the pursuit of evil and badness and stuff.")
                            npc(
                                npc,
                                HeadE.SKEPTICAL_THINKING,
                                "Very well, if it makes you feel any better; I promise you that any knowledge you provide me with, absolutely will not be used in the service of anything more evil than what I was already planning on doing anyway. I hope that sets your conscience at rest."
                            )
                            player(HeadE.CONFUSED, "You know... In a weird way it actually does...")
                            npc(npc, HeadE.CALM_TALK, "Excellent. Was there anything else?")
                            goto("startOptions")
                        }
                    }
                }
                op("All hail Zamorak!") {
                    player(HeadE.CHUCKLE, "All hail Zamorak! He's the man! If he can't do it, maybe some other guy can!")
                    npc(npc, HeadE.SECRETIVE, "...Okay. I appreciate your enthusiasm for Lord Zamorak stranger, but what exactly was it that you wanted?")
                    goto("startOptions")
                }
                op("Nothing, thanks.") {
                    player(HeadE.CALM_TALK, "I didn't really want anything, thanks. I just like talking to random people I meet around the world.")
                    npc(npc, HeadE.SECRETIVE, "...I see. Well, in the future, do not waste my time, or you will feel the wrath of Zamorak upon you.")
                }
            }
        }
    }
}

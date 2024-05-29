package com.rs.game.content.quests.buyersandcellars.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.skills.thieving.thievesGuild.PickPocketDummy
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemOnNpc

@ServerStartupEvent
fun mapDarrenBuyersAndCellars() {
    onItemOnNpc(11273) { e ->
        if (e.item.id == 18648) {
            e.player.startConversation {
                npc(e.npc, SAD, "Have you retrieved the chalice?")
                player(HAPPY_TALKING, "I have!")
                npc(e.npc, HAPPY_TALKING, "Fantastic work! I knew I had chosen wisely when I recruited you. Now we can expand the guild and do some proper training around here.")
                player(SKEPTICAL_HEAD_SHAKE, "Your buyer is still interested, I hope?")
                npc(e.npc, CALM_TALK, "Yes, of course, why?")
                player(CALM_TALK, "Well, the chalice wasn't where you said it was, nor was the owner; I just wanted to make sure you had something right in all of this.")
                npc(e.npc, LAUGH, "Ha! I do appreciate a sense of humor in my members.")
                player(CALM_TALK, "It wasn't actually a joke, to be honest.")
                npc(e.npc, SKEPTICAL_HEAD_SHAKE, "To be honest? You don't want to be honest; you're a member of the illustrious Thieves' Guild! Now get out there and make me proud... and both of us rich!")
                exec {
                    e.player.fadeScreen {
                        e.player.inventory.deleteItem(18648, 1)
                        e.player.tele(Tile.of(3223, 3269, 0))
                        e.player.vars.saveVarBit(7792, 10)
                        e.player.vars.setVarBit(7793, 0)
                    }
                    e.player.questManager.completeQuest(Quest.BUYERS_AND_CELLARS)
                }
            }
        }
    }
}

object DarrenBuyersAndCellars {

    fun stage2(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, HAPPY_TALKING, "Greetings, my young recruit! You return!")
            player(CALM_TALK, "Can we get started? I'm ready.")
            npc(npc, HAPPY_TALKING, "Excellent! I shall let Robin know he should expect you, then. You know what to do?")
            options {
                op("Yes.") {
                    player(CALM_TALK, "Yes.")
                    npc(npc, HAPPY_TALKING, "Best of luck then.")
                    player(CALM_TALK, "Thanks.")
                    exec { p.setQuestStage(Quest.BUYERS_AND_CELLARS, 3) }
                }
                op("Remind me again?") {
                    player(CALM_TALK, "Remind me again?")
                    npc(npc, CALM, "Head over to Lumbridge Castle's courtyard; Robin should have found out the identity of the chalice's owner by then. At that point, you just need to get the key by any means necessary, open the vault and then come back here with the chalice.")
                    player(CALM_TALK, "Why can't you do this?")
                    npc(npc, CALM, "Oh, Robin and I are both too well known; anyone with valuables would be instantly on their guard. No, I'm afraid it will have to be you doing the dirty work this time. Don't worry, you'll get a chance to see me in action some other time!")
                }
            }
        }
    }

    fun preQuest(p: Player, npc: NPC) {
        p.startConversation {
            npc(npc, HAPPY_TALKING, "Ah, come in, come in! I was just about to get started.")
            label("options")
            options {
                op("Don't let me stop you.") {
                    player(CONFUSED, "Don't let me stop you.")
                    npc(npc, HAPPY_TALKING, "Ladies and gentlemen of Lumbridge!")
                    npc(npc, HAPPY_TALKING, "Tonight, I stand before you to offer you the ultimate in opportunities!")
                    npc(npc, HAPPY_TALKING, "I offer you the chance to make your mark on a society rife with imbalance and folly.")
                    npc(npc, HAPPY_TALKING, "I offer you the chance to redistribute the wealth of our very civilization!")
                    npc(npc, HAPPY_TALKING, "I offer you the freedom to live your life without the need to worry whether your rent will be paid this month")
                    npc(npc, HAPPY_TALKING, "I offer you the skills to pay your way through the costs everyone must face.")
                    npc(npc, HAPPY_TALKING, "I offer you vengeance against those who take and take yet give nothing in return.")
                    npc(npc, HAPPY_TALKING, "I offer you justice at its most fundamental level!")
                    npc(npc, HAPPY_TALKING, "Some will say that I am a scofflaw, a thief, a brigand... These people are correct!")
                    npc(npc, HAPPY_TALKING, "But if I scoff at the law, it is because the law as we know it is a tool that 'The Man' is using purely to keep us in our place.")
                    npc(npc, HAPPY_TALKING, "If I thieve, it is from those who have more than they deserve and more than they need.")
                    npc(npc, HAPPY_TALKING, "And if I am a brigand, it is only by the standards of those whom I, er, brig.")
                    npc(npc, HAPPY_TALKING, "If you join me I can offer you every opportunity for reward and for fame, or at least infamy.")
                    npc(npc, HAPPY_TALKING, "Ladies and gentlemen, welcome to your destiny.")
                    npc(npc, HAPPY_TALKING, "Welcome to the preliminary course, you fine members-in-waiting of the underworld gentry!")
                    npc(npc, HAPPY_TALKING, "Here you will discover the techniques, the tricks, the training and the trials that mark your passage into this hidden elite.")
                    npc(npc, HAPPY_TALKING, "Here you will become the best of the best, joining the ranks of the steely-eyed exploiters of this world's bloated social parasites in a quest for community justice and personal enrichment!")
                    npc(npc, HAPPY_TALKING, "You will - Yes, we have a question?")
                    player(SKEPTICAL_THINKING, "Is this it? The 'world-renowned' guild is a cellar with two blokes and a straw dummy lurking in it?")
                    npc(npc, CONFUSED, "Well...")
                    player(CONFUSED, "How long has this guild been in operation?")
                    npc(npc, CALM, "...")
                    player(CONFUSED, "Yes?")
                    npc(npc, FRUSTRATED, "Two weeks.")
                    goto("options")
                }
                op("What are you doing down here?") {
                    player(SKEPTICAL_HEAD_SHAKE, "What are you doing down here?")
                    npc(npc,HAPPY_TALKING, "Why, recruiting! Recruiting agents for a glorious destiny and fantastical missions of derring-do. White suits, classy cocktails, fast carts.. a wealth of rewards await my guild's members!")
                    npc(npc, SHAKING_HEAD, "If only I could have convinced Ozan to sign on with us. SSuch a pity.. A master thief like him would have been perfect for my plans.")
                    npc(npc, HAPPY_TALKING, "Anyway, would you like to stay for the explanation?")
                    label("explanationOptions")
                    options {
                        op("Do tell.") {
                            player(CONFUSED, "Don't let me stop you.")
                            npc(npc, HAPPY_TALKING, "Ladies and gentlemen of Lumbridge!")
                            npc(npc, HAPPY_TALKING, "Tonight, I stand before you to offer you the ultimate in opportunities!")
                            npc(npc, HAPPY_TALKING, "I offer you the chance to make your mark on a society rife with imbalance and folly.")
                            npc(npc, HAPPY_TALKING, "I offer you the chance to redistribute the wealth of our very civilization!")
                            npc(npc, HAPPY_TALKING, "I offer you the freedom to live your life without the need to worry whether your rent will be paid this month")
                            npc(npc, HAPPY_TALKING, "I offer you the skills to pay your way through the costs everyone must face.")
                            npc(npc, HAPPY_TALKING, "I offer you vengeance against those who take and take yet give nothing in return.")
                            npc(npc, HAPPY_TALKING, "I offer you justice at its most fundamental level!")
                            npc(npc, HAPPY_TALKING, "Some will say that I am a scofflaw, a thief, a brigand... These people are correct!")
                            npc(npc, HAPPY_TALKING, "But if I scoff at the law, it is because the law as we know it is a tool that 'The Man' is using purely to keep us in our place.")
                            npc(npc, HAPPY_TALKING, "If I thieve, it is from those who have more than they deserve and more than they need.")
                            npc(npc, HAPPY_TALKING, "And if I am a brigand, it is only by the standards of those whom I, er, brig.")
                            npc(npc, HAPPY_TALKING, "If you join me I can offer you every opportunity for reward and for fame, or at least infamy.")
                            npc(npc, HAPPY_TALKING, "Ladies and gentlemen, welcome to your destiny.")
                            npc(npc, HAPPY_TALKING, "Welcome to the preliminary course, you fine members-in-waiting of the underworld gentry!")
                            npc(npc, HAPPY_TALKING, "Here you will discover the techniques, the tricks, the training and the trials that mark your passage into this hidden elite.")
                            npc(npc, HAPPY_TALKING, "Here you will become the best of the best, joining the ranks of the steely-eyed exploiters of this world's bloated social parasites in a quest for community justice and personal enrichment!")
                            npc(npc, HAPPY_TALKING, "You will - Yes, we have a question?")
                            player(SKEPTICAL_THINKING, "Is this it? The 'world-renowned' guild is a cellar with two blokes and a straw dummy lurking in it?")
                            npc(npc, CONFUSED, "Well...")
                            player(CONFUSED, "How long has this guild been in operation?")
                            npc(npc, CALM, "...")
                            player(CONFUSED, "Yes?")
                            npc(npc, FRUSTRATED, "Two weeks.")
                            goto("explanationOptions")
                        }
                        op("What is it you need done?") {
                            player(CALM_TALK, "And what is it you need done?")
                            npc(npc, CALM_TALK, "Here's what we need: Money!")
                            player(AMAZED_MILD, "Really? You amaze me.")
                            npc(npc, CALM, "I know it's hardly the most high-flying goal, but we really need to start somewhere. To be precise, we need to start by expanding this cellar into a headquarters befitting a major player on the global crime stage.")
                            player(SKEPTICAL, "I take it you have some sort of plan for doing this?")
                            npc(npc, HAPPY_TALKING, "Of course I do. In Lumbridge Castle's bank there is a golden chalice of particular workmanship and value. I have found a willing buyer for it, and now it merely remains to collect the item in question.")
                            player(SKEPTICAL, "From the bank's vault..")
                            npc(npc, HAPPY_TALKING, "Correct! And since the vault is not easily breached, we shall need the key from it's owner.")
                            player(CALM_TALK, "It's owner being?")
                            npc(npc, HAPPY_TALKING, "I shall send my right hand man, Robin, to determine that as soon as I may. He shall be around to brief you in the castle ground.")
                            npc(npc, HAPPY_TALKING, "Then you must merely acquire the key by stealth or by force, open the vault, return the chalice and...")
                            player(SKEPTICAL_THINKING, "You seem to be assuming a certain amount here.")
                            npc(npc, HAPPY_TALKING, "Oh, but of course you'll help! I can offer you the best of training and the greatest of rewards for your assistance.. In fact, Let me have a look at your technique and see what we can do with you.")
                            exec {
                                questStart(Quest.BUYERS_AND_CELLARS)
                                player(CALM_TALK, "Oh very well!") { p.setQuestStage(Quest.BUYERS_AND_CELLARS, 1) }
                                npc(npc, HAPPY_TALKING, "Splendid! Let's get you set up then.")
                                npc(npc, HAPPY_TALKING, "This is a Mark 1 training dummy.")
                                npc(npc, CALM_TALK, "It's designed for maximum pocket size and minimum observation skills, which, seeing as it's made of wood, straw, and canvas, was not hard to achieve.")
                                npc(npc, CALM_TALK, "It will suffice for early training and for testing, but if you have any talent at all it will not be of use to you for long.")
                                npc(npc, CALM, "Right, I want you to pick the pocket of that dummy as sneakily and delicately as you possibly can.")
                                exec {
                                    p.walkToAndExecute(Tile.of(4664, 5903, 0)) {
                                        p.actionManager.setAction(PickPocketDummy(GameObject(52316, 1, 4665, 5903, 0)))
                                        p.lock()
                                    }
                                }
                            }
                        }
                        op("No, thank you.") {
                            player(SHAKING_HEAD, "No, thank you.")
                        }
                    }
                }
                op("Sorry, I was just leaving.") {
                    player(CALM, "Sorry, I was just leaving.")
                }
            }
        }
    }
}

package com.rs.game.content.quests.buyersandcellars.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile

object RobinCastleKt {
    fun stage8(p: Player, npc: NPC) {
        p.startConversation {
            player(HAPPY_TALKING, "I stole Father Urhney's chalice!")
            if (npc.tile == Tile.of(Tile(4664, 5904, 0))) npc(npc, CALM_TALK, "You should hand it over to Darren, then.")
            else npc(npc, CALM_TALK, "You might want to deliver it to Darren, back at the guild, then.")
        }
    }

    fun stage7(p: Player, npc: NPC) {
        p.startConversation {
            player(HAPPY_TALKING, "I have Father Urhney's key!")
            npc(npc, CALM_TALK, "Then what are you doing here? Nab the chalice from the old man and get back to the guild.")
        }
    }

    fun stage5(p: Player, npc: NPC) {
        p.startConversation {
            player(SHAKING_HEAD, "Father Urhney over in the swamp has the chalice in his hut, but I can't get the key off him.")
            npc(npc, CALM_TALK, "You might have to engineer a crisis, then. Don't go setting fire to his house, though! A fire outside his window should do the trick.")
            player(AMAZED_MILD, "Is that not highly irresponsible?")
            npc(npc, CALM_TALK, "Normally I'd say yes, but that swamp is so marshy there's little danger of burning his house down and rendering him homeless. There should be some nice damp wood in that swamp.")
            exec { p.setQuestStage(Quest.BUYERS_AND_CELLARS, 6) }
        }
    }

    fun stage3(p: Player, npc: NPC) {
        if (npc.tile == Tile.of(4664, 5904, 0)) {
            someExtraHelp(p, npc)
        } else p.startConversation {
            npc(npc, CALM_TALK, "The purple owl croaks at dawn...")
            player(CONFUSED, "Um, does it?")
            npc(npc, CALM_TALK, "Oh, never mind. I've some information for you.")
            options {
                op("Go ahead.") {
                    player(CALM_TALK, "Go ahead.")
                    npc(npc, CALM_TALK, "The chalice is no longer being held by the bank. Seems that the owner withdrew it a couple of days ago and wandered off in the direction of Lumbridge Swamp...a wild-haired old man with a bad temper.")
                    player(CONFUSED, "Who'd want to live in a swamp?")
                    npc(npc, CALM_TALK, "Someone who wants to be left alone, I imagine.")
                    player(CALM_TALK, "Looks like that's my next stop, anyway.")
                    npc(npc, CALM_TALK, "No violence, if you please. We're thieves, not muggers, and priests tend to be well in with the gods. Be subtle...if an adventurer can be subtle. See if you can pick his pocket for the key. Good luck.")
                    exec { p.setQuestStage(Quest.BUYERS_AND_CELLARS, 4) }
                }
                op("Not right now.") { player(SHAKING_HEAD, "Not right now.") }
            }
        }
    }

    fun someExtraHelp(p: Player, npc: NPC) {
        p.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, CALM_TALK, "The Guildmaster wanted me to be on hand in case you needed some more hints on picking pockets. Now, what can I do for you?")
            options {
                op("I'm always willing to learn.") {
                    player(CALM_TALK, "I'm always willing to learn.")
                    npc(npc, CALM_TALK, "When you're on the prowl for pickpocketing targets, it should be fairly obvious who's not paying enough attention to the world around them.")
                    npc(npc, CALM_TALK, "Just saunter up to them all casual-like, then dip your hand into their wallets as gently and as neatly as you can.")
                    npc(npc, CALM_TALK, "If you succeed, you'll get some of the contents of their pockets; it not, they'll likely punch you in the face, so be warned.")
                    npc(npc, CALM_TALK, "It stings, and you'll need a moment to gather your wits.")
                    player(CALM_TALK, "Thanks, Robin.")
                    npc(npc, CALM_TALK, "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve.")
                }
                op("I've got it, thanks.") {
                    player(CALM_TALK, "I’ve got it, thanks.")
                    npc(npc, CALM_TALK, "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve.")
                }
                op("Any advice for me?") {
                    player(CALM_TALK, "Any advice for me? The Guildmaster says you'll be shadowing me on this operation.")
                    npc(npc, CALM_TALK, "Yes, I'm heading out to the castle shortly to pick up any information that might help you.")
                    player(CALM_TALK, "Anything I should know?")
                    npc(npc, CALM_TALK, "This caper should be simple enough, since your mark won't be on his guard.")
                    npc(npc, CALM_TALK, "I'll be able to tell you more once I've had a chance to look around, but it should be a matter of finding the chalice's owner, stealing the key, and taking the chalice out of the bank.")
                    player(CALM_TALK, "See you there, then.")
                }
                op("Bye for now.") { player(CALM_TALK, "Bye for now.") }
            }
        }
    }


    fun preQuest(p: Player, npc: NPC) {
        p.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, CALM_TALK, "Greetings. I'm Robin, the Guildmaster's assistant. Now, what can I do for you?")
            label("options")
            options {
                op("That's an appropriate name.") {
                    player(CALM_TALK, "That's an appropriate name.")
                    npc(npc, CALM_TALK, "Yes, I've never heard that one before.")
                    npc(npc, CALM_TALK, "Still, it's not so bad... If I hadn't been mocked for my name as a lad, I might never have decided in a fit of ironic pique to learn how to rob from the rich and give to the poor.")
                    player(CALM_TALK, "Are you the poor in question?")
                    npc(npc, CALM_TALK, "Well, I was.")
                    npc(npc, CALM_TALK, "Having given myself the goods stolen from several rich people, I'm now of limited but comfortable means.")
                    npc(npc, CALM_TALK, "Now, what can I do for you?")
                    goto("options")
                }
                op("How long have you known the Guildmaster?") {
                    player(CALM_TALK, "How long have you known the Guildmaster?")
                    npc(npc, CALM_TALK, "Oh, some time now. We started in business together when he was a con artist, talking people into handing over their hard-earned valuables with lies and vague promises of reward.")
                    npc(npc, SKEPTICAL_HEAD_SHAKE, "Obviously, that's all behind us now. Now, what can I do for you?")
                    goto("options")
                }
                op("Bye for now.") { player(CALM_TALK, "Bye for now.") }
            }
        }
    }
}
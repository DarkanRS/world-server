package com.rs.game.content.minigames.warriors_guild

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

const val SHANOMI = 4290

@ServerStartupEvent
fun mapWarriorsGuildDialogue() {
    onNpcClick(SHANOMI) { (player, npc) ->
        player.startConversation {
            npc(SHANOMI, HeadE.HAPPY_TALKING, "Greetings ${player.displayName} Welcome you are in the test of combat.")
            label("startOps")
            options {
                op("What do I do here?") {
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "A spare suit of plate armour need you will. Full helm, plate leggings and platebody yes? Placing it in the centre of the magical machines you will be doing. KA-POOF! The armour, it attacks most furiously as if alive! Kill it you must, yes.")
                    player(HeadE.CONFUSED, "So I use a full set of plate armour on the centre plate of the machines and it will animate it? Then I have to kill my own armour... how bizarre!")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "Yes. It is as you are saying. For this earn tokens you will. Also gain experience in combat you will. Trained long and hard here have I.")
                    player(HeadE.CHUCKLE, "Your not from around here are you?")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "It is as you say.")
                    player(HeadE.CONFUSED, "So will I lose my armour?")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "Lose armour you will if damaged too much it becomes. Rare this is, but still possible. If kill you the armour does, also lose armour you will.")
                    player(HeadE.CONFUSED, "So, occasionally I might lose a bit because it's being bashed about and I'll obviously lose it if I die... that it?")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "It is as you say.")
                    goto("startOps")
                }
                op("Where do the machines come from?") {
                    player(HeadE.CONFUSED, "Where do the machines come from?")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "Make them I did, with magics.")
                    player(HeadE.CONFUSED, "Magic, in the Warrior's Guild?")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "A skilled warrior also am I. Harrallak mistakes does not make. Potential in my invention he sees and opportunity grasps.")
                    player(HeadE.CHEERFUL, "I see, so you made the magical machines and Harrallak saw how they could be used in the guild to train warrior's combat... interesting. Harrallak certainly is an intelligent guy.")
                    goto("startOps")
                }
                op("Bye!") {
                    player(HeadE.CHEERFUL, "Bye!")
                    npc(SHANOMI, HeadE.HAPPY_TALKING, "Health be with you travelling.")
                }
            }
        }
    }
}
package com.rs.game.content.quests.gunnars_ground.dialogues

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.gunnars_ground.getPoemStage
import com.rs.game.content.quests.gunnars_ground.setPoemStage
import com.rs.game.content.quests.gunnars_ground.utils.STAGE_FINISHED_WRITING_POEM
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DororanPoemD (player: Player, npc: NPC) {

    private val poemOptions = listOf(
        listOf("Cucumber", "Barbarian", "Deviate", "Meander", "Astray", "Beret", "Dismay"),
        listOf("Saradomin", "Roam", "Veer", "Traipse", "Jaunt", "Ham", "Grass", "Fish"),
        listOf("Monkey", "Hay", "May", "Tray", "Fey"),
        listOf("Stray"),

        listOf("Stockade", "Upset", "Brunette", "Crisis", "Peril", "Regret"),
        listOf("Longsword", "Debt", "Sweat", "Wet", "Length", "Set", "Met"),
        listOf("Dungeoneering", "Grass", "Storm", "Hat", "Axe", "Risk", "Menace", "Hazard"),
        listOf("Threat"),

        listOf("Threw the ball", "Picked a rose", "Made a raft", "Picked a fight", "Tamed a shrew", "Shut the door", "Marched to battle"),
        listOf("Ate a tasty pie", "Schemed intently", "Learned to soar", "Cleaned the floor", "Heard a song", "Went for a walk"),
        listOf("Commenced fisticuffs", "Started a war", "Loosed a mighty roar", "Settled the score", "Counted to four"),
        listOf("Swept to war")
    )

    private val firstWordFirstOptions = poemOptions[0].shuffled()
    private val firstWordSecondOptions = (poemOptions[1] + poemOptions[0]).shuffled()
    private val firstWordThirdOptions = (poemOptions[2] + poemOptions[1]).shuffled()
    private val firstWordFourthOptions = (poemOptions[3] + poemOptions[2]).shuffled()

    private val secondWordFirstOptions = poemOptions[4].shuffled()
    private val secondWordSecondOptions = (poemOptions[5] + poemOptions[4]).shuffled()
    private val secondWordThirdOptions = (poemOptions[6] + poemOptions[5]).shuffled()
    private val secondWordFourthOptions = (poemOptions[7] + poemOptions[6]).shuffled()

    private val thirdWordFirstOptions = poemOptions[8].shuffled()
    private val thirdWordSecondOptions = (poemOptions[9] + poemOptions[8]).shuffled()
    private val thirdWordThirdOptions = (poemOptions[10] + poemOptions[9]).shuffled()
    private val thirdWordFourthOptions = (poemOptions[11] + poemOptions[10]).shuffled()

    init {
        player.startConversation {

            when(getPoemStage(player)) {
                1 -> {
                    npc(npc, TALKING_ALOT, "'Even the bloodiest rose must settle.' Mixed metaphor. What settles? Detritus. That's hardly flattering.")
                    npc(npc, TALKING_ALOT, "'Even the rolliest boulder...'")
                    player(CHEERFUL, "How is the poem going?")
                    npc(npc, SAD_EXTREME, "I'm stuck! I'm a worthless wordsmith! My work is pointless! My life is pointless!")
                    options {
                        op("I'm sure that's not true.") {
                            player(CALM_TALK, "I'm sure that's not true.")
                            goto("stuckOnAWord")
                        }
                        op("What's the problem?") {
                            player(CALM_TALK, "What's the problem?")
                            goto("stuckOnAWord")
                        }
                    }
                    label("stuckOnAWord")
                    npc(npc, SAD, "I'm stuck on a word. By the colossus of King Alvis! I can't find the words!")
                    player(CHEERFUL, "Maybe I can help. What sort of word?")
                    npc(npc, SKEPTICAL_HEAD_SHAKE, "I don't know! I'm not some kind of word scientist. I just feel it out as I go.")
                    npc(npc, SKEPTICAL_THINKING, "Maybe you could suggest some words to get me started. Then I can tell you more.")
                    player(CHEERFUL, "Alright, how about, uh...")
                    options {
                        for (option in firstWordFirstOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("firstWordIncorrect1")
                            }
                        }
                    }
                    label("firstWordIncorrect1")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to be one syllable long.")
                    options {
                        for (option in firstWordSecondOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("firstWordIncorrect2")
                            }
                        }
                    }
                    label("firstWordIncorrect2")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to rhyme with the word 'day'.")
                    options {
                        for (option in firstWordThirdOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("firstWordIncorrect3")
                            }
                        }
                    }
                    label("firstWordIncorrect3")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to mean something like 'wandering aimlessly'.")
                    options {
                        for (option in firstWordFourthOptions) {
                            op("${option}.") {
                                player(CHEERFUL, "${option}.")
                                if (option != poemOptions[3][0]) {
                                    goto("firstWordIncorrect3")
                                } else {
                                    goto("firstWordCorrect")
                                }
                            }
                        }
                    }
                    label("firstWordCorrect")
                    npc(npc, HAPPY_TALKING, "'And from his righteous purpose never <col=0000FF>stray</col>.'")
                    npc(npc, HAPPY_TALKING, "That fits! It fits perfectly. Right meaning, right length, right rhyme. Well done!") {
                        player.soundEffect(1723, false)
                        setPoemStage(player, 2)
                    }
                    exec { DororanPoemD(player, npc) }
                }

                2 -> {
                    npc(npc, HAPPY_TALKING, "The poem still isn't finished, though. I have another missing word. Give me another one; anything, to get me started.")
                    options {
                        for (option in secondWordFirstOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("secondWordIncorrect1")
                            }
                        }
                    }
                    label("secondWordIncorrect1")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to be one syllable long.")
                    options {
                        for (option in secondWordSecondOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("secondWordIncorrect2")
                            }
                        }
                    }
                    label("secondWordIncorrect2")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to mean something like 'danger'.")
                    options {
                        for (option in secondWordThirdOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("secondWordIncorrect3")
                            }
                        }
                    }
                    label("secondWordIncorrect3")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to rhyme with the word 'yet'.")
                    options {
                        for (option in secondWordFourthOptions) {
                            op("${option}.") {
                                player(CHEERFUL, "${option}.")
                                if (option != poemOptions[7][0]) {
                                    goto("secondWordIncorrect3")
                                } else {
                                    goto("secondWordCorrect")
                                }
                            }
                        }
                    }
                    label("secondWordCorrect")
                    npc(npc, HAPPY_TALKING, "'But long is gone the author of that <col=0000FF>threat</col>.'")
                    npc(npc, AMAZED, "Perfect! Yes!") {
                        player.soundEffect(1723, false)
                        setPoemStage(player, 3)
                    }
                    exec { DororanPoemD(player, npc) }
                }

                3 -> {
                    npc(npc, AMAZED_MILD, "It's coming together. We're nearly done! One more to go!")
                    npc(npc, HAPPY_TALKING, "This one is tricky, though. It's a phrase I need. Someone did something.")
                    options {
                        for (option in thirdWordFirstOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("thirdWordIncorrect1")
                            }
                        }
                    }
                    label("thirdWordIncorrect1")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to rhyme with the word 'lore'.")
                    options {
                        for (option in thirdWordSecondOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("thirdWordIncorrect2")
                            }
                        }
                    }
                    label("thirdWordIncorrect2")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to imply some aggressive action, like 'started a fight'.")
                    options {
                        for (option in thirdWordThirdOptions) {
                            op("$option.") {
                                player(CHEERFUL, "$option.")
                                goto("thirdWordIncorrect3")
                            }
                        }
                    }
                    label("thirdWordIncorrect3")
                    npc(npc, SHAKING_HEAD, "That doesn't really fit. It needs to be three syllables long.")
                    options {
                        for (option in thirdWordFourthOptions) {
                            op("${option}.") {
                                player(CHEERFUL, "${option}.")
                                if (option != poemOptions[11][0]) {
                                    goto("thirdWordIncorrect3")
                                } else {
                                    goto("thirdWordCorrect")
                                }
                            }
                        }
                    }
                    label("thirdWordCorrect")
                    npc(npc, HAPPY_TALKING, "'Who then, in face of madness, <col=0000FF>swept to war</col>.'")
                    exec {
                        player.soundEffect(1723, false)
                        setPoemStage(player, 4)
                        player.questManager.setStage(Quest.GUNNARS_GROUND, STAGE_FINISHED_WRITING_POEM)
                        DororanD(player, npc)
                    }
                }
            }
        }
    }
}

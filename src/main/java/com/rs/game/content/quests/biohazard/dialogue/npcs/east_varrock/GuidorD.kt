package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item

class GuidorD (player: Player, npc: NPC) {
    init {

        val hasAllItems = player.inventory.containsItems(Item(LIQUID_HONEY), Item(ETHENEA), Item(SULPHURIC_BROLINE), Item(PLAGUE_SAMPLE), Item(TOUCH_PAPER))
        val gaveItemsToGuidor = player.questManager.getAttribs(Quest.BIOHAZARD).getB(GAVE_ITEMS_TO_GUIDOR)

        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_RECEIVED_VIALS..STAGE_RECEIVED_TOUCH_PAPER -> {
                    if (!gaveItemsToGuidor) {
                        player(CALM_TALK, "Hello, you must be Guidor. I understand that you are unwell.")
                        npc(npc, FRUSTRATED, "Is my wife asking priests to visit me now? I'm a man of science for god's sake.")
                        npc(npc, CALM_TALK, "Ever since she heard rumors of a plague carrier travelling from Ardougne she's kept me under house arrest.")
                        npc(npc, SAD, "Of course she means well, and I am quite frail now... So what brings you here?")
                        options {
                            op("I've come to ask your assistance in stopping a plague.") {
                                player(CALM_TALK, "Well it's funny you should ask actually... I've come to ask your assistance in stopping a plague that could kill thousands.")
                                npc(npc, WORRIED, "So you're the plague carrier!")
                                options {
                                    op("No! Well, yes...") {
                                        player(CALM_TALK, "No! Well, yes... but not exactly. It's contained in a sealed unit from Elena.")
                                        goto("elenaEh")
                                    }
                                    op("I've been sent by your old pupil Elena.") {
                                        player(CALM_TALK, "I've been sent by your old pupil Elena, she's trying to halt the virus.")
                                        label("elenaEh")
                                        npc(npc, SKEPTICAL_THINKING, "Elena eh?")
                                        player(CALM_TALK, "Yes, she wants you to analyse it. You might be the only one who can help.")
                                        npc(npc, CALM_TALK, "Right then, sounds like we'd better get to work!")
                                        if (!player.inventory.containsOneItem(PLAGUE_SAMPLE)) {
                                            npc(npc, CALM_TALK, "Seems like you don't actually HAVE the plague sample. It's a long way to come empty-handed... and quite a long way back to.")
                                        } else {
                                            player(CALM_TALK, "I have the plague sample.")
                                            npc(npc, CALM_TALK, "Now I'll be needing some liquid honey, some sulphuric broline, and then...")
                                            player(CALM_TALK, "... some ethenea?")
                                            npc(npc, CALM_TALK, "Indeed!")
                                            if (!hasAllItems) {
                                                npc(npc, CALM_TALK, "Look, I need all three reagents to test the plague sample. Come back when you've got them.")
                                            } else {
                                                npc(npc, CALM_TALK, "Now I'll just apply these to the sample and... I don't get it... the touch paper has remained the same.") {
                                                    player.sendMessage("You give him the vials and the touch paper.")
                                                    BiohazardUtils(player).resetAllErrandBoyItems()
                                                    player.inventory.removeItems(Item(LIQUID_HONEY), Item(ETHENEA), Item(SULPHURIC_BROLINE), Item(PLAGUE_SAMPLE), Item(TOUCH_PAPER))
                                                    player.questManager.getAttribs(Quest.BIOHAZARD).setB(GAVE_ITEMS_TO_GUIDOR, true)
                                                }
                                                options {
                                                    op("That's why Elena wanted you to do it.") {
                                                        player(CALM_TALK, "That's why Elena wanted you to do it, because she wasn't sure what was happening.")
                                                        npc(npc, CALM_TALK, "Well that's just it, nothing has happened.")
                                                        label("whatThisSampleIs")
                                                        npc(npc, CALM_TALK, "I don't know what this sample is, but it certainly isn't toxic.")
                                                        player(CONFUSED, "So what about the plague?")
                                                        npc(npc, FRUSTRATED, "Don't you understand? There is no Plague!")
                                                        npc(npc, CALM_TALK, "I'm very sorry, I can see that you've worked very hard for this... ... but it seems that someone has been lying to you.")
                                                        npc(npc, SHAKING_HEAD, "The only question is... ... why?") { player.questManager.setStage(Quest.BIOHAZARD, STAGE_RETURN_TO_ELENA) }
                                                    }
                                                    op("So what does that mean exactly?") {
                                                        player(CALM_TALK, "So what does that mean exactly?")
                                                        goto("whatThisSampleIs")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            op("I was just going to bless your room and I've done that now.") {
                                player(CALM_TALK, "Oh, nothing, I was just going to bless your room and I've done that now. Goodbye.")
                            }
                        }
                    } else {
                        npc(npc, CALM_TALK, "Now I'll just apply these to the sample and... I don't get it... the touch paper has remained the same.")
                        options {
                            op("That's why Elena wanted you to do it.") {
                                player(CALM_TALK, "That's why Elena wanted you to do it, because she wasn't sure what was happening.")
                                npc(npc, CALM_TALK, "Well that's just it, nothing has happened.")
                                label("whatThisSampleIs")
                                npc(npc, CALM_TALK, "I don't know what this sample is, but it certainly isn't toxic.")
                                player(CONFUSED, "So what about the plague?")
                                npc(npc, FRUSTRATED, "Don't you understand? There is no Plague!")
                                npc(npc, CALM_TALK, "I'm very sorry, I can see that you've worked very hard for this... ... but it seems that someone has been lying to you.")
                                npc(npc, SHAKING_HEAD, "The only question is... ... why?") { player.questManager.setStage(Quest.BIOHAZARD, STAGE_RETURN_TO_ELENA) }
                            }
                            op("So what does that mean exactly?") {
                                player(CALM_TALK, "So what does that mean exactly?")
                                goto("whatThisSampleIs")
                            }
                        }
                    }
                }

                in STAGE_RETURN_TO_ELENA..STAGE_COMPLETE -> {
                    player(CALM_TALK, "Hello again Guidor.")
                    npc(npc, SKEPTICAL, "Well, hello traveller. I still can't understand why they would lie about the plague.")
                    player(CALM_TALK, "It's strange, anyway how are you doing?")
                    npc(npc, CALM_TALK, "I'm hanging in there.")
                    player(CALM_TALK, "Good for you.")
                }

            }
        }
    }
}

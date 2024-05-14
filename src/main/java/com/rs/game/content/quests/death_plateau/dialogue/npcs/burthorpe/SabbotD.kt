package com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe

import com.rs.engine.dialogue.DialogueBuilder
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.death_plateau.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class SabbotD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (val stage = player.questManager.getStage(Quest.DEATH_PLATEAU)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello!")
                    npc(npc, FRUSTRATED, "Hello! What you after then? Come to rob us eh, fancy your chances do ya? Well you just come on then! I'll never give up me home to the likes of you!")
                    player(CONFUSED, "I'm not here to rob you!")
                    npc(npc, FRUSTRATED, "Oh? Well, maybe you are and maybe you aren't...but you're not with the guard so you've no business here.")
                }

                in STAGE_SPEAK_TO_SABBOT..STAGE_RETURN_TO_FREDA -> {
                    player(CALM_TALK, "Hello there.")
                    if (stage == STAGE_SPEAK_TO_SABBOT) npc(npc, CALM_TALK, "Hello. What are you after?") else npc(npc, CALM_TALK, "It's you again, eh? What ye after now?")
                    player(CALM_TALK, "I wanted to ask you something.")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_RECEIVED_SURVEY -> {
                    player(SKEPTICAL_THINKING, "Can you help me with this survey?")
                    npc(npc, CALM_TALK, "Oh? What's this? Looks like the wife's done a nice, simple version for you to follow.")
                    player(CALM_TALK, "Yes, but I'd still like a little help, if that's alright.")
                    npc(npc, FRUSTRATED, "You humans...give it here! Uh...huh...I see... That's it. All ya need to do is dig over there.")
                    npc(npc, CALM_TALK, "Looks like there is a weak point behind the rock that'll let you into the caverns.")
                    exec { DeathPlateauUtils(player).handleSurvey(false) }
                }

                STAGE_READ_SURVEY -> {
                    player(CALM_TALK, "Hey Sabbot, can you give me a hand clearing the tunnel?")
                    npc(npc, SKEPTICAL_THINKING, "You want to get into that tunnel, you can make yer own hole. Just try not to get any rocks on me gear.")
                    player(CONFUSED, "But isn't most of your gear made of rocks?")
                    npc(npc, CALM_TALK, "Uh...just try not to make a mess!")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_MINED_TUNNEL -> {
                    player(CALM_TALK, "Hey Sabbot, can you give me a hand exploring this tunnel?")
                    npc(npc, SHAKING_HEAD, "Nope, I'm too busy.")
                    player(SKEPTICAL_THINKING, "Doing what?")
                    npc(npc, CALM_TALK, "Well, now I've got this nice, new tunnel to work with, I've got to get me tools in order.")
                    npc(npc, CALM_TALK, "That and I need to write to me wife, to tell her we can move in there and make a start on our little shop... Besides, that cave's nowt special.")
                    npc(npc, CALM_TALK, "You'll need to clamber, jump and swing around until someone puts in stairs and the like. Potholing and caving is always pretty demanding work.")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_FOUND_TROLL -> {
                    player(CALM_TALK, "Hey Sabbot, can you give me a hand moving this troll?")
                    npc(npc, CALM_TALK, "Oh yes, I'll go and beat your troll for ya!")
                    player(AMAZED_MILD, "You will?")
                    npc(npc, FRUSTRATED, "No! It's your troll, so it's your problem!")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_ANGERED_TROLL -> {
                    player(CALM_TALK, "Hey Sabbot, can you give me a hand fighting this troll?")
                    npc(npc, CALM_TALK, "Oh yes, I'll go and beat ya troll for ya! Nothing I like better.")
                    player(AMAZED_MILD, "You will?")
                    npc(npc, FRUSTRATED, "No! It's your troll, so it's your problem!")
                    exec { optionsDialogue(player, npc, this) }
                }

                STAGE_KILLED_THE_MAP -> {
                    player(HAPPY_TALKING, "Sabbot, I cleared the troll from the secret path I found!")
                    npc(npc, CALM_TALK, "That's grand news!")
                    player(HAPPY_TALKING, "Yes, it is!")
                    npc(npc, SKEPTICAL_THINKING, "Erm..?")
                    player(HAPPY_TALKING, "Yes...")
                    npc(npc, CALM_TALK, "Do you not think that Denulth's the person you're best off telling about that?")
                    player(SKEPTICAL_THINKING, "Uh, you're right.")
                    npc(npc, CALM_TALK, "I bet the troll hit you round the bonce a few too many times, didn't 'e? Go on, get off with ya.")
                    player(CALM_TALK, "Before I go...")
                    exec { optionsDialogue(player, npc, this) }
                }

            }
        }
    }

    private fun optionsDialogue(player: Player, npc: NPC, dialogue: DialogueBuilder) {
        val stage = player.questManager.getStage(Quest.DEATH_PLATEAU)
        dialogue.label("initialOps")
        dialogue.options {
            when (stage) {
                STAGE_SPEAK_TO_SABBOT -> {
                    op("I've been sent to look for a route to Death Plateau. Can you help?") {
                        npc(npc, CALM_TALK, "Ambush the trolls, eh? That's an idea. First good one I've heard the Guard have in a long while. But it's not my problem, so I suggest you take your fancy talk elsewhere!")
                        player(CALM_TALK, "Well I'm not moving until you help me!")
                        npc(npc, FRUSTRATED, "Well try not to get in me way, because I'm not going to help ya!")
                        player(FRUSTRATED, "Fine!")
                        npc(npc, FRUSTRATED, "Fine!")
                        player(VERY_FRUSTRATED, "Fine!")
                        npc(npc, VERY_FRUSTRATED, "Fine!")
                        player(ANGRY, "Fine!")
                        npc(npc, SKEPTICAL_HEAD_SHAKE, "You're really not goin' to move, are ya?")
                        player(SHAKING_HEAD, "No!")
                        npc(npc, CALM_TALK, "All right. I'll help. If nothin' else I don't want ya standing there watching us while I sleep!")
                        player(SKEPTICAL_THINKING, "So you know a way up to Death Plateau?")
                        npc(npc, CALM_TALK, "Well if ya want up there you've got two choices.")
                        npc(npc, CALM_TALK, "The first one is you can try the airy-fairy, lacklustre, shoddy way, clambering over the rocks like some cross-eyed goat!")
                        npc(npc, CALM_TALK, "Faffing around like a fool, scramblin' about, hand o'er hand, foot o'er foot.")
                        npc(npc, CALM_TALK, "Nobody's meant to get around like that, man! It's demeaning!")
                        player(CONFUSED, "Well, uh, what's the other way?")
                        npc(npc, CALM_TALK, "Ah, the proper way. UNDER the rocks.")
                        player(CONFUSED, "So, what, you want me to dig a tunnel?")
                        npc(npc, CALM_TALK, "Haddaway, man. The trolls might be thick, but they'll spot you carting rubble out by the ton.")
                        npc(npc, CALM_TALK, "No, what you need is to break into one of the natural caves under here. That'll sort you out.")
                        npc(npc, CALM_TALK, "Then you can enjoy the wonderful sport of cavin' and pot holin'.")
                        npc(npc, CALM_TALK, "Ahh, the great feel of scrambling hand o'er foot through dark, dank passages...bliss!")
                        player(SKEPTICAL_THINKING, "Well, where are these caverns, and can you be sure it'll come out where I need to go?")
                        npc(npc, CALM_TALK, "Well I didn't get much time to do anything but make some notes before the trolls came.")
                        npc(npc, CALM_TALK, "I bet Freda had time to put them all together. She's a bit soft in the head from all her living on the surface, but still a canny lass when it comes to geology.")
                        npc(npc, CALM_TALK, "You can't miss her place. It's a little house to the west of here. You just need to follow the only path in that direction and you'll get there.")
                        npc(npc, CALM_TALK, "I don't know why she keeps to that place when we have a nice comfy cave right here we can turn into a proper home.")
                        npc(npc, CALM_TALK, "It's you lot that I blame! All your gallivanting around on the surface has turned her head!")
                        player(CALM_TALK, "Uh...right, I'll go and ask her about the survey, then.")
                        npc(npc, CALM_TALK, "Aye, see you later then. Watch your head on the way out, mind.") { player.setQuestStage(Quest.DEATH_PLATEAU, STAGE_SPEAK_TO_FREDA) }
                    }
                }
                STAGE_SPEAK_TO_FREDA -> {
                    op("Where is your wife's house again?") {
                        npc(npc, CALM_TALK, "You can't miss her place. It's a little house to the west of here. You just need to follow the only path in that direction and you'll get there.")
                        goto("initialOps")
                    }
                }
            }
            op("Why are you living in this cave?") {
                npc(npc, FRUSTRATED, "Cave? CAVE? This is no cave, ya idiot! This is clearly the entrance hall to the trading post I'm gonna set up with me wife, once the trolls are gone.")
                npc(npc, HAPPY_TALKING, "We're going to have some shelves over there, and put the store room over there...")
                player(SKEPTICAL_THINKING, "Well...you have to admit it's a little...unpolished.")
                npc(npc, FRUSTRATED, "Aye? Well blame the missus for that! The second the trolls came along she packed all the tools up and moved into her fancy-dan little cottage. But not me.")
                npc(npc, CALM_TALK, "I'm not shifting for some stupid trolls. It's them or me, and I'm winning so far. One day they'll all be dead. Then I'll work this place into a proper little home and shop. Then who'll be laughing, eh?")
                player(LAUGH, "Probably you, in a high-pitched, insane cackle.")
                npc(npc, FRUSTRATED, "I heard that!")
                player(CALM_TALK, "Uh, I need to ask something else.")
                goto("initialOps")
            }
            if (stage in STAGE_SPEAK_TO_FREDA..STAGE_RETURN_TO_FREDA)
                op("Can you tell me where the tunnel is?") {
                    npc(npc, FRUSTRATED, "Don't be daft, man. Do I look like I can just magic up a tunnel for ya?")
                    player(CONFUSED, "But haven't you lived here a long time? Don't you know?")
                    npc(npc, SAD, "Well, I've been tryin' to work the place a little, you're right. But all I've got to work with is rocks. If I had some ores I'd be laughing mind, but there's none!")
                    goto("initialOps")
                }
            op("Do you want me to get anything for you?") {
                npc(npc, FRUSTRATED, "No!")
                player(CALM_TALK, "But I could get you a pickaxe, or a hammer.")
                npc(npc, FRUSTRATED, "No!")
                player(CALM_TALK, "What about some food, or some tea?")
                npc(npc, ANGRY, "NO! I don't need you, or yer fancy tools. I'm a dwarf! If I can't do it on me own then I might as well not bother!")
                npc(npc, ANGRY, "Do ye think I'd be stuck in this hole, fighting trolls with me bare hands and teeth, eating moss, and chipping the walls with a stone if I didn't think I could do it?")
                npc(npc, FRUSTRATED, "These trolls aren't gonna beat me! I'm gonna outlast them, and when me wife comes to her senses she'll come back and we can set up together!")
                npc(npc, CALM_TALK, "Aye, we'll set up this little shop, just like we planned. And no trolls are gonna stop us.")
                player(CALM_TALK, "Uh...okay.")
                goto("initialOps")
            }
            op("I think that's it for now.") {
                npc(npc, CALM_TALK, "Oh, is it now? Well get yourself off then. Watch your head on the way out, mind.")
            }
        }
    }

}

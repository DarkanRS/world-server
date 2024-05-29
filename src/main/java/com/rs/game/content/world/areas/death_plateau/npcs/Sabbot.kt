package com.rs.game.content.world.areas.death_plateau.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe.SabbotD
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class Sabbot(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello Sabbot, how's it going?")
            npc(npc, FRUSTRATED, "Terrible! The missus is still insisting on living on the surface like some sort of...some sort of...")
            player(CONFUSED, "Tree?")
            npc(npc, HAPPY_TALKING, "Yes! Exactly! Like some sort of TREE! So, I'm going to sort this place out myself! At least there's room to have a separate toilet down here.")
            player(SECRETIVE, "Urgh...that explains the smell.")
            npc(npc, FRUSTRATED, "You shut your cake hole! Why did you come to bother me anyway?")
            label("initialOps")
            options {
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
                    npc(npc, FRUSTRATED, "Oh, is it now? Well get yourself off then. Watch your head on the way out, mind.")
                }
            }
        }
    }
}

class SabbotAfterQuest(id: Int, tile: Tile) : NPC(id, tile) {
    override fun processNPC() {
        faceDir(Direction.NORTH)
    }
}

@ServerStartupEvent
fun mapSabbot() {
    onNpcClick(15095) { (player, npc) -> SabbotD(player, npc) } // During Death Plateau
    onNpcClick(15097) { (player, npc) -> Sabbot(player, npc) } // After Death Plateau
    instantiateNpc(15097) { id, tile -> SabbotAfterQuest(id, tile) }
}

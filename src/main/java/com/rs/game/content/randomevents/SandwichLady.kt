package com.rs.game.content.randomevents

import com.rs.engine.dialogue.Conversation
import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onButtonClick
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.Ticks
import java.util.*

@ServerStartupEvent
fun handleSandwichLady() {
	onNpcClick(SandwichLady.NPC_ID) { event ->
		val npc = event.npc as? SandwichLady ?: return@onNpcClick
		if (npc.ticks >= SandwichLady.DURATION) return@onNpcClick
		if (npc.owner != event.player) {
			event.player.startConversation(
				Conversation(
					Dialogue()
						.addNPC(npc, HeadE.CALM_TALK, "This is for ${npc.owner.displayName}, not you!")
				)
			)
			return@onNpcClick
		}
		if (event.player.inCombat()) {
			event.player.sendMessage("The sandwich lady gives you a ${npc.selectedSandwich.description.lowercase(Locale.getDefault())}!")
			event.player.inventory.addItemDrop(npc.selectedSandwich.itemId, 1)
			npc.forceTalk("Hope that fills you up!")
			npc.ticks = SandwichLady.DURATION + 4
			return@onNpcClick
		}
		event.player.startConversation(
			Conversation(event.player)
				.addNPC(
					npc,
					HeadE.HAPPY_TALKING,
					"You look hungry to me. I tell you what - have a ${npc.selectedSandwich.description.lowercase(Locale.getDefault())} on me."
				)
				.addNext {
					event.player.tempAttribs.setO<SandwichLady>("sandwichLady", npc)
					event.player.packets.setIFText(297, 48, "Have a ${npc.selectedSandwich.description.lowercase(Locale.getDefault())} for free!")
					event.player.interfaceManager.sendInterface(297)
				}
		)
	}

	onButtonClick(297) { e ->
		if (e.componentId in 10..22) {
			val lady = e.player.tempAttribs.getO<SandwichLady>("sandwichLady")
			e.player.closeInterfaces()
			if (lady == null) {
				e.player.sendMessage("An error has occurred.")
				return@onButtonClick
			}
			if (e.componentId == lady.selectedSandwich.componentId) {
				e.player.sendMessage("The sandwich lady gives you a ${lady.selectedSandwich.description.lowercase(Locale.getDefault())}!")
				e.player.inventory.addItemDrop(lady.selectedSandwich.itemId, 1)
				lady.forceTalk("Hope that fills you up!")
				lady.ticks = SandwichLady.DURATION + 4
			} else {
				e.player.sendMessage("The sandwich lady knocks you out and you wake up somewhere... different.")
				lady.forceTalk("Hey, I didn't say you could have that!")
				lady.ticks = SandwichLady.DURATION - 1
			}
			lady.claimed = true
		}
	}
}

class SandwichLady(owner: Player, tile: Tile) : RandomEventNPC(owner, NPC_ID, tile, DURATION, null, false) {

	companion object {
		const val NPC_ID = 8629
		val DURATION = Ticks.fromMinutes(10)
	}

	private val sandwichOptions = listOf(
		SandwichOption(10, "Baguette", 6961),
		SandwichOption(12, "Triangle sandwich", 6962),
		SandwichOption(14, "Square sandwich", 6965),
		SandwichOption(16, "Roll", 6963),
		SandwichOption(18, "Meat pie", 2327),
		SandwichOption(20, "Doughnut", 14665),
		SandwichOption(22, "Chocolate bar", 1973)
	)
	var selectedSandwich = sandwichOptions[Utils.random(sandwichOptions.size)]

	init {
		run = true
		forceTalk("Sandwich delivery for ${owner.displayName}!")
		setNextFaceEntity(owner)
		isAutoDespawnAtDistance = false
	}

	override fun processNPC() {
		super.processNPC()
		if (!claimed && (owner.interfaceManager.containsChatBoxInter() || owner.interfaceManager.containsScreenInter())) return
		when {
			ticks == DURATION - 3 -> forceTalk("Take that, ${owner.displayName}!")
			ticks == DURATION - 2-> {
				anim(3045)
				owner.spotAnim(80, 5, 60)
				owner.lock()
				owner.anim(836)
				owner.stopAll()
				owner.fadeScreen {
					owner.move(RandomEvents.getRandomTile())
					owner.anim(-1)
					owner.schedule {
						wait(2)
						owner.spotAnim(-1)
						owner.unlock()
					}
				}
			}
			ticks == DURATION + 5 -> finish()
			ticks % 30 == 0 -> forceTalk(randomQuote(owner))
		}
	}

	private fun randomQuote(player: Player): String = when (Utils.randomInclusive(0, 4)) {
		0 -> "All types of sandwiches, ${player.displayName}."
		1 -> "Come on ${player.displayName}, I made these specifically!!"
		2 -> "You better start showing some manners young ${if (player.appearance.isMale) "man" else "lady"}!!"
		else -> "You think I made these just for fun?!!?"
	}
}

data class SandwichOption(val componentId: Int, val description: String, val itemId: Int)

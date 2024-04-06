package com.rs.game.content.quests.gunnarsground

import com.rs.game.model.entity.player.Player

class GunnarsGroundPoem(player: Player) {
    private var poem = arrayOf(
        "Gunnar's Ground",
        "",
        "Our people dwelt on mountains steeped in lore,",
        "A mighty tribe as harsh as any beast",
        "Who then, in face of madness swept to war,",
        "The warlord Gunnar leading to the east.",
        "",
        "This legacy of honour still lives on",
        "In Gunnar's bloodline, fierce to this day.",
        "We sing the tales of battles long since won",
        "And from his righteous purpose never stray.",
        "",
        "But long is gone the author of that threat",
        "And even rolling boulders come to rest,",
        "For Gunnar's ground is rich and fruitful yet",
        "And Gunnar's blood with beauty blessed.",
        "",
        "Now let these freemen from this conflict cease",
        "And let this be the time of Gunthor's peace.")

    init {
        player.packets.sendVarcString(359, poem.joinToString("<br>"))
        player.interfaceManager.sendInterface(116)
    }
}

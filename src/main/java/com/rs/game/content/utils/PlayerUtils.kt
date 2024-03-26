package com.rs.game.content.utils

import com.rs.game.content.achievements.AchievementDef
import com.rs.game.content.achievements.SetReward
import com.rs.game.model.entity.player.Player

fun Player.canSpeakWithGhosts(): Boolean {
    if (setOf(552, 4250, 20064, 20065, 20066, 20067).contains(equipment.neckId))
        return true
    return SetReward.MORYTANIA_LEGS.hasRequirements(this, AchievementDef.Area.MORYTANIA, AchievementDef.Difficulty.HARD, false)
}
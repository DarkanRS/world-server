package com.rs.game.content.quests.troll_stronghold.utils

import com.rs.lib.game.Tile

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_ACCEPTED_QUEST = 1
const val STAGE_ENTERED_ARENA = 2
const val STAGE_ENGAGED_DAD = 3
const val STAGE_FINISHED_DAD = 4
const val STAGE_UNLOCKED_PRISON_DOOR = 5
const val STAGE_UNLOCKED_BOTH_CELLS = 6
const val STAGE_COMPLETE = 7

// NPCs
const val EADGAR = 1113
const val GODRIC = 1114
const val DAD = 1125
const val TWIG_AWAKE = 1126
const val BERRY_AWAKE = 1127
const val TWIG_SLEEPING = 1128
const val BERRY_SLEEPING = 1129
val SPECTATORS = intArrayOf(1118, 1120, 1122, 1124)
val TROLL_GENERALS = intArrayOf(1115, 1116, 1117)

// Items
const val PRISON_KEY = 3135
const val CELL_KEY_GODRIC = 3136
const val CELL_KEY_EADGAR = 3137
const val ROCK_CLIMBING_BOOTS = 3105
const val QUEST_REWARD_LAMP = 13227
const val CLIMBING_BOOTS_POST_DEATH_PLATEAU = 18788

// Objects
const val EADGAR_DOOR = 3765
const val GODRIC_DOOR = 3767
const val PRISON_DOOR = 3780
const val ARENA_DOOR_1 = 34836
const val ARENA_DOOR_2 = 34839

// Animations
const val PICKPOCKETING_ANIM = 881
const val NPC_KNOCKBACK_ANIM = 7282
const val PLAYER_KNOCKBACK_ANIM = 10070

// SpotAnims
const val SLEEPING_SPOTANIM = 277

// Tiles
val TWIG_GUARD_TILE: Tile = Tile.of(2832, 10079, 0)
val BERRY_GUARD_TILE: Tile = Tile.of(2832, 10083, 0)
val GODRIC_UNLOCK_CELL_TILE: Tile = Tile.of(2833, 10078, 0)
val EADGAR_UNLOCK_CELL_TILE: Tile = Tile.of(2833, 10082, 0)
val GODRIC_ESCAPE_ROUTE_TILE: Tile = Tile.of(2835, 10078, 0)
val EADGAR_ESCAPE_ROUTE_TILE: Tile = Tile.of(2835, 10082, 0)
val SHARED_ESCAPE_ROUTE_TILE: Tile = Tile.of(2824, 10050, 0)
val SHARED_DEATH_TELE_TILE: Tile = Tile.of(2842, 10001, 0)
val EADGAR_AND_GODRIC_CELL_CHECK: Array<IntArray> = arrayOf(
    intArrayOf(2822, 2831, 10077, 10080),
    intArrayOf(2824, 2831, 10081, 10084)
)

// Var Keys
const val UNLOCKED_GODRIC_CELL = "unlockedGodricCell"
const val UNLOCKED_EADGAR_CELL = "unlockedEadgarCell"
const val TROLL_STRONGHOLD_QUEST_LAMPS_LOST = "trollStrongholdQuestLampsLost"

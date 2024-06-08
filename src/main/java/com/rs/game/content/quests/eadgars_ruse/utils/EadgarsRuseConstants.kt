package com.rs.game.content.quests.eadgars_ruse.utils

import com.rs.lib.game.Tile

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_SPEAK_TO_EADGAR = 1
const val STAGE_SPEAK_TO_BURNTMEAT = 2
const val STAGE_BRING_HUMAN = 3
const val STAGE_GET_PARROT = 4
const val STAGE_NEED_TO_HIDE_PARROT = 5
const val STAGE_HIDDEN_PARROT = 6
const val STAGE_NEED_TROLL_POTION = 7
const val STAGE_FETCH_PARROT = 8
const val STAGE_RETRIEVED_PARROT = 9
const val STAGE_RECEIVED_FAKE_MAN = 10
const val STAGE_GAVE_FAKE_MAN_TO_BURNTMEAT = 11
const val STAGE_DISCOVERED_KEY_LOCATION = 12
const val STAGE_UNLOCKED_STOREROOM = 13
const val STAGE_COMPLETE = 14

// NPCs
const val GOUTWEED_GUARD = 1150
const val THISTLE = 1214
const val PARROTY_PETE = 1216
const val PARROT = 4535
const val FAKE_MAN_NPC = 4460

// Items
const val NORMAL_LOGS = 1511
const val WHEAT = 1947
const val STEW = 2003
const val VODKA = 2015
const val PINEAPPLE_CHUNKS = 2116
const val RAW_CHICKEN = 2138
const val BURNT_MEAT = 2146
const val GOUTWEED = 3261
const val TROLL_THISTLE = 3262
const val DRIED_THISTLE = 3263
const val TROLL_POTION = 3265
const val DRUNK_PARROT = 3266
const val DIRTY_ROBE = 3267
const val FAKE_MAN_ITEM = 3268
const val STOREROOM_KEY = 3269
const val ALCOCHUNKS = 3270

// Objects
const val STOREROOM_DOOR = 3810
val KITCHEN_DRAWERS = intArrayOf(3816, 3817)
const val RACK = 3821
const val GOUTWEED_CRATE = 3822
const val AVIARY_HATCH = 4043

// Animations
const val OPEN_DRAWERS_ANIM = 536
const val GOUTWEED_CAUGHT_ANIM = 787
const val PICKUP_ANIM = 827
const val DRY_THISTLE_ANIM = 897
const val GOUTWEED_CRATE_GUARD_ANIM = 5374
const val GOUTWEED_GUARD_THROW_ANIM = 13790

// SpotAnims
const val ROCK_PROJECTILE = 276
const val SLEEPING_SPOTANIM = 277
const val STUNNED_BIRDS = 348

// Tiles
val OUTSIDE_STOREROOM_TILE = Tile(2865, 10088, 0)

// Var Keys
const val LEARNED_ABOUT_VODKA = "learnedAboutVodka"
const val LEARNED_ABOUT_PINEAPPLE = "learnedAboutPineapple"
const val HIDDEN_PARROT_EADGAR_CHAT = "hiddenParrotEadgarChat"

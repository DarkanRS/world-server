package com.rs.game.content.quests.biohazard.utils

import com.rs.lib.game.Tile

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_SPEAK_TO_JERICO = 1
const val STAGE_SPEAK_TO_OMART = 2
const val STAGE_RETURN_TO_JERICO = 3
const val STAGE_JERICO_SUGGESTS_PIGEONS = 4
const val STAGE_MOURNERS_DISTRACTED = 5
const val STAGE_COMPLETED_WALL_CROSSING = 6
const val STAGE_APPLE_IN_CAULDRON = 7
const val STAGE_FOUND_DISTILLATOR = 8
const val STAGE_RECEIVED_VIALS = 9
const val STAGE_RECEIVED_TOUCH_PAPER = 10
const val STAGE_RETURN_TO_ELENA = 11
const val STAGE_SPEAK_TO_KING = 12
const val STAGE_COMPLETE = 13

// NPCs
const val GUIDOR_WIFE = 342
const val KILRON = 349
const val OMART = 350
const val JERICO = 366
const val VARROCK_GUARD = 368
const val MOURNER_WITH_KEY = 370

// Items
const val ETHENEA = 415
const val LIQUID_HONEY = 416
const val SULPHURIC_BROLINE = 417
const val PLAGUE_SAMPLE = 418
const val TOUCH_PAPER = 419
const val DISTILLATOR = 420
const val BIRD_FEED = 422
const val KEY = 423
const val PIGEON_CAGE = 424
const val PRIEST_GOWN_TOP = 426
const val PRIEST_GOWN_BOTTOM = 428
const val DOCTORS_GOWN = 430
const val ROTTEN_APPLE = 1984

// Objects
const val CAULDRON = 2043
val JERICO_CUPBOARD = intArrayOf(2056, 2057)
val DISTILLATOR_CAGE_GATE = intArrayOf(2058, 2060)
val DOCTORS_GOWN_BOX = intArrayOf(2062, 2063)
const val DISTILLATOR_CRATE = 2064
const val WATCHTOWER_FENCE = 2067
const val WALL_CROSSING_OBJ = 36308

// Sounds
const val APPLE_IN_CAULDRON_SOUND = 1732

// Animations
const val OPEN_CUPBOARD_ANIM = 536

// SpotAnims
const val BIRD_SPOTANIM = 72

// Var Keys
const val USED_BIRD_FEED = "usedBirdFeed"
const val OMART_TEMP_STAGE = "omartTempStage"
const val GOT_DISTILLATOR = "gotDistillator"
const val GAVE_HOPS_VIAL_OF = "gaveHopsItem"
const val GAVE_DA_VINCI_VIAL_OF = "gaveDaVinciItem"
const val GAVE_CHANCY_VIAL_OF = "gaveChancyItem"
const val LOST_ITEM_TO_GUARD = "lostItemToGuard"
const val GAVE_ITEMS_TO_GUIDOR = "gaveItemsToGuidor"

val EAST_ARDOUGNE_WALL: Tile = Tile.of(2558, 3267, 0)
val WEST_ARDOUGNE_WALL: Tile = Tile.of(2557, 3267, 0)

// Pigeon release tiles
val PIGEON_RELEASE_TILES = arrayOf(
    Tile.of(2563, 3301, 0),
    Tile.of(2558, 3304, 0),
    Tile.of(2558, 3307, 0),
    Tile.of(2560, 3307, 0))

// Mourner HQ tile check
val MOURNER_HQ: Array<IntArray> = arrayOf(
    intArrayOf(2542, 2546, 3324, 3327),
    intArrayOf(2547, 2555, 3321, 3327))

// Mourner HQ Garden tile check
val MOURNER_HQ_GARDEN: Array<IntArray> = arrayOf(intArrayOf(2542, 2555, 3328, 3333))

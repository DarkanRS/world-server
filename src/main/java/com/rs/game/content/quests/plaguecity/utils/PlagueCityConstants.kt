package com.rs.game.content.quests.plaguecity.utils

import com.rs.lib.game.Tile

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_SPEAK_TO_ALRENA = 1
const val STAGE_RECEIVED_GAS_MASK = 2
const val STAGE_PREPARE_TO_DIG = 3
const val STAGE_CAN_DIG = 4
const val STAGE_UNCOVERED_SEWER_ENTRANCE = 5
const val STAGE_NEED_HELP_WITH_GRILL = 6
const val STAGE_ROPE_TIED_TO_GRILL = 7
const val STAGE_GRILL_REMOVED = 8
const val STAGE_SPOKEN_TO_JETHICK = 9
const val STAGE_GAVE_BOOK_TO_TED = 10
const val STAGE_SPEAK_TO_MILLI = 11
const val STAGE_SPOKEN_TO_MILLI = 12
const val STAGE_PERMISSION_TO_BRAVEK = 13
const val STAGE_GET_HANGOVER_CURE = 14
const val STAGE_GAVE_HANGOVER_CURE = 15
const val STAGE_FREED_ELENA = 16
const val STAGE_COMPLETE = 17

// NPCs
const val ALRENA = 710
const val EDMOND_ABOVE_GROUND = 3213
const val EDMOND_BELOW_GROUND = 3214
const val JETHICK = 725
const val TED_REHNISON = 721
const val MARTHA_REHNISON = 722
const val BILLY_REHNISON = 723
const val MILLI_REHNISON = 724
const val NON_COMBAT_MOURNER = 718
const val MOURNER_EAST_PRISON_DOOR = 717
const val MOURNER_WEST_PRISON_DOOR = 3216
const val CLERK = 713
const val BRAVEK = 711
const val ELENA_PRISON = 715
const val EAST_ARDOUGNE_MOURNERS = 719

// Items
const val DWELLBERRIES = 2126
const val ROPE = 954
const val SPADE = 952
const val BUCKET = 1925
const val BUCKET_OF_WATER = 1929
const val HANGOVER_CURE = 1504
const val CHOCOLATE_DUST = 1975
const val CHOCOLATEY_MILK = 1977
const val PESTLE_AND_MORTAR = 233
const val BUCKET_OF_MILK = 1927
const val SNAPE_GRASS = 231
const val PICTURE_OF_ELENA = 1510
const val GAS_MASK = 1506
const val A_MAGIC_SCROLL = 1505
const val BOOK_TURNIP_GROWING_FOR_BEGINNERS = 1509
const val SCRUFFY_NOTE = 1508
const val WARRANT = 1503
const val SMALL_KEY = 1507

// Objects
const val MUD_PATCH_UNDIGGABLE = 2531
const val MUD_PATCH_DIGGABLE = 2532
const val MUD_PILE_SEWERS = 2533
val GAS_MASK_WARDROBE = intArrayOf(2524, 2525)
const val SEWER_PIPE = 2542
const val SEWER_GRILL = 11422
const val SEWER_CS_GRILL = 11416
const val SEWER_CS_ROPE_MIDDLE = 11412
const val SEWER_CS_ROPE_END = 11414
val MANHOLES = intArrayOf(2543, 2544, 2545)
const val TED_REHNISON_DOOR = 2537
const val HEAD_MOURNER_DOORS = 35991
const val BRAVEK_DOOR = 2528
const val KEY_BARREL = 2530
const val ELENA_PRISON_DOOR = 2526
const val PRISON_STAIRS_UP = 2523
const val PRISON_STAIRS_DOWN = 2522

// Sounds
const val PULL_GRILL = 1730
const val ATTACH_ROPE_GRILL = 1731
const val OPEN_MANHOLE_SOUND = 75

// Animations
const val DIG_ANIM = 830
const val PESTLE_AND_MORTAR_ANIM = 364
const val OPEN_WARDROBE_ANIM = 536
const val CLIMB_INTO_PIPE_ANIM = 10580
const val TIE_ROPE_TO_GRILL_ANIM = 3191
const val GRILL_PULL_ATTEMPT_ANIM = 3192
const val ROPE_PULL_ANIM = 3187
const val CS_ROPE_ANIM = 3188
const val CS_GRILL_ANIM = 3189
const val POUR_BUCKET_OF_WATER_ANIM = 2283
const val CLIMB_LADDER_ANIM = 828
const val DRINK_HANGOVER_CURE_ANIM = 1330

// Varbits
const val EDMOND_VB = 1783
const val ELENA_VB = 1784
const val MUD_PATCH_VB = 1785
const val GRILL_VB = 1787

// Tiles
val MUD_PATCH_DIG_TILE: Tile = Tile.of(2566, 3332, 0)
val EDMOND_SEWER_TELE_LOC: Tile = Tile.of(2518, 9759, 0)
val EDMOND_HOUSE_TELE_LOC: Tile = Tile.of(2566, 3333, 0)
val ROPE_ATTACH_TILE: Tile = Tile.of(2514, 9740, 0)
val CUTSCENE_END_TILE: Tile = Tile.of(2514, 9740, 0)
val SEWER_PIPE_TELE_LOC: Tile = Tile.of(2529, 3304, 0)
val EDMOND_SEWER_SPAWN_LOC: Tile = Tile.of(2517, 9752, 0)
var MANHOLE_TELE_LOC: Tile = Tile.of(2514, 9739, 0)

// Regions
const val EDMOND_HOUSE_ABOVE_GROUND = 10292
const val ARDOUGNE_SEWERS = 10136

// Var Keys
const val WATER_USED_ON_MUD = "waterUsedOnMudPatch"
const val EDMOND_SUGGESTED_ROPE = "edmondSuggestedRope"
const val ENTERED_CITY = "enteredCity"
const val JETHICK_NEEDS_PICTURE = "jethickNeedsPicture"
const val JETHICK_RETURN_BOOK = "jethickReturnBook"
const val ATTEMPTED_PRISON_HOUSE_DOORS = "attemptedPrisonHouseDoors"
const val ENTERED_PRISON_HOUSE = "enteredPrisonHouse"
const val ATTEMPTED_TO_FREE_ELENA = "attemptedToFreeElena"
const val FOUND_KEY_IN_BARREL = "foundKeyInBarrel"
const val ENTERED_PRISON_CELL = "enteredPrisonCell"
const val ARDOUGNE_TELEPORT_UNLOCKED = "ardougneTeleportUnlocked"

// West Ardougne Area check tiles
val WEST_ARDOUGNE_RANGES: Array<IntArray> = arrayOf(
    intArrayOf(2431, 2460, 3306, 3323),
    intArrayOf(2460, 2557, 3280, 3323),
    intArrayOf(2463, 2557, 3323, 3335),
    intArrayOf(2510, 2557, 3265, 3280),
    intArrayOf(2535, 2543, 9668, 9674),
    intArrayOf(2033, 2046, 4627, 4652))

// South East House Door check tiles
val SOUTH_EAST_HOUSE_EAST_DOOR: Array<IntArray> = arrayOf(
    intArrayOf(2539, 2541, 3273, 3274))

package com.rs.game.content.quests.sheep_herder.utils

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_RECEIVED_SHEEP_FEED = 1
const val STAGE_RECEIVED_PROTECTIVE_CLOTHING = 2
const val STAGE_SHEEP_INCINERATED = 3
const val STAGE_COMPLETE = 4

// NPCs
const val COUNCILLOR_HALGRIVE = 289
const val DOCTOR_ORBON = 290
const val FARMER_BRUMTY = 291

const val RED_SHEEP = 292
const val GREEN_SHEEP = 293
const val BLUE_SHEEP = 294
const val YELLOW_SHEEP = 295

// Items
const val POISON = 273
const val CATTLE_PROD = 278
const val SHEEP_FEED = 279
const val RED_SHEEP_BONES = 280
const val GREEN_SHEEP_BONES = 281
const val BLUE_SHEEP_BONES = 282
const val YELLOW_SHEEP_BONES = 283
const val PLAGUE_JACKET = 284
const val PLAGUE_TROUSERS = 285
const val BALL_OF_WOOL = 1759

// Objects
const val INCINERATOR = 165
const val NORTH_GATE = 166
const val SOUTH_GATE = 167

// Sounds
const val ANGRY_SHEEP_SOUND = 755
const val PROD_SHEEP_SOUND = 756
const val DYING_SHEEP_SOUND = 763
const val INCINERATE_BONES_SOUND = 2725

// Animations
const val PROD_SHEEP_ANIM = 799
const val SHEEP_DEATH_ANIM = 5336
const val INCINERATE_BONES_ANIM = 3243

// Tile Checks
val NO_GO_ZONES: Array<IntArray> = arrayOf(
    intArrayOf(2616, 2623, 3347, 3364),
    intArrayOf(2602, 2611, 3330, 3341))

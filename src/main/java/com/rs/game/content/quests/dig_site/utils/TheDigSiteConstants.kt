package com.rs.game.content.quests.dig_site.utils

import com.rs.lib.game.Tile

// Quest Stages
const val STAGE_UNSTARTED = 0
const val STAGE_GET_LETTER_STAMPED = 1
const val STAGE_RECEIVED_SEALED_LETTER = 2
const val STAGE_BEGIN_EXAM_1 = 3
const val STAGE_BEGIN_EXAM_2 = 4
const val STAGE_BEGIN_EXAM_3 = 5
const val STAGE_COMPLETED_EXAMS = 6
const val STAGE_RECEIVED_INVITATION = 7
const val STAGE_PERMISSION_GRANTED = 8
const val STAGE_SPEAK_TO_DOUG = 9
const val STAGE_COVERED_IN_COMPOUND = 10
const val STAGE_BLOWN_UP_BRICKS = 11
const val STAGE_COMPLETE = 100

// NPCs
const val DOUG_DEEPING = 614
const val GREEN_STUDENT = 615
const val BROWN_STUDENT = 616
const val PURPLE_STUDENT = 617
const val ARCHAEOLOGICAL_EXPERT = 619
const val PANNING_GUIDE = 620
val EXAMINER = intArrayOf(618, 4566, 4567)
val DIGSITE_WORKMEN = intArrayOf(613, 4564, 4565)
const val MUSEUM_GUARD = 5942

// Items
const val VIAL = 229
const val OYSTER = 407
const val GOLD_ORE = 444
const val BONES = 526
const val TINDERBOX = 590
const val SPECIMEN_JAR = 669
const val SPECIMEN_BRUSH = 670
const val ANIMAL_SKULL = 671
const val SPECIAL_CUP = 672
const val TEDDY = 673
const val CRACKED_SAMPLE = 674
const val ROCK_PICK = 675
const val TROWEL = 676
const val EMPTY_PANNING_TRAY = 677
const val GOLD_PANNING_TRAY = 678
const val MUD_PANNING_TRAY = 679
const val NUGGETS = 680
const val ANCIENT_TALISMAN = 681
const val UNSTAMPED_LETTER = 682
const val SEALED_LETTER = 683
const val BELT_BUCKLE = 684
const val OLD_BOOT = 685
const val RUSTY_SWORD = 686
const val BROKEN_ARROW = 687
const val BUTTONS = 688
const val BROKEN_STAFF = 689
const val EXAM_1_CERT = 691
const val EXAM_2_CERT = 692
const val EXAM_3_CERT = 693
const val CERAMIC_REMAINS = 694
const val OLD_TOOTH = 695
const val INVITATION_LETTER = 696
const val DAMAGED_ARMOUR = 697
const val BROKEN_ARMOUR = 698
const val STONE_TABLET = 699
const val CHEMICAL_POWDER = 700
const val AMMONIUM_NITRATE = 701
const val UNIDENTIFIED_LIQUID = 702
const val NITROGLYCERIN = 703
const val GROUND_CHARCOAL = 704
const val MIXED_CHEMICALS = 705
const val MIXED_CHEMICALS_CHARCOAL = 706
const val CHEMICAL_COMPOUND = 707
const val ARCENIA_ROOT = 708
const val CHEST_KEY = 709
const val BOOK_ON_CHEMICALS = 711
const val CUP_OF_TEA = 712
const val KNIFE = 946
const val SPADE = 952
const val ROPE = 954
const val COINS = 995
const val LEATHER_GLOVES = 1059
const val LEATHER_BOOTS = 1061
const val BROKEN_GLASS = 1469
const val OPAL = 1609
const val UNCUT_OPAL = 1625
const val UNCUT_JADE = 1627
const val NEEDLE = 1733
const val CHISEL = 1755
const val CHOCOLATE_CAKE = 1897
const val ROTTEN_APPLE = 1984
const val FRUIT_BLAST = 2084
const val HAMMER = 2347
const val GOLD_BAR = 2357

// Objects
const val ALTAR_WINCH = 2350
const val DOUG_WINCH = 2351
const val DOUG_ROPE = 2352
const val ALTAR_ROPE = 2353
val SACKS = intArrayOf(2354, 2356)
const val WRONG_BUSH = 2357
const val CORRECT_BUSH = 2358
const val BARREL = 2359
val CHEST = intArrayOf(2360, 2361)
const val BRICK = 2362
const val PANNING_POINT = 2363
const val TRAINING_DIG_SIGNPOST = 2366
const val LVL1_DIG_SIGNPOST = 2367
const val LVL2_DIG_SIGNPOST = 2368
const val LVL3_DIG_SIGNPOST = 2369
const val PRIVATE_DIG_SIGNPOST = 2370
const val DIG_EDUCATIONAL_SIGNPOST = 2371
const val SPECIMEN_TRAY = 2375
val SOILS = intArrayOf(2376, 2377, 2378)
val SPECIMEN_JAR_CUPBOARD = intArrayOf(17302, 17303)
const val BURIED_SKELETON = 19996
val MUSEUM_GUARD_GATE = intArrayOf(24560, 24561)
const val STONE_TABLET_OBJ = 17369
val ROCK_PICK_CUPBOARD = intArrayOf(35222, 35223)
val BOOKCASES = intArrayOf(35224, 35226, 35227, 17320, 17321, 17382)

// Animations
const val NPC_STUN_ANIM = 422
const val PLAYER_STUN_ANIM = 424
const val SEARCH_ANIM = 536
const val BEND_DOWN_ANIM = 827
const val CLIMB_ANIM = 828
const val PICKPOCKETING_ANIM = 881
const val DIGGING_ANIM = 2272
const val ROCK_PICK_ANIM = 4592
const val PANNING_ANIM = 4593

// SpotAnims
const val STUN_BIRDS_SPOTANIM = 80

// Varbits
const val BARREL_VB = 2547
const val TABLET_VB = 2548

// Var Keys
const val GREEN_STUDENT_TALKED_TO = "greenStudentTalkedTo"
const val GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER = "greenStudentExam1ObtainedAnswer"
const val GREEN_STUDENT_EXAM_2_OBTAINED_ANSWER = "greenStudentExam2ObtainedAnswer"
const val GREEN_STUDENT_EXAM_3_OBTAINED_ANSWER = "greenStudentExam3ObtainedAnswer"

const val PURPLE_STUDENT_TALKED_TO = "purpleStudentTalkedTo"
const val PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER = "purpleStudentExam1ObtainedAnswer"
const val PURPLE_STUDENT_EXAM_2_OBTAINED_ANSWER = "purpleStudentExam2ObtainedAnswer"
const val PURPLE_STUDENT_EXAM_3_TALKED_TO = "purpleStudentExam3TalkedTo"
const val PURPLE_STUDENT_EXAM_3_OBTAINED_ANSWER = "purpleStudentExam3ObtainedAnswer"

const val BROWN_STUDENT_TALKED_TO = "brownStudentTalkedTo"
const val BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER = "brownStudentExam1ObtainedAnswer"
const val BROWN_STUDENT_EXAM_2_OBTAINED_ANSWER = "brownStudentExam2ObtainedAnswer"
const val BROWN_STUDENT_EXAM_3_OBTAINED_ANSWER = "brownStudentExam3ObtainedAnswer"

const val PANNING_GUIDE_GIVEN_TEA = "panningGuideGivenTea"

const val ATTACHED_ROPE_ALTAR_WINCH = "attachedRopeAltarWinch"
const val ATTACHED_ROPE_DOUG_WINCH = "attachedRopeDougWinch"

const val UNLOCKED_CHEST = "unlockedChest"

// Map of certificates to interfaces
val certToInterfaceMap = mapOf(
    EXAM_1_CERT to 440,
    EXAM_2_CERT to 441,
    EXAM_3_CERT to 444
)

// Dig Site Tile Checks
val TRAINING_DIG: Array<IntArray> = arrayOf(
    intArrayOf(3351, 3358, 3395, 3401),
    intArrayOf(3366, 3373, 3396, 3401))

val LVL_1_DIG: Array<IntArray> = arrayOf(
    intArrayOf(3359, 3364, 3401, 3415),
    intArrayOf(3366, 3373, 3402, 3415))

val LVL_2_DIG: Array<IntArray> = arrayOf(
    intArrayOf(3349, 3364, 3423, 3431),
    intArrayOf(3349, 3356, 3414, 3419))

val LVL_3_DIG: Array<IntArray> = arrayOf(
    intArrayOf(3349, 3358, 3403, 3413),
    intArrayOf(3369, 3378, 3436, 3443))

val PRIVATE_DIG: Array<IntArray> = arrayOf(
    intArrayOf(3366, 3373, 3422, 3431))


val ALTAR_DUNGEON_PRE_EXPLOSION = Tile(3369, 9827, 0)
val ALTAR_DUNGEON_POST_EXPLOSION = Tile(3369, 9763, 0)
val ALTAR_DUNGEON_POST_QUEST = Tile(3177, 5731, 0)
val ALTAR_GROUND_LOCATION = Tile(3353, 3416, 0)

val DOUG_DUNGEON_PRE_EXPLOSION = Tile(3352, 9817, 0)
val DOUG_DUNGEON_POST_EXPLOSION = Tile(3352, 9753, 0)
val DOUG_DUNGEON_POST_QUEST = Tile(3159, 5722, 0)
val DOUG_GROUND_LOCATION = Tile(3370, 3427, 0)

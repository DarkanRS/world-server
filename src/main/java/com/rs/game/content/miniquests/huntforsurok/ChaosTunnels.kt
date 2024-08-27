package com.rs.game.content.miniquests.huntforsurok

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.World.sendProjectile
import com.rs.game.World.setObjectRouteType
import com.rs.game.content.miniquests.huntforsurok.bork.BorkController
import com.rs.game.content.skills.runecrafting.RunecraftingAltar
import com.rs.game.content.world.areas.wilderness.WildernessController
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.npc.OwnedNPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.util.stream.Stream

const val DORN = 5839
const val AEGIS = 5840
const val DAHCSNU = 5841

@ServerStartupEvent
fun mapChaosTunnels() {
    onNpcClick(AEGIS) { e ->
        e.player.startConversation {
            npc(AEGIS, HeadE.CHEERFUL, "Hello. Can I help you?")
            if (e.player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 2) {
                player(HeadE.FRUSTRATED, "Where did he go?")
                npc(AEGIS, HeadE.CONFUSED, "Where did who go?")
                player(HeadE.FRUSTRATED, "Surok! I saw him come in this way!")
                npc(AEGIS, HeadE.AMAZED, "Oh, Lord Magis? Why yes; he went through the portal here.")
                player(HeadE.CHEERFUL, "Right! Thanks!")
                npc(AEGIS, HeadE.CHEERFUL, "Was there anything else?")
            }
            player(HeadE.CONFUSED, "Yes. What is this place?")
            npc(AEGIS, HeadE.CHEERFUL, "These are the Dagon'hai tunnels. They radiate with the energy of chaos magic. At the far end of the tunnel, you will find a portal to the Chaos Altar itself, where chaos runes are crafted.")
            options {
                op("Why is the tunnel so short?") {
                    player(HeadE.CONFUSED, "Why is the tunnel so short?")
                    npc(AEGIS, HeadE.CHEERFUL, "The nature of chaos magic itself is mysterious and hard to understand.")
                    player(HeadE.CONFUSED, "What do you mean?")
                    npc(AEGIS, HeadE.CALM_TALK, "The tunnel is here, and yet it is not here. We also are here, where we should be, yet we are somewhere else. And so, the tunnel goes from one place to another, yet touches neither.")
                    player(HeadE.CONFUSED, "I really don't understand.")
                    npc(AEGIS, HeadE.CHEERFUL, "I told you you wouldn't. The best way to think of it is that the tunnel is like a gateway that allows you to travel from one place in the world to another in a more safe and convenient way.")
                    player(HeadE.CHEERFUL, "Ok, I think I see. So it is like a magical alternate route?")
                    npc(AEGIS, HeadE.CHEERFUL, "Exactly! Well done! And at the end, there is a portal which will transport you to the Chaos Altar.")
                    player(HeadE.CHEERFUL, "Oh, ok.")
                }
                op("Why are there only three of you?") {
                    player(HeadE.CONFUSED, "Why are there only three of you?")
                    npc(AEGIS, HeadE.CHEERFUL, "Our order is a secret one and those of us that remain and survive through the ages choose to hide ourselves away.")
                    player(HeadE.CONFUSED, "Where are the rest of you now?")
                    npc(AEGIS, HeadE.CHEERFUL, "We have built or found many tunnels such as these. This is not the only one. Rest assured, there are many more of us around.")
                }
                op("Is the Wilderness above us?") {
                    player(HeadE.CONFUSED, "Is the Wilderness above us?")
                    npc(AEGIS, HeadE.CHEERFUL, "Yes, I can understand you would expect it to be since the tunnel leads to the Chaos Altar. And yes, the tunnel does not enter the Wilderness, no.")
                    npc(AEGIS, HeadE.CHEERFUL, "Rest assured, you are safe here and should not come to harm.")
                }
                op("You seem quite nice for a Zamorakian.") {
                    player(HeadE.CHEERFUL, "You seem quite nice for a Zamorakian.")
                    npc(AEGIS, HeadE.CALM_TALK, "History is written by the strong and the influential.")
                    player(HeadE.CONFUSED, "What does that mean?")
                    npc(AEGIS, HeadE.CHEERFUL, "It means we cannot always believe what we are told to believe. You would do well to remember that. Especially when thinking about how long it takes to cremate bodies.")
                    player(HeadE.CHEERFUL, "I will! Thank you!")
                }
                op("Thanks for your time.") {
                    player(HeadE.CHEERFUL, "Thanks for your time.")
                }
            }
        }
    }

    onNpcClick(DAHCSNU) { e ->
        e.player.startConversation {
            player(HeadE.CHEERFUL, "Hello there.")
            npc(DAHCSNU, HeadE.FRUSTRATED, "Can't you see that I'm busy here?")
            player(HeadE.AMAZED, "Oh. Sorry, you don't look very busy.")
            npc(DAHCSNU, HeadE.FRUSTRATED, "Don't look busy? I've got a lot of important work to do here.")
            player(HeadE.CONFUSED, "Really? What do you do?")
            npc(DAHCSNU, HeadE.FRUSTRATED, "That doesn't concern you. What are you doing here anyway?")
            options {
                op("I'm not actually sure.") {
                    player(HeadE.SAD_MILD, "I'm not actually sure.")
                    npc(DAHCSNU, HeadE.FRUSTRATED, "Hmph! Well I can tell you what you ARE doing.")
                    player(HeadE.CONFUSED, "What's that?")
                    npc(DAHCSNU, HeadE.FRUSTRATED, "Wasting my time! Now go away! If you must speak to someone, speak to one of the other monks around here!")
                }
                op("I'm on an important quest!") {
                    player(HeadE.CHEERFUL, "I'm on an important quest!")
                    npc(DAHCSNU, HeadE.CALM_TALK, "Really...")
                    player(HeadE.CHEERFUL, "Yes! Do you want to know what my quest is?")
                    npc(DAHCSNU, HeadE.CALM_TALK, "Let me see...you helped out a trader who in turn asked you to deliver something for him, and then you had to journey to some palace to see someone and they said thanks by sending you somewhere else.")
                    player(HeadE.CONFUSED, "Uhh...")
                    npc(DAHCSNU, HeadE.CALM_TALK, "So, now you've ended up down here, bothering me?")
                    player(HeadE.CONFUSED, "Uh, yes, that's about right.")
                    npc(DAHCSNU, HeadE.CALM_TALK, "Lucky me...")
                    player(HeadE.CONFUSED, "Sorry about that. I'll leave you alone, then.")
                }
                op("None of your business!") {
                    player(HeadE.FRUSTRATED, "None of your business!")
                    npc(DAHCSNU, HeadE.FRUSTRATED, "Of course it isn't, but you were the one who came here bothering me! So, unless you want something, be off with you!")
                }
                op("I am looking for something.") {
                    player(HeadE.CONFUSED, "I am looking for something.")
                    npc(DAHCSNU, HeadE.CONFUSED, "What are you looking for exactly?")
                    player(HeadE.CONFUSED, "The Chaos Altar, I think.")
                    npc(DAHCSNU, HeadE.FRUSTRATED, "Hmph! Well, it's just a bit further down. Now if you don't mind, I'm very busy here!")
                }
                op("I'm the leader of the Dagon'hai. Bow before me!") {
                    player(HeadE.FRUSTRATED, "I'm the leader of the Dagon'hai. Bow before me!")
                    npc(DAHCSNU, HeadE.CONFUSED, "You? Leader of the Dagon'hai? Hah! I've never heard of such rubbish! You call yourself a mage?")
                    options {
                        op("I'm a very powerful mage!") {
                            player(HeadE.FRUSTRATED, "I'm a very powerful mage!")
                            npc(DAHCSNU, HeadE.AMAZED, "Really?")
                            player(HeadE.FRUSTRATED, "Yes! Be afraid! Be very afraid! Ha ha ha!")
                            npc(DAHCSNU, HeadE.CALM_TALK, "I see. Well then, let's see how you handle this!")
                            player(HeadE.AMAZED, "What...?!?!") //TODO deflect spell based on magic level?
                            //If fail, player jumps and says they can't stop jumping
                            //then CHEERFUL, "Hah! I knew you were no mage! Let that be a lesson to you! Now, please leave.
                            npc(DAHCSNU, HeadE.AMAZED, "So! You are a good mage! You deflected my spell.")
                            player(HeadE.CALM_TALK, "Hah! Yes. I told you so! Now, I have work to be done.")
                            npc(DAHCSNU, HeadE.CALM_TALK, "Of course. Goodbye!")
                        }
                        op("No, you're right. I'm not a mage.") {
                            player(HeadE.SAD_MILD, "No, you're right. I'm not a mage.")
                            npc(DAHCSNU, HeadE.FRUSTRATED, "I didn't think so! Now go away. As I said, I'm very busy!")
                        }
                    }
                }
            }
        }
    }

    onNpcClick(DORN) { e ->
        e.player.startConversation {
            npc(DORN, HeadE.CALM_TALK, "You are excused. And you are welcome.")
            player(HeadE.CONFUSED, "Excuse me...er...thanks.")
            npc(DORN, HeadE.CALM_TALK, "We are the Order of the Dagon'hai.")
            player(HeadE.CONFUSED, "Who are you?")
            npc(DORN, HeadE.CALM_TALK, "Through my magic, I can see a short way into the future.")
            player(HeadE.CONFUSED, "How do you seem to know what I'm going to say? ...Er...oh.")
            npc(DORN, HeadE.CALM_TALK, "These are the Tunnels of Chaos.")
            player(HeadE.CONFUSED, "What is...uh...aha! I'm not going to ask that. So you got it wrong!")
            npc(DORN, HeadE.CALM_TALK, "Indeed. You are very clever.")
            player(HeadE.CHEERFUL, "So I won!")
            npc(DORN, HeadE.CALM_TALK, "Yes.")
            player(HeadE.CONFUSED, "So, what is this place?")
            player(HeadE.FRUSTRATED, "I mean...Argh! How do you do that?")
            npc(DORN, HeadE.CALM_TALK, "I can tell that your mind is not suited to the paradoxicalities of precognition.")
            player(HeadE.CONFUSED, "Why what does what now?")
            npc(DORN, HeadE.CALM_TALK, "You get confused very easily.")
            player(HeadE.CALM_TALK, "I knew that.")
            npc(DORN, HeadE.CALM_TALK, "Of course you did. Speak to one of my order here. They will be able to explain in a manner more suited to your understanding.")
            npc(DORN, HeadE.CALM_TALK, "You are welcome, " + e.player.displayName + ". There is a bed around here if you wish.")
            player(HeadE.CHEERFUL, "Thanks. My name's " + e.player.displayName + ", by the w...uh...okay, I think I need to lie down.")
        }
    }

    onObjectClick(65203) { e ->
        if (e.player.inCombat(10000) || e.player.hasBeenHit(10000)) {
            e.player.sendMessage("You cannot enter the rift while you're under attack.")
            return@onObjectClick
        }
        if (e.objectAt(3058, 3550)) e.player.tele(e.player.transform(125, 1920, 0))
        if (e.objectAt(3118, 3570)) e.player.tele(e.player.transform(130, 1920, 0))
        if (e.objectAt(3129, 3587)) e.player.tele(e.player.transform(105, 1972, 0))
        if (e.objectAt(3164, 3561)) e.player.tele(e.player.transform(128, 1918, 0))
        if (e.objectAt(3176, 3585)) e.player.tele(Tile.of(3290, 5539, 0))
    }

    onObjectClick(28782) { e ->
        if (e.objectAt(3183, 5470)) e.player.tele(e.player.transform(-125, -1920, 0))
        if (e.objectAt(3248, 5490)) e.player.tele(e.player.transform(-130, -1920, 0))
        if (e.objectAt(3234, 5559)) e.player.tele(e.player.transform(-105, -1972, 0))
        if (e.objectAt(3292, 5479)) e.player.tele(e.player.transform(-128, -1918, 0))
        if (e.objectAt(3291, 5538)) e.player.tele(e.player.transform(-115, -1953, 0))
        e.player.controllerManager.startController(WildernessController())
    }

    //map portal types to walk on top of
    Stream.of(28779, 28888, 29537, 23095).forEach { id -> setObjectRouteType(id, GameObject.RouteType.WALK_ONTO) }

    onObjectClick(28779, 28888, 29537, 23095) { e ->
        val portal = PortalPair.forTile(e.getObject().tile) ?: return@onObjectClick
        portal.travel(e.player, e.getObject())
    }
}

enum class PortalPair(val tile1: Tile, val tile2: Tile, private val surokLocked: Boolean = false) {
    _1(Tile.of(3254, 5451, 0), Tile.of(3250, 5448, 0)),
    _2(Tile.of(3241, 5445, 0), Tile.of(3233, 5445, 0)),
    _3(Tile.of(3259, 5446, 0), Tile.of(3265, 5491, 0), true),
    _4(Tile.of(3299, 5484, 0), Tile.of(3303, 5477, 0)),
    _5(Tile.of(3286, 5470, 0), Tile.of(3285, 5474, 0)),
    _6(Tile.of(3290, 5463, 0), Tile.of(3302, 5469, 0)),
    _7(Tile.of(3296, 5455, 0), Tile.of(3299, 5450, 0)),
    _8(Tile.of(3280, 5501, 0), Tile.of(3285, 5508, 0)),
    _9(Tile.of(3300, 5514, 0), Tile.of(3297, 5510, 0)),
    _10(Tile.of(3289, 5533, 0), Tile.of(3288, 5536, 0)),
    _11(Tile.of(3285, 5527, 0), Tile.of(3282, 5531, 0)),
    _12(Tile.of(3325, 5518, 0), Tile.of(3323, 5531, 0)),
    _13(Tile.of(3299, 5533, 0), Tile.of(3297, 5536, 0)),
    _14(Tile.of(3321, 5554, 0), Tile.of(3315, 5552, 0)),
    _15(Tile.of(3291, 5555, 0), Tile.of(3285, 5556, 0)),
    _16(Tile.of(3266, 5552, 0), Tile.of(3262, 5552, 0)),
    _17(Tile.of(3256, 5561, 0), Tile.of(3253, 5561, 0)),
    _18(Tile.of(3249, 5546, 0), Tile.of(3252, 5543, 0)),
    _19(Tile.of(3261, 5536, 0), Tile.of(3268, 5534, 0)),
    _20(Tile.of(3243, 5526, 0), Tile.of(3241, 5529, 0)),
    _21(Tile.of(3230, 5547, 0), Tile.of(3226, 5553, 0)),
    _22(Tile.of(3206, 5553, 0), Tile.of(3204, 5546, 0)),
    _23(Tile.of(3211, 5533, 0), Tile.of(3214, 5533, 0)),
    _24(Tile.of(3208, 5527, 0), Tile.of(3211, 5523, 0)),
    _25(Tile.of(3201, 5531, 0), Tile.of(3197, 5529, 0), true),
    _26(Tile.of(3202, 5515, 0), Tile.of(3196, 5512, 0), true),
    _27(Tile.of(3190, 5515, 0), Tile.of(3190, 5519, 0)),
    _28(Tile.of(3185, 5518, 0), Tile.of(3181, 5517, 0)),
    _29(Tile.of(3187, 5531, 0), Tile.of(3182, 5530, 0)),
    _30(Tile.of(3169, 5510, 0), Tile.of(3159, 5501, 0)),
    _31(Tile.of(3165, 5515, 0), Tile.of(3173, 5530, 0)),
    _32(Tile.of(3156, 5523, 0), Tile.of(3152, 5520, 0)),
    _33(Tile.of(3148, 5533, 0), Tile.of(3153, 5537, 0)),
    _34(Tile.of(3143, 5535, 0), Tile.of(3147, 5541, 0)),
    _35(Tile.of(3168, 5541, 0), Tile.of(3171, 5542, 0)),
    _36(Tile.of(3190, 5549, 0), Tile.of(3190, 5554, 0)),
    _37(Tile.of(3180, 5557, 0), Tile.of(3174, 5558, 0)),
    _38(Tile.of(3162, 5557, 0), Tile.of(3158, 5561, 0)),
    _39(Tile.of(3166, 5553, 0), Tile.of(3162, 5545, 0)),
    _40(Tile.of(3115, 5528, 0), Tile.of(3142, 5545, 0)),
    _41(Tile.of(3260, 5491, 0), Tile.of(3266, 5446, 0), true),
    _42(Tile.of(3241, 5469, 0), Tile.of(3233, 5470, 0)),
    _43(Tile.of(3235, 5457, 0), Tile.of(3229, 5454, 0)),
    _44(Tile.of(3280, 5460, 0), Tile.of(3273, 5460, 0)),
    _45(Tile.of(3283, 5448, 0), Tile.of(3287, 5448, 0)),
    _46(Tile.of(3244, 5495, 0), Tile.of(3239, 5498, 0)),
    _47(Tile.of(3232, 5501, 0), Tile.of(3238, 5507, 0)),
    _48(Tile.of(3218, 5497, 0), Tile.of(3222, 5488, 0)),
    _49(Tile.of(3218, 5478, 0), Tile.of(3215, 5475, 0)),
    _50(Tile.of(3224, 5479, 0), Tile.of(3222, 5474, 0)),
    _51(Tile.of(3208, 5471, 0), Tile.of(3210, 5477, 0)),
    _52(Tile.of(3214, 5456, 0), Tile.of(3212, 5452, 0)),
    _53(Tile.of(3204, 5445, 0), Tile.of(3197, 5448, 0), true),
    _54(Tile.of(3189, 5444, 0), Tile.of(3187, 5460, 0)),
    _55(Tile.of(3192, 5472, 0), Tile.of(3186, 5472, 0)),
    _56(Tile.of(3185, 5478, 0), Tile.of(3191, 5482, 0)),
    _57(Tile.of(3171, 5473, 0), Tile.of(3167, 5471, 0)),
    _58(Tile.of(3171, 5478, 0), Tile.of(3167, 5478, 0)),
    _59(Tile.of(3168, 5456, 0), Tile.of(3178, 5460, 0)),
    _60(Tile.of(3191, 5495, 0), Tile.of(3194, 5490, 0)),
    _61(Tile.of(3141, 5480, 0), Tile.of(3142, 5489, 0)),
    _62(Tile.of(3142, 5462, 0), Tile.of(3154, 5462, 0)),
    _63(Tile.of(3143, 5443, 0), Tile.of(3155, 5449, 0)),
    _64(Tile.of(3307, 5496, 0), Tile.of(3317, 5496, 0)),
    _65(Tile.of(3318, 5481, 0), Tile.of(3322, 5480, 0)),
    TUNNELS_OF_CHAOS(Tile.of(3326, 5469, 0), Tile.of(3159, 5208, 0)),
    CHAOS_ALTAR(Tile.of(3152, 5233, 0), Tile.of(2282, 4837, 0)),
    BORK(Tile.of(3142, 5545, 0), Tile.of(3115, 5528, 0));

    fun travel(player: Player, fromPortal: GameObject) {
        if (surokLocked && !player.isMiniquestComplete(Miniquest.HUNT_FOR_SUROK, "to travel through this portal.", true)) return
        if (this == _16 && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 2) {
            player.cutscene {
                //Prep cutscene
                endTile = Tile.of(3262, 5552, 0)
                fadeInAndWait()
                player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 3)
                dynamicRegion(if (tile1.tileHash == fromPortal.tile.tileHash) tile2 else tile1, 405, 690, 5, 5)
                val surok = npcCreate(7002, 13, 16, 0)
                val fireGiant1 = npcCreate(110, 10, 16, 0)
                val fireGiant2 = npcCreate(110, 15, 17, 0)
                entityTeleTo(player, 17, 16)
                fireGiant1.faceTile(tileFromLocal(13, 16))
                fireGiant2.faceTile(tileFromLocal(13, 16))
                camPos(13, 6, 11338)
                camLook(13, 16, 0)
                camPos(13, 6, 3722, 0, 5)
                fadeOutAndWait()

                //Play the cutscene
                surok.forceTalk("Pathetic creatures.")
                surok.sync(6098, 1009)
                surok.faceTile(tileFromLocal(10, 16))
                wait(1)
                surok.anim(-1)
                sendProjectile(surok, fireGiant1, 1010, 5 to 15, 0, 8, 10) {
                    fireGiant1.applyHit(Hit.magic(surok, fireGiant1.hitpoints))
                    fireGiant1.spotAnim(1011)
                }
                wait(5)
                fireGiant2.forceTalk("Grraaaah!")
                surok.sync(6098, 1009)
                surok.faceTile(tileFromLocal(15, 17))
                wait(1)
                surok.forceTalk("Feel the power of Zamorak!")
                surok.anim(-1)
                sendProjectile(surok, fireGiant2, 1010, 5 to 15, 0, 8, 10) {
                    fireGiant2.applyHit(Hit.magic(surok, fireGiant2.hitpoints))
                    fireGiant2.spotAnim(1011)
                }
                wait(5)
                entityWalkTo(surok, 18, 8)
                wait(5)

                fadeInAndWait()
                returnPlayerFromInstance()
                fadeOutAndWait()
            }
        }
        if (this == _60 && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 3) {
            player.cutscene {
                endTile = Tile.of(3191, 5495, 0)
                fadeInAndWait()
                player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 4)
                dynamicRegion(if (tile1.tileHash == fromPortal.tile.tileHash) tile2 else tile1, 396, 684, 5, 5)
                val surok = npcCreate(7002, 15, 20, 0)
                entityTeleTo(player, 23, 23)
                surok.faceTile(tileFromLocal(19, 22))
                camLook(18, 22, 0)
                camPos(25, 5, 12000)
                camPos(24, 28, 5125, 0, 20)
                fadeOutAndWait()
                entityWalkTo(player, 19, 22)
                wait(3)
                player.faceEntityTile(surok)
                dialogue {
                    player(HeadE.FRUSTRATED, "Surok! Give yourself up. You can't get away.")
                    npc(7002, HeadE.FRUSTRATED, "Fool! You are in my lair now. These tunnels belong to my people: the Dagon'hai. Here is where you meet your doom.")
                    player(HeadE.FRUSTRATED, "If you do not come peacefully, I will have to arrest you by force.")
                    npc(7002, HeadE.FRUSTRATED, "So be it. Let's see how you deal with some of my pets...")
                }
                waitForDialogue()
                surok.sync(6098, 1009)
                wait(1)
                surok.anim(-1)
                arrayOf(npcCreate(95, 17, 20, 0), npcCreate(95, 15, 21, 0)).forEach { wolf ->
                    wolf.faceTile(tileFromLocal(19, 22))
                    wolf.sync(8298, 1315)
                }
                wait(3)
                surok.forceTalk("Kill " + player.getPronoun("him!", "her!"))
                player.forceTalk("Aargh!")
                wait(3)
                fadeInAndWait()
                returnPlayerFromInstance()
                wait(1)
                OwnedNPC(player, 95, Tile.of(3188, 5496, 0), false).setCombatTarget(player)
                OwnedNPC(player, 95, Tile.of(3188, 5493, 0), false).setCombatTarget(player)
                fadeOutAndWait()
                player.resetReceivedHits()
            }
        }
        if (this == BORK) {
            if (player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) < 4) {
                player.sendMessage("The portal is unresponsive.")
                return
            }
            if (player.getDailyB("borkKilled")) {
                player.sendMessage("You have already killed Bork today.")
                return
            }
            val entering = tile1.tileHash == fromPortal.tile.tileHash
            if (entering) player.controllerManager.startController(BorkController(player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 4))
            return
        }
        if (this == CHAOS_ALTAR && !RunecraftingAltar.checkItems(player, RunecraftingAltar.Ruins.CHAOS)) {
            player.sendMessage("The portal doesn't respond without a tiara or talisman. This must be the Chaos Altar entrance.")
            return
        }
        if (this == TUNNELS_OF_CHAOS && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) < 2) {
            player.sendMessage("The portal is unresponsive.")
            return
        }
        player.spotAnim(110, 10, 96)
        player.useStairs(-1, if (tile1.tileHash == fromPortal.tile.tileHash) tile2 else tile1, 2, 3)
    }

    companion object {
        private val MAPPING: MutableMap<Int, PortalPair> = Int2ObjectOpenHashMap()

        init {
            for (p in entries) {
                MAPPING[p.tile1.tileHash] = p
                MAPPING[p.tile2.tileHash] = p
            }
        }

        fun forTile(tile: Tile): PortalPair? {
            return MAPPING[tile.tileHash]
        }
    }
}

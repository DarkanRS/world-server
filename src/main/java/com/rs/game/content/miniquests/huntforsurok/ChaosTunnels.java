package com.rs.game.content.miniquests.huntforsurok;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.World;
import com.rs.game.content.miniquests.huntforsurok.bork.BorkController;
import com.rs.game.content.skills.runecrafting.RunecraftingAltar;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;
import java.util.stream.Stream;

@PluginEventHandler
public class ChaosTunnels {

    public static NPCClickHandler aegis = new NPCClickHandler(new Object[] { 5840 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addNPC(5840, HeadE.CHEERFUL, "Hello. Can I help you?")
            .addNextIf(() -> e.getPlayer().getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 2, new Dialogue()
                    .addPlayer(HeadE.FRUSTRATED, "Where did he go?")
                    .addNPC(5840, HeadE.CONFUSED, "Where did who go?")
                    .addPlayer(HeadE.FRUSTRATED, "Surok! I saw him come in this way!")
                    .addNPC(5840, HeadE.AMAZED, "Oh, Lord Magis? Why yes; he went through the portal here.")
                    .addPlayer(HeadE.CHEERFUL, "Right! Thanks!")
                    .addNPC(5840, HeadE.CHEERFUL, "Was there anything else?"))
            .addPlayer(HeadE.CONFUSED, "Yes. What is this place?")
            .addNPC(5840, HeadE.CHEERFUL, "These are the Dagon'hai tunnels. They radiate with the energy of chaos magic. At the far end of the tunnel, you will find a portal to the Chaos Altar itself, where chaos runes are crafted.")
            .addOptions(ops -> {
                ops.add("Why is the tunnel so short?")
                        .addPlayer(HeadE.CONFUSED, "Why is the tunnel so short?")
                        .addNPC(5840, HeadE.CHEERFUL, "The nature of chaos magic itself is mysterious and hard to understand.")
                        .addPlayer(HeadE.CONFUSED, "What do you mean?")
                        .addNPC(5840, HeadE.CALM_TALK, "The tunnel is here, and yet it is not here. We also are here, where we should be, yet we are somewhere else. And so, the tunnel goes from one place to another, yet touches neither.")
                        .addPlayer(HeadE.CONFUSED, "I really don't understand.")
                        .addNPC(5840, HeadE.CHEERFUL, "I told you you wouldn't. The best way to think of it is that the tunnel is like a gateway that allows you to travel from one place in the world to another in a more safe and convenient way.")
                        .addPlayer(HeadE.CHEERFUL, "Ok, I think I see. So it is like a magical alternate route?")
                        .addNPC(5840, HeadE.CHEERFUL, "Exactly! Well done! And at the end, there is a portal which will transport you to the Chaos Altar.")
                        .addPlayer(HeadE.CHEERFUL, "Oh, ok.");

                ops.add("Why are there only three of you?")
                        .addPlayer(HeadE.CONFUSED, "Why are there only three of you?")
                        .addNPC(5840, HeadE.CHEERFUL, "Our order is a secret one and those of us that remain and survive through the ages choose to hide ourselves away.")
                        .addPlayer(HeadE.CONFUSED, "Where are the rest of you now?")
                        .addNPC(5840, HeadE.CHEERFUL, "We have built or found many tunnels such as these. This is not the only one. Rest assured, there are many more of us around.");

                ops.add("Is the Wilderness above us?")
                        .addPlayer(HeadE.CONFUSED, "Is the Wilderness above us?")
                        .addNPC(5840, HeadE.CHEERFUL, "Yes, I can understand you would expect it to be since the tunnel leads to the Chaos Altar. And yes, the tunnel does not enter the Wilderness, no.")
                        .addNPC(5840, HeadE.CHEERFUL, "Rest assured, you are safe here and should not come to harm.");

                ops.add("You seem quite nice for a Zamorakian.")
                        .addPlayer(HeadE.CHEERFUL, "You seem quite nice for a Zamorakian.")
                        .addNPC(5840, HeadE.CALM_TALK, "History is written by the strong and the influential.")
                        .addPlayer(HeadE.CONFUSED, "What does that mean?")
                        .addNPC(5840, HeadE.CHEERFUL, "It means we cannot always believe what we are told to believe. You would do well to remember that. Especially when thinking about how long it takes to cremate bodies.")
                        .addPlayer(HeadE.CHEERFUL, "I will! Thank you!");

                ops.add("Thanks for your time.")
                        .addPlayer(HeadE.CHEERFUL, "Thanks for your time.");
            })));

    public static NPCClickHandler silasDahcsnu = new NPCClickHandler(new Object[] { 5841 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addPlayer(HeadE.CHEERFUL, "Hello there.")
            .addNPC(5841, HeadE.FRUSTRATED, "Can't you see that I'm busy here?")
            .addPlayer(HeadE.AMAZED, "Oh. Sorry, you don't look very busy.")
            .addNPC(5841, HeadE.FRUSTRATED, "Don't look busy? I've got a lot of important work to do here.")
            .addPlayer(HeadE.CONFUSED, "Really? What do you do?")
            .addNPC(5841, HeadE.FRUSTRATED, "That doesn't concern you. What are you doing here anyway?")
            .addOptions(ops -> {
                ops.add("I'm not actually sure.")
                        .addPlayer(HeadE.SAD_MILD, "I'm not actually sure.")
                        .addNPC(5841, HeadE.FRUSTRATED, "Hmph! Well I can tell you what you ARE doing.")
                        .addPlayer(HeadE.CONFUSED, "What's that?")
                        .addNPC(5841, HeadE.FRUSTRATED, "Wasting my time! Now go away! If you must speak to someone, speak to one of the other monks around here!");

                ops.add("I'm on an important quest!")
                        .addPlayer(HeadE.CHEERFUL, "I'm on an important quest!")
                        .addNPC(5841, HeadE.CALM_TALK, "Really...")
                        .addPlayer(HeadE.CHEERFUL, "Yes! Do you want to know what my quest is?")
                        .addNPC(5841, HeadE.CALM_TALK, "Let me see...you helped out a trader who in turn asked you to deliver something for him, and then you had to journey to some palace to see someone and they said thanks by sending you somewhere else.")
                        .addPlayer(HeadE.CONFUSED, "Uhh...")
                        .addNPC(5841, HeadE.CALM_TALK, "So, now you've ended up down here, bothering me?")
                        .addPlayer(HeadE.CONFUSED, "Uh, yes, that's about right.")
                        .addNPC(5841, HeadE.CALM_TALK, "Lucky me...")
                        .addPlayer(HeadE.CONFUSED, "Sorry about that. I'll leave you alone, then.");

                ops.add("None of your business!")
                        .addPlayer(HeadE.FRUSTRATED, "None of your business!")
                        .addNPC(5841, HeadE.FRUSTRATED, "Of course it isn't, but you were the one who came here bothering me! So, unless you want something, be off with you!");

                ops.add("I am looking for something.")
                        .addPlayer(HeadE.CONFUSED, "I am looking for something.")
                        .addNPC(5841, HeadE.CONFUSED, "What are you looking for exactly?")
                        .addPlayer(HeadE.CONFUSED, "The Chaos Altar, I think.")
                        .addNPC(5841, HeadE.FRUSTRATED, "Hmph! Well, it's just a bit further down. Now if you don't mind, I'm very busy here!");

                ops.add("I'm the leader of the Dagon'hai. Bow before me!")
                        .addPlayer(HeadE.FRUSTRATED, "I'm the leader of the Dagon'hai. Bow before me!")
                        .addNPC(5841, HeadE.CONFUSED, "You? Leader of the Dagon'hai? Hah! I've never heard of such rubbish! You call yourself a mage?")
                        .addOptions(mage -> {
                            mage.add("I'm a very powerful mage!")
                                    .addPlayer(HeadE.FRUSTRATED, "I'm a very powerful mage!")
                                    .addNPC(5841, HeadE.AMAZED, "Really?")
                                    .addPlayer(HeadE.FRUSTRATED, "Yes! Be afraid! Be very afraid! Ha ha ha!")
                                    .addNPC(5841, HeadE.CALM_TALK, "I see. Well then, let's see how you handle this!")
                                    .addPlayer(HeadE.AMAZED, "What...?!?!")
                                    //TODO deflect spell based on magic level?
                                    //If fail, player jumps and says they can't stop jumping
                                    //then CHEERFUL, "Hah! I knew you were no mage! Let that be a lesson to you! Now, please leave.
                                    .addNPC(5841, HeadE.AMAZED, "So! You are a good mage! You deflected my spell.")
                                    .addPlayer(HeadE.CALM_TALK, "Hah! Yes. I told you so! Now, I have work to be done.")
                                    .addNPC(5841, HeadE.CALM_TALK, "Of course. Goodbye!");

                            mage.add("No, you're right. I'm not a mage.")
                                    .addPlayer(HeadE.SAD_MILD, "No, you're right. I'm not a mage.")
                                    .addNPC(5841, HeadE.FRUSTRATED, "I didn't think so! Now go away. As I said, I'm very busy!");
                        });
            })));

    public static NPCClickHandler mishkalDorn = new NPCClickHandler(new Object[] { 5839 }, e -> {
        e.getPlayer().startConversation(new Dialogue()
                .addNPC(5839, HeadE.CALM_TALK, "You are excused. And you are welcome.")
                .addPlayer(HeadE.CONFUSED, "Excuse me...er...thanks.")
                .addNPC(5839, HeadE.CALM_TALK, "We are the Order of the Dagon'hai.")
                .addPlayer(HeadE.CONFUSED, "Who are you?")
                .addNPC(5839, HeadE.CALM_TALK, "Through my magic, I can see a short way into the future.")
                .addPlayer(HeadE.CONFUSED, "How do you seem to know what I'm going to say? ...Er...oh.")
                .addNPC(5839, HeadE.CALM_TALK, "These are the Tunnels of Chaos.")
                .addPlayer(HeadE.CONFUSED, "What is...uh...aha! I'm not going to ask that. So you got it wrong!")
                .addNPC(5839, HeadE.CALM_TALK, "Indeed. You are very clever.")
                .addPlayer(HeadE.CHEERFUL, "So I won!")
                .addNPC(5839, HeadE.CALM_TALK, "Yes.")
                .addPlayer(HeadE.CONFUSED, "So, what is this place?")
                .addPlayer(HeadE.FRUSTRATED, "I mean...Argh! How do you do that?")
                .addNPC(5839, HeadE.CALM_TALK, "I can tell that your mind is not suited to the paradoxicalities of precognition.")
                .addPlayer(HeadE.CONFUSED, "Why what does what now?")
                .addNPC(5839, HeadE.CALM_TALK, "You get confused very easily.")
                .addPlayer(HeadE.CALM_TALK, "I knew that.")
                .addNPC(5839, HeadE.CALM_TALK, "Of course you did. Speak to one of my order here. They will be able to explain in a manner more suited to your understanding.")
                .addNPC(5839, HeadE.CALM_TALK, "You are welcome, " + e.getPlayer().getDisplayName() + ". There is a bed around here if you wish.")
                .addPlayer(HeadE.CHEERFUL, "Thanks. My name's " + e.getPlayer().getDisplayName() + ", by the w...uh...okay, I think I need to lie down."));
    });

    public static ObjectClickHandler handleRifts = new ObjectClickHandler(new Object[] { 65203 }, e -> {
            if (e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
                e.getPlayer().sendMessage("You cannot enter the rift while you're under attack.");
                return;
            }
            if (e.objectAt(3058, 3550))
                e.getPlayer().setNextTile(e.getPlayer().transform(125, 1920, 0));
            if (e.objectAt(3118, 3570))
                e.getPlayer().setNextTile(e.getPlayer().transform(130, 1920, 0));
            if (e.objectAt(3129, 3587))
                e.getPlayer().setNextTile(e.getPlayer().transform(105, 1972, 0));
            if (e.objectAt(3164, 3561))
                e.getPlayer().setNextTile(e.getPlayer().transform(128, 1918, 0));
            if (e.objectAt(3176, 3585))
                e.getPlayer().setNextTile(Tile.of(3290, 5539, 0));
    });

    public static ObjectClickHandler handleExitRopes = new ObjectClickHandler(new Object[] { 28782 }, e -> {
        if (e.objectAt(3183, 5470))
            e.getPlayer().setNextTile(e.getPlayer().transform(-125, -1920, 0));
        if (e.objectAt(3248, 5490))
            e.getPlayer().setNextTile(e.getPlayer().transform(-130, -1920, 0));
        if (e.objectAt(3234, 5559))
            e.getPlayer().setNextTile(e.getPlayer().transform(-105, -1972, 0));
        if (e.objectAt(3292, 5479))
            e.getPlayer().setNextTile(e.getPlayer().transform(-128, -1918, 0));
        if (e.objectAt(3291, 5538))
            e.getPlayer().setNextTile(e.getPlayer().transform(-115, -1953, 0));
        e.getPlayer().getControllerManager().startController(new WildernessController());
    });

    @ServerStartupEvent
    public static void portalRouteTypeWalkOn() {
        Stream.of(28779, 28888, 29537, 23095).forEach(id -> World.setObjectRouteType(id, GameObject.RouteType.WALK_ONTO));
    }

    public static ObjectClickHandler handleChaosTunnelsPortals = new ObjectClickHandler(new Object[] { 28779, 28888, 29537, 23095 }, e -> {
        PortalPair portal = PortalPair.forTile(e.getObject().getTile());
        if (portal == null)
            return;
        portal.travel(e.getPlayer(), e.getObject());
    });

    public enum PortalPair {
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
        private static Map<Integer, PortalPair> MAPPING = new Int2ObjectOpenHashMap<>();

        static {
            for (PortalPair p : PortalPair.values()) {
                MAPPING.put(p.tile1.getTileHash(), p);
                MAPPING.put(p.tile2.getTileHash(), p);
            }
        }

        public final Tile tile1;
        public final Tile tile2;
        public final boolean surokLocked;

        PortalPair(Tile tile1, Tile tile2, boolean surokLocked) {
            this.tile1 = tile1;
            this.tile2 = tile2;
            this.surokLocked = surokLocked;
        }

        PortalPair(Tile tile1, Tile tile2) {
            this(tile1, tile2, false);
        }

        public static PortalPair forTile(Tile tile) {
            return MAPPING.get(tile.getTileHash());
        }

        public void travel(Player player, GameObject fromPortal) {
            if (surokLocked && !player.isMiniquestComplete(Miniquest.HUNT_FOR_SUROK, "to travel through this portal."))
                return;
            if (this == _16 && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 2) {
                player.playCutscene(cs -> {
                    cs.setEndTile(Tile.of(3262, 5552, 0));
                    cs.fadeIn(5);
                    cs.action(() -> player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 3));
                    cs.dynamicRegion(tile1.getTileHash() == fromPortal.getTile().getTileHash() ? tile2 : tile1, 405, 690, 5, 5);
                    cs.npcCreate("surok", 7002, 13, 16, 0);
                    cs.npcCreate("firegiant1", 110, 10, 16, 0, n -> n.setRandomWalk(false));
                    cs.npcCreate("firegiant2", 110, 15, 17, 0, n -> n.setRandomWalk(false));
                    cs.playerMove(17, 16, Entity.MoveType.TELE);
                    cs.npcFaceTile("firegiant1", 13, 16);
                    cs.npcFaceTile("firegiant2", 13, 16);
                    cs.camPos(13, 6, 11338);
                    cs.camLook(13, 16, 0);
                    cs.camPos(13, 6, 3722, 0, 5);
                    cs.fadeOut(5);
                    cs.npcTalk("surok", "Pathetic creatures.");
                    cs.npcSync("surok", 6098, 1009);
                    cs.npcFaceTile("surok", 10, 16);
                    cs.delay(1);
                    cs.npcAnim("surok", -1);
                    cs.action(() -> World.sendProjectile(cs.getNPC("surok"), cs.getNPC("firegiant1"), 1010, 5, 15, 0, 0.8, 10, 10, proj -> {
                        cs.getNPC("firegiant1").applyHit(Hit.magic(cs.getNPC("surok"), cs.getNPC("firegiant1").getHitpoints()));
                        cs.getNPC("firegiant1").spotAnim(1011);
                    }));
                    cs.delay(5);
                    cs.npcTalk("firegiant2", "Grraaaah!");
                    cs.npcSync("surok", 6098, 1009);
                    cs.npcFaceTile("surok", 15, 17);
                    cs.delay(1);
                    cs.npcTalk("surok", "Feel the power of Zamorak!");
                    cs.npcAnim("surok", -1);
                    cs.action(() -> World.sendProjectile(cs.getNPC("surok"), cs.getNPC("firegiant2"), 1010, 5, 15, 0, 0.8, 10, 10, proj -> {
                        cs.getNPC("firegiant2").applyHit(Hit.magic(cs.getNPC("surok"), cs.getNPC("firegiant2").getHitpoints()));
                        cs.getNPC("firegiant2").spotAnim(1011);
                    }));
                    cs.delay(5);
                    cs.npcWalk("surok", 18, 8);
                    cs.delay(5);
                    cs.fadeIn(5);
                    cs.returnPlayerFromInstance();
                    cs.fadeOut(5);
                });
            }
            if (this == _60 && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 3) {
                player.playCutscene(cs -> {
                    cs.setEndTile(Tile.of(3191, 5495, 0));
                    cs.fadeIn(5);
                    cs.action(() -> player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 4));
                    cs.dynamicRegion(tile1.getTileHash() == fromPortal.getTile().getTileHash() ? tile2 : tile1, 396, 684, 5, 5);
                    cs.npcCreate("surok", 7002, 15, 20, 0);
                    cs.playerMove(23, 23, Entity.MoveType.TELE);
                    cs.npcFaceTile("surok", 19, 22);
                    cs.camLook(18, 22, 0);
                    cs.camPos(25, 5, 12000);
                    cs.camPos(24, 28, 5125, 0, 20);
                    cs.fadeOut(5);
                    cs.playerMove(19, 22, Entity.MoveType.WALK);
                    cs.delay(3);
                    cs.playerFaceEntity("surok");
                    cs.dialogue(new Dialogue()
                            .addPlayer(HeadE.FRUSTRATED, "Surok! Give yourself up. You can't get away.")
                            .addNPC(7002, HeadE.FRUSTRATED, "Fool! You are in my lair now. These tunnels belong to my people: the Dagon'hai. Here is where you meet your doom.")
                            .addPlayer(HeadE.FRUSTRATED, "If you do not come peacefully, I will have to arrest you by force.")
                            .addNPC(7002, HeadE.FRUSTRATED, "So be it. Let's see how you deal with some of my pets..."), true);
                    cs.npcSync("surok", 6098, 1009);
                    cs.delay(1);
                    cs.npcAnim("surok", -1);
                    cs.npcCreate("wolf1", 95, 17, 20, 0);
                    cs.npcCreate("wolf2", 95, 15, 21, 0);
                    cs.npcFaceTile("wolf1", 19, 22);
                    cs.npcFaceTile("wolf2", 19, 22);
                    cs.npcSync("wolf1", 8298, 1315);
                    cs.npcSync("wolf2", 8298, 1315);
                    cs.delay(3);
                    cs.npcTalk("surok", "Kill " + player.getPronoun("him!", "her!"));
                    cs.playerTalk("Aargh!");
                    cs.delay(3);
                    cs.fadeIn(5);
                    cs.returnPlayerFromInstance();
                    cs.delay(0);
                    cs.action(() -> {
                        new OwnedNPC(player, 95, Tile.of(3188, 5496, 0), false).setTarget(player);
                        new OwnedNPC(player, 95, Tile.of(3188, 5493, 0), false).setTarget(player);
                    });
                    cs.fadeOut(5);
                    cs.action(() -> player.resetReceivedHits());
                });
            }
            if (this == BORK) {
                player.setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 4);
                if (player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) < 4) {
                    player.sendMessage("The portal is unresponsive.");
                    return;
                }
                if (player.getDailyB("borkKilled")) {
                    player.sendMessage("You have already killed Bork today.");
                    return;
                }
                boolean entering = tile1.getTileHash() == fromPortal.getTile().getTileHash();
                if (entering)
                    player.getControllerManager().startController(new BorkController(player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 4));
                return;
            }
            if (this == CHAOS_ALTAR && !RunecraftingAltar.checkItems(player, RunecraftingAltar.Ruins.CHAOS)) {
                player.sendMessage("The portal doesn't respond without a tiara or talisman. This must be the Chaos Altar entrance.");
                return;
            }
            if (this == TUNNELS_OF_CHAOS && player.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) < 2) {
                player.sendMessage("The portal is unresponsive.");
                return;
            }
            player.setNextSpotAnim(new SpotAnim(110, 10, 96));
            player.useStairs(-1, tile1.getTileHash() == fromPortal.getTile().getTileHash() ? tile2 : tile1, 2, 3);
        }
    }
}

package com.rs.game.content.dnds.shootingstar;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.gson.internal.LinkedTreeMap;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.mining.Mining;
import com.rs.game.content.skills.mining.RockType;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;
import com.rs.utils.shop.ShopsHandler;

import java.util.*;

@PluginEventHandler
public class ShootingStars {

    public enum Location {
        AL_KHARID(Tile.of(3286, 3197, 0), "Al Kharid"),
        LUMBRIDGE_SWAMP(Tile.of(3217, 3189, 0), "Lumbridge Swamp"),
        DRAYNOR(Tile.of(3076, 3278, 0), "Draynor Village"),
        RIMMINGTON(Tile.of(2972, 3249, 0), "Rimmington"),
        FALADOR(Tile.of(2966, 3379, 0), "Falador"),
        DWARVEN_MINE(Tile.of(3017, 3445, 0), "Ice Mountain"),
        EDGEVILLE(Tile.of(3045, 3515, 0), "Edgeville"),
        CHAMPIONS_GUILD(Tile.of(3167, 3362, 0), "the Champion's Guild"),
        GRAND_EXCHANGE(Tile.of(3136, 3456, 0), "the Grand Exchange"),
        BARBARIAN_VILLAGE(Tile.of(3064, 3434, 0), "Barbarian Village"),
        CATHERBY(Tile.of(2783, 3465, 0), "Catherby"),
        RANGING_GUILD(Tile.of(2688, 3443, 0), "the Ranging Guild"),
        FISHING_GUILD(Tile.of(2594, 3386, 0), "the Fishing Guild"),
        CLOCK_TOWER(Tile.of(2580, 3244, 0), "the clocktower"),
        FIGHT_ARENA(Tile.of(2563, 3202, 0), "the Fight Arena"),
        YANILLE(Tile.of(2533, 3079, 0), "Yanille"),
        CASTLE_WARS(Tile.of(2456, 3104, 0), "Castle Wars"),
        MOBILISING_ARMIES(Tile.of(2408, 2853, 0), "Mobilising Armies"),
        PHOENIX_LAIR(Tile.of(2285, 3625, 0), "the Phoenix Lair"),
        BARBARIAN_ASSAULT(Tile.of(2542, 3559, 0), "Barbarian Assault"),
        LEGENDS_GUILD(Tile.of(2742, 3390, 0), "the Legend's Guild"),
        KARAMJA_GOLD_MINE(Tile.of(2736, 3222, 0), "the Brimhaven gold mine"),
        TAI_BWO_WANNAI_JUNGLE(Tile.of(2835, 3047, 0), "Tai Bwo Wannai jungle"),
        MUDSKIPPER_POINT(Tile.of(2996, 3124, 0), "Mudskipper Point"),
        KARAMJA_VOLCANO(Tile.of(2824, 3170, 0), "the Karamja volcano"),
        VARROCK_SOUTHEAST_MINE(Tile.of(3290, 3352, 0), "the Varrock south-east mine"),
        AL_KHARID_MINE(Tile.of(3299, 3276, 0), "the Al-Kharid mine"),
        NARDAH(Tile.of(3433, 2915, 0), "Nardah"),
        POLLNIVNEACH(Tile.of(3348, 3014, 0), "Pollnivneach"),
        FREMENNIK_SLAYER_DUNGEON(Tile.of(2773, 3595, 0), "the Fremennik Slayer Dungeon"),
        RELLEKKA(Tile.of(2687, 3691, 0), "Rellekka"),
        PARTY_ROOM(Tile.of(3026, 3365, 0), "the Party Room"),
        LUMBRIDGE_CASTLE(Tile.of(3209, 3203, 0), "Lumbridge Castle"),

        ;

        public final Tile tile;
        public final String description;

        Location(Tile tile, String description) {
            this.tile = tile;
            this.description = description;
        }
    }

    private static Star currentStar = null;
    private static final List<Location> LOCATIONS = new ArrayList<>(Arrays.stream(Location.values()).toList());
    private static PeekingIterator<Location> locIterator = Iterators.peekingIterator(LOCATIONS.iterator());

    @ServerStartupEvent
    public static void schedule() {
        Collections.shuffle(LOCATIONS);
        locIterator = Iterators.peekingIterator(LOCATIONS.iterator());
        WorldTasks.scheduleNthHourly(2, ShootingStars::spawnStar);
    }

    public static void spawnStar() {
        if (currentStar != null)
            currentStar.destroy();
        currentStar = new Star(Utils.random(0, 9), nextLocation());
    }

    private static Location nextLocation() {
        if (!locIterator.hasNext())
            locIterator = Iterators.peekingIterator(LOCATIONS.iterator());
        return locIterator.next();
    }

    public static void viewTelescope(Player player) {
        player.getInterfaceManager().sendInterface(782);
        player.startConversation(new Dialogue());
        player.simpleDialogue((currentStar != null ? "A star recently crashed near " + currentStar.location.description + "! " : "") + "The next star looks like it's going to land near " + (!locIterator.hasNext() ? LOCATIONS.getFirst() : locIterator.peek()).description + ".");
    }

    public static void addDiscoveredStar(Star star, String displayName) {
        Map<String, String> discoveries = World.getData().getAttribs().getO("shootingStarDiscoveries");
        if (discoveries == null)
            discoveries = new LinkedTreeMap<>();
        discoveries.put(Long.toString(star.landingTime), displayName);
        Map<String, String> sortedDiscoveries = new LinkedTreeMap<>();
        discoveries.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(Long.parseLong(entry2.getKey()), Long.parseLong(entry1.getKey())))
                .limit(5)
                .forEachOrdered(entry -> sortedDiscoveries.put(entry.getKey(), entry.getValue()));
        World.getData().getAttribs().setO("shootingStarDiscoveries", sortedDiscoveries);
    }

    public static ObjectClickHandler handleNoticeboard = new ObjectClickHandler(new Object[] { 38669 }, e -> {
        Map<String, String> discoveries = World.getData().getAttribs().getO("shootingStarDiscoveries");
        if (discoveries == null)
            discoveries = new HashMap<>();
        int idx = 0;
        for (String time : discoveries.keySet()) {
            if (++idx > 5)
                continue;
            e.getPlayer().getPackets().setIFText(787, 5 + idx, "Crashed " + Utils.ticksToTime(World.getServerTicks() - Long.parseLong(time)).replace(".", "") + " ago");
            e.getPlayer().getPackets().setIFText(787, 10 + idx, discoveries.get(time));
        }
        e.getPlayer().getInterfaceManager().sendInterface(787);
    });

    public static ObjectClickHandler handleStarClick = new ObjectClickHandler(new Object[] { "Crashed star" }, e -> {
        if (!(e.getObject() instanceof Star star)) {
            e.getPlayer().sendMessage("Star is not a real star. Report this to a staff member.");
            return;
        }
        if (star.getTier() < 1 || star.getTier() > 9) {
            e.getPlayer().sendMessage("Invalid star tier " + star.getTier());
            return;
        }
        switch(e.getOption()) {
            case "Prospect" -> e.getPlayer().simpleDialogue("It's a size " + star.getTier() + " star. It looks like it has about " + star.getLife() + "% of this layer remaining.");
            case "Mine" -> {
                if (!star.discovered) {
                    star.discovered = true;
                    e.getPlayer().getSkills().addXp(Skills.MINING, e.getPlayer().getSkills().getLevelForXp(Skills.MINING) * 75);
                    e.getPlayer().simpleDialogue("Congratulations, you were the first to find this star! You receive " + Utils.formatNumber(e.getPlayer().getSkills().getLevelForXp(Skills.MINING) * 75) + " Mining XP as a reward.");
                    ShootingStars.addDiscoveredStar(star, e.getPlayer().getDisplayName());
                    return;
                }
                e.getPlayer().getActionManager().setAction(new Mining(RockType.valueOf("CRASHED_STAR_" + star.getTier()), e.getObject()));
            }
        }
    });

    @ServerStartupEvent
    public static void addLoSOverride() {
        Entity.addLOSOverride(8091);
    }

    public static NPCClickHandler handleStarSprite = new NPCClickHandler(new Object[] { 8091 }, e -> {
       e.getNPC().resetDirection();
       if (e.getPlayer().getDailyI("stardustHandedIn") < 200 && e.getPlayer().getInventory().containsItem(13727, 1)) {
           int toHandIn = e.getPlayer().getInventory().getNumberOf(13727);
           int canHandIn = 200 - e.getPlayer().getDailyI("stardustHandedIn", 0);
           if (toHandIn > canHandIn)
               toHandIn = canHandIn;
           if (toHandIn <= 0)
               return;
           int coins = (int) (50002.0 * ((double) toHandIn / 200.0));
           int cosmics = (int) (152.0 * ((double) toHandIn / 200.0));
           int astrals = (int) (52.0 * ((double) toHandIn / 200.0));
           int gold = (int) (20.0 * ((double) toHandIn / 200.0));
           int ticks = (int) (Ticks.fromMinutes(15) * ((double) toHandIn / 200.0));
           e.getPlayer().getInventory().deleteItem(13727, toHandIn);
           e.getPlayer().getInventory().addCoins(coins);
           e.getPlayer().getInventory().addItemDrop(564, cosmics);
           e.getPlayer().getInventory().addItemDrop(9075, astrals);
           e.getPlayer().getInventory().addItemDrop(445, gold);
           e.getPlayer().addEffect(Effect.SHOOTING_STAR_MINING_BUFF, ticks);
           e.getPlayer().setDailyI("stardustHandedIn", e.getPlayer().getDailyI("stardustHandedIn", 0) + toHandIn);
           e.getPlayer().startConversation(new Dialogue()
                   .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Thank you for helping me out of here.")
                   .addNPC(e.getNPCId(), HeadE.CHEERFUL, "I have rewarded you by making it so you can mine extra ore for the next " + Utils.ticksToTime(ticks))
                   .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Also, have " + cosmics +" cosmic runes, " + astrals + " astral runes, " + gold + " gold ore and " + Utils.formatNumber(coins) + " coins."));
       } else
           e.getPlayer().startConversation(new Dialogue()
                   .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, strange creature.")
                   .addOptions(ops -> {
                       ops.add("What are you? Where did you come from?")
                               .addPlayer(HeadE.CONFUSED, "What are you? Where did you come from?")
                               .addNPC(e.getNPCId(), HeadE.CHEERFUL, "I'm a star sprite! I was in my star in the sky, when it lost control and crashed into the ground. With half my star sticking in the ground, I became stuck. Fortunately, I was mined out by the kind creatures of your race.")
                               .addOptions(moreOps -> {
                                   moreOps.add("What's a star sprite?")
                                           .addPlayer(HeadE.CONFUSED, "What's a star sprite?")
                                           .addNPC(e.getNPCId(), HeadE.CHEERFUL, "We're what makes the stars in the sky shine. I made this star shine when it was in the sky.");

                                   moreOps.add("What are you going to do without your star?")
                                           .addPlayer(HeadE.CONFUSED, "What are you going to do without your star?")
                                           .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Don't worry about me. I'm sure I'll find some good rocks around here and get back up into the sky in no time.");

                                   moreOps.add("I thought stars were huge balls of burning gas.")
                                           .addPlayer(HeadE.CALM_TALK, "I thought stars were huge balls of burning gas.")
                                           .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Most of them are, but a lot of shooting stars on this plane of the multiverse are rocks with star sprites in them.");

                                   moreOps.add("Well, I'm glad you're okay.")
                                           .addPlayer(HeadE.CHEERFUL, "Well, I'm glad you're okay.")
                                           .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Thank you.");
                               });

                       ops.add("Hello, strange glowing creature.")
                               .addPlayer(HeadE.CHEERFUL, "Hello, strange glowing creature.")
                               .addNPC(e.getNPCId(), HeadE.CONFUSED, "Isn't that funny? One of the things I find odd about you is that you DON'T glow.");

                       ops.add("I'm not strange.")
                               .addPlayer(HeadE.CONFUSED, "I'm not strange.")
                               .addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hehe. If you say so.");

                       if (e.getPlayer().getInventory().containsItem(13727))
                           ops.add("Are there any more rewards I can redeem for extra dust?")
                                   .addOptions(rewards -> {
                                       rewards.add("[1000 dust per hour] Buy more star sprite mining buff time", () ->
                                               e.getPlayer().sendInputInteger("How much dust would you like to spend?", num ->
                                                       e.getPlayer().sendOptionDialogue(conf -> {
                                                           final int adjusted = num > e.getPlayer().getInventory().getNumberOf(13727) ? e.getPlayer().getInventory().getNumberOf(13727) : num;
                                                           conf.add("Spend " + Utils.formatNumber(adjusted) + " stardust for " + Utils.ticksToTime(adjusted*6), () -> {
                                                               if (e.getPlayer().getInventory().containsItem(13727, adjusted)) {
                                                                   e.getPlayer().getInventory().deleteItem(13727, adjusted);
                                                                   e.getPlayer().extendEffect(Effect.SHOOTING_STAR_MINING_BUFF, adjusted * 6);
                                                               }
                                                           });
                                                           conf.add("Nevermind. That's too expensive.");
                                                       })));
                                        rewards.add("Open stardust shop", () -> ShopsHandler.openShop(e.getPlayer(), "stardust_shop"));
                                   });
                   }));
    });
}

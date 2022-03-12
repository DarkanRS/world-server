package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.holidayevents.easter.easter22.npcs.EasterChick;
import com.rs.game.player.interactions.PlayerEntityInteraction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerClickEvent;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerClickHandler;

@PluginEventHandler
public class EggsterminatorInteraction extends PlayerEntityInteraction {

    public EggsterminatorInteraction(Entity target) {
        super(target, 7);
    }

    @Override
    public void interact(Player player) {
        player.setNextFaceWorldTile(target.getTile());
        int attackStyle = player.getCombatDefinitions().getAttackStyleId();
        int delay = World.sendProjectile(player, target.getTile(), (attackStyle == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();

        if (target instanceof NPC) {
            NPC npc = ((NPC) target);
            if (Easter2022.hasCompletedHunt(player) && (npc.getId() == Easter2022.CHOCOCHICK || npc.getId() == Easter2022.CHICK)) {
                player.sendMessage("You have already completed this hunt.");
                return;
            }
            player.setNextAnimation(new Animation(12174));
            player.setNextSpotAnim(new SpotAnim(2138));
            WorldTasks.schedule(delay, () -> {
                Item reward = new Item(attackStyle == 0 ? Easter2022.CHOCOTREAT : Easter2022.EVIL_DRUMSTICK);
                player.sendMessage("You turn the " + npc.getName().toLowerCase() + " into a " + reward.getName().toLowerCase() + " The shattered remains of the egg disappear.");
                player.getInventory().addItemDrop(reward);
                if (Easter2022.hasCompletedHunt(player)) {
                    player.getInventory().addItem(Easter2022.XP_LAMP);
                    player.sendMessage("You are rewarded with an XP lamp for finding 5 eggs in a single hunt.");

                    int completedHunts = player.getI(Easter2022.STAGE_KEY+"CompletedHunts", 0);
                    completedHunts++;
                    player.save(Easter2022.STAGE_KEY+"CompletedHunts", completedHunts);
                    if (completedHunts == 3) {
                        player.startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR,"You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it."));
                        player.sendMessage("You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it.");
                    }
                    if (completedHunts <= 3) {
                        player.startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR, "You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."));
                        player.sendMessage("You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt.");
                    }
                }
            });
        }
    }

    @Override
    public boolean canStart(Player player) {
        return true;
    }

    @Override
    public boolean checkAll(Player player) {
        return true;
    }

    @Override
    public void onStop(Player player) {

    }

    public static PlayerClickHandler handleSplatter = new PlayerClickHandler(false, "Splatter") {
        @Override
        public void handle(PlayerClickEvent e) {
            e.getPlayer().getInteractionManager().setInteraction(new EggsterminatorInteraction(e.getTarget()));
        }
    };

    public static ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[ Easter2022.UNCRACKED_EGG ]) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getPlayer().getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR || e.getPlayer().getEquipment().getWeaponId() != Easter2022.PERMANENT_EGGSTERMINATOR) {
                e.getPlayer().sendMessage("You need the Eggsterminator to crack open this egg. Speak with the Evil Chicken or the Chocatrice in Varrock Square.");
                return;
            }
            EasterEgg.Spawns spawn = EasterEgg.Spawns.getEggByLocation(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
            if (spawn == null)
                return;

            int attackStyle = e.getPlayer().getCombatDefinitions().getAttackStyleId();
            int delay = World.sendProjectile(e.getPlayer(), spawn.getTile(), (attackStyle == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();

            WorldTasks.scheduleTimer(delay, (loops) -> {
                switch (loops) {
                    case 0 -> {} //send object animation?
                    case 3 -> {
                        EasterChick npc = new EasterChick(e.getPlayer(), (attackStyle == 0 ? Easter2022.CHOCOCHICK : Easter2022.CHICK), World.getFreeTile(spawn.getTile(), 2));
                        npc.setEasterEggSpawn(spawn);
                        e.getPlayer().startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CALM, "You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears."));
                        e.getPlayer().sendMessage("You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears.");
                        return false;
                    }
                }
                return true;
            });



//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."
//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?
        }
    };

    public static ItemEquipHandler handleEggsterminatorWield = new ItemEquipHandler(Easter2022.EGGSTERMINATOR, Easter2022.PERMANENT_EGGSTERMINATOR) {
        @Override
        public void handle(ItemEquipEvent e) {
            e.getPlayer().setPlayerOption(e.equip() ? "Splatter" : "null", 8, true);
            if (e.dequip() && e.getItem().getId() == Easter2022.EGGSTERMINATOR) {
                e.getPlayer().startConversation(new Dialogue().addOptions("Destroy the Eggsterminator?", new Options() {
                    @Override
                    public void create() {
                        option("Yes", () -> {
                            e.getPlayer().getEquipment().deleteItem(Easter2022.EGGSTERMINATOR, 1);
                            e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
                            e.getPlayer().getAppearance().generateAppearanceData();
                        });
                        option("No");
                    }
                }));
                return;
            }
            if (Easter2022.ENABLED)
                e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
        }
    };

    public static LoginHandler removeTempEggsterminator = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if (Easter2022.ENABLED)
                return;
            if (e.getPlayer().getEquipment().getWeaponId() == Easter2022.EGGSTERMINATOR) {
                e.getPlayer().getEquipment().deleteItem(Easter2022.EGGSTERMINATOR, 1);
                e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
                e.getPlayer().getAppearance().generateAppearanceData();
                e.getPlayer().sendMessage("Your Eggsterminator has vanished. Start a hunt to obtain a new one and even unlock an enchanted permanent version.");
            }
        }
    };
}

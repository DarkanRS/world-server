package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.game.World;
import com.rs.game.player.Equipment;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.*;
import com.rs.plugin.handlers.*;

@PluginEventHandler
public class Eggsterminator {

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

    public static PlayerClickHandler handleSplatter = new PlayerClickHandler(false, "Splatter") {
        @Override
        public void handle(PlayerClickEvent e) {
//            e.getPlayer().getActionManager().setAction(new Eggsterminator(e.getTarget()));
        }
    };

    public static ItemEquipHandler eggsterminator = new ItemEquipHandler(new Object[] { Easter2022.EGGSTERMINATOR, Easter2022.PERMANENT_EGGSTERMINATOR}) {
        @Override
        public void handle(ItemEquipEvent e) {
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

    public static ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[ Easter2022.UNCRACKED_EGG ]) {
        @Override
        public void handle(ObjectClickEvent e) {
//            player.sendMessage("You need the Eggsterminator to crack open this egg. Speak with the Evil Chicken or the Chocatrice in Varrock Square.");
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chocochick appears.");
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chick appears.");
//            "" SENDS AS GAME MESSAGE AND DIALOGUE

//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."
//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?
        }
    };

    public static NPCClickHandler easterChick = new NPCClickHandler(new Object[] { Easter2022.CHICK, Easter2022.CHOCOCHICK }) {
        @Override
        public void handle(NPCClickEvent e) {
            if (Easter2022.hasCompletedHunt(e.getPlayer())) {
                e.getPlayer().sendMessage("You have already completed this hunt.");
                return;
            }

            e.getPlayer().setNextAnimation(new Animation(12174));
            e.getPlayer().setNextSpotAnim(new SpotAnim(2138));
            int delay = World.sendProjectile(e.getPlayer(), e.getNPC().getTile(), (e.getPlayer().getCombatDefinitions().getAttackStyleId() == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();
            WorldTasks.schedule(delay, () -> {
                e.getPlayer().sendMessage("You turn the " + e.getNPC().getName().toLowerCase() + " into a " + (e.getPlayer().getCombatDefinitions().getAttackStyleId() == 0 ? "chocotreat." : "drumstick.") + " The shattered remains of the egg disappear.");
                e.getPlayer().getInventory().addItemDrop(Easter2022.CHOCOTREAT, 1);
            });

            if (Easter2022.hasCompletedHunt(e.getPlayer())) {
                e.getPlayer().getInventory().addItem(Easter2022.XP_LAMP);
                e.getPlayer().sendMessage("You are rewarded with an XP lamp for finding 5 eggs in a single hunt.");

                int completedHunts = e.getPlayer().getI(Easter2022.STAGE_KEY+"CompletedHunts", 0);
                completedHunts++;
                e.getPlayer().save(Easter2022.STAGE_KEY+"CompletedHunts", completedHunts);
                if (completedHunts == 3) {
                    e.getPlayer().sendMessage("You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Cohcatrice in Varrock Square to claim it.");
                }
                if (completedHunts <= 3) {
                    e.getPlayer().sendMessage("You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt.");
                }
            }

//            player.sendMessage("You turn the chick into a chocotreat. The shattered remains of the egg disappear.");
//            player.sendMessage("You turn the chick into a drumstick. The shattered remains of the egg disappear.");
//            On collecting the 5th treat, an xp lamp is instantly placed in the players inventory.
        }
    };

}

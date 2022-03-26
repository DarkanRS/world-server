package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt.Spawns;
import com.rs.game.content.holidayevents.easter.easter22.npcs.EasterChick;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.interactions.PlayerEntityInteraction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerClickEvent;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
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
        int delay = World.sendProjectile(player.getTile(), target.getTile(), (attackStyle == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();

        if (target instanceof NPC) {
            NPC npc = ((NPC) target);
            if (EggHunt.hasCompletedHunt(player) && (npc.getId() == Easter2022.CHOCOCHICK || npc.getId() == Easter2022.CHICK)) {
                player.sendMessage("This hunt has ended.");
                return;
            }
            player.setNextAnimation(new Animation(12174));
            player.setNextSpotAnim(new SpotAnim(2138));
            WorldTasks.schedule(delay, () -> {
            	target.sendDeath(player);
            	target.setNextSpotAnim(new SpotAnim(3962));
            });
        }
    }

    @Override
    public boolean canStart(Player player) { return true; }

    @Override
    public boolean checkAll(Player player) { return true; }

    @Override
    public void onStop(Player player) {}

    public static PlayerClickHandler handlePlayerSplatter = new PlayerClickHandler(false, "Splatter") {
        @Override
        public void handle(PlayerClickEvent e) {
            e.getPlayer().getInteractionManager().setInteraction(new EggsterminatorInteraction(e.getTarget()));
        }
    };
    

    public static NPCClickHandler handleNPCSplatter = new NPCClickHandler(new Object[] { Easter2022.CHOCOCHICK, Easter2022.CHICK }, new String[] { "Splatter" }) {
		@Override
		public void handle(NPCClickEvent e) {
            e.getPlayer().getInteractionManager().setInteraction(new EggsterminatorInteraction(e.getNPC()));
		}
    };

    public static ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[] { 70106, 70107, 70108, 70109, 70110 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getPlayer().getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR && e.getPlayer().getEquipment().getWeaponId() != Easter2022.PERMANENT_EGGSTERMINATOR) {
                e.getPlayer().sendMessage("You need the Eggsterminator to crack open this egg. Speak with the Evil Chicken or the Chocatrice in Varrock Square.");
                return;
            }   
        	Spawns spawn = Spawns.getSpawnByObject(e.getObject());
        	if (spawn == null)
        		return; //Failed to get a spawn location based on the object coordinates. This should never happen.
        	int idx = EggHunt.indexOf(spawn.ordinal());
        	if (idx == -1)
        		return; //Not a part of this hunt. This should only happen if varbits arent being set correctly somewhere.  
            if (e.getOption().equals("Crack")) {
                int attackStyle = e.getPlayer().getCombatDefinitions().getAttackStyleId();
//                int delay = World.sendProjectile(e.getPlayer().getTile(), new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane()),
//                        (attackStyle == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();
                int delay = World.sendProjectile(e.getPlayer(), e.getObject(), (attackStyle == 0 ? 3034 : 3035), 35, 0, 60, 1, 0, 0).getTaskDelay();

                WorldTasks.schedule(delay, () -> {

                	EggHunt.updateVarbits(e.getPlayer(), 1, idx);
                	//TODO - send object animation

                	int npcId = (attackStyle == 0 ? Easter2022.CHICK : Easter2022.CHOCOCHICK);
                    EasterChick npc = new EasterChick(e.getPlayer(), npcId, World.getFreeTile(new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane()), 2), Spawns.getSpawnByObject(e.getObject()));
//                	EggHunt.updateVarbits(e.getPlayer(), 3, idx);
                    e.getPlayer().startConversation(new Dialogue().addNPC(npc.getId(), HeadE.CALM, "You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears."));
                    e.getPlayer().sendMessage("You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears.");
                    World.sendSpotAnim(e.getPlayer(), new SpotAnim(3037), e.getObject());
                    if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", 0) != EggHunt.getHunt()) {
                        e.getPlayer().save(Easter2022.STAGE_KEY+"CurrentHunt", EggHunt.getHunt());
                        if (Settings.getConfig().isDebug()) { System.out.println("Current hunt updated to " + EggHunt.getHunt() + " for " + e.getPlayer().getDisplayName()); }
                    }
                });
            }
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

package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.interactions.PlayerEntityInteraction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.PlayerClickHandler;

@PluginEventHandler
public class EggsterminatorInteraction extends PlayerEntityInteraction {

    public EggsterminatorInteraction(Entity target) {
        super(target, 7);
    }

    @Override
    public void interact(Player player) {
        player.lock();
        player.resetWalkSteps();
        player.setNextFaceTile(target.getTile());
        player.setNextAnimation(new Animation(12174));
        player.setNextSpotAnim(new SpotAnim(2138));
        
        int attackStyle = player.getCombatDefinitions().getAttackStyleId();
        int delay = World.sendProjectile(player.getTile(), target, (attackStyle == 0 ? 3034 : 3035), 20, 20, 30, 1, 0, 0).getTaskDelay();
        
        WorldTasks.schedule(delay, () -> {
            if (target instanceof NPC n) {
                n.sendDeath(player);
                World.sendSpotAnim(target.getTile(), attackStyle == 0 ? new SpotAnim(3033) : new SpotAnim(3032));
            } else if (target instanceof Player p) {
                World.sendSpotAnim(target.getTile(), attackStyle == 0 ? new SpotAnim(3037) : new SpotAnim(3036));
                if ((p.getEquipment().getHatId() == Easter2022.EGG_ON_FACE_MASK || p.getEquipment().getHatId() == Easter2022.CHOCOLATE_EGG_ON_FACE_MASK) && p.getNextAnimation() == null)
                	p.setNextAnimation(new Animation(2107));
            }
            player.unlock();
        });
    }

    @Override
    public boolean canStart(Player player) { return true; }

    @Override
    public boolean checkAll(Player player) { return true; }

    @Override
    public void onStop(Player player) {}

    public static PlayerClickHandler handlePlayerSplatter = new PlayerClickHandler(false, "Splatter", e -> e.getPlayer().getInteractionManager().setInteraction(new EggsterminatorInteraction(e.getTarget())));
    public static NPCClickHandler handleNPCSplatter = new NPCClickHandler(false, new Object[] { Easter2022.CHOCOCHICK, Easter2022.CHICK }, new String[] { "Splatter" }, e -> e.getPlayer().getInteractionManager().setInteraction(new EggsterminatorInteraction(e.getNPC())));

    public static ItemEquipHandler handleEggsterminatorWield = new ItemEquipHandler(new Object[] { Easter2022.EGGSTERMINATOR, Easter2022.PERMANENT_EGGSTERMINATOR }, e -> {
    	if (e.equip()) {
    		e.getPlayer().setPlayerOption("Splatter", 8, true);
    		if (Easter2022.ENABLED)
                e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
            return;
    	}
        if (e.getItem().getId() == Easter2022.EGGSTERMINATOR) {
        	e.cancel();
            e.getPlayer().sendOptionDialogue("Destroy the Eggsterminator?", ops -> {
            	ops.add("Yes, destroy it.", () -> {
            		e.getPlayer().getEquipment().setNoPluginTrigger(Equipment.WEAPON, null);
            		e.getPlayer().setPlayerOption("null", 8, true);
                    e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
                    e.getPlayer().getAppearance().generateAppearanceData();
                });
            	ops.add("No, keep it.");
            });
        } else
        	e.getPlayer().setPlayerOption("null", 8, true);
    });

    public static LoginHandler removeTempEggsterminator = new LoginHandler(e -> {
    	if (Easter2022.ENABLED)
            return;
        if (e.getPlayer().getEquipment().getWeaponId() == Easter2022.EGGSTERMINATOR) {
            e.getPlayer().getEquipment().deleteItem(Easter2022.EGGSTERMINATOR, 1);
            e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
            e.getPlayer().getAppearance().generateAppearanceData();
            e.getPlayer().sendMessage("The Easter event is over and the magic of your Eggsterminator has vanished. You watch as it melts into chocolate.");
        }
    });
}

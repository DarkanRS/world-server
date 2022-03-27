package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.holidayevents.easter.easter22.npcs.EasterChick;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class EventEasterEgg extends GameObject {
	
	private boolean found = false;
	private int varbit = -1;
	private String hint;

	public EventEasterEgg(int id, ObjectType type, int rotation, int x, int y, int plane) {
		super(id, type, rotation, x, y, plane);
	}

	public void setVarbit(int vb) {
		varbit = vb;
	}
	
	public int getVarbit() {
		return varbit;
	}
	
	public void setHint(String loc) {
		hint = loc;
	}
	
	public String getHint() {
		return hint;
	}
	
	public void setFound(boolean spotted) {
		found = spotted;
	}
	
	public boolean getFound() {
		return found;
	}

	private void spotted(Player p) {
		if (getFound() == false)
			World.sendWorldMessage("<col=ffff00>" + p.getDisplayName() + " has found an egg " + getHint(), false);
		setFound(true);
	}
	
    public static ObjectClickHandler crackEgg = new ObjectClickHandler(false, new Object[] { 70106, 70107, 70108, 70109, 70110 }) {
        @Override
        public void handle(ObjectClickEvent e) {
        	if (!Easter2022.ENABLED)
        		return;
        	e.getPlayer().faceObject(e.getObject());
            if (e.getOption().equals("Crack")) {
                if (e.getPlayer().getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR && e.getPlayer().getEquipment().getWeaponId() != Easter2022.PERMANENT_EGGSTERMINATOR) {
                    e.getPlayer().sendMessage("You need the Eggsterminator to crack open this egg. Speak with the Evil Chicken or the Chocatrice in Varrock Square.");
                    return;
                }
                WorldTasks.schedule(1, () -> e.getPlayer().setNextAnimation(new Animation(12174)));
                int attackStyle = e.getPlayer().getCombatDefinitions().getAttackStyleId();
                int delay = World.sendProjectile(e.getPlayer().getTile(), new WorldTile(e.getObject()), (attackStyle == 0 ? 3034 : 3035), 20, 10, 10, 1, 0, 0).getTaskDelay();
        		int npcId = (attackStyle == 0 ? Easter2022.CHICK : Easter2022.CHOCOCHICK);
        		((EventEasterEgg)e.getObject()).spotted(e.getPlayer());
        		WorldTasks.scheduleTimer(delay, (tick) -> {
                	switch (tick) {
                		case 0 -> { 
                			e.getPlayer().getVars().saveVarBit(((EventEasterEgg)e.getObject()).getVarbit(), (attackStyle == 0 ? 2 : 1));
                            World.sendSpotAnim(e.getPlayer(), new SpotAnim(3037), e.getObject());
                			World.sendObjectAnimation(e.getObject(), new Animation(16432));
                		}
                		case 2 -> {
                            EasterChick npc = new EasterChick(e.getPlayer(), npcId, World.getFreeTile(new WorldTile(e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane()), 2), ((EventEasterEgg)e.getObject()).getVarbit());
                            e.getPlayer().startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR, "You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears."));
                            e.getPlayer().sendMessage("You shatter the egg with the Eggsterminator. A " + npc.getName().toLowerCase() + " appears.");
                            if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", 0) != EggHunt.getHunt())
                                e.getPlayer().save(Easter2022.STAGE_KEY+"CurrentHunt", EggHunt.getHunt());
                            return false;
                		}
                	}
                    return true;
                });
            }
        }
    };
}

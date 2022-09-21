package com.rs.game.content.skills.hunter.puropuro;

import java.util.List;

import com.rs.game.World;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class ImpDefender extends NPC {

    public ImpDefender(int id, WorldTile tile) {
        super(id, tile);
        setClipType(ClipType.NORMAL);
        WorldTasks.schedule(0, Ticks.fromSeconds(5), () -> swiperNoSwiping());
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6074 }) {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) { return new ImpDefender(npcId, tile); }
    };
    
    public void swiperNoSwiping() {
    	List<Player> players = World.getPlayersInRegion(10307);
    	for (Player p : players) {
    		if (p.withinDistance(new WorldTile(getX(), getY(), getPlane()), 3)) {
    			if (p.getInventory().containsItem(11261)) {
    				Item i = p.getInventory().getItemById(11261);
    				boolean used = (boolean)i.getMetaData("used");
    				if (used) {
    					i.setId(229);
    					i.deleteMetaData();
    				} else
        				i.addMetaData("used", true);
    				p.sendMessage("Your repellent protects you from the Imp Defender.");
    				return;
    			}
    			if (Utils.random(10) == 0) {
    				p.lock();
    				walkToAndExecute(p.getNearestTeleTile(1), () -> {
    					p.faceEntity(this);
    					p.unlock();
    					if (Utils.random(10000) == 0) {
    						p.setNextAnimation(new Animation(8991));
    						p.setNextForceTalk(new ForceTalk("Swiper, no swiping!"));
    						return;
    					}
        				//find the lowest tier jar of impling in the players inventory.
        	            setNextForceTalk(new ForceTalk("Be Free!"));
        	            
    				});
    			}
    		}
    	}
    }
    
    
}
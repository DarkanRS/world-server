package com.rs.game.content.holidayevents.easter.easter22.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.game.World;
import com.rs.game.content.holidayevents.easter.easter22.Easter2022;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;

@PluginEventHandler
public class EasterChick extends OwnedNPC {

    public int varbit;

    public EasterChick(Player player, int id, Tile tile, int varbit) {
        super(player, id, tile, true);
        this.varbit = varbit;
        setRandomWalk(true);
        setNextAnimation(new Animation(3673));
        setAutoDespawnAtDistance(true);
    }

    @Override
    public void processNPC() {
        super.processNPC();
    }
    
    @Override
    public void onDespawnEarly() {
    	finish();
    	getOwner().getVars().saveVarBit(varbit,  0);
    }
    
    @Override
    public void sendDeath(final Entity source) {
		getInteractionManager().forceStop();
		resetWalkSteps();
		if (source.getAttackedBy() == this) {
			source.setAttackedBy(null);
			source.setFindTargetDelay(0);
		}
		setNextAnimation(null);
		PluginManager.handle(new NPCDeathEvent(this, source));
		
        int attackStyle = getOwner().getCombatDefinitions().getAttackStyleId();
		setNextAnimation(new Animation(16423));
		World.sendSpotAnim(this.getTile(), attackStyle == 0 ? new SpotAnim(3031) : new SpotAnim(3030));

		WorldTasks.scheduleTimer(0, (tick) -> {
			if (tick == 1)
				finish();
			if (tick == 2) {
				EggHunt.incrementScore(attackStyle);
				rewardPlayer(attackStyle);
				return false;
			}
			return true;
		});
    }
    
    public void rewardPlayer(int attackStyle) {
    	if (!Easter2022.ENABLED)
    		return;
        Item reward = new Item(attackStyle == 0 ? Easter2022.EVIL_DRUMSTICK : Easter2022.CHOCOTREAT);
        getOwner().sendMessage("You turn the " + getName().toLowerCase() + " into a " + reward.getName().toLowerCase() + " The shattered remains of the egg disappear.");
    	getOwner().getVars().saveVarBit(varbit,  3);
        getOwner().getInventory().addItemDrop(reward);        
        if (EggHunt.hasCompletedHunt(getOwner())) {
            getOwner().loyaltyPoints += Easter2022.LOYALTY_POINTS_AWARDED;
            getOwner().getInventory().addItem(Easter2022.XP_LAMP);
            getOwner().sendMessage("You are rewarded with an XP lamp and " + Easter2022.LOYALTY_POINTS_AWARDED + " loyalty points for finding 5 eggs in a single hunt.");
            getOwner().incrementCount(Easter2022.STAGE_KEY+"CompletedHunts");
            int completedHunts = getOwner().getCounterValue(Easter2022.STAGE_KEY+"CompletedHunts");
            if (completedHunts == 3) {
                getOwner().startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR,"You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it."));
                getOwner().sendMessage("You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it.");
            }
            if (completedHunts < 3) {
                getOwner().startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR, "You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."));
                getOwner().sendMessage("You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt.");
            }
        }
    }
}
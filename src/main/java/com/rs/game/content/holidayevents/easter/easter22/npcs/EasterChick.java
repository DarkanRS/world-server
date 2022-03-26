package com.rs.game.content.holidayevents.easter.easter22.npcs;

import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.holidayevents.easter.easter22.Easter2022;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt.Spawns;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.others.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;

@PluginEventHandler
public class EasterChick extends OwnedNPC {

    public static Spawns spawnEgg;

    public EasterChick(Player player, int id, WorldTile tile, Spawns spawn) {
        super(player, id, tile, true);
        spawnEgg = spawn;
        setRandomWalk(true);
        setNextAnimation(new Animation(3673));
        setAutoDespawnAtDistance(true);
        
        //3673 falling spawn anim
        //3806 lay down and die anim
        //14842 lay down and die long anim
        //cannon impact gfx 3037
        //3030 spotanim for chocotreat
        //3031 spotanim for evil drumstick
        
//        16432 object anim egg crack (object 70104 chocochick, object 70105 regular chick)
//        scotch egg anim 16433: [3960] SpotAnim: 3034
//        marshmallow anim 16434: [3962] SpotAnim: 3035
//        projectile impact maybe? 16435: [3962] SpotAnim: 3036
//        explosion anim 16436: [3798] SpotAnim: 3037
//        16437: [3959] Object: 70104, Object: 70105
//        16438: [2923] Unknown
//        16439: [3959] Unknown
//        16440: [3959] Object: 69753
//        16441: [3958] Object: 70115, Object: 70116, Object: 70117, Object: 70118, Object: 72591, Object: 72592, Object: 72593, Object: 72594, Object: 72595, Object: 72596, Object: 72597, Object: 72598, Object: 72599, Object: 72600, Object: 72601, Object: 72602, Object: 72603, Object: 72604, Object: 73669, Object: 73670, Object: 73671, Object: 73672
//        16442: [990] SpotAnim: 3038
//        16443: [990] SpotAnim: 3039
//        16444: [233] Unknown
//        16445: [233] Unknown
    }

    @Override
    public void processNPC() {
        super.processNPC();
    }
    
    @Override
    public void onDespawnEarly() {
    	finish();
    	EggHunt.updateVarbits(getOwner(), 0, EggHunt.indexOf(spawnEgg.ordinal()));
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
		WorldTasks.scheduleTimer(0, (loop) -> {
			if (loop == 0)
				setNextAnimation(new Animation(3806));
			else if (loop >= 3) {
				finish();
                int attackStyle = getOwner().getCombatDefinitions().getAttackStyleId();
                Item reward = new Item(attackStyle == 0 ? Easter2022.EVIL_DRUMSTICK : Easter2022.CHOCOTREAT);
                if (attackStyle == 0) 
                	EggHunt.incrementChocatriceScore(); 
                else 
                	EggHunt.incrementEvilChickenScore();
                getOwner().sendMessage("You turn the " + getName().toLowerCase() + " into a " + reward.getName().toLowerCase() + " The shattered remains of the egg disappear.");
                EggHunt.updateVarbits(getOwner(), 2, EggHunt.indexOf(spawnEgg.ordinal()));
                getOwner().getInventory().addItemDrop(reward);
                if (EggHunt.hasCompletedHunt(getOwner())) {
                    getOwner().getInventory().addItem(Easter2022.XP_LAMP);
                    getOwner().sendMessage("You are rewarded with an XP lamp for finding 5 eggs in a single hunt.");

                    int completedHunts = getOwner().getI(Easter2022.STAGE_KEY+"CompletedHunts", 0);
                    completedHunts++;
                    getOwner().save(Easter2022.STAGE_KEY+"CompletedHunts", completedHunts);
                    if (completedHunts == 3) {
                        getOwner().startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR,"You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it."));
                        getOwner().sendMessage("You have earned a permanent version of the Eggsterminator for finding 5 eggs in 3 hunts. Speak to the Evil Chicken or Chocatrice in Varrock Square to claim it.");
                    }
                    if (completedHunts <= 3) {
                        getOwner().startConversation(new Dialogue().addItem(Easter2022.PERMANENT_EGGSTERMINATOR, "You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."));
                        getOwner().sendMessage("You are " + completedHunts + "/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt.");
                    }
                }
				return false;
			}
			return true;
		});
    }

    public static void setEasterEggSpawn(EggHunt.Spawns egg) { spawnEgg = egg; }

    public static Spawns getEasterEggSpawn() { return spawnEgg; }

}
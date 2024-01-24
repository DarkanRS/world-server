package com.rs.game.content.miniquests.FromTinyAcorns;

import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class pickpocketUrist extends PlayerAction{

        private NPC npc;

        private boolean success = false;

        public pickpocketUrist(NPC npc) {
            this.npc = npc;
        }
        @Override
        public boolean start(Player player) {
            if (checkAll(player)) {
                success = successful(player);
                player.faceEntity(npc);
                WorldTasks.delay(0, () -> {
                    player.anim(881);
                });
                setActionDelay(player, 2);
                player.lock();
                return true;
            }
            return false;
        }

        @Override
        public boolean process(Player player) {
            return checkAll(player);
        }

        @Override
        public int processWithDelay(Player player) {
            if (!success) {
                player.npcDialogue(npc.getId(), HeadE.ANGRY, "Oi! Leave that alone.");
            }
            else {
                player.getInventory().addItem(18649,1);
                player.sendMessage("You steal a golden talisman out of Urist's back pocket.");
            }
            stop(player);
            return -1;
        }

        @Override
        public void stop(Player player) {
            player.unlock();
            player.setNextFaceEntity(null);
            setActionDelay(player, 1);
        }

        public boolean rollSuccess(Player player) {
            return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul(), 185, 255);
        }

        private boolean successful(Player player) {
            if (!rollSuccess(player))
                return false;
            return true;
        }

        private boolean checkAll(Player player) {
            if (player.isDead() || player.hasFinished() || player.hasPendingHits())
                return false;
            if (player.getAttackedBy() != null && player.inCombat()) {
                player.sendMessage("You can't do this while you're under combat.");
                return false;
            }
            return true;
        }
    }


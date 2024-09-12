// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world.areas.barbarian_village;

import com.rs.game.content.world.unorganized_dialogue.StrongholdRewardD;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class BarbarianVillage {

    public static ObjectClickHandler handleStrongholdRewards = new ObjectClickHandler(new Object[] { 16135, 16077, 16118, 16047 }, e -> {
        Player player = e.getPlayer();
        switch(e.getObjectId()) {
            case 16135 -> {
                if (player.getEmotesManager().unlockedEmote(EmotesManager.Emote.FLAP))
                    player.sendMessage("You have already claimed your reward from this level.");
                else
                    player.startConversation(new StrongholdRewardD(player, 0));
            }
            case 16077 -> {
                if (player.getEmotesManager().unlockedEmote(EmotesManager.Emote.SLAP_HEAD))
                    player.sendMessage("You have already claimed your reward from this level.");
                else
                    player.startConversation(new StrongholdRewardD(player, 1));
            }
            case 16118 -> {
                if (player.getEmotesManager().unlockedEmote(EmotesManager.Emote.IDEA))
                    player.sendMessage("You have already claimed your reward from this level.");
                else
                    player.startConversation(new StrongholdRewardD(player, 2));
            }
            case 16047 -> player.startConversation(new StrongholdRewardD(player, 3));
        }
    });
}

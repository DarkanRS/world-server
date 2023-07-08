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
package com.rs.engine.quest;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.engine.quest.data.QuestInformation;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class QuestManager {
    public static int MAX_QUESTPOINTS = 0;
    public static ButtonClickHandler handleQuestTabButtons = new ButtonClickHandler(190, e -> {
        if (e.getComponentId() == 38)
            e.getPlayer().getQuestManager().setSort(e.getSlotId());
        else if (e.getComponentId() == 3)
            e.getPlayer().getQuestManager().toggleFilter();
        else if (e.getComponentId() == 7)
            e.getPlayer().getQuestManager().toggleHideDone();
        else if (e.getComponentId() == 15) {
            Quest quest = Quest.forSlot(e.getSlotId());
            e.getPlayer().getQuestManager().showQuestDetailInterface(quest);
        }
    });

    private transient Player player;
    private int sort;
    private boolean filter;
    private boolean hideDone;
    private Map<Integer, Integer> questStages;
    private Map<Integer, GenericAttribMap> questAttribs;

    public QuestManager() {
        questStages = new HashMap<>();
        questAttribs = new HashMap<>();
    }

    public void showQuestDetailInterface(Quest quest) {
        if (quest != null) {
            if (!player.isQuestStarted(quest) || player.isQuestComplete(quest)) {
                quest.openQuestInfo(player, false);
                return;
            }
            QuestInformation info = quest.getDefs().getExtraInfo();
            ArrayList<String> lines = new ArrayList<>();
            if (player.getQuestManager().getStage(quest) > 0)
                for (int i = 0; i < player.getQuestManager().getStage(quest); i++)
                    for (String line : quest.getHandler().getJournalLines(player, i))
                        lines.add("<str>" + line);
            lines.addAll(quest.getHandler().getJournalLines(player, player.getQuestManager().getStage(quest)));


            player.getInterfaceManager().sendInterface(275);
            player.getPackets().sendRunScriptReverse(1207, lines.size());
            player.getPackets().setIFText(275, 1, info.getName());
            for (int i = 10; i < 289; i++)
                player.getPackets().setIFText(275, i, ((i - 10) >= lines.size() ? " " : lines.get(i - 10)));
        }
    }

    public void unlockQuestTabOptions() {
        player.getPackets().setIFRightClickOps(190, 15, 0, 201, 0, 1, 2, 3);
        player.getPackets().setIFEvents(new IFEvents(190, 38, 0, 3).enableRightClickOption(0));
        player.getPackets().sendVarc(234, 0);
        updateOptions();
    }

    public int getStage(Quest quest) {
        if (questStages.get(quest.getId()) == null)
            return 0;
        return questStages.get(quest.getId());
    }

    public void setStage(Quest quest, int stage) {
        if (!quest.isImplemented())
            return;
        questStages.put(quest.getId(), stage);
        sendQuestStage(quest);
    }

    public void completeQuest(Quest quest) {
        if (!quest.isImplemented())
            return;
        if (!isComplete(quest)) {
            setStage(quest, quest.getHandler().getCompletedStage());
            clearQuestAttributes(quest);
            quest.getHandler().complete(player);
            sendQuestStage(quest);
            sendQuestPoints();
        }
    }

    public void resetQuest(Quest quest) {
        if (!quest.isImplemented())
            return;
        clearQuestAttributes(quest);
        setStage(quest, 0);
    }

    private void clearQuestAttributes(Quest quest) {
        questAttribs.remove(quest.getId());
    }

    public GenericAttribMap getAttribs(Quest quest) {
        GenericAttribMap map = questAttribs.get(quest.getId());
        if (map == null) {
            map = new GenericAttribMap();
            questAttribs.put(quest.getId(), map);
        }
        return map;
    }

    public boolean completedAllQuests() {
        for (Quest quest : Quest.values()) {
            if (!quest.isImplemented())
                continue;
            if (!isComplete(quest))
                return false;
        }
        return true;
    }

    public boolean isComplete(Quest quest, String actionForUnimplemented) {
        if (!quest.isImplemented())
            return quest.meetsReqs(player, actionForUnimplemented);
        if (getStage(quest) == quest.getHandler().getCompletedStage())
            return true;
        if (actionForUnimplemented != null)
            player.sendMessage("You must have completed " + quest.getDefs().name + " " + actionForUnimplemented);
        return false;
    }

    public boolean isComplete(Quest quest) {
        return isComplete(quest, null);
    }

    public int getQuestPoints() {
        int points = 0;
        for (Quest quest : Quest.values()) {
            if (quest == null || !quest.isImplemented())
                continue;
            if (isComplete(quest))
                points += quest.getDefs().questpointReward;
        }
        return points;
    }

    public void sendQuestPoints() {
        player.getVars().setVar(101, getQuestPoints());
        player.getVars().setVar(904, MAX_QUESTPOINTS);
    }

    public void updateAllQuestStages() {
        for (Quest quest : Quest.values())
            sendQuestStage(quest);
    }

    public void sendQuestStage(Quest quest) {
        if (!quest.isImplemented() || isComplete(quest))
            quest.getDefs().sendCompleted(player);
        else if (getStage(quest) > 0)
            quest.getDefs().sendStarted(player);
        if (quest.isImplemented())
            quest.getHandler().updateStage(player, getStage(quest));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (MAX_QUESTPOINTS == 0)
            for (Quest quest : Quest.values())
                if (quest.isImplemented())
                    MAX_QUESTPOINTS += quest.getDefs().questpointReward;
    }

    public void updateOptions() {
        //4538 = free/members toggle which is first?
        player.getVars().setVarBit(4536, sort);
        player.getVars().setVarBit(4537, filter ? 0 : 1);
        player.getVars().setVarBit(7264, hideDone ? 0 : 1);
        player.getVars().syncVarsToClient();
        player.getPackets().sendRunScript(2160, 0);
    }

    public void setSort(int sort) {
        this.sort = sort;
        updateOptions();
    }

    public void toggleFilter() {
        filter = !filter;
        updateOptions();
    }

    public void toggleHideDone() {
        hideDone = !hideDone;
        updateOptions();
    }
}

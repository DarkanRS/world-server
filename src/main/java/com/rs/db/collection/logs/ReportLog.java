package com.rs.db.collection.logs;

import com.rs.game.model.entity.player.Player;
import com.rs.utils.ReportsManager.Rule;

public class ReportLog {
    private Object relevantData;

	public ReportLog(Player reporter, Player reported, Rule rule) {
        String player = reported.getUsername();
        String reporter1 = reporter.getUsername();
    }

	//TODO finish

	//TODO create hashCode and equals
}

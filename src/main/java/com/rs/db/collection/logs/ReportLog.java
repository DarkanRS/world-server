package com.rs.db.collection.logs;

import com.rs.game.model.entity.player.Player;
import com.rs.utils.ReportsManager.Rule;

public class ReportLog {
	private String player;
	private String reporter;
	private Rule rule;
	private Object relevantData;

	public ReportLog(Player reporter, Player reported, Rule rule) {
		this.player = reported.getUsername();
		this.reporter = reporter.getUsername();
		this.rule = rule;
	}

	//TODO finish

	//TODO create hashCode and equals
}

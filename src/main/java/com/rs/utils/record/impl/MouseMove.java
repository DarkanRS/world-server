package com.rs.utils.record.impl;

import com.rs.lib.net.packets.decoders.mouse.MouseTrailStep;
import com.rs.lib.net.packets.decoders.mouse.MouseTrailStep.Type;
import com.rs.utils.record.RecordedAction;

import java.util.List;

public class MouseMove extends RecordedAction {
	
	private boolean icmp;
	private List<MouseTrailStep> steps;
	private int x = 0, y = 0;
	private boolean containsSoftwareClicks = false;

	public MouseMove(long timeLogged, int time, List<MouseTrailStep> steps, boolean icmp) {
		super(timeLogged, time);
		this.steps = steps;
		this.icmp = icmp;
		for (MouseTrailStep step : steps) {
			if (!containsSoftwareClicks && !step.isHardware())
				containsSoftwareClicks = true;
				
			if (x == 0 && y == 0 && step.getType() == Type.SET_POSITION) {
				x = step.getX();
				y = step.getY();
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " ["+x+","+y+"] " + (icmp ? ("ICMP: " + containsSoftwareClicks) : "");
	}

	public List<MouseTrailStep> getSteps() {
		return steps;
	}

	public boolean isIcmp() {
		return icmp;
	}

	public boolean containsSoftwareClicks() {
		return containsSoftwareClicks;
	}

}
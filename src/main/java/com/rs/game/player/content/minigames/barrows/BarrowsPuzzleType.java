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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.minigames.barrows;

public enum BarrowsPuzzleType {
	
	ARROWS(6713, new int[] { 6716, 6717, 6718 }, new int[] { 6713, 6714, 6715 }),
	SQUARES(6719, new int[] { 6722, 6723, 6724 }, new int[] { 6719, 6720, 6721 }),
	SQUARES_OFFSET(6725, new int[] { 6728, 6729, 6730 }, new int[] { 6725, 6726, 6727 }),
	SHAPES(6731, new int[] { 6734, 6735, 6736 }, new int[] { 6731, 6732, 6733 });

	private final int answer;
	private final int[] sequence;
	private final int[] options;
	
	private BarrowsPuzzleType(int answer, int[] sequence, int[] options) {
		this.answer = answer;
		this.sequence = sequence;
		this.options = options;
	}
	
	public int getAnswer() {
		return answer;
	}
	
	public int getSequenceModel(int index) {
		return sequence[index];
	}
	
	public int[] getSequence() {
		return sequence;
	}
	
	public int[] getOptions() {
		return options;
	}
}
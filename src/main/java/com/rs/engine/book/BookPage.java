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
package com.rs.engine.book;

public class BookPage {

	private String[] left;
	private String[] right;

	public BookPage(String[] left, String[] right) {
		if (left.length >= 15 || right.length >= 15)
			throw new RuntimeException("Cannot create book page with longer than 15 lines of text.");
		this.left = left;
		this.right = right;
	}

	public String getLeftLine(int line) {
		return line >= left.length ? "" : left[line];
	}

	public String getRightLine(int line) {
		return line >= right.length ? "" : right[line];
	}

}

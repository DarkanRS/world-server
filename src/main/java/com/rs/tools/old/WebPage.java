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
package com.rs.tools.old;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebPage {

	private URL url;
	private ArrayList<String> lines;
	private String wholePage = "";

	public WebPage(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public void load() throws IOException {
		lines = new ArrayList<>();
		HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
		c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		c.setReadTimeout(3000);
		BufferedReader stream = new BufferedReader(new InputStreamReader(c.getInputStream()));
		String line;
		while ((line = stream.readLine()) != null) {
			if (line.contains("}} {{")) {
				lines.add(line.substring(0, line.indexOf("}} {{")));
				lines.add(line.substring(line.indexOf("}} {{")));
			} else if (line.contains("}}{{")) {
				lines.add(line.substring(0, line.indexOf("}}{{")));
				lines.add(line.substring(line.indexOf("}}{{")));
			} else
				lines.add(line);
			wholePage += line;
		}
		stream.close();
	}

	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public String getPage() {
		return wholePage;
	}
}

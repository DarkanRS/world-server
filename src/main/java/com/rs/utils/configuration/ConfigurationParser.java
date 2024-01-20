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
package com.rs.utils.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to parse a configuration file, based on the perl configuration system
 * Azusa::Configuration s
 *
 * @author Nikki
 * @author solar
 *
 */
public class ConfigurationParser {

	/**
	 * The pattern to match scalar lines
	 */
	private final Pattern scalarPattern = Pattern.compile("\\s*(.*?)\\s*\"((?:\\\"|[^\"])+?)\"\\s*(?:\\#.*)?$");

	/**
	 * The pattern to match arrays..
	 */
	private final Pattern arrayPattern = Pattern.compile("\\s*(.*?)\\s*\\s*\\{\\s*(?:\\#.*)?$");

	/**
	 * The pattern to match nested arrays
	 */
	private final Pattern nestedArrayPattern = Pattern.compile("\\s*(.*?)\\s*\"((?:\\\"|[^\"])+?)\"\\s*\\(\\s*(?:\\#.*)?$");

	/**
	 * The pattern to match nested hashes
	 */
	private final Pattern nestedHashPattern = Pattern.compile("\\s*(.*?)\\s*\"((?:\\\\\"|[^\"])+?)\"\\s*\\{\\s*(?:\\#.*)?$");

	/**
	 * The patter to find the end of a nested array
	 */
	private final Pattern nestedEndPattern = Pattern.compile("^\\s*(}|\\))\\s*");

	/**
	 * The reader object which the configuration is parsed from
	 */
	private final BufferedReader reader;

	public ConfigurationParser(InputStream input) {
		reader = new BufferedReader(new InputStreamReader(input));
	}

	/**
	 * Parse the configuration from the specified file
	 *
	 * @return The configuration
	 * @throws IOException
	 */
	public ConfigurationNode parse() throws IOException {
		ConfigurationNode node = new ConfigurationNode();
		try {
			parse(node);
		} finally {
			reader.close();
		}
		return node;
	}

	/**
	 * Parse a block of data, reading line per line from the reader.
	 */
	public void parse(ConfigurationNode node) throws IOException {
		String line = reader.readLine();
		if (line == null)
			return;
		line = line.trim();

		if (!line.startsWith("#") && line.length() != 0) {
			// Scalar match
			Matcher scalar = scalarPattern.matcher(line);
			Matcher array = arrayPattern.matcher(line);
			Matcher nestedArrayBlock = nestedArrayPattern.matcher(line);
			Matcher nestedHashBlock = nestedHashPattern.matcher(line);
			if (scalar.find())
				node.set(scalar.group(1), scalar.group(2));
			else if (nestedArrayBlock.find()) {
				String name = nestedArrayBlock.group(1);
				String key = nestedArrayBlock.group(2);
				key = key.replaceAll("\\\"", "\"");
				if (!node.has(name))
					node.set(name, new ConfigurationNode());
				parse(node.nodeFor(name));
			} else if (nestedHashBlock.find()) {
				String name = nestedHashBlock.group(1);
				String key = nestedHashBlock.group(2);
				ConfigurationNode sub = node.has(name) ? node.nodeFor(name) : new ConfigurationNode();
				if (!node.has(name))
					node.set(name, sub);
				if (!sub.has(key))
					sub.set(key, new ConfigurationNode());
				parse(sub.nodeFor(key));
			} else if (array.find()) {
				ConfigurationNode newNode = new ConfigurationNode();
				node.set(array.group(1), newNode);
				parse(newNode);
			}
			Matcher nestedEnd = nestedEndPattern.matcher(line);
			if (nestedEnd.find())
				return;
		}
		parse(node);
	}
}
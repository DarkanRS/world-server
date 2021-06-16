package com.rs.utils.drop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Rational;
import com.rs.lib.util.Utils;

public class Fix {
	
	private static List<Double> INC = new ArrayList<>();
	private static Map<Double, double[]> MAP = new HashMap<>();
	
	static {
		for (double denom = 1.0;denom <= 2000.0;denom += 1.0) {
			for (double num = 1.0;num <= denom;num += 1.0) {
				double val = num / denom;
				if (MAP.containsKey(val))
					continue;
				INC.add(val);
				MAP.put(val, new double[] { num, denom });
			}
		}
		INC.sort(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return Double.compare(o2, o1);
			}
		});
	}
	
	private final static String PATH = "data/npcs/drops/";
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		//File f = new File(PATH + "general_graardor.json");
		
		File[] dropFiles = new File(PATH).listFiles();
		for (File f : dropFiles) {
			DropSet set = (DropSet) JsonFileManager.loadJsonFile(f, DropSet.class);
			if (set != null) {
				set.getDropList();
				if (set.isOverflowed()) {
					//System.err.println(f.getName() + " is overflowed by " + set.getDropList().getOverflow() + ".. Fixing..");
					
					double total = 0;
					for (DropTable table : set.getTables()) {
						if (table.getRate() == 0.0)
							continue;
						total += table.getRate();
					}
					
					for (DropTable table : set.getTables()) {
						try {
							if (table.getRate() == 0.0)
								continue;
							double rate = table.getRate() / total;
							double last = -1.0;
							for (double d : INC) {
								if (rate > d) {
									rate = last < 0.0 ? d : last;
									break;
								}
								last = rate;
							}
							
							//Rational frac = Utils.toRational(rate);
							//int[] meme = Utils.asFractionArr(frac.getNum(), frac.denom());
//							double num = 1.0;
//							double denom = Utils.round(((double) meme[1]) / ((double) meme[0]), 4);
							double[] fracD = MAP.get(rate);
							table.setChance(fracD[0], fracD[1]);
						} catch (Exception e) {
							double rate = table.getRate() / total;
							Rational frac = Utils.toRational(rate);
							int[] meme = Utils.asFractionArr(frac.getNum(), frac.denom());
							table.setChance(meme[0], meme[1]);
							//e.printStackTrace();
							//System.err.println("Error with table: " + f.getName() + ", " + table.getDrops()[0].toItem().getName());
						}
					}
					//System.out.println("Fixed. Checking validity.");
					set.createDropList();
					//System.out.println(set);
					if (!set.isOverflowed())
						JsonFileManager.saveJsonFile(set, new File("./fixed/"+f.getName()));
				}
			}
		}
	}

}

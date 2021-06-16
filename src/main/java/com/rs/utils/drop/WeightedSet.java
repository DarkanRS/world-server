package com.rs.utils.drop;

public class WeightedSet extends DropSet {
	
	public WeightedSet(WeightedTable... wTables) {
		super();
		int total = 0;
		for (WeightedTable table : wTables) {
			total += table.getWeight();
		}
				
		for (WeightedTable table : wTables) {
			table.setChance(table.getWeight(), total);
		}
		
		DropTable[] tables = new DropTable[wTables.length];
		for (int i = 0;i < tables.length;i++) {
			WeightedTable table = wTables[i];
			tables[i] = new DropTable(table);
		}
		this.tables = tables;
	}

}

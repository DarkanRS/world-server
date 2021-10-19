package com.rs.utils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * My custom Runtime debugging tool
 * Author: Jawarrior1
 */
public class Getlineonce {
    static Dictionary<String, List<Integer>> lines = new Hashtable<String, List<Integer>>();

    public Getlineonce(boolean repeat) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        //Check if this line as been taking, so as not to repeat with loops or recursion
        int line = stackTrace[2].getLineNumber();
        String fileName = stackTrace[2].getFileName();
        List<Integer> tempLines = new ArrayList<Integer>();//only used if key doesn't exist
        if (lines.isEmpty()) {
            tempLines.add(line);
            lines.put(fileName, tempLines);
//            lines.put("tester", oneline);
        } else {
            boolean keyExists = false;
            for (Enumeration<String> k = lines.keys(); k.hasMoreElements(); ) {
                String key = k.nextElement();

                //Check if there is a kay
                if (key.equalsIgnoreCase(fileName)) {
                    keyExists = true;
                    if (!lines.get(key).contains(line)) {
                        tempLines = lines.get(key);
                        tempLines.add(line);
                        lines.put(fileName, tempLines);
                        System.out.println(fileName + ": " + line);// should run once
                    } else { //The line was already run
                        if (repeat) {
                            System.out.println(fileName + ": " + line);
                        }
                    }
                } else {//If there is not key in this index, go to the next
                    continue;
                }
            }
            //If after searching all of those you found nothing
            if (!keyExists) {
                tempLines.add(line);
                lines.put(fileName, tempLines);
            }
        }
        int t = 0;
    }
}
package com.wahkahtah;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;

import java.util.StringTokenizer;

public class WKTReader {
	
	/**
	 * Transforms the input WKT-formatted String into Geometry object
	 */
	public Geometry read(String wktString) {
	  Geometry geom = null;
		char[] chars = wktString.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			if (!Character.isAlphabetic(chars[i]))
				continue;
			int markWord = i;
			for(; i < chars.length && Character.isAlphabetic(chars[i]); i++) { }
      String word = wktString.substring(markWord, i);
			switch (word) {
        case "POINT":
          i = skipUntilLeftParen(chars, i);
          int markStartParen = i;
          int numInnerParen = 0;
          for(; i < chars.length; i++) {
            if (chars[i] == '(')
              numInnerParen++;
            else if (chars[i] == ')' && numInnerParen == 0)
              break;
            else if (chars[i] == ')' && numInnerParen > 0)
              numInnerParen--;
          }
          int markEndParen = i;
          String point = wktString.substring(markStartParen+1, markEndParen-1);
          String x = point.split(" ")[0];
          String y = point.split(" ")[1];
          geom = new Point(dbl(x), dbl(y));
          break;
        case "MULTIPOINT":
          i = skipUntilLeftParen(chars, i);
          break;
      }
          /*
			switch (token) {
				case "POINT":
					String points = st.nextToken();
					if (points.contains("(")) {
						String first = "";
						if (points.length() > 1) {
							first = points.substring(1);
						} else {
							first = st.nextToken();
						}
					} else if (points.equals("EMPTY")) {

					} else {
						throw new RuntimeException();
					}
					break;
			}*/
		}
		return geom;
	}

	private int skipUntilLeftParen(char[] chars, int i) {
    for(; i < chars.length && chars[i] != '('; i++) { }
    return i;
  }

	private double dbl(String str) {
	  return new Double(str);
  }
}

package com.wahkahtah;

import com.sinergise.geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class WKTReader {
	
	/**
	 * Transforms the input WKT-formatted String into Geometry object
	 */
	public Geometry read(String wktString) {
	  Geometry geom = null;
    String str = extractGeometryString(wktString);
	  switch (extractGeometryType(wktString)) {
      case POINT:
        if (str==null) {
          geom = new Point();
        } else {
          double[] points = extractPoints(stripParen(str));
          geom = new Point(points[0], points[1]);
        }
        break;
      case LINESTRING:
        if (str==null) {
          geom = new LineString();
        } else {
          double[] points = extractPoints(stripParen(str));
          geom = new LineString(points);
        }
        break;
      case POLYGON:
        if (str==null) {
          geom = new Polygon();
        } else {
          String[] lineStrings = extractLineStrings(str.substring(1, str.length() - 1));

          LineString body = new LineString( extractPoints(stripParen(lineStrings[0])) );
          LineString[] holes = null;
          if (lineStrings.length > 1) {
            holes = new LineString[lineStrings.length - 1];
            for (int i = 1; i < lineStrings.length; i++) {
              holes[i-1] = new LineString(extractPoints(stripParen(lineStrings[i])));
            }
          }
          geom = new Polygon(body, holes);

        }
        break;
    }
		return geom;
	}

  private String[] extractLineStrings(String context) {
	  int parenLevel = 0;
	  List<Integer> splitAt = new ArrayList<>();
    for (int i=0; i<context.length(); i++) {
      if (context.charAt(i) == '(')
        parenLevel ++;
      else if (context.charAt(i) == ')')
        parenLevel --;
      else if (context.charAt(i) == ',' && parenLevel == 0)
        splitAt.add(i);
    }
    if (splitAt.size()==0) {
      return new String[]{context};
    }
    List<String> substrings = new ArrayList<>();
    int lastSplitAt = 0;
    for (Integer splitAtIdx : splitAt) {
      substrings.add(context.substring(lastSplitAt, splitAtIdx).trim());
      lastSplitAt = splitAtIdx+1;
    }
    substrings.add(context.substring(lastSplitAt).trim());
    return substrings.toArray(new String[substrings.size()]);
  }

  private String extractGeometryString(String context) {
	  if (!context.contains("(") && context.contains("EMPTY")) {
	    return null;
    } else {
      return context.substring(context.indexOf('(')).trim();
    }
  }

  private String stripParen(String str) {
	  String trimmed = str.trim();
	  return trimmed.substring(1, trimmed.length() - 1);
  }

  private double[] extractPoints(String context) {
	  return Arrays.stream(context.split("(\\s|,\\s|,)")).map(Double::parseDouble).mapToDouble(x -> x).toArray();
  }

  private GeometryType extractGeometryType(String context) {
	  if (context.contains("(")) {
      return GeometryType.valueOf(context.substring(0, context.indexOf('(')).trim().toUpperCase());
    } else {
	    return GeometryType.valueOf(context.substring(0, context.indexOf(' ')).trim().toUpperCase());
    }
  }

}

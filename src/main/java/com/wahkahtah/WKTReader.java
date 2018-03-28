package com.wahkahtah;

import com.sinergise.geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WKTReader {
	
	/**
	 * Transforms the input WKT-formatted String into Geometry object
	 */
	public Geometry read(String inputString) {
	  Geometry resultGeometry = null;
    String geometryString = extractGeometryText(inputString);
	  switch (extractGeometryType(inputString)) {
      case POINT:
        if (geometryString==null) {
          resultGeometry = new Point();
        } else {
          double[] points = extractPoints(geometryString);
          resultGeometry = new Point(points[0], points[1]);
        }
        break;
      case MULTIPOINT:
        if (geometryString==null) {
          resultGeometry = new MultiPoint();
        } else {
          String[] pointStrings = extractLineStrings(stripParen(geometryString));
          double[] pointsArray = new double[pointStrings.length*2];
          for (int i=0; i<pointStrings.length; i++) {
            double[] onePoint = extractPoints(pointStrings[i]);
            pointsArray[i*2] = onePoint[0];
            pointsArray[i*2+1] = onePoint[1];
          }
          Point[] points = new Point[pointsArray.length/2];
          for (int i=0; i<points.length; i++) {
            points[i] = new Point(pointsArray[i*2], pointsArray[i*2+1]);
          }
          resultGeometry = new MultiPoint(points);
        }
        break;
      case LINESTRING:
        if (geometryString==null) {
          resultGeometry = new LineString();
        } else {
          double[] points = extractPoints(geometryString);
          resultGeometry = new LineString(points);
        }
        break;
      case MULTILINESTRING:
        if (geometryString==null) {
          resultGeometry = new MultiLineString();
        } else {
          String[] lineStringsStr = extractLineStrings(stripParen(geometryString));
          LineString[] lineStrings = new LineString[lineStringsStr.length];
          for (int i=0; i<lineStrings.length; i++) {
            lineStrings[i] = new LineString(extractPoints(lineStringsStr[i]));
          }
          resultGeometry = new MultiLineString(lineStrings);
        }
        break;
      case POLYGON:
        if (geometryString==null) {
          resultGeometry = new Polygon();
        } else {
          resultGeometry = createPolygon(geometryString);
        }
        break;
      case MULTIPOLYGON:
        if (geometryString==null) {
          resultGeometry = new MultiPolygon();
        } else {
          String[] polygonStrings = extractLineStrings(stripParen(geometryString));
          Polygon[] polygons = new Polygon[polygonStrings.length];

          for (int i=0; i<polygons.length; i++) {
            polygons[i] = createPolygon(polygonStrings[i]);
          }

          resultGeometry = new MultiPolygon(polygons);
        }
        break;
      case GEOMETRYCOLLECTION:
        if (geometryString==null) {
          resultGeometry = new GeometryCollection();
        } else {
          String[] geometriesStrings = extractGeometries(stripParen(geometryString));
          Geometry[] geometries = new Geometry[geometriesStrings.length];
          for(int i=0; i<geometries.length; i++) {
            geometries[i] = read(geometriesStrings[i]);
          }
          resultGeometry = new GeometryCollection<>(geometries);
        }
        break;
    }
		return resultGeometry;
	}

  /**
   * Extract geometry text array from geometrycollection text
   * @param str geometrycollection text
   * @return array of geometry text
   */
  private String[] extractGeometries(String str) {
    return extractLineStrings(str);
  }

  /**
   * Create polygon with provided polygon text
   * @param str polygon text
   * @return a polygon
   */
  private Polygon createPolygon(String str) {
    String[] lineStrings = extractLineStrings(stripParen(str));
    LineString body = new LineString( extractPoints(lineStrings[0]) );
    LineString[] holes = null;
    if (lineStrings.length > 1) {
      holes = new LineString[lineStrings.length - 1];
      for (int i = 1; i < lineStrings.length; i++) {
        holes[i-1] = new LineString( extractPoints(lineStrings[i]) );
      }
    }
    return new Polygon(body, holes);
  }

  /**
   * Splits a string on commas, but only on top level of parenthesis, any commas nested in parenthesis are ignored
   * ex: "1,2,3" -> ["1", "2", "3"]
   * ex": "1,(2,3)" -> ["1", "(2,3)"]
   * @param context line string text
   * @return array of points
   */
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

  /**
   * Extract the geometry text from the string
   *
   * @param context geometry text. ex. "POINT ( 1 2 )"
   * @return for example above "( 1 2 )"
   */
  private String extractGeometryText(String context) {
	  if (!context.contains("(") && context.contains("EMPTY")) {
	    return null;
    } else {
      return context.substring(context.indexOf('(')).trim();
    }
  }

  /**
   * Strip the outermost level of parenthesis
   *
   * @param str string with balanced parenthesis. ex. "(( 1 2 ),( 3 4 ))"
   * @return for example above "( 1 2 ),( 3 4 )"
   */
  private String stripParen(String str) {
    if (str == null || !str.contains("(") || !str.contains(")")) {
      throw new RuntimeException("String you are trying to strip of parenthesis does not have any.");
    }
	  String trimmed = str.trim();
	  return trimmed.substring(trimmed.indexOf('(')+1, trimmed.lastIndexOf(')'));
  }

  /**
   * Extract points from "point text"
   *
   * @param pointText ex. (1 2) or (-1.2 5E3)
   * @return double[] with x as [0] element and y as [1] element; for example above double[]{-1.2, 500.0}
   */
  private double[] extractPoints(String pointText) {
	  return Arrays.stream(stripParen(pointText).split("(\\s|,\\s|,)")).map(Double::parseDouble).mapToDouble(x -> x).toArray();
  }

  /**
   * Will extract geometryType
   *
   * @param context whole geometry tagged text. ex. "POINT ( 1 2 )"
   * @return the found {@link GeometryType} for example above GeometryType.POINT
   */
  private GeometryType extractGeometryType(String context) {
    if (context == null) {
      throw new RuntimeException("Wrong input parameters, context must contain a valid geometry tagged text.");
    } else if (context.contains("(")) {
      return GeometryType.valueOf(context.trim().substring(0, context.indexOf('(')).trim().toUpperCase());
    } else {
	    return GeometryType.valueOf(context.trim().substring(0, context.indexOf(' ')).trim().toUpperCase());
    }
  }

}

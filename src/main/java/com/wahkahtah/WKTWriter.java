package com.wahkahtah;

import com.sinergise.geometry.*;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WKTWriter {

  private static final String EMPTY = "EMPTY";

	/**
	 * Transforms the input Geometry object into WKT-formatted String. e.g.
	 * <pre><code>
	 * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
	 * //returns "LINESTRING (30 10, 10 30, 40 40)"
	 * </code></pre>
	 */
  public String write(Geometry geom) {
    String rval = "";
    if (geom instanceof LineString) {
      rval += GeometryType.LINESTRING + " ";
      rval += outputLineString((LineString) geom);

    } else if (geom instanceof Point) {
      rval += outputPointWithLabel((Point) geom);

    } else if (geom instanceof MultiLineString) {
      rval += GeometryType.MULTILINESTRING + " ";
      if (geom.isEmpty()) {
        rval += EMPTY;
      } else {
        rval += "(" + outputMultiLineString((MultiLineString) geom) + ")";
      }

    } else if (geom instanceof MultiPoint) {
      rval += GeometryType.MULTIPOINT + " ";
      if (geom.isEmpty()) {
        rval += EMPTY;
      } else {
        rval += "(" + outputMultiPoint((MultiPoint) geom) + ")";
      }

    } else if (geom instanceof Polygon) {
      rval += GeometryType.POLYGON + " ";
      if (geom.isEmpty()) {
        rval += EMPTY;
      } else {
        rval += "(" + outputPolygon((Polygon) geom) + ")";
      }

    } else if (geom instanceof MultiPolygon) {
      rval += GeometryType.MULTIPOLYGON + " ";
      if (geom.isEmpty()) {
        rval += EMPTY;
      } else {
        rval += "(" + outputMultiPolygon((MultiPolygon) geom) + ")";
      }

    } else if (geom instanceof GeometryCollection) {
      rval += GeometryType.GEOMETRYCOLLECTION + " ";
      if (geom.isEmpty()) {
        rval += EMPTY;
      } else {
        rval += outputGeometryCollection((GeometryCollection) geom);
      }

    }

    return rval;
  }

  /**
   * Output the geometrycollection text
   * @param geometryCollection
   * @return
   */
  private String outputGeometryCollection(GeometryCollection<Geometry> geometryCollection) {
    if (geometryCollection.size() == 0) {
      return EMPTY;
    }
    StringBuilder sb = new StringBuilder("(");
    for (Iterator<Geometry> it = geometryCollection.iterator(); it.hasNext(); ) {
      Geometry innerGeometry = it.next();
      sb.append(write (innerGeometry));
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * Output the multipolygon text part inside parenthesis
   * @param multiPolygon
   * @return
   */
  private String outputMultiPolygon(MultiPolygon multiPolygon) {
    if (multiPolygon.size() == 0) {
      return EMPTY;
    }
    return iterator(multiPolygon.iterator())
        .map(poly -> "(" + outputPolygon(poly) + ")")
        .collect(Collectors.joining(",\n"));
  }

  /**
   * Output the multipoint text part inside parenthesis
   * @param multiPoint
   * @return
   */
  private String outputMultiPoint(MultiPoint multiPoint) {
    if (multiPoint.size() == 0) {
      return EMPTY;
    }
    return iterator(multiPoint.iterator())
        .map(point -> "(" + outputPoint(point.getX(), point.getY()) + ")")
        .collect(Collectors.joining(", "));
  }

  /**
   * Output the multilinestring text part inside parenthesis
   * @param multiLineString
   * @return
   */
  private String outputMultiLineString(MultiLineString multiLineString) {
    if (multiLineString.size() == 0) {
      return EMPTY;
    }
    StringBuilder sb = new StringBuilder();
    for (Iterator<LineString> it = multiLineString.iterator(); it.hasNext(); ) {
      LineString lineString = it.next();
      sb.append( outputLineString(lineString) );
      if (it.hasNext()) {
        sb.append(",\n");
      }
    }
    return sb.toString();
  }

  /**
   * Output the polygon text part inside parenthesis
   * @param polygon
   * @return
   */
  private String outputPolygon(Polygon polygon) {
    StringBuilder sb = new StringBuilder();
    LineString outer = polygon.getOuter();
    sb.append(outputLineString(outer));
    for (int i=0; i < polygon.getNumHoles(); i++) {
      sb.append(",\n");
      sb.append(outputLineString(polygon.getHole(i)));
    }
    return sb.toString();
  }

  /**
   * output point
   * @param x
   * @param y
   * @return
   */
  private String outputPoint(double x, double y) {
    return string (x) + " " + string (y);
  }

  /**
   * Ouput the point text
   * @param point
   * @return
   */
  private String outputPointWithLabel(Point point) {
    if (point.isEmpty()) {
      return GeometryType.POINT + " " + EMPTY;
    }
    return GeometryType.POINT + " (" + outputPoint(point.getX(), point.getY()) + ")";
  }

  /**
   * Output the linestring text
   * @param lineString
   * @return
   */
  private String outputLineString(LineString lineString) {
    if (lineString.isEmpty()) {
      return EMPTY;
    }
    StringBuilder sb = new StringBuilder("(");
    sb.append( outputPoint(lineString.getX(0), lineString.getY(0)) );
    for (int i = 1; i < lineString.getNumCoords(); i++) {
      sb.append(", ");
      sb.append( outputPoint(lineString.getX(i), lineString.getY(i)) );
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * Output the double number, will strip decimal comma when value is an integer
   * @param number
   * @return
   */
  private String string(Double number) {
    if (Double.isFinite(number) && Double.compare(number, StrictMath.rint(number)) == 0) {
      return "" + number.longValue();
    }
    return number.toString();
  }

  private static <T> Stream<T> iterator(Iterator<T> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }
}

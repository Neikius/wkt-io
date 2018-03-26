package com.wahkahtah;

import com.sinergise.geometry.*;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WKTWriter {

      /*
		return write(
				new GeometryCollection<Geometry>(
				    new Geometry[]{
				        new Point(4,6),
                new LineString(new double[] {4,6,7,10})
				    })
    );
    */

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
      rval += "LINESTRING ";
      rval += outputLineString((LineString) geom);
    } else if (geom instanceof Point) {
      rval += outputPointWithLabel((Point) geom);
    } else if (geom instanceof MultiLineString) {
      rval += "MULTILINESTRING ";
      if (geom.isEmpty())
        rval += "EMPTY";
      else
      rval += "(" + outputMultiLineString((MultiLineString) geom) + ")";
    } else if (geom instanceof MultiPoint) {
      rval += "MULTIPOINT ";
      if (geom.isEmpty())
        rval += "EMPTY";
      else
      rval += "(" + ouputMultiPoint((MultiPoint) geom) + ")";
    } else if (geom instanceof Polygon) {
      rval += "POLYGON ";
      if (geom.isEmpty())
        rval += "EMPTY";
      else
      rval += "(" + outputPolygon((Polygon) geom) + ")";
    } else if (geom instanceof MultiPolygon) {
      rval += "MULTIPOLYGON ";
      if (geom.isEmpty())
        rval += "EMPTY";
      else
      rval += "(" + outputMultiPolygon((MultiPolygon) geom) + ")";
    } else if (geom instanceof GeometryCollection) {
      rval += "GEOMETRYCOLLECTION ";
      if (geom.isEmpty())
        rval += "EMPTY";
      else
      rval += "(" + outputGeometryCollection((GeometryCollection) geom) + ")";
    }

    return rval;
  }

  private String outputGeometryCollection(GeometryCollection<Geometry> geometryCollection) {
    if (geometryCollection.size() == 0) {
      return "EMPTY";
    }
    StringBuilder sb = new StringBuilder();
    for (Iterator<Geometry> it = geometryCollection.iterator(); it.hasNext(); ) {
      Geometry innerGeometry = it.next();
      sb.append(write (innerGeometry));
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  private String outputMultiPolygon(MultiPolygon multiPolygon) {
    if (multiPolygon.size() == 0) {
      return "EMPTY";
    }
    return iterator(multiPolygon.iterator())
        .map(poly -> "(" + outputPolygon(poly) + ")")
        .collect(Collectors.joining(",\n"));
  }

  private String ouputMultiPoint(MultiPoint multiPoint) {
    if (multiPoint.size() == 0) {
      return "EMPTY";
    }
    return iterator(multiPoint.iterator())
        .map(point -> "(" + outputPoint(point.getX(), point.getY()) + ")")
        .collect(Collectors.joining(", "));
  }

  private String outputMultiLineString(MultiLineString multiLineString) {
    if (multiLineString.size() == 0) {
      return "EMPTY";
    }
    String rval = "";
    for (Iterator<LineString> it = multiLineString.iterator(); it.hasNext(); ) {
      LineString lineString = it.next();
      rval += outputLineString(lineString);
      if (it.hasNext()) {
        rval += ",\n";
      }
    }
    return rval;
  }

  private String outputPolygon(Polygon polygon) {
    String rval = "";
    LineString outer = polygon.getOuter();
    rval += outputLineString(outer);
    for (int i=0; i < polygon.getNumHoles(); i++) {
      LineString hole = polygon.getHole(i);
      rval += ",\n";
      rval += outputLineString(polygon.getHole(i));
    }
    return rval;
  }

  private String outputPoint(double[] point) {
    if (point == null) {
      return "EMPTY";
    } else {
      return outputPoint(point[0], point[1]);
    }
  }

  private String outputPoint(double x, double y) {
    return string (x) + " " + string (y);
  }

  private String outputPointWithLabel(Point point) {
    if (point.isEmpty()) {
      return "POINT EMPTY";
    } else {
      return "POINT (" + outputPoint(point.getX(), point.getY()) + ")";
    }
  }

  private String outputLineString(LineString lineString) {
    String repr = "";
    if (lineString.isEmpty()) {
      repr += "EMPTY";
    } else {
      repr += "(";
      for (int i = 0; i < lineString.getNumCoords(); i++) {
        repr += outputPoint(lineString.getX(i), lineString.getY(i));
        if (i < lineString.getNumCoords() - 1) {
          repr += ", ";
        }
      }
      repr += ")";
    }
    return repr;
  }

  private String string(Double number) {
    if (Double.isFinite(number) && Double.compare(number, StrictMath.rint(number)) == 0) {
      return "" + number.longValue();
    } else {
      return number.toString();
    }
  }

  public static <T> Stream<T> iterator(Iterator<T> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }

  public static <T> Stream<T> spliterator(Spliterator<T> spliterator) {
    return StreamSupport.stream(spliterator, false);
  }
}

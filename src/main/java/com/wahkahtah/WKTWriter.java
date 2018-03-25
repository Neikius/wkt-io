package com.wahkahtah;

import com.sinergise.geometry.*;

import java.util.Iterator;

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
      rval += outputLineStringWithLabel((LineString) geom);
    } else if (geom instanceof Point) {
      rval += outputPointWithLabel((Point) geom);
    } else if (geom instanceof MultiLineString) {
      MultiLineString multiLineString = (MultiLineString) geom;
      rval += "MULTILINESTRING (";
      for (Iterator<LineString> it = multiLineString.iterator(); it.hasNext(); ) {
        LineString lineString = it.next();
        rval += outputLineString(lineString);
        if (it.hasNext()) {
          rval += ",\n";
        }
      }
      rval += ")";
    } else if (geom instanceof GeometryCollection) {
      GeometryCollection<Geometry> geometryCollection = (GeometryCollection) geom;
      rval += "GEOMETRYCOLLECTION (";
      for (Iterator<Geometry> it = geometryCollection.iterator(); it.hasNext(); ) {
        Geometry innerGeometry = it.next();
        rval += write (innerGeometry);
        if (it.hasNext()) {
          rval += ", ";
        }
      }
      rval += ")";
    } else if (geom instanceof Polygon) {
      Polygon polygon = (Polygon) geom;
      rval += "POLYGON (";
      LineString outer = polygon.getOuter();
      rval += outputLineString(outer);
      for (int i=0; i < polygon.getNumHoles(); i++) {
        LineString hole = polygon.getHole(i);
        rval += polygon.getHole(i);
      }
      rval += ")";
    }

    return rval;
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

  private String outputLineStringWithLabel(LineString lineString) {
    String repr = "";
    repr += "LINESTRING ";
    repr += outputLineString(lineString);
    return repr;
  }

  private String string(Double number) {
    if (Double.isFinite(number) && Double.compare(number, StrictMath.rint(number)) == 0) {
      return "" + number.longValue();
    } else {
      return number.toString();
    }
  }
}

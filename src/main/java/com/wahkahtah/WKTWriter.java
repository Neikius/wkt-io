package com.wahkahtah;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;

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
      rval += outputLineString((LineString) geom);
    } else if (geom instanceof Point) {
      rval += outputPoint((Point) geom);
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
    }

    return rval;
  }

  private String outputPoint(Point point) {
    return "POINT (" + string (point.getX() ) + " " + string ( point.getY() ) + ")";
  }

  private String outputLineString(LineString lineString) {
    String repr = "";
    repr += "LINESTRING ";
    if (lineString.isEmpty()) {
      repr += "EMPTY";
    } else {
      repr += "(";
      for (int i = 0; i < lineString.getNumCoords(); i++) {
        repr += string( lineString.getX(i) ) + " " + string ( lineString.getY(i) );
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
}

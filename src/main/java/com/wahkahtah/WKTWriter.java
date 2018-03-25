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
	    LineString lineString = (LineString) geom;
	    rval += "LINESTRING ";
	    if (lineString.isEmpty()) {
	      rval += "EMPTY";
      } else {
	      rval += "(";
        for (int i = 0; i < lineString.getNumCoords(); i++) {
          rval += (int) lineString.getX(i) + " " + (int) lineString.getY(i) ;
          if (i < lineString.getNumCoords() - 1) {
            rval += ", ";
          }
        }
        rval += ")";
      }
    }

    return rval;
	}

}

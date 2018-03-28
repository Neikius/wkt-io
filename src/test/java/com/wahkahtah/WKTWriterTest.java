package com.wahkahtah;


import com.sinergise.geometry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class WKTWriterTest {

  private WKTWriter wktWriter;

  @BeforeEach
  public void init() {
    wktWriter = new WKTWriter();
  }

  @Test
  public void writeLineString() {
    assertEquals("LINESTRING (30 10, 10 30, 40 40)",  wktWriter.write(new LineString(new double[]{30, 10, 10, 30, 40, 40})) );
  }

  @Test
  public void writeLineStringEmpty() {
    assertEquals("LINESTRING EMPTY", wktWriter.write(new LineString()));
  }

  @Test
  public void writePoint() {
    assertEquals("POINT (30 10)", wktWriter.write(new Point(30, 10)));
  }

  @Test
  public void pointDoubles() {
    assertEquals("POINT (-5.5 1.23)", wktWriter.write(new Point(-5.5d, 1.23)));
  }

  @Test
  public void writeGeometryCollection() {
    assertEquals("GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))",
        wktWriter.write(new GeometryCollection<Geometry>(new Geometry[]{new Point(4,6), new LineString(new double[] {4,6,7,10})})));
  }

  @Test
  public void multiLineString() {
    assertEquals(
        "MULTILINESTRING ((10 10, 20 20, 10 40),\n" +
        "(40 40, 30 30, 40 20, 30 10))",
        wktWriter.write(new MultiLineString(new LineString[]{
            new LineString(new double[]{10, 10, 20, 20, 10, 40}),
            new LineString(new double[]{40, 40, 30, 30, 40, 20, 30, 10})
        })));
  }

  @Test
  public void polygon() {
    assertEquals(
        "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))",
        wktWriter.write(new Polygon(new LineString(new double[]{30, 10, 10, 20, 20, 40, 40, 40, 30, 10}),null))
    );
  }

  @Test
  public void polygonWithHoles() {
    assertEquals(
        "POLYGON ((35 10, 10 20, 15 40, 45 45, 35 10),\n" +
            "(20 30, 35 35, 30 20, 20 30))",
        wktWriter.write(new Polygon(
            new LineString(new double[]{35, 10, 10, 20, 15, 40, 45, 45, 35, 10}),
            new LineString[]{
              new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30})
            }))
    );
  }

  @Test
  public void multiPoint() {
    assertEquals("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))",
        wktWriter.write(new MultiPoint(new Point[]{new Point(10, 40), new Point(40, 30), new Point(20, 20), new Point(30, 10)})));
  }

  @Test
  public void multiPolygon() {
    assertEquals("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20),\n" +
            "(15 5, 40 10, 10 20, 5 10, 15 5)))",
        wktWriter.write(new MultiPolygon(new Polygon[]{new Polygon(
            new LineString(new double[]{30, 20, 10, 40, 45, 40, 30, 20}),
            new LineString[]{
                new LineString(new double[]{15, 5, 40, 10, 10, 20, 5, 10, 15, 5})
            }
        )})));
  }


  @Test
  public void multiPolygon2() {
    assertEquals("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),\n" +
            "((15 5, 40 10, 10 20, 5 10, 15 5)))",
        wktWriter.write(new MultiPolygon(new Polygon[]{
            new Polygon(new LineString(new double[]{30, 20, 10, 40, 45, 40, 30, 20}),null),
            new Polygon(new LineString(new double[]{15, 5, 40, 10, 10, 20, 5, 10, 15, 5}), null)
        })));
  }
}

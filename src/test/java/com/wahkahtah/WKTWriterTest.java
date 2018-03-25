package com.wahkahtah;


import com.sinergise.geometry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

package com.wahkahtah;


import com.sinergise.geometry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class WKTReaderTest {

  private WKTReader wktReader;

  @BeforeEach
  public void init() {
    wktReader = new WKTReader();
  }

  @Test
  public void readLineString() {
    assertEquals(new LineString(new double[]{30, 10, 10, 30, 40, 40}), wktReader.read("LINESTRING (30 10, 10 30, 40 40)"));
  }

  @Test
  public void readLineStringEmpty() {
    assertEquals(new LineString(), wktReader.read("LINESTRING EMPTY"));
  }

  @Test
  public void readPoint() {
    assertEquals(new Point(30, 10), wktReader.read("POINT (30 10)"));
  }

  @Test
  public void pointDoubles() {
    assertEquals(new Point(-5.5d, 1230.0d), wktReader.read("POINT (-5.5 1.23E3)"));
  }

  @Test
  public void readGeometryCollection() {
    assertEquals(new GeometryCollection<Geometry>(new Geometry[]{new Point(4,6), new LineString(new double[] {4,6,7,10})}),
        wktReader.read("GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))"));
  }

  @Test
  public void readGeometryCollection2() {
    assertEquals(new GeometryCollection<Geometry>(new Geometry[]{
            new Point(10, 10),
            new Point(30, 30),
            new LineString(new double[]{15, 15, 20, 20})
      }),
        wktReader.read("GeometryCollection\n" +
            "(\n" +
            "POINT (10 10),\n" +
            "POINT (30 30),\n" +
            "LINESTRING (15 15, 20 20)\n" +
            ")\n"));
  }

  @Test
  public void multiLineString() {
    assertEquals(new MultiLineString(new LineString[]{
            new LineString(new double[]{10, 10, 20, 20, 10, 40}),
            new LineString(new double[]{40, 40, 30, 30, 40, 20, 30, 10})
        }),
        wktReader.read("MULTILINESTRING ((10 10, 20 20, 10 40),\n" +
            "(40 40, 30 30, 40 20, 30 10))"));
  }

  @Test
  public void polygon() {
    assertEquals(new Polygon(new LineString(new double[]{30, 10, 10, 20, 20, 40, 40, 40, 30, 10}),null),
        wktReader.read("POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))")
    );
  }

  @Test
  public void polygonWithHoles() {
    assertEquals(new Polygon(
        new LineString(new double[]{35, 10, 10, 20, 15, 40, 45, 45, 35, 10}),
        new LineString[]{
            new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30})
        }),
        wktReader.read("POLYGON ((35 10, 10 20, 15 40, 45 45, 35 10),\n" +
            "(20 30, 35 35, 30 20, 20 30))"));
  }

  @Test
  public void multiPoint() {
    assertEquals(new MultiPoint(new Point[]{new Point(10, 40), new Point(40, 30), new Point(20, 20), new Point(30, 10)}),
        wktReader.read("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))"));
  }

  @Test
  public void multiPolygon() {
    assertEquals(new MultiPolygon(new Polygon[]{new Polygon(
            new LineString(new double[]{30, 20, 10, 40, 45, 40, 30, 20}),
            new LineString[]{
                new LineString(new double[]{15, 5, 40, 10, 10, 20, 5, 10, 15, 5})
            }
        )}),
        wktReader.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20),\n" +
            "(15 5, 40 10, 10 20, 5 10, 15 5)))"));
  }


  @Test
  public void multiPolygon2() {
    assertEquals(new MultiPolygon(new Polygon[]{
            new Polygon(new LineString(new double[]{30, 20, 10, 40, 45, 40, 30, 20}),null),
            new Polygon(new LineString(new double[]{15, 5, 40, 10, 10, 20, 5, 10, 15, 5}), null)
        }),
        wktReader.read("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)),\n" +
            "((15 5, 40 10, 10 20, 5 10, 15 5)))"));
  }

}

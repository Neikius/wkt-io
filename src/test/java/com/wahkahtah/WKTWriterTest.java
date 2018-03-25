package com.wahkahtah;


import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;
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

}

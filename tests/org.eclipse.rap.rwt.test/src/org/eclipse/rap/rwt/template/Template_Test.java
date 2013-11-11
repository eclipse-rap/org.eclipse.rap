/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.template.TemplateSerializer;
import org.junit.Test;


public class Template_Test {

  @Test
  public void testAddsCell() {
    Template template = new Template();
    Cell cell = mock( Cell.class );

    template.addCell( cell );

    List<Cell<?>> cells = template.getCells();
    assertEquals( 1, cells.size() );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testAddsCells() {
    Template template = new Template();
    Cell cell1 = mock( Cell.class );
    Cell cell2 = mock( Cell.class );

    template.addCell( cell1 );
    template.addCell( cell2 );

    List<Cell<?>> cells = template.getCells();
    assertEquals( 2, cells.size() );
    assertSame( cell1, cells.get( 0 ) );
    assertSame( cell2, cells.get( 1 ) );
  }

  @Test
  public void testCellsArrayIsASafeCopy() {
    Template template = new Template();
    List<Cell<?>> cells = template.getCells();

    template.addCell( mock( Cell.class ) );

    assertEquals( 0, cells.size() );
  }

  @Test
  public void testTemplateSerializer_toJson() {
    Template template = new Template();
    new TextCell( template );
    new ImageCell( template );

    JsonValue json = template.getAdapter( TemplateSerializer.class ).toJson();

    JsonObject textCellJson = new JsonObject().add( "type", "text" );
    JsonObject imageCellJson = new JsonObject().add( "type", "image" );
    JsonArray expected = new JsonArray().add( textCellJson ).add( imageCellJson );
    assertEquals( expected, json );
  }

}

/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tableitemkit;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public class RichTextToHtmlTransformer_Test extends TestCase {

  private RichTextToHtmlTransformer transformer;
  private TableItem tableItem;
  private Map<String,Image> imageMap;

  public void testParseWithEmptyText() {
    RichTextParser parser = new RichTextParser( transformer );
    
    parser.parse( "<html></html>" );
    
    assertEquals( "<div style=\"line-height:normal;\"></div>", transformer.getHtml() );
  }

  public void testParseWithReservedXmlEntities() {
    RichTextParser parser = new RichTextParser( transformer );
    try {
      parser.parse( "<html>3 <> 2 & \"</html>" );
    } catch( RichTextParserException expected ) {
    }
  }
  
  public void testParseWithImgElement() throws IOException {
    RichTextParser parser = new RichTextParser( transformer );
    Image image = loadImage( Fixture.IMAGE_100x50 );
    imageMap.put( "foo", image );

    parser.parse( "<html><img src=\"foo\"/></html>" );
    
    String expected
      = "<div style=\"line-height:normal;\">" 
      + "<img src=\"rwt-resources/generated/866f5ced\" width=\"100px\" height=\"50px\" />" 
      + "</div>";
    assertEquals( expected, transformer.getHtml() );
  }

  public void testParseWithFontElement() {
    RichTextParser parser = new RichTextParser( transformer );
    
    parser.parse( "<html><font name=\"font-name\" height=\"1\">foo</font></html>" );
    
    String expected
      = "<div style=\"line-height:normal;\">" 
      + "<span style=\"font-family:font-name;font-size:1px\">foo</span>" 
      + "</div>";
    assertEquals( expected, transformer.getHtml() );
  }
  
  public void testParseWithFontElementWithQuotedFontName() {
    RichTextParser parser = new RichTextParser( transformer );
    
    parser.parse( "<html><font name='\"font name\"' height=\"1\">foo</font></html>" );
    
    String expected
      = "<div style=\"line-height:normal;\">" 
      + "<span style=\"font-family:&quot;font name&quot;;font-size:1px\">foo</span>" 
      + "</div>";
    assertEquals( expected, transformer.getHtml() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display );
    imageMap = new HashMap<String,Image>();
    Table table = new Table( shell, SWT.NONE );
    table.setData( Table.IMAGE_MAP, imageMap );
    tableItem = new TableItem( table, SWT.NONE );
    transformer = new RichTextToHtmlTransformer( tableItem );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private Image loadImage( String name ) throws IOException {
    ClassLoader classLoader = Fixture.class.getClassLoader();
    InputStream imageStream = classLoader.getResourceAsStream( name );
    Image result = new Image( tableItem.getDisplay(), imageStream );
    imageStream.close();
    return result;
  }
}

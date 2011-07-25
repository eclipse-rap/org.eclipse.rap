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
package org.eclipse.swt.internal.widgets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.RichTextUtil.IImageSizeProvider;
import org.eclipse.swt.widgets.Display;


public class RichTextUtil_Test extends TestCase {
  
  private Display device;
  private Font font;

  public void testGetTextWidthWithEmptyString() {
    int width = RichTextUtil.getTextWidth( "", font, null );

    assertEquals( 0, width );
  }
  
  public void testGetTextWidthMultilineRichText() {
    String shortLine = "short line";
    String longLine = "very very long line";
    String text = "<html>" + shortLine + "<br />" + longLine + "</html>";

    int longLineWidth = RichTextUtil.getTextWidth( longLine, font, null );
    int width = RichTextUtil.getTextWidth( text, font, null );
    
    assertEquals( width, longLineWidth );
  }
  
  public void testGetTextWidthWithPlainText() {
    int width = RichTextUtil.getTextWidth( "foo", font, null );

    assertTrue( width > 0 );
  }
  
  public void testGetTextWiddthWithDifferentFonts() {
    String string = "xxxxxxxx";
    String text
      = "<html>" 
      + string 
      + "<font name=\"font-name\" height=\"50\">" 
      + string 
      + "</font></html>";
    Font largeFont = new Font( device, "font-name", 50, SWT.NORMAL );

    int expectedWidth 
      = RichTextUtil.getTextWidth( string, font, null ) 
      + RichTextUtil.getTextWidth( string, largeFont, null );
    int width = RichTextUtil.getTextWidth( text, font, null );
    
    assertEquals( expectedWidth, width );
  }
  
  public void testGetTextWidthWithImage() {
    Rectangle imageSize = new Rectangle( 0, 0, 10, 20 );
    IImageSizeProvider imageSizeProvider = mock( IImageSizeProvider.class );
    when( imageSizeProvider.getImageSize( anyString() ) ).thenReturn( imageSize );

    String text = "<html><img src=\"\"/></html>";
    int width = RichTextUtil.getTextWidth( text, font, imageSizeProvider ); 
    
    assertEquals( 10, width );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new Display();
    font = new Font( device, "x", 12, SWT.NORMAL );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}

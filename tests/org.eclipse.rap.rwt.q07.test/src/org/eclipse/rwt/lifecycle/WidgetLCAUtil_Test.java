/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class WidgetLCAUtil_Test extends TestCase {

  public void testHasChanged() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    // test initial behaviour, text is same as default value -> no 'change'
    text.setText( "" );
    boolean hasChanged;
    hasChanged = WidgetLCAUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( false, hasChanged );
    // test initial behaviour, text is different as default value -> 'change'
    text.setText( "other value" );
    hasChanged = WidgetLCAUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( true, hasChanged );
    // test subsequent behaviour (when already initialized)
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( false, hasChanged );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setText( "whatsoevervaluehasbeensetduringrequest" );
    hasChanged = WidgetLCAUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( true, hasChanged );
  }

  public void testHasChangedWidthArrays() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    List list = new List( shell, SWT.MULTI );

    boolean hasChanged;
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", new String[] { "a" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", new String[] { "a" } );
    assertEquals( false, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", new String[] { "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged
      = WidgetLCAUtil.hasChanged( list, "items", new String[] { "a", "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", null );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "selection", new int[] { 0, 1, 4 } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    RWTFixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "selection", new int[] { 0, 1, 2 } );
    assertEquals( false, hasChanged );
  }

  public void testEquals() {
    assertTrue( WidgetLCAUtil.equals( null, null ) );
    assertFalse( WidgetLCAUtil.equals( null, "1" ) );
    assertFalse( WidgetLCAUtil.equals( "1", null ) );
    assertFalse( WidgetLCAUtil.equals( "1", "2" ) );
    assertTrue( WidgetLCAUtil.equals( "1", "1" ) );
    assertTrue( WidgetLCAUtil.equals( new String[] { "1" },
                                   new String[] { "1" } ) );
    assertTrue( WidgetLCAUtil.equals( new int[] { 1 },
                                   new int[] { 1 } ) );
    assertTrue( WidgetLCAUtil.equals( new boolean[] { true },
                                   new boolean[] { true } ) );
    assertTrue( WidgetLCAUtil.equals( new long[] { 232 },
                                   new long[] { 232 } ) );
    assertTrue( WidgetLCAUtil.equals( new float[] { 232 },
                                   new float[] { 232 } ) );
    assertTrue( WidgetLCAUtil.equals( new double[] { 345 },
                                   new double[] { 345 } ) );
    assertTrue( WidgetLCAUtil.equals( new Date[] { new Date( 1 ) },
                                   new Date[] { new Date( 1 ) } ) );
    assertFalse( WidgetLCAUtil.equals( new double[] { 345 },
                                    new float[] { 345 } ) );
    assertFalse( WidgetLCAUtil.equals( new int[] { 345 },
                                    new float[] { 345 } ) );
    assertFalse( WidgetLCAUtil.equals( new int[] { 345 },
                                    new long[] { 345 } ) );
    assertFalse( WidgetLCAUtil.equals( new Date[] { new Date( 3 ) }, null ) );
  }

  public void testEscapeText() throws Exception {
    // Empty Parameter
    try {
      WidgetLCAUtil.escapeText( null, true );
      fail( "NullPointerException expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    // Text that goes unescaped
    assertEquals( "Test", WidgetLCAUtil.escapeText( "Test", false ) );
    assertEquals( "Test", WidgetLCAUtil.escapeText( "Test", true ) );
    assertEquals( "", WidgetLCAUtil.escapeText( "", false ) );
    assertEquals( "", WidgetLCAUtil.escapeText( "", true ) );
    // Brackets
    assertEquals( "&lt;", WidgetLCAUtil.escapeText( "<", false ) );
    assertEquals( "&gt;", WidgetLCAUtil.escapeText( ">", false ) );
    assertEquals( "&lt;&lt;&lt;", WidgetLCAUtil.escapeText( "<<<", false ) );
    String expected = "&lt;File &gt;";
    assertEquals( expected, WidgetLCAUtil.escapeText( "<File >", false ) );
    // Amp
    assertEquals( "&amp;&amp;&amp;File&quot;&gt;",
                  WidgetLCAUtil.escapeText( "&&&File\">", false ) );
//    assertEquals( "&amp;<u>F</u>ile&quot;&gt;",
//                  WidgetLCAUtil.escapeText( "&&&File\">", true ) );
    assertEquals( "Open &amp; Close",
                  WidgetLCAUtil.escapeText( "Open && Close", true ) );
    assertEquals( "E&lt;s&gt;ca'pe&quot; &amp; me",
                  WidgetLCAUtil.escapeText( "&E<s>ca'pe\" && me", true ) );
    // Quotes
    expected = "&quot;File&quot;";
    assertEquals( expected, WidgetLCAUtil.escapeText( "\"File\"", false ) );
    expected = "&quot;&quot;File";
    assertEquals( expected, WidgetLCAUtil.escapeText( "\"\"File", false ) );
    // Mnemonics with underline
//    assertEquals( "<u>F</u>ile", WidgetLCAUtil.escapeText( "&File", true ) );
//    assertEquals( "Fil<u>e</u>", WidgetLCAUtil.escapeText( "Fil&e", true ) );
//    assertEquals( "Open &amp; <u>C</u>lose",
//                  WidgetLCAUtil.escapeText( "&Open && &Close", true ) );
    // Wild combinations
//    assertEquals( "&amp;<u>F</u>ile",
//                  WidgetLCAUtil.escapeText( "&&&File", true ) );
//    assertEquals( "&quot;File' &amp; &lt;b&gt; <u>N</u>ew",
//                  WidgetLCAUtil.escapeText( "\"&File' && <b> &New", true ) );
    // Backslashes not modified
    expected = "Test\\";
    assertEquals( expected, WidgetLCAUtil.escapeText( "Test\\", false ) );
  }

  public void testTruncateZeros() throws Exception {
    assertEquals( ( char )0, "\000".charAt( 0 ) );
    assertEquals( "foo ", WidgetLCAUtil.escapeText( "foo \000 bar", false ) );
    assertEquals( "foo", WidgetLCAUtil.escapeText( "foo\000", false ) );
    assertEquals( "", WidgetLCAUtil.escapeText( "\000foo", false ) );
    assertEquals( "&lt;foo", WidgetLCAUtil.escapeText( "<foo\000>", false ) );
    assertEquals( "&lt;foo", WidgetLCAUtil.escapeText( "<foo\000>", true ) );
  }

  public void testParseFontName() {
    // IE doesn't like quoted font names (or whatever qooxdoo makes out of them)
    String systemFontName
      = "\"Segoe UI\", Corbel, Calibri, Tahoma, \"Lucida Sans Unicode\", "
      + "sans-serif";
    String[] fontNames = WidgetLCAUtil.parseFontName( systemFontName );
    assertEquals( 6, fontNames.length );
    assertEquals( "Segoe UI", fontNames[ 0 ] );
    assertEquals( "Corbel", fontNames[ 1 ] );
    assertEquals( "Calibri", fontNames[ 2 ] );
    assertEquals( "Tahoma", fontNames[ 3 ] );
    assertEquals( "Lucida Sans Unicode", fontNames[ 4 ] );
    assertEquals( "sans-serif", fontNames[ 5 ] );

    // Empty font names don't cause trouble (at least for the browsers
    // currently tested - therefore don't make extra effort to eliminate them
    fontNames = WidgetLCAUtil.parseFontName( "a, , b" );
    assertEquals( 3, fontNames.length );
    assertEquals( "a", fontNames[ 0 ] );
    assertEquals( "", fontNames[ 1 ] );
    assertEquals( "b", fontNames[ 2 ] );
  }

  public void testFontBold() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeFont( label, label.getFont() );
    assertTrue( Fixture.getAllMarkup().endsWith( ", false, false );" ) );

    Font oldFont = label.getFont();
    FontData fontData = oldFont.getFontData()[ 0 ];
    Font newFont = Graphics.getFont( fontData.getName(),
                                     fontData.getHeight(),
                                     SWT.BOLD );
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeFont( label, newFont );
    assertTrue( Fixture.getAllMarkup().endsWith( ", true, false );" ) );
  }

  public void testFontItalic() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeFont( label, label.getFont() );
    assertTrue( Fixture.getAllMarkup().endsWith( ", false, false );" ) );

    Font oldFont = label.getFont();
    FontData fontData = oldFont.getFontData()[ 0 ];
    Font newFont = Graphics.getFont( fontData.getName(),
                                     fontData.getHeight(),
                                     SWT.ITALIC );
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeFont( label, newFont );
    assertTrue( Fixture.getAllMarkup().endsWith( ", false, true );" ) );
  }

  public void testFontSize() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    Font oldFont = label.getFont();
    FontData fontData = oldFont.getFontData()[ 0 ];
    Font newFont = Graphics.getFont( fontData.getName(), 42, SWT.NORMAL );
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeFont( label, newFont );
    assertTrue( Fixture.getAllMarkup().endsWith( ", 42, false, false );" ) );
  }
 
  public void testWriteImage() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label item = new Label( shell, SWT.NONE );

    // for an un-initialized control: no image -> no markup
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeImage( item, item.getImage() );
    assertEquals( "", Fixture.getAllMarkup() );

    // for an un-initialized control: render image, if any
    Fixture.fakeResponseWriter();
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    WidgetLCAUtil.writeImage( item, item.getImage() );
    String expected = "w.setIcon( \""
                    + ResourceFactory.getImagePath( item.getImage() )
                    + "\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    // for an initialized control with change image: render it
    RWTFixture.markInitialized( item );
    RWTFixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    item.setImage( null );
    WidgetLCAUtil.writeImage( item, item.getImage() );
    assertTrue( Fixture.getAllMarkup().indexOf( "w.setIcon( null );" ) != -1 );
  }

  public void testWriteVariant() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeCustomVariant( label );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    label.setData( WidgetUtil.CUSTOM_VARIANT, "my_variant" );
    WidgetLCAUtil.writeCustomVariant( label );
    String expected = "w.addState( \"variant_my_variant\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteCustomVariant() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Label( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeCustomVariant( control );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    control.setData( WidgetUtil.CUSTOM_VARIANT, "my_variant" );
    WidgetLCAUtil.writeCustomVariant( control );
    String expected = "w.addState( \"variant_my_variant\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteBackground() throws Exception {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Label( shell, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );

    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    WidgetLCAUtil.writeBackground( control, null, false );
    assertEquals( "", Fixture.getAllMarkup() );
    RWTFixture.markInitialized( control );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, null, false );
    WidgetLCAUtil.writeBackground( control, null, true );
    String expected = "w.setBackgroundColor( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, null, true );
    WidgetLCAUtil.writeBackground( control, red, true );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, red, true );
    WidgetLCAUtil.writeBackground( control, red, false );
    expected = "w.setBackgroundColor( \"#ff0000\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, red, false );
    WidgetLCAUtil.writeBackground( control, null, false );
    expected = "w.resetBackgroundColor();";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteStyleFlag() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Label( shell, SWT.NONE );
    Control borderControl = new Label( shell, SWT.BORDER );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeStyleFlag( control, SWT.BORDER, "BORDER" );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeStyleFlag( borderControl, SWT.BORDER, "BORDER" );
    String expected = "w.addState( \"rwt_BORDER\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}

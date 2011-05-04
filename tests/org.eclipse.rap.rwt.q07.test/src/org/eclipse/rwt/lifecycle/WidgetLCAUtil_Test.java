/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.compositekit.CompositeLCA;
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
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( text, Props.TEXT, text.getText(), "" );
    assertEquals( false, hasChanged );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
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
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", new String[] { "a" } );
    assertEquals( false, hasChanged );

    list.setItems( new String[] { "a" } );
    Fixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", new String[] { "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    Fixture.preserveWidgets();
    hasChanged
      = WidgetLCAUtil.hasChanged( list, "items", new String[] { "a", "b" } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a" } );
    Fixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "items", null );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    Fixture.preserveWidgets();
    hasChanged = WidgetLCAUtil.hasChanged( list, "selection", new int[] { 0, 1, 4 } );
    assertEquals( true, hasChanged );

    list.setItems( new String[] { "a", "b", "c" } );
    list.setSelection( new int[] { 0, 1, 2 } );
    Fixture.preserveWidgets();
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

  public void testEscapeText() {
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
    assertEquals( "Open &amp; Close",
                  WidgetLCAUtil.escapeText( "Open && Close", true ) );
    assertEquals( "E&lt;s&gt;ca'pe&quot; &amp; me",
                  WidgetLCAUtil.escapeText( "&E<s>ca'pe\" && me", true ) );
    // Quotes
    expected = "&quot;File&quot;";
    assertEquals( expected, WidgetLCAUtil.escapeText( "\"File\"", false ) );
    expected = "&quot;&quot;File";
    assertEquals( expected, WidgetLCAUtil.escapeText( "\"\"File", false ) );
    // Backslashes not modified
    expected = "Test\\";
    assertEquals( expected, WidgetLCAUtil.escapeText( "Test\\", false ) );
    // Escape unicode characters \u2028 and \u2029 - see bug 304364
    expected = "abc&#8232;abc&#8233;abc";
    assertEquals( expected,
                  WidgetLCAUtil.escapeText( "abc\u2028abc\u2029abc", false ) );
  }

  public void testTruncateZeros() {
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
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeFont( label, label.getFont() );
    assertTrue( Fixture.getAllMarkup().endsWith( ", false, false );" ) );

    Font oldFont = label.getFont();
    FontData fontData = FontUtil.getData( oldFont );
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
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeFont( label, label.getFont() );
    assertTrue( Fixture.getAllMarkup().endsWith( ", false, false );" ) );

    Font oldFont = label.getFont();
    FontData fontData = FontUtil.getData( oldFont );
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
    Fixture.markInitialized( display );
    Font oldFont = label.getFont();
    FontData fontData = FontUtil.getData( oldFont );
    Font newFont = Graphics.getFont( fontData.getName(), 42, SWT.NORMAL );
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeFont( label, newFont );
    assertTrue( Fixture.getAllMarkup().endsWith( ", 42, false, false );" ) );
  }

  public void testFontReset() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    Font font = Graphics.getFont( "Arial", 12, SWT.BOLD );
    Fixture.markInitialized( label );
    WidgetLCAUtil.preserveFont( label, font );
    WidgetLCAUtil.writeFont( label, null );
    String expected = "var w = wm.findWidgetById( \"w2\" );w.resetFont();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testForegroundReset() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Fixture.fakeResponseWriter();
    Color red = Graphics.getColor( 255, 0, 0 );
    Fixture.markInitialized( label );
    WidgetLCAUtil.preserveForeground( label, red );
    WidgetLCAUtil.writeForeground( label, null );
    String expected = "var w = wm.findWidgetById( \"w2\" );w.resetTextColor();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteImage() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label item = new Label( shell, SWT.NONE );

    // for an un-initialized control: no image -> no markup
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeImage( item,
                              Props.IMAGE,
                              JSConst.QX_FIELD_ICON,
                              item.getImage() );
    assertEquals( "", Fixture.getAllMarkup() );

    // for an un-initialized control: render image, if any
    Fixture.fakeResponseWriter();
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    WidgetLCAUtil.writeImage( item,
                              Props.IMAGE,
                              JSConst.QX_FIELD_ICON,
                              item.getImage() );
    String expected = "w.setIcon( \""
                    + ImageFactory.getImagePath( item.getImage() )
                    + "\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    // for an initialized control with change image: render it
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    item.setImage( null );
    WidgetLCAUtil.writeImage( item, Props.IMAGE, JSConst.QX_FIELD_ICON, item.getImage() );
    assertTrue( Fixture.getAllMarkup().indexOf( "w.setIcon( null );" ) != -1 );
  }

  public void testWriteVariant() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
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
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeCustomVariant( control );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    control.setData( WidgetUtil.CUSTOM_VARIANT, "my_variant" );
    WidgetLCAUtil.writeCustomVariant( control );
    String expected = "w.addState( \"variant_my_variant\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveCustomVariant( control );
    control.setData( WidgetUtil.CUSTOM_VARIANT, "new_variant" );
    WidgetLCAUtil.writeCustomVariant( control );
    expected
      =   "w.removeState( \"variant_my_variant\" );w.addState( "
        + "\"variant_new_variant\" );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveCustomVariant( control );
    Fixture.markInitialized( control );
    control.setData( WidgetUtil.CUSTOM_VARIANT, null );
    WidgetLCAUtil.writeCustomVariant( control );
    expected = "w.removeState( \"variant_new_variant\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteBackground() throws Exception {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Label( shell, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );

    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeBackground( control, null, false );
    assertEquals( "", Fixture.getAllMarkup() );
    Fixture.markInitialized( control );

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

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, red, false );
    WidgetLCAUtil.writeBackground( control, red, false );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, null );
    WidgetLCAUtil.writeBackground( control, red );
    expected = "w.setBackgroundColor( \"#ff0000\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    WidgetLCAUtil.preserveBackground( control, red );
    WidgetLCAUtil.writeBackground( control, red );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testWriteMenu() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );

    // for an un-initialized control: no menu -> no markup
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    WidgetLCAUtil.writeMenu( label, label.getMenu() );
    assertEquals( "", Fixture.getAllMarkup() );

    // for an un-initialized control: render menu, if any
    Fixture.fakeResponseWriter();
    label.setMenu( new Menu( label ) );
    WidgetLCAUtil.writeMenu( label, label.getMenu() );
    String expected
      =   "var w = wm.findWidgetById( \"w2\" );w.setContextMenu( "
        + "wm.findWidgetById( \"w3\" ) );w.addEventListener( "
        + "\"contextmenu\", org.eclipse.rwt.widgets.Menu.contextMenuHandler );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    // for an initialized control with change menu: render it
    Fixture.markInitialized( label );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    label.setMenu( null );
    WidgetLCAUtil.writeMenu( label, label.getMenu() );
    expected
      =   "w.setContextMenu( null );w.removeEventListener( \"contextmenu\", "
        + "org.eclipse.rwt.widgets.Menu.contextMenuHandler );";
    assertEquals( expected, Fixture.getAllMarkup() );
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

  public void testWriteBackgroundGradient() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Composite( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    CompositeLCA lca = new CompositeLCA();
    lca.preserveValues( control );
    Fixture.markInitialized( control );
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };
    gfxAdapter.setBackgroundGradient( gradientColors, percents, true );
    lca.renderChanges( control );
    String expected
      = "wm.setBackgroundGradient"
      + "( wm.findWidgetById( \"w2\" ), [\"#00ff00\",\"#0000ff\" ], "
      + "[0,100 ], true );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    gradientColors = new Color[] {
      Graphics.getColor( 255, 0, 0 ),
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    percents = new int[] { 0, 50, 100 };
    gfxAdapter.setBackgroundGradient( gradientColors, percents, true );
    lca.renderChanges( control );
    expected
      = "wm.setBackgroundGradient"
      + "( wm.findWidgetById( \"w2\" ), [\"#ff0000\",\"#00ff00\",\"#0000ff\" ],"
      + " [0,50,100 ], true );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    lca.renderChanges( control );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    gfxAdapter.setBackgroundGradient( null, null, true );
    lca.renderChanges( control );
    expected
      = "wm.setBackgroundGradient"
      + "( wm.findWidgetById( \"w2\" ), null, null, true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteBackgroundGradient_Horizontal() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Composite( shell, SWT.NONE );

    Fixture.fakeResponseWriter();
    CompositeLCA lca = new CompositeLCA();
    lca.preserveValues( control );
    Fixture.markInitialized( control );
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color[] gradientColors = new Color[] {
      Graphics.getColor( 0, 255, 0 ),
      Graphics.getColor( 0, 0, 255 )
    };
    int[] percents = new int[] { 0, 100 };
    gfxAdapter.setBackgroundGradient( gradientColors, percents, false );
    lca.renderChanges( control );
    String expected
      = "wm.setBackgroundGradient"
      + "( wm.findWidgetById( \"w2\" ), [\"#00ff00\",\"#0000ff\" ], "
      + "[0,100 ], false );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteRoundedBorder() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Widget widget = new Composite( shell, SWT.NONE );

    CompositeLCA lca = new CompositeLCA();
    Fixture.fakeResponseWriter();
    lca.preserveValues( widget );
    Fixture.markInitialized( widget );
    Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color color = Graphics.getColor( 0, 255, 0 );
    graphicsAdapter.setRoundedBorder( 2, color, 5, 6, 7, 8 );
    lca.renderChanges( widget );
    String expected
      = "wm.setRoundedBorder"
      + "( wm.findWidgetById( \"w2\" ), 2, \"#00ff00\", 5, 6, 7, 8 );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( widget );
    lca.renderChanges( widget );
    assertEquals( "", Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( widget );
    graphicsAdapter.setRoundedBorder( 4, color, 5, 6, 7, 8 );
    lca.renderChanges( widget );
    expected
      = "wm.setRoundedBorder"
      + "( wm.findWidgetById( \"w2\" ), 4, \"#00ff00\", 5, 6, 7, 8 );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    lca.preserveValues( widget );
    graphicsAdapter.setRoundedBorder( 4, color, 5, 4, 7, 8 );
    lca.renderChanges( widget );
    expected
      = "wm.setRoundedBorder"
      + "( wm.findWidgetById( \"w2\" ), 4, \"#00ff00\", 5, 4, 7, 8 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}

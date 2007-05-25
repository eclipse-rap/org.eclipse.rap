/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.lifecycle;

import java.util.Date;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.WidgetLCAUtil;
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

  public void testEscape() throws Exception {
    // Empty Parameter
    try {
      WidgetLCAUtil.escapeText( null, true );
      fail( "NullPointerException expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    assertEquals( "", WidgetLCAUtil.escapeText( "", false ) );
    assertEquals( "", WidgetLCAUtil.escapeText( "", true ) );
    // Brackets
    assertEquals( "&lt;", WidgetLCAUtil.escapeText( "<", false ) );
    assertEquals( "&gt;", WidgetLCAUtil.escapeText( ">", false ) );
    assertEquals( "&lt;&lt;&lt;", WidgetLCAUtil.escapeText( "<<<", false ) );
    String expected = "&lt;File &gt;";
    assertEquals( expected, WidgetLCAUtil.escapeText( "<File >", false ) );
    // Amp
    assertEquals( "Open &amp; Close",
                  WidgetLCAUtil.escapeText( "Open && Close", true ) );
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
    // Mnemonics without underline
    assertEquals( "&amp;File", WidgetLCAUtil.escapeText( "&&&File", true ) );
    // No Mnemonics
    assertEquals( "&amp;&amp;&amp;File&quot;&gt;",
                  WidgetLCAUtil.escapeText( "&&&File\">", false ) );
    // Wild combinations
//    assertEquals( "&amp;<u>F</u>ile",
//                  WidgetLCAUtil.escapeText( "&&&File", true ) );
//    assertEquals( "&quot;File' &amp; &lt;b&gt; <u>N</u>ew",
//                  WidgetLCAUtil.escapeText( "\"&File' && <b> &New", true ) );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}

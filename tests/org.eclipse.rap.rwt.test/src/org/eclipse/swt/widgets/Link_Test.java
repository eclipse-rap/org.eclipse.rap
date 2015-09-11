/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ILinkAdapter;
import org.eclipse.swt.internal.widgets.linkkit.LinkLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Link_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private Link link;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display , SWT.NONE );
    link = new Link( shell, SWT.NONE );
  }

  @Test
  public void testInitialValues() {
    assertEquals( "", link.getText() );
  }

  @Test
  public void testText() {
    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    assertEquals( text, link.getText() );
    try {
      link.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testAdapter() {
    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    ILinkAdapter adapter = link.getAdapter( ILinkAdapter.class );
    String displayText = "Visit the Eclipse.org project and the SWT homepage.";
    assertEquals( displayText, adapter.getDisplayText() );
    String[] ids = adapter.getIds();
    assertEquals( 2, ids.length );
    assertEquals( "www.eclipse.org", ids[ 0 ] );
    assertEquals( "SWT", ids[ 1 ] );
  }

  @Test
  public void testComputeSize() {
    Point expected = new Point( 4, 4 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    expected = new Point( 347, 22 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    link = new Link( shell, SWT.BORDER );
    expected = new Point( 6, 6 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 106, 106 );
    assertEquals( expected, link.computeSize( 100, 100 ) );

    text = "<a>test & test2</a>";
    link = new Link( shell, SWT.NONE );
    link.setText( text );
    expected = new Point( 78, 22 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    text = "<a>test && test2</a>";
    link = new Link( shell, SWT.NONE );
    link.setText( text );
    expected = new Point( 91, 22 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    String text = "text";
    link.getAdapter( ILinkAdapter.class );
    link.setText( text );

    Link deserializedLink = serializeAndDeserialize( link );

    assertEquals( text, deserializedLink.getText() );
  }

  @Test
  public void testAddSelectionListener() {
    link.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( link.isListening( SWT.Selection ) );
    assertTrue( link.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    link.addSelectionListener( listener );

    link.removeSelectionListener( listener );

    assertFalse( link.isListening( SWT.Selection ) );
    assertFalse( link.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    link.addSelectionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    link.removeSelectionListener( null );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( link.getAdapter( WidgetLCA.class ) instanceof LinkLCA );
    assertSame( link.getAdapter( WidgetLCA.class ), link.getAdapter( WidgetLCA.class ) );
  }

}

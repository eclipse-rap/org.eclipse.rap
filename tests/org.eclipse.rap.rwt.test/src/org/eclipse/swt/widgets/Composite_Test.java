/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Composite_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testStyle() {
    Composite composite = new Composite( shell, SWT.NONE );
    assertTrue( ( composite.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
    composite = new Composite( shell, SWT.NONE );
    assertTrue( ( composite.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
  }

  @Test
  public void testTabList() {
    // add different controls to the shell
    Control button = new Button( shell, SWT.PUSH );
    Control link = new Link( shell, SWT.NONE );
    new Label( shell, SWT.NONE );
    new Sash( shell, SWT.HORIZONTAL );
    Combo combo = new Combo( shell, SWT.DROP_DOWN );
    Composite composite = new Composite( shell, SWT.NONE );
    List list = new List( shell, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    Control[] controls = shell.getTabList();
    // check that the right ones are in the list
    assertEquals( 6, controls.length );
    assertEquals( button, controls[ 0 ] );
    assertEquals( link, controls[ 1 ] );
    assertEquals( combo, controls[ 2 ] );
    assertEquals( composite, controls[ 3 ] );
    assertEquals( list, controls[ 4 ] );
    assertEquals( text, controls[ 5 ] );
    // A once manually set tabList doesn't change when new controls are created
    Composite group = new Composite( shell, SWT.NONE );
    Text text1 = new Text( group, SWT.NONE );
    Text text2 = new Text( group, SWT.NONE );
    Control[] tabList = new Control[] { text2, text1 };
    group.setTabList( tabList );
    new Text( group, SWT.NONE );
    assertEquals( 2, group.getTabList().length );
    assertSame( text2, group.getTabList()[ 0 ] );
    assertSame( text1, group.getTabList()[ 1 ] );
  }

  @Test
  public void testLayout() {
    GridLayout gridLayout = new GridLayout();
    shell.setLayout( gridLayout );
    assertSame( gridLayout, shell.getLayout() );
    RowLayout rowLayout = new RowLayout();
    shell.setLayout( rowLayout );
    assertSame( rowLayout, shell.getLayout() );
  }

  @Test
  public void testBackgroundMode() {
    Button button = new Button( shell, SWT.PUSH );
    IControlAdapter adapter = ControlUtil.getControlAdapter( button );
    shell.setBackgroundMode( SWT.INHERIT_NONE );
    assertEquals( SWT.INHERIT_NONE, shell.getBackgroundMode() );
    assertFalse( adapter.getBackgroundTransparency() );
    shell.setBackgroundMode( SWT.INHERIT_DEFAULT );
    assertEquals( SWT.INHERIT_DEFAULT, shell.getBackgroundMode() );
    assertFalse( adapter.getBackgroundTransparency() );
    shell.setBackgroundMode( SWT.INHERIT_FORCE );
    assertEquals( SWT.INHERIT_FORCE, shell.getBackgroundMode() );
    assertTrue( adapter.getBackgroundTransparency() );
  }

  @Test
  public void testComputeSize() {
    Composite composite = new Composite( shell, SWT.BORDER );
    assertEquals( 1, composite.getBorderWidth() );
    composite.setLayout( new FillLayout( SWT.HORIZONTAL ) );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setImage( image );
    Point preferredSize = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    assertEquals( 302, preferredSize.x ); // 3 * 100 + border
    assertEquals( 52, preferredSize.y ); // 50 + border
  }

  @Test
  public void testSetFocus() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setVisible( true );
    Composite composite = new Composite( shell, SWT.BORDER );
    Text text = new Text( composite, SWT.SINGLE );
    final StringBuilder log = new StringBuilder();
    text.addFocusListener( new FocusAdapter() {
      @Override
      public void focusGained( FocusEvent event ) {
        log.append( "focusGained" );
      }
    } );
    assertEquals( "", log.toString() );
    composite.setFocus();
    assertEquals( "focusGained", log.toString() );
  }

  @Test
  public void testMimimumSize() {
    // See bug 333002
    Group group = new Group( shell, SWT.NONE );
    Rectangle clientArea = group.getClientArea();
    Button button = new Button( group, SWT.PUSH );
    button.setBounds( clientArea.x, clientArea.y, 200, 50 );
    Point size = group.minimumSize( SWT.DEFAULT, SWT.DEFAULT, true );
    assertEquals( new Point( 200, 50 ), size );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Composite composite = new Composite( shell, SWT.NONE );
    new Label( composite, SWT.NONE );

    Composite deserializedComposite = Fixture.serializeAndDeserialize( composite );

    assertEquals( 1, deserializedComposite.getChildren().length );
    assertTrue( deserializedComposite.getChildren()[ 0 ] instanceof Label );
  }

}

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
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.compositekit.CompositeLCA;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;


public class Composite_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Composite composite;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    composite = new Composite( shell, SWT.NONE );
  }

  @Test
  public void testGetChildren_initiallyEmpty() {
    assertEquals( 0, composite.getChildren().length );
  }

  @Test
  public void testGetChildren_withSingleChild() {
    Button button = new Button( composite, SWT.PUSH );

    assertArrayEquals( new Control[] { button }, composite.getChildren() );
  }

  @Test
  public void testGetChildren_returnsSafeCopy() {
    Button button = new Button( composite, SWT.PUSH );
    Control[] children = composite.getChildren();

    children[ 0 ] = null;

    assertArrayEquals( new Control[] { button }, composite.getChildren() );
  }

  @Test
  public void testStyle() {
    assertTrue( ( composite.getStyle() & SWT.LEFT_TO_RIGHT ) != 0 );
  }

  @Test
  public void testGetTabList_containsChildrenInCorrectOrder() {
    Control child1 = new Button( composite, SWT.PUSH );
    Control child2 = new Button( composite, SWT.PUSH );
    Control child3 = new Button( composite, SWT.PUSH );

    Control[] controls = composite.getTabList();

    assertArrayEquals( new Control[] { child1, child2, child3 }, controls );
  }

  @Test
  public void testGetTabList_containsOnlyTabGroups() {
    Control child1 = new Button( composite, SWT.PUSH );
    Control child2 = new Button( composite, SWT.PUSH );
    new Label( composite, SWT.PUSH );
    new Label( composite, SWT.PUSH );

    Control[] controls = composite.getTabList();

    // See Button.isTabGroup()
    assertArrayEquals( new Control[] { child1, child2 }, controls );
  }

  @Test
  public void testSetTabList_overridesDefaultTabList() {
    new Label( composite, SWT.NONE );
    Control child2 = new Button( composite, SWT.NONE );
    Control child3 = new Label( composite, SWT.NONE );

    composite.setTabList( new Control[] { child2, child3 } );

    assertArrayEquals( new Control[] { child2, child3 }, composite.getTabList() );
  }

  @Test
  public void testSetTabList_isNotChangedByAddingControls() {
    Text text1 = new Text( composite, SWT.NONE );
    Text text2 = new Text( composite, SWT.NONE );
    composite.setTabList( new Control[] { text2, text1 } );

    new Text( composite, SWT.NONE );

    assertArrayEquals( new Control[] { text2, text1 }, composite.getTabList() );
  }

  @Test
  public void testLayout() {
    GridLayout gridLayout = new GridLayout();
    composite.setLayout( gridLayout );
    assertSame( gridLayout, composite.getLayout() );
    RowLayout rowLayout = new RowLayout();
    composite.setLayout( rowLayout );
    assertSame( rowLayout, composite.getLayout() );
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
  public void testComputeSize() throws IOException {
    Composite composite = new Composite( shell, SWT.BORDER );
    assertEquals( 1, composite.getBorderWidth() );
    composite.setLayout( new FillLayout( SWT.HORIZONTAL ) );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setImage( image );
    Point preferredSize = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    assertEquals( 302, preferredSize.x ); // 3 * 100 + border
    assertEquals( 52, preferredSize.y ); // 50 + border
  }

  @Test
  public void testComputeSize_returnsDefaultSizeIfLayoutManagerIsNotSet() {
    Point preferredSize = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 64, preferredSize.x );
    assertEquals( 64, preferredSize.y );
  }

  @Test
  public void testComputeSize_returnsZeroIfLayoutManagerIsSet() {
    composite.setLayout( new FillLayout( SWT.HORIZONTAL ) );

    Point preferredSize = composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 0, preferredSize.x );
    assertEquals( 0, preferredSize.y );
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
    Point size = group.minimumSize();
    assertEquals( new Point( 200, 50 ), size );
  }

  @Test
  public void testIsSerializable() throws Exception {
    new Label( composite, SWT.NONE );

    Composite deserializedComposite = serializeAndDeserialize( composite );

    assertEquals( 1, deserializedComposite.getChildren().length );
    assertTrue( deserializedComposite.getChildren()[ 0 ] instanceof Label );
  }

  @Test
  public void testDispose_disposesChildren() {
    Button child = new Button( composite, SWT.PUSH );

    composite.dispose();

    assertTrue( composite.isDisposed() );
    assertTrue( child.isDisposed() );
  }

  @Test
  public void testDispose_disposesChildren_inOrder() {
    Listener parentListener = mock( Listener.class );
    composite.addListener( SWT.Dispose, parentListener );
    Button child = new Button( composite, SWT.PUSH );
    Listener childListener = mock( Listener.class );
    child.addListener( SWT.Dispose, childListener );

    composite.dispose();

    InOrder order = inOrder( childListener, parentListener );
    order.verify( parentListener ).handleEvent( any( Event.class ) );
    order.verify( childListener ).handleEvent( any( Event.class ) );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( composite.getAdapter( WidgetLCA.class ) instanceof CompositeLCA );
    assertSame( composite.getAdapter( WidgetLCA.class ), composite.getAdapter( WidgetLCA.class ) );
  }

  @Test
  public void testSetOrientation_updatesChildrenOrientation() {
    Button child = new Button( composite, SWT.PUSH );

    composite.setOrientation( SWT.RIGHT_TO_LEFT );

    assertEquals( SWT.RIGHT_TO_LEFT, child.getOrientation() );
  }

}

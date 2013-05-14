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
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TabItem_Test {

  private Display display;
  private Shell shell;
  private TabFolder folder;
  private TabItem item;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    folder = new TabFolder( shell, SWT.NONE );
    item = new TabItem( folder, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate() {
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
  }

  @Test
  public void testCreateWithIndex() {
    TabItem secondItem = new TabItem( folder, SWT.NONE, 0 );

    assertSame( secondItem, folder.getItem( 0 ) );
    assertEquals( 0, folder.indexOf( secondItem ) );
    assertSame( item, folder.getItem( 1 ) );
    assertEquals( 1, folder.indexOf( item ) );
  }

  @Test
  public void testItemDispose() {
    item.dispose();

    assertTrue( item.isDisposed() );
  }

  @Test
  public void testSetImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );

    item.setImage( image );

    assertSame( image, item.getImage() );
  }

  @Test
  public void testSetText() {
    item.setText( "foo" );

    assertEquals( "foo", item.getText() );
  }

  @Test
  public void testToolTip() {
    item.setToolTipText( "foo" );

    assertEquals( "foo", item.getToolTipText() );
  }

  @Test
  public void testSetControl() {
    Control control = new Label( folder, SWT.NONE );

    item.setControl( control );

    assertSame( control, item.getControl() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetControl_withWrongParent() {
    item.setControl( shell );
  }

  @Test
  public void testSelectedControl() {
    Control control0 = new Button( folder, SWT.PUSH );
    item.setControl( control0 );
    assertTrue( control0.getVisible() );

    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    assertFalse( control1.getVisible() );

    folder.setSelection( item1 );
    assertTrue( control1.getVisible() );

    Control alternativeControl1 = new Button( folder, SWT.PUSH );
    item1.setControl( alternativeControl1 );
    assertFalse( control1.getVisible() );
    assertTrue( alternativeControl1.getVisible() );
  }

  @Test
  public void testSelectedControlVisibility_onItemDispose() {
    Control control0 = new Button( folder, SWT.PUSH );
    item.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );

    item.dispose();

    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
  }

}

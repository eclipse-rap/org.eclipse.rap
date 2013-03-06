/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


// TODO [rh] rename to ControlDecorator_Test
public class Decorator_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    assertEquals( 0, Decorator.getDecorators( control ).length );

    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertSame( control, decoration.getControl() );
    assertEquals( control.getParent(), decoration.getParent() );
    assertNull( decoration.getImage() );
    assertEquals( "", decoration.getText() );
    assertTrue( decoration.getShowHover() );
    assertFalse( decoration.getShowOnlyOnFocus() );
    assertEquals( 0, decoration.getMarginWidth() );
    assertFalse( decoration.isVisible() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
    assertFalse( control.isListening( SWT.FocusIn ) );
    assertFalse( control.isListening( SWT.FocusOut ) );
    assertEquals( 1, Decorator.getDecorators( control ).length );

    decoration = new ControlDecorator( control, SWT.LEFT, shell );
    assertSame( control, decoration.getControl() );
    assertEquals( shell, decoration.getParent() );
    assertEquals( 2, Decorator.getDecorators( control ).length );
  }

  @Test
  public void testDispose() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( 1, Decorator.getDecorators( control ).length );
    control.dispose();
    assertTrue( decoration.isDisposed() );

    control = new Button( composite, SWT.PUSH );
    decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( 1, Decorator.getDecorators( control ).length );
    composite.dispose();
    assertTrue( decoration.isDisposed() );

    control = new Button( composite, SWT.PUSH );
    decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( 1, Decorator.getDecorators( control ).length );

    decoration.dispose();
    assertEquals( 0, Decorator.getDecorators( control ).length );
  }

  @Test
  public void testImage() throws IOException {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertNull( decoration.getImage() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
    Image image = createImage( display, Fixture.IMAGE1 );
    decoration.setImage( image );
    assertSame( image, decoration.getImage() );
    assertEquals( image.getBounds().width, decoration.getBounds().width );
    assertEquals( image.getBounds().height, decoration.getBounds().height );
    decoration.setImage( null );
    assertNull( decoration.getImage() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
  }

  @Test
  public void testDescriptionText() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( "", decoration.getText() );
    decoration.setText( "Click me" );
    assertEquals( "Click me", decoration.getText() );
    decoration.setText( null );
    assertEquals( "", decoration.getText() );
  }

  @Test
  public void testMarginWidth() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( 0, decoration.getMarginWidth() );
    decoration.setMarginWidth( 5 );
    assertEquals( 5, decoration.getMarginWidth() );
  }

  @Test
  public void testSetShowOnlyOnFocusToTrue() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );

    decoration.setShowOnlyOnFocus( true );

    assertTrue( decoration.getShowOnlyOnFocus() );
    assertTrue( control.isListening( SWT.FocusIn ) );
    assertTrue( control.isListening( SWT.FocusOut ) );
  }

  @Test
  public void testSetShowOnlyOnFocusToFalse() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    decoration.setShowOnlyOnFocus( true );

    decoration.setShowOnlyOnFocus( false );

    assertFalse( decoration.getShowOnlyOnFocus() );
    assertFalse( control.isListening( SWT.FocusIn ) );
    assertFalse( control.isListening( SWT.FocusOut ) );
  }

  @Test
  public void testSetShowOnlyOnFocusCalledTwice() {
    Control control = new Button( shell, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    decoration.setShowOnlyOnFocus( true );

    decoration.setShowOnlyOnFocus( true );
    decoration.setShowOnlyOnFocus( false );

    assertFalse( control.isListening( SWT.FocusIn ) );
    assertFalse( control.isListening( SWT.FocusOut ) );
  }

  @Test
  public void testShowHover() {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertTrue( decoration.getShowHover() );
    decoration.setShowHover( false );
    assertFalse( decoration.getShowHover() );
  }

  @Test
  public void testVisible() throws IOException {
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    Control button = new Button( composite, SWT.PUSH );
    ControlDecorator decoration
      = new ControlDecorator( control, SWT.RIGHT, null );
    assertFalse( decoration.isVisible() );
    Image image = createImage( display, Fixture.IMAGE1 );
    decoration.setImage( image );
    shell.open();
    assertTrue( decoration.isVisible() );
    decoration.hide();
    assertFalse( decoration.isVisible() );
    decoration.show();
    assertTrue( decoration.isVisible() );
    control.setVisible( false );
    assertFalse( decoration.isVisible() );
    control.setVisible( true );
    assertTrue( decoration.isVisible() );
    decoration.setShowOnlyOnFocus( true );
    assertFalse( decoration.isVisible() );
    control.setFocus();
    assertTrue( decoration.isVisible() );
    button.setFocus();
    assertFalse( decoration.isVisible() );
  }

  @Test
  public void testAddSelectionListener() {
    ControlDecorator decoration = new ControlDecorator( shell, SWT.RIGHT, null );

    decoration.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( decoration.isListening( SWT.Selection ) );
    assertTrue( decoration.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    ControlDecorator decoration = new ControlDecorator( shell, SWT.RIGHT, null );
    SelectionListener listener = mock( SelectionListener.class );
    decoration.addSelectionListener( listener );

    decoration.removeSelectionListener( listener );

    assertFalse( decoration.isListening( SWT.Selection ) );
    assertFalse( decoration.isListening( SWT.DefaultSelection ) );
  }

}

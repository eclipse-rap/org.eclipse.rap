/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.List;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

import junit.framework.TestCase;

public class Decorator_Test extends TestCase {

  public void testCreate() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    List decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNull( decorations );

    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertSame( control, decoration.getControl() );
    assertEquals( control.getParent(), decoration.getParent() );
    assertNull( decoration.getImage() );
    assertNull( decoration.getText() );
    assertTrue( decoration.getShowHover() );
    assertFalse( decoration.getShowOnlyOnFocus() );
    assertEquals( 0, decoration.getMarginWidth() );
    assertFalse( decoration.isVisible() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
    assertFalse( FocusEvent.hasListener( control ) );
    decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNotNull( decorations );
    assertEquals( 1, decorations.size() );

    decoration = new ControlDecorator( control, SWT.LEFT, shell );
    assertSame( control, decoration.getControl() );
    assertEquals( shell, decoration.getParent() );
    decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNotNull( decorations );
    assertEquals( 2, decorations.size() );
  }

  public void testDispose() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    List decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNotNull( decorations );
    assertEquals( 1, decorations.size() );
    control.dispose();
    assertTrue( decoration.isDisposed() );

    control = new Button( composite, SWT.PUSH );
    decoration = new ControlDecorator( control, SWT.RIGHT, null );
    decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNotNull( decorations );
    assertEquals( 1, decorations.size() );
    composite.dispose();
    assertTrue( decoration.isDisposed() );

    control = new Button( composite, SWT.PUSH );
    decoration = new ControlDecorator( control, SWT.RIGHT, null );
    decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNotNull( decorations );
    assertEquals( 1, decorations.size() );
    decoration.dispose();
    decorations = ( List )control.getData( ControlDecorator.KEY_DECORATIONS );
    assertNull( decorations );
  }

  public void testImage() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertNull( decoration.getImage() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    decoration.setImage( image );
    assertSame( image, decoration.getImage() );
    assertEquals( image.getBounds().width, decoration.getBounds().width );
    assertEquals( image.getBounds().height, decoration.getBounds().height );
    decoration.setImage( null );
    assertNull( decoration.getImage() );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), decoration.getBounds() );
  }

  public void testDescriptionText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertNull( decoration.getText() );
    decoration.setText( "Click me" );
    assertEquals( "Click me", decoration.getText() );
    decoration.setText( null );
    assertNull( decoration.getText() );
  }

  public void testMarginWidth() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertEquals( 0, decoration.getMarginWidth() );
    decoration.setMarginWidth( 5 );
    assertEquals( 5, decoration.getMarginWidth() );
  }

  public void testShowOnlyOnFocus() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertFalse( decoration.getShowOnlyOnFocus() );
    assertFalse( FocusEvent.hasListener( control ) );
    decoration.setShowOnlyOnFocus( true );
    assertTrue( decoration.getShowOnlyOnFocus() );
    assertTrue( FocusEvent.hasListener( control ) );
    decoration.setShowOnlyOnFocus( false );
    assertFalse( decoration.getShowOnlyOnFocus() );
    assertFalse( FocusEvent.hasListener( control ) );
  }
  
  public void testShowOnlyOnFocusCalledTwice() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    decoration.setShowOnlyOnFocus( true );
    assertTrue( FocusEvent.hasListener( control ) );
    decoration.setShowOnlyOnFocus( true );
    decoration.setShowOnlyOnFocus( false );
    assertFalse( FocusEvent.hasListener( control ) );
  }

  public void testShowHover() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertTrue( decoration.getShowHover() );
    decoration.setShowHover( false );
    assertFalse( decoration.getShowHover() );
  }

  public void testVisible() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    Control button = new Button( composite, SWT.PUSH );
    ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    assertFalse( decoration.isVisible() );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
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
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}

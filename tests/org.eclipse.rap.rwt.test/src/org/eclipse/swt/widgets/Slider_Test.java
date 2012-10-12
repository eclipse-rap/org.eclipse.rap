/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;


public class Slider_Test extends TestCase {

  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialValues() {
    Slider slider = new Slider( shell, SWT.NONE );
    assertEquals( 0, slider.getMinimum() );
    assertEquals( 100, slider.getMaximum() );
    assertEquals( 0, slider.getSelection() );
    assertEquals( 1, slider.getIncrement() );
    assertEquals( 10, slider.getPageIncrement() );
    assertEquals( 10, slider.getThumb() );
  }

  public void testValues() {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setSelection( 34 );
    assertEquals( 34, slider.getSelection() );
    slider.setMinimum( 10 );
    assertEquals( 10, slider.getMinimum() );
    slider.setMaximum( 56 );
    assertEquals( 56, slider.getMaximum() );
    slider.setIncrement( 5 );
    assertEquals( 5, slider.getIncrement() );
    slider.setPageIncrement( 15 );
    assertEquals( 15, slider.getPageIncrement() );
    slider.setThumb( 13 );
    assertEquals( 13, slider.getThumb() );

    slider.setMinimum( 40 );
    assertEquals( 40, slider.getMinimum() );
    assertEquals( 40, slider.getSelection() );

    slider.setSelection( 55 );
    slider.setMaximum( 65 );
    assertEquals( 65, slider.getMaximum() );
    assertEquals( 43, slider.getSelection() );

    slider.setMaximum( 30 );
    assertEquals( 65, slider.getMaximum() );

    slider.setSelection( 10 );
    assertEquals( 40, slider.getSelection() );

    slider.setSelection( -10 );
    assertEquals( 40, slider.getSelection() );
    
    slider.setSelection( 73 );
    assertEquals( 52, slider.getSelection() );

    slider.setPageIncrement( -15 );
    assertEquals( 15, slider.getPageIncrement() );

    slider.setIncrement( -5 );
    assertEquals( 5, slider.getIncrement() );
    
    slider.setThumb( -5 );
    assertEquals( 13, slider.getThumb() );
    
    slider.setThumb( 0 );
    assertEquals( 13, slider.getThumb() );
    
    slider.setThumb( 3 );
    assertEquals( 3, slider.getThumb() );
    
    slider.setThumb( 30 );
    assertEquals( 25, slider.getThumb() );
    assertEquals( 40, slider.getSelection() );
  }

  public void testStyle() {
    // Test SWT.NONE
    Slider slider = new Slider( shell, SWT.NONE );
    assertTrue( ( slider.getStyle() & SWT.HORIZONTAL ) != 0 );
    // Test SWT.BORDER
    slider = new Slider( shell, SWT.BORDER );
    assertTrue( ( slider.getStyle() & SWT.HORIZONTAL ) != 0 );
    assertTrue( ( slider.getStyle() & SWT.BORDER ) != 0 );
    // Test SWT.VERTICAL
    slider = new Slider( shell, SWT.VERTICAL );
    assertTrue( ( slider.getStyle() & SWT.VERTICAL ) != 0 );
    // Test combination of SWT.HORIZONTAL | SWT.VERTICAL
    slider = new Slider( shell, SWT.HORIZONTAL | SWT.VERTICAL );
    assertTrue( ( slider.getStyle() & SWT.HORIZONTAL ) != 0 );
    assertTrue( ( slider.getStyle() & SWT.VERTICAL ) == 0 );
  }

  public void testDispose() {
    Slider slider = new Slider( shell, SWT.NONE );
    slider.dispose();
    assertTrue( slider.isDisposed() );
  }

  public void testComputeSize() {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );
    Point expected = new Point( 170, 16 );
    assertEquals( expected, slider.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    slider = new Slider( shell, SWT.VERTICAL );
    expected = new Point( 16, 170 );
    assertEquals( expected, slider.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, slider.computeSize( 100, 100 ) );
  }

  public void testIsSerializable() throws Exception {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );
    slider.setSelection( 2 );
    
    Slider deserializedSlider = Fixture.serializeAndDeserialize( slider );
    
    assertEquals( slider.getSelection(), deserializedSlider.getSelection() );
  }

  public void testAddSelectionListener() {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.addSelectionListener( mock( SelectionListener.class ) );
    
    assertTrue( slider.isListening( SWT.Selection ) );
    assertTrue( slider.isListening( SWT.DefaultSelection ) );
  }
  
  public void testRemoveSelectionListener() {
    Slider slider = new Slider( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    slider.addSelectionListener( listener );

    slider.removeSelectionListener( listener );
    
    assertFalse( slider.isListening( SWT.Selection ) );
    assertFalse( slider.isListening( SWT.DefaultSelection ) );
  }

  public void testAddSelectionListenerWithNullArgument() {
    Slider slider = new Slider( shell, SWT.NONE );
    
    try {
      slider.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemoveSelectionListenerWithNullArgument() {
    Slider slider = new Slider( shell, SWT.NONE );
    
    try {
      slider.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
}

/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class MeasurementHandler_Test extends TestCase {

  private AskForTextSizeListener askForTextSizeListener;
  private Font[] fonts;
  private ResizeListener shellResizeListener;
  private ResizeListener scrolledCompositeContentResizeListener;
  private Shell shell;
  private Composite scrolledCompositeContent;

  
  private final class ResizeListener implements ControlListener {
    private boolean resized;
    
    public void controlResized( ControlEvent e ) {
      resized = true;
    }

    public void controlMoved( ControlEvent e ) {
    }

    public boolean hasBeenResized() {
      return resized;
    }
  }

  private class AskForTextSizeListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    public void afterPhase( final PhaseEvent event ) {
    }

    public void beforePhase( final PhaseEvent event ) {
      askForTextSizes();
    }

    public PhaseId getPhaseId() {
      return PhaseId.RENDER;
    }
  }
  

  public void testProbeStatementCreation() {
    prepareRequestThatAsksForTextSizes();

    Fixture.executeLifeCycleFromServerThread();

    checkProbeStatement();
  }
  
  public void testProbeResultProcessing() {
    executeRequestThatAsksForTextSizes();
    createWidgetTreeForResizeCheck();
    fakeMeasurement();
    
    Fixture.executeLifeCycleFromServerThread();
    
    checkMeasurementResultsHasBeenApplied();
    checkResizeTookPlace();
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    createAskForTextSizeListener();
    createResizeListeners();
    createFonts();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void checkResizeTookPlace() {
    assertEquals( getInitialShellBounds(), shell.getBounds() );
    assertEquals( getInitialCompositeBounds(), scrolledCompositeContent.getBounds() );
    assertTrue( shellResizeListener.hasBeenResized() );
    assertTrue( scrolledCompositeContentResizeListener.hasBeenResized() );
  }

  private Rectangle getInitialCompositeBounds() {
    return new Composite( new Shell(), SWT.NONE ).getBounds();
  }

  private Rectangle getInitialShellBounds() {
    return new Shell().getBounds();
  }

  private void createWidgetTreeForResizeCheck() {
    shell = new Shell();
    shell.setLayout( new FillLayout() );
    ScrolledComposite scrolledComposite = new ScrolledComposite( shell, SWT.NONE );
    scrolledCompositeContent = new Composite( scrolledComposite, SWT.NONE );
    scrolledComposite.setContent( scrolledCompositeContent );
    Fixture.fakePhase( PhaseId.RENDER );
    shell.addControlListener( shellResizeListener );
    scrolledCompositeContent.addControlListener( scrolledCompositeContentResizeListener );
  }
  
  private void fakeMeasurement() {
    fakeFontProbeResults();
    fakeMeasurementResults();
  }

  private void fakeMeasurementResults() {
    TestRequest request = ( TestRequest )RWT.getRequest();
    MeasurementItem[] items = MeasurementUtil.getMeasurementItems();
    for( int i = 0; i < items.length; i++ ) {
      String name = String.valueOf( items[ i ].hashCode() );
      String value = "100,1" + String.valueOf( i );
      request.addParameter( name, value );
    }
  }

  private void fakeFontProbeResults() {
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.addParameter( String.valueOf( fonts[ 0 ].getFontData()[ 0 ].hashCode() ), "5,10" );
    request.addParameter( String.valueOf( fonts[ 1 ].getFontData()[ 0 ].hashCode() ), "6,11" );
    request.addParameter( String.valueOf( fonts[ 2 ].getFontData()[ 0 ].hashCode() ), "7,12" );
  }

  private void prepareRequestThatAsksForTextSizes() {
    Display display = new Display();
    Fixture.fakeNewRequest( display );
    RWT.getLifeCycle().addPhaseListener( askForTextSizeListener );
  }
  
  private void executeRequestThatAsksForTextSizes() {
    prepareRequestThatAsksForTextSizes();
    Fixture.executeLifeCycleFromServerThread();
    RWT.getLifeCycle().removePhaseListener( askForTextSizeListener );
  }

  private void checkProbeStatement() {
    String[] expected = getExpectedProbeStatement();
    String allMarkup = Fixture.getAllMarkup();
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( "Expected: " + expected[ i ], contains( allMarkup, expected[ i ] ) );
    }
  }

  private boolean contains( String allMarkup, String snippet ) {
    return allMarkup.indexOf( snippet ) != -1;
  }
  
  private void createAskForTextSizeListener() {
    askForTextSizeListener = new AskForTextSizeListener();
  }

  private void createResizeListeners() {
    shellResizeListener = new ResizeListener();
    scrolledCompositeContentResizeListener = new ResizeListener();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // Note [fappel]: The literals used in createFontes() askForTextSizes(),
  //                checkMeasurementResultsHasBeenApplied() and getExpectedProbeStatement()
  //                belong together. I do this without constant extraction since I think this
  //                would make the probe statement array even more unreadable. If you find a more
  //                readable option feel free to change :-)
  private void createFonts() {
    fonts = new Font[] {
      Graphics.getFont( "arial", 10, SWT.BOLD ),
      Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD ),
      Graphics.getFont( "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD )
    };
  }
  
  private void askForTextSizes() {
    Graphics.stringExtent( fonts[ 0 ], "FirstString" );
    Graphics.stringExtent( fonts[ 1 ], "SecondString" );
    Graphics.stringExtent( fonts[ 2 ], "Weird \" String \\" );
  }

  private void checkMeasurementResultsHasBeenApplied() {
    assertEquals( new Point( 100, 10 ), Graphics.stringExtent( fonts[ 0 ], "FirstString" ) );
    assertEquals( new Point( 100, 11 ), Graphics.stringExtent( fonts[ 1 ], "SecondString" ) );
    assertEquals( new Point( 100, 12 ), Graphics.stringExtent( fonts[ 2 ], "Weird \" String \\" ) );
  }
  
  private String[] getExpectedProbeStatement() {
    String probe = TextSizeProbeStore.DEFAULT_PROBE;
    return new String[] {
      "org.eclipse.swt.FontSizeCalculation.probe( [ [",
      ", \"" + probe + "\", [ \"arial\" ], 10, true, false ]",
      ", [ ",
      ", \"" + probe + "\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false ]",
      " ] );",
      ", \"" + probe + "\", [ \"Bogus  Font  Name\" ], 12, true, false ]",
      " ] );",
      "org.eclipse.swt.FontSizeCalculation.measureStrings( [ [ ",
      ", \"FirstString\", [ \"arial\" ], 10, true, false, -1 ], [ ",
      ", \"SecondString\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false, -1 ], [",
      ", \"Weird &quot; String \\\\\", [ \"Bogus  Font  Name\" ], 12, true, false, -1 ] ] );"
    };
  }
  // END NOTE
  //////////////////////////////////////////////////////////////////////////////////////////////////
}
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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;


public class MeasurementHandler_Test extends TestCase {

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

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void prepareRequestThatAsksForTextSizes() {
    Display display = new Display();
    Fixture.fakeNewRequest( display );
    RWT.getLifeCycle().addPhaseListener( new AskForTextSizeListener() );
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  // Note [fappel]: The literals used in askForTextSizes and getExpectedProbeStatement work
  //                belong together. I do this without constant extraction since I feel
  //                this makes the probestatment array even more unreadable.
  private void askForTextSizes() {
    Font font = Graphics.getFont( "arial", 10, SWT.BOLD );
    Graphics.stringExtent( font, "FirstString" );
    font = Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD );
    Graphics.stringExtent( font, "SecondString" );
    font = Graphics.getFont( "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD );
    Graphics.stringExtent( font, "Weird \" String \\" );
  }
  
  private String[] getExpectedProbeStatement() {
    String probe = TextSizeProbeStore.DEFAULT_PROBE;
    return new String[] {
      "org.eclipse.swt.FontSizeCalculation.probe( [ [",
      ", \"" + probe + "\", [ \"arial\" ], 10, true, false ]",
      ", [ ",
      ", \"" + probe + "\", [ \"helvetia\", \"ms sans serif\" ], 12, true, false ]",
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

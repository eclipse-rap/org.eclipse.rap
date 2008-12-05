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
package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;


public class TextSizeDeterminationHandler_Test extends TestCase {

  public void testRequestCycle() {
    Display display = new Display();
    String displayId = DisplayUtil.getId( display );

    // Let pass one startup request to init the 'system'
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    ISessionStore session = ContextProvider.getSession();
    String id = LifeCycle.class.getName();
    session.setAttribute( id, lifeCycle );
    RWTFixture.executeLifeCycleFromServerThread( );

    // The actual test request
    Fixture.fakeResponseWriter();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
      }
      public void beforePhase( final PhaseEvent event ) {
        Font font = Graphics.getFont( "arial", 10, SWT.BOLD );
        TextSizeDetermination.stringExtent( font, "FirstString" );
        font = Graphics.getFont( "helvetia, ms sans serif", 12, SWT.BOLD );
        TextSizeDetermination.stringExtent( font, "SecondString" );
        font = Graphics.getFont( "\"Bogus\" \\ Font \" Name", 12, SWT.BOLD );
        TextSizeDetermination.stringExtent( font, "Weird \" String \\" );
      }
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    RWTFixture.executeLifeCycleFromServerThread( );

    String probe = TextSizeProbeStore.DEFAULT_PROBE;
    String[] expected = new String[] {
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
    String allMarkup = Fixture.getAllMarkup();
    for( int i = 0; i < expected.length; i++ ) {
      assertTrue( "Expected: " + expected[ i ],
                  allMarkup.indexOf( expected[ i ] ) != -1 );
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    TextSizeDataBase.reset();
    TextSizeProbeStore.reset();
  }

  protected void tearDown() throws Exception {
    TextSizeProbeStore.reset();
    TextSizeDataBase.reset();
    RWTFixture.tearDown();
  }
}

/*******************************************************************************
 * Copyright (c) 2011, 2018 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.internal.widgets.ControlRemoteAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ReadData_Test {

  private ReadData readData;

  @Before
  public void setUp() {
    Fixture.setUp();
    readData = new ReadData();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPhaseId() {
    assertEquals( PhaseId.READ_DATA, readData.getPhaseId() );
  }

  @Test
  public void testExecute_returnsProcessActionPhase() {
    Display display = new Display();

    PhaseId phaseId = readData.execute( display );

    assertEquals( PhaseId.PROCESS_ACTION, phaseId );
  }

  @Test
  public void testExecute_triggersLCAsReadData() {
    StringBuilder log = new StringBuilder();
    Display display = new Display();
    new TestWidget( display, log );

    readData.execute( display );

    assertEquals( LoggingWidgetLCA.READ_DATA +
                  LoggingWidgetRemoteAdapter.CLEAR_PRESERVED, log.toString() );
  }

  @Test
  public void testExecute_triggersLCAsPreservesValues() {
    StringBuilder log = new StringBuilder();
    Display display = new Display();
    new TestWidget( display, log );
    Fixture.markInitialized( display );

    readData.execute( display );

    assertEquals( LoggingWidgetLCA.READ_DATA +
                  LoggingWidgetRemoteAdapter.CLEAR_PRESERVED +
                  LoggingWidgetLCA.PRESERVE_VALUES, log.toString() );
  }

  private final class TestWidget extends Shell {
    private final StringBuilder log;
    private TestWidget( Display display, StringBuilder log ) {
      super( display );
      this.log = log;
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result = null;
      if( adapter == WidgetLCA.class ) {
        result = new LoggingWidgetLCA( log );
      } else if( adapter == RemoteAdapter.class ) {
        result = new LoggingWidgetRemoteAdapter( "testId", log );
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  private static class LoggingWidgetLCA extends WidgetLCA {
    private static final String READ_DATA = "readData";
    private static final String PRESERVE_VALUES = "preserveValues";
    private static final String RENDER_INITIALIZATION = "renderInitialization";
    private static final String RENDER_CHANGES = "renderChanges";
    private static final String RENDER_DISPOSE = "renderDispose";
    private final StringBuilder log;
    LoggingWidgetLCA( StringBuilder log ) {
      this.log = log;
    }
    @Override
    public void readData( Widget widget ) {
      log.append( READ_DATA );
    }
    @Override
    public void preserveValues( Widget widget ) {
      log.append( PRESERVE_VALUES );
    }
    @Override
    public void renderInitialization( Widget widget ) throws IOException {
      log.append( RENDER_INITIALIZATION );
    }
    @Override
    public void renderChanges( Widget widget ) throws IOException {
      log.append( RENDER_CHANGES );
    }
    @Override
    public void renderDispose( Widget widget ) throws IOException {
      log.append( RENDER_DISPOSE );
    }
  }

  private static class LoggingWidgetRemoteAdapter extends ControlRemoteAdapter {
    private static final String CLEAR_PRESERVED = "clearPreserved";
    private final StringBuilder log;
    public LoggingWidgetRemoteAdapter( String id, StringBuilder log ) {
      super( id );
      this.log = log;
    }

    @Override
    public void clearPreserved() {
      log.append( CLEAR_PRESERVED );
    }

  }

}

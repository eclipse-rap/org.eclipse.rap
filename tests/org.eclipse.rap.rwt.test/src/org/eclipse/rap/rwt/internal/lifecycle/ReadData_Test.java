/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class ReadData_Test extends TestCase {

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
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = new LoggingWidgetLCA( log );
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  private static class LoggingWidgetLCA extends AbstractWidgetLCA {
    private static final String READ_DATA = "readData";
    private static final String PRESERVE_VALUES = "preserveValues";
    private static final String RENDER_INITIALIZATION = "renderInitialization";
    private static final String RENDER_CHANGES = "renderChanges";
    private static final String RENDER_DISPOSE = "renderDispose";
    private final StringBuilder log;
    LoggingWidgetLCA( StringBuilder log ) {
      this.log = log;
    }
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

  private ReadData readData;

  public void testGetPhaseId() {
    assertEquals( PhaseId.READ_DATA, readData.getPhaseId() );
  }

  public void testExecute() {
    StringBuilder log = new StringBuilder();
    Display display = new Display();
    new TestWidget( display, log );
    PhaseId phaseId = readData.execute( display );
    assertEquals( LoggingWidgetLCA.READ_DATA, log.toString() );
    assertEquals( PhaseId.PROCESS_ACTION, phaseId );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    readData = new ReadData();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}

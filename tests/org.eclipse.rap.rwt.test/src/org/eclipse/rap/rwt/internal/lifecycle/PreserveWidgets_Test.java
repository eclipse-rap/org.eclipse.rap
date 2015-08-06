/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class PreserveWidgets_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExecutionOrder() {
    Display display = new Display();
    LoggingWidgetLCA loggingWidgetLCA = new LoggingWidgetLCA();
    Composite shell = new CustomLCAShell( display, loggingWidgetLCA );
    new CustomLCAWidget( shell, loggingWidgetLCA );
    Fixture.markInitialized( display );

    new DisplayLCA().preserveValues( display );

    String expectedorder = CustomLCAShell.class.getName() + CustomLCAWidget.class.getName();
    assertEquals( expectedorder, loggingWidgetLCA.log.toString() );
  }

  @Test
  public void testPreserveValuesWhenDisplayIsUninitialized() {
    StringBuilder log = new StringBuilder();
    Display display = new Display();
    LoggingWidgetLCA loggingWidgetLCA = new LoggingWidgetLCA();
    Composite shell = new CustomLCAShell( display, loggingWidgetLCA );
    new CustomLCAWidget( shell, loggingWidgetLCA );

    new DisplayLCA().preserveValues( display );

    assertEquals( "", log.toString() );
  }

  @Test
  public void testStartup() throws Exception {
    ApplicationContextImpl applicationContext = getApplicationContext();
    applicationContext.getEntryPointManager().register( TestRequest.DEFAULT_SERVLET_PATH,
                                                        TestEntryPointWithShell.class,
                                                        null );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )applicationContext.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    TestMessage message = Fixture.getProtocolMessage();
    assertTrue( message.getOperationCount() > 0 );
  }

  @Test
  public void testClearPreservedWithDisposedDisplay() {
    Display display = new Display();
    display.dispose();
    Fixture.fakePhase( PhaseId.RENDER );
    try {
      new DisplayLCA().clearPreserved( display );
    } catch( Exception e ) {
      fail( "clearPreserved() must succeed even with disposed display" );
    }
  }

  public static class TestEntryPointWithShell implements EntryPoint {
    public int createUI() {
      Display display = new Display();
      new Shell( display, SWT.NONE );
      int count = 0;
      while( count < 1 ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
        count++;
      }
      return 0;
    }
  }

  private static class LoggingWidgetLCA extends WidgetLCA {
    private final StringBuilder log;

    LoggingWidgetLCA() {
      log= new StringBuilder();
    }

    @Override
    public void preserveValues( Widget widget ) {
      log.append( widget.getClass().getName() );
    }

    @Override
    public void readData( Widget widget ) {
    }

    @Override
    public void renderInitialization( Widget widget ) throws IOException {
    }

    @Override
    public void renderChanges( Widget widget ) throws IOException
    {
    }

    @Override
    public void renderDispose( Widget widget ) throws IOException {
    }
  }

  private static class CustomLCAWidget extends Composite {

    private final WidgetLCA widgetLCA;

    CustomLCAWidget( Composite parent, WidgetLCA widgetLCA ) {
      super( parent, 0 );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLCA.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  private static class CustomLCAShell extends Shell {
    private static final long serialVersionUID = 1L;

    private final WidgetLCA widgetLCA;

    CustomLCAShell( Display display, WidgetLCA widgetLCA ) {
      super( display );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLCA.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

}

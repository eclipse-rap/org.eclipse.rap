/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
  public void testInitialization() {
    // ensures that the default preserve mechanism is registered and executes at the designated
    // phases
    Display display = new Display();
    Composite shell = new Shell( display );
    final Text text = new Text( shell, SWT.NONE );
    text.setText( "hello" );
    Fixture.markInitialized( display );
    LifeCycle lifeCycle = getApplicationContext().getLifeCycleFactory().getLifeCycle();
    final StringBuilder log = new StringBuilder();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        if( PhaseId.PROCESS_ACTION.equals( event.getPhaseId() ) ) {
          WidgetAdapter adapter = WidgetUtil.getAdapter( text );
          if( "hello".equals( adapter.getPreserved( Props.TEXT ) ) ) {
            log.append( "copy created" );
          }
        }
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( "copy created", log.toString() );
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
    applicationContext.getEntryPointManager().register( EntryPointManager.DEFAULT_PATH,
                                                        TestEntryPointWithShell.class,
                                                        null );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )applicationContext.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    Message message = Fixture.getProtocolMessage();
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

  private static class LoggingWidgetLCA extends AbstractWidgetLCA {
    private final StringBuilder log;

    LoggingWidgetLCA() {
      log= new StringBuilder();
    }

    @Override
    public void preserveValues( Widget widget ) {
      log.append( widget.getClass().getName() );
    }

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

    private final AbstractWidgetLCA widgetLCA;

    CustomLCAWidget( Composite parent, AbstractWidgetLCA widgetLCA ) {
      super( parent, 0 );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  private static class CustomLCAShell extends Shell {
    private static final long serialVersionUID = 1L;

    private final AbstractWidgetLCA widgetLCA;

    CustomLCAShell( Display display, AbstractWidgetLCA widgetLCA ) {
      super( display );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

}

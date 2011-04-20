/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.*;

public class PreserveWidgets_Test extends TestCase {

  public static class TestEntryPointWithShell implements IEntryPoint {
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

  private static class LoggingLifeCycleAdapterFactory implements AdapterFactory {

    private final StringBuffer log;

    private LoggingLifeCycleAdapterFactory( StringBuffer log ) {
      this.log = log;
    }

    public Object getAdapter( Object adaptable, Class adapter ) {
      Object result = null;
      if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class ) {
        result = new IDisplayLifeCycleAdapter() {
          public void preserveValues( Display display ) {
            log.append( display.getClass().getName() );
          }
          public void readData( Display display ) {
          }
          public void render( Display display ) throws IOException {
          }
          public void clearPreserved( Display display ) {
          }
        };
      } else {
        result = new AbstractWidgetLCA() {
          public void preserveValues( Widget widget ) {
            log.append( widget.getClass().getName() );
          }
          public void readData( Widget widget ) {
          }
          public void renderInitialization( Widget widget ) throws IOException {
          }
          public void renderChanges( Widget widget ) throws IOException
          {
          }
          public void renderDispose( Widget widget ) throws IOException {
          }
        };
      }
      return result;
    }

    public Class[] getAdapterList() {
      return new LifeCycleAdapterFactory().getAdapterList();
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialization() throws Exception {
    // ensures that the default preserve mechanism is registered and executes at the designated 
    // phases
    Display display = new Display();
    Composite shell = new Shell( display );
    final Text text = new Text( shell, SWT.NONE );
    text.setText( "hello" );
    Fixture.markInitialized( display );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    final StringBuffer log = new StringBuffer();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        if( PhaseId.PROCESS_ACTION.equals( event.getPhaseId() ) ) {
          IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
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

  public void testExecutionOrder() {
    StringBuffer log = new StringBuffer();
    installLoggingLifeCycleAdapterFactory( log );
    Display display = new Display();
    Composite shell = new Shell( display );
    new Text( shell, SWT.NONE );
    Fixture.markInitialized( display );
    new DisplayLCA().preserveValues( display );
    String expected = Shell.class.getName() + Text.class.getName();
    assertEquals( expected, log.toString() );
  }

  public void testPreserveValuesWhenDisplayIsUninitialized() {
    StringBuffer log = new StringBuffer();
    installLoggingLifeCycleAdapterFactory( log );
    Display display = new Display();
    Composite shell = new Shell( display );
    new Text( shell, SWT.NONE );
    new DisplayLCA().preserveValues( display );
    assertEquals( "", log.toString() );
  }

  public void testStartup() throws Exception {
    // Simulate startup with no startup entry point set
    // First request: (renders html skeletion that contains 'application')
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, 
                                                TestEntryPointWithShell.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    // Second request: first 'real' one that writes JavaScript to create display
    Fixture.fakeResponseWriter();
    Fixture.fakeNewRequest( LifeCycleUtil.getSessionDisplay() );
    lifeCycle.execute();
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
  }
  
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

  private static void installLoggingLifeCycleAdapterFactory( StringBuffer log ) {
    AdapterFactory lifeCycleAdapterFactory = new LoggingLifeCycleAdapterFactory( log );
    Fixture.disposeOfServiceContext();
    Fixture.createServiceContext();
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
  }
}

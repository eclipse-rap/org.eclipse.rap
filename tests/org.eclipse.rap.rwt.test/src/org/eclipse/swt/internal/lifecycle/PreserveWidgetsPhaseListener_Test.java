/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.lifecycle;

import java.io.IOException;
import javax.servlet.ServletContextEvent;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.internal.engine.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.WidgetAdapterFactory;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.*;
import com.w4t.Fixture.TestServletContext;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.IInitialization;
import com.w4t.util.browser.Ie6;
import com.w4t.util.browser.Ie6up;

public class PreserveWidgetsPhaseListener_Test extends TestCase {

  public static final class TestEntryPointWithShell implements IEntryPoint {
    public Display createUI() {
      Display display = new Display();
      new Shell( display , SWT.NONE );
      return display;
    }
  }
  
  private String savedLifeCycle;

  protected void setUp() throws Exception {
    Fixture.setUp();
    savedLifeCycle = System.getProperty( IInitialization.PARAM_LIFE_CYCLE );
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE,
                        RWTLifeCycle.class.getName() );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
    AdapterFactoryRegistry.clear();
    PhaseListenerRegistry.clear();
    RWTFixture.deregisterResourceManager();
    if( savedLifeCycle != null ) {
      System.setProperty( IInitialization.PARAM_LIFE_CYCLE, savedLifeCycle );
    }
  }

  public void testInitialization() throws Exception {
    // ensures that the default WidgetCopyPhaseListener is registered
    // and executes at the designated phases
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext();
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.setText( "hello" );
    RWTFixture.markInitialized( display );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )W4TContext.getLifeCycle();
    final StringBuffer log = new StringBuffer();
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
        if( PhaseId.PROCESS_ACTION.equals( event.getPhaseId() ) ) {
          IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
          if( "hello".equals( adapter.getPreserved( Props.TEXT ) ) ) {
            log.append( "copy created" );
          }
        }
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );
    lifeCycle.execute();
    assertEquals( "copy created", log.toString() );
    // clean up
    Fixture.removeContext();
  }

  public void testExecutionOrder() throws Exception {
    // Build test environment:
    final StringBuffer log = new StringBuffer();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    AdapterFactory lifeCycleAdapterFactory = new AdapterFactory() {

      private AdapterFactory factory = new LifeCycleAdapterFactory();

      public Object getAdapter( final Object adaptable, final Class adapter ) {
        Object result = null;
        if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class )
        {
          result = new IDisplayLifeCycleAdapter() {

            public void preserveValues( final Display display ) {
              log.append( display.getClass().getName() );
            }

            public void processAction( Device display ) {
            }

            public void readData( Display display ) {
            }

            public void render( Display display ) throws IOException {
            }
          };
        } else {
          result = new AbstractWidgetLCA() {

            public void preserveValues( final Widget widget ) {
              log.append( widget.getClass().getName() );
            }

            public void readData( final Widget widget ) {
            }

            public void renderInitialization( final Widget widget )
              throws IOException
            {
            }

            public void renderChanges( final Widget widget ) throws IOException
            {
            }

            public void renderDispose( final Widget widget ) throws IOException
            {
            }
          };
        }
        return result;
      }

      public Class[] getAdapterList() {
        return factory.getAdapterList();
      }
    };
    AdapterManager manager = W4TContext.getAdapterManager();
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    WidgetAdapterFactory widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
    RWTFixture.registerResourceManager();
    // Create test widget hierarchy
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    new Text( shell, SWT.NONE );
    // Execute life cycle
    RWTFixture.markInitialized( display );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    String expected = Display.class.getName()
                    + Shell.class.getName()
                    + Text.class.getName();
    assertEquals( expected, log.toString() );
  }

  public void testStartup() throws Exception {
    // Simulate startup with no startup entry point set
    // First request: (renders html skeletion that contains 'application')
    Fixture.createContext();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    RWTFixture.registerResourceManager();
    RWTFixture.registerAdapterFactories();
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPointWithShell.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    lifeCycle.execute();
    // Second request: first 'real' one that writes JavaScript to create display
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( Display.getCurrent() );
    lifeCycle.execute();
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
    // clean up
    RWTFixture.deregisterAdapterFactories();
    RWTFixture.deregisterResourceManager();
    Fixture.removeContext();
  }

  private static void fakeUIRootRequestParam( final Display display ) {
    Object adapter = display.getAdapter( IWidgetAdapter.class );
    IWidgetAdapter displayAdapter = ( IWidgetAdapter )adapter;
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayAdapter.getId() );
  }
}

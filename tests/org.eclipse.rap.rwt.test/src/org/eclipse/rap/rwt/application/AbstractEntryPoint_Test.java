/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.application;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.ClientService;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AbstractEntryPoint_Test {

  @Rule public TestContext context = new TestContext();

  @Before
  public void setUp() {
    LifeCycleFactory lifeCycleFactory = getApplicationContext().getLifeCycleFactory();
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();
  }

  @Test
  public void testCreateUI_createsDisplayAndShells() {
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    entryPoint.createUI();

    assertEquals( 1, Display.getCurrent().getShells().length );
  }

  @Test
  public void testCreateContents_parentHasLayout() {
    final AtomicReference<Composite> parentCaptor = new AtomicReference<Composite>();
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
        parentCaptor.set( parent );
      }
    };

    entryPoint.createUI();

    assertNotNull( parentCaptor.get().getLayout() );
  }

  @Test
  public void testGetShell_returnsParentShell() {
    final AtomicReference<Composite> parentCaptor = new AtomicReference<Composite>();
    final AtomicReference<Shell> shellCaptor = new AtomicReference<Shell>();
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
        parentCaptor.set( getShell() );
        shellCaptor.set( getShell() );
      }
    };

    entryPoint.createUI();

    assertSame( parentCaptor.get().getShell(), shellCaptor.get() );
  }

  @Test
  public void testCreateShell() {
    final Shell testShell = mock( Shell.class );
    final AtomicReference<Shell> shellCaptor = new AtomicReference<Shell>();
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
        shellCaptor.set( getShell() );
      }
      @Override
      protected Shell createShell( Display display ) {
        return testShell;
      }
    };

    entryPoint.createUI();

    assertSame( testShell, shellCaptor.get() );
  }

  @Test
  public void testGetParameterNames_withoutService() {
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertTrue( entryPoint.getParameterNames().isEmpty() );
  }

  @Test
  public void testGetParameterNames_withService() {
    StartupParameters startupParameters = mock( StartupParameters.class );
    when( startupParameters.getParameterNames() ).thenReturn( Arrays.asList( "foo", "bar" ) );
    fakeService( StartupParameters.class, startupParameters );
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertEquals( Arrays.asList( "foo", "bar" ), entryPoint.getParameterNames() );
  }

  @Test
  public void testGetParameter_withoutService() {
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertNull( entryPoint.getParameter( "foo" ) );
  }

  @Test
  public void testGetParameter_withService() {
    StartupParameters startupParameters = mock( StartupParameters.class );
    when( startupParameters.getParameter( "foo" ) ).thenReturn( "bar" );
    fakeService( StartupParameters.class, startupParameters );
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertEquals( "bar", entryPoint.getParameter( "foo" ) );
  }

  @Test
  public void testGetParameterValues_withoutService() {
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertNull( entryPoint.getParameterValues( "foo" ) );
  }

  @Test
  public void testGetParameterValues_withService() {
    StartupParameters startupParameters = mock( StartupParameters.class );
    List<String> values = Arrays.asList( new String[] { "bar", "baz" } );
    when( startupParameters.getParameterValues( "foo" ) ).thenReturn( values );
    fakeService( StartupParameters.class, startupParameters );
    AbstractEntryPoint entryPoint = new AbstractEntryPoint() {
      @Override
      protected void createContents( Composite parent ) {
      }
    };

    assertEquals( "bar", entryPoint.getParameterValues( "foo" ).get( 0 ) );
    assertEquals( "baz", entryPoint.getParameterValues( "foo" ).get( 1 ) );
  }

  private <T extends ClientService>  void fakeService( Class<T> type, T service ) {
    Client client = mock( Client.class );
    when( client.getService( type ) ).thenReturn( service );
    context.getUISession().setAttribute( ClientSelector.SELECTED_CLIENT, client );
  }

  static class TestLifeCycle extends LifeCycle {
    public TestLifeCycle( ApplicationContextImpl applicationContext ) {
      super( applicationContext );
    }
    @Override
    public void execute() throws IOException {
    }
    @Override
    public void requestThreadExec( Runnable runnable ) {
    }
    @Override
    public void addPhaseListener( PhaseListener phaseListener ) {
    }
    @Override
    public void removePhaseListener( PhaseListener phaseListener ) {
    }
    @Override
    public void sleep() {
    }
  }

}

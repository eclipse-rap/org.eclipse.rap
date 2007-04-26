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

import java.io.*;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.RWTFixture.TestEntryPoint;
import org.eclipse.swt.internal.engine.PhaseListenerRegistry;
import org.eclipse.swt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import com.w4t.Fixture;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6up;

public class RWTLifeCycle_Test extends TestCase {

  private static final String MY_ENTRY_POINT = "myEntryPoint";
  private static final String BEFORE = "before ";
  private static final String AFTER = "after ";
  private static final String DISPLAY_CREATED = "display created";
  private static String log = "";
  private PrintStream bufferedSystemErr;
  private ByteArrayOutputStream capturedSystemErr;
  private final class ExceptionListenerTest implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void afterPhase( final PhaseEvent event ) {
      log += AFTER + event.getPhaseId() + "|";
      throw new RuntimeException();
    }

    public void beforePhase( final PhaseEvent event ) {
      log += BEFORE + event.getPhaseId() + "|";
      throw new RuntimeException();
    }

    public PhaseId getPhaseId() {
      return PhaseId.PREPARE_UI_ROOT;
    }
  }
  public static class TestEntryPointWithLog implements IEntryPoint {

    public Display createUI() {
      log = DISPLAY_CREATED;
      return new Display();
    }
  }

  public void testNoEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle without entry point must throw exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDefaultEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPointWithLog.class );
    lifeCycle.execute();
    assertEquals( DISPLAY_CREATED, log );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testParamEntryPoint() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, MY_ENTRY_POINT );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    EntryPointManager.register( MY_ENTRY_POINT, TestEntryPointWithLog.class );
    lifeCycle.execute();
    assertEquals( DISPLAY_CREATED, log );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "notRegistered" );
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle with unknown entry point must fail." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    EntryPointManager.deregister( MY_ENTRY_POINT );
  }

  public void testRWTLifeCyclePhases() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    PhaseListener listener = new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void afterPhase( final PhaseEvent event ) {
        log += AFTER + event.getPhaseId() + "|";
      }

      public void beforePhase( final PhaseEvent event ) {
        log += BEFORE + event.getPhaseId() + "|";
      }

      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    };
    lifeCycle.addPhaseListener( listener );
    lifeCycle.execute();
    String expected = BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.RENDER
                      + "|"
                      + AFTER
                      + PhaseId.RENDER
                      + "|";
    assertEquals( expected, log );
    log = "";
    lifeCycle.execute();
    expected = BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + BEFORE
               + PhaseId.READ_DATA
               + "|"
               + AFTER
               + PhaseId.READ_DATA
               + "|"
               + BEFORE
               + PhaseId.PROCESS_ACTION
               + "|"
               + AFTER
               + PhaseId.PROCESS_ACTION
               + "|"
               + BEFORE
               + PhaseId.RENDER
               + "|"
               + AFTER
               + PhaseId.RENDER
               + "|";
    assertEquals( expected, log );
    lifeCycle.removePhaseListener( listener );
    log = "";
    lifeCycle.execute();
    assertEquals( "", log );
    log = "";
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void afterPhase( final PhaseEvent event ) {
        log += AFTER + event.getPhaseId() + "|";
      }

      public void beforePhase( final PhaseEvent event ) {
        log += BEFORE + event.getPhaseId() + "|";
      }

      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    expected = BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|";
    assertEquals( expected, log );
    Fixture.fakeRequestParam( RequestParams.STARTUP,
                              EntryPointManager.DEFAULT );
    try {
      lifeCycle.execute();
      fail();
    } catch( final IllegalStateException iae ) {
      // expected
    }
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testExceptionInPhaseListener() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.execute();
    String expected = BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|";
    assertEquals( expected, log );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testRender() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    assertTrue( Fixture.getAllMarkup().length() > 0 );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testPhaseListenerRegistration() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    final PhaseListener[] callbackHandler = new PhaseListener[ 1 ];
    PhaseListener listener = new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( PhaseEvent event ) {
        callbackHandler[ 0 ] = this;
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    };
    PhaseListenerRegistry.add( listener );
    RWTLifeCycle lifeCycle1 = new RWTLifeCycle();
    lifeCycle1.execute();
    assertSame( callbackHandler[ 0 ], listener );
    callbackHandler[ 0 ] = null;
    RWTLifeCycle lifeCycle2 = new RWTLifeCycle();
    lifeCycle2.execute();
    assertSame( callbackHandler[ 0 ], listener );
    PhaseListenerRegistry.clear();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  protected void setUp() throws Exception {
    bufferedSystemErr = System.err;
    capturedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( capturedSystemErr ) );
    log = "";
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    System.setErr( bufferedSystemErr );
  }
}

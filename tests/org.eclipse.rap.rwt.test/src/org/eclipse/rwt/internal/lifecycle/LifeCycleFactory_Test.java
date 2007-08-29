package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class LifeCycleFactory_Test extends TestCase {
  
  public final static class TestLifeCycle extends LifeCycle {
    public void addPhaseListener( final PhaseListener listener ) {}
    public void execute() throws ServletException {}
    public void removePhaseListener( final PhaseListener listener ) {}
    public Scope getScope() { return Scope.APPLICATION; }
  }
  
  public void testUserdefinedLifeCycleLoading() {
    System.setProperty( "lifecycle", TestLifeCycle.class.getName() );
    ILifeCycle lifeCycle1 = LifeCycleFactory.loadLifeCycle();
    assertTrue( lifeCycle1 instanceof TestLifeCycle );
    ILifeCycle lifeCycle2 = LifeCycleFactory.loadLifeCycle();
    assertSame( lifeCycle1, lifeCycle2 );
    System.getProperties().remove( "lifecycle" );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.createContext();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    Fixture.removeContext();
  }
}

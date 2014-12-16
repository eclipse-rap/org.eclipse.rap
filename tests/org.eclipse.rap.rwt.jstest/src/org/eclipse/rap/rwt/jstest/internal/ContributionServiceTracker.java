/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public class ContributionServiceTracker
  extends ServiceTracker<TestContribution, TestContribution>
{

  private final Map<String,TestContribution> contributions;

  public ContributionServiceTracker( BundleContext context ) {
    super( context, TestContribution.class.getName(), null );
    contributions = new LinkedHashMap<String, TestContribution>();
    register( new RWTContribution() );
    register( new TestFixtureContribution() );
    register( new TestRunnerContribution() );
    register( new RWTTestsContribution() );
    register( new JasmineTestsContribution() );
  }

  public Map<String, TestContribution> getContributions() {
    return Collections.unmodifiableMap( contributions );
  }

  @Override
  public TestContribution addingService( ServiceReference<TestContribution> reference ) {
    TestContribution contribution = super.addingService( reference );
    try {
      register( contribution );
    } catch( Exception exception ) {
      throw new RuntimeException( "Failed to add test contribution", exception );
    }
    return contribution;
  }

  @Override
  public void removedService( ServiceReference<TestContribution> reference,
                              TestContribution service )
  {
    unregister( service );
  }

  private void register( TestContribution contribution ) {
    if( contributions.containsKey( contribution.getName() ) ) {
      System.err.println( "duplicate test contribution name: " + contribution.getName() );
    } else {
      contributions.put( contribution.getName(), contribution );
    }
  }

  private void unregister( TestContribution contribution ) {
    contributions.remove( contribution.getName() );
  }

}

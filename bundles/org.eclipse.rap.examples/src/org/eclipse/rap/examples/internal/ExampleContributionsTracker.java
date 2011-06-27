/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.*;

import org.eclipse.rap.examples.IExampleContribution;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public final class ExampleContributionsTracker
  extends ServiceTracker<IExampleContribution, IExampleContribution>
{

  private final Map<String, IExampleContribution> contributions;

  ExampleContributionsTracker( BundleContext context ) {
    super( context, IExampleContribution.class, null );
    contributions = new HashMap<String, IExampleContribution>();
  }

  @Override
  public IExampleContribution addingService( ServiceReference<IExampleContribution> reference ) {
    IExampleContribution contribution = super.addingService( reference );
    String id = contribution.getId();
    if( contributions.containsKey( id ) ) {
      throw new IllegalStateException( "Duplicate contribution id: " + id );
    }
    contributions.put( id, contribution );
    return contribution;
  }

  @Override
  public void removedService( ServiceReference<IExampleContribution> reference,
                              IExampleContribution service )
  {
    contributions.remove( service.getId() );
    super.removedService( reference, service );
  }

  public Collection<String> getContributionIds() {
    return contributions.keySet();
  }

  public IExampleContribution getContribution( String id ) {
    return contributions.get( id );
  }
}

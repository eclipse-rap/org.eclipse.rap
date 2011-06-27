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
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IApplicationStore;


public final class Examples {

  private static final String ATTR_NAME = Examples.class.getName() + ".instance";
  private final List<IExampleContribution> contributions;
  private static ExampleContributionsTracker tracker;

  private String[] INCLUDE = new String[] {
    "input",
    "dialog",
    "drag-and-drop",
    "treeviewer",
    "tableviewer",
    "complex-data",
    "canvas",
    "row-layout",
    "fill-layout",
    "grid-layout",
    "file-upload",
    "oscilloscope",
    "carousel",
    "gmaps"
  };

  private Examples() {
    readContributions();
    contributions = createContributionList();
  }

  public static Examples getInstance() {
    IApplicationStore store = RWT.getApplicationStore();
    Examples result;
    synchronized( Examples.class ) {
      result = ( Examples )store.getAttribute( ATTR_NAME );
      if( result == null ) {
        result = new Examples();
        store.setAttribute( ATTR_NAME, result );
      }
    }
    return result;
  }

  public List<IExampleContribution> getContributions() {
    return Collections.unmodifiableList( contributions );
  }

  public IExampleContribution getContribution( String id ) {
    return tracker.getContribution( id );
  }

  private List<IExampleContribution> createContributionList() {
    List<IExampleContribution> result = new ArrayList<IExampleContribution>();
    List<String> ids = new ArrayList<String>( tracker.getContributionIds() );
    for( String id : INCLUDE ) {
      IExampleContribution contribution = tracker.getContribution( id );
      if( contribution != null ) {
        result.add( contribution );
        ids.remove( id );
      } else {
        System.out.println( "Missing contribution " + id );
      }
    }
    for( String id : ids ) {
      IExampleContribution contribution = tracker.getContribution( id );
      if( contribution != null ) {
        result.add( contribution );
        System.out.println( "Adding contribution " + id );
      }
    }
    return result;
  }

  private static void readContributions() {
    tracker = Activator.getDefault().getExampleContributions();
  }
}

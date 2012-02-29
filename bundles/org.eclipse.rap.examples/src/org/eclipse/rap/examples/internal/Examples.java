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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.examples.IExampleContribution;


public final class Examples {

  private final String[] INCLUDE = new String[] {
    "input",
    "tableviewer",
    "treeviewer",
    "dialog",
    "canvas",
    "drag-and-drop",
    "complex-data",
    "row-layout",
    "fill-layout",
    "grid-layout",
    "file-upload",
    "oscilloscope",
    "carousel",
    "gmaps"
  };

  private Examples() {
  }

  public static Examples getInstance() {
    return new Examples();
  }

  public List<IExampleContribution> getContributions() {
    return createContributionList();
  }

  public IExampleContribution getContribution( String id ) {
    return getContributionsTracker().getContribution( id );
  }

  private List<IExampleContribution> createContributionList() {
    List<IExampleContribution> result = new ArrayList<IExampleContribution>();
    ExampleContributionsTracker tracker = getContributionsTracker();
    List<String> ids = new ArrayList<String>( tracker.getContributionIds() );
    for( String id : INCLUDE ) {
      IExampleContribution contribution = tracker.getContribution( id );
      if( contribution != null ) {
        result.add( contribution );
        ids.remove( id );
      }
    }
    for( String id : ids ) {
      IExampleContribution contribution = tracker.getContribution( id );
      if( contribution != null ) {
        result.add( contribution );
      }
    }
    return result;
  }

  private static ExampleContributionsTracker getContributionsTracker() {
    return Activator.getDefault().getExampleContributions();
  }
}

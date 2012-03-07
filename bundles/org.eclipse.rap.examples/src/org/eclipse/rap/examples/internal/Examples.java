/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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


public final class Examples {

  private static final List<ExampleCategory> EXAMPLE_CATEGORIES = createCategories();

  private Examples() {
  }

  public static Examples getInstance() {
    return new Examples();
  }

  public List<ExampleCategory> getCategories() {
    return Collections.unmodifiableList( EXAMPLE_CATEGORIES );
  }

  public IExampleContribution getContribution( String id ) {
    return getContributionsTracker().getContribution( id );
  }

  private static ExampleContributionsTracker getContributionsTracker() {
    return Activator.getDefault().getExampleContributions();
  }

  // TODO [rst] Read from configuration file
  private static List<ExampleCategory> createCategories() {
    List<ExampleCategory> exampleCategories = new ArrayList<ExampleCategory>();
    exampleCategories.add( createCategory( "Basic Widgets", "input", "canvas", "dialog" ) );
    exampleCategories.add( createCategory( "Trees && Tables", "treeviewer", "tableviewer", "table-markup" ) );
    exampleCategories.add( createCategory( "Layouts", "row-layout", "fill-layout", "grid-layout" ) );
    exampleCategories.add( createCategory( "Key Features", "drag-and-drop", "file-upload", "complex-data" ) );
    exampleCategories.add( createCategory( "Custom Widgets", "gmaps", "carousel", "oscilloscope" ) );
    return exampleCategories;
  }

  private static ExampleCategory createCategory( String name, String... contributions ) {
    ExampleCategory exampleCategory = new ExampleCategory( name );
    for( String contribution : contributions ) {
      exampleCategory.addContributionId( contribution );
    }
    return exampleCategory;
  }

}

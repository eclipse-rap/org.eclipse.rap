/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.viewer.internal.model;

import org.eclipse.rap.examples.viewer.IExamplePage;


public class ExampleCategory implements Comparable {

  private final String id;
  private final String name;
  private String description;
  private IExamplePage[] pages;

  public ExampleCategory( final String id, final String name ) {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    if( name == null ) {
      throw new NullPointerException( "name" );
    }
    this.id = id;
    this.name = name;
  }
  
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }

  public void setDescription( final String description ) {
    this.description = description;
  }

  public IExamplePage[] getPages() {
    return ( IExamplePage[] )pages.clone();
  }

  public void setPages( final IExamplePage[] pages ) {
    this.pages = ( IExamplePage[] )pages.clone();
  }

  public int compareTo( final Object object ) {
    ExampleCategory other = ( ExampleCategory )object;
    return name.compareTo( other.getName() );
  }

  public String toString() {
    return getName();
  }

  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj.getClass() == getClass() ) {
      ExampleCategory other = ( ExampleCategory )obj;
      result = other.id.equals( id ) && other.name.equals( name );
    }
    return result;
  }

  public int hashCode() {
    return id.hashCode();
  }
}

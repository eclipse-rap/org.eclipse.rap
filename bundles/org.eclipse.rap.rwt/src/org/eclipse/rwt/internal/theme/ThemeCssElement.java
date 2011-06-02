/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.ArrayList;
import java.util.Collection;


public class ThemeCssElement implements IThemeCssElement {

  private final String name;
  private Collection<String> properties;
  private Collection<String> styles;
  private Collection<String> states;

  public ThemeCssElement( final String name ) {
    this.name = name;
    this.properties = new ArrayList<String>();
    this.styles = new ArrayList<String>();
    this.states = new ArrayList<String>();
  }

  public String getName() {
    return name;
  }

  public String[] getProperties() {
    return properties.toArray( new String[ properties.size() ]);
  }

  public String[] getStyles() {
    return styles.toArray( new String[ styles.size() ] );
  }
  
  public String[] getStates() {
    return states.toArray( new String[ states.size() ] );
  }

  public void addProperty( String property ) {
    properties.add( property );
  }

  public void addStyle( String style ) {
    styles.add( style );
  }

  public void addState( String state ) {
    states.add( state );
  }
}

/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.ArrayList;
import java.util.Collection;


public class ThemeCssElement implements IThemeCssElement {

  private final String name;

  private Collection properties;

  private Collection styles;

  private Collection states;

  public ThemeCssElement( final String name ) {
    this.name = name;
    this.properties = new ArrayList();
    this.styles = new ArrayList();
    this.states = new ArrayList();
  }

  public String getName() {
    return name;
  }

  public String[] getProperties() {
    String[] result = new String[ properties.size() ];
    properties.toArray( result );
    return result;
  }

  public String[] getStyles() {
    String[] result = new String[ styles.size() ];
    styles.toArray( result );
    return result;
  }
  
  public String[] getStates() {
    String[] result = new String[ states.size() ];
    states.toArray( result );
    return result;
  }

  public void addProperty( final String property ) {
    properties.add( property );
  }

  public void addStyle( final String style ) {
    styles.add( style );
  }

  public void addState( final String state ) {
    states.add( state );
  }
}

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
package org.eclipse.rwt.internal.theme.css;

import org.eclipse.rwt.internal.theme.*;


/**
 * Contains all properties in a css rule block.
 */
public interface IStylePropertyMap {

  String[] getProperties();

  /**
   * @deprecated remove when property-based theming has been dropped
   */
  QxType getValue( String propertyName );
  
  QxType getValue( String propertyName, String type );

  QxFont getFont( String propertyName );

  QxBorder getBorder( String propertyName );

  QxBoxDimensions getBoxDimensions( String propertyName );

  QxDimension getDimension( String propertyName );

  QxColor getColor( String propertyName );

  QxImage getImage( String propertyName );
}

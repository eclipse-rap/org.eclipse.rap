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
  
  QxType getValue( final String property );

  QxFont getFont( final String propertyName );

  QxBorder getBorder( final String propertyName );

  QxBoxDimensions getBoxDimensions( final String propertyName );

  QxDimension getDimension( final String propertyName );

  QxColor getColor( final String propertyName );

  QxImage getBackgroundImage( final String propertyName );
}

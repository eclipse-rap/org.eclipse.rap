/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rwt.internal.util.EntitiesUtil;


/**
 * <p>
 * Utility class used by <code>HtmlResponseWriter</code>.
 * </p> 
 */
final class HtmlResponseWriterUtil {

  private static final Set EMPTY_TAGS = new HashSet();
  
  static {
    EMPTY_TAGS.add( "area" );
    EMPTY_TAGS.add( "br" );
    EMPTY_TAGS.add( "base" );
    EMPTY_TAGS.add( "basefont" );
    EMPTY_TAGS.add( "col" );
    EMPTY_TAGS.add( "frame" );
    EMPTY_TAGS.add( "hr" );
    EMPTY_TAGS.add( "img" );
    EMPTY_TAGS.add( "input" );
    EMPTY_TAGS.add( "isindex" );
    EMPTY_TAGS.add( "link" );
    EMPTY_TAGS.add( "meta" );
    EMPTY_TAGS.add( "param" );
  }
  
  // TODO [rh] check performance
  static boolean isEmptyTag( final String name ) {
    return EMPTY_TAGS.contains( name.toLowerCase() );
  }
  
  static String encode( final String text ) {
    return EntitiesUtil.encodeHTMLEntities( text );
  }
}
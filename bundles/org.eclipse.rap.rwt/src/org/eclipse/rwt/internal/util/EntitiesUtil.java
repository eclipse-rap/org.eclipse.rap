/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class EntitiesUtil {
  
  private static final Pattern DOUBLE_HYPHEN_PATTERN = Pattern.compile( "--" );

  public static String encodeHTMLEntities( final String text ) {
    String result = Entities.HTML40.escape( text );
    // Encode double-hyphens because they are not allowed inside comments. This
    // is necessary since in AJaX mode most markup is placed inside comment
    // tags. (see [WFT-36]) 
    Matcher matcher = DOUBLE_HYPHEN_PATTERN.matcher( result );
    result = matcher.replaceAll( "&#045;&#045;" );
    return result;
  }

  private EntitiesUtil() {
    // prevent instantiation
  }
}

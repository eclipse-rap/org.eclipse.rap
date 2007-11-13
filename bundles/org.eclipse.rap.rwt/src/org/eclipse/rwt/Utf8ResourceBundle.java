/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.UnsupportedEncodingException;
import java.util.*;

final class Utf8ResourceBundle {
  
  private final static Map bundles = new HashMap();
  
  static ResourceBundle getBundle( final String baseName,
                                   final Locale locale,
                                   final ClassLoader loader )
  {
    ResourceBundle bundle 
      = ResourceBundle.getBundle( baseName, locale, loader );
    ResourceBundle result;
    synchronized( bundles ) {
      result = ( ResourceBundle )bundles.get( bundle );
      if( result == null ) {
        result = createUtf8Bundle( bundle );
        bundles.put( bundle, result );
      }
    }
    return result;
  }

  private static ResourceBundle createUtf8Bundle( final ResourceBundle bundle )
  {
    ResourceBundle result = bundle;
    if( bundle instanceof PropertyResourceBundle ) {
      PropertyResourceBundle prb = ( PropertyResourceBundle )bundle;
      result = new Utf8PropertyResourceBundle( prb );
    }
    return result;
  }
  
  private static final class Utf8PropertyResourceBundle extends ResourceBundle {
    private PropertyResourceBundle bundle;

    private Utf8PropertyResourceBundle( final PropertyResourceBundle bundle ) {
      this.bundle = bundle;
    }

    public Enumeration getKeys() {
      return bundle.getKeys();
    }

    protected Object handleGetObject( final String key ) {
      String result = ( String )bundle.handleGetObject( key );
      try {
        // We do not buffer the encoded result since the RWT.NLS mechanism
        // creates and buffers the completly initialized nls instance. So each
        // entry should only be read once.
        if( result != null ) {
          result = new String( result.getBytes( "ISO-8859-1" ), "UTF-8" );
        }
      } catch( final UnsupportedEncodingException uee ) {
        // Shouldn't fail
        uee.printStackTrace();
      }
      return result;
    }
  }
}
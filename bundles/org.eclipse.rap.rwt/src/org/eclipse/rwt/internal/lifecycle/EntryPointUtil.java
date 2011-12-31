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
package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;


public class EntryPointUtil {

  private EntryPointUtil() {
    // prevent instantiation
  }

  public static String findEntryPoint() {
    String result = readFromStartupParameter();
    if( result == null ) {
      result = readFromBranding();
      if( result == null ) {
        result = EntryPointManager.DEFAULT;
      }
    }
    return result;
  }

  private static String readFromStartupParameter() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( "".equals( result ) ) {
      result = null;
    }
    return result;
  }

  private static String readFromBranding() {
    AbstractBranding branding = BrandingUtil.determineBranding();
    String result = branding.getDefaultEntryPoint();
    if( "".equals( result ) ) {
      result = null;
    }
    return result;
  }

}

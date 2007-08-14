/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.service.IServiceStore;


public interface IServiceStateInfo extends IServiceStore {
  
  void setExpired( boolean expired );

  boolean isExpired();

  void setExceptionOccured( boolean exceptionOcc );

  boolean isExceptionOccured();
  
  void setInvalidated( boolean invalidated );
  
  boolean isInvalidated();

  void setResponseWriter( final HtmlResponseWriter reponseWriter );

  HtmlResponseWriter getResponseWriter();
  
  void setDetectedBrowser( Browser browser );
  Browser getDetectedBrowser();

  /** <p>returns the event queue of this ServiceStateInfo.</p>
   *
   * <p>The event queue for a request contains all WebDataEvents, i.e. 
   * events which are fired from a component when its value changes.</p>
   */
  Object getEventQueue();
  void setEventQueue( Object eventQueue );

  /** <p>returns whether the w4t_startup request parameter should be ignored
   * during the render phase of the requests lifecycle.</p> 
   */
  boolean isIgnoreStartup();

  /** <p>sets whether the w4t_startup request parameter should be ignored
   * during the render phase of the requests lifecycle.</p>
   */
  void setIgnoreStartup( boolean ignoreStartup );

  boolean isFirstAccess();
}
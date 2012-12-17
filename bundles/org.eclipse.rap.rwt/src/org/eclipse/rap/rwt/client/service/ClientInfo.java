/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.client.service;

import java.util.Locale;


/**
 * The ClientInfo service provides basic information about the client device.
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ClientInfo extends ClientService {

  /**
   * Returns the offset between the client's local time and UTC.
   *
   * @return the offset in minutes
   */
  public int getTimezoneOffset();

  /**
   * Returns the first locale given by the client. Is null when the client does not give a locale.
   *
   * @return the locale
   */
  public Locale getLocale();

  /**
   * Returns all locales given by the client. Has length zero when the client gives no locales.
   *
   * @return the locale
   */
  public Locale[] getLocales();

}

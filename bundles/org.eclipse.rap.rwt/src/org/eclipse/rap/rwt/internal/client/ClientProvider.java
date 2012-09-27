package org.eclipse.rap.rwt.internal.client;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.client.Client;


/**
 * @since 2.0
 */
public interface ClientProvider {

  boolean accept( HttpServletRequest request );

  Client getClient();

}

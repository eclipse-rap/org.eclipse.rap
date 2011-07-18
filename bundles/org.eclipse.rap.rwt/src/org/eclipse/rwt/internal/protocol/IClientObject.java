/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.Map;

import org.eclipse.swt.widgets.Widget;


/**
 * A {@link IClientObject} acts as the connection between server and client objects e.g. widgets.
 * It provides helper methods to transfer changes from the server to the client. A 
 * {@link IClientObject} is unique per widget and should always be instantiated using the 
 * {@link ClientObjectFactory#getForWidget(Widget)} method.
 *
 * @see ClientObjectFactory
 * 
 * @since 1.5
 */
public interface IClientObject {

  /**
   * Creates a new object on the client-side by creating an instance of the corresponding client
   * class defined by the server object's class name. This is normally done in the
   * <code>renderInitialization</code> method of the widgets life-cycle adapter (LCA).
   * @param properties The properties which are mandatory to construct this object on the 
   * client-side.
   */
  void create( Map<String, Object> properties );

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param name the attributes name on the client widget.
   * @param value the new value of the property.
   */
  void setProperty( String name, int value );

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param name the attributes name on the client widget.
   * @param value the new value of the property.
   */
  void setProperty( String name, double value );

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param name the attributes name on the client widget.
   * @param value the new value of the property.
   */
  void setProperty( String name, boolean value );

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param name the attributes name on the client widget.
   * @param value the new value of the property.
   */
  void setProperty( String name, String value );

  /**
   * Sets the specified property of the client-side widget to a new value.
   * 
   * @param name the attributes name on the client widget.
   * @param value the new value of the property.
   */
  void setProperty( String name, Object value );

  /**
   * This will add a listener to the client-side widget. This will tell the widget on which events
   * it should react.
   * 
   * @param eventName the name of the event the client-widget should react to.
   */
  void addListener( String eventName );

  /**
   * This will remove a listener from the client side widget of this {@link IClientObject}.
   * 
   * @param eventName the name of the event the client-widget should no longer react to.
   */
  void removeListener( String eventName );

  /**
   * Calls a specific method on the client-side widget.
   * 
   * @param method the method name to call.
   * @param properties named properties to pass in the method call.
   */
  void call( String method, Map<String, Object> properties );

  /**
   * Tells the client that it should execute a script when the given mimetype is supported.
   * 
   * @param type the mimetype of the script, value should be something like "text/javascript".
   * @param script the content of the script which will be executed. The server does not validate 
   * the content.
   */
  void executeScript( String type, String script );

  /**
   * This method should be called right before a server-side widget will be disposed. 
   * After calling this method the client should destroy the client-object too.
   */
  void destroy();
  
}

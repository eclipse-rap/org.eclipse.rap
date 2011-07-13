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
 * A {@link IClientObject} acts as a broker between server and client widgets.
 * It provides helper methods to transfer changes from the server to the client 
 * side widgets. A {@link IClientObject} is unique per widget and should 
 * always be instantiated via the 
 * {@link ClientObjectFactory#getForWidget(Widget)} method.
 *
 * @see ClientObjectFactory
 * 
 * @since 1.5
 */
public interface IClientObject {

  /**
   * Creates a new widget on the client-side by creating an instance of the corresponding client
   * class defined by the widget's class name. This is normally done in the
   * <code>renderInitialization</code> method of the widgets life-cycle adapter (LCA).
   * 
   * @param styles TODO
   * @param properties TODO
   */
  void create( String[] styles, Map<String, Object> properties );

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
   * This will add a listener to the client side widget of this {@link IClientObject}.
   * 
   * @param eventName the name of the event the client widget should listen to.
   */
  void addListener( String eventName );

  /**
   * This will remove a listener from the client side widget of this {@link IClientObject}.
   * 
   * @param eventName the name of the event the client widget should no longer listen to.
   */
  void removeListener( String eventName );

  /**
   * Calls a specific method of the widget on the client-side.
   * 
   * @param methodName the method name.
   * @param properties TODO
   */
  void call( String methodName, Map<String, Object> properties );

  /**
   * Executes a script on the client side.
   * 
   * @param type the type of the script, value should be something like "text/javascript".
   * @param script the content of the script which will be executed.
   */
  void executeScript( String type, String script );

  /**
   * DisposeWidget is used to dispose of the widget of this {@link IClientObject} on the client
   * side.
   */
  void destroy();
  
}

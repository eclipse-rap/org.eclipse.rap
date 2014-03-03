/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;


/**
 * Utility class that provides a number of useful static methods to support the
 * implementation of life cycle adapters (LCAs) for {@link Control}s.
 *
 * @since 2.0
 * @deprecated New custom widgets should use the RemoteObject API instead of LCAs.
 * @see org.eclipse.rap.rwt.remote.RemoteObject
 * @see WidgetLCAUtil
 */
@Deprecated
public class ControlLCAUtil {

  private ControlLCAUtil() {
    // prevent instance creation
  }

  /**
   * Reads the bounds of the specified control from the current request and
   * applies it to the control. If no bounds are not submitted for the control,
   * it remains unchanged.
   *
   * @param control the control whose bounds to read and set
   */
  public static void readBounds( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.readBounds( control );
  }

  /**
   * Process a <code>HelpEvent</code> if the current request specifies that
   * there occured a help event for the given <code>widget</code>.
   *
   * @param control the control to process
   */
  public static void processMenuDetect( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processMenuDetect( control );
  }

  public static void processEvents( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processEvents( control );
  }

  public static void processMouseEvents( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processMouseEvents( control );
  }

  public static void processKeyEvents( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processKeyEvents( control );
  }

  public static void processSelection( Widget widget, Item item, boolean readBounds ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processSelection( widget,
                                                                            item,
                                                                            readBounds );
  }

  public static void processDefaultSelection( Widget widget, Item item ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.processDefaultSelection( widget, item );
  }

  /**
   * Preserves the values of the following properties of the specified control:
   * <ul>
   * <li>bounds</li>
   * <li>z-index (except for Shells)</li>
   * <li>tab index</li>
   * <li>tool tip text</li>
   * <li>menu</li>
   * <li>visible</li>
   * <li>enabled</li>
   * <li>foreground</li>
   * <li>background</li>
   * <li>background image</li>
   * <li>font</li>
   * <li>cursor</li>
   * <li>whether ControlListeners are registered</li>
   * <li>whether ActivateListeners are registered</li>
   * <li>whether MouseListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
   * <li>whether KeyListeners are registered</li>
   * <li>whether TraverseListeners are registered</li>
   * <li>whether HelpListeners are registered</li>
   * <li>whether MenuDetectListeners are registered</li>
   * </ul>
   *
   * @param control the control whose parameters to preserve
   * @see #renderChanges(Control)
   */
  public static void preserveValues( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.preserveValues( control );
  }

  /**
   * Preserves the value of the specified widget's background image.
   *
   * @param control the control whose background image property to preserve
   * @see #renderBackgroundImage(Control)
   */
  public static void preserveBackgroundImage( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.preserveBackgroundImage( control );
  }

  /**
   * Determines for all of the following properties of the specified control
   * whether the property has changed during the processing of the current
   * request and if so, writes a protocol message to the response that updates the
   * corresponding client-side property.
   * <ul>
   * <li>bounds</li>
   * <li>z-index (except for Shells)</li>
   * <li>tab index</li>
   * <li>tool tip text</li>
   * <li>menu</li>
   * <li>visible</li>
   * <li>enabled</li>
   * <li>foreground</li>
   * <li>background</li>
   * <li>background image</li>
   * <li>font</li>
   * <li>cursor</li>
   * <!--li>whether ControlListeners are registered</li-->
   * <li>whether ActivateListeners are registered</li>
   * <li>whether MouseListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
   * <li>whether KeyListeners are registered</li>
   * <li>whether TraverseListeners are registered</li>
   * <li>whether HelpListeners are registered</li>
   * </ul>
   *
   * @param control the control whose properties to set
   * @see #preserveValues(Control)
   */
  public static void renderChanges( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderChanges( control );
  }

  /**
   * Determines whether the bounds of the given control have changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side bounds.
   *
   * @param control the control whose bounds to write
   */
  public static void renderBounds( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderBounds( control );
  }

  /**
   * Determines whether the tool tip of the given control has changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side tool tip.
   *
   * @param control the control whose tool tip to write
   */
  public static void renderToolTip( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderToolTip( control );
  }

  /**
   * Determines whether the property <code>menu</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side menu
   * property.
   *
   * @param control the control whose menu property to write
   */
  public static void renderMenu( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderMenu( control );
  }

  /**
   * Determines whether the visibility of the given control has changed during
   * the processing of the current request and if so, writes JavaScript code to
   * the response that updates the client-side visibility.
   *
   * @param control the control whose visibility to write
   */
  public static void renderVisible( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderVisible( control );
  }

  /**
   * Determines whether the property <code>enabled</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side enabled
   * property.
   *
   * @param control the control whose enabled property to write
   */
  public static void renderEnabled( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderEnabled( control );
  }

  /**
   * Determines whether the property <code>foreground</code> of the given
   * control has changed during the processing of the current request and if so,
   * writes a protocol message to the response that updates the client-side
   * foreground property.
   *
   * @param control the control whose foreground property to write
   */
  public static void renderForeground( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderForeground( control );
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * control has changed during the processing of the current request and if so,
   * writes a protocol message to the response that updates the client-side
   * background property.
   *
   * @param control the control whose background property to write
   */
  public static void renderBackground( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderBackground( control );
  }

  /**
   * Determines whether the background image of the given control has changed
   * during the processing of the current request and if so, writes a protocol
   * message to the response that updates the client-side background image
   * property.
   *
   * @param control the control whose background image property to write
   */
  public static void renderBackgroundImage( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderBackgroundImage( control );
  }

  /**
   * Determines whether the property <code>font</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side font property.
   *
   * @param control the control whose font property to write
   */
  public static void renderFont( Control control ) {
    org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil.renderFont( control );
  }

}

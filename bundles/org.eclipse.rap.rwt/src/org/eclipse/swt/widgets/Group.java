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

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Rectangle;


/**
 * Instances of this class provide an etched border
 * with an optional title.
 * <p>
 * Shadow styles are hints and may not be honoured
 * by the platform.  To create a group with the
 * default shadow style for the platform, do not
 * specify a shadow style.</p>
 * <p>The various SHADOW_* styles are not yet implemented.</p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the above styles may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <hr/>
 * Note: The styles SHADOW_XXX are not yet implemented in RWT.
 */
public class Group extends Composite {
  
  private static final int TRIM_LEFT = 5;
  private static final int TRIM_TOP = 15;
  private static final int TRIM_RIGHT = 5;
  private static final int TRIM_BOTTOM = 5;

  private String text = "";

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   * <p>The various SHADOW_* styles are not yet implemented.</p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#SHADOW_ETCHED_IN
   * @see SWT#SHADOW_ETCHED_OUT
   * @see SWT#SHADOW_IN
   * @see SWT#SHADOW_OUT
   * @see SWT#SHADOW_NONE
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  // TODO: [bm] implement shadow styles for Group
  public Group( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  /**
   * Sets the receiver's text, which is the string that will
   * be displayed as the receiver's <em>title</em>, to the argument,
   * which may not be null. The string may include the mnemonic character.
   * </p>
   * Mnemonics are indicated by an '&amp;' that causes the next
   * character to be the mnemonic.  When the user presses a
   * key sequence that matches the mnemonic, focus is assigned
   * to the first child of the group. On most platforms, the
   * mnemonic appears underlined but may be emphasised in a
   * platform specific manner.  The mnemonic indicator character
   * '&amp;' can be escaped by doubling it in the string, causing
   * a single '&amp;' to be displayed.
   * </p>
   * @param string the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  /**
   * Returns the receiver's text, which is the string that the
   * is used as the <em>title</em>. If the text has not previously
   * been set, returns an empty string.
   *
   * @return the text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText() {
    checkWidget();
    return text;
  }
    
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle bounds = getBounds();
    int width = Math.max( 0, bounds.width - TRIM_LEFT - TRIM_RIGHT );
    int height = Math.max( 0, bounds.height - TRIM_TOP - TRIM_BOTTOM );
    return new Rectangle( TRIM_LEFT, TRIM_TOP, width, height );
  }
  
  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height ) 
  {
    return super.computeTrim( x - TRIM_LEFT,
                              y - TRIM_TOP,
                              width + TRIM_LEFT + TRIM_RIGHT,
                              height + TRIM_TOP + TRIM_BOTTOM );
  }
  
  private static int checkStyle( final int style ) {
    int result = style | SWT.NO_FOCUS;
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    return result & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }
}

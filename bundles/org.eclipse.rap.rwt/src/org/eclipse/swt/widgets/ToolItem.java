/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.IToolItemAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;

/**
 * Instances of this class represent a selectable user interface object
 * that represents a button in a tool bar.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>PUSH, CHECK, RADIO, SEPARATOR, DROP_DOWN</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles CHECK, PUSH, RADIO, SEPARATOR and DROP_DOWN 
 * may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class ToolItem extends Item {

  private static final int DEFAULT_WIDTH = 24;
  private static final int DEFAULT_HEIGHT = 22;
  private static final int DROP_DOWN_ARROW_WIDTH = 13;

  private final ToolBar parent;
  private boolean selected;
  private Control control;
  private int width;
  private String toolTipText;
  private boolean visible = true;
  
  private IToolItemAdapter toolitemAdapter = new IToolItemAdapter() {

    public boolean getVisible() {
      return ToolItem.this.visible;
    }

  };
 

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>ToolBar</code>) and a style value
   * describing its behavior and appearance. The item is added
   * to the end of the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
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
   * @see SWT#PUSH
   * @see SWT#CHECK
   * @see SWT#RADIO
   * @see SWT#SEPARATOR
   * @see SWT#DROP_DOWN
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ToolItem( final ToolBar parent, final int style ) {
    this( checkNull( parent ), checkStyle( style ), parent.getItemCount() );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>ToolBar</code>), a style value
   * describing its behavior and appearance, and the index
   * at which to place it in the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#PUSH
   * @see SWT#CHECK
   * @see SWT#RADIO
   * @see SWT#SEPARATOR
   * @see SWT#DROP_DOWN
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ToolItem( final ToolBar parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    ItemHolder.insertItem( parent, this, index );
    computeInitialWidth();
  }

  /**
   * Returns the receiver's parent, which must be a <code>ToolBar</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public ToolBar getParent() {
    checkWidget();
    return parent;
  }

  ////////////////////////////////////////
  // Displayed content (text, image, etc.)
  
  /**
   * Sets the receiver's text. The string may include
   * the mnemonic character.
   * </p>
   * <p>
   * Mnemonics are indicated by an '&amp;' that causes the next
   * character to be the mnemonic.  When the user presses a
   * key sequence that matches the mnemonic, a selection
   * event occurs. On most platforms, the mnemonic appears
   * underlined but may be emphasised in a platform specific
   * manner.  The mnemonic indicator character '&amp;' can be
   * escaped by doubling it in the string, causing a single
   * '&amp;' to be displayed.
   * </p>
   * 
   * @param text the new text
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
    if( ( style & SWT.SEPARATOR ) == 0 ) {
      super.setText( text );
    }
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & SWT.SEPARATOR ) == 0 ) {
      // TODO [rh] uncomment or remove when decided whether isDisposed will
      //      be 'implemented'
//      if( image != null && image.isDisposed() ) {
//        error( SWT.ERROR_INVALID_ARGUMENT );
//      }
      super.setImage( image );
    } 
  }
  
  /**
   * Sets the control that is used to fill the bounds of
   * the item when the item is a <code>SEPARATOR</code>.
   *
   * @param control the new control
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        SWT.error( SWT.ERROR_INVALID_PARENT );
      }
    }
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      this.control = control;
    }
    resizeControl();
  }

  /**
   * Returns the control that is used to fill the bounds of
   * the item when the item is a <code>SEPARATOR</code>.
   *
   * @return the control
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Control getControl() {
    checkWidget();
    return control;
  }
  
  /**
   * Sets the receiver's tool tip text to the argument, which
   * may be null indicating that no tool tip text should be shown.
   *
   * @param string the new tool tip text (or null)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setToolTipText( final String string ) {
    checkWidget();
    toolTipText = string;
  }

  /**
   * Returns the receiver's tool tip text, or null if it has not been set.
   *
   * @return the receiver's tool tip text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getToolTipText() {
    checkWidget();
    return toolTipText;
  }
  
  ///////////
  // Enabled
  
  /**
   * Enables the receiver if the argument is <code>true</code>,
   * and disables it otherwise.
   * <p>
   * A disabled control is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   * </p>
   *
   * @param enabled the new enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setEnabled( final boolean enabled ) {
    checkWidget();
    if( enabled ) {
      state &= ~DISABLED;
    } else {
      state |= DISABLED;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is enabled, and
   * <code>false</code> otherwise. A disabled control is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #isEnabled
   */
  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }
  
  /**
   * Returns <code>true</code> if the receiver is enabled and all
   * of the receiver's ancestors are enabled, and <code>false</code>
   * otherwise. A disabled control is typically not selectable from the
   * user interface and draws with an inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #getEnabled
   */
  public boolean isEnabled() {
    checkWidget();
    return getEnabled() && parent.isEnabled();
  }

  /////////////
  // Dimensions
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] decent implementation for VERTICAL adlignment missing
  public Rectangle getBounds() {
    checkWidget();
    Rectangle clientArea = parent.getClientArea();
    int left = clientArea.x;
    int top = clientArea.y;
    int index = parent.indexOf( this );
    for( int i = 0; i < index; i++ ) {
      left += parent.getItem( i ).getBounds().width;
    }
    
    int height = DEFAULT_HEIGHT;
    if( !"".equals( getText() ) ) {
      int charHeight = TextSizeDetermination.getCharHeight( parent.getFont()  );
      height = Math.max( DEFAULT_HEIGHT, charHeight );
      if( getImage() != null ) {
        height = Math.max( height, getImage().getBounds().y );
      }
    }
    return new Rectangle( left, top, getWidth(), height );
  }

  /**
   * Gets the width of the receiver.
   *
   * @return the width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getWidth() {
    checkWidget();
    int result;
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      result = width;
    } else {
      // TODO [rh] must be kept in sync with DefaultAppearanceTheme.js
      result = 7; // approx left + right padding as defined in appearance theme 
      if( getImage() != null ) {
        result += getImage().getBounds().width;
      }
      String text = getText();
      if( !"".equals( text ) ) {
        Font font = parent.getFont();
        // TODO [fappel]: need some more space for the Workbench perspective
        //                switcher. Check this after a proper font size
        //                calculation is in place
        result += 11 + TextSizeDetermination.stringExtent( font, getText() ).x;
      }
      if( ( style & SWT.DROP_DOWN ) != 0 ) {
        result += DROP_DOWN_ARROW_WIDTH;
      }
    }
    return result;
  }

  /**
   * Sets the width of the receiver, for <code>SEPARATOR</code> ToolItems.
   *
   * @param width the new width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setWidth( final int width ) {
    checkWidget();
    if( ( style & SWT.SEPARATOR ) != 0 && width >= 0 ) {
      this.width = width;
      resizeControl();
    }
  }
  
  ////////////
  // Selection
  
  /**
   * Returns <code>true</code> if the receiver is selected,
   * and false otherwise.
   * <p>
   * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
   * it is selected when it is checked (which some platforms draw as a
   * pushed in button). If the receiver is of any other type, this method
   * returns false.
   * </p>
   *
   * @return the selection state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getSelection() {
    checkWidget();
    boolean result = selected;
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) == 0 ) {
      result = false;
    }
    return result;
  }
  
  /**
   * Sets the selection state of the receiver.
   * <p>
   * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
   * it is selected when it is checked (which some platforms draw as a
   * pushed in button).
   * </p>
   *
   * @param selected the new selection state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final boolean selected ) {
    checkWidget();
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      this.selected = selected;
    }
  }

  ///////////////////////////////////////////
  // Listener registration and deregistration
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is selected, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * When <code>widgetSelected</code> is called when the mouse is over the arrow portion of a drop-down tool,
   * the event object detail field contains the value <code>SWT.ARROW</code>.
   * <code>widgetDefaultSelected</code> is not called.
   * </p>
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is selected.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #addSelectionListener
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }
  
  ///////////////////////////////////
  // Methods to dispose of the widget
  
  void releaseChildren() {
    // do nothing
  }

  void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  void releaseWidget() {
    // do nothing
  }
  
  //////////////////
  // Helping methods

  public Object getAdapter( final Class adapter ) {
    Object result;
    if ( adapter == IToolItemAdapter.class ) {
      result = toolitemAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  private void resizeControl() {
    if( control != null && !control.isDisposed() ) {
      Rectangle itemRect = getBounds();
      control.setSize( itemRect.width, itemRect.height );
      // In contrast to SWT, placement is relative to the toolitem. 
      Rectangle rect = control.getBounds();
      int xoff = ( itemRect.width - rect.width ) / 2;
      int yoff = ( itemRect.height - rect.height ) / 2;
      control.setLocation( xoff, yoff );
    }
  }

  private void computeInitialWidth() {
    if( ( style & SWT.SEPARATOR ) != 0 ) {
      width = 8;
    } else {
      width = DEFAULT_WIDTH;
      if( ( style & SWT.DROP_DOWN ) != 0 ) {
        width += DROP_DOWN_ARROW_WIDTH;
      }
    }
  }
  
  private static ToolBar checkNull( final ToolBar parent ) {
    if( parent == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return parent;
  }

  private static int checkStyle( final int style ) {
    return checkBits( style, 
                      SWT.PUSH, 
                      SWT.CHECK,
                      SWT.RADIO, 
                      SWT.SEPARATOR, 
                      SWT.DROP_DOWN,
                      0 );
  }
  
  void setVisible( boolean visible ) {
    this.visible = visible;
  }
}

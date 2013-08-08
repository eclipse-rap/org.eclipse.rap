/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.createNamespace( "rwt.scripting", {} );


/**
 * Note: Thise class is available within ClientScriptingFunction simply as "SWT" (no namespace)
 */
rwt.scripting.SWT = {

  /**
   * The key down event type
   */
  KeyDown : 1,

  /**
   * The key up event type
   */
  KeyUp : 2,

  /**
   * The mouse down event type
   */
  MouseDown : 3,

  /**
   * The mouse up event type
   */
  MouseUp : 4,

  /**
   * The mouse move event type
   */
  MouseMove : 5,

  /**
   * The mouse enter event type
   */
  MouseEnter : 6,

  /**
   * The mouse exit event type
   */
  MouseExit : 7,

  /**
   * The mouse double click event type
   */
  MouseDoubleClick : 8,

  /**
   * The paint event type
   */
  Paint : 9,

  /**
   * The selection event type
   */
  Selection : 13,

  /**
   * The default selection event type
   */
  DefaultSelection : 14,

  /**
   * The focus in event type
   */
  FocusIn : 15,

  /**
   * The focus out event type
   */
  FocusOut : 16,

  /**
   * The show event type
   */
  Show : 22,

  /**
   * The hide event type
   */
  Hide : 23,

  /**
   * The modify event type
   *
   * Currently only supported by Text
   */
  Modify : 24,

  /**
   * The verify event type
   *
   * Current limitations:
   *  - works for Text only
   *  - is not fired on programatic changes
   *  - calling setText in verify onsupported
   */
  Verify : 25,

  /**
   * Keyboard event constant representing the UP ARROW key
   */
  ARROW_UP : 38,

  /**
   * Keyboard event constant representing the DOWN ARROW key
   */
  ARROW_DOWN : 40,

  /**
   * Keyboard event constant representing the LEFT ARROW key
   */
  ARROW_LEFT : 37,

  /**
   * Keyboard event constant representing the RIGHT ARROW key
   */
  ARROW_RIGHT : 39,

  /**
   * Keyboard event constant representing the PAGE UP key
   */
  PAGE_UP : 33,

  /**
   * Keyboard event constant representing the PAGE DOWN key
   */
  PAGE_DOWN : 34,

  /**
   * Keyboard event constant representing the HOME key
   */
  HOME : 36,

  /**
   * Keyboard event constant representing the END key
   */
  END : 35,

  /**
   * Keyboard event constant representing the INSERT key
   */
  INSERT : 45,

  /**
   * Keyboard event constant representing the F1 key
   */
  F1 : 112,

  /**
   * Keyboard event constant representing the F2 key
   */
  F2 : 113,

  /**
   * Keyboard event constant representing the F3 key
   */
  F3 : 114,

  /**
   * Keyboard event constant representing the F4 key
   */
  F4 : 115,

  /**
   * Keyboard event constant representing the F5 key
   */
  F5 : 116,

  /**
   * Keyboard event constant representing the F6 key
   */
  F6 : 117,

  /**
   * Keyboard event constant representing the F7 key
   */
  F7 : 118,

  /**
   * Keyboard event constant representing the F8 key
   */
  F8 : 119,

  /**
   * Keyboard event constant representing the F9 key
   */
  F9 : 120,

  /**
   * Keyboard event constant representing the F10 key
   */
  F10 : 121,

  /**
   * Keyboard event constant representing the F11 key
   */
  F11 : 122,

  /**
   * Keyboard event constant representing the F12 key
   */
  F12 : 123,

  /**
   * Keyboard event constant representing the numeric key
   */
  KEYPAD_MULTIPLY : 106,

  /**
   * Keyboard event constant representing the numeric key
   */
  KEYPAD_ADD : 107,

  /**
   * Keyboard event constant representing the numeric key
   */
  KEYPAD_SUBTRACT : 109,

  /**
   * Keyboard event constant representing the numeric key
   */
  KEYPAD_DECIMAL : 110,

  /**
   * Keyboard event constant representing the numeric key
   */
  KEYPAD_DIVIDE : 111,

  /**
   * Keyboard event constant representing the numeric key
   * pad zero key
   */
  KEYPAD_0 : 96,

  /**
   * Keyboard event constant representing the numeric key
   * pad one key
   */
  KEYPAD_1 : 97,

  /**
   * Keyboard event constant representing the numeric key
   * pad two key
   */
  KEYPAD_2 : 98,

  /**
   * Keyboard event constant representing the numeric key
   * pad three key
   */
  KEYPAD_3 : 99,

  /**
   * Keyboard event constant representing the numeric key
   * pad four key
   */
  KEYPAD_4 : 100,

  /**
   * Keyboard event constant representing the numeric key
   * pad five key
   */
  KEYPAD_5 : 101,

  /**
   * Keyboard event constant representing the numeric key
   * pad six key
   */
  KEYPAD_6 : 102,

  /**
   * Keyboard event constant representing the numeric key
   * pad seven key
   */
  KEYPAD_7 : 103,

  /**
   * Keyboard event constant representing the numeric key
   * pad eight key
   */
  KEYPAD_8 : 104,

  /**
   * Keyboard event constant representing the numeric key
   * pad nine key
   */
  KEYPAD_9 : 105,

  /**
   * Keyboard event constant representing the numeric key
   * pad equal key
   */
  KEYPAD_EQUAL : 61,

  /**
   * Keyboard event constant representing the numeric key
   * pad enter key
   */
  KEYPAD_CR : 80,

  /**
   * Keyboard event constant representing the caps
   * lock key
   */
  CAPS_LOCK : 20,

  /**
   * Keyboard event constant representing the num
   * lock key
   */
  NUM_LOCK : 144,

  /**
   * Keyboard event constant representing the scroll
   * lock key
   */
  SCROLL_LOCK : 145,

  /**
   * Keyboard event constant representing the pause
   * key
   */
  PAUSE : 19,

  /**
   * Keyboard event constant representing the break
   * key
   */
  BREAK : 19,

  /**
   * Keyboard event constant representing the print screen
   * key
   */
  PRINT_SCREEN : 44,

  /**
   * keyboard and/or mouse event mask indicating that the ALT key
   * was pushed on the keyboard when the event was generated
   */
  ALT : 1 << 16,

  /**
   * Keyboard and/or mouse event mask indicating that the SHIFT key
   * was pushed on the keyboard when the event was generated
   */
  SHIFT : 1 << 17,

  /**
   * Keyboard and/or mouse event mask indicating that the CTRL key
   * was pushed on the keyboard when the event was generated
   */
  CTRL : 1 << 18,

  /**
   * Keyboard and/or mouse event mask indicating that the CTRL key
   * was pushed on the keyboard when the event was generated. This
   * is a synonym for CTRL
   */
  CONTROL : 1 << 18,

  /**
   * Keyboard and/or mouse event mask indicating that the COMMAND key
   * was pushed on the keyboard when the event was generated
   */
  COMMAND : 1 << 22,

  // NOTE : The following constants are characters in SWT and can be compared with both the
  // keyCode and the character field of an event. Here these keys are currently only representend
  // by the events keyCode, therefore these are numbers instead and can only be compared with
  // the keyCode field.

  /**
   * Keyboard event constant representing the delete key
   */
  DEL : 46,

  /**
   * Keyboard event constant representing the escape key
   */
  ESC : 27,

  /**
   * Keyboard event constant representing the backspace key
   */
  BS : 8,

  /**
   * Keyboard event constant representing the carriage return key
   */
  CR : 13,

  /**
   * Keyboard event constant representing the tab key
   */
  TAB : 9

};

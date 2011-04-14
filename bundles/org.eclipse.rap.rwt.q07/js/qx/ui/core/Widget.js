/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * This is the main widget, all visible objects in the application extend this.
 *
 * @appearance widget
 * @state selected Set by {@link qx.ui.selection.SelectionManager#renderItemSelectionState}
 * @state anchor Set by {@link qx.ui.selection.SelectionManager#renderItemAnchorState}
 * @state lead Set by {@link qx.ui.selection.SelectionManager#renderItemLeadState}
 *
 * @state disabled Set by {@link qx.core.Object#enabled}
 * @state focused Set by {@link #focused}
 */
qx.Class.define( "qx.ui.core.Widget", {
  extend : qx.core.Target,
  include : qx.util.manager.MConnectedObject,
  type : "abstract",

  construct : function() {
    this.base( arguments );
    this._layoutChanges = {};
    this.initHideFocus();
    this._usesComplexBorder = false;
  },

  events: {
    "beforeAppear"    : "qx.event.type.Event",
    "appear"          : "qx.event.type.Event",
    "beforeDisappear" : "qx.event.type.Event",
    "disappear"       : "qx.event.type.Event",
    "insertDom"       : "qx.event.type.Event",
    "create"          : "qx.event.type.Event",
    "execute"         : "qx.event.type.Event",
    "flush"           : "qx.event.type.DataEvent",

    /** (Fired by {@link org.eclipse.rwt.EventHandler}) */
    "mouseover"       : "qx.event.type.MouseEvent",
    "mousemove"       : "qx.event.type.MouseEvent",
    "mouseout"        : "qx.event.type.MouseEvent",
    "mousedown"       : "qx.event.type.MouseEvent",
    "mouseup"         : "qx.event.type.MouseEvent",
    "mousewheel"      : "qx.event.type.MouseEvent",
    "click"           : "qx.event.type.MouseEvent",
    "dblclick"        : "qx.event.type.MouseEvent",
    "contextmenu"     : "qx.event.type.MouseEvent",
    "elementOver"     : "qx.event.type.MouseEvent",
    "elementOut"      : "qx.event.type.MouseEvent",
    "keydown"         : "qx.event.type.KeyEvent",
    "keypress"        : "qx.event.type.KeyEvent",
    "keyup"           : "qx.event.type.KeyEvent",

    /** (Fired by {@link qx.ui.core.Parent}) */
    "focusout"        : "qx.event.type.FocusEvent",
    "focusin"         : "qx.event.type.FocusEvent",
    "blur"            : "qx.event.type.FocusEvent",
    "focus"           : "qx.event.type.FocusEvent",

    /** (Fired by {@link qx.event.handler.DragAndDropHandler}) */
    "dragdrop"        : "qx.event.type.DragEvent",
    "dragout"         : "qx.event.type.DragEvent",
    "dragover"        : "qx.event.type.DragEvent",
    "dragmove"        : "qx.event.type.DragEvent",
    "dragstart"       : "qx.event.type.DragEvent",
    "dragend"         : "qx.event.type.DragEvent"
  },

  statics : {

    //////////////////////
    // Global Widget Flush

    _globalWidgetQueue : [],
    _globalElementQueue : [],
    _globalStateQueue : [],
    _globalJobQueue : [],
    _globalLayoutQueue : [],
    _fastGlobalDisplayQueue : [],
    _lazyGlobalDisplayQueues : {},
    _globalDisposeQueue : [],

    _autoFlushTimeout : null,
    _flushGlobalQueuesPhase : 0,
    _FLUSH_PHASE_IDLE : 0,
    _FLUSH_PHASE_WIDGET : 1,
    _FLUSH_PHASE_STATE : 2,
    _FLUSH_PHASE_ELEMENT : 3,
    _FLUSH_PHASE_JOB : 4,
    _FLUSH_PHASE_LAYOUT : 5,
    _FLUSH_PHASE_DISPLAY : 6,
    _FLUSH_PHASE_DISPOSE : 7,

    _initAutoFlush : function( phase ) {
      if( qx.ui.core.Widget._autoFlushTimeout == null ) {
        // RAP: Fix for bug 303162 
        if(    !qx.ui.core.Widget._inFlushGlobalQueues 
            || phase < qx.ui.core.Widget._flushGlobalQueuesPhase ) {
          qx.ui.core.Widget._autoFlushTimeout 
            = window.setTimeout( qx.ui.core.Widget._autoFlushHelper, 0 );
        }
      }
    },

    _removeAutoFlush : function() {
      if( qx.ui.core.Widget._autoFlushTimeout != null ) {
        window.clearTimeout( qx.ui.core.Widget._autoFlushTimeout );
        qx.ui.core.Widget._autoFlushTimeout = null;
      }
    },

    _autoFlushHelper : function() {
      qx.ui.core.Widget._autoFlushTimeout = null;
      if( !qx.core.Object.inGlobalDispose() ) {
        qx.ui.core.Widget.flushGlobalQueues();
      }
    },

    flushGlobalQueues : function() {
      if( qx.ui.core.Widget._autoFlushTimeout != null ) {
        qx.ui.core.Widget._removeAutoFlush();
      }
      if( qx.ui.core.Widget._inFlushGlobalQueues ) {
        return;
      }
      var app = qx.core.Init.getInstance().getApplication();
      if( app.getUiReady && !app.getUiReady() ) {
        return;
      }
      qx.ui.core.Widget._inFlushGlobalQueues = true;
      qx.ui.core.Widget.flushGlobalWidgetQueue();
      qx.ui.core.Widget.flushGlobalStateQueue();
      qx.ui.core.Widget.flushGlobalElementQueue();
      qx.ui.core.Widget.flushGlobalJobQueue();
      qx.ui.core.Widget.flushGlobalLayoutQueue();
      qx.ui.core.Widget.flushGlobalDisplayQueue();
      qx.ui.core.Widget.flushGlobalDisposeQueue();
      qx.ui.core.Widget._flushGlobalQueuesPhase = qx.ui.core.Widget._FLUSH_PHASE_IDLE;
      delete qx.ui.core.Widget._inFlushGlobalQueues;
    },

    addToGlobalWidgetQueue : function(vWidget) {
      if (!vWidget._isInGlobalWidgetQueue && vWidget._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_WIDGET );
        }
        qx.ui.core.Widget._globalWidgetQueue.push(vWidget);
        vWidget._isInGlobalWidgetQueue = true;
      }
    },

    removeFromGlobalWidgetQueue : function(vWidget) {
      if (vWidget._isInGlobalWidgetQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalWidgetQueue, vWidget);
        delete vWidget._isInGlobalWidgetQueue;
      }
    },

    flushGlobalWidgetQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_WIDGET;      
      var vQueue = qx.ui.core.Widget._globalWidgetQueue, vLength, vWidget;
      while ((vLength = vQueue.length) > 0) {
        for (var i=0; i<vLength; i++) {
          vWidget = vQueue[i];
          vWidget.flushWidgetQueue();
          delete vWidget._isInGlobalWidgetQueue;
        }
        vQueue.splice(0, vLength);
      }
      qx.ui.core.Widget._globalWidgetQueue = [];
    },

    addToGlobalElementQueue : function(vWidget) {
      if (!vWidget._isInGlobalElementQueue && vWidget._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_ELEMENT );
        }
        qx.ui.core.Widget._globalElementQueue.push(vWidget);
        vWidget._isInGlobalElementQueue = true;
      }
    },

    removeFromGlobalElementQueue : function(vWidget) {
      if (vWidget._isInGlobalElementQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalElementQueue, vWidget);
        delete vWidget._isInGlobalElementQueue;
      }
    },

    flushGlobalElementQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_ELEMENT;
      var vQueue = qx.ui.core.Widget._globalElementQueue, vLength, vWidget;
      while ((vLength = vQueue.length) > 0) {
        for (var i=0; i<vLength; i++) {
          vWidget = vQueue[i];
          vWidget._createElementImpl();
          delete vWidget._isInGlobalElementQueue;
        }
        vQueue.splice(0, vLength);
      }
      qx.ui.core.Widget._globalElementQueue = [];
    },

    addToGlobalStateQueue : function(vWidget) {
      if (!vWidget._isInGlobalStateQueue && vWidget._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_STATE );
        }
        qx.ui.core.Widget._globalStateQueue.push(vWidget);
        vWidget._isInGlobalStateQueue = true;
      }
    },

    removeFromGlobalStateQueue : function(vWidget) {
      if (vWidget._isInGlobalStateQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalStateQueue, vWidget);
        delete vWidget._isInGlobalStateQueue;
      }
    },

    flushGlobalStateQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_STATE;
      var Widget = qx.ui.core.Widget;
      // the queue may change while doing the flush so we work on a copy of
      // the queue and loop while the queue has any entries.
      while(Widget._globalStateQueue.length > 0) {
        var queue = qx.lang.Array.copy(Widget._globalStateQueue);
        Widget._globalStateQueue = [];
        for (var i=0, l=queue.length; i<l; i++) {
          var widget = queue[i];
          if (widget._isInGlobalStateQueue) {
            widget._renderAppearance();
            delete widget._isInGlobalStateQueue;
          }
        }
      }
    },

    addToGlobalJobQueue : function(vWidget) {
      if (!vWidget._isInGlobalJobQueue && vWidget._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_JOB );
        }
        qx.ui.core.Widget._globalJobQueue.push(vWidget);
        vWidget._isInGlobalJobQueue = true;
      }
    },

    removeFromGlobalJobQueue : function(vWidget) {
      if (vWidget._isInGlobalJobQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalJobQueue, vWidget);
        delete vWidget._isInGlobalJobQueue;
      }
    },

    flushGlobalJobQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_JOB;
      var vQueue = qx.ui.core.Widget._globalJobQueue, vLength, vWidget;
      while ((vLength = vQueue.length) > 0) {
        for (var i=0; i<vLength; i++) {
          vWidget = vQueue[i];
          vWidget._flushJobQueue(vWidget._jobQueue);
          delete vWidget._isInGlobalJobQueue;
        }
        vQueue.splice(0, vLength);
      }
      qx.ui.core.Widget._globalJobQueue = [];
    },

    addToGlobalLayoutQueue : function(vParent) {
      if (!vParent._isInGlobalLayoutQueue && vParent._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_LAYOUT );
        }
        qx.ui.core.Widget._globalLayoutQueue.push(vParent);
        vParent._isInGlobalLayoutQueue = true;
      }
    },

    removeFromGlobalLayoutQueue : function(vParent) {
      if (vParent._isInGlobalLayoutQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalLayoutQueue, vParent);
        delete vParent._isInGlobalLayoutQueue;
      }
    },

    flushGlobalLayoutQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_LAYOUT;
      var vQueue = qx.ui.core.Widget._globalLayoutQueue, vLength, vParent;
      while ((vLength = vQueue.length) > 0) {
        for (var i=0; i<vLength; i++) {
          vParent = vQueue[i];
          vParent._flushChildrenQueue();
          delete vParent._isInGlobalLayoutQueue;
        }
        vQueue.splice(0, vLength);
      }
      qx.ui.core.Widget._globalLayoutQueue = [];
    },

    addToGlobalDisplayQueue : function(vWidget) {
      if (!vWidget._isInGlobalDisplayQueue && vWidget._isDisplayable) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_DISPLAY );
        }
        var vParent = vWidget.getParent();
        if (vParent.isSeeable()) {
          var vKey = vParent.toHashCode();
          if (qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey]) {
            qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey].push(vWidget);
          } else {
            qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey] = [ vWidget ];
          }
        } else {
          qx.ui.core.Widget._fastGlobalDisplayQueue.push(vWidget);
        }
        vWidget._isInGlobalDisplayQueue = true;
      }
    },

    removeFromGlobalDisplayQueue : function(vWidget) {},

    flushGlobalDisplayQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_DISPLAY;
      var vKey, vLazyQueue, vWidget, vFragment;
      var vFastQueue = qx.ui.core.Widget._fastGlobalDisplayQueue;
      var vLazyQueues = qx.ui.core.Widget._lazyGlobalDisplayQueues;
      // Work on fast queue
      for (var i=0, l=vFastQueue.length; i<l; i++) {
        vWidget = vFastQueue[i];
        vWidget.getParent()._getTargetNode().appendChild(vWidget.getElement());
      }
      // Work on lazy queues: Other widgets
      for (vKey in vLazyQueues) {
        vLazyQueue = vLazyQueues[vKey];
        // Speed enhancement: Choose a fairly small arbitrary value for the number
        // of elements that should be added to the parent individually.  If more
        // than this number of elements is to be added to the parent, we'll create
        // a document fragment, add the elements to the document fragment, and
        // then add the whole fragment to the parent en mass (assuming that
        // creation of a document fragment is supported by the browser).
        if (document.createDocumentFragment && vLazyQueue.length >= 3) {
          // creating new document fragment
          vFragment = document.createDocumentFragment();
          // appending all widget elements to fragment
          for (var i=0, l=vLazyQueue.length; i<l; i++) {
            vWidget = vLazyQueue[i];
            vFragment.appendChild(vWidget.getElement());
          }
          // append all fragment data at once to
          // the already visible parent widget element
          vLazyQueue[0].getParent()._getTargetNode().appendChild(vFragment);
          for (var i=0, l=vLazyQueue.length; i<l; i++) {
            vWidget = vLazyQueue[i];
            vWidget._afterInsertDom();
          }
        } else {
          // appending all widget elements (including previously added children)
          // to the already visible parent widget element
          for (var i=0, l=vLazyQueue.length; i<l; i++)
          {
            vWidget = vLazyQueue[i];

            vWidget.getParent()._getTargetNode().appendChild(vWidget.getElement());
            vWidget._afterInsertDom();
          }
        }
      }
      // Only need to do this with the lazy queues
      // because through the recursion from qx.ui.core.Parent
      // all others get also informed.
      for (vKey in vLazyQueues) { vLazyQueue = vLazyQueues[vKey];
        for (var i=0, l=vLazyQueue.length; i<l; i++) {
          vWidget = vLazyQueue[i];
          if (vWidget.getVisibility()) {
            vWidget._afterAppear();
          }
          // Reset display queue flag
          delete vWidget._isInGlobalDisplayQueue;
        }
        delete vLazyQueues[vKey];
      }
      // reset queue if it is empty. This frees some browser memory
      if (qx.lang.Object.isEmpty(vLazyQueues)) {
        qx.ui.core.Widget._lazyGlobalDisplayQueues = {};
      }
      // Reset display queue flag for widgets in fastQueue
      for (var i=0, l=vFastQueue.length; i<l; i++) {
        delete vFastQueue[i]._isInGlobalDisplayQueue;
      }
      // Remove fast queue entries
      qx.ui.core.Widget._fastGlobalDisplayQueue = [];
    },

    addToGlobalDisposeQueue : function(vWidget) {
      if (!vWidget._isInGlobalDisposeQueue && !vWidget.isDisposed()) {
        if (qx.ui.core.Widget._autoFlushTimeout == null) {
          qx.ui.core.Widget._initAutoFlush( qx.ui.core.Widget._FLUSH_PHASE_DISPOSE );
        }
        qx.ui.core.Widget._globalDisposeQueue.push(vWidget);
        vWidget._isInGlobalDisposeQueue = true;
      }
    },

    removeFromGlobalDisposeQueue : function(vWidget) {
      if (vWidget._isInGlobalDisposeQueue) {
        qx.lang.Array.remove(qx.ui.core.Widget._globalDisposeQueue, vWidget);
        delete vWidget._isInGlobalDisposeQueue;
      }
    },

    flushGlobalDisposeQueue : function() {
      qx.ui.core.Widget._flushGlobalQueuesPhase 
        = qx.ui.core.Widget._FLUSH_PHASE_DISPOSE;
      var vQueue = qx.ui.core.Widget._globalDisposeQueue, vLength, vWidget;
      while ((vLength = vQueue.length) > 0) {
        for (var i=0; i<vLength; i++) {
          vWidget = vQueue[i];
          vWidget.dispose();
          delete vWidget._isInGlobalDisposeQueue;
        }
        vQueue.splice(0, vLength);
      }
      qx.ui.core.Widget._globalDisposeQueue = [];
    },

    /////////////////
    // Global Helpers
    
    getActiveSiblingHelperIgnore : function(vIgnoreClasses, vInstance) {
      for (var j=0; j<vIgnoreClasses.length; j++) {
        if (vInstance instanceof vIgnoreClasses[j]) {
          return true;
        }
      }
      return false;
    },

    getActiveSiblingHelper : function(vObject, vParent, vCalc, vIgnoreClasses, vMode) {
      if (!vIgnoreClasses) {
        vIgnoreClasses = [];
      }
      var vChilds = vParent.getChildren();
      var vPosition = vMode == null ? vChilds.indexOf(vObject) + vCalc : vMode === "first" ? 0 : vChilds.length - 1;
      var vInstance = vChilds[vPosition];
      while (vInstance && (!vInstance.getEnabled() || qx.ui.core.Widget.getActiveSiblingHelperIgnore(vIgnoreClasses, vInstance))) {
        vPosition += vCalc;
        vInstance = vChilds[vPosition];
        if (!vInstance) {
          return null;
        }
      }
      return vInstance;
    },

    ///////////////////////  
    //  APPLY LAYOUT STYLES

    TYPE_NULL : 0,
    TYPE_PIXEL : 1,
    TYPE_PERCENT : 2,
    TYPE_AUTO : 3,
    TYPE_FLEX : 4,

    __initApplyMethods : function(members) {
      var applyRuntime = "_renderRuntime";
      var resetRuntime = "_resetRuntime";
      var style = "this._style.";
      var cssValue = "=((v==null)?0:v)+'px'";
      var parameter = "v";
      var properties = [ "left", "right", "top", "bottom", "width", "height", "minWidth", "maxWidth", "minHeight", "maxHeight" ];
      var propertiesUpper = [ "Left", "Right", "Top", "Bottom", "Width", "Height", "MinWidth", "MaxWidth", "MinHeight", "MaxHeight" ];
      var applyMargin = applyRuntime + "Margin";
      var resetMargin = resetRuntime + "Margin";
      var styleMargin = style + "margin";
      for (var i=0; i<4; i++) {
        members[applyMargin + propertiesUpper[i]] = new Function(parameter, styleMargin + propertiesUpper[i] + cssValue);
        members[resetMargin + propertiesUpper[i]] = new Function(styleMargin + propertiesUpper[i] + "=''");
      }
      var applyPadding = applyRuntime + "Padding";
      var resetPadding = resetRuntime + "Padding";
      var stylePadding = style + "padding";
      // need to use setStyleProperty to keep compatibility with enhanced cross browser borders
      for (var i=0; i<4; i++) {
        members[applyPadding + propertiesUpper[i]] = new Function(parameter, "this.setStyleProperty('padding" + propertiesUpper[i] + "', ((v==null)?0:v)+'px')");
        members[resetPadding + propertiesUpper[i]] = new Function("this.removeStyleProperty('padding" + propertiesUpper[i] + "')");
      }

      /*
        Use optimized method for internet explorer
        to omit string concat and directly setup
        the new layout property.
         We could not use this to reset the value however.
        It seems that is just doesn't work this way. And the
        left/top always get priority. Tried: "", null, "auto".
        Nothing helps.
         Now I've switched back to the conventional method
        to reset the value. This seems to work again.
      */
      for (var i=0; i<properties.length; i++) {
        members[applyRuntime + propertiesUpper[i]] = new Function(parameter, style + properties[i] + cssValue);
        members[resetRuntime + propertiesUpper[i]] = new Function(style + properties[i] + "=''");
      }
    },

    ///////////////////////////////////////
    //  LAYOUT TYPE AND VALUE KEY PRE-CACHE

    layoutPropertyTypes : {},

    __initLayoutProperties : function(statics) {
      var a = [ "width", "height", "minWidth", "maxWidth", "minHeight", "maxHeight", "left", "right", "top", "bottom" ];
      for (var i=0, l=a.length, p, b, t; i<l; i++) {
        p = a[i];
        b = "_computed" + qx.lang.String.toFirstUp(p);
        t = b + "Type";
        statics.layoutPropertyTypes[p] = {
          dataType    : t,
          dataParsed  : b + "Parsed",
          dataValue   : b + "Value",
          typePixel   : t + "Pixel",
          typePercent : t + "Percent",
          typeAuto    : t + "Auto",
          typeFlex    : t + "Flex",
          typeNull    : t + "Null"
        };
      }
    },

    //////////////////
    //  SCROLL-BLOCKER
    
    disableScrolling : function(widget) {
      var el = widget._getTargetNode();
      if(el) {
        qx.html.Scroll.disableScrolling(el);
      } else {
        widget.addEventListener("appear", this._blockScrollingOnAppear, this);
      }
    },
    
    enableScrolling : function(widget) {
      var el = widget._getTargetNode();
      if(el) {
        qx.html.Scroll.enableScrolling(el);
      } else {
        widget.removeEventListener("appear", this._blockScrollingOnAppear, this);
      }     
    },
    
    _blockScrollingOnAppear : function(ev) {
      var widget = ev.getTarget();
      widget.removeEventListener("appear", this._blockScrollingOnAppear, this);
      this.disableScrolling(widget);
    }

  },

  properties : {
    enabled : {
      init : "inherit",
      check : "Boolean",
      inheritable : true,
      apply : "_applyEnabled",
      event : "changeEnabled"
    },

    /** The parent widget (the real object, no ID or something) */
    parent : {
      check : "qx.ui.core.Parent",
      nullable : true,
      event : "changeParent",
      apply : "_applyParent"
    },

    /** The element node (if the widget is created, otherwise null) */
    element : {
      check : "Element",
      nullable : true,
      apply : "_applyElement",
      event : "changeElement"
    },

    /**
     * Toggle the visibility of a widget.
     * Setting this property to false will hide the widget but will not remove
     * it from the layout flow, so other widgets will not be repositioned. This
     * is similar to the CSS property <code>visibility</code>.
     **/
    visibility : {
      check : "Boolean",
      init : true,
      apply : "_applyVisibility",
      event : "changeVisibility"
    },

    /**
     * Whether the widget should be displayed.
     * Use this property instead of visibility if the change of the visibility
     * should remove the widget from the layout flow and force a relayout of the
     * application. This is similar to the CSS property <code>display</code>.
     */
    display : {
      check : "Boolean",
      init : true,
      apply : "_applyDisplay",
      event : "changeDisplay"
    },

    /**
     * If you switch this to true, the widget doesn't handle
     *  events directly. It will redirect them to the parent
     *  widget.
     */
    anonymous : {
      check : "Boolean",
      init: false,
      event : "changeAnonymous"
    },

    /**
     * This is used by many layout managers to control the individual horizontal alignment of this widget inside this parent.
     *
     *  This should be used with caution since in some cases
     *  this might give unrespected results.
     */
    horizontalAlign : {
      check : [ "left", "center", "right" ],
      themeable : true,
      nullable : true
    },

    /**
     * This is used by many layout managers to control the individual vertical alignment of this widget inside this parent.
     *
     *  This should be used with caution since in some cases
     *  this might give unrespected results.
     */
    verticalAlign :  {
      check : [ "top", "middle", "bottom" ],
      themeable : true,
      nullable : true
    },

    /**
     * Should this widget be stretched on the x-axis if the layout handler will do this?
     *  Used by some layout handlers (qx.ui.layout.BoxLayout, ...).
     */
    allowStretchX : {
      check : "Boolean",
      init : true
    },

    /**
     * Should this widget be stretched on the y-axis if the layout handler will do this?
     *  Used by some layout handlers (qx.ui.layout.BoxLayout, ...).
     */
    allowStretchY : {
      check : "Boolean",
      init : true
    },

    /**
     * Mapping to native style property z-index.
     *
     *  This should be used with caution since in some cases
     *  this might give unrespected results.
     */
    zIndex : {
      check : "Number",
      apply : "_applyZIndex",
      event : "changeZIndex",
      themeable : true,
      nullable : true,
      init : null
    },

    /**
     * The backgroundColor style property of the rendered widget.
     */
    backgroundColor : {
      nullable : true,
      init : null,
      check : "Color",
      apply : "_applyBackgroundColor",
      event : "changeBackgroundColor",
      themeable : true
    },
    
    backgroundGradient : {
      check : "Array",
      nullable : true,
      init : null,
      apply : "_applyBackgroundGradient",
      event : "changeBackgroundGradient",      
      themeable : true
    },

    /**
     * Syntax for shadow:
     * [
     *    inset, //boolean, currently not supported
     *    offsetX, // positive or negative number
     *    offsetY, // positive or negative number
     *    blurRadius, // positive number or zero
     *    spread, // positive or negative number
     *    color, // string
     *    opacity, // number between 0 and 1
     * ]
     */
    shadow : {
      check : "Array",
      nullable : true,
      init : null,
      apply : "_applyShadow",
      event : "changeShadow",      
      themeable : true
    },

    /**
     * The color (textColor) style property of the rendered widget.
     */
    textColor : {
      nullable : true,
      init : "inherit",
      check : "Color",
      apply : "_applyTextColor",
      event : "changeTextColor",
      themeable : true,
      inheritable : true
    },

    /**
     * The border property describes how to paint the border on the widget.
     */
    border : {
      nullable : true,
      init : null,
      apply : "_applyBorder",
      event : "changeBorder",
      check : "Border",
      themeable : true
    },

    /** The font property describes how to paint the font on the widget. */
    font : {
      nullable : true,
      init : "inherit",
      apply : "_applyFont",
      check : "Font",
      event : "changeFont",
      themeable : true,
      inheritable : true
    },

    /**
     * Mapping to native style property opacity.
     *
     *  The uniform opacity setting to be applied across an entire object. Behaves like the new CSS-3 Property.
     *  Any values outside the range 0.0 (fully transparent) to 1.0 (fully opaque) will be clamped to this range.
     */
    opacity : {
      check : "Number",
      apply : "_applyOpacity",
      themeable : true,
      nullable : true,
      init : null
    },

    /**
     * Mapping to native style property cursor.
     *
     * The name of the cursor to show when the mouse pointer is over the widget.
     * This is any valid CSS2 cursor name defined by W3C.
     *
     * The following values are possible crossbrowser:
     * <ul><li>default</li>
     * <li>crosshair</li>
     * <li>pointer</li>
     * <li>move</li>
     * <li>n-resize</li>
     * <li>ne-resize</li>
     * <li>e-resize</li>
     * <li>se-resize</li>
     * <li>s-resize</li>
     * <li>sw-resize</li>
     * <li>w-resize</li>
     * <li>nw-resize</li>
     * <li>text</li>
     * <li>wait</li>
     * <li>help </li>
     * </ul>
     */
    cursor : {
      check : "String",
      apply : "_applyCursor",
      themeable : true,
      nullable : true,
      init : null
    },

    /**
     * Mapping to native style property background-image.
     *
     *  The URI of the image file to use as background image.
     */
    backgroundImage : {
      check : "String",
      nullable : true,
      apply : "_applyBackgroundImage",
      themeable : true
    },

    /**
     * Mapping to native style property background-repeat.
     */
    backgroundRepeat : {
      check : "String",
      nullable : true,
      apply : "_applyBackgroundRepeat",
      themeable : true
    },

    /**
     * Describes how to handle content that is too large to fit inside the widget.
     *
     * Overflow modes:
     * <table>
     * <tr><th>hidden</th><td>The content is clipped</td></tr>
     * <tr><th>auto</th><td>Scroll bars are shown as needed</td></tr>
     * <tr><th>scroll</th><td>Scroll bars are always shown. Even if there is enough room for the content inside the widget.</td></tr>
     * <tr><th>scrollX</th><td>Scroll bars for the X-Axis are always shown. Even if there is enough room for the content inside the widget.</td></tr>
     * <tr><th>scrollY</th><td>Scroll bars for the Y-Axis are always shown. Even if there is enough room for the content inside the widget.</td></tr>
     * </table>
     */
    overflow : {
      check : ["hidden", "auto", "scroll" ,"scrollX", "scrollY"],
      nullable : true,
      apply : "_applyOverflow",
      event : "changeOverflow",
      themeable : true,
      init : null
    },
    
    /**
     * Enables/disables overflow on the outer element for enhanced borders.
     */
    containerOverflow : {
      check : "Boolean",
      nullable : false,
      apply : "_applyContainerOverflow",
      themeable : true,
      init : true
    },

    /** Clipping of the widget (left) */
    clipLeft : {
      check : "Integer",
      apply : "_applyClip",
      themeable : true,
      nullable : true
    },

    /** Clipping of the widget (top) */
    clipTop : {
      check : "Integer",
      apply : "_applyClip",
      themeable : true,
      nullable : true
    },

    /** Clipping of the widget (width) */
    clipWidth : {
      check : "Integer",
      apply : "_applyClip",
      themeable : true,
      nullable : true
    },

    /** Clipping of the widget (height) */
    clipHeight : {
      check : "Integer",
      apply : "_applyClip",
      themeable : true,
      nullable : true
    },

    /**
     * Set this to a positive value makes the widget able to get the focus.
     * It even is reachable through the usage of the tab-key.
     *
     * Widgets with the same tabIndex are handled through there position
     * in the document.
     *
     * Setting the tabIndex to -1 deactivate focus handling for the widget.
     */
    tabIndex : {
      check : "Integer",
      nullable : true,
      init : null,
      apply : "_applyTabIndex",
      event : "changeTabIndex"
    },

    /** If the focus outline should be hidden. */
    hideFocus : {
      check : "Boolean",
      init : true,
      apply : "_applyHideFocus",
      themeable : true
    },

    /** Use DOM focussing (focus() and blur() methods of DOM nodes) */
    enableElementFocus : {
      check : "Boolean",
      init : true
    },

    /**
     * Handle focus state of this widget.
     *
     *  someWidget.setFocused(true) set the current focus to this widget.
     *  someWidget.setFocused(false) remove the current focus and leave it blank.
     *
     *  Normally you didn't need to set this directly.
     */
    focused : {
      check : "Boolean",
      init : false,
      apply : "_applyFocused",
      event : "changeFocused"
    },

    /** Toggle the possibility to select the element of this widget. */
    selectable : {
      check : "Boolean",
      init : null,
      nullable : true,
      apply : "_applySelectable"
    },

    /** Contains the tooltip object connected to the widget. */
    toolTip : {
      check : "qx.ui.popup.ToolTip",
      nullable : true
    },

    /** Contains the context menu object connected to the widget. (Need real implementation) */
    // RAP : remove check, allow other menu implementation
    contextMenu : {
      nullable : true
    },

    /** Capture all events and map them to this widget */
    capture : {
      check : "Boolean",
      init : false,
      apply : "_applyCapture",
      event : "changeCapture"
    },

    /** Contains the support drop types for drag and drop support */
    dropDataTypes : {
      nullable : true,
      dispose : true
    },

    /** A command called if the widget should be excecuted (a placeholder for buttons, ...) */
    command : {
      check : "qx.client.Command",
      nullable : true,
      apply : "_applyCommand"
    },

    /** Appearance of the widget */
    appearance : {
      check : "String",
      init : "widget",
      apply : "_applyAppearance",
      event : "changeAppearance"
    },

    /*
     * The method which this.supportsDrop() calls to determine whether the
     * widget supports a particular drop operation.
     *
     * This is a property so that a mixin can modify it.  Otherwise, the mixin
     * would have to override the supportsDrop() method, requiring the mixin
     * to be applied with patch() instead of include().  All normal mixins
     * should be able to be simply include()ed, and not have to be patch()ed.
     *
     * If this property is null, then the default supportsDrop() action
     * defined herein shall be used.
     */
    supportsDropMethod : {
      check : "Function",
      nullable : true,
      init : null
    },

    /** Margin of the widget (top) */
    marginTop : {
      check : "Number",
      apply : "_applyMarginTop",
      nullable : true,
      themeable : true
    },

    /** Margin of the widget (right) */
    marginRight : {
      check : "Number",
      apply : "_applyMarginRight",
      nullable : true,
      themeable : true
    },

    /** Margin of the widget (bottom) */
    marginBottom : {
      check : "Number",
      apply : "_applyMarginBottom",
      nullable : true,
      themeable : true
    },

    /** Margin of the widget (left) */
    marginLeft : {
      check : "Number",
      apply : "_applyMarginLeft",
      nullable : true,
      themeable : true
    },

    /** Padding of the widget (top) */
    paddingTop : {
      check : "Number",
      apply : "_applyPaddingTop",
      nullable : true,
      themeable : true
    },

    /** Padding of the widget (right) */
    paddingRight : {
      check : "Number",
      apply : "_applyPaddingRight",
      nullable : true,
      themeable : true
    },

    /** Padding of the widget (bottom) */
    paddingBottom : {
      check : "Number",
      apply : "_applyPaddingBottom",
      nullable : true,
      themeable : true
    },

    /** Padding of the widget (left) */
    paddingLeft : {
      check : "Number",
      apply : "_applyPaddingLeft",
      nullable : true,
      themeable : true
    },

    /**
     * The distance from the outer left border to the parent left area edge.
     *
     *  You could only set two of the three horizonal dimension properties (boxLeft, boxRight, boxWidth)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    left : {
      apply : "_applyLeft",
      event : "changeLeft",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The distance from the outer right border to the parent right area edge.
     *
     *  You could only set two of the three horizonal dimension properties (boxLeft, boxRight, boxWidth)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    right : {
      apply : "_applyRight",
      event : "changeRight",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The width of the box (including padding and border).
     *
     *  You could only set two of the three horizonal dimension properties (boxLeft, boxRight, boxWidth)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    width : {
      apply : "_applyWidth",
      event : "changeWidth",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The minimum width of the box (including padding and border).
     *
     *  Set this to omit the shrinking of the box width under this value.
     */
    minWidth : {
      apply : "_applyMinWidth",
      event : "changeMinWidth",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The maximum width of the box (including padding and border).
     *
     *  Set this to omit the expanding of the box width above this value.
     */
    maxWidth : {
      apply : "_applyMaxWidth",
      event : "changeMaxWidth",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The distance from the outer top border to the parent top area edge.
     *
     *  You could only set two of the three vertical dimension properties (boxTop, boxBottom, boxHeight)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    top : {
      apply : "_applyTop",
      event : "changeTop",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The distance from the outer bottom border to the parent bottom area edge.
     *
     *  You could only set two of the three vertical dimension properties (boxTop, boxBottom, boxHeight)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    bottom : {
      apply : "_applyBottom",
      event : "changeBottom",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The height of the box (including padding and border).
     *
     *  You could only set two of the three vertical dimension properties (boxTop, boxBottom, boxHeight)
     *  at the same time. This will be omitted during the setup of the new third value. To reset a value
     *  you didn't want anymore, set it to null.
     */
    height : {
      apply : "_applyHeight",
      event : "changeHeight",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The minimum height of the box (including padding and border).
     *
     *  Set this to omit the shrinking of the box height under this value.
     */
    minHeight : {
      apply : "_applyMinHeight",
      event : "changeMinHeight",
      nullable : true,
      themeable : true,
      init : null
    },

    /**
     * The maximum height of the box (including padding and border).
     *
     *  Set this to omit the expanding of the box height above this value.
     */
    maxHeight : {
      apply : "_applyMaxHeight",
      event : "changeMaxHeight",
      nullable : true,
      themeable : true,
      init : null
    },

    location : { group : [ "left", "top" ], themeable : true },
    dimension : { group : [ "width", "height" ], themeable : true },
    space : { group : [ "left", "width", "top", "height" ], themeable : true },

    edge : {
      group : [ "top", "right", "bottom", "left" ],
      themeable : true,
      mode  : "shorthand"
    },

    padding : {
      group : [ "paddingTop", "paddingRight", "paddingBottom", "paddingLeft" ],
      mode  : "shorthand",
      themeable : true
    },

    /**
     * The 'margin' property is a shorthand property for setting 'marginTop',
     * 'marginRight', 'marginBottom' and 'marginLeft' at the same time.
     *
     * If four length values are specified they apply to top, right, bottom and
     * left respectively. If there is only one value, it applies to all sides,
     * if there are two or three, the missing values are taken from the opposite side.
     */
    margin : {
      group : [ "marginTop", "marginRight", "marginBottom", "marginLeft" ],
      mode  : "shorthand",
      themeable : true
    },

    heights : { group : [ "minHeight", "height", "maxHeight" ], themeable : true },
    widths : { group : [ "minWidth", "width", "maxWidth" ], themeable : true },

    /**
     * The 'align' property is a shorthand property for setting 'horizontalAlign',
     * and 'verticalAlign' at the same time.
     */
    align : { group : [ "horizontalAlign", "verticalAlign" ], themeable : true },
    clipLocation : { group : [ "clipLeft", "clipTop" ] },
    clipDimension : { group : [ "clipWidth", "clipHeight" ] },
    clip : { group : [ "clipLeft", "clipTop", "clipWidth", "clipHeight" ] },

    innerWidth : {
      _cached      : true,
      defaultValue : null
    },

    innerHeight : {
      _cached      : true,
      defaultValue : null
    },

    boxWidth : {
      _cached      : true,
      defaultValue : null
    },

    boxHeight : {
      _cached      : true,
      defaultValue : null
    },

    outerWidth : {
      _cached      : true,
      defaultValue : null
    },

    outerHeight : {
      _cached      : true,
      defaultValue : null
    },

    frameWidth : {
      _cached           : true,
      defaultValue      : null,
      addToQueueRuntime : true
    },

    frameHeight : {
      _cached           : true,
      defaultValue      : null,
      addToQueueRuntime : true
    },

    preferredInnerWidth : {
      _cached           : true,
      defaultValue      : null,
      addToQueueRuntime : true
    },

    preferredInnerHeight : {
      _cached           : true,
      defaultValue      : null,
      addToQueueRuntime : true
    },

    preferredBoxWidth : {
      _cached      : true,
      defaultValue : null
    },

    preferredBoxHeight : {
      _cached      : true,
      defaultValue : null
    },

    hasPercentX : {
      _cached      : true,
      defaultValue : false
    },

    hasPercentY : {
      _cached      : true,
      defaultValue : false
    },

    hasAutoX : {
      _cached      : true,
      defaultValue : false
    },

    hasAutoY : {
      _cached      : true,
      defaultValue : false
    },

    hasFlexX : {
      _cached      : true,
      defaultValue : false
    },

    hasFlexY : {
      _cached      : true,
      defaultValue : false
    }
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members : {
    _computedLeftValue : null,
    _computedLeftParsed : null,
    _computedLeftType : null,
    _computedLeftTypeNull : true,
    _computedLeftTypePixel : false,
    _computedLeftTypePercent : false,
    _computedLeftTypeAuto : false,
    _computedLeftTypeFlex : false,
    _computedRightValue : null,
    _computedRightParsed : null,
    _computedRightType : null,
    _computedRightTypeNull : true,
    _computedRightTypePixel : false,
    _computedRightTypePercent : false,
    _computedRightTypeAuto : false,
    _computedRightTypeFlex : false,
    _computedTopValue : null,
    _computedTopParsed : null,
    _computedTopType : null,
    _computedTopTypeNull : true,
    _computedTopTypePixel : false,
    _computedTopTypePercent : false,
    _computedTopTypeAuto : false,
    _computedTopTypeFlex : false,
    _computedBottomValue : null,
    _computedBottomParsed : null,
    _computedBottomType : null,
    _computedBottomTypeNull : true,
    _computedBottomTypePixel : false,
    _computedBottomTypePercent : false,
    _computedBottomTypeAuto : false,
    _computedBottomTypeFlex : false,
    _computedWidthValue : null,
    _computedWidthParsed : null,
    _computedWidthType : null,
    _computedWidthTypeNull : true,
    _computedWidthTypePixel : false,
    _computedWidthTypePercent : false,
    _computedWidthTypeAuto : false,
    _computedWidthTypeFlex : false,
    _computedMinWidthValue : null,
    _computedMinWidthParsed : null,
    _computedMinWidthType : null,
    _computedMinWidthTypeNull : true,
    _computedMinWidthTypePixel : false,
    _computedMinWidthTypePercent : false,
    _computedMinWidthTypeAuto : false,
    _computedMinWidthTypeFlex : false,
    _computedMaxWidthValue : null,
    _computedMaxWidthParsed : null,
    _computedMaxWidthType : null,
    _computedMaxWidthTypeNull : true,
    _computedMaxWidthTypePixel : false,
    _computedMaxWidthTypePercent : false,
    _computedMaxWidthTypeAuto : false,
    _computedMaxWidthTypeFlex : false,
    _computedHeightValue : null,
    _computedHeightParsed : null,
    _computedHeightType : null,
    _computedHeightTypeNull : true,
    _computedHeightTypePixel : false,
    _computedHeightTypePercent : false,
    _computedHeightTypeAuto : false,
    _computedHeightTypeFlex : false,
    _computedMinHeightValue : null,
    _computedMinHeightParsed : null,
    _computedMinHeightType : null,
    _computedMinHeightTypeNull : true,
    _computedMinHeightTypePixel : false,
    _computedMinHeightTypePercent : false,
    _computedMinHeightTypeAuto : false,
    _computedMinHeightTypeFlex : false,
    _computedMaxHeightValue : null,
    _computedMaxHeightParsed : null,
    _computedMaxHeightType : null,
    _computedMaxHeightTypeNull : true,
    _computedMaxHeightTypePixel : false,
    _computedMaxHeightTypePercent : false,
    _computedMaxHeightTypeAuto : false,
    _computedMaxHeightTypeFlex : false,
    
    ///////////////
    // apply layout

    _applyLeft : function(value, old) {
      this._unitDetectionPixelPercent("left", value);
      this.addToQueue("left");
    },

    _applyRight : function(value, old) {
      this._unitDetectionPixelPercent("right", value);
      this.addToQueue("right");
    },

    _applyTop : function(value, old) {
      this._unitDetectionPixelPercent("top", value);
      this.addToQueue("top");
    },

    _applyBottom : function(value, old) {
      this._unitDetectionPixelPercent("bottom", value);
      this.addToQueue("bottom");
    },

    _applyWidth : function(value, old) {
      this._unitDetectionPixelPercentAutoFlex("width", value);
      this.addToQueue("width");
    },

    _applyMinWidth : function(value, old) {
      this._unitDetectionPixelPercentAuto("minWidth", value);
      this.addToQueue("minWidth");
    },

    _applyMaxWidth : function(value, old) {
      this._unitDetectionPixelPercentAuto("maxWidth", value);
      this.addToQueue("maxWidth");
    },

    _applyHeight : function(value, old) {
      this._unitDetectionPixelPercentAutoFlex("height", value);
      this.addToQueue("height");
    },

    _applyMinHeight : function(value, old) {
      this._unitDetectionPixelPercentAuto("minHeight", value);
      this.addToQueue("minHeight");
    },

    _applyMaxHeight : function(value, old) {
      this._unitDetectionPixelPercentAuto("maxHeight", value);
      this.addToQueue("maxHeight");
    },

    ////////////////////
    // CHILDREN HANDLING

    // NOTE: Implemented in Parent.js
    getChildren : qx.lang.Function.returnNull,
    getChildrenLength : qx.lang.Function.returnZero,
    hasChildren : qx.lang.Function.returnFalse,
    isEmpty : qx.lang.Function.returnTrue,
    indexOf : qx.lang.Function.returnNegativeIndex,
    contains : qx.lang.Function.returnFalse,
    getVisibleChildren : qx.lang.Function.returnNull,
    getVisibleChildrenLength : qx.lang.Function.returnZero,
    hasVisibleChildren : qx.lang.Function.returnFalse,
    isVisibleEmpty : qx.lang.Function.returnTrue,

    /////////////////
    //  CORE MODIFIER

    _hasParent : false,
    _isDisplayable : false,

    isDisplayable : function() {
      return this._isDisplayable;
    },

    _checkParent : function(value, old) {
      if (this.contains(value)) {
        throw new Error("Could not insert myself into a child " + value + "!");
      }

      return value;
    },

    _applyParent : function(value, old) {
      if (old) {
        var vOldIndex = old.getChildren().indexOf(this);
        // Reset cached dimension and location values
        this._computedWidthValue = this._computedMinWidthValue = this._computedMaxWidthValue = this._computedLeftValue = this._computedRightValue = null;
        this._computedHeightValue = this._computedMinHeightValue = this._computedMaxHeightValue = this._computedTopValue = this._computedBottomValue = null;
        this._cachedBoxWidth = this._cachedInnerWidth = this._cachedOuterWidth = null;
        this._cachedBoxHeight = this._cachedInnerHeight = this._cachedOuterHeight = null;
        // Finally remove from children array
        qx.lang.Array.removeAt(old.getChildren(), vOldIndex);
        // Invalidate visible children cache
        old._invalidateVisibleChildren();
        // Remove child from old parent's children queue
        old._removeChildFromChildrenQueue(this);
        // The layouter adds some layout jobs
        old.getLayoutImpl().updateChildrenOnRemoveChild(this, vOldIndex);
        // Inform job queue
        old.addToJobQueue("removeChild");
        // Invalidate inner preferred dimensions
        old._invalidatePreferredInnerDimensions();
        // Store old parent (needed later by _handleDisplayable)
        this._oldParent = old;
      }
      if (value) {
        this._hasParent = true;
        if (typeof this._insertIndex == "number") {
          qx.lang.Array.insertAt(value.getChildren(), this, this._insertIndex);
          delete this._insertIndex;
        } else {
          value.getChildren().push(this);
        }
      } else {
        this._hasParent = false;
      }
      qx.core.Property.refresh(this);
      return this._handleDisplayable("parent");
    },

    _applyDisplay : function(value, old) {
      return this._handleDisplayable("display");
    },

    //////////////////////
    // DISPLAYBLE HANDLING

    _handleDisplayable : function( vHint ) {
      var vDisplayable = this._computeDisplayable();
      if(    this._isDisplayable == vDisplayable 
          && !( vDisplayable && vHint == "parent" ) ) 
      {
        return true;
      }
      this._isDisplayable = vDisplayable;
      var vParent = this.getParent();
      if( vParent ) {
        vParent._invalidateVisibleChildren();
        vParent._invalidatePreferredInnerDimensions();
      }
      // Remove old parent's elements from DOM and delete old parent
      if( vHint && this._oldParent && this._oldParent._initialLayoutDone ) {
        var elem = this.getElement();
        if( elem ) {
          if( this.getVisibility() ) {
            this._beforeDisappear();
          }
          try {
            this._oldParent._getTargetNode().removeChild( elem );
          } catch( ex ) {
            // ignore exception
          }
          this._afterRemoveDom();
          if ( this.getVisibility() ) {
            this._afterDisappear();
          }
        }
        delete this._oldParent;
      } // old parent end
      if( vDisplayable ) {
        // The layouter added some layout jobs
        if( vParent._initialLayoutDone ) {
          var index = vParent.getChildren().indexOf( this );
          vParent.getLayoutImpl().updateChildrenOnAddChild( this, index );
          vParent.addToJobQueue( "addChild" );
        }
        // Add to parents children queue
        // (indirectly with a new layout request)
        this.addToLayoutChanges( "initial" );
        // Add to custom queues
        this.addToCustomQueues( vHint );
        if( this.getVisibility() ) {
          this._beforeAppear();
        }
        if( !this._isCreated ) {
          qx.ui.core.Widget.addToGlobalElementQueue(this);
        }
        qx.ui.core.Widget.addToGlobalStateQueue( this );
        if(!qx.lang.Object.isEmpty(this._jobQueue ) ) {
          qx.ui.core.Widget.addToGlobalJobQueue( this );
        }
        if( !qx.lang.Object.isEmpty( this._childrenQueue ) ) {
          qx.ui.core.Widget.addToGlobalLayoutQueue( this );
        }
        // displayable end
      } else {
        qx.ui.core.Widget.removeFromGlobalElementQueue( this );
        qx.ui.core.Widget.removeFromGlobalStateQueue( this );
        qx.ui.core.Widget.removeFromGlobalJobQueue( this );
        qx.ui.core.Widget.removeFromGlobalLayoutQueue( this );
        this.removeFromCustomQueues( vHint );
        // only remove when itself want to be removed
        // through a property change - not a parent signal
        if( vParent && vHint ) {
          if( this.getVisibility() ) {
            this._beforeDisappear();
          }
          if( vParent._initialLayoutDone && this._initialLayoutDone ) {
            var index = vParent.getChildren().indexOf( this );
            vParent.getLayoutImpl().updateChildrenOnRemoveChild( this, index );
            vParent.addToJobQueue( "removeChild" );
            var parentNode = this.getElement().parentNode;
            if( parentNode ){
              parentNode.removeChild( this.getElement() )
            }
            this._afterRemoveDom();
          }
          vParent._removeChildFromChildrenQueue( this );
          if( this.getVisibility() ) {
            this._afterDisappear();
          }
        }
      } // not displayable end
      this._handleDisplayableCustom( vDisplayable, vParent, vHint );
      return true;
    },

    addToCustomQueues : qx.lang.Function.returnTrue,
    removeFromCustomQueues : qx.lang.Function.returnTrue,
    _handleDisplayableCustom : qx.lang.Function.returnTrue,

    _computeDisplayable : function() {
      return this.getDisplay() && this.getParent() && this.getParent()._isDisplayable ? true : false;
    },

    _beforeAppear : function() {
      this.createDispatchEvent("beforeAppear");
    },

    _afterAppear : function() {
      this._isSeeable = true;
      this.createDispatchEvent("appear");
    },

    _beforeDisappear : function() {
      // Remove any hover/pressed styles
      this.removeState("over");

      if (qx.Class.isDefined("qx.ui.form.Button"))
      {
        this.removeState("pressed");
        this.removeState("abandoned");
      }

      this.createDispatchEvent("beforeDisappear");
    },

    _afterDisappear : function() {
      this._isSeeable = false;
      this.createDispatchEvent("disappear");
    },

    _isSeeable : false,
    _isInDom : false,

    /**
     * If the widget is currently seeable which means that it:
     *
     * * has a also seeable parent
     * * visibility is true
     * * display is true
     *
     * @type member
     * @return {boolean} TODOC
     */
    isSeeable : function() {
      return this._isSeeable;
    },
    
    isInDom : function() {
      return this._isInDom;
    },

    isAppearRelevant : function() {
      return this.getVisibility() && this._isDisplayable;
    },

    _afterInsertDom : function() {
      this._isInDom = true;
      this.createDispatchEvent( "insertDom" );
    },

    _afterRemoveDom : function() {
      this._isInDom = true;
    },

    //////////////////////
    // VISIBILITY HANDLING

    _applyVisibility : function(value, old) {
      if (value)
      {
        if ( this._isDisplayable && this._isCreated ) {
          this._beforeAppear();
        }

        this.removeStyleProperty("display");

        if ( this._isDisplayable && this._isCreated ) {
          this._afterAppear();
        }
      }
      else
      {
        if ( this._isDisplayable && this._isCreated ) {
          this._beforeDisappear();
        }

        this.setStyleProperty("display", "none");

        if ( this._isDisplayable && this._isCreated ) {
          this._afterDisappear();
        }
      }
    },

    show : function() {
      this.setVisibility(true);
      this.setDisplay(true);
    },

    hide : function() {
      this.setVisibility(false);
    },

    destroy : function () {
      this.setParent(null);
      qx.ui.core.Widget.addToGlobalDisposeQueue(this);
    },
    
    ///////////////////////
    // DOM ELEMENT HANDLING

    _isCreated : false,
    _element : null,
    _targetNode : null,
    _style : null,
    _innerStyle : null,

    _getTargetNode : function() {
      return this._targetNode || this._element;
    },

    addToDocument : function() {
      qx.ui.core.ClientDocument.getInstance().add(this);
    },

    /**
     * Check if the element is already available.
     */
    isCreated : function() {
      return this._isCreated;
    },

    _createElementImpl : function() {
      this.setElement(this.getTopLevelWidget().getDocumentElement().createElement("div"));
    },

    _applyElement : function(value, old) {
      this._isCreated = value != null;
      if (old) {
        // reset reference to widget instance
        old.qx_Widget = null;
      }
      if (value) {
        // add reference to widget instance
        value.qx_Widget = this;
        // make absolute
        value.style.position = "absolute";
        // link element and style reference
        this._element = value;
        this._style = value.style;
        this._applyStyleProperties(value);
        this._applyHtmlProperties(value);
        this._applyHtmlAttributes(value);
        this._applyElementData(value);
        // send out create event
        this.createDispatchEvent("create");
        // add created instances to state queue
        this.addToStateQueue();
      } else {
        this._element = this._style = null;
      }
    },

    ////////////
    // JOB QUEUE

    addToJobQueue : function(p){
      if (this._hasParent) {
        qx.ui.core.Widget.addToGlobalJobQueue(this);
      }
      if (!this._jobQueue) {
        this._jobQueue = {};
      }
      this._jobQueue[p] = true;
      return true;
    },

    _flushJobQueue : function(q) {
      // 1. Pre checks
      try {
        var vQueue = this._jobQueue;
        var vParent = this.getParent();

        if (!vParent || qx.lang.Object.isEmpty(vQueue)) {
          return;
        }

        var vLayoutImpl = this instanceof qx.ui.core.Parent ? this.getLayoutImpl() : null;

        if (vLayoutImpl) {
          vLayoutImpl.updateSelfOnJobQueueFlush(vQueue);
        }
      } catch(ex) {
        throw new Error( "Flushing job queue (prechecks#1) failed " + ex );
      }
      // 2. Recompute dimensions
      try {
        var vFlushParentJobQueue = false;
        var vRecomputeOuterWidth = vQueue.marginLeft || vQueue.marginRight;
        var vRecomputeOuterHeight = vQueue.marginTop || vQueue.marginBottom;
        var vRecomputeInnerWidth = vQueue.frameWidth;
        var vRecomputeInnerHeight = vQueue.frameHeight;
        var vRecomputeParentPreferredInnerWidth = (vQueue.frameWidth || vQueue.preferredInnerWidth) && this._recomputePreferredBoxWidth();
        var vRecomputeParentPreferredInnerHeight = (vQueue.frameHeight || vQueue.preferredInnerHeight) && this._recomputePreferredBoxHeight();
        if (vRecomputeParentPreferredInnerWidth) {
          var vPref = this.getPreferredBoxWidth();
          if (this._computedWidthTypeAuto) {
            this._computedWidthValue = vPref;
            vQueue.width = true;
          }
          if (this._computedMinWidthTypeAuto) {
            this._computedMinWidthValue = vPref;
            vQueue.minWidth = true;
          }

          if (this._computedMaxWidthTypeAuto) {
            this._computedMaxWidthValue = vPref;
            vQueue.maxWidth = true;
          }
        }
        if (vRecomputeParentPreferredInnerHeight) {
          var vPref = this.getPreferredBoxHeight();
          if (this._computedHeightTypeAuto) {
            this._computedHeightValue = vPref;
            vQueue.height = true;
          }
          if (this._computedMinHeightTypeAuto) {
            this._computedMinHeightValue = vPref;
            vQueue.minHeight = true;
          }
          if (this._computedMaxHeightTypeAuto) {
            this._computedMaxHeightValue = vPref;
            vQueue.maxHeight = true;
          }
        }
        if ((vQueue.width || vQueue.minWidth || vQueue.maxWidth || vQueue.left || vQueue.right) && this._recomputeBoxWidth()) {
          vRecomputeOuterWidth = vRecomputeInnerWidth = true;
        }
        if ((vQueue.height || vQueue.minHeight || vQueue.maxHeight || vQueue.top || vQueue.bottom) && this._recomputeBoxHeight()) {
          vRecomputeOuterHeight = vRecomputeInnerHeight = true;
        }
      } catch(ex) {
        throw new Error( "Flushing job queue (recompute#2) failed " + ex );
      }
      // 3. Signals to parent widgets
      try {
        if ((vRecomputeOuterWidth && this._recomputeOuterWidth()) || vRecomputeParentPreferredInnerWidth) {
          vParent._invalidatePreferredInnerWidth();
          vParent.getLayoutImpl().updateSelfOnChildOuterWidthChange(this);
          vFlushParentJobQueue = true;
        }
        if ((vRecomputeOuterHeight && this._recomputeOuterHeight()) || vRecomputeParentPreferredInnerHeight) {
          vParent._invalidatePreferredInnerHeight();
          vParent.getLayoutImpl().updateSelfOnChildOuterHeightChange(this);
          vFlushParentJobQueue = true;
        }
        if (vFlushParentJobQueue) {
          vParent._flushJobQueue();
        }
      } catch(ex) {
        throw new Error( "Flushing job queue (parentsignals#3) failed " + ex );
      }
      //  4. Add layout jobs
      try {
        // add to layout queue
        vParent._addChildToChildrenQueue(this);
        // convert jobs to layout jobs
        for (var i in vQueue) {
          this._layoutChanges[i] = true;
        }
      } catch(ex) {
        throw new Error( "Flushing job queue (addjobs#4) failed " + ex );
      }
      // 5. Signals to children
      try {
        // inform children about padding change
        if (this instanceof qx.ui.core.Parent && (vQueue.paddingLeft || vQueue.paddingRight || vQueue.paddingTop || vQueue.paddingBottom)) {
          var ch = this.getChildren(), chl = ch.length;
          if (vQueue.paddingLeft) {
            for (var i=0; i<chl; i++) {
              ch[i].addToLayoutChanges("parentPaddingLeft");
            }
          }
          if (vQueue.paddingRight) {
            for (var i=0; i<chl; i++) {
              ch[i].addToLayoutChanges("parentPaddingRight");
            }
          } 
          if (vQueue.paddingTop) {
            for (var i=0; i<chl; i++) {
              ch[i].addToLayoutChanges("parentPaddingTop");
            }
          } 
          if (vQueue.paddingBottom) {
            for (var i=0; i<chl; i++) {
              ch[i].addToLayoutChanges("parentPaddingBottom");
            }
          }
        } 
        if (vRecomputeInnerWidth) { 
          this._recomputeInnerWidth();
        } 
        if (vRecomputeInnerHeight) {
          this._recomputeInnerHeight();
        } 
        if (this._initialLayoutDone) {
          if (vLayoutImpl) {
            vLayoutImpl.updateChildrenOnJobQueueFlush(vQueue);
          }
        }
      } catch(ex) {
        throw new Error( "Flushing job queue (childrensignals#5) failed " + ex );
      }
      // 5. Cleanup
      delete this._jobQueue;
    },

    ////////////////////////////////////////////
    // METHODS TO GIVE THE LAYOUTERS INFORMATION

    _isWidthEssential : qx.lang.Function.returnTrue,
    _isHeightEssential : qx.lang.Function.returnTrue,

    _computeBoxWidthFallback : function() {
      return 0;
    },

    _computeBoxHeightFallback : function() {
      return 0;
    },

    _computeBoxWidth : function() {
      var vLayoutImpl = this.getParent().getLayoutImpl();
      return Math.max(0, qx.lang.Number.limit(vLayoutImpl.computeChildBoxWidth(this), this.getMinWidthValue(), this.getMaxWidthValue()));
    },

    _computeBoxHeight : function() {
      var vLayoutImpl = this.getParent().getLayoutImpl();
      return Math.max(0, qx.lang.Number.limit(vLayoutImpl.computeChildBoxHeight(this), this.getMinHeightValue(), this.getMaxHeightValue()));
    },

    _computeOuterWidth : function() {
      return Math.max(0, (this.getMarginLeft() + this.getBoxWidth() + this.getMarginRight()));
    },

    _computeOuterHeight : function() {
      return Math.max(0, (this.getMarginTop() + this.getBoxHeight() + this.getMarginBottom()));
    },

    _computeInnerWidth : function() {
      return Math.max(0, this.getBoxWidth() - this.getFrameWidth());
    },

    _computeInnerHeight : function() {
      return Math.max(0, this.getBoxHeight() - this.getFrameHeight());
    },

    getNeededWidth : function() {
      var vLayoutImpl = this.getParent().getLayoutImpl();
      return Math.max(0, vLayoutImpl.computeChildNeededWidth(this));
    },

    getNeededHeight : function() {
      var vLayoutImpl = this.getParent().getLayoutImpl();
      return Math.max(0, vLayoutImpl.computeChildNeededHeight(this));
    },

    /////////////////////////
    //  RECOMPUTE FLEX VALUES

    _recomputeFlexX : function() {
      if (!this.getHasFlexX()) {
        return false;
      }
      if (this._computedWidthTypeFlex) {
        this._computedWidthValue = null;
        this.addToLayoutChanges("width");
      }
      return true;
    },

    _recomputeFlexY : function() {
      if (!this.getHasFlexY()) {
        return false;
      }
      if (this._computedHeightTypeFlex) {
        this._computedHeightValue = null;
        this.addToLayoutChanges("height");
      }
      return true;
    },

    /////////////////////
    // RECOMPUTE PERCENTS

    _recomputePercentX : function() {
      if (!this.getHasPercentX()) {
        return false;
      }
      if (this._computedWidthTypePercent) {
        this._computedWidthValue = null;
        this.addToLayoutChanges("width");
      }
      if (this._computedMinWidthTypePercent) {
        this._computedMinWidthValue = null;
        this.addToLayoutChanges("minWidth");
      }
      if (this._computedMaxWidthTypePercent) {
        this._computedMaxWidthValue = null;
        this.addToLayoutChanges("maxWidth");
      }
      if (this._computedLeftTypePercent) {
        this._computedLeftValue = null;
        this.addToLayoutChanges("left");
      }
      if (this._computedRightTypePercent) {
        this._computedRightValue = null;
        this.addToLayoutChanges("right");
      }
      return true;
    },

    _recomputePercentY : function() {
      if (!this.getHasPercentY()) {
        return false;
      }
      if (this._computedHeightTypePercent) {
        this._computedHeightValue = null;
        this.addToLayoutChanges("height");
      }
      if (this._computedMinHeightTypePercent) {
        this._computedMinHeightValue = null;
        this.addToLayoutChanges("minHeight");
      }
      if (this._computedMaxHeightTypePercent) {
        this._computedMaxHeightValue = null;
        this.addToLayoutChanges("maxHeight");
      }
      if (this._computedTopTypePercent) {
        this._computedTopValue = null;
        this.addToLayoutChanges("top");
      }
      if (this._computedBottomTypePercent) {
        this._computedBottomValue = null;
        this.addToLayoutChanges("bottom");
      }
      return true;
    },

    ///////////////////
    // RECOMPUTE RANGES

    _recomputeRangeX : qx.core.Variant.select("qx.client", {
      "mshtml|opera|webkit" : function() {
        if (this._computedLeftTypeNull || this._computedRightTypeNull) {
          return false;
        }
        this.addToLayoutChanges("width");
        return true;
      },
      "default" : function() {
        return !(this._computedLeftTypeNull || this._computedRightTypeNull);
      }
    } ),

    _recomputeRangeY : qx.core.Variant.select("qx.client", {
      "mshtml|opera|webkit" : function() {
        if (this._computedTopTypeNull || this._computedBottomTypeNull) {
          return false;
        }
        this.addToLayoutChanges("height");
        return true;
      },
      "default" : function() {
        return !(this._computedTopTypeNull || this._computedBottomTypeNull);
      }
    } ),

    ///////////////////////
    // RECOMPUTE STRETCHING

    _recomputeStretchingX : qx.core.Variant.select("qx.client", {
      "mshtml|opera|webkit" : function() {
        if (this.getAllowStretchX() && this._computedWidthTypeNull) {
          this._computedWidthValue = null;
          this.addToLayoutChanges("width");
          return true;
        }
        return false;
      },
      "default" : function() {
        if (this.getAllowStretchX() && this._computedWidthTypeNull) {
          return true;
        }
        return false;
      }
    } ),

    _recomputeStretchingY : qx.core.Variant.select("qx.client", {
      "mshtml|opera|webkit" : function() {
        if (this.getAllowStretchY() && this._computedHeightTypeNull) {
          this._computedHeightValue = null;
          this.addToLayoutChanges("height");
          return true;
        }
        return false;
      },
      "default" : function() {
        if (this.getAllowStretchY() && this._computedHeightTypeNull) {
          return true;
        }
        return false;
      }
    } ),

    /////////////////////////////////////////////////
    //  INTELLIGENT GETTERS FOR STANDALONE DIMENSIONS

    _computeValuePixel : function(v) {
      return Math.round(v);
    },

    _computeValuePixelLimit : function(v) {
      return Math.max(0, this._computeValuePixel(v));
    },

    _computeValuePercentX : function(v) {
      return Math.round(this.getParent().getInnerWidthForChild(this) * v * 0.01);
    },

    _computeValuePercentXLimit : function(v) {
      return Math.max(0, this._computeValuePercentX(v));
    },

    _computeValuePercentY : function(v) {
      return Math.round(this.getParent().getInnerHeightForChild(this) * v * 0.01);
    },

    _computeValuePercentYLimit : function(v) {
      return Math.max(0, this._computeValuePercentY(v));
    },

    getWidthValue : function() {
      if (this._computedWidthValue != null) {
        return this._computedWidthValue;
      }
      switch(this._computedWidthType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedWidthValue = this._computeValuePixelLimit(this._computedWidthParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedWidthValue = this._computeValuePercentXLimit(this._computedWidthParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedWidthValue = this.getPreferredBoxWidth();
        case qx.ui.core.Widget.TYPE_FLEX:
          if (this.getParent().getLayoutImpl().computeChildrenFlexWidth === undefined) {
            throw new Error("Widget " + this + ": having horizontal flex size (width=" + this.getWidth() + ") but parent layout " + this.getParent() + " does not support it");
          }
          this.getParent().getLayoutImpl().computeChildrenFlexWidth();
          return this._computedWidthValue = this._computedWidthFlexValue;
      }
      return null;
    },

    getMinWidthValue : function() {
      if (this._computedMinWidthValue != null) {
        return this._computedMinWidthValue;
      }
      switch(this._computedMinWidthType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedWidthValue = this._computeValuePixelLimit(this._computedMinWidthParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedWidthValue = this._computeValuePercentXLimit(this._computedMinWidthParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedMinWidthValue = this.getPreferredBoxWidth();
      }
      return null;
    },

    getMaxWidthValue : function() {
      if (this._computedMaxWidthValue != null) {
        return this._computedMaxWidthValue;
      }
      switch(this._computedMaxWidthType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedWidthValue = this._computeValuePixelLimit(this._computedMaxWidthParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedWidthValue = this._computeValuePercentXLimit(this._computedMaxWidthParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedMaxWidthValue = this.getPreferredBoxWidth();
      }
      return null;
    },

    getLeftValue : function() {
      if (this._computedLeftValue != null) {
        return this._computedLeftValue;
      }
      switch(this._computedLeftType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedLeftValue = this._computeValuePixel(this._computedLeftParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedLeftValue = this._computeValuePercentX(this._computedLeftParsed);
      }
      return null;
    },

    getRightValue : function() {
      if (this._computedRightValue != null) {
        return this._computedRightValue;
      }
      switch(this._computedRightType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedRightValue = this._computeValuePixel(this._computedRightParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedRightValue = this._computeValuePercentX(this._computedRightParsed);
      }
      return null;
    },

    getHeightValue : function() {
      if (this._computedHeightValue != null) {
        return this._computedHeightValue;
      }
      switch(this._computedHeightType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedHeightValue = this._computeValuePixelLimit(this._computedHeightParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedHeightValue = this._computeValuePercentYLimit(this._computedHeightParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedHeightValue = this.getPreferredBoxHeight();
        case qx.ui.core.Widget.TYPE_FLEX:
          if (this.getParent().getLayoutImpl().computeChildrenFlexHeight === undefined) {
            throw new Error("Widget " + this + ": having vertical flex size (height=" + this.getHeight() + ") but parent layout " + this.getParent() + " does not support it");
          }
          this.getParent().getLayoutImpl().computeChildrenFlexHeight();
          return this._computedHeightValue = this._computedHeightFlexValue;
      }
      return null;
    },

    getMinHeightValue : function() {
      if (this._computedMinHeightValue != null) {
        return this._computedMinHeightValue;
      }
      switch(this._computedMinHeightType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedMinHeightValue = this._computeValuePixelLimit(this._computedMinHeightParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedMinHeightValue = this._computeValuePercentYLimit(this._computedMinHeightParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedMinHeightValue = this.getPreferredBoxHeight();
      }
      return null;
    },

    getMaxHeightValue : function() {
      if (this._computedMaxHeightValue != null) {
        return this._computedMaxHeightValue;
      }
      switch(this._computedMaxHeightType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedMaxHeightValue = this._computeValuePixelLimit(this._computedMaxHeightParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedMaxHeightValue = this._computeValuePercentYLimit(this._computedMaxHeightParsed);
        case qx.ui.core.Widget.TYPE_AUTO:
          return this._computedMaxHeightValue = this.getPreferredBoxHeight();
      }
      return null;
    },

    getTopValue : function() {
      if (this._computedTopValue != null) {
        return this._computedTopValue;
      }
      switch(this._computedTopType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedTopValue = this._computeValuePixel(this._computedTopParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedTopValue = this._computeValuePercentY(this._computedTopParsed);
      }
      return null;
    },

    getBottomValue : function() {
      if (this._computedBottomValue != null) {
        return this._computedBottomValue;
      }
      switch(this._computedBottomType) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          return this._computedBottomValue = this._computeValuePixel(this._computedBottomParsed);
        case qx.ui.core.Widget.TYPE_PERCENT:
          return this._computedBottomValue = this._computeValuePercentY(this._computedBottomParsed);
      }
      return null;
    },

    _computeFrameWidth : function() {
      var fw = this._cachedBorderLeft + this.getPaddingLeft() + this.getPaddingRight() + this._cachedBorderRight;
      switch(this.getOverflow()) {
        case "scroll":
        case "scrollY":
          fw += qx.ui.core.Widget.SCROLLBAR_SIZE;
          break;
        case "auto":
          // This seems to be really hard to implement
          break;
      }
      return fw;
    },

    _computeFrameHeight : function() {
      var fh = this._cachedBorderTop + this.getPaddingTop() + this.getPaddingBottom() + this._cachedBorderBottom;
      switch(this.getOverflow()) {
        case "scroll":
        case "scrollX":
          fh += qx.ui.core.Widget.SCROLLBAR_SIZE;
          break;
        case "auto":
          // This seems to be really hard to implement
          break;
      }
      return fh;
    },

    _invalidateFrameDimensions : function() {
      this._invalidateFrameWidth();
      this._invalidateFrameHeight();
    },

    _invalidatePreferredInnerDimensions : function() {
      this._invalidatePreferredInnerWidth();
      this._invalidatePreferredInnerHeight();
    },

    _computePreferredBoxWidth : function() {
      try {
        return Math.max(0, this.getPreferredInnerWidth() + this.getFrameWidth());
      } catch(ex) {
        throw new Error( "_computePreferredBoxWidth failed " + ex);
      }
    },

    _computePreferredBoxHeight : function() {
      try {
        return Math.max(0, this.getPreferredInnerHeight() + this.getFrameHeight());
      } catch(ex) {
        throw new Error( "_computePreferredBoxHeight failed " + ex );
      }
    },

    ///////////////
    // LAYOUT QUEUE

    _initialLayoutDone : false,

    addToLayoutChanges : function(p) {
      if (this._isDisplayable) {
        this.getParent()._addChildToChildrenQueue(this);
      }
      return this._layoutChanges[p] = true;
    },

    addToQueue : function(p) {
      this._initialLayoutDone ? this.addToJobQueue(p) : this.addToLayoutChanges(p);
    },

    addToQueueRuntime : function(p) {
      return !this._initialLayoutDone || this.addToJobQueue(p);
    },

    /////////////////////
    // LAYOUTER INTERNALS

    _computeHasPercentX : function() {
      return (this._computedLeftTypePercent || this._computedWidthTypePercent || this._computedMinWidthTypePercent || this._computedMaxWidthTypePercent || this._computedRightTypePercent);
    },

    _computeHasPercentY : function() {
      return (this._computedTopTypePercent || this._computedHeightTypePercent || this._computedMinHeightTypePercent || this._computedMaxHeightTypePercent || this._computedBottomTypePercent);
    },

    _computeHasAutoX : function() {
      return (this._computedWidthTypeAuto || this._computedMinWidthTypeAuto || this._computedMaxWidthTypeAuto);
    },

    _computeHasAutoY : function() {
      return (this._computedHeightTypeAuto || this._computedMinHeightTypeAuto || this._computedMaxHeightTypeAuto);
    },

    _computeHasFlexX : function() {
      return this._computedWidthTypeFlex;
    },

    _computeHasFlexY : function() {
      return this._computedHeightTypeFlex;
    },

    _evalUnitsPixelPercentAutoFlex : function(value) {
      switch(value) {
        case "auto":
          return qx.ui.core.Widget.TYPE_AUTO;
        case Infinity:
        case -Infinity:
          return qx.ui.core.Widget.TYPE_NULL;
      }
      switch(typeof value) {
        case "number":
          return isNaN(value) ? qx.ui.core.Widget.TYPE_NULL : qx.ui.core.Widget.TYPE_PIXEL;

        case "string":
          return value.indexOf("%") != -1 ? qx.ui.core.Widget.TYPE_PERCENT : value.indexOf("*") != -1 ? qx.ui.core.Widget.TYPE_FLEX : qx.ui.core.Widget.TYPE_NULL;
      }
      return qx.ui.core.Widget.TYPE_NULL;
    },

    _evalUnitsPixelPercentAuto : function(value) {
      switch(value) {
        case "auto":
          return qx.ui.core.Widget.TYPE_AUTO;
        case Infinity:
        case -Infinity:
          return qx.ui.core.Widget.TYPE_NULL;
      }
      switch(typeof value) {
        case "number":
          return isNaN(value) ? qx.ui.core.Widget.TYPE_NULL : qx.ui.core.Widget.TYPE_PIXEL;
        case "string":
          return value.indexOf("%") != -1 ? qx.ui.core.Widget.TYPE_PERCENT : qx.ui.core.Widget.TYPE_NULL;
      }
      return qx.ui.core.Widget.TYPE_NULL;
    },

    _evalUnitsPixelPercent : function(value) {
      switch(value) {
        case Infinity:
        case -Infinity:
          return qx.ui.core.Widget.TYPE_NULL;
      }
      switch(typeof value) {
        case "number":
          return isNaN(value) ? qx.ui.core.Widget.TYPE_NULL : qx.ui.core.Widget.TYPE_PIXEL;
        case "string":
          return value.indexOf("%") != -1 ? qx.ui.core.Widget.TYPE_PERCENT : qx.ui.core.Widget.TYPE_NULL;
      }
      return qx.ui.core.Widget.TYPE_NULL;
    },

    ///////////////////////////////////
    // UNIT DETECTION FOR LAYOUT SYSTEM

    _unitDetectionPixelPercentAutoFlex : function(name, value) {
      var r = qx.ui.core.Widget.layoutPropertyTypes[name];
      var s = r.dataType;
      var p = r.dataParsed;
      var v = r.dataValue;
      var s1 = r.typePixel;
      var s2 = r.typePercent;
      var s3 = r.typeAuto;
      var s4 = r.typeFlex;
      var s5 = r.typeNull;
      var wasPercent = this[s2];
      var wasAuto = this[s3];
      var wasFlex = this[s4];
      switch(this[s] = this._evalUnitsPixelPercentAutoFlex(value)) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          this[s1] = true;
          this[s2] = this[s3] = this[s4] = this[s5] = false;
          this[p] = this[v] = Math.round(value);
        break;
        case qx.ui.core.Widget.TYPE_PERCENT:
          this[s2] = true;
          this[s1] = this[s3] = this[s4] = this[s5] = false;
          this[p] = parseFloat(value);
          this[v] = null;
        break;
        case qx.ui.core.Widget.TYPE_AUTO:
          this[s3] = true;
          this[s1] = this[s2] = this[s4] = this[s5] = false;
          this[p] = this[v] = null;
        break;
        case qx.ui.core.Widget.TYPE_FLEX:
          this[s4] = true;
          this[s1] = this[s2] = this[s3] = this[s5] = false;
          this[p] = parseFloat(value);
          this[v] = null;
        break;
        default:
          this[s5] = true;
          this[s1] = this[s2] = this[s3] = this[s4] = false;
          this[p] = this[v] = null;
        break;
      }
      if (wasPercent != this[s2]) {
        switch(name) {
          case "minWidth":
          case "maxWidth":
          case "width":
          case "left":
          case "right":
            this._invalidateHasPercentX();
          break;
          case "maxHeight":
          case "minHeight":
          case "height":
          case "top":
          case "bottom":
            this._invalidateHasPercentY();
          break;
        }
      }
      // No ELSE because you can also switch from percent to auto
      if (wasAuto != this[s3]) {
        switch(name) {
          case "minWidth":
          case "maxWidth":
          case "width":
            this._invalidateHasAutoX();
          break;
          case "minHeight":
          case "maxHeight":
          case "height":
            this._invalidateHasAutoY();
          break;
        }
      }

      // No ELSE because you can also switch from percent to auto
      if (wasFlex != this[s4]) {
        switch(name) {
          case "width":
            this._invalidateHasFlexX();
          break;
          case "height":
            this._invalidateHasFlexY();
          break;
        }
      }
    },

    _unitDetectionPixelPercentAuto : function(name, value) {
      var r = qx.ui.core.Widget.layoutPropertyTypes[name];
      var s = r.dataType;
      var p = r.dataParsed;
      var v = r.dataValue;
      var s1 = r.typePixel;
      var s2 = r.typePercent;
      var s3 = r.typeAuto;
      var s4 = r.typeNull;
      var wasPercent = this[s2];
      var wasAuto = this[s3];
      switch(this[s] = this._evalUnitsPixelPercentAuto(value)) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          this[s1] = true;
          this[s2] = this[s3] = this[s4] = false;
          this[p] = this[v] = Math.round(value);
        break;
        case qx.ui.core.Widget.TYPE_PERCENT:
          this[s2] = true;
          this[s1] = this[s3] = this[s4] = false;
          this[p] = parseFloat(value);
          this[v] = null;
        break;
        case qx.ui.core.Widget.TYPE_AUTO:
          this[s3] = true;
          this[s1] = this[s2] = this[s4] = false;
          this[p] = this[v] = null;
        break;
        default:
          this[s4] = true;
          this[s1] = this[s2] = this[s3] = false;
          this[p] = this[v] = null;
          break;
      }
      if (wasPercent != this[s2]) {
        switch(name) {
          case "minWidth":
          case "maxWidth":
          case "width":
          case "left":
          case "right":
            this._invalidateHasPercentX();
          break;
          case "minHeight":
          case "maxHeight":
          case "height":
          case "top":
          case "bottom":
            this._invalidateHasPercentY();
            break;
        }
      }
      // No ELSE because you can also switch from percent to auto
      if (wasAuto != this[s3]) {
        switch(name) {
          case "minWidth":
          case "maxWidth":
          case "width":
            this._invalidateHasAutoX();
          break;
          case "minHeight":
          case "maxHeight":
          case "height":
            this._invalidateHasAutoY();
          break;
        }
      }
    },

    _unitDetectionPixelPercent : function(name, value) {
      var r = qx.ui.core.Widget.layoutPropertyTypes[name];
      var s = r.dataType;
      var p = r.dataParsed;
      var v = r.dataValue;
      var s1 = r.typePixel;
      var s2 = r.typePercent;
      var s3 = r.typeNull;
      var wasPercent = this[s2];
      switch(this[s] = this._evalUnitsPixelPercent(value)) {
        case qx.ui.core.Widget.TYPE_PIXEL:
          this[s1] = true;
          this[s2] = this[s3] = false;
          this[p] = this[v] = Math.round(value);
        break;
        case qx.ui.core.Widget.TYPE_PERCENT:
          this[s2] = true;
          this[s1] = this[s3] = false;
          this[p] = parseFloat(value);
          this[v] = null;
        break;
        default:
          this[s3] = true;
          this[s1] = this[s2] = false;
          this[p] = this[v] = null;
        break;
      }
      if (wasPercent != this[s2]) {
        switch(name) {
          case "minWidth":
          case "maxWidth":
          case "width":
          case "left":
          case "right":
            this._invalidateHasPercentX();
          break;
          case "minHeight":
          case "maxHeight":
          case "height":
          case "top":
          case "bottom":
            this._invalidateHasPercentY();
          break;
        }
      }
    },

    /////////////////////
    // CHILDREN MANAGMENT

    /**
     * The widget which is at the top level,
     *  which contains all others (normally a
     *  instance of qx.ui.core.ClientDocument).
     */
    getTopLevelWidget : function() {
      return this._hasParent ? this.getParent().getTopLevelWidget() : null;
    },

    moveSelfToBegin : function() {
      this.getParent().addAtBegin(this);
    },

    moveSelfToEnd : function() {
      this.getParent().addAtEnd(this);
    },

    getPreviousSibling : function() {
      var p = this.getParent();
      if (p == null) {
        return null;
      }
      var cs = p.getChildren();
      return cs[cs.indexOf(this) - 1];
    },

    getNextSibling : function() {
      var p = this.getParent();
      if (p == null) {
        return null;
      }
      var cs = p.getChildren();
      return cs[cs.indexOf(this) + 1];
    },

    getPreviousVisibleSibling : function() {
      if (!this._hasParent) {
        return null;
      }
      var vChildren = this.getParent().getVisibleChildren();
      return vChildren[vChildren.indexOf(this) - 1];
    },

    getNextVisibleSibling : function() {
      if (!this._hasParent) {
        return null;
      }
      var vChildren = this.getParent().getVisibleChildren();
      return vChildren[vChildren.indexOf(this) + 1];
    },

    getPreviousActiveSibling : function(vIgnoreClasses) {
      var vPrev = qx.ui.core.Widget.getActiveSiblingHelper(this, this.getParent(), -1, vIgnoreClasses, null);
      return vPrev ? vPrev : this.getParent().getLastActiveChild();
    },

    getNextActiveSibling : function(vIgnoreClasses) {
      var vNext = qx.ui.core.Widget.getActiveSiblingHelper(this, this.getParent(), 1, vIgnoreClasses, null);
      return vNext ? vNext : this.getParent().getFirstActiveChild();
    },

    isFirstChild : function() {
      return this._hasParent && this.getParent().getFirstChild() == this;
    },

    isLastChild : function() {
      return this._hasParent && this.getParent().getLastChild() == this;
    },

    isFirstVisibleChild : function() {
      return this._hasParent && this.getParent().getFirstVisibleChild() == this;
    },

    isLastVisibleChild : function() {
      return this._hasParent && this.getParent().getLastVisibleChild() == this;
    },

    /////////////////
    // STATE HANDLING

    hasState : function(vState) {
      return this.__states && this.__states[vState] ? true : false;
    },

    addState : function(vState) {
      if (!this.__states) {
        this.__states = {};
      }
      if (!this.__states[vState]) {
        this.__states[vState] = true;
        if (this._hasParent) {
          qx.ui.core.Widget.addToGlobalStateQueue(this);
        }
      }
    },

    removeState : function(vState) {
      if (this.__states && this.__states[vState]) {
        delete this.__states[vState];
        if (this._hasParent) {
          qx.ui.core.Widget.addToGlobalStateQueue(this);
        }
      }
    },

    /////////////////////
    // APPEARANCE SUPPORT

    /**
     * Style multiple properties at once by using a property list
     *
     * @type member
     * @param data {Map} a map of property values. The key is the name of the property.
     * @return {Object} this instance.
     * @throws an error if the incoming data field is not a map.
     */
    _styleFromMap : function(data) {
      var styler = qx.core.Property.$$method.style;
      var unstyler = qx.core.Property.$$method.unstyle;
      var value;
      if (qx.core.Variant.isSet("qx.debug", "on")) {
        for (var prop in data) {
          if (!this[styler[prop]]) {
            throw new Error(this.classname + ' has no themeable property "' + prop + '"');
          }
        }
      }
      for (var prop in data) {
        value = data[prop];
        value === "undefined" ? this[unstyler[prop]]() : this[styler[prop]](value);
      }
    },

    _unstyleFromArray : function(data) {
      var unstyler = qx.core.Property.$$method.unstyle;
      if (qx.core.Variant.isSet("qx.debug", "on")) {
        for (var i=0, l=data.length; i<l; i++) {
          if (!this[unstyler[data[i]]]) {
            throw new Error(this.classname + ' has no themeable property "' + data[i] + '"');
          }
        }
      }
      for (var i=0, l=data.length; i<l; i++) {
        this[unstyler[data[i]]]();
      }
    },

    _renderAppearance : function() {
      if (!this.__states) {
        this.__states = {};
      }
      // HACK: Is there a cleaner way to implement this?
      // Maybe not use the appearance for this, but a simple property and event handler combination?
      this._applyStateStyleFocus(this.__states);
      var vAppearance = this.getAppearance();
      if (vAppearance) {
          var r = qx.theme.manager.Appearance.getInstance().styleFrom(vAppearance, this.__states);
          if (r) {
            this._styleFromMap(r);
          }
      }
    },

    _resetAppearanceThemeWrapper : function(vNewAppearanceTheme, vOldAppearanceTheme) {
      var vAppearance = this.getAppearance();
      if (vAppearance) {
        var vAppearanceManager = qx.theme.manager.Appearance.getInstance();
        var vOldAppearanceProperties = vAppearanceManager.styleFromTheme(vOldAppearanceTheme, vAppearance, this.__states);
        var vNewAppearanceProperties = vAppearanceManager.styleFromTheme(vNewAppearanceTheme, vAppearance, this.__states);
        var vUnstyleList = [];
        for (var prop in vOldAppearanceProperties) {
          if (vNewAppearanceProperties[prop] === undefined) {
            vUnstyleList.push(prop);
          }
        }
        this._unstyleFromArray(vUnstyleList);
        this._styleFromMap(vNewAppearanceProperties);
      }
    },

    _applyStateStyleFocus : qx.core.Variant.select("qx.client", {
      "mshtml" : function(vStates) {},
      "default" : function(vStates) {
        if (vStates.focused) {
          if (!qx.event.handler.FocusHandler.mouseFocus && !this.getHideFocus()) {
            this.setStyleProperty("outline", "1px dotted");
          }
        } else {
          this.setStyleProperty("outline", "none");
        }
      }
    } ),

    addToStateQueue : function() {
      qx.ui.core.Widget.addToGlobalStateQueue(this);
    },

    recursiveAddToStateQueue : function() {
      this.addToStateQueue();
    },

    _applyAppearance : function(value, old) {
      if (!this.__states) {
        this.__states = {};
      }
      var vAppearanceManager = qx.theme.manager.Appearance.getInstance();
      if (value) {
        var vNewAppearanceProperties = vAppearanceManager.styleFrom(value, this.__states) || {};
      }
      if (old) {
        var vOldAppearanceProperties = vAppearanceManager.styleFrom(old, this.__states) || {};
        var vUnstyleList = [];
        for (var prop in vOldAppearanceProperties) {
          if (!vNewAppearanceProperties || !(prop in vNewAppearanceProperties)) {
            vUnstyleList.push(prop);
          }
        }
      }
      if (vUnstyleList) {
        this._unstyleFromArray(vUnstyleList);
      }
      if (vNewAppearanceProperties) {
        this._styleFromMap(vNewAppearanceProperties);
      }
    },

    _recursiveAppearanceThemeUpdate : function(vNewAppearanceTheme, vOldAppearanceTheme) {
      try {
        this._resetAppearanceThemeWrapper(vNewAppearanceTheme, vOldAppearanceTheme);
      } catch(ex) {
        throw new Error( "Failed to update appearance theme " + ex );
      }
    },

    ///////////////
    // ELEMENT DATA

    _applyElementData : function(elem) {},

    //////////////////
    // HTML PROPERTIES

    setHtmlProperty : function(propName, value) {
      if (!this._htmlProperties) {
        this._htmlProperties = {};
      }
      this._htmlProperties[propName] = value;
      if (this._isCreated && this.getElement()[propName] != value) {
        this.getElement()[propName] = value;
      }
      return true;
    },

    removeHtmlProperty : qx.core.Variant.select("qx.client", {
      "mshtml" : function(propName) {
        if (!this._htmlProperties) {
          return;
        }
        delete this._htmlProperties[propName];
        if (this._isCreated) {
          this.getElement().removeAttribute(propName);
        }
        return true;
      },
      "default" : function(propName) {
        if (!this._htmlProperties) {
          return;
        }
        delete this._htmlProperties[propName];
        if (this._isCreated) {
          this.getElement().removeAttribute(propName);
          delete this.getElement()[propName];
        }
        return true;
      }
    } ),

    getHtmlProperty : function(propName) {
      if (!this._htmlProperties) {
        return "";
      }
      return this._htmlProperties[propName] || "";
    },

    _applyHtmlProperties : function(elem) {
      var vProperties = this._htmlProperties;
      if (vProperties) {
        var propName;
        for (propName in vProperties) {
          elem[propName] = vProperties[propName];
        }
      }
    },

    //////////////////
    // HTML ATTRIBUTES

    setHtmlAttribute : function(propName, value) {
      if (!this._htmlAttributes) {
        this._htmlAttributes = {};
      }
      this._htmlAttributes[propName] = value;
      if (this._isCreated) {
        this.getElement().setAttribute(propName, value);
      }
      return true;
    },

    removeHtmlAttribute : function(propName) {
      if (!this._htmlAttributes) {
        return;
      }
      delete this._htmlAttributes[propName];
      if (this._isCreated) {
        this.getElement().removeAttribute(propName);
      }
      return true;
    },

    getHtmlAttribute : function(propName) {
      if (!this._htmlAttributes) {
        return "";
      }
      return this._htmlAttributes[propName] || "";
    },

    _applyHtmlAttributes : function(elem) {
      var vAttributes = this._htmlAttributes;
      if (vAttributes) {
        var propName;
        for (propName in vAttributes) {
          elem.setAttribute(propName, vAttributes[propName]);
        }
      }
    },

    ///////////////////
    // STYLE PROPERTIES

    getStyleProperty : function(propName) {
      if (!this._styleProperties) {
        return "";
      }
      return this._styleProperties[propName] || "";
    },

    //Some properties should always be applied on the outer element node:
    __outerElementStyleProperties : {
      cursor : true,
      zIndex : true,
      filter : true,
      display : true,
      visibility : true,
      outline : true
    },

    setStyleProperty : function(propName, value) {
      if (!this._styleProperties) {
        this._styleProperties = {};
      }
      this._styleProperties[propName] = value;
      if (this._isCreated) {
        var elem = this.__outerElementStyleProperties[propName] ? this.getElement() : this._getTargetNode();
        if (elem) {
          elem.style[propName] = (value == null) ? "" : value;
        }
      }
    },

    removeStyleProperty : function(propName) {
      if (!this._styleProperties) {
        return;
      }
      delete this._styleProperties[propName];
      if (this._isCreated) {
        var elem = this.__outerElementStyleProperties[propName] ? this.getElement() : this._getTargetNode();
        if (elem) {
          elem.style[propName] = "";
        }
      }
    },

    _applyStyleProperties : function(elem) {
      var vProperties = this._styleProperties;
      if (!vProperties) {
        return;
      }
      var propName;
      var vBaseElement = elem;
      var vTargetElement = this._getTargetNode();
      var elem;
      var value;
      for (propName in vProperties) {
        elem = this.__outerElementStyleProperties[propName] ? vBaseElement : vTargetElement;
        value = vProperties[propName];
        elem.style[propName] = (value == null) ? "" : value;
      }
    },
    
    /////////////////////////
    // ENABLE/DISABLE SUPPORT

    _applyEnabled : function(value, old) {
      if (value===false) {
        this.addState("disabled");
        // Also reset some states to be sure a pressed/hovered button gets reset
        this.removeState("over");
        if (qx.Class.isDefined("qx.ui.form.Button")) {
          this.removeState("abandoned");
          this.removeState("pressed");
        }
      } else {
        this.removeState("disabled");
      }
    },

    /////////////////
    // FOCUS HANDLING

    isFocusable : function() {
      return this.getEnabled() && this.isSeeable() && this.getTabIndex() != null;
    },

    isFocusRoot : function() {
      return false;
    },

    getFocusRoot : function() {
      if (this._hasParent) {
        return this.getParent().getFocusRoot();
      }
      return null;
    },

    getActiveChild : function() {
      var vRoot = this.getFocusRoot();
      if (vRoot) {
        return vRoot.getActiveChild();
      }
      return null;
    },

    _ontabfocus : qx.lang.Function.returnTrue,

    _applyFocused : function(value, old) {
      if (!this.isCreated()) {
        return;
      }
      var vFocusRoot = this.getFocusRoot();
      if (vFocusRoot) {
        // may be undefined if this widget has been removed
        if (value) {
          vFocusRoot.setFocusedChild(this);
          this._visualizeFocus();
        } else {
          if (vFocusRoot.getFocusedChild() == this) {
            vFocusRoot.setFocusedChild(null);
          }
          this._visualizeBlur();
        }
      }
    },

    _applyHideFocus : qx.core.Variant.select("qx.client", {
      "mshtml" : function(value, old) {
        this.setHtmlProperty("hideFocus", value);
      },
      // Need no implementation for others then mshtml, because
      // all these browsers support css outlines and do not
      // have an attribute "hideFocus" as IE.
      "default" : qx.lang.Function.returnTrue
    } ),

    _visualizeBlur : function() {
      // Force blur, even if mouseFocus is not active because we
      // need to be sure that the previous focus rect gets removed.
      // But this only needs to be done, if there is no new focused element.
      if (this.getEnableElementFocus() && (!this.getFocusRoot().getFocusedChild() || (this.getFocusRoot().getFocusedChild() && this.getFocusRoot().getFocusedChild().getEnableElementFocus()))) {
        try {
          this.getElement().blur();
        } catch(ex) {}
      }
      this.removeState("focused");
    },

    _visualizeFocus : function() {
      if (!qx.event.handler.FocusHandler.mouseFocus && this.getEnableElementFocus()) {
        try {
          this.getElement().focus();
        } catch(ex) {}
      }
      this.addState("focused");
    },

    focus : function() {
      delete qx.event.handler.FocusHandler.mouseFocus;
      this.setFocused(true);
    },

    blur : function() {
      delete qx.event.handler.FocusHandler.mouseFocus;
      this.setFocused(false);
    },

    ////////////////////
    // CAPTURING SUPPORT

    _applyCapture : function(value, old) {
      var vMgr = org.eclipse.rwt.EventHandler;
      if (old) {
        vMgr.setCaptureWidget(null);
      }
      if (value) {
        vMgr.setCaptureWidget(this);
      }
    },

    /////////////////
    // ZINDEX SUPPORT

    _applyZIndex : function(value, old) {
      if (value == null) {
        this.removeStyleProperty("zIndex");
      } else {
        this.setStyleProperty("zIndex", value);
      }
    },

    ////////////////////
    // TAB INDEX SUPPORT

    _applyTabIndex : qx.core.Variant.select("qx.client", {
      "mshtml" : function(value, old) {
        /*
        if (value < 0 || !this.getEnabled()) {
          this.setHtmlProperty("unselectable", "on");
        } else {
          this.removeHtmlProperty("unselectable");
        }
        */
        // Legacy tabIndex property
        this.setHtmlProperty("tabIndex", value < 0 ? -1 : 1);
      },
      // [if] Fix for bug:
      // 288348: The focus stays on the browser location bar after clicking some
      // of the widgets.
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=288348
      //"gecko" : function(value, old)
      //{
      //  // CSS 3 draft userFocus property
      //  this.setStyleProperty("MozUserFocus", (value < 0 ? "ignore" : "normal"));
      //},
      "default" : function(value, old) {
        // CSS 3 draft userFocus property
        this.setStyleProperty("userFocus", (value < 0 ? "ignore" : "normal"));
        // Legacy tabIndex property
        this.setHtmlProperty("tabIndex", value < 0 ? -1 : 1);
      }
    } ),

    /////////////////////
    // SELECTABLE SUPPORT

    _applySelectable : qx.core.Variant.select("qx.client", {
      // "unselectable" works locally and does not affect children
      // "user-select" is not inherited, but it does affect children
      // in the same way that display: none does, it limits it.
      // Normally "unselectable" must be applied recursively because
      // of this restriction. We use the "selectstart" event instead
      // and apply a preventDefault() on this event in the EventHandler
      // class. This event works at least in MSHTML and Webkit.
      // Opera has no support for any of these options yet. No "selectstart"
      // event no user-select property.
      "mshtml" : function(value, old) {
        // IMPORTANT
        // to ensure that widgets using the execCommand method work
        // properly it is necessary to keep these lines. Otherwise
        // the widget e.g. the HtmlArea will loose its selection of text
        // and the execCommand would target an empty selection at the
        // beginning of the editable document.

        // AGAIN: This makes more problems when enabled, because
        // it interrupts the normal focus flow. We definitely need
        // to find a solution which works without the unselectable
        // property. The problem is that other clients than
        // Firefox or Opera do not allow multiple selections.
        // Interesting read is the WHATWG text selection suggestion.
        // We are in an evaluating phase to find better solutions, but
        // this needs some time - and maybe can only be made available
        // when the browsers at least support a common subset here.
        /*
        if (value) {
          return this.removeHtmlProperty("unselectable");
        } else {
          return this.setHtmlProperty("unselectable", "on");
        }
        */
      },
      "gecko" : function(value, old) {
        if (value) {
          this.removeStyleProperty("MozUserSelect");
        } else {
          this.setStyleProperty("MozUserSelect", "none");
        }
      },
      "webkit" : function(value, old) {
        if (value) {
          this.removeStyleProperty("WebkitUserSelect");
          this.removeStyleProperty("KhtmlUserSelect");
        } else {
          this.setStyleProperty("WebkitUserSelect", "none");
          this.setStyleProperty("KhtmlUserSelect", "none");
        }
      },
      // Opera currently has no support to prohibit user selection
      "default" : function(value, old) {
        if (value) {
          return this.removeStyleProperty("userSelect");
        } else {
          this.setStyleProperty("userSelect", "none");
        }
      }
    } ),

    //////////////////
    // OPACITY SUPPORT

    /**
     * Sets the opacity for the widget. Any child widget inside the widget will also
     * become (semi-)transparent. The value should be a number between 0 and 1
     * inclusive, where 1 means totally opaque and 0 invisible.
     */
    _applyOpacity : function( value, old ) {
      org.eclipse.rwt.HtmlUtil.setOpacity( this, value );
    },

    /////////////////
    // CURSOR SUPPORT

    // TODO: maybe we could use cursor:url() for not suppoted cursors.
    __cursorMap : qx.core.Variant.select("qx.client", {
      "mshtml" : {
        "default" : "default",
        "wait" : "wait",
        "crosshair" : "crosshair",
        "help" : "help",
        "move" : "move",
        "text" : "text",
        "pointer" : "pointer",
        "e-resize" : "e-resize",
        "n-resize" : "n-resize",
        "w-resize" : "w-resize",
        "s-resize" : "s-resize",
        "ne-resize" : "ne-resize",
        "se-resize" : "se-resize",
        "nw-resize" : "nw-resize",
        "sw-resize" : "sw-resize",
        "col-resize" : "col-resize",
        "row-resize" : "row-resize",
        "progress" : "progress",
        "not-allowed" : "not-allowed",
        "no-drop" : "no-drop",
        "cursor" : "hand",
        "ew-resize" : "e-resize",
        "ns-resize" : "n-resize",
        "nesw-resize" : "ne-resize",
        "nwse-resize" : "nw-resize"
      },
      "opera" : {
        "default" : "default",
        "wait" : "wait",
        "crosshair" : "crosshair",
        "help" : "help",
        "move" : "move",
        "text" : "text",
        "pointer" : "pointer",
        "e-resize" : "e-resize",
        "n-resize" : "n-resize",
        "w-resize" : "w-resize",
        "s-resize" : "s-resize",
        "ne-resize" : "ne-resize",
        "se-resize" : "se-resize",
        "nw-resize" : "nw-resize",
        "sw-resize" : "sw-resize",
        "progress" : "progress",
        "not-allowed" : "not-allowed",
        "no-drop" : "no-drop",
        "col-resize" : "e-resize",
        "row-resize" : "n-resize",
        "ew-resize" : "e-resize",
        "ns-resize" : "n-resize",
        "nesw-resize" : "ne-resize",
        "nwse-resize" : "nw-resize"
      },
      "default" : {
        "default" : "default",
        "wait" : "wait",
        "crosshair" : "crosshair",
        "help" : "help",
        "move" : "move",
        "text" : "text",
        "pointer" : "pointer",
        "e-resize" : "e-resize",
        "n-resize" : "n-resize",
        "w-resize" : "w-resize",
        "s-resize" : "s-resize",
        "ne-resize" : "ne-resize",
        "se-resize" : "se-resize",
        "nw-resize" : "nw-resize",
        "sw-resize" : "sw-resize",
        "col-resize" : "col-resize",
        "row-resize" : "row-resize",
        "progress" : "progress",
        "not-allowed" : "not-allowed",
        "no-drop" : "no-drop"
      }
    } ),

    _applyCursor : function(value, old) {
      if (value) {
        var url = "url( " + qx.io.Alias.getInstance().resolve( value ) + " ), default";
        this.setStyleProperty("cursor", this.__cursorMap[value] || url);
      } else {
        this.removeStyleProperty("cursor");
      }
    },

    //////////////////
    // COMMAND SUPPORT

    // TODO [tb] : used only in listitem - remove
    _applyCommand : function(value, old) {
      // place holder
    },
    
    ///////////////////////////
    // BACKGROUND IMAGE SUPPORT

    _applyBackgroundImage : function(value, old) {
      var imageMgr = qx.io.image.Manager.getInstance();
      var aliasMgr = qx.io.Alias.getInstance();
      if (old) {
        imageMgr.hide(old);
      }
      if (value) {
        imageMgr.show(value);
      }
      aliasMgr.connect(this._styleBackgroundImage, this, value);
    },

    _styleBackgroundImage : function(value) {
      value ? this.setStyleProperty("backgroundImage", "url(" + value + ")") : this.removeStyleProperty("backgroundImage");
    },

    _applyBackgroundRepeat : function(value, old) {
      value ? this.setStyleProperty("backgroundRepeat", value) : this.removeStyleProperty("backgroundRepeat");
    },

    ///////////////////
    // CLIPPING SUPPORT

    _applyClip : function(value, old) {
      return this._compileClipString();
    },

    _compileClipString : function() {
      var vLeft = this.getClipLeft();
      var vTop = this.getClipTop();
      var vWidth = this.getClipWidth();
      var vHeight = this.getClipHeight();
      var vRight, vBottom;
      if (vLeft == null) {
        vRight = (vWidth == null ? "auto" : vWidth + "px");
        vLeft = "auto";
      } else {
        vRight = (vWidth == null ? "auto" : vLeft + vWidth + "px");
        vLeft = vLeft + "px";
      }
      if (vTop == null) {
        vBottom = (vHeight == null ? "auto" : vHeight + "px");
        vTop = "auto";
      } else {
        vBottom = (vHeight == null ? "auto" : vTop + vHeight + "px");
        vTop = vTop + "px";
      }
      return this.setStyleProperty("clip", ("rect(" + vTop + "," + vRight + "," + vBottom + "," + vLeft + ")"));
    },

    ///////////////////  
    // OVERFLOW SUPPORT

    _applyOverflow : qx.core.Variant.select("qx.client", {
      "default" : function(value, old) {
        // Mshtml and WebKit conform to CSS3 Spec. Eventually there will be multiple
        // browsers which support these new overflowX overflowY properties.
        var pv = value;
        var pn = "overflow";
        switch(value) {
          case "scrollX":
            pn = "overflowX";
            pv = "scroll";
          break;
          case "scrollY":
            pn = "overflowY";
            pv = "scroll";
          break;
        }
        // Clear up concurrenting rules
        var a = [ "overflow", "overflowX", "overflowY" ];
        for (var i=0; i<a.length; i++) {
          if (a[i] != pn) {
            this.removeStyleProperty(a[i]);
          }
        }
        switch(value) {
          case "scrollX":
            this.setStyleProperty("overflowY", "hidden");
          break;
          case "scrollY":
            this.setStyleProperty("overflowX", "hidden");
          break;
        }
        this._renderOverflow(pn, pv, value, old);
        this.addToQueue("overflow");
      },
      "gecko" : function(value, old) {
        var pv = value;
        var pn = "overflow";
        switch(pv) {
          case "hidden":
            pv = "-moz-scrollbars-none";
          break;
          case "scrollX":
            pv = "-moz-scrollbars-horizontal";
          break;
          case "scrollY":
            pv = "-moz-scrollbars-vertical";
          break;
        }
        this._renderOverflow(pn, pv, value, old);
        this.addToQueue("overflow");
      },
      "opera" : function(value, old) {
        // Opera/Khtml Mode...
        // hopefully somewhat of this is supported in the near future.
        // overflow-x and overflow-y are also not supported by Opera 9.0 Beta1
        // and also not if we switch to IE emulation mode
        var pv = value;
        var pn = "overflow";
        if( org.eclipse.rwt.Client.getVersion() < 9.8 ) {
	        switch( pv ) {
	          case "scrollX":
	          case "scrollY":
	            pv = "scroll";
           break;
	        }
        } else {
          switch( pv ) {
	          case "scrollX":
	            pn = "overflowX";
	            pv = "scroll";
            break;	
	          case "scrollY":
	            pn = "overflowY";
	            pv = "scroll";
	          break;
	        }
	        // Clear up concurrenting rules
	        var a = [ "overflow", "overflowX", "overflowY" ];	
	        for( var i = 0; i < a.length; i++ ) {
	          if( a[ i ] != pn ) {
	            this.removeStyleProperty( a[ i ] );
	          }
	        }	
	        switch( value ) {
	          case "scrollX":
	            this.setStyleProperty( "overflowY", "hidden" );
	            break;	
	          case "scrollY":
	            this.setStyleProperty( "overflowX", "hidden" );
	            break;
	        }
        }
        this._renderOverflow(pn, pv, value, old);
        this.addToQueue("overflow");
      }
    } ),

    _renderOverflow : function(pn, pv, value, old) {
      // Apply Style
      this.setStyleProperty(pn, pv || "");
      // Invalidate Frame
      this._invalidateFrameWidth();
      this._invalidateFrameHeight();
    },

    getOverflowX : function() {
      var vOverflow = this.getOverflow();
      return vOverflow == "scrollY" ? "hidden" : vOverflow;
    },

    getOverflowY : function() {
      var vOverflow = this.getOverflow();
      return vOverflow == "scrollX" ? "hidden" : vOverflow;
    },
    
    _applyContainerOverflow : function( value, oldValue ) {
      if( this._innerStyle ) {
        this._style.overflow = value ? "" : "hidden";
      }
    },

    /////////////////////////
    // FONT AND COLOR SUPPORT

    _applyBackgroundColor : function(value, old) {
      qx.theme.manager.Color.getInstance().connect(this._styleBackgroundColor, this, value);
    },

    _applyBackgroundGradient : function( value, oldValue ) {
      org.eclipse.rwt.HtmlUtil.setBackgroundGradient( this, value );
    },

    _applyShadow : function( value, oldValue ) {
      org.eclipse.rwt.HtmlUtil.setBoxShadow( this, value );      
    },

    _styleBackgroundColor : function(value) {
      value ? this.setStyleProperty("backgroundColor", value) : this.removeStyleProperty("backgroundColor");
    },

    _applyTextColor : function(value, old) {
      // place holder
    },

    _applyFont : function(value, old) {
      // place holder
    },

    /////////////////
    // BORDER SUPPORT

    _cachedBorderTop : 0,
    _cachedBorderRight : 0,
    _cachedBorderBottom : 0,
    _cachedBorderLeft : 0,
    _usesComplexBorder : false,
    _layoutTargetNode : false,

    _applyBorder : function( value, old ) {
      this._queueBorder( value );
    },

    _queueBorder : function( value ) {
      this.addToQueue( "border" );
      this.__reflowBorderX( value );
      this.__reflowBorderY( value );
      this.__borderObject = value;
    },

    /**
     * Invalidates the cached frame on y-axis when border changes occour
     */
    __reflowBorderX : function( value ) {
      var oldLeftWidth = this._cachedBorderLeft;
      var oldRightWidth = this._cachedBorderRight;
      this._cachedBorderLeft = value ? value.getWidthLeft() : 0;
      this._cachedBorderRight = value ? value.getWidthRight() : 0;
      if ((oldLeftWidth + oldRightWidth) != (this._cachedBorderLeft + this._cachedBorderRight)) {
        this._invalidateFrameWidth();
      }
    },

    /**
     * Invalidates the cached frame on y-axis when border changes occour
     */
    __reflowBorderY : function(value) {
      var oldTopWidth = this._cachedBorderTop;
      var oldBottomWidth = this._cachedBorderBottom;
      this._cachedBorderTop = value ? value.getWidthTop() : 0;
      this._cachedBorderBottom = value ? value.getWidthBottom() : 0;
      if ((oldTopWidth + oldBottomWidth) != (this._cachedBorderTop + this._cachedBorderBottom)) {
        this._invalidateFrameHeight();
      }
    },

    /**
     * Renders border object to widget.
     * Callback from layout queue
     */
    renderBorder : function( changes ) {
      var value = this.__borderObject;
      if( value ) {
        value.render( this );
      } else {
        org.eclipse.rwt.Border.reset( this );
      }
      // RAP: Fix for Bug 301709
      this._usesComplexBorder = this._computeUsesComplexBorder();
    },

    _computeUsesComplexBorder : function() {
      return this.__borderObject && this.__borderObject.getStyle() === "complex";
    },

    prepareEnhancedBorder : function() {
      this._targetNode = document.createElement( "div" );
      this._innerStyle = this._targetNode.style;
      this._layoutTargetNode = true;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        this.addToQueue( "width" );
        this.addToQueue( "height" );
      } else {
        this._innerStyle.width = "100%";
        this._innerStyle.height = "100%";
      }
      this._innerStyle.position = "absolute";
      for( var i in this._styleProperties ) {
        switch( i ) {
          case "zIndex":
          case "filter":
          case "opacity":
          case "MozOpacity":
          case "display":
          case "cursor":
          break;
          default:
            this._innerStyle[i] = this._styleProperties[i];
            this._style[i] = "";
        }
      }
      // [if] Fix for bug 279800: Some focused widgets look strange in webkit
      this._style.outline = "none";
      this._applyContainerOverflow( this.getContainerOverflow() );
      for( var i in this._htmlProperties ) {
        switch( i ) {
          case "unselectable":
            this._targetNode.unselectable = this._htmlProperties[ i ];
        }
      }
      while( this._element.firstChild ) {
        this._targetNode.appendChild( this._element.firstChild );
      }
      this._element.appendChild( this._targetNode );
      if( this.isInDom() ) {
        // TODO [tb] : check if this works for ProgressBar
        this._afterRemoveDom(); 
        this._afterInsertDom(); 
      }
    },

    //////////////////
    // PADDING SUPPORT

    _applyPaddingTop : function(value, old) {
      this.addToQueue("paddingTop");
      this._invalidateFrameHeight();
    },

    _applyPaddingRight : function(value, old) {
      this.addToQueue("paddingRight");
      this._invalidateFrameWidth();
    },

    _applyPaddingBottom : function(value, old) {
      this.addToQueue("paddingBottom");
      this._invalidateFrameHeight();
    },

    _applyPaddingLeft : function(value, old) {
      this.addToQueue("paddingLeft");
      this._invalidateFrameWidth();
    },

    /**
     * Renders padding to widget
     * Callback from layout queue
     */
    renderPadding : function(changes) {
      // empty
    },

    /////////////////
    // MARGIN SUPPORT

    _applyMarginLeft : function(value, old) {
      this.addToQueue("marginLeft");
    },

    _applyMarginRight : function(value, old) {
      this.addToQueue("marginRight");
    },

    _applyMarginTop : function(value, old) {
      this.addToQueue("marginTop");
    },

    _applyMarginBottom : function(value, old) {
      this.addToQueue("marginBottom");
    },

    //////////////////
    // COMMAND SUPPORT

    execute : function() {
      var cmd = this.getCommand();
      if (cmd) {
        cmd.execute(this);
      }
      this.createDispatchEvent( "execute" );
    },

    ///////////////////////////////
    // DOM: OFFSET & SCROLL SUPPORT

    _visualPropertyCheck : function() {
      if (!this.isCreated()) {
        throw new Error(this.classname + ": Element must be created previously!");
      }
    },

    setScrollLeft : function(nScrollLeft) {
      this._visualPropertyCheck();
      this._getTargetNode().scrollLeft = nScrollLeft;
    },

    setScrollTop : function(nScrollTop) {
      this._visualPropertyCheck();
      this._getTargetNode().scrollTop = nScrollTop;
    },

    getOffsetLeft : function() {
      this._visualPropertyCheck();
      return qx.html.Offset.getLeft(this.getElement());
    },

    getOffsetTop : function() {
      this._visualPropertyCheck();
      return qx.html.Offset.getTop(this.getElement());
    },

    getScrollLeft : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().scrollLeft;
    },

    getScrollTop : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().scrollTop;
    },

    getClientWidth : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().clientWidth;
    },

    getClientHeight : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().clientHeight;
    },

    getOffsetWidth : function() {
      this._visualPropertyCheck();
      return this.getElement().offsetWidth;
    },

    getOffsetHeight : function() {
      this._visualPropertyCheck();
      return this.getElement().offsetHeight;
    },

    getScrollWidth : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().scrollWidth;
    },

    getScrollHeight : function() {
      this._visualPropertyCheck();
      return this._getTargetNode().scrollHeight;
    },

    ////////////////////////////////
    // DOM: SCROLL INTO VIEW SUPPORT

    /**
     * Scroll the widget into the view.
     *
     * This function works on DOM level and needs the widget to be already rendered.
     * This is true for example in the "appear" event handler of a widget.
     *
     * @type member
     * @param alignTopLeft {Boolean} Set the alignment. "True" means top left align, "False" means bottom right.
     */
    scrollIntoView : function(alignTopLeft) {
      this.scrollIntoViewX(alignTopLeft);
      this.scrollIntoViewY(alignTopLeft);
    },

    scrollIntoViewX : function(alignLeft) {
      if (!this._isCreated || !this._isDisplayable) {
        return false;
      }
      return qx.html.ScrollIntoView.scrollX(this.getElement(), alignLeft);
    },

    scrollIntoViewY : function(alignTop) {
      if (!this._isCreated || !this._isDisplayable) {
        return false;
      }
      return qx.html.ScrollIntoView.scrollY(this.getElement(), alignTop);
    },

    ////////////////////////
    // DRAG AND DROP SUPPORT

    supportsDrop : function(dragCache) {
      // Is there a user-supplied supportsDropMethod?
      var supportsDropMethod = this.getSupportsDropMethod();
      if (supportsDropMethod !== null) {
        // Yup.  Let it determine whether a drop is allowed.
        return supportsDropMethod.call(this, dragCache);
      }
      // Default behavior is to allow drop only if not dropping onto self
      return (this != dragCache.sourceWidget);
    },
    
    //////////////////
    // Adapter Support
    
    getAdapter : function( clazz ) {
      if( this._adapters === undefined ) {
        this._adapters = {};
      }
      var key = clazz.classname;
      var result = this._adapters[ key ];
      if( result == null ) {
        new clazz( this );
        result = this._adapters[ key ];
      }
      return result;
    }
    
  },

  ///////////
  // SETTINGS

  settings : {
    "qx.widgetQueueDebugging" : false,
    "qx.widgetDebugId"        : false   // true: auto-generate HTML id's
  },

  ////////
  // DEFER

  defer : function(statics, members) {
    statics.__initApplyMethods(members);
    // In MSHTML we rewrite these runtime setters to improve the
    // performance when using enhanced borders (2px complex borders)
    // The problem are percentage width used by other browsers, too, to
    // allow these complex borders to be rendered. IE performs worst
    // when using percent. Because of this we add a lot overhead to
    // calculate the inner size in IE. This is faster than the old
    // much simpler solution with applying 100% width and height
    // See also bug http://bugzilla.qooxdoo.org/show_bug.cgi?id=487
    // See also: global cursor handling in ClientDocument
    // Regarding innerStyle:
    // Enhanced border are always 2px width, we need
    // to substract the two border pixels assigned to
    // the outer element from the outer width to get
    // the inner width
    if (qx.core.Variant.isSet("qx.client", "mshtml")) {
      members._renderRuntimeWidth = function(v) {
        this._style.pixelWidth = (v==null)?0:v;
        if( this._layoutTargetNode ) {
          var innerValue = this._usesComplexBorder && v != null ? v - 2 : v; 
          this._innerStyle.pixelWidth = innerValue == null ? 0 : innerValue;
        }
      };
      members._renderRuntimeHeight = function(v) {
        this._style.pixelHeight = (v==null)?0:v;
        if( this._layoutTargetNode ) {
          var innerValue = this._usesComplexBorder && v != null ? v - 2 : v;
          this._innerStyle.pixelHeight = innerValue == null ? 0 : innerValue;
        }
      };
      members._resetRuntimeWidth = function() {
        this._style.width = "";
        if( this._layoutTargetNode ) {
          this._innerStyle.width = "";
        }
      };
      members._resetRuntimeHeight = function() {
        this._style.height = "";
        if( this._layoutTargetNode ) {
          this._innerStyle.height = "";
        }
      };
    }
    statics.__initLayoutProperties(statics);
  },

  /////////////
  // DESTRUCTOR

  destruct : function() {
    var elem = this.getElement();
    if (elem) {
      elem.qx_Widget = null;
    }
    this._disposeObjectDeep( "_adapters", 1 );
    this._disposeFields("_isCreated", "_inlineEvents", "_element", "_style",
      "_targetNode", "_innerStyle", "_oldParent", "_styleProperties",
      "_htmlProperties", "_htmlAttributes", "__states", "_jobQueue",
      "_layoutChanges", "__borderObject");
  }
} );
/*******************************************************************************
 *  Copyright: 2006, 2010 STZ-IDA, Germany, http://www.stz-ida.de
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    STZ-IDA and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * A scroll bar.
 */
qx.Class.define("qx.ui.basic.ScrollBar",
{
  extend : qx.ui.layout.CanvasLayout,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param horizontal {Boolean ? false} whether the scroll bar should be
   *    horizontal. If false it will be vertical.
   */
  construct : function(horizontal)
  {
    this.base(arguments, horizontal ? "horizontal" : "vertical");

    this._horizontal = (horizontal == true);

    this._scrollBar = new qx.ui.basic.ScrollArea;

    if (qx.core.Variant.isSet("qx.client", "gecko"))
    {
      // NOTE: We have to force not using position:absolute, because this causes
      //     strange looking scrollbars in some cases (e.g. in Firefox under
      //     Linux the horizontal scrollbar is too high)
      this._scrollBar.setStyleProperty("position", "");
    }

    this._scrollBar.setOverflow(horizontal ? "scrollX" : "scrollY");
    this._scrollBar.addEventListener("scroll", this._onscroll, this);

    this._scrollContent = new qx.ui.basic.Terminator;

    if (qx.core.Variant.isSet("qx.client", "gecko")) {
      this._scrollContent.setStyleProperty("position", "");
    }

    this._scrollBar.add(this._scrollContent);

    if (this._horizontal)
    {
      this._scrollContent.setHeight(5);
      this._scrollBar.setWidth("100%");
      this._scrollBar.setHeight(this._getScrollBarWidth());

      // IE needs that the scrollbar element has a width of +1
      if (qx.core.Variant.isSet("qx.client", "mshtml"))
      {
        this.setHeight(this._getScrollBarWidth());
        this.setOverflow("hidden");
        this._scrollBar.setHeight(this._getScrollBarWidth() + 1);
        this._scrollBar.setTop(-1);
      }
    }
    else
    {
      this._scrollContent.setWidth(5);
      this._scrollBar.setHeight("100%");
      this._scrollBar.setWidth(this._getScrollBarWidth());

      // IE needs that the scrollbar element has a width of +1
      if (qx.core.Variant.isSet("qx.client", "mshtml"))
      {
        this.setWidth(this._getScrollBarWidth());
        this.setOverflow("hidden");
        this._scrollBar.setWidth(this._getScrollBarWidth() + 1);
        this._scrollBar.setLeft(-1);
      }
    }

    // Fix for http://bugzilla.qooxdoo.org/show_bug.cgi?id=1862
    if( qx.core.Variant.isSet( "qx.client", "mshtml" ) || qx.core.Variant.isSet( "qx.client", "opera" ) ) {
      if( this._horizontal ) {
        this.addEventListener( "changeWidth", this._onresize, this );
      } else {
        this.addEventListener( "changeHeight", this._onresize, this );
      }
    }

    this.add(this._scrollBar);

    this._blocker = new qx.ui.basic.Terminator();
    this._blocker.set({
      left : 0,
      top : 0,
      height : "100%",
      width : "100%",
      display : !this.getEnabled()
    });
    this._blocker.setAppearance( "scrollbar-blocker" );
    this.add(this._blocker);


    this.setMaximum(0);
  },




  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /**
     * The delay when to update the scroll bar value after a scroll event if
     * {@link #mergeEvents} is true (in milliseconds). All scroll events that arrive
     * in shorter time will be merged.
     */
    EVENT_DELAY : 250
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * The current value of the scroll bar. This value is between 0 and
     * (maxium - size), where size is the width of a horizontal resp. the height of
     * a vertical scroll bar in pixels.
     *
     * @see #maximum
     */
    value :
    {
      check : "Number",
      init : 0,
      apply : "_applyValue",
      event : "changeValue",
      transform : "_checkValue"
    },


    /**
     * The maximum value of the scroll bar. Note that the size of the scroll bar is
     * substracted.
     *
     * @see #value
     */
    maximum :
    {
      check : "Integer",
      apply : "_applyMaximum"
    },


    /**
     * Whether to merge consecutive scroll event. If true, events will be collected
     * until the user stops scrolling, so the scroll bar itself will move smoothly
     * and the scrolled content will update asynchroniously.
     */
    mergeEvents :
    {
      check : "Boolean",
      init : false
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Limit the value to the allowed range of the scroll bar.
     *
     * @type member
     * @param value {Number} Current value
     * @return {Number} Limited value.
     */
    _checkValue : function(value)
    {
      var innerSize = !this.getElement() ? 0 : (this._horizontal ? this.getInnerWidth() : this.getInnerHeight());

      // NOTE: We can't use Number.limit here because our maximum may get negative
      //       (when the scrollbar isn't needed). In this case Number.limit returns
      //       this negative maximum instead of 0. But we need that the minimum is
      //       stronger than the maximum.
      //       -> We use Math.max and Math.min
      return Math.max(0, Math.min(this.getMaximum() - innerSize, value));
    },


    _applyValue : function(value, old)
    {
      if (!this._internalValueChange && this._isCreated) {
        this._positionKnob(value);
      }
    },


    _applyMaximum : function(value, old)
    {
      if (this._horizontal) {
        this._scrollContent.setWidth(value);
        // fix for Bug 299620: set maximum and value in same call:
        if( this._scrollContent.isCreated() ) {
          this._scrollContent.getElement().style.width = value + "px";
        }
      } else {
        this._scrollContent.setHeight(value);
        // fix for Bug 299620: set maximum and value in same call:
        if( this._scrollContent.isCreated() ) {
          this._scrollContent.getElement().style.height = value + "px";
        }
      }

      // recheck the value
      this.setValue(this._checkValue(this.getValue()));
    },


    // property modifier
    _applyVisibility : function(value, old)
    {
      if (!value) {
        this._positionKnob(0);
      } else {
        this._positionKnob(this.getValue());
      }

      return this.base(arguments, value, old);
    },


    /**
     * overridden
     * @return {Integer}
     */
    _computePreferredInnerWidth : function() {
      return this._horizontal ? 0 : this._getScrollBarWidth();
    },


    /**
     * overridden
     * @return {Integer}
     */
    _computePreferredInnerHeight : function() {
      return this._horizontal ? this._getScrollBarWidth() : 0;
    },


    _applyEnabled : function(isEnabled)
    {
      this.base(arguments);

			/**
			 * In Opera for OS X it is not possible to block the scrollbar knob.
			 * To avoid scrolling set the overflow to hidden.
			 */
	    if(qx.core.Variant.isSet("qx.client", "opera") &&  qx.core.Client.runsOnMacintosh())
			{
				var overflow = isEnabled ? (this._horizontal ? "scrollX" : "scrollY") : "hidden";
		    this._scrollBar.setOverflow(overflow);
			}

      this._blocker.setDisplay(!this.getEnabled());
    },


    /**
     * Gets the width of vertical scroll bar.
     *
     * @type member
     * @return {Integer} the width in pixels.
     */
    _getScrollBarWidth : function()
    {
      // Auto-detect the scrollbar width
      if (qx.ui.basic.ScrollBar._scrollBarWidth == null)
      {
        var dummy = document.createElement("div");
        dummy.style.width = "100px";
        dummy.style.height = "100px";
        dummy.style.overflow = "scroll";
        dummy.style.visibility = "hidden";
        document.body.appendChild(dummy);
        qx.ui.basic.ScrollBar._scrollBarWidth = dummy.offsetWidth - dummy.clientWidth;
        document.body.removeChild(dummy);
      }

      return qx.ui.basic.ScrollBar._scrollBarWidth;
    },


    /**
     * Event handler. Called when the user scrolled.
     *
     * @type member
     * @param evt {Map} the event.
     * @return {void}
     */
    _onscroll : function(evt)
    {
      var value = this._horizontal ? this._scrollBar.getScrollLeft() : this._scrollBar.getScrollTop();

      if (this.getMergeEvents())
      {
        this._lastScrollEventValue = value;
        window.clearTimeout(this._setValueTimerId);
        var self = this;

        this._setValueTimerId = window.setTimeout(function()
        {
          self._internalValueChange = true;
          self.setValue(self._lastScrollEventValue);
          self._internalValueChange = false;
          qx.ui.core.Widget.flushGlobalQueues();
        },
        qx.ui.basic.ScrollBar.EVENT_DELAY);
      }
      else
      {
        this._internalValueChange = true;
        this.setValue(value);
        this._internalValueChange = false;
        qx.ui.core.Widget.flushGlobalQueues();
      }
    },

    // Fix for http://bugzilla.qooxdoo.org/show_bug.cgi?id=1862
    // Event handler, called when the width of horizontal scroll bar 
    // or the height of vertical scroll bar is changed. 
    _onresize : function( evt ) {
      var currentValue = this.getValue();
      var maxValue = 0;
      if( this._horizontal ) {
        maxValue = this.getMaximum() - this.getWidth();
      } else {
        maxValue = this.getMaximum() - this.getHeight();
      }
      if( currentValue > maxValue ) {
        this.setValue( maxValue );
      }
    },

    /**
     * Positions the scroll bar knob at a certain value.
     *
     * @type member
     * @param value {Integer} The value where to postion the scroll bar.
     * @return {void}
     */
    _positionKnob : function(value)
    {
      if (this.isCreated())
      {
        if (this._horizontal) {
          this._scrollBar.setScrollLeft(value);
        } else {
          this._scrollBar.setScrollTop(value);
        }
      }
    },

    // overridden
    _afterAppear : function()
    {
      this.base(arguments);
      // fix for RAP Bug 299620: scrolling in IE
      if(    qx.core.Client.getEngine() == "mshtml" 
          && this._scrollContent.isCreated() )
      {
        var style = this._scrollContent.getElement().style;
        var width = style.width;
        var height = style.height;
        style.width = "0px";
        style.height = "0px";
        style.width = width;
        style.height = height;       
      } 
      this._positionKnob(this.getValue());
    }
  },



  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_scrollContent", "_scrollBar", "_blocker");
  }
});

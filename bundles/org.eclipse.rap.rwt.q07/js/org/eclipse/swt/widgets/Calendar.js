/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
 /**
  * This is a modified version of qooxdoo qx.ui.component.DateChooser component.
  */

/**
 * Shows a calendar and allows choosing a date.
 *
 * @appearance calendar-toolbar-button {qx.ui.toolbar.Button}
 * @appearance calendar-navBar {qx.ui.layout.BoxLayout}
 * @appearance calendar-monthyear {qx.ui.basic.Label}
 * @appearance calendar-weekday {qx.ui.basic.Label}
 * @appearance calendar-datepane {qx.ui.layout.CanvasLayout}
 * @appearance calendar-weekday {qx.ui.basic.Label}
 *
 * @appearance calendar-day {qx.ui.basic.Label}
 * @state weekend {calendar-day}
 * @state otherMonth {calendar-day}
 * @state today {calendar-day}
 * @state selected {calendar-day}
 */
qx.Class.define("org.eclipse.swt.widgets.Calendar", {
  extend : qx.ui.layout.BoxLayout,

  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param date {Date ? null} The initial date to show. If <code>null</code>
   *        the current day (today) is shown.
   */
  construct : function(date) {
    this.base(arguments);

    this.setOrientation("vertical");

    // Create the navigation bar
    var navBar = new qx.ui.layout.BoxLayout;
    navBar.setAppearance("calendar-navBar");

    navBar.set({
      height  : "auto",
      spacing : 1
    });

    var lastMonthBt = new qx.ui.toolbar.Button(null, "widget/calendar/lastMonth.gif");
    var monthYearLabel = new qx.ui.basic.Label;
    var nextMonthBt = new qx.ui.toolbar.Button(null, "widget/calendar/nextMonth.gif");

    this._lastMonthBtToolTip = new qx.ui.popup.ToolTip(this.tr("Last month"));
    this._nextMonthBtToolTip = new qx.ui.popup.ToolTip(this.tr("Next month"));
    
    lastMonthBt.set({
      show    : 'icon',
      toolTip : this._lastMonthBtToolTip
    });    
    
    nextMonthBt.set({
      show    : 'icon',
      toolTip : this._nextMonthBtToolTip
    });

    lastMonthBt.setAppearance("calendar-toolbar-button");
    nextMonthBt.setAppearance("calendar-toolbar-button");

    lastMonthBt.addEventListener("click", this._onNavButtonClicked, this);
    nextMonthBt.addEventListener("click", this._onNavButtonClicked, this);

    this._lastMonthBt = lastMonthBt;
    this._nextMonthBt = nextMonthBt;

    monthYearLabel.setAppearance("calendar-monthyear");
    monthYearLabel.set({ width : "1*" });

    navBar.add(lastMonthBt, monthYearLabel, nextMonthBt);
    this._monthYearLabel = monthYearLabel;
    navBar.setHtmlProperty("id", "navBar");

    // Create the date pane
    var datePane = new qx.ui.layout.CanvasLayout;
    datePane.setAppearance("calendar-datepane");

    datePane.set({
      width  : org.eclipse.swt.widgets.Calendar.CELL_WIDTH * 7,
      height : org.eclipse.swt.widgets.Calendar.CELL_HEIGHT * 7
    });

    this._weekdayLabelArr = [];

    for (var i=0; i<7; i++) {
      var label = new qx.ui.basic.Label;
      label.setAppearance("calendar-weekday");
      label.setSelectable(false);
      label.setCursor("default");

      label.set({
        width  : org.eclipse.swt.widgets.Calendar.CELL_WIDTH,
        height : org.eclipse.swt.widgets.Calendar.CELL_HEIGHT,
        left   : i * org.eclipse.swt.widgets.Calendar.CELL_WIDTH
      });

      datePane.add(label);
      this._weekdayLabelArr.push(label);
    }

    // Add the days
    this._dayLabelArr = [];

    for (var y=0; y<6; y++) {
      // Add the day labels
      for (var x=0; x<7; x++) {
        var label = new qx.ui.basic.Label;
        label.setAppearance("calendar-day");
        label.setSelectable(false);
        label.setCursor("default");

        label.set({
          width  : org.eclipse.swt.widgets.Calendar.CELL_WIDTH,
          height : org.eclipse.swt.widgets.Calendar.CELL_HEIGHT,
          left   : x * org.eclipse.swt.widgets.Calendar.CELL_WIDTH,
          top    : (y + 1) * org.eclipse.swt.widgets.Calendar.CELL_HEIGHT
        });

        label.addEventListener("mousedown", this._onDayClicked, this);
        label.addEventListener("dblclick", this._onDayDblClicked, this);
        datePane.add(label);
        this._dayLabelArr.push(label);
      }
    }

    // Make focusable
    this.setTabIndex(1);
    this.addEventListener("keypress", this._onkeypress);

    // Show the right date
    var shownDate = (date != null) ? date : new Date();
    this.showMonth(shownDate.getMonth(), shownDate.getFullYear());

    // listen for locale changes
    qx.locale.Manager.getInstance().addEventListener("changeLocale", this._updateDatePane, this);

    // Add the main widgets
    this.add(navBar);
    this.add(datePane);

    // Initialize dimensions
    this.initWidth();
    this.initHeight();
  },

  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events: {
    /** Fired when a date was selected. The event holds the new selected date in its data property.*/
    "select"     : "qx.event.type.DataEvent"
  },

  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics : {
  	CELL_WIDTH : 24,
  	CELL_HEIGHT : 16,
    MONTH_NAMES : [],
    WEEKDAY_NAMES : []
  },

  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties : {
    width : {
      refine : true,
      init : "auto"
    },

    height : {
      refine : true,
      init : "auto"
    },

    /** The currently shown month. 0 = january, 1 = february, and so on. */
    shownMonth : {
      check : "Integer",
      init : null,
      nullable : true,
      event : "changeShownMonth"
    },

    /** The currently shown year. */
    shownYear : {
      check : "Integer",
      init : null,
      nullable : true,
      event : "changeShownYear"
    },

    /** {Date} The currently selected date. */
    date : {
      check : "Date",
      init : null,
      nullable : true,
      apply : "_applyDate",
      event : "changeDate",
      transform : "_checkDate"
    }
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members : {
    // property checker
    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @return {var} TODOC
     */
    _checkDate : function(value) {
      // Use a clone of the date internally since date instances may be changed
      return (value == null) ? null : new Date(value.getTime());
    },

    // property modifier
    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyDate : function(value, old) {
      if ((value != null) && (this.getShownMonth() != value.getMonth() || this.getShownYear() != value.getFullYear())) {
        // The new date is in another month -> Show that month
        this.showMonth(value.getMonth(), value.getFullYear());
      } else {
        // The new date is in the current month -> Just change the states
        var newDay = (value == null) ? -1 : value.getDate();

        for (var i=0; i<6*7; i++) {
          var dayLabel = this._dayLabelArr[i];

          if (dayLabel.hasState("otherMonth")) {
            if (dayLabel.hasState("selected")) {
              dayLabel.removeState("selected");
            }
          } else {
            var day = parseInt(dayLabel.getText());

            if (day == newDay) {
              dayLabel.addState("selected");
            } else if (dayLabel.hasState("selected")) {
              dayLabel.removeState("selected");
            }
          }
        }
      }
    },

    /**
     * Event handler. Called when a navigation button has been clicked.
     *
     * @type member
     * @param evt {Map} the event.
     * @return {void}
     */
    _onNavButtonClicked : function(evt) {
      var year = this.getShownYear();
      var month = this.getShownMonth();

      switch(evt.getCurrentTarget()) {
        case this._lastMonthBt:
          month--;

          if (month < 0) {
            month = 11;
            year--;
          }

          break;

        case this._nextMonthBt:
          month++;

          if (month >= 12) {
            month = 0;
            year++;
          }

          break;
      }

      this.showMonth(month, year);
    },

    /**
     * Event handler. Called when a day has been clicked.
     *
     * @type member
     * @param evt {Map} the event.
     * @return {void}
     */
    _onDayClicked : function(evt) {
    	if( evt.isLeftButtonPressed() ) {
        var time = evt.getCurrentTarget().dateTime;
        this.setDate(new Date(time));
    	}
    },

    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    _onDayDblClicked : function() {
      this.createDispatchDataEvent("select", this.getDate());
    },

    /**
     * Event handler. Called when a key was pressed.
     *
     * @type member
     * @param evt {Map} the event.
     * @return {boolean | void} TODOC
     */
    _onkeypress : function(evt) {
      var dayIncrement = null;
      var monthIncrement = null;
      var yearIncrement = null;

      if (evt.getModifiers() == 0) {
        switch(evt.getKeyIdentifier()) {
          case "Left":
            dayIncrement = -1;
            break;

          case "Right":
            dayIncrement = 1;
            break;

          case "Up":
            dayIncrement = -7;
            break;

          case "Down":
            dayIncrement = 7;
            break;

          case "PageUp":
            monthIncrement = -1;
            break;

          case "PageDown":
            monthIncrement = 1;
            break;

          case "Escape":
            if (this.getDate() != null) {
              this.setDate(null);
              return true;
            }

            break;

          case "Enter":
          case "Space":
            if (this.getDate() != null) {
              this.createDispatchDataEvent("select", this.getDate());
            }

            return;
        }
      }
      else if (evt.isShiftPressed()) {
        switch(evt.getKeyIdentifier()) {
          case "PageUp":
            yearIncrement = -1;
            break;

          case "PageDown":
            yearIncrement = 1;
            break;
        }
      }

      if (dayIncrement != null || monthIncrement != null || yearIncrement != null) {
        var date = this.getDate();

        if (date != null) {
          date = new Date(date.getTime()); // TODO: Do cloning in getter
        }

        if (date == null) {
          date = new Date();
        } else {
          if (dayIncrement != null) date.setDate(date.getDate() + dayIncrement);
          if (monthIncrement != null) date.setMonth(date.getMonth() + monthIncrement);
          if (yearIncrement != null) date.setFullYear(date.getFullYear() + yearIncrement);
        }

        this.setDate(date);
      }
    },

    // ***** Methods *****
    /**
     * Shows a certain month.
     *
     * @type member
     * @param month {Integer ? null} the month to show (0 = january). If not set the month
     *      will remain the same.
     * @param year {Integer ? null} the year to show. If not set the year will remain the
     *      same.
     * @return {void}
     */
    showMonth : function(month, year) {
      if ((month != null && month != this.getShownMonth()) || (year != null && year != this.getShownYear())) {
        if (month != null) {
          this.setShownMonth(month);
        }

        if (year != null) {
          this.setShownYear(year);
        }

        this._updateDatePane();
      }
    },

    /**
     * Updates the date pane.
     *
     * @type member
     * @return {void}
     */
    _updateDatePane : function() {
      var today = new Date();
      var todayYear = today.getFullYear();
      var todayMonth = today.getMonth();
      var todayDayOfMonth = today.getDate();

      var selDate = this.getDate();
      var selYear = (selDate == null) ? -1 : selDate.getFullYear();
      var selMonth = (selDate == null) ? -1 : selDate.getMonth();
      var selDayOfMonth = (selDate == null) ? -1 : selDate.getDate();

      var shownMonth = this.getShownMonth();
      var shownYear = this.getShownYear();

      var startOfWeek = this.__getWeekStart();

      // Create a help date that points to the first of the current month
      var helpDate = new Date(this.getShownYear(), this.getShownMonth(), 1);
      
      var year = this.getShownYear();      
      var month = org.eclipse.swt.widgets.Calendar.MONTH_NAMES[ this.getShownMonth() ];
      this._monthYearLabel.setText( month + ", " + year );

      // Show the day names
      var firstDayOfWeek = helpDate.getDay();
      var firstSundayInMonth = (1 + 7 - firstDayOfWeek) % 7;
      
      for (var i=0; i<7; i++) {
        var day = (i + startOfWeek) % 7;

        var dayLabel = this._weekdayLabelArr[i];

        helpDate.setDate(firstSundayInMonth + day);
        
        var weekdayName = org.eclipse.swt.widgets.Calendar.WEEKDAY_NAMES[ helpDate.getDay() + 1 ];
        weekdayName = weekdayName.substring( 0, 3 );
        
        dayLabel.setText( weekdayName );

        if (this.__isWeekend(day)) {
          dayLabel.addState("weekend");
        } else {
          dayLabel.removeState("weekend");
        }
      }

      // Show the days
      helpDate = new Date(shownYear, shownMonth, 1);
      var nrDaysOfLastMonth = (7 + firstDayOfWeek - startOfWeek) % 7;
      helpDate.setDate(helpDate.getDate() - nrDaysOfLastMonth);

      for (var week=0; week<6; week++) {
        for (var i=0; i<7; i++) {
          var dayLabel = this._dayLabelArr[week * 7 + i];

          var year = helpDate.getFullYear();
          var month = helpDate.getMonth();
          var dayOfMonth = helpDate.getDate();

          var isSelectedDate = (selYear == year && selMonth == month && selDayOfMonth == dayOfMonth);

          if (isSelectedDate) {
            dayLabel.addState("selected");
          } else {
            dayLabel.removeState("selected");
          }

          if (month != shownMonth) {
            dayLabel.addState("otherMonth");
          } else {
            dayLabel.removeState("otherMonth");
          }

          var isToday = (year == todayYear && month == todayMonth && dayOfMonth == todayDayOfMonth);

          if (isToday) {
            dayLabel.addState("today");
          } else {
            dayLabel.removeState("today");
          }

          dayLabel.setText("" + dayOfMonth);
          dayLabel.dateTime = helpDate.getTime();

          // Go to the next day
          helpDate.setDate(helpDate.getDate() + 1);
        }
      }
    }, 
    
    /**
     * Return the day the week starts with
     *
     * Reference: Common Locale Data Repository (cldr) supplementalData.xml
     *
     * @type member
     * @param locale {String} optional locale to be used
     * @return {Integer} index of the first day of the week. 0=sunday, 1=monday, ...
     */
    __getWeekStart : function(locale) {
      var weekStart = {
        // default is monday
        "MV" : 5, // friday
        "AE" : 6, // saturday
        "AF" : 6,
        "BH" : 6,
        "DJ" : 6,
        "DZ" : 6,
        "EG" : 6,
        "ER" : 6,
        "ET" : 6,
        "IQ" : 6,
        "IR" : 6,
        "JO" : 6,
        "KE" : 6,
        "KW" : 6,
        "LB" : 6,
        "LY" : 6,
        "MA" : 6,
        "OM" : 6,
        "QA" : 6,
        "SA" : 6,
        "SD" : 6,
        "SO" : 6,
        "TN" : 6,
        "YE" : 6,
        "AS" : 0, // sunday
        "AU" : 0,
        "AZ" : 0,
        "BW" : 0,
        "CA" : 0,
        "CN" : 0,
        "FO" : 0,
        "GE" : 0,
        "GL" : 0,
        "GU" : 0,
        "HK" : 0,
        "IE" : 0,
        "IL" : 0,
        "IS" : 0,
        "JM" : 0,
        "JP" : 0,
        "KG" : 0,
        "KR" : 0,
        "LA" : 0,
        "MH" : 0,
        "MN" : 0,
        "MO" : 0,
        "MP" : 0,
        "MT" : 0,
        "NZ" : 0,
        "PH" : 0,
        "PK" : 0,
        "SG" : 0,
        "TH" : 0,
        "TT" : 0,
        "TW" : 0,
        "UM" : 0,
        "US" : 0,
        "UZ" : 0,
        "VI" : 0,
        "ZA" : 0,
        "ZW" : 0,
        "MW" : 0,
        "NG" : 0,
        "TJ" : 0
      };

      var territory = this.__getTerritory(locale);

      // default is monday
      return weekStart[territory] != null ? weekStart[territory] : 1;
    },
    
    /**
     * Return the day the weekend starts with
     *
     * Reference: Common Locale Data Repository (cldr) supplementalData.xml
     *
     * @type member
     * @param locale {String} optional locale to be used
     * @return {Integer} index of the first day of the weekend. 0=sunday, 1=monday, ...
     */
    __getWeekendStart : function(locale) {
      var weekendStart = {
        // default is saturday
        "EG" : 5, // friday
        "IL" : 5,
        "SY" : 5,
        "IN" : 0, // sunday
        "AE" : 4, // thursday
        "BH" : 4,
        "DZ" : 4,
        "IQ" : 4,
        "JO" : 4,
        "KW" : 4,
        "LB" : 4,
        "LY" : 4,
        "MA" : 4,
        "OM" : 4,
        "QA" : 4,
        "SA" : 4,
        "SD" : 4,
        "TN" : 4,
        "YE" : 4
      };

      var territory = this.__getTerritory(locale);

      // default is saturday
      return weekendStart[territory] != null ? weekendStart[territory] : 6;
    },

    /**
     * Return the day the weekend ends with
     *
     * Reference: Common Locale Data Repository (cldr) supplementalData.xml
     *
     * @type member
     * @param locale {String} optional locale to be used
     * @return {Integer} index of the last day of the weekend. 0=sunday, 1=monday, ...
     */
    __getWeekendEnd : function(locale) {
      var weekendEnd = {
        // default is sunday
        "AE" : 5, // friday
        "BH" : 5,
        "DZ" : 5,
        "IQ" : 5,
        "JO" : 5,
        "KW" : 5,
        "LB" : 5,
        "LY" : 5,
        "MA" : 5,
        "OM" : 5,
        "QA" : 5,
        "SA" : 5,
        "SD" : 5,
        "TN" : 5,
        "YE" : 5,
        "AF" : 5,
        "IR" : 5,
        "EG" : 6, // saturday
        "IL" : 6,
        "SY" : 6
      };

      var territory = this.__getTerritory(locale);

      // default is sunday
      return weekendEnd[territory] != null ? weekendEnd[territory] : 0;
    },

    /**
     * Returns whether a certain day of week belongs to the week end.
     *
     * @type member
     * @param day {Integer} index of the day. 0=sunday, 1=monday, ...
     * @param locale {String} optional locale to be used
     * @return {Boolean} whether the given day is a weekend day
     */
    __isWeekend : function(day, locale) {
      var weekendStart = this.__getWeekendStart(locale);
      var weekendEnd = this.__getWeekendEnd(locale);

      if (weekendEnd > weekendStart) {
        return ((day >= weekendStart) && (day <= weekendEnd));
      } else {
        return ((day >= weekendStart) || (day <= weekendEnd));
      }
    },
    
    /**
     * Extract the territory part from a locale
     *
     * @type member
     * @param locale {String} the locale
     * @return {String} territory
     */
    __getTerritory : function(locale) {
      if (locale) {
        var territory = locale.split("_")[1] || locale;
      } else {
        territory = qx.locale.Manager.getInstance().getTerritory() || qx.locale.Manager.getInstance().getLanguage();
      }

      return territory.toUpperCase();
    }
  },

  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    qx.locale.Manager.getInstance().removeEventListener("changeLocale", this._updateDatePane, this);
    
    this._disposeObjects("_lastMonthBtToolTip", "_nextMonthBtToolTip");
    this._disposeObjects("_lastMonthBt", "_nextMonthBt", "_monthYearLabel");

    this._disposeObjectDeep("_weekdayLabelArr", 1);
    this._disposeObjectDeep("_dayLabelArr", 1);    
  }
});

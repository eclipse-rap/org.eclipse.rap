CKEDITOR.editorConfig = function( config ) {

  /**
   * If you change the language, remember to also change CKEditor#RESOURCE_FILES to
   * load the corresponding "/lang/xxx.js" file.
   */
  config.language = 'en';

  /**
   * You can freely change the tool-items arrangement or remove them. You can not add new ones
   * (from the full CKEditor version) without adapting config.removePlugins and
   * building a new ckeditor.js. Take care: An incorrectly placed "," and the editor
   * won't start in IE.
   */
  config.toolbar = [
    {
      name: 'basicstyles',
      items : [
        'Bold',
        'Italic',
        'Underline',
        'Strike',
        'Subscript',
        'Superscript',
        '-',
        'RemoveFormat'
      ]
    },
    {
      name: 'paragraph',
      items : [
       'JustifyLeft',
       'JustifyCenter',
       'JustifyRight',
       'JustifyBlock',
       '-',
       'Outdent',
       'Indent',
       '-',
       'NumberedList',
       'BulletedList'
     ]
    },
    {
      name: 'advanced',
      items : [
        'Format',
        'Font',
        'FontSize',
        'TextColor',
        'BGColor'
      ]
    }
  ];

  config.toolbarCanCollapse = false;

  /**
   * Defines the colors to be displayed in the color selectors. This is a string
   * containing hexadecimal notation for HTML colors, without the "#" prefix.
   * A color name may optionally be defined by prefixing the entries with
   * a name and the slash character. For example, "FontColor1/FF9900" will be
   * displayed as the color #FF9900 in the selector, but will be output as "FontColor1".
   */
  config.colorButton_colors =
    '000,800000,8B4513,2F4F4F,008080,000080,4B0082,696969,' +
    'B22222,A52A2A,DAA520,006400,40E0D0,0000CD,800080,808080,' +
    'F00,FF8C00,FFD700,008000,0FF,00F,EE82EE,A9A9A9,' +
    'FFA07A,FFA500,FFFF00,00FF00,AFEEEE,ADD8E6,DDA0DD,D3D3D3,' +
    'FFF0F5,FAEBD7,FFFFE0,F0FFF0,F0FFFF,F0F8FF,E6E6FA,FFF';


  /**
   * The list of fonts names to be displayed in the Font combo in the toolbar.
   * Entries are separated by semi-colons (;), while it's possible to have more
   * than one font for each entry, in the HTML way (separated by comma).
   *
   * A display name may be optionally defined by prefixing the entries with the
   * name and the slash character. For example, "Arial/Arial, Helvetica, sans-serif"
   * will be displayed as "Arial" in the list, but will be outputted as
   * "Arial, Helvetica, sans-serif".
   */
  config.font_names =
    'Arial/Arial, Helvetica, sans-serif;' +
    'Comic Sans MS/Comic Sans MS, cursive;' +
    'Courier New/Courier New, Courier, monospace;' +
    'Georgia/Georgia, serif;' +
    'Lucida Sans Unicode/Lucida Sans Unicode, Lucida Grande, sans-serif;' +
    'Tahoma/Tahoma, Geneva, sans-serif;' +
    'Times New Roman/Times New Roman, Times, serif;' +
    'Trebuchet MS/Trebuchet MS, Helvetica, sans-serif;' +
    'Verdana/Verdana, Geneva, sans-serif';

  /**
   * The text to be displayed in the Font combo is none of the available values
   * matches the current cursor position or text selection. This could be the font
   * set on the SWT widget or in the RAP theming.
   */
  config.font_defaultLabel = '';

  /**
   * The list of fonts size to be displayed in the Font Size combo in the
   * toolbar. Entries are separated by semi-colons (;).
   *
   * Any kind of "CSS like" size can be used, like "12px", "2.3em", "130%",
   * "larger" or "x-small".
   *
   * A display name may be optionally defined by prefixing the entries with the
   * name and the slash character. For example, "Bigger Font/14px" will be
   * displayed as "Bigger Font" in the list, but will be outputted as "14px".
   */
  config.fontSize_sizes =
    '8/8px;9/9px;10/10px;11/11px;12/12px;14/14px;16/16px;18/18px;20/20px;22/22px;' +
    '24/24px;26/26px;28/28px;36/36px;48/48px;72/72px';

  /**
   * The text to be displayed in the Font Size combo is none of the available
   * values matches the current cursor position or text selection. This could be the fontsize
   * set on the SWT widget or in the RAP theming.
   */
  config.fontSize_defaultLabel = '';

  /**
   * A list of semi colon separated style names (by default tags) representing
   * the style definition for each entry to be displayed in the Format combo in
   * the toolbar. Each entry must have its relative definition configuration in a
   * setting named "format_(tagName)". Example:
   * config.format_tags = "bla;[...]";
   * config.format_bla = { element : "p", styles : { 'color' : 'Blue' } };
   * The current language file must also have an entry for each tag, like this: "tag_bla : 'Bla'"
   * The tags 'p','pre','address','div', and h1-h6 are predefined, but can be overwritten.
   */
  config.format_tags = 'p;h1;h2;h3;h4;h5;h6;pre;address';

  /**
   * A comma separated list of elements to be removed when executing the "remove
   " format" command. Note that only inline elements are allowed.
   * @type String
   * @default 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var'
   * @example
   */
  config.removeFormatTags = 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var';

  /**
   * A comma separated list of elements attributes to be removed when executing
   * the "remove format" command.
   * @type String
   * @default 'class,style,lang,width,height,align,hspace,valign'
   * @example
   */
  config.removeFormatAttributes = 'class,style,lang,width,height,align,hspace,valign';

  // [ RAP ] - do not change:

  config.skin = 'moono';

  config.colorButton_enableMore = false;

  config.autoGrow_onStartup = true;

  config.autoGrow_maxHeight = 0;

  config.autoGrow_minHeight = 0;

  config.baseFloatZIndex = 3000000;

  config.removePlugins = [
    "about",
    "a11yhelp",
    "bidi",
    "blockquote",
    "clipboard",
    "colordialog",
    "contextmenu",
    "dialogadvtab",
    "div",
    "elementspath",
    "filebrowser",
    "find",
    "flash",
    "forms",
    "horizontalrule",
    "iframe","" +
    "image",
    "link",
    "liststyle",
    "newpage",
    "pagebreak",
    "pastefromword",
    "pastetext",
    "popup",
    "preview",
    "print",
    "resize",
    "save",
    "scayt",
    "smiley",
    "showblocks",
    "showborders",
    "sourcearea",
    "stylescombo",
    "table",
    "tabletools",
    "specialchar",
    "tab",
    "templates",
    "undo",
    "wsc"
  ].join();

};

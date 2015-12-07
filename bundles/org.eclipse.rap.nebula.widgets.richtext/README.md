# RAP port of Nebula RichTextEditor

This bundle contains a RAP version of the nebula RichTextEditor [1] widget, based on CKEditor [2],
a web-based WYSIWYG/Rich-Text editor. CKEditor version 4.4.7 is used.

## Customization

The editor can be customized by editing the files in the `src/resources` folder of the
`org.eclipse.rap.nebula.widgets.richtext` bundle. You might need to clear the browsers cache and
restart the server for all changes to take effect.

### Editor Configuration

Editing the file `config.js` lets you change the toolbar, language, and formatting options (fonts,
colors). Be careful, all changes here bear the risk of breaking the editor.

### Editor Theming

To change the icons, edit or replace `icons.png`.
To change the editors colors, borders, spacings, etc, edit `editor.css`.
Use your browser's developer tools to examine which CSS classes are used by the editor.

### Advanced Customization

Some plugins had to be disabled in `config.js`, therefore not all options of the full CKEdtior are
supported.

[1] http://www.eclipse.org/nebula/widgets/richtext/richtext.php
[2] http://ckeditor.com/

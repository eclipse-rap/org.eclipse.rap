document.write('\
\
<!-- Qooxdoo -->\
<script src="../org.eclipse.rap.rwt.q07/js/qx-debug.js" type="text/javascript"></script>\
\
<!-- RAP -->\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/KeyEventHandlerPatch.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/Application.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/Request.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/WidgetManager.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/EventUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/KeyEventUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/AsyncKeyEventUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/SyncKeyEventUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/TabUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/ButtonUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/ToolItemUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/MenuUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/LinkUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/WidgetUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/custom/CTabFolder.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/custom/CTabItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/CLabelUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Sash.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/CoolItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/List.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Shell.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Tree.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/TreeItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/TreeItemUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/TreeColumn.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/custom/ScrolledComposite.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Separator.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/LabelUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Combo.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Group.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/TextUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Spinner.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Table.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/TableColumn.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/TableItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/TableRow.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/ExternalBrowser.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/ProgressBar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/browser/Browser.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/FontSizeCalculation.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/qx/constant/Core.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/qx/constant/Layout.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/qx/constant/Style.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Scale.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/DateTimeDate.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/DateTimeTime.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/DateTimeCalendar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Calendar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/ExpandBar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/ExpandItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/Slider.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/CheckBox.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/widgets/RadioButton.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/MultiCellWidget.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/AbstractButton.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/Button.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/ToolBar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/ToolItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/ToolSeparator.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/ToolTip.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/Menu.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/MenuItem.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/widgets/MenuBar.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/RadioButtonUtil.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/GfxMixin.js" type="text/javascript"></script>\
<script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/rwt/RoundedBorder.js" type="text/javascript"></script>\
\
<!-- rwt.test -->\
<script src="./js/org/eclipse/rwt/test/fixture/RAPRequestPatch.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/fixture/DummyRequest.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/fixture/RAPServer.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/fixture/AppSimulator.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/Presenter.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/TestRunner.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/fixture/TestUtil.js" type="text/javascript"></script>\
<script src="./js/org/eclipse/rwt/test/Asserts.js" type="text/javascript"></script>\
');

// How to generate the RAPThemeSupport.js:
// - Start an RAP-application
// - Open the application in an Firefox with Firebug 
// - In Firebug, go to "Html"
// - In the document, go to <body> -> <script>
// - Copy everything after call "qx.Class.define("org.eclipse.swt.theme.ThemeValues"
// - be careful not to copy the line-numbers with the code
// - add the following lines: (without the "//")
//  qx.io.Alias.getInstance().add( "static", "../org.eclipse.rap.rwt.q07/js/resource/static" );
//  qx.io.Alias.getInstance().add( "org.eclipse.swt", "../org.eclipse.rap.rwt.q07/js/resource" );


if( qxsettings["qx.theme"] == "org.eclipse.swt.theme.Default" ) {
  document.write('\
    <script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/theme/ThemeStore.js" type="text/javascript"></script>\
    <script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/theme/ThemeValues.js" type="text/javascript"></script>\
    <script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/theme/BordersBase.js" type="text/javascript"></script>\
    <script src="../org.eclipse.rap.rwt.q07/js/org/eclipse/swt/theme/AppearancesBase.js" type="text/javascript"></script>\
    <script src="./js/resource/RAPThemeSupport.js" type="text/javascript"></script>\
  ');
}
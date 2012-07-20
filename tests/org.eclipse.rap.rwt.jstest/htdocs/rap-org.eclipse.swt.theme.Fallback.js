/*
  NOTE : delete this file after fixing
  Bug 378225 - [ipad] JavaScript tests not working when using jetty/ClientResourcesServiceHandler
*/
( function( ts ) {
ts.defineValues( {
"colors": {
"3ff98c68": "#a7a6aa",
"4004dc1c": "#ffffff",
"3ff53320": "#85878c",
"40006240": "#dcdfe4",
"3ffa2a74": "#aca899",
"400457b0": "#fec83c",
"3fed9ae8": "#4a4a4a",
"3ffeb15c": "#cfcfcf",
"ffffffff": "undefined",
"3ff9f068": "#aaaaaa",
"3fff9798": "#d6d6d6",
"3fe42cec": "#0000ff",
"3fe4aeec": "#00589f",
"4001abb4": "#ec7869",
"3ffb5a34": "#b5b5b5",
"3ff3d584": "#7996a5",
"3fe4f000": "#0080c0",
"3ff48b00": "#808080",
"3fefb044": "#5882b5",
"40009eb8": "#dedede",
"3fe41900": "#000000"
},
"images": {
"994dccc6": [ 32, 32 ],
"2c4058a5": [ 32, 32 ],
"83e441e9": [ 32, 32 ],
"aa96e44e": [ 32, 32 ],
"3c7d3330": [ 1, 1600 ],
"fea0bb8": [ 21, 17 ],
"3655dc8d": [ 21, 17 ],
"903dd6ca": [ 21, 17 ],
"46d9d135": [ 17, 17 ],
"c290fe4b": [ 17, 17 ],
"9f435c8c": [ 9, 9 ],
"5c00d24b": [ 9, 9 ],
"c94fc8ad": [ 9, 9 ],
"1d0cc994": [ 9, 9 ],
"7d708cd9": [ 7, 5 ],
"fa5334b": [ 10, 7 ],
"144ea8f9": [ 16, 7 ],
"d218be7e": [ 16, 5 ],
"eb8ac0f1": [ 13, 13 ],
"dd851a27": [ 32, 32 ],
"63a3ac85": [ 27, 27 ],
"679d075a": [ 27, 27 ],
"60504cca": [ 27, 27 ],
"4cdd7012": [ 27, 27 ],
"49104d78": [ 7, 5 ],
"d1516768": [ 7, 5 ],
"2fc16aa8": [ 9, 5 ],
"5b8ba614": [ 9, 5 ],
"110f0331": [ 7, 5 ],
"d282b4e5": [ 7, 7 ],
"4ee00e3c": [ 6, 9 ],
"5fd49387": [ 6, 7 ],
"1faf9af7": [ 6, 7 ],
"89265bb2": [ 10, 7 ],
"2d244d58": [ 10, 7 ],
"d886e9d6": [ 7, 5 ],
"306e0068": [ 7, 5 ],
"6cbccb98": [ 16, 16 ],
"50cab11f": [ 16, 16 ],
"2ed10572": [ 32, 32 ],
"5be1033a": [ 10, 10 ],
"17352e6f": [ 10, 10 ],
"827a3489": [ 10, 10 ],
"a30b8da1": [ 10, 10 ],
"1888a858": [ 32, 32 ],
"d19cc93d": [ 32, 32 ],
"627301a4": [ 32, 32 ],
"56abe5a7": [ 7, 5 ],
"bf671604": [ 32, 32 ],
"ba1d7000": [ 10, 10 ],
"fb90316c": [ 10, 10 ],
"6edf2b8a": [ 10, 10 ],
"3aee7904": [ 10, 10 ]
},
"fonts": {
"67d9b899": {
"family": [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode" ],
"size": 14,
"bold": false,
"italic": false
},
"3a7cfc11": {
"family": [ "Arial", "sans-serif" ],
"size": 14,
"bold": false,
"italic": false
},
"3a7ce581": {
"family": [ "Arial", "sans-serif" ],
"size": 10,
"bold": false,
"italic": false
},
"18a06a3": {
"family": [ "Arial", "sans-serif" ],
"size": 14,
"bold": true,
"italic": false
},
"e7de1195": {
"family": [ "Segoe UI", "Corbel", "Calibri", "Tahoma", "Lucida Sans Unicode", "sans-serif" ],
"size": 11,
"bold": false,
"italic": false
}
},
"borders": {
"dee0ab31": {
"width": 1,
"style": "solid",
"color": "#aaaaaa"
},
"36a": {
"width": 0,
"style": null,
"color": null
},
"2b4cf00b": {
"width": 1,
"style": "dotted",
"color": "#aaaaaa"
},
"e0c04a17": {
"width": 1,
"style": "solid",
"color": "#bdbdbd"
},
"c411a436": {
"width": 1,
"style": "outset",
"color": null
},
"5fbe2ff": {
"width": 1,
"style": "inset",
"color": null
},
"5fbe325": {
"width": 2,
"style": "inset",
"color": null
}
},
"boxdims": {
"2144df1c": [ 0, 0, 0, 0 ],
"2ce1a471": [ 8, 8, 8, 8 ],
"8393ccd2": [ 3, 3, 3, 3 ],
"ca2ee18a": [ 4, 4, 4, 4 ],
"c9967dea": [ 6, 15, 6, 15 ],
"54f1c057": [ 2, 2, 2, 2 ],
"86afb7a3": [ 5, 10, 5, 10 ],
"e9929b96": [ 20, 10, 10, 10 ],
"b1369cc8": [ 15, 8, 8, 8 ],
"543919dd": [ 2, 10, 2, 10 ],
"a35b8659": [ 10, 10, 10, 20 ],
"bf374b8e": [ 6, 10, 6, 10 ],
"27a454c8": [ 4, 6, 4, 6 ],
"cc06b3d4": [ 0, 10, 0, 10 ],
"2708e187": [ 4, 10, 4, 10 ],
"655c8929": [ 15, 15, 15, 15 ],
"9e1e81c6": [ 0, -2, 0, 0 ],
"a680cf80": [ 6, 6, 7, 6 ],
"bf9bfec1": [ 6, 6, 6, 6 ],
"b7669218": [ 8, 3, 8, 3 ],
"c93ac8a5": [ 6, 3, 6, 3 ],
"26291b05": [ 0, 0, 0, 4 ],
"e35af382": [ 9, 8, 8, 9 ],
"c8dd5d18": [ 8, 10, 8, 6 ],
"22c00b72": [ 0, 2, 0, 0 ],
"db8f674b": [ 5, 3, 6, 3 ]
},
"animations": {
"7": {}
},
"shadows": {
"306900d": null
},
"cursors": {
"5c13d641": "default",
"e81f3e3d": "pointer"
},
"dimensions": {
"149": 7,
"5e": 2,
"582": 30,
"1d6": 10,
"6fa": 38,
"8d": 3,
"3db": 21,
"0": 0,
"bc": 4,
"2f0": 16,
"eb": 5,
"3ac": 20
}
} );
ts.setThemeCssValues( "org.eclipse.swt.theme.Fallback", {
"Spinner-DownButton": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"DateTime-DropDownButton": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"CoolBar": {
"background-image": [ [ [], "a505df1b" ] ]
},
"TreeColumn": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border-bottom": [ [ [], "dee0ab31" ] ],
"padding": [ [ [], "c8dd5d18" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"ScrollBar-UpButton": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [ "[VERTICAL" ], "6edf2b8a" ], [ [ "[HORIZONTAL" ], "3aee7904" ], [ [], "a505df1b" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"ExpandItem-Button": {
"background-image": [ [ [ ":expanded" ], "6cbccb98" ], [ [], "50cab11f" ] ]
},
"Spinner-UpButton": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"Button-ArrowIcon": {
"background-image": [ [ [ "[RIGHT" ], "9f435c8c" ], [ [ "[LEFT" ], "5c00d24b" ], [ [ "[DOWN" ], "c94fc8ad" ], [ [ "[UP" ], "1d0cc994" ], [ [], "a505df1b" ] ]
},
"Group": {
"background-color": [ [ [], "4004dc1c" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"font": [ [ [], "3a7ce581" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ]
},
"Label-SeparatorLine": {
"border": [ [ [ "[SHADOW_OUT" ], "c411a436" ], [ [ "[SHADOW_IN" ], "5fbe2ff" ], [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-color": [ [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"width": [ [ [], "5e" ] ]
},
"Slider-UpButton": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"padding": [ [ [], "2144df1c" ] ]
},
"DateTime": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"padding": [ [ [], "2144df1c" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Slider-DownButton": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"padding": [ [ [], "2144df1c" ] ]
},
"Tree-Cell": {
"padding": [ [ [], "bf9bfec1" ] ],
"spacing": [ [ [], "0" ] ]
},
"CCombo-Button": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"Shell": {
"border": [ [ [ ":inactive", "[TITLE" ], "dee0ab31" ], [ [ ":inactive", "[BORDER" ], "dee0ab31" ], [ [ ":maximized", "[BORDER" ], "36a" ], [ [ "[BORDER" ], "dee0ab31" ], [ [ "[TITLE" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [ "[BORDER" ], "655c8929" ], [ [ "[TITLE" ], "655c8929" ], [ [], "2144df1c" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"opacity": [ [ [], "1.0" ] ],
"box-shadow": [ [ [], "306900d" ] ],
"animation": [ [ [], "7" ] ]
},
"Combo": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"ExpandItem-Header": {
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "e81f3e3d" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Combo-Button": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "e0c04a17" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"ToolItem-DropDownIcon": {
"background-image": [ [ [], "110f0331" ] ],
"border": [ [ [], "36a" ] ]
},
"TabFolder-ContentContainer": {
"border": [ [ [], "dee0ab31" ] ]
},
"DateTime-Calendar-Day": {
"color": [ [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"TableItem": {
"color": [ [ [ ":selected" ], "4004dc1c" ], [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [ ":selected" ], "3fefb044" ], [ [ ":hover" ], "3ffb5a34" ], [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"text-decoration": [ [ [], "none" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"*": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"padding": [ [ [], "2144df1c" ] ]
},
"Tree-GridLine": {
"color": [ [ [ ":horizontal" ], "ffffffff" ], [ [], "40009eb8" ] ]
},
"CCombo-Button-Icon": {
"background-image": [ [ [], "56abe5a7" ] ]
},
"Combo-Button-Icon": {
"background-image": [ [ [], "7d708cd9" ] ]
},
"DateTime-UpButton-Icon": {
"background-image": [ [ [], "d886e9d6" ] ]
},
"Sash": {
"background-color": [ [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ]
},
"TabItem": {
"background-color": [ [ [ ":selected" ], "3fff9798" ], [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border-top-color": [ [ [], "3ff9f068" ] ],
"border-bottom-color": [ [ [], "3ff9f068" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"padding": [ [ [ ":selected" ], "a680cf80" ], [ [], "bf9bfec1" ] ]
},
"ToolItem-Separator": {
"width": [ [ [], "bc" ] ]
},
"TableColumn-SortIndicator": {
"background-image": [ [ [ ":down" ], "2fc16aa8" ], [ [ ":up" ], "5b8ba614" ], [ [], "a505df1b" ] ]
},
"ToolTip-Text": {
"color": [ [ [], "3fed9ae8" ] ],
"font": [ [ [], "18a06a3" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"ScrollBar": {
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"width": [ [ [], "1d6" ] ]
},
"ProgressBar": {
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "dd851a27" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"Group-Frame": {
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"margin": [ [ [], "e9929b96" ] ],
"padding": [ [ [], "b1369cc8" ] ]
},
"TreeColumn-SortIndicator": {
"background-image": [ [ [ ":down" ], "2fc16aa8" ], [ [ ":up" ], "5b8ba614" ], [ [], "a505df1b" ] ]
},
"Shell-MinButton": {
"margin": [ [ [ ":inactive" ], "9e1e81c6" ], [ [], "9e1e81c6" ] ],
"background-image": [ [ [ ":inactive" ], "63a3ac85" ], [ [], "63a3ac85" ] ]
},
"TableColumn": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border-bottom": [ [ [], "dee0ab31" ] ],
"padding": [ [ [], "b7669218" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"ToolTip-Image": {
"background-image": [ [ [ "[ICON_WARNING" ], "1888a858" ], [ [ "[ICON_INFORMATION" ], "d19cc93d" ], [ [ "[ICON_ERROR" ], "627301a4" ], [ [], "a505df1b" ] ]
},
"ScrollBar-Thumb-Icon": {
"background-image": [ [ [], "a505df1b" ] ]
},
"CTabItem": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [ ":selected" ], "3fed9ae8" ], [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [ ":selected" ], "3fff9798" ], [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [], "c9967dea" ] ],
"spacing": [ [ [], "1d6" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Combo-Field": {
"padding": [ [ [], "86afb7a3" ] ]
},
"Table": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"background-image": [ [ [], "a505df1b" ] ]
},
"Text-Message": {
"color": [ [ [], "3fed9ae8" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Slider-DownButton-Icon": {
"background-image": [ [ [ "[VERTICAL" ], "5be1033a" ], [ [ "[HORIZONTAL" ], "17352e6f" ], [ [], "a505df1b" ] ]
},
"Spinner-Field": {
"padding": [ [ [], "bf374b8e" ] ]
},
"Tree": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ]
},
"Widget-ToolTip": {
"color": [ [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [], "2ce1a471" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"opacity": [ [ [], "1.0" ] ],
"animation": [ [ [], "7" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"DateTime-Calendar-PreviousYearButton": {
"background-image": [ [ [], "89265bb2" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"List-Item": {
"color": [ [ [ ":selected" ], "4004dc1c" ], [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [ ":selected" ], "3fe4aeec" ], [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"padding": [ [ [], "bf374b8e" ] ]
},
"Scale-Thumb": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"FileUpload": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "1.0" ] ],
"animation": [ [ [], "7" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"padding": [ [ [], "c9967dea" ] ],
"spacing": [ [ [], "5e" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Link": {
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Button-CheckIcon": {
"background-image": [ [ [ ":grayed", ":selected" ], "fea0bb8" ], [ [ ":selected" ], "3655dc8d" ], [ [], "903dd6ca" ] ]
},
"Table-GridLine": {
"color": [ [ [ ":horizontal" ], "ffffffff" ], [ [], "40009eb8" ] ]
},
"Shell-CloseButton": {
"margin": [ [ [ ":inactive" ], "9e1e81c6" ], [ [], "9e1e81c6" ] ],
"background-image": [ [ [ ":inactive" ], "4cdd7012" ], [ [], "4cdd7012" ] ]
},
"DateTime-DownButton-Icon": {
"background-image": [ [ [], "306e0068" ] ]
},
"Label": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"text-decoration": [ [ [], "none" ] ],
"opacity": [ [ [], "1.0" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Shell-Titlebar": {
"color": [ [ [], "4004dc1c" ] ],
"background-color": [ [ [ ":inactive" ], "3ff3d584" ], [ [], "3fe4f000" ] ],
"background-gradient-color": [ [ [ ":inactive" ], "3ff3d584" ], [ [], "3fe4f000" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"margin": [ [ [], "2144df1c" ] ],
"padding": [ [ [], "cc06b3d4" ] ],
"height": [ [ [], "6fa" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"Tree-Indent": {
"width": [ [ [], "2f0" ] ],
"background-image": [ [ [ ":expanded", ":first", ":last" ], "d282b4e5" ], [ [ ":collapsed", ":first", ":last" ], "4ee00e3c" ], [ [ ":first", ":last" ], "a505df1b" ], [ [ ":expanded", ":last" ], "d282b4e5" ], [ [ ":collapsed", ":last" ], "4ee00e3c" ], [ [ ":expanded", ":first" ], "d282b4e5" ], [ [ ":collapsed", ":first" ], "4ee00e3c" ], [ [ ":last" ], "a505df1b" ], [ [ ":first" ], "a505df1b" ], [ [ ":line" ], "a505df1b" ], [ [ ":expanded" ], "d282b4e5" ], [ [ ":collapsed" ], "4ee00e3c" ], [ [], "a505df1b" ] ]
},
"Combo-List": {
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"Combo-FocusIndicator": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [], "36a" ] ],
"margin": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "0.0" ] ]
},
"DateTime-DownButton": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"Link-Hyperlink": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fe42cec" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Spinner": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"padding": [ [ [], "2144df1c" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"MenuItem-CascadeIcon": {
"background-image": [ [ [], "eb8ac0f1" ] ]
},
"ToolItem": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [ ":selected" ], "dee0ab31" ], [ [ ":pressed" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"animation": [ [ [], "7" ] ],
"spacing": [ [ [], "bc" ] ],
"padding": [ [ [ ":selected" ], "e35af382" ], [ [ ":pressed" ], "e35af382" ], [ [], "2ce1a471" ] ],
"opacity": [ [ [], "1.0" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"CTabFolder": {
"border-color": [ [ [], "3ff9f068" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"Tree-Checkbox": {
"margin": [ [ [], "22c00b72" ] ],
"background-image": [ [ [ ":checked", ":grayed" ], "fea0bb8" ], [ [ ":checked" ], "3655dc8d" ], [ [], "903dd6ca" ] ]
},
"FileUpload-FocusIndicator": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [], "2b4cf00b" ] ],
"padding": [ [ [], "2144df1c" ] ],
"margin": [ [ [], "54f1c057" ] ],
"opacity": [ [ [], "1.0" ] ]
},
"Menu": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "1.0" ] ],
"padding": [ [ [], "2144df1c" ] ],
"animation": [ [ [], "7" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"TabFolder": {
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"font": [ [ [], "3a7cfc11" ] ]
},
"Button": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [ "[ARROW" ], "dee0ab31" ], [ [ "[BORDER" ], "dee0ab31" ], [ [ "[TOGGLE" ], "dee0ab31" ], [ [ "[PUSH" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "1.0" ] ],
"animation": [ [ [], "7" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"padding": [ [ [ "[RADIO" ], "8393ccd2" ], [ [ "[CHECK" ], "8393ccd2" ], [ [ "[ARROW" ], "ca2ee18a" ], [ [], "c9967dea" ] ],
"spacing": [ [ [ "[RADIO" ], "149" ], [ [ "[CHECK" ], "149" ], [ [], "5e" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"CLabel": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [], "bf9bfec1" ] ],
"spacing": [ [ [], "eb" ] ],
"opacity": [ [ [], "1.0" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"MenuItem-CheckIcon": {
"background-image": [ [ [], "144ea8f9" ] ]
},
"Scale": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"ToolTip": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"opacity": [ [ [], "1.0" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"padding": [ [ [], "2ce1a471" ] ],
"animation": [ [ [], "7" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"Slider-Thumb": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "2ed10572" ] ]
},
"CCombo-List": {
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"ScrollBar-DownButton-Icon": {
"background-image": [ [ [], "a505df1b" ] ]
},
"Display": {
"rwt-shadow-color": [ [ [], "3ff98c68" ] ],
"rwt-highlight-color": [ [ [], "4004dc1c" ] ],
"rwt-darkshadow-color": [ [ [], "3ff53320" ] ],
"rwt-lightshadow-color": [ [ [], "40006240" ] ],
"rwt-thinborder-color": [ [ [], "3ffa2a74" ] ],
"rwt-selectionmarker-color": [ [ [], "400457b0" ] ],
"rwt-infobackground-color": [ [ [], "4004dc1c" ] ],
"rwt-error-image": [ [ [], "994dccc6" ] ],
"rwt-information-image": [ [ [], "2c4058a5" ] ],
"rwt-working-image": [ [ [], "2c4058a5" ] ],
"rwt-question-image": [ [ [], "83e441e9" ] ],
"rwt-warning-image": [ [ [], "aa96e44e" ] ],
"rwt-fontlist": [ [ [], "67d9b899" ] ],
"background-image": [ [ [], "3c7d3330" ] ],
"font": [ [ [], "3a7cfc11" ] ]
},
"ToolBar": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [], "2144df1c" ] ],
"spacing": [ [ [], "0" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "1.0" ] ]
},
"ExpandBar": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"Button-RadioIcon": {
"background-image": [ [ [ ":selected" ], "46d9d135" ], [ [], "c290fe4b" ] ]
},
"DateTime-DropDownCalendar": {
"border": [ [ [], "dee0ab31" ] ]
},
"DateTime-Field": {
"color": [ [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"padding": [ [ [], "db8f674b" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"DateTime-Calendar-NextYearButton": {
"background-image": [ [ [], "2d244d58" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"FormText": {
"color": [ [ [], "3fe41900" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"font": [ [ [], "e7de1195" ] ],
"border": [ [ [ "[BORDER" ], "5fbe325" ], [ [], "36a" ] ]
},
"ScrollBar-UpButton-Icon": {
"background-image": [ [ [], "a505df1b" ] ]
},
"DateTime-Calendar-NextMonthButton": {
"background-image": [ [ [], "1faf9af7" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"CCombo": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"ScrollBar-Thumb": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "bf671604" ] ],
"min-height": [ [ [], "3ac" ] ]
},
"CCombo-FocusIndicator": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [], "36a" ] ],
"margin": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "0.0" ] ]
},
"Composite": {
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [], "2144df1c" ] ],
"opacity": [ [ [], "1.0" ] ],
"box-shadow": [ [ [], "306900d" ] ],
"animation": [ [ [], "7" ] ]
},
"MenuItem-RadioIcon": {
"background-image": [ [ [], "d218be7e" ] ]
},
"Slider-UpButton-Icon": {
"background-image": [ [ [ "[VERTICAL" ], "827a3489" ], [ [ "[HORIZONTAL" ], "a30b8da1" ], [ [], "a505df1b" ] ]
},
"Group-Label": {
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"padding": [ [ [], "543919dd" ] ],
"margin": [ [ [], "a35b8659" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"color": [ [ [], "3fed9ae8" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"Table-Checkbox": {
"width": [ [ [], "3db" ] ],
"margin": [ [ [], "26291b05" ] ],
"background-image": [ [ [ ":checked", ":grayed" ], "fea0bb8" ], [ [ ":checked" ], "3655dc8d" ], [ [], "903dd6ca" ] ]
},
"TreeItem": {
"color": [ [ [ ":selected" ], "4004dc1c" ], [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [ ":selected" ], "3fefb044" ], [ [ ":hover" ], "3ffb5a34" ], [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"text-decoration": [ [ [], "none" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"DateTime-DropDownButton-Icon": {
"background-image": [ [ [], "d1516768" ] ]
},
"Slider": {
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"Browser": {
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ]
},
"DateTime-Calendar-Navbar": {
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"CoolItem-Handle": {
"border": [ [ [], "dee0ab31" ] ],
"width": [ [ [], "5e" ] ]
},
"ToolTip-Message": {
"color": [ [ [], "3fed9ae8" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"text-shadow": [ [ [], "306900d" ] ]
},
"ScrollBar-DownButton": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [ "[VERTICAL" ], "ba1d7000" ], [ [ "[HORIZONTAL" ], "fb90316c" ], [ [], "a505df1b" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"List": {
"font": [ [ [], "3a7cfc11" ] ],
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ]
},
"DateTime-UpButton": {
"background-image": [ [ [], "a505df1b" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"border": [ [ [], "36a" ] ],
"border-left": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"cursor": [ [ [], "5c13d641" ] ],
"width": [ [ [], "582" ] ]
},
"Shell-DisplayOverlay": {
"background-color": [ [ [], "3ff48b00" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"animation": [ [ [], "7" ] ],
"opacity": [ [ [], "0.2" ] ]
},
"Button-FocusIndicator": {
"background-color": [ [ [], "ffffffff" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "2b4cf00b" ] ],
"padding": [ [ [], "2144df1c" ] ],
"margin": [ [ [], "54f1c057" ] ],
"opacity": [ [ [], "1.0" ] ]
},
"Sash-Handle": {
"background-image": [ [ [], "a505df1b" ] ]
},
"CTabFolder-DropDownButton-Icon": {
"background-image": [ [ [], "fa5334b" ] ]
},
"DateTime-Calendar-PreviousMonthButton": {
"background-image": [ [ [], "5fd49387" ] ],
"cursor": [ [ [], "5c13d641" ] ]
},
"Table-RowOverlay": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "ffffffff" ] ],
"background-color": [ [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ]
},
"Spinner-DownButton-Icon": {
"background-image": [ [ [], "d1516768" ] ]
},
"Tree-RowOverlay": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "ffffffff" ] ],
"background-color": [ [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ]
},
"Table-Cell": {
"padding": [ [ [], "c93ac8a5" ] ],
"spacing": [ [ [], "8d" ] ]
},
"Text": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "4004dc1c" ] ],
"font": [ [ [], "3a7cfc11" ] ],
"border": [ [ [ "[BORDER" ], "dee0ab31" ], [ [], "36a" ] ],
"border-radius": [ [ [], "2144df1c" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"padding": [ [ [ "[MULTI" ], "86afb7a3" ], [ [], "86afb7a3" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"box-shadow": [ [ [], "306900d" ] ]
},
"MenuItem": {
"color": [ [ [ ":disabled" ], "3ffeb15c" ], [ [], "3fed9ae8" ] ],
"background-color": [ [ [], "ffffffff" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"opacity": [ [ [], "1.0" ] ],
"text-shadow": [ [ [], "306900d" ] ],
"padding": [ [ [ ":onMenuBar" ], "27a454c8" ], [ [ "[SEPARATOR" ], "cc06b3d4" ], [ [], "2708e187" ] ]
},
"ProgressBar-Indicator": {
"background-color": [ [ [ ":error" ], "4001abb4" ], [ [ ":paused" ], "3ffb5a34" ], [ [], "3fe4aeec" ] ],
"background-image": [ [ [], "a505df1b" ] ],
"border": [ [ [], "36a" ] ],
"opacity": [ [ [], "1.0" ] ]
},
"ExpandItem": {
"border": [ [ [], "dee0ab31" ] ],
"border-radius": [ [ [], "2144df1c" ] ]
},
"CCombo-Field": {
"padding": [ [ [], "86afb7a3" ] ]
},
"Shell-MaxButton": {
"margin": [ [ [ ":inactive" ], "9e1e81c6" ], [ [], "9e1e81c6" ] ],
"background-image": [ [ [ ":inactive", ":maximized" ], "679d075a" ], [ [ ":maximized" ], "679d075a" ], [ [ ":inactive" ], "60504cca" ], [ [], "60504cca" ] ]
},
"Spinner-UpButton-Icon": {
"background-image": [ [ [], "49104d78" ] ]
}
}, true );
} )( org.eclipse.swt.theme.ThemeStore.getInstance() );

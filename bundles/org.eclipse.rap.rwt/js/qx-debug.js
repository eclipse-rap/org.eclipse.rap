if(!window.qxsettings)qxsettings={};
if(qxsettings["qx.theme"]==undefined)qxsettings["qx.theme"]="org.eclipse.swt.theme.Default";
if(qxsettings["qx.logAppender"]==undefined)qxsettings["qx.logAppender"]="qx.log.appender.Native";
if(qxsettings["qx.version"]==undefined)qxsettings["qx.version"]="0.7.0 (r9276) [debug]";
if(qxsettings["qx.isSource"]==undefined)qxsettings["qx.isSource"]=false;
if(!window.qxvariants)qxvariants={};
qxvariants["qx.compatibility"]="off";
qxvariants["qx.debug"]="on";



/* ID: qx.core.Bootstrap */
qx={Class:{createNamespace:function(name,
object){var splits=name.split(".");
var parent=window;
var part=splits[0];
for(var i=0,
len=splits.length-1;i<len;i++,
part=splits[i]){if(!parent[part]){parent=parent[part]={};
}else{parent=parent[part];
}}parent[part]=object;
return part;
},
define:function(name,
config){if(!config){var config={statics:{}};
}this.createNamespace(name,
config.statics);
if(config.defer){config.defer(config.statics);
}qx.core.Bootstrap.__registry[name]=config.statics;
}}};
qx.Class.define("qx.core.Bootstrap",
{statics:{LOADSTART:new Date,
time:function(){return new Date().getTime();
},
since:function(){return this.time()-this.LOADSTART;
},
__registry:{}}});




/* ID: qx.lang.Core */
qx.Class.define("qx.lang.Core");
if(!Error.prototype.toString||Error.prototype.toString()=="[object Error]"){Error.prototype.toString=function(){return this.message;
};
}if(!Array.prototype.indexOf){Array.prototype.indexOf=function(searchElement,
fromIndex){if(fromIndex==null){fromIndex=0;
}else if(fromIndex<0){fromIndex=Math.max(0,
this.length+fromIndex);
}
for(var i=fromIndex;i<this.length;i++){if(this[i]===searchElement){return i;
}}return -1;
};
}
if(!Array.prototype.lastIndexOf){Array.prototype.lastIndexOf=function(searchElement,
fromIndex){if(fromIndex==null){fromIndex=this.length-1;
}else if(fromIndex<0){fromIndex=Math.max(0,
this.length+fromIndex);
}
for(var i=fromIndex;i>=0;i--){if(this[i]===searchElement){return i;
}}return -1;
};
}
if(!Array.prototype.forEach){Array.prototype.forEach=function(callback,
obj){var l=this.length;
for(var i=0;i<l;i++){callback.call(obj,
this[i],
i,
this);
}};
}
if(!Array.prototype.filter){Array.prototype.filter=function(callback,
obj){var l=this.length;
var res=[];
for(var i=0;i<l;i++){if(callback.call(obj,
this[i],
i,
this)){res.push(this[i]);
}}return res;
};
}
if(!Array.prototype.map){Array.prototype.map=function(callback,
obj){var l=this.length;
var res=[];
for(var i=0;i<l;i++){res.push(callback.call(obj,
this[i],
i,
this));
}return res;
};
}
if(!Array.prototype.some){Array.prototype.some=function(callback,
obj){var l=this.length;
for(var i=0;i<l;i++){if(callback.call(obj,
this[i],
i,
this)){return true;
}}return false;
};
}
if(!Array.prototype.every){Array.prototype.every=function(callback,
obj){var l=this.length;
for(var i=0;i<l;i++){if(!callback.call(obj,
this[i],
i,
this)){return false;
}}return true;
};
}if(!String.prototype.quote){String.prototype.quote=function(){return '"'+this.replace(/\\/g,
"\\\\").replace(/\"/g,
"\\\"")+'"';
};
}




/* ID: qx.lang.Generics */
qx.Class.define("qx.lang.Generics",
{statics:{__map:{"Array":["join",
"reverse",
"sort",
"push",
"pop",
"shift",
"unshift",
"splice",
"concat",
"slice",
"indexOf",
"lastIndexOf",
"forEach",
"map",
"filter",
"some",
"every"],
"String":["quote",
"substring",
"toLowerCase",
"toUpperCase",
"charAt",
"charCodeAt",
"indexOf",
"lastIndexOf",
"toLocaleLowerCase",
"toLocaleUpperCase",
"localeCompare",
"match",
"search",
"replace",
"split",
"substr",
"concat",
"slice"]},
__wrap:function(obj,
func){return function(s){return obj.prototype[func].apply(s,
Array.prototype.slice.call(arguments,
1));
};
},
__init:function(){var map=qx.lang.Generics.__map;
for(var key in map){var obj=window[key];
var arr=map[key];
for(var i=0,
l=arr.length;i<l;i++){var func=arr[i];
if(!obj[func]){obj[func]=qx.lang.Generics.__wrap(obj,
func);
}}}}},
defer:function(statics){statics.__init();
}});




/* ID: qx.core.Log */
qx.Class.define("qx.core.Log",
{statics:{log:function(varargs){this._write(arguments,
"");
},
debug:function(varargs){this._write(arguments,
"debug");
},
info:function(varargs){this._write(arguments,
"info");
},
warn:function(varargs){this._write(arguments,
"warning");
},
error:function(varargs){this._write(arguments,
"error");
},
clear:function(){if(this._frame){this._frame.innerHTML="";
}},
open:function(){if(!this._frame){this._create();
}this._frame.style.display="";
},
close:function(){if(!this._frame){this._create();
}this._frame.style.display="none";
},
emu:true,
_unsupported:function(){this.warn("This method is not supported.");
},
_map:{debug:"blue",
info:"green",
warning:"orange",
error:"red"},
_cache:[],
_write:function(args,
level){if(!this._frame){this._create();
}
if(!this._frame){this._cache.push(arguments);
return;
}var important=level=="warning"||level=="error";
var node=document.createElement("div");
var sty=node.style;
sty.borderBottom="1px solid #CCC";
sty.padding="1px 8px";
sty.margin="1px 0";
sty.color=this._map[level]||"blue";
if(important){sty.background="#FFFFE0";
}
for(var i=0,
l=args.length;i<l;i++){node.appendChild(document.createTextNode(args[i]));
if(i<l-1){node.appendChild(document.createTextNode(", "));
}}this._frame.appendChild(node);
this._frame.scrollTop=this._frame.scrollHeight;
if(important){this.open();
}},
_create:function(){if(!document.body){return;
}var frame=this._frame=document.createElement("div");
frame.className="console";
var sty=frame.style;
sty.zIndex="2147483647";
sty.background="white";
sty.position="absolute";
sty.display="none";
sty.width="100%";
sty.height="200px";
sty.left=sty.right=sty.bottom=0;
sty.borderTop="3px solid #134275";
sty.overflow="auto";
sty.font='10px normal Consolas, "Bitstream Vera Sans Mono", "Courier New", monospace';
sty.color="blue";
document.body.appendChild(frame);
if(this._cache){for(var i=0,
c=this._cache,
l=c.length;i<l;i++){this._write(c[i][0],
c[i][1]);
}this._cache=null;
}}},
defer:function(statics,
members,
properties){statics.assert=statics.dir=statics.dirxml=statics.group=statics.groupEnd=statics.time=statics.timeEnd=statics.count=statics.trance=statics.profile=statics.profileEnd=statics._unsupported;
if(!window.console){window.console=statics;
}else if(window.console&&(!console.debug||!console.trace||!console.group)){window.console=statics;
}}});




/* ID: qx.core.Setting */
qx.Class.define("qx.core.Setting",
{statics:{__settings:{},
define:function(key,
defaultValue){if(defaultValue===undefined){throw new Error('Default value of setting "'+key+'" must be defined!');
}
if(!this.__settings[key]){this.__settings[key]={};
}else if(this.__settings[key].defaultValue!==undefined){throw new Error('Setting "'+key+'" is already defined!');
}this.__settings[key].defaultValue=defaultValue;
},
get:function(key){var cache=this.__settings[key];
if(cache===undefined){throw new Error('Setting "'+key+'" is not defined.');
}
if(cache.defaultValue===undefined){throw new Error('Setting "'+key+'" is not supported by API.');
}
if(cache.value!==undefined){return cache.value;
}return cache.defaultValue;
},
__init:function(){if(window.qxsettings){for(var key in qxsettings){if((key.split(".")).length!==2){throw new Error('Malformed settings key "'+key+'". Must be following the schema "namespace.key".');
}
if(!this.__settings[key]){this.__settings[key]={};
}this.__settings[key].value=qxsettings[key];
}window.qxsettings=undefined;
try{delete window.qxsettings;
}catch(ex){}this.__loadUrlSettings();
}},
__loadUrlSettings:function(){if(this.get("qx.allowUrlSettings")!=true){return;
}var urlSettings=document.location.search.slice(1).split("&");
for(var i=0;i<urlSettings.length;i++){var setting=urlSettings[i].split(":");
if(setting.length!=3||setting[0]!="qxsetting"){continue;
}var key=setting[1];
if(!this.__settings[key]){this.__settings[key]={};
}this.__settings[key].value=decodeURIComponent(setting[2]);
}}},
defer:function(statics){statics.define("qx.allowUrlSettings",
true);
statics.__init();
}});




/* ID: qx.lang.Array */
qx.Class.define("qx.lang.Array",
{statics:{fromArguments:function(args){return Array.prototype.slice.call(args,
0);
},
fromCollection:function(coll){return Array.prototype.slice.call(coll,
0);
},
fromShortHand:function(input){var len=input.length;
if(len>4||len==0){this.error("Invalid number of arguments!");
}var result=qx.lang.Array.copy(input);
switch(len){case 1:result[1]=result[2]=result[3]=result[0];
break;
case 2:result[2]=result[0];
case 3:result[3]=result[1];
}return result;
},
copy:function(arr){return arr.concat();
},
clone:function(arr){return arr.concat();
},
getLast:function(arr){return arr[arr.length-1];
},
getFirst:function(arr){return arr[0];
},
insertAt:function(arr,
obj,
i){arr.splice(i,
0,
obj);
return arr;
},
insertBefore:function(arr,
obj,
obj2){var i=arr.indexOf(obj2);
if(i==-1){arr.push(obj);
}else{arr.splice(i,
0,
obj);
}return arr;
},
insertAfter:function(arr,
obj,
obj2){var i=arr.indexOf(obj2);
if(i==-1||i==(arr.length-1)){arr.push(obj);
}else{arr.splice(i+1,
0,
obj);
}return arr;
},
removeAt:function(arr,
i){return arr.splice(i,
1)[0];
},
removeAll:function(arr){return arr.length=0;
},
append:function(arr,
a){{if(!(typeof (a)=="object"&&a instanceof Array)){throw new Error("The second parameter must be an array!");
}};
Array.prototype.push.apply(arr,
a);
return arr;
},
remove:function(arr,
obj){var i=arr.indexOf(obj);
if(i!=-1){arr.splice(i,
1);
return obj;
}},
contains:function(arr,
obj){return arr.indexOf(obj)!=-1;
}}});




/* ID: qx.core.Variant */
qx.Class.define("qx.core.Variant",
{statics:{__variants:{},
__cache:{},
compilerIsSet:function(){return true;
},
define:function(key,
allowedValues,
defaultValue){{if(!this.__isValidArray(allowedValues)){throw new Error('Allowed values of variant "'+key+'" must be defined!');
}
if(defaultValue===undefined){throw new Error('Default value of variant "'+key+'" must be defined!');
}};
if(!this.__variants[key]){this.__variants[key]={};
}else{if(this.__variants[key].defaultValue!==undefined){throw new Error('Variant "'+key+'" is already defined!');
}}this.__variants[key].allowedValues=allowedValues;
this.__variants[key].defaultValue=defaultValue;
},
get:function(key){var data=this.__variants[key];
{if(data===undefined){throw new Error('Variant "'+key+'" is not defined.');
}
if(data.defaultValue===undefined){throw new Error('Variant "'+key+'" is not supported by API.');
}};
if(data.value!==undefined){return data.value;
}return data.defaultValue;
},
__init:function(){if(window.qxvariants){for(var key in qxvariants){{if((key.split(".")).length!==2){throw new Error('Malformed settings key "'+key+'". Must be following the schema "namespace.key".');
}};
if(!this.__variants[key]){this.__variants[key]={};
}this.__variants[key].value=qxvariants[key];
}window.qxvariants=undefined;
try{delete window.qxvariants;
}catch(ex){}this.__loadUrlVariants(this.__variants);
}},
__loadUrlVariants:function(){if(qx.core.Setting.get("qx.allowUrlSettings")!=true){return;
}var urlVariants=document.location.search.slice(1).split("&");
for(var i=0;i<urlVariants.length;i++){var variant=urlVariants[i].split(":");
if(variant.length!=3||variant[0]!="qxvariant"){continue;
}var key=variant[1];
if(!this.__variants[key]){this.__variants[key]={};
}this.__variants[key].value=decodeURIComponent(variant[2]);
}},
select:function(key,
variantFunctionMap){{if(!this.__isValidObject(this.__variants[key])){throw new Error("Variant \""+key+"\" is not defined");
}
if(!this.__isValidObject(variantFunctionMap)){throw new Error("the second parameter must be a map!");
}};
for(var variant in variantFunctionMap){if(this.isSet(key,
variant)){return variantFunctionMap[variant];
}}
if(variantFunctionMap["default"]!==undefined){return variantFunctionMap["default"];
}{throw new Error('No match for variant "'+key+'" in variants ['+qx.lang.Object.getKeysAsString(variantFunctionMap)+'] found, and no default ("default") given');
};
},
isSet:function(key,
variants){var access=key+"$"+variants;
if(this.__cache[access]!==undefined){return this.__cache[access];
}var retval=false;
if(variants.indexOf("|")<0){retval=this.get(key)===variants;
}else{var keyParts=variants.split("|");
for(var i=0,
l=keyParts.length;i<l;i++){if(this.get(key)===keyParts[i]){retval=true;
break;
}}}this.__cache[access]=retval;
return retval;
},
__isValidArray:function(v){return typeof v==="object"&&v!==null&&v instanceof Array;
},
__isValidObject:function(v){return typeof v==="object"&&v!==null&&!(v instanceof Array);
},
__arrayContains:function(arr,
obj){for(var i=0,
l=arr.length;i<l;i++){if(arr[i]==obj){return true;
}}return false;
}},
defer:function(statics){statics.define("qx.debug",
["on",
"off"],
"on");
statics.define("qx.compatibility",
["on",
"off"],
"on");
statics.define("qx.eventMonitorNoListeners",
["on",
"off"],
"off");
statics.define("qx.aspects",
["on",
"off"],
"off");
statics.__init();
}});




/* ID: qx.core.Client */
qx.Class.define("qx.core.Client",
{statics:{__init:function(){var vRunsLocally=window.location.protocol==="file:";
var vBrowserUserAgent=navigator.userAgent;
var vBrowserVendor=navigator.vendor;
var vBrowserProduct=navigator.product;
var vBrowserPlatform=navigator.platform;
var vBrowserModeHta=false;
var vBrowser;
var vEngine=null;
var vEngineVersion=null;
var vEngineVersionMajor=0;
var vEngineVersionMinor=0;
var vEngineVersionRevision=0;
var vEngineVersionBuild=0;
var vEngineEmulation=null;
var vEngineNightly=null;
var vVersionHelper;
if(window.opera&&/Opera[\s\/]([0-9\.]*)/.test(vBrowserUserAgent)){vEngine="opera";
vEngineVersion=RegExp.$1;
vBrowser="opera";
vEngineVersion=vEngineVersion.substring(0,
3)+"."+vEngineVersion.substring(3);
vEngineEmulation=vBrowserUserAgent.indexOf("MSIE")!==-1?"mshtml":vBrowserUserAgent.indexOf("Mozilla")!==-1?"gecko":null;
}else if(typeof vBrowserVendor==="string"&&vBrowserVendor==="KDE"&&/KHTML\/([0-9-\.]*)/.test(vBrowserUserAgent)){vEngine="khtml";
vBrowser="konqueror";
vEngineVersion=RegExp.$1;
}else if(vBrowserUserAgent.indexOf("AppleWebKit")!=-1&&/AppleWebKit\/([^ ]+)/.test(vBrowserUserAgent)){vEngine="webkit";
vEngineVersion=RegExp.$1;
vEngineNightly=vEngineVersion.indexOf("+")!=-1;
var invalidCharacter=RegExp("[^\\.0-9]").exec(vEngineVersion);
if(invalidCharacter){vEngineVersion=vEngineVersion.slice(0,
invalidCharacter.index);
}
if(vBrowserUserAgent.indexOf("Safari")!=-1){vBrowser="safari";
}else if(vBrowserUserAgent.indexOf("OmniWeb")!=-1){vBrowser="omniweb";
}else if(vBrowserUserAgent.indexOf("Shiira")!=-1){vBrowser="shiira";
}else if(vBrowserUserAgent.indexOf("NetNewsWire")!=-1){vBrowser="netnewswire";
}else if(vBrowserUserAgent.indexOf("RealPlayer")!=-1){vBrowser="realplayer";
}else{vBrowser="other webkit";
}
if(vEngineNightly){vBrowser+=" (nightly)";
}}else if(window.controllers&&typeof vBrowserProduct==="string"&&vBrowserProduct==="Gecko"&&/rv\:([^\);]+)(\)|;)/.test(vBrowserUserAgent)){vEngine="gecko";
vEngineVersion=RegExp.$1;
if(vBrowserUserAgent.indexOf("Firefox")!=-1){vBrowser="firefox";
}else if(vBrowserUserAgent.indexOf("Camino")!=-1){vBrowser="camino";
}else if(vBrowserUserAgent.indexOf("Galeon")!=-1){vBrowser="galeon";
}else{vBrowser="other gecko";
}}else if(/MSIE\s+([^\);]+)(\)|;)/.test(vBrowserUserAgent)){vEngine="mshtml";
vEngineVersion=RegExp.$1;
vBrowser="explorer";
vBrowserModeHta=!window.external;
}
if(vEngineVersion){vVersionHelper=vEngineVersion.split(".");
vEngineVersionMajor=vVersionHelper[0]||0;
vEngineVersionMinor=vVersionHelper[1]||0;
vEngineVersionRevision=vVersionHelper[2]||0;
vEngineVersionBuild=vVersionHelper[3]||0;
}var vEngineBoxSizingAttr=[];
switch(vEngine){case "gecko":vEngineBoxSizingAttr.push("-moz-box-sizing");
break;
case "khtml":vEngineBoxSizingAttr.push("-khtml-box-sizing");
break;
case "webkit":vEngineBoxSizingAttr.push("-khtml-box-sizing");
vEngineBoxSizingAttr.push("-webkit-box-sizing");
break;
case "mshtml":break;
default:break;
}vEngineBoxSizingAttr.push("box-sizing");
var vEngineQuirksMode=document.compatMode!=="CSS1Compat";
var vDefaultLocale="en";
var vBrowserLocale=(vEngine=="mshtml"?navigator.userLanguage:navigator.language).toLowerCase();
var vBrowserLocaleVariant=null;
var vBrowserLocaleVariantIndex=vBrowserLocale.indexOf("-");
if(vBrowserLocaleVariantIndex!=-1){vBrowserLocaleVariant=vBrowserLocale.substr(vBrowserLocaleVariantIndex+1);
vBrowserLocale=vBrowserLocale.substr(0,
vBrowserLocaleVariantIndex);
}var vPlatform="none";
var vPlatformWindows=false;
var vPlatformMacintosh=false;
var vPlatformUnix=false;
var vPlatformOther=false;
if(vBrowserPlatform.indexOf("Windows")!=-1||vBrowserPlatform.indexOf("Win32")!=-1||vBrowserPlatform.indexOf("Win64")!=-1){vPlatformWindows=true;
vPlatform="win";
}else if(vBrowserPlatform.indexOf("Macintosh")!=-1||vBrowserPlatform.indexOf("MacPPC")!=-1||vBrowserPlatform.indexOf("MacIntel")!=-1){vPlatformMacintosh=true;
vPlatform="mac";
}else if(vBrowserPlatform.indexOf("X11")!=-1||vBrowserPlatform.indexOf("Linux")!=-1||vBrowserPlatform.indexOf("BSD")!=-1){vPlatformUnix=true;
vPlatform="unix";
}else{vPlatformOther=true;
vPlatform="other";
}var vGfxVml=false;
var vGfxSvg=false;
var vGfxSvgBuiltin=false;
var vGfxSvgPlugin=false;
if(vEngine=="mshtml"){vGfxVml=true;
}if(document.implementation&&document.implementation.hasFeature){if(document.implementation.hasFeature("org.w3c.dom.svg",
"1.0")){vGfxSvg=vGfxSvgBuiltin=true;
}}this._runsLocally=vRunsLocally;
this._engineName=vEngine;
this._engineNameMshtml=vEngine==="mshtml";
this._engineNameGecko=vEngine==="gecko";
this._engineNameOpera=vEngine==="opera";
this._engineNameKhtml=vEngine==="khtml";
this._engineNameWebkit=vEngine==="webkit";
this._engineVersion=parseFloat(vEngineVersion);
this._engineVersionMajor=parseInt(vEngineVersionMajor);
this._engineVersionMinor=parseInt(vEngineVersionMinor);
this._engineVersionRevision=parseInt(vEngineVersionRevision);
this._engineVersionBuild=parseInt(vEngineVersionBuild);
this._engineQuirksMode=vEngineQuirksMode;
this._engineBoxSizingAttributes=vEngineBoxSizingAttr;
this._engineEmulation=vEngineEmulation;
this._browserName=vBrowser;
this._defaultLocale=vDefaultLocale;
this._browserPlatform=vPlatform;
this._browserPlatformWindows=vPlatformWindows;
this._browserPlatformMacintosh=vPlatformMacintosh;
this._browserPlatformUnix=vPlatformUnix;
this._browserPlatformOther=vPlatformOther;
this._browserModeHta=vBrowserModeHta;
this._browserLocale=vBrowserLocale;
this._browserLocaleVariant=vBrowserLocaleVariant;
this._gfxVml=vGfxVml;
this._gfxSvg=vGfxSvg;
this._gfxSvgBuiltin=vGfxSvgBuiltin;
this._gfxSvgPlugin=vGfxSvgPlugin;
this._fireBugActive=(window.console&&console.log&&console.debug&&console.assert);
this._supportsTextContent=(document.documentElement.textContent!==undefined);
this._supportsInnerText=(document.documentElement.innerText!==undefined);
this._supportsXPath=!!document.evaluate;
this._supportsElementExtensions=!!window.HTMLElement;
},
getRunsLocally:function(){return this._runsLocally;
},
getEngine:function(){return this._engineName;
},
getBrowser:function(){return this._browserName;
},
getVersion:function(){return this._engineVersion;
},
getMajor:function(){return this._engineVersionMajor;
},
getMinor:function(){return this._engineVersionMinor;
},
getRevision:function(){return this._engineVersionRevision;
},
getBuild:function(){return this._engineVersionBuild;
},
getEmulation:function(){return this._engineEmulation;
},
isMshtml:function(){return this._engineNameMshtml;
},
isGecko:function(){return this._engineNameGecko;
},
isOpera:function(){return this._engineNameOpera;
},
isKhtml:function(){return this._engineNameKhtml;
},
isWebkit:function(){return this._engineNameWebkit;
},
isSafari2:function(){return this._engineNameWebkit&&(this._engineVersion<420);
},
isInQuirksMode:function(){return this._engineQuirksMode;
},
getLocale:function(){return this._browserLocale;
},
getLocaleVariant:function(){return this._browserLocaleVariant;
},
getDefaultLocale:function(){return this._defaultLocale;
},
usesDefaultLocale:function(){return this._browserLocale===this._defaultLocale;
},
getEngineBoxSizingAttributes:function(){return this._engineBoxSizingAttributes;
},
getPlatform:function(){return this._browserPlatform;
},
runsOnWindows:function(){return this._browserPlatformWindows;
},
runsOnMacintosh:function(){return this._browserPlatformMacintosh;
},
runsOnUnix:function(){return this._browserPlatformUnix;
},
supportsVml:function(){return this._gfxVml;
},
supportsSvg:function(){return this._gfxSvg;
},
usesSvgBuiltin:function(){return this._gfxSvgBuiltin;
},
usesSvgPlugin:function(){return this._gfxSvgPlugin;
},
isFireBugActive:function(){return this._fireBugActive;
},
supportsTextContent:function(){return this._supportsTextContent;
},
supportsInnerText:function(){return this._supportsInnerText;
},
getInstance:function(){return this;
}},
defer:function(statics,
members,
properties){statics.__init();
qx.core.Variant.define("qx.client",
["gecko",
"mshtml",
"opera",
"webkit",
"khtml"],
qx.core.Client.getInstance().getEngine());
}});




/* ID: qx.lang.Object */
qx.Class.define("qx.lang.Object",
{statics:{isEmpty:function(map){for(var key in map){return false;
}return true;
},
hasMinLength:function(map,
length){var i=0;
for(var key in map){if((++i)>=length){return true;
}}return false;
},
getLength:function(map){var i=0;
for(var key in map){i++;
}return i;
},
_shadowedKeys:["isPrototypeOf",
"hasOwnProperty",
"toLocaleString",
"toString",
"valueOf"],
getKeys:qx.core.Variant.select("qx.client",
{"mshtml":function(map){var arr=[];
for(var key in map){arr.push(key);
}for(var i=0,
a=this._shadowedKeys,
l=a.length;i<l;i++){if(map.hasOwnProperty(a[i])){arr.push(a[i]);
}}return arr;
},
"default":function(map){var arr=[];
for(var key in map){arr.push(key);
}return arr;
}}),
getKeysAsString:function(map){var keys=qx.lang.Object.getKeys(map);
if(keys.length==0){return "";
}return '"'+keys.join('\", "')+'"';
},
getValues:function(map){var arr=[];
for(var key in map){arr.push(map[key]);
}return arr;
},
mergeWith:function(target,
source,
overwrite){if(overwrite===undefined){overwrite=true;
}
for(var key in source){if(overwrite||target[key]===undefined){target[key]=source[key];
}}return target;
},
carefullyMergeWith:function(target,
source){return qx.lang.Object.mergeWith(target,
source,
false);
},
merge:function(target,
varargs){var len=arguments.length;
for(var i=1;i<len;i++){qx.lang.Object.mergeWith(target,
arguments[i]);
}return target;
},
copy:function(source){var clone={};
for(var key in source){clone[key]=source[key];
}return clone;
},
invert:function(map){var result={};
for(var key in map){result[map[key].toString()]=key;
}return result;
},
getKeyFromValue:function(obj,
value){for(var key in obj){if(obj[key]===value){return key;
}}return null;
},
select:function(key,
map){return map[key];
},
fromArray:function(array){var obj={};
for(var i=0,
l=array.length;i<l;i++){{switch(typeof array[i]){case "object":case "function":case "undefined":throw new Error("Could not convert complex objects like "+array[i]+" at array index "+i+" to map syntax");
}};
obj[array[i].toString()]=true;
}return obj;
}}});




/* ID: qx.lang.String */
qx.Class.define("qx.lang.String",
{statics:{toCamelCase:function(string){return string.replace(/\-([a-z])/g,
function(match,
chr){return chr.toUpperCase();
});
},
trimLeft:function(str){return str.replace(/^\s+/,
"");
},
trimRight:function(str){return str.replace(/\s+$/,
"");
},
trim:function(str){return str.replace(/^\s+|\s+$/g,
"");
},
startsWith:function(fullstr,
substr){return !fullstr.indexOf(substr);
},
startsWithAlternate:function(fullstr,
substr){return fullstr.substring(0,
substr.length)===substr;
},
endsWith:function(fullstr,
substr){return fullstr.lastIndexOf(substr)===fullstr.length-substr.length;
},
endsWithAlternate:function(fullstr,
substr){return fullstr.substring(fullstr.length-substr.length,
fullstr.length)===substr;
},
pad:function(str,
length,
ch){if(typeof ch==="undefined"){ch="0";
}var temp="";
for(var i=str.length;i<length;i++){temp+=ch;
}return temp+str;
},
toFirstUp:function(str){return str.charAt(0).toUpperCase()+str.substr(1);
},
toFirstLower:function(str){return str.charAt(0).toLowerCase()+str.substr(1);
},
addListItem:function(str,
item,
sep){if(str==item||str==""){return item;
}
if(sep==null){sep=",";
}var a=str.split(sep);
if(a.indexOf(item)==-1){a.push(item);
return a.join(sep);
}else{return str;
}},
removeListItem:function(str,
item,
sep){if(str==item||str==""){return "";
}else{if(sep==null){sep=",";
}var a=str.split(sep);
var p=a.indexOf(item);
if(p===-1){return str;
}
do{a.splice(p,
1);
}while((p=a.indexOf(item))!=-1);
return a.join(sep);
}},
contains:function(str,
substring){return str.indexOf(substring)!=-1;
},
format:function(pattern,
args){var str=pattern;
for(var i=0;i<args.length;i++){str=str.replace(new RegExp("%"+(i+1),
"g"),
args[i]);
}return str;
},
escapeRegexpChars:function(str){return str.replace(/([\\\.\(\)\[\]\{\}\^\$\?\+\*])/g,
"\\$1");
},
toArray:function(str){return str.split(/\B|\b/g);
}}});




/* ID: qx.lang.Function */
qx.Class.define("qx.lang.Function",
{statics:{globalEval:function(data){if(window.execScript){window.execScript(data);
}else{eval.call(window,
data);
}},
returnTrue:function(){return true;
},
returnFalse:function(){return false;
},
returnNull:function(){return null;
},
returnThis:function(){return this;
},
returnInstance:function(){if(!this._instance){this._instance=new this;
}return this._instance;
},
returnZero:function(){return 0;
},
returnNegativeIndex:function(){return -1;
},
bind:function(fcn,
self,
varargs){{if(typeof fcn!=="function"){throw new Error("First parameter to bind() needs to be of type function!");
}
if(typeof self!=="object"){throw new Error("Second parameter to bind() needs to be of type object!");
}};
if(arguments.length>2){var args=Array.prototype.slice.call(arguments,
2);
var wrap=function(){fcn.context=self;
var ret=fcn.apply(self,
args.concat(qx.lang.Array.fromArguments(arguments)));
fcn.context=null;
return ret;
};
}else{var wrap=function(){fcn.context=self;
var ret=fcn.apply(self,
arguments);
fcn.context=null;
return ret;
};
}wrap.self=fcn.self?fcn.self.constructor:self;
return wrap;
},
bindEvent:function(fcn,
self){{if(typeof fcn!=="function"){throw new Error("First parameter to bindEvent() needs to be of type function!");
}
if(typeof self!=="object"){throw new Error("Second parameter to bindEvent() needs to be of type object!");
}};
var wrap=function(event){fcn.context=self;
var ret=fcn.call(self,
event||window.event);
fcn.context=null;
return ret;
};
wrap.self=fcn.self?fcn.self.constructor:self;
return wrap;
},
getCaller:function(args){return args.caller?args.caller.callee:args.callee.caller;
}}});




/* ID: qx.core.Aspect */
qx.Class.define("qx.core.Aspect",
{statics:{__registry:[],
wrap:function(fullName,
fcn,
type){if(!qx.core.Setting.get("qx.enableAspect")){return fcn;
}var before=[];
var after=[];
for(var i=0;i<this.__registry.length;i++){var aspect=this.__registry[i];
if(fullName.match(aspect.re)&&(type==aspect.type||aspect.type=="*")){var pos=aspect.pos;
if(pos=="before"){before.push(aspect.fcn);
}else{after.push(aspect.fcn);
}}}
if(before.length==0&&after.length==0){return fcn;
}var wrapper=function(){for(var i=0;i<before.length;i++){before[i].call(this,
fullName,
fcn,
type,
arguments);
}var ret=fcn.apply(this,
arguments);
for(var i=0;i<after.length;i++){after[i].call(this,
fullName,
fcn,
type,
arguments,
ret);
}return ret;
};
if(type!="static"){wrapper.self=fcn.self;
wrapper.base=fcn.base;
}fcn.wrapper=wrapper;
return wrapper;
},
addAdvice:function(position,
type,
nameRegExp,
fcn){if(position!="before"&&position!="after"){throw new Error("Unknown position: '"+position+"'");
}this.__registry.push({pos:position,
type:type,
re:nameRegExp,
fcn:fcn});
}},
defer:function(){qx.core.Setting.define("qx.enableAspect",
false);
}});




/* ID: qx.Class */
qx.Class.define("qx.Class",
{statics:{define:function(name,
config){if(!config){var config={};
}if(config.include&&!(config.include instanceof Array)){config.include=[config.include];
}if(config.implement&&!(config.implement instanceof Array)){config.implement=[config.implement];
}if(!config.hasOwnProperty("extend")&&!config.type){config.type="static";
}{this.__validateConfig(name,
config);
};
var clazz=this.__createClass(name,
config.type,
config.extend,
config.statics,
config.construct,
config.destruct);
if(config.extend){var superclass=config.extend;
if(config.properties){this.__addProperties(clazz,
config.properties,
true);
}if(config.members){this.__addMembers(clazz,
config.members,
true,
true);
}if(config.events){this.__addEvents(clazz,
config.events,
true);
}if(config.include){for(var i=0,
l=config.include.length;i<l;i++){this.__addMixin(clazz,
config.include[i],
false);
}}}if(config.settings){for(var key in config.settings){qx.core.Setting.define(key,
config.settings[key]);
}}if(config.variants){for(var key in config.variants){qx.core.Variant.define(key,
config.variants[key].allowedValues,
config.variants[key].defaultValue);
}}if(config.defer){config.defer.self=clazz;
config.defer(clazz,
clazz.prototype,
{add:function(name,
config){var properties={};
properties[name]=config;
qx.Class.__addProperties(clazz,
properties,
true);
}});
}if(config.implement){for(var i=0,
l=config.implement.length;i<l;i++){this.__addInterface(clazz,
config.implement[i]);
}}},
createNamespace:function(name,
object){var splits=name.split(".");
var parent=window;
var part=splits[0];
for(var i=0,
l=splits.length-1;i<l;i++,
part=splits[i]){if(!parent[part]){parent=parent[part]={};
}else{parent=parent[part];
}}{if(parent[part]!==undefined){throw new Error("An object of the name '"+name+"' already exists and overwriting is not allowed!");
}};
parent[part]=object;
return part;
},
isDefined:function(name){return this.getByName(name)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.__registry);
},
getByName:function(name){return this.__registry[name];
},
include:function(clazz,
mixin){{if(!mixin){throw new Error("Includes of mixins must be mixins. The mixin of class '"+clazz.classname+"' is undefined/null!");
}qx.Mixin.isCompatible(mixin,
clazz);
};
qx.Class.__addMixin(clazz,
mixin,
false);
},
patch:function(clazz,
mixin){{qx.Mixin.isCompatible(mixin,
clazz);
};
qx.Class.__addMixin(clazz,
mixin,
true);
},
isSubClassOf:function(clazz,
superClass){if(!clazz){return false;
}
if(clazz==superClass){return true;
}
if(clazz.prototype instanceof superClass){return true;
}return false;
},
getPropertyDefinition:function(clazz,
name){while(clazz){if(clazz.$$properties&&clazz.$$properties[name]){return clazz.$$properties[name];
}clazz=clazz.superclass;
}return null;
},
getByProperty:function(clazz,
name){while(clazz){if(clazz.$$properties&&clazz.$$properties[name]){return clazz;
}clazz=clazz.superclass;
}return null;
},
hasProperty:function(clazz,
name){return !!this.getPropertyDefinition(clazz,
name);
},
getEventType:function(clazz,
name){var clazz=clazz.constructor;
while(clazz.superclass){if(clazz.$$events&&clazz.$$events[name]!==undefined){return clazz.$$events[name];
}clazz=clazz.superclass;
}return null;
},
supportsEvent:function(clazz,
name){return !!this.getEventType(clazz,
name);
},
hasOwnMixin:function(clazz,
mixin){return clazz.$$includes&&clazz.$$includes.indexOf(mixin)!==-1;
},
getByMixin:function(clazz,
mixin){var list,
i,
l;
while(clazz){if(clazz.$$includes){list=clazz.$$flatIncludes;
for(i=0,
l=list.length;i<l;i++){if(list[i]===mixin){return clazz;
}}}clazz=clazz.superclass;
}return null;
},
getMixins:function(clazz){var list=[];
while(clazz){if(clazz.$$includes){list.push.apply(list,
clazz.$$flatIncludes);
}clazz=clazz.superclass;
}return list;
},
hasMixin:function(clazz,
mixin){return !!this.getByMixin(clazz,
mixin);
},
hasOwnInterface:function(clazz,
iface){return clazz.$$implements&&clazz.$$implements.indexOf(iface)!==-1;
},
getByInterface:function(clazz,
iface){var list,
i,
l;
while(clazz){if(clazz.$$implements){list=clazz.$$flatImplements;
for(i=0,
l=list.length;i<l;i++){if(list[i]===iface){return clazz;
}}}clazz=clazz.superclass;
}return null;
},
getInterfaces:function(clazz){var list=[];
while(clazz){if(clazz.$$implements){list.push.apply(list,
clazz.$$flatImplements);
}clazz=clazz.superclass;
}return list;
},
hasInterface:function(clazz,
iface){return !!this.getByInterface(clazz,
iface);
},
implementsInterface:function(clazz,
iface){if(this.hasInterface(clazz,
iface)){return true;
}
try{qx.Interface.assert(clazz,
iface,
false);
return true;
}catch(ex){}return false;
},
getInstance:function(){if(!this.$$instance){this.$$allowconstruct=true;
this.$$instance=new this;
delete this.$$allowconstruct;
}return this.$$instance;
},
genericToString:function(){return "[Class "+this.classname+"]";
},
__registry:qx.core.Bootstrap.__registry,
__allowedKeys:{"type":"string",
"extend":"function",
"implement":"object",
"include":"object",
"construct":"function",
"statics":"object",
"properties":"object",
"members":"object",
"settings":"object",
"variants":"object",
"events":"object",
"defer":"function",
"destruct":"function"},
__staticAllowedKeys:{"type":"string",
"statics":"object",
"settings":"object",
"variants":"object",
"defer":"function"},
__validateConfig:function(name,
config){if(config.type&&!(config.type==="static"||config.type==="abstract"||config.type==="singleton")){throw new Error('Invalid type "'+config.type+'" definition for class "'+name+'"!');
}var allowed=config.type==="static"?this.__staticAllowedKeys:this.__allowedKeys;
for(var key in config){if(!allowed[key]){throw new Error('The configuration key "'+key+'" in class "'+name+'" is not allowed!');
}
if(config[key]==null){throw new Error('Invalid key "'+key+'" in class "'+name+'"! The value is undefined/null!');
}
if(typeof config[key]!==allowed[key]){throw new Error('Invalid type of key "'+key+'" in class "'+name+'"! The type of the key must be "'+allowed[key]+'"!');
}}var maps=["statics",
"properties",
"members",
"settings",
"variants",
"events"];
for(var i=0,
l=maps.length;i<l;i++){var key=maps[i];
if(config[key]!==undefined&&(config[key] instanceof Array||config[key] instanceof RegExp||config[key] instanceof Date||config[key].classname!==undefined)){throw new Error('Invalid key "'+key+'" in class "'+name+'"! The value needs to be a map!');
}}if(config.include){if(config.include instanceof Array){for(var i=0,
a=config.include,
l=a.length;i<l;i++){if(a[i]==null||a[i].$$type!=="Mixin"){throw new Error('The include definition in class "'+name+'" contains an invalid mixin at position '+i+': '+a[i]);
}}}else{throw new Error('Invalid include definition in class "'+name+'"! Only mixins and arrays of mixins are allowed!');
}}if(config.implement){if(config.implement instanceof Array){for(var i=0,
a=config.implement,
l=a.length;i<l;i++){if(a[i]==null||a[i].$$type!=="Interface"){throw new Error('The implement definition in class "'+name+'" contains an invalid interface at position '+i+': '+a[i]);
}}}else{throw new Error('Invalid implement definition in class "'+name+'"! Only interfaces and arrays of interfaces are allowed!');
}}if(config.include){try{qx.Mixin.checkCompatibility(config.include);
}catch(ex){throw new Error('Error in include definition of class "'+name+'"! '+ex.message);
}}if(config.settings){for(var key in config.settings){if(key.substr(0,
key.indexOf("."))!=name.substr(0,
name.indexOf("."))){qx.log.Logger.ROOT_LOGGER.error('Forbidden setting "'+key+'" found in "'+name+'". It is forbidden to define a default setting for an external namespace!');
}}}if(config.variants){for(var key in config.variants){if(key.substr(0,
key.indexOf("."))!=name.substr(0,
name.indexOf("."))){throw new Error('Forbidden variant "'+key+'" found in "'+name+'". It is forbidden to define a variant for an external namespace!');
}}}},
__createClass:function(name,
type,
extend,
statics,
construct,
destruct){var clazz;
if(!extend&&qx.core.Variant.isSet("qx.aspects",
"off")){clazz=statics||{};
}else{clazz={};
if(extend){if(!construct){construct=this.__createDefaultConstructor();
}clazz=this.__wrapConstructor(construct,
name,
type);
}if(statics){var key;
for(var i=0,
a=qx.lang.Object.getKeys(statics),
l=a.length;i<l;i++){key=a[i];
if(qx.core.Variant.isSet("qx.aspects",
"on")){var staticValue=statics[key];
if(staticValue instanceof Function){staticValue=qx.core.Aspect.wrap(name+"."+key,
staticValue,
"static");
}clazz[key]=staticValue;
}else{clazz[key]=statics[key];
}}}}var basename=this.createNamespace(name,
clazz,
false);
clazz.name=clazz.classname=name;
clazz.basename=basename;
if(!clazz.hasOwnProperty("toString")){clazz.toString=this.genericToString;
}
if(extend){var superproto=extend.prototype;
var helper=this.__createEmptyFunction();
helper.prototype=superproto;
var proto=new helper;
clazz.prototype=proto;
proto.name=proto.classname=name;
proto.basename=basename;
construct.base=clazz.superclass=extend;
construct.self=clazz.constructor=proto.constructor=clazz;
if(destruct){if(qx.core.Variant.isSet("qx.aspects",
"on")){destruct=qx.core.Aspect.wrap(name,
destruct,
"destructor");
}clazz.$$destructor=destruct;
}}{};
this.__registry[name]=clazz;
return clazz;
},
__addEvents:function(clazz,
events,
patch){{if(!qx.core.Target){throw new Error(clazz.classname+": the class 'qx.core.Target' must be availabe to use events!");
}
if(typeof events!=="object"||events instanceof Array){throw new Error(clazz.classname+": the events must be defined as map!");
}
for(var key in events){if(typeof events[key]!=="string"){throw new Error(clazz.classname+"/"+key+": the event value needs to be a string with the class name of the event object which will be fired.");
}}if(clazz.$$events&&patch!==true){for(var key in events){if(clazz.$$events[key]!==undefined&&clazz.$$events[key]!==events[key]){throw new Error(clazz.classname+"/"+key+": the event value/type cannot be changed from "+clazz.$$events[key]+" to "+events[key]);
}}}};
if(clazz.$$events){for(var key in events){clazz.$$events[key]=events[key];
}}else{clazz.$$events=events;
}},
__addProperties:function(clazz,
properties,
patch){var config;
if(patch===undefined){patch=false;
}var attach=!!clazz.$$propertiesAttached;
for(var name in properties){config=properties[name];
{this.__validateProperty(clazz,
name,
config,
patch);
};
config.name=name;
if(!config.refine){if(clazz.$$properties===undefined){clazz.$$properties={};
}clazz.$$properties[name]=config;
}if(config.init!==undefined){clazz.prototype["__init$"+name]=config.init;
}if(config.event!==undefined){var event={};
event[config.event]="qx.event.type.ChangeEvent";
this.__addEvents(clazz,
event,
patch);
}if(config.inheritable){qx.core.Property.$$inheritable[name]=true;
}if(attach){qx.core.Property.attachMethods(clazz,
name,
config);
}if(config._fast){qx.core.LegacyProperty.addFastProperty(config,
clazz.prototype);
}else if(config._cached){qx.core.LegacyProperty.addCachedProperty(config,
clazz.prototype);
}else if(config._legacy){qx.core.LegacyProperty.addProperty(config,
clazz.prototype);
}}},
__validateProperty:function(clazz,
name,
config,
patch){var has=this.hasProperty(clazz,
name);
var compat=config._legacy||config._fast||config._cached;
if(has){var existingProperty=this.getPropertyDefinition(clazz,
name);
var existingCompat=existingProperty._legacy||existingProperty._fast||existingProperty._cached;
if(compat!=existingCompat){throw new Error("Could not redefine existing property '"+name+"' of class '"+clazz.classname+"'.");
}
if(config.refine&&existingProperty.init===undefined){throw new Error("Could not refine a init value if there was previously no init value defined. Property '"+name+"' of class '"+clazz.classname+"'.");
}}
if(!has&&config.refine){throw new Error("Could not refine non-existent property: "+name+"!");
}
if(has&&!patch){throw new Error("Class "+clazz.classname+" already has a property: "+name+"!");
}
if(has&&patch&&!compat){if(!config.refine){throw new Error('Could not refine property "'+name+'" without a "refine" flag in the property definition! This class: '+clazz.classname+', original class: '+this.getByProperty(clazz,
name).classname+'.');
}
for(var key in config){if(key!=="init"&&key!=="refine"){throw new Error("Class "+clazz.classname+" could not refine property: "+name+"! Key: "+key+" could not be refined!");
}}}
if(compat){return;
}var allowed=config.group?qx.core.Property.$$allowedGroupKeys:qx.core.Property.$$allowedKeys;
for(var key in config){if(allowed[key]===undefined){throw new Error('The configuration key "'+key+'" of property "'+name+'" in class "'+clazz.classname+'" is not allowed!');
}
if(config[key]===undefined){throw new Error('Invalid key "'+key+'" of property "'+name+'" in class "'+clazz.classname+'"! The value is undefined: '+config[key]);
}
if(allowed[key]!==null&&typeof config[key]!==allowed[key]){throw new Error('Invalid type of key "'+key+'" of property "'+name+'" in class "'+clazz.classname+'"! The type of the key must be "'+allowed[key]+'"!');
}}
if(config.transform!=null){if(!(typeof config.transform=="string")){throw new Error('Invalid transform definition of property "'+name+'" in class "'+clazz.classname+'"! Needs to be a String.');
}}
if(config.check!=null){if(!(typeof config.check=="string"||config.check instanceof Array||config.check instanceof Function)){throw new Error('Invalid check definition of property "'+name+'" in class "'+clazz.classname+'"! Needs to be a String, Array or Function.');
}}
if(config.event!=null&&!this.isSubClassOf(clazz,
qx.core.Target)){throw new Error("Invalid property '"+name+"' in class '"+clazz.classname+"': Properties defining an event can only be defined in sub classes of 'qx.core.Target'!");
}},
__addMembers:function(clazz,
members,
patch,
base){var superproto=clazz.superclass.prototype;
var proto=clazz.prototype;
var key,
member;
for(var i=0,
a=qx.lang.Object.getKeys(members),
l=a.length;i<l;i++){key=a[i];
member=members[key];
{if(patch!==true&&proto[key]!==undefined){throw new Error('Overwriting member "'+key+'" of Class "'+clazz.classname+'" is not allowed!');
}};
if(base!==false&&member instanceof Function){if(superproto[key]){member.base=superproto[key];
}member.self=clazz;
if(qx.core.Variant.isSet("qx.aspects",
"on")){member=qx.core.Aspect.wrap(clazz.classname+"."+key,
member,
"member");
}}proto[key]=member;
}},
__addInterface:function(clazz,
iface){{if(!clazz||!iface){throw new Error("Incomplete parameters!");
}if(this.hasOwnInterface(clazz,
iface)){throw new Error('Interface "'+iface.name+'" is already used by Class "'+clazz.classname+'" by class: '+this.getByMixin(clazz,
mixin).classname+'!');
}qx.Interface.assert(clazz,
iface,
true);
};
var list=qx.Interface.flatten([iface]);
if(clazz.$$implements){clazz.$$implements.push(iface);
clazz.$$flatImplements.push.apply(clazz.$$flatImplements,
list);
}else{clazz.$$implements=[iface];
clazz.$$flatImplements=list;
}},
__addMixin:function(clazz,
mixin,
patch){{if(!clazz||!mixin){throw new Error("Incomplete parameters!");
}
if(this.hasMixin(clazz,
mixin)){throw new Error('Mixin "'+mixin.name+'" is already included into Class "'+clazz.classname+'" by class: '+this.getByMixin(clazz,
mixin).classname+'!');
}};
var list=qx.Mixin.flatten([mixin]);
var entry;
for(var i=0,
l=list.length;i<l;i++){entry=list[i];
if(entry.$$events){this.__addEvents(clazz,
entry.$$events,
patch);
}if(entry.$$properties){this.__addProperties(clazz,
entry.$$properties,
patch);
}if(entry.$$members){this.__addMembers(clazz,
entry.$$members,
patch,
false);
}}if(clazz.$$includes){clazz.$$includes.push(mixin);
clazz.$$flatIncludes.push.apply(clazz.$$flatIncludes,
list);
}else{clazz.$$includes=[mixin];
clazz.$$flatIncludes=list;
}},
__createDefaultConstructor:function(){function defaultConstructor(){arguments.callee.base.apply(this,
arguments);
}return defaultConstructor;
},
__createEmptyFunction:function(){return function(){};
},
__wrapConstructor:function(construct,
name,
type){var code=[];
code.push('var clazz=arguments.callee.constructor;');
{code.push('if(!(this instanceof clazz))throw new Error("Please initialize ',
name,
' objects using the new keyword!");');
if(type==="abstract"){code.push('if(this.classname===',
name,
'.classname)throw new Error("The class ',
name,
' is abstract! It is not possible to instantiate it.");');
}else if(type==="singleton"){code.push('if(!clazz.$$allowconstruct)throw new Error("The class ',
name,
' is a singleton! It is not possible to instantiate it directly. Use the static getInstance() method instead.");');
}};
code.push('if(!clazz.$$propertiesAttached)qx.core.Property.attach(clazz);');
code.push('var retval=clazz.$$original.apply(this,arguments);');
code.push('if(clazz.$$includes){var mixins=clazz.$$flatIncludes;');
code.push('for(var i=0,l=mixins.length;i<l;i++){');
code.push('if(mixins[i].$$constructor){mixins[i].$$constructor.apply(this,arguments);}}}');
code.push('if(this.classname===',
name,
'.classname)this.$$initialized=true;');
code.push('return retval;');
var wrapper=new Function(code.join(""));
if(qx.core.Variant.isSet("qx.aspects",
"on")){var aspectWrapper=qx.core.Aspect.wrap(name,
wrapper,
"constructor");
wrapper.$$original=construct;
wrapper.constructor=aspectWrapper;
wrapper=aspectWrapper;
}if(type==="singleton"){wrapper.getInstance=this.getInstance;
}wrapper.$$original=construct;
construct.wrapper=wrapper;
return wrapper;
}},
defer:function(statics){if(qx.core.Variant.isSet("qx.aspects",
"on")){for(var key in statics){if(statics[key] instanceof Function){statics[key]=qx.core.Aspect.wrap("qx.Class."+key,
statics[key],
"static");
}}}}});




/* ID: qx.Mixin */
qx.Class.define("qx.Mixin",
{statics:{define:function(name,
config){if(config){if(config.include&&!(config.include instanceof Array)){config.include=[config.include];
}{this.__validateConfig(name,
config);
};
var mixin=config.statics?config.statics:{};
for(var key in mixin){mixin[key].mixin=mixin;
}if(config.construct){mixin.$$constructor=config.construct;
}
if(config.include){mixin.$$includes=config.include;
}
if(config.properties){mixin.$$properties=config.properties;
}
if(config.members){mixin.$$members=config.members;
}
for(var key in mixin.$$members){mixin.$$members[key].mixin=mixin;
}
if(config.events){mixin.$$events=config.events;
}
if(config.destruct){mixin.$$destructor=config.destruct;
}}else{var mixin={};
}mixin.$$type="Mixin";
mixin.name=name;
mixin.toString=this.genericToString;
mixin.basename=qx.Class.createNamespace(name,
mixin);
this.__registry[name]=mixin;
return mixin;
},
checkCompatibility:function(mixins){var list=this.flatten(mixins);
var len=list.length;
if(len<2){return true;
}var properties={};
var members={};
var events={};
var mixin;
for(var i=0;i<len;i++){mixin=list[i];
for(var key in mixin.events){if(events[key]){throw new Error('Conflict between mixin "'+mixin.name+'" and "'+events[key]+'" in member "'+key+'"!');
}events[key]=mixin.name;
}
for(var key in mixin.properties){if(properties[key]){throw new Error('Conflict between mixin "'+mixin.name+'" and "'+properties[key]+'" in property "'+key+'"!');
}properties[key]=mixin.name;
}
for(var key in mixin.members){if(members[key]){throw new Error('Conflict between mixin "'+mixin.name+'" and "'+members[key]+'" in member "'+key+'"!');
}members[key]=mixin.name;
}}return true;
},
isCompatible:function(mixin,
clazz){var list=qx.Class.getMixins(clazz);
list.push(mixin);
return qx.Mixin.checkCompatibility(list);
},
getByName:function(name){return this.__registry[name];
},
isDefined:function(name){return this.getByName(name)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.__registry);
},
flatten:function(mixins){if(!mixins){return [];
}var list=mixins.concat();
for(var i=0,
l=mixins.length;i<l;i++){if(mixins[i].$$includes){list.push.apply(list,
this.flatten(mixins[i].$$includes));
}}return list;
},
genericToString:function(){return "[Mixin "+this.name+"]";
},
__registry:{},
__allowedKeys:{"include":"object",
"statics":"object",
"members":"object",
"properties":"object",
"events":"object",
"destruct":"function",
"construct":"function"},
__validateConfig:function(name,
config){var allowed=this.__allowedKeys;
for(var key in config){if(!allowed[key]){throw new Error('The configuration key "'+key+'" in mixin "'+name+'" is not allowed!');
}
if(config[key]==null){throw new Error('Invalid key "'+key+'" in mixin "'+name+'"! The value is undefined/null!');
}
if(allowed[key]!==null&&typeof config[key]!==allowed[key]){throw new Error('Invalid type of key "'+key+'" in mixin "'+name+'"! The type of the key must be "'+allowed[key]+'"!');
}}var maps=["statics",
"members",
"properties",
"events"];
for(var i=0,
l=maps.length;i<l;i++){var key=maps[i];
if(config[key]!==undefined&&(config[key] instanceof Array||config[key] instanceof RegExp||config[key] instanceof Date||config[key].classname!==undefined)){throw new Error('Invalid key "'+key+'" in mixin "'+name+'"! The value needs to be a map!');
}}if(config.include){for(var i=0,
a=config.include,
l=a.length;i<l;i++){if(a[i]==null){throw new Error("Includes of mixins must be mixins. The include number '"+(i+1)+"' in mixin '"+name+"'is undefined/null!");
}
if(a[i].$$type!=="Mixin"){throw new Error("Includes of mixins must be mixins. The include number '"+(i+1)+"' in mixin '"+name+"'is not a mixin!");
}}this.checkCompatibility(config.include);
}}}});




/* ID: qx.Interface */
qx.Class.define("qx.Interface",
{statics:{define:function(name,
config){if(config){if(config.extend&&!(config.extend instanceof Array)){config.extend=[config.extend];
}{this.__validateConfig(name,
config);
};
var iface=config.statics?config.statics:{};
if(config.extend){iface.$$extends=config.extend;
}
if(config.properties){iface.$$properties=config.properties;
}
if(config.members){iface.$$members=config.members;
}
if(config.events){iface.$$events=config.events;
}}else{var iface={};
}iface.$$type="Interface";
iface.name=name;
iface.toString=this.genericToString;
iface.basename=qx.Class.createNamespace(name,
iface);
qx.Interface.__registry[name]=iface;
return iface;
},
getByName:function(name){return this.__registry[name];
},
isDefined:function(name){return this.getByName(name)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.__registry);
},
flatten:function(ifaces){if(!ifaces){return [];
}var list=ifaces.concat();
for(var i=0,
l=ifaces.length;i<l;i++){if(ifaces[i].$$extends){list.push.apply(list,
this.flatten(ifaces[i].$$extends));
}}return list;
},
assert:function(clazz,
iface,
wrap){var members=iface.$$members;
if(members){var proto=clazz.prototype;
for(var key in members){if(typeof members[key]==="function"){if(typeof proto[key]!=="function"){throw new Error('Implementation of method "'+key+'" is missing in class "'+clazz.classname+'" required by interface "'+iface.name+'"');
}if(wrap===true&&!qx.Class.hasInterface(clazz,
iface)){proto[key]=this.__wrapInterfaceMember(iface,
proto[key],
key,
members[key]);
}}else{if(typeof proto[key]===undefined){if(typeof proto[key]!=="function"){throw new Error('Implementation of member "'+key+'" is missing in class "'+clazz.classname+'" required by interface "'+iface.name+'"');
}}}}}if(iface.$$properties){for(var key in iface.$$properties){if(!qx.Class.hasProperty(clazz,
key)){throw new Error('The property "'+key+'" is not supported by Class "'+clazz.classname+'"!');
}}}if(iface.$$events){for(var key in iface.$$events){if(!qx.Class.supportsEvent(clazz,
key)){throw new Error('The event "'+key+'" is not supported by Class "'+clazz.classname+'"!');
}}}var extend=iface.$$extends;
if(extend){for(var i=0,
l=extend.length;i<l;i++){this.assert(clazz,
extend[i],
wrap);
}}},
genericToString:function(){return "[Interface "+this.name+"]";
},
__registry:{},
__wrapInterfaceMember:function(iface,
origFunction,
functionName,
preCondition){function wrappedFunction(){if(!preCondition.apply(this,
arguments)){throw new Error('Pre condition of method "'+functionName+'" defined by "'+iface.name+'" failed.');
}return origFunction.apply(this,
arguments);
}origFunction.wrapper=wrappedFunction;
return wrappedFunction;
},
__allowedKeys:{"extend":"object",
"statics":"object",
"members":"object",
"properties":"object",
"events":"object"},
__validateConfig:function(name,
config){{var allowed=this.__allowedKeys;
for(var key in config){if(allowed[key]===undefined){throw new Error('The configuration key "'+key+'" in class "'+name+'" is not allowed!');
}
if(config[key]==null){throw new Error("Invalid key '"+key+"' in interface '"+name+"'! The value is undefined/null!");
}
if(allowed[key]!==null&&typeof config[key]!==allowed[key]){throw new Error('Invalid type of key "'+key+'" in interface "'+name+'"! The type of the key must be "'+allowed[key]+'"!');
}}var maps=["statics",
"members",
"properties",
"events"];
for(var i=0,
l=maps.length;i<l;i++){var key=maps[i];
if(config[key]!==undefined&&(config[key] instanceof Array||config[key] instanceof RegExp||config[key] instanceof Date||config[key].classname!==undefined)){throw new Error('Invalid key "'+key+'" in interface "'+name+'"! The value needs to be a map!');
}}if(config.extend){for(var i=0,
a=config.extend,
l=a.length;i<l;i++){if(a[i]==null){throw new Error("Extends of interfaces must be interfaces. The extend number '"+i+1+"' in interface '"+name+"' is undefined/null!");
}
if(a[i].$$type!=="Interface"){throw new Error("Extends of interfaces must be interfaces. The extend number '"+i+1+"' in interface '"+name+"' is not an interface!");
}}}if(config.statics){for(var key in config.statics){if(key.toUpperCase()!==key){throw new Error('Invalid key "'+key+'" in interface "'+name+'"! Static constants must be all uppercase.');
}
switch(typeof config.statics[key]){case "boolean":case "string":case "number":break;
default:throw new Error('Invalid key "'+key+'" in interface "'+name+'"! Static constants must be all of a primitive type.');
}}}};
}}});




/* ID: qx.locale.MTranslation */
qx.Mixin.define("qx.locale.MTranslation",
{members:{tr:function(messageId,
varargs){var nlsManager=qx.locale.Manager;
if(nlsManager){return nlsManager.tr.apply(nlsManager,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
},
trn:function(singularMessageId,
pluralMessageId,
count,
varargs){var nlsManager=qx.locale.Manager;
if(nlsManager){return nlsManager.trn.apply(nlsManager,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
},
marktr:function(messageId){var nlsManager=qx.locale.Manager;
if(nlsManager){return nlsManager.marktr.apply(nlsManager,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
}}});




/* ID: qx.log.MLogging */
qx.Mixin.define("qx.log.MLogging",
{members:{getLogger:function(){if(qx.log.Logger){return qx.log.Logger.getClassLogger(this.constructor);
}throw new Error("To enable logging please include qx.log.Logger into your build!");
},
debug:function(msg,
exc){this.getLogger().debug(msg,
this.toHashCode(),
exc);
},
info:function(msg,
exc){this.getLogger().info(msg,
this.toHashCode(),
exc);
},
warn:function(msg,
exc){this.getLogger().warn(msg,
this.toHashCode(),
exc);
},
error:function(msg,
exc){this.getLogger().error(msg,
this.toHashCode(),
exc);
},
printStackTrace:function(){this.getLogger().printStackTrace();
}}});




/* ID: qx.core.MUserData */
qx.Mixin.define("qx.core.MUserData",
{members:{setUserData:function(key,
value){if(!this.__userData){this.__userData={};
}this.__userData[key]=value;
},
getUserData:function(key){if(!this.__userData){return null;
}return this.__userData[key];
}},
destruct:function(){this._disposeFields("__userData");
}});




/* ID: qx.core.LegacyProperty */
qx.Class.define("qx.core.LegacyProperty",
{statics:{getSetterName:function(name){return qx.core.Property.$$method.set[name];
},
getGetterName:function(name){return qx.core.Property.$$method.get[name];
},
getResetterName:function(name){return qx.core.Property.$$method.reset[name];
},
addFastProperty:function(config,
proto){var vName=config.name;
var vUpName=qx.lang.String.toFirstUp(vName);
var vStorageField="_value"+vUpName;
var vGetterName="get"+vUpName;
var vSetterName="set"+vUpName;
var vComputerName="_compute"+vUpName;
proto[vStorageField]=typeof config.defaultValue!=="undefined"?config.defaultValue:null;
if(config.noCompute){proto[vGetterName]=function(){return this[vStorageField];
};
}else{proto[vGetterName]=function(){return this[vStorageField]==null?this[vStorageField]=this[vComputerName]():this[vStorageField];
};
}proto[vGetterName].self=proto.constructor;
if(config.setOnlyOnce){proto[vSetterName]=function(vValue){this[vStorageField]=vValue;
this[vSetterName]=null;
return vValue;
};
}else{proto[vSetterName]=function(vValue){return this[vStorageField]=vValue;
};
}proto[vSetterName].self=proto.constructor;
if(!config.noCompute){proto[vComputerName]=function(){return null;
};
proto[vComputerName].self=proto.constructor;
}},
addCachedProperty:function(config,
proto){var vName=config.name;
var vUpName=qx.lang.String.toFirstUp(vName);
var vStorageField="_cached"+vUpName;
var vComputerName="_compute"+vUpName;
var vChangeName="_change"+vUpName;
if(typeof config.defaultValue!=="undefined"){proto[vStorageField]=config.defaultValue;
}proto["get"+vUpName]=function(){if(this[vStorageField]==null){this[vStorageField]=this[vComputerName]();
}return this[vStorageField];
};
proto["_invalidate"+vUpName]=function(){if(this[vStorageField]!=null){this[vStorageField]=null;
if(config.addToQueueRuntime){this.addToQueueRuntime(config.name);
}}};
proto["_recompute"+vUpName]=function(){var vOld=this[vStorageField];
var vNew=this[vComputerName]();
if(vNew!=vOld){this[vStorageField]=vNew;
this[vChangeName](vNew,
vOld);
return true;
}return false;
};
proto[vChangeName]=function(vNew,
vOld){};
proto[vComputerName]=function(){return null;
};
proto["get"+vUpName].self=proto.constructor;
proto["_invalidate"+vUpName].self=proto.constructor;
proto["_recompute"+vUpName].self=proto.constructor;
},
addProperty:function(config,
proto){if(typeof config!=="object"){throw new Error("AddProperty: Param should be an object!");
}
if(typeof config.name!=="string"){throw new Error("AddProperty: Malformed input parameters: name needed!");
}if(config.dispose===undefined&&(config.type=="function"||config.type=="object")){config.dispose=true;
}config.method=qx.lang.String.toFirstUp(config.name);
config.implMethod=config.impl?qx.lang.String.toFirstUp(config.impl):config.method;
if(config.defaultValue==undefined){config.defaultValue=null;
}config.allowNull=config.allowNull!==false;
config.allowMultipleArguments=config.allowMultipleArguments===true;
if(typeof config.type==="string"){config.hasType=true;
}else if(typeof config.type!=="undefined"){throw new Error("AddProperty: Invalid type definition for property "+config.name+": "+config.type);
}else{config.hasType=false;
}
if(typeof config.instance==="string"){config.hasInstance=true;
}else if(typeof config.instance!=="undefined"){throw new Error("AddProperty: Invalid instance definition for property "+config.name+": "+config.instance);
}else{config.hasInstance=false;
}
if(typeof config.classname==="string"){config.hasClassName=true;
}else if(typeof config.classname!=="undefined"){throw new Error("AddProperty: Invalid classname definition for property "+config.name+": "+config.classname);
}else{config.hasClassName=false;
}config.hasConvert=config.convert!=null;
config.hasPossibleValues=config.possibleValues!=null;
config.addToQueue=config.addToQueue||false;
config.addToQueueRuntime=config.addToQueueRuntime||false;
config.up=config.name.toUpperCase();
var valueKey=qx.core.Property.$$store.user[config.name]="__user$"+config.name;
var changeKey="change"+config.method;
var modifyKey="_modify"+config.implMethod;
var checkKey="_check"+config.implMethod;
var method=qx.core.Property.$$method;
if(!method.set[config.name]){method.set[config.name]="set"+config.method;
method.get[config.name]="get"+config.method;
method.reset[config.name]="reset"+config.method;
}proto[valueKey]=config.defaultValue;
proto["get"+config.method]=function(){return this[valueKey];
};
proto["force"+config.method]=function(newValue){return this[valueKey]=newValue;
};
proto["reset"+config.method]=function(){return this["set"+config.method](config.defaultValue);
};
if(config.type==="boolean"){proto["toggle"+config.method]=function(newValue){return this["set"+config.method](!this[valueKey]);
};
}
if(config.allowMultipleArguments||config.hasConvert||config.hasInstance||config.hasClassName||config.hasPossibleValues||config.hasUnitDetection||config.addToQueue||config.addToQueueRuntime||config.addToStateQueue){proto["set"+config.method]=function(newValue){if(config.allowMultipleArguments&&arguments.length>1){newValue=qx.lang.Array.fromArguments(arguments);
}if(config.hasConvert){try{newValue=config.convert.call(this,
newValue,
config);
}catch(ex){throw new Error("Attention! Could not convert new value for "+config.name+": "+newValue+": "+ex);
}}var oldValue=this[valueKey];
if(newValue===oldValue){return newValue;
}
if(!(config.allowNull&&newValue==null)){if(config.hasType&&typeof newValue!==config.type){throw new Error("Attention! The value \""+newValue+"\" is an invalid value for the property \""+config.name+"\" which must be typeof \""+config.type+"\" but is typeof \""+typeof newValue+"\"!");
}
if(qx.Class.getByName(config.instance)){if(config.hasInstance&&!(newValue instanceof qx.Class.getByName(config.instance))){throw new Error("Attention! The value \""+newValue+"\" is an invalid value for the property \""+config.name+"\" which must be an instance of \""+config.instance+"\"!");
}}else{}
if(config.hasClassName&&newValue.classname!=config.classname){throw new Error("Attention! The value \""+newValue+"\" is an invalid value for the property \""+config.name+"\" which must be an object with the classname \""+config.classname+"\"!");
}
if(config.hasPossibleValues&&newValue!=null&&!qx.lang.Array.contains(config.possibleValues,
newValue)){throw new Error("Failed to save value for "+config.name+". '"+newValue+"' is not a possible value!");
}}if(this[checkKey]){try{newValue=this[checkKey](newValue,
config);
if(newValue===oldValue){return newValue;
}}catch(ex){return this.error("Failed to check property "+config.name,
ex);
}}this[valueKey]=newValue;
if(this[modifyKey]){try{this[modifyKey](newValue,
oldValue,
config);
}catch(ex){return this.error("Modification of property \""+config.name+"\" failed with exception",
ex);
}}if(config.addToQueue){this.addToQueue(config.name);
}
if(config.addToQueueRuntime){this.addToQueueRuntime(config.name);
}if(config.addToStateQueue){this.addToStateQueue();
}if(this.hasEventListeners&&this.hasEventListeners(changeKey)){try{this.createDispatchDataEvent(changeKey,
newValue);
}catch(ex){throw new Error("Property "+config.name+" modified: Failed to dispatch change event: "+ex);
}}return newValue;
};
}else{proto["set"+config.method]=function(newValue){var oldValue=this[valueKey];
if(newValue===oldValue){return newValue;
}
if(!(config.allowNull&&newValue==null)){if(config.hasType&&typeof newValue!==config.type){throw new Error("Attention! The value \""+newValue+"\" is an invalid value for the property \""+config.name+"\" which must be typeof \""+config.type+"\" but is typeof \""+typeof newValue+"\"!");
}}if(this[checkKey]){try{newValue=this[checkKey](newValue,
config);
if(newValue===oldValue){return newValue;
}}catch(ex){return this.error("Failed to check property "+config.name,
ex);
}}this[valueKey]=newValue;
if(this[modifyKey]){try{this[modifyKey](newValue,
oldValue,
config);
}catch(ex){var valueStr=new String(newValue).substring(0,
50);
this.error("Setting property \""+config.name+"\" to \""+valueStr+"\" failed with exception",
ex);
}}if(this.hasEventListeners&&this.hasEventListeners(changeKey)){var vEvent=new qx.event.type.DataEvent(changeKey,
newValue,
oldValue,
false);
vEvent.setTarget(this);
try{this.dispatchEvent(vEvent,
true);
}catch(ex){throw new Error("Property "+config.name+" modified: Failed to dispatch change event: "+ex);
}}return newValue;
};
}proto["set"+config.method].self=proto.constructor;
if(typeof config.getAlias==="string"){proto[config.getAlias]=proto["get"+config.method];
}if(typeof config.setAlias==="string"){proto[config.setAlias]=proto["set"+config.method];
}}}});




/* ID: qx.core.Property */
qx.Class.define("qx.core.Property",
{statics:{__checks:{"Boolean":'typeof value === "boolean"',
"String":'typeof value === "string"',
"NonEmptyString":'typeof value === "string" && value.length > 0',
"Number":'typeof value === "number" && isFinite(value)',
"Integer":'typeof value === "number" && isFinite(value) && value%1 === 0',
"Float":'typeof value === "number" && isFinite(value)',
"Double":'typeof value === "number" && isFinite(value)',
"Error":'value instanceof Error',
"RegExp":'value instanceof RegExp',
"Object":'value !== null && typeof value === "object"',
"Array":'value instanceof Array',
"Map":'value !== null && typeof value === "object" && !(value instanceof Array) && !(value instanceof qx.core.Object)',
"Function":'value instanceof Function',
"Date":'value instanceof Date',
"Node":'value !== null && value.nodeType !== undefined',
"Element":'value !== null && value.nodeType === 1 && value.attributes',
"Document":'value !== null && value.nodeType === 9 && value.documentElement',
"Window":'value !== null && window.document',
"Event":'value !== null && value.type !== undefined',
"Class":'value !== null && value.$$type === "Class"',
"Mixin":'value !== null && value.$$type === "Mixin"',
"Interface":'value !== null && value.$$type === "Interface"',
"Theme":'value !== null && value.$$type === "Theme"',
"Color":'typeof value === "string" && qx.util.ColorUtil.isValid(value)',
"Border":'value !== null && qx.theme.manager.Border.getInstance().isDynamic(value)',
"Font":'value !== null && qx.theme.manager.Font.getInstance().isDynamic(value)',
"Label":'value !== null && (qx.locale.Manager.getInstance().isDynamic(value) || typeof value === "string")'},
__dispose:{"Object":true,
"Array":true,
"Map":true,
"Function":true,
"Date":true,
"Node":true,
"Element":true,
"Document":true,
"Window":true,
"Event":true,
"Class":true,
"Mixin":true,
"Interface":true,
"Theme":true,
"Border":true,
"Font":true},
$$inherit:"inherit",
$$idcounter:0,
$$store:{user:{},
theme:{},
inherit:{},
init:{},
useinit:{}},
$$method:{get:{},
set:{},
reset:{},
init:{},
refresh:{},
style:{},
unstyle:{}},
$$allowedKeys:{name:"string",
dispose:"boolean",
inheritable:"boolean",
nullable:"boolean",
themeable:"boolean",
refine:"boolean",
init:null,
apply:"string",
event:"string",
check:null,
transform:"string"},
$$allowedGroupKeys:{name:"string",
group:"object",
mode:"string",
themeable:"boolean"},
$$inheritable:{},
refresh:function(widget){var parent=widget.getParent();
if(parent){var clazz=widget.constructor;
var inherit=this.$$store.inherit;
var refresh=this.$$method.refresh;
var properties;
{if(qx.core.Setting.get("qx.propertyDebugLevel")>1){widget.debug("Update widget: "+widget);
}};
while(clazz){properties=clazz.$$properties;
if(properties){for(var name in this.$$inheritable){if(properties[name]){{if(qx.core.Setting.get("qx.propertyDebugLevel")>2){widget.debug("Updating property: "+name+" to '"+parent[inherit[name]]+"'");
}};
widget[refresh[name]](parent[inherit[name]]);
}}}clazz=clazz.superclass;
}}},
attach:function(clazz){var properties=clazz.$$properties;
if(properties){for(var name in properties){this.attachMethods(clazz,
name,
properties[name]);
}}clazz.$$propertiesAttached=true;
},
attachMethods:function(clazz,
name,
config){if(config._legacy||config._fast||config._cached){return;
}var prefix,
postfix;
if(name.charAt(0)==="_"){if(name.charAt(1)==="_"){prefix="__";
postfix=qx.lang.String.toFirstUp(name.substring(2));
}else{prefix="_";
postfix=qx.lang.String.toFirstUp(name.substring(1));
}}else{prefix="";
postfix=qx.lang.String.toFirstUp(name);
}config.group?this.__attachGroupMethods(clazz,
config,
prefix,
postfix):this.__attachPropertyMethods(clazz,
config,
prefix,
postfix);
},
__attachGroupMethods:function(clazz,
config,
prefix,
postfix){var members=clazz.prototype;
var name=config.name;
var themeable=config.themeable===true;
{if(qx.core.Setting.get("qx.propertyDebugLevel")>1){console.debug("Generating property group: "+name);
}};
var setter=[];
var resetter=[];
if(themeable){var styler=[];
var unstyler=[];
}var argHandler="var a=arguments[0] instanceof Array?arguments[0]:arguments;";
setter.push(argHandler);
if(themeable){styler.push(argHandler);
}
if(config.mode=="shorthand"){var shorthand="a=qx.lang.Array.fromShortHand(qx.lang.Array.fromArguments(a));";
setter.push(shorthand);
if(themeable){styler.push(shorthand);
}}
for(var i=0,
a=config.group,
l=a.length;i<l;i++){{if(!this.$$method.set[a[i]]||!this.$$method.reset[a[i]]){throw new Error("Cannot create property group '"+name+"' including non-existing property '"+a[i]+"'!");
}};
setter.push("this.",
this.$$method.set[a[i]],
"(a[",
i,
"]);");
resetter.push("this.",
this.$$method.reset[a[i]],
"();");
if(themeable){styler.push("this.",
this.$$method.style[a[i]],
"(a[",
i,
"]);");
unstyler.push("this.",
this.$$method.unstyle[a[i]],
"();");
}}this.$$method.set[name]=prefix+"set"+postfix;
members[this.$$method.set[name]]=new Function(setter.join(""));
this.$$method.reset[name]=prefix+"reset"+postfix;
members[this.$$method.reset[name]]=new Function(resetter.join(""));
if(themeable){this.$$method.style[name]=prefix+"style"+postfix;
members[this.$$method.style[name]]=new Function(styler.join(""));
this.$$method.unstyle[name]=prefix+"unstyle"+postfix;
members[this.$$method.unstyle[name]]=new Function(unstyler.join(""));
}},
__attachPropertyMethods:function(clazz,
config,
prefix,
postfix){var members=clazz.prototype;
var name=config.name;
{if(qx.core.Setting.get("qx.propertyDebugLevel")>1){console.debug("Generating property wrappers: "+name);
}};
if(config.dispose===undefined&&typeof config.check==="string"){config.dispose=this.__dispose[config.check]||qx.Class.isDefined(config.check);
}var method=this.$$method;
var store=this.$$store;
store.user[name]="__user$"+name;
store.theme[name]="__theme$"+name;
store.init[name]="__init$"+name;
store.inherit[name]="__inherit$"+name;
store.useinit[name]="__useinit$"+name;
method.get[name]=prefix+"get"+postfix;
members[method.get[name]]=function(){return qx.core.Property.executeOptimizedGetter(this,
clazz,
name,
"get");
};
method.set[name]=prefix+"set"+postfix;
members[method.set[name]]=function(value){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"set",
arguments);
};
method.reset[name]=prefix+"reset"+postfix;
members[method.reset[name]]=function(){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"reset");
};
if(config.inheritable||config.apply||config.event){method.init[name]=prefix+"init"+postfix;
members[method.init[name]]=function(value){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"init",
arguments);
};
}
if(config.inheritable){method.refresh[name]=prefix+"refresh"+postfix;
members[method.refresh[name]]=function(value){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"refresh",
arguments);
};
}
if(config.themeable){method.style[name]=prefix+"style"+postfix;
members[method.style[name]]=function(value){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"style",
arguments);
};
method.unstyle[name]=prefix+"unstyle"+postfix;
members[method.unstyle[name]]=function(){return qx.core.Property.executeOptimizedSetter(this,
clazz,
name,
"unstyle");
};
}
if(config.check==="Boolean"){members[prefix+"toggle"+postfix]=new Function("return this."+method.set[name]+"(!this."+method.get[name]+"())");
members[prefix+"is"+postfix]=new Function("return this."+method.get[name]+"()");
}},
__errors:{0:'Could not change or apply init value after constructing phase!',
1:'Requires exactly one argument!',
2:'Undefined value is not allowed!',
3:'Does not allow any arguments!',
4:'Null value is not allowed!',
5:'Is invalid!'},
error:function(obj,
id,
property,
variant,
value){var classname=obj.constructor.classname;
var msg="Error in property "+property+" of class "+classname+" in method "+this.$$method[variant][property]+" with incoming value '"+value+"': ";
obj.printStackTrace();
throw new Error(msg+(this.__errors[id]||"Unknown reason: "+id));
},
__unwrapFunctionFromCode:function(instance,
members,
name,
variant,
code,
args){var store=this.$$method[variant][name];
{if(qx.core.Setting.get("qx.propertyDebugLevel")>1){console.debug("Code["+this.$$method[variant][name]+"]: "+code.join(""));
}try{members[store]=new Function("value",
code.join(""));
}catch(ex){alert("Malformed generated code to unwrap method: "+this.$$method[variant][name]+"\n"+code.join(""));
}};
if(qx.core.Variant.isSet("qx.aspects",
"on")){members[store]=qx.core.Aspect.wrap(instance.classname+"."+store,
members[store],
"property");
}if(args===undefined){return instance[store]();
}else{return instance[store].apply(instance,
args);
}},
executeOptimizedGetter:function(instance,
clazz,
name,
variant){var config=clazz.$$properties[name];
var members=clazz.prototype;
var code=[];
if(config.inheritable){code.push('if(this.',
this.$$store.inherit[name],
'!==undefined)');
code.push('return this.',
this.$$store.inherit[name],
';');
code.push('else ');
}code.push('if(this.',
this.$$store.user[name],
'!==undefined)');
code.push('return this.',
this.$$store.user[name],
';');
if(config.themeable){code.push('else if(this.',
this.$$store.theme[name],
'!==undefined)');
code.push('return this.',
this.$$store.theme[name],
';');
}
if(config.deferredInit&&config.init===undefined){code.push('else if(this.',
this.$$store.init[name],
'!==undefined)');
code.push('return this.',
this.$$store.init[name],
';');
}code.push('else ');
if(config.init!==undefined){code.push('return this.',
this.$$store.init[name],
';');
}else if(config.inheritable||config.nullable){code.push('return null;');
}else{code.push('throw new Error("Property ',
name,
' of an instance of ',
clazz.classname,
' is not (yet) ready!");');
}return this.__unwrapFunctionFromCode(instance,
members,
name,
variant,
code);
},
executeOptimizedSetter:function(instance,
clazz,
name,
variant,
args){var config=clazz.$$properties[name];
var members=clazz.prototype;
var value=args?args[0]:undefined;
var code=[];
var incomingValue=variant==="set"||variant==="style"||(variant==="init"&&config.init===undefined);
var resetValue=variant==="reset"||variant==="unstyle";
var hasCallback=config.apply||config.event||config.inheritable;
if(variant==="style"||variant==="unstyle"){var store=this.$$store.theme[name];
}else if(variant==="init"){var store=this.$$store.init[name];
}else{var store=this.$$store.user[name];
}{code.push('var prop=qx.core.Property;');
if(variant==="init"){code.push('if(this.$$initialized)prop.error(this,0,"'+name+'","'+variant+'",value);');
}
if(variant==="refresh"){}else if(incomingValue){code.push('if(arguments.length!==1)prop.error(this,1,"'+name+'","'+variant+'",value);');
code.push('if(value===undefined)prop.error(this,2,"'+name+'","'+variant+'",value);');
}else{code.push('if(arguments.length!==0)prop.error(this,3,"'+name+'","'+variant+'",value);');
}};
if(incomingValue){if(config.transform){code.push('value=this.',
config.transform,
'(value);');
}}if(hasCallback){if(incomingValue){code.push('if(this.',
store,
'===value)return value;');
}else if(resetValue){code.push('if(this.',
store,
'===undefined)return;');
}}if(config.inheritable){code.push('var inherit=prop.$$inherit;');
}if(incomingValue&&true){if(!config.nullable){code.push('if(value===null)prop.error(this,4,"'+name+'","'+variant+'",value);');
}if(config.check!==undefined){if(config.nullable){code.push('if(value!==null)');
}if(config.inheritable){code.push('if(value!==inherit)');
}code.push('if(');
if(this.__checks[config.check]!==undefined){code.push('!(',
this.__checks[config.check],
')');
}else if(qx.Class.isDefined(config.check)){code.push('!(value instanceof ',
config.check,
')');
}else if(qx.Interface.isDefined(config.check)){code.push('!(value && qx.Class.hasInterface(value.constructor, ',
config.check,
'))');
}else if(typeof config.check==="function"){code.push('!',
clazz.classname,
'.$$properties.',
name);
code.push('.check.call(this, value)');
}else if(typeof config.check==="string"){code.push('!(',
config.check,
')');
}else if(config.check instanceof Array){config.checkMap=qx.lang.Object.fromArray(config.check);
code.push(clazz.classname,
'.$$properties.',
name);
code.push('.checkMap[value]===undefined');
}else{throw new Error("Could not add check to property "+name+" of class "+clazz.classname);
}code.push(')prop.error(this,5,"'+name+'","'+variant+'",value);');
}}
if(!hasCallback){if(variant==="set"){code.push('this.',
this.$$store.user[name],
'=value;');
}else if(variant==="reset"){code.push('if(this.',
this.$$store.user[name],
'!==undefined)');
code.push('delete this.',
this.$$store.user[name],
';');
}else if(variant==="style"){code.push('this.',
this.$$store.theme[name],
'=value;');
}else if(variant==="unstyle"){code.push('if(this.',
this.$$store.theme[name],
'!==undefined)');
code.push('delete this.',
this.$$store.theme[name],
';');
}else if(variant==="init"&&incomingValue){code.push('this.',
this.$$store.init[name],
'=value;');
}}else{if(config.inheritable){code.push('var computed, old=this.',
this.$$store.inherit[name],
';');
}else{code.push('var computed, old;');
}code.push('if(this.',
this.$$store.user[name],
'!==undefined){');
if(variant==="set"){if(!config.inheritable){code.push('old=this.',
this.$$store.user[name],
';');
}code.push('computed=this.',
this.$$store.user[name],
'=value;');
}else if(variant==="reset"){if(!config.inheritable){code.push('old=this.',
this.$$store.user[name],
';');
}code.push('delete this.',
this.$$store.user[name],
';');
code.push('if(this.',
this.$$store.theme[name],
'!==undefined)');
code.push('computed=this.',
this.$$store.theme[name],
';');
code.push('else if(this.',
this.$$store.init[name],
'!==undefined){');
code.push('computed=this.',
this.$$store.init[name],
';');
code.push('this.',
this.$$store.useinit[name],
'=true;');
code.push('}');
}else{if(config.inheritable){code.push('computed=this.',
this.$$store.user[name],
';');
}else{code.push('old=computed=this.',
this.$$store.user[name],
';');
}if(variant==="style"){code.push('this.',
this.$$store.theme[name],
'=value;');
}else if(variant==="unstyle"){code.push('delete this.',
this.$$store.theme[name],
';');
}else if(variant==="init"&&incomingValue){code.push('this.',
this.$$store.init[name],
'=value;');
}}code.push('}');
if(config.themeable){code.push('else if(this.',
this.$$store.theme[name],
'!==undefined){');
if(!config.inheritable){code.push('old=this.',
this.$$store.theme[name],
';');
}
if(variant==="set"){code.push('computed=this.',
this.$$store.user[name],
'=value;');
}else if(variant==="style"){code.push('computed=this.',
this.$$store.theme[name],
'=value;');
}else if(variant==="unstyle"){code.push('delete this.',
this.$$store.theme[name],
';');
code.push('if(this.',
this.$$store.init[name],
'!==undefined){');
code.push('computed=this.',
this.$$store.init[name],
';');
code.push('this.',
this.$$store.useinit[name],
'=true;');
code.push('}');
}else if(variant==="init"){if(incomingValue){code.push('this.',
this.$$store.init[name],
'=value;');
}code.push('computed=this.',
this.$$store.theme[name],
';');
}else if(variant==="refresh"){code.push('computed=this.',
this.$$store.theme[name],
';');
}code.push('}');
}code.push('else if(this.',
this.$$store.useinit[name],
'){');
if(!config.inheritable){code.push('old=this.',
this.$$store.init[name],
';');
}
if(variant==="init"){if(incomingValue){code.push('computed=this.',
this.$$store.init[name],
'=value;');
}else{code.push('computed=this.',
this.$$store.init[name],
';');
}}else if(variant==="set"||variant==="style"||variant==="refresh"){code.push('delete this.',
this.$$store.useinit[name],
';');
if(variant==="set"){code.push('computed=this.',
this.$$store.user[name],
'=value;');
}else if(variant==="style"){code.push('computed=this.',
this.$$store.theme[name],
'=value;');
}else if(variant==="refresh"){code.push('computed=this.',
this.$$store.init[name],
';');
}}code.push('}');
if(variant==="set"||variant==="style"||variant==="init"){code.push('else{');
if(variant==="set"){code.push('computed=this.',
this.$$store.user[name],
'=value;');
}else if(variant==="style"){code.push('computed=this.',
this.$$store.theme[name],
'=value;');
}else if(variant==="init"){if(incomingValue){code.push('computed=this.',
this.$$store.init[name],
'=value;');
}else{code.push('computed=this.',
this.$$store.init[name],
';');
}code.push('this.',
this.$$store.useinit[name],
'=true;');
}code.push('}');
}}
if(config.inheritable){code.push('if(computed===undefined||computed===inherit){');
if(variant==="refresh"){code.push('computed=value;');
}else{code.push('var pa=this.getParent();if(pa)computed=pa.',
this.$$store.inherit[name],
';');
}code.push('if((computed===undefined||computed===inherit)&&');
code.push('this.',
this.$$store.init[name],
'!==undefined&&');
code.push('this.',
this.$$store.init[name],
'!==inherit){');
code.push('computed=this.',
this.$$store.init[name],
';');
code.push('this.',
this.$$store.useinit[name],
'=true;');
code.push('}else{');
code.push('delete this.',
this.$$store.useinit[name],
';}');
code.push('}');
code.push('if(old===computed)return value;');
code.push('if(computed===inherit){');
code.push('computed=undefined;delete this.',
this.$$store.inherit[name],
';');
code.push('}');
code.push('else if(computed===undefined)');
code.push('delete this.',
this.$$store.inherit[name],
';');
code.push('else this.',
this.$$store.inherit[name],
'=computed;');
code.push('var backup=computed;');
code.push('if(computed===undefined)computed=null;');
code.push('if(old===undefined)old=null;');
}else if(hasCallback){if(variant!=="set"&&variant!=="style"){code.push('if(computed===undefined)computed=null;');
}code.push('if(old===computed)return value;');
code.push('if(old===undefined)old=null;');
}if(hasCallback){if(config.apply){code.push('this.',
config.apply,
'(computed, old);');
}if(config.event){code.push('this.createDispatchChangeEvent("',
config.event,
'", computed, old);');
}if(config.inheritable&&members.getChildren){code.push('var a=this.getChildren();if(a)for(var i=0,l=a.length;i<l;i++){');
code.push('if(a[i].',
this.$$method.refresh[name],
')a[i].',
this.$$method.refresh[name],
'(backup);');
code.push('}');
}}if(incomingValue){code.push('return value;');
}return this.__unwrapFunctionFromCode(instance,
members,
name,
variant,
code,
args);
}},
settings:{"qx.propertyDebugLevel":0}});




/* ID: qx.core.Object */
qx.Class.define("qx.core.Object",
{extend:Object,
include:[qx.locale.MTranslation,
qx.log.MLogging,
qx.core.MUserData],
construct:function(){this._hashCode=qx.core.Object.__availableHashCode++;
if(this._autoDispose){this.__dbKey=qx.core.Object.__db.length;
qx.core.Object.__db.push(this);
}},
statics:{__availableHashCode:0,
__db:[],
__disposeAll:false,
$$type:"Object",
toHashCode:function(obj){if(obj._hashCode!=null){return obj._hashCode;
}return obj._hashCode=this.__availableHashCode++;
},
getDb:function(){return this.__db;
},
dispose:function(){if(this.__disposed){return;
}this.__disposed=true;
{if(qx.core.Setting.get("qx.disposerDebugLevel")>=1){var disposeStart=new Date;
console.debug("Disposing qooxdoo application...");
}};
var vObject,
vObjectDb=this.__db;
for(var i=vObjectDb.length-1;i>=0;i--){vObject=vObjectDb[i];
if(vObject&&vObject.__disposed===false){try{vObject.dispose();
}catch(ex){try{console.warn("Could not dispose: "+vObject+": "+ex);
}catch(exc){throw new Error("Could not dispose: "+vObject+": "+ex);
}}}}{if(qx.core.Setting.get("qx.disposerDebugLevel")>=1){var elems=document.all?document.all:document.getElementsByTagName("*");
console.debug("Checking "+elems.length+" elements for object references...");
for(var i=0,
l=elems.length;i<l;i++){var elem=elems[i];
for(var key in elem){try{if(typeof elem[key]=="object"){if(elem[key] instanceof qx.core.Object||elem[key] instanceof Array){var name="unknown object";
if(elem[key] instanceof qx.core.Object){name=elem[key].classname+"["+elem[key].toHashCode()+"]";
}console.debug("Attribute '"+key+"' references "+name+" in DOM element: "+elem.tagName);
}}}catch(ex){}}}console.debug("Disposing done in "+(new Date()-disposeStart)+"ms");
}};
},
inGlobalDispose:function(){return this.__disposed;
}},
members:{_autoDispose:true,
toHashCode:function(){return this._hashCode;
},
toString:function(){if(this.classname){return "[object "+this.classname+"]";
}return "[object Object]";
},
base:function(args,
varags){if(arguments.length===1){return args.callee.base.call(this);
}else{return args.callee.base.apply(this,
Array.prototype.slice.call(arguments,
1));
}},
self:function(args){return args.callee.self;
},
set:function(data,
value){var setter=qx.core.Property.$$method.set;
if(typeof data==="string"){{if(!this[setter[data]]){this.warn("No such property: "+data);
return;
}};
return this[setter[data]](value);
}else{for(var prop in data){{if(!this[setter[prop]]){this.warn("No such property: "+prop);
continue;
}};
this[setter[prop]](data[prop]);
}return this;
}},
get:function(prop){var getter=qx.core.Property.$$method.get;
{if(!this[getter[prop]]){this.warn("No such property: "+prop);
return;
}};
return this[getter[prop]]();
},
reset:function(prop){var resetter=qx.core.Property.$$method.reset;
{if(!this[resetter[prop]]){this.warn("No such property: "+prop);
return;
}};
this[resetter[prop]]();
},
__disposed:false,
getDisposed:function(){return this.__disposed;
},
isDisposed:function(){return this.__disposed;
},
dispose:function(){if(this.__disposed){return;
}this.__disposed=true;
{if(qx.core.Setting.get("qx.disposerDebugLevel")>1){console.debug("Disposing "+this.classname+"["+this.toHashCode()+"]");
}};
var clazz=this.constructor;
var mixins;
while(clazz.superclass){if(clazz.$$destructor){clazz.$$destructor.call(this);
}if(clazz.$$includes){mixins=clazz.$$flatIncludes;
for(var i=0,
l=mixins.length;i<l;i++){if(mixins[i].$$destructor){mixins[i].$$destructor.call(this);
}}}clazz=clazz.superclass;
}{if(qx.core.Setting.get("qx.disposerDebugLevel")>0){for(var vKey in this){if(this[vKey]!==null&&typeof this[vKey]==="object"&&this.constructor.prototype[vKey]===undefined){console.warn("Missing destruct definition for '"+vKey+"' in "+this.classname+"["+this.toHashCode()+"]: "+this[vKey]);
delete this[vKey];
}}}};
},
_disposeFields:function(varargs){var name;
for(var i=0,
l=arguments.length;i<l;i++){var name=arguments[i];
if(this[name]==null){continue;
}
if(!this.hasOwnProperty(name)){{if(qx.core.Setting.get("qx.disposerDebugLevel")>1){console.debug(this.classname+" has no own field "+name);
}};
continue;
}this[name]=null;
}},
_disposeObjects:function(varargs){var name;
for(var i=0,
l=arguments.length;i<l;i++){var name=arguments[i];
if(this[name]==null){continue;
}
if(!this.hasOwnProperty(name)){{if(qx.core.Setting.get("qx.disposerDebugLevel")>1){console.debug(this.classname+" has no own field "+name);
}};
continue;
}
if(!this[name].dispose){throw new Error(this.classname+" has no own object "+name);
}this[name].dispose();
this[name]=null;
}},
_disposeObjectDeep:function(name,
deep){var name;
if(this[name]==null){return;
}
if(!this.hasOwnProperty(name)){{if(qx.core.Setting.get("qx.disposerDebugLevel")>1){console.debug(this.classname+" has no own field "+name);
}};
return;
}{if(qx.core.Setting.get("qx.disposerDebugLevel")>1){console.debug("Dispose Deep: "+name);
}};
this.__disposeObjectsDeepRecurser(this[name],
deep||0);
this[name]=null;
},
__disposeObjectsDeepRecurser:function(obj,
deep){if(obj instanceof qx.core.Object){{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("Sending dispose to "+obj.classname);
}};
obj.dispose();
}else if(obj instanceof Array){for(var i=0,
l=obj.length;i<l;i++){var entry=obj[i];
if(entry==null){continue;
}
if(typeof entry=="object"){if(deep>0){{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Deep processing item '"+i+"'");
}};
this.__disposeObjectsDeepRecurser(entry,
deep-1);
}{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Resetting key (object) '"+key+"'");
}};
obj[i]=null;
}else if(typeof entry=="function"){{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Resetting key (function) '"+key+"'");
}};
obj[i]=null;
}}}else if(obj instanceof Object){for(var key in obj){if(obj[key]==null||!obj.hasOwnProperty(key)){continue;
}var entry=obj[key];
if(typeof entry=="object"){if(deep>0){{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Deep processing key '"+key+"'");
}};
this.__disposeObjectsDeepRecurser(entry,
deep-1);
}{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Resetting key (object) '"+key+"'");
}};
obj[key]=null;
}else if(typeof entry=="function"){{if(qx.core.Setting.get("qx.disposerDebugLevel")>2){console.debug("- Resetting key (function) '"+key+"'");
}};
obj[key]=null;
}}}}},
settings:{"qx.disposerDebugLevel":0},
destruct:function(){var clazz=this.constructor;
var properties;
var store=qx.core.Property.$$store;
var storeUser=store.user;
var storeTheme=store.theme;
var storeInherit=store.inherit;
var storeUseinit=store.useinit;
var storeInit=store.init;
while(clazz){properties=clazz.$$properties;
if(properties){for(var name in properties){if(properties[name].dispose){this[storeUser[name]]=this[storeTheme[name]]=this[storeInherit[name]]=this[storeUseinit[name]]=this[storeInit[name]]=undefined;
}}}clazz=clazz.superclass;
}if(this.__dbKey!=null){if(qx.core.Object.__disposeAll){qx.core.Object.__db[this.__dbKey]=null;
}else{delete qx.core.Object.__db[this.__dbKey];
}}}});




/* ID: qx.core.Target */
qx.Class.define("qx.core.Target",
{extend:qx.core.Object,
construct:function(){this.base(arguments);
},
members:{addEventListener:function(type,
func,
obj){if(this.getDisposed()){return;
}{if(typeof type!=="string"){this.warn("addEventListener("+type+"): '"+type+"' is not a string!");
return;
}
if(typeof func!=="function"){this.warn("addEventListener("+type+"): '"+func+"' is not a function!");
return;
}if(this.constructor.classname&&!qx.Class.supportsEvent(this.constructor,
type)){this.warn("Objects of class '"+this.constructor.classname+"' do not support the event '"+type+"'");
}};
if(this.__listeners===undefined){this.__listeners={};
}
if(this.__listeners[type]===undefined){this.__listeners[type]={};
}var key="event"+qx.core.Object.toHashCode(func)+(obj?"$"+qx.core.Object.toHashCode(obj):"");
this.__listeners[type][key]={handler:func,
object:obj};
},
removeEventListener:function(type,
func,
obj){if(this.getDisposed()){return;
}var listeners=this.__listeners;
if(!listeners||listeners[type]===undefined){return;
}
if(typeof func!=="function"){throw new Error("qx.core.Target: removeEventListener("+type+"): '"+func+"' is not a function!");
}var key="event"+qx.core.Object.toHashCode(func)+(obj?"$"+qx.core.Object.toHashCode(obj):"");
delete this.__listeners[type][key];
},
hasEventListeners:function(type){return this.__listeners&&this.__listeners[type]!==undefined&&!qx.lang.Object.isEmpty(this.__listeners[type]);
},
createDispatchEvent:function(type){if(this.hasEventListeners(type)){this.dispatchEvent(new qx.event.type.Event(type),
true);
}},
createDispatchDataEvent:function(type,
data){if(this.hasEventListeners(type)){this.dispatchEvent(new qx.event.type.DataEvent(type,
data),
true);
}},
createDispatchChangeEvent:function(type,
value,
old){if(this.hasEventListeners(type)){this.dispatchEvent(new qx.event.type.ChangeEvent(type,
value,
old),
true);
}},
dispatchEvent:function(evt,
dispose){if(this.getDisposed()){return;
}
if(evt.getTarget()==null){evt.setTarget(this);
}
if(evt.getCurrentTarget()==null){evt.setCurrentTarget(this);
}this._dispatchEvent(evt,
dispose);
var defaultPrevented=evt._defaultPrevented;
dispose&&evt.dispose();
return !defaultPrevented;
},
_dispatchEvent:function(evt){var listeners=this.__listeners;
if(listeners){evt.setCurrentTarget(this);
var typeListeners=listeners[evt.getType()];
if(typeListeners){var func,
obj;
for(var vHashCode in typeListeners){func=typeListeners[vHashCode].handler;
obj=typeListeners[vHashCode].object||this;
func.call(obj,
evt);
}}}if(evt.getBubbles()&&!evt.getPropagationStopped()&&typeof (this.getParent)=="function"){var parent=this.getParent();
if(parent&&!parent.getDisposed()&&parent.getEnabled()){parent._dispatchEvent(evt);
}}}},
destruct:function(){this._disposeObjectDeep("__listeners",
2);
}});




/* ID: qx.event.type.Event */
qx.Class.define("qx.event.type.Event",
{extend:qx.core.Object,
construct:function(vType){this.base(arguments);
this.setType(vType);
},
properties:{type:{_fast:true,
setOnlyOnce:true},
originalTarget:{_fast:true,
setOnlyOnce:true},
target:{_fast:true,
setOnlyOnce:true},
relatedTarget:{_fast:true,
setOnlyOnce:true},
currentTarget:{_fast:true},
bubbles:{_fast:true,
defaultValue:false,
noCompute:true},
propagationStopped:{_fast:true,
defaultValue:true,
noCompute:true},
defaultPrevented:{_fast:true,
defaultValue:false,
noCompute:true}},
members:{_autoDispose:false,
preventDefault:function(){this.setDefaultPrevented(true);
},
stopPropagation:function(){this.setPropagationStopped(true);
}},
destruct:function(){this._disposeFields("_valueOriginalTarget",
"_valueTarget",
"_valueRelatedTarget",
"_valueCurrentTarget");
}});




/* ID: qx.event.type.DataEvent */
qx.Class.define("qx.event.type.DataEvent",
{extend:qx.event.type.Event,
construct:function(vType,
vData){this.base(arguments,
vType);
this.setData(vData);
},
properties:{propagationStopped:{_fast:true,
defaultValue:false},
data:{_fast:true}},
destruct:function(){this._disposeFields("_valueData");
}});




/* ID: qx.event.type.ChangeEvent */
qx.Class.define("qx.event.type.ChangeEvent",
{extend:qx.event.type.Event,
construct:function(type,
value,
old){this.base(arguments,
type);
this.setValue(value);
this.setOldValue(old);
},
properties:{value:{_fast:true},
oldValue:{_fast:true}},
members:{getData:function(){return this.getValue();
}},
destruct:function(){this._disposeFields("_valueValue",
"_valueOldValue");
}});




/* ID: qx.html.EventRegistration */
qx.Class.define("qx.html.EventRegistration",
{statics:{addEventListener:qx.core.Variant.select("qx.client",
{"mshtml":function(vElement,
vType,
vFunction){vElement.attachEvent("on"+vType,
vFunction);
},
"default":function(vElement,
vType,
vFunction){vElement.addEventListener(vType,
vFunction,
false);
}}),
removeEventListener:qx.core.Variant.select("qx.client",
{"mshtml":function(vElement,
vType,
vFunction){vElement.detachEvent("on"+vType,
vFunction);
},
"default":function(vElement,
vType,
vFunction){vElement.removeEventListener(vType,
vFunction,
false);
}})}});




/* ID: qx.core.Init */
qx.Class.define("qx.core.Init",
{type:"singleton",
extend:qx.core.Target,
construct:function(){this.base(arguments);
qx.html.EventRegistration.addEventListener(window,
"load",
qx.lang.Function.bind(this._onload,
this));
qx.html.EventRegistration.addEventListener(window,
"beforeunload",
qx.lang.Function.bind(this._onbeforeunload,
this));
qx.html.EventRegistration.addEventListener(window,
"unload",
qx.lang.Function.bind(this._onunload,
this));
},
events:{"load":"qx.event.type.Event",
"beforeunload":"qx.event.type.Event",
"unload":"qx.event.type.Event"},
properties:{application:{nullable:true,
check:function(value){if(typeof value=="function"){throw new Error("The application property takes an application instance as parameter "+"and no longer a class/constructor. You may have to fix your 'index.html'.");
}return value&&qx.Class.hasInterface(value.constructor,
qx.application.IApplication);
}}},
members:{_autoDispose:false,
_onload:function(e){this.createDispatchEvent("load");
this.debug("qooxdoo "+qx.core.Version.toString());
{};
this.debug("loaded "+qx.Class.getTotalNumber()+" classes");
this.debug("loaded "+qx.Interface.getTotalNumber()+" interfaces");
this.debug("loaded "+qx.Mixin.getTotalNumber()+" mixins");
if(qx.Theme){this.debug("loaded "+qx.Theme.getTotalNumber()+" themes");
}
if(qx.locale&&qx.locale.Manager){this.debug("loaded "+qx.locale.Manager.getInstance().getAvailableLocales().length+" locales");
}var cl=qx.core.Client.getInstance();
this.debug("client: "+cl.getEngine()+"-"+cl.getMajor()+"."+cl.getMinor()+"/"+cl.getPlatform()+"/"+cl.getLocale());
this.debug("browser: "+cl.getBrowser()+"/"+(cl.supportsSvg()?"svg":cl.supportsVml()?"vml":"none"));
{if(qx.core.Variant.isSet("qx.client",
"mshtml")){if(!cl.isInQuirksMode()){this.warn("Wrong box sizing: Please modify the document's DOCTYPE!");
}}};
if(!this.getApplication()){var clazz=qx.Class.getByName(qx.core.Setting.get("qx.application"));
if(clazz){this.setApplication(new clazz(this));
}}
if(!this.getApplication()){return;
}this.debug("application: "+this.getApplication().classname);
var start=new Date;
{var app=this.getApplication();
if(app.initialize){this.warn("The 'initialize' method is no longer called automatically! Please call it manually "+"from the end of the constructor or the start of the 'main' method.");
}
if(app.finalize){this.warn("The 'finalize' method is no longer called automatically! It is save to call it at the "+"end of the 'main' method.");
}var msg="The overridden 'main' method has to be called. Please add "+"the following command at beginning of your 'main' method: "+"'this.base(arguments)'. The same is true over overridden "+"'terminate' and 'close' methods.";
var exception;
try{app.main();
}catch(ex){exception=ex;
}
if(!app._initializedMain){if(exception){this.error(msg);
}else{throw new Error(msg);
}}
if(exception){throw exception;
}};
this.info("main runtime: "+(new Date-start)+"ms");
},
_onbeforeunload:function(e){this.createDispatchEvent("beforeunload");
if(this.getApplication()){var result=this.getApplication().close();
if(result!=null){e.returnValue=result;
}}},
_onunload:function(e){this.createDispatchEvent("unload");
if(this.getApplication()){this.getApplication().terminate();
}qx.core.Object.dispose();
}},
settings:{"qx.application":"qx.application.Gui",
"qx.isSource":true},
defer:function(statics,
proto,
properties){statics.getInstance();
}});




/* ID: qx.application.IApplication */
qx.Interface.define("qx.application.IApplication",
{members:{main:function(){return true;
},
close:function(){return true;
},
terminate:function(){return true;
}}});




/* ID: qx.core.Version */
qx.Class.define("qx.core.Version",
{statics:{major:0,
minor:0,
revision:0,
state:"",
svn:0,
folder:"",
toString:function(){return this.major+"."+this.minor+(this.revision==0?"":"."+this.revision)+(this.state==""?"":"-"+this.state)+(this.svn==0?"":" (r"+this.svn+")")+(this.folder==""?"":" ["+this.folder+"]");
},
__init:function(){var vSplit=qx.core.Setting.get("qx.version").split(" ");
var vVersion=vSplit.shift();
var vInfos=vSplit.join(" ");
if(/([0-9]+)\.([0-9]+)(\.([0-9]))?(-([a-z0-9]+))?/.test(vVersion)){this.major=(RegExp.$1!=""?parseInt(RegExp.$1):0);
this.minor=(RegExp.$2!=""?parseInt(RegExp.$2):0);
this.revision=(RegExp.$4!=""?parseInt(RegExp.$4):0);
this.state=typeof RegExp.$6=="string"?RegExp.$6:"";
}
if(/(\(r([0-9]+)\))?(\s\[([a-zA-Z0-9_-]+)\])?/.test(vInfos)){this.svn=(RegExp.$2!=""?parseInt(RegExp.$2):0);
this.folder=typeof RegExp.$4=="string"?RegExp.$4:"";
}}},
settings:{"qx.version":"0.0"},
defer:function(statics){statics.__init();
}});




/* ID: qx.log.Filter */
qx.Class.define("qx.log.Filter",
{extend:qx.core.Object,
type:"abstract",
construct:function(){this.base(arguments);
},
statics:{ACCEPT:1,
DENY:2,
NEUTRAL:3},
members:{decide:function(evt){throw new Error("decide is abstract");
}}});




/* ID: qx.log.DefaultFilter */
qx.Class.define("qx.log.DefaultFilter",
{extend:qx.log.Filter,
construct:function(){this.base(arguments);
},
properties:{enabled:{check:"Boolean",
init:true},
minLevel:{check:"Number",
nullable:true}},
members:{decide:function(evt){var Filter=qx.log.Filter;
if(!this.getEnabled()){return Filter.DENY;
}else if(this.getMinLevel()==null){return Filter.NEUTRAL;
}else{return (evt.level>=this.getMinLevel())?Filter.ACCEPT:Filter.DENY;
}}}});




/* ID: qx.log.LogEventProcessor */
qx.Class.define("qx.log.LogEventProcessor",
{extend:qx.core.Object,
type:"abstract",
construct:function(){this.base(arguments);
},
members:{addFilter:function(filter){if(this._filterArr==null){this._filterArr=[];
}this._filterArr.push(filter);
},
clearFilters:function(){this._filterArr=null;
},
getHeadFilter:function(){return (this._filterArr==null||this._filterArr.length==0)?null:this._filterArr[0];
},
_getDefaultFilter:function(){var headFilter=this.getHeadFilter();
if(!(headFilter instanceof qx.log.DefaultFilter)){this.clearFilters();
headFilter=new qx.log.DefaultFilter();
this.addFilter(headFilter);
}return headFilter;
},
setEnabled:function(enabled){this._getDefaultFilter().setEnabled(enabled);
},
setMinLevel:function(minLevel){this._getDefaultFilter().setMinLevel(minLevel);
},
decideLogEvent:function(evt){var NEUTRAL=qx.log.Filter.NEUTRAL;
if(this._filterArr!=null){for(var i=0;i<this._filterArr.length;i++){var decision=this._filterArr[i].decide(evt);
if(decision!=NEUTRAL){return decision;
}}}return NEUTRAL;
},
handleLogEvent:function(evt){throw new Error("handleLogEvent is abstract");
}},
destruct:function(){this._disposeFields("_filterArr");
}});




/* ID: qx.log.appender.Abstract */
qx.Class.define("qx.log.appender.Abstract",
{extend:qx.log.LogEventProcessor,
type:"abstract",
construct:function(){this.base(arguments);
},
properties:{useLongFormat:{check:"Boolean",
init:true}},
members:{handleLogEvent:function(evt){if(this.decideLogEvent(evt)!=qx.log.Filter.DENY){this.appendLogEvent(evt);
}},
appendLogEvent:function(evt){throw new Error("appendLogEvent is abstract");
},
formatLogEvent:function(evt){var Logger=qx.log.Logger;
var text="";
var time=new String(new Date().getTime()-qx.core.Bootstrap.LOADSTART);
while(time.length<6){time="0"+time;
}text+=time;
if(this.getUseLongFormat()){switch(evt.level){case Logger.LEVEL_DEBUG:text+=" DEBUG: ";
break;
case Logger.LEVEL_INFO:text+=" INFO:  ";
break;
case Logger.LEVEL_WARN:text+=" WARN:  ";
break;
case Logger.LEVEL_ERROR:text+=" ERROR: ";
break;
case Logger.LEVEL_FATAL:text+=" FATAL: ";
break;
}}else{text+=": ";
}var indent="";
for(var i=0;i<evt.indent;i++){indent+="  ";
}text+=indent;
if(this.getUseLongFormat()){text+=evt.logger.getName();
if(evt.instanceId!=null){text+="["+evt.instanceId+"]";
}text+=": ";
}if(typeof evt.message=="string"){text+=evt.message;
}else{var obj=evt.message;
if(obj==null){text+="Object is null";
}else{text+="--- Object: "+obj+" ---\n";
var attrArr=new Array();
try{for(var attr in obj){attrArr.push(attr);
}}catch(exc){text+=indent+"  [not readable: "+exc+"]\n";
}attrArr.sort();
for(var i=0;i<attrArr.length;i++){try{text+=indent+"  "+attrArr[i]+"="+obj[attrArr[i]]+"\n";
}catch(exc){text+=indent+"  "+attrArr[i]+"=[not readable: "+exc+"]\n";
}}text+=indent+"--- End of object ---";
}}if(evt.throwable!=null){var thr=evt.throwable;
if(thr.name==null){text+=": "+thr;
}else{text+=": "+thr.name;
}
if(thr.message!=null){text+=" - "+thr.message;
}
if(thr.number!=null){text+=" (#"+thr.number+")";
}var trace=qx.dev.StackTrace.getStackTraceFromError(thr);
}
if(evt.trace){var trace=evt.trace;
}
if(trace&&trace.length>0){text+="\n";
for(var i=0;i<trace.length;i++){text+="  at "+trace[i]+"\n";
}}return text;
}}});




/* ID: qx.log.appender.Window */
qx.Class.define("qx.log.appender.Window",
{extend:qx.log.appender.Abstract,
construct:function(name){this.base(arguments);
this._id=qx.log.appender.Window.register(this);
this._name=(name==null)?"qx_log"+(new Date()).getTime():name;
this._errorsPreventingAutoCloseCount=0;
this._divDataSets=[];
this._filterTextWords=[];
this._filterText="";
},
statics:{_nextId:1,
_registeredAppenders:{},
register:function(appender){var WindowAppender=qx.log.appender.Window;
var id=WindowAppender._nextId++;
WindowAppender._registeredAppenders[id]=appender;
return id;
},
getAppender:function(id){return qx.log.appender.Window._registeredAppenders[id];
}},
properties:{maxMessages:{check:"Integer",
init:500},
popUnder:{check:"Boolean",
init:false},
autoCloseWithErrors:{check:"Boolean",
init:true,
apply:"_applyAutoCloseWithErrors"},
windowWidth:{check:"Integer",
init:600},
windowHeight:{check:"Integer",
init:350},
windowLeft:{check:"Integer",
nullable:true},
windowTop:{check:"Integer",
nullable:true}},
members:{openWindow:function(){if(this._logWindow&&!this._logWindow.closed){return ;
}var winWidth=this.getWindowWidth();
var winHeight=this.getWindowHeight();
var winLeft=this.getWindowLeft();
if(winLeft===null){winLeft=window.screen.width-winWidth;
}var winTop=this.getWindowTop();
if(winTop===null){winTop=window.screen.height-winHeight;
}var params="toolbar=no,scrollbars=no,resizable=yes,"+"width="+winWidth+",height="+winHeight+",left="+winLeft+",top="+winTop;
this._logWindow=window.open("",
this._name,
params);
if(!this._logWindow||this._logWindow.closed){if(this._popupBlockerWarning){return;
}alert("Could not open log window. Please disable your popup blocker!");
this._popupBlockerWarning=true;
return;
}this._popupBlockerWarning=false;
if(this.getPopUnder()){this._logWindow.blur();
window.focus();
}var logDocument=this._logWindow.document;
var logFix=qx.core.Variant.isSet("qx.client",
"mshtml")?'#lines { width: 100%; height: expression((document.body.offsetHeight - 30) + "px"); }':'';
logDocument.open();
logDocument.write("<html><head><title>"+this._name+"</title></head>"+'<body onload="qx = opener.qx;" onunload="try{qx.log.WindowAppender._registeredAppenders['+this._id+']._autoCloseWindow()}catch(e){}">'+'  <style type="text/css">'+'    html, body, input, pre{ font-size: 11px; font-family: Tahoma, sans-serif; line-height : 1 }'+'    html, body{ padding: 0; margin: 0; border : 0 none; }'+'    * { box-sizing: border-box; -moz-box-sizing: border-box; -webkit-box-sizing: border-box }'+'    #lines{ top: 30px; left: 0; right: 0; bottom: 0; position: absolute; overflow: auto; }'+'    #control { top: 0; left: 0; right: 0; padding: 4px 8px; background: #eee; border-bottom: 1px solid #ccc; height: 30px }'+'    pre { margin: 0; padding: 4px 8px; font-family: Consolas, "Bitstream Vera Sans Mono", monospace; }'+'    hr { border: 0 none; border-bottom: 1px solid #ccc; margin: 8px 0; padding: 0; height: 1px }'+logFix+'  </style>'+'  <div id="control">'+'    <input id="marker" type="button" value="Add divider"/> &#160; &#160; Filter: <input name="filter" id="filter" type="text" value="'+this._filterText+'">'+'  </div>'+'  <div id="lines">'+'    <pre id="log" wrap="wrap"></pre>'+'  </div>'+'</body></html>');
logDocument.close();
this._logElem=logDocument.getElementById("log");
this._markerBtn=logDocument.getElementById("marker");
this._filterInput=logDocument.getElementById("filter");
this._logLinesDiv=logDocument.getElementById("lines");
var self=this;
this._markerBtn.onclick=function(){self._showMessageInLog("<hr/>");
};
this._filterInput.onkeyup=function(){self.setFilterText(self._filterInput.value);
};
if(this._logEventQueue!=null){for(var i=0;i<this._logEventQueue.length;i++){this.appendLogEvent(this._logEventQueue[i]);
}this._logEventQueue.length=0;
}},
closeWindow:function(){if(this._logWindow!=null){this._logWindow.close();
this._logWindow=null;
this._logElem=null;
}},
_autoCloseWindow:function(){if(this.getAutoCloseWithErrors()||this._errorsPreventingAutoCloseCount==0){this.closeWindow();
}else{this._showMessageInLog("Log window message: <b>Note: "+this._errorsPreventingAutoCloseCount+" errors have been recorded, keeping log window open.</b>");
}},
_showMessageInLog:function(msg){var dummyEvent={message:msg,
isDummyEventForMessage:true};
this.appendLogEvent(dummyEvent);
},
appendLogEvent:function(evt){if(!this._logWindow||this._logWindow.closed){if(!this._logWindow||!this._logEventQueue){this._logEventQueue=[];
}this._logEventQueue.push(evt);
this.openWindow();
}else if(this._logElem==null){this._logEventQueue.push(evt);
}else{var divElem=this._logWindow.document.createElement("div");
if(evt.level>=qx.log.Logger.LEVEL_ERROR){divElem.style.backgroundColor="#FFEEEE";
if(!this.getAutoCloseWithErrors()){this._errorsPreventingAutoCloseCount+=1;
}}else if(evt.level==qx.log.Logger.LEVEL_DEBUG){divElem.style.color="gray";
}var txt;
if(evt.isDummyEventForMessage){txt=evt.message;
}else{txt=qx.html.String.fromText(this.formatLogEvent(evt));
}divElem.innerHTML=txt;
this._logElem.appendChild(divElem);
var divDataSet={txt:txt.toUpperCase(),
elem:divElem};
this._divDataSets.push(divDataSet);
this._setDivVisibility(divDataSet);
while(this._logElem.childNodes.length>this.getMaxMessages()){this._logElem.removeChild(this._logElem.firstChild);
if(this._removedMessageCount==null){this._removedMessageCount=1;
}else{this._removedMessageCount++;
}}
if(this._removedMessageCount!=null){this._logElem.firstChild.innerHTML="("+this._removedMessageCount+" messages removed)";
}this._logWindow.scrollTop=this._logElem.offsetHeight;
}},
setFilterText:function(text){if(text==null){text="";
}this._filterText=text;
text=text.toUpperCase();
this._filterTextWords=text.split(" ");
for(var divIdx=0;divIdx<this._divDataSets.length;divIdx++){this._setDivVisibility(this._divDataSets[divIdx]);
}},
_setDivVisibility:function(divDataSet){var visible=true;
for(var txtIndex=0;visible&&(txtIndex<this._filterTextWords.length);txtIndex++){visible=divDataSet.txt.indexOf(this._filterTextWords[txtIndex])>=0;
}divDataSet.elem.style["display"]=(visible?"":"none");
},
_applyAutoCloseWithErrors:function(value,
old){if(!value&&old){this._errorsPreventingAutoCloseCount=0;
this._showMessageInLog("Log window message: Starting error recording, any errors below this line will prevent the log window from closing");
}else if(value&&!old){this._showMessageInLog("Log window message: Stopping error recording, discarding "+this._errorsPreventingAutoCloseCount+" errors.");
}}},
destruct:function(){try{if(this._markerBtn){this._markerBtn.onclick=null;
}
if(this._filterInput){this._filterInput.onkeyup=null;
}}catch(ex){}this._autoCloseWindow();
}});




/* ID: qx.log.appender.FireBug */
qx.Class.define("qx.log.appender.FireBug",
{extend:qx.log.appender.Abstract,
construct:function(){this.base(arguments);
},
members:{appendLogEvent:function(evt){if(typeof console!='undefined'){var log=qx.log.Logger;
var msg=this.formatLogEvent(evt);
switch(evt.level){case log.LEVEL_DEBUG:if(console.debug){console.debug(msg);
}break;
case log.LEVEL_INFO:if(console.info){console.info(msg);
}break;
case log.LEVEL_WARN:if(console.warn){console.warn(msg);
}break;
default:if(console.error){console.error(msg);
}break;
}if(evt.level>=log.LEVEL_WARN&&(!evt.throwable||!evt.throwable.stack)&&console.trace){console.trace();
}}}}});




/* ID: qx.log.appender.Native */
qx.Class.define("qx.log.appender.Native",
{extend:qx.log.appender.Abstract,
construct:function(){this.base(arguments);
if(typeof console!='undefined'&&console.debug&&!console.emu){this._appender=new qx.log.appender.FireBug;
}else{this._appender=new qx.log.appender.Window;
}},
members:{appendLogEvent:function(evt){if(this._appender){return this._appender.appendLogEvent(evt);
}}},
destruct:function(){this._disposeObjects("_appender");
}});




/* ID: qx.log.Logger */
qx.Class.define("qx.log.Logger",
{extend:qx.log.LogEventProcessor,
construct:function(name,
parentLogger){this.base(arguments);
this._name=name;
this._parentLogger=parentLogger;
},
statics:{getClassLogger:function(clazz){var logger=clazz._logger;
if(logger==null){var classname=clazz.classname;
var splits=classname.split(".");
var currPackage=window;
var currPackageName="";
var parentLogger=qx.log.Logger.ROOT_LOGGER;
for(var i=0;i<splits.length-1;i++){currPackage=currPackage[splits[i]];
currPackageName+=((i!=0)?".":"")+splits[i];
if(currPackage._logger==null){currPackage._logger=new qx.log.Logger(currPackageName,
parentLogger);
}parentLogger=currPackage._logger;
}logger=new qx.log.Logger(classname,
parentLogger);
clazz._logger=logger;
}return logger;
},
_indent:0,
LEVEL_ALL:0,
LEVEL_DEBUG:200,
LEVEL_INFO:500,
LEVEL_WARN:600,
LEVEL_ERROR:700,
LEVEL_FATAL:800,
LEVEL_OFF:1000,
ROOT_LOGGER:null},
members:{getName:function(){return this._name;
},
getParentLogger:function(){return this._parentLogger;
},
indent:function(){qx.log.Logger._indent++;
},
unindent:function(){qx.log.Logger._indent--;
},
addAppender:function(appender){if(this._appenderArr==null){this._appenderArr=[];
}this._appenderArr.push(appender);
},
removeAppender:function(appender){if(this._appenderArr!=null){this._appenderArr.remove(appender);
}},
removeAllAppenders:function(){this._appenderArr=null;
},
handleLogEvent:function(evt){var Filter=qx.log.Filter;
var decision=Filter.NEUTRAL;
var logger=this;
while(decision==Filter.NEUTRAL&&logger!=null){decision=logger.decideLogEvent(evt);
logger=logger.getParentLogger();
}
if(decision!=Filter.DENY){this.appendLogEvent(evt);
}},
appendLogEvent:function(evt){if(this._appenderArr!=null&&this._appenderArr.length!=0){for(var i=0;i<this._appenderArr.length;i++){this._appenderArr[i].handleLogEvent(evt);
}}else if(this._parentLogger!=null){this._parentLogger.appendLogEvent(evt);
}},
log:function(level,
msg,
instanceId,
exc,
trace){var evt={logger:this,
level:level,
message:msg,
throwable:exc,
trace:trace,
indent:qx.log.Logger._indent,
instanceId:instanceId};
this.handleLogEvent(evt);
},
debug:function(msg,
instanceId,
exc){this.log(qx.log.Logger.LEVEL_DEBUG,
msg,
instanceId,
exc);
},
info:function(msg,
instanceId,
exc){this.log(qx.log.Logger.LEVEL_INFO,
msg,
instanceId,
exc);
},
warn:function(msg,
instanceId,
exc){this.log(qx.log.Logger.LEVEL_WARN,
msg,
instanceId,
exc);
},
error:function(msg,
instanceId,
exc){this.log(qx.log.Logger.LEVEL_ERROR,
msg,
instanceId,
exc);
},
fatal:function(msg,
instanceId,
exc){this.log(qx.log.Logger.LEVEL_FATAL,
msg,
instanceId,
exc);
},
measureReset:function(){if(this._totalMeasureTime!=null){this.debug("Measure reset. Total measure time: "+this._totalMeasureTime+" ms");
}this._lastMeasureTime=null;
this._totalMeasureTime=null;
},
measure:function(msg,
instanceId,
exc){if(this._lastMeasureTime==null){msg="(measure start) "+msg;
}else{var delta=new Date().getTime()-this._lastMeasureTime;
if(this._totalMeasureTime==null){this._totalMeasureTime=0;
}this._totalMeasureTime+=delta;
msg="(passed time: "+delta+" ms) "+msg;
}this.debug(msg,
instanceId,
exc);
this._lastMeasureTime=new Date().getTime();
},
printStackTrace:function(){var trace=qx.dev.StackTrace.getStackTrace();
qx.lang.Array.removeAt(trace,
0);
this.log(qx.log.Logger.LEVEL_DEBUG,
"Current stack trace",
"",
null,
trace);
}},
settings:{"qx.logAppender":"qx.log.appender.Native",
"qx.minLogLevel":200},
defer:function(statics){statics.ROOT_LOGGER=new statics("root",
null);
statics.ROOT_LOGGER.setMinLevel(qx.core.Setting.get("qx.minLogLevel"));
statics.ROOT_LOGGER.addAppender(new (qx.Class.getByName(qx.core.Setting.get("qx.logAppender"))));
},
destruct:function(){this._disposeFields("_parentLogger",
"_appenderArr");
}});




/* ID: qx.dev.StackTrace */
qx.Class.define("qx.dev.StackTrace",
{statics:{getStackTrace:qx.core.Variant.select("qx.client",
{"gecko":function(){try{throw new Error();
}catch(e){var errorTrace=this.getStackTraceFromError(e);
qx.lang.Array.removeAt(errorTrace,
0);
var callerTrace=this.getStackTraceFromCaller(arguments);
var trace=callerTrace.length>errorTrace.length?callerTrace:errorTrace;
for(var i=0;i<Math.min(callerTrace.length,
errorTrace.length);i++){callerCall=callerTrace[i];
if(callerCall.indexOf("anonymous")>=0){continue;
}callerArr=callerCall.split(":");
if(callerArr.length!=2){continue;
}var callerClassName=callerArr[0];
var methodName=callerArr[1];
var errorCall=errorTrace[i];
var errorArr=errorCall.split(":");
var errorClassName=errorArr[0];
var lineNumber=errorArr[1];
if(qx.Class.getByName(errorClassName)){var className=errorClassName;
}else{className=callerClassName;
}var line=className+":";
if(methodName){line+=methodName+":";
}line+=lineNumber;
trace[i]=line;
}return trace;
}},
"mshtml|webkit":function(){return this.getStackTraceFromCaller(arguments);
},
"opera":function(){var foo;
try{foo.bar();
}catch(e){var trace=this.getStackTraceFromError(e);
qx.lang.Array.removeAt(trace,
0);
return trace;
}return [];
}}),
getStackTraceFromCaller:qx.core.Variant.select("qx.client",
{"opera":function(args){return [];
},
"default":function(args){var trace=[];
var fcn=qx.lang.Function.getCaller(args);
var i=0;
var knownFunction={};
while(fcn){var fcnName=this.getFunctionName(fcn);
trace.push(fcnName);
fcn=fcn.caller;
if(!fcn){break;
}var hash=qx.core.Object.toHashCode(fcn);
if(knownFunction[hash]){trace.push("...");
break;
}knownFunction[hash]=fcn;
}return trace;
}}),
getStackTraceFromError:qx.core.Variant.select("qx.client",
{"gecko":function(error){if(!error.stack){return [];
}var lineRe=/@(.+):(\d+)$/gm;
var hit;
var trace=[];
while((hit=lineRe.exec(error.stack))!=null){var url=hit[1];
var lineNumber=hit[2];
var className=this.__fileNameToClassName(url);
trace.push(className+":"+lineNumber);
}return trace;
},
"webkit":function(error){if(error.sourceURL&&error.line){return [this.__fileNameToClassName(error.sourceURL)+":"+error.line];
}},
"opera":function(error){if(error.message.indexOf("Backtrace:")<0){return [];
}var trace=[];
var traceString=qx.lang.String.trim(error.message.split("Backtrace:")[1]);
var lines=traceString.split("\n");
for(var i=0;i<lines.length;i++){var reResult=lines[i].match(/\s*Line ([0-9]+) of.* (\S.*)/);
if(reResult&&reResult.length>=2){var lineNumber=reResult[1];
var fileName=this.__fileNameToClassName(reResult[2]);
trace.push(fileName+":"+lineNumber);
}}return trace;
},
"default":function(){return [];
}}),
getFunctionName:function(fcn){if(fcn.$$original){return fcn.classname+":constructor wrapper";
}
if(fcn.wrapper){return fcn.wrapper.classname+":constructor";
}
if(fcn.classname){return fcn.classname+":constructor";
}
if(fcn.mixin){for(var key in fcn.mixin.$$members){if(fcn.mixin.$$members[key]==fcn){return fcn.mixin.name+":"+key;
}}for(var key in fcn.mixin){if(fcn.mixin[key]==fcn){return fcn.mixin.name+":"+key;
}}}
if(fcn.self){var clazz=fcn.self.constructor;
if(clazz){for(var key in clazz.prototype){if(clazz.prototype[key]==fcn){return clazz.classname+":"+key;
}}for(var key in clazz){if(clazz[key]==fcn){return clazz.classname+":"+key;
}}}}var fcnReResult=fcn.toString().match(/(function\s*\w*\(.*?\))/);
if(fcnReResult&&fcnReResult.length>=1&&fcnReResult[1]){return fcnReResult[1];
}var fcnReResult=fcn.toString().match(/(function\s*\(.*?\))/);
if(fcnReResult&&fcnReResult.length>=1&&fcnReResult[1]){return "anonymous: "+fcnReResult[1];
}return 'anonymous';
},
__fileNameToClassName:function(fileName){var scriptDir="/source/class/";
var jsPos=fileName.indexOf(scriptDir);
var className=(jsPos==-1)?fileName:fileName.substring(jsPos+scriptDir.length).replace(/\//g,
".").replace(/\.js$/,
"");
return className;
}}});




/* ID: qx.html.String */
qx.Class.define("qx.html.String",
{statics:{escape:function(str){return qx.dom.String.escapeEntities(str,
qx.html.Entity.FROM_CHARCODE);
},
unescape:function(str){return qx.dom.String.unescapeEntities(str,
qx.html.Entity.TO_CHARCODE);
},
fromText:function(str){return qx.html.String.escape(str).replace(/(  |\n)/g,
function(chr){var map={"  ":" &nbsp;",
"\n":"<br>"};
return map[chr]||chr;
});
},
toText:function(str){return qx.html.String.unescape(str.replace(/\s+|<([^>])+>/gi,
function(chr){if(/\s+/.test(chr)){return " ";
}else if(/^<BR|^<br/gi.test(chr)){return "\n";
}else{return "";
}}));
}}});




/* ID: qx.dom.String */
qx.Class.define("qx.dom.String",
{statics:{escapeEntities:qx.core.Variant.select("qx.client",
{"mshtml":function(str,
charCodeToEntities){var entity,
result=[];
for(var i=0,
l=str.length;i<l;i++){var chr=str.charAt(i);
var code=chr.charCodeAt(0);
if(charCodeToEntities[code]){entity="&"+charCodeToEntities[code]+";";
}else{if(code>0x7F){entity="&#"+code+";";
}else{entity=chr;
}}result[result.length]=entity;
}return result.join("");
},
"default":function(str,
charCodeToEntities){var entity,
result="";
for(var i=0,
l=str.length;i<l;i++){var chr=str.charAt(i);
var code=chr.charCodeAt(0);
if(charCodeToEntities[code]){entity="&"+charCodeToEntities[code]+";";
}else{if(code>0x7F){entity="&#"+code+";";
}else{entity=chr;
}}result+=entity;
}return result;
}}),
unescapeEntities:function(str,
entitiesToCharCode){return str.replace(/&[#\w]+;/gi,
function(entity){var chr=entity;
var entity=entity.substring(1,
entity.length-1);
var code=entitiesToCharCode[entity];
if(code){chr=String.fromCharCode(code);
}else{if(entity.charAt(0)=='#'){if(entity.charAt(1).toUpperCase()=='X'){code=entity.substring(2);
if(code.match(/^[0-9A-Fa-f]+$/gi)){chr=String.fromCharCode(parseInt("0x"+code));
}}else{code=entity.substring(1);
if(code.match(/^\d+$/gi)){chr=String.fromCharCode(parseInt(code));
}}}}return chr;
});
},
stripTags:function(str){return str.replace(/<\/?[^>]+>/gi,
"");
}}});




/* ID: qx.html.Entity */
qx.Class.define("qx.html.Entity",
{statics:{TO_CHARCODE:{"quot":34,
"amp":38,
"lt":60,
"gt":62,
"nbsp":160,
"iexcl":161,
"cent":162,
"pound":163,
"curren":164,
"yen":165,
"brvbar":166,
"sect":167,
"uml":168,
"copy":169,
"ordf":170,
"laquo":171,
"not":172,
"shy":173,
"reg":174,
"macr":175,
"deg":176,
"plusmn":177,
"sup2":178,
"sup3":179,
"acute":180,
"micro":181,
"para":182,
"middot":183,
"cedil":184,
"sup1":185,
"ordm":186,
"raquo":187,
"frac14":188,
"frac12":189,
"frac34":190,
"iquest":191,
"Agrave":192,
"Aacute":193,
"Acirc":194,
"Atilde":195,
"Auml":196,
"Aring":197,
"AElig":198,
"Ccedil":199,
"Egrave":200,
"Eacute":201,
"Ecirc":202,
"Euml":203,
"Igrave":204,
"Iacute":205,
"Icirc":206,
"Iuml":207,
"ETH":208,
"Ntilde":209,
"Ograve":210,
"Oacute":211,
"Ocirc":212,
"Otilde":213,
"Ouml":214,
"times":215,
"Oslash":216,
"Ugrave":217,
"Uacute":218,
"Ucirc":219,
"Uuml":220,
"Yacute":221,
"THORN":222,
"szlig":223,
"agrave":224,
"aacute":225,
"acirc":226,
"atilde":227,
"auml":228,
"aring":229,
"aelig":230,
"ccedil":231,
"egrave":232,
"eacute":233,
"ecirc":234,
"euml":235,
"igrave":236,
"iacute":237,
"icirc":238,
"iuml":239,
"eth":240,
"ntilde":241,
"ograve":242,
"oacute":243,
"ocirc":244,
"otilde":245,
"ouml":246,
"divide":247,
"oslash":248,
"ugrave":249,
"uacute":250,
"ucirc":251,
"uuml":252,
"yacute":253,
"thorn":254,
"yuml":255,
"fnof":402,
"Alpha":913,
"Beta":914,
"Gamma":915,
"Delta":916,
"Epsilon":917,
"Zeta":918,
"Eta":919,
"Theta":920,
"Iota":921,
"Kappa":922,
"Lambda":923,
"Mu":924,
"Nu":925,
"Xi":926,
"Omicron":927,
"Pi":928,
"Rho":929,
"Sigma":931,
"Tau":932,
"Upsilon":933,
"Phi":934,
"Chi":935,
"Psi":936,
"Omega":937,
"alpha":945,
"beta":946,
"gamma":947,
"delta":948,
"epsilon":949,
"zeta":950,
"eta":951,
"theta":952,
"iota":953,
"kappa":954,
"lambda":955,
"mu":956,
"nu":957,
"xi":958,
"omicron":959,
"pi":960,
"rho":961,
"sigmaf":962,
"sigma":963,
"tau":964,
"upsilon":965,
"phi":966,
"chi":967,
"psi":968,
"omega":969,
"thetasym":977,
"upsih":978,
"piv":982,
"bull":8226,
"hellip":8230,
"prime":8242,
"Prime":8243,
"oline":8254,
"frasl":8260,
"weierp":8472,
"image":8465,
"real":8476,
"trade":8482,
"alefsym":8501,
"larr":8592,
"uarr":8593,
"rarr":8594,
"darr":8595,
"harr":8596,
"crarr":8629,
"lArr":8656,
"uArr":8657,
"rArr":8658,
"dArr":8659,
"hArr":8660,
"forall":8704,
"part":8706,
"exist":8707,
"empty":8709,
"nabla":8711,
"isin":8712,
"notin":8713,
"ni":8715,
"prod":8719,
"sum":8721,
"minus":8722,
"lowast":8727,
"radic":8730,
"prop":8733,
"infin":8734,
"ang":8736,
"and":8743,
"or":8744,
"cap":8745,
"cup":8746,
"int":8747,
"there4":8756,
"sim":8764,
"cong":8773,
"asymp":8776,
"ne":8800,
"equiv":8801,
"le":8804,
"ge":8805,
"sub":8834,
"sup":8835,
"sube":8838,
"supe":8839,
"oplus":8853,
"otimes":8855,
"perp":8869,
"sdot":8901,
"lceil":8968,
"rceil":8969,
"lfloor":8970,
"rfloor":8971,
"lang":9001,
"rang":9002,
"loz":9674,
"spades":9824,
"clubs":9827,
"hearts":9829,
"diams":9830,
"OElig":338,
"oelig":339,
"Scaron":352,
"scaron":353,
"Yuml":376,
"circ":710,
"tilde":732,
"ensp":8194,
"emsp":8195,
"thinsp":8201,
"zwnj":8204,
"zwj":8205,
"lrm":8206,
"rlm":8207,
"ndash":8211,
"mdash":8212,
"lsquo":8216,
"rsquo":8217,
"sbquo":8218,
"ldquo":8220,
"rdquo":8221,
"bdquo":8222,
"dagger":8224,
"Dagger":8225,
"permil":8240,
"lsaquo":8249,
"rsaquo":8250,
"euro":8364}},
defer:function(statics,
members,
properties){statics.FROM_CHARCODE=qx.lang.Object.invert(statics.TO_CHARCODE);
}});




/* ID: qx.Theme */
qx.Class.define("qx.Theme",
{statics:{define:function(name,
config){if(!config){var config={};
}
if(config.include&&!(config.include instanceof Array)){config.include=[config.include];
}{this.__validateConfig(name,
config);
};
var theme={$$type:"Theme",
name:name,
title:config.title,
type:config.type||"normal",
toString:this.genericToString};
if(config.extend){theme.supertheme=config.extend;
}theme.basename=qx.Class.createNamespace(name,
theme);
this.__convert(theme,
config);
this.__registry[name]=theme;
if(config.include){for(var i=0,
a=config.include,
l=a.length;i<l;i++){this.include(theme,
a[i]);
}}},
getAll:function(){return this.__registry;
},
getByName:function(name){return this.__registry[name];
},
isDefined:function(name){return this.getByName(name)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.__registry);
},
genericToString:function(){return "[Theme "+this.name+"]";
},
__extractInheritableKey:function(config){for(var i=0,
keys=this.__inheritableKeys,
l=keys.length;i<l;i++){if(config[keys[i]]){return keys[i];
}}},
__convert:function(theme,
config){var keyCurrent=this.__extractInheritableKey(config);
if(config.extend){var keyExtended=this.__extractInheritableKey(config.extend);
if(!keyCurrent){keyCurrent=keyExtended;
}}if(!keyCurrent){return;
}var clazz=function(){};
if(config.extend){clazz.prototype=new config.extend.$$clazz;
}var target=clazz.prototype;
var source=config[keyCurrent];
for(var id in source){target[id]=source[id];
}theme.$$clazz=clazz;
theme[keyCurrent]=new clazz;
},
__registry:{},
__inheritableKeys:["colors",
"borders",
"fonts",
"icons",
"widgets",
"appearances",
"meta"],
__allowedKeys:{"title":"string",
"type":"string",
"extend":"object",
"colors":"object",
"borders":"object",
"fonts":"object",
"icons":"object",
"widgets":"object",
"appearances":"object",
"meta":"object",
"include":"object"},
__metaKeys:{"color":"object",
"border":"object",
"font":"object",
"widget":"object",
"icon":"object",
"appearance":"object"},
__validateConfig:function(name,
config){var allowed=this.__allowedKeys;
for(var key in config){if(allowed[key]===undefined){throw new Error('The configuration key "'+key+'" in theme "'+name+'" is not allowed!');
}
if(config[key]==null){throw new Error('Invalid key "'+key+'" in theme "'+name+'"! The value is undefined/null!');
}
if(allowed[key]!==null&&typeof config[key]!==allowed[key]){throw new Error('Invalid type of key "'+key+'" in theme "'+name+'"! The type of the key must be "'+allowed[key]+'"!');
}}if(config.title===undefined){throw new Error("Missing title definition in theme: "+name);
}var maps=["colors",
"borders",
"fonts",
"icons",
"widgets",
"appearances",
"meta"];
for(var i=0,
l=maps.length;i<l;i++){var key=maps[i];
if(config[key]!==undefined&&(config[key] instanceof Array||config[key] instanceof RegExp||config[key] instanceof Date||config[key].classname!==undefined)){throw new Error('Invalid key "'+key+'" in theme "'+name+'"! The value needs to be a map!');
}}var counter=0;
for(var i=0,
l=maps.length;i<l;i++){var key=maps[i];
if(config[key]){counter++;
}
if(counter>1){throw new Error("You can only define one theme category per file! Invalid theme: "+name);
}}if(!config.extend&&counter===0){throw new Error("You must define at least one entry in your theme configuration :"+name);
}if(config.meta){var value;
for(var key in config.meta){value=config.meta[key];
if(this.__metaKeys[key]===undefined){throw new Error('The key "'+key+'" is not allowed inside a meta theme block.');
}
if(typeof value!==this.__metaKeys[key]){throw new Error('The type of the key "'+key+'" inside the meta block is wrong.');
}
if(!(typeof value==="object"&&value!==null&&value.$$type==="Theme")){throw new Error('The content of a meta theme must reference to other themes. The value for "'+key+'" in theme "'+name+'" is invalid: '+value);
}}}if(config.extend&&config.extend.$$type!=="Theme"){throw new Error('Invalid extend in theme "'+name+'": '+config.extend);
}},
patch:function(theme,
mixinTheme){var keyCurrent=this.__extractInheritableKey(mixinTheme);
if(keyCurrent!==this.__extractInheritableKey(mixinTheme)){throw new Error("The mixins '"+theme.name+"' are not compatible '"+mixinTheme.name+"'!");
}var source=mixinTheme[keyCurrent];
var target=theme[keyCurrent];
for(var key in source){target[key]=source[key];
}},
include:function(theme,
mixinTheme){var keyCurrent=this.__extractInheritableKey(mixinTheme);
if(keyCurrent!==this.__extractInheritableKey(mixinTheme)){throw new Error("The mixins '"+theme.name+"' are not compatible '"+mixinTheme.name+"'!");
}var source=mixinTheme[keyCurrent];
var target=theme[keyCurrent];
for(var key in source){if(target[key]!==undefined){throw new Error("It is not allowed to overwrite the key '"+key+"' of theme '"+theme.name+"' by mixin theme '"+mixinTheme.name+"'.");
}target[key]=source[key];
}}}});




/* ID: qx.application.Basic */
qx.Class.define("qx.application.Basic",
{extend:qx.core.Target,
implement:qx.application.IApplication,
members:{main:function(){this._initializedMain=true;
},
close:function(){},
terminate:function(){}}});




/* ID: qx.locale.Locale */
qx.Class.define("qx.locale.Locale",
{statics:{define:function(name,
config){qx.locale.Manager.getInstance().addTranslationFromClass(name,
config);
}}});




/* ID: qx.util.manager.Value */
qx.Class.define("qx.util.manager.Value",
{type:"abstract",
extend:qx.core.Target,
construct:function(){this.base(arguments);
this._registry={};
this._dynamic={};
},
members:{connect:function(callback,
obj,
value){{if(!callback){throw new Error("Can not connect to invalid callback: "+callback);
}
if(!obj){throw new Error("Can not connect to invalid object: "+obj);
}
if(value===undefined){throw new Error("Undefined values are not allowed for connect: "+callback+"["+obj+"]");
}
if(typeof value==="boolean"){throw new Error("Boolean values are not allowed for connect: "+callback+"["+obj+"]");
}};
var key="v"+obj.toHashCode()+"$"+qx.core.Object.toHashCode(callback);
var reg=this._registry;
if(value!==null&&this._preprocess){value=this._preprocess(value);
}if(this.isDynamic(value)){reg[key]={callback:callback,
object:obj,
value:value};
}else if(reg[key]){delete reg[key];
}callback.call(obj,
this.resolveDynamic(value)||value);
},
resolveDynamic:function(value){return this._dynamic[value];
},
isDynamic:function(value){return this._dynamic[value]!==undefined;
},
_updateObjects:function(){var reg=this._registry;
var entry;
for(var key in reg){entry=reg[key];
entry.callback.call(entry.object,
this.resolveDynamic(entry.value));
}}},
destruct:function(){this._disposeFields("_registry",
"_dynamic");
}});




/* ID: qx.locale.Manager */
qx.Class.define("qx.locale.Manager",
{type:"singleton",
extend:qx.util.manager.Value,
construct:function(){this.base(arguments);
this._translationCatalog={};
this.setLocale(qx.core.Client.getInstance().getLocale()||this._defaultLocale);
},
statics:{tr:function(messageId,
varargs){var args=qx.lang.Array.fromArguments(arguments);
args.splice(0,
1);
return new qx.locale.LocalizedString(messageId,
args);
},
trn:function(singularMessageId,
pluralMessageId,
count,
varargs){var args=qx.lang.Array.fromArguments(arguments);
args.splice(0,
3);
if(count>1){return new qx.locale.LocalizedString(pluralMessageId,
args);
}else{return new qx.locale.LocalizedString(singularMessageId,
args);
}},
trc:function(hint,
messageId,
varargs){var args=qx.lang.Array.fromArguments(arguments);
args.splice(0,
2);
return new qx.locale.LocalizedString(messageId,
args);
},
marktr:function(messageId){return messageId;
}},
properties:{locale:{check:"String",
nullable:true,
apply:"_applyLocale",
event:"changeLocale"}},
members:{_defaultLocale:"C",
getLanguage:function(){return this._language;
},
getTerritory:function(){return this.getLocale().split("_")[1]||"";
},
getAvailableLocales:function(){var locales=[];
for(var locale in this._translationCatalog){if(locale!=this._defaultLocale){locales.push(locale);
}}return locales;
},
_extractLanguage:function(locale){var language;
var pos=locale.indexOf("_");
if(pos==-1){language=locale;
}else{language=locale.substring(0,
pos);
}return language;
},
_applyLocale:function(value,
old){this._locale=value;
var pos=value.indexOf("_");
this._language=this._extractLanguage(value);
this._updateObjects();
},
addTranslation:function(languageCode,
translationMap){if(this._translationCatalog[languageCode]){for(var key in translationMap){this._translationCatalog[languageCode][key]=translationMap[key];
}}else{this._translationCatalog[languageCode]=translationMap;
}},
addTranslationFromClass:function(classname,
translationMap){this.addTranslation(classname.substring(classname.lastIndexOf(".")+1),
translationMap);
},
translate:function(messageId,
args,
locale){var txt;
if(locale){var language=this._extractLanguage(locale);
}else{locale=this._locale;
language=this._language;
}
if(!txt&&this._translationCatalog[locale]){txt=this._translationCatalog[locale][messageId];
}
if(!txt&&this._translationCatalog[language]){txt=this._translationCatalog[language][messageId];
}
if(!txt&&this._translationCatalog[this._defaultLocale]){txt=this._translationCatalog[this._defaultLocale][messageId];
}
if(!txt){txt=messageId;
}
if(args.length>0){txt=qx.lang.String.format(txt,
args);
}return txt;
},
isDynamic:function(text){return text instanceof qx.locale.LocalizedString;
},
resolveDynamic:function(text){return text.toString();
}},
destruct:function(){this._disposeFields("_translationCatalog");
}});




/* ID: qx.locale.LocalizedString */
qx.Class.define("qx.locale.LocalizedString",
{extend:qx.core.Object,
construct:function(messageId,
args,
locale){this.base(arguments);
this.setId(messageId);
this._locale=locale;
var storedArguments=[];
for(var i=0;i<args.length;i++){var arg=args[i];
if(arg instanceof qx.locale.LocalizedString){storedArguments.push(arg);
}else{storedArguments.push(arg+"");
}}this.setArgs(storedArguments);
},
properties:{id:{check:"String",
nullable:true},
args:{nullable:true,
dispose:true}},
members:{toString:function(){return qx.locale.Manager.getInstance().translate(this.getId(),
this.getArgs(),
this._locale);
}}});




/* ID: qx.util.Validation */
qx.Class.define("qx.util.Validation",
{statics:{isValid:function(v){switch(typeof v){case "undefined":return false;
case "object":return v!==null;
case "string":return v!=="";
case "number":return !isNaN(v);
case "function":case "boolean":return true;
}return false;
},
isInvalid:function(v){switch(typeof v){case "undefined":return true;
case "object":return v===null;
case "string":return v==="";
case "number":return isNaN(v);
case "function":case "boolean":return false;
}return true;
},
isValidNumber:function(v){return typeof v==="number"&&!isNaN(v);
},
isInvalidNumber:function(v){return typeof v!=="number"||isNaN(v);
},
isValidString:function(v){return typeof v==="string"&&v!=="";
},
isInvalidString:function(v){return typeof v!=="string"||v==="";
},
isValidArray:function(v){return typeof v==="object"&&v!==null&&v instanceof Array;
},
isInvalidArray:function(v){return typeof v!=="object"||v===null||!(v instanceof Array);
},
isValidObject:function(v){return typeof v==="object"&&v!==null&&!(v instanceof Array);
},
isInvalidObject:function(v){return typeof v!=="object"||v===null||v instanceof Array;
},
isValidNode:function(v){return typeof v==="object"&&v!==null;
},
isInvalidNode:function(v){return typeof v!=="object"||v===null;
},
isValidElement:function(v){return typeof v==="object"&&v!==null||v.nodeType!==1;
},
isInvalidElement:function(v){return typeof v!=="object"||v===null||v.nodeType!==1;
},
isValidFunction:function(v){return typeof v==="function";
},
isInvalidFunction:function(v){return typeof v!=="function";
},
isValidBoolean:function(v){return typeof v==="boolean";
},
isInvalidBoolean:function(v){return typeof v!=="boolean";
},
isValidStringOrNumber:function(v){switch(typeof v){case "string":return v!=="";
case "number":return !isNaN(v);
}return false;
},
isInvalidStringOrNumber:function(v){switch(typeof v){case "string":return v==="";
case "number":return isNaN(v);
}return false;
}}});




/* ID: qx.application.Gui */
qx.Class.define("qx.application.Gui",
{extend:qx.core.Target,
implement:qx.application.IApplication,
properties:{uiReady:{check:"Boolean",
init:false}},
members:{main:function(){this._initializedMain=true;
qx.ui.core.Widget.initScrollbarWidth();
qx.theme.manager.Meta.getInstance().initialize();
qx.event.handler.EventHandler.getInstance();
qx.ui.core.ClientDocument.getInstance();
qx.client.Timer.once(this._preload,
this,
0);
},
close:function(){},
terminate:function(){},
_preload:function(){this.debug("preloading visible images...");
this.__preloader=new qx.io.image.PreloaderSystem(qx.io.image.Manager.getInstance().getVisibleImages(),
this._preloaderDone,
this);
this.__preloader.start();
},
_preloaderDone:function(){this.setUiReady(true);
this.__preloader.dispose();
this.__preloader=null;
var start=(new Date).valueOf();
qx.ui.core.Widget.flushGlobalQueues();
this.info("render runtime: "+(new Date-start)+"ms");
qx.event.handler.EventHandler.getInstance().attachEvents();
qx.client.Timer.once(this._postload,
this,
100);
},
_postload:function(){this.debug("preloading hidden images...");
this.__postloader=new qx.io.image.PreloaderSystem(qx.io.image.Manager.getInstance().getHiddenImages(),
this._postloaderDone,
this);
this.__postloader.start();
},
_postloaderDone:function(){this.__postloader.dispose();
this.__postloader=null;
}}});




/* ID: qx.ui.core.Widget */
qx.Class.define("qx.ui.core.Widget",
{extend:qx.core.Target,
type:"abstract",
construct:function(){this.base(arguments);
this._layoutChanges={};
},
events:{"beforeAppear":"qx.event.type.Event",
"appear":"qx.event.type.Event",
"beforeDisappear":"qx.event.type.Event",
"disappear":"qx.event.type.Event",
"beforeInsertDom":"qx.event.type.Event",
"insertDom":"qx.event.type.Event",
"beforeRemoveDom":"qx.event.type.Event",
"removeDom":"qx.event.type.Event",
"create":"qx.event.type.Event",
"execute":"qx.event.type.Event",
"mouseover":"qx.event.type.MouseEvent",
"mousemove":"qx.event.type.MouseEvent",
"mouseout":"qx.event.type.MouseEvent",
"mousedown":"qx.event.type.MouseEvent",
"mouseup":"qx.event.type.MouseEvent",
"mousewheel":"qx.event.type.MouseEvent",
"click":"qx.event.type.MouseEvent",
"dblclick":"qx.event.type.MouseEvent",
"contextmenu":"qx.event.type.MouseEvent",
"keydown":"qx.event.type.KeyEvent",
"keypress":"qx.event.type.KeyEvent",
"keyinput":"qx.event.type.KeyEvent",
"keyup":"qx.event.type.KeyEvent",
"focusout":"qx.event.type.FocusEvent",
"focusin":"qx.event.type.FocusEvent",
"blur":"qx.event.type.FocusEvent",
"focus":"qx.event.type.FocusEvent",
"dragdrop":"qx.event.type.DragEvent",
"dragout":"qx.event.type.DragEvent",
"dragover":"qx.event.type.DragEvent",
"dragmove":"qx.event.type.DragEvent",
"dragstart":"qx.event.type.DragEvent",
"dragend":"qx.event.type.DragEvent"},
statics:{create:function(clazz,
appearance){clazz._appearance=appearance;
return new clazz;
},
SCROLLBAR_SIZE:null,
_autoFlushTimeout:null,
_initAutoFlush:function(){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._autoFlushTimeout=window.setTimeout(qx.ui.core.Widget._autoFlushHelper,
0);
}},
_removeAutoFlush:function(){if(qx.ui.core.Widget._autoFlushTimeout!=null){window.clearTimeout(qx.ui.core.Widget._autoFlushTimeout);
qx.ui.core.Widget._autoFlushTimeout=null;
}},
_autoFlushHelper:function(){qx.ui.core.Widget._autoFlushTimeout=null;
if(!qx.core.Object.inGlobalDispose()){qx.ui.core.Widget.flushGlobalQueues();
}},
flushGlobalQueues:function(){if(qx.ui.core.Widget._autoFlushTimeout!=null){qx.ui.core.Widget._removeAutoFlush();
}
if(qx.ui.core.Widget._inFlushGlobalQueues||!qx.core.Init.getInstance().getApplication().getUiReady()){return;
}qx.ui.core.Widget._inFlushGlobalQueues=true;
qx.ui.core.Widget.flushGlobalWidgetQueue();
qx.ui.core.Widget.flushGlobalStateQueue();
qx.ui.core.Widget.flushGlobalElementQueue();
qx.ui.core.Widget.flushGlobalJobQueue();
qx.ui.core.Widget.flushGlobalLayoutQueue();
qx.ui.core.Widget.flushGlobalDisplayQueue();
delete qx.ui.core.Widget._inFlushGlobalQueues;
},
_globalWidgetQueue:[],
addToGlobalWidgetQueue:function(vWidget){if(!vWidget._isInGlobalWidgetQueue&&vWidget._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}qx.ui.core.Widget._globalWidgetQueue.push(vWidget);
vWidget._isInGlobalWidgetQueue=true;
}},
removeFromGlobalWidgetQueue:function(vWidget){if(vWidget._isInGlobalWidgetQueue){qx.lang.Array.remove(qx.ui.core.Widget._globalWidgetQueue,
vWidget);
delete vWidget._isInGlobalWidgetQueue;
}},
flushGlobalWidgetQueue:function(){var vQueue=qx.ui.core.Widget._globalWidgetQueue,
vLength,
vWidget;
while((vLength=vQueue.length)>0){for(var i=0;i<vLength;i++){vWidget=vQueue[i];
vWidget.flushWidgetQueue();
delete vWidget._isInGlobalWidgetQueue;
}vQueue.splice(0,
vLength);
}},
_globalElementQueue:[],
addToGlobalElementQueue:function(vWidget){if(!vWidget._isInGlobalElementQueue&&vWidget._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}qx.ui.core.Widget._globalElementQueue.push(vWidget);
vWidget._isInGlobalElementQueue=true;
}},
removeFromGlobalElementQueue:function(vWidget){if(vWidget._isInGlobalElementQueue){qx.lang.Array.remove(qx.ui.core.Widget._globalElementQueue,
vWidget);
delete vWidget._isInGlobalElementQueue;
}},
flushGlobalElementQueue:function(){var vQueue=qx.ui.core.Widget._globalElementQueue,
vLength,
vWidget;
while((vLength=vQueue.length)>0){for(var i=0;i<vLength;i++){vWidget=vQueue[i];
vWidget._createElementImpl();
delete vWidget._isInGlobalElementQueue;
}vQueue.splice(0,
vLength);
}},
_globalStateQueue:[],
addToGlobalStateQueue:function(vWidget){if(!vWidget._isInGlobalStateQueue&&vWidget._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}qx.ui.core.Widget._globalStateQueue.push(vWidget);
vWidget._isInGlobalStateQueue=true;
}},
removeFromGlobalStateQueue:function(vWidget){if(vWidget._isInGlobalStateQueue){qx.lang.Array.remove(qx.ui.core.Widget._globalStateQueue,
vWidget);
delete vWidget._isInGlobalStateQueue;
}},
flushGlobalStateQueue:function(){var vQueue=qx.ui.core.Widget._globalStateQueue,
vLength,
vWidget;
while((vLength=vQueue.length)>0){for(var i=0;i<vLength;i++){vWidget=vQueue[i];
vWidget._renderAppearance();
delete vWidget._isInGlobalStateQueue;
}vQueue.splice(0,
vLength);
}},
_globalJobQueue:[],
addToGlobalJobQueue:function(vWidget){if(!vWidget._isInGlobalJobQueue&&vWidget._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}qx.ui.core.Widget._globalJobQueue.push(vWidget);
vWidget._isInGlobalJobQueue=true;
}},
removeFromGlobalJobQueue:function(vWidget){if(vWidget._isInGlobalJobQueue){qx.lang.Array.remove(qx.ui.core.Widget._globalJobQueue,
vWidget);
delete vWidget._isInGlobalJobQueue;
}},
flushGlobalJobQueue:function(){var vQueue=qx.ui.core.Widget._globalJobQueue,
vLength,
vWidget;
while((vLength=vQueue.length)>0){for(var i=0;i<vLength;i++){vWidget=vQueue[i];
vWidget._flushJobQueue(vWidget._jobQueue);
delete vWidget._isInGlobalJobQueue;
}vQueue.splice(0,
vLength);
}},
_globalLayoutQueue:[],
addToGlobalLayoutQueue:function(vParent){if(!vParent._isInGlobalLayoutQueue&&vParent._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}qx.ui.core.Widget._globalLayoutQueue.push(vParent);
vParent._isInGlobalLayoutQueue=true;
}},
removeFromGlobalLayoutQueue:function(vParent){if(vParent._isInGlobalLayoutQueue){qx.lang.Array.remove(qx.ui.core.Widget._globalLayoutQueue,
vParent);
delete vParent._isInGlobalLayoutQueue;
}},
flushGlobalLayoutQueue:function(){var vQueue=qx.ui.core.Widget._globalLayoutQueue,
vLength,
vParent;
while((vLength=vQueue.length)>0){for(var i=0;i<vLength;i++){vParent=vQueue[i];
vParent._flushChildrenQueue();
delete vParent._isInGlobalLayoutQueue;
}vQueue.splice(0,
vLength);
}},
_fastGlobalDisplayQueue:[],
_lazyGlobalDisplayQueues:{},
addToGlobalDisplayQueue:function(vWidget){if(!vWidget._isInGlobalDisplayQueue&&vWidget._isDisplayable){if(qx.ui.core.Widget._autoFlushTimeout==null){qx.ui.core.Widget._initAutoFlush();
}var vParent=vWidget.getParent();
if(vParent.isSeeable()){var vKey=vParent.toHashCode();
if(qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey]){qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey].push(vWidget);
}else{qx.ui.core.Widget._lazyGlobalDisplayQueues[vKey]=[vWidget];
}}else{qx.ui.core.Widget._fastGlobalDisplayQueue.push(vWidget);
}vWidget._isInGlobalDisplayQueue=true;
}},
removeFromGlobalDisplayQueue:function(vWidget){},
flushGlobalDisplayQueue:function(){var vKey,
vLazyQueue,
vWidget,
vFragment;
var vFastQueue=qx.ui.core.Widget._fastGlobalDisplayQueue;
var vLazyQueues=qx.ui.core.Widget._lazyGlobalDisplayQueues;
for(var i=0,
l=vFastQueue.length;i<l;i++){vWidget=vFastQueue[i];
vWidget.getParent()._getTargetNode().appendChild(vWidget.getElement());
}if(qx.Class.isDefined("qx.ui.basic.Inline")){for(vKey in vLazyQueues){vLazyQueue=vLazyQueues[vKey];
for(var i=0;i<vLazyQueue.length;i++){vWidget=vLazyQueue[i];
if(vWidget instanceof qx.ui.basic.Inline){vWidget._beforeInsertDom();
try{document.getElementById(vWidget.getInlineNodeId()).appendChild(vWidget.getElement());
}catch(ex){vWidget.debug("Could not append to inline id: "+vWidget.getInlineNodeId(),
ex);
}vWidget._afterInsertDom();
vWidget._afterAppear();
qx.lang.Array.remove(vLazyQueue,
vWidget);
i--;
delete vWidget._isInGlobalDisplayQueue;
}}}}for(vKey in vLazyQueues){vLazyQueue=vLazyQueues[vKey];
if(document.createDocumentFragment&&vLazyQueue.length>=3){vFragment=document.createDocumentFragment();
for(var i=0,
l=vLazyQueue.length;i<l;i++){vWidget=vLazyQueue[i];
vWidget._beforeInsertDom();
vFragment.appendChild(vWidget.getElement());
}vLazyQueue[0].getParent()._getTargetNode().appendChild(vFragment);
for(var i=0,
l=vLazyQueue.length;i<l;i++){vWidget=vLazyQueue[i];
vWidget._afterInsertDom();
}}else{for(var i=0,
l=vLazyQueue.length;i<l;i++){vWidget=vLazyQueue[i];
vWidget._beforeInsertDom();
vWidget.getParent()._getTargetNode().appendChild(vWidget.getElement());
vWidget._afterInsertDom();
}}}for(vKey in vLazyQueues){vLazyQueue=vLazyQueues[vKey];
for(var i=0,
l=vLazyQueue.length;i<l;i++){vWidget=vLazyQueue[i];
if(vWidget.getVisibility()){vWidget._afterAppear();
}delete vWidget._isInGlobalDisplayQueue;
}delete vLazyQueues[vKey];
}for(var i=0,
l=vFastQueue.length;i<l;i++){delete vFastQueue[i]._isInGlobalDisplayQueue;
}qx.lang.Array.removeAll(vFastQueue);
},
getActiveSiblingHelperIgnore:function(vIgnoreClasses,
vInstance){for(var j=0;j<vIgnoreClasses.length;j++){if(vInstance instanceof vIgnoreClasses[j]){return true;
}}return false;
},
getActiveSiblingHelper:function(vObject,
vParent,
vCalc,
vIgnoreClasses,
vMode){if(!vIgnoreClasses){vIgnoreClasses=[];
}var vChilds=vParent.getChildren();
var vPosition=vMode==null?vChilds.indexOf(vObject)+vCalc:vMode==="first"?0:vChilds.length-1;
var vInstance=vChilds[vPosition];
while(vInstance&&(!vInstance.getEnabled()||qx.ui.core.Widget.getActiveSiblingHelperIgnore(vIgnoreClasses,
vInstance))){vPosition+=vCalc;
vInstance=vChilds[vPosition];
if(!vInstance){return null;
}}return vInstance;
},
__initApplyMethods:function(members){var applyRuntime="_renderRuntime";
var resetRuntime="_resetRuntime";
var style="this._style.";
var cssValue="=((v==null)?0:v)+'px'";
var parameter="v";
var properties=["left",
"right",
"top",
"bottom",
"width",
"height",
"minWidth",
"maxWidth",
"minHeight",
"maxHeight"];
var propertiesUpper=["Left",
"Right",
"Top",
"Bottom",
"Width",
"Height",
"MinWidth",
"MaxWidth",
"MinHeight",
"MaxHeight"];
var applyMargin=applyRuntime+"Margin";
var resetMargin=resetRuntime+"Margin";
var styleMargin=style+"margin";
for(var i=0;i<4;i++){members[applyMargin+propertiesUpper[i]]=new Function(parameter,
styleMargin+propertiesUpper[i]+cssValue);
members[resetMargin+propertiesUpper[i]]=new Function(styleMargin+propertiesUpper[i]+"=''");
}var applyPadding=applyRuntime+"Padding";
var resetPadding=resetRuntime+"Padding";
var stylePadding=style+"padding";
if(qx.core.Variant.isSet("qx.client",
"gecko")){for(var i=0;i<4;i++){members[applyPadding+propertiesUpper[i]]=new Function(parameter,
stylePadding+propertiesUpper[i]+cssValue);
members[resetPadding+propertiesUpper[i]]=new Function(stylePadding+propertiesUpper[i]+"=''");
}}else{for(var i=0;i<4;i++){members[applyPadding+propertiesUpper[i]]=new Function(parameter,
"this.setStyleProperty('padding"+propertiesUpper[i]+"', ((v==null)?0:v)+'px')");
members[resetPadding+propertiesUpper[i]]=new Function("this.removeStyleProperty('padding"+propertiesUpper[i]+"')");
}}for(var i=0;i<properties.length;i++){members[applyRuntime+propertiesUpper[i]]=new Function(parameter,
style+properties[i]+cssValue);
members[resetRuntime+propertiesUpper[i]]=new Function(style+properties[i]+"=''");
}},
TYPE_NULL:0,
TYPE_PIXEL:1,
TYPE_PERCENT:2,
TYPE_AUTO:3,
TYPE_FLEX:4,
layoutPropertyTypes:{},
__initLayoutProperties:function(statics){var a=["width",
"height",
"minWidth",
"maxWidth",
"minHeight",
"maxHeight",
"left",
"right",
"top",
"bottom"];
for(var i=0,
l=a.length,
p,
b,
t;i<l;i++){p=a[i];
b="_computed"+qx.lang.String.toFirstUp(p);
t=b+"Type";
statics.layoutPropertyTypes[p]={dataType:t,
dataParsed:b+"Parsed",
dataValue:b+"Value",
typePixel:t+"Pixel",
typePercent:t+"Percent",
typeAuto:t+"Auto",
typeFlex:t+"Flex",
typeNull:t+"Null"};
}},
initScrollbarWidth:function(){var t=document.createElement("div");
var s=t.style;
s.height=s.width="100px";
s.overflow="scroll";
document.body.appendChild(t);
var c=qx.html.Dimension.getScrollBarSizeRight(t);
qx.ui.core.Widget.SCROLLBAR_SIZE=c?c:16;
document.body.removeChild(t);
}},
properties:{enabled:{init:"inherit",
check:"Boolean",
inheritable:true,
apply:"_applyEnabled",
event:"changeEnabled"},
parent:{check:"qx.ui.core.Parent",
nullable:true,
event:"changeParent",
apply:"_applyParent"},
element:{check:"Element",
nullable:true,
apply:"_applyElement",
event:"changeElement"},
visibility:{check:"Boolean",
init:true,
apply:"_applyVisibility",
event:"changeVisibility"},
display:{check:"Boolean",
init:true,
apply:"_applyDisplay",
event:"changeDisplay"},
anonymous:{check:"Boolean",
init:false,
event:"changeAnonymous"},
horizontalAlign:{check:["left",
"center",
"right"],
themeable:true,
nullable:true},
verticalAlign:{check:["top",
"middle",
"bottom"],
themeable:true,
nullable:true},
allowStretchX:{check:"Boolean",
init:true},
allowStretchY:{check:"Boolean",
init:true},
zIndex:{check:"Number",
apply:"_applyZIndex",
event:"changeZIndex",
themeable:true,
nullable:true,
init:null},
backgroundColor:{nullable:true,
init:null,
check:"Color",
apply:"_applyBackgroundColor",
event:"changeBackgroundColor",
themeable:true},
textColor:{nullable:true,
init:"inherit",
check:"Color",
apply:"_applyTextColor",
event:"changeTextColor",
themeable:true,
inheritable:true},
border:{nullable:true,
init:null,
apply:"_applyBorder",
event:"changeBorder",
check:"Border",
themeable:true},
font:{nullable:true,
init:"inherit",
apply:"_applyFont",
check:"Font",
event:"changeFont",
themeable:true,
inheritable:true},
opacity:{check:"Number",
apply:"_applyOpacity",
themeable:true,
nullable:true,
init:null},
cursor:{check:"String",
apply:"_applyCursor",
themeable:true,
nullable:true,
init:null},
backgroundImage:{check:"String",
nullable:true,
apply:"_applyBackgroundImage",
themeable:true},
overflow:{check:["hidden",
"auto",
"scroll",
"scrollX",
"scrollY"],
nullable:true,
apply:"_applyOverflow",
event:"changeOverflow",
themeable:true,
init:null},
clipLeft:{check:"Integer",
apply:"_applyClip",
themeable:true,
nullable:true},
clipTop:{check:"Integer",
apply:"_applyClip",
themeable:true,
nullable:true},
clipWidth:{check:"Integer",
apply:"_applyClip",
themeable:true,
nullable:true},
clipHeight:{check:"Integer",
apply:"_applyClip",
themeable:true,
nullable:true},
tabIndex:{check:"Integer",
nullable:true,
init:null,
apply:"_applyTabIndex",
event:"changeTabIndex"},
hideFocus:{check:"Boolean",
init:false,
apply:"_applyHideFocus",
themeable:true},
enableElementFocus:{check:"Boolean",
init:true},
focused:{check:"Boolean",
init:false,
apply:"_applyFocused",
event:"changeFocused"},
selectable:{check:"Boolean",
init:null,
nullable:true,
apply:"_applySelectable"},
toolTip:{check:"qx.ui.popup.ToolTip",
nullable:true},
contextMenu:{check:"qx.ui.menu.Menu",
nullable:true},
capture:{check:"Boolean",
init:false,
apply:"_applyCapture"},
dropDataTypes:{nullable:true},
command:{check:"qx.client.Command",
nullable:true,
apply:"_applyCommand"},
appearance:{check:"String",
init:"widget",
apply:"_applyAppearance",
event:"changeAppearance"},
marginTop:{check:"Number",
apply:"_applyMarginTop",
nullable:true,
themeable:true},
marginRight:{check:"Number",
apply:"_applyMarginRight",
nullable:true,
themeable:true},
marginBottom:{check:"Number",
apply:"_applyMarginBottom",
nullable:true,
themeable:true},
marginLeft:{check:"Number",
apply:"_applyMarginLeft",
nullable:true,
themeable:true},
paddingTop:{check:"Number",
apply:"_applyPaddingTop",
nullable:true,
themeable:true},
paddingRight:{check:"Number",
apply:"_applyPaddingRight",
nullable:true,
themeable:true},
paddingBottom:{check:"Number",
apply:"_applyPaddingBottom",
nullable:true,
themeable:true},
paddingLeft:{check:"Number",
apply:"_applyPaddingLeft",
nullable:true,
themeable:true},
left:{apply:"_applyLeft",
event:"changeLeft",
nullable:true,
themeable:true,
init:null},
right:{apply:"_applyRight",
event:"changeRight",
nullable:true,
themeable:true,
init:null},
width:{apply:"_applyWidth",
event:"changeWidth",
nullable:true,
themeable:true,
init:null},
minWidth:{apply:"_applyMinWidth",
event:"changeMinWidth",
nullable:true,
themeable:true,
init:null},
maxWidth:{apply:"_applyMaxWidth",
event:"changeMaxWidth",
nullable:true,
themeable:true,
init:null},
top:{apply:"_applyTop",
event:"changeTop",
nullable:true,
themeable:true,
init:null},
bottom:{apply:"_applyBottom",
event:"changeBottom",
nullable:true,
themeable:true,
init:null},
height:{apply:"_applyHeight",
event:"changeHeight",
nullable:true,
themeable:true,
init:null},
minHeight:{apply:"_applyMinHeight",
event:"changeMinHeight",
nullable:true,
themeable:true,
init:null},
maxHeight:{apply:"_applyMaxHeight",
event:"changeMaxHeight",
nullable:true,
themeable:true,
init:null},
location:{group:["left",
"top"],
themeable:true},
dimension:{group:["width",
"height"],
themeable:true},
space:{group:["left",
"width",
"top",
"height"],
themeable:true},
edge:{group:["top",
"right",
"bottom",
"left"],
themeable:true,
mode:"shorthand"},
padding:{group:["paddingTop",
"paddingRight",
"paddingBottom",
"paddingLeft"],
mode:"shorthand",
themeable:true},
margin:{group:["marginTop",
"marginRight",
"marginBottom",
"marginLeft"],
mode:"shorthand",
themeable:true},
heights:{group:["minHeight",
"height",
"maxHeight"],
themeable:true},
widths:{group:["minWidth",
"width",
"maxWidth"],
themeable:true},
align:{group:["horizontalAlign",
"verticalAlign"],
themeable:true},
clipLocation:{group:["clipLeft",
"clipTop"]},
clipDimension:{group:["clipWidth",
"clipHeight"]},
clip:{group:["clipLeft",
"clipTop",
"clipWidth",
"clipHeight"]},
innerWidth:{_cached:true,
defaultValue:null},
innerHeight:{_cached:true,
defaultValue:null},
boxWidth:{_cached:true,
defaultValue:null},
boxHeight:{_cached:true,
defaultValue:null},
outerWidth:{_cached:true,
defaultValue:null},
outerHeight:{_cached:true,
defaultValue:null},
frameWidth:{_cached:true,
defaultValue:null,
addToQueueRuntime:true},
frameHeight:{_cached:true,
defaultValue:null,
addToQueueRuntime:true},
preferredInnerWidth:{_cached:true,
defaultValue:null,
addToQueueRuntime:true},
preferredInnerHeight:{_cached:true,
defaultValue:null,
addToQueueRuntime:true},
preferredBoxWidth:{_cached:true,
defaultValue:null},
preferredBoxHeight:{_cached:true,
defaultValue:null},
hasPercentX:{_cached:true,
defaultValue:false},
hasPercentY:{_cached:true,
defaultValue:false},
hasAutoX:{_cached:true,
defaultValue:false},
hasAutoY:{_cached:true,
defaultValue:false},
hasFlexX:{_cached:true,
defaultValue:false},
hasFlexY:{_cached:true,
defaultValue:false}},
members:{_computedLeftValue:null,
_computedLeftParsed:null,
_computedLeftType:null,
_computedLeftTypeNull:true,
_computedLeftTypePixel:false,
_computedLeftTypePercent:false,
_computedLeftTypeAuto:false,
_computedLeftTypeFlex:false,
_computedRightValue:null,
_computedRightParsed:null,
_computedRightType:null,
_computedRightTypeNull:true,
_computedRightTypePixel:false,
_computedRightTypePercent:false,
_computedRightTypeAuto:false,
_computedRightTypeFlex:false,
_computedTopValue:null,
_computedTopParsed:null,
_computedTopType:null,
_computedTopTypeNull:true,
_computedTopTypePixel:false,
_computedTopTypePercent:false,
_computedTopTypeAuto:false,
_computedTopTypeFlex:false,
_computedBottomValue:null,
_computedBottomParsed:null,
_computedBottomType:null,
_computedBottomTypeNull:true,
_computedBottomTypePixel:false,
_computedBottomTypePercent:false,
_computedBottomTypeAuto:false,
_computedBottomTypeFlex:false,
_computedWidthValue:null,
_computedWidthParsed:null,
_computedWidthType:null,
_computedWidthTypeNull:true,
_computedWidthTypePixel:false,
_computedWidthTypePercent:false,
_computedWidthTypeAuto:false,
_computedWidthTypeFlex:false,
_computedMinWidthValue:null,
_computedMinWidthParsed:null,
_computedMinWidthType:null,
_computedMinWidthTypeNull:true,
_computedMinWidthTypePixel:false,
_computedMinWidthTypePercent:false,
_computedMinWidthTypeAuto:false,
_computedMinWidthTypeFlex:false,
_computedMaxWidthValue:null,
_computedMaxWidthParsed:null,
_computedMaxWidthType:null,
_computedMaxWidthTypeNull:true,
_computedMaxWidthTypePixel:false,
_computedMaxWidthTypePercent:false,
_computedMaxWidthTypeAuto:false,
_computedMaxWidthTypeFlex:false,
_computedHeightValue:null,
_computedHeightParsed:null,
_computedHeightType:null,
_computedHeightTypeNull:true,
_computedHeightTypePixel:false,
_computedHeightTypePercent:false,
_computedHeightTypeAuto:false,
_computedHeightTypeFlex:false,
_computedMinHeightValue:null,
_computedMinHeightParsed:null,
_computedMinHeightType:null,
_computedMinHeightTypeNull:true,
_computedMinHeightTypePixel:false,
_computedMinHeightTypePercent:false,
_computedMinHeightTypeAuto:false,
_computedMinHeightTypeFlex:false,
_computedMaxHeightValue:null,
_computedMaxHeightParsed:null,
_computedMaxHeightType:null,
_computedMaxHeightTypeNull:true,
_computedMaxHeightTypePixel:false,
_computedMaxHeightTypePercent:false,
_computedMaxHeightTypeAuto:false,
_computedMaxHeightTypeFlex:false,
_applyLeft:function(value,
old){this._unitDetectionPixelPercent("left",
value);
this.addToQueue("left");
},
_applyRight:function(value,
old){this._unitDetectionPixelPercent("right",
value);
this.addToQueue("right");
},
_applyTop:function(value,
old){this._unitDetectionPixelPercent("top",
value);
this.addToQueue("top");
},
_applyBottom:function(value,
old){this._unitDetectionPixelPercent("bottom",
value);
this.addToQueue("bottom");
},
_applyWidth:function(value,
old){this._unitDetectionPixelPercentAutoFlex("width",
value);
this.addToQueue("width");
},
_applyMinWidth:function(value,
old){this._unitDetectionPixelPercentAuto("minWidth",
value);
this.addToQueue("minWidth");
},
_applyMaxWidth:function(value,
old){this._unitDetectionPixelPercentAuto("maxWidth",
value);
this.addToQueue("maxWidth");
},
_applyHeight:function(value,
old){this._unitDetectionPixelPercentAutoFlex("height",
value);
this.addToQueue("height");
},
_applyMinHeight:function(value,
old){this._unitDetectionPixelPercentAuto("minHeight",
value);
this.addToQueue("minHeight");
},
_applyMaxHeight:function(value,
old){this._unitDetectionPixelPercentAuto("maxHeight",
value);
this.addToQueue("maxHeight");
},
isMaterialized:function(){var elem=this._element;
return (this._initialLayoutDone&&this._isDisplayable&&qx.html.Style.getStyleProperty(elem,
"display")!="none"&&qx.html.Style.getStyleProperty(elem,
"visibility")!="hidden"&&elem.offsetWidth>0&&elem.offsetHeight>0);
},
pack:function(){this.setWidth(this.getPreferredBoxWidth());
this.setHeight(this.getPreferredBoxHeight());
},
auto:function(){this.setWidth("auto");
this.setHeight("auto");
},
getChildren:qx.lang.Function.returnNull,
getChildrenLength:qx.lang.Function.returnZero,
hasChildren:qx.lang.Function.returnFalse,
isEmpty:qx.lang.Function.returnTrue,
indexOf:qx.lang.Function.returnNegativeIndex,
contains:qx.lang.Function.returnFalse,
getVisibleChildren:qx.lang.Function.returnNull,
getVisibleChildrenLength:qx.lang.Function.returnZero,
hasVisibleChildren:qx.lang.Function.returnFalse,
isVisibleEmpty:qx.lang.Function.returnTrue,
_hasParent:false,
_isDisplayable:false,
isDisplayable:function(){return this._isDisplayable;
},
_checkParent:function(value,
old){if(this.contains(value)){throw new Error("Could not insert myself into a child "+value+"!");
}return value;
},
_applyParent:function(value,
old){if(old){var vOldIndex=old.getChildren().indexOf(this);
this._computedWidthValue=this._computedMinWidthValue=this._computedMaxWidthValue=this._computedLeftValue=this._computedRightValue=null;
this._computedHeightValue=this._computedMinHeightValue=this._computedMaxHeightValue=this._computedTopValue=this._computedBottomValue=null;
this._cachedBoxWidth=this._cachedInnerWidth=this._cachedOuterWidth=null;
this._cachedBoxHeight=this._cachedInnerHeight=this._cachedOuterHeight=null;
qx.lang.Array.removeAt(old.getChildren(),
vOldIndex);
old._invalidateVisibleChildren();
old._removeChildFromChildrenQueue(this);
old.getLayoutImpl().updateChildrenOnRemoveChild(this,
vOldIndex);
old.addToJobQueue("removeChild");
old._invalidatePreferredInnerDimensions();
this._oldParent=old;
}
if(value){this._hasParent=true;
if(typeof this._insertIndex=="number"){qx.lang.Array.insertAt(value.getChildren(),
this,
this._insertIndex);
delete this._insertIndex;
}else{value.getChildren().push(this);
}}else{this._hasParent=false;
}qx.core.Property.refresh(this);
return this._handleDisplayable("parent");
},
_applyDisplay:function(value,
old){return this._handleDisplayable("display");
},
_handleDisplayable:function(vHint){var vDisplayable=this._computeDisplayable();
if(this._isDisplayable==vDisplayable&&!(vDisplayable&&vHint=="parent")){return true;
}this._isDisplayable=vDisplayable;
var vParent=this.getParent();
if(vParent){vParent._invalidateVisibleChildren();
vParent._invalidatePreferredInnerDimensions();
}if(vHint&&this._oldParent&&this._oldParent._initialLayoutDone){var elem=this.getElement();
if(elem){if(this.getVisibility()){this._beforeDisappear();
}this._beforeRemoveDom();
this._oldParent._getTargetNode().removeChild(elem);
this._afterRemoveDom();
if(this.getVisibility()){this._afterDisappear();
}}delete this._oldParent;
}if(vDisplayable){if(vParent._initialLayoutDone){vParent.getLayoutImpl().updateChildrenOnAddChild(this,
vParent.getChildren().indexOf(this));
vParent.addToJobQueue("addChild");
}this.addToLayoutChanges("initial");
this.addToCustomQueues(vHint);
if(this.getVisibility()){this._beforeAppear();
}if(!this._isCreated){qx.ui.core.Widget.addToGlobalElementQueue(this);
}qx.ui.core.Widget.addToGlobalStateQueue(this);
if(!qx.lang.Object.isEmpty(this._jobQueue)){qx.ui.core.Widget.addToGlobalJobQueue(this);
}
if(!qx.lang.Object.isEmpty(this._childrenQueue)){qx.ui.core.Widget.addToGlobalLayoutQueue(this);
}}else{qx.ui.core.Widget.removeFromGlobalElementQueue(this);
qx.ui.core.Widget.removeFromGlobalStateQueue(this);
qx.ui.core.Widget.removeFromGlobalJobQueue(this);
qx.ui.core.Widget.removeFromGlobalLayoutQueue(this);
this.removeFromCustomQueues(vHint);
if(vParent&&vHint){if(this.getVisibility()){this._beforeDisappear();
}if(vParent._initialLayoutDone&&this._initialLayoutDone){vParent.getLayoutImpl().updateChildrenOnRemoveChild(this,
vParent.getChildren().indexOf(this));
vParent.addToJobQueue("removeChild");
this._beforeRemoveDom();
vParent._getTargetNode().removeChild(this.getElement());
this._afterRemoveDom();
}vParent._removeChildFromChildrenQueue(this);
if(this.getVisibility()){this._afterDisappear();
}}}this._handleDisplayableCustom(vDisplayable,
vParent,
vHint);
return true;
},
addToCustomQueues:qx.lang.Function.returnTrue,
removeFromCustomQueues:qx.lang.Function.returnTrue,
_handleDisplayableCustom:qx.lang.Function.returnTrue,
_computeDisplayable:function(){return this.getDisplay()&&this._hasParent&&this.getParent()._isDisplayable?true:false;
},
_beforeAppear:function(){this.createDispatchEvent("beforeAppear");
},
_afterAppear:function(){this._isSeeable=true;
this.createDispatchEvent("appear");
},
_beforeDisappear:function(){this.removeState("over");
if(qx.Class.isDefined("qx.ui.form.Button")){this.removeState("pressed");
this.removeState("abandoned");
}this.createDispatchEvent("beforeDisappear");
},
_afterDisappear:function(){this._isSeeable=false;
this.createDispatchEvent("disappear");
},
_isSeeable:false,
isSeeable:function(){return this._isSeeable;
},
isAppearRelevant:function(){return this.getVisibility()&&this._isDisplayable;
},
_beforeInsertDom:function(){this.createDispatchEvent("beforeInsertDom");
},
_afterInsertDom:function(){this.createDispatchEvent("insertDom");
},
_beforeRemoveDom:function(){this.createDispatchEvent("beforeRemoveDom");
},
_afterRemoveDom:function(){this.createDispatchEvent("removeDom");
},
_applyVisibility:function(value,
old){if(value){if(this._isDisplayable){this._beforeAppear();
}this.removeStyleProperty("display");
if(this._isDisplayable){this._afterAppear();
}}else{if(this._isDisplayable){this._beforeDisappear();
}this.setStyleProperty("display",
"none");
if(this._isDisplayable){this._afterDisappear();
}}},
show:function(){this.setVisibility(true);
this.setDisplay(true);
},
hide:function(){this.setVisibility(false);
},
connect:function(){this.setDisplay(true);
},
disconnect:function(){this.setDisplay(false);
},
_isCreated:false,
_getTargetNode:qx.core.Variant.select("qx.client",
{"gecko":function(){return this._element;
},
"default":function(){return this._borderElement||this._element;
}}),
addToDocument:function(){qx.ui.core.ClientDocument.getInstance().add(this);
},
isCreated:function(){return this._isCreated;
},
_createElementImpl:function(){this.setElement(this.getTopLevelWidget().getDocumentElement().createElement("div"));
},
_applyElement:function(value,
old){this._isCreated=value!=null;
if(old){old.qx_Widget=null;
}
if(value){value.qx_Widget=this;
value.style.position="absolute";
this._element=value;
this._style=value.style;
this._applyStyleProperties(value);
this._applyHtmlProperties(value);
this._applyHtmlAttributes(value);
this._applyElementData(value);
this.createDispatchEvent("create");
this.addToStateQueue();
}else{this._element=this._style=null;
}},
addToJobQueue:function(p){if(this._hasParent){qx.ui.core.Widget.addToGlobalJobQueue(this);
}
if(!this._jobQueue){this._jobQueue={};
}this._jobQueue[p]=true;
return true;
},
_flushJobQueue:function(q){try{var vQueue=this._jobQueue;
var vParent=this.getParent();
if(!vParent||qx.lang.Object.isEmpty(vQueue)){return;
}var vLayoutImpl=this instanceof qx.ui.core.Parent?this.getLayoutImpl():null;
if(vLayoutImpl){vLayoutImpl.updateSelfOnJobQueueFlush(vQueue);
}}catch(ex){this.error("Flushing job queue (prechecks#1) failed",
ex);
}try{var vFlushParentJobQueue=false;
var vRecomputeOuterWidth=vQueue.marginLeft||vQueue.marginRight;
var vRecomputeOuterHeight=vQueue.marginTop||vQueue.marginBottom;
var vRecomputeInnerWidth=vQueue.frameWidth;
var vRecomputeInnerHeight=vQueue.frameHeight;
var vRecomputeParentPreferredInnerWidth=(vQueue.frameWidth||vQueue.preferredInnerWidth)&&this._recomputePreferredBoxWidth();
var vRecomputeParentPreferredInnerHeight=(vQueue.frameHeight||vQueue.preferredInnerHeight)&&this._recomputePreferredBoxHeight();
if(vRecomputeParentPreferredInnerWidth){var vPref=this.getPreferredBoxWidth();
if(this._computedWidthTypeAuto){this._computedWidthValue=vPref;
vQueue.width=true;
}
if(this._computedMinWidthTypeAuto){this._computedMinWidthValue=vPref;
vQueue.minWidth=true;
}
if(this._computedMaxWidthTypeAuto){this._computedMaxWidthValue=vPref;
vQueue.maxWidth=true;
}}
if(vRecomputeParentPreferredInnerHeight){var vPref=this.getPreferredBoxHeight();
if(this._computedHeightTypeAuto){this._computedHeightValue=vPref;
vQueue.height=true;
}
if(this._computedMinHeightTypeAuto){this._computedMinHeightValue=vPref;
vQueue.minHeight=true;
}
if(this._computedMaxHeightTypeAuto){this._computedMaxHeightValue=vPref;
vQueue.maxHeight=true;
}}
if((vQueue.width||vQueue.minWidth||vQueue.maxWidth||vQueue.left||vQueue.right)&&this._recomputeBoxWidth()){vRecomputeOuterWidth=vRecomputeInnerWidth=true;
}
if((vQueue.height||vQueue.minHeight||vQueue.maxHeight||vQueue.top||vQueue.bottom)&&this._recomputeBoxHeight()){vRecomputeOuterHeight=vRecomputeInnerHeight=true;
}}catch(ex){this.error("Flushing job queue (recompute#2) failed",
ex);
}try{if((vRecomputeOuterWidth&&this._recomputeOuterWidth())||vRecomputeParentPreferredInnerWidth){vParent._invalidatePreferredInnerWidth();
vParent.getLayoutImpl().updateSelfOnChildOuterWidthChange(this);
vFlushParentJobQueue=true;
}
if((vRecomputeOuterHeight&&this._recomputeOuterHeight())||vRecomputeParentPreferredInnerHeight){vParent._invalidatePreferredInnerHeight();
vParent.getLayoutImpl().updateSelfOnChildOuterHeightChange(this);
vFlushParentJobQueue=true;
}
if(vFlushParentJobQueue){vParent._flushJobQueue();
}}catch(ex){this.error("Flushing job queue (parentsignals#3) failed",
ex);
}try{vParent._addChildToChildrenQueue(this);
for(var i in vQueue){this._layoutChanges[i]=true;
}}catch(ex){this.error("Flushing job queue (addjobs#4) failed",
ex);
}try{if(this instanceof qx.ui.core.Parent&&(vQueue.paddingLeft||vQueue.paddingRight||vQueue.paddingTop||vQueue.paddingBottom)){var ch=this.getChildren(),
chl=ch.length;
if(vQueue.paddingLeft){for(var i=0;i<chl;i++){ch[i].addToLayoutChanges("parentPaddingLeft");
}}
if(vQueue.paddingRight){for(var i=0;i<chl;i++){ch[i].addToLayoutChanges("parentPaddingRight");
}}
if(vQueue.paddingTop){for(var i=0;i<chl;i++){ch[i].addToLayoutChanges("parentPaddingTop");
}}
if(vQueue.paddingBottom){for(var i=0;i<chl;i++){ch[i].addToLayoutChanges("parentPaddingBottom");
}}}
if(vRecomputeInnerWidth){this._recomputeInnerWidth();
}
if(vRecomputeInnerHeight){this._recomputeInnerHeight();
}
if(this._initialLayoutDone){if(vLayoutImpl){vLayoutImpl.updateChildrenOnJobQueueFlush(vQueue);
}}}catch(ex){this.error("Flushing job queue (childrensignals#5) failed",
ex);
}delete this._jobQueue;
},
_isWidthEssential:qx.lang.Function.returnTrue,
_isHeightEssential:qx.lang.Function.returnTrue,
_computeBoxWidthFallback:function(){return 0;
},
_computeBoxHeightFallback:function(){return 0;
},
_computeBoxWidth:function(){var vLayoutImpl=this.getParent().getLayoutImpl();
return Math.max(0,
qx.lang.Number.limit(vLayoutImpl.computeChildBoxWidth(this),
this.getMinWidthValue(),
this.getMaxWidthValue()));
},
_computeBoxHeight:function(){var vLayoutImpl=this.getParent().getLayoutImpl();
return Math.max(0,
qx.lang.Number.limit(vLayoutImpl.computeChildBoxHeight(this),
this.getMinHeightValue(),
this.getMaxHeightValue()));
},
_computeOuterWidth:function(){return Math.max(0,
(this.getMarginLeft()+this.getBoxWidth()+this.getMarginRight()));
},
_computeOuterHeight:function(){return Math.max(0,
(this.getMarginTop()+this.getBoxHeight()+this.getMarginBottom()));
},
_computeInnerWidth:function(){return Math.max(0,
this.getBoxWidth()-this.getFrameWidth());
},
_computeInnerHeight:function(){return Math.max(0,
this.getBoxHeight()-this.getFrameHeight());
},
getNeededWidth:function(){var vLayoutImpl=this.getParent().getLayoutImpl();
return Math.max(0,
vLayoutImpl.computeChildNeededWidth(this));
},
getNeededHeight:function(){var vLayoutImpl=this.getParent().getLayoutImpl();
return Math.max(0,
vLayoutImpl.computeChildNeededHeight(this));
},
_recomputeFlexX:function(){if(!this.getHasFlexX()){return false;
}
if(this._computedWidthTypeFlex){this._computedWidthValue=null;
this.addToLayoutChanges("width");
}return true;
},
_recomputeFlexY:function(){if(!this.getHasFlexY()){return false;
}
if(this._computedHeightTypeFlex){this._computedHeightValue=null;
this.addToLayoutChanges("height");
}return true;
},
_recomputePercentX:function(){if(!this.getHasPercentX()){return false;
}
if(this._computedWidthTypePercent){this._computedWidthValue=null;
this.addToLayoutChanges("width");
}
if(this._computedMinWidthTypePercent){this._computedMinWidthValue=null;
this.addToLayoutChanges("minWidth");
}
if(this._computedMaxWidthTypePercent){this._computedMaxWidthValue=null;
this.addToLayoutChanges("maxWidth");
}
if(this._computedLeftTypePercent){this._computedLeftValue=null;
this.addToLayoutChanges("left");
}
if(this._computedRightTypePercent){this._computedRightValue=null;
this.addToLayoutChanges("right");
}return true;
},
_recomputePercentY:function(){if(!this.getHasPercentY()){return false;
}
if(this._computedHeightTypePercent){this._computedHeightValue=null;
this.addToLayoutChanges("height");
}
if(this._computedMinHeightTypePercent){this._computedMinHeightValue=null;
this.addToLayoutChanges("minHeight");
}
if(this._computedMaxHeightTypePercent){this._computedMaxHeightValue=null;
this.addToLayoutChanges("maxHeight");
}
if(this._computedTopTypePercent){this._computedTopValue=null;
this.addToLayoutChanges("top");
}
if(this._computedBottomTypePercent){this._computedBottomValue=null;
this.addToLayoutChanges("bottom");
}return true;
},
_recomputeRangeX:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(){if(this._computedLeftTypeNull||this._computedRightTypeNull){return false;
}this.addToLayoutChanges("width");
return true;
},
"default":function(){return !(this._computedLeftTypeNull||this._computedRightTypeNull);
}}),
_recomputeRangeY:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(){if(this._computedTopTypeNull||this._computedBottomTypeNull){return false;
}this.addToLayoutChanges("height");
return true;
},
"default":function(){return !(this._computedTopTypeNull||this._computedBottomTypeNull);
}}),
_recomputeStretchingX:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(){if(this.getAllowStretchX()&&this._computedWidthTypeNull){this._computedWidthValue=null;
this.addToLayoutChanges("width");
return true;
}return false;
},
"default":function(){if(this.getAllowStretchX()&&this._computedWidthTypeNull){return true;
}return false;
}}),
_recomputeStretchingY:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(){if(this.getAllowStretchY()&&this._computedHeightTypeNull){this._computedHeightValue=null;
this.addToLayoutChanges("height");
return true;
}return false;
},
"default":function(){if(this.getAllowStretchY()&&this._computedHeightTypeNull){return true;
}return false;
}}),
_computeValuePixel:function(v){return Math.round(v);
},
_computeValuePixelLimit:function(v){return Math.max(0,
this._computeValuePixel(v));
},
_computeValuePercentX:function(v){return Math.round(this.getParent().getInnerWidthForChild(this)*v*0.01);
},
_computeValuePercentXLimit:function(v){return Math.max(0,
this._computeValuePercentX(v));
},
_computeValuePercentY:function(v){return Math.round(this.getParent().getInnerHeightForChild(this)*v*0.01);
},
_computeValuePercentYLimit:function(v){return Math.max(0,
this._computeValuePercentY(v));
},
getWidthValue:function(){if(this._computedWidthValue!=null){return this._computedWidthValue;
}
switch(this._computedWidthType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedWidthValue=this._computeValuePixelLimit(this._computedWidthParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedWidthValue=this._computeValuePercentXLimit(this._computedWidthParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedWidthValue=this.getPreferredBoxWidth();
case qx.ui.core.Widget.TYPE_FLEX:if(this.getParent().getLayoutImpl().computeChildrenFlexWidth===undefined){throw new Error("Widget "+this+": having horizontal flex size (width="+this.getWidth()+") but parent layout "+this.getParent()+" does not support it");
}this.getParent().getLayoutImpl().computeChildrenFlexWidth();
return this._computedWidthValue=this._computedWidthFlexValue;
}return null;
},
getMinWidthValue:function(){if(this._computedMinWidthValue!=null){return this._computedMinWidthValue;
}
switch(this._computedMinWidthType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedWidthValue=this._computeValuePixelLimit(this._computedMinWidthParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedWidthValue=this._computeValuePercentXLimit(this._computedMinWidthParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedMinWidthValue=this.getPreferredBoxWidth();
}return null;
},
getMaxWidthValue:function(){if(this._computedMaxWidthValue!=null){return this._computedMaxWidthValue;
}
switch(this._computedMaxWidthType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedWidthValue=this._computeValuePixelLimit(this._computedMaxWidthParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedWidthValue=this._computeValuePercentXLimit(this._computedMaxWidthParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedMaxWidthValue=this.getPreferredBoxWidth();
}return null;
},
getLeftValue:function(){if(this._computedLeftValue!=null){return this._computedLeftValue;
}
switch(this._computedLeftType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedLeftValue=this._computeValuePixel(this._computedLeftParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedLeftValue=this._computeValuePercentX(this._computedLeftParsed);
}return null;
},
getRightValue:function(){if(this._computedRightValue!=null){return this._computedRightValue;
}
switch(this._computedRightType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedRightValue=this._computeValuePixel(this._computedRightParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedRightValue=this._computeValuePercentX(this._computedRightParsed);
}return null;
},
getHeightValue:function(){if(this._computedHeightValue!=null){return this._computedHeightValue;
}
switch(this._computedHeightType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedHeightValue=this._computeValuePixelLimit(this._computedHeightParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedHeightValue=this._computeValuePercentYLimit(this._computedHeightParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedHeightValue=this.getPreferredBoxHeight();
case qx.ui.core.Widget.TYPE_FLEX:if(this.getParent().getLayoutImpl().computeChildrenFlexHeight===undefined){throw new Error("Widget "+this+": having vertical flex size (height="+this.getHeight()+") but parent layout "+this.getParent()+" does not support it");
}this.getParent().getLayoutImpl().computeChildrenFlexHeight();
return this._computedHeightValue=this._computedHeightFlexValue;
}return null;
},
getMinHeightValue:function(){if(this._computedMinHeightValue!=null){return this._computedMinHeightValue;
}
switch(this._computedMinHeightType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedMinHeightValue=this._computeValuePixelLimit(this._computedMinHeightParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedMinHeightValue=this._computeValuePercentYLimit(this._computedMinHeightParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedMinHeightValue=this.getPreferredBoxHeight();
}return null;
},
getMaxHeightValue:function(){if(this._computedMaxHeightValue!=null){return this._computedMaxHeightValue;
}
switch(this._computedMaxHeightType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedMaxHeightValue=this._computeValuePixelLimit(this._computedMaxHeightParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedMaxHeightValue=this._computeValuePercentYLimit(this._computedMaxHeightParsed);
case qx.ui.core.Widget.TYPE_AUTO:return this._computedMaxHeightValue=this.getPreferredBoxHeight();
}return null;
},
getTopValue:function(){if(this._computedTopValue!=null){return this._computedTopValue;
}
switch(this._computedTopType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedTopValue=this._computeValuePixel(this._computedTopParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedTopValue=this._computeValuePercentY(this._computedTopParsed);
}return null;
},
getBottomValue:function(){if(this._computedBottomValue!=null){return this._computedBottomValue;
}
switch(this._computedBottomType){case qx.ui.core.Widget.TYPE_PIXEL:return this._computedBottomValue=this._computeValuePixel(this._computedBottomParsed);
case qx.ui.core.Widget.TYPE_PERCENT:return this._computedBottomValue=this._computeValuePercentY(this._computedBottomParsed);
}return null;
},
_computeFrameWidth:function(){var fw=this._cachedBorderLeft+this.getPaddingLeft()+this.getPaddingRight()+this._cachedBorderRight;
switch(this.getOverflow()){case "scroll":case "scrollY":fw+=qx.ui.core.Widget.SCROLLBAR_SIZE;
break;
case "auto":break;
}return fw;
},
_computeFrameHeight:function(){var fh=this._cachedBorderTop+this.getPaddingTop()+this.getPaddingBottom()+this._cachedBorderBottom;
switch(this.getOverflow()){case "scroll":case "scrollX":fh+=qx.ui.core.Widget.SCROLLBAR_SIZE;
break;
case "auto":break;
}return fh;
},
_invalidateFrameDimensions:function(){this._invalidateFrameWidth();
this._invalidateFrameHeight();
},
_invalidatePreferredInnerDimensions:function(){this._invalidatePreferredInnerWidth();
this._invalidatePreferredInnerHeight();
},
_computePreferredBoxWidth:function(){try{return Math.max(0,
this.getPreferredInnerWidth()+this.getFrameWidth());
}catch(ex){this.error("_computePreferredBoxWidth failed",
ex);
}},
_computePreferredBoxHeight:function(){try{return Math.max(0,
this.getPreferredInnerHeight()+this.getFrameHeight());
}catch(ex){this.error("_computePreferredBoxHeight failed",
ex);
}},
_initialLayoutDone:false,
addToLayoutChanges:function(p){if(this._isDisplayable){this.getParent()._addChildToChildrenQueue(this);
}return this._layoutChanges[p]=true;
},
addToQueue:function(p){this._initialLayoutDone?this.addToJobQueue(p):this.addToLayoutChanges(p);
},
addToQueueRuntime:function(p){return !this._initialLayoutDone||this.addToJobQueue(p);
},
_computeHasPercentX:function(){return (this._computedLeftTypePercent||this._computedWidthTypePercent||this._computedMinWidthTypePercent||this._computedMaxWidthTypePercent||this._computedRightTypePercent);
},
_computeHasPercentY:function(){return (this._computedTopTypePercent||this._computedHeightTypePercent||this._computedMinHeightTypePercent||this._computedMaxHeightTypePercent||this._computedBottomTypePercent);
},
_computeHasAutoX:function(){return (this._computedWidthTypeAuto||this._computedMinWidthTypeAuto||this._computedMaxWidthTypeAuto);
},
_computeHasAutoY:function(){return (this._computedHeightTypeAuto||this._computedMinHeightTypeAuto||this._computedMaxHeightTypeAuto);
},
_computeHasFlexX:function(){return this._computedWidthTypeFlex;
},
_computeHasFlexY:function(){return this._computedHeightTypeFlex;
},
_evalUnitsPixelPercentAutoFlex:function(value){switch(value){case "auto":return qx.ui.core.Widget.TYPE_AUTO;
case Infinity:case -Infinity:return qx.ui.core.Widget.TYPE_NULL;
}
switch(typeof value){case "number":return isNaN(value)?qx.ui.core.Widget.TYPE_NULL:qx.ui.core.Widget.TYPE_PIXEL;
case "string":return value.indexOf("%")!=-1?qx.ui.core.Widget.TYPE_PERCENT:value.indexOf("*")!=-1?qx.ui.core.Widget.TYPE_FLEX:qx.ui.core.Widget.TYPE_NULL;
}return qx.ui.core.Widget.TYPE_NULL;
},
_evalUnitsPixelPercentAuto:function(value){switch(value){case "auto":return qx.ui.core.Widget.TYPE_AUTO;
case Infinity:case -Infinity:return qx.ui.core.Widget.TYPE_NULL;
}
switch(typeof value){case "number":return isNaN(value)?qx.ui.core.Widget.TYPE_NULL:qx.ui.core.Widget.TYPE_PIXEL;
case "string":return value.indexOf("%")!=-1?qx.ui.core.Widget.TYPE_PERCENT:qx.ui.core.Widget.TYPE_NULL;
}return qx.ui.core.Widget.TYPE_NULL;
},
_evalUnitsPixelPercent:function(value){switch(value){case Infinity:case -Infinity:return qx.ui.core.Widget.TYPE_NULL;
}
switch(typeof value){case "number":return isNaN(value)?qx.ui.core.Widget.TYPE_NULL:qx.ui.core.Widget.TYPE_PIXEL;
case "string":return value.indexOf("%")!=-1?qx.ui.core.Widget.TYPE_PERCENT:qx.ui.core.Widget.TYPE_NULL;
}return qx.ui.core.Widget.TYPE_NULL;
},
_unitDetectionPixelPercentAutoFlex:function(name,
value){var r=qx.ui.core.Widget.layoutPropertyTypes[name];
var s=r.dataType;
var p=r.dataParsed;
var v=r.dataValue;
var s1=r.typePixel;
var s2=r.typePercent;
var s3=r.typeAuto;
var s4=r.typeFlex;
var s5=r.typeNull;
var wasPercent=this[s2];
var wasAuto=this[s3];
var wasFlex=this[s4];
switch(this[s]=this._evalUnitsPixelPercentAutoFlex(value)){case qx.ui.core.Widget.TYPE_PIXEL:this[s1]=true;
this[s2]=this[s3]=this[s4]=this[s5]=false;
this[p]=this[v]=Math.round(value);
break;
case qx.ui.core.Widget.TYPE_PERCENT:this[s2]=true;
this[s1]=this[s3]=this[s4]=this[s5]=false;
this[p]=parseFloat(value);
this[v]=null;
break;
case qx.ui.core.Widget.TYPE_AUTO:this[s3]=true;
this[s1]=this[s2]=this[s4]=this[s5]=false;
this[p]=this[v]=null;
break;
case qx.ui.core.Widget.TYPE_FLEX:this[s4]=true;
this[s1]=this[s2]=this[s3]=this[s5]=false;
this[p]=parseFloat(value);
this[v]=null;
break;
default:this[s5]=true;
this[s1]=this[s2]=this[s3]=this[s4]=false;
this[p]=this[v]=null;
break;
}
if(wasPercent!=this[s2]){switch(name){case "minWidth":case "maxWidth":case "width":case "left":case "right":this._invalidateHasPercentX();
break;
case "maxHeight":case "minHeight":case "height":case "top":case "bottom":this._invalidateHasPercentY();
break;
}}if(wasAuto!=this[s3]){switch(name){case "minWidth":case "maxWidth":case "width":this._invalidateHasAutoX();
break;
case "minHeight":case "maxHeight":case "height":this._invalidateHasAutoY();
break;
}}if(wasFlex!=this[s4]){switch(name){case "width":this._invalidateHasFlexX();
break;
case "height":this._invalidateHasFlexY();
break;
}}},
_unitDetectionPixelPercentAuto:function(name,
value){var r=qx.ui.core.Widget.layoutPropertyTypes[name];
var s=r.dataType;
var p=r.dataParsed;
var v=r.dataValue;
var s1=r.typePixel;
var s2=r.typePercent;
var s3=r.typeAuto;
var s4=r.typeNull;
var wasPercent=this[s2];
var wasAuto=this[s3];
switch(this[s]=this._evalUnitsPixelPercentAuto(value)){case qx.ui.core.Widget.TYPE_PIXEL:this[s1]=true;
this[s2]=this[s3]=this[s4]=false;
this[p]=this[v]=Math.round(value);
break;
case qx.ui.core.Widget.TYPE_PERCENT:this[s2]=true;
this[s1]=this[s3]=this[s4]=false;
this[p]=parseFloat(value);
this[v]=null;
break;
case qx.ui.core.Widget.TYPE_AUTO:this[s3]=true;
this[s1]=this[s2]=this[s4]=false;
this[p]=this[v]=null;
break;
default:this[s4]=true;
this[s1]=this[s2]=this[s3]=false;
this[p]=this[v]=null;
break;
}
if(wasPercent!=this[s2]){switch(name){case "minWidth":case "maxWidth":case "width":case "left":case "right":this._invalidateHasPercentX();
break;
case "minHeight":case "maxHeight":case "height":case "top":case "bottom":this._invalidateHasPercentY();
break;
}}if(wasAuto!=this[s3]){switch(name){case "minWidth":case "maxWidth":case "width":this._invalidateHasAutoX();
break;
case "minHeight":case "maxHeight":case "height":this._invalidateHasAutoY();
break;
}}},
_unitDetectionPixelPercent:function(name,
value){var r=qx.ui.core.Widget.layoutPropertyTypes[name];
var s=r.dataType;
var p=r.dataParsed;
var v=r.dataValue;
var s1=r.typePixel;
var s2=r.typePercent;
var s3=r.typeNull;
var wasPercent=this[s2];
switch(this[s]=this._evalUnitsPixelPercent(value)){case qx.ui.core.Widget.TYPE_PIXEL:this[s1]=true;
this[s2]=this[s3]=false;
this[p]=this[v]=Math.round(value);
break;
case qx.ui.core.Widget.TYPE_PERCENT:this[s2]=true;
this[s1]=this[s3]=false;
this[p]=parseFloat(value);
this[v]=null;
break;
default:this[s3]=true;
this[s1]=this[s2]=false;
this[p]=this[v]=null;
break;
}
if(wasPercent!=this[s2]){switch(name){case "minWidth":case "maxWidth":case "width":case "left":case "right":this._invalidateHasPercentX();
break;
case "minHeight":case "maxHeight":case "height":case "top":case "bottom":this._invalidateHasPercentY();
break;
}}},
getTopLevelWidget:function(){return this._hasParent?this.getParent().getTopLevelWidget():null;
},
moveSelfBefore:function(vBefore){this.getParent().addBefore(this,
vBefore);
},
moveSelfAfter:function(vAfter){this.getParent().addAfter(this,
vAfter);
},
moveSelfToBegin:function(){this.getParent().addAtBegin(this);
},
moveSelfToEnd:function(){this.getParent().addAtEnd(this);
},
getPreviousSibling:function(){var p=this.getParent();
if(p==null){return null;
}var cs=p.getChildren();
return cs[cs.indexOf(this)-1];
},
getNextSibling:function(){var p=this.getParent();
if(p==null){return null;
}var cs=p.getChildren();
return cs[cs.indexOf(this)+1];
},
getPreviousVisibleSibling:function(){if(!this._hasParent){return null;
}var vChildren=this.getParent().getVisibleChildren();
return vChildren[vChildren.indexOf(this)-1];
},
getNextVisibleSibling:function(){if(!this._hasParent){return null;
}var vChildren=this.getParent().getVisibleChildren();
return vChildren[vChildren.indexOf(this)+1];
},
getPreviousActiveSibling:function(vIgnoreClasses){var vPrev=qx.ui.core.Widget.getActiveSiblingHelper(this,
this.getParent(),
-1,
vIgnoreClasses,
null);
return vPrev?vPrev:this.getParent().getLastActiveChild();
},
getNextActiveSibling:function(vIgnoreClasses){var vNext=qx.ui.core.Widget.getActiveSiblingHelper(this,
this.getParent(),
1,
vIgnoreClasses,
null);
return vNext?vNext:this.getParent().getFirstActiveChild();
},
isFirstChild:function(){return this._hasParent&&this.getParent().getFirstChild()==this;
},
isLastChild:function(){return this._hasParent&&this.getParent().getLastChild()==this;
},
isFirstVisibleChild:function(){return this._hasParent&&this.getParent().getFirstVisibleChild()==this;
},
isLastVisibleChild:function(){return this._hasParent&&this.getParent().getLastVisibleChild()==this;
},
hasState:function(vState){return this.__states&&this.__states[vState]?true:false;
},
addState:function(vState){if(!this.__states){this.__states={};
}
if(!this.__states[vState]){this.__states[vState]=true;
if(this._hasParent){qx.ui.core.Widget.addToGlobalStateQueue(this);
}}},
removeState:function(vState){if(this.__states&&this.__states[vState]){delete this.__states[vState];
if(this._hasParent){qx.ui.core.Widget.addToGlobalStateQueue(this);
}}},
_styleFromMap:function(data){var styler=qx.core.Property.$$method.style;
var unstyler=qx.core.Property.$$method.unstyle;
var value;
{for(var prop in data){if(!this[styler[prop]]){throw new Error(this.classname+' has no themeable property "'+prop+'"');
}}};
for(var prop in data){value=data[prop];
value==="undefined"?this[unstyler[prop]]():this[styler[prop]](value);
}},
_unstyleFromArray:function(data){var unstyler=qx.core.Property.$$method.unstyle;
{for(var i=0,
l=data.length;i<l;i++){if(!this[unstyler[data[i]]]){throw new Error(this.classname+' has no themeable property "'+prop+'"');
}}};
for(var i=0,
l=data.length;i<l;i++){this[unstyler[data[i]]]();
}},
_renderAppearance:function(){if(!this.__states){this.__states={};
}this._applyStateStyleFocus(this.__states);
var vAppearance=this.getAppearance();
if(vAppearance){try{var r=qx.theme.manager.Appearance.getInstance().styleFrom(vAppearance,
this.__states);
if(r){this._styleFromMap(r);
}}catch(ex){this.error("Could not apply state appearance",
ex);
}}},
_resetAppearanceThemeWrapper:function(vNewAppearanceTheme,
vOldAppearanceTheme){var vAppearance=this.getAppearance();
if(vAppearance){var vAppearanceManager=qx.theme.manager.Appearance.getInstance();
var vOldAppearanceProperties=vAppearanceManager.styleFromTheme(vOldAppearanceTheme,
vAppearance,
this.__states);
var vNewAppearanceProperties=vAppearanceManager.styleFromTheme(vNewAppearanceTheme,
vAppearance,
this.__states);
var vUnstyleList=[];
for(var prop in vOldAppearanceProperties){if(vNewAppearanceProperties[prop]===undefined){vUnstyleList.push(prop);
}}this._unstyleFromArray(vUnstyleList);
this._styleFromMap(vNewAppearanceProperties);
}},
_applyStateStyleFocus:qx.core.Variant.select("qx.client",
{"mshtml":function(vStates){},
"gecko":function(vStates){if(vStates.focused){if(!qx.event.handler.FocusHandler.mouseFocus&&!this.getHideFocus()){this.setStyleProperty("MozOutline",
"1px dotted invert");
}}else{this.removeStyleProperty("MozOutline");
}},
"default":function(vStates){if(vStates.focused){if(!qx.event.handler.FocusHandler.mouseFocus&&!this.getHideFocus()){this.setStyleProperty("outline",
"1px dotted invert");
}}else{this.removeStyleProperty("outline");
}}}),
addToStateQueue:function(){qx.ui.core.Widget.addToGlobalStateQueue(this);
},
recursiveAddToStateQueue:function(){this.addToStateQueue();
},
_applyAppearance:function(value,
old){if(!this.__states){this.__states={};
}var vAppearanceManager=qx.theme.manager.Appearance.getInstance();
if(value){var vNewAppearanceProperties=vAppearanceManager.styleFrom(value,
this.__states)||{};
}
if(old){var vOldAppearanceProperties=vAppearanceManager.styleFrom(old,
this.__states)||{};
var vUnstyleList=[];
for(var prop in vOldAppearanceProperties){if(!vNewAppearanceProperties||!(prop in vNewAppearanceProperties)){vUnstyleList.push(prop);
}}}
if(vUnstyleList){this._unstyleFromArray(vUnstyleList);
}
if(vNewAppearanceProperties){this._styleFromMap(vNewAppearanceProperties);
}},
_recursiveAppearanceThemeUpdate:function(vNewAppearanceTheme,
vOldAppearanceTheme){try{this._resetAppearanceThemeWrapper(vNewAppearanceTheme,
vOldAppearanceTheme);
}catch(ex){this.error("Failed to update appearance theme",
ex);
}},
_applyElementData:function(elem){},
setHtmlProperty:function(propName,
value){if(!this._htmlProperties){this._htmlProperties={};
}this._htmlProperties[propName]=value;
if(this._isCreated&&this.getElement()[propName]!=value){this.getElement()[propName]=value;
}return true;
},
removeHtmlProperty:qx.core.Variant.select("qx.client",
{"mshtml":function(propName){if(!this._htmlProperties){return;
}delete this._htmlProperties[propName];
if(this._isCreated){this.getElement().removeAttribute(propName);
}return true;
},
"default":function(propName){if(!this._htmlProperties){return;
}delete this._htmlProperties[propName];
if(this._isCreated){this.getElement().removeAttribute(propName);
delete this.getElement()[propName];
}return true;
}}),
getHtmlProperty:function(propName){if(!this._htmlProperties){return "";
}return this._htmlProperties[propName]||"";
},
_applyHtmlProperties:function(elem){var vProperties=this._htmlProperties;
if(vProperties){var propName;
for(propName in vProperties){elem[propName]=vProperties[propName];
}}},
setHtmlAttribute:function(propName,
value){if(!this._htmlAttributes){this._htmlAttributes={};
}this._htmlAttributes[propName]=value;
if(this._isCreated){this.getElement().setAttribute(propName,
value);
}return true;
},
removeHtmlAttribute:function(propName){if(!this._htmlAttributes){return;
}delete this._htmlAttributes[propName];
if(this._isCreated){this.getElement().removeAttribute(propName);
}return true;
},
getHtmlAttribute:function(propName){if(!this._htmlAttributes){return "";
}return this._htmlAttributes[propName]||"";
},
_applyHtmlAttributes:function(elem){var vAttributes=this._htmlAttributes;
if(vAttributes){var propName;
for(propName in vAttributes){elem.setAttribute(propName,
vAttributes[propName]);
}}},
getStyleProperty:function(propName){if(!this._styleProperties){return "";
}return this._styleProperties[propName]||"";
},
__outerElementStyleProperties:{cursor:true,
zIndex:true,
filter:true,
display:true,
visibility:true},
setStyleProperty:function(propName,
value){if(!this._styleProperties){this._styleProperties={};
}this._styleProperties[propName]=value;
if(this._isCreated){var elem=this.__outerElementStyleProperties[propName]?this.getElement():this._getTargetNode();
if(elem){elem.style[propName]=(value==null)?"":value;
}}},
removeStyleProperty:function(propName){if(!this._styleProperties){return;
}delete this._styleProperties[propName];
if(this._isCreated){var elem=this.__outerElementStyleProperties[propName]?this.getElement():this._getTargetNode();
if(elem){elem.style[propName]="";
}}},
_applyStyleProperties:function(elem){var vProperties=this._styleProperties;
if(!vProperties){return;
}var propName;
var vBaseElement=elem;
var vTargetElement=this._getTargetNode();
var elem;
var value;
for(propName in vProperties){elem=this.__outerElementStyleProperties[propName]?vBaseElement:vTargetElement;
value=vProperties[propName];
elem.style[propName]=(value==null)?"":value;
}},
_applyEnabled:function(value,
old){if(value===false){this.addState("disabled");
this.removeState("over");
if(qx.Class.isDefined("qx.ui.form.Button")){this.removeState("abandoned");
this.removeState("pressed");
}}else{this.removeState("disabled");
}},
isFocusable:function(){return this.getEnabled()&&this.isSeeable()&&this.getTabIndex()>=0&&this.getTabIndex()!=null;
},
isFocusRoot:function(){return false;
},
getFocusRoot:function(){if(this._hasParent){return this.getParent().getFocusRoot();
}return null;
},
getActiveChild:function(){var vRoot=this.getFocusRoot();
if(vRoot){return vRoot.getActiveChild();
}return null;
},
_ontabfocus:qx.lang.Function.returnTrue,
_applyFocused:function(value,
old){if(!this.isCreated()){return;
}var vFocusRoot=this.getFocusRoot();
if(vFocusRoot){if(value){vFocusRoot.setFocusedChild(this);
this._visualizeFocus();
}else{if(vFocusRoot.getFocusedChild()==this){vFocusRoot.setFocusedChild(null);
}this._visualizeBlur();
}}},
_applyHideFocus:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){this.setHtmlProperty("hideFocus",
value);
},
"default":qx.lang.Function.returnTrue}),
_visualizeBlur:function(){if(this.getEnableElementFocus()&&(!this.getFocusRoot().getFocusedChild()||(this.getFocusRoot().getFocusedChild()&&this.getFocusRoot().getFocusedChild().getEnableElementFocus()))){try{this.getElement().blur();
}catch(ex){}}this.removeState("focused");
},
_visualizeFocus:function(){if(!qx.event.handler.FocusHandler.mouseFocus&&this.getEnableElementFocus()){try{this.getElement().focus();
}catch(ex){}}this.addState("focused");
},
focus:function(){delete qx.event.handler.FocusHandler.mouseFocus;
this.setFocused(true);
},
blur:function(){delete qx.event.handler.FocusHandler.mouseFocus;
this.setFocused(false);
},
_applyCapture:function(value,
old){var vMgr=qx.event.handler.EventHandler.getInstance();
if(old){vMgr.setCaptureWidget(null);
}else if(value){vMgr.setCaptureWidget(this);
}},
_applyZIndex:function(value,
old){if(value==null){this.removeStyleProperty("zIndex");
}else{this.setStyleProperty("zIndex",
value);
}},
_applyTabIndex:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){this.setHtmlProperty("tabIndex",
value<0?-1:1);
},
"gecko":function(value,
old){this.setStyleProperty("MozUserFocus",
(value<0?"ignore":"normal"));
},
"default":function(value,
old){this.setStyleProperty("userFocus",
(value<0?"ignore":"normal"));
this.setHtmlProperty("tabIndex",
value<0?-1:1);
}}),
_applySelectable:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){},
"gecko":function(value,
old){if(value){this.removeStyleProperty("MozUserSelect");
}else{this.setStyleProperty("MozUserSelect",
"none");
}},
"webkit":function(value,
old){if(value){this.removeStyleProperty("WebkitUserSelect");
}else{this.setStyleProperty("WebkitUserSelect",
"none");
}},
"khtml":function(value,
old){if(value){this.removeStyleProperty("KhtmlUserSelect");
}else{this.setStyleProperty("KhtmlUserSelect",
"none");
}},
"default":function(value,
old){if(value){return this.removeStyleProperty("userSelect");
}else{this.setStyleProperty("userSelect",
"none");
}}}),
_applyOpacity:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){if(value==null||value>=1||value<0){this.removeStyleProperty("filter");
}else{this.setStyleProperty("filter",
("Alpha(Opacity="+Math.round(value*100)+")"));
}},
"default":function(value,
old){if(value==null||value>1){if(qx.core.Variant.isSet("qx.client",
"gecko")){this.removeStyleProperty("MozOpacity");
}else if(qx.core.Variant.isSet("qx.client",
"khtml")){this.removeStyleProperty("KhtmlOpacity");
}this.removeStyleProperty("opacity");
}else{value=qx.lang.Number.limit(value,
0,
1);
if(qx.core.Variant.isSet("qx.client",
"gecko")){this.setStyleProperty("MozOpacity",
value);
}else if(qx.core.Variant.isSet("qx.client",
"khtml")){this.setStyleProperty("KhtmlOpacity",
value);
}this.setStyleProperty("opacity",
value);
}}}),
__cursorMap:qx.core.Variant.select("qx.client",
{"mshtml":{"cursor":"hand",
"ew-resize":"e-resize",
"ns-resize":"n-resize",
"nesw-resize":"ne-resize",
"nwse-resize":"nw-resize"},
"opera":{"col-resize":"e-resize",
"row-resize":"n-resize",
"ew-resize":"e-resize",
"ns-resize":"n-resize",
"nesw-resize":"ne-resize",
"nwse-resize":"nw-resize"},
"default":{}}),
_applyCursor:function(value,
old){if(value){this.setStyleProperty("cursor",
this.__cursorMap[value]||value);
}else{this.removeStyleProperty("cursor");
}},
_applyCommand:function(value,
old){},
_applyBackgroundImage:function(value,
old){var imageMgr=qx.io.image.Manager.getInstance();
var aliasMgr=qx.io.Alias.getInstance();
if(old){imageMgr.hide(old);
}
if(value){imageMgr.show(value);
}aliasMgr.connect(this._styleBackgroundImage,
this,
value);
},
_styleBackgroundImage:function(value){value?this.setStyleProperty("backgroundImage",
"url("+value+")"):this.removeStyleProperty("backgroundImage");
},
_applyClip:function(value,
old){return this._compileClipString();
},
_compileClipString:function(){var vLeft=this.getClipLeft();
var vTop=this.getClipTop();
var vWidth=this.getClipWidth();
var vHeight=this.getClipHeight();
var vRight,
vBottom;
if(vLeft==null){vRight=(vWidth==null?"auto":vWidth+"px");
vLeft="auto";
}else{vRight=(vWidth==null?"auto":vLeft+vWidth+"px");
vLeft=vLeft+"px";
}
if(vTop==null){vBottom=(vHeight==null?"auto":vHeight+"px");
vTop="auto";
}else{vBottom=(vHeight==null?"auto":vTop+vHeight+"px");
vTop=vTop+"px";
}return this.setStyleProperty("clip",
("rect("+vTop+","+vRight+","+vBottom+","+vLeft+")"));
},
_applyOverflow:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){var pv=value;
var pn="overflow";
switch(value){case "scrollX":pn="overflowX";
pv="scroll";
break;
case "scrollY":pn="overflowY";
pv="scroll";
break;
}var a=["overflow",
"overflowX",
"overflowY"];
for(var i=0;i<a.length;i++){if(a[i]!=pn){this.removeStyleProperty(a[i]);
}}
switch(value){case "scrollX":this.setStyleProperty("overflowY",
"hidden");
break;
case "scrollY":this.setStyleProperty("overflowX",
"hidden");
break;
}this._renderOverflow(pn,
pv,
value,
old);
this.addToQueue("overflow");
},
"gecko":function(value,
old){var pv=value;
var pn="overflow";
switch(pv){case "hidden":pv="-moz-scrollbars-none";
break;
case "scrollX":pv="-moz-scrollbars-horizontal";
break;
case "scrollY":pv="-moz-scrollbars-vertical";
break;
}this._renderOverflow(pn,
pv,
value,
old);
this.addToQueue("overflow");
},
"default":function(value,
old){var pv=value;
var pn="overflow";
switch(pv){case "scrollX":case "scrollY":pv="scroll";
break;
}this._renderOverflow(pn,
pv,
value,
old);
this.addToQueue("overflow");
}}),
_renderOverflow:function(pn,
pv,
value,
old){this.setStyleProperty(pn,
pv||"");
this._invalidateFrameWidth();
this._invalidateFrameHeight();
},
getOverflowX:function(){var vOverflow=this.getOverflow();
return vOverflow=="scrollY"?"hidden":vOverflow;
},
getOverflowY:function(){var vOverflow=this.getOverflow();
return vOverflow=="scrollX"?"hidden":vOverflow;
},
_applyBackgroundColor:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._styleBackgroundColor,
this,
value);
},
_styleBackgroundColor:function(value){value?this.setStyleProperty("backgroundColor",
value):this.removeStyleProperty("backgroundColor");
},
_applyTextColor:function(value,
old){},
_applyFont:function(value,
old){},
_cachedBorderTop:0,
_cachedBorderRight:0,
_cachedBorderBottom:0,
_cachedBorderLeft:0,
_applyBorder:function(value,
old){qx.theme.manager.Border.getInstance().connect(this._queueBorder,
this,
value);
},
__borderJobs:{top:"borderTop",
right:"borderRight",
bottom:"borderBottom",
left:"borderLeft"},
_queueBorder:function(value,
edge){if(!edge){var jobs=this.__borderJobs;
for(var entry in jobs){this.addToQueue(jobs[entry]);
}this.__reflowBorderX(value);
this.__reflowBorderY(value);
}else{if(edge==="left"||edge==="right"){this.__reflowBorderX(value);
}else{this.__reflowBorderY(value);
}this.addToQueue(this.__borderJobs[edge]);
}this.__borderObject=value;
},
__reflowBorderX:function(value){var oldLeftWidth=this._cachedBorderLeft;
var oldRightWidth=this._cachedBorderRight;
this._cachedBorderLeft=value?value.getWidthLeft():0;
this._cachedBorderRight=value?value.getWidthRight():0;
if((oldLeftWidth+oldRightWidth)!=(this._cachedBorderLeft+this._cachedBorderRight)){this._invalidateFrameWidth();
}},
__reflowBorderY:function(value){var oldTopWidth=this._cachedBorderTop;
var oldBottomWidth=this._cachedBorderBottom;
this._cachedBorderTop=value?value.getWidthTop():0;
this._cachedBorderBottom=value?value.getWidthBottom():0;
if((oldTopWidth+oldBottomWidth)!=(this._cachedBorderTop+this._cachedBorderBottom)){this._invalidateFrameHeight();
}},
renderBorder:function(changes){var value=this.__borderObject;
var mgr=qx.theme.manager.Border.getInstance();
if(value){if(changes.borderTop){value.renderTop(this);
}
if(changes.borderRight){value.renderRight(this);
}
if(changes.borderBottom){value.renderBottom(this);
}
if(changes.borderLeft){value.renderLeft(this);
}}else{var border=qx.ui.core.Border;
if(changes.borderTop){border.resetTop(this);
}
if(changes.borderRight){border.resetRight(this);
}
if(changes.borderBottom){border.resetBottom(this);
}
if(changes.borderLeft){border.resetLeft(this);
}}},
prepareEnhancedBorder:qx.core.Variant.select("qx.client",
{"gecko":qx.lang.Function.returnTrue,
"default":function(){var elem=this.getElement();
var cl=this._borderElement=document.createElement("div");
var es=elem.style;
var cs=this._innerStyle=cl.style;
if(qx.core.Variant.isSet("qx.client",
"mshtml")){}else{cs.width=cs.height="100%";
}cs.position="absolute";
for(var i in this._styleProperties){switch(i){case "zIndex":case "filter":case "display":break;
default:cs[i]=es[i];
es[i]="";
}}
for(var i in this._htmlProperties){switch(i){case "unselectable":cl.unselectable=this._htmlProperties[i];
}}while(elem.firstChild){cl.appendChild(elem.firstChild);
}elem.appendChild(cl);
}}),
_applyPaddingTop:function(value,
old){this.addToQueue("paddingTop");
this._invalidateFrameHeight();
},
_applyPaddingRight:function(value,
old){this.addToQueue("paddingRight");
this._invalidateFrameWidth();
},
_applyPaddingBottom:function(value,
old){this.addToQueue("paddingBottom");
this._invalidateFrameHeight();
},
_applyPaddingLeft:function(value,
old){this.addToQueue("paddingLeft");
this._invalidateFrameWidth();
},
renderPadding:function(changes){},
_applyMarginLeft:function(value,
old){this.addToQueue("marginLeft");
},
_applyMarginRight:function(value,
old){this.addToQueue("marginRight");
},
_applyMarginTop:function(value,
old){this.addToQueue("marginTop");
},
_applyMarginBottom:function(value,
old){this.addToQueue("marginBottom");
},
execute:function(){var cmd=this.getCommand();
if(cmd){cmd.execute(this);
}this.createDispatchEvent("execute");
},
_visualPropertyCheck:function(){if(!this.isCreated()){throw new Error(this.classname+": Element must be created previously!");
}},
setScrollLeft:function(nScrollLeft){this._visualPropertyCheck();
this._getTargetNode().scrollLeft=nScrollLeft;
},
setScrollTop:function(nScrollTop){this._visualPropertyCheck();
this._getTargetNode().scrollTop=nScrollTop;
},
getOffsetLeft:function(){this._visualPropertyCheck();
return qx.html.Offset.getLeft(this.getElement());
},
getOffsetTop:function(){this._visualPropertyCheck();
return qx.html.Offset.getTop(this.getElement());
},
getScrollLeft:function(){this._visualPropertyCheck();
return this._getTargetNode().scrollLeft;
},
getScrollTop:function(){this._visualPropertyCheck();
return this._getTargetNode().scrollTop;
},
getClientWidth:function(){this._visualPropertyCheck();
return this._getTargetNode().clientWidth;
},
getClientHeight:function(){this._visualPropertyCheck();
return this._getTargetNode().clientHeight;
},
getOffsetWidth:function(){this._visualPropertyCheck();
return this.getElement().offsetWidth;
},
getOffsetHeight:function(){this._visualPropertyCheck();
return this.getElement().offsetHeight;
},
getScrollWidth:function(){this._visualPropertyCheck();
return this.getElement().scrollWidth;
},
getScrollHeight:function(){this._visualPropertyCheck();
return this.getElement().scrollHeight;
},
scrollIntoView:function(alignTopLeft){this.scrollIntoViewX(alignTopLeft);
this.scrollIntoViewY(alignTopLeft);
},
scrollIntoViewX:function(alignLeft){if(!this._isCreated||!this._isDisplayable){this.warn("The function scrollIntoViewX can only be called after the widget is created!");
return false;
}return qx.html.ScrollIntoView.scrollX(this.getElement(),
alignLeft);
},
scrollIntoViewY:function(alignTop){if(!this._isCreated||!this._isDisplayable){this.warn("The function scrollIntoViewY can only be called after the widget is created!");
return false;
}return qx.html.ScrollIntoView.scrollY(this.getElement(),
alignTop);
},
supportsDrop:function(dragCache){return true;
}},
settings:{"qx.widgetQueueDebugging":false},
defer:function(statics,
members){statics.__initApplyMethods(members);
if(qx.core.Variant.isSet("qx.client",
"mshtml")){members._renderRuntimeWidth=function(v){this._style.pixelWidth=(v==null)?0:v;
if(this._innerStyle){this._innerStyle.pixelWidth=(v==null)?0:v-2;
}};
members._renderRuntimeHeight=function(v){this._style.pixelHeight=(v==null)?0:v;
if(this._innerStyle){this._innerStyle.pixelHeight=(v==null)?0:v-2;
}};
members._resetRuntimeWidth=function(){this._style.width="";
if(this._innerStyle){this._innerStyle.width="";
}};
members._resetRuntimeHeight=function(){this._style.height="";
if(this._innerStyle){this._innerStyle.height="";
}};
}statics.__initLayoutProperties(statics);
{if(qx.core.Setting.get("qx.widgetQueueDebugging")){statics.flushGlobalQueues=function(){if(statics._inFlushGlobalQueues||!qx.core.Init.getInstance().getApplication().getUiReady()){return;
}
if(!(statics._globalWidgetQueue.length>0||statics._globalElementQueue.length>0||statics._globalStateQueue.length>0||statics._globalJobQueue.length>0||statics._globalLayoutQueue.length>0||statics._fastGlobalDisplayQueue.length>0||!qx.lang.Object.isEmpty(statics._lazyGlobalDisplayQueue))){return;
}var globalWidgetQueueLength=statics._globalWidgetQueue.length;
var globalElementQueueLength=statics._globalElementQueue.length;
var globalStateQueueLength=statics._globalStateQueue.length;
var globalJobQueueLength=statics._globalJobQueue.length;
var globalLayoutQueueLength=statics._globalLayoutQueue.length;
var fastGlobalDisplayQueueLength=statics._fastGlobalDisplayQueue.length;
var lazyGlobalDisplayQueueLength=statics._lazyGlobalDisplayQueue?statics._lazyGlobalDisplayQueue.length:0;
statics._inFlushGlobalQueues=true;
var start;
start=(new Date).valueOf();
statics.flushGlobalWidgetQueue();
var vWidgetDuration=(new Date).valueOf()-start;
start=(new Date).valueOf();
statics.flushGlobalStateQueue();
var vStateDuration=(new Date).valueOf()-start;
start=(new Date).valueOf();
statics.flushGlobalElementQueue();
var vElementDuration=(new Date).valueOf()-start;
start=(new Date).valueOf();
statics.flushGlobalJobQueue();
var vJobDuration=(new Date).valueOf()-start;
start=(new Date).valueOf();
statics.flushGlobalLayoutQueue();
var vLayoutDuration=(new Date).valueOf()-start;
start=(new Date).valueOf();
statics.flushGlobalDisplayQueue();
var vDisplayDuration=(new Date).valueOf()-start;
var vSum=vWidgetDuration+vStateDuration+vElementDuration+vJobDuration+vLayoutDuration+vDisplayDuration;
if(vSum>0){var logger=qx.log.Logger.getClassLogger(qx.ui.core.Widget);
logger.debug("Flush Global Queues");
logger.debug("Widgets: "+vWidgetDuration+"ms ("+globalWidgetQueueLength+")");
logger.debug("State: "+vStateDuration+"ms ("+globalStateQueueLength+")");
logger.debug("Element: "+vElementDuration+"ms ("+globalElementQueueLength+")");
logger.debug("Job: "+vJobDuration+"ms ("+globalJobQueueLength+")");
logger.debug("Layout: "+vLayoutDuration+"ms ("+globalLayoutQueueLength+")");
logger.debug("Display: "+vDisplayDuration+"ms (fast:"+fastGlobalDisplayQueueLength+",lazy:"+lazyGlobalDisplayQueueLength+")");
window.status="Flush: Widget:"+vWidgetDuration+" State:"+vStateDuration+" Element:"+vElementDuration+" Job:"+vJobDuration+" Layout:"+vLayoutDuration+" Display:"+vDisplayDuration;
}delete statics._inFlushGlobalQueues;
};
}};
},
destruct:function(){var elem=this.getElement();
if(elem){elem.qx_Widget=null;
}this._disposeFields("_isCreated",
"_inlineEvents",
"_element",
"_style",
"_borderElement",
"_innerStyle",
"_oldParent",
"_styleProperties",
"_htmlProperties",
"_htmlAttributes",
"__states",
"_jobQueue",
"_layoutChanges",
"__borderObject");
}});




/* ID: qx.html.Dimension */
qx.Class.define("qx.html.Dimension",
{statics:{getOuterWidth:function(el){return qx.html.Dimension.getBoxWidth(el)+qx.html.Style.getMarginLeft(el)+qx.html.Style.getMarginRight(el);
},
getOuterHeight:function(el){return qx.html.Dimension.getBoxHeight(el)+qx.html.Style.getMarginTop(el)+qx.html.Style.getMarginBottom(el);
},
getBoxWidthForZeroHeight:function(el){var h=el.offsetHeight;
if(h==0){var o=el.style.height;
el.style.height="1px";
}var v=el.offsetWidth;
if(h==0){el.style.height=o;
}return v;
},
getBoxHeightForZeroWidth:function(el){var w=el.offsetWidth;
if(w==0){var o=el.style.width;
el.style.width="1px";
}var v=el.offsetHeight;
if(w==0){el.style.width=o;
}return v;
},
getBoxWidth:function(el){return el.offsetWidth;
},
getBoxHeight:function(el){return el.offsetHeight;
},
getAreaWidth:qx.core.Variant.select("qx.client",
{"gecko":function(el){if(el.clientWidth!=0&&el.clientWidth!=(qx.html.Style.getBorderLeft(el)+qx.html.Style.getBorderRight(el))){return el.clientWidth;
}else{return qx.html.Dimension.getBoxWidth(el)-qx.html.Dimension.getInsetLeft(el)-qx.html.Dimension.getInsetRight(el);
}},
"default":function(el){return el.clientWidth!=0?el.clientWidth:(qx.html.Dimension.getBoxWidth(el)-qx.html.Dimension.getInsetLeft(el)-qx.html.Dimension.getInsetRight(el));
}}),
getAreaHeight:qx.core.Variant.select("qx.client",
{"gecko":function(el){if(el.clientHeight!=0&&el.clientHeight!=(qx.html.Style.getBorderTop(el)+qx.html.Style.getBorderBottom(el))){return el.clientHeight;
}else{return qx.html.Dimension.getBoxHeight(el)-qx.html.Dimension.getInsetTop(el)-qx.html.Dimension.getInsetBottom(el);
}},
"default":function(el){return el.clientHeight!=0?el.clientHeight:(qx.html.Dimension.getBoxHeight(el)-qx.html.Dimension.getInsetTop(el)-qx.html.Dimension.getInsetBottom(el));
}}),
getInnerWidth:function(el){return qx.html.Dimension.getAreaWidth(el)-qx.html.Style.getPaddingLeft(el)-qx.html.Style.getPaddingRight(el);
},
getInnerHeight:function(el){return qx.html.Dimension.getAreaHeight(el)-qx.html.Style.getPaddingTop(el)-qx.html.Style.getPaddingBottom(el);
},
getInsetLeft:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.clientLeft;
},
"default":function(el){return qx.html.Style.getBorderLeft(el);
}}),
getInsetTop:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.clientTop;
},
"default":function(el){return qx.html.Style.getBorderTop(el);
}}),
getInsetRight:qx.core.Variant.select("qx.client",
{"mshtml":function(el){if(qx.html.Style.getStyleProperty(el,
"overflowY")=="hidden"||el.clientWidth==0){return qx.html.Style.getBorderRight(el);
}return Math.max(0,
el.offsetWidth-el.clientLeft-el.clientWidth);
},
"default":function(el){if(el.clientWidth==0){var ov=qx.html.Style.getStyleProperty(el,
"overflow");
var sbv=ov=="scroll"||ov=="-moz-scrollbars-vertical"?16:0;
return Math.max(0,
qx.html.Style.getBorderRight(el)+sbv);
}return Math.max(0,
el.offsetWidth-el.clientWidth-qx.html.Style.getBorderLeft(el));
}}),
getInsetBottom:qx.core.Variant.select("qx.client",
{"mshtml":function(el){if(qx.html.Style.getStyleProperty(el,
"overflowX")=="hidden"||el.clientHeight==0){return qx.html.Style.getBorderBottom(el);
}return Math.max(0,
el.offsetHeight-el.clientTop-el.clientHeight);
},
"default":function(el){if(el.clientHeight==0){var ov=qx.html.Style.getStyleProperty(el,
"overflow");
var sbv=ov=="scroll"||ov=="-moz-scrollbars-horizontal"?16:0;
return Math.max(0,
qx.html.Style.getBorderBottom(el)+sbv);
}return Math.max(0,
el.offsetHeight-el.clientHeight-qx.html.Style.getBorderTop(el));
}}),
getScrollBarSizeLeft:function(el){return 0;
},
getScrollBarSizeTop:function(el){return 0;
},
getScrollBarSizeRight:function(el){return qx.html.Dimension.getInsetRight(el)-qx.html.Style.getBorderRight(el);
},
getScrollBarSizeBottom:function(el){return qx.html.Dimension.getInsetBottom(el)-qx.html.Style.getBorderBottom(el);
},
getScrollBarVisibleX:function(el){return qx.html.Dimension.getScrollBarSizeRight(el)>0;
},
getScrollBarVisibleY:function(el){return qx.html.Dimension.getScrollBarSizeBottom(el)>0;
}}});




/* ID: qx.html.Style */
qx.Class.define("qx.html.Style",
{statics:{getStylePropertySure:qx.lang.Object.select((document.defaultView&&document.defaultView.getComputedStyle)?"hasComputed":"noComputed",
{"hasComputed":function(el,
prop){return !el?null:el.ownerDocument?el.ownerDocument.defaultView.getComputedStyle(el,
"")[prop]:el.style[prop];
},
"noComputed":qx.core.Variant.select("qx.client",
{"mshtml":function(el,
prop){try{if(!el){return null;
}
if(el.parentNode&&el.currentStyle){return el.currentStyle[prop];
}else{var v1=el.runtimeStyle[prop];
if(v1!=null&&typeof v1!="undefined"&&v1!=""){return v1;
}return el.style[prop];
}}catch(ex){throw new Error("Could not evaluate computed style: "+el+"["+prop+"]: "+ex);
}},
"default":function(el,
prop){return !el?null:el.style[prop];
}})}),
getStyleProperty:qx.lang.Object.select((document.defaultView&&document.defaultView.getComputedStyle)?"hasComputed":"noComputed",
{"hasComputed":function(el,
prop){try{return el.ownerDocument.defaultView.getComputedStyle(el,
"")[prop];
}catch(ex){throw new Error("Could not evaluate computed style: "+el+"["+prop+"]: "+ex);
}},
"noComputed":qx.core.Variant.select("qx.client",
{"mshtml":function(el,
prop){try{return el.currentStyle[prop];
}catch(ex){throw new Error("Could not evaluate computed style: "+el+"["+prop+"]: "+ex);
}},
"default":function(el,
prop){try{return el.style[prop];
}catch(ex){throw new Error("Could not evaluate computed style: "+el+"["+prop+"]");
}}})}),
getStyleSize:function(vElement,
propertyName){return parseInt(qx.html.Style.getStyleProperty(vElement,
propertyName))||0;
},
getMarginLeft:function(vElement){return qx.html.Style.getStyleSize(vElement,
"marginLeft");
},
getMarginTop:function(vElement){return qx.html.Style.getStyleSize(vElement,
"marginTop");
},
getMarginRight:function(vElement){return qx.html.Style.getStyleSize(vElement,
"marginRight");
},
getMarginBottom:function(vElement){return qx.html.Style.getStyleSize(vElement,
"marginBottom");
},
getPaddingLeft:function(vElement){return qx.html.Style.getStyleSize(vElement,
"paddingLeft");
},
getPaddingTop:function(vElement){return qx.html.Style.getStyleSize(vElement,
"paddingTop");
},
getPaddingRight:function(vElement){return qx.html.Style.getStyleSize(vElement,
"paddingRight");
},
getPaddingBottom:function(vElement){return qx.html.Style.getStyleSize(vElement,
"paddingBottom");
},
getBorderLeft:function(vElement){return qx.html.Style.getStyleProperty(vElement,
"borderLeftStyle")=="none"?0:qx.html.Style.getStyleSize(vElement,
"borderLeftWidth");
},
getBorderTop:function(vElement){return qx.html.Style.getStyleProperty(vElement,
"borderTopStyle")=="none"?0:qx.html.Style.getStyleSize(vElement,
"borderTopWidth");
},
getBorderRight:function(vElement){return qx.html.Style.getStyleProperty(vElement,
"borderRightStyle")=="none"?0:qx.html.Style.getStyleSize(vElement,
"borderRightWidth");
},
getBorderBottom:function(vElement){return qx.html.Style.getStyleProperty(vElement,
"borderBottomStyle")=="none"?0:qx.html.Style.getStyleSize(vElement,
"borderBottomWidth");
}}});




/* ID: qx.html.StyleSheet */
qx.Class.define("qx.html.StyleSheet",
{statics:{includeFile:function(vHref){var el=document.createElement("link");
el.type="text/css";
el.rel="stylesheet";
el.href=vHref;
var head=document.getElementsByTagName("head")[0];
head.appendChild(el);
},
createElement:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vCssText){var vSheet=document.createStyleSheet();
if(vCssText){vSheet.cssText=vCssText;
}return vSheet;
},
"other":function(vCssText){var vElement=document.createElement("style");
vElement.type="text/css";
vElement.appendChild(document.createTextNode(vCssText||"body {}"));
document.getElementsByTagName("head")[0].appendChild(vElement);
if(vElement.sheet){return vElement.sheet;
}else{var styles=document.styleSheets;
for(var i=styles.length-1;i>=0;i--){if(styles[i].ownerNode==vElement){return styles[i];
}}}throw "Error: Could not get a reference to the sheet object";
}}),
addRule:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet,
vSelector,
vStyle){vSheet.addRule(vSelector,
vStyle);
},
"other":qx.lang.Object.select(qx.core.Client.getInstance().isSafari2()?"safari2":"other",
{"safari2+":function(vSheet,
vSelector,
vStyle){if(!vSheet._qxRules){vSheet._qxRules={};
}
if(!vSheet._qxRules[vSelector]){var ruleNode=document.createTextNode(vSelector+"{"+vStyle+"}");
vSheet.ownerNode.appendChild(ruleNode);
vSheet._qxRules[vSelector]=ruleNode;
}},
"other":function(vSheet,
vSelector,
vStyle){vSheet.insertRule(vSelector+"{"+vStyle+"}",
vSheet.cssRules.length);
}})}),
removeRule:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet,
vSelector){var vRules=vSheet.rules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){if(vRules[i].selectorText==vSelector){vSheet.removeRule(i);
}}},
"other":qx.lang.Object.select(qx.core.Client.getInstance().isSafari2()?"safari2":"other",
{"safari2+":function(vSheet,
vSelector){var warn=function(){qx.log.Logger.ROOT_LOGGER.warn("In Safari/Webkit you can only remove rules that are created using qx.html.StyleSheet.addRule");
};
if(!vSheet._qxRules){warn();
}var ruleNode=vSheet._qxRules[vSelector];
if(ruleNode){vSheet.ownerNode.removeChild(ruleNode);
vSheet._qxRules[vSelector]=null;
}else{warn();
}},
"other":function(vSheet,
vSelector){var vRules=vSheet.cssRules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){if(vRules[i].selectorText==vSelector){vSheet.deleteRule(i);
}}}})}),
removeAllRules:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet){var vRules=vSheet.rules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){vSheet.removeRule(i);
}},
"other":qx.lang.Object.select(qx.core.Client.getInstance().isSafari2()?"safari2":"other",
{"safari2+":function(vSheet){var node=vSheet.ownerNode;
var rules=node.childNodes;
while(rules.length>0){node.removeChild(rules[0]);
}},
"other":function(vSheet){var vRules=vSheet.cssRules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){vSheet.deleteRule(i);
}}})}),
addImport:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet,
vUrl){vSheet.addImport(vUrl);
},
"other":qx.lang.Object.select(qx.core.Client.getInstance().isSafari2()?"safari2":"other",
{"safari2+":function(vSheet,
vUrl){vSheet.ownerNode.appendChild(document.createTextNode('@import "'+vUrl+'";'));
},
"other":function(vSheet,
vUrl){vSheet.insertRule('@import "'+vUrl+'";',
vSheet.cssRules.length);
}})}),
removeImport:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet,
vUrl){var vImports=vSheet.imports;
var vLength=vImports.length;
for(var i=vLength-1;i>=0;i--){if(vImports[i].href==vUrl){vSheet.removeImport(i);
}}},
"other":function(vSheet,
vUrl){var vRules=vSheet.cssRules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){if(vRules[i].href==vUrl){vSheet.deleteRule(i);
}}}}),
removeAllImports:qx.lang.Object.select(document.createStyleSheet?"ie4+":"other",
{"ie4+":function(vSheet){var vImports=vSheet.imports;
var vLength=vImports.length;
for(var i=vLength-1;i>=0;i--){vSheet.removeImport(i);
}},
"other":function(vSheet){var vRules=vSheet.cssRules;
var vLength=vRules.length;
for(var i=vLength-1;i>=0;i--){if(vRules[i].type==vRules[i].IMPORT_RULE){vSheet.deleteRule(i);
}}}})}});




/* ID: qx.ui.core.Parent */
qx.Class.define("qx.ui.core.Parent",
{extend:qx.ui.core.Widget,
type:"abstract",
construct:function(){this.base(arguments);
this._children=[];
this._layoutImpl=this._createLayoutImpl();
},
properties:{focusHandler:{check:"qx.event.handler.FocusHandler",
apply:"_applyFocusHandler",
nullable:true},
activeChild:{check:"qx.ui.core.Widget",
apply:"_applyActiveChild",
event:"changeActiveChild",
nullable:true},
focusedChild:{check:"qx.ui.core.Widget",
apply:"_applyFocusedChild",
event:"changeFocusedChild",
nullable:true},
visibleChildren:{_cached:true,
defaultValue:null}},
members:{isFocusRoot:function(){return this.getFocusHandler()!=null;
},
getFocusRoot:function(){if(this.isFocusRoot()){return this;
}
if(this._hasParent){return this.getParent().getFocusRoot();
}return null;
},
activateFocusRoot:function(){this.setFocusHandler(new qx.event.handler.FocusHandler(this));
},
_onfocuskeyevent:function(e){this.getFocusHandler()._onkeyevent(this,
e);
},
_applyFocusHandler:function(value,
old){if(value){this.addEventListener("keypress",
this._onfocuskeyevent);
if(this.getTabIndex()<1){this.setTabIndex(1);
}this.setHideFocus(true);
this.setActiveChild(this);
}else{this.removeEventListener("keydown",
this._onfocuskeyevent);
this.removeEventListener("keypress",
this._onfocuskeyevent);
this.setTabIndex(-1);
this.setHideFocus(false);
}},
_applyActiveChild:function(value,
old){},
_applyFocusedChild:function(value,
old){var vFocusValid=value!=null;
var vBlurValid=old!=null;
if(qx.Class.isDefined("qx.ui.popup.PopupManager")&&vFocusValid){var vMgr=qx.ui.popup.PopupManager.getInstance();
if(vMgr){vMgr.update(value);
}}
if(vBlurValid){if(old.hasEventListeners("focusout")){var vEventObject=new qx.event.type.FocusEvent("focusout",
old);
if(vFocusValid){vEventObject.setRelatedTarget(value);
}old.dispatchEvent(vEventObject);
vEventObject.dispose();
}}
if(vFocusValid){if(value.hasEventListeners("focusin")){var vEventObject=new qx.event.type.FocusEvent("focusin",
value);
if(vBlurValid){vEventObject.setRelatedTarget(old);
}value.dispatchEvent(vEventObject);
vEventObject.dispose();
}}
if(vBlurValid){if(this.getActiveChild()==old&&!vFocusValid){this.setActiveChild(null);
}old.setFocused(false);
var vEventObject=new qx.event.type.FocusEvent("blur",
old);
if(vFocusValid){vEventObject.setRelatedTarget(value);
}old.dispatchEvent(vEventObject);
if(qx.Class.isDefined("qx.ui.popup.ToolTipManager")){var vMgr=qx.ui.popup.ToolTipManager.getInstance();
if(vMgr){vMgr.handleBlur(vEventObject);
}}vEventObject.dispose();
}
if(vFocusValid){this.setActiveChild(value);
value.setFocused(true);
qx.event.handler.EventHandler.getInstance().setFocusRoot(this);
var vEventObject=new qx.event.type.FocusEvent("focus",
value);
if(vBlurValid){vEventObject.setRelatedTarget(old);
}value.dispatchEvent(vEventObject);
if(qx.Class.isDefined("qx.ui.popup.ToolTipManager")){var vMgr=qx.ui.popup.ToolTipManager.getInstance();
if(vMgr){vMgr.handleFocus(vEventObject);
}}vEventObject.dispose();
}},
_layoutImpl:null,
_createLayoutImpl:function(){return null;
},
getLayoutImpl:function(){return this._layoutImpl;
},
getChildren:function(){return this._children;
},
getChildrenLength:function(){return this.getChildren().length;
},
hasChildren:function(){return this.getChildrenLength()>0;
},
isEmpty:function(){return this.getChildrenLength()==0;
},
indexOf:function(vChild){return this.getChildren().indexOf(vChild);
},
contains:function(vWidget){switch(vWidget){case null:return false;
case this:return true;
default:return this.contains(vWidget.getParent());
}},
_computeVisibleChildren:function(){var vVisible=[];
var vChildren=this.getChildren();
if(!vChildren){return 0;
}var vLength=vChildren.length;
for(var i=0;i<vLength;i++){var vChild=vChildren[i];
if(vChild._isDisplayable){vVisible.push(vChild);
}}return vVisible;
},
getVisibleChildrenLength:function(){return this.getVisibleChildren().length;
},
hasVisibleChildren:function(){return this.getVisibleChildrenLength()>0;
},
isVisibleEmpty:function(){return this.getVisibleChildrenLength()==0;
},
add:function(varargs){var vWidget;
for(var i=0,
l=arguments.length;i<l;i++){vWidget=arguments[i];
if(!(vWidget instanceof qx.ui.core.Parent)&&!(vWidget instanceof qx.ui.basic.Terminator)){throw new Error("Invalid Widget: "+vWidget);
}else{vWidget.setParent(this);
}}return this;
},
addAt:function(vChild,
vIndex){if(vIndex==null||vIndex<0){throw new Error("Not a valid index for addAt(): "+vIndex);
}
if(vChild.getParent()==this){var vChildren=this.getChildren();
var vOldIndex=vChildren.indexOf(vChild);
if(vOldIndex!=vIndex){if(vOldIndex!=-1){qx.lang.Array.removeAt(vChildren,
vOldIndex);
}qx.lang.Array.insertAt(vChildren,
vChild,
vIndex);
if(this._initialLayoutDone){this._invalidateVisibleChildren();
this.getLayoutImpl().updateChildrenOnMoveChild(vChild,
vIndex,
vOldIndex);
}}}else{vChild._insertIndex=vIndex;
vChild.setParent(this);
}},
addAtBegin:function(vChild){return this.addAt(vChild,
0);
},
addAtEnd:function(vChild){var vLength=this.getChildrenLength();
return this.addAt(vChild,
vChild.getParent()==this?vLength-1:vLength);
},
addBefore:function(vChild,
vBefore){var vChildren=this.getChildren();
var vTargetIndex=vChildren.indexOf(vBefore);
if(vTargetIndex==-1){throw new Error("Child to add before: "+vBefore+" is not inside this parent.");
}var vSourceIndex=vChildren.indexOf(vChild);
if(vSourceIndex==-1||vSourceIndex>vTargetIndex){vTargetIndex++;
}return this.addAt(vChild,
Math.max(0,
vTargetIndex-1));
},
addAfter:function(vChild,
vAfter){var vChildren=this.getChildren();
var vTargetIndex=vChildren.indexOf(vAfter);
if(vTargetIndex==-1){throw new Error("Child to add after: "+vAfter+" is not inside this parent.");
}var vSourceIndex=vChildren.indexOf(vChild);
if(vSourceIndex!=-1&&vSourceIndex<vTargetIndex){vTargetIndex--;
}return this.addAt(vChild,
Math.min(vChildren.length,
vTargetIndex+1));
},
remove:function(varargs){var vWidget;
for(var i=0,
l=arguments.length;i<l;i++){vWidget=arguments[i];
if(!(vWidget instanceof qx.ui.core.Parent)&&!(vWidget instanceof qx.ui.basic.Terminator)){throw new Error("Invalid Widget: "+vWidget);
}else if(vWidget.getParent()==this){vWidget.setParent(null);
}}},
removeAt:function(vIndex){var vChild=this.getChildren()[vIndex];
if(vChild){delete vChild._insertIndex;
vChild.setParent(null);
}},
removeAll:function(){var cs=this.getChildren();
var co=cs[0];
while(co){this.remove(co);
co=cs[0];
}},
getFirstChild:function(){return qx.lang.Array.getFirst(this.getChildren())||null;
},
getFirstVisibleChild:function(){return qx.lang.Array.getFirst(this.getVisibleChildren())||null;
},
getFirstActiveChild:function(vIgnoreClasses){return qx.ui.core.Widget.getActiveSiblingHelper(null,
this,
1,
vIgnoreClasses,
"first")||null;
},
getLastChild:function(){return qx.lang.Array.getLast(this.getChildren())||null;
},
getLastVisibleChild:function(){return qx.lang.Array.getLast(this.getVisibleChildren())||null;
},
getLastActiveChild:function(vIgnoreClasses){return qx.ui.core.Widget.getActiveSiblingHelper(null,
this,
-1,
vIgnoreClasses,
"last")||null;
},
forEachChild:function(vFunc){var ch=this.getChildren(),
chc,
i=-1;
if(!ch){return;
}
while(chc=ch[++i]){vFunc.call(chc,
i);
}},
forEachVisibleChild:function(vFunc){var ch=this.getVisibleChildren(),
chc,
i=-1;
if(!ch){return;
}
while(chc=ch[++i]){vFunc.call(chc,
i);
}},
_beforeAppear:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._beforeAppear();
}});
},
_afterAppear:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._afterAppear();
}});
},
_beforeDisappear:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._beforeDisappear();
}});
},
_afterDisappear:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._afterDisappear();
}});
},
_beforeInsertDom:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._beforeInsertDom();
}});
},
_afterInsertDom:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._afterInsertDom();
}});
},
_beforeRemoveDom:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._beforeRemoveDom();
}});
},
_afterRemoveDom:function(){this.base(arguments);
this.forEachVisibleChild(function(){if(this.isAppearRelevant()){this._afterRemoveDom();
}});
},
_handleDisplayableCustom:function(vDisplayable,
vParent,
vHint){this.forEachChild(function(){this._handleDisplayable();
});
},
_addChildrenToStateQueue:function(){this.forEachVisibleChild(function(){this.addToStateQueue();
});
},
recursiveAddToStateQueue:function(){this.addToStateQueue();
this.forEachVisibleChild(function(){this.recursiveAddToStateQueue();
});
},
_recursiveAppearanceThemeUpdate:function(vNewAppearanceTheme,
vOldAppearanceTheme){this.base(arguments,
vNewAppearanceTheme,
vOldAppearanceTheme);
this.forEachVisibleChild(function(){this._recursiveAppearanceThemeUpdate(vNewAppearanceTheme,
vOldAppearanceTheme);
});
},
_addChildToChildrenQueue:function(vChild){if(!vChild._isInParentChildrenQueue&&!vChild._isDisplayable){this.warn("Ignoring invisible child: "+vChild);
}
if(!vChild._isInParentChildrenQueue&&vChild._isDisplayable){qx.ui.core.Widget.addToGlobalLayoutQueue(this);
if(!this._childrenQueue){this._childrenQueue={};
}this._childrenQueue[vChild.toHashCode()]=vChild;
}},
_removeChildFromChildrenQueue:function(vChild){if(this._childrenQueue&&vChild._isInParentChildrenQueue){delete this._childrenQueue[vChild.toHashCode()];
if(qx.lang.Object.isEmpty(this._childrenQueue)){qx.ui.core.Widget.removeFromGlobalLayoutQueue(this);
}}},
_flushChildrenQueue:function(){if(!qx.lang.Object.isEmpty(this._childrenQueue)){this.getLayoutImpl().flushChildrenQueue(this._childrenQueue);
delete this._childrenQueue;
}},
_addChildrenToLayoutQueue:function(p){this.forEachChild(function(){this.addToLayoutChanges(p);
});
},
_layoutChild:function(vChild){if(!vChild._isDisplayable){return ;
}var vChanges=vChild._layoutChanges;
try{if(vChild.renderBorder){if(vChanges.borderTop||vChanges.borderRight||vChanges.borderBottom||vChanges.borderLeft){vChild.renderBorder(vChanges);
}}}catch(ex){this.error("Could not apply border to child "+vChild,
ex);
}
try{if(vChild.renderPadding){if(vChanges.paddingLeft||vChanges.paddingRight||vChanges.paddingTop||vChanges.paddingBottom){vChild.renderPadding(vChanges);
}}}catch(ex){this.error("Could not apply padding to child "+vChild,
ex);
}try{this.getLayoutImpl().layoutChild(vChild,
vChanges);
}catch(ex){this.error("Could not layout child "+vChild+" through layout handler",
ex);
}try{vChild._layoutPost(vChanges);
}catch(ex){this.error("Could not post layout child "+vChild,
ex);
}try{if(vChanges.initial){vChild._initialLayoutDone=true;
qx.ui.core.Widget.addToGlobalDisplayQueue(vChild);
}}catch(ex){this.error("Could not handle display updates from layout flush for child "+vChild,
ex);
}vChild._layoutChanges={};
delete vChild._isInParentLayoutQueue;
delete this._childrenQueue[vChild.toHashCode()];
},
_layoutPost:qx.lang.Function.returnTrue,
_computePreferredInnerWidth:function(){return this.getLayoutImpl().computeChildrenNeededWidth();
},
_computePreferredInnerHeight:function(){return this.getLayoutImpl().computeChildrenNeededHeight();
},
_changeInnerWidth:function(vNew,
vOld){var vLayout=this.getLayoutImpl();
if(vLayout.invalidateChildrenFlexWidth){vLayout.invalidateChildrenFlexWidth();
}this.forEachVisibleChild(function(){if(vLayout.updateChildOnInnerWidthChange(this)&&this._recomputeBoxWidth()){this._recomputeOuterWidth();
this._recomputeInnerWidth();
}});
},
_changeInnerHeight:function(vNew,
vOld){var vLayout=this.getLayoutImpl();
if(vLayout.invalidateChildrenFlexHeight){vLayout.invalidateChildrenFlexHeight();
}this.forEachVisibleChild(function(){if(vLayout.updateChildOnInnerHeightChange(this)&&this._recomputeBoxHeight()){this._recomputeOuterHeight();
this._recomputeInnerHeight();
}});
},
getInnerWidthForChild:function(vChild){return this.getInnerWidth();
},
getInnerHeightForChild:function(vChild){return this.getInnerHeight();
},
_remappingChildTable:["add",
"remove",
"addAt",
"addAtBegin",
"addAtEnd",
"removeAt",
"addBefore",
"addAfter",
"removeAll"],
_remapStart:"return this._remappingChildTarget.",
_remapStop:".apply(this._remappingChildTarget, arguments)",
remapChildrenHandlingTo:function(vTarget){var t=this._remappingChildTable;
this._remappingChildTarget=vTarget;
for(var i=0,
l=t.length,
s;i<l;i++){s=t[i];
this[s]=new Function(qx.ui.core.Parent.prototype._remapStart+s+qx.ui.core.Parent.prototype._remapStop);
}}},
defer:function(statics,
members,
properties){if(qx.core.Variant.isSet("qx.client",
"opera")){members._layoutChildOrig=members._layoutChild;
members._layoutChild=function(vChild){if(!vChild._initialLayoutDone||!vChild._layoutChanges.border){return this._layoutChildOrig(vChild);
}var vStyle=vChild.getElement().style;
var vOldDisplay=vStyle.display;
vStyle.display="none";
var vRet=this._layoutChildOrig(vChild);
vStyle.display=vOldDisplay;
return vRet;
};
}},
destruct:function(){this._disposeObjectDeep("_children",
1);
this._disposeObjects("_layoutImpl");
this._disposeFields("_childrenQueue",
"_childrenQueue",
"_remappingChildTable",
"_remappingChildTarget",
"_cachedVisibleChildren");
}});




/* ID: qx.event.type.FocusEvent */
qx.Class.define("qx.event.type.FocusEvent",
{extend:qx.event.type.Event,
construct:function(type,
target){this.base(arguments,
type);
this.setTarget(target);
switch(type){case "focusin":case "focusout":this.setBubbles(true);
this.setPropagationStopped(false);
}}});




/* ID: qx.event.handler.EventHandler */
qx.Class.define("qx.event.handler.EventHandler",
{type:"singleton",
extend:qx.core.Target,
construct:function(){this.base(arguments);
this.__onmouseevent=qx.lang.Function.bind(this._onmouseevent,
this);
this.__ondragevent=qx.lang.Function.bind(this._ondragevent,
this);
this.__onselectevent=qx.lang.Function.bind(this._onselectevent,
this);
this.__onwindowblur=qx.lang.Function.bind(this._onwindowblur,
this);
this.__onwindowfocus=qx.lang.Function.bind(this._onwindowfocus,
this);
this.__onwindowresize=qx.lang.Function.bind(this._onwindowresize,
this);
this._commands={};
},
events:{"error":"qx.event.type.DataEvent"},
statics:{mouseEventTypes:["mouseover",
"mousemove",
"mouseout",
"mousedown",
"mouseup",
"click",
"dblclick",
"contextmenu",
qx.core.Variant.isSet("qx.client",
"mshtml")?"mousewheel":"DOMMouseScroll"],
keyEventTypes:["keydown",
"keypress",
"keyup"],
dragEventTypes:qx.core.Variant.select("qx.client",
{"gecko":["dragdrop",
"dragover",
"dragenter",
"dragexit",
"draggesture"],
"mshtml":["dragend",
"dragover",
"dragstart",
"drag",
"dragenter",
"dragleave"],
"default":["dragstart",
"dragdrop",
"dragover",
"drag",
"dragleave",
"dragenter",
"dragexit",
"draggesture"]}),
getDomTarget:qx.core.Variant.select("qx.client",
{"mshtml":function(vDomEvent){return vDomEvent.target||vDomEvent.srcElement;
},
"webkit":function(vDomEvent){var vNode=vDomEvent.target||vDomEvent.srcElement;
if(vNode&&(vNode.nodeType==qx.dom.Node.TEXT)){vNode=vNode.parentNode;
}return vNode;
},
"default":function(vDomEvent){return vDomEvent.target;
}}),
stopDomEvent:function(vDomEvent){if(vDomEvent.preventDefault){vDomEvent.preventDefault();
}vDomEvent.returnValue=false;
},
getOriginalTargetObject:function(vNode){if(vNode==document.documentElement){vNode=document.body;
}while(vNode!=null&&vNode.qx_Widget==null){try{vNode=vNode.parentNode;
}catch(vDomEvent){vNode=null;
}}return vNode?vNode.qx_Widget:null;
},
getOriginalTargetObjectFromEvent:function(vDomEvent,
vWindow){var vNode=qx.event.handler.EventHandler.getDomTarget(vDomEvent);
if(vWindow){var vDocument=vWindow.document;
if(vNode==vWindow||vNode==vDocument||vNode==vDocument.documentElement||vNode==vDocument.body){return vDocument.body.qx_Widget;
}}return qx.event.handler.EventHandler.getOriginalTargetObject(vNode);
},
getRelatedOriginalTargetObjectFromEvent:function(vDomEvent){return qx.event.handler.EventHandler.getOriginalTargetObject(vDomEvent.relatedTarget||(vDomEvent.type=="mouseover"?vDomEvent.fromElement:vDomEvent.toElement));
},
getTargetObject:function(vNode,
vObject,
allowDisabled){if(!vObject){var vObject=qx.event.handler.EventHandler.getOriginalTargetObject(vNode);
if(!vObject){return null;
}}while(vObject){if(!allowDisabled&&!vObject.getEnabled()){return null;
}if(!vObject.getAnonymous()){break;
}vObject=vObject.getParent();
}return vObject;
},
getTargetObjectFromEvent:function(vDomEvent){return qx.event.handler.EventHandler.getTargetObject(qx.event.handler.EventHandler.getDomTarget(vDomEvent));
},
getRelatedTargetObjectFromEvent:function(vDomEvent){var target=vDomEvent.relatedTarget;
if(!target){if(vDomEvent.type=="mouseover"){target=vDomEvent.fromElement;
}else{target=vDomEvent.toElement;
}}return qx.event.handler.EventHandler.getTargetObject(target);
}},
properties:{allowClientContextMenu:{check:"Boolean",
init:false},
allowClientSelectAll:{check:"Boolean",
init:false},
captureWidget:{check:"qx.ui.core.Widget",
nullable:true,
apply:"_applyCaptureWidget"},
focusRoot:{check:"qx.ui.core.Parent",
nullable:true,
apply:"_applyFocusRoot"}},
members:{_lastMouseEventType:null,
_lastMouseDown:false,
_lastMouseEventDate:0,
_applyCaptureWidget:function(value,
old){if(old){old.setCapture(false);
}
if(value){value.setCapture(true);
}},
_applyFocusRoot:function(value,
old){if(old){old.setFocusedChild(null);
}
if(value&&value.getFocusedChild()==null){value.setFocusedChild(value);
}},
addCommand:function(vCommand){this._commands[vCommand.toHashCode()]=vCommand;
},
removeCommand:function(vCommand){delete this._commands[vCommand.toHashCode()];
},
_checkKeyEventMatch:function(e){var vCommand;
for(var vHash in this._commands){vCommand=this._commands[vHash];
if(vCommand.getEnabled()&&vCommand.matchesKeyEvent(e)){if(!vCommand.execute(e.getTarget())){e.preventDefault();
}break;
}}},
attachEvents:function(){this.attachEventTypes(qx.event.handler.EventHandler.mouseEventTypes,
this.__onmouseevent);
this.attachEventTypes(qx.event.handler.EventHandler.dragEventTypes,
this.__ondragevent);
qx.event.handler.KeyEventHandler.getInstance()._attachEvents();
qx.html.EventRegistration.addEventListener(window,
"blur",
this.__onwindowblur);
qx.html.EventRegistration.addEventListener(window,
"focus",
this.__onwindowfocus);
qx.html.EventRegistration.addEventListener(window,
"resize",
this.__onwindowresize);
document.body.onselect=document.onselectstart=document.onselectionchange=this.__onselectevent;
},
detachEvents:function(){this.detachEventTypes(qx.event.handler.EventHandler.mouseEventTypes,
this.__onmouseevent);
this.detachEventTypes(qx.event.handler.EventHandler.dragEventTypes,
this.__ondragevent);
qx.event.handler.KeyEventHandler.getInstance()._detachEvents();
qx.html.EventRegistration.removeEventListener(window,
"blur",
this.__onwindowblur);
qx.html.EventRegistration.removeEventListener(window,
"focus",
this.__onwindowfocus);
qx.html.EventRegistration.removeEventListener(window,
"resize",
this.__onwindowresize);
document.body.onselect=document.onselectstart=document.onselectionchange=null;
},
attachEventTypes:function(vEventTypes,
vFunctionPointer){try{var el=qx.core.Variant.isSet("qx.client",
"gecko")?window:document.body;
for(var i=0,
l=vEventTypes.length;i<l;i++){qx.html.EventRegistration.addEventListener(el,
vEventTypes[i],
vFunctionPointer);
}}catch(ex){throw new Error("qx.event.handler.EventHandler: Failed to attach window event types: "+vEventTypes+": "+ex);
}},
detachEventTypes:function(vEventTypes,
vFunctionPointer){try{var el=qx.core.Variant.isSet("qx.client",
"gecko")?window:document.body;
for(var i=0,
l=vEventTypes.length;i<l;i++){qx.html.EventRegistration.removeEventListener(el,
vEventTypes[i],
vFunctionPointer);
}}catch(ex){throw new Error("qx.event.handler.EventHandler: Failed to detach window event types: "+vEventTypes+": "+ex);
}},
_onkeyevent_post:function(vDomEvent,
vType,
vKeyCode,
vCharCode,
vKeyIdentifier){var vDomTarget=qx.event.handler.EventHandler.getDomTarget(vDomEvent);
var vFocusRoot=this.getFocusRoot();
var vTarget=this.getCaptureWidget()||(vFocusRoot==null?null:vFocusRoot.getActiveChild());
if(vTarget==null||!vTarget.getEnabled()){return false;
}var vDomEventTarget=vTarget.getElement();
switch(vKeyIdentifier){case "Escape":case "Tab":if(qx.Class.isDefined("qx.ui.menu.Manager")){qx.ui.menu.Manager.getInstance().update(vTarget,
vType);
}break;
}if(!this.getAllowClientSelectAll()){if(vDomEvent.ctrlKey&&vKeyIdentifier=="A"){switch(vDomTarget.tagName.toLowerCase()){case "input":case "textarea":case "iframe":break;
default:qx.event.handler.EventHandler.stopDomEvent(vDomEvent);
}}}var vKeyEventObject=new qx.event.type.KeyEvent(vType,
vDomEvent,
vDomTarget,
vTarget,
null,
vKeyCode,
vCharCode,
vKeyIdentifier);
if(vType=="keydown"){this._checkKeyEventMatch(vKeyEventObject);
}
try{vTarget.dispatchEvent(vKeyEventObject);
if(qx.Class.isDefined("qx.event.handler.DragAndDropHandler")){qx.event.handler.DragAndDropHandler.getInstance().handleKeyEvent(vKeyEventObject);
}}catch(ex){this.error("Failed to dispatch key event",
ex);
this.createDispatchDataEvent("error",
ex);
}vKeyEventObject.dispose();
qx.ui.core.Widget.flushGlobalQueues();
},
_onmouseevent:qx.core.Variant.select("qx.client",
{"mshtml":function(vDomEvent){if(!vDomEvent){vDomEvent=window.event;
}var vDomTarget=qx.event.handler.EventHandler.getDomTarget(vDomEvent);
var vType=vDomEvent.type;
if(vType=="mousemove"){if(this._mouseIsDown&&vDomEvent.button==0){this._onmouseevent_post(vDomEvent,
"mouseup");
this._mouseIsDown=false;
}}else{if(vType=="mousedown"){this._mouseIsDown=true;
}else if(vType=="mouseup"){this._mouseIsDown=false;
}if(vType=="mouseup"&&!this._lastMouseDown&&((new Date).valueOf()-this._lastMouseEventDate)<250){this._onmouseevent_post(vDomEvent,
"mousedown");
}else if(vType=="dblclick"&&this._lastMouseEventType=="mouseup"&&((new Date).valueOf()-this._lastMouseEventDate)<250){this._onmouseevent_post(vDomEvent,
"click");
}
switch(vType){case "mousedown":case "mouseup":case "click":case "dblclick":case "contextmenu":this._lastMouseEventType=vType;
this._lastMouseEventDate=(new Date).valueOf();
this._lastMouseDown=vType=="mousedown";
}}this._onmouseevent_post(vDomEvent,
vType,
vDomTarget);
},
"default":function(vDomEvent){var vDomTarget=qx.event.handler.EventHandler.getDomTarget(vDomEvent);
var vType=vDomEvent.type;
switch(vType){case "DOMMouseScroll":vType="mousewheel";
break;
case "click":case "dblclick":if(vDomEvent.which!==1){return;
}}this._onmouseevent_post(vDomEvent,
vType,
vDomTarget);
}}),
_onmouseevent_click_fix:qx.core.Variant.select("qx.client",
{"gecko":function(vDomTarget,
vType,
vDispatchTarget){var vReturn=false;
switch(vType){case "mousedown":this._lastMouseDownDomTarget=vDomTarget;
this._lastMouseDownDispatchTarget=vDispatchTarget;
break;
case "mouseup":if(this._lastMouseDownDispatchTarget===vDispatchTarget&&vDomTarget!==this._lastMouseDownDomTarget){vReturn=true;
}else{this._lastMouseDownDomTarget=null;
this._lastMouseDownDispatchTarget=null;
}}return vReturn;
},
"default":null}),
_onmouseevent_post:function(vDomEvent,
vType,
vDomTarget){var vEventObject,
vCaptureTarget,
vDispatchTarget,
vTarget,
vOriginalTarget,
vRelatedTarget,
vFixClick,
vTargetIsEnabled;
vCaptureTarget=this.getCaptureWidget();
vOriginalTarget=qx.event.handler.EventHandler.getOriginalTargetObject(vDomTarget);
if(!vCaptureTarget){vDispatchTarget=vTarget=qx.event.handler.EventHandler.getTargetObject(null,
vOriginalTarget,
true);
}else{vDispatchTarget=vCaptureTarget;
vTarget=qx.event.handler.EventHandler.getTargetObject(null,
vOriginalTarget,
true);
}if(!vTarget){return;
}vTargetIsEnabled=vTarget.getEnabled();
if(qx.core.Variant.isSet("qx.client",
"gecko")){vFixClick=this._onmouseevent_click_fix(vDomTarget,
vType,
vDispatchTarget);
}if(vType=="contextmenu"&&!this.getAllowClientContextMenu()){qx.event.handler.EventHandler.stopDomEvent(vDomEvent);
}if(vTargetIsEnabled&&vType=="mousedown"){qx.event.handler.FocusHandler.mouseFocus=true;
var vRoot=vTarget.getFocusRoot();
if(vRoot){this.setFocusRoot(vRoot);
var vFocusTarget=vTarget;
while(!vFocusTarget.isFocusable()&&vFocusTarget!=vRoot){vFocusTarget=vFocusTarget.getParent();
}vRoot.setFocusedChild(vFocusTarget);
vRoot.setActiveChild(vTarget);
}}var vDomEventTarget=vTarget.getElement();
switch(vType){case "mouseover":case "mouseout":vRelatedTarget=qx.event.handler.EventHandler.getRelatedTargetObjectFromEvent(vDomEvent);
if(vRelatedTarget==vTarget){return;
}}vEventObject=new qx.event.type.MouseEvent(vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget,
vRelatedTarget);
qx.event.type.MouseEvent.storeEventState(vEventObject);
if(vTargetIsEnabled){var vEventWasProcessed=false;
vEventWasProcessed=vDispatchTarget?vDispatchTarget.dispatchEvent(vEventObject):true;
this._onmouseevent_special_post(vType,
vTarget,
vOriginalTarget,
vDispatchTarget,
vEventWasProcessed,
vEventObject,
vDomEvent);
}else{if(vType=="mouseover"){if(qx.Class.isDefined("qx.ui.popup.ToolTipManager")){qx.ui.popup.ToolTipManager.getInstance().handleMouseOver(vEventObject);
}}}vEventObject.dispose();
vEventObject=null;
qx.ui.core.Widget.flushGlobalQueues();
if(vFixClick){this._onmouseevent_post(vDomEvent,
"click",
this._lastMouseDownDomTarget);
this._lastMouseDownDomTarget=null;
this._lastMouseDownDispatchTarget=null;
}},
_onmouseevent_special_post:function(vType,
vTarget,
vOriginalTarget,
vDispatchTarget,
vEventWasProcessed,
vEventObject,
vDomEvent){switch(vType){case "mousedown":if(qx.Class.isDefined("qx.ui.popup.PopupManager")){qx.ui.popup.PopupManager.getInstance().update(vTarget);
}
if(qx.Class.isDefined("qx.ui.menu.Manager")){qx.ui.menu.Manager.getInstance().update(vTarget,
vType);
}
if(qx.Class.isDefined("qx.ui.embed.IframeManager")){qx.ui.embed.IframeManager.getInstance().handleMouseDown(vEventObject);
}break;
case "mouseup":if(qx.Class.isDefined("qx.ui.menu.Manager")){qx.ui.menu.Manager.getInstance().update(vTarget,
vType);
}
if(qx.Class.isDefined("qx.ui.embed.IframeManager")){qx.ui.embed.IframeManager.getInstance().handleMouseUp(vEventObject);
}break;
case "mouseover":if(qx.Class.isDefined("qx.ui.popup.ToolTipManager")){qx.ui.popup.ToolTipManager.getInstance().handleMouseOver(vEventObject);
}break;
case "mouseout":if(qx.Class.isDefined("qx.ui.popup.ToolTipManager")){qx.ui.popup.ToolTipManager.getInstance().handleMouseOut(vEventObject);
}break;
}this._ignoreWindowBlur=vType==="mousedown";
if(qx.Class.isDefined("qx.event.handler.DragAndDropHandler")&&vTarget){qx.event.handler.DragAndDropHandler.getInstance().handleMouseEvent(vEventObject);
}},
_ondragevent:function(vEvent){if(!vEvent){vEvent=window.event;
}qx.event.handler.EventHandler.stopDomEvent(vEvent);
},
_onselectevent:function(e){if(!e){e=window.event;
}var target=qx.event.handler.EventHandler.getOriginalTargetObjectFromEvent(e);
while(target){if(target.getSelectable()!=null){if(!target.getSelectable()){qx.event.handler.EventHandler.stopDomEvent(e);
}break;
}target=target.getParent();
}},
_focused:false,
_onwindowblur:function(e){if(!this._focused||this._ignoreWindowBlur){return;
}this._focused=false;
this.setCaptureWidget(null);
if(qx.Class.isDefined("qx.ui.popup.PopupManager")){qx.ui.popup.PopupManager.getInstance().update();
}if(qx.Class.isDefined("qx.ui.menu.Manager")){qx.ui.menu.Manager.getInstance().update();
}if(qx.Class.isDefined("qx.event.handler.DragAndDropHandler")){qx.event.handler.DragAndDropHandler.getInstance().globalCancelDrag();
}qx.ui.core.ClientDocument.getInstance().createDispatchEvent("windowblur");
},
_onwindowfocus:function(e){if(this._focused){return;
}this._focused=true;
qx.ui.core.ClientDocument.getInstance().createDispatchEvent("windowfocus");
},
_onwindowresize:function(e){qx.ui.core.ClientDocument.getInstance().createDispatchEvent("windowresize");
}},
destruct:function(){this.detachEvents();
this._disposeObjectDeep("_commands",
1);
this._disposeFields("__onmouseevent",
"__ondragevent",
"__onselectevent",
"__onwindowblur",
"__onwindowfocus",
"__onwindowresize");
this._disposeFields("_lastMouseEventType",
"_lastMouseDown",
"_lastMouseEventDate",
"_lastMouseDownDomTarget",
"_lastMouseDownDispatchTarget");
}});




/* ID: qx.dom.Node */
qx.Class.define("qx.dom.Node",
{statics:{ELEMENT:1,
ATTRIBUTE:2,
TEXT:3,
CDATA_SECTION:4,
ENTITY_REFERENCE:5,
ENTITY:6,
PROCESSING_INSTRUCTION:7,
COMMENT:8,
DOCUMENT:9,
DOCUMENT_TYPE:10,
DOCUMENT_FRAGMENT:11,
NOTATION:12}});




/* ID: qx.event.handler.KeyEventHandler */
qx.Class.define("qx.event.handler.KeyEventHandler",
{type:"singleton",
extend:qx.core.Target,
construct:function(){this.base(arguments);
this.__onkeypress=qx.lang.Function.bind(this._onkeypress,
this);
this.__onkeyupdown=qx.lang.Function.bind(this._onkeyupdown,
this);
},
members:{_attachEvents:function(){var el=qx.core.Variant.isSet("qx.client",
"gecko")?window:document.body;
qx.html.EventRegistration.addEventListener(el,
"keypress",
this.__onkeypress);
qx.html.EventRegistration.addEventListener(el,
"keyup",
this.__onkeyupdown);
qx.html.EventRegistration.addEventListener(el,
"keydown",
this.__onkeyupdown);
},
_detachEvents:function(){var el=qx.core.Variant.isSet("qx.client",
"gecko")?window:document.body;
qx.html.EventRegistration.removeEventListener(el,
"keypress",
this.__onkeypress);
qx.html.EventRegistration.removeEventListener(el,
"keyup",
this.__onkeyupdown);
qx.html.EventRegistration.removeEventListener(el,
"keydown",
this.__onkeyupdown);
},
_onkeyupdown:qx.core.Variant.select("qx.client",
{"mshtml":function(domEvent){domEvent=window.event||domEvent;
var keyCode=domEvent.keyCode;
var charcode=0;
var type=domEvent.type;
if(!(this._lastUpDownType[keyCode]=="keydown"&&type=="keydown")){this._idealKeyHandler(keyCode,
charcode,
type,
domEvent);
}if(type=="keydown"){if(this._isNonPrintableKeyCode(keyCode)||
keyCode==
8||keyCode==9){this._idealKeyHandler(keyCode,
charcode,
"keypress",
domEvent);
}}this._lastUpDownType[keyCode]=type;
},
"gecko":function(domEvent){var keyCode=this._keyCodeFix[domEvent.keyCode]||domEvent.keyCode;
var charCode=domEvent.charCode;
var type=domEvent.type;
if(qx.core.Client.getInstance().runsOnWindows()){var keyIdentifier=keyCode?this._keyCodeToIdentifier(keyCode):this._charCodeToIdentifier(charCode);
if(!(this._lastUpDownType[keyIdentifier]=="keypress"&&type=="keydown")){this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
}this._lastUpDownType[keyIdentifier]=type;
}else{this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
}},
"webkit":function(domEvent){var keyCode=0;
var charCode=0;
var type=domEvent.type;
if(qx.core.Client.getInstance().getVersion()<420){if(!this._lastCharCodeForType){this._lastCharCodeForType={};
}var isSafariSpecialKey=this._lastCharCodeForType[type]>63000;
if(isSafariSpecialKey){this._lastCharCodeForType[type]=null;
return;
}this._lastCharCodeForType[type]=domEvent.charCode;
}
if(type=="keyup"||type=="keydown"){keyCode=this._charCode2KeyCode[domEvent.charCode]||domEvent.keyCode;
}else{if(this._charCode2KeyCode[domEvent.charCode]){keyCode=this._charCode2KeyCode[domEvent.charCode];
}else{charCode=domEvent.charCode;
}}this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
},
"opera":function(domEvent){this._idealKeyHandler(domEvent.keyCode,
0,
domEvent.type,
domEvent);
},
"default":function(){throw new Error("Unsupported browser for key event handler!");
}}),
_onkeypress:qx.core.Variant.select("qx.client",
{"mshtml":function(domEvent){var domEvent=window.event||domEvent;
if(this._charCode2KeyCode[domEvent.keyCode]){this._idealKeyHandler(this._charCode2KeyCode[domEvent.keyCode],
0,
domEvent.type,
domEvent);
}else{this._idealKeyHandler(0,
domEvent.keyCode,
domEvent.type,
domEvent);
}},
"gecko":function(domEvent){var keyCode=this._keyCodeFix[domEvent.keyCode]||domEvent.keyCode;
var charCode=domEvent.charCode;
var type=domEvent.type;
if(qx.core.Client.getInstance().runsOnWindows()){var keyIdentifier=keyCode?this._keyCodeToIdentifier(keyCode):this._charCodeToIdentifier(charCode);
if(!(this._lastUpDownType[keyIdentifier]=="keypress"&&type=="keydown")){this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
}this._lastUpDownType[keyIdentifier]=type;
}else{this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
}},
"webkit":function(domEvent){var keyCode=0;
var charCode=0;
var type=domEvent.type;
if(qx.core.Client.getInstance().getVersion()<420){if(!this._lastCharCodeForType){this._lastCharCodeForType={};
}var isSafariSpecialKey=this._lastCharCodeForType[type]>63000;
if(isSafariSpecialKey){this._lastCharCodeForType[type]=null;
return;
}this._lastCharCodeForType[type]=domEvent.charCode;
}
if(type=="keyup"||type=="keydown"){keyCode=this._charCode2KeyCode[domEvent.charCode]||domEvent.keyCode;
}else{if(this._charCode2KeyCode[domEvent.charCode]){keyCode=this._charCode2KeyCode[domEvent.charCode];
}else{charCode=domEvent.charCode;
}}this._idealKeyHandler(keyCode,
charCode,
type,
domEvent);
},
"opera":function(domEvent){if(this._keyCodeToIdentifierMap[domEvent.keyCode]){this._idealKeyHandler(domEvent.keyCode,
0,
domEvent.type,
domEvent);
}else{this._idealKeyHandler(0,
domEvent.keyCode,
domEvent.type,
domEvent);
}},
"default":function(){throw new Error("Unsupported browser for key event handler!");
}}),
_specialCharCodeMap:{8:"Backspace",
9:"Tab",
13:"Enter",
27:"Escape",
32:"Space"},
_keyCodeToIdentifierMap:{16:"Shift",
17:"Control",
18:"Alt",
20:"CapsLock",
224:"Meta",
37:"Left",
38:"Up",
39:"Right",
40:"Down",
33:"PageUp",
34:"PageDown",
35:"End",
36:"Home",
45:"Insert",
46:"Delete",
112:"F1",
113:"F2",
114:"F3",
115:"F4",
116:"F5",
117:"F6",
118:"F7",
119:"F8",
120:"F9",
121:"F10",
122:"F11",
123:"F12",
144:"NumLock",
44:"PrintScreen",
145:"Scroll",
19:"Pause",
91:"Win",
93:"Apps"},
_numpadToCharCode:{96:"0".charCodeAt(0),
97:"1".charCodeAt(0),
98:"2".charCodeAt(0),
99:"3".charCodeAt(0),
100:"4".charCodeAt(0),
101:"5".charCodeAt(0),
102:"6".charCodeAt(0),
103:"7".charCodeAt(0),
104:"8".charCodeAt(0),
105:"9".charCodeAt(0),
106:"*".charCodeAt(0),
107:"+".charCodeAt(0),
109:"-".charCodeAt(0),
110:",".charCodeAt(0),
111:"/".charCodeAt(0)},
_charCodeA:"A".charCodeAt(0),
_charCodeZ:"Z".charCodeAt(0),
_charCode0:"0".charCodeAt(0),
_charCode9:"9".charCodeAt(0),
_isNonPrintableKeyCode:function(keyCode){return this._keyCodeToIdentifierMap[keyCode]?true:false;
},
_isIdentifiableKeyCode:function(keyCode){if(keyCode>=this._charCodeA&&keyCode<=this._charCodeZ){return true;
}if(keyCode>=this._charCode0&&keyCode<=this._charCode9){return true;
}if(this._specialCharCodeMap[keyCode]){return true;
}if(this._numpadToCharCode[keyCode]){return true;
}if(this._isNonPrintableKeyCode(keyCode)){return true;
}return false;
},
isValidKeyIdentifier:function(keyIdentifier){if(this._identifierToKeyCodeMap[keyIdentifier]){return true;
}
if(keyIdentifier.length!=1){return false;
}
if(keyIdentifier>="0"&&keyIdentifier<="9"){return true;
}
if(keyIdentifier>="A"&&keyIdentifier<="Z"){return true;
}
switch(keyIdentifier){case "+":case "-":case "*":case "/":return true;
default:return false;
}},
_keyCodeToIdentifier:function(keyCode){if(this._isIdentifiableKeyCode(keyCode)){var numPadKeyCode=this._numpadToCharCode[keyCode];
if(numPadKeyCode){return String.fromCharCode(numPadKeyCode);
}return (this._keyCodeToIdentifierMap[keyCode]||this._specialCharCodeMap[keyCode]||String.fromCharCode(keyCode));
}else{return "Unidentified";
}},
_charCodeToIdentifier:function(charCode){return this._specialCharCodeMap[charCode]||String.fromCharCode(charCode).toUpperCase();
},
_identifierToKeyCode:function(keyIdentifier){return this._identifierToKeyCodeMap[keyIdentifier]||keyIdentifier.charCodeAt(0);
},
_idealKeyHandler:function(keyCode,
charCode,
eventType,
domEvent){if(!keyCode&&!charCode){return;
}var keyIdentifier;
if(keyCode){keyIdentifier=this._keyCodeToIdentifier(keyCode);
qx.event.handler.EventHandler.getInstance()._onkeyevent_post(domEvent,
eventType,
keyCode,
charCode,
keyIdentifier);
}else{keyIdentifier=this._charCodeToIdentifier(charCode);
qx.event.handler.EventHandler.getInstance()._onkeyevent_post(domEvent,
"keypress",
keyCode,
charCode,
keyIdentifier);
qx.event.handler.EventHandler.getInstance()._onkeyevent_post(domEvent,
"keyinput",
keyCode,
charCode,
keyIdentifier);
}}},
defer:function(statics,
members,
properties){if(!members._identifierToKeyCodeMap){members._identifierToKeyCodeMap={};
for(var key in members._keyCodeToIdentifierMap){members._identifierToKeyCodeMap[members._keyCodeToIdentifierMap[key]]=parseInt(key);
}
for(var key in members._specialCharCodeMap){members._identifierToKeyCodeMap[members._specialCharCodeMap[key]]=parseInt(key);
}}
if(qx.core.Variant.isSet("qx.client",
"mshtml")){members._lastUpDownType={};
members._charCode2KeyCode={13:13,
27:27};
}else if(qx.core.Variant.isSet("qx.client",
"gecko")){members._lastUpDownType={};
members._keyCodeFix={12:members._identifierToKeyCode("NumLock")};
}else if(qx.core.Variant.isSet("qx.client",
"webkit")){members._charCode2KeyCode={63289:members._identifierToKeyCode("NumLock"),
63276:members._identifierToKeyCode("PageUp"),
63277:members._identifierToKeyCode("PageDown"),
63275:members._identifierToKeyCode("End"),
63273:members._identifierToKeyCode("Home"),
63234:members._identifierToKeyCode("Left"),
63232:members._identifierToKeyCode("Up"),
63235:members._identifierToKeyCode("Right"),
63233:members._identifierToKeyCode("Down"),
63272:members._identifierToKeyCode("Delete"),
63302:members._identifierToKeyCode("Insert"),
63236:members._identifierToKeyCode("F1"),
63237:members._identifierToKeyCode("F2"),
63238:members._identifierToKeyCode("F3"),
63239:members._identifierToKeyCode("F4"),
63240:members._identifierToKeyCode("F5"),
63241:members._identifierToKeyCode("F6"),
63242:members._identifierToKeyCode("F7"),
63243:members._identifierToKeyCode("F8"),
63244:members._identifierToKeyCode("F9"),
63245:members._identifierToKeyCode("F10"),
63246:members._identifierToKeyCode("F11"),
63247:members._identifierToKeyCode("F12"),
63248:members._identifierToKeyCode("PrintScreen"),
3:members._identifierToKeyCode("Enter"),
12:members._identifierToKeyCode("NumLock"),
13:members._identifierToKeyCode("Enter")};
}},
destruct:function(){this._detachEvents();
this._disposeFields("_lastUpDownType");
}});




/* ID: qx.event.type.DomEvent */
qx.Class.define("qx.event.type.DomEvent",
{extend:qx.event.type.Event,
construct:function(vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget){this.base(arguments,
vType);
this.setDomEvent(vDomEvent);
this.setDomTarget(vDomTarget);
this.setTarget(vTarget);
this.setOriginalTarget(vOriginalTarget);
},
statics:{SHIFT_MASK:1,
CTRL_MASK:2,
ALT_MASK:4,
META_MASK:8},
properties:{bubbles:{_fast:true,
defaultValue:true,
noCompute:true},
propagationStopped:{_fast:true,
defaultValue:false,
noCompute:true},
domEvent:{_fast:true,
setOnlyOnce:true,
noCompute:true},
domTarget:{_fast:true,
setOnlyOnce:true,
noCompute:true},
modifiers:{_cached:true,
defaultValue:null}},
members:{_computeModifiers:function(){var mask=0;
var evt=this.getDomEvent();
if(evt.shiftKey)mask|=qx.event.type.DomEvent.SHIFT_MASK;
if(evt.ctrlKey)mask|=qx.event.type.DomEvent.CTRL_MASK;
if(evt.altKey)mask|=qx.event.type.DomEvent.ALT_MASK;
if(evt.metaKey)mask|=qx.event.type.DomEvent.META_MASK;
return mask;
},
isCtrlPressed:function(){return this.getDomEvent().ctrlKey;
},
isShiftPressed:function(){return this.getDomEvent().shiftKey;
},
isAltPressed:function(){return this.getDomEvent().altKey;
},
isMetaPressed:function(){return this.getDomEvent().metaKey;
},
isCtrlOrCommandPressed:function(){if(qx.core.Client.getInstance().runsOnMacintosh()){return this.getDomEvent().metaKey;
}else{return this.getDomEvent().ctrlKey;
}},
setDefaultPrevented:qx.core.Variant.select("qx.client",
{"mshtml":function(vValue){if(!vValue){return this.error("It is not possible to set preventDefault to false if it was true before!",
"setDefaultPrevented");
}this.getDomEvent().returnValue=false;
this.base(arguments,
vValue);
},
"default":function(vValue){if(!vValue){return this.error("It is not possible to set preventDefault to false if it was true before!",
"setDefaultPrevented");
}this.getDomEvent().preventDefault();
this.getDomEvent().returnValue=false;
this.base(arguments,
vValue);
}})},
destruct:function(){this._disposeFields("_valueDomEvent",
"_valueDomTarget");
}});




/* ID: qx.event.type.KeyEvent */
qx.Class.define("qx.event.type.KeyEvent",
{extend:qx.event.type.DomEvent,
construct:function(vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget,
vKeyCode,
vCharCode,
vKeyIdentifier){this.base(arguments,
vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget);
this._keyCode=vKeyCode;
this.setCharCode(vCharCode);
this.setKeyIdentifier(vKeyIdentifier);
},
statics:{keys:{esc:27,
enter:13,
tab:9,
space:32,
up:38,
down:40,
left:37,
right:39,
shift:16,
ctrl:17,
alt:18,
f1:112,
f2:113,
f3:114,
f4:115,
f5:116,
f6:117,
f7:118,
f8:119,
f9:120,
f10:121,
f11:122,
f12:123,
print:124,
del:46,
backspace:8,
insert:45,
home:36,
end:35,
pageup:33,
pagedown:34,
numlock:144,
numpad_0:96,
numpad_1:97,
numpad_2:98,
numpad_3:99,
numpad_4:100,
numpad_5:101,
numpad_6:102,
numpad_7:103,
numpad_8:104,
numpad_9:105,
numpad_divide:111,
numpad_multiply:106,
numpad_minus:109,
numpad_plus:107},
codes:{}},
properties:{charCode:{_fast:true,
setOnlyOnce:true,
noCompute:true},
keyIdentifier:{_fast:true,
setOnlyOnce:true,
noCompute:true}},
members:{getKeyCode:function(){this.warn("Deprecated: please use getKeyIdentifier() instead.");
this.printStackTrace();
return this._keyCode;
}},
defer:function(statics){for(var i in statics.keys){statics.codes[statics.keys[i]]=i;
}}});




/* ID: qx.event.type.MouseEvent */
qx.Class.define("qx.event.type.MouseEvent",
{extend:qx.event.type.DomEvent,
construct:function(vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget,
vRelatedTarget){this.base(arguments,
vType,
vDomEvent,
vDomTarget,
vTarget,
vOriginalTarget);
if(vRelatedTarget){this.setRelatedTarget(vRelatedTarget);
}},
statics:{C_BUTTON_LEFT:"left",
C_BUTTON_MIDDLE:"middle",
C_BUTTON_RIGHT:"right",
C_BUTTON_NONE:"none",
_screenX:0,
_screenY:0,
_clientX:0,
_clientY:0,
_pageX:0,
_pageY:0,
_button:null,
buttons:qx.core.Variant.select("qx.client",
{"mshtml":{left:1,
right:2,
middle:4},
"default":{left:0,
right:2,
middle:1}}),
storeEventState:function(e){this._screenX=e.getScreenX();
this._screenY=e.getScreenY();
this._clientX=e.getClientX();
this._clientY=e.getClientY();
this._pageX=e.getPageX();
this._pageY=e.getPageY();
this._button=e.getButton();
},
getScreenX:function(){return this._screenX;
},
getScreenY:function(){return this._screenY;
},
getClientX:function(){return this._clientX;
},
getClientY:function(){return this._clientY;
},
getPageX:function(){return this._pageX;
},
getPageY:function(){return this._pageY;
},
getButton:function(){return this._button;
}},
properties:{button:{_fast:true,
readOnly:true},
wheelDelta:{_fast:true,
readOnly:true}},
members:{getPageX:qx.core.Variant.select("qx.client",
{"mshtml":qx.lang.Object.select(qx.core.Client.getInstance().isInQuirksMode()?"quirks":"standard",
{"quirks":function(){return this.getDomEvent().clientX+document.documentElement.scrollLeft;
},
"standard":function(){return this.getDomEvent().clientX+document.body.scrollLeft;
}}),
"gecko":function(){return this.getDomEvent().pageX;
},
"default":function(){return this.getDomEvent().clientX;
}}),
getPageY:qx.core.Variant.select("qx.client",
{"mshtml":qx.lang.Object.select(qx.core.Client.getInstance().isInQuirksMode()?"quirks":"standard",
{"quirks":function(){return this.getDomEvent().clientY+document.documentElement.scrollTop;
},
"standard":function(){return this.getDomEvent().clientY+document.body.scrollTop;
}}),
"gecko":function(){return this.getDomEvent().pageY;
},
"default":function(){return this.getDomEvent().clientY;
}}),
getClientX:qx.core.Variant.select("qx.client",
{"mshtml|gecko":function(){return this.getDomEvent().clientX;
},
"default":function(){return this.getDomEvent().clientX+(document.body&&document.body.scrollLeft!=null?document.body.scrollLeft:0);
}}),
getClientY:qx.core.Variant.select("qx.client",
{"mshtml|gecko":function(){return this.getDomEvent().clientY;
},
"default":function(){return this.getDomEvent().clientY+(document.body&&document.body.scrollTop!=null?document.body.scrollTop:0);
}}),
getScreenX:function(){return this.getDomEvent().screenX;
},
getScreenY:function(){return this.getDomEvent().screenY;
},
isLeftButtonPressed:qx.core.Variant.select("qx.client",
{"mshtml":function(){if(this.getType()=="click"){return true;
}else{return this.getButton()===qx.event.type.MouseEvent.C_BUTTON_LEFT;
}},
"default":function(){return this.getButton()===qx.event.type.MouseEvent.C_BUTTON_LEFT;
}}),
isMiddleButtonPressed:function(){return this.getButton()===qx.event.type.MouseEvent.C_BUTTON_MIDDLE;
},
isRightButtonPressed:function(){return this.getButton()===qx.event.type.MouseEvent.C_BUTTON_RIGHT;
},
_computeButton:function(){var e=this.getDomEvent();
if(e.which!=null){switch(e.which){case 1:return qx.event.type.MouseEvent.C_BUTTON_LEFT;
case 3:return qx.event.type.MouseEvent.C_BUTTON_RIGHT;
case 2:return qx.event.type.MouseEvent.C_BUTTON_MIDDLE;
default:return qx.event.type.MouseEvent.C_BUTTON_NONE;
}}else{switch(e.button){case 1:return qx.event.type.MouseEvent.C_BUTTON_LEFT;
case 2:return qx.event.type.MouseEvent.C_BUTTON_RIGHT;
case 4:return qx.event.type.MouseEvent.C_BUTTON_MIDDLE;
default:return qx.event.type.MouseEvent.C_BUTTON_NONE;
}}},
_computeWheelDelta:qx.core.Variant.select("qx.client",
{"mshtml|opera":function(){return this.getDomEvent().wheelDelta/120;
},
"default":function(){return -(this.getDomEvent().detail/3);
}})}});




/* ID: qx.util.manager.Object */
qx.Class.define("qx.util.manager.Object",
{extend:qx.core.Target,
construct:function(){this.base(arguments);
this._objects={};
},
members:{add:function(vObject){if(this.getDisposed()){return;
}this._objects[vObject.toHashCode()]=vObject;
},
remove:function(vObject){if(this.getDisposed()){return false;
}delete this._objects[vObject.toHashCode()];
},
has:function(vObject){return this._objects[vObject.toHashCode()]!=null;
},
get:function(vObject){return this._objects[vObject.toHashCode()];
},
getAll:function(){return this._objects;
},
enableAll:function(){for(var vHashCode in this._objects){this._objects[vHashCode].setEnabled(true);
}},
disableAll:function(){for(var vHashCode in this._objects){this._objects[vHashCode].setEnabled(false);
}}},
destruct:function(){this._disposeObjectDeep("_objects");
}});




/* ID: qx.ui.embed.IframeManager */
qx.Class.define("qx.ui.embed.IframeManager",
{type:"singleton",
extend:qx.util.manager.Object,
construct:function(){this.base(arguments);
},
members:{handleMouseDown:function(evt){var iframeMap=this.getAll();
for(var key in iframeMap){var iframe=iframeMap[key];
iframe.block();
}},
handleMouseUp:function(evt){var iframeMap=this.getAll();
for(var key in iframeMap){var iframe=iframeMap[key];
iframe.release();
}}}});




/* ID: qx.ui.layout.CanvasLayout */
qx.Class.define("qx.ui.layout.CanvasLayout",
{extend:qx.ui.core.Parent,
construct:function(){this.base(arguments);
},
members:{_createLayoutImpl:function(){return new qx.ui.layout.impl.CanvasLayoutImpl(this);
}}});




/* ID: qx.ui.layout.impl.LayoutImpl */
qx.Class.define("qx.ui.layout.impl.LayoutImpl",
{extend:qx.core.Object,
construct:function(vWidget){this.base(arguments);
this._widget=vWidget;
},
members:{getWidget:function(){return this._widget;
},
computeChildBoxWidth:function(vChild){return vChild.getWidthValue()||vChild._computeBoxWidthFallback();
},
computeChildBoxHeight:function(vChild){return vChild.getHeightValue()||vChild._computeBoxHeightFallback();
},
computeChildNeededWidth:function(vChild){var vMinBox=vChild._computedMinWidthTypePercent?null:vChild.getMinWidthValue();
var vMaxBox=vChild._computedMaxWidthTypePercent?null:vChild.getMaxWidthValue();
var vBox=(vChild._computedWidthTypePercent||vChild._computedWidthTypeFlex?null:vChild.getWidthValue())||vChild.getPreferredBoxWidth()||0;
return qx.lang.Number.limit(vBox,
vMinBox,
vMaxBox)+vChild.getMarginLeft()+vChild.getMarginRight();
},
computeChildNeededHeight:function(vChild){var vMinBox=vChild._computedMinHeightTypePercent?null:vChild.getMinHeightValue();
var vMaxBox=vChild._computedMaxHeightTypePercent?null:vChild.getMaxHeightValue();
var vBox=(vChild._computedHeightTypePercent||vChild._computedHeightTypeFlex?null:vChild.getHeightValue())||vChild.getPreferredBoxHeight()||0;
return qx.lang.Number.limit(vBox,
vMinBox,
vMaxBox)+vChild.getMarginTop()+vChild.getMarginBottom();
},
computeChildrenNeededWidth_max:function(){for(var i=0,
ch=this.getWidget().getVisibleChildren(),
chl=ch.length,
maxv=0;i<chl;i++){maxv=Math.max(maxv,
ch[i].getNeededWidth());
}return maxv;
},
computeChildrenNeededHeight_max:function(){for(var i=0,
ch=this.getWidget().getVisibleChildren(),
chl=ch.length,
maxv=0;i<chl;i++){maxv=Math.max(maxv,
ch[i].getNeededHeight());
}return maxv;
},
computeChildrenNeededWidth_sum:function(){for(var i=0,
ch=this.getWidget().getVisibleChildren(),
chl=ch.length,
sumv=0;i<chl;i++){sumv+=ch[i].getNeededWidth();
}return sumv;
},
computeChildrenNeededHeight_sum:function(){for(var i=0,
ch=this.getWidget().getVisibleChildren(),
chl=ch.length,
sumv=0;i<chl;i++){sumv+=ch[i].getNeededHeight();
}return sumv;
},
computeChildrenNeededWidth:null,
computeChildrenNeededHeight:null,
updateSelfOnChildOuterWidthChange:function(vChild){},
updateSelfOnChildOuterHeightChange:function(vChild){},
updateChildOnInnerWidthChange:function(vChild){},
updateChildOnInnerHeightChange:function(vChild){},
updateSelfOnJobQueueFlush:function(vJobQueue){},
updateChildrenOnJobQueueFlush:function(vJobQueue){},
updateChildrenOnAddChild:function(vChild,
vIndex){},
updateChildrenOnRemoveChild:function(vChild,
vIndex){},
updateChildrenOnMoveChild:function(vChild,
vIndex,
vOldIndex){},
flushChildrenQueue:function(vChildrenQueue){var vWidget=this.getWidget();
for(var vHashCode in vChildrenQueue){vWidget._layoutChild(vChildrenQueue[vHashCode]);
}},
layoutChild:function(vChild,
vJobs){},
layoutChild_sizeLimitX:qx.core.Variant.select("qx.client",
{"mshtml":qx.lang.Function.returnTrue,
"default":function(vChild,
vJobs){if(vJobs.minWidth){vChild._computedMinWidthTypeNull?vChild._resetRuntimeMinWidth():vChild._renderRuntimeMinWidth(vChild.getMinWidthValue());
}else if(vJobs.initial&&!vChild._computedMinWidthTypeNull){vChild._renderRuntimeMinWidth(vChild.getMinWidthValue());
}
if(vJobs.maxWidth){vChild._computedMaxWidthTypeNull?vChild._resetRuntimeMaxWidth():vChild._renderRuntimeMaxWidth(vChild.getMaxWidthValue());
}else if(vJobs.initial&&!vChild._computedMaxWidthTypeNull){vChild._renderRuntimeMaxWidth(vChild.getMaxWidthValue());
}}}),
layoutChild_sizeLimitY:qx.core.Variant.select("qx.client",
{"mshtml":qx.lang.Function.returnTrue,
"default":function(vChild,
vJobs){if(vJobs.minHeight){vChild._computedMinHeightTypeNull?vChild._resetRuntimeMinHeight():vChild._renderRuntimeMinHeight(vChild.getMinHeightValue());
}else if(vJobs.initial&&!vChild._computedMinHeightTypeNull){vChild._renderRuntimeMinHeight(vChild.getMinHeightValue());
}
if(vJobs.maxHeight){vChild._computedMaxHeightTypeNull?vChild._resetRuntimeMaxHeight():vChild._renderRuntimeMaxHeight(vChild.getMaxHeightValue());
}else if(vJobs.initial&&!vChild._computedMaxHeightTypeNull){vChild._renderRuntimeMaxHeight(vChild.getMaxHeightValue());
}}}),
layoutChild_marginX:function(vChild,
vJobs){if(vJobs.marginLeft||vJobs.initial){var vValueLeft=vChild.getMarginLeft();
vValueLeft!=null?vChild._renderRuntimeMarginLeft(vValueLeft):vChild._resetRuntimeMarginLeft();
}
if(vJobs.marginRight||vJobs.initial){var vValueRight=vChild.getMarginRight();
vValueRight!=null?vChild._renderRuntimeMarginRight(vValueRight):vChild._resetRuntimeMarginRight();
}},
layoutChild_marginY:function(vChild,
vJobs){if(vJobs.marginTop||vJobs.initial){var vValueTop=vChild.getMarginTop();
vValueTop!=null?vChild._renderRuntimeMarginTop(vValueTop):vChild._resetRuntimeMarginTop();
}
if(vJobs.marginBottom||vJobs.initial){var vValueBottom=vChild.getMarginBottom();
vValueBottom!=null?vChild._renderRuntimeMarginBottom(vValueBottom):vChild._resetRuntimeMarginBottom();
}},
layoutChild_sizeX_essentialWrapper:function(vChild,
vJobs){return vChild._isWidthEssential()?this.layoutChild_sizeX(vChild,
vJobs):vChild._resetRuntimeWidth();
},
layoutChild_sizeY_essentialWrapper:function(vChild,
vJobs){return vChild._isHeightEssential()?this.layoutChild_sizeY(vChild,
vJobs):vChild._resetRuntimeHeight();
}},
defer:function(statics,
members){members.computeChildrenNeededWidth=members.computeChildrenNeededWidth_max;
members.computeChildrenNeededHeight=members.computeChildrenNeededHeight_max;
},
destruct:function(){this._disposeFields("_widget");
}});




/* ID: qx.lang.Number */
qx.Class.define("qx.lang.Number",
{statics:{isInRange:function(nr,
vmin,
vmax){return nr>=vmin&&nr<=vmax;
},
isBetweenRange:function(nr,
vmin,
vmax){return nr>vmin&&nr<vmax;
},
limit:function(nr,
vmin,
vmax){if(typeof vmax==="number"&&nr>vmax){return vmax;
}else if(typeof vmin==="number"&&nr<vmin){return vmin;
}else{return nr;
}}}});




/* ID: qx.ui.layout.impl.CanvasLayoutImpl */
qx.Class.define("qx.ui.layout.impl.CanvasLayoutImpl",
{extend:qx.ui.layout.impl.LayoutImpl,
construct:function(vWidget){this.base(arguments,
vWidget);
},
members:{computeChildBoxWidth:function(vChild){var vValue=null;
if(vChild._computedLeftTypeNull||vChild._computedRightTypeNull){vValue=vChild.getWidthValue();
}else if(vChild._hasParent){vValue=this.getWidget().getInnerWidth()-vChild.getLeftValue()-vChild.getRightValue();
}return vValue||vChild._computeBoxWidthFallback();
},
computeChildBoxHeight:function(vChild){var vValue=null;
if(vChild._computedTopTypeNull||vChild._computedBottomTypeNull){vValue=vChild.getHeightValue();
}else if(vChild._hasParent){vValue=this.getWidget().getInnerHeight()-vChild.getTopValue()-vChild.getBottomValue();
}return vValue||vChild._computeBoxHeightFallback();
},
computeChildNeededWidth:function(vChild){var vLeft=vChild._computedLeftTypePercent?null:vChild.getLeftValue();
var vRight=vChild._computedRightTypePercent?null:vChild.getRightValue();
var vMinBox=vChild._computedMinWidthTypePercent?null:vChild.getMinWidthValue();
var vMaxBox=vChild._computedMaxWidthTypePercent?null:vChild.getMaxWidthValue();
if(vLeft!=null&&vRight!=null){var vBox=vChild.getPreferredBoxWidth()||0;
}else{var vBox=(vChild._computedWidthTypePercent?null:vChild.getWidthValue())||vChild.getPreferredBoxWidth()||0;
}return qx.lang.Number.limit(vBox,
vMinBox,
vMaxBox)+vLeft+vRight+vChild.getMarginLeft()+vChild.getMarginRight();
},
computeChildNeededHeight:function(vChild){var vTop=vChild._computedTopTypePercent?null:vChild.getTopValue();
var vBottom=vChild._computedBottomTypePercent?null:vChild.getBottomValue();
var vMinBox=vChild._computedMinHeightTypePercent?null:vChild.getMinHeightValue();
var vMaxBox=vChild._computedMaxHeightTypePercent?null:vChild.getMaxHeightValue();
if(vTop!=null&&vBottom!=null){var vBox=vChild.getPreferredBoxHeight()||0;
}else{var vBox=(vChild._computedHeightTypePercent?null:vChild.getHeightValue())||vChild.getPreferredBoxHeight()||0;
}return qx.lang.Number.limit(vBox,
vMinBox,
vMaxBox)+vTop+vBottom+vChild.getMarginTop()+vChild.getMarginBottom();
},
updateChildOnInnerWidthChange:function(vChild){var vUpdatePercent=vChild._recomputePercentX();
var vUpdateRange=vChild._recomputeRangeX();
return vUpdatePercent||vUpdateRange;
},
updateChildOnInnerHeightChange:function(vChild){var vUpdatePercent=vChild._recomputePercentY();
var vUpdateRange=vChild._recomputeRangeY();
return vUpdatePercent||vUpdateRange;
},
layoutChild:function(vChild,
vJobs){this.layoutChild_sizeX_essentialWrapper(vChild,
vJobs);
this.layoutChild_sizeY_essentialWrapper(vChild,
vJobs);
this.layoutChild_sizeLimitX(vChild,
vJobs);
this.layoutChild_sizeLimitY(vChild,
vJobs);
this.layoutChild_locationX(vChild,
vJobs);
this.layoutChild_locationY(vChild,
vJobs);
this.layoutChild_marginX(vChild,
vJobs);
this.layoutChild_marginY(vChild,
vJobs);
},
layoutChild_sizeX:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.width||vJobs.minWidth||vJobs.maxWidth||vJobs.left||vJobs.right){if(vChild._computedMinWidthTypeNull&&vChild._computedWidthTypeNull&&vChild._computedMaxWidthTypeNull&&!(!vChild._computedLeftTypeNull&&!vChild._computedRightTypeNull)){vChild._resetRuntimeWidth();
}else{vChild._renderRuntimeWidth(vChild.getBoxWidth());
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.width){vChild._computedWidthTypeNull?vChild._resetRuntimeWidth():vChild._renderRuntimeWidth(vChild.getWidthValue());
}}}),
layoutChild_sizeY:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.height||vJobs.minHeight||vJobs.maxHeight||vJobs.top||vJobs.bottom){if(vChild._computedMinHeightTypeNull&&vChild._computedHeightTypeNull&&vChild._computedMaxHeightTypeNull&&!(!vChild._computedTopTypeNull&&!vChild._computedBottomTypeNull)){vChild._resetRuntimeHeight();
}else{vChild._renderRuntimeHeight(vChild.getBoxHeight());
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.height){vChild._computedHeightTypeNull?vChild._resetRuntimeHeight():vChild._renderRuntimeHeight(vChild.getHeightValue());
}}}),
layoutChild_locationX:function(vChild,
vJobs){var vWidget=this.getWidget();
if(vJobs.initial||vJobs.left||vJobs.parentPaddingLeft){vChild._computedLeftTypeNull?vChild._computedRightTypeNull&&vWidget.getPaddingLeft()>0?vChild._renderRuntimeLeft(vWidget.getPaddingLeft()):vChild._resetRuntimeLeft():vChild._renderRuntimeLeft(vChild.getLeftValue()+vWidget.getPaddingLeft());
}
if(vJobs.initial||vJobs.right||vJobs.parentPaddingRight){vChild._computedRightTypeNull?vChild._computedLeftTypeNull&&vWidget.getPaddingRight()>0?vChild._renderRuntimeRight(vWidget.getPaddingRight()):vChild._resetRuntimeRight():vChild._renderRuntimeRight(vChild.getRightValue()+vWidget.getPaddingRight());
}},
layoutChild_locationY:function(vChild,
vJobs){var vWidget=this.getWidget();
if(vJobs.initial||vJobs.top||vJobs.parentPaddingTop){vChild._computedTopTypeNull?vChild._computedBottomTypeNull&&vWidget.getPaddingTop()>0?vChild._renderRuntimeTop(vWidget.getPaddingTop()):vChild._resetRuntimeTop():vChild._renderRuntimeTop(vChild.getTopValue()+vWidget.getPaddingTop());
}
if(vJobs.initial||vJobs.bottom||vJobs.parentPaddingBottom){vChild._computedBottomTypeNull?vChild._computedTopTypeNull&&vWidget.getPaddingBottom()>0?vChild._renderRuntimeBottom(vWidget.getPaddingBottom()):vChild._resetRuntimeBottom():vChild._renderRuntimeBottom(vChild.getBottomValue()+vWidget.getPaddingBottom());
}}}});




/* ID: qx.ui.core.ClientDocument */
qx.Class.define("qx.ui.core.ClientDocument",
{type:"singleton",
extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
this._window=window;
this._document=window.document;
this.setElement(this._document.body);
this._document.body.style.position="";
try{document.execCommand("BackgroundImageCache",
false,
true);
}catch(err){}this._cachedInnerWidth=this._document.body.offsetWidth;
this._cachedInnerHeight=this._document.body.offsetHeight;
this.addEventListener("windowresize",
this._onwindowresize);
this._modalWidgets=[];
this._modalNativeWindow=null;
this.activateFocusRoot();
this.initHideFocus();
this.initSelectable();
qx.event.handler.EventHandler.getInstance().setFocusRoot(this);
},
events:{"focus":"qx.event.type.Event",
"windowblur":"qx.event.type.Event",
"windowfocus":"qx.event.type.Event",
"windowresize":"qx.event.type.Event"},
properties:{appearance:{refine:true,
init:"client-document"},
enableElementFocus:{refine:true,
init:false},
enabled:{refine:true,
init:true},
selectable:{refine:true,
init:false},
hideFocus:{refine:true,
init:true},
globalCursor:{check:"String",
nullable:true,
themeable:true,
apply:"_applyGlobalCursor",
event:"changeGlobalCursor"}},
members:{_applyParent:qx.lang.Function.returnTrue,
getTopLevelWidget:qx.lang.Function.returnThis,
getWindowElement:function(){return this._window;
},
getDocumentElement:function(){return this._document;
},
getParent:qx.lang.Function.returnNull,
getToolTip:qx.lang.Function.returnNull,
isMaterialized:qx.lang.Function.returnTrue,
isSeeable:qx.lang.Function.returnTrue,
_isDisplayable:true,
_hasParent:false,
_initialLayoutDone:true,
_getBlocker:function(){if(!this._blocker){this._blocker=new qx.ui.core.ClientDocumentBlocker;
this._blocker.addEventListener("mousedown",
this.blockHelper,
this);
this._blocker.addEventListener("mouseup",
this.blockHelper,
this);
this.add(this._blocker);
}return this._blocker;
},
blockHelper:function(e){if(this._modalNativeWindow){if(!this._modalNativeWindow.isClosed()){this._modalNativeWindow.focus();
}else{this.debug("Window seems to be closed already! => Releasing Blocker");
this.release(this._modalNativeWindow);
}}},
block:function(vActiveChild){this._getBlocker().show();
if(qx.Class.isDefined("qx.ui.window.Window")&&vActiveChild instanceof qx.ui.window.Window){this._modalWidgets.push(vActiveChild);
var vOrigIndex=vActiveChild.getZIndex();
this._getBlocker().setZIndex(vOrigIndex);
vActiveChild.setZIndex(vOrigIndex+1);
}else if(qx.Class.isDefined("qx.client.NativeWindow")&&vActiveChild instanceof qx.client.NativeWindow){this._modalNativeWindow=vActiveChild;
this._getBlocker().setZIndex(1e7);
}},
release:function(vActiveChild){if(vActiveChild){if(qx.Class.isDefined("qx.client.NativeWindow")&&vActiveChild instanceof qx.client.NativeWindow){this._modalNativeWindow=null;
}else{qx.lang.Array.remove(this._modalWidgets,
vActiveChild);
}}var l=this._modalWidgets.length;
if(l==0){this._getBlocker().hide();
}else{var oldActiveChild=this._modalWidgets[l-1];
var o=oldActiveChild.getZIndex();
this._getBlocker().setZIndex(o);
oldActiveChild.setZIndex(o+1);
}},
createStyleElement:function(vCssText){return qx.html.StyleSheet.createElement(vCssText);
},
addCssRule:function(vSheet,
vSelector,
vStyle){return qx.html.StyleSheet.addRule(vSheet,
vSelector,
vStyle);
},
removeCssRule:function(vSheet,
vSelector){return qx.html.StyleSheet.removeRule(vSheet,
vSelector);
},
removeAllCssRules:function(vSheet){return qx.html.StyleSheet.removeAllRules(vSheet);
},
_applyGlobalCursor:qx.core.Variant.select("qx.client",
{"mshtml":function(value,
old){if(value=="pointer"){value="hand";
}
if(old=="pointer"){old="hand";
}var elem,
current;
var list=this._cursorElements;
if(list){for(var i=0,
l=list.length;i<l;i++){elem=list[i];
if(elem.style.cursor==old){elem.style.cursor=elem._oldCursor;
elem._oldCursor=null;
}}}var all=document.all;
var list=this._cursorElements=[];
if(value!=null&&value!=""&&value!="auto"){for(var i=0,
l=all.length;i<l;i++){elem=all[i];
current=elem.style.cursor;
if(current!=null&&current!=""&&current!="auto"){elem._oldCursor=current;
elem.style.cursor=value;
list.push(elem);
}}document.body.style.cursor=value;
}else{document.body.style.cursor="";
}},
"default":function(value,
old){if(!this._globalCursorStyleSheet){this._globalCursorStyleSheet=this.createStyleElement();
}this.removeCssRule(this._globalCursorStyleSheet,
"*");
if(value){this.addCssRule(this._globalCursorStyleSheet,
"*",
"cursor:"+value+" !important");
}}}),
_onwindowresize:function(e){if(qx.Class.isDefined("qx.ui.popup.PopupManager")){qx.ui.popup.PopupManager.getInstance().update();
}this._recomputeInnerWidth();
this._recomputeInnerHeight();
qx.ui.core.Widget.flushGlobalQueues();
},
_computeInnerWidth:function(){return this._document.body.offsetWidth;
},
_computeInnerHeight:function(){return this._document.body.offsetHeight;
}},
settings:{"qx.enableApplicationLayout":true,
"qx.boxModelCorrection":true},
defer:function(){if(qx.core.Setting.get("qx.boxModelCorrection")){var boxSizingAttr=qx.core.Client.getInstance().getEngineBoxSizingAttributes();
var borderBoxCss=boxSizingAttr.join(":border-box;")+":border-box;";
var contentBoxCss=boxSizingAttr.join(":content-box;")+":content-box;";
qx.html.StyleSheet.createElement("html,body { margin:0;border:0;padding:0; } "+"html { border:0 none; } "+"*{"+borderBoxCss+"} "+"img{"+contentBoxCss+"}");
}
if(qx.core.Setting.get("qx.enableApplicationLayout")){qx.html.StyleSheet.createElement("html,body{width:100%;height:100%;overflow:hidden;}");
}},
destruct:function(){this._disposeObjects("_blocker");
this._disposeFields("_window",
"_document",
"_modalWidgets",
"_modalNativeWindow",
"_globalCursorStyleSheet");
}});




/* ID: qx.ui.basic.Terminator */
qx.Class.define("qx.ui.basic.Terminator",
{extend:qx.ui.core.Widget,
members:{renderPadding:function(changes){if(changes.paddingLeft){this._renderRuntimePaddingLeft(this.getPaddingLeft());
}
if(changes.paddingRight){this._renderRuntimePaddingRight(this.getPaddingRight());
}
if(changes.paddingTop){this._renderRuntimePaddingTop(this.getPaddingTop());
}
if(changes.paddingBottom){this._renderRuntimePaddingBottom(this.getPaddingBottom());
}},
_renderContent:function(){if(this._computedWidthTypePixel){this._cachedPreferredInnerWidth=null;
}else{this._invalidatePreferredInnerWidth();
}if(this._computedHeightTypePixel){this._cachedPreferredInnerHeight=null;
}else{this._invalidatePreferredInnerHeight();
}if(this._initialLayoutDone){this.addToJobQueue("load");
}},
_layoutPost:function(changes){if(changes.initial||changes.load||changes.width||changes.height){this._postApply();
}},
_postApply:qx.lang.Function.returnTrue,
_computeBoxWidthFallback:function(){return this.getPreferredBoxWidth();
},
_computeBoxHeightFallback:function(){return this.getPreferredBoxHeight();
},
_computePreferredInnerWidth:qx.lang.Function.returnZero,
_computePreferredInnerHeight:qx.lang.Function.returnZero,
_isWidthEssential:function(){if(!this._computedLeftTypeNull&&!this._computedRightTypeNull){return true;
}
if(!this._computedWidthTypeNull&&!this._computedWidthTypeAuto){return true;
}
if(!this._computedMinWidthTypeNull&&!this._computedMinWidthTypeAuto){return true;
}
if(!this._computedMaxWidthTypeNull&&!this._computedMaxWidthTypeAuto){return true;
}
if(this._borderElement){return true;
}return false;
},
_isHeightEssential:function(){if(!this._computedTopTypeNull&&!this._computedBottomTypeNull){return true;
}
if(!this._computedHeightTypeNull&&!this._computedHeightTypeAuto){return true;
}
if(!this._computedMinHeightTypeNull&&!this._computedMinHeightTypeAuto){return true;
}
if(!this._computedMaxHeightTypeNull&&!this._computedMaxHeightTypeAuto){return true;
}
if(this._borderElement){return true;
}return false;
}}});




/* ID: qx.ui.core.ClientDocumentBlocker */
qx.Class.define("qx.ui.core.ClientDocumentBlocker",
{extend:qx.ui.basic.Terminator,
construct:function(){this.base(arguments);
this.initTop();
this.initRight();
this.initBottom();
this.initLeft();
this.initZIndex();
},
properties:{appearance:{refine:true,
init:"client-document-blocker"},
zIndex:{refine:true,
init:1e8},
top:{refine:true,
init:0},
right:{refine:true,
init:0},
bottom:{refine:true,
init:0},
left:{refine:true,
init:0},
display:{refine:true,
init:false}},
members:{getFocusRoot:function(){return null;
}}});




/* ID: qx.theme.manager.Appearance */
qx.Class.define("qx.theme.manager.Appearance",
{type:"singleton",
extend:qx.util.manager.Object,
construct:function(){this.base(arguments);
this.__cache={};
this.__stateMap={};
this.__stateMapLength=1;
},
properties:{appearanceTheme:{check:"Theme",
nullable:true,
apply:"_applyAppearanceTheme",
event:"changeAppearanceTheme"}},
members:{_applyAppearanceTheme:function(value,
old){this._currentTheme=value;
this._oldTheme=old;
if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncAppearanceTheme();
}},
syncAppearanceTheme:function(){if(!this._currentTheme&&!this._oldTheme){return;
}
if(this._currentTheme){this.__cache[this._currentTheme.name]={};
}var app=qx.core.Init.getInstance().getApplication();
if(app&&app.getUiReady()){qx.ui.core.ClientDocument.getInstance()._recursiveAppearanceThemeUpdate(this._currentTheme,
this._oldTheme);
}
if(this._oldTheme){delete this.__cache[this._oldTheme.name];
}delete this._currentTheme;
delete this._oldTheme;
},
styleFrom:function(id,
states){var theme=this.getAppearanceTheme();
if(!theme){return;
}return this.styleFromTheme(theme,
id,
states);
},
styleFromTheme:function(theme,
id,
states){var entry=theme.appearances[id];
if(!entry){{this.warn("Missing appearance entry: "+id);
};
return null;
}if(!entry.style){if(entry.include){return this.styleFromTheme(theme,
entry.include,
states);
}else{return null;
}}var map=this.__stateMap;
var helper=[id];
for(var state in states){if(!map[state]){map[state]=this.__stateMapLength++;
}helper[map[state]]=true;
}var unique=helper.join();
var cache=this.__cache[theme.name];
if(cache&&cache[unique]!==undefined){return cache[unique];
}var result;
if(entry.include||entry.base){var local=entry.style(states);
var incl;
if(entry.include){incl=this.styleFromTheme(theme,
entry.include,
states);
}result={};
if(entry.base&&theme.supertheme){var base=this.styleFromTheme(theme.supertheme,
id,
states);
if(entry.include){for(var key in base){if(incl[key]===undefined&&local[key]===undefined){result[key]=base[key];
}}}else{for(var key in base){if(local[key]===undefined){result[key]=base[key];
}}}}if(entry.include){for(var key in incl){if(local[key]===undefined){result[key]=incl[key];
}}}for(var key in local){result[key]=local[key];
}}else{result=entry.style(states);
}if(cache){cache[unique]=result||null;
}return result||null;
}},
destruct:function(){this._disposeFields("__cache",
"__stateMap");
}});




/* ID: qx.theme.manager.Meta */
qx.Class.define("qx.theme.manager.Meta",
{type:"singleton",
extend:qx.core.Target,
properties:{theme:{check:"Theme",
nullable:true,
apply:"_applyTheme",
event:"changeTheme"},
autoSync:{check:"Boolean",
init:true,
apply:"_applyAutoSync"}},
members:{_applyTheme:function(value,
old){var color=null;
var border=null;
var font=null;
var widget=null;
var icon=null;
var appearance=null;
if(value){color=value.meta.color||null;
border=value.meta.border||null;
font=value.meta.font||null;
widget=value.meta.widget||null;
icon=value.meta.icon||null;
appearance=value.meta.appearance||null;
}
if(old){this.setAutoSync(false);
}var colorMgr=qx.theme.manager.Color.getInstance();
var borderMgr=qx.theme.manager.Border.getInstance();
var fontMgr=qx.theme.manager.Font.getInstance();
var iconMgr=qx.theme.manager.Icon.getInstance();
var widgetMgr=qx.theme.manager.Widget.getInstance();
var appearanceMgr=qx.theme.manager.Appearance.getInstance();
colorMgr.setColorTheme(color);
borderMgr.setBorderTheme(border);
fontMgr.setFontTheme(font);
widgetMgr.setWidgetTheme(widget);
iconMgr.setIconTheme(icon);
appearanceMgr.setAppearanceTheme(appearance);
if(old){this.setAutoSync(true);
}},
_applyAutoSync:function(value,
old){if(value){qx.theme.manager.Appearance.getInstance().syncAppearanceTheme();
qx.theme.manager.Icon.getInstance().syncIconTheme();
qx.theme.manager.Widget.getInstance().syncWidgetTheme();
qx.theme.manager.Font.getInstance().syncFontTheme();
qx.theme.manager.Border.getInstance().syncBorderTheme();
qx.theme.manager.Color.getInstance().syncColorTheme();
}},
initialize:function(){var setting=qx.core.Setting;
var theme,
obj;
theme=setting.get("qx.theme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The meta theme to use is not available: "+theme);
}this.setTheme(obj);
}theme=setting.get("qx.colorTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The color theme to use is not available: "+theme);
}qx.theme.manager.Color.getInstance().setColorTheme(obj);
}theme=setting.get("qx.borderTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The border theme to use is not available: "+theme);
}qx.theme.manager.Border.getInstance().setBorderTheme(obj);
}theme=setting.get("qx.fontTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The font theme to use is not available: "+theme);
}qx.theme.manager.Font.getInstance().setFontTheme(obj);
}theme=setting.get("qx.widgetTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The widget theme to use is not available: "+theme);
}qx.theme.manager.Widget.getInstance().setWidgetTheme(obj);
}theme=setting.get("qx.iconTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The icon theme to use is not available: "+theme);
}qx.theme.manager.Icon.getInstance().setIconTheme(obj);
}theme=setting.get("qx.appearanceTheme");
if(theme){obj=qx.Theme.getByName(theme);
if(!obj){throw new Error("The appearance theme to use is not available: "+theme);
}qx.theme.manager.Appearance.getInstance().setAppearanceTheme(obj);
}},
__queryThemes:function(key){var reg=qx.Theme.getAll();
var theme;
var list=[];
for(var name in reg){theme=reg[name];
if(theme[key]){list.push(theme);
}}return list;
},
getMetaThemes:function(){return this.__queryThemes("meta");
},
getColorThemes:function(){return this.__queryThemes("colors");
},
getBorderThemes:function(){return this.__queryThemes("borders");
},
getFontThemes:function(){return this.__queryThemes("fonts");
},
getWidgetThemes:function(){return this.__queryThemes("widgets");
},
getIconThemes:function(){return this.__queryThemes("icons");
},
getAppearanceThemes:function(){return this.__queryThemes("appearance");
}},
settings:{"qx.theme":"qx.theme.ClassicRoyale",
"qx.colorTheme":null,
"qx.borderTheme":null,
"qx.fontTheme":null,
"qx.widgetTheme":null,
"qx.appearanceTheme":null,
"qx.iconTheme":null}});




/* ID: qx.theme.manager.Color */
qx.Class.define("qx.theme.manager.Color",
{type:"singleton",
extend:qx.util.manager.Value,
properties:{colorTheme:{check:"Theme",
nullable:true,
apply:"_applyColorTheme",
event:"changeColorTheme"}},
members:{_applyColorTheme:function(value){var dest=this._dynamic={};
if(value){var source=value.colors;
var util=qx.util.ColorUtil;
var temp;
for(var key in source){temp=source[key];
if(typeof temp==="string"){if(!util.isCssString(temp)){throw new Error("Could not parse color: "+temp);
}}else if(temp instanceof Array){temp=util.rgbToRgbString(temp);
}else{throw new Error("Could not parse color: "+temp);
}dest[key]=temp;
}}
if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncColorTheme();
}},
syncColorTheme:function(){this._updateObjects();
}}});




/* ID: qx.util.ColorUtil */
qx.Class.define("qx.util.ColorUtil",
{statics:{REGEXP:{hex3:/^#([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
hex6:/^#([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
rgb:/^rgb\(\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*,\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*,\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*\)$/},
SYSTEM:{activeborder:true,
activecaption:true,
appworkspace:true,
background:true,
buttonface:true,
buttonhighlight:true,
buttonshadow:true,
buttontext:true,
captiontext:true,
graytext:true,
highlight:true,
highlighttext:true,
inactiveborder:true,
inactivecaption:true,
inactivecaptiontext:true,
infobackground:true,
infotext:true,
menu:true,
menutext:true,
scrollbar:true,
threeddarkshadow:true,
threedface:true,
threedhighlight:true,
threedlightshadow:true,
threedshadow:true,
window:true,
windowframe:true,
windowtext:true},
NAMED:{black:[0,
0,
0],
silver:[192,
192,
192],
gray:[128,
128,
128],
white:[255,
255,
255],
maroon:[128,
0,
0],
red:[255,
0,
0],
purple:[128,
0,
128],
fuchsia:[255,
0,
255],
green:[0,
128,
0],
lime:[0,
255,
0],
olive:[128,
128,
0],
yellow:[255,
255,
0],
navy:[0,
0,
128],
blue:[0,
0,
255],
teal:[0,
128,
128],
aqua:[0,
255,
255],
transparent:[-1,
-1,
-1],
grey:[128,
128,
128],
magenta:[255,
0,
255],
orange:[255,
165,
0],
brown:[165,
42,
42]},
isNamedColor:function(value){return this.NAMED[value]!==undefined;
},
isSystemColor:function(value){return this.SYSTEM[value]!==undefined;
},
isThemedColor:function(value){return qx.theme.manager.Color.getInstance().isDynamic(value);
},
stringToRgb:function(str){if(this.isThemedColor(str)){var str=qx.theme.manager.Color.getInstance().resolveDynamic(str);
}
if(this.isNamedColor(str)){return this.NAMED[str];
}else if(this.isSystemColor(str)){throw new Error("Could not convert system colors to RGB: "+str);
}else if(this.isRgbString(str)){return this.__rgbStringToRgb();
}else if(this.isHex3String(str)){return this.__hex3StringToRgb();
}else if(this.isHex6String(str)){return this.__hex6StringToRgb();
}throw new Error("Could not parse color: "+str);
},
cssStringToRgb:function(str){if(this.isNamedColor(str)){return this.NAMED[str];
}else if(this.isSystemColor(str)){throw new Error("Could not convert system colors to RGB: "+str);
}else if(this.isRgbString(str)){return this.__rgbStringToRgb();
}else if(this.isHex3String(str)){return this.__hex3StringToRgb();
}else if(this.isHex6String(str)){return this.__hex6StringToRgb();
}throw new Error("Could not parse color: "+str);
},
stringToRgbString:function(str){return this.rgbToRgbString(this.stringToRgb(str));
},
rgbToRgbString:function(rgb){return "rgb("+rgb[0]+","+rgb[1]+","+rgb[2]+")";
},
isValid:function(str){return this.isThemedColor(str)||this.isCssString(str);
},
isCssString:function(str){return this.isSystemColor(str)||this.isNamedColor(str)||this.isHex3String(str)||this.isHex6String(str)||this.isRgbString(str);
},
isHex3String:function(str){return this.REGEXP.hex3.test(str);
},
isHex6String:function(str){return this.REGEXP.hex6.test(str);
},
isRgbString:function(str){return this.REGEXP.rgb.test(str);
},
__rgbStringToRgb:function(){var red=parseInt(RegExp.$1);
var green=parseInt(RegExp.$2);
var blue=parseInt(RegExp.$3);
return [red,
green,
blue];
},
__hex3StringToRgb:function(){var red=parseInt(RegExp.$1,
16)*17;
var green=parseInt(RegExp.$2,
16)*17;
var blue=parseInt(RegExp.$3,
16)*17;
return [red,
green,
blue];
},
__hex6StringToRgb:function(){var red=(parseInt(RegExp.$1,
16)*16)+parseInt(RegExp.$2,
16);
var green=(parseInt(RegExp.$3,
16)*16)+parseInt(RegExp.$4,
16);
var blue=(parseInt(RegExp.$5,
16)*16)+parseInt(RegExp.$6,
16);
return [red,
green,
blue];
},
hex3StringToRgb:function(value){if(this.isHex3String(value)){return this.__hex3StringToRgb(value);
}throw new Error("Invalid hex3 value: "+value);
},
hex6StringToRgb:function(value){if(this.isHex6String(value)){return this.__hex6StringToRgb(value);
}throw new Error("Invalid hex6 value: "+value);
},
hexStringToRgb:function(value){if(this.isHex3String(value)){return this.__hex3StringToRgb(value);
}
if(this.isHex6String(value)){return this.__hex6StringToRgb(value);
}throw new Error("Invalid hex value: "+value);
},
rgbToHsb:function(rgb){var hue,
saturation,
brightness;
var red=rgb[0];
var green=rgb[1];
var blue=rgb[2];
var cmax=(red>green)?red:green;
if(blue>cmax){cmax=blue;
}var cmin=(red<green)?red:green;
if(blue<cmin){cmin=blue;
}brightness=cmax/255.0;
if(cmax!=0){saturation=(cmax-cmin)/cmax;
}else{saturation=0;
}
if(saturation==0){hue=0;
}else{var redc=(cmax-red)/(cmax-cmin);
var greenc=(cmax-green)/(cmax-cmin);
var bluec=(cmax-blue)/(cmax-cmin);
if(red==cmax){hue=bluec-greenc;
}else if(green==cmax){hue=2.0+redc-bluec;
}else{hue=4.0+greenc-redc;
}hue=hue/6.0;
if(hue<0){hue=hue+1.0;
}}return [Math.round(hue*360),
Math.round(saturation*100),
Math.round(brightness*100)];
},
hsbToRgb:function(hsb){var i,
f,
p,
q,
t;
var hue=hsb[0]/360;
var saturation=hsb[1]/100;
var brightness=hsb[2]/100;
if(hue>=1.0){hue%=1.0;
}
if(saturation>1.0){saturation=1.0;
}
if(brightness>1.0){brightness=1.0;
}var tov=Math.floor(255*brightness);
var rgb={};
if(saturation==0.0){rgb.red=rgb.green=rgb.blue=tov;
}else{hue*=6.0;
i=Math.floor(hue);
f=hue-i;
p=Math.floor(tov*(1.0-saturation));
q=Math.floor(tov*(1.0-(saturation*f)));
t=Math.floor(tov*(1.0-(saturation*(1.0-f))));
switch(i){case 0:rgb.red=tov;
rgb.green=t;
rgb.blue=p;
break;
case 1:rgb.red=q;
rgb.green=tov;
rgb.blue=p;
break;
case 2:rgb.red=p;
rgb.green=tov;
rgb.blue=t;
break;
case 3:rgb.red=p;
rgb.green=q;
rgb.blue=tov;
break;
case 4:rgb.red=t;
rgb.green=p;
rgb.blue=tov;
break;
case 5:rgb.red=tov;
rgb.green=p;
rgb.blue=q;
break;
}}return rgb;
},
randomColor:function(){var r=Math.round(Math.random()*255);
var g=Math.round(Math.random()*255);
var b=Math.round(Math.random()*255);
return this.rgbToRgbString([r,
g,
b]);
}}});




/* ID: qx.theme.manager.Border */
qx.Class.define("qx.theme.manager.Border",
{type:"singleton",
extend:qx.util.manager.Value,
properties:{borderTheme:{check:"Theme",
nullable:true,
apply:"_applyBorderTheme",
event:"changeBorderTheme"}},
members:{resolveDynamic:function(value){return value instanceof qx.ui.core.Border?value:this._dynamic[value];
},
isDynamic:function(value){return value&&(value instanceof qx.ui.core.Border||this._dynamic[value]!==undefined);
},
syncBorderTheme:function(){this._updateObjects();
},
updateObjectsEdge:function(border,
edge){var reg=this._registry;
var dynamics=this._dynamic;
var entry;
for(var key in reg){entry=reg[key];
if(entry.value===border||dynamics[entry.value]===border){entry.callback.call(entry.object,
border,
edge);
}}},
_applyBorderTheme:function(value){var dest=this._dynamic;
for(var key in dest){if(dest[key].themed){dest[key].dispose();
delete dest[key];
}}
if(value){var source=value.borders;
var border=qx.ui.core.Border;
for(var key in source){dest[key]=(new border).set(source[key]);
dest[key].themed=true;
}}
if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncBorderTheme();
}}}});




/* ID: qx.ui.core.Border */
qx.Class.define("qx.ui.core.Border",
{extend:qx.core.Object,
construct:function(width,
style,
color){this.base(arguments);
if(width!==undefined){this.setWidth(width);
}
if(style!==undefined){this.setStyle(style);
}
if(color!==undefined){this.setColor(color);
}},
statics:{fromString:function(str){var border=new qx.ui.core.Border;
var parts=str.split(/\s+/);
var part,
temp;
for(var i=0,
l=parts.length;i<l;i++){part=parts[i];
switch(part){case "groove":case "ridge":case "inset":case "outset":case "solid":case "dotted":case "dashed":case "double":case "none":border.setStyle(part);
break;
default:temp=parseInt(part);
if(temp===part||qx.lang.String.contains(part,
"px")){border.setWidth(temp);
}else{border.setColor(part);
}break;
}}return border;
},
fromConfig:function(config){var border=new qx.ui.core.Border;
border.set(config);
return border;
},
resetTop:qx.core.Variant.select("qx.client",
{"gecko":function(widget){var style=widget._style;
if(style){style.borderTopWidth=style.borderTopStyle=style.borderTopColor=style.MozBorderTopColors="";
}},
"default":function(widget){var style=widget._style;
if(style){style.borderTopWidth=style.borderTopStyle=style.borderTopColor="";
}style=widget._innerStyle;
if(style){style.borderTopWidth=style.borderTopStyle=style.borderTopColor="";
}}}),
resetRight:qx.core.Variant.select("qx.client",
{"gecko":function(widget){var style=widget._style;
if(style){style.borderRightWidth=style.borderRightStyle=style.borderRightColor=style.MozBorderRightColors="";
}},
"default":function(widget){var style=widget._style;
if(style){style.borderRightWidth=style.borderRightStyle=style.borderRightColor="";
}style=widget._innerStyle;
if(style){style.borderRightWidth=style.borderRightStyle=style.borderRightColor="";
}}}),
resetBottom:qx.core.Variant.select("qx.client",
{"gecko":function(widget){var style=widget._style;
if(style){style.borderBottomWidth=style.borderBottomStyle=style.borderBottomColor=style.MozBorderBottomColors="";
}},
"default":function(widget){var style=widget._style;
if(style){style.borderBottomWidth=style.borderBottomStyle=style.borderBottomColor="";
}style=widget._innerStyle;
if(style){style.borderBottomWidth=style.borderBottomStyle=style.borderBottomColor="";
}}}),
resetLeft:qx.core.Variant.select("qx.client",
{"gecko":function(widget){var style=widget._style;
if(style){style.borderLeftWidth=style.borderLeftStyle=style.borderLeftColor=style.MozBorderLeftColors="";
}},
"default":function(widget){var style=widget._style;
if(style){style.borderLeftWidth=style.borderLeftStyle=style.borderLeftColor="";
}style=widget._innerStyle;
if(style){style.borderLeftWidth=style.borderLeftStyle=style.borderLeftColor="";
}}})},
properties:{widthTop:{check:"Number",
init:0,
apply:"_applyWidthTop"},
widthRight:{check:"Number",
init:0,
apply:"_applyWidthRight"},
widthBottom:{check:"Number",
init:0,
apply:"_applyWidthBottom"},
widthLeft:{check:"Number",
init:0,
apply:"_applyWidthLeft"},
styleTop:{nullable:true,
check:["solid",
"dotted",
"dashed",
"double",
"outset",
"inset",
"ridge",
"groove"],
init:"solid",
apply:"_applyStyleTop"},
styleRight:{nullable:true,
check:["solid",
"dotted",
"dashed",
"double",
"outset",
"inset",
"ridge",
"groove"],
init:"solid",
apply:"_applyStyleRight"},
styleBottom:{nullable:true,
check:["solid",
"dotted",
"dashed",
"double",
"outset",
"inset",
"ridge",
"groove"],
init:"solid",
apply:"_applyStyleBottom"},
styleLeft:{nullable:true,
check:["solid",
"dotted",
"dashed",
"double",
"outset",
"inset",
"ridge",
"groove"],
init:"solid",
apply:"_applyStyleLeft"},
colorTop:{nullable:true,
check:"Color",
apply:"_applyColorTop"},
colorRight:{nullable:true,
check:"Color",
apply:"_applyColorRight"},
colorBottom:{nullable:true,
check:"Color",
apply:"_applyColorBottom"},
colorLeft:{nullable:true,
check:"Color",
apply:"_applyColorLeft"},
colorInnerTop:{nullable:true,
check:"Color",
apply:"_applyColorInnerTop"},
colorInnerRight:{nullable:true,
check:"Color",
apply:"_applyColorInnerRight"},
colorInnerBottom:{nullable:true,
check:"Color",
apply:"_applyColorInnerBottom"},
colorInnerLeft:{nullable:true,
check:"Color",
apply:"_applyColorInnerLeft"},
left:{group:["widthLeft",
"styleLeft",
"colorLeft"]},
right:{group:["widthRight",
"styleRight",
"colorRight"]},
top:{group:["widthTop",
"styleTop",
"colorTop"]},
bottom:{group:["widthBottom",
"styleBottom",
"colorBottom"]},
width:{group:["widthTop",
"widthRight",
"widthBottom",
"widthLeft"],
mode:"shorthand"},
style:{group:["styleTop",
"styleRight",
"styleBottom",
"styleLeft"],
mode:"shorthand"},
color:{group:["colorTop",
"colorRight",
"colorBottom",
"colorLeft"],
mode:"shorthand"},
innerColor:{group:["colorInnerTop",
"colorInnerRight",
"colorInnerBottom",
"colorInnerLeft"],
mode:"shorthand"}},
members:{_applyWidthTop:function(value,
old){this.__widthTop=value==null?"0px":value+"px";
this.__computeComplexTop();
this.__informManager("top");
},
_applyWidthRight:function(value,
old){this.__widthRight=value==null?"0px":value+"px";
this.__computeComplexRight();
this.__informManager("right");
},
_applyWidthBottom:function(value,
old){this.__widthBottom=value==null?"0px":value+"px";
this.__computeComplexBottom();
this.__informManager("bottom");
},
_applyWidthLeft:function(value,
old){this.__widthLeft=value==null?"0px":value+"px";
this.__computeComplexLeft();
this.__informManager("left");
},
_applyColorTop:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorTop,
this,
value);
},
_applyColorRight:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorRight,
this,
value);
},
_applyColorBottom:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorBottom,
this,
value);
},
_applyColorLeft:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorLeft,
this,
value);
},
_applyColorInnerTop:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorInnerTop,
this,
value);
},
_applyColorInnerRight:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorInnerRight,
this,
value);
},
_applyColorInnerBottom:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorInnerBottom,
this,
value);
},
_applyColorInnerLeft:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._changeColorInnerLeft,
this,
value);
},
_applyStyleTop:function(){this.__informManager("top");
},
_applyStyleRight:function(){this.__informManager("right");
},
_applyStyleBottom:function(){this.__informManager("bottom");
},
_applyStyleLeft:function(){this.__informManager("left");
},
_changeColorTop:function(value){this.__colorTop=value;
this.__computeComplexTop();
this.__informManager("top");
},
_changeColorInnerTop:function(value){this.__colorInnerTop=value;
this.__computeComplexTop();
this.__informManager("top");
},
_changeColorRight:function(value){this.__colorRight=value;
this.__computeComplexRight();
this.__informManager("right");
},
_changeColorInnerRight:function(value){this.__colorInnerRight=value;
this.__computeComplexRight();
this.__informManager("right");
},
_changeColorBottom:function(value){this.__colorBottom=value;
this.__computeComplexBottom();
this.__informManager("bottom");
},
_changeColorInnerBottom:function(value){this.__colorInnerBottom=value;
this.__computeComplexBottom();
this.__informManager("bottom");
},
_changeColorLeft:function(value){this.__colorLeft=value;
this.__computeComplexLeft();
this.__informManager("left");
},
_changeColorInnerLeft:function(value){this.__colorInnerLeft=value;
this.__computeComplexLeft();
this.__informManager("left");
},
__computeComplexTop:function(){this.__complexTop=this.getWidthTop()===2&&this.__colorInnerTop!=null&&this.__colorTop!=this.__colorInnerTop;
},
__computeComplexRight:function(){this.__complexRight=this.getWidthRight()===2&&this.__colorInnerRight!=null&&this.__colorRight!=this.__colorInnerRight;
},
__computeComplexBottom:function(){this.__complexBottom=this.getWidthBottom()===2&&this.__colorInnerBottom!=null&&this.__colorBottom!=this.__colorInnerBottom;
},
__computeComplexLeft:function(){this.__complexLeft=this.getWidthLeft()===2&&this.__colorInnerLeft!=null&&this.__colorLeft!=this.__colorInnerLeft;
},
__informManager:function(edge){qx.theme.manager.Border.getInstance().updateObjectsEdge(this,
edge);
},
renderTop:qx.core.Variant.select("qx.client",
{"gecko":function(obj){var style=obj._style;
style.borderTopWidth=this.__widthTop||"0px";
style.borderTopColor=this.__colorTop||"";
if(this.__complexTop){style.borderTopStyle="solid";
style.MozBorderTopColors=this.__colorTop+" "+this.__colorInnerTop;
}else{style.borderTopStyle=this.getStyleTop()||"none";
style.MozBorderTopColors="";
}},
"default":function(obj){var outer=obj._style;
var inner=obj._innerStyle;
if(this.__complexTop){if(!inner){obj.prepareEnhancedBorder();
inner=obj._innerStyle;
}outer.borderTopWidth=inner.borderTopWidth="1px";
outer.borderTopStyle=inner.borderTopStyle="solid";
outer.borderTopColor=this.__colorTop;
inner.borderTopColor=this.__colorInnerTop;
}else{outer.borderTopWidth=this.__widthTop||"0px";
outer.borderTopStyle=this.getStyleTop()||"none";
outer.borderTopColor=this.__colorTop||"";
if(inner){inner.borderTopWidth=inner.borderTopStyle=inner.borderTopColor="";
}}}}),
renderRight:qx.core.Variant.select("qx.client",
{"gecko":function(obj){var style=obj._style;
style.borderRightWidth=this.__widthRight||"0px";
style.borderRightColor=this.__colorRight||"";
if(this.__complexRight){style.borderRightStyle="solid";
style.MozBorderRightColors=this.__colorRight+" "+this.__colorInnerRight;
}else{style.borderRightStyle=this.getStyleRight()||"none";
style.MozBorderRightColors="";
}},
"default":function(obj){var outer=obj._style;
var inner=obj._innerStyle;
if(this.__complexRight){if(!inner){obj.prepareEnhancedBorder();
inner=obj._innerStyle;
}outer.borderRightWidth=inner.borderRightWidth="1px";
outer.borderRightStyle=inner.borderRightStyle="solid";
outer.borderRightColor=this.__colorRight;
inner.borderRightColor=this.__colorInnerRight;
}else{outer.borderRightWidth=this.__widthRight||"0px";
outer.borderRightStyle=this.getStyleRight()||"none";
outer.borderRightColor=this.__colorRight||"";
if(inner){inner.borderRightWidth=inner.borderRightStyle=inner.borderRightColor="";
}}}}),
renderBottom:qx.core.Variant.select("qx.client",
{"gecko":function(obj){var style=obj._style;
style.borderBottomWidth=this.__widthBottom||"0px";
style.borderBottomColor=this.__colorBottom||"";
if(this.__complexBottom){style.borderBottomStyle="solid";
style.MozBorderBottomColors=this.__colorBottom+" "+this.__colorInnerBottom;
}else{style.borderBottomStyle=this.getStyleBottom()||"none";
style.MozBorderBottomColors="";
}},
"default":function(obj){var outer=obj._style;
var inner=obj._innerStyle;
if(this.__complexBottom){if(!inner){obj.prepareEnhancedBorder();
inner=obj._innerStyle;
}outer.borderBottomWidth=inner.borderBottomWidth="1px";
outer.borderBottomStyle=inner.borderBottomStyle="solid";
outer.borderBottomColor=this.__colorBottom;
inner.borderBottomColor=this.__colorInnerBottom;
}else{outer.borderBottomWidth=this.__widthBottom||"0px";
outer.borderBottomStyle=this.getStyleBottom()||"none";
outer.borderBottomColor=this.__colorBottom||"";
if(inner){inner.borderBottomWidth=inner.borderBottomStyle=inner.borderBottomColor="";
}}}}),
renderLeft:qx.core.Variant.select("qx.client",
{"gecko":function(obj){var style=obj._style;
style.borderLeftWidth=this.__widthLeft||"0px";
style.borderLeftColor=this.__colorLeft||"";
if(this.__complexLeft){style.borderLeftStyle="solid";
style.MozBorderLeftColors=this.__colorLeft+" "+this.__colorInnerLeft;
}else{style.borderLeftStyle=this.getStyleLeft()||"none";
style.MozBorderLeftColors="";
}},
"default":function(obj){var outer=obj._style;
var inner=obj._innerStyle;
if(this.__complexLeft){if(!inner){obj.prepareEnhancedBorder();
inner=obj._innerStyle;
}outer.borderLeftWidth=inner.borderLeftWidth="1px";
outer.borderLeftStyle=inner.borderLeftStyle="solid";
outer.borderLeftColor=this.__colorLeft;
inner.borderLeftColor=this.__colorInnerLeft;
}else{outer.borderLeftWidth=this.__widthLeft||"0px";
outer.borderLeftStyle=this.getStyleLeft()||"none";
outer.borderLeftColor=this.__colorLeft||"";
if(inner){inner.borderLeftWidth=inner.borderLeftStyle=inner.borderLeftColor="";
}}}})}});




/* ID: qx.theme.manager.Font */
qx.Class.define("qx.theme.manager.Font",
{type:"singleton",
extend:qx.util.manager.Value,
properties:{fontTheme:{check:"Theme",
nullable:true,
apply:"_applyFontTheme",
event:"changeFontTheme"}},
members:{resolveDynamic:function(value){return value instanceof qx.ui.core.Font?value:this._dynamic[value];
},
isDynamic:function(value){return value&&(value instanceof qx.ui.core.Font||this._dynamic[value]!==undefined);
},
syncFontTheme:function(){this._updateObjects();
},
_applyFontTheme:function(value){var dest=this._dynamic;
for(var key in dest){if(dest[key].themed){dest[key].dispose();
delete dest[key];
}}
if(value){var source=value.fonts;
var font=qx.ui.core.Font;
for(var key in source){dest[key]=(new font).set(source[key]);
dest[key].themed=true;
}}
if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncFontTheme();
}}}});




/* ID: qx.ui.core.Font */
qx.Class.define("qx.ui.core.Font",
{extend:qx.core.Object,
construct:function(size,
family){this.base(arguments);
if(size!==undefined){this.setSize(size);
}
if(family!==undefined){this.setFamily(family);
}},
statics:{fromString:function(str){var font=new qx.ui.core.Font;
var parts=str.split(/\s+/);
var name=[];
var part;
for(var i=0;i<parts.length;i++){switch(part=parts[i]){case "bold":font.setBold(true);
break;
case "italic":font.setItalic(true);
break;
case "underline":font.setDecoration("underline");
break;
default:var temp=parseInt(part);
if(temp==part||qx.lang.String.contains(part,
"px")){font.setSize(temp);
}else{name.push(part);
}break;
}}
if(name.length>0){font.setFamily(name);
}return font;
},
fromConfig:function(config){var font=new qx.ui.core.Font;
font.set(config);
return font;
},
reset:function(widget){widget.removeStyleProperty("fontFamily");
widget.removeStyleProperty("fontSize");
widget.removeStyleProperty("fontWeight");
widget.removeStyleProperty("fontStyle");
widget.removeStyleProperty("textDecoration");
},
resetElement:function(element){var style=element.style;
style.fontFamily="";
style.fontSize="";
style.fontWeight="";
style.fontStyle="";
style.textDecoration="";
},
resetStyle:function(style){style.fontFamily="";
style.fontSize="";
style.fontWeight="";
style.fontStyle="";
style.textDecoration="";
}},
properties:{size:{check:"Integer",
nullable:true,
apply:"_applySize"},
family:{check:"Array",
nullable:true,
apply:"_applyFamily"},
bold:{check:"Boolean",
nullable:true,
apply:"_applyBold"},
italic:{check:"Boolean",
nullable:true,
apply:"_applyItalic"},
decoration:{check:["underline",
"line-through",
"overline"],
nullable:true,
apply:"_applyDecoration"}},
members:{__size:null,
__family:null,
__bold:null,
__italic:null,
__decoration:null,
_applySize:function(value,
old){this.__size=value===null?null:value+"px";
},
_applyFamily:function(value,
old){var family="";
for(var i=0,
l=value.length;i<l;i++){if(value[i].indexOf(" ")>0){family+='"'+value[i]+'"';
}else{family+=value[i];
}
if(i!=l-1){family+=",";
}}this.__family=family;
},
_applyBold:function(value,
old){this.__bold=value===null?null:value?"bold":"normal";
},
_applyItalic:function(value,
old){this.__italic=value===null?null:value?"italic":"normal";
},
_applyDecoration:function(value,
old){this.__decoration=value===null?null:value;
},
render:function(widget){widget.setStyleProperty("fontFamily",
this.__family);
widget.setStyleProperty("fontSize",
this.__size);
widget.setStyleProperty("fontWeight",
this.__bold);
widget.setStyleProperty("fontStyle",
this.__italic);
widget.setStyleProperty("textDecoration",
this.__decoration);
},
renderStyle:function(style){style.fontFamily=this.__family||"";
style.fontSize=this.__size||"";
style.fontWeight=this.__bold||"";
style.fontStyle=this.__italic||"";
style.textDecoration=this.__decoration||"";
},
renderElement:function(element){var style=element.style;
style.fontFamily=this.__family||"";
style.fontSize=this.__size||"";
style.fontWeight=this.__bold||"";
style.fontStyle=this.__italic||"";
style.textDecoration=this.__decoration||"";
},
generateStyle:function(){return (this.__family?"font-family:"+this.__family.replace(/\"/g,
"'")+";":"")+(this.__size?"font-size:"+this.__size+";":"")+(this.__weight?"font-weight:"+this.__weight+";":"")+(this.__style?"font-style:"+this.__style+";":"")+(this.__decoration?"text-decoration:"+this.__decoration+";":"");
}}});




/* ID: qx.theme.manager.Icon */
qx.Class.define("qx.theme.manager.Icon",
{type:"singleton",
extend:qx.core.Target,
properties:{iconTheme:{check:"Theme",
nullable:true,
apply:"_applyIconTheme",
event:"changeIconTheme"}},
members:{_applyIconTheme:function(value,
old){if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncIconTheme();
}},
syncIconTheme:function(){var value=this.getIconTheme();
var alias=qx.io.Alias.getInstance();
value?alias.add("icon",
value.icons.uri):alias.remove("icon");
}}});




/* ID: qx.io.Alias */
qx.Class.define("qx.io.Alias",
{type:"singleton",
extend:qx.util.manager.Value,
construct:function(){this.base(arguments);
this._aliases={};
this.add("static",
qx.core.Setting.get("qx.resourceUri")+"/static");
},
members:{_preprocess:function(value){var dynamics=this._dynamic;
if(dynamics[value]===false){return value;
}else if(dynamics[value]===undefined){if(value.charAt(0)==="/"||value.charAt(0)==="."||value.indexOf("http://")===0||value.indexOf("https://")==="0"||value.indexOf("file://")===0){dynamics[value]=false;
return value;
}var alias=value.substring(0,
value.indexOf("/"));
var resolved=this._aliases[alias];
if(resolved!==undefined){dynamics[value]=resolved+value.substring(alias.length);
}}return value;
},
add:function(alias,
base){this._aliases[alias]=base;
var dynamics=this._dynamic;
var reg=this._registry;
var entry;
var paths={};
for(var path in dynamics){if(path.substring(0,
path.indexOf("/"))===alias){dynamics[path]=base+path.substring(alias.length);
paths[path]=true;
}}for(var key in reg){entry=reg[key];
if(paths[entry.value]){entry.callback.call(entry.object,
dynamics[entry.value]);
}}},
remove:function(alias){delete this._aliases[alias];
},
resolve:function(path){if(path!==null){path=this._preprocess(path);
}return this._dynamic[path]||path;
}},
settings:{"qx.resourceUri":"./resource"},
destruct:function(){this._disposeFields("_aliases");
}});




/* ID: qx.theme.manager.Widget */
qx.Class.define("qx.theme.manager.Widget",
{type:"singleton",
extend:qx.core.Target,
properties:{widgetTheme:{check:"Theme",
nullable:true,
apply:"_applyWidgetTheme",
event:"changeWidgetTheme"}},
members:{_applyWidgetTheme:function(value,
old){if(qx.theme.manager.Meta.getInstance().getAutoSync()){this.syncWidgetTheme();
}},
syncWidgetTheme:function(){var value=this.getWidgetTheme();
var alias=qx.io.Alias.getInstance();
value?alias.add("widget",
value.widgets.uri):alias.remove("widget");
}}});




/* ID: qx.event.handler.FocusHandler */
qx.Class.define("qx.event.handler.FocusHandler",
{extend:qx.core.Target,
construct:function(widget){this.base(arguments);
if(widget!=null){this._attachedWidget=widget;
}},
statics:{mouseFocus:false},
members:{getAttachedWidget:function(){return this._attachedWidget;
},
_onkeyevent:function(container,
ev){if(ev.getKeyIdentifier()!="Tab"){return;
}ev.stopPropagation();
ev.preventDefault();
qx.event.handler.FocusHandler.mouseFocus=false;
var vCurrent=this.getAttachedWidget().getFocusedChild();
if(!ev.isShiftPressed()){var vNext=vCurrent?this.getWidgetAfter(container,
vCurrent):this.getFirstWidget(container);
}else{var vNext=vCurrent?this.getWidgetBefore(container,
vCurrent):this.getLastWidget(container);
}if(vNext){vNext.setFocused(true);
vNext._ontabfocus();
}},
compareTabOrder:function(c1,
c2){if(c1==c2){return 0;
}var t1=c1.getTabIndex();
var t2=c2.getTabIndex();
if(t1!=t2){return t1-t2;
}var y1=qx.html.Location.getPageBoxTop(c1.getElement());
var y2=qx.html.Location.getPageBoxTop(c2.getElement());
if(y1!=y2){return y1-y2;
}var x1=qx.html.Location.getPageBoxLeft(c1.getElement());
var x2=qx.html.Location.getPageBoxLeft(c2.getElement());
if(x1!=x2){return x1-x2;
}var z1=c1.getZIndex();
var z2=c2.getZIndex();
if(z1!=z2){return z1-z2;
}return 0;
},
getFirstWidget:function(parentContainer){return this._getFirst(parentContainer,
null);
},
getLastWidget:function(parentContainer){return this._getLast(parentContainer,
null);
},
getWidgetAfter:function(parentContainer,
widget){if(parentContainer==widget){return this.getFirstWidget(parentContainer);
}
if(widget.getAnonymous()){widget=widget.getParent();
}
if(widget==null){return [];
}var vAll=[];
this._getAllAfter(parentContainer,
widget,
vAll);
vAll.sort(this.compareTabOrder);
return vAll.length>0?vAll[0]:this.getFirstWidget(parentContainer);
},
getWidgetBefore:function(parentContainer,
widget){if(parentContainer==widget){return this.getLastWidget(parentContainer);
}
if(widget.getAnonymous()){widget=widget.getParent();
}
if(widget==null){return [];
}var vAll=[];
this._getAllBefore(parentContainer,
widget,
vAll);
vAll.sort(this.compareTabOrder);
var len=vAll.length;
return len>0?vAll[len-1]:this.getLastWidget(parentContainer);
},
_getAllAfter:function(parent,
widget,
arr){var children=parent.getChildren();
var child;
var len=children.length;
for(var i=0;i<len;i++){child=children[i];
if(!(child instanceof qx.ui.core.Parent)&&!(child instanceof qx.ui.basic.Terminator)){continue;
}
if(child.isFocusable()&&child.getTabIndex()>0&&this.compareTabOrder(widget,
child)<0){arr.push(children[i]);
}
if(!child.isFocusRoot()&&child instanceof qx.ui.core.Parent){this._getAllAfter(child,
widget,
arr);
}}},
_getAllBefore:function(parent,
widget,
arr){var children=parent.getChildren();
var child;
var len=children.length;
for(var i=0;i<len;i++){child=children[i];
if(!(child instanceof qx.ui.core.Parent)&&!(child instanceof qx.ui.basic.Terminator)){continue;
}
if(child.isFocusable()&&child.getTabIndex()>0&&this.compareTabOrder(widget,
child)>0){arr.push(child);
}
if(!child.isFocusRoot()&&child instanceof qx.ui.core.Parent){this._getAllBefore(child,
widget,
arr);
}}},
_getFirst:function(parent,
firstWidget){var children=parent.getChildren();
var child;
var len=children.length;
for(var i=0;i<len;i++){child=children[i];
if(!(child instanceof qx.ui.core.Parent)&&!(child instanceof qx.ui.basic.Terminator)){continue;
}
if(child.isFocusable()&&child.getTabIndex()>0){if(firstWidget==null||this.compareTabOrder(child,
firstWidget)<0){firstWidget=child;
}}
if(!child.isFocusRoot()&&child instanceof qx.ui.core.Parent){firstWidget=this._getFirst(child,
firstWidget);
}}return firstWidget;
},
_getLast:function(parent,
lastWidget){var children=parent.getChildren();
var child;
var len=children.length;
for(var i=0;i<len;i++){child=children[i];
if(!(child instanceof qx.ui.core.Parent)&&!(child instanceof qx.ui.basic.Terminator)){continue;
}
if(child.isFocusable()&&child.getTabIndex()>0){if(lastWidget==null||this.compareTabOrder(child,
lastWidget)>0){lastWidget=child;
}}
if(!child.isFocusRoot()&&child instanceof qx.ui.core.Parent){lastWidget=this._getLast(child,
lastWidget);
}}return lastWidget;
}},
destruct:function(){this._disposeFields("_attachedWidget");
}});




/* ID: qx.html.Location */
qx.Class.define("qx.html.Location",
{statics:{getPageOuterLeft:function(el){return qx.html.Location.getPageBoxLeft(el)-qx.html.Style.getMarginLeft(el);
},
getPageOuterTop:function(el){return qx.html.Location.getPageBoxTop(el)-qx.html.Style.getMarginTop(el);
},
getPageOuterRight:function(el){return qx.html.Location.getPageBoxRight(el)+qx.html.Style.getMarginRight(el);
},
getPageOuterBottom:function(el){return qx.html.Location.getPageBoxBottom(el)+qx.html.Style.getMarginBottom(el);
},
getClientOuterLeft:function(el){return qx.html.Location.getClientBoxLeft(el)-qx.html.Style.getMarginLeft(el);
},
getClientOuterTop:function(el){return qx.html.Location.getClientBoxTop(el)-qx.html.Style.getMarginTop(el);
},
getClientOuterRight:function(el){return qx.html.Location.getClientBoxRight(el)+qx.html.Style.getMarginRight(el);
},
getClientOuterBottom:function(el){return qx.html.Location.getClientBoxBottom(el)+qx.html.Style.getMarginBottom(el);
},
getClientBoxLeft:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.getBoundingClientRect().left;
},
"gecko":function(el){return qx.html.Location.getClientAreaLeft(el)-qx.html.Style.getBorderLeft(el);
},
"default":function(el){var sum=el.offsetLeft;
while(el.tagName.toLowerCase()!="body"){el=el.offsetParent;
sum+=el.offsetLeft-el.scrollLeft;
}return sum;
}}),
getClientBoxTop:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.getBoundingClientRect().top;
},
"gecko":function(el){return qx.html.Location.getClientAreaTop(el)-qx.html.Style.getBorderTop(el);
},
"default":function(el){var sum=el.offsetTop;
while(el.tagName.toLowerCase()!="body"){el=el.offsetParent;
sum+=el.offsetTop-el.scrollTop;
}return sum;
}}),
getClientBoxRight:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.getBoundingClientRect().right;
},
"default":function(el){return qx.html.Location.getClientBoxLeft(el)+qx.html.Dimension.getBoxWidth(el);
}}),
getClientBoxBottom:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return el.getBoundingClientRect().bottom;
},
"default":function(el){return qx.html.Location.getClientBoxTop(el)+qx.html.Dimension.getBoxHeight(el);
}}),
getPageBoxLeft:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return qx.html.Location.getClientBoxLeft(el)+qx.html.Scroll.getLeftSum(el);
},
"gecko":function(el){return qx.html.Location.getPageAreaLeft(el)-qx.html.Style.getBorderLeft(el);
},
"default":function(el){var sum=el.offsetLeft;
while(el.tagName.toLowerCase()!="body"){el=el.offsetParent;
sum+=el.offsetLeft;
}return sum;
}}),
getPageBoxTop:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return qx.html.Location.getClientBoxTop(el)+qx.html.Scroll.getTopSum(el);
},
"gecko":function(el){return qx.html.Location.getPageAreaTop(el)-qx.html.Style.getBorderTop(el);
},
"default":function(el){var sum=el.offsetTop;
while(el.tagName.toLowerCase()!="body"){el=el.offsetParent;
sum+=el.offsetTop;
}return sum;
}}),
getPageBoxRight:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return qx.html.Location.getClientBoxRight(el)+qx.html.Scroll.getLeftSum(el);
},
"default":function(el){return qx.html.Location.getPageBoxLeft(el)+qx.html.Dimension.getBoxWidth(el);
}}),
getPageBoxBottom:qx.core.Variant.select("qx.client",
{"mshtml":function(el){return qx.html.Location.getClientBoxBottom(el)+qx.html.Scroll.getTopSum(el);
},
"default":function(el){return qx.html.Location.getPageBoxTop(el)+qx.html.Dimension.getBoxHeight(el);
}}),
getClientAreaLeft:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getPageAreaLeft(el)-qx.html.Scroll.getLeftSum(el);
},
"default":function(el){return qx.html.Location.getClientBoxLeft(el)+qx.html.Style.getBorderLeft(el);
}}),
getClientAreaTop:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getPageAreaTop(el)-qx.html.Scroll.getTopSum(el);
},
"default":function(el){return qx.html.Location.getClientBoxTop(el)+qx.html.Style.getBorderTop(el);
}}),
getClientAreaRight:function(el){return qx.html.Location.getClientAreaLeft(el)+qx.html.Dimension.getAreaWidth(el);
},
getClientAreaBottom:function(el){return qx.html.Location.getClientAreaTop(el)+qx.html.Dimension.getAreaHeight(el);
},
getPageAreaLeft:qx.core.Variant.select("qx.client",
{"gecko":function(el){return el.ownerDocument.getBoxObjectFor(el).x;
},
"default":function(el){return qx.html.Location.getPageBoxLeft(el)+qx.html.Style.getBorderLeft(el);
}}),
getPageAreaTop:qx.core.Variant.select("qx.client",
{"gecko":function(el){return el.ownerDocument.getBoxObjectFor(el).y;
},
"default":function(el){return qx.html.Location.getPageBoxTop(el)+qx.html.Style.getBorderTop(el);
}}),
getPageAreaRight:function(el){return qx.html.Location.getPageAreaLeft(el)+qx.html.Dimension.getAreaWidth(el);
},
getPageAreaBottom:function(el){return qx.html.Location.getPageAreaTop(el)+qx.html.Dimension.getAreaHeight(el);
},
getClientInnerLeft:function(el){return qx.html.Location.getClientAreaLeft(el)+qx.html.Style.getPaddingLeft(el);
},
getClientInnerTop:function(el){return qx.html.Location.getClientAreaTop(el)+qx.html.Style.getPaddingTop(el);
},
getClientInnerRight:function(el){return qx.html.Location.getClientInnerLeft(el)+qx.html.Dimension.getInnerWidth(el);
},
getClientInnerBottom:function(el){return qx.html.Location.getClientInnerTop(el)+qx.html.Dimension.getInnerHeight(el);
},
getPageInnerLeft:function(el){return qx.html.Location.getPageAreaLeft(el)+qx.html.Style.getPaddingLeft(el);
},
getPageInnerTop:function(el){return qx.html.Location.getPageAreaTop(el)+qx.html.Style.getPaddingTop(el);
},
getPageInnerRight:function(el){return qx.html.Location.getPageInnerLeft(el)+qx.html.Dimension.getInnerWidth(el);
},
getPageInnerBottom:function(el){return qx.html.Location.getPageInnerTop(el)+qx.html.Dimension.getInnerHeight(el);
},
getScreenBoxLeft:qx.core.Variant.select("qx.client",
{"gecko":function(el){var sum=0;
var p=el.parentNode;
while(p.nodeType==1){sum+=p.scrollLeft;
p=p.parentNode;
}return el.ownerDocument.getBoxObjectFor(el).screenX-sum;
},
"default":function(el){return qx.html.Location.getScreenDocumentLeft(el)+qx.html.Location.getPageBoxLeft(el);
}}),
getScreenBoxTop:qx.core.Variant.select("qx.client",
{"gecko":function(el){var sum=0;
var p=el.parentNode;
while(p.nodeType==1){sum+=p.scrollTop;
p=p.parentNode;
}return el.ownerDocument.getBoxObjectFor(el).screenY-sum;
},
"default":function(el){return qx.html.Location.getScreenDocumentTop(el)+qx.html.Location.getPageBoxTop(el);
}}),
getScreenBoxRight:function(el){return qx.html.Location.getScreenBoxLeft(el)+qx.html.Dimension.getBoxWidth(el);
},
getScreenBoxBottom:function(el){return qx.html.Location.getScreenBoxTop(el)+qx.html.Dimension.getBoxHeight(el);
},
getScreenOuterLeft:function(el){return qx.html.Location.getScreenBoxLeft(el)-qx.html.Style.getMarginLeft(el);
},
getScreenOuterTop:function(el){return qx.html.Location.getScreenBoxTop(el)-qx.html.Style.getMarginTop(el);
},
getScreenOuterRight:function(el){return qx.html.Location.getScreenBoxRight(el)+qx.html.Style.getMarginRight(el);
},
getScreenOuterBottom:function(el){return qx.html.Location.getScreenBoxBottom(el)+qx.html.Style.getMarginBottom(el);
},
getScreenAreaLeft:function(el){return qx.html.Location.getScreenBoxLeft(el)+qx.html.Dimension.getInsetLeft(el);
},
getScreenAreaTop:function(el){return qx.html.Location.getScreenBoxTop(el)+qx.html.Dimension.getInsetTop(el);
},
getScreenAreaRight:function(el){return qx.html.Location.getScreenBoxRight(el)-qx.html.Dimension.getInsetRight(el);
},
getScreenAreaBottom:function(el){return qx.html.Location.getScreenBoxBottom(el)-qx.html.Dimension.getInsetBottom(el);
},
getScreenInnerLeft:function(el){return qx.html.Location.getScreenAreaLeft(el)+qx.html.Style.getPaddingLeft(el);
},
getScreenInnerTop:function(el){return qx.html.Location.getScreenAreaTop(el)+qx.html.Style.getPaddingTop(el);
},
getScreenInnerRight:function(el){return qx.html.Location.getScreenAreaRight(el)-qx.html.Style.getPaddingRight(el);
},
getScreenInnerBottom:function(el){return qx.html.Location.getScreenAreaBottom(el)-qx.html.Style.getPaddingBottom(el);
},
getScreenDocumentLeft:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getScreenOuterLeft(el.ownerDocument.body);
},
"default":function(el){return el.document.parentWindow.screenLeft;
}}),
getScreenDocumentTop:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getScreenOuterTop(el.ownerDocument.body);
},
"default":function(el){return el.document.parentWindow.screenTop;
}}),
getScreenDocumentRight:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getScreenOuterRight(el.ownerDocument.body);
},
"default":function(el){}}),
getScreenDocumentBottom:qx.core.Variant.select("qx.client",
{"gecko":function(el){return qx.html.Location.getScreenOuterBottom(el.ownerDocument.body);
},
"default":function(el){}})}});




/* ID: qx.html.Scroll */
qx.Class.define("qx.html.Scroll",
{statics:{getLeftSum:function(el){var sum=0;
var p=el.parentNode;
while(p.nodeType==1){sum+=p.scrollLeft;
p=p.parentNode;
}return sum;
},
getTopSum:function(el){var sum=0;
var p=el.parentNode;
while(p.nodeType==1){sum+=p.scrollTop;
p=p.parentNode;
}return sum;
}}});




/* ID: qx.io.image.Manager */
qx.Class.define("qx.io.image.Manager",
{type:"singleton",
extend:qx.core.Target,
construct:function(){this.base(arguments);
this.__visible={};
this.__all={};
},
members:{add:function(source){var data=this.__all;
if(data[source]===undefined){data[source]=1;
}else{data[source]++;
}},
remove:function(source){var data=this.__all;
if(data[source]!==undefined){data[source]--;
}
if(data[source]<=0){delete data[source];
}},
show:function(source){var data=this.__visible;
if(data[source]===undefined){data[source]=1;
}else{data[source]++;
}},
hide:function(source){var data=this.__visible;
if(data[source]!==undefined){data[source]--;
}
if(data[source]<=0){delete data[source];
}},
getVisibleImages:function(){var visible=this.__visible;
var list={};
for(var source in visible){if(visible[source]>0){list[source]=true;
}}return list;
},
getHiddenImages:function(){var visible=this.__visible;
var all=this.__all;
var list={};
for(var source in all){if(visible[source]===undefined){list[source]=true;
}}return list;
}},
destruct:function(){this._disposeFields("__all",
"__visible");
}});




/* ID: qx.html.Offset */
qx.Class.define("qx.html.Offset",
{statics:{getLeft:qx.core.Variant.select("qx.client",
{"gecko":function(el){var val=el.offsetLeft;
var pa=el.parentNode;
var pose=qx.html.Style.getStyleProperty(el,
"position");
var posp=qx.html.Style.getStyleProperty(pa,
"position");
if(pose!="absolute"&&pose!="fixed"){val-=qx.html.Style.getBorderLeft(pa);
}if(posp!="absolute"&&posp!="fixed"){while(pa){pa=pa.parentNode;
if(!pa||typeof pa.tagName!=="string"){break;
}var posi=qx.html.Style.getStyleProperty(pa,
"position");
if(posi=="absolute"||posi=="fixed"){val-=qx.html.Style.getBorderLeft(pa)+qx.html.Style.getPaddingLeft(pa);
break;
}}}return val;
},
"default":function(el){return el.offsetLeft;
}}),
getTop:qx.core.Variant.select("qx.client",
{"gecko":function(el){var val=el.offsetTop;
var pa=el.parentNode;
var pose=qx.html.Style.getStyleProperty(el,
"position");
var posp=qx.html.Style.getStyleProperty(pa,
"position");
if(pose!="absolute"&&pose!="fixed"){val-=qx.html.Style.getBorderTop(pa);
}if(posp!="absolute"&&posp!="fixed"){while(pa){pa=pa.parentNode;
if(!pa||typeof pa.tagName!=="string"){break;
}var posi=qx.html.Style.getStyleProperty(pa,
"position");
if(posi=="absolute"||posi=="fixed"){val-=qx.html.Style.getBorderTop(pa)+qx.html.Style.getPaddingTop(pa);
break;
}}}return val;
},
"default":function(el){return el.offsetTop;
}})}});




/* ID: qx.html.ScrollIntoView */
qx.Class.define("qx.html.ScrollIntoView",
{statics:{scrollX:function(vElement,
vAlignLeft){var vParentWidth,
vParentScrollLeft,
vWidth,
vHasScroll;
var vParent=vElement.parentNode;
var vOffset=vElement.offsetLeft;
var vWidth=vElement.offsetWidth;
while(vParent){switch(qx.html.Style.getStyleProperty(vParent,
"overflow")){case "scroll":case "auto":case "-moz-scrollbars-horizontal":vHasScroll=true;
break;
default:switch(qx.html.Style.getStyleProperty(vParent,
"overflowX")){case "scroll":case "auto":vHasScroll=true;
break;
default:vHasScroll=false;
}}
if(vHasScroll){vParentWidth=vParent.clientWidth;
vParentScrollLeft=vParent.scrollLeft;
if(vAlignLeft){vParent.scrollLeft=vOffset;
}else if(vAlignLeft==false){vParent.scrollLeft=vOffset+vWidth-vParentWidth;
}else if(vWidth>vParentWidth||vOffset<vParentScrollLeft){vParent.scrollLeft=vOffset;
}else if((vOffset+vWidth)>(vParentScrollLeft+vParentWidth)){vParent.scrollLeft=vOffset+vWidth-vParentWidth;
}vOffset=vParent.offsetLeft;
vWidth=vParent.offsetWidth;
}else{vOffset+=vParent.offsetLeft;
}
if(vParent.tagName.toLowerCase()=="body"){break;
}vParent=vParent.offsetParent;
}return true;
},
scrollY:function(vElement,
vAlignTop){var vParentHeight,
vParentScrollTop,
vHeight,
vHasScroll;
var vParent=vElement.parentNode;
var vOffset=vElement.offsetTop;
var vHeight=vElement.offsetHeight;
while(vParent){switch(qx.html.Style.getStyleProperty(vParent,
"overflow")){case "scroll":case "auto":case "-moz-scrollbars-vertical":vHasScroll=true;
break;
default:switch(qx.html.Style.getStyleProperty(vParent,
"overflowY")){case "scroll":case "auto":vHasScroll=true;
break;
default:vHasScroll=false;
}}
if(vHasScroll){vParentHeight=vParent.clientHeight;
vParentScrollTop=vParent.scrollTop;
if(vAlignTop){vParent.scrollTop=vOffset;
}else if(vAlignTop==false){vParent.scrollTop=vOffset+vHeight-vParentHeight;
}else if(vHeight>vParentHeight||vOffset<vParentScrollTop){vParent.scrollTop=vOffset;
}else if((vOffset+vHeight)>(vParentScrollTop+vParentHeight)){vParent.scrollTop=vOffset+vHeight-vParentHeight;
}vOffset=vParent.offsetTop;
vHeight=vParent.offsetHeight;
}else{vOffset+=vParent.offsetTop;
}
if(vParent.tagName.toLowerCase()=="body"){break;
}vParent=vParent.offsetParent;
}return true;
}}});




/* ID: qx.client.Timer */
qx.Class.define("qx.client.Timer",
{extend:qx.core.Target,
construct:function(interval){this.base(arguments);
this.setEnabled(false);
if(interval!=null){this.setInterval(interval);
}this.__oninterval=qx.lang.Function.bind(this._oninterval,
this);
},
events:{"interval":"qx.event.type.Event"},
statics:{once:function(func,
obj,
timeout){var timer=new qx.client.Timer(timeout);
timer.addEventListener("interval",
function(e){timer.dispose();
func.call(obj,
e);
obj=null;
},
obj);
timer.start();
}},
properties:{enabled:{init:true,
check:"Boolean",
apply:"_applyEnabled"},
interval:{check:"Integer",
init:1000,
apply:"_applyInterval"}},
members:{__intervalHandler:null,
_applyInterval:function(value,
old){if(this.getEnabled()){this.restart();
}},
_applyEnabled:function(value,
old){if(old){window.clearInterval(this.__intervalHandler);
this.__intervalHandler=null;
}else if(value){this.__intervalHandler=window.setInterval(this.__oninterval,
this.getInterval());
}},
start:function(){this.setEnabled(true);
},
startWith:function(interval){this.setInterval(interval);
this.start();
},
stop:function(){this.setEnabled(false);
},
restart:function(){this.stop();
this.start();
},
restartWith:function(interval){this.stop();
this.startWith(interval);
},
_oninterval:function(){if(this.getEnabled()){this.createDispatchEvent("interval");
}}},
destruct:function(){if(this.__intervalHandler){window.clearInterval(this.__intervalHandler);
}this._disposeFields("__intervalHandler",
"__oninterval");
}});




/* ID: qx.io.image.PreloaderSystem */
qx.Class.define("qx.io.image.PreloaderSystem",
{extend:qx.core.Target,
construct:function(vPreloadList,
vCallBack,
vCallBackScope){this.base(arguments);
if(vPreloadList instanceof Array){this._list=qx.lang.Object.fromArray(vPreloadList);
}else{this._list=vPreloadList;
}this._timer=new qx.client.Timer(qx.core.Setting.get("qx.preloaderTimeout"));
this._timer.addEventListener("interval",
this.__oninterval,
this);
if(vCallBack){this.addEventListener("completed",
vCallBack,
vCallBackScope||null);
}},
events:{"completed":"qx.event.type.Event"},
members:{_stopped:false,
start:function(){if(qx.lang.Object.isEmpty(this._list)){this.createDispatchEvent("completed");
return;
}
for(var vSource in this._list){var vPreloader=qx.io.image.PreloaderManager.getInstance().create(qx.io.Alias.getInstance().resolve(vSource));
if(vPreloader.isErroneous()||vPreloader.isLoaded()){delete this._list[vSource];
}else{vPreloader._origSource=vSource;
vPreloader.addEventListener("load",
this.__onload,
this);
vPreloader.addEventListener("error",
this.__onerror,
this);
}}this._check();
},
__onload:function(e){if(this.getDisposed()){return;
}delete this._list[e.getTarget()._origSource];
this._check();
},
__onerror:function(e){if(this.getDisposed()){return;
}delete this._list[e.getTarget()._origSource];
this._check();
},
__oninterval:function(e){this.warn("Cannot preload: "+qx.lang.Object.getKeysAsString(this._list));
this._stopped=true;
this._timer.stop();
this.createDispatchEvent("completed");
},
_check:function(){if(this._stopped){return;
}if(qx.lang.Object.isEmpty(this._list)){this._timer.stop();
this.createDispatchEvent("completed");
}else{this._timer.restart();
}}},
settings:{"qx.preloaderTimeout":3000},
destruct:function(){this._disposeObjects("_timer");
this._disposeFields("_list");
}});




/* ID: qx.io.image.PreloaderManager */
qx.Class.define("qx.io.image.PreloaderManager",
{type:"singleton",
extend:qx.core.Object,
construct:function(){this.base(arguments);
this._objects={};
},
members:{add:function(vObject){this._objects[vObject.getUri()]=vObject;
},
remove:function(vObject){delete this._objects[vObject.getUri()];
},
has:function(vSource){return this._objects[vSource]!=null;
},
get:function(vSource){return this._objects[vSource];
},
create:function(vSource){if(this._objects[vSource]){return this._objects[vSource];
}return new qx.io.image.Preloader(vSource);
}},
destruct:function(){this._disposeFields("_objects");
}});




/* ID: qx.io.image.Preloader */
qx.Class.define("qx.io.image.Preloader",
{extend:qx.core.Target,
events:{"load":"qx.event.type.Event",
"error":"qx.event.type.Event"},
construct:function(imageUrl){if(qx.io.image.PreloaderManager.getInstance().has(imageUrl)){this.debug("Reuse qx.io.image.Preloader in old-style!");
this.debug("Please use qx.io.image.PreloaderManager.getInstance().create(source) instead!");
return qx.io.image.PreloaderManager.getInstance().get(imageUrl);
}this.base(arguments);
this._element=new Image;
this._element.onload=qx.lang.Function.bind(this.__onload,
this);
this._element.onerror=qx.lang.Function.bind(this.__onerror,
this);
this._source=imageUrl;
this._element.src=imageUrl;
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this._isPng=/\.png$/i.test(this._element.nameProp);
}qx.io.image.PreloaderManager.getInstance().add(this);
},
members:{_source:null,
_isLoaded:false,
_isErroneous:false,
getUri:function(){return this._source;
},
getSource:function(){return this._source;
},
isLoaded:function(){return this._isLoaded;
},
isErroneous:function(){return this._isErroneous;
},
_isPng:false,
getIsPng:function(){return this._isPng;
},
getWidth:qx.core.Variant.select("qx.client",
{"gecko":function(){return this._element.naturalWidth;
},
"default":function(){return this._element.width;
}}),
getHeight:qx.core.Variant.select("qx.client",
{"gecko":function(){return this._element.naturalHeight;
},
"default":function(){return this._element.height;
}}),
__onload:function(){if(this._isLoaded||this._isErroneous){return;
}this._isLoaded=true;
this._isErroneous=false;
if(this.hasEventListeners("load")){this.dispatchEvent(new qx.event.type.Event("load"),
true);
}},
__onerror:function(){if(this._isLoaded||this._isErroneous){return;
}this.debug("Could not load: "+this._source);
this._isLoaded=false;
this._isErroneous=true;
if(this.hasEventListeners("error")){this.dispatchEvent(new qx.event.type.Event("error"),
true);
}}},
destruct:function(){if(this._element){this._element.onload=this._element.onerror=null;
}this._disposeFields("_element",
"_isLoaded",
"_isErroneous",
"_isPng");
}});




/* ID: qx.client.Command */
qx.Class.define("qx.client.Command",
{extend:qx.core.Target,
events:{"execute":"qx.event.type.DataEvent"},
construct:function(shortcut,
keyCode){this.base(arguments);
this.__modifier={};
this.__key=null;
if(shortcut!=null){this.setShortcut(shortcut);
}
if(keyCode!=null){this.warn("The use of keyCode in command is deprecated. Use keyIdentifier instead.");
this.setKeyCode(keyCode);
}{if(this.__modifier.Alt&&this.__key&&this.__key.length==1){if((this.__key>="A"&&this.__key<="Z")||(this.__key>="0"&&this.__key<="9")){this.warn("A shortcut containing Alt and a letter or number will not work under OS X!");
}}};
qx.event.handler.EventHandler.getInstance().addCommand(this);
},
properties:{enabled:{init:true,
check:"Boolean",
event:"changeEnabled"},
shortcut:{check:"String",
apply:"_applyShortcut",
nullable:true},
keyCode:{check:"Number",
nullable:true},
keyIdentifier:{check:"String",
nullable:true}},
members:{execute:function(vTarget){if(this.hasEventListeners("execute")){var event=new qx.event.type.DataEvent("execute",
vTarget);
this.dispatchEvent(event,
true);
}return false;
},
_applyShortcut:function(value,
old){if(value){this.__modifier={};
this.__key=null;
var a=value.split(/[-+\s]+/);
var al=a.length;
for(var i=0;i<al;i++){var identifier=this.__oldKeyNameToKeyIdentifier(a[i]);
switch(identifier){case "Control":case "Shift":case "Meta":case "Alt":this.__modifier[identifier]=true;
break;
case "Unidentified":var msg="Not a valid key name for a command: "+a[i];
this.error(msg);
throw msg;
default:if(this.__key){var msg="You can only specify one non modifier key!";
this.error(msg);
throw msg;
}this.__key=identifier;
}}}return true;
},
matchesKeyEvent:function(e){var key=this.__key||this.getKeyIdentifier();
if(!key&&!this.getKeyCode()){return ;
}if((this.__modifier.Shift&&!e.isShiftPressed())||
(this.__modifier.Control&&!e.isCtrlPressed())||
(this.__modifier.Alt&&!e.isAltPressed())){return false;
}
if(key){if(key==e.getKeyIdentifier()){return true;
}}else{if(this.getKeyCode()==e.getKeyCode()){return true;
}}return false;
},
__oldKeyNameToKeyIdentifierMap:{esc:"Escape",
ctrl:"Control",
print:"PrintScreen",
del:"Delete",
pageup:"PageUp",
pagedown:"PageDown",
numlock:"NumLock",
numpad_0:"0",
numpad_1:"1",
numpad_2:"2",
numpad_3:"3",
numpad_4:"4",
numpad_5:"5",
numpad_6:"6",
numpad_7:"7",
numpad_8:"8",
numpad_9:"9",
numpad_divide:"/",
numpad_multiply:"*",
numpad_minus:"-",
numpad_plus:"+"},
__oldKeyNameToKeyIdentifier:function(keyName){var keyHandler=qx.event.handler.KeyEventHandler.getInstance();
var keyIdentifier="Unidentified";
if(keyHandler.isValidKeyIdentifier(keyName)){return keyName;
}
if(keyName.length==1&&keyName>="a"&&keyName<="z"){return keyName.toUpperCase();
}keyName=keyName.toLowerCase();
if(!qx.event.type.KeyEvent.keys[keyName]){return "Unidentified";
}var keyIdentifier=this.__oldKeyNameToKeyIdentifierMap[keyName];
if(keyIdentifier){return keyIdentifier;
}else{return qx.lang.String.toFirstUp(keyName);
}},
toString:function(){var keyCode=this.getKeyCode();
var key=this.__key||this.getKeyIdentifier();
var str=[];
for(var modifier in this.__modifier){str.push(qx.locale.Key.getKeyName("short",
modifier));
}
if(key){str.push(qx.locale.Key.getKeyName("short",
key));
}
if(keyCode!=null){var vTemp=qx.event.type.KeyEvent.codes[keyCode];
str.push(vTemp?qx.lang.String.toFirstUp(vTemp):String(keyCode));
}return str.join("-");
}},
destruct:function(){var mgr=qx.event.handler.EventHandler.getInstance();
if(mgr){mgr.removeCommand(this);
}this._disposeFields("__modifier",
"__key");
}});




/* ID: qx.locale.Key */
qx.Class.define("qx.locale.Key",
{statics:{getKeyName:function(size,
keyIdentifier,
locale){if(size!="short"&&size!="full"){throw new Error('format must be one of: "short", "full"');
}var key="key_"+size+"_"+keyIdentifier;
var localizedKey=new qx.locale.LocalizedString(key,
[],
locale);
if(localizedKey==key){return qx.locale.Key._keyNames[key]||keyIdentifier;
}else{return localizedKey.toString();
}}},
defer:function(statics,
members,
properties){var keyNames={};
var Manager=qx.locale.Manager;
keyNames[Manager.marktr("key_short_Backspace")]="Backspace";
keyNames[Manager.marktr("key_short_Tab")]="Tab";
keyNames[Manager.marktr("key_short_Space")]="Space";
keyNames[Manager.marktr("key_short_Enter")]="Enter";
keyNames[Manager.marktr("key_short_Shift")]="Shift";
keyNames[Manager.marktr("key_short_Control")]="Ctrl";
keyNames[Manager.marktr("key_short_Alt")]="Alt";
keyNames[Manager.marktr("key_short_CapsLock")]="Caps";
keyNames[Manager.marktr("key_short_Meta")]="Meta";
keyNames[Manager.marktr("key_short_Escape")]="Esc";
keyNames[Manager.marktr("key_short_Left")]="Left";
keyNames[Manager.marktr("key_short_Up")]="Up";
keyNames[Manager.marktr("key_short_Right")]="Right";
keyNames[Manager.marktr("key_short_Down")]="Down";
keyNames[Manager.marktr("key_short_PageUp")]="PgUp";
keyNames[Manager.marktr("key_short_PageDown")]="PgDn";
keyNames[Manager.marktr("key_short_End")]="End";
keyNames[Manager.marktr("key_short_Home")]="Home";
keyNames[Manager.marktr("key_short_Insert")]="Ins";
keyNames[Manager.marktr("key_short_Delete")]="Del";
keyNames[Manager.marktr("key_short_NumLock")]="Num";
keyNames[Manager.marktr("key_short_PrintScreen")]="Print";
keyNames[Manager.marktr("key_short_Scroll")]="Scroll";
keyNames[Manager.marktr("key_short_Pause")]="Pause";
keyNames[Manager.marktr("key_short_Win")]="Win";
keyNames[Manager.marktr("key_short_Apps")]="Apps";
keyNames[Manager.marktr("key_full_Backspace")]="Backspace";
keyNames[Manager.marktr("key_full_Tab")]="Tabulator";
keyNames[Manager.marktr("key_full_Space")]="Space";
keyNames[Manager.marktr("key_full_Enter")]="Enter";
keyNames[Manager.marktr("key_full_Shift")]="Shift";
keyNames[Manager.marktr("key_full_Control")]="Control";
keyNames[Manager.marktr("key_full_Alt")]="Alt";
keyNames[Manager.marktr("key_full_CapsLock")]="CapsLock";
keyNames[Manager.marktr("key_full_Meta")]="Meta";
keyNames[Manager.marktr("key_full_Escape")]="Escape";
keyNames[Manager.marktr("key_full_Left")]="Left";
keyNames[Manager.marktr("key_full_Up")]="Up";
keyNames[Manager.marktr("key_full_Right")]="Right";
keyNames[Manager.marktr("key_full_Down")]="Down";
keyNames[Manager.marktr("key_full_PageUp")]="PageUp";
keyNames[Manager.marktr("key_full_PageDown")]="PageDown";
keyNames[Manager.marktr("key_full_End")]="End";
keyNames[Manager.marktr("key_full_Home")]="Home";
keyNames[Manager.marktr("key_full_Insert")]="Insert";
keyNames[Manager.marktr("key_full_Delete")]="Delete";
keyNames[Manager.marktr("key_full_NumLock")]="NumLock";
keyNames[Manager.marktr("key_full_PrintScreen")]="PrintScreen";
keyNames[Manager.marktr("key_full_Scroll")]="Scroll";
keyNames[Manager.marktr("key_full_Pause")]="Pause";
keyNames[Manager.marktr("key_full_Win")]="Win";
keyNames[Manager.marktr("key_full_Apps")]="Apps";
statics._keyNames=keyNames;
}});




/* ID: qx.ui.window.Manager */
qx.Class.define("qx.ui.window.Manager",
{extend:qx.util.manager.Object,
properties:{activeWindow:{check:"Object",
nullable:true,
apply:"_applyActiveWindow"}},
members:{_applyActiveWindow:function(value,
old){qx.ui.popup.PopupManager.getInstance().update();
if(old){old.setActive(false);
}
if(value){value.setActive(true);
}
if(old&&old.getModal()){old.getTopLevelWidget().release(old);
}
if(value&&value.getModal()){value.getTopLevelWidget().block(value);
}},
update:function(){var vWindow,
vHashCode;
var vAll=this.getAll();
for(var vHashCode in vAll){vWindow=vAll[vHashCode];
if(!vWindow.getAutoHide()){continue;
}vWindow.hide();
}},
compareWindows:function(w1,
w2){switch(w1.getWindowManager().getActiveWindow()){case w1:return 1;
case w2:return -1;
}return w1.getZIndex()-w2.getZIndex();
},
add:function(vWindow){this.base(arguments,
vWindow);
this.setActiveWindow(vWindow);
},
remove:function(vWindow){this.base(arguments,
vWindow);
if(this.getActiveWindow()==vWindow){var a=[];
for(var i in this._objects){a.push(this._objects[i]);
}var l=a.length;
if(l==0){this.setActiveWindow(null);
}else if(l==1){this.setActiveWindow(a[0]);
}else if(l>1){a.sort(this.compareWindows);
this.setActiveWindow(a[l-1]);
}}}}});




/* ID: qx.ui.popup.PopupManager */
qx.Class.define("qx.ui.popup.PopupManager",
{type:"singleton",
extend:qx.util.manager.Object,
construct:function(){this.base(arguments);
},
members:{update:function(vTarget){if(!(vTarget instanceof qx.ui.core.Widget)){vTarget=null;
}var vPopup,
vHashCode;
var vAll=this.getAll();
for(vHashCode in vAll){vPopup=vAll[vHashCode];
if(!vPopup.getAutoHide()||vTarget==vPopup||vPopup.contains(vTarget)){continue;
}
if(qx.Class.isDefined("qx.ui.popup.ToolTip")&&vTarget instanceof qx.ui.popup.ToolTip&&!(vPopup instanceof qx.ui.popup.ToolTip)){continue;
}vPopup.hide();
}}}});




/* ID: qx.ui.popup.Popup */
qx.Class.define("qx.ui.popup.Popup",
{extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
this.setZIndex(this._minZIndex);
if(this._isFocusRoot){this.activateFocusRoot();
}this.initHeight();
this.initWidth();
},
properties:{appearance:{refine:true,
init:"popup"},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
display:{refine:true,
init:false},
autoHide:{check:"Boolean",
init:true},
centered:{check:"Boolean",
init:false},
restrictToPageOnOpen:{check:"Boolean",
init:true},
restrictToPageLeft:{check:"Integer",
init:0},
restrictToPageRight:{check:"Integer",
init:0},
restrictToPageTop:{check:"Integer",
init:0},
restrictToPageBottom:{check:"Integer",
init:0}},
members:{_isFocusRoot:true,
_showTimeStamp:(new Date(0)).valueOf(),
_hideTimeStamp:(new Date(0)).valueOf(),
_beforeAppear:function(){this.base(arguments);
if(this.getRestrictToPageOnOpen()){this._wantedLeft=this.getLeft();
if(this._wantedLeft!=null){this.setLeft(10000);
if(this.getElement()!=null){this.getElement().style.left=10000;
}}}qx.ui.popup.PopupManager.getInstance().add(this);
qx.ui.popup.PopupManager.getInstance().update(this);
this._showTimeStamp=(new Date).valueOf();
this.bringToFront();
},
_beforeDisappear:function(){this.base(arguments);
qx.ui.popup.PopupManager.getInstance().remove(this);
this._hideTimeStamp=(new Date).valueOf();
},
_afterAppear:function(){this.base(arguments);
if(this.getRestrictToPageOnOpen()){var doc=qx.ui.core.ClientDocument.getInstance();
var docWidth=doc.getClientWidth();
var docHeight=doc.getClientHeight();
var restrictToPageLeft=this.getRestrictToPageLeft();
var restrictToPageRight=this.getRestrictToPageRight();
var restrictToPageTop=this.getRestrictToPageTop();
var restrictToPageBottom=this.getRestrictToPageBottom();
var left=(this._wantedLeft==null)?this.getLeft():this._wantedLeft;
var top=this.getTop();
var width=this.getBoxWidth();
var height=this.getBoxHeight();
var oldLeft=this.getLeft();
var oldTop=top;
if(left+width>docWidth-restrictToPageRight){left=docWidth-restrictToPageRight-width;
}
if(top+height>docHeight-restrictToPageBottom){top=docHeight-restrictToPageBottom-height;
}
if(left<restrictToPageLeft){left=restrictToPageLeft;
}
if(top<restrictToPageTop){top=restrictToPageTop;
}
if(left!=oldLeft||top!=oldTop){var self=this;
window.setTimeout(function(){self.setLeft(left);
self.setTop(top);
},
0);
}}},
_makeActive:function(){this.getFocusRoot().setActiveChild(this);
},
_makeInactive:function(){var vRoot=this.getFocusRoot();
var vCurrent=vRoot.getActiveChild();
if(vCurrent==this){vRoot.setActiveChild(vRoot);
}},
_minZIndex:1e6,
bringToFront:function(){this.setZIndex(this._minZIndex+1000000);
this._sendTo();
},
sendToBack:function(){this.setZIndex(this._minZIndex+1);
this._sendTo();
},
_sendTo:function(){var vPopups=qx.lang.Object.getValues(qx.ui.popup.PopupManager.getInstance().getAll());
if(qx.Class.isDefined("qx.ui.menu.Manager")){var vMenus=qx.lang.Object.getValues(qx.ui.menu.Manager.getInstance().getAll());
var vAll=vPopups.concat(vMenus).sort(qx.util.Compare.byZIndex);
}else{var vAll=vPopups.sort(qx.util.Compare.byZIndex);
}var vLength=vAll.length;
var vIndex=this._minZIndex;
for(var i=0;i<vLength;i++){vAll[i].setZIndex(vIndex++);
}},
getShowTimeStamp:function(){return this._showTimeStamp;
},
getHideTimeStamp:function(){return this._hideTimeStamp;
},
positionRelativeTo:function(el,
offsetX,
offsetY){if(el instanceof qx.ui.core.Widget){el=el.getElement();
}
if(el){var loc=qx.html.Location;
this.setLocation(loc.getClientAreaLeft(el)-(qx.core.Variant.isSet("qx.client",
"gecko")?qx.html.Style.getBorderLeft(el):0)+(offsetX||0),
loc.getClientAreaTop(el)-(qx.core.Variant.isSet("qx.client",
"gecko")?qx.html.Style.getBorderTop(el):0)+(offsetY||0));
}else{this.warn('Missing reference element');
}},
centerToBrowser:function(){var d=qx.ui.core.ClientDocument.getInstance();
var left=(d.getClientWidth()-this.getBoxWidth())/2;
var top=(d.getClientHeight()-this.getBoxHeight())/2;
this.setLeft(left<0?0:left);
this.setTop(top<0?0:top);
}},
destruct:function(){this._disposeFields("_showTimeStamp",
"_hideTimeStamp");
}});




/* ID: qx.util.Compare */
qx.Class.define("qx.util.Compare",
{statics:{byString:function(a,
b){return a==b?0:a>b?1:-1;
},
byStringCaseInsensitive:function(a,
b){return qx.util.Compare.byString(a.toLowerCase(),
b.toLowerCase());
},
byStringUmlautsShort:function(a,
b){return qx.util.Compare.byString(qx.util.Normalization.umlautsShort(a),
qx.util.Normalization.umlautsShort(b));
},
byStringUmlautsShortCaseInsensitive:function(a,
b){return qx.util.Compare.byString(qx.util.Normalization.umlautsShort(a).toLowerCase(),
qx.util.Normalization.umlautsShort(b).toLowerCase());
},
byStringUmlautsLong:function(a,
b){return qx.util.Compare.byString(qx.util.Normalization.umlautsLong(a),
qx.util.Normalization.umlautsLong(b));
},
byStringUmlautsLongCaseInsensitive:function(a,
b){return qx.util.Compare.byString(qx.util.Normalization.umlautsLong(a).toLowerCase(),
qx.util.Normalization.umlautsLong(b).toLowerCase());
},
byFloat:function(a,
b){return a-b;
},
byIntegerString:function(a,
b){return parseInt(a)-parseInt(b);
},
byFloatString:function(a,
b){return parseFloat(a)-parseFloat(b);
},
byIPv4:function(a,
b){var ipa=a.split(".",
4);
var ipb=b.split(".",
4);
for(var i=0;i<3;i++){a=parseInt(ipa[i]);
b=parseInt(ipb[i]);
if(a!=b){return a-b;
}}return parseInt(ipa[3])-parseInt(ipb[3]);
},
byZIndex:function(a,
b){return a.getZIndex()-b.getZIndex();
}},
defer:function(statics){statics.byInteger=statics.byNumber=statics.byFloat;
statics.byNumberString=statics.byFloatString;
}});




/* ID: qx.util.Normalization */
qx.Class.define("qx.util.Normalization",
{statics:{__umlautsRegExp:new RegExp("[\xE4\xF6\xFC\xDF\xC4\xD6\xDC]",
"g"),
__umlautsShortData:{"\xC4":"A",
"\xD6":"O",
"\xDC":"U",
"\xE4":"a",
"\xF6":"o",
"\xFC":"u",
"\xDF":"s"},
__umlautsShort:function(vChar){return qx.util.Normalization.__umlautsShortData[vChar];
},
umlautsShort:function(vString){return vString.replace(qx.util.Normalization.__umlautsRegExp,
qx.lang.Function.bind(this.__umlautsShort,
this));
},
__umlautsLongData:{"\xC4":"Ae",
"\xD6":"Oe",
"\xDC":"Ue",
"\xE4":"ae",
"\xF6":"oe",
"\xFC":"ue",
"\xDF":"ss"},
__umlautsLong:function(vChar){return qx.util.Normalization.__umlautsLongData[vChar];
},
umlautsLong:function(vString){return vString.replace(qx.util.Normalization.__umlautsRegExp,
qx.lang.Function.bind(this.__umlautsLong,
this));
}}});




/* ID: qx.ui.resizer.MResizable */
qx.Mixin.define("qx.ui.resizer.MResizable",
{construct:function(child){this._frame=new qx.ui.basic.Terminator;
this._frame.setAppearance("resizer-frame");
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("mouseup",
this._onmouseup);
this.addEventListener("mousemove",
this._onmousemove);
},
properties:{resizableWest:{check:"Boolean",
init:true,
apply:"_applyResizable"},
resizableNorth:{check:"Boolean",
init:true,
apply:"_applyResizable"},
resizableEast:{check:"Boolean",
init:true,
apply:"_applyResizable"},
resizableSouth:{check:"Boolean",
init:true,
apply:"_applyResizable"},
resizable:{group:["resizableNorth",
"resizableEast",
"resizableSouth",
"resizableWest"],
mode:"shorthand"},
resizeMethod:{init:"frame",
check:["opaque",
"lazyopaque",
"frame",
"translucent"],
event:"changeResizeMethod"}},
members:{isResizable:function(){return this.getResizableWest()||this.getResizableEast()||this.getResizableNorth()||this.getResizableSouth();
},
getResizable:function(){return this.isResizable();
},
_applyResizable:function(value,
old){},
_onmousedown:function(e){if(this._resizeNorth||this._resizeSouth||this._resizeWest||this._resizeEast){this.setCapture(true);
this.getTopLevelWidget().setGlobalCursor(this.getCursor());
var el=this.getElement();
var pa=this._getResizeParent();
var pl=pa.getElement();
var l=qx.html.Location.getPageAreaLeft(pl);
var t=qx.html.Location.getPageAreaTop(pl);
var r=qx.html.Location.getPageAreaRight(pl);
var b=qx.html.Location.getPageAreaBottom(pl);
switch(this.getResizeMethod()){case "translucent":this.setOpacity(0.5);
break;
case "frame":var f=this._frame;
if(f.getParent()!=pa){f.setParent(pa);
qx.ui.core.Widget.flushGlobalQueues();
}f._renderRuntimeLeft(qx.html.Location.getPageBoxLeft(el)-l);
f._renderRuntimeTop(qx.html.Location.getPageBoxTop(el)-t);
f._renderRuntimeWidth(qx.html.Dimension.getBoxWidth(el));
f._renderRuntimeHeight(qx.html.Dimension.getBoxHeight(el));
f.setZIndex(this.getZIndex()+1);
break;
}var s=this._resizeSession={};
var minRef=this._getMinSizeReference();
if(this._resizeWest){s.boxWidth=qx.html.Dimension.getBoxWidth(el);
s.boxRight=qx.html.Location.getPageBoxRight(el);
}
if(this._resizeWest||this._resizeEast){s.boxLeft=qx.html.Location.getPageBoxLeft(el);
s.parentAreaOffsetLeft=l;
s.parentAreaOffsetRight=r;
s.minWidth=minRef.getMinWidthValue();
s.maxWidth=minRef.getMaxWidthValue();
}
if(this._resizeNorth){s.boxHeight=qx.html.Dimension.getBoxHeight(el);
s.boxBottom=qx.html.Location.getPageBoxBottom(el);
}
if(this._resizeNorth||this._resizeSouth){s.boxTop=qx.html.Location.getPageBoxTop(el);
s.parentAreaOffsetTop=t;
s.parentAreaOffsetBottom=b;
s.minHeight=minRef.getMinHeightValue();
s.maxHeight=minRef.getMaxHeightValue();
}}else{delete this._resizeSession;
}e.stopPropagation();
},
_onmouseup:function(e){var s=this._resizeSession;
if(s){this.setCapture(false);
this.getTopLevelWidget().setGlobalCursor(null);
switch(this.getResizeMethod()){case "frame":var o=this._frame;
if(!(o&&o.getParent())){break;
}case "lazyopaque":if(s.lastLeft!=null){this.setLeft(s.lastLeft);
}
if(s.lastTop!=null){this.setTop(s.lastTop);
}
if(s.lastWidth!=null){this._changeWidth(s.lastWidth);
}
if(s.lastHeight!=null){this._changeHeight(s.lastHeight);
}
if(this.getResizeMethod()=="frame"){this._frame.setParent(null);
}break;
case "translucent":this.setOpacity(null);
break;
}delete this._resizeSession;
}e.stopPropagation();
},
_near:function(p,
e){return e>(p-5)&&e<(p+5);
},
_onmousemove:function(e){var s=this._resizeSession;
if(s){if(this._resizeWest){s.lastWidth=qx.lang.Number.limit(s.boxWidth+s.boxLeft-Math.max(e.getPageX(),
s.parentAreaOffsetLeft),
s.minWidth,
s.maxWidth);
s.lastLeft=s.boxRight-s.lastWidth-s.parentAreaOffsetLeft;
}else if(this._resizeEast){s.lastWidth=qx.lang.Number.limit(Math.min(e.getPageX(),
s.parentAreaOffsetRight)-s.boxLeft,
s.minWidth,
s.maxWidth);
}
if(this._resizeNorth){s.lastHeight=qx.lang.Number.limit(s.boxHeight+s.boxTop-Math.max(e.getPageY(),
s.parentAreaOffsetTop),
s.minHeight,
s.maxHeight);
s.lastTop=s.boxBottom-s.lastHeight-s.parentAreaOffsetTop;
}else if(this._resizeSouth){s.lastHeight=qx.lang.Number.limit(Math.min(e.getPageY(),
s.parentAreaOffsetBottom)-s.boxTop,
s.minHeight,
s.maxHeight);
}
switch(this.getResizeMethod()){case "opaque":case "translucent":if(this._resizeWest||this._resizeEast){this.setWidth(s.lastWidth);
if(this._resizeWest){this.setLeft(s.lastLeft);
}}
if(this._resizeNorth||this._resizeSouth){this.setHeight(s.lastHeight);
if(this._resizeNorth){this.setTop(s.lastTop);
}}break;
default:var o=this.getResizeMethod()=="frame"?this._frame:this;
if(this._resizeWest||this._resizeEast){o._renderRuntimeWidth(s.lastWidth);
if(this._resizeWest){o._renderRuntimeLeft(s.lastLeft);
}}
if(this._resizeNorth||this._resizeSouth){o._renderRuntimeHeight(s.lastHeight);
if(this._resizeNorth){o._renderRuntimeTop(s.lastTop);
}}}}else{var resizeMode="";
var el=this.getElement();
this._resizeNorth=this._resizeSouth=this._resizeWest=this._resizeEast=false;
if(this._near(qx.html.Location.getPageBoxTop(el),
e.getPageY())){if(this.getResizableNorth()){resizeMode="n";
this._resizeNorth=true;
}}else if(this._near(qx.html.Location.getPageBoxBottom(el),
e.getPageY())){if(this.getResizableSouth()){resizeMode="s";
this._resizeSouth=true;
}}
if(this._near(qx.html.Location.getPageBoxLeft(el),
e.getPageX())){if(this.getResizableWest()){resizeMode+="w";
this._resizeWest=true;
}}else if(this._near(qx.html.Location.getPageBoxRight(el),
e.getPageX())){if(this.getResizableEast()){resizeMode+="e";
this._resizeEast=true;
}}
if(this._resizeNorth||this._resizeSouth||this._resizeWest||this._resizeEast){this.setCursor(resizeMode+"-resize");
}else{this.resetCursor();
}}e.stopPropagation();
}},
destruct:function(){this._disposeObjects("_frame");
}});




/* ID: qx.ui.resizer.IResizable */
qx.Interface.define("qx.ui.resizer.IResizable",
{members:{_changeWidth:function(newWidth){return true;
},
_changeHeight:function(newHeight){return true;
},
_getResizeParent:function(){return true;
},
_getMinSizeReference:function(){return true;
}}});




/* ID: qx.ui.resizer.ResizablePopup */
qx.Class.define("qx.ui.resizer.ResizablePopup",
{extend:qx.ui.popup.Popup,
include:qx.ui.resizer.MResizable,
implement:qx.ui.resizer.IResizable,
construct:function(){this.base(arguments);
this.initMinWidth();
this.initMinHeight();
this.initWidth();
this.initHeight();
},
properties:{appearance:{refine:true,
init:"resizer"},
minWidth:{refine:true,
init:"auto"},
minHeight:{refine:true,
init:"auto"},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"}},
members:{_changeWidth:function(value){this.setWidth(value);
},
_changeHeight:function(value){this.setHeight(value);
},
_getResizeParent:function(){return this.getParent();
},
_getMinSizeReference:function(){return this;
}}});




/* ID: qx.ui.window.Window */
qx.Class.define("qx.ui.window.Window",
{extend:qx.ui.resizer.ResizablePopup,
construct:function(vCaption,
vIcon,
vWindowManager){this.base(arguments);
this.setWindowManager(vWindowManager||qx.ui.window.Window.getDefaultWindowManager());
var l=this._layout=new qx.ui.layout.VerticalBoxLayout;
l.setEdge(0);
this.add(l);
var cb=this._captionBar=new qx.ui.layout.HorizontalBoxLayout;
cb.setAppearance("window-captionbar");
cb.setHeight("auto");
cb.setOverflow("hidden");
l.add(cb);
var ci=this._captionIcon=new qx.ui.basic.Image;
ci.setAppearance("window-captionbar-icon");
cb.add(ci);
var ct=this._captionTitle=new qx.ui.basic.Label(vCaption);
ct.setAppearance("window-captionbar-title");
ct.setSelectable(false);
cb.add(ct);
var cf=this._captionFlex=new qx.ui.basic.HorizontalSpacer;
cb.add(cf);
var bm=this._minimizeButton=new qx.ui.form.Button;
bm.setAppearance("window-captionbar-minimize-button");
bm.setTabIndex(-1);
bm.addEventListener("execute",
this._onminimizebuttonclick,
this);
bm.addEventListener("mousedown",
this._onbuttonmousedown,
this);
cb.add(bm);
var br=this._restoreButton=new qx.ui.form.Button;
br.setAppearance("window-captionbar-restore-button");
br.setTabIndex(-1);
br.addEventListener("execute",
this._onrestorebuttonclick,
this);
br.addEventListener("mousedown",
this._onbuttonmousedown,
this);
var bx=this._maximizeButton=new qx.ui.form.Button;
bx.setAppearance("window-captionbar-maximize-button");
bx.setTabIndex(-1);
bx.addEventListener("execute",
this._onmaximizebuttonclick,
this);
bx.addEventListener("mousedown",
this._onbuttonmousedown,
this);
cb.add(bx);
var bc=this._closeButton=new qx.ui.form.Button;
bc.setAppearance("window-captionbar-close-button");
bc.setTabIndex(-1);
bc.addEventListener("execute",
this._onclosebuttonclick,
this);
bc.addEventListener("mousedown",
this._onbuttonmousedown,
this);
cb.add(bc);
var p=this._pane=new qx.ui.layout.CanvasLayout;
p.setHeight("1*");
p.setOverflow("hidden");
l.add(p);
var sb=this._statusBar=new qx.ui.layout.HorizontalBoxLayout;
sb.setAppearance("window-statusbar");
sb.setHeight("auto");
var st=this._statusText=new qx.ui.basic.Label("Ready");
st.setAppearance("window-statusbar-text");
st.setSelectable(false);
sb.add(st);
if(vCaption!=null){this.setCaption(vCaption);
}
if(vIcon!=null){this.setIcon(vIcon);
}this.setAutoHide(false);
this.addEventListener("mousedown",
this._onwindowmousedown);
this.addEventListener("click",
this._onwindowclick);
cb.addEventListener("mousedown",
this._oncaptionmousedown,
this);
cb.addEventListener("mouseup",
this._oncaptionmouseup,
this);
cb.addEventListener("mousemove",
this._oncaptionmousemove,
this);
cb.addEventListener("dblclick",
this._oncaptiondblblick,
this);
this.remapChildrenHandlingTo(this._pane);
},
statics:{getDefaultWindowManager:function(){if(!qx.ui.window.Window._defaultWindowManager){qx.ui.window.Window._defaultWindowManager=new qx.ui.window.Manager;
}return qx.ui.window.Window._defaultWindowManager;
}},
properties:{appearance:{refine:true,
init:"window"},
windowManager:{check:"qx.ui.window.Manager",
event:"changeWindowManager"},
active:{check:"Boolean",
init:false,
apply:"_applyActive",
event:"changeActive"},
modal:{check:"Boolean",
init:false,
apply:"_applyModal",
event:"changeModal"},
mode:{check:["minimized",
"maximized"],
init:null,
nullable:true,
apply:"_applyMode",
event:"changeMode"},
opener:{check:"qx.ui.core.Widget"},
caption:{apply:"_applyCaption",
event:"changeCaption",
dispose:true},
icon:{check:"String",
nullable:true,
apply:"_applyIcon",
event:"changeIcon"},
status:{check:"String",
init:"Ready",
apply:"_applyStatus",
event:"changeStatus"},
showClose:{check:"Boolean",
init:true,
apply:"_applyShowClose"},
showMaximize:{check:"Boolean",
init:true,
apply:"_applyShowMaximize"},
showMinimize:{check:"Boolean",
init:true,
apply:"_applyShowMinimize"},
showStatusbar:{check:"Boolean",
init:false,
apply:"_applyShowStatusbar"},
allowClose:{check:"Boolean",
init:true,
apply:"_applyAllowClose"},
allowMaximize:{check:"Boolean",
init:true,
apply:"_applyAllowMaximize"},
allowMinimize:{check:"Boolean",
init:true,
apply:"_applyAllowMinimize"},
showCaption:{check:"Boolean",
init:true,
apply:"_applyShowCaption"},
showIcon:{check:"Boolean",
init:true,
apply:"_applyShowIcon"},
moveable:{check:"Boolean",
init:true,
event:"changeMoveable"},
moveMethod:{check:["opaque",
"frame",
"translucent"],
init:"opaque",
event:"changeMoveMethod"}},
members:{getPane:function(){return this._pane;
},
getCaptionBar:function(){return this._captionBar;
},
getStatusBar:function(){return this._statusBar;
},
close:function(){this.hide();
},
open:function(vOpener){if(vOpener!=null){this.setOpener(vOpener);
}
if(this.getCentered()){this.centerToBrowser();
}this.show();
},
focus:function(){this.setActive(true);
},
blur:function(){this.setActive(false);
},
maximize:function(){this.setMode("maximized");
},
minimize:function(){this.setMode("minimized");
},
restore:function(){this.setMode(null);
},
_beforeAppear:function(){qx.ui.layout.CanvasLayout.prototype._beforeAppear.call(this);
qx.ui.popup.PopupManager.getInstance().update();
qx.event.handler.EventHandler.getInstance().setFocusRoot(this);
this.getWindowManager().add(this);
this._makeActive();
},
_beforeDisappear:function(){qx.ui.layout.CanvasLayout.prototype._beforeDisappear.call(this);
var vFocusRoot=qx.event.handler.EventHandler.getInstance().getFocusRoot();
if(vFocusRoot==this||this.contains(vFocusRoot)){qx.event.handler.EventHandler.getInstance().setFocusRoot(null);
}var vWidget=qx.event.handler.EventHandler.getInstance().getCaptureWidget();
if(vWidget&&this.contains(vWidget)){vWidget.setCapture(false);
}this.getWindowManager().remove(this);
this._makeInactive();
},
_minZIndex:1e5,
_sendTo:function(){var vAll=qx.lang.Object.getValues(this.getWindowManager().getAll()).sort(qx.util.Compare.byZIndex);
var vLength=vAll.length;
var vIndex=this._minZIndex;
for(var i=0;i<vLength;i++){vAll[i].setZIndex(vIndex++);
}},
_applyActive:function(value,
old){if(old){if(this.getFocused()){this.setFocused(false);
}
if(this.getWindowManager().getActiveWindow()==this){this.getWindowManager().setActiveWindow(null);
}this.removeState("active");
this._captionBar.removeState("active");
this._minimizeButton.removeState("active");
this._restoreButton.removeState("active");
this._maximizeButton.removeState("active");
this._closeButton.removeState("active");
}else{if(!this.getFocusedChild()){this.setFocused(true);
}this.getWindowManager().setActiveWindow(this);
this.bringToFront();
this.addState("active");
this._captionBar.addState("active");
this._minimizeButton.addState("active");
this._restoreButton.addState("active");
this._maximizeButton.addState("active");
this._closeButton.addState("active");
}},
_applyModal:function(value,
old){if(this._initialLayoutDone&&this.getVisibility()&&this.getDisplay()){var vTop=this.getTopLevelWidget();
value?vTop.block(this):vTop.release(this);
}},
_applyAllowClose:function(value,
old){this._closeButtonManager();
},
_applyAllowMaximize:function(value,
old){this._maximizeButtonManager();
},
_applyAllowMinimize:function(value,
old){this._minimizeButtonManager();
},
_applyMode:function(value,
old){switch(value){case "minimized":this._minimize();
break;
case "maximized":this._maximize();
break;
default:switch(old){case "maximized":this._restoreFromMaximized();
break;
case "minimized":this._restoreFromMinimized();
break;
}}},
_applyShowCaption:function(value,
old){if(value){this._captionBar.addAt(this._captionTitle,
this.getShowIcon()?1:0);
}else{this._captionBar.remove(this._captionTitle);
}},
_applyShowIcon:function(value,
old){if(value){this._captionBar.addAtBegin(this._captionIcon);
}else{this._captionBar.remove(this._captionIcon);
}},
_applyShowStatusbar:function(value,
old){if(value){this._layout.addAtEnd(this._statusBar);
}else{this._layout.remove(this._statusBar);
}},
_applyShowClose:function(value,
old){if(value){this._captionBar.addAtEnd(this._closeButton);
}else{this._captionBar.remove(this._closeButton);
}},
_applyShowMaximize:function(value,
old){if(value){var t=this.getMode()=="maximized"?this._restoreButton:this._maximizeButton;
if(this.getShowMinimize()){this._captionBar.addAfter(t,
this._minimizeButton);
}else{this._captionBar.addAfter(t,
this._captionFlex);
}}else{this._captionBar.remove(this._maximizeButton);
this._captionBar.remove(this._restoreButton);
}},
_applyShowMinimize:function(value,
old){if(value){this._captionBar.addAfter(this._minimizeButton,
this._captionFlex);
}else{this._captionBar.remove(this._minimizeButton);
}},
_minimizeButtonManager:function(){this.getAllowMinimize()===false?this._minimizeButton.setEnabled(false):this._minimizeButton.resetEnabled();
},
_closeButtonManager:function(){this.getAllowClose()===false?this._closeButton.setEnabled(false):this._closeButton.resetEnabled();
},
_maximizeButtonManager:function(){var b=this.getAllowMaximize()&&this.getResizable()&&this._computedMaxWidthTypeNull&&this._computedMaxHeightTypeNull;
if(this._maximizeButton){b===false?this._maximizeButton.setEnabled(false):this._maximizeButton.resetEnabled();
}
if(this._restoreButton){b===false?this._restoreButton.setEnabled(false):this._restoreButton.resetEnabled();
}},
_applyStatus:function(value,
old){this._statusText.setText(value);
},
_applyMaxWidth:function(value,
old){this.base(arguments);
this._maximizeButtonManager();
},
_applyMaxHeight:function(value,
old){this.base(arguments);
this._maximizeButtonManager();
},
_applyResizable:function(value,
old){this._maximizeButtonManager();
},
_applyCaption:function(value,
old){this._captionTitle.setText(value);
},
_applyIcon:function(value,
old){this._captionIcon.setSource(value);
},
_minimize:function(){this.blur();
this.hide();
},
_restoreFromMaximized:function(){this.setLeft(this._previousLeft?this._previousLeft:null);
this.setWidth(this._previousWidth?this._previousWidth:null);
this.setRight(this._previousRight?this._previousRight:null);
this.setTop(this._previousTop?this._previousTop:null);
this.setHeight(this._previousHeight?this._previousHeight:null);
this.setBottom(this._previousBottom?this._previousBottom:null);
this.removeState("maximized");
if(this.getShowMaximize()){var cb=this._captionBar;
var v=cb.indexOf(this._restoreButton);
cb.remove(this._restoreButton);
cb.addAt(this._maximizeButton,
v);
}this.focus();
},
_restoreFromMinimized:function(){if(this.hasState("maximized")){this.setMode("maximized");
}this.show();
this.focus();
},
_maximize:function(){if(this.hasState("maximized")){return;
}this._previousLeft=this.getLeft();
this._previousWidth=this.getWidth();
this._previousRight=this.getRight();
this._previousTop=this.getTop();
this._previousHeight=this.getHeight();
this._previousBottom=this.getBottom();
this.setWidth(null);
this.setLeft(0);
this.setRight(0);
this.setHeight(null);
this.setTop(0);
this.setBottom(0);
this.addState("maximized");
if(this.getShowMaximize()){var cb=this._captionBar;
var v=cb.indexOf(this._maximizeButton);
cb.remove(this._maximizeButton);
cb.addAt(this._restoreButton,
v);
}this.focus();
},
_onwindowclick:function(e){e.stopPropagation();
},
_onwindowmousedown:function(e){this.focus();
},
_onbuttonmousedown:function(e){e.stopPropagation();
},
_onminimizebuttonclick:function(e){this.minimize();
this._minimizeButton.removeState("pressed");
this._minimizeButton.removeState("abandoned");
this._minimizeButton.removeState("over");
e.stopPropagation();
},
_onrestorebuttonclick:function(e){this.restore();
this._restoreButton.removeState("pressed");
this._restoreButton.removeState("abandoned");
this._restoreButton.removeState("over");
e.stopPropagation();
},
_onmaximizebuttonclick:function(e){this.maximize();
this._maximizeButton.removeState("pressed");
this._maximizeButton.removeState("abandoned");
this._maximizeButton.removeState("over");
e.stopPropagation();
},
_onclosebuttonclick:function(e){this.close();
this._closeButton.removeState("pressed");
this._closeButton.removeState("abandoned");
this._closeButton.removeState("over");
e.stopPropagation();
},
_oncaptionmousedown:function(e){if(!e.isLeftButtonPressed()||!this.getMoveable()||this.getMode()!=null){return;
}this._captionBar.setCapture(true);
var el=this.getElement();
var pa=this.getParent();
var pl=pa.getElement();
var l=qx.html.Location.getPageAreaLeft(pl);
var t=qx.html.Location.getPageAreaTop(pl);
var r=qx.html.Location.getPageAreaRight(pl);
var b=qx.html.Location.getPageAreaBottom(pl);
this._dragSession={offsetX:e.getPageX()-qx.html.Location.getPageBoxLeft(el)+l,
offsetY:e.getPageY()-qx.html.Location.getPageBoxTop(el)+t,
parentAvailableAreaLeft:l+5,
parentAvailableAreaTop:t+5,
parentAvailableAreaRight:r-5,
parentAvailableAreaBottom:b-5};
switch(this.getMoveMethod()){case "translucent":this.setOpacity(0.5);
break;
case "frame":var f=this._frame;
if(f.getParent()!=this.getParent()){f.setParent(this.getParent());
qx.ui.core.Widget.flushGlobalQueues();
}f._renderRuntimeLeft(qx.html.Location.getPageBoxLeft(el)-l);
f._renderRuntimeTop(qx.html.Location.getPageBoxTop(el)-t);
f._renderRuntimeWidth(qx.html.Dimension.getBoxWidth(el));
f._renderRuntimeHeight(qx.html.Dimension.getBoxHeight(el));
f.setZIndex(this.getZIndex()+1);
break;
}},
_oncaptionmouseup:function(e){var s=this._dragSession;
if(!s){return;
}this._captionBar.setCapture(false);
if(s.lastX!=null){this.setLeft(s.lastX);
}
if(s.lastY!=null){this.setTop(s.lastY);
}switch(this.getMoveMethod()){case "translucent":this.setOpacity(null);
break;
case "frame":this._frame.setParent(null);
break;
}delete this._dragSession;
},
_oncaptionmousemove:function(e){var s=this._dragSession;
if(!s||!this._captionBar.getCapture()){return;
}if(!qx.lang.Number.isBetweenRange(e.getPageX(),
s.parentAvailableAreaLeft,
s.parentAvailableAreaRight)||!qx.lang.Number.isBetweenRange(e.getPageY(),
s.parentAvailableAreaTop,
s.parentAvailableAreaBottom)){return;
}var o=this.getMoveMethod()=="frame"?this._frame:this;
o._renderRuntimeLeft(s.lastX=e.getPageX()-s.offsetX);
o._renderRuntimeTop(s.lastY=e.getPageY()-s.offsetY);
},
_oncaptiondblblick:function(e){if(!this._maximizeButton.getEnabled()){return;
}return this.getMode()=="maximized"?this.restore():this.maximize();
}},
destruct:function(){this._disposeObjects("_layout",
"_captionBar",
"_captionIcon",
"_captionTitle",
"_captionFlex",
"_closeButton",
"_minimizeButton",
"_maximizeButton",
"_restoreButton",
"_pane",
"_statusBar",
"_statusText");
}});




/* ID: qx.ui.layout.BoxLayout */
qx.Class.define("qx.ui.layout.BoxLayout",
{extend:qx.ui.core.Parent,
construct:function(orientation){this.base(arguments);
if(orientation!=null){this.setOrientation(orientation);
}else{this.initOrientation();
}},
statics:{STR_REVERSED:"-reversed"},
properties:{orientation:{check:["horizontal",
"vertical"],
init:"horizontal",
apply:"_applyOrientation",
event:"changeOrientation"},
spacing:{check:"Integer",
init:0,
themeable:true,
apply:"_applySpacing",
event:"changeSpacing"},
horizontalChildrenAlign:{check:["left",
"center",
"right"],
init:"left",
themeable:true,
apply:"_applyHorizontalChildrenAlign"},
verticalChildrenAlign:{check:["top",
"middle",
"bottom"],
init:"top",
themeable:true,
apply:"_applyVerticalChildrenAlign"},
reverseChildrenOrder:{check:"Boolean",
init:false,
apply:"_applyReverseChildrenOrder"},
stretchChildrenOrthogonalAxis:{check:"Boolean",
init:true,
apply:"_applyStretchChildrenOrthogonalAxis"},
useAdvancedFlexAllocation:{check:"Boolean",
init:false,
apply:"_applyUseAdvancedFlexAllocation"},
accumulatedChildrenOuterWidth:{_cached:true,
defaultValue:null},
accumulatedChildrenOuterHeight:{_cached:true,
defaultValue:null}},
members:{_createLayoutImpl:function(){return this.getOrientation()=="vertical"?new qx.ui.layout.impl.VerticalBoxLayoutImpl(this):new qx.ui.layout.impl.HorizontalBoxLayoutImpl(this);
},
_layoutHorizontal:false,
_layoutVertical:false,
_layoutMode:"left",
isHorizontal:function(){return this._layoutHorizontal;
},
isVertical:function(){return this._layoutVertical;
},
getLayoutMode:function(){if(this._layoutMode==null){this._updateLayoutMode();
}return this._layoutMode;
},
_updateLayoutMode:function(){this._layoutMode=this._layoutVertical?this.getVerticalChildrenAlign():this.getHorizontalChildrenAlign();
if(this.getReverseChildrenOrder()){this._layoutMode+=qx.ui.layout.BoxLayout.STR_REVERSED;
}},
_invalidateLayoutMode:function(){this._layoutMode=null;
},
_applyOrientation:function(value,
old){this._layoutHorizontal=value=="horizontal";
this._layoutVertical=value=="vertical";
if(this._layoutImpl){this._layoutImpl.dispose();
this._layoutImpl=null;
}
if(value){this._layoutImpl=this._createLayoutImpl();
}this._doLayoutOrder(value,
old);
this.addToQueueRuntime("orientation");
},
_applySpacing:function(value,
old){this._doLayout();
this.addToQueueRuntime("spacing");
},
_applyHorizontalChildrenAlign:function(value,
old){this._doLayoutOrder();
this.addToQueueRuntime("horizontalChildrenAlign");
},
_applyVerticalChildrenAlign:function(value,
old){this._doLayoutOrder();
this.addToQueueRuntime("verticalChildrenAlign");
},
_applyReverseChildrenOrder:function(value,
old){this._doLayoutOrder();
this.addToQueueRuntime("reverseChildrenOrder");
},
_applyStretchChildrenOrthogonalAxis:function(value,
old){this.addToQueueRuntime("stretchChildrenOrthogonalAxis");
},
_applyUseAdvancedFlexAllocation:function(value,
old){this.addToQueueRuntime("useAdvancedFlexAllocation");
},
_doLayoutOrder:function(){this._invalidateLayoutMode();
this._doLayout();
},
_doLayout:function(){this._invalidatePreferredInnerDimensions();
this._invalidateAccumulatedChildrenOuterWidth();
this._invalidateAccumulatedChildrenOuterHeight();
},
_computeAccumulatedChildrenOuterWidth:function(){var ch=this.getVisibleChildren(),
chc,
i=-1,
sp=this.getSpacing(),
s=-sp;
while(chc=ch[++i]){s+=chc.getOuterWidth()+sp;
}return s;
},
_computeAccumulatedChildrenOuterHeight:function(){var ch=this.getVisibleChildren(),
chc,
i=-1,
sp=this.getSpacing(),
s=-sp;
while(chc=ch[++i]){s+=chc.getOuterHeight()+sp;
}return s;
},
_recomputeChildrenStretchingX:function(){var ch=this.getVisibleChildren(),
chc,
i=-1;
while(chc=ch[++i]){if(chc._recomputeStretchingX()&&chc._recomputeBoxWidth()){chc._recomputeOuterWidth();
}}},
_recomputeChildrenStretchingY:function(){var ch=this.getVisibleChildren(),
chc,
i=-1;
while(chc=ch[++i]){if(chc._recomputeStretchingY()&&chc._recomputeBoxHeight()){chc._recomputeOuterHeight();
}}}}});




/* ID: qx.ui.layout.impl.VerticalBoxLayoutImpl */
qx.Class.define("qx.ui.layout.impl.VerticalBoxLayoutImpl",
{extend:qx.ui.layout.impl.LayoutImpl,
properties:{enableFlexSupport:{check:"Boolean",
init:true}},
members:{computeChildBoxWidth:function(vChild){if(this.getWidget().getStretchChildrenOrthogonalAxis()&&vChild._computedWidthTypeNull&&vChild.getAllowStretchX()){return this.getWidget().getInnerWidth();
}return vChild.getWidthValue()||vChild._computeBoxWidthFallback();
},
computeChildBoxHeight:function(vChild){return vChild.getHeightValue()||vChild._computeBoxHeightFallback();
},
computeChildrenFlexHeight:function(){if(this._childrenFlexHeightComputed||!this.getEnableFlexSupport()){return;
}this._childrenFlexHeightComputed=true;
var vWidget=this.getWidget();
var vChildren=vWidget.getVisibleChildren();
var vChildrenLength=vChildren.length;
var vCurrentChild;
var vFlexibleChildren=[];
var vAvailHeight=vWidget.getInnerHeight();
var vUsedHeight=vWidget.getSpacing()*(vChildrenLength-1);
var vIterator;
for(vIterator=0;vIterator<vChildrenLength;vIterator++){vCurrentChild=vChildren[vIterator];
if(vCurrentChild._computedHeightTypeFlex){vFlexibleChildren.push(vCurrentChild);
if(vWidget._computedHeightTypeAuto){vUsedHeight+=vCurrentChild.getPreferredBoxHeight();
}}else{vUsedHeight+=vCurrentChild.getOuterHeight();
}}var vRemainingHeight=vAvailHeight-vUsedHeight;
var vFlexibleChildrenLength=vFlexibleChildren.length;
var vPrioritySum=0;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vPrioritySum+=vFlexibleChildren[vIterator]._computedHeightParsed;
}var vPartHeight=vRemainingHeight/vPrioritySum;
if(!vWidget.getUseAdvancedFlexAllocation()){for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightParsed*vPartHeight);
vUsedHeight+=vCurrentChild._computedHeightFlexValue;
}}else{var vAllocationDiff=0;
var vMinAllocationLoops,
vFlexibleChildrenLength,
vAdjust,
vCurrentAllocationSum,
vFactorSum,
vComputedFlexibleHeight;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vComputedFlexibleHeight=vCurrentChild._computedHeightFlexValue=vCurrentChild._computedHeightParsed*vPartHeight;
vAllocationDiff+=vComputedFlexibleHeight-qx.lang.Number.limit(vComputedFlexibleHeight,
vCurrentChild.getMinHeightValue(),
vCurrentChild.getMaxHeightValue());
}vAllocationDiff=Math.round(vAllocationDiff);
if(vAllocationDiff==0){for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightFlexValue);
vUsedHeight+=vCurrentChild._computedHeightFlexValue;
}}else{var vUp=vAllocationDiff>0;
for(vIterator=vFlexibleChildrenLength-1;vIterator>=0;vIterator--){vCurrentChild=vFlexibleChildren[vIterator];
if(vUp){vAdjust=(vCurrentChild.getMaxHeightValue()||Infinity)-vCurrentChild._computedHeightFlexValue;
if(vAdjust>0){vCurrentChild._allocationLoops=Math.floor(vAdjust/vCurrentChild._computedHeightParsed);
}else{qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightFlexValue);
vUsedHeight+=Math.round(vCurrentChild._computedHeightFlexValue+vAdjust);
}}else{vAdjust=qx.util.Validation.isValidNumber(vCurrentChild.getMinHeightValue())?vCurrentChild._computedHeightFlexValue-vCurrentChild.getMinHeightValue():vCurrentChild._computedHeightFlexValue;
if(vAdjust>0){vCurrentChild._allocationLoops=Math.floor(vAdjust/vCurrentChild._computedHeightParsed);
}else{qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightFlexValue);
vUsedHeight+=Math.round(vCurrentChild._computedHeightFlexValue-vAdjust);
}}}while(vAllocationDiff!=0&&vFlexibleChildrenLength>0){vFlexibleChildrenLength=vFlexibleChildren.length;
vMinAllocationLoops=Infinity;
vFactorSum=0;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vMinAllocationLoops=Math.min(vMinAllocationLoops,
vFlexibleChildren[vIterator]._allocationLoops);
vFactorSum+=vFlexibleChildren[vIterator]._computedHeightParsed;
}vCurrentAllocationSum=Math.min(vFactorSum*vMinAllocationLoops,
vAllocationDiff);
vAllocationDiff-=vCurrentAllocationSum;
for(vIterator=vFlexibleChildrenLength-1;vIterator>=0;vIterator--){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedHeightFlexValue+=vCurrentAllocationSum/vFactorSum*vCurrentChild._computedHeightParsed;
if(vCurrentChild._allocationLoops==vMinAllocationLoops){vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightFlexValue);
vUsedHeight+=vCurrentChild._computedHeightFlexValue;
delete vCurrentChild._allocationLoops;
qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
}else{if(vAllocationDiff==0){vCurrentChild._computedHeightFlexValue=Math.round(vCurrentChild._computedHeightFlexValue);
vUsedHeight+=vCurrentChild._computedHeightFlexValue;
delete vCurrentChild._allocationLoops;
}else{vCurrentChild._allocationLoops-=vMinAllocationLoops;
}}}}}}vCurrentChild._computedHeightFlexValue+=vAvailHeight-vUsedHeight;
},
invalidateChildrenFlexHeight:function(){delete this._childrenFlexHeightComputed;
},
computeChildrenNeededHeight:function(){var w=this.getWidget();
return qx.ui.layout.impl.LayoutImpl.prototype.computeChildrenNeededHeight_sum.call(this)+((w.getVisibleChildrenLength()-1)*w.getSpacing());
},
updateSelfOnChildOuterHeightChange:function(vChild){this.getWidget()._invalidateAccumulatedChildrenOuterHeight();
},
updateChildOnInnerWidthChange:function(vChild){var vUpdatePercent=vChild._recomputePercentX();
var vUpdateStretch=vChild._recomputeStretchingX();
if((vChild.getHorizontalAlign()||this.getWidget().getHorizontalChildrenAlign())=="center"){vChild.addToLayoutChanges("locationX");
}return vUpdatePercent||vUpdateStretch;
},
updateChildOnInnerHeightChange:function(vChild){if(this.getWidget().getVerticalChildrenAlign()=="middle"){vChild.addToLayoutChanges("locationY");
}var vUpdatePercent=vChild._recomputePercentY();
var vUpdateFlex=vChild._recomputeFlexY();
return vUpdatePercent||vUpdateFlex;
},
updateSelfOnJobQueueFlush:function(vJobQueue){if(vJobQueue.addChild||vJobQueue.removeChild){this.getWidget()._invalidateAccumulatedChildrenOuterHeight();
}},
updateChildrenOnJobQueueFlush:function(vQueue){var vStretchX=false,
vStretchY=false;
var vWidget=this.getWidget();
if(vQueue.orientation){vStretchX=vStretchY=true;
}if(vQueue.spacing||vQueue.orientation||vQueue.reverseChildrenOrder||vQueue.verticalChildrenAlign){vWidget._addChildrenToLayoutQueue("locationY");
}
if(vQueue.horizontalChildrenAlign){vWidget._addChildrenToLayoutQueue("locationX");
}
if(vQueue.stretchChildrenOrthogonalAxis){vStretchX=true;
}if(vStretchX){vWidget._recomputeChildrenStretchingX();
vWidget._addChildrenToLayoutQueue("width");
}
if(vStretchY){vWidget._recomputeChildrenStretchingY();
vWidget._addChildrenToLayoutQueue("height");
}return true;
},
updateChildrenOnRemoveChild:function(vChild,
vIndex){var w=this.getWidget(),
ch=w.getVisibleChildren(),
chl=ch.length,
chc,
i=-1;
if(this.getEnableFlexSupport()){for(var i=0;i<chl;i++){chc=ch[i];
if(chc.getHasFlexY()){vIndex=Math.min(vIndex,
i);
break;
}}i=-1;
}switch(w.getLayoutMode()){case "bottom":case "top-reversed":while((chc=ch[++i])&&i<vIndex){chc.addToLayoutChanges("locationY");
}break;
case "middle":case "middle-reversed":while(chc=ch[++i]){chc.addToLayoutChanges("locationY");
}break;
default:i+=vIndex;
while(chc=ch[++i]){chc.addToLayoutChanges("locationY");
}}},
updateChildrenOnMoveChild:function(vChild,
vIndex,
vOldIndex){var vChildren=this.getWidget().getVisibleChildren();
var vStart=Math.min(vIndex,
vOldIndex);
var vStop=Math.max(vIndex,
vOldIndex)+1;
for(var i=vStart;i<vStop;i++){vChildren[i].addToLayoutChanges("locationY");
}},
flushChildrenQueue:function(vChildrenQueue){var w=this.getWidget(),
ch=w.getVisibleChildren(),
chl=ch.length,
chc,
i;
if(this.getEnableFlexSupport()){this.invalidateChildrenFlexHeight();
for(i=0;i<chl;i++){chc=ch[i];
if(chc.getHasFlexY()){chc._computedHeightValue=null;
if(chc._recomputeBoxHeight()){chc._recomputeOuterHeight();
chc._recomputeInnerHeight();
}vChildrenQueue[chc.toHashCode()]=chc;
chc._layoutChanges.height=true;
}}}
switch(w.getLayoutMode()){case "bottom":case "top-reversed":for(var i=chl-1;i>=0&&!vChildrenQueue[ch[i].toHashCode()];i--){}for(var j=0;j<=i;j++){w._layoutChild(chc=ch[j]);
}break;
case "middle":case "middle-reversed":i=-1;
while(chc=ch[++i]){w._layoutChild(chc);
}break;
default:i=-1;
var changed=false;
while(chc=ch[++i]){if(changed||vChildrenQueue[chc.toHashCode()]){w._layoutChild(chc);
changed=true;
}}}},
layoutChild:function(vChild,
vJobs){this.layoutChild_sizeX(vChild,
vJobs);
this.layoutChild_sizeY(vChild,
vJobs);
this.layoutChild_sizeLimitX(vChild,
vJobs);
this.layoutChild_sizeLimitY(vChild,
vJobs);
this.layoutChild_locationX(vChild,
vJobs);
this.layoutChild_locationY(vChild,
vJobs);
this.layoutChild_marginX(vChild,
vJobs);
this.layoutChild_marginY(vChild,
vJobs);
},
layoutChild_sizeX:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.width||vJobs.minWidth||vJobs.maxWidth){if((vChild._isWidthEssential()&&(!vChild._computedWidthTypeNull||!vChild._computedMinWidthTypeNull||!vChild._computedMaxWidthTypeNull))||(vChild.getAllowStretchX()&&this.getWidget().getStretchChildrenOrthogonalAxis())){vChild._renderRuntimeWidth(vChild.getBoxWidth());
}else{vChild._resetRuntimeWidth();
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.width){if(vChild._isWidthEssential()&&!vChild._computedWidthTypeNull){vChild._renderRuntimeWidth(vChild.getWidthValue());
}else{vChild._resetRuntimeWidth();
}}}}),
layoutChild_sizeY:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.height||vJobs.minHeight||vJobs.maxHeight){if(vChild._isHeightEssential()&&(!vChild._computedHeightTypeNull||!vChild._computedMinHeightTypeNull||!vChild._computedMaxHeightTypeNull)){vChild._renderRuntimeHeight(vChild.getBoxHeight());
}else{vChild._resetRuntimeHeight();
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.height){if(vChild._isHeightEssential()&&!vChild._computedHeightTypeNull){vChild._renderRuntimeHeight(vChild.getHeightValue());
}else{vChild._resetRuntimeHeight();
}}}}),
layoutChild_locationY:function(vChild,
vJobs){var vWidget=this.getWidget();
if(vWidget.getFirstVisibleChild()==vChild){switch(vWidget.getLayoutMode()){case "bottom":case "top-reversed":var vPos=vWidget.getPaddingBottom()+vWidget.getAccumulatedChildrenOuterHeight()-vChild.getOuterHeight();
break;
case "middle":case "middle-reversed":var vPos=vWidget.getPaddingTop()+Math.round((vWidget.getInnerHeight()-vWidget.getAccumulatedChildrenOuterHeight())/2);
break;
default:var vPos=vWidget.getPaddingTop();
}}else{var vPrev=vChild.getPreviousVisibleSibling();
switch(vWidget.getLayoutMode()){case "bottom":case "top-reversed":var vPos=vPrev._cachedLocationVertical-vChild.getOuterHeight()-vWidget.getSpacing();
break;
default:var vPos=vPrev._cachedLocationVertical+vPrev.getOuterHeight()+vWidget.getSpacing();
}}vChild._cachedLocationVertical=vPos;
switch(this.getWidget().getLayoutMode()){case "bottom":case "bottom-reversed":case "middle-reversed":vPos+=!vChild._computedBottomTypeNull?vChild.getBottomValue():!vChild._computedTopTypeNull?-(vChild.getTopValue()):0;
vChild._resetRuntimeTop();
vChild._renderRuntimeBottom(vPos);
break;
default:vPos+=!vChild._computedTopTypeNull?vChild.getTopValue():!vChild._computedBottomTypeNull?-(vChild.getBottomValue()):0;
vChild._resetRuntimeBottom();
vChild._renderRuntimeTop(vPos);
}},
layoutChild_locationX:function(vChild,
vJobs){var vWidget=this.getWidget();
if(qx.core.Variant.isSet("qx.client",
"gecko")){if(vChild.getAllowStretchX()&&vWidget.getStretchChildrenOrthogonalAxis()&&vChild._computedWidthTypeNull){vChild._renderRuntimeLeft(vWidget.getPaddingLeft()||0);
vChild._renderRuntimeRight(vWidget.getPaddingRight()||0);
return;
}}var vAlign=vChild.getHorizontalAlign()||vWidget.getHorizontalChildrenAlign();
var vPos=vAlign=="center"?Math.round((vWidget.getInnerWidth()-vChild.getOuterWidth())/2):0;
if(vAlign=="right"){vPos+=vWidget.getPaddingRight();
if(!vChild._computedRightTypeNull){vPos+=vChild.getRightValue();
}else if(!vChild._computedLeftTypeNull){vPos-=vChild.getLeftValue();
}vChild._resetRuntimeLeft();
vChild._renderRuntimeRight(vPos);
}else{vPos+=vWidget.getPaddingLeft();
if(!vChild._computedLeftTypeNull){vPos+=vChild.getLeftValue();
}else if(!vChild._computedRightTypeNull){vPos-=vChild.getRightValue();
}vChild._resetRuntimeRight();
vChild._renderRuntimeLeft(vPos);
}}}});




/* ID: qx.ui.layout.impl.HorizontalBoxLayoutImpl */
qx.Class.define("qx.ui.layout.impl.HorizontalBoxLayoutImpl",
{extend:qx.ui.layout.impl.LayoutImpl,
properties:{enableFlexSupport:{check:"Boolean",
init:true}},
members:{computeChildBoxWidth:function(vChild){return vChild.getWidthValue()||vChild._computeBoxWidthFallback();
},
computeChildBoxHeight:function(vChild){if(this.getWidget().getStretchChildrenOrthogonalAxis()&&vChild._computedHeightTypeNull&&vChild.getAllowStretchY()){return this.getWidget().getInnerHeight();
}return vChild.getHeightValue()||vChild._computeBoxHeightFallback();
},
computeChildrenFlexWidth:function(){if(this._childrenFlexWidthComputed||!this.getEnableFlexSupport()){return;
}this._childrenFlexWidthComputed=true;
var vWidget=this.getWidget();
var vChildren=vWidget.getVisibleChildren();
var vChildrenLength=vChildren.length;
var vCurrentChild;
var vFlexibleChildren=[];
var vAvailWidth=vWidget.getInnerWidth();
var vUsedWidth=vWidget.getSpacing()*(vChildrenLength-1);
var vIterator;
for(vIterator=0;vIterator<vChildrenLength;vIterator++){vCurrentChild=vChildren[vIterator];
if(vCurrentChild._computedWidthTypeFlex){vFlexibleChildren.push(vCurrentChild);
if(vWidget._computedWidthTypeAuto){vUsedWidth+=vCurrentChild.getPreferredBoxWidth();
}}else{vUsedWidth+=vCurrentChild.getOuterWidth();
}}var vRemainingWidth=vAvailWidth-vUsedWidth;
var vFlexibleChildrenLength=vFlexibleChildren.length;
var vPrioritySum=0;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vPrioritySum+=vFlexibleChildren[vIterator]._computedWidthParsed;
}var vPartWidth=vRemainingWidth/vPrioritySum;
if(!vWidget.getUseAdvancedFlexAllocation()){for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthParsed*vPartWidth);
vUsedWidth+=vCurrentChild._computedWidthFlexValue;
}}else{var vAllocationDiff=0;
var vMinAllocationLoops,
vFlexibleChildrenLength,
vAdjust,
vCurrentAllocationSum,
vFactorSum,
vComputedFlexibleWidth;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vComputedFlexibleWidth=vCurrentChild._computedWidthFlexValue=vCurrentChild._computedWidthParsed*vPartWidth;
vAllocationDiff+=vComputedFlexibleWidth-qx.lang.Number.limit(vComputedFlexibleWidth,
vCurrentChild.getMinWidthValue(),
vCurrentChild.getMaxWidthValue());
}vAllocationDiff=Math.round(vAllocationDiff);
if(vAllocationDiff==0){for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthFlexValue);
vUsedWidth+=vCurrentChild._computedWidthFlexValue;
}}else{var vUp=vAllocationDiff>0;
for(vIterator=vFlexibleChildrenLength-1;vIterator>=0;vIterator--){vCurrentChild=vFlexibleChildren[vIterator];
if(vUp){vAdjust=(vCurrentChild.getMaxWidthValue()||Infinity)-vCurrentChild._computedWidthFlexValue;
if(vAdjust>0){vCurrentChild._allocationLoops=Math.floor(vAdjust/vCurrentChild._computedWidthParsed);
}else{qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthFlexValue);
vUsedWidth+=Math.round(vCurrentChild._computedWidthFlexValue+vAdjust);
}}else{vAdjust=qx.util.Validation.isValidNumber(vCurrentChild.getMinWidthValue())?vCurrentChild._computedWidthFlexValue-vCurrentChild.getMinWidthValue():vCurrentChild._computedWidthFlexValue;
if(vAdjust>0){vCurrentChild._allocationLoops=Math.floor(vAdjust/vCurrentChild._computedWidthParsed);
}else{qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthFlexValue);
vUsedWidth+=Math.round(vCurrentChild._computedWidthFlexValue-vAdjust);
}}}while(vAllocationDiff!=0&&vFlexibleChildrenLength>0){vFlexibleChildrenLength=vFlexibleChildren.length;
vMinAllocationLoops=Infinity;
vFactorSum=0;
for(vIterator=0;vIterator<vFlexibleChildrenLength;vIterator++){vMinAllocationLoops=Math.min(vMinAllocationLoops,
vFlexibleChildren[vIterator]._allocationLoops);
vFactorSum+=vFlexibleChildren[vIterator]._computedWidthParsed;
}vCurrentAllocationSum=Math.min(vFactorSum*vMinAllocationLoops,
vAllocationDiff);
vAllocationDiff-=vCurrentAllocationSum;
for(vIterator=vFlexibleChildrenLength-1;vIterator>=0;vIterator--){vCurrentChild=vFlexibleChildren[vIterator];
vCurrentChild._computedWidthFlexValue+=vCurrentAllocationSum/vFactorSum*vCurrentChild._computedWidthParsed;
if(vCurrentChild._allocationLoops==vMinAllocationLoops){vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthFlexValue);
vUsedWidth+=vCurrentChild._computedWidthFlexValue;
delete vCurrentChild._allocationLoops;
qx.lang.Array.removeAt(vFlexibleChildren,
vIterator);
}else{if(vAllocationDiff==0){vCurrentChild._computedWidthFlexValue=Math.round(vCurrentChild._computedWidthFlexValue);
vUsedWidth+=vCurrentChild._computedWidthFlexValue;
delete vCurrentChild._allocationLoops;
}else{vCurrentChild._allocationLoops-=vMinAllocationLoops;
}}}}}}vCurrentChild._computedWidthFlexValue+=vAvailWidth-vUsedWidth;
},
invalidateChildrenFlexWidth:function(){delete this._childrenFlexWidthComputed;
},
computeChildrenNeededWidth:function(){var w=this.getWidget();
return qx.ui.layout.impl.LayoutImpl.prototype.computeChildrenNeededWidth_sum.call(this)+((w.getVisibleChildrenLength()-1)*w.getSpacing());
},
updateSelfOnChildOuterWidthChange:function(vChild){this.getWidget()._invalidateAccumulatedChildrenOuterWidth();
},
updateChildOnInnerWidthChange:function(vChild){if(this.getWidget().getHorizontalChildrenAlign()=="center"){vChild.addToLayoutChanges("locationX");
}var vUpdatePercent=vChild._recomputePercentX();
var vUpdateFlex=vChild._recomputeFlexX();
return vUpdatePercent||vUpdateFlex;
},
updateChildOnInnerHeightChange:function(vChild){var vUpdatePercent=vChild._recomputePercentY();
var vUpdateStretch=vChild._recomputeStretchingY();
if((vChild.getVerticalAlign()||this.getWidget().getVerticalChildrenAlign())=="middle"){vChild.addToLayoutChanges("locationY");
}return vUpdatePercent||vUpdateStretch;
},
updateSelfOnJobQueueFlush:function(vJobQueue){if(vJobQueue.addChild||vJobQueue.removeChild){this.getWidget()._invalidateAccumulatedChildrenOuterWidth();
}},
updateChildrenOnJobQueueFlush:function(vQueue){var vStretchX=false,
vStretchY=false;
var vWidget=this.getWidget();
if(vQueue.orientation){vStretchX=vStretchY=true;
}if(vQueue.spacing||vQueue.orientation||vQueue.reverseChildrenOrder||vQueue.horizontalChildrenAlign){vWidget._addChildrenToLayoutQueue("locationX");
}
if(vQueue.verticalChildrenAlign){vWidget._addChildrenToLayoutQueue("locationY");
}
if(vQueue.stretchChildrenOrthogonalAxis){vStretchY=true;
}if(vStretchX){vWidget._recomputeChildrenStretchingX();
vWidget._addChildrenToLayoutQueue("width");
}
if(vStretchY){vWidget._recomputeChildrenStretchingY();
vWidget._addChildrenToLayoutQueue("height");
}return true;
},
updateChildrenOnRemoveChild:function(vChild,
vIndex){var w=this.getWidget(),
ch=w.getVisibleChildren(),
chl=ch.length,
chc,
i=-1;
if(this.getEnableFlexSupport()){for(i=0;i<chl;i++){chc=ch[i];
if(chc.getHasFlexX()){vIndex=Math.min(vIndex,
i);
break;
}}i=-1;
}switch(w.getLayoutMode()){case "right":case "left-reversed":while((chc=ch[++i])&&i<vIndex){chc.addToLayoutChanges("locationX");
}break;
case "center":case "center-reversed":while(chc=ch[++i]){chc.addToLayoutChanges("locationX");
}break;
default:i+=vIndex;
while(chc=ch[++i]){chc.addToLayoutChanges("locationX");
}}},
updateChildrenOnMoveChild:function(vChild,
vIndex,
vOldIndex){var vChildren=this.getWidget().getVisibleChildren();
var vStart=Math.min(vIndex,
vOldIndex);
var vStop=Math.max(vIndex,
vOldIndex)+1;
for(var i=vStart;i<vStop;i++){vChildren[i].addToLayoutChanges("locationX");
}},
flushChildrenQueue:function(vChildrenQueue){var w=this.getWidget(),
ch=w.getVisibleChildren(),
chl=ch.length,
chc,
i;
if(this.getEnableFlexSupport()){this.invalidateChildrenFlexWidth();
for(i=0;i<chl;i++){chc=ch[i];
if(chc.getHasFlexX()){chc._computedWidthValue=null;
if(chc._recomputeBoxWidth()){chc._recomputeOuterWidth();
chc._recomputeInnerWidth();
}vChildrenQueue[chc.toHashCode()]=chc;
chc._layoutChanges.width=true;
}}}
switch(w.getLayoutMode()){case "right":case "left-reversed":for(var i=chl-1;i>=0&&!vChildrenQueue[ch[i].toHashCode()];i--){}for(var j=0;j<=i;j++){w._layoutChild(chc=ch[j]);
}break;
case "center":case "center-reversed":i=-1;
while(chc=ch[++i]){w._layoutChild(chc);
}break;
default:i=-1;
var changed=false;
while(chc=ch[++i]){if(changed||vChildrenQueue[chc.toHashCode()]){w._layoutChild(chc);
changed=true;
}}}},
layoutChild:function(vChild,
vJobs){this.layoutChild_sizeX(vChild,
vJobs);
this.layoutChild_sizeY(vChild,
vJobs);
this.layoutChild_sizeLimitX(vChild,
vJobs);
this.layoutChild_sizeLimitY(vChild,
vJobs);
this.layoutChild_locationX(vChild,
vJobs);
this.layoutChild_locationY(vChild,
vJobs);
this.layoutChild_marginX(vChild,
vJobs);
this.layoutChild_marginY(vChild,
vJobs);
},
layoutChild_sizeX:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.width||vJobs.minWidth||vJobs.maxWidth){if(vChild._isWidthEssential()&&(!vChild._computedWidthTypeNull||!vChild._computedMinWidthTypeNull||!vChild._computedMaxWidthTypeNull)){vChild._renderRuntimeWidth(vChild.getBoxWidth());
}else{vChild._resetRuntimeWidth();
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.width){if(vChild._isWidthEssential()&&!vChild._computedWidthTypeNull){vChild._renderRuntimeWidth(vChild.getWidthValue());
}else{vChild._resetRuntimeWidth();
}}}}),
layoutChild_sizeY:qx.core.Variant.select("qx.client",
{"mshtml|opera|webkit":function(vChild,
vJobs){if(vJobs.initial||vJobs.height||vJobs.minHeight||vJobs.maxHeight){if((vChild._isHeightEssential()&&(!vChild._computedHeightTypeNull||!vChild._computedMinHeightTypeNull||!vChild._computedMaxHeightTypeNull))||(vChild.getAllowStretchY()&&this.getWidget().getStretchChildrenOrthogonalAxis())){vChild._renderRuntimeHeight(vChild.getBoxHeight());
}else{vChild._resetRuntimeHeight();
}}},
"default":function(vChild,
vJobs){if(vJobs.initial||vJobs.height){if(vChild._isHeightEssential()&&!vChild._computedHeightTypeNull){vChild._renderRuntimeHeight(vChild.getHeightValue());
}else{vChild._resetRuntimeHeight();
}}}}),
layoutChild_locationX:function(vChild,
vJobs){var vWidget=this.getWidget();
if(vWidget.getFirstVisibleChild()==vChild){switch(vWidget.getLayoutMode()){case "right":case "left-reversed":var vPos=vWidget.getPaddingRight()+vWidget.getAccumulatedChildrenOuterWidth()-vChild.getOuterWidth();
break;
case "center":case "center-reversed":var vPos=vWidget.getPaddingLeft()+Math.round((vWidget.getInnerWidth()-vWidget.getAccumulatedChildrenOuterWidth())/2);
break;
default:var vPos=vWidget.getPaddingLeft();
}}else{var vPrev=vChild.getPreviousVisibleSibling();
switch(vWidget.getLayoutMode()){case "right":case "left-reversed":var vPos=vPrev._cachedLocationHorizontal-vChild.getOuterWidth()-vWidget.getSpacing();
break;
default:var vPos=vPrev._cachedLocationHorizontal+vPrev.getOuterWidth()+vWidget.getSpacing();
}}vChild._cachedLocationHorizontal=vPos;
switch(vWidget.getLayoutMode()){case "right":case "right-reversed":case "center-reversed":vPos+=!vChild._computedRightTypeNull?vChild.getRightValue():!vChild._computedLeftTypeNull?-(vChild.getLeftValue()):0;
vChild._resetRuntimeLeft();
vChild._renderRuntimeRight(vPos);
break;
default:vPos+=!vChild._computedLeftTypeNull?vChild.getLeftValue():!vChild._computedRightTypeNull?-(vChild.getRightValue()):0;
vChild._resetRuntimeRight();
vChild._renderRuntimeLeft(vPos);
}},
layoutChild_locationY:function(vChild,
vJobs){var vWidget=this.getWidget();
if(qx.core.Variant.isSet("qx.client",
"gecko")){if(vChild.getAllowStretchY()&&vWidget.getStretchChildrenOrthogonalAxis()&&vChild._computedHeightTypeNull){vChild._renderRuntimeTop(vWidget.getPaddingTop()||0);
vChild._renderRuntimeBottom(vWidget.getPaddingBottom()||0);
return;
}}var vAlign=vChild.getVerticalAlign()||vWidget.getVerticalChildrenAlign();
var vPos=vAlign=="middle"?Math.round((vWidget.getInnerHeight()-vChild.getOuterHeight())/2):0;
if(vAlign=="bottom"){vPos+=vWidget.getPaddingBottom();
if(!vChild._computedBottomTypeNull){vPos+=vChild.getBottomValue();
}else if(!vChild._computedTopTypeNull){vPos-=vChild.getTopValue();
}vChild._resetRuntimeTop();
vChild._renderRuntimeBottom(vPos);
}else{vPos+=vWidget.getPaddingTop();
if(!vChild._computedTopTypeNull){vPos+=vChild.getTopValue();
}else if(!vChild._computedBottomTypeNull){vPos-=vChild.getBottomValue();
}vChild._resetRuntimeBottom();
vChild._renderRuntimeTop(vPos);
}}}});




/* ID: qx.ui.layout.VerticalBoxLayout */
qx.Class.define("qx.ui.layout.VerticalBoxLayout",
{extend:qx.ui.layout.BoxLayout,
properties:{orientation:{refine:true,
init:"vertical"}}});




/* ID: qx.ui.layout.HorizontalBoxLayout */
qx.Class.define("qx.ui.layout.HorizontalBoxLayout",
{extend:qx.ui.layout.BoxLayout});




/* ID: qx.ui.basic.Image */
qx.Class.define("qx.ui.basic.Image",
{extend:qx.ui.basic.Terminator,
construct:function(vSource,
vWidth,
vHeight){this.base(arguments);
this._blank=qx.io.Alias.getInstance().resolve("static/image/blank.gif");
if(vSource!=null){this.setSource(vSource);
}if(vWidth!=null){this.setWidth(vWidth);
}else{this.initWidth();
}
if(vHeight!=null){this.setHeight(vHeight);
}else{this.initHeight();
}this.initSelectable();
},
events:{"error":"qx.event.type.Event"},
properties:{allowStretchX:{refine:true,
init:false},
allowStretchY:{refine:true,
init:false},
selectable:{refine:true,
init:false},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
appearance:{refine:true,
init:"image"},
source:{check:"String",
apply:"_applySource",
event:"changeSource",
nullable:true,
themeable:true},
preloader:{check:"qx.io.image.Preloader",
apply:"_applyPreloader",
nullable:true},
loaded:{check:"Boolean",
init:false,
apply:"_applyLoaded"},
resizeToInner:{check:"Boolean",
init:false}},
members:{_onload:function(){this.setLoaded(true);
},
_onerror:function(){this.warn("Could not load: "+this.getSource());
this.setLoaded(false);
if(this.hasEventListeners("error")){this.dispatchEvent(new qx.event.type.Event("error"),
true);
}},
_beforeAppear:function(){var source=this.getSource();
if(source){qx.io.image.Manager.getInstance().show(source);
this._registeredAsVisible=true;
}return this.base(arguments);
},
_beforeDisappear:function(){var source=this.getSource();
if(source&&this._registeredAsVisible){qx.io.image.Manager.getInstance().hide(source);
delete this._registeredAsVisible;
}return this.base(arguments);
},
_applySource:function(value,
old){var imageMgr=qx.io.image.Manager.getInstance();
if(old){imageMgr.remove(old);
if(this._registeredAsVisible){imageMgr.hide(old);
delete this._registeredAsVisible;
}}
if(value){imageMgr.add(value);
if(this.isSeeable()){this._registeredAsVisible=true;
imageMgr.show(value);
}}
if(this.isCreated()){this._connect();
}},
_connect:function(){var aliasMgr=qx.io.Alias.getInstance();
aliasMgr.connect(this._syncSource,
this,
this.getSource());
},
_syncSource:function(value){if(value===null){this.setPreloader(null);
}else{var preloader=qx.io.image.PreloaderManager.getInstance().create(value);
this.setPreloader(preloader);
}},
_applyPreloader:function(value,
old){if(old){old.removeEventListener("load",
this._onload,
this);
old.removeEventListener("error",
this._onerror,
this);
}var imageMgr=qx.io.image.Manager.getInstance();
if(value){this.setLoaded(false);
if(value.isErroneous()){this._onerror();
}else if(value.isLoaded()){this.setLoaded(true);
}else{value.addEventListener("load",
this._onload,
this);
value.addEventListener("error",
this._onerror,
this);
}}else{this.setLoaded(false);
}},
_applyLoaded:function(value,
old){if(value&&this.isCreated()){this._renderContent();
}else if(!value){this._invalidatePreferredInnerWidth();
this._invalidatePreferredInnerHeight();
}},
_applyElement:function(value,
old){if(value){if(!this._image){try{if(qx.core.Variant.isSet("qx.client",
"webkit")){this._image=document.createElement("img");
}else{this._image=new Image;
}this._image.style.border="0 none";
this._image.style.verticalAlign="top";
this._image.alt="";
this._image.title="";
}catch(ex){this.error("Failed while creating image #1",
ex);
}
if(qx.core.Variant.isSet("qx.client",
"gecko|opera|webkit")){this._styleEnabled();
}}value.appendChild(this._image);
}this.base(arguments,
value,
old);
if(value&&this.getSource()){this._connect();
}},
_postApply:function(){this._postApplyDimensions();
this._updateContent();
},
_applyEnabled:function(value,
old){if(this._image){this._styleEnabled();
}return this.base(arguments,
value,
old);
},
_updateContent:qx.core.Variant.select("qx.client",
{"mshtml":function(){var i=this._image;
var pl=this.getPreloader();
var source=pl&&pl.isLoaded()?pl.getSource():this._blank;
if(pl&&pl.getIsPng()&&this.getEnabled()){i.src=this._blank;
i.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+source+"',sizingMethod='scale')";
}else{i.src=source;
i.style.filter=this.getEnabled()?"":"Gray() Alpha(Opacity=30)";
}},
"default":function(){var pl=this.getPreloader();
var source=pl&&pl.isLoaded()?pl.getSource():this._blank;
this._image.src=source;
}}),
_resetContent:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._image.src=this._blank;
this._image.style.filter="";
},
"default":function(){this._image.src=this._blank;
}}),
_styleEnabled:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._updateContent();
},
"default":function(){if(this._image){var o=this.getEnabled()===false?0.3:"";
var s=this._image.style;
s.opacity=s.KhtmlOpacity=s.MozOpacity=o;
}}}),
_computePreferredInnerWidth:function(){var preloader=this.getPreloader();
return preloader?preloader.getWidth():0;
},
_computePreferredInnerHeight:function(){var preloader=this.getPreloader();
return preloader?preloader.getHeight():0;
},
_renderContent:function(){this.base(arguments);
qx.ui.core.Widget.flushGlobalQueues();
},
_postApplyDimensions:qx.core.Variant.select("qx.client",
{"mshtml":function(){try{var vImageStyle=this._image.style;
if(this.getResizeToInner()){vImageStyle.pixelWidth=this.getInnerWidth();
vImageStyle.pixelHeight=this.getInnerHeight();
}else{vImageStyle.pixelWidth=this.getPreferredInnerWidth();
vImageStyle.pixelHeight=this.getPreferredInnerHeight();
}}catch(ex){this.error("postApplyDimensions failed",
ex);
}},
"default":function(){try{var vImageNode=this._image;
if(this.getResizeToInner()){vImageNode.width=this.getInnerWidth();
vImageNode.height=this.getInnerHeight();
}else{vImageNode.width=this.getPreferredInnerWidth();
vImageNode.height=this.getPreferredInnerHeight();
}}catch(ex){this.error("postApplyDimensions failed",
ex);
}}}),
_changeInnerWidth:qx.core.Variant.select("qx.client",
{"mshtml":function(vNew,
vOld){if(this.getResizeToInner()){this._image.style.pixelWidth=vNew;
}},
"default":function(vNew,
vOld){if(this.getResizeToInner()){this._image.width=vNew;
}}}),
_changeInnerHeight:qx.core.Variant.select("qx.client",
{"mshtml":function(vNew,
vOld){if(this.getResizeToInner()){this._image.style.pixelHeight=vNew;
}},
"default":function(vNew,
vOld){if(this.getResizeToInner()){this._image.height=vNew;
}}})},
destruct:function(){if(this._image){this._image.style.filter="";
}this._disposeFields("_image");
}});




/* ID: qx.ui.basic.Label */
qx.Class.define("qx.ui.basic.Label",
{extend:qx.ui.basic.Terminator,
construct:function(text){this.base(arguments);
if(text!=null){this.setText(text);
}this.initWidth();
this.initHeight();
this.initSelectable();
this.initCursor();
this.initWrap();
},
statics:{_getMeasureNode:function(){var node=this._measureNode;
if(!node){node=document.createElement("div");
var style=node.style;
style.width=style.height="auto";
style.visibility="hidden";
style.position="absolute";
style.zIndex="-1";
document.body.appendChild(node);
this._measureNode=node;
}return node;
}},
properties:{appearance:{refine:true,
init:"label"},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
allowStretchX:{refine:true,
init:false},
allowStretchY:{refine:true,
init:false},
selectable:{refine:true,
init:false},
cursor:{refine:true,
init:"default"},
text:{apply:"_applyText",
init:"",
dispose:true,
check:"Label"},
wrap:{check:"Boolean",
init:false,
nullable:true,
apply:"_applyWrap"},
textAlign:{check:["left",
"center",
"right",
"justify"],
nullable:true,
themeable:true,
apply:"_applyTextAlign"},
textOverflow:{check:"Boolean",
init:true},
mode:{check:["html",
"text",
"auto"],
init:"auto"}},
members:{_content:"",
_applyTextAlign:function(value,
old){value===null?this.removeStyleProperty("textAlign"):this.setStyleProperty("textAlign",
value);
},
_applyFont:function(value,
old){qx.theme.manager.Font.getInstance().connect(this._styleFont,
this,
value);
},
_styleFont:function(font){this._invalidatePreferredInnerDimensions();
font?font.render(this):qx.ui.core.Font.reset(this);
},
_applyTextColor:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._styleTextColor,
this,
value);
},
_styleTextColor:function(value){value?this.setStyleProperty("color",
value):this.removeStyleProperty("color");
},
_applyWrap:function(value,
old){value==null?this.removeStyleProperty("whiteSpace"):this.setStyleProperty("whiteSpace",
value?"normal":"nowrap");
},
_applyText:function(value,
old){this._syncText(this.getText());
},
_syncText:function(text){this._content=text;
if(this._isCreated){this._renderContent();
}},
_computeObjectNeededDimensions:function(){var element=this.self(arguments)._getMeasureNode();
var style=element.style;
var source=this._styleProperties;
style.fontFamily=source.fontFamily||"";
style.fontSize=source.fontSize||"";
style.fontWeight=source.fontWeight||"";
style.fontStyle=source.fontStyle||"";
element.innerHTML=this._content;
this._cachedPreferredInnerWidth=element.scrollWidth;
this._cachedPreferredInnerHeight=element.scrollHeight;
},
_computePreferredInnerWidth:function(){this._computeObjectNeededDimensions();
return this._cachedPreferredInnerWidth;
},
_computePreferredInnerHeight:function(){this._computeObjectNeededDimensions();
return this._cachedPreferredInnerHeight;
},
_postApply:function(){var html=this._content;
var element=this._getTargetNode();
if(html==null){element.innerHTML="";
}else{var style=element.style;
if(!this.getWrap()){if(this.getInnerWidth()<this.getPreferredInnerWidth()){style.overflow="hidden";
}else{style.overflow="";
}}element.innerHTML=html;
}}}});




/* ID: qx.ui.basic.HorizontalSpacer */
qx.Class.define("qx.ui.basic.HorizontalSpacer",
{extend:qx.ui.basic.Terminator,
construct:function(){this.base(arguments);
this.initWidth();
},
properties:{width:{refine:true,
init:"1*"}}});




/* ID: qx.ui.basic.Atom */
qx.Class.define("qx.ui.basic.Atom",
{extend:qx.ui.layout.BoxLayout,
construct:function(vLabel,
vIcon,
vIconWidth,
vIconHeight,
vFlash){this.base(arguments);
this.getLayoutImpl().setEnableFlexSupport(false);
if(vLabel!==undefined){this.setLabel(vLabel);
}if(qx.Class.isDefined("qx.ui.embed.Flash")&&vFlash!=null&&vIconWidth!=null&&vIconHeight!=null&&qx.ui.embed.Flash.getPlayerVersion().getMajor()>0){this._flashMode=true;
this.setIcon(vFlash);
}else if(vIcon!=null){this.setIcon(vIcon);
}
if(vIcon||vFlash){if(vIconWidth!=null){this.setIconWidth(vIconWidth);
}
if(vIconHeight!=null){this.setIconHeight(vIconHeight);
}}this.initWidth();
this.initHeight();
},
properties:{orientation:{refine:true,
init:"horizontal"},
allowStretchX:{refine:true,
init:false},
allowStretchY:{refine:true,
init:false},
appearance:{refine:true,
init:"atom"},
stretchChildrenOrthogonalAxis:{refine:true,
init:false},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
horizontalChildrenAlign:{refine:true,
init:"center"},
verticalChildrenAlign:{refine:true,
init:"middle"},
spacing:{refine:true,
init:4},
label:{apply:"_applyLabel",
nullable:true,
dispose:true,
check:"Label"},
icon:{check:"String",
apply:"_applyIcon",
nullable:true,
themeable:true},
disabledIcon:{check:"String",
apply:"_applyDisabledIcon",
nullable:true,
themeable:true},
show:{init:"both",
check:["both",
"label",
"icon",
"none"],
themeable:true,
nullable:true,
inheritable:true,
apply:"_applyShow",
event:"changeShow"},
iconPosition:{init:"left",
check:["top",
"right",
"bottom",
"left"],
themeable:true,
apply:"_applyIconPosition"},
iconWidth:{check:"Integer",
themeable:true,
apply:"_applyIconWidth",
nullable:true},
iconHeight:{check:"Integer",
themeable:true,
apply:"_applyIconHeight",
nullable:true}},
members:{_flashMode:false,
_labelObject:null,
_iconObject:null,
_createLabel:function(){var l=this._labelObject=new qx.ui.basic.Label(this.getLabel());
l.setAnonymous(true);
l.setCursor("default");
this.addAt(l,
this._iconObject?1:0);
},
_createIcon:function(){if(this._flashMode&&qx.Class.isDefined("qx.ui.embed.Flash")){var i=this._iconObject=new qx.ui.embed.Flash(this.getIcon());
}else{var i=this._iconObject=new qx.ui.basic.Image();
}i.setAnonymous(true);
var width=this.getIconWidth();
if(width!==null){this._iconObject.setWidth(width);
}var height=this.getIconWidth();
if(height!==null){this._iconObject.setHeight(height);
}this._updateIcon();
this.addAt(i,
0);
},
_updateIcon:function(){var icon=this.getIcon();
if(this._iconObject&&this.getIcon&&this.getDisabledIcon){var disabledIcon=this.getDisabledIcon();
if(disabledIcon){if(this.getEnabled()){icon?this._iconObject.setSource(icon):this._iconObject.resetSource();
}else{disabledIcon?this._iconObject.setSource(disabledIcon):this._iconObject.resetSource();
}this._iconObject.setEnabled(true);
}else{icon?this._iconObject.setSource(icon):this._iconObject.resetSource();
this._iconObject.resetEnabled();
}}},
getLabelObject:function(){return this._labelObject;
},
getIconObject:function(){return this._iconObject;
},
_applyIconPosition:function(value,
old){switch(value){case "top":case "bottom":this.setOrientation("vertical");
this.setReverseChildrenOrder(value=="bottom");
break;
default:this.setOrientation("horizontal");
this.setReverseChildrenOrder(value=="right");
break;
}},
_applyShow:function(value,
old){this._handleIcon();
this._handleLabel();
},
_applyLabel:function(value,
old){if(this._labelObject){value?this._labelObject.setText(value):this._labelObject.resetText();
}this._handleLabel();
},
_applyIcon:function(value,
old){this._updateIcon();
this._handleIcon();
},
_applyDisabledIcon:function(value,
old){this._updateIcon();
this._handleIcon();
},
_applyIconWidth:function(value,
old){if(this._iconObject){this._iconObject.setWidth(value);
}},
_applyIconHeight:function(value,
old){if(this._iconObject){this._iconObject.setHeight(value);
}},
_iconIsVisible:false,
_labelIsVisible:false,
_handleLabel:function(){switch(this.getShow()){case "label":case "both":case "inherit":this._labelIsVisible=!!this.getLabel();
break;
default:this._labelIsVisible=false;
}
if(this._labelIsVisible){this._labelObject?this._labelObject.setDisplay(true):this._createLabel();
}else if(this._labelObject){this._labelObject.setDisplay(false);
}},
_handleIcon:function(){switch(this.getShow()){case "icon":case "both":case "inherit":this._iconIsVisible=!!this.getIcon();
break;
default:this._iconIsVisible=false;
}
if(this._iconIsVisible){this._iconObject?this._iconObject.setDisplay(true):this._createIcon();
}else if(this._iconObject){this._iconObject.setDisplay(false);
}}},
destruct:function(){this._disposeObjects("_iconObject",
"_labelObject");
}});




/* ID: qx.ui.form.Button */
qx.Class.define("qx.ui.form.Button",
{extend:qx.ui.basic.Atom,
construct:function(vText,
vIcon,
vIconWidth,
vIconHeight,
vFlash){this.base(arguments,
vText,
vIcon,
vIconWidth,
vIconHeight,
vFlash);
this.initTabIndex();
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mouseout",
this._onmouseout);
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("mouseup",
this._onmouseup);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keyup",
this._onkeyup);
},
properties:{appearance:{refine:true,
init:"button"},
tabIndex:{refine:true,
init:1}},
members:{_onmouseover:function(e){if(e.getTarget()!=this){return;
}
if(this.hasState("abandoned")){this.removeState("abandoned");
this.addState("pressed");
}this.addState("over");
},
_onmouseout:function(e){if(e.getTarget()!=this){return;
}this.removeState("over");
if(this.hasState("pressed")){this.setCapture(true);
this.removeState("pressed");
this.addState("abandoned");
}},
_onmousedown:function(e){if(e.getTarget()!=this||!e.isLeftButtonPressed()){return;
}this.removeState("abandoned");
this.addState("pressed");
},
_onmouseup:function(e){this.setCapture(false);
var hasPressed=this.hasState("pressed");
var hasAbandoned=this.hasState("abandoned");
if(hasPressed){this.removeState("pressed");
}
if(hasAbandoned){this.removeState("abandoned");
}
if(!hasAbandoned){this.addState("over");
if(hasPressed){this.execute();
}}},
_onkeydown:function(e){switch(e.getKeyIdentifier()){case "Enter":case "Space":this.removeState("abandoned");
this.addState("pressed");
}},
_onkeyup:function(e){switch(e.getKeyIdentifier()){case "Enter":case "Space":if(this.hasState("pressed")){this.removeState("abandoned");
this.removeState("pressed");
this.execute();
}}}}});




/* ID: qx.log.appender.HtmlElement */
qx.Class.define("qx.log.appender.HtmlElement",
{extend:qx.log.appender.Abstract,
properties:{element:{check:"Element",
nullable:true,
apply:"_applyElement"},
maxMessages:{check:"Integer",
init:500},
useLongFormat:{refine:true,
init:false}},
members:{__backgroundColors:{0:"white",
200:"white",
500:"#F1FBF3",
600:"#FEF0D2",
700:"#FCE1D8",
800:"#FCE1D8",
1000:"white"},
_prepare:function(){if(!this._frame){this._frame=document.createElement("div");
}},
_applyElement:function(value,
old){this._prepare();
if(value){value.appendChild(this._frame);
}else if(old){old.removeChild(this._frame);
}},
clear:function(){if(this._frame){this._frame.innerHTML="";
}},
appendLogEvent:function(evt){var Logger=qx.log.Logger;
this._prepare();
var group=evt.logger.getName();
if(evt.instanceId!=null){group+="["+evt.instanceId+"]";
}
if(group!=this._lastGroup){var elem=document.createElement("div");
elem.style.fontWeight="bold";
elem.innerHTML=group;
this._frame.appendChild(elem);
this._lastGroup=group;
}var elem=document.createElement("div");
elem.style.backgroundColor=this.__backgroundColors[evt.level];
elem.innerHTML=this.formatLogEvent(evt).replace(/&/g,
"&amp;").replace(/</g,
"&lt;").replace(/  /g,
" &#160;").replace(/[\n]/g,
"<br>");
this._frame.appendChild(elem);
while(this._frame.childNodes.length>this.getMaxMessages()){this._frame.removeChild(this._frame.firstChild);
if(this._removedMessageCount==null){this._removedMessageCount=1;
}else{this._removedMessageCount++;
}}
if(this._removedMessageCount!=null){this._frame.firstChild.className="";
this._frame.firstChild.innerHTML="("+this._removedMessageCount+" messages removed)";
}}},
destruct:function(){this._disposeFields("_frame");
}});




/* ID: qx.ui.tree.AbstractTreeElement */
qx.Class.define("qx.ui.tree.AbstractTreeElement",
{type:"abstract",
extend:qx.ui.layout.BoxLayout,
construct:function(treeRowStructure){this._indentObject=treeRowStructure._indentObject;
this._iconObject=treeRowStructure._iconObject;
this._labelObject=treeRowStructure._labelObject;
this._indentObject.setAnonymous(true);
this._iconObject.setAnonymous(true);
this._labelObject.setAnonymous(true);
this._labelObject.setSelectable(false);
this._labelObject.setStyleProperty("lineHeight",
"100%");
this._labelObject.setMode("text");
this.base(arguments);
if(qx.util.Validation.isValid(treeRowStructure._label)){this.setLabel(treeRowStructure._label);
}this.initSelectable();
this.BASE_URI=qx.io.Alias.getInstance().resolve("widget/tree/");
for(var i=0;i<treeRowStructure._fields.length;i++){this.add(treeRowStructure._fields[i]);
}if(treeRowStructure._icons.unselected!==undefined){this.setIcon(treeRowStructure._icons.unselected);
this.setIconSelected(treeRowStructure._icons.unselected);
}else{this.initIcon();
}
if(treeRowStructure._icons.selected!==undefined){this.setIconSelected(treeRowStructure._icons.selected);
}
if((treeRowStructure._icons.selected===undefined)&&(treeRowStructure._icons.unselected!==undefined)){this.initIconSelected();
}this._iconObject.setAppearance("tree-element-icon");
this._labelObject.setAppearance("tree-element-label");
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("mouseup",
this._onmouseup);
},
properties:{orientation:{refine:true,
init:"horizontal"},
selectable:{refine:true,
init:false},
appearance:{refine:true,
init:"tree-element"},
icon:{check:"String",
nullable:true,
init:"icon/16/actions/document-new.png",
apply:"_applyIcon"},
iconSelected:{check:"String",
event:"iconSelected",
nullable:true,
init:null,
apply:"_applyIcon"},
label:{check:"Label",
apply:"_applyLabel",
dispose:true},
selected:{check:"Boolean",
init:false,
apply:"_applySelected",
event:"changeSelected"}},
members:{_applyLabel:function(value,
old){if(this._labelObject){this._labelObject.setText(value);
}},
_applyIcon:function(value,
old){var iconObject=this.getIconObject();
if(iconObject){var source=this._evalCurrentIcon();
if(!source){iconObject.setDisplay(false);
}else{iconObject.setDisplay(true);
iconObject.setSource(source);
}this.addToTreeQueue();
}},
_applySelected:function(value,
old){if(value){this.addState("selected");
this._labelObject.addState("selected");
}else{this.removeState("selected");
this._labelObject.removeState("selected");
}var vTree=this.getTree();
if(!vTree._fastUpdate||(old&&vTree._oldItem==this)){this._iconObject.setSource(this._evalCurrentIcon());
if(value){this._iconObject.addState("selected");
}else{this._iconObject.removeState("selected");
}}var vManager=this.getTree().getManager();
if(old&&vManager.getSelectedItem()==this){vManager.deselectAll();
}else if(value&&vManager.getSelectedItem()!=this){vManager.setSelectedItem(this);
}},
_getRowStructure:function(labelOrTreeRowStructure,
icon,
iconSelected){if(labelOrTreeRowStructure instanceof qx.ui.tree.TreeRowStructure){return labelOrTreeRowStructure;
}else{return qx.ui.tree.TreeRowStructure.getInstance().standard(labelOrTreeRowStructure,
icon,
iconSelected);
}},
_evalCurrentIcon:function(){if(this.getSelected()&&this.getIconSelected()){return this.getIconSelected();
}else{return this.getIcon();
}},
getParentFolder:function(){try{return this.getParent().getParent();
}catch(ex){}return null;
},
getLevel:function(){var vParentFolder=this.getParentFolder();
return vParentFolder?vParentFolder.getLevel()+1:null;
},
getTree:function(){var vParentFolder=this.getParentFolder();
return vParentFolder?vParentFolder.getTree():null;
},
getIndentObject:function(){return this._indentObject;
},
getIconObject:function(){return this._iconObject;
},
getLabelObject:function(){return this._labelObject;
},
destroy:function(){var manager=this.getTree()?this.getTree().getManager():null;
if(manager){if(manager.getItemSelected(this)){if(manager.getMultiSelection()){manager.setItemSelected(this,
false);
}else{manager.deselectAll();
}}if(manager.getLeadItem()==this){manager.setLeadItem(null);
}if(manager.getAnchorItem()==this){manager.setAnchorItem(null);
}}if(this.destroyContent){this.destroyContent();
}this.disconnect();
var parentFolder=this.getParentFolder();
if(parentFolder){parentFolder.remove(this);
}qx.client.Timer.once(function(){this.dispose();
},
this,
0);
},
getHierarchy:function(vArr){if(this._labelObject){vArr.unshift(this._labelObject.getText());
}var parent=this.getParentFolder();
if(parent){parent.getHierarchy(vArr);
}return vArr;
},
addToTreeQueue:function(){var vTree=this.getTree();
if(vTree){vTree.addChildToTreeQueue(this);
}},
removeFromTreeQueue:function(){var vTree=this.getTree();
if(vTree){vTree.removeChildFromTreeQueue(this);
}},
addToCustomQueues:function(vHint){this.addToTreeQueue();
this.base(arguments,
vHint);
},
removeFromCustomQueues:function(vHint){this.removeFromTreeQueue();
this.base(arguments,
vHint);
},
_applyParent:function(value,
old){this.base(arguments,
value,
old);
if(old&&!old.isDisplayable()&&old.getParent()&&old.getParent().isDisplayable()){old.getParent().addToTreeQueue();
}if(value&&!value.isDisplayable()&&value.getParent()&&value.getParent().isDisplayable()){value.getParent().addToTreeQueue();
}},
_handleDisplayableCustom:function(vDisplayable,
vParent,
vHint){this.base(arguments,
vDisplayable,
vParent,
vHint);
if(vHint){var vParentFolder=this.getParentFolder();
var vPreviousParentFolder=this._previousParentFolder;
if(vPreviousParentFolder){if(this._wasLastVisibleChild){vPreviousParentFolder._updateIndent();
}else if(!vPreviousParentFolder.hasContent()){vPreviousParentFolder.addToTreeQueue();
}}
if(vParentFolder&&vParentFolder.isDisplayable()&&vParentFolder._initialLayoutDone){vParentFolder.addToTreeQueue();
}
if(this.isLastVisibleChild()){var vPrev=this.getPreviousVisibleSibling();
if(vPrev&&vPrev instanceof qx.ui.tree.AbstractTreeElement){vPrev._updateIndent();
}}
if(vDisplayable){this._updateIndent();
}}},
_onmousedown:function(e){if(e._treeProcessed){return;
}this.getTree().getManager().handleMouseDown(this,
e);
e._treeProcessed=true;
},
_onmouseup:qx.lang.Function.returnTrue,
flushTree:function(){this._previousParentFolder=this.getParentFolder();
this._wasLastVisibleChild=this.isLastVisibleChild();
var vLevel=this.getLevel();
var vTree=this.getTree();
var vImage;
var vHtml=[];
var vCurrentObject=this;
var vMinLevel=0;
var vMaxLevel=vLevel;
if(vTree.getRootOpenClose()){vMaxLevel=vLevel+1;
}if(vTree.getHideNode()){vMinLevel=1;
}
for(var i=vMinLevel;i<vMaxLevel;i++){vImage=vCurrentObject.getIndentSymbol(vTree.getUseTreeLines(),
i,
vMinLevel,
vMaxLevel);
if(vImage){vHtml.push("<img style=\"position:absolute;top:0px;left:");
vHtml.push((vMaxLevel-i-1)*19);
vHtml.push("px\" src=\"");
vHtml.push(this.BASE_URI);
vHtml.push(vImage);
vHtml.push(".");
vHtml.push("gif");
vHtml.push("\" />");
}vCurrentObject=vCurrentObject.getParentFolder();
}this._indentObject.setHtml(vHtml.join(""));
this._indentObject.setWidth((vMaxLevel-vMinLevel)*19);
}},
destruct:function(){this._disposeObjects("_indentObject",
"_iconObject",
"_labelObject");
this._disposeFields("_previousParentFolder");
}});




/* ID: qx.ui.tree.TreeRowStructure */
qx.Class.define("qx.ui.tree.TreeRowStructure",
{type:"singleton",
extend:qx.core.Object,
construct:function(){this.base(arguments);
},
members:{newRow:function(){this._indentObject=new qx.ui.embed.HtmlEmbed;
this._iconObject=new qx.ui.basic.Image;
this._labelObject=new qx.ui.basic.Label;
this._fields=new Array;
this._icons=new Object;
this._fields.push(this._indentObject);
this._indentAdded=false;
this._iconAdded=false;
this._labelAdded=false;
return this;
},
standard:function(vLabel,
vIcon,
vIconSelected){this.newRow();
this.addIcon(vIcon,
vIconSelected);
this.addLabel(vLabel);
return this;
},
addIndent:function(){if(!this._indentAdded){this._fields.shift();
this._indentAdded=true;
}else{throw new Error("Indent object added more than once.");
}this._fields.push(this._indentObject);
},
addIcon:function(vIcon,
vIconSelected){if(!this._iconAdded){this._iconAdded=true;
}else{throw new Error("Icon object added more than once.");
}if(vIcon!==undefined){this._icons.unselected=vIcon;
}
if(vIconSelected!==undefined){this._icons.selected=vIconSelected;
}this._fields.push(this._iconObject);
},
addLabel:function(vLabel){if(!this._labelAdded){this._labelAdded=true;
}else{throw new Error("Label added more than once.");
}this._label=vLabel;
this._fields.push(this._labelObject);
},
addObject:function(vObj,
vAnonymous){if(typeof vAnonymous=="boolean"){vObj.setAnonymous(vAnonymous);
}this._fields.push(vObj);
},
getLabelObject:function(){return this._labelObject;
},
getIconObject:function(){return this._iconObject;
}},
destruct:function(){this._disposeFields("_icons");
this._disposeObjects('_indentObject',
'_iconObject',
'_labelObject');
this._disposeObjectDeep("_fields",
1);
}});




/* ID: qx.ui.embed.HtmlEmbed */
qx.Class.define("qx.ui.embed.HtmlEmbed",
{extend:qx.ui.basic.Terminator,
construct:function(vHtml){this.base(arguments);
if(vHtml!=null){this.setHtml(vHtml);
}},
properties:{html:{check:"String",
init:"",
apply:"_applyHtml",
event:"changeHtml"},
textAlign:{check:["left",
"center",
"right",
"justify"],
nullable:true,
themeable:true,
apply:"_applyTextAlign"},
font:{refine:true,
init:null},
textColor:{refine:true,
init:null}},
members:{_applyHtml:function(){if(this._isCreated){this._syncHtml();
}},
_applyTextAlign:function(value,
old){value===null?this.removeStyleProperty("textAlign"):this.setStyleProperty("textAlign",
value);
},
_applyFont:function(value,
old){qx.theme.manager.Font.getInstance().connect(this._styleFont,
this,
value);
},
_styleFont:function(value){value?value.render(this):qx.ui.core.Font.reset(this);
},
_applyTextColor:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._styleTextColor,
this,
value);
},
_styleTextColor:function(value){value?this.setStyleProperty("color",
value):this.removeStyleProperty("color");
},
_applyElementData:function(){this._syncHtml();
},
_syncHtml:function(){this.getElement().innerHTML=this.getHtml();
}}});




/* ID: qx.ui.tree.TreeFolder */
qx.Class.define("qx.ui.tree.TreeFolder",
{extend:qx.ui.tree.AbstractTreeElement,
construct:function(labelOrTreeRowStructure,
icon,
iconSelected){var treeRowStructure=this._getRowStructure(labelOrTreeRowStructure,
icon,
iconSelected);
this.base(arguments,
treeRowStructure);
this._treeRowStructureFields=treeRowStructure._fields;
this._iconObject.setAppearance("tree-folder-icon");
this._labelObject.setAppearance("tree-folder-label");
this.addEventListener("dblclick",
this._ondblclick);
this.add=this.addToFolder;
this.addBefore=this.addBeforeToFolder;
this.addAfter=this.addAfterToFolder;
this.addAt=this.addAtToFolder;
this.addAtBegin=this.addAtBeginToFolder;
this.addAtEnd=this.addAtEndToFolder;
},
events:{"treeOpenWithContent":"qx.event.type.DataEvent",
"treeOpenWhileEmpty":"qx.event.type.DataEvent",
"treeClose":"qx.event.type.DataEvent"},
properties:{appearance:{refine:true,
init:"tree-folder"},
icon:{refine:true,
init:"icon/16/places/folder.png"},
iconSelected:{refine:true,
init:"icon/16/status/folder-open.png"},
open:{check:"Boolean",
init:false,
apply:"_applyOpen",
event:"changeOpen"},
alwaysShowPlusMinusSymbol:{check:"Boolean",
init:false,
apply:"_applyAlwaysShowPlusMinusSymbol"}},
members:{hasContent:function(){return this._containerObject&&this._containerObject.getChildrenLength()>0;
},
open:function(){if(this.getOpen()){return;
}
if(this.hasContent()){if(this.getTree().hasEventListeners("treeOpenWithContent")){this.getTree().dispatchEvent(new qx.event.type.DataEvent("treeOpenWithContent",
this),
true);
}this.getTopLevelWidget().setGlobalCursor("progress");
qx.client.Timer.once(this._openCallback,
this,
0);
}else{if(this.getTree().hasEventListeners("treeOpenWhileEmpty")){this.getTree().dispatchEvent(new qx.event.type.DataEvent("treeOpenWhileEmpty",
this),
true);
}this.setOpen(true);
}},
close:function(){if(this.getTree().hasEventListeners("treeClose")){this.getTree().dispatchEvent(new qx.event.type.DataEvent("treeClose",
this),
true);
}this.setOpen(false);
},
toggle:function(){this.getOpen()?this.close():this.open();
},
_openCallback:function(){this.setOpen(true);
qx.ui.core.Widget.flushGlobalQueues();
this.getTopLevelWidget().setGlobalCursor(null);
},
_createChildrenStructure:function(){if(!(this instanceof qx.ui.tree.Tree)){this.setHeight("auto");
}this.setVerticalChildrenAlign("top");
if(!this._horizontalLayout){this.setOrientation("vertical");
this._horizontalLayout=new qx.ui.layout.HorizontalBoxLayout;
this._horizontalLayout.setWidth(null);
this._horizontalLayout.setParent(this);
this._horizontalLayout.setAnonymous(true);
this._horizontalLayout.setAppearance(this instanceof qx.ui.tree.Tree?"tree":"tree-folder");
for(var i=0;i<this._treeRowStructureFields.length;i++){this._treeRowStructureFields[i].setParent(this._horizontalLayout);
}this._treeRowStructureFields=null;
}
if(!this._containerObject){this._containerObject=new qx.ui.layout.VerticalBoxLayout;
this._containerObject.setWidth(null);
this._containerObject.setAnonymous(true);
this._containerObject.setDisplay(this.getOpen());
this._containerObject.setParent(this);
this.remapChildrenHandlingTo(this._containerObject);
}},
_handleChildMove:function(vChild,
vRelationIndex,
vRelationChild){if(vChild.isDisplayable()){var vChildren=this._containerObject.getChildren();
var vOldChildIndex=vChildren.indexOf(vChild);
if(vOldChildIndex!=-1){if(vRelationChild){vRelationIndex=vChildren.indexOf(vRelationChild);
}
if(vRelationIndex==vChildren.length-1){vChild._updateIndent();
this._containerObject.getLastVisibleChild()._updateIndent();
}else if(vChild._wasLastVisibleChild){vChild._updateIndent();
var vPreviousSibling=vChild.getPreviousVisibleSibling();
if(vPreviousSibling){vPreviousSibling._updateIndent();
}}}}},
addToFolder:function(varargs){this._createChildrenStructure();
if(this._containerObject){return this._containerObject.add.apply(this._containerObject,
arguments);
}},
addBeforeToFolder:function(vChild,
vBefore){this._createChildrenStructure();
if(this._containerObject){this._handleChildMove(vChild,
null,
vBefore);
return this._containerObject.addBefore.apply(this._containerObject,
arguments);
}},
addAfterToFolder:function(vChild,
vAfter){this._createChildrenStructure();
if(this._containerObject){this._handleChildMove(vChild,
null,
vAfter);
return this._containerObject.addAfter.apply(this._containerObject,
arguments);
}},
addAtToFolder:function(vChild,
vIndex){this._createChildrenStructure();
if(this._containerObject){this._handleChildMove(vChild,
vIndex);
return this._containerObject.addAt.apply(this._containerObject,
arguments);
}},
addAtBeginToFolder:function(vChild){return this.addAtToFolder(vChild,
0);
},
addAtEndToFolder:function(vChild){this._createChildrenStructure();
if(this._containerObject){var vLast=this._containerObject.getLastChild();
if(vLast){this._handleChildMove(vChild,
null,
vLast);
return this._containerObject.addAfter.call(this._containerObject,
vChild,
vLast);
}else{return this.addAtBeginToFolder(vChild);
}}},
_remappingChildTable:["remove",
"removeAt",
"removeAll"],
getContainerObject:function(){return this._containerObject;
},
getHorizontalLayout:function(){return this._horizontalLayout;
},
getFirstVisibleChildOfFolder:function(){if(this._containerObject){return this._containerObject.getFirstChild();
}},
getLastVisibleChildOfFolder:function(){if(this._containerObject){return this._containerObject.getLastChild();
}},
getItems:function(recursive,
invisible){var a=[this];
if(this._containerObject){var ch=invisible==true?this._containerObject.getChildren():this._containerObject.getVisibleChildren();
if(recursive==false){a=a.concat(ch);
}else{for(var i=0,
chl=ch.length;i<chl;i++){a=a.concat(ch[i].getItems(recursive,
invisible));
}}}return a;
},
destroyContent:function(){if(!this.hasContent()){return;
}var manager=this.getTree()?this.getTree().getManager():null;
var leadItem;
var anchorItem;
if(manager){leadItem=manager.getLeadItem();
anchorItem=manager.getAnchorItem();
}this._containerObject.setDisplay(true);
var items=this._containerObject.getChildren();
var item;
for(var i=items.length-1;i>=0;--i){item=items[i];
if(item!=this){if(manager){if(leadItem==item){manager.setLeadItem(null);
}if(anchorItem==item){manager.setAnchorItem(null);
}if(manager.getItemSelected(item)){if(manager.getMultiSelection()){manager.setItemSelected(item,
false);
}else{manager.deselectAll();
}}if(item.destroyContent){item.destroyContent();
}}item.removeFromTreeQueue();
item.disconnect();
this._containerObject.remove(item);
qx.client.Timer.once(function(){item.dispose();
delete items[i];
},
this,
0);
}}},
_applyOpen:function(value,
old){var tree=this.getTree();
if(tree&&tree.getExcludeSpecificTreeLines().length>0){this._updateIndent();
}else{this._updateLastColumn();
}
if(this._containerObject){this._containerObject.setDisplay(value);
}},
_applyAlwaysShowPlusMinusSymbol:function(value,
old){var t=this.getTree();
if(t){if(t.getExcludeSpecificTreeLines().length>0){this._updateIndent();
}else{this._updateLastColumn();
}}},
_updateLastColumn:function(){if(this._indentObject){var vElement=this._indentObject.getElement();
if(vElement&&vElement.firstChild){vElement.firstChild.src=this.BASE_URI+this.getIndentSymbol(this.getTree().getUseTreeLines(),
0,
0,
0)+".gif";
}}},
_onmousedown:function(e){if(e._treeProcessed){return;
}var vOriginalTarget=e.getOriginalTarget();
switch(vOriginalTarget){case this._indentObject:if(this._indentObject.getElement().firstChild==e.getDomTarget()){this.getTree().getManager().handleMouseDown(this,
e);
this.toggle();
}break;
case this._containerObject:break;
case this:if(this._containerObject){break;
}default:this.getTree().getManager().handleMouseDown(this,
e);
}e._treeProcessed=true;
},
_onmouseup:function(e){var vOriginalTarget=e.getOriginalTarget();
switch(vOriginalTarget){case this._indentObject:case this._containerObject:case this:break;
default:if(!this.getTree().getUseDoubleClick()){this.open();
}}},
_ondblclick:function(e){if(!this.getTree().getUseDoubleClick()){return;
}this.toggle();
e.stopPropagation();
},
getIndentSymbol:function(vUseTreeLines,
vColumn,
vFirstColumn,
vLastColumn){var vLevel=this.getLevel();
var vExcludeList=this.getTree().getExcludeSpecificTreeLines();
var vExclude=vExcludeList[vLastColumn-vColumn-1];
if(vColumn==vFirstColumn){if(this.hasContent()||this.getAlwaysShowPlusMinusSymbol()){if(!vUseTreeLines){return this.getOpen()?"minus":"plus";
}if(vLevel==1){var vParentFolder=this.getParentFolder();
if(vParentFolder&&!vParentFolder._horizontalLayout.getVisibility()&&this.isFirstChild()){if(this.isLastChild()||vExclude===true){return this.getOpen()?"only_minus":"only_plus";
}else{return this.getOpen()?"start_minus":"start_plus";
}}}
if(vExclude===true){return this.getOpen()?"only_minus":"only_plus";
}else if(this.isLastChild()){return this.getOpen()?"end_minus":"end_plus";
}else{return this.getOpen()?"cross_minus":"cross_plus";
}}else if(vUseTreeLines&&!(vExclude===true)){return this.isLastChild()?"end":"cross";
}}else{if(vUseTreeLines&&!this.isLastChild()){if(vExclude===true){return null;
}return "line";
}return null;
}},
_updateIndent:function(){qx.ui.tree.TreeFile.prototype._updateIndent.call(this);
if(!this._containerObject){return;
}var ch=this._containerObject.getVisibleChildren();
for(var i=0,
l=ch.length;i<l;i++){ch[i]._updateIndent();
}}},
destruct:function(){this._disposeFields('_treeRowStructureFields');
this._disposeObjects("_horizontalLayout",
"_containerObject");
}});




/* ID: qx.ui.tree.Tree */
qx.Class.define("qx.ui.tree.Tree",
{extend:qx.ui.tree.TreeFolder,
construct:function(labelOrTreeRowStructure,
icon,
iconSelected){this.base(arguments,
this._getRowStructure(labelOrTreeRowStructure,
icon,
iconSelected));
this._manager=new qx.ui.tree.SelectionManager(this);
this._iconObject.setAppearance("tree-icon");
this._labelObject.setAppearance("tree-label");
this.setOpen(true);
this.addToFolder();
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keypress",
this._onkeypress);
this.addEventListener("keyup",
this._onkeyup);
},
statics:{isTreeFolder:function(vObject){return (vObject&&vObject instanceof qx.ui.tree.TreeFolder&&!(vObject instanceof qx.ui.tree.Tree));
},
isOpenTreeFolder:function(vObject){return (vObject instanceof qx.ui.tree.TreeFolder&&vObject.getOpen()&&vObject.hasContent());
}},
properties:{useDoubleClick:{check:"Boolean",
init:false},
useTreeLines:{check:"Boolean",
init:true,
apply:"_applyUseTreeLines"},
tabIndex:{refine:true,
init:1},
excludeSpecificTreeLines:{check:"Array",
init:[],
apply:"_applyExcludeSpecificTreeLines"},
hideNode:{check:"Boolean",
init:false,
apply:"_applyHideNode"},
rootOpenClose:{check:"Boolean",
init:false,
apply:"_applyRootOpenClose"}},
members:{useDoubleClick:function(){return this.getUseDoubleClick();
},
useTreeLines:function(){return this.getUseTreeLines();
},
hideNode:function(){return this.getHideNode();
},
getManager:function(){return this._manager;
},
getSelectedElement:function(){return this.getManager().getSelectedItems()[0];
},
getItems:function(recursive,
invisible){var a=[];
if(!this.getHideNode()){a.push(this);
}
if(this._containerObject){var ch=invisible==true?this._containerObject.getChildren():this._containerObject.getVisibleChildren();
if(recursive==false){a=a.concat(ch);
}else{for(var i=0,
chl=ch.length;i<chl;i++){a=a.concat(ch[i].getItems(recursive,
invisible));
}}}return a;
},
addChildToTreeQueue:function(vChild){if(!vChild._isInTreeQueue&&!vChild._isDisplayable){this.debug("Ignoring invisible child: "+vChild);
}
if(!vChild._isInTreeQueue&&vChild._isDisplayable){qx.ui.core.Widget.addToGlobalWidgetQueue(this);
if(!this._treeQueue){this._treeQueue={};
}this._treeQueue[vChild.toHashCode()]=vChild;
vChild._isInTreeQueue=true;
}},
removeChildFromTreeQueue:function(vChild){if(vChild._isInTreeQueue){if(this._treeQueue){delete this._treeQueue[vChild.toHashCode()];
}delete vChild._isInTreeQueue;
}},
flushWidgetQueue:function(){this.flushTreeQueue();
},
flushTreeQueue:function(){if(!qx.lang.Object.isEmpty(this._treeQueue)){for(var vHashCode in this._treeQueue){this._treeQueue[vHashCode].flushTree();
delete this._treeQueue[vHashCode]._isInTreeQueue;
}delete this._treeQueue;
}},
_applyUseTreeLines:function(value,
old){if(this._initialLayoutDone){this._updateIndent();
}},
_applyHideNode:function(value,
old){if(!value){this._horizontalLayout.setHeight(this._horizontalLayout.originalHeight);
this._horizontalLayout.show();
}else{this._horizontalLayout.originalHeight=this._horizontalLayout.getHeight();
this._horizontalLayout.setHeight(0);
this._horizontalLayout.hide();
}
if(this._initialLayoutDone){this._updateIndent();
}},
_applyRootOpenClose:function(value,
old){if(this._initialLayoutDone){this._updateIndent();
}},
getExcludeSpecificTreeLines:function(){return qx.lang.Array.clone(this["__user$excludeSpecificTreeLines"]);
},
_applyExcludeSpecificTreeLines:function(value,
old){if(this._initialLayoutDone){this._updateIndent();
}},
getTree:function(){return this;
},
getParentFolder:function(){return null;
},
getLevel:function(){return 0;
},
_onkeydown:function(e){var vManager=this.getManager();
var vSelectedItem=vManager.getSelectedItem();
},
_onkeypress:function(e){var vManager=this.getManager();
var vSelectedItem=vManager.getSelectedItem();
switch(e.getKeyIdentifier()){case "Enter":e.preventDefault();
if(qx.ui.tree.Tree.isTreeFolder(vSelectedItem)){return vSelectedItem.toggle();
}break;
case "Left":e.preventDefault();
if(qx.ui.tree.Tree.isTreeFolder(vSelectedItem)){if(!vSelectedItem.getOpen()){var vParent=vSelectedItem.getParentFolder();
if(vParent instanceof qx.ui.tree.TreeFolder){if(!(vParent instanceof qx.ui.tree.Tree)){vParent.close();
}this.setSelectedElement(vParent);
}}else{return vSelectedItem.close();
}}else if(vSelectedItem instanceof qx.ui.tree.TreeFile){var vParent=vSelectedItem.getParentFolder();
if(vParent instanceof qx.ui.tree.TreeFolder){if(!(vParent instanceof qx.ui.tree.Tree)){vParent.close();
}this.setSelectedElement(vParent);
}}break;
case "Right":e.preventDefault();
if(qx.ui.tree.Tree.isTreeFolder(vSelectedItem)){if(!vSelectedItem.getOpen()){return vSelectedItem.open();
}else if(vSelectedItem.hasContent()){var vFirst=vSelectedItem.getFirstVisibleChildOfFolder();
this.setSelectedElement(vFirst);
if(vFirst instanceof qx.ui.tree.TreeFolder){vFirst.open();
}return;
}}break;
default:if(!this._fastUpdate){this._fastUpdate=true;
this._oldItem=vSelectedItem;
}vManager.handleKeyPress(e);
}},
_onkeyup:function(e){if(this._fastUpdate){var vNewItem=this.getManager().getSelectedItem();
if(!vNewItem){return;
}vNewItem.getIconObject().addState("selected");
delete this._fastUpdate;
delete this._oldItem;
}},
getLastTreeChild:function(){var vLast=this;
while(vLast instanceof qx.ui.tree.AbstractTreeElement){if(!(vLast instanceof qx.ui.tree.TreeFolder)||!vLast.getOpen()){return vLast;
}vLast=vLast.getLastVisibleChildOfFolder();
}return null;
},
getFirstTreeChild:function(){return this;
},
setSelectedElement:function(vElement){var vManager=this.getManager();
vManager.setSelectedItem(vElement);
vManager.setLeadItem(vElement);
},
getHierarchy:function(vArr){if(!this.hideNode()&&this._labelObject){vArr.unshift(this._labelObject.getText());
}return vArr;
},
getIndentSymbol:function(vUseTreeLines,
vColumn,
vLastColumn){if(vColumn==vLastColumn&&(this.hasContent()||this.getAlwaysShowPlusMinusSymbol())){if(!vUseTreeLines){return this.getOpen()?"minus":"plus";
}else{return this.getOpen()?"only_minus":"only_plus";
}}else{return null;
}}},
destruct:function(){this._disposeObjects("_manager");
}});




/* ID: qx.ui.selection.SelectionManager */
qx.Class.define("qx.ui.selection.SelectionManager",
{extend:qx.core.Target,
construct:function(vBoundedWidget){this.base(arguments);
this._selectedItems=new qx.ui.selection.Selection(this);
if(vBoundedWidget!=null){this.setBoundedWidget(vBoundedWidget);
}},
events:{"changeSelection":"qx.event.type.DataEvent"},
properties:{boundedWidget:{check:"qx.ui.core.Widget",
nullable:true},
multiSelection:{check:"Boolean",
init:true},
dragSelection:{check:"Boolean",
init:true},
canDeselect:{check:"Boolean",
init:true},
fireChange:{check:"Boolean",
init:true},
anchorItem:{check:"Object",
nullable:true,
apply:"_applyAnchorItem",
event:"changeAnchorItem"},
leadItem:{check:"Object",
nullable:true,
apply:"_applyLeadItem",
event:"changeLeadItem"},
multiColumnSupport:{check:"Boolean",
init:false}},
members:{_applyAnchorItem:function(value,
old){if(old){this.renderItemAnchorState(old,
false);
}
if(value){this.renderItemAnchorState(value,
true);
}},
_applyLeadItem:function(value,
old){if(old){this.renderItemLeadState(old,
false);
}
if(value){this.renderItemLeadState(value,
true);
}},
_getFirst:function(){return this.getBoundedWidget().getFirstVisibleChild();
},
_getLast:function(){return this.getBoundedWidget().getLastVisibleChild();
},
getFirst:function(){var vItem=this._getFirst();
if(vItem){return vItem.getEnabled()?vItem:this.getNext(vItem);
}},
getLast:function(){var vItem=this._getLast();
if(vItem){return vItem.getEnabled()?vItem:this.getPrevious(vItem);
}},
getItems:function(){return this.getBoundedWidget().getChildren();
},
getNextSibling:function(vItem){return vItem.getNextSibling();
},
getPreviousSibling:function(vItem){return vItem.getPreviousSibling();
},
getNext:function(vItem){while(vItem){vItem=this.getNextSibling(vItem);
if(!vItem){break;
}
if(this.getItemEnabled(vItem)){return vItem;
}}return null;
},
getPrevious:function(vItem){while(vItem){vItem=this.getPreviousSibling(vItem);
if(!vItem){break;
}
if(this.getItemEnabled(vItem)){return vItem;
}}return null;
},
isBefore:function(vItem1,
vItem2){var cs=this.getItems();
return cs.indexOf(vItem1)<cs.indexOf(vItem2);
},
isEqual:function(vItem1,
vItem2){return vItem1==vItem2;
},
getItemHashCode:function(vItem){return vItem.toHashCode();
},
scrollItemIntoView:function(vItem,
vTopLeft){vItem.scrollIntoView(vTopLeft);
},
getItemLeft:function(vItem){return vItem.getOffsetLeft();
},
getItemTop:function(vItem){return vItem.getOffsetTop();
},
getItemWidth:function(vItem){return vItem.getOffsetWidth();
},
getItemHeight:function(vItem){return vItem.getOffsetHeight();
},
getItemEnabled:function(vItem){return vItem.getEnabled();
},
renderItemSelectionState:function(vItem,
vIsSelected){vIsSelected?vItem.addState("selected"):vItem.removeState("selected");
if(vItem.handleStateChange){vItem.handleStateChange();
}},
renderItemAnchorState:function(vItem,
vIsAnchor){vIsAnchor?vItem.addState("anchor"):vItem.removeState("anchor");
if(vItem.handleStateChange!=null){vItem.handleStateChange();
}},
renderItemLeadState:function(vItem,
vIsLead){vIsLead?vItem.addState("lead"):vItem.removeState("lead");
if(vItem.handleStateChange!=null){vItem.handleStateChange();
}},
getItemSelected:function(vItem){return this._selectedItems.contains(vItem);
},
setItemSelected:function(vItem,
vSelected){var hc=this.getItemHashCode(vItem);
switch(this.getMultiSelection()){case true:if(!this.getItemEnabled(vItem)){return;
}if(this.getItemSelected(vItem)==vSelected){return;
}this.renderItemSelectionState(vItem,
vSelected);
vSelected?this._selectedItems.add(vItem):this._selectedItems.remove(vItem);
this._dispatchChange();
break;
case false:var item0=this.getSelectedItems()[0];
if(vSelected){var old=item0;
if(this.isEqual(vItem,
old)){return;
}if(old!=null){this.renderItemSelectionState(old,
false);
}this.renderItemSelectionState(vItem,
true);
this._selectedItems.removeAll();
this._selectedItems.add(vItem);
this._dispatchChange();
}else{if(!this.isEqual(item0,
vItem)){this.renderItemSelectionState(vItem,
false);
this._selectedItems.removeAll();
this._dispatchChange();
}}break;
}},
getSelectedItems:function(){return this._selectedItems.toArray();
},
getSelectedItem:function(){return this._selectedItems.getFirst();
},
setSelectedItems:function(vItems){var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
this._deselectAll();
var vItem;
var vItemLength=vItems.length;
for(var i=0;i<vItemLength;i++){vItem=vItems[i];
if(!this.getItemEnabled(vItem)){continue;
}this._selectedItems.add(vItem);
this.renderItemSelectionState(vItem,
true);
}this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
setSelectedItem:function(vItem){if(!vItem){return;
}
if(!this.getItemEnabled(vItem)){return;
}var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
this._deselectAll();
this._selectedItems.add(vItem);
this.renderItemSelectionState(vItem,
true);
this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
selectAll:function(){var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
this._selectAll();
this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
_selectAll:function(){if(!this.getMultiSelection()){return;
}var vItem;
var vItems=this.getItems();
var vItemsLength=vItems.length;
this._selectedItems.removeAll();
for(var i=0;i<vItemsLength;i++){vItem=vItems[i];
if(!this.getItemEnabled(vItem)){continue;
}this._selectedItems.add(vItem);
this.renderItemSelectionState(vItem,
true);
}return true;
},
deselectAll:function(){var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
this._deselectAll();
this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal))this._dispatchChange();
},
_deselectAll:function(){var items=this._selectedItems.toArray();
for(var i=0;i<items.length;i++){this.renderItemSelectionState(items[i],
false);
}this._selectedItems.removeAll();
return true;
},
selectItemRange:function(vItem1,
vItem2){var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
this._selectItemRange(vItem1,
vItem2,
true);
this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
_selectItemRange:function(vItem1,
vItem2,
vDeselect){if(this.isBefore(vItem2,
vItem1)){return this._selectItemRange(vItem2,
vItem1,
vDeselect);
}if(vDeselect){this._deselectAll();
}var vCurrentItem=vItem1;
while(vCurrentItem!=null){if(this.getItemEnabled(vCurrentItem)){this._selectedItems.add(vCurrentItem);
this.renderItemSelectionState(vCurrentItem,
true);
}if(this.isEqual(vCurrentItem,
vItem2)){break;
}vCurrentItem=this.getNext(vCurrentItem);
}return true;
},
_deselectItemRange:function(vItem1,
vItem2){if(this.isBefore(vItem2,
vItem1)){return this._deselectItemRange(vItem2,
vItem1);
}var vCurrentItem=vItem1;
while(vCurrentItem!=null){this._selectedItems.remove(vCurrentItem);
this.renderItemSelectionState(vCurrentItem,
false);
if(this.isEqual(vCurrentItem,
vItem2)){break;
}vCurrentItem=this.getNext(vCurrentItem);
}},
_activeDragSession:false,
handleMouseDown:function(vItem,
e){if(!e.isLeftButtonPressed()&&!e.isRightButtonPressed()){return;
}if(e.isRightButtonPressed()&&this.getItemSelected(vItem)){return;
}if(e.isShiftPressed()||this.getDragSelection()||(!this.getItemSelected(vItem)&&!e.isCtrlPressed())){this._onmouseevent(vItem,
e);
}else{this.setLeadItem(vItem);
}this._activeDragSession=this.getDragSelection();
if(this._activeDragSession){this.getBoundedWidget().addEventListener("mouseup",
this._ondragup,
this);
this.getBoundedWidget().setCapture(true);
}},
_ondragup:function(e){this.getBoundedWidget().removeEventListener("mouseup",
this._ondragup,
this);
this.getBoundedWidget().setCapture(false);
this._activeDragSession=false;
},
handleMouseUp:function(vItem,
e){if(!e.isLeftButtonPressed()){return;
}
if(e.isCtrlPressed()||this.getItemSelected(vItem)&&!this._activeDragSession){this._onmouseevent(vItem,
e);
}
if(this._activeDragSession){this._activeDragSession=false;
this.getBoundedWidget().setCapture(false);
}},
handleMouseOver:function(oItem,
e){if(!this.getDragSelection()||!this._activeDragSession){return;
}this._onmouseevent(oItem,
e,
true);
},
handleClick:function(vItem,
e){},
handleDblClick:function(vItem,
e){},
_onmouseevent:function(oItem,
e,
bOver){if(!this.getItemEnabled(oItem)){return;
}var oldVal=this._getChangeValue();
var oldLead=this.getLeadItem();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
var selectedItems=this.getSelectedItems();
var selectedCount=selectedItems.length;
this.setLeadItem(oItem);
var currentAnchorItem=this.getAnchorItem();
var vCtrlKey=e.isCtrlPressed();
var vShiftKey=e.isShiftPressed();
if(!currentAnchorItem||selectedCount==0||(vCtrlKey&&!vShiftKey&&this.getMultiSelection()&&!this.getDragSelection())){this.setAnchorItem(oItem);
currentAnchorItem=oItem;
}if((!vCtrlKey&&!vShiftKey&&!this._activeDragSession||!this.getMultiSelection())){if(!this.getItemEnabled(oItem)){return;
}this._deselectAll();
this.setAnchorItem(oItem);
if(this._activeDragSession){this.scrollItemIntoView((this.getBoundedWidget().getScrollTop()>(this.getItemTop(oItem)-1)?this.getPrevious(oItem):this.getNext(oItem))||oItem);
}
if(!this.getItemSelected(oItem)){this.renderItemSelectionState(oItem,
true);
}this._selectedItems.add(oItem);
this._addToCurrentSelection=true;
}else if(this._activeDragSession&&bOver){if(oldLead){this._deselectItemRange(currentAnchorItem,
oldLead);
}if(this.isBefore(currentAnchorItem,
oItem)){if(this._addToCurrentSelection){this._selectItemRange(currentAnchorItem,
oItem,
false);
}else{this._deselectItemRange(currentAnchorItem,
oItem);
}}else{if(this._addToCurrentSelection){this._selectItemRange(oItem,
currentAnchorItem,
false);
}else{this._deselectItemRange(oItem,
currentAnchorItem);
}}this.scrollItemIntoView((this.getBoundedWidget().getScrollTop()>(this.getItemTop(oItem)-1)?this.getPrevious(oItem):this.getNext(oItem))||oItem);
}else if(this.getMultiSelection()&&vCtrlKey&&!vShiftKey){if(!this._activeDragSession){this._addToCurrentSelection=!(this.getCanDeselect()&&this.getItemSelected(oItem));
}this.setItemSelected(oItem,
this._addToCurrentSelection);
this.setAnchorItem(oItem);
}else if(this.getMultiSelection()&&vCtrlKey&&vShiftKey){if(!this._activeDragSession){this._addToCurrentSelection=!(this.getCanDeselect()&&this.getItemSelected(oItem));
}
if(this._addToCurrentSelection){this._selectItemRange(currentAnchorItem,
oItem,
false);
}else{this._deselectItemRange(currentAnchorItem,
oItem);
}}else if(this.getMultiSelection()&&!vCtrlKey&&vShiftKey){if(this.getCanDeselect()){this._selectItemRange(currentAnchorItem,
oItem,
true);
}else{if(oldLead){this._deselectItemRange(currentAnchorItem,
oldLead);
}this._selectItemRange(currentAnchorItem,
oItem,
false);
}}this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
handleKeyDown:function(vDomEvent){this.warn("qx.ui.selection.SelectionManager.handleKeyDown is deprecated! "+"Use keypress insted and bind it to the onkeypress event.");
this.handleKeyPress(vDomEvent);
},
handleKeyPress:function(vDomEvent){var oldVal=this._getChangeValue();
var oldFireChange=this.getFireChange();
this.setFireChange(false);
if(vDomEvent.getKeyIdentifier()=="A"&&vDomEvent.isCtrlPressed()){if(this.getMultiSelection()){this._selectAll();
this.setLeadItem(this.getFirst());
}}else{var aIndex=this.getAnchorItem();
var itemToSelect=this.getItemToSelect(vDomEvent);
if(itemToSelect&&this.getItemEnabled(itemToSelect)){this.setLeadItem(itemToSelect);
this.scrollItemIntoView(itemToSelect);
vDomEvent.preventDefault();
if(vDomEvent.isShiftPressed()&&this.getMultiSelection()){if(aIndex==null){this.setAnchorItem(itemToSelect);
}this._selectItemRange(this.getAnchorItem(),
itemToSelect,
true);
}else if(!vDomEvent.isCtrlPressed()){this._deselectAll();
this.renderItemSelectionState(itemToSelect,
true);
this._selectedItems.add(itemToSelect);
this.setAnchorItem(itemToSelect);
}else if(vDomEvent.getKeyIdentifier()=="Space"){if(this._selectedItems.contains(itemToSelect)){this.renderItemSelectionState(itemToSelect,
false);
this._selectedItems.remove(itemToSelect);
this.setAnchorItem(this._selectedItems.getFirst());
}else{if(!vDomEvent.isCtrlPressed()||!this.getMultiSelection()){this._deselectAll();
}this.renderItemSelectionState(itemToSelect,
true);
this._selectedItems.add(itemToSelect);
this.setAnchorItem(itemToSelect);
}}}}this.setFireChange(oldFireChange);
if(oldFireChange&&this._hasChanged(oldVal)){this._dispatchChange();
}},
getItemToSelect:function(vKeyboardEvent){if(vKeyboardEvent.isAltPressed()){return null;
}switch(vKeyboardEvent.getKeyIdentifier()){case "Home":return this.getHome(this.getLeadItem());
case "End":return this.getEnd(this.getLeadItem());
case "Down":return this.getDown(this.getLeadItem());
case "Up":return this.getUp(this.getLeadItem());
case "Left":return this.getLeft(this.getLeadItem());
case "Right":return this.getRight(this.getLeadItem());
case "PageUp":return this.getPageUp(this.getLeadItem())||this.getHome(this.getLeadItem());
case "PageDown":return this.getPageDown(this.getLeadItem())||this.getEnd(this.getLeadItem());
case "Space":if(vKeyboardEvent.isCtrlPressed()){return this.getLeadItem();
}}return null;
},
_dispatchChange:function(){if(!this.getFireChange()){return;
}
if(this.hasEventListeners("changeSelection")){this.dispatchEvent(new qx.event.type.DataEvent("changeSelection",
this.getSelectedItems()),
true);
}},
_hasChanged:function(sOldValue){return sOldValue!=this._getChangeValue();
},
_getChangeValue:function(){return this._selectedItems.getChangeValue();
},
getHome:function(){return this.getFirst();
},
getEnd:function(){return this.getLast();
},
getDown:function(vItem){if(!vItem){return this.getFirst();
}return this.getMultiColumnSupport()?(this.getUnder(vItem)||this.getLast()):this.getNext(vItem);
},
getUp:function(vItem){if(!vItem){return this.getLast();
}return this.getMultiColumnSupport()?(this.getAbove(vItem)||this.getFirst()):this.getPrevious(vItem);
},
getLeft:function(vItem){if(!this.getMultiColumnSupport()){return null;
}return !vItem?this.getLast():this.getPrevious(vItem);
},
getRight:function(vItem){if(!this.getMultiColumnSupport()){return null;
}return !vItem?this.getFirst():this.getNext(vItem);
},
getAbove:function(vItem){throw new Error("getAbove(): Not implemented yet");
},
getUnder:function(vItem){throw new Error("getUnder(): Not implemented yet");
},
getPageUp:function(vItem){var vBoundedWidget=this.getBoundedWidget();
var vParentScrollTop=vBoundedWidget.getScrollTop();
var vParentClientHeight=vBoundedWidget.getClientHeight();
var newItem;
var nextItem=this.getLeadItem();
if(!nextItem){nextItem=this.getFirst();
}var tryLoops=0;
while(tryLoops<2){while(nextItem&&(this.getItemTop(nextItem)-this.getItemHeight(nextItem)>=vParentScrollTop)){nextItem=this.getUp(nextItem);
}if(nextItem==null){break;
}if(nextItem!=this.getLeadItem()){this.scrollItemIntoView(nextItem,
true);
break;
}vBoundedWidget.setScrollTop(vParentScrollTop-vParentClientHeight-this.getItemHeight(nextItem));
vParentScrollTop=vBoundedWidget.getScrollTop();
tryLoops++;
}return nextItem;
},
getPageDown:function(vItem){var vBoundedWidget=this.getBoundedWidget();
var vParentScrollTop=vBoundedWidget.getScrollTop();
var vParentClientHeight=vBoundedWidget.getClientHeight();
var newItem;
var nextItem=this.getLeadItem();
if(!nextItem){nextItem=this.getFirst();
}var tryLoops=0;
while(tryLoops<2){while(nextItem&&((this.getItemTop(nextItem)+(2*this.getItemHeight(nextItem)))<=(vParentScrollTop+vParentClientHeight))){nextItem=this.getDown(nextItem);
}if(nextItem==null){break;
}if(nextItem!=this.getLeadItem()){break;
}vBoundedWidget.setScrollTop(vParentScrollTop+vParentClientHeight-2*this.getItemHeight(nextItem));
vParentScrollTop=vBoundedWidget.getScrollTop();
tryLoops++;
}return nextItem;
}},
destruct:function(){this._disposeObjects("_selectedItems");
}});




/* ID: qx.ui.selection.Selection */
qx.Class.define("qx.ui.selection.Selection",
{extend:qx.core.Object,
construct:function(mgr){this.base(arguments);
this.__manager=mgr;
this.removeAll();
},
members:{add:function(item){this.__storage[this.getItemHashCode(item)]=item;
},
remove:function(item){delete this.__storage[this.getItemHashCode(item)];
},
removeAll:function(){this.__storage={};
},
contains:function(item){return this.getItemHashCode(item) in this.__storage;
},
toArray:function(){var res=[];
for(var key in this.__storage){res.push(this.__storage[key]);
}return res;
},
getFirst:function(){for(var key in this.__storage){return this.__storage[key];
}return null;
},
getChangeValue:function(){var sb=[];
for(var key in this.__storage){sb.push(key);
}sb.sort();
return sb.join(";");
},
getItemHashCode:function(item){return this.__manager.getItemHashCode(item);
},
isEmpty:function(){return qx.lang.Object.isEmpty(this.__storage);
}},
destruct:function(){this._disposeFields("__storage",
"__manager");
}});




/* ID: qx.ui.tree.SelectionManager */
qx.Class.define("qx.ui.tree.SelectionManager",
{extend:qx.ui.selection.SelectionManager,
construct:function(vBoundedWidget){this.base(arguments,
vBoundedWidget);
},
properties:{multiSelection:{refine:true,
init:false},
dragSelection:{refine:true,
init:false}},
members:{_getFirst:function(){return qx.lang.Array.getFirst(this.getItems());
},
_getLast:function(){return qx.lang.Array.getLast(this.getItems());
},
getItems:function(){return this.getBoundedWidget().getItems();
},
getNext:function(vItem){if(vItem){if(qx.ui.tree.Tree.isOpenTreeFolder(vItem)){return vItem.getFirstVisibleChildOfFolder();
}else if(vItem.isLastVisibleChild()){var vCurrent=vItem;
while(vCurrent&&vCurrent.isLastVisibleChild()){vCurrent=vCurrent.getParentFolder();
}
if(vCurrent&&vCurrent instanceof qx.ui.tree.AbstractTreeElement&&vCurrent.getNextVisibleSibling()&&vCurrent.getNextVisibleSibling() instanceof qx.ui.tree.AbstractTreeElement){return vCurrent.getNextVisibleSibling();
}}else{return vItem.getNextVisibleSibling();
}}else{return this.getBoundedWidget().getFirstTreeChild();
}},
getPrevious:function(vItem){if(vItem){if(vItem==this.getBoundedWidget()){return;
}else if(vItem.isFirstVisibleChild()){if(vItem.getParentFolder() instanceof qx.ui.tree.TreeFolder){if(vItem.getParentFolder() instanceof qx.ui.tree.Tree&&vItem.getParentFolder().getHideNode()){return vItem;
}return vItem.getParentFolder();
}}else{var vPrev=vItem.getPreviousVisibleSibling();
while(vPrev instanceof qx.ui.tree.AbstractTreeElement){if(qx.ui.tree.Tree.isOpenTreeFolder(vPrev)){vPrev=vPrev.getLastVisibleChildOfFolder();
}else{break;
}}return vPrev;
}}else{return this.getBoundedWidget().getLastTreeChild();
}},
getItemTop:function(vItem){var vBoundedWidget=this.getBoundedWidget();
var vElement=vItem.getElement();
var vOffset=0;
while(vElement&&vElement.qx_Widget!=vBoundedWidget){vOffset+=vElement.offsetTop;
vElement=vElement.parentNode;
}return vOffset;
},
getItemHeight:function(vItem){if(vItem instanceof qx.ui.tree.TreeFolder&&vItem._horizontalLayout){return vItem._horizontalLayout.getOffsetHeight();
}else{return vItem.getOffsetHeight();
}},
scrollItemIntoView:function(vItem){if(vItem instanceof qx.ui.tree.TreeFolder&&vItem._horizontalLayout){return vItem._horizontalLayout.scrollIntoView();
}else{return vItem.scrollIntoView();
}},
renderItemSelectionState:function(treeNode,
isSelected){if(isSelected&&!treeNode.isSeeable()){var treeFolder=treeNode;
var parentFolders=[];
while(treeFolder){treeFolder=treeFolder.getParentFolder();
parentFolders.push(treeFolder);
}parentFolders.pop();
while(parentFolders.length){parentFolders.pop().open();
}}
if(isSelected){if(treeNode.isCreated()){this.scrollItemIntoView(treeNode);
}else{treeNode.addEventListener("appear",
function(e){this.scrollItemIntoView(treeNode);
},
this);
}}treeNode.setSelected(isSelected);
}}});




/* ID: qx.ui.tree.TreeFile */
qx.Class.define("qx.ui.tree.TreeFile",
{extend:qx.ui.tree.AbstractTreeElement,
construct:function(labelOrTreeRowStructure,
icon,
iconSelected){this.base(arguments,
this._getRowStructure(labelOrTreeRowStructure,
icon,
iconSelected));
},
members:{getIndentSymbol:function(vUseTreeLines,
vColumn,
vFirstColumn,
vLastColumn){var vLevel=this.getLevel();
var vExcludeList=this.getTree().getExcludeSpecificTreeLines();
var vExclude=vExcludeList[vLastColumn-vColumn-1];
if(vUseTreeLines&&!(vExclude===true)){if(vColumn==vFirstColumn){return this.isLastChild()?"end":"cross";
}else{return "line";
}}return null;
},
_updateIndent:function(){this.addToTreeQueue();
},
getItems:function(){return [this];
}}});




/* ID: qx.ui.popup.PopupAtom */
qx.Class.define("qx.ui.popup.PopupAtom",
{extend:qx.ui.popup.Popup,
construct:function(vLabel,
vIcon){this.base(arguments);
this._atom=new qx.ui.basic.Atom(vLabel,
vIcon);
this._atom.setParent(this);
},
members:{_isFocusRoot:false,
getAtom:function(){return this._atom;
}},
destruct:function(){this._disposeObjects("_atom");
}});




/* ID: qx.ui.popup.ToolTip */
qx.Class.define("qx.ui.popup.ToolTip",
{extend:qx.ui.popup.PopupAtom,
construct:function(vLabel,
vIcon){this.base(arguments,
vLabel,
vIcon);
this.setStyleProperty("filter",
"progid:DXImageTransform.Microsoft.Shadow(color='Gray', Direction=135, Strength=4)");
this._showTimer=new qx.client.Timer(this.getShowInterval());
this._showTimer.addEventListener("interval",
this._onshowtimer,
this);
this._hideTimer=new qx.client.Timer(this.getHideInterval());
this._hideTimer.addEventListener("interval",
this._onhidetimer,
this);
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mouseout",
this._onmouseover);
},
properties:{appearance:{refine:true,
init:"tool-tip"},
hideOnHover:{check:"Boolean",
init:true},
mousePointerOffsetX:{check:"Integer",
init:1},
mousePointerOffsetY:{check:"Integer",
init:20},
showInterval:{check:"Integer",
init:1000,
apply:"_applyShowInterval"},
hideInterval:{check:"Integer",
init:4000,
apply:"_applyHideInterval"},
boundToWidget:{check:"qx.ui.core.Widget",
apply:"_applyBoundToWidget"}},
members:{_minZIndex:1e7,
_applyHideInterval:function(value,
old){this._hideTimer.setInterval(value);
},
_applyShowInterval:function(value,
old){this._showTimer.setInterval(value);
},
_applyBoundToWidget:function(value,
old){if(value){this.setParent(value.getTopLevelWidget());
}else if(old){this.setParent(null);
}},
_beforeAppear:function(){this.base(arguments);
this._stopShowTimer();
this._startHideTimer();
},
_beforeDisappear:function(){this.base(arguments);
this._stopHideTimer();
},
_afterAppear:function(){this.base(arguments);
if(this.getRestrictToPageOnOpen()){var doc=qx.ui.core.ClientDocument.getInstance();
var docWidth=doc.getClientWidth();
var docHeight=doc.getClientHeight();
var restrictToPageLeft=parseInt(this._restrictToPageLeft);
var restrictToPageRight=parseInt(this._restrictToPageRight);
var restrictToPageTop=parseInt(this._restrictToPageTop);
var restrictToPageBottom=parseInt(this._restrictToPageBottom);
var left=(this._wantedLeft==null)?this.getLeft():this._wantedLeft;
var top=this.getTop();
var width=this.getBoxWidth();
var height=this.getBoxHeight();
var mouseX=qx.event.type.MouseEvent.getPageX();
var mouseY=qx.event.type.MouseEvent.getPageY();
var oldLeft=this.getLeft();
var oldTop=top;
if(left+width>docWidth-restrictToPageRight){left=docWidth-restrictToPageRight-width;
}
if(top+height>docHeight-restrictToPageBottom){top=docHeight-restrictToPageBottom-height;
}
if(left<restrictToPageLeft){left=restrictToPageLeft;
}
if(top<restrictToPageTop){top=restrictToPageTop;
}if(left<=mouseX&&mouseX<=left+width&&top<=mouseY&&mouseY<=top+height){var deltaYdown=mouseY-top;
var deltaYup=deltaYdown-height;
var deltaXright=mouseX-left;
var deltaXleft=deltaXright-width;
var violationUp=Math.max(0,
restrictToPageTop-(top+deltaYup));
var violationDown=Math.max(0,
top+height+deltaYdown-(docHeight-restrictToPageBottom));
var violationLeft=Math.max(0,
restrictToPageLeft-(left+deltaXleft));
var violationRight=Math.max(0,
left+width+deltaXright-(docWidth-restrictToPageRight));
var possibleMovements=[[0,
deltaYup,
violationUp],
[0,
deltaYdown,
violationDown],
[deltaXleft,
0,
violationLeft],
[deltaXright,
0,
violationRight]];
possibleMovements.sort(function(a,
b){return a[2]-b[2]||(Math.abs(a[0])+Math.abs(a[1]))-(Math.abs(b[0])+Math.abs(b[1]));
});
var minimalNonClippingMovement=possibleMovements[0];
left=left+minimalNonClippingMovement[0];
top=top+minimalNonClippingMovement[1];
}
if(left!=oldLeft||top!=oldTop){var self=this;
window.setTimeout(function(){self.setLeft(left);
self.setTop(top);
},
0);
}}},
_startShowTimer:function(){if(!this._showTimer.getEnabled()){this._showTimer.start();
}},
_startHideTimer:function(){if(!this._hideTimer.getEnabled()){this._hideTimer.start();
}},
_stopShowTimer:function(){if(this._showTimer.getEnabled()){this._showTimer.stop();
}},
_stopHideTimer:function(){if(this._hideTimer.getEnabled()){this._hideTimer.stop();
}},
_onmouseover:function(e){if(this.getHideOnHover()){this.hide();
}},
_onshowtimer:function(e){this.setLeft(qx.event.type.MouseEvent.getPageX()+this.getMousePointerOffsetX());
this.setTop(qx.event.type.MouseEvent.getPageY()+this.getMousePointerOffsetY());
this.show();
},
_onhidetimer:function(e){return this.hide();
}},
destruct:function(){this._disposeObjects("_showTimer",
"_hideTimer");
}});




/* ID: qx.ui.popup.ToolTipManager */
qx.Class.define("qx.ui.popup.ToolTipManager",
{type:"singleton",
extend:qx.util.manager.Object,
properties:{currentToolTip:{check:"qx.ui.popup.ToolTip",
nullable:true,
apply:"_applyCurrentToolTip"}},
members:{_applyCurrentToolTip:function(value,
old){if(old&&old.contains(value)){return;
}if(old&&!old.isDisposed()){old.hide();
old._stopShowTimer();
old._stopHideTimer();
}if(value){value._startShowTimer();
}},
handleMouseOver:function(e){var vTarget=e.getTarget();
var vToolTip;
if(!(vTarget instanceof qx.ui.core.Widget)&&vTarget.nodeType==1){vTarget=qx.event.handler.EventHandler.getTargetObject(vTarget);
}while(vTarget!=null&&!(vToolTip=vTarget.getToolTip())){vTarget=vTarget.getParent();
}if(vToolTip!=null){vToolTip.setBoundToWidget(vTarget);
}this.setCurrentToolTip(vToolTip);
},
handleMouseOut:function(e){var vTarget=e.getTarget();
var vRelatedTarget=e.getRelatedTarget();
var vToolTip=this.getCurrentToolTip();
if(vToolTip&&(vRelatedTarget==vToolTip||vToolTip.contains(vRelatedTarget))){return;
}if(vRelatedTarget&&vTarget&&vTarget.contains(vRelatedTarget)){return;
}if(vToolTip&&!vRelatedTarget){this.setCurrentToolTip(null);
}},
handleFocus:function(e){var vTarget=e.getTarget();
var vToolTip=vTarget.getToolTip();
if(vToolTip!=null){vToolTip.setBoundToWidget(vTarget);
this.setCurrentToolTip(vToolTip);
}},
handleBlur:function(e){var vTarget=e.getTarget();
if(!vTarget){return;
}var vToolTip=this.getCurrentToolTip();
if(vToolTip&&vToolTip==vTarget.getToolTip()){this.setCurrentToolTip(null);
}}}});




/* ID: qx.ui.pageview.AbstractBar */
qx.Class.define("qx.ui.pageview.AbstractBar",
{type:"abstract",
extend:qx.ui.layout.BoxLayout,
construct:function(){this.base(arguments);
this._manager=new qx.ui.selection.RadioManager;
this.addEventListener("mousewheel",
this._onmousewheel);
},
members:{getManager:function(){return this._manager;
},
_lastDate:(new Date(0)).valueOf(),
_onmousewheel:function(e){var vDate=(new Date).valueOf();
if((vDate-50)<this._lastDate){return;
}this._lastDate=vDate;
var vManager=this.getManager();
var vItems=vManager.getEnabledItems();
var vPos=vItems.indexOf(vManager.getSelected());
if(this.getWheelDelta(e)>0){var vNext=vItems[vPos+1];
if(!vNext){vNext=vItems[0];
}}else if(vPos>0){var vNext=vItems[vPos-1];
if(!vNext){vNext=vItems[0];
}}else{vNext=vItems[vItems.length-1];
}vManager.setSelected(vNext);
},
getWheelDelta:function(e){return e.getWheelDelta();
}},
destruct:function(){this._disposeObjects("_manager");
}});




/* ID: qx.ui.selection.RadioManager */
qx.Class.define("qx.ui.selection.RadioManager",
{extend:qx.core.Target,
construct:function(vName,
vMembers){this.base(arguments);
this._items=[];
this.setName(vName!=null?vName:qx.ui.selection.RadioManager.AUTO_NAME_PREFIX+this.toHashCode());
if(vMembers!=null){this.add.apply(this,
vMembers);
}},
statics:{AUTO_NAME_PREFIX:"qx-radio-"},
properties:{selected:{nullable:true,
apply:"_applySelected",
event:"changeSelected",
check:"qx.core.Object"},
name:{check:"String",
nullable:true,
apply:"_applyName"}},
members:{getItems:function(){return this._items;
},
getEnabledItems:function(){var b=[];
for(var i=0,
a=this._items,
l=a.length;i<l;i++){if(a[i].getEnabled()){b.push(a[i]);
}}return b;
},
handleItemChecked:function(vItem,
vChecked){if(vChecked){this.setSelected(vItem);
}else if(this.getSelected()==vItem){this.setSelected(null);
}},
add:function(varargs){var vItems=arguments;
var vLength=vItems.length;
var vItem;
for(var i=0;i<vLength;i++){vItem=vItems[i];
if(qx.lang.Array.contains(this._items,
vItem)){return;
}this._items.push(vItem);
vItem.setManager(this);
if(vItem.getChecked()){this.setSelected(vItem);
}vItem.setName(this.getName());
}},
remove:function(vItem){qx.lang.Array.remove(this._items,
vItem);
vItem.setManager(null);
if(vItem.getChecked()){this.setSelected(null);
}},
_applySelected:function(value,
old){if(old){old.setChecked(false);
}
if(value){value.setChecked(true);
}},
_applyName:function(value,
old){for(var i=0,
vItems=this._items,
vLength=vItems.length;i<vLength;i++){vItems[i].setName(value);
}},
selectNext:function(vItem){var vIndex=this._items.indexOf(vItem);
if(vIndex==-1){return;
}var i=0;
var vLength=this._items.length;
vIndex=(vIndex+1)%vLength;
while(i<vLength&&!this._items[vIndex].getEnabled()){vIndex=(vIndex+1)%vLength;
i++;
}this._selectByIndex(vIndex);
},
selectPrevious:function(vItem){var vIndex=this._items.indexOf(vItem);
if(vIndex==-1){return;
}var i=0;
var vLength=this._items.length;
vIndex=(vIndex-1+vLength)%vLength;
while(i<vLength&&!this._items[vIndex].getEnabled()){vIndex=(vIndex-1+vLength)%vLength;
i++;
}this._selectByIndex(vIndex);
},
_selectByIndex:function(vIndex){if(this._items[vIndex].getEnabled()){this.setSelected(this._items[vIndex]);
this._items[vIndex].setFocused(true);
}}},
destruct:function(){this._disposeObjectDeep("_items",
1);
}});




/* ID: qx.ui.pageview.tabview.Bar */
qx.Class.define("qx.ui.pageview.tabview.Bar",
{extend:qx.ui.pageview.AbstractBar,
construct:function(){this.base(arguments);
this.initZIndex();
this.initHeight();
},
properties:{appearance:{refine:true,
init:"tab-view-bar"},
zIndex:{refine:true,
init:2},
height:{refine:true,
init:"auto"}}});




/* ID: qx.ui.pageview.AbstractButton */
qx.Class.define("qx.ui.pageview.AbstractButton",
{type:"abstract",
extend:qx.ui.basic.Atom,
construct:function(vText,
vIcon,
vIconWidth,
vIconHeight,
vFlash){this.base(arguments,
vText,
vIcon,
vIconWidth,
vIconHeight,
vFlash);
this.initChecked();
this.initTabIndex();
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mouseout",
this._onmouseout);
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keypress",
this._onkeypress);
},
properties:{tabIndex:{refine:true,
init:1},
checked:{check:"Boolean",
init:false,
apply:"_applyChecked",
event:"changeChecked"},
page:{check:"qx.ui.pageview.AbstractPage",
apply:"_applyPage",
nullable:true},
manager:{check:"qx.ui.selection.RadioManager",
nullable:true,
apply:"_applyManager"},
name:{check:"String",
apply:"_applyName"}},
members:{getView:function(){var pa=this.getParent();
return pa?pa.getParent():null;
},
_applyManager:function(value,
old){if(old){old.remove(this);
}
if(value){value.add(this);
}},
_applyParent:function(value,
old){this.base(arguments,
value,
old);
if(old){old.getManager().remove(this);
}
if(value){value.getManager().add(this);
}},
_applyPage:function(value,
old){if(old){old.setButton(null);
}
if(value){value.setButton(this);
this.getChecked()?value.show():value.hide();
}},
_applyChecked:function(value,
old){if(this._hasParent){var vManager=this.getManager();
if(vManager){vManager.handleItemChecked(this,
value);
}}value?this.addState("checked"):this.removeState("checked");
var vPage=this.getPage();
if(vPage){this.getChecked()?vPage.show():vPage.hide();
}},
_applyName:function(value,
old){if(this.getManager()){this.getManager().setName(value);
}},
_onmousedown:function(e){this.setChecked(true);
},
_onmouseover:function(e){this.addState("over");
},
_onmouseout:function(e){this.removeState("over");
},
_onkeydown:function(e){},
_onkeypress:function(e){}}});




/* ID: qx.ui.pageview.tabview.Button */
qx.Class.define("qx.ui.pageview.tabview.Button",
{extend:qx.ui.pageview.AbstractButton,
events:{"closetab":"qx.event.type.Event"},
properties:{appearance:{refine:true,
init:"tab-view-button"},
showCloseButton:{check:"Boolean",
init:false,
apply:"_applyShowCloseButton",
event:"changeShowCloseButton"},
closeButtonImage:{check:"String",
init:"icon/16/actions/dialog-cancel.png",
apply:"_applyCloseButtonImage"}},
members:{_onkeydown:function(e){var identifier=e.getKeyIdentifier();
if(identifier=="Enter"||identifier=="Space"){this.setChecked(true);
}},
_onkeypress:function(e){switch(e.getKeyIdentifier()){case "Left":var vPrev=this.getPreviousActiveSibling();
if(vPrev&&vPrev!=this){delete qx.event.handler.FocusHandler.mouseFocus;
vPrev.setFocused(true);
vPrev.setChecked(true);
}break;
case "Right":var vNext=this.getNextActiveSibling();
if(vNext&&vNext!=this){delete qx.event.handler.FocusHandler.mouseFocus;
vNext.setFocused(true);
vNext.setChecked(true);
}break;
}},
_ontabclose:function(e){this.createDispatchDataEvent("closetab",
this);
e.stopPropagation();
},
_applyChecked:function(value,
old){this.base(arguments,
value,
old);
this.setZIndex(value?1:0);
},
_applyShowCloseButton:function(value,
old){if(!this._closeButtonImage){this._closeButtonImage=new qx.ui.basic.Image(this.getCloseButtonImage());
}
if(value){this._closeButtonImage.addEventListener("click",
this._ontabclose,
this);
this.add(this._closeButtonImage);
}else{this.remove(this._closeButtonImage);
this._closeButtonImage.removeEventListener("click",
this._ontabclose);
}},
_applyCloseButtonImage:function(value,
old){if(this._closeButtonImage){this._closeButtonImage.setSource(value);
}},
_renderAppearance:function(){if(this.getView()){this.isFirstVisibleChild()?this.addState("firstChild"):this.removeState("lastChild");
this.isLastVisibleChild()?this.addState("lastChild"):this.removeState("lastChild");
this.getView().getAlignTabsToLeft()?this.addState("alignLeft"):this.removeState("alignLeft");
!this.getView().getAlignTabsToLeft()?this.addState("alignRight"):this.removeState("alignRight");
this.getView().getPlaceBarOnTop()?this.addState("barTop"):this.removeState("barTop");
!this.getView().getPlaceBarOnTop()?this.addState("barBottom"):this.removeState("barBottom");
}this.base(arguments);
}},
destruct:function(){this._disposeObjects("_closeButtonImage");
}});




/* ID: qx.ui.pageview.AbstractPage */
qx.Class.define("qx.ui.pageview.AbstractPage",
{type:"abstract",
extend:qx.ui.layout.CanvasLayout,
construct:function(vButton){this.base(arguments);
if(vButton!==undefined){this.setButton(vButton);
}this.initTop();
this.initRight();
this.initBottom();
this.initLeft();
},
properties:{top:{refine:true,
init:0},
right:{refine:true,
init:0},
bottom:{refine:true,
init:0},
left:{refine:true,
init:0},
display:{refine:true,
init:false},
button:{check:"qx.ui.pageview.AbstractButton",
apply:"_applyButton"}},
members:{_applyButton:function(value,
old){if(old){old.setPage(null);
}
if(value){value.setPage(this);
}}}});




/* ID: qx.ui.pageview.tabview.Page */
qx.Class.define("qx.ui.pageview.tabview.Page",
{extend:qx.ui.pageview.AbstractPage,
properties:{appearance:{refine:true,
init:"tab-view-page"}}});




/* ID: qx.ui.pageview.AbstractPane */
qx.Class.define("qx.ui.pageview.AbstractPane",
{type:"abstract",
extend:qx.ui.layout.CanvasLayout});




/* ID: qx.ui.pageview.tabview.Pane */
qx.Class.define("qx.ui.pageview.tabview.Pane",
{extend:qx.ui.pageview.AbstractPane,
construct:function(){this.base(arguments);
this.initZIndex();
this.initHeight();
},
properties:{appearance:{refine:true,
init:"tab-view-pane"},
zIndex:{refine:true,
init:1},
height:{refine:true,
init:"1*"}}});




/* ID: qx.ui.pageview.AbstractPageView */
qx.Class.define("qx.ui.pageview.AbstractPageView",
{type:"abstract",
extend:qx.ui.layout.BoxLayout,
construct:function(vBarClass,
vPaneClass){this.base(arguments);
this._bar=new vBarClass;
this._pane=new vPaneClass;
this.add(this._bar,
this._pane);
},
members:{getPane:function(){return this._pane;
},
getBar:function(){return this._bar;
}},
destruct:function(){this._disposeObjects("_bar",
"_pane");
}});




/* ID: qx.ui.pageview.tabview.TabView */
qx.Class.define("qx.ui.pageview.tabview.TabView",
{extend:qx.ui.pageview.AbstractPageView,
construct:function(){this.base(arguments,
qx.ui.pageview.tabview.Bar,
qx.ui.pageview.tabview.Pane);
},
properties:{appearance:{refine:true,
init:"tab-view"},
orientation:{refine:true,
init:"vertical"},
alignTabsToLeft:{check:"Boolean",
init:true,
apply:"_applyAlignTabsToLeft"},
placeBarOnTop:{check:"Boolean",
init:true,
apply:"_applyPlaceBarOnTop"}},
members:{_applyAlignTabsToLeft:function(value,
old){var vBar=this._bar;
vBar.setHorizontalChildrenAlign(value?"left":"right");
vBar._addChildrenToStateQueue();
},
_applyPlaceBarOnTop:function(value,
old){var vBar=this._bar;
if(value){vBar.moveSelfToBegin();
}else{vBar.moveSelfToEnd();
}vBar._addChildrenToStateQueue();
}}});




/* ID: qx.ui.toolbar.Button */
qx.Class.define("qx.ui.toolbar.Button",
{extend:qx.ui.form.Button,
properties:{tabIndex:{refine:true,
init:-1},
appearance:{refine:true,
init:"toolbar-button"},
show:{refine:true,
init:"inherit"},
height:{refine:true,
init:null},
allowStretchY:{refine:true,
init:true}},
members:{_onkeydown:qx.lang.Function.returnTrue,
_onkeyup:qx.lang.Function.returnTrue}});




/* ID: qx.ui.toolbar.CheckBox */
qx.Class.define("qx.ui.toolbar.CheckBox",
{extend:qx.ui.toolbar.Button,
construct:function(vText,
vIcon,
vChecked){this.base(arguments,
vText,
vIcon);
if(vChecked!=null){this.setChecked(vChecked);
}},
properties:{checked:{check:"Boolean",
init:false,
apply:"_applyChecked",
event:"changeChecked"}},
members:{_applyChecked:function(value,
old){value?this.addState("checked"):this.removeState("checked");
},
_onmouseup:function(e){this.setCapture(false);
if(!this.hasState("abandoned")){this.addState("over");
this.setChecked(!this.getChecked());
this.execute();
}this.removeState("abandoned");
this.removeState("pressed");
e.stopPropagation();
}}});




/* ID: qx.ui.toolbar.MenuButton */
qx.Class.define("qx.ui.toolbar.MenuButton",
{extend:qx.ui.toolbar.Button,
construct:function(vText,
vMenu,
vIcon,
vIconWidth,
vIconHeight,
vFlash){this.base(arguments,
vText,
vIcon,
vIconWidth,
vIconHeight,
vFlash);
if(vMenu!=null){this.setMenu(vMenu);
}},
properties:{menu:{check:"qx.ui.menu.Menu",
nullable:true,
apply:"_applyMenu",
event:"changeMenu"},
direction:{check:["up",
"down"],
init:"down",
event:"changeDirection"}},
members:{getParentToolBar:function(){var vParent=this.getParent();
if(vParent instanceof qx.ui.toolbar.Part){vParent=vParent.getParent();
}return vParent instanceof qx.ui.toolbar.ToolBar?vParent:null;
},
_showMenu:function(vFromKeyEvent){var vMenu=this.getMenu();
if(vMenu){var vMenuParent=vMenu.getParent();
var vMenuParentElement=vMenuParent.getElement();
var vButtonElement=this.getElement();
var vButtonHeight=qx.html.Dimension.getBoxHeight(vButtonElement);
var vMenuParentLeft=qx.html.Location.getPageBoxLeft(vMenuParentElement);
var vButtonLeft=qx.html.Location.getPageBoxLeft(vButtonElement);
var vScrollLeft=qx.html.Scroll.getLeftSum(vButtonElement);
vMenu.setLeft(vButtonLeft-vMenuParentLeft-vScrollLeft);
var vScrollTop=qx.html.Scroll.getTopSum(vButtonElement);
switch(this.getDirection()){case "up":var vBodyHeight=qx.html.Dimension.getInnerHeight(document.body);
var vMenuParentBottom=qx.html.Location.getPageBoxBottom(vMenuParentElement);
var vButtonBottom=qx.html.Location.getPageBoxBottom(vButtonElement);
vMenu.setBottom(vButtonHeight+(vBodyHeight-vButtonBottom)-(vBodyHeight-vMenuParentBottom)-vScrollTop);
vMenu.setTop(null);
break;
case "down":var vButtonTop=qx.html.Location.getPageBoxTop(vButtonElement);
vMenu.setTop(vButtonTop+vButtonHeight-vScrollTop);
vMenu.setBottom(null);
break;
}this.addState("pressed");
if(vFromKeyEvent){vMenu.setHoverItem(vMenu.getFirstActiveChild());
}vMenu.show();
}},
_hideMenu:function(){var vMenu=this.getMenu();
if(vMenu){vMenu.hide();
}},
_applyMenu:function(value,
old){if(old){old.setOpener(null);
old.removeEventListener("appear",
this._onmenuappear,
this);
old.removeEventListener("disappear",
this._onmenudisappear,
this);
}
if(value){value.setOpener(this);
value.addEventListener("appear",
this._onmenuappear,
this);
value.addEventListener("disappear",
this._onmenudisappear,
this);
}},
_onmousedown:function(e){if(e.getTarget()!=this||!e.isLeftButtonPressed()){return;
}this.hasState("pressed")?this._hideMenu():this._showMenu();
},
_onmouseup:function(e){},
_onmouseout:function(e){if(e.getTarget()!=this){return;
}this.removeState("over");
},
_onmouseover:function(e){var vToolBar=this.getParentToolBar();
if(vToolBar){var vMenu=this.getMenu();
switch(vToolBar.getOpenMenu()){case null:case vMenu:break;
default:qx.ui.menu.Manager.getInstance().update();
this._showMenu();
}}return this.base(arguments,
e);
},
_onmenuappear:function(e){var vToolBar=this.getParentToolBar();
if(!vToolBar){return;
}var vMenu=this.getMenu();
vToolBar.setOpenMenu(vMenu);
},
_onmenudisappear:function(e){var vToolBar=this.getParentToolBar();
if(!vToolBar){return;
}var vMenu=this.getMenu();
if(vToolBar.getOpenMenu()==vMenu){vToolBar.setOpenMenu(null);
}}}});




/* ID: qx.ui.toolbar.Part */
qx.Class.define("qx.ui.toolbar.Part",
{extend:qx.ui.layout.HorizontalBoxLayout,
construct:function(){this.base(arguments);
this._handle=new qx.ui.toolbar.PartHandle;
this.add(this._handle);
this.initWidth();
},
properties:{appearance:{refine:true,
init:"toolbar-part"},
width:{refine:true,
init:"auto"},
show:{init:"inherit",
check:["both",
"label",
"icon",
"none"],
nullable:true,
inheritable:true,
event:"changeShow"}},
destruct:function(){this._disposeObjects("_handle");
}});




/* ID: qx.ui.toolbar.PartHandle */
qx.Class.define("qx.ui.toolbar.PartHandle",
{extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
var l=new qx.ui.basic.Terminator;
l.setAppearance("toolbar-part-handle-line");
this.add(l);
},
properties:{appearance:{refine:true,
init:"toolbar-part-handle"}}});




/* ID: qx.ui.toolbar.ToolBar */
qx.Class.define("qx.ui.toolbar.ToolBar",
{extend:qx.ui.layout.HorizontalBoxLayout,
construct:function(){this.base(arguments);
this.addEventListener("keypress",
this._onkeypress);
this.initHeight();
},
properties:{appearance:{refine:true,
init:"toolbar"},
height:{refine:true,
init:"auto"},
openMenu:{check:"qx.ui.menu.Menu",
event:"changeOpenMenu",
nullable:true},
show:{init:"both",
check:["both",
"label",
"icon",
"none"],
nullable:true,
inheritable:true,
event:"changeShow"}},
members:{getAllButtons:function(){var vChildren=this.getChildren();
var vLength=vChildren.length;
var vDeepChildren=[];
var vCurrent;
for(var i=0;i<vLength;i++){vCurrent=vChildren[i];
if(vCurrent instanceof qx.ui.toolbar.MenuButton){vDeepChildren.push(vCurrent);
}else if(vCurrent instanceof qx.ui.toolbar.Part){vDeepChildren=vDeepChildren.concat(vCurrent.getChildren());
}}return vDeepChildren;
},
_onkeypress:function(e){switch(e.getKeyIdentifier()){case "Left":return this._onkeypress_left();
case "Right":return this._onkeypress_right();
}},
_onkeypress_left:function(){var vMenu=this.getOpenMenu();
if(!vMenu){return;
}var vOpener=vMenu.getOpener();
if(!vOpener){return;
}var vChildren=this.getAllButtons();
var vChildrenLength=vChildren.length;
var vIndex=vChildren.indexOf(vOpener);
var vCurrent;
var vPrevButton=null;
for(var i=vIndex-1;i>=0;i--){vCurrent=vChildren[i];
if(vCurrent instanceof qx.ui.toolbar.MenuButton&&vCurrent.getEnabled()){vPrevButton=vCurrent;
break;
}}if(!vPrevButton){for(var i=vChildrenLength-1;i>vIndex;i--){vCurrent=vChildren[i];
if(vCurrent instanceof qx.ui.toolbar.MenuButton&&vCurrent.getEnabled()){vPrevButton=vCurrent;
break;
}}}
if(vPrevButton){qx.ui.menu.Manager.getInstance().update();
vPrevButton._showMenu(true);
}},
_onkeypress_right:function(){var vMenu=this.getOpenMenu();
if(!vMenu){return;
}var vOpener=vMenu.getOpener();
if(!vOpener){return;
}var vChildren=this.getAllButtons();
var vChildrenLength=vChildren.length;
var vIndex=vChildren.indexOf(vOpener);
var vCurrent;
var vNextButton=null;
for(var i=vIndex+1;i<vChildrenLength;i++){vCurrent=vChildren[i];
if(vCurrent instanceof qx.ui.toolbar.MenuButton&&vCurrent.getEnabled()){vNextButton=vCurrent;
break;
}}if(!vNextButton){for(var i=0;i<vIndex;i++){vCurrent=vChildren[i];
if(vCurrent instanceof qx.ui.toolbar.MenuButton&&vCurrent.getEnabled()){vNextButton=vCurrent;
break;
}}}
if(vNextButton){qx.ui.menu.Manager.getInstance().update();
vNextButton._showMenu(true);
}}}});




/* ID: qx.ui.menu.Manager */
qx.Class.define("qx.ui.menu.Manager",
{type:"singleton",
extend:qx.util.manager.Object,
construct:function(){this.base(arguments);
},
members:{update:function(vTarget,
vEventName){var vMenu,
vHashCode;
var vAll=this.getAll();
for(vHashCode in vAll){vMenu=vAll[vHashCode];
if(!vMenu.getAutoHide()){continue;
}
if(vTarget&&vTarget.getMenu&&vTarget.getMenu()){continue;
}if(!vTarget){vMenu.hide();
continue;
}var isMouseDown=vEventName=="mousedown";
var isMouseUp=vEventName=="mouseup";
if(vMenu.getOpener()!==
vTarget&&
(vTarget&&
(!vMenu.isSubElement(vTarget)&&isMouseDown)||
(vMenu.isSubElement(vTarget,
true)&&isMouseUp)||(!isMouseDown&&!isMouseUp))){vMenu.hide();
continue;
}}}}});




/* ID: qx.ui.toolbar.RadioButton */
qx.Class.define("qx.ui.toolbar.RadioButton",
{extend:qx.ui.toolbar.CheckBox,
properties:{manager:{check:"qx.ui.selection.RadioManager",
apply:"_applyManager",
nullable:true},
name:{check:"String",
event:"changeName"},
disableUncheck:{check:"Boolean",
init:false}},
members:{_applyChecked:function(value,
old){this.base(arguments,
value,
old);
var vManager=this.getManager();
if(vManager){vManager.handleItemChecked(this,
value);
}},
_applyManager:function(value,
old){if(old){old.remove(this);
}
if(value){value.add(this);
}},
_onmouseup:function(e){this.setCapture(false);
if(!this.hasState("abandoned")){this.addState("over");
this.setChecked(this.getDisableUncheck()||!this.getChecked());
this.execute();
}this.removeState("abandoned");
this.removeState("pressed");
e.stopPropagation();
}}});




/* ID: qx.ui.toolbar.Separator */
qx.Class.define("qx.ui.toolbar.Separator",
{extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
var l=new qx.ui.basic.Terminator;
l.setAppearance("toolbar-separator-line");
this.add(l);
},
properties:{appearance:{refine:true,
init:"toolbar-separator"}}});




/* ID: qx.ui.form.CheckBox */
qx.Class.define("qx.ui.form.CheckBox",
{extend:qx.ui.basic.Atom,
construct:function(vText,
vValue,
vName,
vChecked){this.base(arguments,
vText);
this.initTabIndex();
this._createIcon();
if(vValue!=null){this.setValue(vValue);
}
if(vName!=null){this.setName(vName);
}
if(vChecked!=null){this.setChecked(vChecked);
}else{this.initChecked();
}this.addEventListener("click",
this._onclick);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keyup",
this._onkeyup);
},
properties:{appearance:{refine:true,
init:"check-box"},
tabIndex:{refine:true,
init:1},
name:{check:"String",
event:"changeName"},
value:{check:"String",
event:"changeValue"},
checked:{check:"Boolean",
apply:"_applyChecked",
init:false,
event:"changeChecked"}},
members:{INPUT_TYPE:"checkbox",
_createIcon:function(){var i=this._iconObject=new qx.ui.form.InputCheckSymbol;
i.setType(this.INPUT_TYPE);
i.setChecked(this.getChecked());
i.setAnonymous(true);
this.addAtBegin(i);
},
_applyChecked:function(value,
old){if(this._iconObject){this._iconObject.setChecked(value);
}},
_applyIcon:null,
_applyDisabledIcon:null,
_handleIcon:function(){switch(this.getShow()){case "icon":case "both":this._iconIsVisible=true;
break;
default:this._iconIsVisible=false;
}
if(this._iconIsVisible){this._iconObject?this._iconObject.setDisplay(true):this._createIcon();
}else if(this._iconObject){this._iconObject.setDisplay(false);
}},
_onclick:function(e){this.toggleChecked();
},
_onkeydown:function(e){if(e.getKeyIdentifier()=="Enter"&&!e.isAltPressed()){this.toggleChecked();
}},
_onkeyup:function(e){if(e.getKeyIdentifier()=="Space"){this.toggleChecked();
}}}});




/* ID: qx.ui.form.InputCheckSymbol */
qx.Class.define("qx.ui.form.InputCheckSymbol",
{extend:qx.ui.basic.Terminator,
construct:function(){this.base(arguments);
this.setSelectable(false);
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this.setWidth(13);
this.setHeight(13);
}else if(qx.core.Variant.isSet("qx.client",
"gecko")){this.setMargin(0);
}this.initTabIndex();
this.setChecked(false);
},
properties:{tabIndex:{refine:true,
init:-1},
name:{check:"String",
init:null,
nullable:true,
apply:"_applyName"},
value:{init:null,
nullable:true,
apply:"_applyValue"},
type:{init:null,
nullable:true,
apply:"_applyType"},
checked:{check:"Boolean",
init:false,
apply:"_applyChecked"}},
members:{_createElementImpl:function(){this.setElement(this.getTopLevelWidget().getDocumentElement().createElement("input"));
},
_applyName:function(value,
old){return this.setHtmlProperty("name",
value);
},
_applyValue:function(value,
old){return this.setHtmlProperty("value",
value);
},
_applyType:function(value,
old){return this.setHtmlProperty("type",
value);
},
_applyChecked:function(value,
old){return this.setHtmlProperty("checked",
value);
},
getPreferredBoxWidth:function(){return 13;
},
getPreferredBoxHeight:function(){return 13;
},
_afterAppear:qx.core.Variant.select("qx.client",
{"mshtml":function(){this.base(arguments);
var vElement=this.getElement();
vElement.checked=this.getChecked();
if(this.getEnabled()===false){vElement.disabled=false;
}},
"default":qx.lang.Function.returnTrue}),
_applyEnabled:function(value,
old){value===false?this.setHtmlProperty("disabled",
"disabled"):this.removeHtmlProperty("disabled");
return this.base(arguments,
value,
old);
}},
defer:function(statics,
members){members.getBoxWidth=members.getPreferredBoxWidth;
members.getBoxHeight=members.getPreferredBoxHeight;
members.getInnerWidth=members.getPreferredBoxWidth;
members.getInnerHeight=members.getPreferredBoxHeight;
}});




/* ID: qx.ui.form.ComboBox */
qx.Class.define("qx.ui.form.ComboBox",
{extend:qx.ui.layout.HorizontalBoxLayout,
construct:function(){this.base(arguments);
var l=this._list=new qx.ui.form.List;
l.setAppearance("combo-box-list");
l.setEdge(0);
var m=this._manager=this._list.getManager();
m.setMultiSelection(false);
m.setDragSelection(false);
var p=this._popup=new qx.ui.popup.Popup;
p.setAppearance("combo-box-popup");
p.setRestrictToPageLeft(-100000);
p.setRestrictToPageRight(-100000);
p.setAutoHide(false);
p.setHeight("auto");
p.add(l);
var f=this._field=new qx.ui.form.TextField;
f.setAppearance("combo-box-text-field");
f.setTabIndex(-1);
f.setWidth("1*");
f.setAllowStretchY(true);
f.setHeight(null);
this.add(f);
var b=this._button=new qx.ui.basic.Atom;
b.setAppearance("combo-box-button");
b.setAllowStretchY(true);
b.setTabIndex(-1);
b.setHeight(null);
this.add(b);
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("mouseup",
this._onmouseup);
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mousewheel",
this._onmousewheel);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keypress",
this._onkeypress);
this.addEventListener("keyinput",
this._onkeyinput);
this.addEventListener("beforeDisappear",
this._onbeforedisappear);
this._popup.addEventListener("appear",
this._onpopupappear,
this);
this._field.addEventListener("input",
this._oninput,
this);
qx.locale.Manager.getInstance().addEventListener("changeLocale",
this._onlocalechange,
this);
var vDoc=qx.ui.core.ClientDocument.getInstance();
vDoc.addEventListener("windowblur",
this._testClosePopup,
this);
this.remapChildrenHandlingTo(l);
this.initEditable();
this.initTabIndex();
this.initWidth();
this.initHeight();
this.initMinWidth();
},
events:{"beforeInitialOpen":"qx.event.type.Event"},
properties:{appearance:{refine:true,
init:"combo-box"},
allowStretchY:{refine:true,
init:false},
width:{refine:true,
init:120},
height:{refine:true,
init:"auto"},
minWidth:{refine:true,
init:40},
tabIndex:{refine:true,
init:1},
editable:{check:"Boolean",
apply:"_applyEditable",
event:"changeEditable",
init:false},
selected:{check:"qx.ui.form.ListItem",
nullable:true,
apply:"_applySelected",
event:"changeSelected"},
value:{check:"String",
nullable:true,
apply:"_applyValue",
event:"changeValue"},
pagingInterval:{check:"Integer",
init:10}},
members:{getManager:function(){return this._manager;
},
getPopup:function(){return this._popup;
},
getList:function(){return this._list;
},
getField:function(){return this._field;
},
getButton:function(){return this._button;
},
_applySelected:function(value,
old){this._fromSelected=true;
if(!this._fromValue){this.setValue(value?value.getLabel().toString():"");
}this._manager.setLeadItem(value);
this._manager.setAnchorItem(value);
if(value){this._manager.setSelectedItem(value);
}else{this._manager.deselectAll();
}delete this._fromSelected;
},
_applyValue:function(value,
old){this._fromValue=true;
if(!this._fromInput){if(this._field.getValue()==value){this._field.setValue(null);
}this._field.setValue(value);
}if(!this._fromSelected){var vSelItem=this._list.findStringExact(value);
if(vSelItem!=null&&!vSelItem.getEnabled()){vSelItem=null;
}this.setSelected(vSelItem);
}delete this._fromValue;
},
_applyEditable:function(value,
old){var f=this._field;
f.setReadOnly(!value);
f.setCursor(value?null:"default");
f.setSelectable(value);
},
_oldSelected:null,
_openPopup:function(){var p=this._popup;
var el=this.getElement();
if(!p.isCreated()){this.createDispatchEvent("beforeInitialOpen");
}
if(this._list.getChildrenLength()==0){return;
}p.positionRelativeTo(el,
1,
qx.html.Dimension.getBoxHeight(el));
p.setWidth(this.getBoxWidth()-2);
p.setParent(this.getTopLevelWidget());
p.show();
this._oldSelected=this.getSelected();
this.setCapture(true);
},
_closePopup:function(){this._popup.hide();
this.setCapture(false);
},
_testClosePopup:function(){if(this._popup.isSeeable()){this._closePopup();
}},
_togglePopup:function(){this._popup.isSeeable()?this._closePopup():this._openPopup();
},
_onpopupappear:function(e){var vSelItem=this.getSelected();
if(vSelItem){vSelItem.scrollIntoView();
}},
_oninput:function(e){this._fromInput=true;
this.setValue(this._field.getComputedValue());
if(this.getPopup().isSeeable()&&this.getSelected()){this.getSelected().scrollIntoView();
}delete this._fromInput;
},
_onbeforedisappear:function(e){this._testClosePopup();
},
_onlocalechange:function(e){var selected=this.getSelected();
this._applySelected(selected,
selected);
},
_onmousedown:function(e){if(!e.isLeftButtonPressed()){return;
}var vTarget=e.getTarget();
switch(vTarget){case this._field:if(this.getEditable()){break;
}case this._button:this._button.addState("pressed");
this._togglePopup();
break;
case this:case this._list:break;
default:if(vTarget instanceof qx.ui.form.ListItem&&vTarget.getParent()==this._list){this._list._onmousedown(e);
this.setSelected(this._list.getSelectedItem());
this._closePopup();
this.setFocused(true);
}else if(this._popup.isSeeable()){this._popup.hide();
this.setCapture(false);
}}},
_onmouseup:function(e){switch(e.getTarget()){case this._field:if(this.getEditable()){break;
}default:this._button.removeState("pressed");
break;
}},
_onmouseover:function(e){var vTarget=e.getTarget();
if(vTarget instanceof qx.ui.form.ListItem){var vManager=this._manager;
vManager.deselectAll();
vManager.setLeadItem(vTarget);
vManager.setAnchorItem(vTarget);
vManager.setSelectedItem(vTarget);
}},
_onmousewheel:function(e){if(!this._popup.isSeeable()){var toSelect;
var isSelected=this.getSelected();
if(e.getWheelDelta()<0){toSelect=isSelected?this._manager.getNext(isSelected):this._manager.getFirst();
}else{toSelect=isSelected?this._manager.getPrevious(isSelected):this._manager.getLast();
}
if(toSelect){this.setSelected(toSelect);
}}else{var vTarget=e.getTarget();
if(vTarget!=this&&vTarget.getParent()!=this._list){this._popup.hide();
this.setCapture(false);
}}},
_onkeydown:function(e){var vManager=this._manager;
var vVisible=this._popup.isSeeable();
switch(e.getKeyIdentifier()){case "Enter":if(vVisible){this.setSelected(this._manager.getSelectedItem());
this._closePopup();
this.setFocused(true);
}else{this._openPopup();
}return;
case "Escape":if(vVisible){vManager.setLeadItem(this._oldSelected);
vManager.setAnchorItem(this._oldSelected);
vManager.setSelectedItem(this._oldSelected);
this._field.setValue(this._oldSelected?this._oldSelected.getLabel():"");
this._closePopup();
this.setFocused(true);
}return;
case "Down":if(e.isAltPressed()){this._togglePopup();
return;
}break;
}},
_onkeypress:function(e){var vVisible=this._popup.isSeeable();
var vManager=this._manager;
switch(e.getKeyIdentifier()){case "PageUp":if(!vVisible){var vPrevious;
var vTemp=this.getSelected();
if(vTemp){var vInterval=this.getPagingInterval();
do{vPrevious=vTemp;
}while(--vInterval&&(vTemp=vManager.getPrevious(vPrevious)));
}else{vPrevious=vManager.getLast();
}this.setSelected(vPrevious);
return;
}break;
case "PageDown":if(!vVisible){var vNext;
var vTemp=this.getSelected();
if(vTemp){var vInterval=this.getPagingInterval();
do{vNext=vTemp;
}while(--vInterval&&(vTemp=vManager.getNext(vNext)));
}else{vNext=vManager.getFirst();
}this.setSelected(vNext||null);
return;
}break;
}if(!this.isEditable()||vVisible){this._list._onkeypress(e);
var vSelected=this._manager.getSelectedItem();
if(!vVisible){this.setSelected(vSelected);
}else if(vSelected){this._field.setValue(vSelected.getLabel());
}}},
_onkeyinput:function(e){var vVisible=this._popup.isSeeable();
if(!this.isEditable()||vVisible){this._list._onkeyinput(e);
var vSelected=this._manager.getSelectedItem();
if(!vVisible){this.setSelected(vSelected);
}else if(vSelected){this._field.setValue(vSelected.getLabel());
}}},
_visualizeBlur:function(){this.getField()._visualizeBlur();
this.removeState("focused");
},
_visualizeFocus:function(){this.getField()._visualizeFocus();
this.getField().selectAll();
this.addState("focused");
}},
destruct:function(){if(this._popup&&!qx.core.Object.inGlobalDispose()){this._popup.setParent(null);
}var vDoc=qx.ui.core.ClientDocument.getInstance();
vDoc.removeEventListener("windowblur",
this._testClosePopup,
this);
var vMgr=qx.locale.Manager.getInstance();
vMgr.removeEventListener("changeLocale",
this._onlocalechange,
this);
this._disposeObjects("_popup",
"_list",
"_manager",
"_field",
"_button");
}});




/* ID: qx.ui.form.List */
qx.Class.define("qx.ui.form.List",
{extend:qx.ui.layout.VerticalBoxLayout,
construct:function(){this.base(arguments);
this._manager=new qx.ui.selection.SelectionManager(this);
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mousedown",
this._onmousedown);
this.addEventListener("mouseup",
this._onmouseup);
this.addEventListener("click",
this._onclick);
this.addEventListener("dblclick",
this._ondblclick);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keypress",
this._onkeypress);
this.addEventListener("keyinput",
this._onkeyinput);
this.initOverflow();
this.initTabIndex();
},
properties:{appearance:{refine:true,
init:"list"},
overflow:{refine:true,
init:"hidden"},
tabIndex:{refine:true,
init:1},
enableInlineFind:{check:"Boolean",
init:true},
markLeadingItem:{check:"Boolean",
init:false}},
members:{_pressedString:"",
getManager:function(){return this._manager;
},
getListItemTarget:function(vItem){while(vItem!=null&&vItem.getParent()!=this){vItem=vItem.getParent();
}return vItem;
},
getSelectedItem:function(){return this.getSelectedItems()[0]||null;
},
getSelectedItems:function(){return this._manager.getSelectedItems();
},
_onmouseover:function(e){var vItem=this.getListItemTarget(e.getTarget());
if(vItem){this._manager.handleMouseOver(vItem,
e);
}},
_onmousedown:function(e){var vItem=this.getListItemTarget(e.getTarget());
if(vItem){this._manager.handleMouseDown(vItem,
e);
}},
_onmouseup:function(e){var vItem=this.getListItemTarget(e.getTarget());
if(vItem){this._manager.handleMouseUp(vItem,
e);
}},
_onclick:function(e){var vItem=this.getListItemTarget(e.getTarget());
if(vItem){this._manager.handleClick(vItem,
e);
}},
_ondblclick:function(e){var vItem=this.getListItemTarget(e.getTarget());
if(vItem){this._manager.handleDblClick(vItem,
e);
}},
_onkeydown:function(e){if(e.getKeyIdentifier()=="Enter"&&!e.isAltPressed()){var items=this.getSelectedItems();
var currentItem;
for(var i=0;i<items.length;i++){items[i].createDispatchEvent("action");
}}},
_onkeypress:function(e){this._manager.handleKeyPress(e);
},
_lastKeyPress:0,
_onkeyinput:function(e){if(!this.getEnableInlineFind()){return;
}if(((new Date).valueOf()-this._lastKeyPress)>1000){this._pressedString="";
}this._pressedString+=String.fromCharCode(e.getCharCode());
var matchedItem=this.findString(this._pressedString,
null);
if(matchedItem){var oldVal=this._manager._getChangeValue();
var oldFireChange=this._manager.getFireChange();
this._manager.setFireChange(false);
this._manager._deselectAll();
this._manager.setItemSelected(matchedItem,
true);
this._manager.setAnchorItem(matchedItem);
this._manager.setLeadItem(matchedItem);
matchedItem.scrollIntoView();
this._manager.setFireChange(oldFireChange);
if(oldFireChange&&this._manager._hasChanged(oldVal)){this._manager._dispatchChange();
}}this._lastKeyPress=(new Date).valueOf();
e.preventDefault();
},
_findItem:function(vUserValue,
vStartIndex,
vType){var vAllItems=this.getChildren();
if(vStartIndex==null){vStartIndex=vAllItems.indexOf(this.getSelectedItem());
if(vStartIndex==-1){vStartIndex=0;
}}var methodName="matches"+vType;
for(var i=vStartIndex;i<vAllItems.length;i++){if(vAllItems[i][methodName](vUserValue)){return vAllItems[i];
}}for(var i=0;i<vStartIndex;i++){if(vAllItems[i][methodName](vUserValue)){return vAllItems[i];
}}return null;
},
findString:function(vText,
vStartIndex){return this._findItem(vText,
vStartIndex||0,
"String");
},
findStringExact:function(vText,
vStartIndex){return this._findItem(vText,
vStartIndex||0,
"StringExact");
},
findValue:function(vText,
vStartIndex){return this._findItem(vText,
vStartIndex||0,
"Value");
},
findValueExact:function(vText,
vStartIndex){return this._findItem(vText,
vStartIndex||0,
"ValueExact");
},
_sortItemsCompare:function(a,
b){return a.key<b.key?-1:a.key==b.key?0:1;
},
sortItemsByString:function(vReverse){var sortitems=[];
var items=this.getChildren();
for(var i=0,
l=items.length;i<l;i++){sortitems[i]={key:items[i].getLabel(),
item:items[i]};
}sortitems.sort(this._sortItemsCompare);
if(vReverse){sortitems.reverse();
}
for(var i=0;i<l;i++){this.addAt(sortitems[i].item,
i);
}},
sortItemsByValue:function(vReverse){var sortitems=[];
var items=this.getChildren();
for(var i=0,
l=items.length;i<l;i++){sortitems[i]={key:items[i].getValue(),
item:items[i]};
}sortitems.sort(this._sortItemsCompare);
if(vReverse){sortitems.reverse();
}
for(var i=0;i<l;i++){this.addAt(sortitems[i].item,
i);
}}},
destruct:function(){this._disposeObjects("_manager");
}});




/* ID: qx.ui.form.TextField */
qx.Class.define("qx.ui.form.TextField",
{extend:qx.ui.basic.Terminator,
construct:function(value){this.base(arguments);
if(value!=null){this.setValue(value);
}this.initHideFocus();
this.initWidth();
this.initHeight();
this.initTabIndex();
this.initSpellCheck();
this.__oninput=qx.lang.Function.bindEvent(this._oninputDom,
this);
this.addEventListener("blur",
this._onblur);
this.addEventListener("focus",
this._onfocus);
this.addEventListener("input",
this._oninput);
},
statics:{createRegExpValidator:function(vRegExp){return function(s){return vRegExp.test(s);
};
}},
events:{"input":"qx.event.type.DataEvent"},
properties:{allowStretchX:{refine:true,
init:true},
allowStretchY:{refine:true,
init:false},
appearance:{refine:true,
init:"text-field"},
tabIndex:{refine:true,
init:1},
hideFocus:{refine:true,
init:true},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
selectable:{refine:true,
init:true},
value:{init:"",
nullable:true,
event:"changeValue",
apply:"_applyValue"},
textAlign:{check:["left",
"center",
"right",
"justify"],
nullable:true,
themeable:true,
apply:"_applyTextAlign"},
spellCheck:{check:"Boolean",
init:false,
apply:"_applySpellCheck"},
liveUpdate:{check:"Boolean",
init:false},
maxLength:{check:"Integer",
apply:"_applyMaxLength",
nullable:true},
readOnly:{check:"Boolean",
apply:"_applyReadOnly",
init:false},
validator:{check:"Function",
event:"changeValidator",
nullable:true}},
members:{_inputTag:"input",
_inputType:"text",
_inputOverflow:"hidden",
_applyElement:function(value,
old){this.base(arguments,
value,
old);
if(value){var inp=this._inputElement=document.createElement(this._inputTag);
if(this._inputType){inp.type=this._inputType;
}inp.autoComplete="off";
inp.setAttribute("autoComplete",
"off");
inp.disabled=this.getEnabled()===false;
inp.readOnly=this.getReadOnly();
inp.value=this.getValue()?this.getValue():"";
if(this.getMaxLength()!=null){inp.maxLength=this.getMaxLength();
}var istyle=inp.style;
istyle.padding=istyle.margin=0;
istyle.border="0 none";
istyle.background="transparent";
istyle.overflow=this._inputOverflow;
istyle.outline="none";
istyle.resize="none";
istyle.WebkitAppearance="none";
istyle.MozAppearance="none";
if(qx.core.Variant.isSet("qx.client",
"gecko|opera|webkit")){istyle.margin="1px 0";
}this._renderFont();
this._renderTextColor();
this._renderTextAlign();
this._renderCursor();
this._renderSpellCheck();
if(qx.core.Variant.isSet("qx.client",
"mshtml")){inp.onpropertychange=this.__oninput;
}else{inp.addEventListener("input",
this.__oninput,
false);
}value.appendChild(inp);
}},
_postApply:function(){this._syncFieldWidth();
this._syncFieldHeight();
},
_changeInnerWidth:function(value,
old){this._syncFieldWidth();
},
_changeInnerHeight:function(value,
old){this._syncFieldHeight();
},
_syncFieldWidth:function(){this._inputElement.style.width=this.getInnerWidth()+"px";
},
_syncFieldHeight:function(){this._inputElement.style.height=(this.getInnerHeight()-2)+"px";
},
_applyCursor:function(value,
old){if(this._inputElement){this._renderCursor();
}},
_renderCursor:function(){var style=this._inputElement.style;
var value=this.getCursor();
if(value){if(value=="pointer"&&qx.core.Client.getInstance().isMshtml()){style.cursor="hand";
}else{style.cursor=value;
}}else{style.cursor="";
}},
_applyTextAlign:function(value,
old){if(this._inputElement){this._renderTextAlign();
}},
_renderTextAlign:function(){this._inputElement.style.textAlign=this.getTextAlign()||"";
},
_applySpellCheck:function(value,
old){if(this._inputElement){this._renderSpellCheck();
}},
_renderSpellCheck:function(){this._inputElement.spellcheck=this.getSpellCheck();
},
_applyEnabled:function(value,
old){if(this._inputElement){this._inputElement.disabled=value===false;
}return this.base(arguments,
value,
old);
},
_applyValue:function(value,
old){this._inValueProperty=true;
if(this._inputElement){if(value===null){value="";
}
if(this._inputElement.value!==value){this._inputElement.value=value;
}}delete this._inValueProperty;
},
_applyMaxLength:function(value,
old){if(this._inputElement){this._inputElement.maxLength=value==null?"":value;
}},
_applyReadOnly:function(value,
old){if(this._inputElement){this._inputElement.readOnly=value;
}
if(value){this.addState("readonly");
}else{this.removeState("readonly");
}},
_applyTextColor:function(value,
old){qx.theme.manager.Color.getInstance().connect(this._styleTextColor,
this,
value);
},
_styleTextColor:function(value){this.__textColor=value;
this._renderTextColor();
},
_renderTextColor:function(){var inp=this._inputElement;
if(inp){inp.style.color=this.__textColor||"";
}},
_applyFont:function(value,
old){qx.theme.manager.Font.getInstance().connect(this._styleFont,
this,
value);
},
_styleFont:function(value){this.__font=value;
this._renderFont();
},
_renderFont:function(){var inp=this._inputElement;
if(inp){var value=this.__font;
value?value.renderElement(inp):qx.ui.core.Font.resetElement(inp);
}},
_visualizeFocus:function(){this.base(arguments);
if(!qx.event.handler.FocusHandler.mouseFocus&&this.getEnableElementFocus()){try{this._inputElement.focus();
}catch(ex){}}},
_visualizeBlur:function(){this.base(arguments);
if(!qx.event.handler.FocusHandler.mouseFocus){try{this._inputElement.blur();
}catch(ex){}}},
getComputedValue:function(){if(this._inputElement){return this._inputElement.value;
}return this.getValue();
},
getInputElement:function(){return this._inputElement||null;
},
isValid:function(){var vValidator=this.getValidator();
return !vValidator||vValidator(this.getValue());
},
isComputedValid:function(){var vValidator=this.getValidator();
return !vValidator||vValidator(this.getComputedValue());
},
_computePreferredInnerWidth:function(){return 120;
},
_computePreferredInnerHeight:function(){return 16;
},
_ieFirstInputFix:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._inValueProperty=true;
this._inputElement.value=this.getValue()===null?"":this.getValue();
this._firstInputFixApplied=true;
delete this._inValueProperty;
},
"default":null}),
_afterAppear:qx.core.Variant.select("qx.client",
{"mshtml":function(){this.base(arguments);
if(!this._firstInputFixApplied){qx.client.Timer.once(this._ieFirstInputFix,
this,
1);
}},
"default":function(){this.base(arguments);
}}),
_firstInputFixApplied:false,
_textOnFocus:null,
_oninputDom:qx.core.Variant.select("qx.client",
{"mshtml":function(e){if(!this._inValueProperty&&e.propertyName==="value"){this.createDispatchDataEvent("input",
this.getComputedValue());
}},
"default":function(e){this.createDispatchDataEvent("input",
this.getComputedValue());
}}),
_ontabfocus:function(){this.selectAll();
},
_onfocus:function(){this._textOnFocus=this.getComputedValue();
},
_onblur:function(){var vValue=this.getComputedValue().toString();
if(this._textOnFocus!=vValue){this.setValue(vValue);
}this.setSelectionLength(0);
},
_oninput:function(){if(!this.isLiveUpdate()){return;
}var vValue=this.getComputedValue().toString();
this.setValue(vValue);
},
__getRange:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._visualPropertyCheck();
return this._inputElement.createTextRange();
},
"default":null}),
__getSelectionRange:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._visualPropertyCheck();
return this.getTopLevelWidget().getDocumentElement().selection.createRange();
},
"default":null}),
setSelectionStart:qx.core.Variant.select("qx.client",
{"mshtml":function(vStart){this._visualPropertyCheck();
var vText=this._inputElement.value;
var i=0;
while(i<vStart){i=vText.indexOf("\r\n",
i);
if(i==-1){break;
}vStart--;
i++;
}var vRange=this.__getRange();
vRange.collapse();
vRange.move("character",
vStart);
vRange.select();
},
"default":function(vStart){this._visualPropertyCheck();
this._inputElement.selectionStart=vStart;
}}),
getSelectionStart:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._visualPropertyCheck();
var vSelectionRange=this.__getSelectionRange();
if(!this._inputElement.contains(vSelectionRange.parentElement())){return -1;
}var vRange=this.__getRange();
vRange.setEndPoint("EndToStart",
vSelectionRange);
return vRange.text.length;
},
"default":function(){this._visualPropertyCheck();
return this._inputElement.selectionStart;
}}),
setSelectionLength:qx.core.Variant.select("qx.client",
{"mshtml":function(vLength){this._visualPropertyCheck();
var vSelectionRange=this.__getSelectionRange();
if(!this._inputElement.contains(vSelectionRange.parentElement())){return;
}vSelectionRange.collapse();
vSelectionRange.moveEnd("character",
vLength);
vSelectionRange.select();
},
"default":function(vLength){this._visualPropertyCheck();
var el=this._inputElement;
if(qx.util.Validation.isValidString(el.value)){el.selectionEnd=el.selectionStart+vLength;
}}}),
getSelectionLength:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._visualPropertyCheck();
var vSelectionRange=this.__getSelectionRange();
if(!this._inputElement.contains(vSelectionRange.parentElement())){return 0;
}return vSelectionRange.text.length;
},
"default":function(){this._visualPropertyCheck();
var el=this._inputElement;
return el.selectionEnd-el.selectionStart;
}}),
setSelectionText:qx.core.Variant.select("qx.client",
{"mshtml":function(vText){this._visualPropertyCheck();
var vStart=this.getSelectionStart();
var vSelectionRange=this.__getSelectionRange();
if(!this._inputElement.contains(vSelectionRange.parentElement())){return;
}vSelectionRange.text=vText;
this.setValue(this._inputElement.value);
this.setSelectionStart(vStart);
this.setSelectionLength(vText.length);
},
"default":function(vText){this._visualPropertyCheck();
var el=this._inputElement;
var vOldText=el.value;
var vStart=el.selectionStart;
var vOldTextBefore=vOldText.substr(0,
vStart);
var vOldTextAfter=vOldText.substr(el.selectionEnd);
var vValue=el.value=vOldTextBefore+vText+vOldTextAfter;
el.selectionStart=vStart;
el.selectionEnd=vStart+vText.length;
this.setValue(vValue);
}}),
getSelectionText:qx.core.Variant.select("qx.client",
{"mshtml":function(){this._visualPropertyCheck();
var vSelectionRange=this.__getSelectionRange();
if(!this._inputElement.contains(vSelectionRange.parentElement())){return "";
}return vSelectionRange.text;
},
"default":function(){this._visualPropertyCheck();
return this._inputElement.value.substr(this.getSelectionStart(),
this.getSelectionLength());
}}),
selectAll:function(){this._visualPropertyCheck();
if(this.getValue()!=null){this.setSelectionStart(0);
this.setSelectionLength(this._inputElement.value.length);
}this._inputElement.select();
this._inputElement.focus();
},
selectFromTo:qx.core.Variant.select("qx.client",
{"mshtml":function(vStart,
vEnd){this._visualPropertyCheck();
this.setSelectionStart(vStart);
this.setSelectionLength(vEnd-vStart);
},
"default":function(vStart,
vEnd){this._visualPropertyCheck();
var el=this._inputElement;
el.selectionStart=vStart;
el.selectionEnd=vEnd;
}})},
destruct:function(){if(this._inputElement){if(qx.core.Variant.isSet("qx.client",
"mshtml")){this._inputElement.onpropertychange=null;
}else{this._inputElement.removeEventListener("input",
this.__oninput,
false);
}}this._disposeFields("_inputElement",
"__font",
"__oninput");
}});




/* ID: qx.ui.form.ListItem */
qx.Class.define("qx.ui.form.ListItem",
{extend:qx.ui.basic.Atom,
construct:function(vText,
vIcon,
vValue){this.base(arguments,
vText,
vIcon);
if(vValue!=null){this.setValue(vValue);
}this.addEventListener("dblclick",
this._ondblclick);
this.initMinWidth();
},
events:{"action":"qx.event.type.Event"},
properties:{appearance:{refine:true,
init:"list-item"},
minWidth:{refine:true,
init:"auto"},
width:{refine:true,
init:null},
allowStretchX:{refine:true,
init:true},
value:{event:"changeValue"}},
members:{handleStateChange:function(){if(this.hasState("lead")){this.setStyleProperty("MozOutline",
"1px dotted invert");
this.setStyleProperty("outline",
"1px dotted invert");
}else{this.removeStyleProperty("MozOutline");
this.setStyleProperty("outline",
"0px none");
}},
_applyStateStyleFocus:function(vStates){},
matchesString:function(vText){return vText!=""&&this.getLabel().toString().toLowerCase().indexOf(vText.toLowerCase())==0;
},
matchesStringExact:function(vText){return vText!=""&&this.getLabel().toString().toLowerCase()==String(vText).toLowerCase();
},
matchesValue:function(vText){return vText!=""&&this.getValue().toLowerCase().indexOf(vText.toLowerCase())==0;
},
matchesValueExact:function(vText){return vText!=""&&this.getValue().toLowerCase()==String(vText).toLowerCase();
},
_ondblclick:function(e){var vCommand=this.getCommand();
if(vCommand){vCommand.execute();
}}}});




/* ID: qx.ui.form.PasswordField */
qx.Class.define("qx.ui.form.PasswordField",
{extend:qx.ui.form.TextField,
members:{_inputType:"password"}});




/* ID: qx.ui.form.RadioButton */
qx.Class.define("qx.ui.form.RadioButton",
{extend:qx.ui.form.CheckBox,
construct:function(vText,
vValue,
vName,
vChecked){this.base(arguments,
vText,
vValue,
vName,
vChecked);
this.addEventListener("keypress",
this._onkeypress);
},
properties:{appearance:{refine:true,
init:"radio-button"},
manager:{check:"qx.ui.selection.RadioManager",
nullable:true,
apply:"_applyManager"}},
members:{INPUT_TYPE:"radio",
_applyChecked:function(value,
old){if(this._iconObject){this._iconObject.setChecked(value);
}var vManager=this.getManager();
if(vManager){vManager.handleItemChecked(this,
value);
}},
_applyManager:function(value,
old){if(old){old.remove(this);
}
if(value){value.add(this);
}},
_applyName:function(value,
old){if(this._iconObject){this._iconObject.setName(value);
}
if(this.getManager()){this.getManager().setName(value);
}},
_applyValue:function(value,
old){if(this.isCreated()&&this._iconObject){this._iconObject.setValue(value);
}},
_onkeydown:function(e){if(e.getKeyIdentifier()=="Enter"&&!e.isAltPressed()){this.setChecked(true);
}},
_onkeypress:function(e){switch(e.getKeyIdentifier()){case "Left":case "Up":qx.event.handler.FocusHandler.mouseFocus=false;
qx.event.handler.FocusHandler.mouseFocus=false;
return this.getManager()?this.getManager().selectPrevious(this):true;
case "Right":case "Down":qx.event.handler.FocusHandler.mouseFocus=false;
return this.getManager()?this.getManager().selectNext(this):true;
}},
_onclick:function(e){this.setChecked(true);
},
_onkeyup:function(e){if(e.getKeyIdentifier()=="Space"){this.setChecked(true);
}}}});




/* ID: qx.ui.form.Spinner */
qx.Class.define("qx.ui.form.Spinner",
{extend:qx.ui.layout.HorizontalBoxLayout,
construct:function(vMin,
vValue,
vMax){this.base(arguments);
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this.setStyleProperty("fontSize",
"0px");
}this._textfield=new qx.ui.form.TextField;
this._textfield.setBorder(null);
this._textfield.setWidth("1*");
this._textfield.setAllowStretchY(true);
this._textfield.setHeight(null);
this._textfield.setVerticalAlign("middle");
this._textfield.setAppearance("spinner-text-field");
this.add(this._textfield);
this._buttonlayout=new qx.ui.layout.VerticalBoxLayout;
this._buttonlayout.setWidth("auto");
this.add(this._buttonlayout);
this._upbutton=new qx.ui.basic.Image;
this._upbutton.setAppearance("spinner-button-up");
this._upbutton.setHeight("1*");
this._buttonlayout.add(this._upbutton);
this._downbutton=new qx.ui.basic.Image;
this._downbutton.setAppearance("spinner-button-down");
this._downbutton.setHeight("1*");
this._buttonlayout.add(this._downbutton);
this._timer=new qx.client.Timer(this.getInterval());
this.setManager(new qx.util.range.Range());
this.initWrap();
this.addEventListener("keypress",
this._onkeypress,
this);
this.addEventListener("keydown",
this._onkeydown,
this);
this.addEventListener("keyup",
this._onkeyup,
this);
this.addEventListener("mousewheel",
this._onmousewheel,
this);
this._textfield.addEventListener("input",
this._oninput,
this);
this._textfield.addEventListener("blur",
this._onblur,
this);
this._upbutton.addEventListener("mousedown",
this._onmousedown,
this);
this._downbutton.addEventListener("mousedown",
this._onmousedown,
this);
this._timer.addEventListener("interval",
this._oninterval,
this);
if(vMin!=null){this.setMin(vMin);
}
if(vMax!=null){this.setMax(vMax);
}
if(vValue!=null){this.setValue(vValue);
}this._checkValue=this.__checkValue;
this.initWidth();
this.initHeight();
},
events:{"change":"qx.event.type.DataEvent"},
properties:{appearance:{refine:true,
init:"spinner"},
width:{refine:true,
init:60},
height:{refine:true,
init:22},
incrementAmount:{check:"Integer",
init:1,
apply:"_applyIncrementAmount"},
wheelIncrementAmount:{check:"Integer",
init:1},
pageIncrementAmount:{check:"Integer",
init:10},
interval:{check:"Integer",
init:100},
firstInterval:{check:"Integer",
init:500},
minTimer:{check:"Integer",
init:20},
timerDecrease:{check:"Integer",
init:2},
amountGrowth:{check:"Number",
init:1.01},
wrap:{check:"Boolean",
init:false,
apply:"_applyWrap"},
editable:{check:"Boolean",
init:true,
apply:"_applyEditable"},
manager:{check:"qx.util.range.IRange",
apply:"_applyManager",
dispose:true},
checkValueFunction:{apply:"_applyCheckValueFunction"}},
members:{_applyIncrementAmount:function(value,
old){this._computedIncrementAmount=value;
},
_applyEditable:function(value,
old){if(this._textfield){this._textfield.setReadOnly(!value);
}},
_applyWrap:function(value,
old){this.getManager().setWrap(value);
this._onchange();
},
_applyManager:function(value,
old){if(old){old.removeEventListener("change",
this._onchange,
this);
}
if(value){value.addEventListener("change",
this._onchange,
this);
}this._onchange();
},
_applyCheckValueFunction:function(value,
old){this._checkValue=value;
},
_computePreferredInnerWidth:function(){return 50;
},
_computePreferredInnerHeight:function(){return 14;
},
_onkeypress:function(e){var vIdentifier=e.getKeyIdentifier();
if(vIdentifier=="Enter"&&!e.isAltPressed()){this._checkValue(true,
false,
false);
this._textfield.selectAll();
}else{switch(vIdentifier){case "Up":case "Down":case "Left":case "Right":case "Shift":case "Control":case "Alt":case "Escape":case "Delete":case "Backspace":case "Insert":case "Home":case "End":case "PageUp":case "PageDown":case "NumLock":case "Tab":break;
default:if(vIdentifier>="0"&&vIdentifier<="9"){return;
}if(e.getModifiers()==0){e.preventDefault();
}}}},
_onkeydown:function(e){var vIdentifier=e.getKeyIdentifier();
if(this._intervalIncrease==null){switch(vIdentifier){case "Up":case "Down":this._intervalIncrease=vIdentifier=="Up";
this._intervalMode="single";
this._resetIncrements();
this._checkValue(true,
false,
false);
this._increment();
this._timer.startWith(this.getFirstInterval());
break;
case "PageUp":case "PageDown":this._intervalIncrease=vIdentifier=="PageUp";
this._intervalMode="page";
this._resetIncrements();
this._checkValue(true,
false,
false);
this._pageIncrement();
this._timer.startWith(this.getFirstInterval());
break;
}}},
_onkeyup:function(e){if(this._intervalIncrease!=null){switch(e.getKeyIdentifier()){case "Up":case "Down":case "PageUp":case "PageDown":this._timer.stop();
this._intervalIncrease=null;
this._intervalMode=null;
}}},
_onmousedown:function(e){if(!e.isLeftButtonPressed()){return;
}this._checkValue(true);
var vButton=e.getCurrentTarget();
vButton.addState("pressed");
vButton.addEventListener("mouseup",
this._onmouseup,
this);
vButton.addEventListener("mouseout",
this._onmouseup,
this);
this._intervalIncrease=vButton==this._upbutton;
this._resetIncrements();
this._increment();
this._textfield.selectAll();
this._timer.setInterval(this.getFirstInterval());
this._timer.start();
},
_onmouseup:function(e){var vButton=e.getCurrentTarget();
vButton.removeState("pressed");
vButton.removeEventListener("mouseup",
this._onmouseup,
this);
vButton.removeEventListener("mouseout",
this._onmouseup,
this);
this._textfield.selectAll();
this._textfield.setFocused(true);
this._timer.stop();
this._intervalIncrease=null;
},
_onmousewheel:function(e){if(this.getManager().incrementValue){this.getManager().incrementValue(this.getWheelIncrementAmount()*e.getWheelDelta());
}else{var value=this.getManager().getValue()+(this.getWheelIncrementAmount()*e.getWheelDelta());
value=this.getManager().limit(value);
this.getManager().setValue(value);
}this._textfield.selectAll();
},
_oninput:function(e){this._checkValue(true,
true);
},
_onchange:function(e){var vValue=this.getManager().getValue();
this._textfield.setValue(String(vValue));
if(vValue==this.getMin()&&!this.getWrap()){this._downbutton.removeState("pressed");
this._downbutton.setEnabled(false);
this._timer.stop();
}else{this._downbutton.resetEnabled();
}
if(vValue==this.getMax()&&!this.getWrap()){this._upbutton.removeState("pressed");
this._upbutton.setEnabled(false);
this._timer.stop();
}else{this._upbutton.resetEnabled();
}this.createDispatchDataEvent("change",
vValue);
},
_onblur:function(e){this._checkValue(false);
},
setValue:function(nValue){this.getManager().setValue(this.getManager().limit(nValue));
},
getValue:function(){this._checkValue(true);
return this.getManager().getValue();
},
resetValue:function(){this.getManager().resetValue();
},
setMax:function(vMax){return this.getManager().setMax(vMax);
},
getMax:function(){return this.getManager().getMax();
},
setMin:function(vMin){return this.getManager().setMin(vMin);
},
getMin:function(){return this.getManager().getMin();
},
_intervalIncrease:null,
_oninterval:function(e){this._timer.stop();
this.setInterval(Math.max(this.getMinTimer(),
this.getInterval()-this.getTimerDecrease()));
if(this._intervalMode=="page"){this._pageIncrement();
}else{if(this.getInterval()==this.getMinTimer()){this._computedIncrementAmount=this.getAmountGrowth()*this._computedIncrementAmount;
}this._increment();
}var wrap=this.getManager().getWrap();
switch(this._intervalIncrease){case true:if(this.getValue()==this.getMax()&&!wrap){return;
}case false:if(this.getValue()==this.getMin()&&!wrap){return;
}}this._timer.restartWith(this.getInterval());
},
__checkValue:function(acceptEmpty,
acceptEdit){var el=this._textfield.getInputElement();
if(!el){return;
}
if(el.value==""){if(!acceptEmpty){this.setValue(this.getMax());
this.resetValue();
return;
}}else{var val=el.value;
if(val.length>1){while(val.charAt(0)=="0"){val=val.substr(1,
val.length);
}var f1=parseInt(val)||0;
if(f1!=el.value){el.value=f1;
return;
}}if(val=="-"&&acceptEmpty&&this.getMin()<0){if(el.value!=val){el.value=val;
}return;
}val=parseInt(val);
var doFix=true;
var fixedVal=this.getManager().limit(val);
if(isNaN(fixedVal)){fixedVal=this.getManager().getValue();
}if(acceptEmpty&&val==""){doFix=false;
}else if(!isNaN(val)){if(acceptEdit){if(val>fixedVal&&!(val>0&&fixedVal<=0)&&String(val).length<String(fixedVal).length){doFix=false;
}else if(val<fixedVal&&!(val<0&&fixedVal>=0)&&String(val).length<String(fixedVal).length){doFix=false;
}}}if(doFix&&el.value!=fixedVal){el.value=fixedVal;
}if(!acceptEdit){this.getManager().setValue(fixedVal);
}}},
_increment:function(){if(this.getManager().incrementValue){this.getManager().incrementValue((this._intervalIncrease?1:-1)*this._computedIncrementAmount);
}else{var value=this.getManager().getValue()+((this._intervalIncrease?1:-1)*this._computedIncrementAmount);
value=this.getManager().limit(value);
this.getManager().setValue(value);
}},
_pageIncrement:function(){if(this.getManager().pageIncrementValue){this.getManager().pageIncrementValue();
}else{var value=this.getManager().getValue()+((this._intervalIncrease?1:-1)*this.getPageIncrementAmount());
value=this.getManager().limit(value);
this.getManager().setValue(value);
}},
_resetIncrements:function(){this._computedIncrementAmount=this.getIncrementAmount();
this.resetInterval();
}},
destruct:function(){this._disposeObjects("_textfield",
"_buttonlayout",
"_upbutton",
"_downbutton",
"_timer");
}});




/* ID: qx.util.range.IRange */
qx.Interface.define("qx.util.range.IRange",
{properties:{value:{},
min:{},
max:{},
wrap:{}},
members:{limit:function(value){return true;
}}});




/* ID: qx.util.range.Range */
qx.Class.define("qx.util.range.Range",
{extend:qx.core.Target,
implement:[qx.util.range.IRange],
events:{"change":"qx.event.type.Event"},
properties:{value:{check:"!isNaN(value)&&value>=this.getMin()&&value<=this.getMax()",
nullable:true,
init:0,
event:"change"},
min:{check:"Number",
apply:"_applyMin",
event:"change",
init:0},
max:{check:"Number",
apply:"_applyMax",
event:"change",
init:100},
wrap:{check:"Boolean",
init:false}},
members:{_applyMax:function(value,
old){this.setValue(Math.min(this.getValue(),
value));
},
_applyMin:function(value,
old){this.setValue(Math.max(this.getValue(),
value));
},
limit:function(value){if(this.getWrap()){var value=Math.round(value);
if(value<this.getMin()){return (this.getMax()-(this.getMin()-value))+1;
}
if(value>this.getMax()){return (this.getMin()+(value-this.getMax()))-1;
}}
if(value<this.getMin()){return this.getMin();
}
if(value>this.getMax()){return this.getMax();
}return Math.round(value);
}}});




/* ID: qx.ui.form.TextArea */
qx.Class.define("qx.ui.form.TextArea",
{extend:qx.ui.form.TextField,
properties:{appearance:{refine:true,
init:"text-area"},
allowStretchY:{refine:true,
init:true},
spellCheck:{refine:true,
init:true},
wrap:{check:"Boolean",
init:true,
apply:"_applyWrap"}},
members:{_inputTag:"textarea",
_inputType:null,
_inputOverflow:"auto",
_applyElement:function(value,
old){this.base(arguments,
value,
old);
this._styleWrap();
},
_applyWrap:function(value,
old){this._styleWrap();
},
_styleWrap:qx.core.Variant.select("qx.client",
{"mshtml":function(){if(this._inputElement){this._inputElement.wrap=this.getWrap()?"soft":"off";
}},
"default":function(){if(this._inputElement){this._inputElement.style.whiteSpace=this.getWrap()?"normal":"nowrap";
}}}),
_computePreferredInnerHeight:function(){return 60;
}}});




/* ID: qx.ui.groupbox.GroupBox */
qx.Class.define("qx.ui.groupbox.GroupBox",
{extend:qx.ui.layout.CanvasLayout,
construct:function(vLegend,
vIcon){this.base(arguments);
this._createFrameObject();
this._createLegendObject();
this.setLegend(vLegend||"");
if(vIcon!=null){this.setIcon(vIcon);
}this.remapChildrenHandlingTo(this._frameObject);
},
properties:{appearance:{refine:true,
init:"group-box"}},
members:{_createLegendObject:function(){this._legendObject=new qx.ui.basic.Atom;
this._legendObject.setAppearance("group-box-legend");
this.add(this._legendObject);
},
_createFrameObject:function(){this._frameObject=new qx.ui.layout.CanvasLayout;
this._frameObject.setAppearance("group-box-frame");
this.add(this._frameObject);
},
getFrameObject:function(){return this._frameObject;
},
getLegendObject:function(){return this._legendObject;
},
setLegend:function(vLegend){if(vLegend!==""&&vLegend!==null){this._legendObject.setLabel(vLegend);
this._legendObject.setDisplay(true);
}else{this._legendObject.setDisplay(false);
}},
getLegend:function(){return this._legendObject.getLabel();
},
setIcon:function(vIcon){this._legendObject.setIcon(vIcon);
},
getIcon:function(){this._legendObject.getIcon();
}},
destruct:function(){this._disposeObjects("_legendObject",
"_frameObject");
}});




/* ID: qx.ui.menu.Button */
qx.Class.define("qx.ui.menu.Button",
{extend:qx.ui.layout.HorizontalBoxLayout,
construct:function(vLabel,
vIcon,
vCommand,
vMenu){this.base(arguments);
var io=this._iconObject=new qx.ui.basic.Image;
io.setWidth(16);
io.setAnonymous(true);
var lo=this._labelObject=new qx.ui.basic.Label;
lo.setAnonymous(true);
lo.setSelectable(false);
var so=this._shortcutObject=new qx.ui.basic.Label;
so.setAnonymous(true);
so.setSelectable(false);
var ao=this._arrowObject=new qx.ui.basic.Image;
ao.setAppearance("menu-button-arrow");
ao.setAnonymous(true);
if(vLabel!=null){this.setLabel(vLabel);
}
if(vIcon!=null){this.setIcon(vIcon);
}
if(vCommand!=null){this.setCommand(vCommand);
qx.locale.Manager.getInstance().addEventListener("changeLocale",
function(e){this._applyCommand(vCommand,
vCommand);
},
this);
}
if(vMenu!=null){this.setMenu(vMenu);
}this.initMinWidth();
this.initHeight();
this.addEventListener("mouseup",
this._onmouseup);
},
properties:{allowStretchX:{refine:true,
init:true},
appearance:{refine:true,
init:"menu-button"},
minWidth:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
icon:{check:"String",
apply:"_applyIcon",
nullable:true,
themeable:true},
label:{apply:"_applyLabel",
nullable:true,
dispose:true},
menu:{check:"qx.ui.menu.Menu",
nullable:true,
apply:"_applyMenu"}},
members:{_hasIcon:false,
_hasLabel:false,
_hasShortcut:false,
_hasMenu:false,
hasIcon:function(){return this._hasIcon;
},
hasLabel:function(){return this._hasLabel;
},
hasShortcut:function(){return this._hasShortcut;
},
hasMenu:function(){return this._hasMenu;
},
getIconObject:function(){return this._iconObject;
},
getLabelObject:function(){return this._labelObject;
},
getShortcutObject:function(){return this._shortcutObject;
},
getArrowObject:function(){return this._arrowObject;
},
getParentMenu:function(){var vParent=this.getParent();
if(vParent){vParent=vParent.getParent();
if(vParent&&vParent instanceof qx.ui.menu.Menu){return vParent;
}}return null;
},
_createLayoutImpl:function(){return new qx.ui.menu.ButtonLayoutImpl(this);
},
_applyIcon:function(value,
old){this._iconObject.setSource(value);
if(value&&value!==""){this._hasIcon=true;
if(!old||old===""){this.addAtBegin(this._iconObject);
}}else{this._hasIcon=false;
this.remove(this._iconObject);
}},
_applyLabel:function(value,
old){this._labelObject.setText(value);
if(value&&value!==""){this._hasLabel=true;
if(!old||old===""){this.addAt(this._labelObject,
this.getFirstChild()==this._iconObject?1:0);
}}else{this._hasLabel=false;
this.remove(this._labelObject);
}},
_applyCommand:function(value,
old){var vHtml=value?value.toString():"";
this._shortcutObject.setText(vHtml);
if(qx.util.Validation.isValidString(vHtml)){this._hasShortcut=true;
var vOldHtml=old?old.toString():"";
if(qx.util.Validation.isInvalidString(vOldHtml)){if(this.getLastChild()==this._arrowObject){this.addBefore(this._shortcutObject,
this._arrowObject);
}else{this.addAtEnd(this._shortcutObject);
}}}else{this._hasShortcut=false;
this.remove(this._shortcutObject);
}},
_applyMenu:function(value,
old){if(value){this._hasMenu=true;
if(qx.util.Validation.isInvalidObject(old)){this.addAtEnd(this._arrowObject);
}}else{this._hasMenu=false;
this.remove(this._arrowObject);
}},
_onmouseup:function(e){this.execute();
}},
destruct:function(){this._disposeObjects("_iconObject",
"_labelObject",
"_shortcutObject",
"_arrowObject");
}});




/* ID: qx.ui.menu.Menu */
qx.Class.define("qx.ui.menu.Menu",
{extend:qx.ui.popup.Popup,
construct:function(){this.base(arguments);
var l=this._layout=new qx.ui.menu.Layout;
l.setEdge(0);
this.add(l);
this._openTimer=new qx.client.Timer(this.getOpenInterval());
this._openTimer.addEventListener("interval",
this._onopentimer,
this);
this._closeTimer=new qx.client.Timer(this.getCloseInterval());
this._closeTimer.addEventListener("interval",
this._onclosetimer,
this);
this.addEventListener("mouseover",
this._onmouseover);
this.addEventListener("mousemove",
this._onmouseover);
this.addEventListener("mouseout",
this._onmouseout);
this.addEventListener("keydown",
this._onkeydown);
this.addEventListener("keypress",
this._onkeypress);
this.remapChildrenHandlingTo(this._layout);
this.initWidth();
this.initHeight();
},
properties:{appearance:{refine:true,
init:"menu"},
width:{refine:true,
init:"auto"},
height:{refine:true,
init:"auto"},
iconContentGap:{check:"Integer",
themeable:true,
init:4},
labelShortcutGap:{check:"Integer",
themeable:true,
init:10},
contentArrowGap:{check:"Integer",
themeable:true,
init:8},
contentNonIconPadding:{check:"Integer",
themeable:true,
init:20},
contentNonArrowPadding:{check:"Integer",
themeable:true,
init:8},
hoverItem:{check:"qx.ui.core.Widget",
nullable:true,
apply:"_applyHoverItem"},
openItem:{check:"qx.ui.core.Widget",
nullable:true,
apply:"_applyOpenItem"},
opener:{check:"qx.ui.core.Widget",
nullable:true},
parentMenu:{check:"qx.ui.menu.Menu",
nullable:true},
fastReopen:{check:"Boolean",
init:false},
openInterval:{check:"Integer",
themeable:true,
init:250},
closeInterval:{check:"Integer",
themeable:true,
init:250},
subMenuHorizontalOffset:{check:"Integer",
themeable:true,
init:-3},
subMenuVerticalOffset:{check:"Integer",
themeable:true,
init:-2},
indentShortcuts:{check:"Boolean",
init:true},
maxIconWidth:{_cached:true},
maxLabelWidth:{_cached:true},
maxLabelWidthIncShortcut:{_cached:true},
maxShortcutWidth:{_cached:true},
maxArrowWidth:{_cached:true},
maxContentWidth:{_cached:true},
iconPosition:{_cached:true,
defaultValue:0},
labelPosition:{_cached:true},
shortcutPosition:{_cached:true},
arrowPosition:{_cached:true},
menuButtonNeededWidth:{_cached:true}},
members:{_remappingChildTable:["add",
"remove",
"addAt",
"addAtBegin",
"addAtEnd",
"removeAt",
"addBefore",
"addAfter",
"removeAll",
"getFirstChild",
"getFirstActiveChild",
"getLastChild",
"getLastActiveChild"],
_isFocusRoot:false,
getLayout:function(){return this._layout;
},
isSubElement:function(vElement,
vButtonsOnly){if((vElement.getParent()===this._layout)||((!vButtonsOnly)&&(vElement===this))){return true;
}
for(var a=this._layout.getChildren(),
l=a.length,
i=0;i<l;i++){if(a[i].getMenu&&a[i].getMenu()&&a[i].getMenu().isSubElement(vElement,
vButtonsOnly)){return true;
}}return false;
},
_beforeAppear:function(){qx.ui.layout.CanvasLayout.prototype._beforeAppear.call(this);
qx.ui.menu.Manager.getInstance().add(this);
this.bringToFront();
this._makeActive();
},
_beforeDisappear:function(){qx.ui.layout.CanvasLayout.prototype._beforeDisappear.call(this);
qx.ui.menu.Manager.getInstance().remove(this);
this._makeInactive();
this.setHoverItem(null);
this.setOpenItem(null);
var vOpener=this.getOpener();
if(vOpener){vOpener.removeState("pressed");
}},
_applyHoverItem:function(value,
old){if(old){old.removeState("over");
}
if(value){value.addState("over");
}},
_applyOpenItem:function(value,
old){var vMakeActive=false;
if(old){var vOldSub=old.getMenu();
if(vOldSub){vOldSub.setParentMenu(null);
vOldSub.setOpener(null);
vOldSub.hide();
}}
if(value){var vSub=value.getMenu();
if(vSub){vSub.setOpener(value);
vSub.setParentMenu(this);
var pl=value.getElement();
var el=this.getElement();
vSub.setTop(qx.html.Location.getPageBoxTop(pl)+this.getSubMenuVerticalOffset());
vSub.setLeft(qx.html.Location.getPageBoxLeft(el)+qx.html.Dimension.getBoxWidth(el)+this.getSubMenuHorizontalOffset());
vSub.show();
}}},
_computeMaxIconWidth:function(){var ch=this.getLayout().getChildren(),
chl=ch.length,
chc,
m=0;
for(var i=0;i<chl;i++){chc=ch[i];
if(chc.hasIcon()){m=Math.max(m,
16);
}}return m;
},
_computeMaxLabelWidth:function(){var ch=this.getLayout().getChildren(),
chl=ch.length,
chc,
m=0;
for(var i=0;i<chl;i++){chc=ch[i];
if(chc.hasLabel()){m=Math.max(m,
chc.getLabelObject().getPreferredBoxWidth());
}}return m;
},
_computeMaxLabelWidthIncShortcut:function(){var ch=this.getLayout().getChildren(),
chl=ch.length,
chc,
m=0;
for(var i=0;i<chl;i++){chc=ch[i];
if(chc.hasLabel()&&chc.hasShortcut()){m=Math.max(m,
chc.getLabelObject().getPreferredBoxWidth());
}}return m;
},
_computeMaxShortcutWidth:function(){var ch=this.getLayout().getChildren(),
chl=ch.length,
chc,
m=0;
for(var i=0;i<chl;i++){chc=ch[i];
if(chc.hasShortcut()){m=Math.max(m,
chc.getShortcutObject().getPreferredBoxWidth());
}}return m;
},
_computeMaxArrowWidth:function(){var ch=this.getLayout().getChildren(),
chl=ch.length,
chc,
m=0;
for(var i=0;i<chl;i++){chc=ch[i];
if(chc.hasMenu()){m=Math.max(m,
4);
}}return m;
},
_computeMaxContentWidth:function(){var vSum;
var lw=this.getMaxLabelWidth();
var sw=this.getMaxShortcutWidth();
if(this.getIndentShortcuts()){var vTemp=sw+this.getMaxLabelWidthIncShortcut();
if(sw>0){vTemp+=this.getLabelShortcutGap();
}vSum=Math.max(lw,
vTemp);
}else{vSum=lw+sw;
if(lw>0&&sw>0){vSum+=this.getLabelShortcutGap();
}}return vSum;
},
_computeIconPosition:function(){return 0;
},
_computeLabelPosition:function(){var v=this.getMaxIconWidth();
return v>0?v+this.getIconContentGap():this.getContentNonIconPadding();
},
_computeShortcutPosition:function(){return this.getLabelPosition()+this.getMaxContentWidth()-this.getMaxShortcutWidth();
},
_computeArrowPosition:function(){var v=this.getMaxContentWidth();
return this.getLabelPosition()+(v>0?v+this.getContentArrowGap():v);
},
_invalidateMaxIconWidth:function(){this._cachedMaxIconWidth=null;
this._invalidateLabelPosition();
this._invalidateMenuButtonNeededWidth();
},
_invalidateMaxLabelWidth:function(){this._cachedMaxLabelWidth=null;
this._invalidateShortcutPosition();
this._invalidateMaxLabelWidthIncShortcut();
this._invalidateMaxContentWidth();
this._invalidateMenuButtonNeededWidth();
},
_invalidateMaxShortcutWidth:function(){this._cachedMaxShortcutWidth=null;
this._invalidateArrowPosition();
this._invalidateMaxContentWidth();
this._invalidateMenuButtonNeededWidth();
},
_invalidateMaxLabelWidth:function(){this._cachedMaxArrowWidth=null;
this._invalidateMenuButtonNeededWidth();
},
_invalidateLabelPosition:function(){this._cachedLabelPosition=null;
this._invalidateShortcutPosition();
},
_invalidateShortcutPosition:function(){this._cachedShortcutPosition=null;
this._invalidateArrowPosition();
},
_computeMenuButtonNeededWidth:function(){var vSum=0;
var vMaxIcon=this.getMaxIconWidth();
var vMaxContent=this.getMaxContentWidth();
var vMaxArrow=this.getMaxArrowWidth();
if(vMaxIcon>0){vSum+=vMaxIcon;
}else{vSum+=this.getContentNonIconPadding();
}
if(vMaxContent>0){if(vMaxIcon>0){vSum+=this.getIconContentGap();
}vSum+=vMaxContent;
}
if(vMaxArrow>0){if(vMaxIcon>0||vMaxContent>0){vSum+=this.getContentArrowGap();
}vSum+=vMaxArrow;
}else{vSum+=this.getContentNonArrowPadding();
}return vSum;
},
_onmouseover:function(e){var vParent=this.getParentMenu();
if(vParent){vParent._closeTimer.stop();
var vOpener=this.getOpener();
if(vOpener){vParent.setHoverItem(vOpener);
}}var t=e.getTarget();
if(t==this){this._openTimer.stop();
this._closeTimer.start();
this.setHoverItem(null);
return;
}var vOpen=this.getOpenItem();
if(vOpen){this.setHoverItem(t);
this._openTimer.stop();
if(t.hasMenu()){if(this.getFastReopen()){this.setOpenItem(t);
this._closeTimer.stop();
}else{this._openTimer.start();
}}else{this._closeTimer.start();
}}else{this.setHoverItem(t);
this._openTimer.stop();
if(t.hasMenu()){this._openTimer.start();
}}},
_onmouseout:function(e){this._openTimer.stop();
var t=e.getTarget();
if(t!=this&&t.hasMenu()){this._closeTimer.start();
}this.setHoverItem(null);
},
_onopentimer:function(e){this._openTimer.stop();
var vHover=this.getHoverItem();
if(vHover&&vHover.hasMenu()){this.setOpenItem(vHover);
}},
_onclosetimer:function(e){this._closeTimer.stop();
this.setOpenItem(null);
},
_onkeydown:function(e){if(e.getKeyIdentifier()=="Enter"){this._onkeydown_enter(e);
}e.preventDefault();
},
_onkeypress:function(e){switch(e.getKeyIdentifier()){case "Up":this._onkeypress_up(e);
break;
case "Down":this._onkeypress_down(e);
break;
case "Left":this._onkeypress_left(e);
break;
case "Right":this._onkeypress_right(e);
break;
default:return;
}e.preventDefault();
},
_onkeypress_up:function(e){var vHover=this.getHoverItem();
var vPrev=vHover?vHover.isFirstChild()?this.getLastActiveChild():vHover.getPreviousActiveSibling([qx.ui.menu.Separator]):this.getLastActiveChild();
this.setHoverItem(vPrev);
},
_onkeypress_down:function(e){var vHover=this.getHoverItem();
var vNext=vHover?vHover.isLastChild()?this.getFirstActiveChild():vHover.getNextActiveSibling([qx.ui.menu.Separator]):this.getFirstActiveChild();
this.setHoverItem(vNext);
},
_onkeypress_left:function(e){var vOpener=this.getOpener();
if(vOpener instanceof qx.ui.menu.Button){var vOpenerParent=this.getOpener().getParentMenu();
vOpenerParent.setOpenItem(null);
vOpenerParent.setHoverItem(vOpener);
vOpenerParent._makeActive();
}else if(vOpener instanceof qx.ui.toolbar.MenuButton){var vToolBar=vOpener.getParentToolBar();
this.getFocusRoot().setActiveChild(vToolBar);
vToolBar._onkeypress(e);
}},
_onkeypress_right:function(e){var vHover=this.getHoverItem();
if(vHover){var vMenu=vHover.getMenu();
if(vMenu){this.setOpenItem(vHover);
vMenu.setHoverItem(vMenu.getFirstActiveChild());
return;
}}else if(!this.getOpenItem()){var vFirst=this.getLayout().getFirstActiveChild();
if(vFirst){vFirst.hasMenu()?this.setOpenItem(vFirst):this.setHoverItem(vFirst);
}}var vOpener=this.getOpener();
if(vOpener instanceof qx.ui.toolbar.MenuButton){var vToolBar=vOpener.getParentToolBar();
this.getFocusRoot().setActiveChild(vToolBar);
vToolBar._onkeypress(e);
}else if(vOpener instanceof qx.ui.menu.Button&&vHover){var vOpenerParent=vOpener.getParentMenu();
while(vOpenerParent&&vOpenerParent instanceof qx.ui.menu.Menu){vOpener=vOpenerParent.getOpener();
if(vOpener instanceof qx.ui.menu.Button){vOpenerParent=vOpener.getParentMenu();
}else{if(vOpener){vOpenerParent=vOpener.getParent();
}break;
}}
if(vOpenerParent instanceof qx.ui.toolbar.Part){vOpenerParent=vOpenerParent.getParent();
}
if(vOpenerParent instanceof qx.ui.toolbar.ToolBar){this.getFocusRoot().setActiveChild(vOpenerParent);
vOpenerParent._onkeypress(e);
}}},
_onkeydown_enter:function(e){var vHover=this.getHoverItem();
if(vHover){vHover.execute();
}qx.ui.menu.Manager.getInstance().update();
}},
destruct:function(){this.hide();
this._disposeObjects("_openTimer",
"_closeTimer",
"_layout");
}});




/* ID: qx.ui.menu.Layout */
qx.Class.define("qx.ui.menu.Layout",
{extend:qx.ui.layout.VerticalBoxLayout,
properties:{anonymous:{refine:true,
init:true},
appearance:{refine:true,
init:"menu-layout"}},
members:{_createLayoutImpl:function(){return new qx.ui.menu.MenuLayoutImpl(this);
}}});




/* ID: qx.ui.menu.MenuLayoutImpl */
qx.Class.define("qx.ui.menu.MenuLayoutImpl",
{extend:qx.ui.layout.impl.VerticalBoxLayoutImpl,
construct:function(vWidget){this.base(arguments,
vWidget);
this.setEnableFlexSupport(false);
},
members:{updateChildrenOnJobQueueFlush:function(vQueue){var vWidget=this.getWidget();
var ch,
chc;
if(vQueue.preferredInnerWidth){var ch=vWidget.getChildren(),
chl=ch.length,
chc;
var sch,
schl;
for(var i=0;i<chl;i++){chc=ch[i];
sch=chc.getChildren();
schl=sch.length;
for(var j=0;j<schl;j++){sch[j].addToLayoutChanges("locationX");
}}}return this.base(arguments,
vQueue);
}}});




/* ID: qx.ui.menu.Separator */
qx.Class.define("qx.ui.menu.Separator",
{extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
this.initHeight();
this.setStyleProperty("fontSize",
"0");
this.setStyleProperty("lineHeight",
"0");
this._line=new qx.ui.basic.Terminator;
this._line.setAnonymous(true);
this._line.setAppearance("menu-separator-line");
this.add(this._line);
this.addEventListener("mousedown",
this._onmousedown);
},
properties:{height:{refine:true,
init:"auto"},
appearance:{refine:true,
init:"menu-separator"}},
members:{hasIcon:qx.lang.Function.returnFalse,
hasLabel:qx.lang.Function.returnFalse,
hasShortcut:qx.lang.Function.returnFalse,
hasMenu:qx.lang.Function.returnFalse,
_onmousedown:function(e){e.stopPropagation();
}},
destruct:function(){this._disposeObjects("_line");
}});




/* ID: qx.ui.menu.ButtonLayoutImpl */
qx.Class.define("qx.ui.menu.ButtonLayoutImpl",
{extend:qx.ui.layout.impl.HorizontalBoxLayoutImpl,
construct:function(vWidget){this.base(arguments,
vWidget);
this.setEnableFlexSupport(false);
},
members:{computeChildrenNeededWidth:function(){var vWidget=this.getWidget();
var vMenu=vWidget.getParent().getParent();
return vMenu.getMenuButtonNeededWidth();
},
updateSelfOnChildOuterWidthChange:function(vChild){var vWidget=this.getWidget();
var vMenu=vWidget.getParent().getParent();
switch(vChild){case vWidget._iconObject:vMenu._invalidateMaxIconWidth();
break;
case vWidget._labelObject:vMenu._invalidateMaxLabelWidth();
break;
case vWidget._shortcutObject:vMenu._invalidateMaxShortcutWidth();
break;
case vWidget._arrowObject:vMenu._invalidateMaxArrowWidth();
break;
}return this.base(arguments,
vChild);
},
layoutChild_locationX:function(vChild,
vJobs){var vWidget=this.getWidget();
var vMenu=vWidget.getParent().getParent();
var vPos=null;
switch(vChild){case vWidget._iconObject:vPos=vMenu.getIconPosition();
break;
case vWidget._labelObject:vPos=vMenu.getLabelPosition();
break;
case vWidget._shortcutObject:vPos=vMenu.getShortcutPosition();
break;
case vWidget._arrowObject:vPos=vMenu.getArrowPosition();
break;
}
if(vPos!=null){vPos+=vWidget.getPaddingLeft();
vChild._renderRuntimeLeft(vPos);
}}}});




/* ID: qx.ui.menu.CheckBox */
qx.Class.define("qx.ui.menu.CheckBox",
{extend:qx.ui.menu.Button,
construct:function(vLabel,
vCommand,
vChecked){this.base(arguments,
vLabel,
null,
vCommand);
if(vChecked!=null){this.setChecked(vChecked);
}},
properties:{appearance:{refine:true,
init:"menu-check-box"},
name:{check:"String"},
value:{check:"String",
event:"changeValue"},
checked:{check:"Boolean",
init:false,
apply:"_applyChecked"}},
members:{_applyChecked:function(value,
old){value===true?this.addState("checked"):this.removeState("checked");
},
execute:function(){this._processExecute();
this.base(arguments);
},
_processExecute:function(){this.toggleChecked();
}}});




/* ID: qx.ui.menu.RadioButton */
qx.Class.define("qx.ui.menu.RadioButton",
{extend:qx.ui.menu.CheckBox,
properties:{appearance:{refine:true,
init:"menu-radio-button"},
manager:{check:"qx.ui.selection.RadioManager",
nullable:true,
apply:"_applyManager"}},
members:{_applyChecked:function(value,
old){this.base(arguments,
value,
old);
var vManager=this.getManager();
if(vManager){vManager.handleItemChecked(this,
value);
}},
_applyManager:function(value,
old){if(old){old.remove(this);
}
if(value){value.add(this);
}},
_applyName:function(value,
old){if(this.getManager()){this.getManager().setName(value);
}},
_processExecute:function(){this.setChecked(true);
}}});




/* ID: qx.ui.menubar.Button */
qx.Class.define("qx.ui.menubar.Button",
{extend:qx.ui.toolbar.MenuButton});




/* ID: qx.ui.menubar.MenuBar */
qx.Class.define("qx.ui.menubar.MenuBar",
{extend:qx.ui.toolbar.ToolBar});




/* ID: qx.event.handler.DragAndDropHandler */
qx.Class.define("qx.event.handler.DragAndDropHandler",
{type:"singleton",
extend:qx.util.manager.Object,
construct:function(){this.base(arguments);
this.__data={};
this.__actions={};
this.__cursors={};
var vCursor,
vAction;
var vActions=["move",
"copy",
"alias",
"nodrop"];
for(var i=0,
l=vActions.length;i<l;i++){vAction=vActions[i];
vCursor=this.__cursors[vAction]=new qx.ui.basic.Image;
vCursor.setAppearance("cursor-dnd-"+vAction);
vCursor.setZIndex(1e8);
}},
properties:{sourceWidget:{check:"qx.ui.core.Widget",
nullable:true},
destinationWidget:{check:"qx.ui.core.Widget",
nullable:true,
apply:"_applyDestinationWidget"},
currentAction:{check:"String",
nullable:true},
defaultCursorDeltaX:{check:"Integer",
init:5},
defaultCursorDeltaY:{check:"Integer",
init:15}},
members:{__lastDestinationEvent:null,
_applyDestinationWidget:function(value,
old){if(value){value.dispatchEvent(new qx.event.type.DragEvent("dragdrop",
this.__lastDestinationEvent,
value,
this.getSourceWidget()));
this.__lastDestinationEvent=null;
}},
addData:function(vMimeType,
vData){this.__data[vMimeType]=vData;
},
getData:function(vMimeType){return this.__data[vMimeType];
},
clearData:function(){this.__data={};
},
getDropDataTypes:function(){var vDestination=this.getDestinationWidget();
var vDropTypes=[];
if(!vDestination){return vDropTypes;
}var vDropDataTypes=vDestination.getDropDataTypes();
for(var i=0,
l=vDropDataTypes.length;i<l;i++){if(vDropDataTypes[i] in this.__data){vDropTypes.push(vDropDataTypes[i]);
}}return vDropTypes;
},
getDropTarget:qx.core.Variant.select("qx.client",
{"gecko":function(e){var vCurrent=e.getTarget();
if(vCurrent==this.__dragCache.sourceWidget){vCurrent=qx.event.handler.EventHandler.getTargetObject(qx.html.ElementFromPoint.getElementFromPoint(e.getPageX(),
e.getPageY()));
}else{vCurrent=qx.event.handler.EventHandler.getTargetObject(null,
vCurrent);
}
while(vCurrent!=null&&vCurrent!=this.__dragCache.sourceWidget){if(!vCurrent.supportsDrop(this.__dragCache)){return null;
}
if(this.supportsDrop(vCurrent)){return vCurrent;
}vCurrent=vCurrent.getParent();
}return null;
},
"default":function(e){var vCurrent=e.getTarget();
while(vCurrent!=null){if(!vCurrent.supportsDrop(this.__dragCache)){return null;
}
if(this.supportsDrop(vCurrent)){return vCurrent;
}vCurrent=vCurrent.getParent();
}return null;
}}),
startDrag:function(){if(!this.__dragCache){throw new Error("Invalid usage of startDrag. Missing dragInfo!");
}this.__dragCache.dragHandlerActive=true;
this.setSourceWidget(this.__dragCache.sourceWidget);
if(this.__feedbackWidget){this.__feedbackWidget.setVisibility(false);
var doc=qx.ui.core.ClientDocument.getInstance();
doc.add(this.__feedbackWidget);
}},
_fireUserEvents:function(fromWidget,
toWidget,
e){if(fromWidget&&fromWidget!=toWidget&&fromWidget.hasEventListeners("dragout")){fromWidget.dispatchEvent(new qx.event.type.DragEvent("dragout",
e,
fromWidget,
toWidget),
true);
}
if(toWidget){if(fromWidget!=toWidget&&toWidget.hasEventListeners("dragover")){toWidget.dispatchEvent(new qx.event.type.DragEvent("dragover",
e,
toWidget,
fromWidget),
true);
}
if(toWidget.hasEventListeners("dragmove")){toWidget.dispatchEvent(new qx.event.type.DragEvent("dragmove",
e,
toWidget,
null),
true);
}}},
handleMouseEvent:function(e){switch(e.getType()){case "mousedown":return this._handleMouseDown(e);
case "mouseup":return this._handleMouseUp(e);
case "mousemove":return this._handleMouseMove(e);
}},
_handleMouseDown:function(e){if(e.getDefaultPrevented()||!e.isLeftButtonPressed()){return;
}this.__dragCache={startScreenX:e.getScreenX(),
startScreenY:e.getScreenY(),
pageX:e.getPageX(),
pageY:e.getPageY(),
sourceWidget:e.getTarget(),
sourceTopLevel:e.getTarget().getTopLevelWidget(),
dragHandlerActive:false,
hasFiredDragStart:false};
},
_handleMouseMove:function(e){if(!this.__dragCache){return;
}if(this.__dragCache.dragHandlerActive){this.__dragCache.pageX=e.getPageX();
this.__dragCache.pageY=e.getPageY();
var currentDropTarget=this.getDropTarget(e);
this.setCurrentAction(currentDropTarget?this._evalNewAction(e.isShiftPressed(),
e.isCtrlPressed(),
e.isAltPressed()):null);
this._fireUserEvents(this.__dragCache.currentDropWidget,
currentDropTarget,
e);
this.__dragCache.currentDropWidget=currentDropTarget;
this._renderCursor();
this._renderFeedbackWidget();
}else if(!this.__dragCache.hasFiredDragStart){if(Math.abs(e.getScreenX()-this.__dragCache.startScreenX)>5||Math.abs(e.getScreenY()-this.__dragCache.startScreenY)>5){this.__dragCache.sourceWidget.dispatchEvent(new qx.event.type.DragEvent("dragstart",
e,
this.__dragCache.sourceWidget),
true);
this.__dragCache.hasFiredDragStart=true;
if(this.__dragCache.dragHandlerActive){this._fireUserEvents(this.__dragCache.currentDropWidget,
this.__dragCache.sourceWidget,
e);
this.__dragCache.currentDropWidget=this.__dragCache.sourceWidget;
qx.ui.core.ClientDocument.getInstance().setCapture(true);
}}}},
_handleMouseUp:function(e){if(!this.__dragCache){return;
}
if(this.__dragCache.dragHandlerActive){this._endDrag(this.getDropTarget(e),
e);
}else{this.__dragCache=null;
}},
handleKeyEvent:function(e){if(!this.__dragCache){return;
}
switch(e.getType()){case "keydown":this._handleKeyDown(e);
return;
case "keyup":this._handleKeyUp(e);
return;
}},
_handleKeyDown:function(e){if(e.getKeyIdentifier()=="Escape"){this.cancelDrag(e);
}else if(this.getCurrentAction()!=null){switch(e.getKeyIdentifier()){case "Shift":case "Control":case "Alt":this.setAction(this._evalNewAction(e.isShiftPressed(),
e.isCtrlPressed(),
e.isAltPressed()));
this._renderCursor();
e.preventDefault();
}}},
_handleKeyUp:function(e){var bShiftPressed=e.getKeyIdentifier()=="Shift";
var bCtrlPressed=e.getKeyIdentifier()=="Control";
var bAltPressed=e.getKeyIdentifier()=="Alt";
if(bShiftPressed||bCtrlPressed||bAltPressed){if(this.getCurrentAction()!=null){this.setAction(this._evalNewAction(!bShiftPressed&&e.isShiftPressed(),
!bCtrlPressed&&e.isCtrlPressed(),
!bAltPressed&&e.isAltPressed()));
this._renderCursor();
e.preventDefault();
}}},
cancelDrag:function(e){if(!this.__dragCache){return;
}
if(this.__dragCache.dragHandlerActive){this._endDrag(null,
e);
}else{this.__dragCache=null;
}},
globalCancelDrag:function(){if(this.__dragCache&&this.__dragCache.dragHandlerActive){this._endDragCore();
}},
_endDrag:function(currentDestinationWidget,
e){if(currentDestinationWidget){this.__lastDestinationEvent=e;
this.setDestinationWidget(currentDestinationWidget);
}this.getSourceWidget().dispatchEvent(new qx.event.type.DragEvent("dragend",
e,
this.getSourceWidget(),
currentDestinationWidget),
true);
this._fireUserEvents(this.__dragCache&&this.__dragCache.currentDropWidget,
null,
e);
this._endDragCore();
},
_endDragCore:function(){if(this.__feedbackWidget){var doc=qx.ui.core.ClientDocument.getInstance();
doc.remove(this.__feedbackWidget);
if(this.__feedbackAutoDispose){this.__feedbackWidget.dispose();
}this.__feedbackWidget=null;
}var oldCursor=this.__cursor;
if(oldCursor){oldCursor._style.display="none";
this.__cursor=null;
}this._cursorDeltaX=null;
this._cursorDeltaY=null;
if(this.__dragCache){this.__dragCache.currentDropWidget=null;
this.__dragCache=null;
}qx.ui.core.ClientDocument.getInstance().setCapture(false);
this.clearData();
this.clearActions();
this.setSourceWidget(null);
this.setDestinationWidget(null);
},
setCursorPosition:function(deltaX,
deltaY){this._cursorDeltaX=deltaX;
this._cursorDeltaY=deltaY;
},
_renderCursor:function(){var vNewCursor;
var vOldCursor=this.__cursor;
switch(this.getCurrentAction()){case "move":vNewCursor=this.__cursors.move;
break;
case "copy":vNewCursor=this.__cursors.copy;
break;
case "alias":vNewCursor=this.__cursors.alias;
break;
default:vNewCursor=this.__cursors.nodrop;
}if(vNewCursor!=vOldCursor&&vOldCursor!=null){vOldCursor._style.display="none";
}if(!vNewCursor._initialLayoutDone){qx.ui.core.ClientDocument.getInstance().add(vNewCursor);
qx.ui.core.Widget.flushGlobalQueues();
}vNewCursor._renderRuntimeLeft(this.__dragCache.pageX+((this._cursorDeltaX!=null)?this._cursorDeltaX:this.getDefaultCursorDeltaX()));
vNewCursor._renderRuntimeTop(this.__dragCache.pageY+((this._cursorDeltaY!=null)?this._cursorDeltaY:this.getDefaultCursorDeltaY()));
if(vNewCursor!=vOldCursor){vNewCursor._style.display="";
}this.__cursor=vNewCursor;
},
supportsDrop:function(vWidget){var vTypes=vWidget.getDropDataTypes();
if(!vTypes){return false;
}
for(var i=0;i<vTypes.length;i++){if(vTypes[i] in this.__data){return true;
}}return false;
},
addAction:function(vAction,
vForce){this.__actions[vAction]=true;
if(vForce||this.getCurrentAction()==null){this.setCurrentAction(vAction);
}},
clearActions:function(){this.__actions={};
this.setCurrentAction(null);
},
removeAction:function(vAction){delete this.__actions[vAction];
if(this.getCurrentAction()==vAction){this.setCurrentAction(null);
}},
setAction:function(vAction){if(vAction!=null&&!(vAction in this.__actions)){this.addAction(vAction,
true);
}else{this.setCurrentAction(vAction);
}},
_evalNewAction:function(vKeyShift,
vKeyCtrl,
vKeyAlt){if(vKeyShift&&vKeyCtrl&&"alias" in this.__actions){return "alias";
}else if(vKeyShift&&vKeyAlt&&"copy" in this.__actions){return "copy";
}else if(vKeyShift&&"move" in this.__actions){return "move";
}else if(vKeyAlt&&"alias" in this.__actions){return "alias";
}else if(vKeyCtrl&&"copy" in this.__actions){return "copy";
}else{for(var vAction in this.__actions){return vAction;
}}return null;
},
setFeedbackWidget:function(widget,
deltaX,
deltaY,
autoDisposeWidget){this.__feedbackWidget=widget;
this.__feedbackDeltaX=(deltaX!=null)?deltaX:10;
this.__feedbackDeltaY=(deltaY!=null)?deltaY:10;
this.__feedbackAutoDispose=autoDisposeWidget?true:false;
},
_renderFeedbackWidget:function(){if(this.__feedbackWidget){this.__feedbackWidget.setVisibility(true);
this.__feedbackWidget._renderRuntimeLeft(this.__dragCache.pageX+this.__feedbackDeltaX);
this.__feedbackWidget._renderRuntimeTop(this.__dragCache.pageY+this.__feedbackDeltaY);
}}},
destruct:function(){this._disposeObjectDeep("__cursors",
1);
this._disposeObjects("__feedbackWidget");
this._disposeFields("__dragCache",
"__data",
"__actions",
"__lastDestinationEvent");
}});




/* ID: qx.event.type.DragEvent */
qx.Class.define("qx.event.type.DragEvent",
{extend:qx.event.type.MouseEvent,
construct:function(vType,
vMouseEvent,
vTarget,
vRelatedTarget){this._mouseEvent=vMouseEvent;
var vOriginalTarget=null;
switch(vType){case "dragstart":case "dragover":vOriginalTarget=vMouseEvent.getOriginalTarget();
}this.base(arguments,
vType,
vMouseEvent.getDomEvent(),
vTarget.getElement(),
vTarget,
vOriginalTarget,
vRelatedTarget);
},
members:{getMouseEvent:function(){return this._mouseEvent;
},
startDrag:function(){if(this.getType()!="dragstart"){throw new Error("qx.event.type.DragEvent startDrag can only be called during the dragstart event: "+this.getType());
}this.stopPropagation();
qx.event.handler.DragAndDropHandler.getInstance().startDrag();
},
addData:function(sType,
oData){qx.event.handler.DragAndDropHandler.getInstance().addData(sType,
oData);
},
getData:function(sType){return qx.event.handler.DragAndDropHandler.getInstance().getData(sType);
},
clearData:function(){qx.event.handler.DragAndDropHandler.getInstance().clearData();
},
getDropDataTypes:function(){return qx.event.handler.DragAndDropHandler.getInstance().getDropDataTypes();
},
addAction:function(sAction){qx.event.handler.DragAndDropHandler.getInstance().addAction(sAction);
},
removeAction:function(sAction){qx.event.handler.DragAndDropHandler.getInstance().removeAction(sAction);
},
getAction:function(){return qx.event.handler.DragAndDropHandler.getInstance().getCurrentAction();
},
clearActions:function(){qx.event.handler.DragAndDropHandler.getInstance().clearActions();
},
setFeedbackWidget:function(widget,
deltaX,
deltaY,
autoDisposeWidget){qx.event.handler.DragAndDropHandler.getInstance().setFeedbackWidget(widget,
deltaX,
deltaY,
autoDisposeWidget);
},
setCursorPosition:function(deltaX,
deltaY){qx.event.handler.DragAndDropHandler.getInstance().setCursorPosition(deltaX,
deltaY);
}},
destruct:function(){this._disposeFields("_mouseEvent");
}});




/* ID: qx.html.ElementFromPoint */
qx.Class.define("qx.html.ElementFromPoint",
{statics:{getElementFromPoint:function(x,
y){return this.getElementFromPointHandler(document.body,
x,
y);
},
getElementFromPointHandler:function(node,
x,
y,
recursive){var ch=node.childNodes;
var childNodesLength=ch.length-1;
if(childNodesLength<0){return null;
}var childNode,
subres,
ret;
do{childNode=ch[childNodesLength];
ret=this.getElementFromPointChecker(childNode,
x,
y);
if(ret){if(typeof recursive==="boolean"&&recursive==false){return childNode;
}else{subres=this.getElementFromPointHandler(childNode,
x-ret[0]-qx.html.Style.getBorderLeft(childNode),
y-ret[2]-qx.html.Style.getBorderTop(childNode),
true);
return subres?subres:childNode;
}}}while(childNodesLength--);
return null;
},
getElementFromPointChecker:function(element,
x,
y){var xstart,
ystart,
xstop,
ystop;
if(element.nodeType!=qx.dom.Node.ELEMENT){return false;
}xstart=qx.html.Offset.getLeft(element);
if(x>xstart){ystart=qx.html.Offset.getTop(element);
if(y>ystart){xstop=xstart+element.offsetWidth;
if(x<xstop){ystop=ystart+element.offsetHeight;
if(y<ystop){return [xstart,
xstop,
ystart,
ystop];
}}}}return false;
},
getElementAbsolutePointChecker:function(element,
x,
y){var xstart,
ystart,
xstop,
ystop;
if(!element||element.nodeType!=qx.dom.Node.ELEMENT){return false;
}xstart=qx.html.Location.getPageBoxLeft(element);
if(x>xstart){ystart=qx.html.Location.getPageBoxTop(element);
if(y>ystart){xstop=xstart+element.offsetWidth;
if(x<xstop){ystop=ystart+element.offsetHeight;
if(y<ystop){return [xstart,
xstop,
ystart,
ystop];
}}}}return false;
}}});




/* ID: qx.html.Iframe */
qx.Class.define("qx.html.Iframe",
{statics:{getWindow:qx.core.Variant.select("qx.client",
{"mshtml":function(vIframe){try{return vIframe.contentWindow;
}catch(ex){return null;
}},
"default":function(vIframe){try{var vDoc=qx.html.Iframe.getDocument(vIframe);
return vDoc?vDoc.defaultView:null;
}catch(ex){return null;
}}}),
getDocument:qx.core.Variant.select("qx.client",
{"mshtml":function(vIframe){try{var vWin=qx.html.Iframe.getWindow(vIframe);
return vWin?vWin.document:null;
}catch(ex){return null;
}},
"default":function(vIframe){try{return vIframe.contentDocument;
}catch(ex){return null;
}}}),
getBody:function(vIframe){var vDoc=qx.html.Iframe.getDocument(vIframe);
return vDoc?vDoc.getElementsByTagName("body")[0]:null;
}}});




/* ID: qx.io.remote.AbstractRemoteTransport */
qx.Class.define("qx.io.remote.AbstractRemoteTransport",
{type:"abstract",
extend:qx.core.Target,
construct:function(){this.base(arguments);
},
events:{"created":"qx.event.type.Event",
"configured":"qx.event.type.Event",
"sending":"qx.event.type.Event",
"receiving":"qx.event.type.Event",
"completed":"qx.event.type.Event",
"aborted":"qx.event.type.Event",
"failed":"qx.event.type.Event",
"timeout":"qx.event.type.Event"},
properties:{url:{check:"String",
nullable:true},
method:{check:"String",
nullable:true},
asynchronous:{check:"Boolean",
nullable:true},
data:{check:"String",
nullable:true},
username:{check:"String",
nullable:true},
password:{check:"String",
nullable:true},
state:{check:["created",
"configured",
"sending",
"receiving",
"completed",
"aborted",
"timeout",
"failed"],
init:"created",
event:"changeState",
apply:"_applyState"},
requestHeaders:{check:"Object",
nullable:true},
parameters:{check:"Object",
nullable:true},
formFields:{check:"Object",
nullable:true},
responseType:{check:"String",
nullable:true},
useBasicHttpAuth:{check:"Boolean",
nullable:true}},
members:{send:function(){throw new Error("send is abstract");
},
abort:function(){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Aborting...");
}};
this.setState("aborted");
},
timeout:function(){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Timeout...");
}};
this.setState("timeout");
},
failed:function(){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Failed...");
}};
this.setState("failed");
},
setRequestHeader:function(vLabel,
vValue){throw new Error("setRequestHeader is abstract");
},
getResponseHeader:function(vLabel){throw new Error("getResponseHeader is abstract");
},
getResponseHeaders:function(){throw new Error("getResponseHeaders is abstract");
},
getStatusCode:function(){throw new Error("getStatusCode is abstract");
},
getStatusText:function(){throw new Error("getStatusText is abstract");
},
getResponseText:function(){throw new Error("getResponseText is abstract");
},
getResponseXml:function(){throw new Error("getResponseXml is abstract");
},
getFetchedLength:function(){throw new Error("getFetchedLength is abstract");
},
_applyState:function(value,
old){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("State: "+value);
}};
switch(value){case "created":this.createDispatchEvent("created");
break;
case "configured":this.createDispatchEvent("configured");
break;
case "sending":this.createDispatchEvent("sending");
break;
case "receiving":this.createDispatchEvent("receiving");
break;
case "completed":this.createDispatchEvent("completed");
break;
case "aborted":this.createDispatchEvent("aborted");
break;
case "failed":this.createDispatchEvent("failed");
break;
case "timeout":this.createDispatchEvent("timeout");
break;
}return true;
}}});




/* ID: qx.io.remote.Exchange */
qx.Class.define("qx.io.remote.Exchange",
{extend:qx.core.Target,
construct:function(vRequest){this.base(arguments);
this.setRequest(vRequest);
vRequest.setTransport(this);
},
events:{"sending":"qx.event.type.Event",
"receiving":"qx.event.type.Event",
"completed":"qx.io.remote.Response",
"aborted":"qx.io.remote.Response",
"failed":"qx.io.remote.Response",
"timeout":"qx.io.remote.Response"},
statics:{typesOrder:["qx.io.remote.XmlHttpTransport",
"qx.io.remote.IframeTransport",
"qx.io.remote.ScriptTransport"],
typesReady:false,
typesAvailable:{},
typesSupported:{},
registerType:function(vClass,
vId){qx.io.remote.Exchange.typesAvailable[vId]=vClass;
},
initTypes:function(){if(qx.io.remote.Exchange.typesReady){return;
}
for(var vId in qx.io.remote.Exchange.typesAvailable){var vTransporterImpl=qx.io.remote.Exchange.typesAvailable[vId];
if(vTransporterImpl.isSupported()){qx.io.remote.Exchange.typesSupported[vId]=vTransporterImpl;
}}qx.io.remote.Exchange.typesReady=true;
if(qx.lang.Object.isEmpty(qx.io.remote.Exchange.typesSupported)){throw new Error("No supported transport types were found!");
}},
canHandle:function(vImpl,
vNeeds,
vResponseType){if(!qx.lang.Array.contains(vImpl.handles.responseTypes,
vResponseType)){return false;
}
for(var vKey in vNeeds){if(!vImpl.handles[vKey]){return false;
}}return true;
},
_nativeMap:{0:"created",
1:"configured",
2:"sending",
3:"receiving",
4:"completed"},
wasSuccessful:function(vStatusCode,
vReadyState,
vIsLocal){if(vIsLocal){switch(vStatusCode){case null:case 0:return true;
case -1:return vReadyState<4;
default:return typeof vStatusCode==="undefined";
}}else{switch(vStatusCode){case -1:{if(qx.core.Setting.get("qx.ioRemoteDebug")&&vReadyState>3){qx.log.Logger.getClassLogger(qx.io.remote.Exchange).debug("Failed with statuscode: -1 at readyState "+vReadyState);
}};
return vReadyState<4;
case 200:case 304:return true;
case 201:case 202:case 203:case 204:case 205:return true;
case 206:{if(qx.core.Setting.get("qx.ioRemoteDebug")&&vReadyState===4){qx.log.Logger.getClassLogger(qx.io.remote.Exchange).debug("Failed with statuscode: 206 (Partial content while being complete!)");
}};
return vReadyState!==4;
case 300:case 301:case 302:case 303:case 305:case 400:case 401:case 402:case 403:case 404:case 405:case 406:case 407:case 408:case 409:case 410:case 411:case 412:case 413:case 414:case 415:case 500:case 501:case 502:case 503:case 504:case 505:{if(qx.core.Setting.get("qx.ioRemoteDebug")){qx.log.Logger.getClassLogger(qx.io.remote.Exchange).debug("Failed with typical HTTP statuscode: "+vStatusCode);
}};
return false;
case 12002:case 12029:case 12030:case 12031:case 12152:case 13030:{if(qx.core.Setting.get("qx.ioRemoteDebug")){qx.log.Logger.getClassLogger(qx.io.remote.Exchange).debug("Failed with MSHTML specific HTTP statuscode: "+vStatusCode);
}};
return false;
default:if(vStatusCode>206&&vStatusCode<300){return true;
}qx.log.Logger.getClassLogger(qx.io.remote.Exchange).debug("Unknown status code: "+vStatusCode+" ("+vReadyState+")");
throw new Error("Unknown status code: "+vStatusCode);
}}},
statusCodeToString:function(vStatusCode){switch(vStatusCode){case -1:return "Not available";
case 200:return "Ok";
case 304:return "Not modified";
case 206:return "Partial content";
case 204:return "No content";
case 300:return "Multiple choices";
case 301:return "Moved permanently";
case 302:return "Moved temporarily";
case 303:return "See other";
case 305:return "Use proxy";
case 400:return "Bad request";
case 401:return "Unauthorized";
case 402:return "Payment required";
case 403:return "Forbidden";
case 404:return "Not found";
case 405:return "Method not allowed";
case 406:return "Not acceptable";
case 407:return "Proxy authentication required";
case 408:return "Request time-out";
case 409:return "Conflict";
case 410:return "Gone";
case 411:return "Length required";
case 412:return "Precondition failed";
case 413:return "Request entity too large";
case 414:return "Request-URL too large";
case 415:return "Unsupported media type";
case 500:return "Server error";
case 501:return "Not implemented";
case 502:return "Bad gateway";
case 503:return "Out of resources";
case 504:return "Gateway time-out";
case 505:return "HTTP version not supported";
case 12002:return "Server timeout";
case 12029:return "Connection dropped";
case 12030:return "Connection dropped";
case 12031:return "Connection dropped";
case 12152:return "Connection closed by server";
case 13030:return "MSHTML-specific HTTP status code";
default:return "Unknown status code";
}}},
properties:{request:{check:"qx.io.remote.Request",
nullable:true},
implementation:{check:"qx.io.remote.AbstractRemoteTransport",
nullable:true,
apply:"_applyImplementation"},
state:{check:["configured",
"sending",
"receiving",
"completed",
"aborted",
"timeout",
"failed"],
init:"configured",
event:"changeState",
apply:"_applyState"}},
members:{send:function(){var vRequest=this.getRequest();
if(!vRequest){return this.error("Please attach a request object first");
}qx.io.remote.Exchange.initTypes();
var vUsage=qx.io.remote.Exchange.typesOrder;
var vSupported=qx.io.remote.Exchange.typesSupported;
var vResponseType=vRequest.getResponseType();
var vNeeds={};
if(vRequest.getAsynchronous()){vNeeds.asynchronous=true;
}else{vNeeds.synchronous=true;
}
if(vRequest.getCrossDomain()){vNeeds.crossDomain=true;
}
if(vRequest.getFileUpload()){vNeeds.fileUpload=true;
}for(var field in vRequest.getFormFields()){vNeeds.programaticFormFields=true;
break;
}var vTransportImpl,
vTransport;
for(var i=0,
l=vUsage.length;i<l;i++){vTransportImpl=vSupported[vUsage[i]];
if(vTransportImpl){if(!qx.io.remote.Exchange.canHandle(vTransportImpl,
vNeeds,
vResponseType)){continue;
}
try{{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Using implementation: "+vTransportImpl.classname);
}};
vTransport=new vTransportImpl;
this.setImplementation(vTransport);
vTransport.setUseBasicHttpAuth(vRequest.getUseBasicHttpAuth());
vTransport.send();
return true;
}catch(ex){return this.error("Request handler throws error",
ex);
}}}this.error("There is no transport implementation available to handle this request: "+vRequest);
},
abort:function(){var vImplementation=this.getImplementation();
if(vImplementation){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Abort: implementation "+vImplementation.toHashCode());
}};
vImplementation.abort();
}else{{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Abort: forcing state to be aborted");
}};
this.setState("aborted");
}},
timeout:function(){var vImplementation=this.getImplementation();
if(vImplementation){this.warn("Timeout: implementation "+vImplementation.toHashCode());
vImplementation.timeout();
}else{this.warn("Timeout: forcing state to timeout");
this.setState("timeout");
}if(this.getRequest()){this.getRequest().setTimeout(0);
}},
_onsending:function(e){this.setState("sending");
},
_onreceiving:function(e){this.setState("receiving");
},
_oncompleted:function(e){this.setState("completed");
},
_onabort:function(e){this.setState("aborted");
},
_onfailed:function(e){this.setState("failed");
},
_ontimeout:function(e){this.setState("timeout");
},
_applyImplementation:function(value,
old){if(old){old.removeEventListener("sending",
this._onsending,
this);
old.removeEventListener("receiving",
this._onreceiving,
this);
old.removeEventListener("completed",
this._oncompleted,
this);
old.removeEventListener("aborted",
this._onabort,
this);
old.removeEventListener("timeout",
this._ontimeout,
this);
old.removeEventListener("failed",
this._onfailed,
this);
}
if(value){var vRequest=this.getRequest();
value.setUrl(vRequest.getUrl());
value.setMethod(vRequest.getMethod());
value.setAsynchronous(vRequest.getAsynchronous());
value.setUsername(vRequest.getUsername());
value.setPassword(vRequest.getPassword());
value.setParameters(vRequest.getParameters());
value.setFormFields(vRequest.getFormFields());
value.setRequestHeaders(vRequest.getRequestHeaders());
value.setData(vRequest.getData());
value.setResponseType(vRequest.getResponseType());
value.addEventListener("sending",
this._onsending,
this);
value.addEventListener("receiving",
this._onreceiving,
this);
value.addEventListener("completed",
this._oncompleted,
this);
value.addEventListener("aborted",
this._onabort,
this);
value.addEventListener("timeout",
this._ontimeout,
this);
value.addEventListener("failed",
this._onfailed,
this);
}},
_applyState:function(value,
old){var vRequest=this.getRequest();
{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("State: "+old+" => "+value);
}};
switch(value){case "sending":this.createDispatchEvent("sending");
break;
case "receiving":this.createDispatchEvent("receiving");
break;
case "completed":case "aborted":case "timeout":case "failed":var vImpl=this.getImplementation();
if(!vImpl){break;
}
if(this.hasEventListeners(value)){var vResponse=new qx.io.remote.Response(value);
if(value=="completed"){var vContent=vImpl.getResponseContent();
vResponse.setContent(vContent);
if(vContent===null){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Altered State: "+value+" => failed");
}};
value="failed";
}}vResponse.setStatusCode(vImpl.getStatusCode());
vResponse.setResponseHeaders(vImpl.getResponseHeaders());
this.dispatchEvent(vResponse);
}this.setImplementation(null);
vImpl.dispose();
break;
}}},
settings:{"qx.ioRemoteDebug":false,
"qx.ioRemoteDebugData":false},
destruct:function(){var vImpl=this.getImplementation();
if(vImpl){this.setImplementation(null);
vImpl.dispose();
}this.setRequest(null);
}});




/* ID: qx.io.remote.Response */
qx.Class.define("qx.io.remote.Response",
{extend:qx.event.type.Event,
construct:function(eventType){this.base(arguments,
eventType);
},
properties:{state:{check:"Integer",
nullable:true},
statusCode:{check:"Integer",
nullable:true},
content:{nullable:true},
responseHeaders:{check:"Object",
nullable:true}},
members:{getResponseHeader:function(vHeader){var vAll=this.getResponseHeaders();
if(vAll){return vAll[vHeader]||null;
}return null;
},
getData:function(){return this;
}}});




/* ID: qx.util.Mime */
qx.Class.define("qx.util.Mime",
{statics:{JAVASCRIPT:"text/javascript",
JSON:"application/json",
XML:"application/xml",
TEXT:"text/plain",
HTML:"text/html"}});




/* ID: qx.io.remote.XmlHttpTransport */
qx.Class.define("qx.io.remote.XmlHttpTransport",
{extend:qx.io.remote.AbstractRemoteTransport,
construct:function(){this.base(arguments);
this._req=qx.io.remote.XmlHttpTransport.createRequestObject();
this._req.onreadystatechange=qx.lang.Function.bind(this._onreadystatechange,
this);
},
events:{"created":"qx.event.type.Event",
"configured":"qx.event.type.Event",
"sending":"qx.event.type.Event",
"receiving":"qx.event.type.Event",
"completed":"qx.event.type.Event",
"aborted":"qx.event.type.Event",
"failed":"qx.event.type.Event",
"timeout":"qx.event.type.Event"},
statics:{handles:{synchronous:true,
asynchronous:true,
crossDomain:false,
fileUpload:false,
programaticFormFields:false,
responseTypes:[qx.util.Mime.TEXT,
qx.util.Mime.JAVASCRIPT,
qx.util.Mime.JSON,
qx.util.Mime.XML,
qx.util.Mime.HTML]},
requestObjects:[],
requestObjectCount:0,
isSupported:function(){return qx.net.HttpRequest.create()!=null?true:false;
},
createRequestObject:function(){return qx.net.HttpRequest.create();
}},
members:{_localRequest:false,
_lastReadyState:0,
getRequest:function(){return this._req;
},
send:function(){this._lastReadyState=0;
var vRequest=this.getRequest();
var vMethod=this.getMethod();
var vAsynchronous=this.getAsynchronous();
var vUrl=this.getUrl();
var vLocalRequest=(qx.core.Client.getInstance().getRunsLocally()&&!(/^http(s){0,1}\:/.test(vUrl)));
this._localRequest=vLocalRequest;
var vParameters=this.getParameters();
var vParametersList=[];
for(var vId in vParameters){var value=vParameters[vId];
if(value instanceof Array){for(var i=0;i<value.length;i++){vParametersList.push(encodeURIComponent(vId)+"="+encodeURIComponent(value[i]));
}}else{vParametersList.push(encodeURIComponent(vId)+"="+encodeURIComponent(value));
}}
if(vParametersList.length>0){vUrl+=(vUrl.indexOf("?")>=0?"&":"?")+vParametersList.join("&");
}var encode64=function(input){var keyStr="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
var output="";
var chr1,
chr2,
chr3;
var enc1,
enc2,
enc3,
enc4;
var i=0;
do{chr1=input.charCodeAt(i++);
chr2=input.charCodeAt(i++);
chr3=input.charCodeAt(i++);
enc1=chr1>>2;
enc2=((chr1&3)<<4)|(chr2>>4);
enc3=((chr2&15)<<2)|(chr3>>6);
enc4=chr3&63;
if(isNaN(chr2)){enc3=enc4=64;
}else if(isNaN(chr3)){enc4=64;
}output+=keyStr.charAt(enc1)+keyStr.charAt(enc2)+keyStr.charAt(enc3)+keyStr.charAt(enc4);
}while(i<input.length);
return output;
};
var onreadyStateChangeCallback=qx.lang.Function.bind(this._onreadystatechange,
this);
if(qx.core.Variant.isSet("qx.client",
"mshtml")&&this.getAsynchronous()){vRequest.onreadystatechange=function(e){var self=this;
window.setTimeout(function(e){onreadyStateChangeCallback(e);
},
0);
};
}else{vRequest.onreadystatechange=onreadyStateChangeCallback;
}if(this.getUsername()){if(this.getUseBasicHttpAuth()){vRequest.open(vMethod,
vUrl,
vAsynchronous);
vRequest.setRequestHeader('Authorization',
'Basic '+encode64(this.getUsername()+':'+this.getPassword()));
}else{vRequest.open(vMethod,
vUrl,
vAsynchronous,
this.getUsername(),
this.getPassword());
}}else{vRequest.open(vMethod,
vUrl,
vAsynchronous);
}vRequest.setRequestHeader('Referer',
window.location.href);
var vRequestHeaders=this.getRequestHeaders();
for(var vId in vRequestHeaders){vRequest.setRequestHeader(vId,
vRequestHeaders[vId]);
}try{{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Request: "+this.getData());
}};
vRequest.send(this.getData());
}catch(ex){if(vLocalRequest){this.failedLocally();
}else{this.error("Failed to send data: "+ex,
"send");
this.failed();
}return;
}if(!vAsynchronous){this._onreadystatechange();
}},
failedLocally:function(){if(this.getState()==="failed"){return;
}this.warn("Could not load from file: "+this.getUrl());
this.failed();
},
_onreadystatechange:function(e){switch(this.getState()){case "completed":case "aborted":case "failed":case "timeout":{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Ignore Ready State Change");
}};
return;
}var vReadyState=this.getReadyState();
if(vReadyState==4){if(!qx.io.remote.Exchange.wasSuccessful(this.getStatusCode(),
vReadyState,
this._localRequest)){return this.failed();
}}while(this._lastReadyState<vReadyState){this.setState(qx.io.remote.Exchange._nativeMap[++this._lastReadyState]);
}},
getReadyState:function(){var vReadyState=null;
try{vReadyState=this._req.readyState;
}catch(ex){}return vReadyState;
},
setRequestHeader:function(vLabel,
vValue){this._req.setRequestHeader(vLabel,
vValue);
},
getResponseHeader:function(vLabel){var vResponseHeader=null;
try{this.getRequest().getResponseHeader(vLabel)||null;
}catch(ex){}return vResponseHeader;
},
getStringResponseHeaders:function(){var vSourceHeader=null;
try{var vLoadHeader=this._req.getAllResponseHeaders();
if(vLoadHeader){vSourceHeader=vLoadHeader;
}}catch(ex){}return vSourceHeader;
},
getResponseHeaders:function(){var vSourceHeader=this.getStringResponseHeaders();
var vHeader={};
if(vSourceHeader){var vValues=vSourceHeader.split(/[\r\n]+/g);
for(var i=0,
l=vValues.length;i<l;i++){var vPair=vValues[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(vPair){vHeader[vPair[1]]=vPair[2];
}}}return vHeader;
},
getStatusCode:function(){var vStatusCode=-1;
try{vStatusCode=this.getRequest().status;
}catch(ex){}return vStatusCode;
},
getStatusText:function(){var vStatusText="";
try{vStatusText=this.getRequest().statusText;
}catch(ex){}return vStatusText;
},
getResponseText:function(){var vResponseText=null;
var vStatus=this.getStatusCode();
var vReadyState=this.getReadyState();
if(qx.io.remote.Exchange.wasSuccessful(vStatus,
vReadyState,
this._localRequest)){try{vResponseText=this.getRequest().responseText;
}catch(ex){}}return vResponseText;
},
getResponseXml:function(){var vResponseXML=null;
var vStatus=this.getStatusCode();
var vReadyState=this.getReadyState();
if(qx.io.remote.Exchange.wasSuccessful(vStatus,
vReadyState,
this._localRequest)){try{vResponseXML=this.getRequest().responseXML;
}catch(ex){}}if(typeof vResponseXML=="object"&&vResponseXML!=null){if(!vResponseXML.documentElement){var s=String(this.getRequest().responseText).replace(/<\?xml[^\?]*\?>/,
"");
vResponseXML.loadXML(s);
}if(!vResponseXML.documentElement){throw new Error("Missing Document Element!");
}
if(vResponseXML.documentElement.tagName=="parseerror"){throw new Error("XML-File is not well-formed!");
}}else{throw new Error("Response was not a valid xml document ["+this.getRequest().responseText+"]");
}return vResponseXML;
},
getFetchedLength:function(){var vText=this.getResponseText();
return typeof vText=="string"?vText.length:0;
},
getResponseContent:function(){if(this.getState()!=="completed"){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Transfer not complete, ignoring content!");
}};
return null;
}{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Returning content for responseType: "+this.getResponseType());
}};
var vText=this.getResponseText();
switch(this.getResponseType()){case qx.util.Mime.TEXT:case qx.util.Mime.HTML:{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+vText);
}};
return vText;
case qx.util.Mime.JSON:{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+vText);
}};
try{if(vText&&vText.length>0){return qx.io.Json.parseQx(vText)||null;
}else{return null;
}}catch(ex){this.error("Could not execute json: ["+vText+"]",
ex);
return "<pre>Could not execute json: \n"+vText+"\n</pre>";
}case qx.util.Mime.JAVASCRIPT:{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+vText);
}};
try{if(vText&&vText.length>0){return window.eval(vText)||null;
}else{return null;
}}catch(ex){this.error("Could not execute javascript: ["+vText+"]",
ex);
return null;
}case qx.util.Mime.XML:vText=this.getResponseXml();
{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+vText);
}};
return vText||null;
default:this.warn("No valid responseType specified ("+this.getResponseType()+")!");
return null;
}},
_applyState:function(value,
old){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("State: "+value);
}};
switch(value){case "created":this.createDispatchEvent("created");
break;
case "configured":this.createDispatchEvent("configured");
break;
case "sending":this.createDispatchEvent("sending");
break;
case "receiving":this.createDispatchEvent("receiving");
break;
case "completed":this.createDispatchEvent("completed");
break;
case "failed":this.createDispatchEvent("failed");
break;
case "aborted":this.getRequest().abort();
this.createDispatchEvent("aborted");
break;
case "timeout":this.getRequest().abort();
this.createDispatchEvent("timeout");
break;
}}},
defer:function(statics,
members){qx.io.remote.Exchange.registerType(qx.io.remote.XmlHttpTransport,
"qx.io.remote.XmlHttpTransport");
},
destruct:function(){var vRequest=this.getRequest();
if(vRequest){if(qx.core.Variant.isSet("qx.client",
"mshtml")){}else{vRequest.onreadystatechange=null;
}switch(vRequest.readyState){case 1:case 2:case 3:vRequest.abort();
}}this._disposeFields("_req");
}});




/* ID: qx.net.HttpRequest */
qx.Class.define("qx.net.HttpRequest",
{statics:{create:qx.core.Variant.select("qx.client",
{"default":function(){return new XMLHttpRequest;
},
"mshtml":qx.lang.Object.select(location.protocol!=="file:"&&window.XMLHttpRequest?"native":"activeX",
{"native":function(){return new XMLHttpRequest;
},
"activeX":function(){if(this.__server){return new ActiveXObject(this.__server);
}var servers=["MSXML2.XMLHTTP.3.0",
"MSXML2.XMLHTTP.6.0",
"MSXML2.XMLHTTP.4.0",
"MSXML2.XMLHTTP",
"Microsoft.XMLHTTP"];
var obj;
var server;
for(var i=0,
l=servers.length;i<l;i++){server=servers[i];
try{obj=new ActiveXObject(server);
break;
}catch(ex){obj=null;
}}
if(obj){this.__server=server;
}return obj;
}})})}});




/* ID: qx.io.remote.IframeTransport */
qx.Class.define("qx.io.remote.IframeTransport",
{extend:qx.io.remote.AbstractRemoteTransport,
construct:function(){this.base(arguments);
var vUniqueId=(new Date).valueOf();
var vFrameName="frame_"+vUniqueId;
var vFormName="form_"+vUniqueId;
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this._frame=document.createElement('<iframe name="'+vFrameName+'"></iframe>');
}else{this._frame=document.createElement("iframe");
}this._frame.src="javascript:void(0)";
this._frame.id=this._frame.name=vFrameName;
this._frame.onload=qx.lang.Function.bind(this._onload,
this);
this._frame.style.display="none";
document.body.appendChild(this._frame);
this._form=document.createElement("form");
this._form.target=vFrameName;
this._form.id=this._form.name=vFormName;
this._form.style.display="none";
document.body.appendChild(this._form);
this._data=document.createElement("textarea");
this._data.id=this._data.name="_data_";
this._form.appendChild(this._data);
this._frame.onreadystatechange=qx.lang.Function.bind(this._onreadystatechange,
this);
},
statics:{handles:{synchronous:false,
asynchronous:true,
crossDomain:false,
fileUpload:true,
programaticFormFields:true,
responseTypes:[qx.util.Mime.TEXT,
qx.util.Mime.JAVASCRIPT,
qx.util.Mime.JSON,
qx.util.Mime.XML,
qx.util.Mime.HTML]},
isSupported:function(){return true;
},
_numericMap:{"uninitialized":1,
"loading":2,
"loaded":2,
"interactive":3,
"complete":4}},
members:{_lastReadyState:0,
send:function(){var vMethod=this.getMethod();
var vUrl=this.getUrl();
var vParameters=this.getParameters();
var vParametersList=[];
for(var vId in vParameters){var value=vParameters[vId];
if(value instanceof Array){for(var i=0;i<value.length;i++){vParametersList.push(encodeURIComponent(vId)+"="+encodeURIComponent(value[i]));
}}else{vParametersList.push(encodeURIComponent(vId)+"="+encodeURIComponent(value));
}}
if(vParametersList.length>0){vUrl+=(vUrl.indexOf("?")>=0?"&":"?")+vParametersList.join("&");
}var vFormFields=this.getFormFields();
for(var vId in vFormFields){var vField=document.createElement("textarea");
vField.name=vId;
vField.appendChild(document.createTextNode(vFormFields[vId]));
this._form.appendChild(vField);
}this._form.action=vUrl;
this._form.method=vMethod;
this._data.appendChild(document.createTextNode(this.getData()));
this._form.submit();
this.setState("sending");
},
_onload:function(e){if(this._form.src){return;
}this._switchReadyState(qx.io.remote.IframeTransport._numericMap.complete);
},
_onreadystatechange:function(e){this._switchReadyState(qx.io.remote.IframeTransport._numericMap[this._frame.readyState]);
},
_switchReadyState:function(vReadyState){switch(this.getState()){case "completed":case "aborted":case "failed":case "timeout":this.warn("Ignore Ready State Change");
return;
}while(this._lastReadyState<vReadyState){this.setState(qx.io.remote.Exchange._nativeMap[++this._lastReadyState]);
}},
setRequestHeader:function(vLabel,
vValue){},
getResponseHeader:function(vLabel){return null;
},
getResponseHeaders:function(){return {};
},
getStatusCode:function(){return 200;
},
getStatusText:function(){return "";
},
getIframeWindow:function(){return qx.html.Iframe.getWindow(this._frame);
},
getIframeDocument:function(){return qx.html.Iframe.getDocument(this._frame);
},
getIframeBody:function(){return qx.html.Iframe.getBody(this._frame);
},
getIframeTextContent:function(){var vBody=this.getIframeBody();
if(!vBody){return null;
}
if(!vBody.firstChild){return "";
}if(vBody.firstChild.tagName&&vBody.firstChild.tagName.toLowerCase()=="pre"){return vBody.firstChild.innerHTML;
}else{return vBody.innerHTML;
}},
getIframeHtmlContent:function(){var vBody=this.getIframeBody();
return vBody?vBody.innerHTML:null;
},
getFetchedLength:function(){return 0;
},
getResponseContent:function(){if(this.getState()!=="completed"){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.warn("Transfer not complete, ignoring content!");
}};
return null;
}{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Returning content for responseType: "+this.getResponseType());
}};
var vText=this.getIframeTextContent();
switch(this.getResponseType()){case qx.util.Mime.TEXT:{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+this._responseContent);
}};
return vText;
break;
case qx.util.Mime.HTML:vText=this.getIframeHtmlContent();
{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+this._responseContent);
}};
return vText();
break;
case qx.util.Mime.JSON:vText=this.getIframeHtmlContent();
{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+this._responseContent);
}};
try{return vText&&vText.length>0?qx.io.Json.parseQx(vText):null;
}catch(ex){return this.error("Could not execute json: ("+vText+")",
ex);
}case qx.util.Mime.JAVASCRIPT:vText=this.getIframeHtmlContent();
{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+this._responseContent);
}};
try{return vText&&vText.length>0?window.eval(vText):null;
}catch(ex){return this.error("Could not execute javascript: ("+vText+")",
ex);
}case qx.util.Mime.XML:vText=this.getIframeDocument();
{if(qx.core.Setting.get("qx.ioRemoteDebugData")){this.debug("Response: "+this._responseContent);
}};
return vText;
default:this.warn("No valid responseType specified ("+this.getResponseType()+")!");
return null;
}}},
defer:function(statics,
members,
properties){qx.io.remote.Exchange.registerType(qx.io.remote.IframeTransport,
"qx.io.remote.IframeTransport");
},
destruct:function(){if(this._frame){this._frame.onload=null;
this._frame.onreadystatechange=null;
if(qx.core.Variant.isSet("qx.client",
"gecko")){this._frame.src=qx.io.Alias.getInstance().resolve("static/image/blank.gif");
}document.body.removeChild(this._frame);
}
if(this._form){document.body.removeChild(this._form);
}this._disposeFields("_frame",
"_form");
}});




/* ID: qx.net.Http */
qx.Class.define("qx.net.Http",
{statics:{METHOD_GET:"GET",
METHOD_POST:"POST",
METHOD_PUT:"PUT",
METHOD_HEAD:"HEAD",
METHOD_DELETE:"DELETE"}});




/* ID: qx.io.remote.Request */
qx.Class.define("qx.io.remote.Request",
{extend:qx.core.Target,
construct:function(vUrl,
vMethod,
vResponseType){this.base(arguments);
this._requestHeaders={};
this._parameters={};
this._formFields={};
if(vUrl!==undefined){this.setUrl(vUrl);
}
if(vMethod!==undefined){this.setMethod(vMethod);
}
if(vResponseType!==undefined){this.setResponseType(vResponseType);
}this.setProhibitCaching(true);
this.setRequestHeader("X-Requested-With",
"qooxdoo");
this.setRequestHeader("X-Qooxdoo-Version",
qx.core.Version.toString());
this._seqNum=++qx.io.remote.Request._seqNum;
},
events:{"created":"qx.event.type.Event",
"configured":"qx.event.type.Event",
"sending":"qx.event.type.Event",
"receiving":"qx.event.type.Event",
"completed":"qx.io.remote.Response",
"aborted":"qx.io.remote.Response",
"failed":"qx.io.remote.Response",
"timeout":"qx.io.remote.Response"},
statics:{_seqNum:0},
properties:{url:{check:"String",
init:""},
method:{check:[qx.net.Http.METHOD_GET,
qx.net.Http.METHOD_POST,
qx.net.Http.METHOD_PUT,
qx.net.Http.METHOD_HEAD,
qx.net.Http.METHOD_DELETE],
apply:"_applyMethod",
init:qx.net.Http.METHOD_GET},
asynchronous:{check:"Boolean",
init:true},
data:{check:"String",
nullable:true},
username:{check:"String",
nullable:true},
password:{check:"String",
nullable:true},
state:{check:["configured",
"queued",
"sending",
"receiving",
"completed",
"aborted",
"timeout",
"failed"],
init:"configured",
apply:"_applyState",
event:"changeState"},
responseType:{check:[qx.util.Mime.TEXT,
qx.util.Mime.JAVASCRIPT,
qx.util.Mime.JSON,
qx.util.Mime.XML,
qx.util.Mime.HTML],
init:qx.util.Mime.TEXT,
apply:"_applyResponseType"},
timeout:{check:"Integer",
nullable:true},
prohibitCaching:{check:"Boolean",
init:true,
apply:"_applyProhibitCaching"},
crossDomain:{check:"Boolean",
init:false},
fileUpload:{check:"Boolean",
init:false},
transport:{check:"qx.io.remote.Exchange",
nullable:true},
useBasicHttpAuth:{check:"Boolean",
init:false}},
members:{send:function(){qx.io.remote.RequestQueue.getInstance().add(this);
},
abort:function(){qx.io.remote.RequestQueue.getInstance().abort(this);
},
reset:function(){switch(this.getState()){case "sending":case "receiving":this.error("Aborting already sent request!");
case "queued":this.abort();
break;
}},
isConfigured:function(){return this.getState()==="configured";
},
isQueued:function(){return this.getState()==="queued";
},
isSending:function(){return this.getState()==="sending";
},
isReceiving:function(){return this.getState()==="receiving";
},
isCompleted:function(){return this.getState()==="completed";
},
isAborted:function(){return this.getState()==="aborted";
},
isTimeout:function(){return this.getState()==="timeout";
},
isFailed:function(){return this.getState()==="failed";
},
_onqueued:function(e){this.setState("queued");
this.dispatchEvent(e);
},
_onsending:function(e){this.setState("sending");
this.dispatchEvent(e);
},
_onreceiving:function(e){this.setState("receiving");
this.dispatchEvent(e);
},
_oncompleted:function(e){this.setState("completed");
this.dispatchEvent(e);
this.dispose();
},
_onaborted:function(e){this.setState("aborted");
this.dispatchEvent(e);
this.dispose();
},
_ontimeout:function(e){this.setState("timeout");
this.dispatchEvent(e);
this.dispose();
},
_onfailed:function(e){this.setState("failed");
this.dispatchEvent(e);
this.dispose();
},
_applyState:function(value,
old){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("State: "+value);
}};
},
_applyProhibitCaching:function(value,
old){if(value){this.setParameter("nocache",
new Date().valueOf());
this.setRequestHeader("Pragma",
"no-cache");
this.setRequestHeader("Cache-Control",
"no-cache");
}else{this.removeParameter("nocache");
this.removeRequestHeader("Pragma");
this.removeRequestHeader("Cache-Control");
}},
_applyMethod:function(value,
old){if(value===qx.net.Http.METHOD_POST){this.setRequestHeader("Content-Type",
"application/x-www-form-urlencoded");
}else{this.removeRequestHeader("Content-Type");
}},
_applyResponseType:function(value,
old){this.setRequestHeader("X-Qooxdoo-Response-Type",
value);
},
setRequestHeader:function(vId,
vValue){this._requestHeaders[vId]=vValue;
},
removeRequestHeader:function(vId){delete this._requestHeaders[vId];
},
getRequestHeader:function(vId){return this._requestHeaders[vId]||null;
},
getRequestHeaders:function(){return this._requestHeaders;
},
setParameter:function(vId,
vValue){this._parameters[vId]=vValue;
},
removeParameter:function(vId){delete this._parameters[vId];
},
getParameter:function(vId){return this._parameters[vId]||null;
},
getParameters:function(){return this._parameters;
},
setFormField:function(vId,
vValue){this._formFields[vId]=vValue;
},
removeFormField:function(vId){delete this._formFields[vId];
},
getFormField:function(vId){return this._formFields[vId]||null;
},
getFormFields:function(){return this._formFields;
},
getSequenceNumber:function(){return this._seqNum;
}},
destruct:function(){this.setTransport(null);
this._disposeFields("_requestHeaders",
"_parameters",
"_formFields");
}});




/* ID: qx.io.remote.RequestQueue */
qx.Class.define("qx.io.remote.RequestQueue",
{type:"singleton",
extend:qx.core.Target,
construct:function(){this.base(arguments);
this._queue=[];
this._active=[];
this._totalRequests=0;
this._timer=new qx.client.Timer(500);
this._timer.addEventListener("interval",
this._oninterval,
this);
},
properties:{enabled:{init:true,
check:"Boolean",
apply:"_applyEnabled"},
maxTotalRequests:{check:"Integer",
nullable:true},
maxConcurrentRequests:{check:"Integer",
init:3},
defaultTimeout:{check:"Integer",
init:5000}},
members:{_debug:function(){var vText=this._active.length+"/"+(this._queue.length+this._active.length);
{if(qx.core.Setting.get("qx.ioRemoteDebug")){this.debug("Progress: "+vText);
window.status="Request-Queue Progress: "+vText;
}};
},
_check:function(){this._debug();
if(this._active.length==0&&this._queue.length==0){this._timer.stop();
}if(!this.getEnabled()){return;
}if(this._active.length>=this.getMaxConcurrentRequests()||this._queue.length==0){return;
}if(this.getMaxTotalRequests()!=null&&this._totalRequests>=this.getMaxTotalRequests()){return;
}var vRequest=this._queue.shift();
var vTransport=new qx.io.remote.Exchange(vRequest);
this._totalRequests++;
this._active.push(vTransport);
this._debug();
vTransport.addEventListener("sending",
vRequest._onsending,
vRequest);
vTransport.addEventListener("receiving",
vRequest._onreceiving,
vRequest);
vTransport.addEventListener("completed",
vRequest._oncompleted,
vRequest);
vTransport.addEventListener("aborted",
vRequest._onaborted,
vRequest);
vTransport.addEventListener("timeout",
vRequest._ontimeout,
vRequest);
vTransport.addEventListener("failed",
vRequest._onfailed,
vRequest);
vTransport.addEventListener("sending",
this._onsending,
this);
vTransport.addEventListener("completed",
this._oncompleted,
this);
vTransport.addEventListener("aborted",
this._oncompleted,
this);
vTransport.addEventListener("timeout",
this._oncompleted,
this);
vTransport.addEventListener("failed",
this._oncompleted,
this);
vTransport._start=(new Date).valueOf();
vTransport.send();
if(this._queue.length>0){this._check();
}},
_remove:function(vTransport){qx.lang.Array.remove(this._active,
vTransport);
vTransport.dispose();
this._check();
},
_activeCount:0,
_onsending:function(e){{if(qx.core.Setting.get("qx.ioRemoteDebug")){this._activeCount++;
e.getTarget()._counted=true;
this.debug("ActiveCount: "+this._activeCount);
}};
},
_oncompleted:function(e){{if(qx.core.Setting.get("qx.ioRemoteDebug")){if(e.getTarget()._counted){this._activeCount--;
this.debug("ActiveCount: "+this._activeCount);
}}};
this._remove(e.getTarget());
},
_oninterval:function(e){var vActive=this._active;
if(vActive.length==0){return;
}var vCurrent=(new Date).valueOf();
var vTransport;
var vRequest;
var vDefaultTimeout=this.getDefaultTimeout();
var vTimeout;
var vTime;
for(var i=vActive.length-1;i>=0;i--){vTransport=vActive[i];
vRequest=vTransport.getRequest();
if(vRequest.isAsynchronous()){vTimeout=vRequest.getTimeout();
if(vTimeout==0){continue;
}
if(vTimeout==null){vTimeout=vDefaultTimeout;
}vTime=vCurrent-vTransport._start;
if(vTime>vTimeout){this.warn("Timeout: transport "+vTransport.toHashCode());
this.warn(vTime+"ms > "+vTimeout+"ms");
vTransport.timeout();
}}}},
_applyEnabled:function(value,
old){if(value){this._check();
}this._timer.setEnabled(value);
},
add:function(vRequest){vRequest.setState("queued");
this._queue.push(vRequest);
this._check();
if(this.getEnabled()){this._timer.start();
}},
abort:function(vRequest){var vTransport=vRequest.getTransport();
if(vTransport){vTransport.abort();
}else if(qx.lang.Array.contains(this._queue,
vRequest)){qx.lang.Array.remove(this._queue,
vRequest);
}}},
destruct:function(){this._disposeObjectDeep("_active",
1);
this._disposeObjects("_timer");
this._disposeFields("_queue");
}});




/* ID: qx.ui.embed.Iframe */
qx.Class.define("qx.ui.embed.Iframe",
{extend:qx.ui.basic.Terminator,
construct:function(vSource){this.base(arguments);
this.initSelectable();
this.initTabIndex();
if(vSource!=undefined){this.setSource(vSource);
}},
events:{"load":"qx.event.type.Event"},
statics:{load:function(obj){if(!obj){throw new Error("Could not find iframe which was loaded [A]!");
}if(obj.currentTarget){obj=obj.currentTarget;
}if(obj._QxIframe){obj._QxIframe._onload();
}else{throw new Error("Could not find iframe which was loaded [B]!");
}}},
properties:{tabIndex:{refine:true,
init:0},
selectable:{refine:true,
init:false},
appearance:{refine:true,
init:"iframe"},
source:{check:"String",
init:"",
apply:"_applySource",
event:"changeSource"},
frameName:{check:"String",
init:"",
apply:"_applyFrameName"}},
members:{getIframeNode:function(){return this._iframeNode;
},
setIframeNode:function(vIframeNode){return this._iframeNode=vIframeNode;
},
getBlockerNode:function(){return this._blockerNode;
},
setBlockerNode:function(vBlockerNode){return this._blockerNode=vBlockerNode;
},
getContentWindow:function(){if(this.isCreated()){return qx.html.Iframe.getWindow(this.getIframeNode());
}else{return null;
}},
getContentDocument:function(){if(this.isCreated()){return qx.html.Iframe.getDocument(this.getIframeNode());
}else{return null;
}},
isLoaded:qx.core.Variant.select("qx.client",
{"mshtml":function(){var doc=this.getContentDocument();
return doc?doc.readyState=="complete":false;
},
"default":function(){return this._isLoaded;
}}),
reload:function(){if(this.isCreated()&&this.getContentWindow()){this.getContentWindow().location.replace(this.getContentWindow().location.href);
}},
queryCurrentUrl:function(){var doc=this.getContentDocument();
try{if(doc&&doc.location){return doc.location.href;
}}catch(ex){}return null;
},
block:function(){if(this._blockerNode){this._blockerNode.style.display="";
}},
release:function(){if(this._blockerNode){this._blockerNode.style.display="none";
}},
_generateIframeElement:function(vFrameName){if(qx.core.Variant.isSet("qx.client",
"mshtml")){var nameStr=vFrameName?'name="'+vFrameName+'"':'';
var frameEl=qx.ui.embed.Iframe._element=document.createElement('<iframe onload="parent.qx.ui.embed.Iframe.load(this)"'+nameStr+'></iframe>');
}else{var frameEl=qx.ui.embed.Iframe._element=document.createElement("iframe");
frameEl.onload=qx.ui.embed.Iframe.load;
if(vFrameName){frameEl.name=vFrameName;
}}frameEl._QxIframe=this;
frameEl.frameBorder="0";
frameEl.frameSpacing="0";
frameEl.marginWidth="0";
frameEl.marginHeight="0";
frameEl.width="100%";
frameEl.height="100%";
frameEl.hspace="0";
frameEl.vspace="0";
frameEl.border="0";
frameEl.scrolling="auto";
frameEl.unselectable="on";
frameEl.allowTransparency="true";
frameEl.style.position="absolute";
frameEl.style.top=0;
frameEl.style.left=0;
return frameEl;
},
_generateBlockerElement:function(){var blockerEl=qx.ui.embed.Iframe._blocker=document.createElement("div");
var blockerStyle=blockerEl.style;
if(qx.core.Variant.isSet("qx.client",
"mshtml")){blockerStyle.backgroundColor="white";
blockerStyle.filter="Alpha(Opacity=0)";
}blockerStyle.position="absolute";
blockerStyle.top=0;
blockerStyle.left=0;
blockerStyle.width="100%";
blockerStyle.height="100%";
blockerStyle.zIndex=1;
blockerStyle.display="none";
return blockerEl;
},
_applyElement:function(value,
old){var iframeNode=this.setIframeNode(this._generateIframeElement());
var blockerNode=this.setBlockerNode(this._generateBlockerElement());
this._syncSource();
value.appendChild(iframeNode);
value.appendChild(blockerNode);
this.base(arguments,
value,
old);
},
_beforeAppear:function(){this.base(arguments);
qx.ui.embed.IframeManager.getInstance().add(this);
},
_beforeDisappear:function(){this.base(arguments);
qx.ui.embed.IframeManager.getInstance().remove(this);
},
_applySource:function(value,
old){if(this.isCreated()){this._syncSource();
}},
_syncSource:function(){var currentSource=this.getSource();
if(qx.util.Validation.isInvalidString(currentSource)){currentSource=qx.io.Alias.getInstance().resolve("static/html/blank.html");
}this._isLoaded=false;
if(this.getContentWindow()){this.getContentWindow().location.replace(currentSource);
}else{this.getIframeNode().src=currentSource;
}},
_applyFrameName:function(value,
old,
propName,
uniqModIds){if(this.isCreated()){throw new Error("Not allowed to set frame name after it has been created");
}},
_onload:function(){if(!this._inLoaded){this._isLoaded=true;
this.createDispatchEvent("load");
}},
_isLoaded:false},
destruct:function(){if(this._iframeNode){this._iframeNode._QxIframe=null;
this._iframeNode.onload=null;
}this._disposeFields("__onload",
"_iframeNode",
"_blockerNode");
}});




/* ID: qx.html.Window */
qx.Class.define("qx.html.Window",
{statics:{getInnerWidth:qx.core.Variant.select("qx.client",
{"mshtml":function(vWindow){if(vWindow.document.documentElement&&vWindow.document.documentElement.clientWidth){return vWindow.document.documentElement.clientWidth;
}else if(vWindow.document.body){return vWindow.document.body.clientWidth;
}return 0;
},
"default":function(vWindow){return vWindow.innerWidth;
}}),
getInnerHeight:qx.core.Variant.select("qx.client",
{"mshtml":function(vWindow){if(vWindow.document.documentElement&&vWindow.document.documentElement.clientHeight){return vWindow.document.documentElement.clientHeight;
}else if(vWindow.document.body){return vWindow.document.body.clientHeight;
}return 0;
},
"default":function(vWindow){return vWindow.innerHeight;
}}),
getScrollLeft:qx.core.Variant.select("qx.client",
{"mshtml":function(vWindow){if(vWindow.document.documentElement&&vWindow.document.documentElement.scrollLeft){return vWindow.document.documentElement.scrollLeft;
}else if(vWindow.document.body){return vWindow.document.body.scrollTop;
}return 0;
},
"default":function(vWindow){return vWindow.document.body.scrollLeft;
}}),
getScrollTop:qx.core.Variant.select("qx.client",
{"mshtml":function(vWindow){if(vWindow.document.documentElement&&vWindow.document.documentElement.scrollTop){return vWindow.document.documentElement.scrollTop;
}else if(vWindow.document.body){return vWindow.document.body.scrollTop;
}return 0;
},
"default":function(vWindow){return vWindow.document.body.scrollTop;
}})}});




/* ID: qx.ui.basic.ScrollBar */
qx.Class.define("qx.ui.basic.ScrollBar",
{extend:qx.ui.layout.CanvasLayout,
construct:function(horizontal){this.base(arguments,
horizontal?"horizontal":"vertical");
this._horizontal=(horizontal==true);
this._scrollBar=new qx.ui.basic.ScrollArea;
if(qx.core.Variant.isSet("qx.client",
"gecko")){this._scrollBar.setStyleProperty("position",
"");
}this._scrollBar.setOverflow(horizontal?"scrollX":"scrollY");
this._scrollBar.addEventListener("scroll",
this._onscroll,
this);
this._scrollContent=new qx.ui.basic.Terminator;
if(qx.core.Variant.isSet("qx.client",
"gecko")){this._scrollContent.setStyleProperty("position",
"");
}this._scrollBar.add(this._scrollContent);
if(this._horizontal){this._scrollContent.setHeight(5);
this._scrollBar.setWidth("100%");
this._scrollBar.setHeight(this._getScrollBarWidth());
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this.setHeight(this._getScrollBarWidth());
this.setOverflow("hidden");
this._scrollBar.setHeight(this._getScrollBarWidth()+1);
this._scrollBar.setTop(-1);
}}else{this._scrollContent.setWidth(5);
this._scrollBar.setHeight("100%");
this._scrollBar.setWidth(this._getScrollBarWidth());
if(qx.core.Variant.isSet("qx.client",
"mshtml")){this.setWidth(this._getScrollBarWidth());
this.setOverflow("hidden");
this._scrollBar.setWidth(this._getScrollBarWidth()+1);
this._scrollBar.setLeft(-1);
}}this.add(this._scrollBar);
this._blocker=new qx.ui.basic.Terminator();
this._blocker.set({left:0,
top:0,
height:"100%",
width:"100%",
display:!this.getEnabled()});
this._blocker.setAppearance("scrollbar-blocker");
this.add(this._blocker);
this.setMaximum(0);
},
statics:{EVENT_DELAY:250},
properties:{value:{check:"Number",
init:0,
apply:"_applyValue",
event:"changeValue",
transform:"_checkValue"},
maximum:{check:"Integer",
apply:"_applyMaximum"},
mergeEvents:{check:"Boolean",
init:false}},
members:{_checkValue:function(value){var innerSize=!this.getElement()?0:(this._horizontal?this.getInnerWidth():this.getInnerHeight());
return Math.max(0,
Math.min(this.getMaximum()-innerSize,
value));
},
_applyValue:function(value,
old){if(!this._internalValueChange&&this._isCreated){this._positionKnob(value);
}},
_applyMaximum:function(value,
old){if(this._horizontal){this._scrollContent.setWidth(value);
}else{this._scrollContent.setHeight(value);
}this.setValue(this._checkValue(this.getValue()));
},
_applyVisibility:function(value,
old){if(!value){this._positionKnob(0);
}else{this._positionKnob(this.getValue());
}return this.base(arguments,
value,
old);
},
_computePreferredInnerWidth:function(){return this._horizontal?0:this._getScrollBarWidth();
},
_computePreferredInnerHeight:function(){return this._horizontal?this._getScrollBarWidth():0;
},
_applyEnabled:function(isEnabled){this.base(arguments);
this._blocker.setDisplay(!this.getEnabled());
},
_getScrollBarWidth:function(){if(qx.ui.basic.ScrollBar._scrollBarWidth==null){var dummy=document.createElement("div");
dummy.style.width="100px";
dummy.style.height="100px";
dummy.style.overflow="scroll";
dummy.style.visibility="hidden";
document.body.appendChild(dummy);
qx.ui.basic.ScrollBar._scrollBarWidth=dummy.offsetWidth-dummy.clientWidth;
document.body.removeChild(dummy);
}return qx.ui.basic.ScrollBar._scrollBarWidth;
},
_onscroll:function(evt){var value=this._horizontal?this._scrollBar.getScrollLeft():this._scrollBar.getScrollTop();
if(this.getMergeEvents()){this._lastScrollEventValue=value;
window.clearTimeout(this._setValueTimerId);
var self=this;
this._setValueTimerId=window.setTimeout(function(){self._internalValueChange=true;
self.setValue(self._lastScrollEventValue);
self._internalValueChange=false;
qx.ui.core.Widget.flushGlobalQueues();
},
qx.ui.basic.ScrollBar.EVENT_DELAY);
}else{this._internalValueChange=true;
this.setValue(value);
this._internalValueChange=false;
qx.ui.core.Widget.flushGlobalQueues();
}},
_positionKnob:function(value){if(this.isCreated()){if(this._horizontal){this._scrollBar.setScrollLeft(value);
}else{this._scrollBar.setScrollTop(value);
}}},
_afterAppear:function(){this.base(arguments);
this._positionKnob(this.getValue());
}},
destruct:function(){this._disposeObjects("_scrollContent",
"_scrollBar",
"_blocker");
}});




/* ID: qx.ui.basic.ScrollArea */
qx.Class.define("qx.ui.basic.ScrollArea",
{extend:qx.ui.layout.CanvasLayout,
construct:function(){this.base(arguments);
this.__onscroll=qx.lang.Function.bindEvent(this._onscroll,
this);
},
events:{"scroll":"qx.event.type.Event"},
members:{_applyElement:function(value,
old){this.base(arguments,
value,
old);
if(value){if(qx.core.Variant.isSet("qx.client",
"mshtml")){value.attachEvent("onscroll",
this.__onscroll);
}else{value.addEventListener("scroll",
this.__onscroll,
false);
}}},
_onscroll:function(e){this.createDispatchEvent("scroll");
qx.event.handler.EventHandler.stopDomEvent(e);
}},
destruct:function(){var el=this.getElement();
if(el){if(qx.core.Variant.isSet("qx.client",
"mshtml")){el.detachEvent("onscroll",
this.__onscroll);
}else{el.removeEventListener("scroll",
this.__onscroll,
false);
}delete this.__onscroll;
}}});


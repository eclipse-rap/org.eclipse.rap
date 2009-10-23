// TODO This has to be included into the qx.js and qx-debug.js!
qx.Class.define("qx.client.History",
{type:"singleton",
extend:qx.core.Target,
construct:qx.core.Variant.select("qx.client",
{"mshtml":function(){this.base(arguments);
this._iframe=document.createElement("iframe");
this._iframe.style.visibility="hidden";
this._iframe.style.position="absolute";
this._iframe.style.left="-1000px";
this._iframe.style.top="-1000px";
var src=qx.io.Alias.getInstance().resolve("static/html/blank.html");
this._iframe.src=src;
document.body.appendChild(this._iframe);
this._titles={};
this._state=decodeURIComponent(this.__getHash());
this._locationState=decodeURIComponent(this.__getHash());
this.__waitForIFrame(function(){this.__storeState(this._state);
this.__startTimer();
},
this);
},
"default":function(){this.base(arguments);
this._titles={};
this._state=this.__getState();
this.__startTimer();
}}),
events:{"request":"qx.event.type.DataEvent"},
properties:{timeoutInterval:{check:"Number",
init:100,
apply:"_applyTimeoutInterval"}},
members:{init:function(){qx.log.Logger.deprecatedMethodWarning(arguments.callee,
"This method call is no longer needed.");
},
addToHistory:function(state,
newTitle){if(newTitle!=null){document.title=newTitle;
this._titles[state]=newTitle;
}
if(state!=this._state){top.location.hash="#"+encodeURIComponent(state);
this.__storeState(state);
}},
getState:function(){return this._state;
},
navigateBack:function(){qx.client.Timer.once(function(){history.back();
},
0);
},
navigateForward:function(){qx.client.Timer.once(function(){history.forward();
},
0);
},
_applyTimeoutInterval:function(value){this._timer.setInterval(value);
},
__onHistoryLoad:function(state){this._state=state;
this.createDispatchDataEvent("request",
state);
if(this._titles[state]!=null){document.title=this._titles[state];
}},
__startTimer:function(){this._timer=new qx.client.Timer(this.getTimeoutInterval());
this._timer.addEventListener("interval",
function(e){var newHash=this.__getState();
if(newHash!=this._state){this.__onHistoryLoad(newHash);
}},
this);
this._timer.start();
},
__getHash:function(){var href=top.location.href;
var idx=href.indexOf("#");
return idx>=0?href.substring(idx+1):"";
},
__getState:qx.core.Variant.select("qx.client",
{"mshtml":function(){var locationState=decodeURIComponent(this.__getHash());
if(locationState!=this._locationState){this._locationState=locationState;
this.__storeState(locationState);
return locationState;
}var doc=this._iframe.contentWindow.document;
var elem=doc.getElementById("state");
var iframeState=elem?decodeURIComponent(elem.innerText):"";
return iframeState;
},
"default":function(){return decodeURIComponent(this.__getHash());
}}),
__storeState:qx.core.Variant.select("qx.client",
{"mshtml":function(state){var html='<html><body><div id="state">'+encodeURIComponent(state)+'</div></body></html>';
try{var doc=this._iframe.contentWindow.document;
doc.open();
doc.write(html);
doc.close();
}catch(ex){return false;
}return true;
},
"default":function(state){qx.client.Timer.once(function(){top.location.hash="#"+encodeURIComponent(state);
},
this,
0);
return true;
}}),
__waitForIFrame:qx.core.Variant.select("qx.client",
{"mshtml":function(callback,
context){if(!this._iframe.contentWindow||!this._iframe.contentWindow.document){qx.client.Timer.once(function(){this.__waitForIFrame(callback,
context);
},
this,
10);
return;
}callback.call(context||window);
},
"default":null})},
destruct:function(){this._timer.stop();
this._disposeObjects("_timer");
this._disposeFields("_iframe",
"_titles");
}});

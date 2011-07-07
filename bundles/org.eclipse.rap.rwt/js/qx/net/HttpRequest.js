/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
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

qx.Class.define("qx.net.HttpRequest",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /**
     * Return a new XMLHttpRequest object suitable for the client browser.
     *
     * TODO: extract detection of MSXML version (run once)
     *
     * @type static
     * @return {HttpRequest} TODOC
     * @signature function()
     */
    create : qx.core.Variant.select("qx.client",
    {
      "default" : function() {
        return new XMLHttpRequest;
      },

      /*
         IE7's native XmlHttp does not care about trusted zones. To make this
         work in the localhost scenario, you can use the following registry setting:

          [HKEY_CURRENT_USER\Software\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_XMLHTTP_RESPECT_ZONEPOLICY]
          "Iexplore.exe"=dword:00000001

         We always use activeX if the file served is locally = file protocol.
      */
      "mshtml" : qx.lang.Object.select(location.protocol !== "file:" && window.XMLHttpRequest ? "native" : "activeX",
      {
        "native" : function() {
          return new XMLHttpRequest;
        },

        "activeX" : function()
        {
          if (this.__server) {
            return new ActiveXObject(this.__server);
          }

          /*
           According to information on the Microsoft XML Team's WebLog
           it is recommended to check for availability of MSXML versions 6.0 and 3.0.
           Other versions are included for completeness, 5.0 is excluded as it is
           "off-by-default" in IE7 (which could trigger a goldbar).

           http://blogs.msdn.com/xmlteam/archive/2006/10/23/using-the-right-version-of-msxml-in-internet-explorer.aspx
           http://msdn.microsoft.com/library/default.asp?url=/library/en-us/xmlsdk/html/aabe29a2-bad2-4cea-8387-314174252a74.asp

           MSXML 3 is preferred over MSXML 6 because the IE7 native XMLHttpRequest returns
           a MSXML 3 document and so does not properly work with other types of xml documents.
          */

          var servers =
          [
            "MSXML2.XMLHTTP.3.0",
            "MSXML2.XMLHTTP.6.0",
            "MSXML2.XMLHTTP.4.0",
            "MSXML2.XMLHTTP",    // v3.0
            "Microsoft.XMLHTTP"  // v2.x
          ];

          var obj;
          var server;

          for (var i=0, l=servers.length; i<l; i++)
          {
            server = servers[i];

            try
            {
              obj = new ActiveXObject(server);
              break;
            }
            catch(ex)
            {
              obj = null;
            }
          }

          if (obj) {
            this.__server = server;
          }

          return obj;
        }
      })
    })
  }
});

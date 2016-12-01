package com.wrupple.muba.desktop.client.activity.widgets.fields.colorPicker;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
/**
 * A helpful class to set transparencies in browsers GWT supports
 * @author AurorisNET
 */
public class TransparencyImpl
{
    // Caches the original DXImageTransform.Microsoft.AlphaImageLoader settings for IE6
    private static Map<Element, String> map = new HashMap<Element, String>();

    // Get IE version (provided by Microsoft)
    public static native float getIEVersion()
    /*-{
        var rv = -1; // Return value assumes failure.
        if (navigator.appName == 'Microsoft Internet Explorer')
        {
            var ua = navigator.userAgent;
            var re  = new RegExp("MSIE ([0-9]{1,}[TOKEN_SPLITTER0-9]{0,})");
            if (re.exec(ua) != null)
              rv = parseFloat( RegExp.$1 );
        }
        return rv;
    }-*/;

    public static void setBackgroundColor(Element elem, String color)
    {
        try
        {
            DOM.setStyleAttribute(elem, "backgroundColor", color);
        }
        catch (com.google.gwt.core.client.JavaScriptException e)
        {
            // Called if backgroundColor could not be set.
        }
    }

    /* Given a DOM element, set the transparency value, with 100 being fully opaque and
     * 0 being fully transparent
     * @param elem A com.google.gwt.user.client.Element object
     * @param alpha An alpha value
     */
    public static void setTransparency(Element elem, int alpha)
    {
        float ieVersion = getIEVersion();

        if (ieVersion >= 5.5 && ieVersion < 7.0)
        {
            elem = DOM.getChild(elem, 0);

            // Cache existing filters on the image, then re-apply everything with our Alpha filter
            // stacked on the end.
            if (map.containsKey(elem))
            {
                if (alpha == 100)
                {
                    DOM.setStyleAttribute(elem, "filter", map.get(elem) + "");
                }
                else
                {
                    DOM.setStyleAttribute(elem, "filter", map.get(elem) +
                        ", progid:DXImageTransform.Microsoft.Alpha(opacity=" + alpha + ");");
                }
            }
            else
            {
                map.put(elem, DOM.getStyleAttribute(elem, "filter"));

                if (alpha == 100)
                {
                    DOM.setStyleAttribute(elem, "filter", map.get(elem) + "");
                }
                else
                {
                    DOM.setStyleAttribute(elem, "filter", map.get(elem) +
                        ", progid:DXImageTransform.Microsoft.Alpha(opacity=" + alpha + ");");
                }
            }
        }
        // If IE 7 (or better)
        else if (ieVersion >= 7.0)
        {
            DOM.setStyleAttribute(elem, "filter", "alpha(opacity="+alpha+")");
        }
        else // Everyone else
        {
            DOM.setStyleAttribute(elem, "-moz-opacity", ""+(new Integer(alpha).floatValue() / 100)+"");
            DOM.setStyleAttribute(elem, "opacity", ""+(new Integer(alpha).floatValue() / 100)+"");
        }
    }
}
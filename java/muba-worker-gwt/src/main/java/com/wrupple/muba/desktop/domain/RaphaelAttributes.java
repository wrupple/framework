package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * 
 * <p>
 * 1. cx number
 * </p>
 * <p>
 * 2. cy number
 * </p>
 * <p>
 * 3. fill colour
 * </p>
 * <p>
 * 4. fill-opacity number
 * </p>
 * <p>
 * 5. font string
 * </p>
 * <p>
 * 6. font-family string
 * </p>
 * <p>
 * 7. font-size number
 * </p>
 * <p>
 * 8. font-weight string
 * </p>
 * <p>
 * 9. gradient object|string
 * </p>
 * <p>
 * 10. height number
 * </p>
 * <p>
 * 11. opacity number
 * </p>
 * <p>
 * 12. path pathString
 * </p>
 * <p>
 * 13. r number
 * </p>
 * <p>
 * 14. rotation number
 * </p>
 * <p>
 * 15. rx number
 * </p>
 * <p>
 * 16. ry number
 * </p>
 * <p>
 * 17. scale CSV
 * </p>
 * <p>
 * 18. src string (URL)
 * </p>
 * <p>
 * 19. stroke colour
 * </p>
 * <p>
 * 20. stroke-dasharray string
 * </p>
 * <p>
 * 21. stroke-linecap string
 * </p>
 * <p>
 * 22. stroke-linejoin string
 * </p>
 * <p>
 * 23. stroke-miterlimit number
 * </p>
 * <p>
 * 24. stroke-opacity number
 * </p>
 * <p>
 * 25. stroke-width number
 * </p>
 * <p>
 * 26. translation CSV
 * </p>
 * <p>
 * 27. width number
 * </p>
 * <p>
 * 28. x number
 * </p>
 * <p>
 * 29. y number
 * </p>
 * 
 * @author japi
 * 
 */
public class RaphaelAttributes {
	public static final String NONE = "none";
	public static final String ARIAL_SANS_SERIFF = "Fontin-Sans, Arial";
	private JSONObject o;

	public RaphaelAttributes() {
		super();
		o = new JSONObject();
	}

	public void setY(int value) {
		o.put("y", new JSONNumber(value));
	}

	public void setX(int value) {
		o.put("x", new JSONNumber(value));
	}

	public JavaScriptObject getValue() {
		return o.getJavaScriptObject();
	}

	public void setCx(int value) {
		o.put("cx", new JSONNumber(value));
	}

	public void setCy(int value) {
		o.put("cy", new JSONNumber(value));
	}

	public void setWidth(int value) {
		o.put("width", new JSONNumber(value));
	}

	public void setHeight(int value) {
		o.put("height", new JSONNumber(value));
	}

	public void setScale(String string) {
		o.put("scale", new JSONString(string));
	}

	public void setGradient(String string) {
		o.put("gradient", new JSONString(string));
	}

	public void setStroke(String stroke) {
		o.put("stroke", new JSONString(stroke));
	}

	public void setStrokeWidth(int n) {
		o.put("stroke-width", new JSONNumber(n));
	}

	public void setFill(String color) {
		o.put("fill", new JSONString(color));
	}

	public void setOpacity(int opacity) {
		o.put("opacity", new JSONNumber(opacity));
	}

	public void setFontFamily(String string) {
		o.put("font-family", new JSONString(string));
	}

	public void setFontSize(String string) {
		o.put("font-size", new JSONString(string));
	}

}

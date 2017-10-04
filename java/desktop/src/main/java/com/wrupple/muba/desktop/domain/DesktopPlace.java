package com.wrupple.muba.desktop.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.Place;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

/**
 * 
 * An Application's Atomic Unit of User Interaction. It holds the information to
 * start an element inside the application and allow the user to start working
 * 
 * The unique identifier, while recommended to be a
 * no-spaces-no-special-characters String, no actual checks will be done on
 * runtime to assert it.
 * 
 * 
 * @author japi
 * 
 */
public class DesktopPlace extends Place implements Serializable {
	
	public static final String IMAGE_PARAMETER = PersistentImageMetadata.IMAGE_FIELD;
	public static final String LABEL_PARAMETER = CatalogEntry.NAME_FIELD;

	private static final long serialVersionUID = -7689056368151452282L;

	private String[] tokens;
	
	private String activityUri;
	
	/**
	 * The next token is usually the task Token, and all subsequent tokens usually 
	 */
	private int lastActivityToken;

	private Map<String, String> properties;
	
	/**
	 * a descriptor of this place in the application hierarchy
	 */
	private JavaScriptObject applicationItem;
	private List<String> taskTokens;

	/**
	 * Creates a new Workflow
	 */
	protected DesktopPlace() {
		super();
		this.properties = new HashMap<String, String>();
		this.lastActivityToken=0;
	}
	
	public DesktopPlace(DesktopPlace place) {
		super();
		lastActivityToken=place.getLastActivityToken();
		this.tokens=place.tokens;
		if(this.tokens==null){
			throw new IllegalArgumentException("null activity");
		}
		setProperties(place.getProperties());
	}

	public int getLastActivityToken() {
		return lastActivityToken;
	}

	public void setLastActivityToken(int lastActivityToken) {
		this.lastActivityToken = lastActivityToken;
	}

	public DesktopPlace(String[] tokens) {
		this();
		if(tokens==null){
			throw new IllegalArgumentException("null activity");
		}
		this.tokens = tokens;
	}

	

	/**
	 * Propperties plugins will look for when the item gets called
	 * 
	 * @param name
	 *            name of the propperty
	 * @return Propperty a String value
	 */
	public String getProperty(String name) {
		return this.properties.get(name);
	}

	/**
	 * @return the map of properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param map
	 *            of properties the properties to set
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	public DesktopPlace copyWithoutProperties() {
		DesktopPlace item = new DesktopPlace();
		item.setTokens(getTokens());
		item.properties=properties;
		return item;
	}

	public DesktopPlace cloneItem() {
		DesktopPlace item = copyWithoutProperties();
		item.setProperties(new HashMap<String, String>(properties));
		return item;
	}

	public void removeProperty(String key) {
		properties.remove(key);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + Arrays.hashCode(tokens);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DesktopPlace other = (DesktopPlace) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (!Arrays.equals(tokens, other.tokens))
			return false;
		return true;
	}

	public String getImage(){
		return getProperty(IMAGE_PARAMETER);
	}
	
	public void setImage(String image){
		setProperty(IMAGE_PARAMETER, image);
	}
	
	public String getName(){
		return getProperty(LABEL_PARAMETER);
	}

	public void setName(String value) {
		setProperty(LABEL_PARAMETER, value);
	}

	public JavaScriptObject getApplicationItem() {
		return applicationItem;
	}

	public void setApplicationItem(JavaScriptObject applicationItem) {
		this.applicationItem = applicationItem;
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public String getActivityUri() {
		if(activityUri ==null){
			if(applicationItem==null){
				throw new IllegalArgumentException("unknown activity of this place");
			}else{
				StringBuilder builder;
				int length;
				String[] arr;
				if(lastActivityToken<0){
					builder = new StringBuilder(4);
					arr = DesktopLoadingStateHolder.homeActivity;
					length = arr.length;
				}else{
					builder = new StringBuilder((lastActivityToken+1)*10);
					arr = tokens;
					length = lastActivityToken+1;
				}
				
				for(int i = 0;  i < length; i++){
					builder.append(arr[i]);
					if( i < (length-1)){
						builder.append('/');
					}
				}
			}
		}
		return activityUri;
	}

	public void setTaskTokens(List<String> taskTokens) {
		this.taskTokens=taskTokens;
	}

	public List<String> getTaskTokens() {
		return taskTokens;
	}


}
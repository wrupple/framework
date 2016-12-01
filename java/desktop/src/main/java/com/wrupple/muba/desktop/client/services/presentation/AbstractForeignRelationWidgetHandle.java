package com.wrupple.muba.desktop.client.services.presentation;


public class AbstractForeignRelationWidgetHandle {

	private String customJoins;
	private int pageSize;

	public void setCustomJoins(String s) {
		this.customJoins=s;
	}

	public void setPageSize(String s) {
		try{
			this.pageSize=Integer.parseInt(s);
		}catch(Exception e){
			this.pageSize=-1;
		}
	}
	
	public int getPageSize(){
		return this.pageSize;
	}
	
	public String getCustomJoins(){
		return this.customJoins;
	}
	
}

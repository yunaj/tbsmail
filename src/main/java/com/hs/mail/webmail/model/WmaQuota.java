package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WmaQuota {

	private List<WmaResource> resources;
	
	public WmaQuota() {
		resources = new ArrayList<WmaResource>(); 
	}
	
	public WmaQuota(WmaResource[] wmaResource) {
		resources = Arrays.asList(wmaResource); 
	}
	
	public WmaResource[] getResources() {
		return resources.toArray(new WmaResource[resources.size()]);
	}
	
    public void setResource(WmaResource wr){
    	resources.add(wr);
    }
    
}

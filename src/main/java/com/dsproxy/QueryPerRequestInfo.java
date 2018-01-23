package com.dsproxy;

import java.util.ArrayList;
import java.util.List;

import net.ttddyy.dsproxy.QueryType;

public class QueryPerRequestInfo {

	private String queryArgs;
	
	private QueryType queryType;
	
	private String query;

	private long time;

	private List<StackTraceElement> staceTracePerDbCall = new ArrayList<StackTraceElement>();

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<StackTraceElement> getStaceTracePerDbCall() {
		return staceTracePerDbCall;
	}

	public void setStaceTracePerDbCall(
			List<StackTraceElement> staceTracePerDbCall) {
		this.staceTracePerDbCall = staceTracePerDbCall;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public String getQueryArgs() {
		return queryArgs;
	}

	public void setQueryArgs(String queryArgs) {
		this.queryArgs = queryArgs;
	}
}
package com.dsproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.QueryUtils;

public class DataSourceQueryCountListener implements QueryExecutionListener {

	@Override
	public void beforeQuery(ExecutionInfo execInfo,
			List<QueryInfo> queryInfoList) {
	}

	@Override
	public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

		StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();

		final String dataSourceName = execInfo.getDataSourceName();
		List<QueryPerRequestInfo> listOfQueryInfo = CustomQueryCountHolder
				.get(dataSourceName);
		if (listOfQueryInfo == null) {
			listOfQueryInfo = new ArrayList<QueryPerRequestInfo>();
			CustomQueryCountHolder.put(dataSourceName, listOfQueryInfo);
		}

		long elapsedTime = execInfo.getElapsedTime();

		for (QueryInfo queryInfo : queryInfoList) {
			String query = queryInfo.getQuery();
			QueryType type = getQueryType(query);
			
			StringBuffer argsSb = new StringBuffer();
			List queryArgsList = queryInfo.getQueryArgsList();
			
			QueryPerRequestInfo queryPerRequest = new QueryPerRequestInfo();
			queryPerRequest.setQueryArgs(queryArgsList.toString());
			queryPerRequest.setQuery(queryInfo.getQuery());
			queryPerRequest.setTime(elapsedTime);
			queryPerRequest
					.setStaceTracePerDbCall(Arrays.asList(traceElements));
			queryPerRequest.setQueryType(type);
			listOfQueryInfo.add(queryPerRequest);
		}

	}

	protected QueryType getQueryType(String query) {
		final String trimmedQuery = QueryUtils
				.removeCommentAndWhiteSpace(query);
		final char firstChar = trimmedQuery.charAt(0);

		final QueryType type;
		switch (firstChar) {
		case 'S':
		case 's':
			type = QueryType.SELECT;
			break;
		case 'I':
		case 'i':
			type = QueryType.INSERT;
			break;
		case 'U':
		case 'u':
			type = QueryType.UPDATE;
			break;
		case 'D':
		case 'd':
			type = QueryType.DELETE;
			break;
		default:
			type = QueryType.OTHER;
		}
		return type;
	}

}

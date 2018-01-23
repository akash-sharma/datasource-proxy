package com.dsproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomQueryCountHolder {

	private static ThreadLocal<Map<String, List<QueryPerRequestInfo>>> queryCountMapHolder = 
			new ThreadLocal<Map<String, List<QueryPerRequestInfo>>>() {
		@Override
		protected Map<String, List<QueryPerRequestInfo>> initialValue() {
			return new HashMap<String, List<QueryPerRequestInfo>>();
		}
	};

	public static List<QueryPerRequestInfo> get(String datasource) {

		Map<String, List<QueryPerRequestInfo>> queryInfoMap = queryCountMapHolder.get();
		if (queryInfoMap != null) {
			return queryInfoMap.get(datasource);
		}
		return new ArrayList<QueryPerRequestInfo>();
	}

	public static void put(String dataSourceName, List<QueryPerRequestInfo> count) {
		queryCountMapHolder.get().put(dataSourceName, count);
	}

	public static List<String> getDataSourceNamesAsList() {
		return new ArrayList<String>(getDataSourceNames());
	}

	public static Set<String> getDataSourceNames() {
		return queryCountMapHolder.get().keySet();
	}

	public static void clear() {
		queryCountMapHolder.get().clear();
	}
}
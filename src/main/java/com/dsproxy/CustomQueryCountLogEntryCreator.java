package com.dsproxy;

import java.util.List;

public class CustomQueryCountLogEntryCreator {
	
	public static String HTML_STATIC_CONTENT = ""
			+"<button id=\"expandAll\">expandAll</button>"
			+"<button id=\"collapseAll\">collapseAll</button>"
			+"<head>"
			+"<link rel=\"stylesheet\" href=\"https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css\">"
			+"<script src=\"https://code.jquery.com/jquery-1.11.3.min.js\"></script>"
			+"<script src=\"https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js\"></script>"
			+"<script>"
			+"$( document ).ready(function() {"
			+"$(\"#expandAll\").click(function(){"
					+"$(\".operateAll\").collapsible( \"option\", \"collapsed\", false );"
					+"});"
					+"$(\"#collapseAll\").click(function(){"
							+"$(\".operateAll\").collapsible( \"option\", \"collapsed\", true );"
							+"});"
					+"});"
					+"</script>"
					+"</head>" ;

	public String getLogMessageAsJson(String dsName, List<QueryPerRequestInfo> listOfQueryPerRequest) {

		StringBuffer sb = new StringBuffer("");
		int count = 1;
		for (QueryPerRequestInfo queryPerRequestInfo : listOfQueryPerRequest) {
			printData(sb, queryPerRequestInfo, count);
			count++;
		}
		return sb.toString();
	}

	public String getLogMessage(String dsName, List<QueryPerRequestInfo> listOfQueryPerRequest) {

		StringBuffer sb = new StringBuffer("");
		int count = 1;
		for (QueryPerRequestInfo queryPerRequestInfo : listOfQueryPerRequest) {
			printData(sb, queryPerRequestInfo, count);
			count++;
		}
		return sb.toString();
	}

	public String getLogMessageAsHtml(String dsName, List<QueryPerRequestInfo> listOfQueryPerRequest) {

		StringBuffer sb = new StringBuffer("");
		int count = 1;
		for (QueryPerRequestInfo queryPerRequestInfo : listOfQueryPerRequest) {
			printDataAsHtml(sb, queryPerRequestInfo, count);
			count++;
		}
		return sb.toString();
	}

	private void printData(StringBuffer sb, QueryPerRequestInfo queryPerRequestInfo, int count) {

		sb.append("<------------------------------------------->\n");
		sb.append("Query Type : ");
		sb.append(queryPerRequestInfo.getQueryType());
		sb.append("\n Query : [");
		sb.append(queryPerRequestInfo.getQuery());
		sb.append("] \n");
		sb.append("arguments : [");
		sb.append(queryPerRequestInfo.getQueryArgs());
		sb.append("] \n");
		sb.append("time taken : ");
		sb.append(queryPerRequestInfo.getTime());
		sb.append(" ms \n Stack Trace : \n\t\t\t");
		List<StackTraceElement> listOfStackTraceElement = queryPerRequestInfo.getStaceTracePerDbCall();
		sb.append(listOfStackTraceElement);
		sb.append("<------------------------------------------->\n");
	}

	private void printDataAsHtml(StringBuffer sb, QueryPerRequestInfo queryPerRequestInfo, int count) {

		sb.append("<div><div class=\"operateAll\" data-role=\"collapsible\">");
		sb.append("<h1>");
		sb.append(count);
		sb.append(". Query Type : ");
		sb.append(queryPerRequestInfo.getQueryType());
		sb.append(", time taken : ");
		sb.append(queryPerRequestInfo.getTime());
		sb.append(" ms");
		sb.append("</h1>");
		sb.append("<p>");
		sb.append("arguments : [");
		sb.append(queryPerRequestInfo.getQueryArgs());
		sb.append("] <br>");
		sb.append("sql query : ");
		sb.append(queryPerRequestInfo.getQuery());
		sb.append("<div class=\"operateAll\" data-role=\"collapsible\"><h1>stack trace</h1><p>");
		List<StackTraceElement> listOfStackTraceElement = queryPerRequestInfo.getStaceTracePerDbCall();
		sb.append(listOfStackTraceElement);
		sb.append("</p></div></p></div></div>");
	}
}
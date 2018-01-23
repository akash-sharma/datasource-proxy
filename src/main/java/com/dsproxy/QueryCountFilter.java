package com.dsproxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryCountFilter implements Filter {

	private String htmlFilePath = "F://dsproxy//";
	public static final String FORMAT_PARAM = "format";
	public static final String LOGS_LOCATION = "logsLocation";
	public static final String WRITE_AS_HTML = "writeAsHtml";
	protected boolean clearQueryCounter = true;
	protected boolean writeAsJson = false;
	protected boolean writeAsHtml = true;
	protected CustomQueryCountLogEntryCreator logFormatter = new CustomQueryCountLogEntryCreator();
	private final long FIVE_MB = 5000000l;

	private static Logger logger = LoggerFactory
			.getLogger(QueryCountFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		String format = filterConfig.getInitParameter(FORMAT_PARAM);
		if (format != null && "html".equalsIgnoreCase(format)) {
			this.writeAsHtml = true;
		} else {
			this.writeAsHtml = false;
		}
		String logsLocation = filterConfig.getInitParameter(LOGS_LOCATION);
		System.out.println("logsLocation : "+logsLocation);
		if(logsLocation != null) {
			htmlFilePath = logsLocation;
		}
		File file = new File(htmlFilePath + "perf.html");
		if(file.exists()) {
			file.delete();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		Date startTime = new Date();

		chain.doFilter(request, response);

		List<String> urlsToIgnore = new ArrayList<String>();
		urlsToIgnore.addAll(Arrays.asList("/css", "/js", "/images", "/fonts"));

		if (!shouldNotFilter(request, urlsToIgnore)) {

			Date endTime = new Date();
			Long totalTimeForResponse = endTime.getTime() - startTime.getTime();

			final List<String> dsNames = CustomQueryCountHolder
					.getDataSourceNamesAsList();
			Collections.sort(dsNames);

			for (String dsName : dsNames) {
				List<QueryPerRequestInfo> listOfQueryPerRequest = CustomQueryCountHolder
						.get(dsName);
				String message;
				if (this.writeAsJson) {
					message = this.logFormatter.getLogMessageAsJson(dsName,
							listOfQueryPerRequest);
				} else if(this.writeAsHtml) {
					message = this.logFormatter.getLogMessageAsHtml(dsName,
							listOfQueryPerRequest);
				} else {
					message = this.logFormatter.getLogMessage(dsName,
							listOfQueryPerRequest);
				}
				writeLog(message, listOfQueryPerRequest.size(), request,
						totalTimeForResponse);
			}
		}
		if (clearQueryCounter) {
			CustomQueryCountHolder.clear();
		}
	}

	@Override
	public void destroy() {
	}

	public void setWriteAsJson(boolean writeAsJson) {
		this.writeAsJson = writeAsJson;
	}

	protected void writeLog(String message, int listOfQueries,
			ServletRequest request, Long totalTimeForResponse) {

		StringBuffer sb = new StringBuffer("");
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if(this.writeAsHtml) {
			sb.append("<div class=\"operateAll\" data-role=\"collapsible\">")
				.append("<h1>")
				.append(httpRequest.getRequestURL())
				.append("</h1>")
				.append("<p>")
				.append(" Total SQL query count : ").append(listOfQueries)
				.append("<br>")
				.append(" Total server execution time :")
				.append(totalTimeForResponse).append(" ms ")
				.append("</p>");
			sb.append(message);
			sb.append("</div>");
			writeToFile(sb.toString());
		} else {
			sb.append("###############################################################################\n");
			sb.append("Request URL : ").append(httpRequest.getRequestURL())
					.append("\n Total SQL query count : ").append(listOfQueries)
					.append("\n Total server execution time :")
					.append(totalTimeForResponse).append(" ms \n");
			sb.append(message);
			sb.append("\n###############################################################################\n");
			logger.info(sb.toString());
		}
	}

	private boolean shouldNotFilter(ServletRequest request,
			List<String> urlsToIgnore) throws ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (urlsToIgnore != null && request != null) {
			String requestUrl = httpRequest.getRequestURI();
			if (requestUrl != null) {
				for (String urlToIgnore : urlsToIgnore) {
					if (requestUrl.contains(urlToIgnore)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void writeToFile(String content) {
		if (this.writeAsHtml) {
			FileWriter writer = null;
			try {
				File directory = new File(htmlFilePath);
				if (!directory.exists()) {
					directory.mkdir();
				}
				boolean newFileCreated = false;
				File logFile = new File(htmlFilePath + "perf.html");
				System.out.println(logFile.toString());
				if (!logFile.exists()) {
					newFileCreated = true;
					logFile.createNewFile();
				} else if (logFile.length() >= FIVE_MB) {
					File newLogFile = new File(htmlFilePath + "perf_" + new Date().getTime() + ".html");
					if (logFile.renameTo(newLogFile)) {
						newFileCreated = true;
					}
				}
				if (newFileCreated) {
					File oldLogFile = new File(htmlFilePath + "perf.html");
					writer = new FileWriter(oldLogFile, true);
					writer.write(CustomQueryCountLogEntryCreator.HTML_STATIC_CONTENT);
				} else {
					writer = new FileWriter(logFile, true);
				}
				writer.write(content);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

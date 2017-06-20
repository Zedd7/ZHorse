package com.gmail.xibalbazedd.zhorse.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gmail.xibalbazedd.zhorse.managers.MessageManager;

public class CompoundMessage {
	
	public static final int FIRST_PAGE_NUMBER = 1;
	
	private List<List<String>> pageList = new ArrayList<>();
	private int pageLength = MessageManager.PAGE_LENGTH;
	private int lineLength = MessageManager.LINE_LENGTH;
	private Map<Integer, String> headerMap = new TreeMap<>(); // Headers are mapped to the first page in which they must appear
	
	public CompoundMessage(boolean spareRoomForHeader) {
		if (spareRoomForHeader) pageLength--;
		addPage();
	}
	
	public void addPage() {
		pageList.add(new ArrayList<>());
	}
	
	public void addLine(String line) {
		int lineSegmentCount = (int) Math.ceil((MessageManager.removeChatColors(line).length() / (double) lineLength));
		if (pageList.get(pageList.size() - 1).size() >= pageLength || lineSegmentCount > getRemainingLines()) {
			addPage();
		}
		pageList.get(pageList.size() - 1).add(line);
		for (int segmentIndex = 1; segmentIndex < lineSegmentCount; segmentIndex++) {
			pageList.get(pageList.size() - 1).add(null);
		}
	}
	
	public int getPageCount() {
		return pageList.size();
	}
	
	public int getRemainingLines() {
		return pageLength - pageList.get(pageList.size() - 1).size();
	}
	
	public List<String> getPage(int pageNumber) {
		if (pageNumber >= FIRST_PAGE_NUMBER && pageNumber <= pageList.size()) {
			return pageList.get(pageNumber - 1);
		}
		return new ArrayList<>();
	}
	
	public String getHeader(int pageNumber) {
		int closestStartPageNumber = FIRST_PAGE_NUMBER;
		for (int startPageNumber : headerMap.keySet()) { // Iterating over an ordered key set
			if (startPageNumber <= pageNumber) {
				closestStartPageNumber = startPageNumber;
			}
			else {
				break;
			}
		}
		return headerMap.get(closestStartPageNumber);
	}
	
	public void addHeader(String header) {
		addHeader(header, FIRST_PAGE_NUMBER);
	}
	
	public void addHeader(String header, int startPageNumber) {
		headerMap.put(startPageNumber, header);
	}

}

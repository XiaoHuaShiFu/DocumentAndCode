package com.qa.bean;

public class Search {
	
	private int id;
	private String keyword;
	private int numberSearch;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getNumberSearch() {
		return numberSearch;
	}
	public void setNumberSearch(int numberSearch) {
		this.numberSearch = numberSearch;
	}
	@Override
	public String toString() {
		return "Search [id=" + id + ", keyword=" + keyword + ", numberSearch=" + numberSearch + "]";
	}

}

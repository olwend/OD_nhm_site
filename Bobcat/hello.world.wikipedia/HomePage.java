package com.hello.world.wikipedia;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.cognifide.qa.bb.qualifier.PageObject;
import com.google.inject.Inject;

@PageObject
public class HomePage {

	private static final String URL = "https://en.wikipedia.org";

	@Inject
	private WebDriver webDriver;

	@FindBy(id = "p-search")
	private SearchComponent searchComponent;

	public SearchComponent getSearchComponent() {
		return searchComponent;
	}
}

	public HomePage open() {
		webDriver.get(URL);
		return this;
	}

}
package payloads;

import java.util.ArrayList;
import java.util.List;

import core.ApiUtils;
import pojo.AddPlaceBuilder;
import pojo.Location;

public class Maps_Payload {

	public AddPlaceBuilder addPlacePayLoad(String name, String language, String address) {
		AddPlaceBuilder p = new AddPlaceBuilder();
		double accuracy = Double.parseDouble(ApiUtils.testData.getTestData("accuracy"));
		String phoneNumber = ApiUtils.testData.getTestData("phoneNumber");
		String website = ApiUtils.testData.getTestData("website");
		String types = ApiUtils.testData.getTestData("types");

		p.setAccuracy(accuracy);
		p.setAddress(address);
		p.setLanguage(language);
		p.setPhone_number(phoneNumber);
		p.setWebsite(website);
		p.setName(name);

		List<String> myList = new ArrayList<String>();
		for (String type : types.split(","))
			myList.add(type);

		p.setTypes(myList);
		Location l = new Location();
		l.setLat(-38.383494);
		l.setLng(33.427362);
		p.setLocation(l);

		return p;
	}
}
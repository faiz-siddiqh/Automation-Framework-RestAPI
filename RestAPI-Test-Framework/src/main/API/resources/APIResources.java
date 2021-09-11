package resources;

public enum APIResources {

	AddPlace("/maps/api/place/add/json"), getPlace("/maps/api/place/get/json"),
	deletePlace("/maps/api/place/delete/json");

	private String resource;

	APIResources(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}

}

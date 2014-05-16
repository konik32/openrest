package pl.stalkon.data.boost.response;

public class PageableResponse extends Response {

	public PageableResponse(Object content, Integer size,
			Integer totalElements, Integer totalPages, Integer number) {
		super(content);
		page = new Page(size, totalElements, totalPages, number);
	}

	public PageableResponse(Object content, Page page) {
		super(content);
		this.page = page;
	}

	protected Page page;
	
	public Page getPage(){
		return page;
	}
}

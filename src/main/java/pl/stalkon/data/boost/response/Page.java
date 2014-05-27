package pl.stalkon.data.boost.response;

public class Page {

	private Integer size;
	private Integer totalElements;
	private Integer totalPages;
	private Integer number;

	public Page(Integer size, Integer totalElements, Integer totalPages,
			Integer number) {
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.number = number;
	}

	public Integer getSize() {
		return size;
	}

	public Integer getTotalElements() {
		return totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public Integer getNumber() {
		return number;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setTotalElements(Integer totalElements) {
		this.totalElements = totalElements;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

}

package openrest.webmvc;

public class ParentAwareObject {
	private final ParentAwareObject parent;
	private final Object object;

	public ParentAwareObject(ParentAwareObject parent, Object object) {
		super();
		this.parent = parent;
		this.object = object;
	}

	public ParentAwareObject getParent() {
		return parent;
	}

	public Object getObject() {
		return object;
	}
}

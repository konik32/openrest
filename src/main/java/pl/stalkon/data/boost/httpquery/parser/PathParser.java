package pl.stalkon.data.boost.httpquery.parser;

import lombok.Getter;

public class PathParser {

	private final String path;
	private @Getter String id;
	private @Getter String property;

	public PathParser(String path) {
		super();
		this.path = path;
	}

	public void parse() {
		String parts[] = path.substring(1).split("/");
		try {
			id = parts[1] == "" ? null : parts[1];
			property = parts[2] == "" ? null : parts[2];
		} catch (IndexOutOfBoundsException e) {
			// ignore
		}
	}


}

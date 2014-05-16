package org.springframework.data.query.parser;

import java.util.List;

import org.springframework.data.query.parser.Part.Type;

public interface TreePart {
	List<Part> getParts();
	List<Part> getParts(Type type);
}

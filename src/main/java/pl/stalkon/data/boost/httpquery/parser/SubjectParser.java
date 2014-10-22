package pl.stalkon.data.boost.httpquery.parser;

public class SubjectParser {

	private final String subject;
	private Boolean countProjection;
	private Boolean distinct;

	public SubjectParser(String subject) {
		super();
		this.subject = subject;
	}

	public void parse() {
		if (subject == null)
			return;
		String parts[] = subject.split(",");
		for (String part : parts) {
			part = part.trim();
			if (part.matches("count"))
				countProjection = true;
			else if (part.matches("distinct"))
				distinct = true;
		}
	}

	public Boolean getCountProjection() {
		return countProjection;
	}

	public Boolean getDistinct() {
		return distinct;
	}

}

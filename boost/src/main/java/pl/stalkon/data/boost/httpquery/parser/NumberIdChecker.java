package pl.stalkon.data.boost.httpquery.parser;

public class NumberIdChecker implements IdChecker{

	@Override
	public boolean isId(String possibleId) {
		return possibleId.matches("[0-9]+");
	}

	

}

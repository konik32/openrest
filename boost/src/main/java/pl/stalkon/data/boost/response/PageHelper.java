package pl.stalkon.data.boost.response;

public final class PageHelper {

	public static int getPagesCount(int total, int perPage){
		return (int) Math.ceil((double) total/ (double) perPage);
	}
	
	
}

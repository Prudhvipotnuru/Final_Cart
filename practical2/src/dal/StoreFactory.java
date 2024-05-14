package dal;

public class StoreFactory {
	private static StoreDAL d = null;

	private StoreFactory() {

	}

	public static StoreDAL getProductsDALImpl() {
		if (d == null) {
			d = new StoreDALImpl();
		}
		return d;
	}

}

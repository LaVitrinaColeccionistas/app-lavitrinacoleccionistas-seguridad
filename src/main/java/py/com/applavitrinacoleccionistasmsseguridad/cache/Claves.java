package py.com.applavitrinacoleccionistasmsseguridad.cache;

public final class Claves {

    private static final String BASE = "T(py.com.applavitrinacoleccionistasmsseguridad.cache.Claves).objeto(";

    public static final String POR_ID = BASE + "#id)";
    public static final String POR_ID_ORDEN = BASE + "#idOrden)";
    public static final String POR_RESULTADO = BASE + "#result.id)";

    private Claves() {
    }

    public static String objeto(Object id) {
        return "id:" + id;
    }
}

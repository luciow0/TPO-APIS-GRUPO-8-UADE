package com.uade.tpo.marketplace.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.uade.tpo.marketplace.Enum.MetodoPago;
import com.uade.tpo.marketplace.exception.DatoInvalidoException;

/**
 * Catalogo de validaciones de los datos que llegan del front.
 * Cada metodo publico valida un campo concreto y llama a un metodo primitivo con su mensaje.
 * Las reglas de negocio (fechas superpuestas, estados, carrito vencido) no van aca.
 */
public final class ValidacionVolanta {

    private static final String SOLO_LETRAS_REGEX = "^[\\p{L} '\\-]+$";
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    private static final int ANIO_MINIMO = 1900;
    private static final int ANIO_MAXIMO = 2027;

    private ValidacionVolanta() {
    }

    // ---------- Cuenta ----------

    public static void validarNombreNoVacio(String nombre) {
        validarTextoNoVacio(nombre, "Ingresá tu nombre.");
        validarSoloLetras(nombre, "El nombre no puede tener números ni símbolos.");
        validarLargoMaximo(nombre, 50, "El nombre puede tener hasta 50 caracteres.");
    }

    public static void validarApellidoNoVacio(String apellido) {
        validarTextoNoVacio(apellido, "Ingresá tu apellido.");
        validarSoloLetras(apellido, "El apellido no puede tener números ni símbolos.");
        validarLargoMaximo(apellido, 50, "El apellido puede tener hasta 50 caracteres.");
    }

    public static void validarEmailValido(String email) {
        validarTextoNoVacio(email, "Ingresá tu email.");
        validarEmail(email, "Ingresá un email válido.");
        validarLargoMaximo(email, 150, "El email puede tener hasta 150 caracteres.");
    }

    public static void validarPasswordValida(String password) {
        validarTextoNoVacio(password, "Ingresá una contraseña.");
        validarLargoMinimo(password, 6, "La contraseña tiene que tener al menos 6 caracteres.");
        validarLargoMaximo(password, 100, "La contraseña puede tener hasta 100 caracteres.");
    }

    public static void validarPasswordIngresada(String password) {
        validarTextoNoVacio(password, "Ingresá tu contraseña.");
    }

    public static void validarTelefonoLargo(String telefono) {
        validarLargoMaximo(telefono, 20, "El teléfono puede tener hasta 20 caracteres.");
    }

    public static void validarFechaNacimientoPasada(LocalDate fechaNacimiento) {
        validarFechaObligatoria(fechaNacimiento, "Ingresá tu fecha de nacimiento.");
        if (!fechaNacimiento.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de nacimiento tiene que ser anterior a hoy.");
        }
    }

    public static void validarIdUsuarioPositivo(Long idUsuario) {
        validarLongPositivo(idUsuario, "No encontramos esa cuenta.");
    }

    // ---------- Vehiculo ----------

    public static void validarPatenteLargo(String patente) {
        validarTextoNoVacio(patente, "Ingresá la patente.");
        validarLargoMaximo(patente, 7, "La patente puede tener hasta 7 caracteres.");
    }

    public static void validarMarcaNoVacia(String marca) {
        validarTextoNoVacio(marca, "Ingresá la marca.");
        validarLargoMaximo(marca, 50, "La marca puede tener hasta 50 caracteres.");
    }

    public static void validarModeloNoVacio(String modelo) {
        validarTextoNoVacio(modelo, "Ingresá el modelo.");
        validarLargoMaximo(modelo, 50, "El modelo puede tener hasta 50 caracteres.");
    }

    public static void validarAnioValido(Integer anio) {
        validarEnteroPositivo(anio, "Ingresá el año del vehículo.");
        validarEnteroEnRango(anio, ANIO_MINIMO, ANIO_MAXIMO,
                "El año tiene que estar entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".");
    }

    public static void validarColorNoVacio(String color) {
        validarTextoNoVacio(color, "Ingresá el color.");
        validarLargoMaximo(color, 30, "El color puede tener hasta 30 caracteres.");
    }

    public static void validarCantidadAsientosValida(Integer cantidadAsientos) {
        validarEnteroPositivo(cantidadAsientos, "Ingresá la cantidad de asientos.");
        validarEnteroEnRango(cantidadAsientos, 1, 9, "La cantidad de asientos tiene que estar entre 1 y 9.");
    }

    // ---------- Ubicacion ----------

    public static void validarDireccionNoVacia(String direccion) {
        validarTextoNoVacio(direccion, "Ingresá la dirección.");
    }

    public static void validarCiudadNoVacia(String ciudad) {
        validarTextoNoVacio(ciudad, "Ingresá la ciudad.");
    }

    public static void validarProvinciaNoVacia(String provincia) {
        validarTextoNoVacio(provincia, "Ingresá la provincia.");
    }

    public static void validarLocalidadNoVacia(String localidad) {
        validarTextoNoVacio(localidad, "Ingresá la localidad.");
    }

    public static void validarCodigoPostalNoVacio(String codigoPostal) {
        validarTextoNoVacio(codigoPostal, "Ingresá el código postal.");
    }

    public static void validarZonaNoVacia(String zona) {
        validarTextoNoVacio(zona, "Elegí la zona.");
    }

    // Latitud y longitud las arma el autocompletado de Google Places.
    public static void validarCoordenadas(BigDecimal latitud, BigDecimal longitud) {
        String mensaje = "Elegí la dirección de la lista de sugerencias.";
        validarDecimalEnRango(latitud, new BigDecimal("-90"), new BigDecimal("90"), mensaje);
        validarDecimalEnRango(longitud, new BigDecimal("-180"), new BigDecimal("180"), mensaje);
    }

    // ---------- Publicacion ----------

    public static void validarDatosCompletos(Object datos) {
        validarObligatorio(datos, "Completá los datos antes de guardar.");
    }

    public static void validarIdPublicacionPositivo(Long idPublicacion) {
        validarLongPositivo(idPublicacion, "Elegí una publicación.");
    }

    public static void validarIdVehiculoPositivo(Long idVehiculo) {
        validarLongPositivo(idVehiculo, "Elegí un vehículo.");
    }

    public static void validarIdUbicacionPositivo(Long idUbicacion) {
        validarLongPositivo(idUbicacion, "Elegí una ubicación.");
    }

    public static void validarPrecioDiaPositivo(BigDecimal precioDia) {
        validarDecimalPositivo(precioDia, "El precio por día tiene que ser mayor a cero.");
    }

    public static void validarDescuentoValido(BigDecimal descuento) {
        if (descuento == null) {
            return;
        }
        validarDecimalEnRango(descuento, BigDecimal.ZERO, new BigDecimal("50"),
                "El descuento tiene que estar entre 0 y 50 %.");
    }

    public static void validarDescripcionNoVacia(String descripcion) {
        validarTextoNoVacio(descripcion, "Ingresá una descripción.");
        validarLargoMaximo(descripcion, 600, "La descripción puede tener hasta 600 caracteres.");
    }

    public static void validarHoraRetiroObligatoria(Object hora) {
        validarObligatorio(hora, "Elegí la hora de retiro y devolución.");
    }

    // ---------- Fechas, carrito y pago ----------

    public static void validarFechasRetiroYDevolucion(LocalDate fechaInicio, LocalDate fechaFin) {
        String mensaje = "Elegí la fecha de retiro y la de devolución.";
        validarFechaObligatoria(fechaInicio, mensaje);
        validarFechaObligatoria(fechaFin, mensaje);
    }

    public static void validarFechasPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        String mensaje = "Elegí la fecha de inicio y la de fin del período.";
        validarFechaObligatoria(fechaInicio, mensaje);
        validarFechaObligatoria(fechaFin, mensaje);
    }

    public static void validarAutoYFechasElegidos(Long idPublicacion, Object fechas) {
        String mensaje = "Elegí un auto y sus fechas para agregarlo al carrito.";
        validarObligatorio(fechas, mensaje);
        validarLongPositivo(idPublicacion, mensaje);
    }

    public static void validarMetodoPagoElegido(MetodoPago metodoPago) {
        validarObligatorio(metodoPago, "Elegí cómo querés pagar: Mercado Pago o efectivo.");
    }

    public static void validarIdReservaPositivo(Long idReserva) {
        validarLongPositivo(idReserva, "No encontramos esa reserva.");
    }

    // ---------- Primitivas ----------

    private static void validarObligatorio(Object valor, String mensaje) {
        if (valor == null) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarLongPositivo(Long valor, String mensaje) {
        if (valor == null || valor <= 0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarEnteroPositivo(Integer valor, String mensaje) {
        if (valor == null || valor <= 0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarEnteroEnRango(Integer valor, int minimo, int maximo, String mensaje) {
        if (valor == null || valor < minimo || valor > maximo) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarDecimalPositivo(BigDecimal valor, String mensaje) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarDecimalEnRango(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, String mensaje) {
        if (valor == null || valor.compareTo(minimo) < 0 || valor.compareTo(maximo) > 0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarTextoNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarSoloLetras(String valor, String mensaje) {
        if (valor != null && !valor.trim().matches(SOLO_LETRAS_REGEX)) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarLargoMinimo(String valor, int minimo, String mensaje) {
        if (valor != null && valor.length() < minimo) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarLargoMaximo(String valor, int maximo, String mensaje) {
        if (valor != null && valor.length() > maximo) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarEmail(String valor, String mensaje) {
        if (valor != null && !valor.trim().matches(EMAIL_REGEX)) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    private static void validarFechaObligatoria(LocalDate valor, String mensaje) {
        if (valor == null) {
            throw new DatoInvalidoException(mensaje);
        }
    }
}

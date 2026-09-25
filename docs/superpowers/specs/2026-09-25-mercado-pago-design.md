# Integración con Mercado Pago (Checkout Pro, sandbox)

Fecha: 2026-09-25

## Objetivo

Permitir que un usuario pague una reserva con Mercado Pago (tarjeta o dinero en cuenta) usando el
Checkout Pro real en modo sandbox, sin mover dinero real. El estado del pago y de la reserva se
actualiza automáticamente cuando Mercado Pago notifica el resultado. El pago en efectivo sigue
funcionando como hoy (aprobación manual por un admin).

## Alcance

- Backend Spring Boot únicamente. El frontend (React + Vite) se construye después; el backend deja
  configuradas las URLs de retorno hacia él.
- Fuera de alcance: reembolsos, pagos parciales, validación de firma `x-signature` del webhook.

## Métodos de pago

`MetodoPago` queda con dos valores:

- `MERCADO_PAGO`: cubre tarjeta de crédito/débito y dinero en cuenta (lo elige el usuario dentro del
  checkout de Mercado Pago).
- `EFECTIVO`: flujo manual actual.

Se eliminan `TARJETA_DEBITO` y `TARJETA_CREDITO`. Solo aparecen en el enum. Si la base local tiene
filas de `pago` con esos valores, hay que borrarlas o pasarlas a otro método antes de levantar la
app, porque Hibernate no puede mapear un valor de enum que ya no existe.

## Flujo

1. `POST /pagos` (existente, sin cambios) crea el `Pago` en `PENDIENTE` con `metodoPago = MERCADO_PAGO`.
2. `POST /pagos/{idPago}/checkout` (nuevo, autenticado, dueño de la reserva o admin):
   - Valida que el pago exista, sea `MERCADO_PAGO` y esté `PENDIENTE`.
   - Crea una Preference en Mercado Pago con el SDK Java oficial:
     - un ítem: título `Reserva #<idReserva>`, cantidad 1, precio unitario `pago.monto`, moneda ARS;
     - `external_reference = idPago`;
     - `back_urls` success / failure / pending hacia el frontend;
     - `auto_return = approved`;
     - `notification_url` hacia nuestro webhook.
   - Guarda `preferenceId` en el `Pago`.
   - Responde `{ idPago, preferenceId, initPoint }`. `initPoint` es la URL del checkout sandbox.
   - Si ya tenía preferencia, crea una nueva y reemplaza el `preferenceId` (el usuario puede
     reintentar tras cerrar la pestaña).
3. El usuario paga en el checkout de Mercado Pago con una tarjeta de prueba o un usuario comprador de prueba.
4. Mercado Pago redirige el navegador a la `back_url` que corresponda, agregando sus query params
   (`payment_id`, `status`, `external_reference`, ...). Esto es solo para la UX del frontend y no
   cambia ningún estado.
5. Mercado Pago llama a `POST /pagos/webhook/mercadopago` (nuevo, público) con el id del pago de MP
   (`type=payment` y `data.id`, en query params o en el body).
6. El backend no confía en el contenido de la notificación: consulta el pago a la API de Mercado Pago
   con el access token y usa `status` y `external_reference` de esa respuesta.
7. Se busca el `Pago` local por `external_reference`:
   - `approved` → `APROBADO` y `reservaService.confirmarReserva(...)`.
   - `rejected` / `cancelled` → `RECHAZADO` y `reservaService.rechazarReserva(...)`.
   - cualquier otro estado (`pending`, `in_process`, ...) → no se cambia nada.
   - Se guarda `mercadoPagoPaymentId`.
   - Si el pago local ya no está `PENDIENTE`, no se hace nada (MP reintenta notificaciones).
8. El webhook siempre responde `200` cuando la notificación se procesó o se ignoró a propósito
   (tipo distinto de `payment`, pago desconocido, ya procesado). Responde `500` solo si falla la
   consulta a Mercado Pago, para que MP reintente.
9. El frontend consulta `GET /pagos/reserva/{idReserva}` para mostrar "reserva confirmada" o no.

## Aprobación manual

- `PUT /pagos/{id}/aprobar` y `PUT /pagos/{id}/rechazar` tiran `PagoInvalidException` si el pago es
  `MERCADO_PAGO`. Para esos pagos, el estado lo define solo el webhook.
- Para `EFECTIVO` siguen igual que hoy (solo admin).

## Cambios de modelo

`Pago` suma dos columnas nullable (se crean solas con `ddl-auto=update`):

- `preferenceId` (String)
- `mercadoPagoPaymentId` (String)

`PagoResponse` expone `preferenceId` y `mercadoPagoPaymentId`.

## Componentes

- `pom.xml`: dependencia `com.mercadopago:sdk-java`; `spring-boot-starter-test` en scope test.
- `config/MercadoPagoProperties`: lee la configuración de abajo.
- `service/MercadoPagoGateway` (interfaz) + `MercadoPagoGatewayImpl`: única clase que usa el SDK.
  Expone `crearPreferencia(Pago)` → `{ preferenceId, initPoint }` y `obtenerPago(String paymentId)` →
  `{ status, externalReference }`. Así el servicio se puede testear sin llamar a Mercado Pago.
- `PagoService` / `PagoServiceImpl`:
  - `crearCheckout(Long idPago)` con `@PreAuthorize` de dueño del pago o admin.
  - `procesarNotificacionMercadoPago(String paymentId)` sin `@PreAuthorize`, porque la llamada viene
    de Mercado Pago, no de un usuario.
  - La lógica de pasar a aprobado/rechazado se extrae a métodos privados que usan tanto los
    endpoints manuales como el webhook.
- `PagoController`: `POST /pagos/{idPago}/checkout`.
- `MercadoPagoWebhookController`: `POST /pagos/webhook/mercadopago`.
- `SecurityConfig`: `permitAll()` para `POST /pagos/webhook/mercadopago`.
- DTO `CheckoutResponse`.
- `application.properties.example`: las nuevas claves con placeholders.

## Configuración

Por variables de entorno, nunca commiteadas:

| Propiedad | Variable | Ejemplo |
|---|---|---|
| `mercadopago.access-token` | `MERCADOPAGO_ACCESS_TOKEN` | `TEST-...` |
| `mercadopago.notification-url` | `MERCADOPAGO_NOTIFICATION_URL` | `https://<id>.ngrok-free.app/pagos/webhook/mercadopago` |
| `app.frontend-url` | `APP_FRONTEND_URL` | `http://localhost:5173` (default) |

`back_urls`: `${app.frontend-url}/reservas/{idReserva}/pago/exitoso`, `/fallido` y `/pendiente`.
El frontend va a tener que implementar esas rutas.

La Public Key no se usa en el backend (Checkout Pro redirige a `initPoint`), así que no se configura.

## Errores

- `POST /checkout` sobre un pago inexistente → `PagoNotFoundException`.
- `POST /checkout` sobre un pago `EFECTIVO` o no `PENDIENTE` → `PagoInvalidException`.
- Falla del SDK al crear la preferencia → excepción nueva `MercadoPagoException`, mapeada a `502`.

## Testing

Tests unitarios de `PagoServiceImpl` con `MercadoPagoGateway` y repositorios mockeados (Mockito):

- `crearCheckout` guarda `preferenceId` y devuelve `initPoint`.
- `crearCheckout` rechaza pagos `EFECTIVO` y pagos no `PENDIENTE`.
- Webhook `approved` → `APROBADO` y confirma la reserva.
- Webhook `rejected` → `RECHAZADO` y rechaza la reserva.
- Webhook `pending` → no cambia nada.
- Webhook repetido sobre un pago ya aprobado → no cambia nada.
- `aprobarPago` / `rechazarPago` sobre un pago `MERCADO_PAGO` → `PagoInvalidException`.

Prueba manual end-to-end (guía en el plan): crear app de prueba en Mercado Pago, credenciales TEST,
usuario comprador de prueba, ngrok, compra con tarjeta de prueba y verificación con
`GET /pagos/{id}`. Se agregan los requests nuevos a la colección de Postman.

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

1. El usuario confirma el carrito: `POST /carritos/{idCarrito}/confirmar` con
   `{ "metodoPago": "MERCADO_PAGO" }` (existente, sin cambios).
2. El backend crea la `Reserva` en `PENDIENTE` y el `Pago` en `PENDIENTE` con `MERCADO_PAGO`
   (ya lo hace `CarritoServiceImpl.confirmarCarrito`).
3. El frontend recibe `idPago` en `ConfirmacionCarritoResponse` (ya viene hoy).
   Si el método elegido fue `EFECTIVO`, el frontend no sigue con el checkout; ese pago lo resuelve
   un admin a mano como hoy.
4. El frontend inicia el checkout: `POST /pagos/{idPago}/checkout` (nuevo, autenticado, dueño del
   pago o admin).
5. El backend valida que el pago exista, sea `MERCADO_PAGO` y esté `PENDIENTE`, y crea o recupera
   la Preference:
   - Si el `Pago` ya tiene `preferenceId`, la recupera de Mercado Pago y reutiliza su `initPoint`
     (el usuario cerró la pestaña y vuelve a intentar).
   - Si no tiene, crea una con el SDK Java oficial:
     - un ítem: título `Reserva #<idReserva>`, cantidad 1, precio unitario `pago.monto`, moneda ARS;
     - `external_reference = idPago`;
     - `back_urls` success / failure / pending hacia el frontend;
     - `auto_return = approved`;
     - `notification_url` hacia nuestro webhook;
     y guarda `preferenceId` en el `Pago`.
6. El backend responde:
   ```json
   { "idPago": 21, "preferenceId": "...", "initPoint": "https://..." }
   ```
7. El frontend redirige el navegador a `initPoint`.
8. El usuario paga en Mercado Pago con una tarjeta de prueba o un usuario comprador de prueba.
9. Mercado Pago:
   - redirige el navegador a la `back_url` que corresponda, agregando sus query params
     (`payment_id`, `status`, `external_reference`, ...). Es solo para la UX del frontend y no
     cambia ningún estado;
   - llama a `POST /pagos/webhook/mercadopago` (nuevo, público) con el id del pago de MP
     (`type=payment` y `data.id`, en query params o en el body).
10. El webhook no confía en el contenido de la notificación: consulta el pago real a la API de
    Mercado Pago con el access token y usa `status` y `external_reference` de esa respuesta.
11. El backend busca el `Pago` local por `external_reference` y actualiza:
    - `approved` → `Pago APROBADO` y `Reserva CONFIRMADA` (`reservaService.confirmarReserva`).
    - `rejected` / `cancelled` → `Pago RECHAZADO` y `Reserva RECHAZADA` (`reservaService.rechazarReserva`).
    - `pending` / `in_process` → ambos siguen `PENDIENTE`.
    - Guarda `mercadoPagoPaymentId`.
    - Si el pago local ya no está `PENDIENTE`, no hace nada (Mercado Pago reintenta notificaciones).

    El webhook responde `200` cuando la notificación se procesó o se ignoró a propósito (tipo
    distinto de `payment`, pago desconocido, ya procesado). Responde `500` solo si falla la consulta
    a Mercado Pago, para que reintente.
12. El frontend consulta `GET /pagos/{idPago}` (existente) para mostrar "reserva confirmada" o no.

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
  Expone `crearPreferencia(Pago)` → `{ preferenceId, initPoint }`,
  `obtenerPreferencia(String preferenceId)` → `{ preferenceId, initPoint }` y
  `obtenerPago(String paymentId)` → `{ status, externalReference }`. Así el servicio se puede testear sin llamar a Mercado Pago.
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

- `crearCheckout` sin preferencia previa crea una, guarda `preferenceId` y devuelve `initPoint`.
- `crearCheckout` con `preferenceId` existente la recupera y no crea otra.
- `crearCheckout` rechaza pagos `EFECTIVO` y pagos no `PENDIENTE`.
- Webhook `approved` → `APROBADO` y confirma la reserva.
- Webhook `rejected` → `RECHAZADO` y rechaza la reserva.
- Webhook `pending` → no cambia nada.
- Webhook repetido sobre un pago ya aprobado → no cambia nada.
- `aprobarPago` / `rechazarPago` sobre un pago `MERCADO_PAGO` → `PagoInvalidException`.

Prueba manual end-to-end (guía en el plan): crear app de prueba en Mercado Pago, credenciales TEST,
usuario comprador de prueba, ngrok, compra con tarjeta de prueba y verificación con
`GET /pagos/{id}`. Se agregan los requests nuevos a la colección de Postman.

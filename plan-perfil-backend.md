# Plan perfil y reseñas (backend)

> El front no puede avanzar con esta parte hasta que existan estos endpoints. La maqueta de referencia es `volanta-maqueta-compartir.html`: abrila y seguí los pasos de «Cómo probarla». La calificación por aspectos y eliminar una reseña aparecen en la maqueta, pero quedan afuera para mantenerlo simple.

## Contexto

Vamos a sumar a Volanta:
- un **perfil propio editable**: foto, correo, teléfono, contraseña y "Miembro desde";
- un **perfil público** con los autos y las reseñas de cada persona;
- **reseñas entre las dos partes de un alquiler**: el conductor califica al propietario y el propietario califica al conductor.

Las reseñas son de **doble ciego**: se publican cuando califican los dos o cuando vencen los 7 días. Así nadie califica en respuesta a lo que le pusieron ("me puso 3, le pongo 2").

Hoy el front usa `GET /usuarios/me`, `GET /reservas/mias`, `GET /reservas/{id}` y `PUT /reservas/{id}/cancelar`. No hay forma de modificar el usuario, ni de que el propietario vea o finalice las reservas de sus autos, ni nada de reseñas.

## Criterio: claro y simple

- Cada endpoint hace **una sola cosa**. No se agregan endpoints de resumen ni campos calculados que el front pueda sacar de una lista.
- La reseña tiene solo lo necesario: **estrellas y comentario**.
- Las reglas importantes (quién puede calificar, el plazo y el doble ciego) se validan en el servicio, en un solo lugar. El front no las repite.
- Se siguen las convenciones que ya hay:
  - JWT en `Authorization: Bearer <token>`.
  - JSON en `camelCase` y los ids como `idX`.
  - Errores con `{ "mensaje": "..." }` en español, listo para mostrar.
  - Códigos de error: 400 validación, 401 sin sesión o contraseña incorrecta, 403 sin permiso, 409 conflicto de estado.
- Los dos roles se llaman **`PROPIETARIO`** y **`CONDUCTOR`**, igual que en la pantalla.

## Pasos

### 1. Reservas: el propietario las ve y las finaliza
- **Campos nuevos en cada reserva:**
  - `propietario { idUsuario, nombre, apellido }`;
  - `conductor { idUsuario, nombre, apellido }`;
  - `fechaFinalizacion`, que es `null` hasta que se finaliza.
- **`GET /reservas/de-mis-vehiculos`:** las reservas de los autos del usuario logueado, con la misma forma que `/reservas/mias`.
- **`PUT /reservas/{id}/finalizar`:** el propietario confirma que recibió el auto.
  - Lo puede pedir solo el propietario; si no, 403.
  - La reserva tiene que estar `CONFIRMADA` y ya tiene que haber llegado `fechaFin`; si no, 409.
  - Pasa la reserva a `FINALIZADA`, guarda `fechaFinalizacion` y la devuelve.
- **No hace falta un estado nuevo:** el front muestra "Devolución pendiente" cuando la reserva está `CONFIRMADA` y ya pasó `fechaFin`.

### 2. Reseñas
**Entidad `Resena`**

| Campo | Detalle |
|---|---|
| `idResena`, `idReserva`, `idAutor`, `idDestinatario` | |
| `rol` | El rol de quien **recibe** la reseña: `PROPIETARIO` o `CONDUCTOR`. |
| `estrellas` | De 1 a 5, obligatorio. |
| `comentario` | Hasta 500 caracteres, opcional. |
| `fecha` | |
| Restricción | `UNIQUE (idReserva, idAutor)`: una sola reseña por persona y por reserva. |

**Reglas (en el servicio)**
1. Califican solo el conductor y el propietario de la reserva; cualquier otro recibe 403.
2. La reserva tiene que estar `FINALIZADA` y no tienen que haber pasado 7 días desde `fechaFinalizacion`. Si no, 409.
3. **Doble ciego.** Una reseña está **publicada** si la otra parte también calificó esa reserva o si ya pasaron los 7 días.
   - Conviene un método `estaPublicada(resena)` y usarlo en todos los listados.
   - Las reseñas no publicadas **no se devuelven** a nadie más que a su autor.
4. Se puede editar solo el autor, mientras la reseña no esté publicada. Si no, 409.

**Endpoints**

| Método y ruta | Qué hace |
|---|---|
| `POST /resenas` | Crea una reseña con `{ idReserva, estrellas, comentario }`. Devuelve la reseña con `publicada`. |
| `PUT /resenas/{id}` | Edita `{ estrellas, comentario }`. |
| `GET /resenas/pendientes` | Reservas que el usuario todavía puede calificar, con `idReserva`, el vehículo, las fechas, `fechaFinalizacion` y la otra parte. |
| `GET /resenas/mias` | Las reseñas que escribió el usuario, cada una con `publicada`. |
| `GET /resenas/reserva/{idReserva}` | Para el detalle de la reserva, solo para las dos partes. Devuelve `{ mia, deLaOtraParte, otraParteYaCalifico }`. `deLaOtraParte` viene `null` mientras no esté publicada. |
| `GET /resenas/usuario/{idUsuario}?rol=PROPIETARIO\|CONDUCTOR` | Las reseñas publicadas de un usuario, de la más nueva a la más vieja, con el nombre del autor. Sin token. |

El promedio y la distribución por estrellas los calcula el front con esa lista.

### 3. Perfil propio
| Cambio | Detalle |
|---|---|
| `fechaRegistro` (campo nuevo) | Se completa sola al registrarse. Los usuarios que ya existen pueden quedar en `null`. |
| `foto` (campo nuevo) | Imagen en base64 o `null`, igual que `imagen-vehiculo`. |
| `GET /usuarios/me` | Suma `fechaRegistro` y `foto`. |
| `POST /usuarios/me/foto` | Multipart con el campo `file`. Acepta JPG o PNG de hasta 5 MB; si no, 400. `DELETE /usuarios/me/foto` la quita. |
| `PUT /usuarios/me/telefono` | Recibe `{ telefono }`, de 8 a 15 dígitos. |
| `PUT /usuarios/me/email` | Recibe `{ email, password }`. Contraseña incorrecta → 401. Correo en uso → 409. Si el token se arma con el email, devuelve un `token` nuevo. |
| `PUT /usuarios/me/password` | Recibe `{ actual, nueva }`. Actual incorrecta → 401. La nueva necesita al menos 8 caracteres, con una letra y un número. Se guarda hasheada. |
| Nombre, apellido y fecha de nacimiento | **No se editan**: no hay endpoint. |

### 4. Perfil público
- **`GET /usuarios/{idUsuario}/perfil`** (sin token):
  - Devuelve `idUsuario`, `nombre`, `apellido`, `foto` y `fechaRegistro`.
  - **Nunca** incluye email, teléfono ni fecha de nacimiento.
- **`GET /publicaciones/usuario/{idUsuario}`** (sin token): sus publicaciones `ACTIVAS`.
- **No cambia nada en publicaciones:** el front ya sabe quién es el dueño por `vehiculo.idUsuario`.

## Casos para probar en Postman
- **Finalizar una reserva:** lo pide el conductor → 403; antes de `fechaFin` → 409.
- **Calificar:**
  - Un tercero califica → 403.
  - Calificar dos veces la misma reserva → 409.
  - Calificar el día 8 → 409.
- **Doble ciego:**
  - Califica solo el propietario, y entonces:
    - `/resenas/usuario/{idConductor}` no devuelve esa reseña;
    - `/resenas/reserva/{id}` visto por el conductor trae `deLaOtraParte: null` y `otraParteYaCalifico: true`.
  - Cuando califica el conductor, aparecen las dos.
- **Editar:** una reseña ya publicada → 409.
- **Correo:** con la contraseña incorrecta → 401; con un correo ya usado → 409.
- **Perfil público:** `/usuarios/{id}/perfil` no expone email, teléfono ni fecha de nacimiento.

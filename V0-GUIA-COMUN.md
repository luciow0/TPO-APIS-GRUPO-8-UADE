# V0 comun - Marketplace de alquiler de vehiculos

Esta V0 fija solamente el contrato comun para que el equipo pueda trabajar en paralelo. No intenta resolver toda la logica de negocio.

## Referencia de la profesora

En `Clase Hibernate-v1.zip`, la feature mas completa es `Category`: llega desde Entity hasta Repository, Service, ServiceImpl, Controller, DTO y excepcion.

Ese ejemplo queda como referencia externa para que cada integrante vea hasta donde debe desarrollar su feature. En esta V0 **ninguna entidad esta desarrollada al completo**: todas parten solamente con su Entity y el esqueleto comun de Repository, Service, ServiceImpl y Controller.

De esta forma nadie recibe una feature ya resuelta y todos trabajan desde la misma base.

## Entidades V0

1. Usuario
2. Vehiculo
3. ImagenVehiculo
4. TipoVehiculo
5. Ubicacion
6. Publicacion
7. Disponibilidad
8. Reserva
9. Pago

No se agrega `Rol` todavia. La consigna exige permisos, pero la implementacion concreta de roles/JWT se deja para la clase de seguridad. Cliente y propietario NO se modelan como tipos distintos de usuario: el rol de negocio surge de la relacion con cada recurso.

## Decisiones comunes ya fijadas

- `Usuario.nombreUsuario` y `Usuario.email` son unicos.
- `Vehiculo.patente` es unica.
- Un Vehiculo pertenece a un Usuario propietario y a un TipoVehiculo.
- Un Vehiculo puede tener varias ImagenVehiculo.
- Un Vehiculo tiene como maximo una Publicacion.
- Publicacion contiene precio por dia, descuento porcentual, Ubicacion y una unica `horaRetiroDevolucion`.
- Disponibilidad almacena periodos habilitados de una Publicacion.
- Reserva almacena fechas, cliente, Publicacion, estado y `precioDiaAplicado` historico.
- Pago se relaciona con Reserva, no directamente con Usuario.
- Una Reserva puede existir sin Pago; un Pago siempre pertenece a una Reserva y una Reserva tiene como maximo un Pago.
- Pago es simulado: no hay integracion real con MercadoPago/tarjetas.
- Los importes monetarios usan `BigDecimal`.
- Estados y metodo de pago se modelan como enums.

## Regla de fechas elegida para esta V0

- Alquiler minimo: 1 dia.
- Solo se alquila por dias enteros.
- `fechaFin` debe ser posterior a `fechaInicio`.
- Duracion: diferencia de dias entre fechaInicio y fechaFin.
- Ejemplo: lunes -> martes = 1 dia; lunes -> viernes = 4 dias.
- Retiro y devolucion se realizan en la misma ubicacion y a la misma hora fija de la Publicacion.
- Esta V0 NO agrega un dia extra de bloqueo despues de devolver el vehiculo.
- Por lo tanto una reserva puede comenzar exactamente cuando finaliza la anterior.
- Antes de confirmar una Reserva se debe volver a verificar disponibilidad y superposicion.

Importante: la regla de permitir o no una reserva inmediatamente despues de otra es logica del Service. Si el equipo decide luego exigir un dia de margen, no hace falta cambiar el DER ni las entidades: cambia principalmente la validacion de superposicion/disponibilidad.

## Reglas que NO se implementan en la V0

- JWT / Spring Security.
- Roles/permisos definitivos (`USER`, `ADMIN`, tabla Role, enum, etc.).
- Logica de superposicion de reservas.
- Calculo de precio final/descuento.
- Validaciones de propiedad (solo el dueno modifica su vehiculo/publicacion).
- CRUD completo de cada entidad.
- Filtros de busqueda.
- DTOs y excepciones especificas de cada feature.

Esas reglas pertenecen al desarrollo de cada feature y/o a la siguiente clase.

## Division acordada

- Usuario: una persona.
- Vehiculo + ImagenVehiculo: una persona.
- Publicacion + Disponibilidad: una persona.
- Reserva + Pago: una persona.
- TipoVehiculo + Ubicacion: una persona.

## Convenciones

- Entity: `com.uade.tpo.marketplace.entity`
- Repository: `com.uade.tpo.marketplace.repository`
- Service + ServiceImpl: `com.uade.tpo.marketplace.service`
- Controller REST: `com.uade.tpo.marketplace.controller`
- DTO: `com.uade.tpo.marketplace.dto` cuando la feature lo necesite
- Excepciones: `com.uade.tpo.marketplace.exception` cuando la feature lo necesite
- Enums: `com.uade.tpo.marketplace.enums`

No hace falta usar `@Column` solo porque un atributo Java tenga camelCase; Spring/Hibernate aplica su estrategia de nombres. Se usa cuando hay una restriccion o nombre de columna que se desea declarar explicitamente.

## application.properties / MySQL

Se sigue el mismo esquema usado por la profesora en clase:

- `src/main/resources/application.properties` es local de cada integrante y esta incluido en `.gitignore`.
- `src/main/resources/application.properties.example` SI se versiona y muestra las propiedades necesarias sin contrasenas reales.
- Cada integrante copia el `.example` a `application.properties` y coloca su propia URL/puerto, usuario, contrasena y nombre de base MySQL.

Ejemplo al clonar por primera vez:

`cp src/main/resources/application.properties.example src/main/resources/application.properties`

En Windows tambien se puede copiar el archivo desde el explorador y renombrarlo.

Si `application.properties` ya habia sido agregado a Git antes de incluirlo en `.gitignore`, hay que dejar de trackearlo una unica vez con:

`git rm --cached src/main/resources/application.properties`

Despues se hace commit de esa eliminacion. El archivo local no se borra del disco y cada integrante conserva su propia configuracion.

No se usan variables de entorno en esta V0 porque, aunque son validas y utiles en despliegues reales, para este TPO agregan configuracion innecesaria y se alejan del flujo mostrado por la profesora.

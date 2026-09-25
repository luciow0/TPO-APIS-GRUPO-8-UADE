package com.uade.tpo.marketplace.controller;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.CheckoutResponse;
import com.uade.tpo.marketplace.dto.PagoRequest;
import com.uade.tpo.marketplace.dto.PagoResponse;
import com.uade.tpo.marketplace.entity.Pago;
import com.uade.tpo.marketplace.exception.MercadoPagoException;
import com.uade.tpo.marketplace.exception.PagoDuplicateException;
import com.uade.tpo.marketplace.exception.PagoInvalidException;
import com.uade.tpo.marketplace.exception.PagoNotFoundException;
import com.uade.tpo.marketplace.exception.ReservaInvalidException;
import com.uade.tpo.marketplace.exception.ReservaNotFoundException;
import com.uade.tpo.marketplace.service.PagoService;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/{idPago}")
    public ResponseEntity<PagoResponse> getPagoById(@PathVariable Long idPago) {

        Optional<Pago> result = pagoService.getPagoById(idPago);

        if (result.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(result.get()));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<PagoResponse> getPagoByReserva(@PathVariable Long idReserva) {

        Optional<Pago> result = pagoService.getPagoByReserva(idReserva);

        if (result.isPresent()) {
            return ResponseEntity.ok(convertirAResponse(result.get()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(@RequestBody PagoRequest pagoRequest)
            throws ReservaNotFoundException,
            PagoDuplicateException,
            PagoInvalidException {

        if (pagoRequest == null) {
            throw new PagoInvalidException();
        }

        Pago result = pagoService.crearPago(
                pagoRequest.getIdReserva(),
                pagoRequest.getMetodoPago());

        return ResponseEntity
                .created(URI.create("/pagos/" + result.getIdPago()))
                .body(convertirAResponse(result));
    }

    @PutMapping("/{idPago}/aprobar")
    public ResponseEntity<PagoResponse> aprobarPago(@PathVariable Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {

        Pago result = pagoService.aprobarPago(idPago);

        return ResponseEntity.ok(convertirAResponse(result));
    }

    @PutMapping("/{idPago}/rechazar")
    public ResponseEntity<PagoResponse> rechazarPago(@PathVariable Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            ReservaNotFoundException, ReservaInvalidException {

        Pago result = pagoService.rechazarPago(idPago);

        return ResponseEntity.ok(convertirAResponse(result));
    }

    @PostMapping("/{idPago}/checkout")
    public ResponseEntity<CheckoutResponse> crearCheckout(@PathVariable Long idPago)
            throws PagoNotFoundException, PagoInvalidException,
            MercadoPagoException {

        return ResponseEntity.ok(pagoService.crearCheckout(idPago));
    }

    // back_url de la preferencia: Mercado Pago devuelve el navegador aca y lo mandamos a la pantalla de la reserva.
    @GetMapping("/mercadopago/retorno")
    public ResponseEntity<Void> retornoDesdeMercadoPago(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalReference) {

        Optional<Pago> pago = Optional.empty();
        Long idPaymentMercadoPago = parsearId(paymentId);

        if (idPaymentMercadoPago != null) {
            try {
                pago = pagoService.procesarPagoMercadoPago(idPaymentMercadoPago);
            } catch (Exception e) {
                log.warn("No se pudo procesar el pago {} al volver de Mercado Pago", paymentId, e);
            }
        }

        Long idPago = parsearId(externalReference);

        if (pago.isEmpty() && idPago != null) {
            pago = pagoService.buscarPagoMercadoPago(idPago);
        }

        String destino = pago
                .map(p -> frontendUrl + "/reservas/" + p.getReserva().getIdReserva()
                        + "?idPago=" + p.getIdPago()
                        + "&estadoPago=" + p.getEstado())
                .orElse(frontendUrl + "/reservas");

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(destino))
                .build();
    }

    private Long parsearId(String valor) {
        try {
            return valor == null ? null : Long.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private PagoResponse convertirAResponse(Pago pago) {
        return new PagoResponse(
                pago.getIdPago(),
                pago.getReserva().getIdReserva(),
                pago.getFecha(),
                pago.getMonto(),
                pago.getEstado(),
                pago.getMetodo(),
                pago.getPreferenceId(),
                pago.getMercadoPagoPaymentId());
    }

}

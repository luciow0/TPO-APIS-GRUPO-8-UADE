package com.uade.tpo.marketplace.entity;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.uade.tpo.marketplace.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String telefono;

    @NotNull
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @JsonIgnore
    @OneToMany(mappedBy = "propietario")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Reserva> reservas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;   // default: todo el que se registra es USER


    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
        vehiculo.setPropietario(this);
    }

    public void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
        reserva.setCliente(this);
    }

    public void eliminarVehiculo(Vehiculo vehiculo) {
        vehiculos.remove(vehiculo);
        vehiculo.setPropietario(null);
    }

    public void eliminarReserva(Reserva reserva) {
        reservas.remove(reserva);
        reserva.setCliente(null);
    }

    public void actualizarDatosUsuario(Usuario nuevosDatos) {
        this.nombre = nuevosDatos.getNombre();
        this.apellido = nuevosDatos.getApellido();
        this.email = nuevosDatos.getEmail();
        this.password = nuevosDatos.getPassword();
        this.telefono = nuevosDatos.getTelefono();
        this.fechaNacimiento = nuevosDatos.getFechaNacimiento();
    }
}

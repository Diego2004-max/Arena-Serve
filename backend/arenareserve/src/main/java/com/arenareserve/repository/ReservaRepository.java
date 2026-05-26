package com.arenareserve.repository;

import com.arenareserve.enums.EstadoReserva;
import com.arenareserve.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("""
            select count(r) > 0 from Reserva r
            where r.cancha.id = :canchaId and r.fecha = :fecha
            and r.estadoReserva <> com.arenareserve.enums.EstadoReserva.CANCELADA
            and (:inicio < r.horaFin and :fin > r.horaInicio)
            """)
    boolean existeSolapamiento(@Param("canchaId") Long canchaId, @Param("fecha") LocalDate fecha,
                               @Param("inicio") LocalTime inicio, @Param("fin") LocalTime fin);

    @Query("""
            select distinct r from Reserva r
            left join fetch r.serviciosAdicionales
            join fetch r.cliente
            join fetch r.cancha
            """)
    List<Reserva> findAllConDetalle();

    @Query("""
            select distinct r from Reserva r
            left join fetch r.serviciosAdicionales
            join fetch r.cliente
            join fetch r.cancha
            where r.id = :id
            """)
    Optional<Reserva> findByIdConDetalle(@Param("id") Long id);

    List<Reserva> findByFecha(LocalDate fecha);
    List<Reserva> findTop5ByFechaGreaterThanEqualAndEstadoReservaNotOrderByFechaAscHoraInicioAsc(LocalDate fecha, EstadoReserva estado);
    long countByFecha(LocalDate fecha);
    long countByEstadoReserva(EstadoReserva estadoReserva);
}

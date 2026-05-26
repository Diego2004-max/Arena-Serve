package com.arenareserve.repository;

import com.arenareserve.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReservaId(Long reservaId);

    @Query("select coalesce(sum(p.valor), 0) from Pago p where p.fechaPago between :inicio and :fin")
    BigDecimal ingresosEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}

package com.arenareserve.config;

import com.arenareserve.enums.EstadoCancha;
import com.arenareserve.enums.Rol;
import com.arenareserve.enums.TipoCancha;
import com.arenareserve.model.Cancha;
import com.arenareserve.model.ServicioAdicional;
import com.arenareserve.repository.CanchaRepository;
import com.arenareserve.repository.ServicioAdicionalRepository;
import com.arenareserve.user.UsuarioRepository;
import com.arenareserve.user.UsuarioSistema;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CanchaRepository canchaRepository;
    private final ServicioAdicionalRepository servicioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        UsuarioSistema admin = usuarioRepository.findByEmail("admin@arenareserve.com")
                .orElseGet(UsuarioSistema::new);

        admin.setEmail("admin@arenareserve.com");
        admin.setPassword(passwordEncoder.encode("Admin12345"));
        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);

        if (canchaRepository.count() == 0) {
            crearCancha("Cancha Norte", TipoCancha.FUTBOL_5, 10, "80000");
            crearCancha("Cancha Sur", TipoCancha.FUTBOL_7, 14, "120000");
        }

        crearServicio("Balon", "10000");
        crearServicio("Petos", "15000");
        crearServicio("Arbitraje", "50000");
    }

    private void crearCancha(String nombre, TipoCancha tipo, int capacidad, String precio) {
        Cancha cancha = new Cancha();
        cancha.setNombre(nombre);
        cancha.setTipoCancha(tipo);
        cancha.setCapacidad(capacidad);
        cancha.setPrecioHora(new BigDecimal(precio));
        cancha.setEstadoCancha(EstadoCancha.ACTIVA);
        canchaRepository.save(cancha);
    }

    private void crearServicio(String nombre, String precio) {
        if (!servicioRepository.existsByNombre(nombre)) {
            ServicioAdicional servicio = new ServicioAdicional();
            servicio.setNombre(nombre);
            servicio.setPrecio(new BigDecimal(precio));
            servicioRepository.save(servicio);
        }
    }
}
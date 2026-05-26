package com.arenareserve.service;

import com.arenareserve.dto.ClienteRequest;
import com.arenareserve.dto.ClienteResponse;
import com.arenareserve.exception.OperacionNoPermitidaException;
import com.arenareserve.exception.ResourceNotFoundException;
import com.arenareserve.model.Cliente;
import com.arenareserve.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ClienteResponse obtener(Long id) {
        return toResponse(find(id));
    }

    public ClienteResponse crear(ClienteRequest request) {
        if (repository.existsByDocumento(request.documento())) {
            throw new OperacionNoPermitidaException("El documento del cliente ya existe");
        }
        Cliente cliente = new Cliente();
        apply(cliente, request);
        return toResponse(repository.save(cliente));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = find(id);
        repository.findByDocumento(request.documento())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new OperacionNoPermitidaException("El documento del cliente ya existe"); });
        apply(cliente, request);
        return toResponse(repository.save(cliente));
    }

    public void eliminar(Long id) {
        Cliente cliente = find(id);
        cliente.desactivar();
        repository.save(cliente);
    }

    public ClienteResponse cambiarEstado(Long id, boolean activo) {
        Cliente cliente = find(id);
        if (activo) {
            cliente.activar();
        } else {
            cliente.desactivar();
        }
        return toResponse(repository.save(cliente));
    }

    public Cliente find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    private void apply(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre());
        cliente.setTelefono(request.telefono());
        cliente.setEmail(request.email());
        cliente.setDocumento(request.documento());
        cliente.setDireccion(request.direccion());
    }

    ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getTelefono(), cliente.getEmail(),
                cliente.getDocumento(), cliente.getDireccion(), cliente.getFechaRegistro(), cliente.isActivo());
    }
}

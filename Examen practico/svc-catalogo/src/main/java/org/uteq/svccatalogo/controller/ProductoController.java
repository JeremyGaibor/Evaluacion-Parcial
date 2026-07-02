package org.uteq.svccatalogo.controller;

import org.uteq.svccatalogo.dto.ProductoRequest;
import org.uteq.svccatalogo.dto.ProductoResponse;
import org.uteq.svccatalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @Valid @RequestBody ProductoRequest request
    ) {
        ProductoResponse productoCreado = productoService.crear(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productoCreado.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(productoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.ok(
                productoService.actualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
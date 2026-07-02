package org.uteq.svccatalogo.service;

import org.uteq.svccatalogo.dto.ProductoRequest;
import org.uteq.svccatalogo.dto.ProductoResponse;
import org.uteq.svccatalogo.exception.ConflictoException;
import org.uteq.svccatalogo.exception.RecursoNoEncontradoException;
import org.uteq.svccatalogo.model.Producto;
import org.uteq.svccatalogo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        Producto producto = obtenerEntidadPorId(id);
        return convertirAResponse(producto);
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        String skuNormalizado = request.sku().trim().toUpperCase();

        if (productoRepository.existsBySku(skuNormalizado)) {
            throw new ConflictoException(
                    "Ya existe un producto con el SKU " + skuNormalizado
            );
        }

        Producto producto = Producto.builder()
                .sku(skuNormalizado)
                .nombre(request.nombre().trim())
                .precio(request.precio())
                .stock(request.stock())
                .build();

        Producto guardado = productoRepository.save(producto);

        return convertirAResponse(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtenerEntidadPorId(id);

        String skuNormalizado = request.sku().trim().toUpperCase();

        if (productoRepository.existsBySkuAndIdNot(skuNormalizado, id)) {
            throw new ConflictoException(
                    "Ya existe otro producto con el SKU " + skuNormalizado
            );
        }

        producto.setSku(skuNormalizado);
        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtenerEntidadPorId(id);
        productoRepository.delete(producto);
    }

    private Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el producto con ID " + id
                        )
                );
    }

    private ProductoResponse convertirAResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}

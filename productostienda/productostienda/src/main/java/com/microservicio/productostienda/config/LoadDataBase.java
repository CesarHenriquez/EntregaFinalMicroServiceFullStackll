package com.microservicio.productostienda.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservicio.productostienda.model.Categoria;
import com.microservicio.productostienda.model.Producto;
import com.microservicio.productostienda.repository.CategoriaRepository;
import com.microservicio.productostienda.repository.ProductoRepository;

@Configuration
public class LoadDataBase {
    @Bean
  
    CommandLineRunner initDatabase(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        return args -> {
           
            Categoria accesorios = categoriaRepository.findByNombre("Accesorios")
                    .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Accesorios")));

            Categoria pesas = categoriaRepository.findByNombre("Pesas")
                    .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Pesas")));

           
            if (productoRepository.count() == 0) {
                
                
                productoRepository.save(new Producto(null, "Cinturón de Halterofilia", "Cinturón de cuero reforzado",
                        19990.0, 10, "cinturon_fit", accesorios));

              
                productoRepository.save(new Producto(null, "Magnesio para Agarre", "Bloque de magnesio para mejor agarre",
                        4990.0, 50, "magnesio_fit", accesorios));
                
               
                productoRepository.save(new Producto(null, "Muñequeras de Soporte", "Muñequeras ajustables para levantamiento",
                        9990.0, 30, "munequeras_fit", accesorios));

              
                productoRepository.save(new Producto(null, "Straps de Levantamiento", "Correas de algodón reforzadas",
                        7990.0, 25, "straps_fit", accesorios));

           
                productoRepository.save(
                        new Producto(null, "Coderas", "Coderas acolchadas para soporte de codo", 49990.0, 5, "coderas_fit", accesorios));
                
                
                productoRepository.save(
                        new Producto(null, "Discos", "Discos de diferentes pesos para levantamiento", 49990.0, 20, "discos_fit", accesorios));
            }
        };
    }
}
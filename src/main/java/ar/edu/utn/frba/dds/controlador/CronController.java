package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.consenso.ServicioDeConsenso;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CronController {

    private final ServicioDeConsenso servicioDeConsenso;
    private final RepositorioDeFuentes repositorioDeFuentes;
    private final RepositorioDeColecciones repositorioDeColecciones;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CronController(ServicioDeConsenso servicioDeConsenso, RepositorioDeFuentes repositorioDeFuentes
            , RepositorioDeColecciones repositorioDeColecciones) {
        this.servicioDeConsenso = servicioDeConsenso;
        this.repositorioDeFuentes = repositorioDeFuentes;
        this.repositorioDeColecciones = repositorioDeColecciones;
    }

    public void calcularConsenso(@NotNull Context context){
        try {
            String token = context.header("X-Cron-Token");
            if (!"mi-super-token".equals(token)) {
                context.status(401).result("Unauthorized");
                return;
            }
            // Ejecutar en segundo plano
            executor.submit(() -> {
                try {
                    ejecutarPonderacion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            context.status(400);
        }
    }

    public void ejecutarPonderacion(){
        servicioDeConsenso.ponderarConsensoGlobal(repositorioDeFuentes.buscarTodas(), repositorioDeColecciones.consultarTodas());
    }
}

package ar.edu.utn.frba.dds.controlador;

import ar.edu.utn.frba.dds.dominio.consenso.ServicioDeConsenso;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeColecciones;
import ar.edu.utn.frba.dds.persistencia.RepositorioDeFuentes;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class CronController {

    private final ServicioDeConsenso servicioDeConsenso;
    private final RepositorioDeFuentes repositorioDeFuentes;
    private final RepositorioDeColecciones repositorioDeColecciones;

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
            servicioDeConsenso.ponderarConsensoGlobal(repositorioDeFuentes.buscarTodas(), repositorioDeColecciones.consultarTodas());
            context.status(200);

        } catch (Exception e) {
            context.status(400);
        }
    }
}

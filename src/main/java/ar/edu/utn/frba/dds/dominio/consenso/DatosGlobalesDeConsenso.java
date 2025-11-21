package ar.edu.utn.frba.dds.dominio.consenso;

// Un objeto inmutable para pasar los datos pre-calculados a la estrategia
public record DatosGlobalesDeConsenso(
    int totalDeFuentes,
    long mencionesDelHecho,
    boolean existeConflicto
) {
}
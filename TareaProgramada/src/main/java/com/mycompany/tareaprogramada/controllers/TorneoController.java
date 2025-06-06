package com.mycompany.tareaprogramada.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.tareaprogramada.models.Certificado;
import com.mycompany.tareaprogramada.models.Estadistica;
import com.mycompany.tareaprogramada.models.Equipo;
import com.mycompany.tareaprogramada.models.Partido;
import com.mycompany.tareaprogramada.models.Torneo;
import com.mycompany.tareaprogramada.util.LlaveGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controlador de persistencia y flujo de negocio para la entidad Torneo.
 * - Lee/escribe en "torneos.json"
 * - Inscribe/desinscribe equipos
 * - Genera llaves (iniciales y siguientes rondas)
 * - Registra resultados (incluye desempate)
 * - Calcula estadísticas por torneo
 * - Genera certificado para el campeón
 */
public class TorneoController {

    private static final String ARCHIVO_JSON = "torneos.json";
    private final Gson gson = new Gson();
    private ObservableList<Torneo> torneos;

    public TorneoController() {
        File archivo = new File(ARCHIVO_JSON);
        if (archivo.exists()) {
            try (FileReader reader = new FileReader(archivo)) {
                Type tipoLista = new TypeToken<List<Torneo>>() {}.getType();
                List<Torneo> lista = gson.fromJson(reader, tipoLista);
                torneos = FXCollections.observableArrayList(lista);
            } catch (Exception e) {
                e.printStackTrace();
                torneos = FXCollections.observableArrayList();
            }
        } else {
            torneos = FXCollections.observableArrayList();
        }
    }

    /**
     * Devuelve la lista observable de torneos (para tablas/comboboxes).
     */
    public ObservableList<Torneo> getTorneos() {
        return torneos;
    }

    /**
     * Crea un nuevo Torneo y lo agrega a la lista + persiste en el JSON.
     */
    public void crearTorneo(Torneo nuevo) {
        torneos.add(nuevo);
        guardarTorneos();
    }

    /**
     * Elimina un Torneo y actualiza el JSON.
     */
    public void eliminarTorneo(Torneo t) {
        torneos.remove(t);
        guardarTorneos();
    }

    /**
     * Guarda la lista completa de torneos en "torneos.json".
     */
    public void guardarTorneos() {
        try (FileWriter writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(torneos, writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inscribe un equipo en el torneo indicado (si aún hay cupo).
     * @param t Torneo donde se inscribirá.
     * @param e Equipo a inscribir.
     * @return true si se inscribió; false si ya no había cupo.
     */
    public boolean inscribirEquipoEnTorneo(Torneo t, Equipo e) {
        boolean ok = false;
        if (t != null) {
            ok = t.inscribirEquipo(e);
            if (ok) {
                guardarTorneos();
            }
        }
        return ok;
    }

    /**
     * Desinscribe un equipo de un torneo (antes de generar llaves).
     * @param t Torneo.
     * @param e Equipo a desinscribir.
     */
    public void desinscribirEquipoDeTorneo(Torneo t, Equipo e) {
        if (t != null) {
            t.desinscribirEquipo(e);
            guardarTorneos();
        }
    }

    /**
     * Genera las llaves iniciales de enfrentamientos para el torneo (si no estaban).
     * Utiliza LlaveGenerator.
     * @param t Torneo en el cual generar llaves.
     */
    public void generarLlavesTorneo(Torneo t) {
        if (t != null && !t.isLlavesGeneradas()) {
            LlaveGenerator.generarLlavesIniciales(t);
            guardarTorneos();
        }
    }

    /**
     * Registra el resultado de un partido dentro de un torneo:
     * - Actualiza goles.
     * - Marca finalizado = true.
     * - Si el partido estaba empatado, habrá que invocar desempate
     *   desde la capa de vista (PartidoViewController) para asignar golesLocal/golesVisitante.
     * @param t Torneo que contiene los partidos.
     * @param partidoId ID del partido a finalizar.
     * @param golesLocal Goles finales del equipo local.
     * @param golesVisitante Goles finales del equipo visitante.
     */
    public void registrarResultado(Torneo t, String partidoId, int golesLocal, int golesVisitante) {
        if (t == null || partidoId == null) return;

        for (Partido p : t.getPartidos()) {
            if (p.getId().equals(partidoId) && !p.isFinalizado()) {
                p.finalizarPartido(golesLocal, golesVisitante);
                break;
            }
        }
        guardarTorneos();
    }

    /**
     * Genera la siguiente ronda de partidos, tomando los ganadores de la ronda actual.
     * - Recoge a todos los ganadores de los partidos finalizados.
     * - Si hay al menos 2, limpia la lista de partidos y crea nuevos enfrentamientos.
     * - Persiste los cambios en JSON.
     * @param t Torneo en el que generar la siguiente ronda.
     */
    public void generarSiguienteRonda(Torneo t) {
        if (t == null) return;

        List<Equipo> ganadores = new ArrayList<>();
        for (Partido p : t.getPartidos()) {
            if (p.isFinalizado()) {
                Equipo g = p.obtenerGanador();
                if (g != null) {
                    ganadores.add(g);
                }
            }
        }

        if (ganadores.size() >= 2) {
            // Vaciar los partidos anteriores
            t.getPartidos().clear();
            // Barajar ganadores para emparejar aleatoriamente
            Collections.shuffle(ganadores);
            int total = ganadores.size();
            for (int i = 0; i < total / 2; i++) {
                Partido nuevo = new Partido(ganadores.get(i), ganadores.get(total - 1 - i), t.getTiempoPorPartido());
                t.agregarPartido(nuevo);
            }
            // Marcar que esta nueva ronda es generada
            t.setLlavesGeneradas(true);
            guardarTorneos();
        }
    }

    /**
     * Calcula y devuelve una lista de Estadistica para cada equipo en el torneo.
     * - Recorre todos los partidos finalizados.
     * - Suma goles, victorias directas, victorias por desempate y asigna puntos.
     * @param t Torneo.
     * @return Lista de Estadistica (uno por equipo inscrito).
     */
    public List<Estadistica> calcularEstadisticas(Torneo t) {
        Map<String, Estadistica> mapaEst = new HashMap<>();
        // Inicializar estadística para cada equipo inscrito
        for (Equipo e : t.getEquiposInscritos()) {
            mapaEst.put(e.getNombre(), new Estadistica(e));
        }

        // Recorrer cada partido finalizado y actualizar la estadística de ambos equipos
        for (Partido p : t.getPartidos()) {
            if (!p.isFinalizado()) continue;

            Equipo local = p.getEquipoLocal();
            Equipo visitante = p.getEquipoVisitante();
            int gl = p.getGolesLocal();
            int gv = p.getGolesVisitante();

            boolean empate = (gl == gv);
            boolean victoriaDirectaLocal = (gl > gv) && !p.isHuboDesempate();
            boolean victoriaDirectaVisit = (gv > gl) && !p.isHuboDesempate();
            boolean victoriaDesempateLocal = (gl > gv) && p.isHuboDesempate();
            boolean victoriaDesempateVisit = (gv > gl) && p.isHuboDesempate();

            Estadistica estLocal = mapaEst.get(local.getNombre());
            Estadistica estVisit = mapaEst.get(visitante.getNombre());

            if (estLocal != null && estVisit != null) {
                estLocal.registrarPartido(gl, gv, victoriaDirectaLocal, victoriaDesempateLocal);
                estVisit.registrarPartido(gv, gl, victoriaDirectaVisit, victoriaDesempateVisit);
            }
        }

        return new ArrayList<>(mapaEst.values());
    }

    /**
     * Genera un Certificado para el equipo campeón del torneo:
     * - Asume que el último partido en la lista es la final.
     * - Verifica que esté finalizado y obtiene su ganador.
     * - Calcula estadística y crea un objeto Certificado con todos los datos.
     * @param t Torneo del cual se desea generar el certificado.
     * @return Certificado completo o null si no se puede generar.
     */
    public Certificado generarCertificado(Torneo t) {
        if (t == null || t.getPartidos().isEmpty()) {
            return null;
        }

        // El último partido en la lista se asume como la final
        List<Partido> listaPartidos = t.getPartidos();
        Partido partidoFinal = listaPartidos.get(listaPartidos.size() - 1);
        if (!partidoFinal.isFinalizado()) {
            // Aun no se jugó la final
            return null;
        }

        Equipo ganador = partidoFinal.obtenerGanador();
        if (ganador == null) {
            // Empate sin desempate (no debería ocurrir)
            return null;
        }

        List<Estadistica> todasEst = calcularEstadisticas(t);
        Estadistica estCampeon = null;
        for (Estadistica est : todasEst) {
            if (est.getEquipo().getNombre().equals(ganador.getNombre())) {
                estCampeon = est;
                break;
            }
        }

        if (estCampeon == null) {
            return null;
        }

        return new Certificado(ganador, t, estCampeon);
    }
}

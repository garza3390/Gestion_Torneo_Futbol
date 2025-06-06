/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tareaprogramada.util;

import com.mycompany.tareaprogramada.models.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

/**
 * Generador de certificado en formato PDF usando Apache PDFBox.
 * Toma un objeto Certificado que contiene:
 *   - Equipo campeón (con su fotoPath)
 *   - Torneo (con sus datos)
 *   - Estadística del equipo campeón
 *
 * Genera un PDF en disco con diseño sencillo:
 *   • Encabezado: "Certificado de Campeón"
 *   • Foto del equipo (central)
 *   • Texto con detalles (nombre equipo, torneo, rendimiento)
 *
 * El archivo se nombra: "certificado_<idTorneo>_<nombreEquipo>.pdf"
 */
public class CertificadoPDFGenerator {

    /**
     * Genera el PDF y lo guarda en la carpeta actual.
     * @param cert Objeto Certificado con todos los datos.
     * @throws IOException si ocurre un error al escribir el PDF.
     */
    public static void generarPDF(Certificado cert) throws IOException {
        if (cert == null) {
            throw new IllegalArgumentException("El objeto Certificado no puede ser nulo.");
        }

        Equipo equipo = cert.getEquipoCampeon();
        Torneo torneo = cert.getTorneo();
        Estadistica estadistica = cert.getEstadisticaCampeon();

        // Crear documento y página
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.LETTER);
            documento.addPage(pagina);

            // Fuente para texto (incluye acentos si se carga bien la fuente TTF)
            // Ajusta la ruta a la fuente si hace falta, o usa la fuente predeterminada
            PDType0Font fuente = PDType0Font.load(documento, new File("src/main/resources/fonts/arial.ttf"));

            // Iniciar escritura de contenido
            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                float anchoPagina = pagina.getMediaBox().getWidth();
                float yBase = pagina.getMediaBox().getHeight() - 50;

                // 1. Encabezado centrado
                String titulo = "Certificado de Campeón";
                contenido.beginText();
                contenido.setFont(fuente, 24);
                float tituloWidth = fuente.getStringWidth(titulo) / 1000 * 24;
                contenido.newLineAtOffset((anchoPagina - tituloWidth) / 2, yBase);
                contenido.showText(titulo);
                contenido.endText();

                // 2. Agregar imagen del equipo (redimensionada)
                if (equipo.getFotoPath() != null) {
                    File archivoImagen = new File(equipo.getFotoPath());
                    if (archivoImagen.exists()) {
                        PDImageXObject imagen = PDImageXObject.createFromFileByExtension(archivoImagen, documento);
                        // Escalamos la imagen para que no sea demasiado grande
                        float escala = 150f / imagen.getWidth();
                        float imgWidth = imagen.getWidth() * escala;
                        float imgHeight = imagen.getHeight() * escala;
                        float xImage = (anchoPagina - imgWidth) / 2;
                        float yImage = yBase - 100 - imgHeight;
                        contenido.drawImage(imagen, xImage, yImage, imgWidth, imgHeight);
                    }
                }

                // 3. Añadir texto con detalles debajo de la imagen
                float yTexto = yBase - 280; 
                contenido.beginText();
                contenido.setFont(fuente, 14);
                contenido.newLineAtOffset(50, yTexto);

                // Nombre del equipo campeón
                contenido.showText("Equipo Campeón: " + equipo.getNombre());
                contenido.newLineAtOffset(0, -20);

                // Datos del torneo
                contenido.showText("Torneo: " + torneo.getDeporte().getNombre()
                        + " (Equipos: " + torneo.getEquiposInscritos().size() + ")");
                contenido.newLineAtOffset(0, -20);
                contenido.showText("Tiempo por partido: " + torneo.getTiempoPorPartido() + " minutos");
                contenido.newLineAtOffset(0, -20);
                contenido.showText("Fecha de creaci\u00F3n del torneo: " + torneo.getFechaCreacion());
                contenido.newLineAtOffset(0, -30);

                // Rendimiento del equipo
                contenido.showText("Rendimiento del equipo:");
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Partidos jugados: " + estadistica.getPartidosJugados());
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Goles a favor: " + estadistica.getGolesAFavor());
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Goles en contra: " + estadistica.getGolesEnContra());
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Vict. directas: " + estadistica.getVictoriasDirectas());
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Vict. por desempate: " + estadistica.getVictoriasPorDesempate());
                contenido.newLineAtOffset(0, -20);
                contenido.showText("• Puntos: " + estadistica.getPuntos());

                contenido.endText();
            }

            // Guardar el PDF con nombre dinámico
            String nombreArchivo = String.format("certificado_%s_%s.pdf",
                    torneo.getId(), equipo.getNombre().replaceAll("\\s+", "_"));
            documento.save(nombreArchivo);
            System.out.println("PDF generado: " + nombreArchivo);
        }
    }
}

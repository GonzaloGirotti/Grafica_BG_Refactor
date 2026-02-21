package PdfFormater;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import org.javatuples.Pair;
import utils.PDFOpener;
import utils.TextUtils;
import utils.databases.ClientsDatabaseConnection;
import utils.databases.hibernate.ClientesDBConnection;
import utils.databases.hibernate.entities.Clientes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static utils.TextUtils.truncateAndRound;

public class WorkBudgetPDFConverter {
    private static PdfFont ARIAL_FONT;
    private static PdfFont TAHOMA_FONT;
    private static final TextUtils textUtils = new TextUtils();
    private static final ClientesDBConnection clientsDB = new ClientesDBConnection();
    private static PDFOpener pdfOpener = new PDFOpener();
    private int copyCounter = 1;


    private static void initFonts() throws IOException {

        FontProgram arialNarrowProgram =
                FontProgramFactory.createFont("src/main/resources/fonts/arial_narrow_7.ttf");

        FontProgram tahomaProgram =
                FontProgramFactory.createFont("src/main/resources/fonts/tahoma.ttf");

        ARIAL_FONT = PdfFontFactory.createFont(
                arialNarrowProgram,
                PdfEncodings.IDENTITY_H
        );

        TAHOMA_FONT = PdfFontFactory.createFont(
                tahomaProgram,
                PdfEncodings.IDENTITY_H
        );

    }

    private String getClientName(int clientID) {
        try {
            Clientes cliente = clientsDB.getOneClient(clientID);
            if(cliente != null){
                return cliente.getNombre();
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error fetching client name for ID: " + clientID);
            e.printStackTrace();
            return null;
        }
    }

    public void generateWorkBill(boolean modified, int billNumber,  int clientID, ArrayList<Pair<String,String>> strMaterials, Pair<String,String> logistics, ArrayList<Pair<String,String>> placers,
                                        String deposit, String balance, String budgetCost, String finalCost) throws IOException {
        initFonts();

        ArrayList<NewRow> materials = textUtils.toTableRow(strMaterials);

        String actualDir = System.getProperty("user.dir");
        String folderDir = "/Presupuestos_Trabajo_Internos_PDF/";

        String clientName = getClientName(clientID);
        String fechaActualYankee = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaActualArg = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        File pdfsFolder = new File(actualDir + folderDir);
        if (!pdfsFolder.exists()) {
            pdfsFolder.mkdir();
        }

        String outputPath = actualDir + folderDir + "p_interno_" + billNumber + "_" + clientName + "_" + fechaActualYankee + ".pdf";

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);

        doc.setMargins(20, 20, 20, 20);

        // ================= ENCABEZADO =================
        Table header = new Table(new float[]{2, 5});
        header.setWidth(UnitValue.createPercentValue(100));

        String logoPath = "src/main/resources/BGLogo.png"; // Ruta de la imagen del logo

        // Imagen (logo)
        Image logo = new Image(
                ImageDataFactory.create(logoPath) // imagen cualquiera
        ).scaleToFit(120, 60);

        Cell logoCell = new Cell()
                .add(logo)
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(30);

        header.addCell(logoCell);

        Table datos = new Table(1);
        datos.setWidth(UnitValue.createPercentValue(100));
        datos.setMarginBottom(30);

// ===== FILA FECHA =====
        Table filaFecha = new Table(new float[]{3, 2});
        filaFecha.setWidth(UnitValue.createPercentValue(100));

        filaFecha.addCell(new Cell()
                        .add(new Paragraph("Fecha:")
                                .setFontSize(10))
                        .setBorder(Border.NO_BORDER))
                .setFont(TAHOMA_FONT);

        filaFecha.addCell(new Cell()
                .add(new Paragraph(fechaActualArg)
                        .setFontSize(10)
                        .setFont(TAHOMA_FONT)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER));

        datos.addCell(new Cell()
                .add(filaFecha)
                .setBorder(new SolidBorder(1))
                .setPadding(6)
        );

        // ===== FILA NÚMERO PRESUPUESTO =====
        Table filaNumeroPresupuesto = new Table(new float[]{3, 2});
        filaNumeroPresupuesto.setWidth(UnitValue.createPercentValue(100));

        filaNumeroPresupuesto.addCell(new Cell()
                .add(new Paragraph("N° Presupuesto:")
                        .setFontSize(10))
                .setFont(TAHOMA_FONT)
                .setBorder(Border.NO_BORDER));

        filaNumeroPresupuesto.addCell(new Cell()
                .add(new Paragraph(String.valueOf(billNumber))
                        .setFontSize(10)
                        .setFont(TAHOMA_FONT)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER));

        datos.addCell(new Cell()
                .add(filaNumeroPresupuesto)
                .setBorder(new SolidBorder(1))
                .setPadding(6)
        );

// ===== FILA NOMBRE CLIENTE =====
        Table filaNombre = new Table(new float[]{3, 2});
        filaNombre.setWidth(UnitValue.createPercentValue(100));

        filaNombre.addCell(new Cell()
                .add(new Paragraph("Nombre del cliente:")
                        .setFontSize(10))
                .setFont(TAHOMA_FONT)
                .setBorder(Border.NO_BORDER));

        filaNombre.addCell(new Cell().add(new Paragraph(clientName).setFontSize(10).setFont(TAHOMA_FONT).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

        datos.addCell(new Cell()
                .add(filaNombre)
                .setBorder(new SolidBorder(1))
                .setPadding(6)
        );

        header.addCell(new Cell().add(datos).setBorder(Border.NO_BORDER));

        doc.add(header);
        doc.add(separador());

        // ================= MATERIALES =================
        doc.add(bloqueMateriales(materials));
        doc.add(espacio());

        // ================= LOGÍSTICA =================
        doc.add(bloqueLogistica(logistics.getValue0(), Double.parseDouble(logistics.getValue1())));
        doc.add(espacio());

        // ================= COLOCACIÓN =================
        doc.add(bloqueColocacion(placers));
        doc.add(espacio());

        // ================= PAGOS =================
        double doubleDepositValue = Double.parseDouble(truncateAndRound(deposit));
        double doubleBalanceValue = Double.parseDouble(truncateAndRound(balance));
        doc.add(bloquePagos(doubleDepositValue, doubleBalanceValue));
        doc.add(espacio());

        // ================= COSTO PRESUPUESTO =================
        double doubleBudgetCost = Double.parseDouble(truncateAndRound(budgetCost));
        doc.add(bloquesSubtotalYTotal(doubleBudgetCost, "COSTO DE PRESUPUESTO"));
        doc.add(espacio());

        // ================= PRESUPUESTO FINAL =================
        double doubleFinalCost = Double.parseDouble(truncateAndRound(finalCost));
        doc.add(bloquesSubtotalYTotal(doubleFinalCost, "PRESUPUESTO FINAL"));
        doc.add(espacio());

        doc.close();

        //GeneratePDF
        pdfOpener.openPDF(false, false, folderDir, billNumber, clientName, fechaActualYankee);
    }

    // ================= UTILIDADES =================

    private static Table bloquesSubtotalYTotal (double cost, String titulo) {
        Table t = new Table(1);
        t.setWidth(UnitValue.createPercentValue(100));

        Cell bloque = new Cell()
                .setHeight(50)
                .setBorder(new SolidBorder(1))
                .setPadding(6);

        bloque.add(new Paragraph(titulo)
                .setBold()
                .setFontSize(10)
                .setFont(TAHOMA_FONT)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(4));

        // Agregar el valor del costo subtotal, pegado al fondo del bloque, sin etiqueta
        bloque.add(new Paragraph("").setHeight(10));
        bloque.add(new Paragraph("$" + String.format("%.2f", cost))
                .setFontSize(10)
                .setFont(TAHOMA_FONT)
                .setTextAlignment(TextAlignment.RIGHT));

        t.addCell(bloque);
        return t;
    }

    private static Table bloquePagos(double deposit, double balance) {
        Table t = new Table(1);
        t.setWidth(UnitValue.createPercentValue(100));

        Cell bloque = new Cell()
                .setMinHeight(50)
                .setBorder(new SolidBorder(1))
                .setPadding(6);

        // Tabla interna para Seña y Saldo en horizontal
        Table fila = new Table(new float[]{3, 2});
        fila.setWidth(UnitValue.createPercentValue(100));

        Paragraph depositParagraph = new Paragraph("");
        depositParagraph.add(new Paragraph("Seña: ")
                .setBold()
                .setFontSize(10)
                .setFont(TAHOMA_FONT));

        depositParagraph.add(new Paragraph("$" + String.format("%.2f", deposit))
                .setFontSize(10)
                .setFont(TAHOMA_FONT));

        Paragraph balanceParagraph = new Paragraph("");
        balanceParagraph.add(new Paragraph("Saldo: ")
                .setBold()
                .setFontSize(10)
                .setFont(TAHOMA_FONT));

        balanceParagraph.add(new Paragraph("$" + String.format("%.2f", balance))
                .setFontSize(10)
                .setFont(TAHOMA_FONT));

        fila.addCell(new Cell()
                .add(depositParagraph)
                .setFont(TAHOMA_FONT)
                .setBorder(Border.NO_BORDER));

        fila.addCell(new Cell()
                .add(balanceParagraph)
                .setFont(TAHOMA_FONT)
                .setBorder(Border.NO_BORDER));

        // Empuja la fila hacia abajo del bloque
        bloque.add(new Paragraph("").setHeight(10));
        bloque.add(fila);

        t.addCell(bloque);
        return t;
    }

    private static Table bloqueMateriales(ArrayList<NewRow> materials) {
        Table t = new Table(1);
        t.setWidth(UnitValue.createPercentValue(100));

        Cell bloque = new Cell()
                .setMinHeight(20)
                .setMaxWidth(400)
                .setBorder(new SolidBorder(1))
                .setPadding(6);

        bloque.add(new Paragraph("MATERIALES")
                .setBold()
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(4));

        // Tabla interna para los materiales (2 columnas)
        Table materialesTable = new Table(new float[]{4, 2});
        materialesTable.setWidth(UnitValue.createPercentValue(100));
        for (NewRow row : materials) {
            materialesTable.addCell(new Cell()
                    .add(new Paragraph(row.getProductDescription())
                            .setMaxWidth(300)
                            .setFontSize(10))
                    .setFont(TAHOMA_FONT)
                    .setBorder(Border.NO_BORDER));

            if(row.getTotal().isEmpty()){
                materialesTable.addCell(new Cell()
                        .add(new Paragraph(""))
                        .setFont(TAHOMA_FONT)
                        .setBorder(Border.NO_BORDER));
            }else {
                materialesTable.addCell(new Cell()
                        .add(new Paragraph("$" + String.format("%.2f",Double.parseDouble(row.getTotal())))
                                .setFontSize(10)
                                .setTextAlignment(TextAlignment.RIGHT))
                        .setFont(TAHOMA_FONT)
                        .setBorder(Border.NO_BORDER));
            }

        }

        bloque.add(materialesTable);
        t.addCell(bloque);
        return t;
    }

    private static Table bloqueLogistica(String description, double price) {
        // 1. Tabla principal (contenedor)
        Table t = new Table(1);
        t.setWidth(UnitValue.createPercentValue(100));

        // 2. Celda contenedora principal
        Cell bloque = new Cell()
                .setMinHeight(50)
                .setBorder(new SolidBorder(1))
                .setPadding(6)
                .setFont(TAHOMA_FONT); // Aplicamos la fuente a nivel de celda

        // Título de la sección
        bloque.add(new Paragraph("LOGÍSTICA")
                .setBold()
                .setFontSize(10)
                .setMarginBottom(4));

        // 3. Tabla interna para Descripción y Precio
        Table fila = new Table(UnitValue.createPercentArray(new float[]{70, 30})); // Usar porcentajes 70/30
        fila.setWidth(UnitValue.createPercentValue(100));

        // Celda de Descripción
        Paragraph descPara = new Paragraph()
                .add(new Text("Descripción: ").setBold())
                .add(autoShrinkParagraph(description, 10)); // Asumiendo que devuelve un Paragraph o Text

        fila.addCell(new Cell()
                .add(descPara)
                .setFontSize(10)
                .setBorder(Border.NO_BORDER));

        // Celda de Precio
        Paragraph pricePara = new Paragraph()
                .add(new Text("Precio: ").setBold())
                .add(new Text("$" + String.format("%.2f", price)));

        fila.addCell(new Cell()
                .add(pricePara)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT) // Generalmente los precios se alinean a la derecha
                .setBorder(Border.NO_BORDER));

        bloque.add(fila);
        t.addCell(bloque);

        return t;
    }

    private static LineSeparator separador() {
        return new LineSeparator(new SolidLine());
    }

    private static Paragraph espacio() {
        return new Paragraph("").setMarginBottom(10);
    }

    private static Table bloqueColocacion(ArrayList<Pair<String,String>> placers) {
        // 1. Tabla contenedora
        Table t = new Table(1);
        t.setWidth(UnitValue.createPercentValue(100));

        // 2. Celda con borde y estilo base
        Cell bloque = new Cell()
                .setMinHeight(50)
                .setBorder(new SolidBorder(1))
                .setPadding(6)
                .setFont(TAHOMA_FONT); // Fuente aplicada a todo el bloque

        // Título de la sección
        bloque.add(new Paragraph("COLOCACIÓN")
                .setBold()
                .setFontSize(10)
                .setMarginBottom(4));

        // 3. Tabla interna para Colocador y Precio (Distribuida 60/40)
        Table fila = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
        fila.setWidth(UnitValue.createPercentValue(100));

        for(Pair<String, String> placer : placers){
            String colocatorName = placer.getValue0();
            double price = Double.parseDouble(placer.getValue1());

            // Celda: Colocador
            Paragraph pColocador = new Paragraph()
                    .add(new Text("Colocador: ").setBold())
                    .add(new Text(colocatorName))
                    .setFontSize(10);

            fila.addCell(new Cell()
                    .add(pColocador)
                    .setBorder(Border.NO_BORDER));

            // Celda: Precio
            Paragraph pPrecio = new Paragraph()
                    .add(new Text("Precio: ").setBold())
                    .add(new Text("$" + String.format("%.2f", price)))
                    .setFontSize(10);

            fila.addCell(new Cell()
                    .add(pPrecio)
                    .setTextAlignment(TextAlignment.RIGHT) // Alineación a la derecha para legibilidad
                    .setBorder(Border.NO_BORDER));
        }

        // Espaciado opcional antes de la fila (reemplaza el Paragraph vacío con setHeight)
        fila.setMarginTop(10);
        bloque.add(fila);
        t.addCell(bloque);

        return t;
    }

    private static Paragraph autoShrinkParagraph(String text, float baseSize) {
        float size = baseSize;

        if (text.length() > 250) size -= 0.5;
        if (text.length() > 500) size -= 0.5;
        if (text.length() > 800) size -= 0.5;
        if (size < 7) size = 7;

        return new Paragraph(text)
                .setFont(TAHOMA_FONT)
                .setFontSize(size);
    }
}


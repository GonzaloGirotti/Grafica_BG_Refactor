package PdfFormater;

import PdfFormater.codingerror.model.AddressDetails;
import PdfFormater.codingerror.model.HeaderDetails;
import PdfFormater.codingerror.service.CodingErrorPdfInvoiceCreator;
import com.itextpdf.layout.element.Paragraph;
import org.javatuples.Pair;
import utils.Client;
import utils.NewProduct;
import utils.PDFOpener;
import utils.TextUtils;
import utils.databases.hibernate.entities.Clientes;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static utils.TextUtils.truncateAndRound;

public class WorkBudgetClientPDFConverter {
    String pdfName = "Hola.pdf";
    CodingErrorPdfInvoiceCreator cepdf =new CodingErrorPdfInvoiceCreator(pdfName);
    private final TextUtils textUtils = new TextUtils();
    private final PDFOpener pdfOpener = new PDFOpener();
/*
    String pdfName = "job_budget_client.pdf";
*/

    public void generateBill(Clientes client, int billNumber, ArrayList<String> baseTableContent, String budgetTotal, Pair<String, String> depositAndBalance) throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate ld = LocalDate.now();

        String formattedDate = ld.format(formatter);
        String fileFormattedDate = ld.format(fileFormatter);

        String pdfName = "p_cliente_" + billNumber + "_" + client.getNombre() +"_"+ fileFormattedDate + ".pdf";

        String imagePath="src/main/resources/BGLogo.png"; // Path to your logo image
        CodingErrorPdfInvoiceCreator cepdf =new CodingErrorPdfInvoiceCreator(pdfName);
        cepdf.createClientWorkDocument(client.getNombre(), fileFormattedDate, billNumber);

        //Create Header start
        HeaderDetails header=new HeaderDetails();
        header.setInvoiceNo(billNumber+""). // Set invoice number
                setInvoiceDate(formattedDate) // Set invoice date
                .setInvoiceTitle("")
                .setInvoiceNoText("N° presupuesto")
                .setInvoiceDateText("Fecha:")
                .build();
        cepdf.createHeader(header, imagePath);
        //Header End

        //Create Address start
        AddressDetails addressDetails=new AddressDetails();
        addressDetails
                .setBillingInfoText("Cliente")
                .setBillingCompanyText("Nombre")
                .setBillingCompany(client.getNombre())
                .setBillingNameText("Dirección")
                .setBillingName(client.getDireccion())
                .setBillingAddressText("Localidad")
                .setBillingAddress(client.getTipoCliente())
                .setBillingEmailText("Teléfono")
                .setBillingEmail(client.getTelefono())
                .setShippingInfoText("")
                .setShippingNameText("")
                .setShippingAddressText("")
                .build();

        cepdf.createAddress(addressDetails);
        //Address end

        //Product Start
        double total = Double.parseDouble(truncateAndRound(budgetTotal));
        cepdf.createNewProduct(baseTableContent, total);
        //Product End

        //Term and Condition Start
        String deposit = depositAndBalance.getValue0();
        String balance = depositAndBalance.getValue1();

        Paragraph tncLine1 = new Paragraph("Presupuesto válido por 48 hs. \n ").setItalic();
        Paragraph tncLine2 = new Paragraph("Modalidad de pago: 50% al confirmar ($" + deposit + ") y 50% al finalizar ($" + balance + "). \n ").setItalic();
        Paragraph tncLine3 = new Paragraph("En caso de necesitar factura, al precio se le agregará el IVA. \n").setBold();
        Paragraph tncLine4 = new Paragraph("\n Por cualquier consulta, no dude en comunicarse con nosotros. \n ").setUnderline();
        List<Paragraph> TncList=new ArrayList<>();
        TncList.add(tncLine1);
        TncList.add(tncLine2);
        TncList.add(tncLine3);
        TncList.add(tncLine4);
        cepdf.createTnc(TncList,false,imagePath);
        // Term and condition end

        String folderDir = "/Presupuestos_Trabajo_Clientes_PDF/";
        pdfOpener.openPDF(false, true, folderDir, billNumber, client.getNombre(), fileFormattedDate);
    }
}

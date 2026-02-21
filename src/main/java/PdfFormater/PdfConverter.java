package PdfFormater;

import PdfFormater.codingerror.model.AddressDetails;
import PdfFormater.codingerror.model.HeaderDetails;
import PdfFormater.codingerror.model.Product;
import PdfFormater.codingerror.service.CodingErrorPdfInvoiceCreator;
import com.itextpdf.layout.element.Paragraph;
import utils.Client;
import utils.databases.hibernate.entities.Clientes;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PdfConverter implements IPdfConverter{
    public PdfConverter(){}
    @Override
    public void generateBill(boolean isPreview, Clientes client, int billNumber, ArrayList<Row> tableContent, double total) throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate ld = LocalDate.now();
        String formattedDate = ld.format(formatter);
        LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);

        String pdfName;

        // Generate PDF file name
        if(isPreview){
            pdfName = "temp_preview.pdf";
        }else {
            pdfName = client.getNombre() +"_"+ parsedDate + "_" + billNumber + ".pdf";
        }

        String imagePath="src/main/resources/BGLogo.png"; // Path to your logo image

        CodingErrorPdfInvoiceCreator cepdf =new CodingErrorPdfInvoiceCreator(pdfName);
        String finalPath = "";

        try{
            finalPath = cepdf.createDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create Header start
        HeaderDetails header=new HeaderDetails();
        header.setInvoiceNo(billNumber+""). // Set invoice number
                setInvoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
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
                .setBillingAddress(client.getLocalidad())
                .setBillingEmailText("Teléfono")
                .setBillingEmail(client.getTelefono())
                //.setShippingName("Customer Name \n")
                .setShippingInfoText("")
                .setShippingNameText("")
                .setShippingAddressText("")
                //.setShippingAddress("Banglore Name sdss\n swjs\n")
                .build();

        cepdf.createAddress(addressDetails);
        //Address end

        //Product Start
        List<Product> productList = cepdf.formatProductsToProductsList(tableContent);
        // productList=cepdf.modifyProductList(productList);
        cepdf.createProduct(productList, total);
        //Product End

        //Term and Condition Start
        Paragraph tncLine1 = new Paragraph("Presupuesto válido por 48hs.");
        List<Paragraph> TncList=new ArrayList<>();
        TncList.add(tncLine1);
        //I left this as a sample, you can add more stuff here
        /*
        TncList.add("1. The Seller shall not be liable to the Buyer directly or indirectly for any loss or damage suffered by the Buyer.");
        TncList.add("2. The Seller warrants the product for one (1) year from the date of shipment");*/
        cepdf.createTnc(TncList,false,imagePath);
        // Term and condition end

        try {
            File pdf = new File(finalPath);
            if (pdf.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdf); // Opens with system default viewer
            } else {
                System.out.println("File not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

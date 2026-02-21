package PdfFormater;

import utils.Client;
import utils.databases.hibernate.entities.Clientes;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface IPdfConverter {
    void generateBill(boolean isPreview, Clientes client, int billNumber, ArrayList<Row> tableContent, double total) throws FileNotFoundException;

}

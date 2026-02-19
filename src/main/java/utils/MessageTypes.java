package utils;

import lombok.Getter;

import javax.swing.*;

@Getter
public enum MessageTypes {
    INCOMPLETE_INFO_FIELDS("Campos incompletos"
            , "Por favor, complete todos los campos obligatorios antes de continuar."
            , JOptionPane.ERROR_MESSAGE),
    INCOMPLETE_MATERIAL_FIELDS("Campo de material incompletos"
            , "Por favor, complete el campo de material antes de agregarlo a la lista."
            , JOptionPane.ERROR_MESSAGE),
    INCOMPLETE_PLACER_FIELDS("Campo de colocación incompletos"
            , "Por favor, complete el campo de colocación antes de agregarlo a la lista."
            , JOptionPane.ERROR_MESSAGE),
    NO_ROW_SELECTED_FOR_DELETION("Error al eliminar fila"
            , "Por favor seleccione una fila de la tabla correspondiente antes de presionar el botón de eliminar."
            , JOptionPane.ERROR_MESSAGE),
    NO_ROW_SELECTED_FOR_MODIFYING("Error al modificar fila"
            , "Por favor seleccione una fila válida antes de presionar el botón de modificar."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_CREATION_SUCCESS("Cliente creado con éxito"
            , "El cliente fue agregado a la base de datos correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    DEBUG("DEBUG"
            , "CURRENTLY DEBUGGING INFO."
            , JOptionPane.INFORMATION_MESSAGE),
    ERROR_DEBUG("ERROR_DEBUG"
            , "CURRENTLY DEBUGGING ERROR."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_CREATION_FAILURE("Error al crear el cliente"
            , "Ha ocurrido un error al intentar crear el cliente."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_SEARCH_FAILURE("Error al buscar el cliente"
            , "Ha ocurrido un error al intentar buscar el cliente."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_NOT_SELECTED("Cliente no seleccionado"
            , "Por favor, seleccione un cliente de la tabla para poder continuar."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_DELETION_FAILURE ("Error al eliminar el cliente"
            , "Ha ocurrido un error al intentar eliminar el cliente."
            , JOptionPane.ERROR_MESSAGE),
    CITY_FETCH_FAILURE("Error al consultar las ciudades"
            , "Ha ocurrido un error al consultar las ciudades en la base de datos."
            , JOptionPane.ERROR_MESSAGE),
    ANY_CREATION_EMPTY_FIELDS("Debe llenar los campos obligatorios"
            , "Por favor, llene los campos obligatorios para poder crear un cliente."
            , JOptionPane.ERROR_MESSAGE),
    BUDGET_CREATION_EMPTY_COLUMN("Faltan datos para el presupuesto."
    , "Por favor, llene los campos obligatorios para poder crear un presupuesto."
    , JOptionPane.ERROR_MESSAGE),
    PRODUCT_CREATION_SUCCESS("Producto creado con éxito"
            , "El producto fue agregado a la base de datos correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    PRODUCT_CREATION_FAILURE("Error al crear el producto"
            , "Ha ocurrido un error al intentar crear el producto."
            , JOptionPane.ERROR_MESSAGE),
    PRODUCT_SEARCH_FAILURE("Error al buscar el producto"
            , "Ha ocurrido un error al intentar buscar el producto."
            , JOptionPane.ERROR_MESSAGE),
    PRODUCT_LIST_GENERATION_FAILURE("Error al generar la lista de productos"
            , "Ha ocurrido un error al intentar generar la lista de productos."
            , JOptionPane.ERROR_MESSAGE),
    MISSING_PRODUCT_NAME("Nombre de producto faltante"
            , "Por favor, ingrese un nombre para el producto."
            , JOptionPane.ERROR_MESSAGE),
    PRODUCT_DELETION_FAILURE ("Error al eliminar el producto"
            , "Ha ocurrido un error al intentar eliminar el producto."
            , JOptionPane.ERROR_MESSAGE),
    PRODUCT_ADDING_FAILURE("Error al agregar el producto"
            , "Ha ocurrido un error al intentar agregar el producto."
            , JOptionPane.ERROR_MESSAGE),
    SUBCATEGORY_CREATION_SUCCESS("SubCategoria creada con éxito"
            , "La subcategoria fue agregada a la base de datos correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    SUBCATEGORY_CREATION_FAILURE("Error al crear la subcategoria"
            , "Ha ocurrido un error al intentar crear la subcategoria."
            , JOptionPane.ERROR_MESSAGE),
    SUBCATEGORY_SEARCH_FAILURE("Error al buscar la subcategoria"
            , "Ha ocurrido un error al intentar buscar la subcategoria."
            , JOptionPane.ERROR_MESSAGE),
    BUDGET_SEARCH_FAILURE("Error al buscar el presupuesto"
            , "Ha ocurrido un error al intentar buscar el presupuesto."
            , JOptionPane.ERROR_MESSAGE),
    BUDGET_DELETE_SUCCESS("Presupuesto eliminado con éxito",
            "El presupuesto fue eliminado de la base de datos correctamente.",
            JOptionPane.INFORMATION_MESSAGE),
    BUDGET_DELETE_FAILURE("Error al eliminar el presupuesto",
            "Ha ocurrido un error al intentar eliminar el presupuesto.",
            JOptionPane.ERROR_MESSAGE),
    BUDGET_CREATION_SUCCESS("Presupuesto creado con éxito"
            , "El presupuesto fue agregado a la base de datos correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    BUDGET_CREATION_FAILURE("Error al crear el presupuesto"
            , "Ha ocurrido un error al intentar crear el presupuesto."
            , JOptionPane.ERROR_MESSAGE),
    BUDGET_MODIFY_SUCCESS("Presupuesto modificado con éxito"
            , "El presupuesto fue modificado en la base de datos correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    BUDGET_MODIFY_FAILURE("Error al modificar el presupuesto"
            , "Ha ocurrido un error al intentar modificar el presupuesto."
            , JOptionPane.ERROR_MESSAGE),
    PDF_GENERATION_SUCCESS("PDF generado con éxito"
            , "El PDF fue generado correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    FLOAT_PARSING_ERROR("Formato numérico requerido"
            , "Se esperaba un número para el campo ingresado."
            , JOptionPane.ERROR_MESSAGE),
    INT_PARSING_ERROR("Formato numérico entero requerido"
            , "Se esperaba un número entero para el campo ingresado."
            , JOptionPane.ERROR_MESSAGE),
    MISSING_MODULAR_VIEW("Error creando el producto"
            , "POR FAVOR SELECCIONE UNA CATEGORÍA"
            , JOptionPane.ERROR_MESSAGE),
    SETTINGS_SAVE_FAILURE("Error al guardar la configuración"
            , "Ha ocurrido un error al intentar guardar la configuración de la tabla "
            , JOptionPane.ERROR_MESSAGE),
    SETTINGS_SAVE_SUCCESS("Configuración guardada con éxito"
            , "La configuración de las tablas fue guardada con éxito."
            , JOptionPane.INFORMATION_MESSAGE),
WORK_BUDGET_CREATION_SUCCESS("Presupuesto de trabajo creado con éxito"
            , "El presupuesto fue agregado a la base de datos y sus correspondientes PDF fueron generados correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    WORK_BUDGET_CREATION_FAILURE("Error al crear presupuesto de trabajo"
            , "Ha ocurrido un error al intentar crear el presupuesto de trabajo."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_BUDGET_SEARCH_FAILURE("Error al buscar los presupuestos del cliente"
            , "Ha ocurrido un error al intentar buscar los presupuestos del cliente."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_BUDGET_OPENING_FAILURE("Error al abrir el presupuesto del cliente"
            , "Ha ocurrido un error al intentar abrir el presupuesto del cliente."
            , JOptionPane.ERROR_MESSAGE),
    CLIENT_BUDGET_NO_BUDGETS("El cliente no tiene presupuestos"
            , "El cliente seleccionado no tiene presupuestos asociados."
            , JOptionPane.INFORMATION_MESSAGE),
    WORK_BUDGET_UPDATE_SUCCESS("Presupuesto de trabajo modificado con éxito"
            , "El presupuesto fue modificado en la base de datos y sus correspondientes PDF fueron generados correctamente."
            , JOptionPane.INFORMATION_MESSAGE),
    WORK_BUDGET_UPDATE_FAILURE("Error al modificar presupuesto de trabajo"
            , "Ha ocurrido un error al intentar modificar el presupuesto de trabajo."
            , JOptionPane.ERROR_MESSAGE),
    WORK_BUDGET_PDF_CREATION_FAILURE("Error al generar los PDFs del presupuesto de trabajo"
            , "Ha ocurrido un error al intentar generar los PDFs del presupuesto de trabajo."
            , JOptionPane.ERROR_MESSAGE),
    MODIFIED_WORK_BUDGET_PDF_CREATION_FAILURE("Error al generar los PDFs modificados del presupuesto de trabajo"
            , "Ha ocurrido un error al intentar generar los PDFs modificados del presupuesto de trabajo."
            , JOptionPane.ERROR_MESSAGE),
    NO_ROW_SELECTED("Error al seleccionar fila"
            , "Por favor seleccione una fila de la tabla."
            , JOptionPane.ERROR_MESSAGE);


    private final String title;
    private final String message;
    private final int messageType;

    MessageTypes(String title, String message, int messageType) {
        this.title = title;
        this.message = message;
        this.messageType = messageType;
    }

}

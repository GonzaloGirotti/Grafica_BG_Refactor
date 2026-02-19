package utils;

import presenters.product.ProductPresenter;
import views.products.modular.IModularCategoryView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetCategoryPanelsMap {
    private final TextUtils textUtils = new TextUtils();

    public GetCategoryPanelsMap() {
    }


    public Map<String, IModularCategoryView> getCategoryPanelsMap(ProductPresenter presenter) {
        String directoryPath = "src/main/java/views/products/modular";
        List<String> nombresDeModulars = textUtils.getFileNamesInDirectory(directoryPath);

        nombresDeModulars.removeIf(nombreCompleto -> nombreCompleto.startsWith("I"));

        List<String> subStringModulars = new ArrayList<>();
        List<IModularCategoryView> categoryViews = TextUtils.loadAllViewPanels("views.products.modular", presenter, true
        );
        Map<String, IModularCategoryView> categoryPanelsMap = new HashMap<>();

        //Se extraen los substrings de los nombres de los modulars. EJ: ModularCapView -> Cap
        for (String stringModular : nombresDeModulars) {
            String subString = textUtils.extractor(stringModular);
            subStringModulars.add(subString);
        }

        for (int i = 0; i < subStringModulars.size(); i++) {
            categoryPanelsMap.put(subStringModulars.get(i), categoryViews.get(i));
        }
        return categoryPanelsMap;
    }
}

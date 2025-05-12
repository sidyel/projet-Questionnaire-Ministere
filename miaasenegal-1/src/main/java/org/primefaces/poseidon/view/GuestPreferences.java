package org.primefaces.poseidon.view;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped

public class GuestPreferences implements Serializable {

    private String menuMode = "layout-static layout-static-active";

    private String componentTheme = "denim";
    
    private String topbarTheme = "light";

    private String menuTheme = "dim";

    private boolean orientationRTL;

    private String darkMode = "light";

    private String inputStyle = "outlined";
 
    private List<ComponentTheme> componentThemes = new ArrayList<ComponentTheme>();

    @PostConstruct
    public void init() {  
        componentThemes.add(new ComponentTheme("Denim", "denim", "#2f8ee5"));
        componentThemes.add(new ComponentTheme("Sea Green", "sea-green", "#30A059"));
        componentThemes.add(new ComponentTheme("Amethyst", "amethyst", "#834CA8"));
        componentThemes.add(new ComponentTheme("Wedgewood", "wedgewood", "#557DAA"));
        componentThemes.add(new ComponentTheme("Tapestry", "tapestry", "#A74896"));
        componentThemes.add(new ComponentTheme("Cape Palliser", "cape-palliser", "#A46B3E"));
        componentThemes.add(new ComponentTheme("Apple", "apple", "#52A235"));
        componentThemes.add(new ComponentTheme("Gigas", "gigas", "#5751A9"));
        componentThemes.add(new ComponentTheme("Jungle Green", "jungle-green", "#2B9F9C"));
        componentThemes.add(new ComponentTheme("Camelot", "camelot", "#A54357"));
        componentThemes.add(new ComponentTheme("Amber", "amber", "#D49341"));
        componentThemes.add(new ComponentTheme("Cyan", "cyan", "#399DB2"));
    }

   
    public String getDarkMode() {
        return darkMode;
    }
  
    public void setDarkMode(String darkMode) {
        this.darkMode = darkMode;
        this.menuTheme = darkMode;
        this.topbarTheme = darkMode;
    }

    public String getTheme() {
        return this.componentTheme + '-' + this.darkMode ;
    }

    public String getComponentTheme() {
        return componentTheme;
    }

    public void setComponentTheme(String componentTheme) {
        this.componentTheme = componentTheme;
    }

    public String getMenuTheme() {
        return menuTheme;
    }
    
    public void setMenuTheme(String menuTheme) {
            this.menuTheme = menuTheme;
    }

    public String getTopbarTheme() {
        return topbarTheme;
    }
    
    public void setTopbarTheme(String topbarTheme) {
        this.topbarTheme = topbarTheme;
    }

    public String getMenuMode() {
        return this.menuMode;
    }
    
    public void setMenuMode(String menuMode) {
        this.menuMode = menuMode;
    }

    public boolean isOrientationRTL() {
        return orientationRTL;
    }

    public void setOrientationRTL(boolean orientationRTL) {
        this.orientationRTL = orientationRTL;
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public List<ComponentTheme> getComponentThemes() {
        return componentThemes;
    }  

    public class ComponentTheme {
        String name;
        String file;
        String color;

        public ComponentTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }

   

}
